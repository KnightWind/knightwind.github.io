package com.sktlab.bizconfmobile.model;

import com.sktlab.bizconfmobile.util.Util;

public class PhoneNumLocationSegment {
	
	public static final String TAG = "PhoneNumLocationSegment";
	
	private int prifex = -1;
	private int start = -1;
	private int end = -1;
	private String area = "";
	
	public PhoneNumLocationSegment(String segmentInfo) {
		
		String[] data = segmentInfo.split(",");
		
		prifex = Integer.valueOf(data[1]);
		start = Integer.valueOf(data[2]);
		end = Integer.valueOf(data[3]);
		area = data[4].replace(")", "");
	}
	
	public boolean isNumberInSegment(String phoneNumber) {
		
		boolean isInSeg = false;
		
		PhoneNumLocInfo phoneInfo = new PhoneNumLocInfo(phoneNumber);
		
		int loc = phoneInfo.getLoc();
		
		Util.BIZ_CONF_DEBUG(TAG, "loc: " + loc + "start:"  + start + "end:" + end);
		
		isInSeg = (loc >= start) && (loc <= end);
		
		return isInSeg;
	}
	
	public int getPrifex() {
		return prifex;
	}
	public void setPrifex(int prifex) {
		this.prifex = prifex;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
}
