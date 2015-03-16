package com.sktlab.bizconfmobile.model;

import java.util.ArrayList;
import java.util.Calendar;

import com.sktlab.bizconfmobile.util.Util;

public class AppointmentConf {
	
	public long id;
	//the account id which used to order meeting
	public String accountId;
	//会议主题
	public String title;
	//meeting date
	public String date;
	//会议开始时间
	public Long startTime;
	//会议结束时间
	public Long endTime;
	public String accessNumber;
	public String securityCode;
	//note of meeting
	public String note;
	//meeting's participant,this variable just save the phone number or email address
	public String invitees;
	//会议周期
	public String period;
	//日历下面显示的时间描述文字
	public String calendarDes;
	//会议的简单描述
	public String description;
	public String repeatCount;
	//email templet
	private String emailTemplet;
	
	public String eventId;

	public ArrayList<String> emails;
	public ArrayList<String> phones;
	
	public int freq;
	
	public AppointmentConf() {
		
		id = -1;
		title = "";
		period = "";
		startTime = 0L;
		endTime = 0L;
		accessNumber = "";
		securityCode = "";
		note = "";
		invitees = "";
		description = "";
		calendarDes = "";
		repeatCount = "";
		freq = -1;		
		eventId= "";
		
		emails = new ArrayList<String>();
		phones = new ArrayList<String>();
		
	}

	
	public AppointmentConf(AppointmentConf sourceMeeting) {
		
		this.accountId = sourceMeeting.accountId;
		this.title = sourceMeeting.title;
		this.date = sourceMeeting.date;
		this.startTime = sourceMeeting.startTime;
		this.endTime = sourceMeeting.endTime;
		this.accessNumber = sourceMeeting.accessNumber;
		this.securityCode = sourceMeeting.securityCode;
		this.note = sourceMeeting.note;
		this.invitees = sourceMeeting.invitees;
		this.period = sourceMeeting.period;
		this.calendarDes = sourceMeeting.calendarDes;
		this.description = sourceMeeting.description;
		this.repeatCount = sourceMeeting.repeatCount;
		this.eventId = sourceMeeting.eventId;
		
		emails = new ArrayList<String>();
		phones = new ArrayList<String>();
		
		emails.addAll(sourceMeeting.getEmails());
		phones.addAll(sourceMeeting.getPhones());

		this.freq = sourceMeeting.freq;
	}


	public String getCalendarDes() {
		return calendarDes;
	}

	public void setCalendarDes(String calendarDes) {
		this.calendarDes = calendarDes;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getAccessNumber() {
		return accessNumber;
	}

	public void setAccessNumber(String accessNumber) {
		this.accessNumber = accessNumber;
	}

	public String getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getInvitees() {
		return invitees;
	}

	public void setInvitees(String invitees) {
		this.invitees = invitees;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getRepeatCount() {
		return repeatCount;
	}

	public void setRepeatCount(String repeatCount) {
		this.repeatCount = repeatCount;
	}

	public int getFreq() {
		return freq;
	}

	public void setFreq(int freq) {
		this.freq = freq;
	}

	public boolean isInPeroid(Long day) {
		
		boolean isInConfPeroid = false;
		
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		Calendar current = Calendar.getInstance();
		
		start.setTimeInMillis(startTime);
		end.setTimeInMillis(endTime);
		current.setTimeInMillis(day);
		
		
		int startYear = start.get(Calendar.YEAR);
		int startMonth = start.get(Calendar.MONTH);
		int startDay = start.get(Calendar.DAY_OF_MONTH);
		
		int endYear = end.get(Calendar.YEAR);
		int endMonth = end.get(Calendar.MONTH);
		int endDay = end.get(Calendar.DAY_OF_MONTH);
		
		int currentYear = current.get(Calendar.YEAR);
		int currentMonth = current.get(Calendar.MONTH);
		int currentDay = current.get(Calendar.DAY_OF_MONTH);

		if(startYear <= currentYear && endYear >= currentYear
				&& startMonth <= currentMonth && endMonth >= currentMonth
				&& startDay <= currentDay && endDay >= currentDay
				) {
			
			isInConfPeroid = true;
		}	
		
		return isInConfPeroid;
	}

	public ArrayList<String> getEmails() {
		return emails;
	}

	public ArrayList<String> getPhones() {
		return phones;
	}
	
	public void addEmail(String email) {
		
		if (!Util.isEmpty(emails) && !Util.isEmpty(email)
				&&!emails.contains(email)) {
			
			emails.add(email);
		}
	}
	
	public void addPhone(String phone) {
		
		if (!Util.isEmpty(phones) && !Util.isEmpty(phone)
				&&!phones.contains(phone)) {
			
			phones.add(phone);
		}
	}
	
	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public String getEmailTemplet() {
		return emailTemplet;
	}


	public void setEmailTemplet(String emailTemplet) {
		this.emailTemplet = emailTemplet;
	}	
}
