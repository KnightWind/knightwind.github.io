package com.sktlab.bizconfmobile.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Colors;
import android.provider.CalendarContract.Events;
import android.text.format.Time;
import android.widget.Toast;

import com.sktlab.bizconfmobile.activity.AppClass;
import com.sktlab.bizconfmobile.activity.RepeatSelectionActivity;
import com.sktlab.bizconfmobile.model.CalendarEvent;
import com.sktlab.bizconfmobile.model.AppointmentConf;

public class CalendarUtil {
	
	public static String TAG = "CalendarUtil";
	
	// Projection array. Creating indices for this array instead of doing
	// dynamic lookups improves performance.
	//	public static final String[] EVENT_PROJECTION = new String[] {
	//	    Calendars._ID,                           // 0
	//	    Calendars.ACCOUNT_NAME,                  // 1
	//	    Calendars.CALENDAR_DISPLAY_NAME,         // 2
	//	    Calendars.OWNER_ACCOUNT                  // 3
	//	};
	  
	// The indices for the projection array above.
	public static final int PROJECTION_ID_INDEX = 0;
	public static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
	public static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
	public static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
	
    //URL to operate calendar
	public static String calanderURL = "content://com.android.calendar/calendars";
	public static String calanderEventURL = "content://com.android.calendar/events";
	public static String calanderRemiderURL = "content://com.android.calendar/reminders";

    
	public static void insertEvent(AppointmentConf meeting){

		//Util.BIZ_CONF_DEBUG(TAG, "insert event to system calendar");
		
		Context ctx =  AppClass.getInstance();
		
		long calId = 0;
		
		Cursor userCursor = ctx.getContentResolver()
				.query(Uri.parse(calanderURL), null, null, null, null);
		
		if(userCursor.getCount() > 0){
			
			userCursor.moveToFirst();
				
			calId = userCursor.getLong(PROJECTION_ID_INDEX);
		}
			
		ContentResolver cr = ctx.getContentResolver();
		ContentValues values = new ContentValues();
		
		values.put(CalendarEventsColumns.CALENDAR_ID, calId);
		values.put(CalendarEventsColumns.HAS_ALARM,1);
		values.put(CalendarEventsColumns.TITLE, meeting.getTitle());
		values.put(CalendarEventsColumns.DTSTART, meeting.getStartTime());
		values.put(CalendarEventsColumns.DTEND, meeting.getEndTime());
		values.put(CalendarEventsColumns.DESCRIPTION, meeting.getNote());
		values.put(CalendarEventsColumns.EVENT_TIMEZONE, Time.getCurrentTimezone());
		
		int freq = meeting.getFreq();
		String repeatCount = meeting.getRepeatCount();
		
		if (freq != RepeatSelectionActivity.PERIOD_NONE) {
			
			String strFreq = getFreqStr(freq);
			
			if (!Util.isEmpty(strFreq)) {
				
				String rrule = "FREQ=" + strFreq + ";" +
							   "COUNT=" + repeatCount + ";";
				
				//Util.BIZ_CONF_DEBUG(TAG, "rrule:" + rrule);
				
				values.put(CalendarEventsColumns.RRULE, rrule);
			}
		}
		
		Uri uri = cr.insert(Uri.parse(calanderEventURL), values);

		// get the event ID that is the last element in the Uri
		long eventID = Long.parseLong(uri.getLastPathSegment());
		
		if (eventID > 0) {
			
			meeting.setEventId(String.valueOf(eventID));
		}
		
		//Toast.makeText(ctx, "插入事件成功!!! eventId: " + eventID, Toast.LENGTH_LONG).show();
		
		values.clear();
		values.put(CalendarRemindersColumns.EVENT_ID, eventID);
		//reminder minutes
		values.put(CalendarRemindersColumns.MINUTES, 5);
		values.put(CalendarRemindersColumns.METHOD, CalendarRemindersColumns.METHOD_ALERT);
		
		Uri reminderUri = cr.insert(Uri.parse(calanderRemiderURL), values);
		
		//Toast.makeText(ctx, "插入提醒成功!!!", Toast.LENGTH_LONG).show();
	}
	
