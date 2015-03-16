package com.sktlab.bizconfmobile.model.db;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class AccountsDbTable {
	public static final String ACCOUNTS_DB_TABLE = "accounts";
	public static final String KEY_ID = "_id";
	public static final String KEY_DIAL_OUT_ENABLE = "dialoutenable";
	public static final String KEY_CONFERENCE_CODE = "conferencecode";
	public static final String KEY_DIALINNUMBER = "dialinnumber";
	public static final String KEY_ACCOUNT_NAME = "accountname";
	public static final String KEY_ISACTIVE = "isactive";
	public static final String KEY_SECURITY_CODE_ENABLE = "securitycodeenable";
	public static final String KEY_SECURITY_CODE = "securitycode";
	//There is two type of conference account: normal account and moderator account
	//this field is used to separate the two types
	//if a account's moderator password value is "null",we can consider it is a normal account,
	//otherwise it is a moderator account
	public static final String KEY_MODERATOR_PW = "moderatorpw";
	public static final String KEY_DIAL_OUT_NUMBER = "dialoutnumber";
	
	public static final String NORMAL_ACCOUNT_MODERATOR_PW = "null";
	
  	public static final String TABLE_CREATE 
		= "CREATE TABLE " + ACCOUNTS_DB_TABLE + " ("
				+ KEY_ID + " integer primary key autoincrement, "
				+ KEY_DIAL_OUT_ENABLE + " integer, "
				+ KEY_CONFERENCE_CODE + " text, " 
				+ KEY_DIALINNUMBER  + " text, " 
				+ KEY_ACCOUNT_NAME + " text, "
				+ KEY_ISACTIVE + " integer, "
				+ KEY_SECURITY_CODE_ENABLE + " integer, "
				+ KEY_SECURITY_CODE + " text, "
				+ KEY_MODERATOR_PW + " text, "
				+ KEY_DIAL_OUT_NUMBER + " text"
				+ " );";
  
	public static String[] getAllColumns() {
		
		return new String[] { 
				KEY_ID,
				KEY_ACCOUNT_NAME, 
				KEY_CONFERENCE_CODE,
				KEY_DIALINNUMBER,
				KEY_MODERATOR_PW,
				KEY_DIAL_OUT_NUMBER,
			    KEY_SECURITY_CODE, 
				KEY_ISACTIVE, 
				KEY_DIAL_OUT_ENABLE,
				KEY_SECURITY_CODE_ENABLE };
	}

	public static void onCreate(SQLiteDatabase paramSQLiteDatabase) {
		
		try {
			paramSQLiteDatabase.execSQL(TABLE_CREATE);
			return;
		} catch (SQLException localSQLException) {
		}
	}

	public static void onUpgrade(SQLiteDatabase paramSQLiteDatabase,
			int paramInt1, int paramInt2) {
		
		paramSQLiteDatabase
				.execSQL("DROP TABLE IF EXISTS " + ACCOUNTS_DB_TABLE);
		onCreate(paramSQLiteDatabase);
	}
}