package com.sktlab.bizconfmobile.model.manager;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.util.SparseArray;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.activity.AppClass;
import com.sktlab.bizconfmobile.model.ConfAccount;
import com.sktlab.bizconfmobile.model.ContactItem;
import com.sktlab.bizconfmobile.model.Participant;
import com.sktlab.bizconfmobile.util.Util;

/**
 * This class is a manager of information about contacts
 * @author wenjuan.li
 *
 */
public class ContactManager {
	
	public static final String TAG = "ContactManager";
	
	private static class InstanceHolder {

		private final static ContactManager instance = new ContactManager();
	}
	
	//all contacts, this collection's element contains all information about a contact
	private ArrayList<ContactItem> mAllContacts;	
	//all contact info selected by user,its element only contains the user selected information
	private ArrayList<Participant> mSelectedContacts;
	//a map from contact id to contact object
	private SparseArray<ContactItem> Id2Contact;
	
	private Participant mCurrentUser = null;
	//The party use the app
	private Participant originalParty;
	//the party the phone will transformer to
	private Participant destinateParty;
	
	private ArrayList<Participant> mInputParty;
	
	private ContactManager() {
		
		mAllContacts = new ArrayList<ContactItem>();
		mSelectedContacts = new ArrayList<Participant>();
		Id2Contact = new SparseArray<ContactItem>();
		mCurrentUser = new Participant();
		mInputParty = new ArrayList<Participant>();
		
		originalParty = new Participant();
		destinateParty = new Participant();
	}

	public boolean addInputParty(Participant party){
		
		boolean isAddSuccess = false;
		
		//Util.BIZ_CONF_DEBUG(TAG, "input party size: " + mInputParty.size());
		
		if (null != party && !mInputParty.contains(party)) {
			
			//Util.BIZ_CONF_DEBUG(TAG, "add new input party, party number: " + party.getPhone());
			
			isAddSuccess = mInputParty.add(party);
		}else {
			
			//Util.BIZ_CONF_DEBUG(TAG, "not add this party, number: " + party.getPhone());
		}
		
		return isAddSuccess;
	}
	
	public ArrayList<Participant> getInputParties() {
		
		return mInputParty;
	}
	
	public void removeInputParty(Participant rmParty) {
		
		if (mInputParty.contains(rmParty)) {
			
			mInputParty.remove(rmParty);
		}
	}
	
	public void Load() {
		
		if (mSelectedContacts.isEmpty()) {
			
			clearContacts();
			
			loadAllContacts(AppClass.getInstance());
			rmNoNumberContact();
			sortAllContacts();
		}		
	}
	
	/**
	 * 
	 */
	public void reset(){
		
		clear();
	}
	
	/*
	 * find the person name through phone number,
	 * if not find a person who has this number, return the phone number as its name
	 */
	public static String getContactNameByPhoneNumber(String phoneNumber) {
		
		String[] projection = { ContactsContract.PhoneLookup.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.NUMBER };


		Cursor cursor = AppClass.getInstance().getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				projection, // Which columns to return.
				ContactsContract.CommonDataKinds.Phone.NUMBER + " = '"
						+ phoneNumber + "'", // WHERE clause.
				null, // WHERE clause value substitution
				null); // Sort order.
		
		String name = phoneNumber;
		
		if (cursor == null) {

			return name;
		}
		
		if (cursor.moveToNext()) {
			
			// 取得联系人名字
			int nameFieldColumnIndex = cursor
					.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
			
			name = cursor.getString(nameFieldColumnIndex);
		}

		if (null != cursor) {
			
			cursor.close();
		}
		
