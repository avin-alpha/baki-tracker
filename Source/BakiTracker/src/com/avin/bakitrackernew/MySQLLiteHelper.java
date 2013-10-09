package com.avin.bakitrackernew;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLLiteHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "participants.db";
	private static final int DATABASE_VERSION = 1;
	
	public static final String TABLE_PARTICIPANTS = "participants";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "participant";
	public static final String COLUMN_AMOUNT = "amount";
	
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_PARTICIPANTS + " ("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_NAME + " text not null, "
			+ COLUMN_AMOUNT + " currency);"; 

	public MySQLLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(MySQLLiteHelper.class.getName(),
	            "Upgrading database from version " + oldVersion + " to "
	                + newVersion + ", which will destroy all old data");
	        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARTICIPANTS);
	        onCreate(db);
	}

}
