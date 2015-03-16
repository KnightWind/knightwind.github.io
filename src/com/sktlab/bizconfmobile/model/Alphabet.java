package com.sktlab.bizconfmobile.model;

import java.util.ArrayList;
import java.util.List;

public class Alphabet implements Comparable {
	private String alp;
	public Alphabet(String alp){
		this.alp=alp;
	}
	public String getAlp() {
		return alp;
	}

	public void setAlp(String alp) {
		this.alp = alp;
	}

	private List<ContactItem> contacts=new ArrayList<ContactItem>();
	public  void addContactItem(ContactItem contact){
		contacts.add(contact);
	}
	
	public List<ContactItem> getContacts(){
		return contacts;
	}
	
	
	public ContactItem getClickedAttr(int childPosition) {
		
		return contacts.get(childPosition);
	}
//	public boolean getAttrSelectedState(int childPosition) {
//		
//		return contacts.get(childPosition);
//	}
	@Override
	public int compareTo(Object obj) {
		Alphabet alp=(Alphabet)obj;
		if(alp.getAlp().charAt(0)=='#') return -1;
		if(this.alp.charAt(0) =='#') return 1;
		if(alp.getAlp().charAt(0)<this.alp.charAt(0)){
			return 1;
		}else if(alp.getAlp().charAt(0)==this.alp.charAt(0)){
			return 0;
		}else{
			return -1;
		}
	}
}
