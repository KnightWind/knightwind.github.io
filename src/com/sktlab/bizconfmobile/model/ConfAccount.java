package com.sktlab.bizconfmobile.model;

/**
 * This class is the abstract conference account
 * @author wenjuan.li 2013-08-06
 *
 */
public class ConfAccount {
	public static final String TAG = "ConfAccount";
	
	public final String JOIN_ACCOUNT = "";
	private String confAccountName = "";
	//dial in number
	private String accessNumber = "";
	//conference number
	private String confCode = "";
	//dial out number when dial out enable
	private String dialOutNumber = "";
	//security code
	private String securityCode = "";
	//moderator password, the default password is join account
	private String moderatorPw = JOIN_ACCOUNT;
	//is enable dial out
	private boolean isDialOutEnable;
	//is security code enable
	private boolean isSecurityCodeEnable;
	//is current account created by link in email
	private boolean isUseDefaultAccessNum;
	
	//this account id will be assigned by the database table row number
	//when this account be put into database
	private long accountId = -1L;
	
	public ConfAccount() {
	
		isDialOutEnable = false;
		isSecurityCodeEnable = false;
		isUseDefaultAccessNum = false;
	}
	
	public ConfAccount(String confAccountName, String dialInNumber,
			String confNumber, String dialOutNumber, String securityCode,
			boolean isDialOutEnable, boolean isSecurityCodeEnable) {
		
		this.confAccountName = confAccountName;
		this.accessNumber = dialInNumber;
		this.confCode = confNumber;
		this.dialOutNumber = dialOutNumber;
		this.securityCode = securityCode;
		this.isDialOutEnable = isDialOutEnable;
		this.isSecurityCodeEnable = isSecurityCodeEnable;
	}
	
	public ConfAccount(String confAccountName, String dialInNumber,
			String confNumber, String dialOutNumber, String securityCode,
			boolean isDialOutEnable, boolean isSecurityCodeEnable,
			String moderatorPw) {
		
		this.confAccountName = confAccountName;
		this.accessNumber = dialInNumber;
		this.confCode = confNumber;
		this.dialOutNumber = dialOutNumber;
		this.securityCode = securityCode;
		this.isDialOutEnable = isDialOutEnable;
		this.isSecurityCodeEnable = isSecurityCodeEnable;
		this.moderatorPw = moderatorPw;
	}
	
	public String getConfAccountName() {
		return confAccountName;
	}

	public void setConfAccountName(String confAccountName) {
		this.confAccountName = confAccountName;
	}

	public String getAccessNumber() {
		return accessNumber;
	}

	public void setAccessNumber(String accessNumber) {
		this.accessNumber = accessNumber;
	}

	public String getConfCode() {
		return confCode;
	}

	public void setConfCode(String confNumber) {
		this.confCode = confNumber;
	}

	public String getDialOutNumber() {
		return dialOutNumber;
	}

	public void setDialOutNumber(String dialOutNumber) {
		this.dialOutNumber = dialOutNumber;
	}

	public String getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}

	public boolean isDialOutEnable() {
		return isDialOutEnable;
	}

	public void setDialOutEnable(boolean isDialOutEnable) {
		this.isDialOutEnable = isDialOutEnable;
	}

	public boolean isSecurityCodeEnable() {
		return isSecurityCodeEnable;
	}

	public void setSecurityCodeEnable(boolean isSecurityCodeEnable) {
		this.isSecurityCodeEnable = isSecurityCodeEnable;
	}

	public long getAccountId() {
		return accountId;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	public String getModeratorPw() {
		return moderatorPw;
	}

	public void setModeratorPw(String moderatorPw) {
		this.moderatorPw = moderatorPw;
	}

	public boolean isUseDefaultAccessNum() {
		return isUseDefaultAccessNum;
	}

	public void setUseDefaultAccessNum(boolean isUseDefaultAccessNum) {
		
		this.isUseDefaultAccessNum = isUseDefaultAccessNum;
	}
	
	
}
