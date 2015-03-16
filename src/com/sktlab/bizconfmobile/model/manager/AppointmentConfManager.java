package com.sktlab.bizconfmobile.model.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import android.database.Cursor;
import android.net.Uri;

import com.sktlab.bizconfmobile.activity.AppClass;
import com.sktlab.bizconfmobile.model.CalendarEvent;
import com.sktlab.bizconfmobile.model.AppointmentConf;
import com.sktlab.bizconfmobile.model.db.AppointmentConfDbAdapter;
import com.sktlab.bizconfmobile.model.db.AppointmentConfDbTable;
import com.sktlab.bizconfmobile.util.CalendarUtil;
import com.sktlab.bizconfmobile.util.Util;

public class AppointmentConfManager {
	
	public static final String TAG = "AppointmentConfManager";
	
    private ArrayList<AppointmentConf> appointmentConf = null;
	private Hashtable<String, ArrayList<AppointmentConf>> day2AppointmentConf;
	private AppointmentConfDbAdapter appointmentConfDb;
	
	private AppointmentConfManager() {
		
		day2AppointmentConf = new Hashtable<String, ArrayList<AppointmentConf>>();
		appointmentConf = new ArrayList<AppointmentConf>(); 
		appointmentConfDb = new AppointmentConfDbAdapter(AppointmentConfDbTable.MEETING_DB_TABLE, 
				AppointmentConfDbTable.getAllColumns());
	}
	
	private static class InstanceHolder {

		private static AppointmentConfManager instance = new AppointmentConfManager();
	}
	
	public static AppointmentConfManager getInstance() {

		return InstanceHolder.instance;
	}
	
	public boolean hasRecord(Long date) {
		
		String dateFomat = CalendarUtil.getFomatDateStr(date);
		
		//Util.BIZ_CONF_DEBUG(TAG, "date fomat str: " + dateFomat);
		return day2AppointmentConf.containsKey(dateFomat);
	}
	
	public void insertMeetingToDb(AppointmentConf newMeeting) {
		
		if (null != newMeeting) {
			
			appointmentConfDb.open();
			
			long id = appointmentConfDb.insertObject(newMeeting);		
			newMeeting.setId(id);
			
			appointmentConfDb.close();
		}
	}
	
	public boolean addMeeting(AppointmentConf newMeeting) {

		boolean result = appointmentConf.add(newMeeting);

		do {

			if (!result) {

				//Util.BIZ_CONF_DEBUG(TAG, "add meeting failed");
				break;
			}

			String key = newMeeting.getDate();

			if (day2AppointmentConf.containsKey(key)) {

				//Util.BIZ_CONF_DEBUG(TAG, "contains key: " + key);

				day2AppointmentConf.get(key).add(newMeeting);
				break;
			}

			//Util.BIZ_CONF_DEBUG(TAG, "add new meeting key: " + key);
			
			ArrayList<AppointmentConf> dateMeeting = new ArrayList<AppointmentConf>();
			dateMeeting.add(newMeeting);

			day2AppointmentConf.put(key, dateMeeting);
		} while (false);

		return result;
	}
	
	public ArrayList<AppointmentConf> getCellMeetings(String dateFomatStr) {
		
		ArrayList<AppointmentConf> meetings = null;
		
		if (day2AppointmentConf.containsKey(dateFomatStr)) {
			
			meetings = day2AppointmentConf.get(dateFomatStr);
		}
		
		return meetings;
	}
	
	public Hashtable<String, ArrayList<AppointmentConf>> getDayToMeetings() {
		return day2AppointmentConf;
	}

	public void setDayToMeetings(Hashtable<String, ArrayList<AppointmentConf>> dayToConfs) {
		this.day2AppointmentConf = dayToConfs;
	}

	public  ArrayList<AppointmentConf> getAllMeetings() {
		return appointmentConf;
	}

	public  void setMeetings(ArrayList<AppointmentConf> confs) {
		appointmentConf = confs;
	}	
	
