package com.avin.bakitrackernew;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;

public class MainActivity extends Activity implements OnMenuItemClickListener {
	private ParticipantsDataSource dataSource;
	List<Participant> mValues;
//	ArrayAdapter<Participant> mAdapter;
	ParticipantsAdapter mParticipantAdapter;
	
	PopupMenu mPopupMenu = null;
	
	private static final int POPUPMENU_ITEM_GIVE = 1;
	private static final int POPUPMENU_ITEM_TAKE = 2;
	
	private Participant mSelectedParticipant = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		dataSource = new ParticipantsDataSource(this);
		dataSource.open();
		mValues = dataSource.getAllParticipants();
		
//	    mAdapter = new ArrayAdapter<Participant>(this,
//	            android.R.layout.simple_list_item_1, mValues);
//	        setListAdapter(mAdapter);
		mParticipantAdapter = new ParticipantsAdapter();
		setListAdapter(mParticipantAdapter);
		
		setupListeners();
	}
	
	private void setupListeners() {
		Button updateButton = (Button) findViewById(R.id.button_update);
		updateButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				updateCacheAmount();
				
			}
		});
	}

	protected void updateCacheAmount() {
		double total = 0;
		for (Participant p: mValues) {
			total += p.getAmount();
			updateAmount(p, p.amountCache, true);
		}
		
		TextView tv = (TextView) findViewById(R.id.text_view_total);
		tv.setText("" + total);
	}

	private void setListAdapter(ParticipantsAdapter adapter) {
		ListView lv = (ListView) findViewById(R.id.participants_list);
		registerForContextMenu(lv);		
		lv.setAdapter(adapter);
		lv.setItemsCanFocus(true);
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int pos,
					long id) {
				showOptionPopup(view);
				mSelectedParticipant = mValues.get(pos);
			}
		});
	}
	
	void showOptionPopup(View view) {
		mPopupMenu = new PopupMenu(this, view);
		mPopupMenu.getMenu().add(Menu.NONE, POPUPMENU_ITEM_GIVE, Menu.NONE, "Have to give amount");
		mPopupMenu.getMenu().add(Menu.NONE, POPUPMENU_ITEM_TAKE, Menu.NONE, "Have to take amount");
		mPopupMenu.show();
		mPopupMenu.setOnMenuItemClickListener(this);
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
		case POPUPMENU_ITEM_GIVE:
			showParticipantAmountUpdateDialog(mSelectedParticipant, false);
			break;
		case POPUPMENU_ITEM_TAKE:
			showParticipantAmountUpdateDialog(mSelectedParticipant, true);
			break;
			
		}
		return true;
	}
	
	private void showParticipantAmountUpdateDialog(
			final Participant participant, final boolean isTake) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		String title;
		if (isTake) {
			title = "Have to take from " + participant.getName();
		} else {
			title = "Have to give to " + participant.getName();
		}
		
		LayoutInflater inflater = getLayoutInflater();
		final View v = inflater.inflate(R.layout.dialog, null);
	    builder.setView(v)
	           .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   EditText et = (EditText) v.findViewById(R.id.amount);
	            	   String amountText= et.getText().toString();
	            	   Float amount = 0f;
	            	   if (amountText != null && !amountText.isEmpty()) {
	            		   amount = Float.parseFloat(et.getText().toString());
	            	   }
	            	   updateAmount(participant, amount, isTake);
	               }
	           })
	           .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	            	   
	               }
	           });  

		builder.setTitle(title);
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	private void updateAmount(Participant participant, double amount, boolean isTake) {
		if (isTake) {
			amount = participant.getAmount() - amount;
		} else {
			amount = participant.getAmount() + amount;
		}
		participant.setAmount(amount);
		dataSource.updateAmount(participant, amount);
		mParticipantAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.participants_list) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
			menu.setHeaderTitle(mValues.get(info.position).getName());
			menu.add("Delete");
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		deleteParticipant(mValues.get(info.position));
		return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add_participant:
			showAddParticipantDialog();
			break;

		default:
			break;
		}
		return true;
	}
	
	private void showAddParticipantDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		LayoutInflater inflator = getLayoutInflater();
		final View v = inflator.inflate(R.layout.dialog_add_participant, null);
		builder.setView(v)
			.setPositiveButton("Add", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					EditText et = (EditText) v.findViewById(R.id.participant_name_input);
					String name = et.getText().toString();
					addParticipant(name);
				}
			})
			.setNegativeButton("Cancel", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
		
		AlertDialog dialog = builder.create();
		dialog.show();
		
	}
	
	private void addParticipant(String name) {
		Participant p = dataSource.createParticipant(name);
		mValues.add(p);
		mParticipantAdapter.notifyDataSetChanged();
	}
	
	private void deleteParticipant(Participant participant) {
		dataSource.deletePaticipant(participant);
		mValues.remove(participant);
		mParticipantAdapter.notifyDataSetChanged();
	}
	
	public class ParticipantsAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		
		public ParticipantsAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		class ViewHolder{
			TextView name;
			TextView amount;
			EditText amountCache;
		}
		
		@Override
		public int getCount() {
			return mValues.size();
		}

		@Override
		public Object getItem(int position) {
			return mValues.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.participant_list_item, null);
				holder.name = (TextView) convertView.findViewById(R.id.text_view_name);
				holder.amount = (TextView) convertView.findViewById(R.id.text_view_amount);
				holder.amountCache = (EditText) convertView.findViewById(R.id.edit_text_amountcache);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Participant p = mValues.get(position);
			holder.name.setText(p.getName());
			if (p.getAmount() < 0) {
				holder.amount.setText("Take amount: Rs " + Math.abs(p.getAmount()));
			} else {
				holder.amount.setText("Give amount: Rs " + Math.abs(p.getAmount()));
			}
			holder.amountCache.setText(""+ p.amountCache);
			holder.amountCache.setId(position);
			
			holder.amountCache.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (!hasFocus) {
						int id = v.getId();
						EditText amountCache = (EditText) v;
						mValues.get(id).amountCache = Double.parseDouble(amountCache.getText().toString());
					}
				}
			});
			
			return convertView;
		}
		
	}
}
