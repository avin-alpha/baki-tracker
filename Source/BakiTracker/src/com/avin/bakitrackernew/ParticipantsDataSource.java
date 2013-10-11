package com.avin.bakitrackernew;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ParticipantsDataSource {
	
	private MySQLLiteHelper dbHelper = null;
	private SQLiteDatabase dataBase = null;
	
	private String[] allColumns = {MySQLLiteHelper.COLUMN_ID, MySQLLiteHelper.COLUMN_NAME,
			MySQLLiteHelper.COLUMN_AMOUNT};

	public ParticipantsDataSource(Context context) {
		dbHelper = new MySQLLiteHelper(context);
	}
	
	public void open() throws SQLException {
		dataBase = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		dbHelper.close();
	}
	
	public Participant createParticipant(String name) {
		ContentValues values = new ContentValues();
		values.put(MySQLLiteHelper.COLUMN_NAME, name);
		
		long insertId = dataBase.insert(MySQLLiteHelper.TABLE_PARTICIPANTS, null, values);
		
		Cursor cursor = dataBase.query(MySQLLiteHelper.TABLE_PARTICIPANTS, allColumns, MySQLLiteHelper.COLUMN_ID + " = "
							+ insertId, null, null, null, null);
		cursor.moveToFirst();
		Participant newParticipant = cursorToParticipant(cursor);
		cursor.close();
		
		return newParticipant;
	}
	
	public List<Participant> getAllParticipants() {
		List<Participant> participants = new ArrayList<Participant>();
		
		Cursor cursor = dataBase.query(MySQLLiteHelper.TABLE_PARTICIPANTS, allColumns, null, null, null, null, null);
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()) {
			Participant participant = cursorToParticipant(cursor);
			participants.add(participant);
			cursor.moveToNext();
		}
		
		cursor.close();
		return participants;
	}
	
	public void deletePaticipant(Participant participant) {
		long id = participant.getId();
		dataBase.delete(MySQLLiteHelper.TABLE_PARTICIPANTS, MySQLLiteHelper.COLUMN_ID + " = " + id, null);
	}
	
	public void updateAmount(Participant participant, double amount) {
		long id = participant.getId();
		ContentValues values = new ContentValues();
		values.put(MySQLLiteHelper.COLUMN_AMOUNT, amount);
		dataBase.update(MySQLLiteHelper.TABLE_PARTICIPANTS, values, MySQLLiteHelper.COLUMN_ID + " = " + id, null);
	}
	
	private Participant cursorToParticipant(Cursor cursor) {
		Participant newParticipant = new Participant();
		newParticipant.setId(cursor.getLong(0));
		newParticipant.setName(cursor.getString(1));
		newParticipant.setAmount(cursor.getDouble(2));
		return newParticipant;
	}
}
