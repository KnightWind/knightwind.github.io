package com.sktlab.bizconfmobile.model.db;

import com.sktlab.bizconfmobile.model.AppointmentConf;

import android.content.ContentValues;

public class AppointmentConfDbAdapter extends ObjectDBAdapter {
	
	public AppointmentConfDbAdapter(String paramString, String[] paramArrayOfString) {
		
		super(paramString, paramArrayOfString);
	}

	@Override
	protected ContentValues createContentValues(Object obj) {
		
		ContentValues cv = new ContentValues();
		
		if ((obj instanceof AppointmentConf)) {
			
			AppointmentConf meeting = (AppointmentConf) obj;
			
			cv.put(AppointmentConfDbTable.KEY_ACCOUNT_ID, meeting.getAccountId());
			cv.put(AppointmentConfDbTable.KEY_TITLE, meeting.getTitle());
			cv.put(AppointmentConfDbTable.KEY_STARTDATE, meeting.getStartTime());
			cv.put(AppointmentConfDbTable.KEY_ENDDATE, meeting.getEndTime());
			cv.put(AppointmentConfDbTable.KEY_ACCESS_NUMBER,meeting.getAccessNumber());
			cv.put(AppointmentConfDbTable.KEY_SECURITY_CODE, meeting.getSecurityCode());
			cv.put(AppointmentConfDbTable.KEY_NOTES, meeting.getNote());
			cv.put(AppointmentConfDbTable.KEY_INVITEES, meeting.getInvitees());					
			cv.put(AppointmentConfDbTable.KEY_MEETING_DATE, meeting.getDate());
			cv.put(AppointmentConfDbTable.KEY_EVENT_ID, meeting.getEventId());
		}
		
		return cv;
	}
}