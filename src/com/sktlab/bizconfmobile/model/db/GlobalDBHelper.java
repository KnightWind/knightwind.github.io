package com.sktlab.bizconfmobile.model.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class GlobalDBHelper extends SQLiteOpenHelper {
	
	public static final String TAG = "GlobalDBHelper";
	
	private static final String DATABASE_NAME = "applicationdata";
	private static final int DATABASE_VERSION = 1;

	public GlobalDBHelper(Context paramContext) {
		
		super(paramContext, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public static List<String> GetColumns(SQLiteDatabase paramSQLiteDatabase,
													String paramString) {
		
		Cursor localCursor = null;
		ArrayList<String> localList = null;
		
		try {
			
			localCursor = 
					paramSQLiteDatabase.rawQuery("select * from " + paramString + " limit 1", null);

			if (localCursor != null) {
				
				localList = 
						new ArrayList(Arrays.asList(localCursor.getColumnNames()));
			}

		} catch (Exception localException) {
			
			Log.v(paramString, localException.getMessage(), localException);
			localException.printStackTrace();			
		} finally {
			
			if (localCursor != null) {
				
				localCursor.close();
			}		
		}

		return localList;
	}

	public static String join(List<String> paramList, String paramString) {
		
		StringBuilder localStringBuilder = new StringBuilder();
		
		int i = paramList.size();
		
		for (int j = 0; j < i; j++) {
			
			if (j != 0) {
				
				localStringBuilder.append(paramString);
			}
				
			localStringBuilder.append(paramList.get(j));
		}
		
		return localStringBuilder.toString();
	}

	@Override
	public void onCreate(SQLiteDatabase sqlDb) {
		
		AccountsDbTable.onCreate(sqlDb);
		
		HistoryDbTable.onCreate(sqlDb);
		
		AppointmentConfDbTable.onCreate(sqlDb);
		
		BillingReferenceDbTable.onCreate(sqlDb);
		
		UnUsedDbTable.onCreate(sqlDb);
		
		ShangHaiNumSegmentDbTable.onCreate(sqlDb);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqlDb, int oldVersion,
			int newVersion) {
		
		AccountsDbTable.onUpgrade(sqlDb, oldVersion, newVersion);
		
		HistoryDbTable.onUpgrade(sqlDb, oldVersion, newVersion);
		
		AppointmentConfDbTable.onUpgrade(sqlDb, oldVersion, newVersion);
		
		BillingReferenceDbTable.onUpgrade(sqlDb, oldVersion,newVersion);
		
		UnUsedDbTable.onUpgrade(sqlDb, oldVersion,newVersion);
		
		ShangHaiNumSegmentDbTable.onUpgrade(sqlDb, oldVersion,newVersion);	
	}
}