	public static String getFreqStr(int freq) {
		
		switch (freq) {
		
		case RepeatSelectionActivity.PERIOD_DAY:
			return "DAILY";
		case RepeatSelectionActivity.PERIOD_WEEK:
			return "WEEKLY";
		case RepeatSelectionActivity.PERIOD_MONTH:
			return "MONTHLY";
		case RepeatSelectionActivity.PERIOD_YEAR:
			return "YEARLY";
		}
		
		return null;
	}
	
	public static void deleteEvent(long eventId) {
		
		 Uri eventUri = Uri.parse(calanderEventURL); 

		 Uri deleteUri = ContentUris.withAppendedId(eventUri, eventId); 
		 AppClass.getInstance().getContentResolver().delete(deleteUri, null, null);
	}
	
	public static String getFomatDateStr(Long date) {
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date);
		
		String dateFormatStr = "yyyy-MM-dd";
		TimeZone timeZone = TimeZone.getDefault();
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(dateFormatStr);
		
		localSimpleDateFormat.setTimeZone(timeZone);
		String str = localSimpleDateFormat.format(date);
		
		return str;
	}
	
	public static HashMap<String,CalendarEvent> loadEventsFromSystemCalendar() {
		
		HashMap<String,CalendarEvent> events = new HashMap<String,CalendarEvent>();
		
		// 日历里面相应的Event的URI
		Uri uri = Uri.parse(calanderEventURL);
		
		// String[] column=new String[]{"_id","title"};
		Cursor cur = AppClass.getInstance().getContentResolver()
								.query(uri, null, null, null, null);

		while (cur.moveToNext()) {
			
			String id = cur.getString(cur.getColumnIndex("_id"));
			
			String title = cur.getString(cur
					.getColumnIndex("title"));
			
			String status = cur.getString(cur
					.getColumnIndex("eventStatus"));
			
			int deleted = cur.getInt(cur.getColumnIndex("deleted"));
			
//			Util.BIZ_CONF_DEBUG(TAG, "event id: " + id + " title: " + title 
//								+ " status: " + status
//								+ " deleted: " + deleted);
			
			if (deleted == 1) {
				
				continue;
			}
			
			CalendarEvent event = new CalendarEvent();
			
			event.setEventId(id);
			event.setEventTitle(title);
			
			events.put(id, event);
		}
		
		if (null != cur) {
			
			cur.close();
		}
		
		return events;
	}
	
	public interface CalendarRemindersColumns {
        /**
         * The event the reminder belongs to. Column name.
         * <P>Type: INTEGER (foreign key to the Events table)</P>
         */
        public static final String EVENT_ID = "event_id";

        /**
         * The minutes prior to the event that the alarm should ring.  -1
         * specifies that we should use the default value for the system.
         * Column name.
         * <P>Type: INTEGER</P>
         */
        public static final String MINUTES = "minutes";

        /**
         * Passing this as a minutes value will use the default reminder
         * minutes.
         */
        public static final int MINUTES_DEFAULT = -1;

        /**
         * The alarm method, as set on the server. {@link #METHOD_DEFAULT},
         * {@link #METHOD_ALERT}, {@link #METHOD_EMAIL}, {@link #METHOD_SMS} and
         * {@link #METHOD_ALARM} are possible values; the device will only
         * process {@link #METHOD_DEFAULT} and {@link #METHOD_ALERT} reminders
         * (the other types are simply stored so we can send the same reminder
         * info back to the server when we make changes).
         */
        public static final String METHOD = "method";

        public static final int METHOD_DEFAULT = 0;
        public static final int METHOD_ALERT = 1;
        public static final int METHOD_EMAIL = 2;
        public static final int METHOD_SMS = 3;
        public static final int METHOD_ALARM = 4;
    }

	 /**
	  * This interface had been defined in system whose api >= 14, but our software should compatible for
	  * 2.3 platform whose api level is 9
     * Columns from the Events table that other tables join into themselves.
     */
    public interface CalendarEventsColumns {

        /**
         * The {@link Calendars#_ID} of the calendar the event belongs to.
         * Column name.
         * <P>Type: INTEGER</P>
         */
        public static final String CALENDAR_ID = "calendar_id";

        /**
         * The title of the event. Column name.
         * <P>Type: TEXT</P>
         */
        public static final String TITLE = "title";

        /**
         * The description of the event. Column name.
         * <P>Type: TEXT</P>
         */
        public static final String DESCRIPTION = "description";

        /**
         * Where the event takes place. Column name.
         * <P>Type: TEXT</P>
         */
        public static final String EVENT_LOCATION = "eventLocation";

        /**
         * A secondary color for the individual event. This should only be
         * updated by the sync adapter for a given account.
         * <P>Type: INTEGER</P>
         */
        public static final String EVENT_COLOR = "eventColor";

        /**
         * A secondary color key for the individual event. NULL or an empty
         * string are reserved for indicating that the event does not use a key
         * for looking up the color. The provider will update
         * {@link #EVENT_COLOR} automatically when a valid key is written to
         * this column. The key must reference an existing row of the
         * {@link Colors} table. @see Colors
         * <P>
         * Type: TEXT
         * </P>
         */
        public static final String EVENT_COLOR_KEY = "eventColor_index";

        /**
         * This will be {@link #EVENT_COLOR} if it is not null; otherwise, this will be
         * {@link Calendars#CALENDAR_COLOR}.
         * Read-only value. To modify, write to {@link #EVENT_COLOR} or
         * {@link Calendars#CALENDAR_COLOR} directly.
         *<P>
         *     Type: INTEGER
         *</P>
         */
        public static final String DISPLAY_COLOR = "displayColor";

        /**
         * The event status. Column name.
         * <P>Type: INTEGER (one of {@link #STATUS_TENTATIVE}...)</P>
         */
        public static final String STATUS = "eventStatus";

        public static final int STATUS_TENTATIVE = 0;
        public static final int STATUS_CONFIRMED = 1;
        public static final int STATUS_CANCELED = 2;

        /**
         * This is a copy of the attendee status for the owner of this event.
         * This field is copied here so that we can efficiently filter out
         * events that are declined without having to look in the Attendees
         * table. Column name.
         *
         * <P>Type: INTEGER (int)</P>
         */
        public static final String SELF_ATTENDEE_STATUS = "selfAttendeeStatus";

        /**
         * This column is available for use by sync adapters. Column name.
         * <P>Type: TEXT</P>
         */
        public static final String SYNC_DATA1 = "sync_data1";

        /**
         * This column is available for use by sync adapters. Column name.
         * <P>Type: TEXT</P>
         */
        public static final String SYNC_DATA2 = "sync_data2";

        /**
         * This column is available for use by sync adapters. Column name.
         * <P>Type: TEXT</P>
         */
        public static final String SYNC_DATA3 = "sync_data3";

        /**
         * This column is available for use by sync adapters. Column name.
         * <P>Type: TEXT</P>
         */
        public static final String SYNC_DATA4 = "sync_data4";

        /**
         * This column is available for use by sync adapters. Column name.
         * <P>Type: TEXT</P>
         */
        public static final String SYNC_DATA5 = "sync_data5";

        /**
         * This column is available for use by sync adapters. Column name.
         * <P>Type: TEXT</P>
         */
        public static final String SYNC_DATA6 = "sync_data6";

        /**
         * This column is available for use by sync adapters. Column name.
         * <P>Type: TEXT</P>
         */
        public static final String SYNC_DATA7 = "sync_data7";

        /**
         * This column is available for use by sync adapters. Column name.
         * <P>Type: TEXT</P>
         */
        public static final String SYNC_DATA8 = "sync_data8";

        /**
         * This column is available for use by sync adapters. Column name.
         * <P>Type: TEXT</P>
         */
        public static final String SYNC_DATA9 = "sync_data9";

        /**
         * This column is available for use by sync adapters. Column name.
         * <P>Type: TEXT</P>
         */
        public static final String SYNC_DATA10 = "sync_data10";

        /**
         * Used to indicate that a row is not a real event but an original copy of a locally
         * modified event. A copy is made when an event changes from non-dirty to dirty and the
         * event is on a calendar with {@link Calendars#CAN_PARTIALLY_UPDATE} set to 1. This copy
         * does not get expanded in the instances table and is only visible in queries made by a
         * sync adapter. The copy gets removed when the event is changed back to non-dirty by a
         * sync adapter.
         * <P>Type: INTEGER (boolean)</P>
         */
        public static final String LAST_SYNCED = "lastSynced";

        /**
         * The time the event starts in UTC millis since epoch. Column name.
         * <P>Type: INTEGER (long; millis since epoch)</P>
         */
        public static final String DTSTART = "dtstart";

        /**
         * The time the event ends in UTC millis since epoch. Column name.
         * <P>Type: INTEGER (long; millis since epoch)</P>
         */
        public static final String DTEND = "dtend";

        /**
         * The duration of the event in RFC2445 format. Column name.
         * <P>Type: TEXT (duration in RFC2445 format)</P>
         */
        public static final String DURATION = "duration";

        /**
         * The timezone for the event. Column name.
         * <P>Type: TEXT</P>
         */
        public static final String EVENT_TIMEZONE = "eventTimezone";

        /**
         * The timezone for the end time of the event. Column name.
         * <P>Type: TEXT</P>
         */
        public static final String EVENT_END_TIMEZONE = "eventEndTimezone";

        /**
         * Is the event all day (time zone independent). Column name.
         * <P>Type: INTEGER (boolean)</P>
         */
        public static final String ALL_DAY = "allDay";

        /**
         * Defines how the event shows up for others when the calendar is
         * shared. Column name.
         * <P>Type: INTEGER (One of {@link #ACCESS_DEFAULT}, ...)</P>
         */
        public static final String ACCESS_LEVEL = "accessLevel";

        /**
         * Default access is controlled by the server and will be treated as
         * public on the device.
         */
        public static final int ACCESS_DEFAULT = 0;
        /**
         * Confidential is not used by the app.
         */
        public static final int ACCESS_CONFIDENTIAL = 1;
        /**
         * Private shares the event as a free/busy slot with no details.
         */
        public static final int ACCESS_PRIVATE = 2;
        /**
         * Public makes the contents visible to anyone with access to the
         * calendar.
         */
        public static final int ACCESS_PUBLIC = 3;

        /**
         * If this event counts as busy time or is still free time that can be
         * scheduled over. Column name.
         * <P>
         * Type: INTEGER (One of {@link #AVAILABILITY_BUSY},
         * {@link #AVAILABILITY_FREE}, {@link #AVAILABILITY_TENTATIVE})
         * </P>
         */
        public static final String AVAILABILITY = "availability";

        /**
         * Indicates that this event takes up time and will conflict with other
         * events.
         */
        public static final int AVAILABILITY_BUSY = 0;
        /**
         * Indicates that this event is free time and will not conflict with
         * other events.
         */
        public static final int AVAILABILITY_FREE = 1;
        /**
         * Indicates that the owner's availability may change, but should be
         * considered busy time that will conflict.
         */
        public static final int AVAILABILITY_TENTATIVE = 2;

        /**
         * Whether the event has an alarm or not. Column name.
         * <P>Type: INTEGER (boolean)</P>
         */
        public static final String HAS_ALARM = "hasAlarm";

        /**
         * Whether the event has extended properties or not. Column name.
         * <P>Type: INTEGER (boolean)</P>
         */
        public static final String HAS_EXTENDED_PROPERTIES = "hasExtendedProperties";

        /**
         * The recurrence rule for the event. Column name.
         * <P>Type: TEXT</P>
         */
        public static final String RRULE = "rrule";

        /**
         * The recurrence dates for the event. Column name.
         * <P>Type: TEXT</P>
         */
        public static final String RDATE = "rdate";

        /**
         * The recurrence exception rule for the event. Column name.
         * <P>Type: TEXT</P>
         */
        public static final String EXRULE = "exrule";

        /**
         * The recurrence exception dates for the event. Column name.
         * <P>Type: TEXT</P>
         */
        public static final String EXDATE = "exdate";

        /**
         * The {@link Events#_ID} of the original recurring event for which this
         * event is an exception. Column name.
         * <P>Type: TEXT</P>
         */
        public static final String ORIGINAL_ID = "original_id";

        /**
         * The _sync_id of the original recurring event for which this event is
         * an exception. The provider should keep the original_id in sync when
         * this is updated. Column name.
         * <P>Type: TEXT</P>
         */
        public static final String ORIGINAL_SYNC_ID = "original_sync_id";

        /**
         * The original instance time of the recurring event for which this
         * event is an exception. Column name.
         * <P>Type: INTEGER (long; millis since epoch)</P>
         */
        public static final String ORIGINAL_INSTANCE_TIME = "originalInstanceTime";

        /**
         * The allDay status (true or false) of the original recurring event
         * for which this event is an exception. Column name.
         * <P>Type: INTEGER (boolean)</P>
         */
        public static final String ORIGINAL_ALL_DAY = "originalAllDay";

        /**
         * The last date this event repeats on, or NULL if it never ends. Column
         * name.
         * <P>Type: INTEGER (long; millis since epoch)</P>
         */
        public static final String LAST_DATE = "lastDate";

        /**
         * Whether the event has attendee information.  True if the event
         * has full attendee data, false if the event has information about
         * self only. Column name.
         * <P>Type: INTEGER (boolean)</P>
         */
        public static final String HAS_ATTENDEE_DATA = "hasAttendeeData";

        /**
         * Whether guests can modify the event. Column name.
         * <P>Type: INTEGER (boolean)</P>
         */
        public static final String GUESTS_CAN_MODIFY = "guestsCanModify";

        /**
         * Whether guests can invite other guests. Column name.
         * <P>Type: INTEGER (boolean)</P>
         */
        public static final String GUESTS_CAN_INVITE_OTHERS = "guestsCanInviteOthers";

        /**
         * Whether guests can see the list of attendees. Column name.
         * <P>Type: INTEGER (boolean)</P>
         */
        public static final String GUESTS_CAN_SEE_GUESTS = "guestsCanSeeGuests";

        /**
         * Email of the organizer (owner) of the event. Column name.
         * <P>Type: STRING</P>
         */
        public static final String ORGANIZER = "organizer";

        /**
         * Are we the organizer of this event. If this column is not explicitly set, the provider
         * will return 1 if {@link #ORGANIZER} is equal to {@link Calendars#OWNER_ACCOUNT}.
         * Column name.
         * <P>Type: STRING</P>
         */
        public static final String IS_ORGANIZER = "isOrganizer";

        /**
         * Whether the user can invite others to the event. The
         * GUESTS_CAN_INVITE_OTHERS is a setting that applies to an arbitrary
         * guest, while CAN_INVITE_OTHERS indicates if the user can invite
         * others (either through GUESTS_CAN_INVITE_OTHERS or because the user
         * has modify access to the event). Column name.
         * <P>Type: INTEGER (boolean, readonly)</P>
         */
        public static final String CAN_INVITE_OTHERS = "canInviteOthers";

        /**
         * The package name of the custom app that can provide a richer
         * experience for the event. See the ACTION TYPE
         * {@link CalendarContract#ACTION_HANDLE_CUSTOM_EVENT} for details.
         * Column name.
         * <P> Type: TEXT </P>
         */
        public static final String CUSTOM_APP_PACKAGE = "customAppPackage";

        /**
         * The URI used by the custom app for the event. Column name.
         * <P>Type: TEXT</P>
         */
        public static final String CUSTOM_APP_URI = "customAppUri";

        /**
         * The UID for events added from the RFC 2445 iCalendar format.
         * Column name.
         * <P>Type: TEXT</P>
         */
        public static final String UID_2445 = "uid2445";
    }
}
