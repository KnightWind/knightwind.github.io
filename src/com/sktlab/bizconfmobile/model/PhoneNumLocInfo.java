package com.sktlab.bizconfmobile.model;

import com.sktlab.bizconfmobile.util.ValidatorUtil;

public class PhoneNumLocInfo {

	public static final String TAG = "NumberSegment";
	
	public int preFix;
	public int loc;
	
	public PhoneNumLocInfo() {
		
		preFix = -1;
		loc = -1;
	}
	
	public PhoneNumLocInfo(String phoneNum) {
		
		boolean isValidNum = ValidatorUtil.isNumberValid(phoneNum);
		
		do {
			
			if (!isValidNum) {
				
				//Util.BIZ_CONF_DEBUG(TAG, "number not valid");
				break;
			}
			
			String preFixValue = phoneNum.substring(0, 4);
			String locValue = phoneNum.substring(4, 7);
			
			preFix = Integer.valueOf(preFixValue);
			loc = Integer.valueOf(locValue);			
		}while(false);	
	}

	public int getPreFix() {
		return preFix;
	}

	public void setPreFix(int preFix) {
		this.preFix = preFix;
	}

	public int getLoc() {
		return loc;
	}

	public void setLoc(int start) {
		this.loc = start;
	}	
}
