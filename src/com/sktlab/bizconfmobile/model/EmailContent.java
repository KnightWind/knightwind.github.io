package com.sktlab.bizconfmobile.model;

import java.util.Date;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.activity.AppClass;
import com.sktlab.bizconfmobile.model.manager.AccountsManager;
import com.sktlab.bizconfmobile.util.DateUtil;
import com.sktlab.bizconfmobile.util.Util;

public class EmailContent {
	
	public static final String TAG = "EmailContent";
	
	protected String globalAccessNumCH = "";
	protected String globalAccessNumEN = "";
	protected String local400AccessNumCH = "";
	protected String local400AccessNumEN = "";
	protected String local800AccessNumCH = "";
	protected String local800AccessNumEN = "";
	protected String accessNumListUrl = "";
	protected String suffixOfCommandToast = "";
	protected String serviceCommand = "";
	
	protected ConfAccount account = null;
	protected AppointmentConf appointConf = null;
	
	public EmailContent(AppointmentConf appointmentConf) {
		
		appointConf = appointmentConf;
		
		if (null != appointConf) {
			
			account = AccountsManager.getInstance()
					.getAccountById(Long.valueOf(appointConf.getAccountId()));
		}
	}
	
	public String generateEmailBody() {
		
		String body = "";
		
		do{
			
			if (null == appointConf || null == account){
				
				break;
			}
			
			String confCode = account.getConfCode();
			
			String securityCode = getSecurityCode();
					
			String date = getDate();
			
			String time = getTime();
			
			String title = appointConf.getTitle();
			
			String inviteeName = account.getConfAccountName();
			
			String content = appointConf.getNote();	
			
			String accessNumber = account.getAccessNumber();
			
			String downUrl = getDownUrl();
			
			String templet = appointConf.getEmailTemplet();
			
			templet = templet.replace("[~text]", content)
						.replace("[~accountName]", inviteeName)
						.replace("[~date]", date)
						.replace("[~time]", time)
						.replace("[~meetingTitle]", title)
						.replace("[~meetingNumber]", confCode)
						.replace("[~meetingCode]", securityCode)
						.replace("[~accessNumber]", accessNumber);
			
			body = templet;
			
			Util.BIZ_CONF_DEBUG(TAG, "email body:" + body);
			
//			body = Util.replaceString(AppClass.getInstance(), 
//							R.string.email_html_content, 
//							content,
//							inviteeName,
//							date,
//							time,	
//							title,
//							downUrl,
//							globalAccessNumCH,
//							globalAccessNumEN,
//							local400AccessNumCH,
//							local400AccessNumEN,
//							local800AccessNumCH,
//							local800AccessNumEN,
//							accessNumListUrl,
//							confCode,
//							suffixOfCommandToast,
//							securityCode,
//							serviceCommand
//							);
			
		}while(false);
		
		return body;
	}
	
	private String getDownUrl() {
		
		String linkAddr = "http://bizconf.mobile.com/" 
							+ account.getConfCode()
							+"/"
							+ account.getAccessNumber();
		
		String iphoneAddr = "bizconf://mobile/" 
							+ account.getConfCode()
							+"/"
							+ account.getAccessNumber();
		
		String downUrl = 
						"<HTML><HEAD>" +
						"<META content=\"text/html; charset=gb2312\" http-equiv=Content-Type>" +
						"<META name=GENERATOR content=\"MSHTML 9.00.8112.16496\"></HEAD>" +
						"<BODY style=\"MARGIN: 10px\">" +
						"<p>Android: </p>" +
						"<DIV><A href=\"" + linkAddr + "\">" + linkAddr + "</A></DIV>" + 						
						"<p>iPhone: </p>" +
						"<DIV><A href=\"" + iphoneAddr + "\">" + iphoneAddr + "</A></DIV>" + 					 					 
						"<DIV><FONT size=3 face=\"Times New Roman\"></FONT></DIV></BODY></HTML>";
		
		return downUrl;
	}
	
	private String getTime() {
		
		return DateUtil.getFormatString(
				new Date(appointConf.getStartTime()), DateUtil.HH_MM_24)
				+ "-"
				+ DateUtil.getFormatString(
				new Date(appointConf.getEndTime()), DateUtil.HH_MM_24);
	}
	
	private String getDate(){
		
		return DateUtil.getFormatString(
				new Date(appointConf.getStartTime()), 
				DateUtil.YY_MM_DD);
	}
	
	private String getSecurityCode(){
		
		String securityCode = "";
		
		if (account.isSecurityCodeEnable()) {
			
			securityCode = account.getSecurityCode();
		}			
		
		return securityCode;
	}
}
