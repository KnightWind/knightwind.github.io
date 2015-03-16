package com.sktlab.bizconfmobile.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.activity.AppClass;
import com.sktlab.bizconfmobile.activity.RepeatSelectionActivity;
import com.sktlab.bizconfmobile.model.AppointmentConf;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.PartStat;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.parameter.Rsvp;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Created;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;

import android.os.Environment;

public class FileUtil {

	public static final String TAG = "FileUtil";

	public static final File SD_ROOT = Environment
			.getExternalStorageDirectory();
	public static final String APP_FLAG = "BizConfMobile";
	public static final String DOWNLOAD_FOLDER = "Download";
	public static final String SD_PATH = SD_ROOT + "/" + APP_FLAG + "/";
	public static final String EMAIL_FILE_PATH = SD_PATH + "email/";
	public static final String CSV_FILE_PATH = SD_PATH + "csv/";
	public static final String LOG_PATH = SD_PATH + "log/";
	public static final String SD_DOWNLOAD_PATH = SD_ROOT + "/" + DOWNLOAD_FOLDER + "/";

    /**
     * 判断存储卡是否存在
     * 
     * @return
     */
	public static boolean isExistSDcard() {
		
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
				.getExternalStorageState())) {
			
			return true;
		} else {
			
			return false;			
		}
	}
    
	public static File createEvent(AppointmentConf meeting){
		
		File file = null;

		if (meeting != null) {	
			
			String mailContent = "";
			
			try {
				
				mailContent = generateMailContent(meeting);
				
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ValidationException e) {
				e.printStackTrace();
			}
			
			// 指定ics文件名称
			String name = CalendarUtil.getFomatDateStr(meeting.getStartTime());

			// 生成ics文件
			String fileName = name + ".ics";

			file = createFile(EMAIL_FILE_PATH, fileName, true);
			
			FileWriter writer = null;
			
			try {
				writer = new FileWriter(new File(EMAIL_FILE_PATH
						+ fileName));
				writer.write(mailContent);
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
				//Util.shortToast(AppClass.getInstance(), R.string.toast_storage_access_denied);
			}finally{
				
			}
			
			return file;
		}

		return null;
		
	}
	
	public static String generateMailContent(AppointmentConf meeting)  throws ParseException,
	IOException, ValidationException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		String content = meeting.getNote();

