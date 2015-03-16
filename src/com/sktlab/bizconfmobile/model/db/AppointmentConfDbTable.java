package com.sktlab.bizconfmobile.model.db;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class AppointmentConfDbTable {
	
	public static final String KEY_ID = "_id";
	public static final String KEY_ACCOUNT_ID = "accountid";
	public static final String KEY_CALENDAR_EVENT_ID = "calenadareventid";
	public static final String KEY_TITLE = "title";
	public static final String KEY_STARTDATE = "startdate";
	public static final String KEY_ENDDATE = "enddate";
	public static final String KEY_REPEAT = "repeat";
	public static final String KEY_ACCESS_NUMBER = "accessNumber";
	public static final String KEY_SECURITY_CODE = "securityCode";
	public static final String KEY_NOTES = "notes";
	public static final String KEY_INVITEES = "invitees";
	public static final String KEY_CALEDAR_DES = "calendarDescription";
	public static final String KEY_MEETING_DATE = "dateOfMeeting";
	public static final String KEY_EVENT_ID = "meetingEventId";
	
	public static final String MEETING_DB_TABLE = "meetings";

	private static final String TABLE_CREATE = "CREATE TABLE "
			+ MEETING_DB_TABLE + " (" 
			+ KEY_ID+ " integer primary key autoincrement, " 
			+ KEY_ACCOUNT_ID + " text, " 
			+ KEY_CALENDAR_EVENT_ID + " text, " 
			+ KEY_TITLE + " text, " 
			+ KEY_STARTDATE + " text, " 
			+ KEY_ENDDATE + " text, "
			+ KEY_REPEAT + " integer, " 
			+ KEY_ACCESS_NUMBER + " text, "
			+ KEY_SECURITY_CODE + " text, " 
			+ KEY_NOTES + " text, "
			+ KEY_INVITEES + " text, " 
			+ KEY_CALEDAR_DES + " text, "
			+ KEY_MEETING_DATE + " text, "
			+ KEY_EVENT_ID + " text"
			+ " );";

	
	public static String[] getAllColumns() {
		
		return new String[] { 
				KEY_ID, 
				KEY_ACCOUNT_ID, 
				KEY_CALENDAR_EVENT_ID,
				KEY_TITLE, 
				KEY_STARTDATE, 
				KEY_ENDDATE, 
				KEY_REPEAT,
				KEY_ACCESS_NUMBER, 
				KEY_SECURITY_CODE, 
				KEY_NOTES, 
				KEY_INVITEES,
				KEY_CALEDAR_DES,
				KEY_MEETING_DATE,
				KEY_EVENT_ID};
	}

	public static void onCreate(SQLiteDatabase paramSQLiteDatabase) {

		try {

			paramSQLiteDatabase.execSQL(TABLE_CREATE);
			return;

		} catch (SQLException localSQLException) {
			
			//Util.BIZ_CONF_DEBUG("MeetingDbTable create error", "exception: " + localSQLException.getMessage());
		}
	}

	public static void onUpgrade(SQLiteDatabase paramSQLiteDatabase,
			int paramInt1, int paramInt2) {

		paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MEETING_DB_TABLE);
		onCreate(paramSQLiteDatabase);
	}
}