package com.sktlab.bizconfmobile.model;

import java.util.Date;

public class ConfHistory {
	
	private long historyId = -1L;
	private String accountId;
	private String accountName;
	private String confCode;
	private String title;
	private Date startDate;
	private Date endDate;

	public ConfHistory() {
		
		setAccountID("");
		setConfCode("");
		setStartDate(new Date());
		setEndDate(new Date());
		setTitle("");
		setAccountName("");
	}

	public String getAccountID() {
		return this.accountId;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public void setAccountID(String accountId) {
		this.accountId = accountId;
	}

	public void setEndDate(Date paramDate) {
		this.endDate = paramDate;
	}

	public void setStartDate(Date paramDate) {
		this.startDate = paramDate;
	}

	public String getConfCode() {
		return confCode;
	}

	public void setConfCode(String confCode) {
		this.confCode = confCode;
	}

	public long getHistoryId() {
		return historyId;
	}

	public void setHistoryId(long historyId) {
		this.historyId = historyId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}	
}