//		// 创建时区
//		TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance()
//				.createRegistry();
//		 //设置时区
//		TimeZone timezone = registry.getTimeZone("Asia/Hong_Kong");
//		 VTimeZone tz = timezone.getVTimeZone();
		
		CalendarBuilder builder = new CalendarBuilder(); 
		TimeZoneRegistry registry = builder.getRegistry(); 
		TimeZone tz = registry.getTimeZone("America/Mexico_City");
		
		if (null == tz) {
			
			Util.BIZ_CONF_DEBUG(TAG, "sorry time zone is null");
		}

		// Create the event
		String eventName = meeting.getTitle();
		DateTime start = new DateTime(meeting.getStartTime());
		DateTime end = new DateTime(meeting.getEndTime());

		VEvent events = new VEvent(start, end, eventName);
		// add timezone info..
		// events.getProperties().add(tz.getTimeZoneId());
		
		Date date = new Date();
		
		String created = date.getYear()+ date.getDay() + "T" + 
					date.getHours()+date.getMinutes()+date.getSeconds() + "Z";
		
		String createdDate = DateUtil.getFormatString(date, DateUtil.ICS_FORMAT_DATE);
		String createdTime = DateUtil.getFormatString(date, DateUtil.ICS_FORMAT_TIME);
		
		created = createdDate+ "T" + createdTime + "Z";
		
		//Util.BIZ_CONF_DEBUG(TAG, "created: " + created);
		
		events.getProperties().add(new Location("online"));
		events.getProperties().add(new Created(created));
		
		Organizer org = new Organizer();
		
		try {
			
			org.setValue("no-reply@bizconf.cn");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		events.getProperties().add(org);
		
		// generate unique identifier..
		// UidGenerator ug = new UidGenerator("1");
		Uid uid = new Uid();
		
		Random r = new Random();
		
		uid.setValue(String.valueOf(r.nextInt()));
		
		events.getProperties().add(uid);

		// add attendees..
		
		ArrayList<String> emails = meeting.getEmails();
		
		for (String email : emails) {
			
			Attendee dev1 = new Attendee(URI.create("mailto:" + email));
			dev1.getParameters().add(Role.REQ_PARTICIPANT);
			dev1.getParameters().add(Rsvp.TRUE);
			dev1.getParameters().add(PartStat.NEEDS_ACTION);
			dev1.getParameters().add(new Cn(content));

			events.getProperties().add(dev1);
		}
		
		int freq = meeting.getFreq();
		
		String count = meeting.getRepeatCount();
		
		do{
			
			if (Util.isEmpty(count)) {					
				break;
			}
			
			int counts = Integer.valueOf(count);
			
			Recur recur = null;
			
			switch(freq) {
				
			case RepeatSelectionActivity.PERIOD_DAY:
				recur = new Recur(Recur.DAILY, counts);
				break;
			case RepeatSelectionActivity.PERIOD_WEEK:
				recur = new Recur(Recur.WEEKLY, counts);
				break;
			case RepeatSelectionActivity.PERIOD_MONTH:
				recur = new Recur(Recur.MONTHLY, counts);
				break;
			case RepeatSelectionActivity.PERIOD_YEAR:
				recur = new Recur(Recur.YEARLY, counts);
				break;
			}
			
			if (null == recur) {	
				break;
			}
			
			// recur.setInterval(2);
			RRule rule = new RRule(recur);
			events.getProperties().add(rule);
		}while(false);
		
		// Create a calendar
		net.fortuna.ical4j.model.Calendar icsCalendar = new net.fortuna.ical4j.model.Calendar();
		icsCalendar.getProperties().add(
				new ProdId("--//Events Calendar//iCal4j 1.0//EN"));
		 icsCalendar.getProperties().add(Version.VERSION_2_0);  
	     icsCalendar.getProperties().add(Method.REQUEST);

		// Add the event and print
		icsCalendar.getComponents().add(events);
		
		icsCalendar.validate();
		
		CalendarOutputter co = new CalendarOutputter(false);
		Writer wtr = new StringWriter();
		co.output(icsCalendar, wtr);
		// 日历提醒文件转换问文本，做为邮件发送
		String mailContent = wtr.toString();
		
		return mailContent;
	}
	
	public static boolean createFileDir(String dirPath) {
		
		boolean result = false;	
		File dirFile = null;
		
		try {
			
			dirFile = new File(dirPath);

			if (dirFile.exists() && dirFile.isDirectory()) {
				
				Util.BIZ_CONF_DEBUG(TAG,"the " + dirPath +  " is existing now---");
				result = true;
			} else {
				
				result = dirFile.mkdirs();
				
				if (result) {

					Util.BIZ_CONF_DEBUG(TAG, "create dir success " + dirPath);
					result = true;
				} else {
					
					Util.BIZ_CONF_DEBUG(TAG, "create dir failed " + dirPath);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Util.BIZ_CONF_DEBUG(TAG, " ---exception when create dir---");
		}

		return result;
	}

	public static File createFile(String folderName, String fileName, boolean delIfExist) {

		File file = null;

		do {

			if (FileUtil.createFileDir(folderName)) {

				file = new File(folderName + fileName);
			} else {

				//Util.BIZ_CONF_DEBUG(TAG, "create file folder failed");
				break;
			}

			if (delIfExist && file.exists()) {
				
				file.delete();
			}

		} while (false);

		return file;
	}

	public static void createFile(InputStream inputStream, String folderName,
			String fileName) {

		RandomAccessFile raf = null;
		File file = null;

		try {
			do {

				if (FileUtil.createFileDir(folderName)) {
					file = new File(folderName + fileName);
				} else {

					//Util.BIZ_CONF_DEBUG(TAG, "create file folder failed");
					return;
				}

				if (file.exists()) {
					//Util.BIZ_CONF_DEBUG(TAG, "file already exist!");
					file.delete();
				}

				raf = new RandomAccessFile(file, "rw");

				byte[] bs = new byte[1024 * 16];
				int len = 0;
				while (len != -1) {
					len = inputStream.read(bs);
					if (len != -1) {
						raf.write(bs, 0, len);
					}
				}
			} while (false);

		} catch (IOException e) {

			//Util.BIZ_CONF_DEBUG(TAG, "read assert file error");
			e.printStackTrace();
		} finally {
			try {
				raf.close();
			} catch (Exception ex) {
			}
			try {
				inputStream.close();
			} catch (Exception ex) {
			}
			raf = null;
			inputStream = null;
		}
	}
}
