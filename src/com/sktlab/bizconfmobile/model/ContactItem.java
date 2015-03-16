package com.sktlab.bizconfmobile.model;

import java.util.ArrayList;
import java.util.List;

import android.net.Uri;
import android.util.SparseArray;

public class ContactItem {
	private int selectPosition;
	private int id;
	private String name;
	//used to sort the contact
	private String sortKey;
	private List<String> phone;
	private List<String> email;
	private String address;
	private SparseArray<Boolean> attrSelectedState;
	private Uri uri;
	private boolean isCheck;
	public Uri getUri() {
		return uri;
	}

	public void setUri(Uri uri) {
		this.uri = uri;
	}

	/**
	 * -1 initialize value
	 * 
	 * 0  selected phone number
	 * 
	 * 1 selected email address
	 */
	private int selectedType = -1;
	
	
	public static final int TYPE_PHONE_NUMBER = 0;
	public static final int TYPE_EMAIL_ADDRESS = 1;
	public ContactItem() {

		phone = new ArrayList<String>();
		email = new ArrayList<String>();
		attrSelectedState = new SparseArray<Boolean>();
	}
	
	public void initSelectedState() {
		
		int size = phone.size() + email.size();
		
		for(int i = 0;i < size;i++) {
			
			attrSelectedState.put(i, false);
		}		
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getPhone() {
		return phone;
	}

	public void setPhone(List<String> phone) {
		this.phone = phone;
	}

	public List<String> getEmail() {
		return email;
	}

	public void setEmail(List<String> email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void addEmail(String email) {

		this.email.add(email);
	}

	public void addPhone(String phone) {
		this.phone.add(phone);
	}

	public String getSortKey() {
		return sortKey;
	}

	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}
	
	public boolean isPosValid(int pos) {
		
		int size = phone.size() + email.size();
		
		if(pos < 0 || pos >= size) {
			
			return false;
		}
		
		return true;	
	}
	
	public boolean getAttrSelectedState(int pos) {
		
		if(isPosValid(pos)) {
			
			return attrSelectedState.get(pos);
		}
		
		return false;
	}
	
	public void setAttrSelectedState(int pos, boolean state) {
		
		if(!isPosValid(pos)) {
			
			return ;
		}
		
		attrSelectedState.put(pos, state);
	}
	
	public String getClickedAttr(int pos) {

		String value = null;

		do {

			if (!isPosValid(pos)) {

				break;
			}
			
			if (pos < phone.size()) {

				value = phone.get(pos);
				selectedType = TYPE_PHONE_NUMBER;
				break;
			}
			
			if (pos >= phone.size()) {

				value = email.get(pos - phone.size());
				selectedType = TYPE_EMAIL_ADDRESS;
				break;
			}

		} while (false);

		return value;
	}

	public int getSelectedType() {
		return selectedType;
	}

	public void setSelectedType(int selectedType) {
		this.selectedType = selectedType;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	public int getSelectPosition() {
		return selectPosition;
	}

	public void setSelectPosition(int selectPosition) {
		this.selectPosition = selectPosition;
	}
	
	
}
