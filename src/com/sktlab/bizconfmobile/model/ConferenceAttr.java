package com.sktlab.bizconfmobile.model;

public class ConferenceAttr {
	
	//conference id in bridge
	private String confId;
	//conference number,this is the same as the conference account code
	private String confName;
	private String confKey;
	private String groupKey;
	private String mainConfId;
	private String subConfNumber;
	private String partition;
	private String hostCode;
	private String guestCode;
	
	public ConferenceAttr() {
		
		setConfId("");
		setConfName("");
		setGroupKey("");
		setConfKey("");
		setMainConfId("");
		setSubConfNumber("");
		setPartition("");
		setHostCode("");
		setGuestCode("");
	}
	
	public String getConfId() {
		return confId;
	}
	public void setConfId(String confId) {
		this.confId = confId;
	}
	public String getConfName() {
		return confName;
	}
	public void setConfName(String confName) {
		this.confName = confName;
	}

	public String getConfKey() {
		return confKey;
	}

	public void setConfKey(String confKey) {
		this.confKey = confKey;
	}

	public String getMainConfId() {
		return mainConfId;
	}

	public void setMainConfId(String mainConfId) {
		this.mainConfId = mainConfId;
	}

	public String getSubConfNumber() {
		return subConfNumber;
	}

	public void setSubConfNumber(String subConfNumber) {
		this.subConfNumber = subConfNumber;
	}

	public String getPartition() {
		return partition;
	}

	public void setPartition(String partition) {
		this.partition = partition;
	}

	public String getGroupKey() {
		return groupKey;
	}

	public void setGroupKey(String groupKey) {
		this.groupKey = groupKey;
	}

	public String getHostCode() {
		return hostCode;
	}

	public void setHostCode(String hostCode) {
		this.hostCode = hostCode;
	}

	public String getGuestCode() {
		return guestCode;
	}

	public void setGuestCode(String guestCode) {
		this.guestCode = guestCode;
	}
	
}
