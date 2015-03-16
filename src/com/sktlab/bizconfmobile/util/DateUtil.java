package com.sktlab.bizconfmobile.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

	public static final String YY_MM_DD = "yyyy-MM-dd";
	public static final String A_HH_MM = "a hh:mm";
	public static final String YY_MM_DD_E = "yyyy-MM-dd E";
	//12小时制
	public static final String HH_MM_12 = "hh:mm";
	
	//24小时制
	public static final String HH_MM_24 = "HH:mm";
	
	public static final String YY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
	
	public static final String YY_MM_DD_HH_MM_SS_E = "yyyy-MM-dd HH:mm:ss E";
	
	public static final String ICS_FORMAT_DATE = "yyyyMMdd";
	public static final String ICS_FORMAT_TIME = "HHmmss";
	
	public static String getFormatString(Date date, String format) {
		
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(format);
		localSimpleDateFormat.setTimeZone(TimeZone.getDefault());
		String str = localSimpleDateFormat.format(date);
		
		return str;
	}
}
