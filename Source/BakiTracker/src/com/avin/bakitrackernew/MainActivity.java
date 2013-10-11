package com.avin.bakitrackernew;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;

public class MainActivity extends Activity implements OnMenuItemClickListener {
	private ParticipantsDataSource dataSource;
	List<Participant> mValues;
	ArrayAdapter<Participant> mAdapter;
	
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
		
	    mAdapter = new ArrayAdapter<Participant>(this,
	            android.R.layout.simple_list_item_1, mValues);
	        setListAdapter(mAdapter);
	}
	
	private void setListAdapter(ArrayAdapter<Participant> adapter) {
		ListView lv = (ListView) findViewById(R.id.participants_list);
		registerForContextMenu(lv);		
		lv.setAdapter(adapter);
		
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
		mAdapter.notifyDataSetChanged();
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
		mAdapter.notifyDataSetChanged();
	}
	
	private void deleteParticipant(Participant participant) {
		dataSource.deletePaticipant(participant);
		mValues.remove(participant);
		mAdapter.notifyDataSetChanged();
	}
}
