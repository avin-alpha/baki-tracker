package com.avin.bakitrackernew;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private ParticipantsDataSource dataSource;
	List<Participant> mValues;
	ArrayAdapter<Participant> mAdapter;
	
	private enum names {
		mukeshgive, mukeshtake, ullasgive, ullastake, prashanthgive, prashanthtake
	}
	
	private names mNameState; 
	
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
		
//		updateFromPreference();
		setupViews();
		updateScores();
		registerListeners();
	}
	
	private void setListAdapter(ArrayAdapter<Participant> adapter) {
		ListView lv = (ListView) findViewById(R.id.participants_list);
		registerForContextMenu(lv);
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				
				return false;
			}
		});
		lv.setAdapter(adapter);
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
			Toast.makeText(this, "Adding person", Toast.LENGTH_SHORT).show();
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
					// TODO Auto-generated method stub
					
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

	private void registerListeners() {
		
	}
	
	private void setupViews() {
	}
	
	private void updateScores() {
	}
	
//	private void updateFromPreference() {
//		 SharedPreferences preference = getSharedPreferences(BAKI_PREFERENCE, MODE_PRIVATE);
//		 mMukesh = preference.getFloat(MUKESH, 0);
//		 mPrashanth = preference.getFloat(PRASHANTH, 0);
//		 mUllas = preference.getFloat(ULLAS, 0);
//	}
	
//	private void updateAll() {
//		float mukeshValue = Float.parseFloat(mMukeshEdit.getText().toString().isEmpty() ? "0" : mMukeshEdit.getText().toString());
//		float prashanthValue = Float.parseFloat(mPrashanthEdit.getText().toString().isEmpty() ? "0" : mPrashanthEdit.getText().toString());
//		float ullasValue = Float.parseFloat(mUllasEdit.getText().toString().isEmpty() ? "0" : mUllasEdit.getText().toString());
//		mMukesh -= mukeshValue;
//		mUllas -= ullasValue;
//		mPrashanth -= prashanthValue;
//		saveToPreference(mMukesh , mUllas , mPrashanth );
//		updateScores();
//	}
//
//	private void calculateTotal() {
//		float mukeshValue = Float.parseFloat(mMukeshEdit.getText().toString().isEmpty() ? "0" : mMukeshEdit.getText().toString());
//		float prashanthValue = Float.parseFloat(mPrashanthEdit.getText().toString().isEmpty() ? "0" : mPrashanthEdit.getText().toString());
//		float ullasValue = Float.parseFloat(mUllasEdit.getText().toString().isEmpty() ? "0" : mUllasEdit.getText().toString());
//		float avinValue = Float.parseFloat(mAvinEdit.getText().toString().isEmpty() ? "0" : mAvinEdit.getText().toString());
//		TextView et = (TextView) findViewById(R.id.current_total_text);
//		float total = mukeshValue + prashanthValue + ullasValue + avinValue;
//		et.setText("Rs " + total );
//	}
//
//	private void showDialog(String title, boolean isTake) {
//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		
//		LayoutInflater inflater = getLayoutInflater();
//		final View v = inflater.inflate(R.layout.dialog, null);
//	    builder.setView(v)
//	           .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//	               @Override
//	               public void onClick(DialogInterface dialog, int id) {
//	            	   EditText et = (EditText) v.findViewById(R.id.amount);
//	            	   String amountText= et.getText().toString();
//	            	   Float amount = 0f;
//	            	   if (amountText != null && !amountText.isEmpty()) {
//	            		   amount = Float.parseFloat(et.getText().toString());
//	            	   }
//	            	   saveAmount(amount);
//	               }
//	           })
//	           .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//	               public void onClick(DialogInterface dialog, int id) {
//	            	   
//	               }
//	           });  
//
//		builder.setTitle(title);
//		AlertDialog dialog = builder.create();
//		dialog.show();
//	}
//	
//	
//	private void saveAmount(float amount) {
//		
//		switch(mNameState) {
//			case mukeshgive:
//				mMukesh = mMukesh + amount;
//				break;
//			case mukeshtake:
//				mMukesh = mMukesh - amount;
//				break;
//			case ullasgive:
//				mUllas = mUllas + amount;
//				break;
//			case ullastake:
//				mUllas = mUllas - amount;
//				break;
//			case prashanthgive:
//				mPrashanth = mPrashanth + amount;
//				break;
//			case prashanthtake:
//				mPrashanth = mPrashanth - amount;
//				break;
//		}
//		saveToPreference(mMukesh, mUllas, mPrashanth);
//		updateScores();
//	}
//	
//	
//	private void saveToPreference(float mu, float ul, float pr) {
//		SharedPreferences preference = getSharedPreferences(BAKI_PREFERENCE, MODE_PRIVATE);
//		Editor edit = preference.edit();
//		edit.putFloat(MUKESH, mu);
//		edit.putFloat(PRASHANTH, pr);
//		edit.putFloat(ULLAS, ul);
//		edit.commit();
//	}

}
