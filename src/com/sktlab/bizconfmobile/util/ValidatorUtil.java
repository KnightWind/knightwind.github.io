package com.sktlab.bizconfmobile.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.activity.AppClass;

public class ValidatorUtil {
	
	public static final int FIXED_LINE_MIN_NUMBERS = 6;
	
	public static boolean isNum(String str) {
		
		String expression = "[0-9]*";
		
		return str.matches(expression);
	}
	
	public static boolean isCellPhoneValid(String phoneNum) {
		
		boolean result = false;
		
		do{
			if(phoneNum == null || phoneNum.equalsIgnoreCase("") ){
				
				Util.shortToast(AppClass.getInstance(), R.string.toast_phone_num_null);
				break;
			}
			
			if (!isNum(phoneNum)) {
				
				break;
			}
			
			if(!ValidatorUtil.checkPhoneNumber(phoneNum)){
				
				Util.shortToast(AppClass.getInstance(), R.string.toast_phone_num_invalid);
				break;
			}
			
			result = true;
			
		}while(false);
		
		return result;
	}
	
	private static boolean checkPhoneNumber(String phoneNum) {

		boolean isValid = false;

		/*
		 * accept cell phone format
		 */
		String expression = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{5})$";
		String expression2 = "^\\(?(\\d{3})\\)?[- ]?(\\d{4})[- ]?(\\d{4})$";

		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(phoneNum);

		Pattern pattern2 = Pattern.compile(expression2);
		Matcher matcher2 = pattern2.matcher(phoneNum);

		if (matcher.matches() || matcher2.matches()) {
			isValid = true;
		}

		return isValid;
	}
	
	public static boolean isNumberCodeValid(String phoneNum){
		boolean isValid = false;
		if(!phoneNum.contains("-")){
			return false;
		}
		String str[] = phoneNum.split("-");
		if(str.length!=2){
			return false;
		}
		if(isNumberValid(str[0])){
			if(str[1].matches("\\d+")) isValid = true;
		}
		return isValid;
	}
	
	public static boolean isNumberValid(String phoneNum) {
		if(phoneNum.startsWith("+")){
			phoneNum = phoneNum.replace("+", "00");
		}
		
		boolean isValid = false;

		do {
			
			if(phoneNum == null || phoneNum.equalsIgnoreCase("")){
				
				Util.shortToast(AppClass.getInstance(), R.string.toast_phone_num_null);
				break;
			}
			
			if (!isNum(phoneNum)) {
				
				break;
			}

			if (phoneNum.startsWith("1")) {

				isValid = isCellPhoneValid(phoneNum);
				break;
			}

			if (phoneNum.length() < FIXED_LINE_MIN_NUMBERS) {

				Util.shortToast(AppClass.getInstance(), Util.replaceString(
						AppClass.getInstance(),
						R.string.toast_fixed_line_verify,
						FIXED_LINE_MIN_NUMBERS));
				break;
			}

			isValid = true;
		} while (false);

		return isValid;
	}

	
	public static boolean isEmailValid(String paramString) {
		Matcher matcher;
		Pattern pattern = Pattern
				.compile("(?i)^[\\p{L}0-9._%+-\\\\']+@[\\p{L}0-9._%+-\\\\']+\\.[\\p{L}]{2,}$");

		matcher = pattern.matcher(paramString);
		return matcher.matches();
	}
}
