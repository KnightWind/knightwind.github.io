package com.sktlab.bizconfmobile.model.db;

import com.sktlab.bizconfmobile.util.Util;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ShangHaiNumSegmentDbTable {
	
	public static final String TAG = "ShangHaiNumSegmentDbTable";
	
	public static final String SEGMENT_DB_TABLE = "numsegment";
	
	public static final String KEY_ID = "_id";
	public static final String KEY_PREFIX = "phoneNumPrefix";
	public static final String KEY_START = "phoneNumLocStart";
	public static final String KEY_END = "phoneNumLocEnd";
	
  	public static final String TABLE_CREATE 
  	
		= "CREATE TABLE " + SEGMENT_DB_TABLE + " ("
				+ KEY_ID + " integer primary key autoincrement, "
				+ KEY_PREFIX + " int, " 
				+ KEY_START + " int, "
				+ KEY_END + " int"
				+ " );";
  	
	public static String[] getAllColumns() {
		
		return new String[] { 
				KEY_PREFIX,
				KEY_START,
				KEY_END
				};
	}

	public static void onCreate(SQLiteDatabase db) {
		
		try {
			
			db.execSQL(TABLE_CREATE);
		} catch (SQLException localSQLException) {
			
			Util.BIZ_CONF_DEBUG(TAG, "create db error");
		}
	}

	public static void onUpgrade(SQLiteDatabase paramSQLiteDatabase,
			int paramInt1, int paramInt2) {
		
		paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SEGMENT_DB_TABLE);
		onCreate(paramSQLiteDatabase);
	}
}