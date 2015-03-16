package com.sktlab.bizconfmobile.model;

public class CalendarEvent {

	private String eventId;
	private String eventTitle;
	private String eventStartTime;
	private String eventEndTime;
	private String description;
	
	public CalendarEvent() {
		
		eventId = "";
		eventTitle = "";
		eventStartTime = "";
		eventEndTime = "";
		description = "";
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getEventTitle() {
		return eventTitle;
	}

	public void setEventTitle(String eventTitle) {
		this.eventTitle = eventTitle;
	}

	public String getEventStartTime() {
		return eventStartTime;
	}

	public void setEventStartTime(String eventStartTime) {
		this.eventStartTime = eventStartTime;
	}

	public String getEventEndTime() {
		return eventEndTime;
	}

	public void setEventEndTime(String eventEndTime) {
		this.eventEndTime = eventEndTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}	
}
