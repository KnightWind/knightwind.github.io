package com.sktlab.bizconfmobile.model;


public class Participant {
	
	private int contactId;
	private int selectedAttrPosInContactItem = -1;
	private String name;
	private String phone;
	private String email;
	private boolean isModerator;
	//id generated in conference
	private String idInConference;
	//seq number which used to add this participant to a conference
	private int seqNumber;
	
	private boolean isOutCalled;
	private boolean isMuted;
	private boolean isTalking;
	
	public Participant() {
		
		init();
	}
		
	public Participant(Participant party) {
		
		this.contactId = party.contactId;
		this.selectedAttrPosInContactItem = party.selectedAttrPosInContactItem;
		this.name = party.name;
		this.phone = party.phone;
		this.email = party.email;
		this.isModerator = party.isModerator;
		this.idInConference = party.idInConference;
		this.seqNumber = party.seqNumber;
		this.isOutCalled = party.isOutCalled;
		this.isMuted = party.isMuted;
		this.isTalking = party.isTalking;
	}

	public void init() {
		
		contactId = -1;
		name = "null";
		phone = "null";
		email = "null";
		isModerator = false;
		idInConference = "null";
		isOutCalled = false;
		setMuted(false);
		isTalking = false;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
			
		this.phone = phone;	
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public int getSelectedAttrPosInContactItem() {
		return selectedAttrPosInContactItem;
	}

	public void setSelectedAttrPosInContactItem(int selectedAttrPosInContactItem) {
		this.selectedAttrPosInContactItem = selectedAttrPosInContactItem;
	}

	public int getContactId() {
		return contactId;
	}

	public void setContactId(int contactId) {
		this.contactId = contactId;
	}

	public int getSeqNumber() {
		return seqNumber;
	}

	public void setSeqNumber(int seqNumber) {
		this.seqNumber = seqNumber;
	}

	public boolean isModerator() {
		return isModerator;
	}

	public void setIsModerator(boolean isModerator) {
		this.isModerator = isModerator;
	}

	public String getIdInConference() {
		return idInConference;
	}

	public void setIdInConference(String idInConference) {
		this.idInConference = idInConference;
	}

	public boolean isOutCalled() {
		return isOutCalled;
	}

	public void setOutCalled(boolean isOutCalled) {
		this.isOutCalled = isOutCalled;
	}

	public boolean isMuted() {
		return isMuted;
	}

	public void setMuted(boolean isMuted) {
		this.isMuted = isMuted;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Participant other = (Participant) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		return true;
	}

	public boolean isTalking() {
		return isTalking;
	}

	public void setTalking(boolean isTalking) {
		this.isTalking = isTalking;
	}	
	
	public void clear() {
		
		init();
	}
}