	public void deleteMeetingsInDb(ArrayList<Long> meetingIds) {
		
		appointmentConfDb.open();
		
		for (long id : meetingIds) {
			
			appointmentConfDb.deleteObject(id);
		}
		
		appointmentConfDb.close();
	}
	
	public void loadMeetingFromDb() {

		HashMap<String, CalendarEvent> systemEvents = 
				CalendarUtil.loadEventsFromSystemCalendar();
		
		appointmentConf.clear();
		day2AppointmentConf.clear();		
		appointmentConfDb.open();
			
		Cursor cursor = appointmentConfDb.fetchAllObjects(null, null);	
		
		ArrayList<Long> deleteMeetings = new ArrayList<Long>();
		
		while (cursor.moveToNext()) {
			
			//Util.BIZ_CONF_DEBUG(TAG, "meeting has data");
			long id = cursor.getLong(cursor
					.getColumnIndex(AppointmentConfDbTable.KEY_ID));
						
			String title = cursor.getString(cursor
					.getColumnIndex(AppointmentConfDbTable.KEY_TITLE));
			
			String eventId = cursor.getString(cursor
					.getColumnIndex(AppointmentConfDbTable.KEY_EVENT_ID));
			
			//Util.BIZ_CONF_DEBUG(TAG, "load calendar event id: " + eventId + "title: " + title);
			
			if (!systemEvents.containsKey(eventId)) {
				
				deleteMeetings.add(id);
				continue;
			}
			//read system event title
			title = systemEvents.get(eventId).getEventTitle();
			
			String accountId = cursor.getString(cursor
					.getColumnIndex(AppointmentConfDbTable.KEY_ACCOUNT_ID));
			
			String startDate = cursor.getString(cursor
					.getColumnIndex(AppointmentConfDbTable.KEY_STARTDATE));
			
			String endDate = cursor.getString(cursor
					.getColumnIndex(AppointmentConfDbTable.KEY_ENDDATE));
			
			String accessNumber = cursor.getString(cursor
					.getColumnIndex(AppointmentConfDbTable.KEY_ACCESS_NUMBER));
			
			String notes = cursor.getString(cursor
					.getColumnIndex(AppointmentConfDbTable.KEY_NOTES));
			
			String invitees = cursor.getString(cursor
					.getColumnIndex(AppointmentConfDbTable.KEY_INVITEES));
			
			String date = cursor.getString(cursor
					.getColumnIndex(AppointmentConfDbTable.KEY_MEETING_DATE));
			
			AppointmentConf meeting = new AppointmentConf();
			meeting.setAccountId(accountId);
			meeting.setTitle(title);
			meeting.setStartTime(Long.valueOf(startDate));
			meeting.setEndTime(Long.valueOf(endDate));
			meeting.setAccessNumber(accessNumber);
			meeting.setNote(notes);
			meeting.setInvitees(invitees);						
			meeting.setDate(date);
			meeting.setEventId(eventId);
			meeting.setId(id);
			
			appointmentConf.add(meeting);
		
			if (day2AppointmentConf.containsKey(date)) {
				
				//Util.BIZ_CONF_DEBUG(TAG, "add meeting,date: " + date);
				day2AppointmentConf.get(date).add(meeting);
			} else {
				
				ArrayList<AppointmentConf> dateMeetings = new ArrayList<AppointmentConf>();
				dateMeetings.add(meeting);
				day2AppointmentConf.put(date, dateMeetings);
				//Util.BIZ_CONF_DEBUG(TAG, "new a meeting: date:" + date);
			}
			
		};

		if (cursor != null) {

			cursor.close();
		}
		
		for(Long deleteId : deleteMeetings) {
			
			appointmentConfDb.deleteObject(deleteId);
		}
		
		appointmentConfDb.close();		
		//Util.BIZ_CONF_DEBUG(TAG, "load meeting number: " + dateToMeeting.size());
	}
}
