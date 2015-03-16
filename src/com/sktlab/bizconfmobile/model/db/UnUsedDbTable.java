package com.sktlab.bizconfmobile.model.db;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class UnUsedDbTable {
	public static final String KEY_EMAIL_ADDRESS = "emailaddress";
	public static final String KEY_EMAIL_TYPE = "emailtype";
	public static final String KEY_MEETING_ID = "meetingid";
	public static final String KEY_NAME = "name";
	public static final String MEETING_CONTACT_DB_TABLE = "meeting_contact";
	private static final String TABLE_MEETING_CONTACT_CREATE = "create table meeting_contact (_id integer primary key autoincrement, meetingid integer, name text, emailtype text, emailaddress text);";

	public static String[] getAllColumns() {
		return new String[] { "name", "emailtype", "emailaddress", "meetingid" };
	}

	public static void onCreate(SQLiteDatabase paramSQLiteDatabase) {
		try {
			paramSQLiteDatabase
					.execSQL("create table meeting_contact (_id integer primary key autoincrement, meetingid integer, name text, emailtype text, emailaddress text);");
			return;
		} catch (SQLException localSQLException) {
		}
	}

	public static void onUpgrade(SQLiteDatabase paramSQLiteDatabase,
			int paramInt1, int paramInt2) {
		paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS "
				+ MEETING_CONTACT_DB_TABLE);
		onCreate(paramSQLiteDatabase);
	}
}
