package com.sktlab.bizconfmobile.model;

public class NumberSegment {

	public int preFix;
	public int start;
	public int end;
	
	
	public NumberSegment() {
		
		preFix = -1;
		start = -1;
		end = -1;
	}
	
	public NumberSegment(String preFixValue, String startValue, String endValue) {
		
		preFix = Integer.valueOf(preFixValue);
		start = Integer.valueOf(startValue);
		end = Integer.valueOf(endValue); 
	}
	
	public int getPreFix() {
		return preFix;
	}
	public void setPreFix(int preFix) {
		this.preFix = preFix;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
}