		return name;
	}
	 
	public void clearSelectedParties() {
		
		if (!Util.isEmpty(mSelectedContacts)) {
			
			for(Participant item : mSelectedContacts) {
				
				int conatactId = item.getContactId();
				int selectedAttrPos = item.getSelectedAttrPosInContactItem();
				
				ContactItem contact = getContactById(conatactId);
				
				if (!Util.isEmpty(contact)) {
					
					contact.setAttrSelectedState(selectedAttrPos, false);
				}			
			}
			
			mSelectedContacts.clear();
		}
		
		if (null != mInputParty) {
			
			mInputParty.clear();
		}
	}
	
	public void clearContacts() {
		
		if (null != mAllContacts) {
			
			//Util.BIZ_CONF_DEBUG(TAG, "clear contacts now");
			mAllContacts.clear();
		}

		if (null != Id2Contact) {

			Id2Contact.clear();
		}
		
		if (null != mCurrentUser) {
			
			mCurrentUser.setOutCalled(false);
		}
	}
	
	public void clear() {
		
		clearSelectedParties();		
		clearContacts();
		mInputParty.clear();
		mCurrentUser.clear();
		originalParty.clear();
		destinateParty.clear();
	}
	
	public static ContactManager getInstance() {
		return InstanceHolder.instance;
	}
	
	public void setCurrentUser(Participant party) {
		
		mCurrentUser.setIdInConference(party.getIdInConference());
		mCurrentUser.setName(party.getName());
		mCurrentUser.setPhone(party.getPhone());
		mCurrentUser.setIsModerator(party.isModerator());
	}
	
	public Participant getCurrentUserObject() {
					
		do{			
			
			if (!Util.isEmpty(mCurrentUser.getPhone())) {
				
				break;
			}
			
			boolean isModerator = CommunicationManager.getInstance().isModeratorAccount();
			
			if (!isModerator) {
				
				mCurrentUser.setName(
						AppClass.getInstance().getResources()
							.getString(R.string.user_guest_name));
				
				mCurrentUser.setIsModerator(false);
			}else {
				
				mCurrentUser.setName(
						AppClass.getInstance().getResources()
							.getString(R.string.user_moderator_name));
				
				mCurrentUser.setIsModerator(true);
			}
			
			ConfAccount account = CommunicationManager.getInstance().getActiveAccount();
			
			if (account.isDialOutEnable()) {
				
				mCurrentUser.setPhone(account.getDialOutNumber().replace("+", "00"));
				//mCurrentUser.setName(account.getDialOutNumber());
			}
			
		}while(false);
					
		return mCurrentUser;
	}
	
	public ContactItem getContactById(int contactId) {
		
		return Id2Contact.get(contactId);
	}
	
	public ArrayList<ContactItem> getAllContacts() {
		return mAllContacts;
	}

	public void setAllContacts(ArrayList<ContactItem> mAllContacts) {
		this.mAllContacts = mAllContacts;
	}
	
	public Participant getPartyByPhone(String phoneNumber) {
		
		Participant part = null;
		
		ArrayList<Participant> parties = new ArrayList<Participant>();
		
		parties.addAll(mSelectedContacts);
		parties.addAll(mInputParty);
		
		do {
			
//			Util.BIZ_CONF_DEBUG(TAG, "phoneNumber: " + phoneNumber 
//					+ "destinate party phoneNum: " + destinateParty.getPhone());
			
			if (phoneNumber.contains(destinateParty.getPhone())) {
				
				part = destinateParty;
				break;
			}
			
			for (Participant party: parties) {
				
				if (phoneNumber.contains(party.getPhone())){	
					part = party;
					break;
				}
			}	
			
			Participant currentUser = ContactManager.getInstance().getCurrentUserObject();
			
			//When we get a null party object from user selected parties, we check whether the party is the 
			//one user specified in dial out moudle
			if (null == part && !Util.isEmpty(currentUser.getPhone()) 
					&& phoneNumber.contains(currentUser.getPhone())){
				
				//Util.BIZ_CONF_DEBUG(TAG, "current user assigned");
				part = currentUser;
			}			
		}while(false);	
			
		return part;	
	}
	
	public Participant getSelectedContactByIndex(int index) {
		
		Participant part = null;
		
		if(isIndexValid(index)){
			
			part = mSelectedContacts.get(index);
		}
		
		return part;
	}
	
	public boolean isIndexValid(int index) {
		
		if(index < 0 || index > mSelectedContacts.size()) {
			
			return false;
		}
		
		return true;
	}
	
	/**
	 * add a selected contact
	 * @param participant
	 */
	public void addSelectedContact(Participant participant){
		
		if(!mSelectedContacts.contains(participant)) {
			
			mSelectedContacts.add(participant);
		}		
	}
	
	public void removeSelectedContact(String phoneNumber) {
		
		Participant delParty = null;
		
		for (Participant sParty : mSelectedContacts) {
			
			if (phoneNumber.contains(sParty.getPhone())) {
				
				delParty = sParty;
				break;
			}
		}
		
		if (null != delParty) {
			
			removeSelectedContact(delParty);
			
			for (ContactItem item: mAllContacts) {
				
				if (item.getId() == delParty.getContactId()) {
					
					item.setAttrSelectedState(delParty.getSelectedAttrPosInContactItem(), false);
					break;
				}
			}
		}
	}
	
	public void removeSelectedContact(Participant participant) {
		
		if(mSelectedContacts.contains(participant)) {
			
			mSelectedContacts.remove(participant);
		}		
	}
	
	public void removePartiesInList(String partyId) {
		
		ArrayList<Participant> parties = getAllSelectedContacts();
		
		Participant delParty = null;
		
		for (Participant party : parties) {
			
			if (partyId.equalsIgnoreCase(party.getIdInConference())) {
				
				delParty = party;
				break;
			}
		}
		
		removeInputParty(delParty);
		removeSelectedContact(delParty);
	}
	
	public ArrayList<Participant> getSelectedContacts() {
		
		return mSelectedContacts;
	}
	
	public ArrayList<Participant> getAllSelectedContacts() {
		
		ArrayList<Participant> allSelectParties = new ArrayList<Participant>();
		
		allSelectParties.addAll(mInputParty);
		allSelectParties.addAll(mSelectedContacts);
		
		return allSelectParties;
	}
	
	public String[] getSelectedEmails() {
		
		ArrayList<Participant> allSelectParties = getAllSelectedContacts();
		
		ArrayList<String> emails = new ArrayList<String>();
		
		//Util.BIZ_CONF_DEBUG(TAG, "selectedParty size: " + allSelectParties.size());
		
		for (Participant party : allSelectParties) {			
			
			//Util.BIZ_CONF_DEBUG(TAG, "party name: " + party.getName());
			//Util.BIZ_CONF_DEBUG(TAG, "email: " + party.getEmail());
			if (!Util.isEmpty(party.getEmail())) {
				
				emails.add(party.getEmail());
			}
		}
		
		String[] contents = new String[emails.size()];
		
		//Util.BIZ_CONF_DEBUG(TAG, "emails size: " + emails.size());
		
		for (int i = 0; i < contents.length ;i++) {
			
			contents[i] = emails.get(i);
		}
		
		return contents;
	}
	
	public String[] getSelectedPhones() {
		
		ArrayList<Participant> allSelectParties = getAllSelectedContacts();
		
		ArrayList<String> phones = new ArrayList<String>();
		
		for (Participant party : allSelectParties) {
			
			if (!Util.isEmpty(party.getPhone())) {
				
				phones.add(party.getPhone());
			}
		}
		
		String[] contents = new String[phones.size()];
		
		for (int i = 0; i < contents.length ;i++) {
			
			contents[i] = phones.get(i);
			//Util.BIZ_CONF_DEBUG(TAG, "selected phone: " + contents[i]);
		}
		return contents;
	}
	
	private void sortAllContacts() {

		@SuppressWarnings("rawtypes")
		final Comparator cmp = Collator.getInstance(java.util.Locale.CHINA);

		@SuppressWarnings("unchecked")
		Comparator<ContactItem> comparator = new Comparator<ContactItem>() {
			@Override
			public int compare(ContactItem entry1, ContactItem entry2) {
				return cmp.compare(entry1.getSortKey(), entry2.getSortKey());
			}
		};

		Collections.sort(mAllContacts, comparator);	
	}

	private ArrayList<ContactItem> rmNoNumberContact() {

		Cursor contactCursor = AppClass.getInstance().getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

		while (contactCursor.moveToNext()) {

			int hasNumber = contactCursor
					.getInt(contactCursor
							.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
			int id = contactCursor.getInt(contactCursor
					.getColumnIndex(ContactsContract.Contacts._ID));

			String sortKey = contactCursor
					.getString(contactCursor
							.getColumnIndex(ContactsContract.Contacts.SORT_KEY_PRIMARY));

			if (hasNumber == 0) {

				Id2Contact.remove(id);
			} else {

				ContactItem person = Id2Contact.get(id);
				person.setSortKey(sortKey);
				person.setId(id);
				
				//Util.BIZ_CONF_DEBUG(TAG, "had phone data");
				
				if (person != null) {
					
					person.initSelectedState();
					
					//Util.BIZ_CONF_DEBUG(TAG, "add contact to array");
					
					mAllContacts.add(person);
				}
			}
		};

		if (contactCursor != null) {

			contactCursor.close();
		}
		return mAllContacts;
	}

	private void loadAllContacts(Context ctx) {
		
		Cursor cursor = ctx.getContentResolver().query(
				ContactsContract.Data.CONTENT_URI, null, null, null, null);
		
		//Util.BIZ_CONF_DEBUG(TAG, "loadAllContacts");
		
		while (cursor.moveToNext()) {
			
			//Util.BIZ_CONF_DEBUG(TAG, "has datas");
			
			int id = cursor.getInt(cursor
					.getColumnIndex(ContactsContract.Data.CONTACT_ID));
			String info = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Data.DATA1));
			String mimeType = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Data.MIMETYPE));

			ContactItem person = null;

			if (Id2Contact.get(id) != null) {

				person = Id2Contact.get(id);
			} else {

				person = new ContactItem();
				Id2Contact.put(id, person);
			}

			if (mimeType.equals(CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
				person.addEmail(info);

			} else if (mimeType
					.equals(CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)) {
				person.setAddress(info);
			} else if (mimeType.equals(CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
				person.addPhone(info);
			} else if (mimeType
					.equals(CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)) {
				person.setName(info);
			}
		}

		if (cursor != null) {

			cursor.close();
		}
	}
	
	public Participant getOriginalParty() {
		return originalParty;
	}

	public void setOriginalParty(Participant newParty) {
		
		//Util.BIZ_CONF_DEBUG(TAG, "set originalParty party now");
		
		originalParty.setIdInConference(newParty.getIdInConference());
		originalParty.setName(newParty.getName());
		originalParty.setPhone(newParty.getPhone());
		originalParty.setIsModerator(newParty.isModerator());
	}

	public Participant getDestinateParty() {
		return destinateParty;
	}

	public void setDestinateParty(Participant newParty) {
		
		//Util.BIZ_CONF_DEBUG(TAG, "set destinate party now");
		
		destinateParty.setIdInConference(newParty.getIdInConference());
		destinateParty.setName(newParty.getName());
		destinateParty.setPhone(newParty.getPhone());
	}
}
