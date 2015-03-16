package com.sktlab.bizconfmobile.model.db;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class HistoryDbTable {
	public static final String HISTORY_DB_TABLE = "history";
	
	public static final String KEY_ID = "_id";
	public static final String KEY_ACCOUNT_ID = "historyAccountId";
	public static final String KEY_ACCOUNT_NAME = "historyAccountName";
	public static final String KEY_CONF_TITLE = "historyConfTitle";
	public static final String KEY_CONF_CODE = "historyConfCode";
	public static final String KEY_END_TIME = "historyConfEndTime";
	public static final String KEY_START_TIME = "historyConfStartTime";
	
  	public static final String TABLE_CREATE 
		= "CREATE TABLE " + HISTORY_DB_TABLE + " ("
				+ KEY_ID + " integer primary key autoincrement, "
				+ KEY_ACCOUNT_ID + " text, " 
				+ KEY_ACCOUNT_NAME + " text, "
				+ KEY_CONF_TITLE + " text, "
				+ KEY_CONF_CODE  + " text, " 
				+ KEY_START_TIME + " text, "
				+ KEY_END_TIME + " text"
				+ " );";
  	
	public static String[] getAllColumns() {
		
		return new String[] { 
				KEY_ACCOUNT_ID,
				KEY_ACCOUNT_NAME,
				KEY_CONF_TITLE,
				KEY_CONF_CODE,
				KEY_START_TIME,
				KEY_END_TIME
				};
	}

	public static void onCreate(SQLiteDatabase paramSQLiteDatabase) {
		try {
			paramSQLiteDatabase
					.execSQL(TABLE_CREATE);
			return;
		} catch (SQLException localSQLException) {
		}
	}

	public static void onUpgrade(SQLiteDatabase paramSQLiteDatabase,
			int paramInt1, int paramInt2) {
		
		paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS " + HISTORY_DB_TABLE);
		onCreate(paramSQLiteDatabase);
	}
}