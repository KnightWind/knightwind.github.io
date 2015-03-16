package com.sktlab.bizconfmobile.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.adapter.ContactListAdapter;
import com.sktlab.bizconfmobile.interfaces.ILoadingDialogCallback;
import com.sktlab.bizconfmobile.interfaces.IOnCheckedListener;
import com.sktlab.bizconfmobile.model.Alphabet;
import com.sktlab.bizconfmobile.model.ConfControl;
import com.sktlab.bizconfmobile.model.ContactItem;
import com.sktlab.bizconfmobile.model.Participant;
import com.sktlab.bizconfmobile.model.manager.ContactManager;
import com.sktlab.bizconfmobile.util.AlphbetToListUtil;
import com.sktlab.bizconfmobile.util.GetFirstSpell;
import com.sktlab.bizconfmobile.util.LoadingDialogUtil;
import com.sktlab.bizconfmobile.util.Util;
import com.sktlab.bizconfmobile.util.ValidatorUtil;

@SuppressLint("NewApi")
public class AddParticipantActivity extends BaseActivity implements IOnCheckedListener, ILoadingDialogCallback{
	
	public static final String TAG = "AddParticipantActivity";
	
	public static final String KEY_OF_SHOW_EMAIL_ADDRESS = "com.sktlab.show.email.key";
	private ExpandableListView mExpandListview;
	private ListView mListView;
	private ContactListAdapter mContactListAdapter;
	private List<Alphabet> alphabetsList;
	
	private Map<String,Alphabet> alphabets; 
	//all contacts in user's phone book
	private ArrayList<ContactItem> mContacts;
	//user checked parties
	private Set<Participant> mSelectParties;
	//the new increase parties
	private ArrayList<Participant> mNewSelectedParties;
	//the new remove parties
	private ArrayList<Participant> mNewRmSelectedParties;
	//the new input parties
	private ArrayList<Participant> mNewInputParties;
	//the new remove input parties
	private ArrayList<Participant> mNewRmInputParties;
	
	private LinearLayout mSelectedPartLayout;
	
	private Context mCtx;
	
	private LayoutInflater mInflater;
	
	private HorizontalScrollView mHsView;
	
	private Button mBtConfirm;
	private EditText mEtInputContent;
	
	private boolean isShowEmail = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activtiy_add_participant);		
		
		init();	
		//Util.shortToast(mCtx, R.string.toast_add_fixed_phone_num);
	}

	@Override
	protected void onResume() {
		
		super.onResume();	
		//when this activity resume, refresh data.
		//because if data refresh not in time, the list view's data was recycled by system
		//then cause APP crash
		//init();
	}
	
	private boolean needClearCheckedState = true;
	
	@Override
	public void finish() {
		
		super.finish();
		
		if (needClearCheckedState) {
			
			changePartyState(mNewSelectedParties, false);
			changePartyState(mNewRmSelectedParties, true);			
		}		
		
	}

	private void changePartyState(ArrayList<Participant> parties, boolean state) {
		
		if (!Util.isEmpty(parties)) {
			
			for(Participant item : parties) {
				
				int conatactId = item.getContactId();
				int selectedAttrPos = item.getSelectedAttrPosInContactItem();
				
				ContactItem contact = ContactManager.getInstance().getContactById(conatactId);
				
				if (!Util.isEmpty(contact)) {
					
					contact.setAttrSelectedState(selectedAttrPos, state);
				}			
			}
			
			parties.clear();
		}			
	}
	/**
	 * should add selected party to conference
	 * 
	 * we can get it from ContactManager.getInstance().getAllSelectedContacts()
	 */
	@Override
	public void onRightButtonClicked(View v) {
				
		do {
			
			ContactManager.getInstance().getSelectedContacts().addAll(mNewSelectedParties);
			ContactManager.getInstance().getInputParties().addAll(mNewInputParties);
			
			for (Participant party : mNewRmSelectedParties) {
				party.setSelectedAttrPosInContactItem(-1);
				ContactManager.getInstance().removeSelectedContact(party);
			}
			
			for (Participant party : mNewRmInputParties) {
				party.setSelectedAttrPosInContactItem(-1);
				ContactManager.getInstance().removeInputParty(party);
			}
			
			needClearCheckedState = false;
			
			if (isShowEmail) {
				
				break;
			}
			
			//add input parties to be guest call
			mNewSelectedParties.addAll(mNewInputParties);
			
			Handler handler = new Handler();
			
			//the interval time when send add party message to server, milliseconds
			int interval = 20;
			int i = 0;
			//for (Participant party : mSelectParties){
			for (final Participant party : mNewSelectedParties){
				
				if (Util.isEmpty(party.getIdInConference())) {
					
					handler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							
							ConfControl.getInstance().addPartyToConf(party, false);
						}
					}, i * interval);	
					
					i++;
				}	
								
			}									
		}while(false);
				
		finish();
	}

	public void init(){
		
		mCtx = this;
		
		setShowRightButton(true);
		
		mInflater = (LayoutInflater)mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		mSelectedPartLayout = (LinearLayout)findViewById(R.id.layout_select_contact);
		mExpandListview = (ExpandableListView) findViewById(R.id.elv_contact_list);
		mListView = (ListView) findViewById(R.id.elv_contact_alpha);
		mListViewInit();
		mSelectParties = new HashSet<Participant>();
		
		mNewSelectedParties = new ArrayList<Participant>();
		mNewRmSelectedParties = new ArrayList<Participant>();
		mNewInputParties = new ArrayList<Participant>();
		mNewRmInputParties = new ArrayList<Participant>();
		
		final LoadingDialogUtil dialog = new LoadingDialogUtil(this, this);
		
		AppClass.getInstance().getService().submit(new Runnable() {
			
			@Override
			public void run() {
			
				dialog.showDialog(R.string.toast_loading_contacts);
				
				ContactManager.getInstance().Load();
				
				dialog.finishDialogSuccessDone();
			}
		});
	}
	
	
	private void mListViewInit(){
		mListView.setDividerHeight(0);
		
		mListView.setAdapter(new ArrayAdapter<String>(this,R.layout.simple_text,R.id.contact_index_tv,AlphbetToListUtil.getAlphbet()));
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				char c=AlphbetToListUtil.ALPHABET[arg2];
				
				for(int i=0;i<alphabetsList.size();i++){
					String name=alphabetsList.get(i).getAlp();
					if(name.charAt(0)==c){
						mExpandListview.setSelectedGroup(i);
					}
				}
				
			}
		});
	}
	/**
	 * when user checked or unchecked a a party 
	 */
	@Override
	public void onChecked(boolean checkState, Participant party) {
		
		do {
			
			if (null == mNewSelectedParties) {
				
				break;
			}
			
			if (mNewSelectedParties.contains(party)) {
				party.setSelectedAttrPosInContactItem(-1);
				mNewSelectedParties.remove(party);
				break;
			}
			
			//increase a new checked party
			if (checkState) {
				
				mNewSelectedParties.add(party);
				break;
			} 
			
			//unchecked a party
			
			
			//unchecked a party inclued in Contact
			//ContactManager.getInstance().removeSelectedContact(party);
			mNewRmSelectedParties.add(party);
		}while(false);
				
		showSelectedParty();
	}
	
	public void showSelectedParty() {
		
		mSelectParties.clear();
					
		mSelectParties.addAll(ContactManager.getInstance().getAllSelectedContacts());	
		mSelectParties.addAll(mNewSelectedParties);	
		mSelectParties.addAll(mNewInputParties);
		
		mSelectParties.removeAll(mNewRmSelectedParties);
		mSelectParties.removeAll(mNewRmInputParties);
		
		int partCount = mSelectParties.size();
		
		if(partCount > 0) {
			
			mSelectedPartLayout.setVisibility(View.VISIBLE);
		}else {
			
			mSelectedPartLayout.setVisibility(View.GONE);
		}
		
		//remove all views added before
		mSelectedPartLayout.removeAllViews();
		
		for(Participant participant: mSelectParties){		
			
			//show the specified participant
			TextView tvName = getTextView();		
			tvName.setText(participant.getName());
			tvName.setVisibility(View.VISIBLE);
			tvName.setOnClickListener(onItemClickListener);
			
			//set the participant as tag of the text view, we will use it when user click the textview
			tvName.setTag(participant);
			
			mSelectedPartLayout.addView(tvName);						
		}		
        
		//use this method to have a full scroll
		mHsView.post(new Runnable() {
			
			@Override
			public void run() {
			
				mHsView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
			}
		});
		
		mContactListAdapter.notifyDataSetChanged();
	}
	
	private TextView getTextView() {

		LinearLayout layout = (LinearLayout)mInflater.inflate(R.layout.item_participant_list, null);
		
		TextView tv = (TextView)layout.findViewById(R.id.tv_1);
		
		layout.removeView(tv);
		return tv;
	}

	private OnClickListener onItemClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			//Util.BIZ_CONF_DEBUG(TAG, "onItemClicked called");
			
			Participant item = (Participant)v.getTag();
			
			if(!Util.isEmpty(item)) {
				
				int conatactId = item.getContactId();
				int selectedAttrPos = item.getSelectedAttrPosInContactItem();
				
				ContactManager cm = ContactManager.getInstance();
				
				if (selectedAttrPos != -1) {
					
					if (mNewSelectedParties.contains(item)) {
						
						mNewSelectedParties.remove(item);
					} else {
						
						mNewRmSelectedParties.add(item);
					}
					
					//cm.removeSelectedContact(item);
					
					ContactItem contact = cm.getContactById(conatactId);
					contact.setAttrSelectedState(selectedAttrPos, false);
				}else {
					
					if (mNewInputParties.contains(item)) {
						
						mNewInputParties.remove(item);
					}else {
						
						mNewRmInputParties.add(item);
					}					
					//cm.removeInputParty(item);
				}
				
				showSelectedParty();
			}	
		}
	};
	
	/**
	 * When order a meeting, user can input email and phones, so we should check email and phone both
	 * 
	 * @param v
	 */
	private void select(){
		
		String phoneNumOrEmail = mEtInputContent.getText().toString().trim().toLowerCase();
		Participant inputParty = new Participant();
		List<ContactItem> resultContacts = new ArrayList<ContactItem>();
		ContactItem resultItem = null;
		
		if(phoneNumOrEmail.length()!=0){
			String str=GetFirstSpell.getFirstSpell( phoneNumOrEmail).toLowerCase();
			int m = 0;
			int n = 0;
			
			//boolean isNumberValid = ValidatorUtil.isNumberValid(inputContent);
				for(int i=0;i<alphabetsList.size();i++){
						
						List<ContactItem> contacts = alphabetsList.get(i).getContacts();
						for(int j=0;j<contacts.size();j++){
							
							ContactItem item = contacts.get(j);
							
							for(int a=0;a<item.getPhone().size();a++){
									if((item.getPhone().get(a).toLowerCase().trim()).indexOf((phoneNumOrEmail.toLowerCase()))==0){
										m = i;
										n= j;
										resultItem = item;
										resultContacts.add(item);
										break;
									}
							}
							for(int a=0;a<item.getEmail().size();a++){
								if(isShowEmail&&(item.getEmail().get(a).toLowerCase().trim()).indexOf((phoneNumOrEmail.toLowerCase()))==0){
									m = i;
									n= j;
									resultItem = item;
									resultContacts.add(item);
									break;
								}
							}
						}
//							if(item.getEmail().contains(phoneNumOrEmail)){
//								resultContacts.add(item);
//								inputParty.setName(ContactManager.getContactNameByPhoneNumber(phoneNumOrEmail));
//								inputParty.setPhone(phoneNumOrEmail);
//								//ContactManager.getInstance().addInputParty(inputParty);
//								mNewInputParties.add(inputParty);
//								showSelectedParty();
//								mEtInputContent.setText("");
//								mExpandListview.setSelectedChild(i, j, false);
//								return;
//							}
						
				}
				
				if(alphabets.get(str)!=null){
					for(int i=0;i<alphabetsList.size();i++){
						String name=alphabetsList.get(i).getAlp();
						if(name.charAt(0)==str.charAt(0)){
							List<ContactItem> contacts = alphabetsList.get(i).getContacts();
							for(int j=0;j<contacts.size();j++){
								ContactItem item = contacts.get(j);
								if(item.getName().trim().toLowerCase().indexOf( phoneNumOrEmail)==0){
									resultItem = item;
									resultContacts.add(item);
									m = i;
									n= j;
//									if((contacts.get(j).getEmail().size()+contacts.get(j).getPhone().size())>1){
//										mExpandListview.setSelectedChild(i, j, false);
//										return;
//									}
//									inputParty = new Participant();
//									inputParty.setName(inputContent);
//									inputParty.setPhone(contacts.get(j).getPhone().get(0));
//									//ContactManager.getInstance().addInputParty(inputParty);
//									mNewInputParties.add(inputParty);
//									showSelectedParty();
//									mEtInputContent.setText("");
//									mExpandListview.setSelectedChild(i, j, false);
//									return;
								}
							}
						}
				}
				
//				if(resultContacts.size()==1){
//					if((resultItem.getPhone().size()+resultItem.getEmail().size())>1){
//						mEtInputContent.setText("");
//						getAlphabets(resultContacts);
//						mContactListAdapter.setData(alphabetsList);
//						mContactListAdapter.notifyDataSetChanged();
//						getAlphabets(mContacts);
//						return;
//					}else if((resultItem.getPhone().size()+resultItem.getEmail().size())==1){
//						if(flag==1){
//							inputParty.setPhone(phoneAndMail);
//						}else if(flag == 2){
//							inputParty.setEmail(phoneAndMail);
//						}else if(flag == 3){
//							if(resultItem.getPhone().get(0)!=null){
//								inputParty.setPhone(resultItem.getPhone().get(0));
//							}else if(resultItem.getPhone().get(0)!=null){
//								inputParty.setEmail(resultItem.getEmail().get(0));
//							}
//						}
//						inputParty.setName(ContactManager.getContactNameByPhoneNumber(phoneAndMail));
//						//ContactManager.getInstance().addInputParty(inputParty);
//						mNewInputParties.add(inputParty);
//						showSelectedParty();
//						mEtInputContent.setText("");
//						mExpandListview.setSelectedChild(m, n, false);
//					}
//					
//				}
				}
				mExpandListview.setSelectedChild(m, n, false);
				getAlphabets(resultContacts);
				mContactListAdapter.setData(alphabetsList);
				mContactListAdapter.notifyDataSetChanged();
				int groupCount = mContactListAdapter.getGroupCount();
				   for (int i=0; i<groupCount; i++) {
				       mExpandListview.expandGroup(i);
				       };
				getAlphabets(mContacts);
				return;
		}else{
			getAlphabets(mContacts);
			mContactListAdapter.setData(alphabetsList);
			mContactListAdapter.notifyDataSetChanged();
			int groupCount = mContactListAdapter.getGroupCount();
			   for (int i=0; i<groupCount; i++) {
			       mExpandListview.expandGroup(i);
			       };
		}
		
//		boolean isNumberValid = ValidatorUtil.isNumberValid(inputContent);
//		boolean isEmailValid =  ValidatorUtil.isEmailValid(inputContent);
//		
//		do{
//					
//			if (isNumberValid) {
//				
//				String phoneNum = mEtInputContent.getText().toString();
//				
//				Participant inputParty = new Participant();
//				
//				inputParty.setName(ContactManager.getContactNameByPhoneNumber(phoneNum));
//				inputParty.setPhone(phoneNum);
//				
//				//ContactManager.getInstance().addInputParty(inputParty);
//				mNewInputParties.add(inputParty);
//				
//				showSelectedParty();
//				mEtInputContent.setText("");
//				break;
//			}
//			
//			if (isShowEmail && isEmailValid) {
//				
//				String emailAddress = mEtInputContent.getText().toString();
//				
//				Participant inputParty = new Participant();
//				
//				inputParty.setName(emailAddress);
//				inputParty.setEmail(emailAddress);
//				
//				//ContactManager.getInstance().addInputParty(inputParty);
//				mNewInputParties.add(inputParty);
//				
//				showSelectedParty();
//				mEtInputContent.setText("");
//				break;
//			}
//			
//			if (isShowEmail) {
//				
//				Util.shortToast(mCtx, R.string.toast_input_verify);
//				Util.requestFocus(mEtInputContent);
//				break;
//			}
//
//			if(!isNumberValid) {
//				
//				Util.shortToast(mCtx, R.string.toast_phone_num_invalid);
//				Util.requestFocus(mEtInputContent);
//				break;
//			}
//			
//		}while(false);
		
		
	}
	public void onInputNumConfirmClicked(View v) {
		
		String inputContent = mEtInputContent.getText().toString().trim();
		String phoneNumOrEmail = mEtInputContent.getText().toString();
		Participant inputParty = new Participant();
		
		do{
			
			if(ValidatorUtil.isNumberValid(phoneNumOrEmail)
					|| ValidatorUtil.isNumberCodeValid(phoneNumOrEmail)){			
				
				String partyName = ContactManager.getContactNameByPhoneNumber(phoneNumOrEmail);
				
				inputParty.setName(partyName);
				
				if (partyName.equals(phoneNumOrEmail)) {
					
					Util.shortToast(AppClass.getInstance(), R.string.cont_not_exist1);
				}
				
				inputParty.setPhone(phoneNumOrEmail.replace("-", "w,,,,,").replace("+", "00"));
				mNewInputParties.add(inputParty);
				showSelectedParty();
				mEtInputContent.setText("");
				
				break;
			}
			
			if(isShowEmail&&ValidatorUtil.isEmailValid(phoneNumOrEmail)){
				
				Util.shortToast(AppClass.getInstance(), R.string.cont_not_exist1);
				
				inputParty.setName(ContactManager.getContactNameByPhoneNumber(phoneNumOrEmail));
				inputParty.setEmail(phoneNumOrEmail);
				mNewInputParties.add(inputParty);
				showSelectedParty();
				mEtInputContent.setText("");
				
				break;
			}
			
			Util.shortToast(AppClass.getInstance(), R.string.cont_not_exist);
		}while(false);
	}
	
	private void classifyContacts(List<ContactItem> resultContacts){
		alphabets = new HashMap<String,Alphabet>();
		SharedPreferences sp=getSharedPreferences("head",Context.MODE_PRIVATE);
		for(ContactItem contact : resultContacts){
			
			String uriStr=sp.getString(contact.getId()+"", null);
			if(uriStr!=null){
				contact.setUri(Uri.parse(uriStr));
			}
			
			String name = contact.getName();
			String firstSpell="#";
			
			if(name!=null&& !name.isEmpty()){
				firstSpell=GetFirstSpell.getFirstSpell(name);
			}else{
				contact.setName("No name");
			}
			
			Alphabet alphabet = alphabets.get(firstSpell);
			if(alphabet==null){
				alphabet=new Alphabet(firstSpell);
				alphabet.addContactItem(contact);
				alphabets.put(firstSpell,alphabet);
			}else{
				alphabet.addContactItem(contact);
				alphabets.put(firstSpell,alphabet);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void getAlphabets(List<ContactItem> resultContacts){
		classifyContacts(resultContacts);
		 alphabetsList = new ArrayList<Alphabet>();
		for(String str:alphabets.keySet()){
			alphabetsList.add(alphabets.get(str));
		};
		Collections.sort(alphabetsList);
	}
	@Override
	public void onSuccessDone() {
		
		Util.shortToast(mCtx, R.string.toast_add_fixed_phone_num);
		
		mContacts = ContactManager.getInstance().getAllContacts();
						
		getAlphabets(mContacts);
		
		//Util.BIZ_CONF_DEBUG(TAG, "mContacts.size: " + mContacts.size());
		
		//mContactListAdapter.setData(mContacts); 
		
		isShowEmail = getIntent().getBooleanExtra(KEY_OF_SHOW_EMAIL_ADDRESS, false);
		
		mContactListAdapter = 
				new ContactListAdapter(this, alphabetsList, isShowEmail);
		
		mExpandListview.setAdapter(mContactListAdapter);
		mContactListAdapter.setCallback(this);	
		mExpandListview.setOnChildClickListener(mContactListAdapter);
		//this method make group can not answer user's click
		mExpandListview.setOnGroupClickListener(mContactListAdapter);
		mExpandListview.setOnItemLongClickListener(mContactListAdapter); 
		   //将所有项设置成默认展开
		int groupCount = mContactListAdapter.getGroupCount();
		  
		
		for(int i = 0; i < groupCount; i++) {
			
			mExpandListview.expandGroup(i);
		}
		
		mHsView = (HorizontalScrollView) findViewById(R.id.hs_add_party);
		
		LinearLayout layoutInputNumber = (LinearLayout) findViewById(R.id.layout_input_num);
		
		mBtConfirm = (Button) layoutInputNumber.findViewById(R.id.bt_for_confirm);
		mEtInputContent = (EditText) layoutInputNumber.findViewById(R.id.et_for_input);
		
		mEtInputContent.setInputType(InputType.TYPE_CLASS_TEXT);
	
		showSelectedParty();	
		mEtInputContent.addTextChangedListener(new TextWatcher() {
			
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				//select();
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			public void afterTextChanged(Editable arg0) {
				select();
				
			}
		});
	}

	@Override
	public void onDoneWithError() {
	
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 1){
			if(data!=null){
				if(ContactListAdapter.parent!=-1&&ContactListAdapter.child!=-1){
					ContactItem item=alphabetsList.get(ContactListAdapter.parent).getContacts().get(ContactListAdapter.child);
					ContactListAdapter.parent = -1;
					ContactListAdapter.child = -1;
					item.setUri(Uri.parse(data.getDataString()));
					
					SharedPreferences sp=getSharedPreferences("head",Context.MODE_PRIVATE);
					Editor ed=sp.edit();
					ed.putString(item.getId()+"", data.getDataString());
					ed.commit();
					mContactListAdapter.notifyDataSetChanged();
				}
			}
		}else if(requestCode == 10){
				if(null != data ){
					String info = data.getStringExtra("pom");
					int pos = data.getIntExtra("pos", -1);
					mContactListAdapter.addPart(info,pos);
				}
		}
		
	}
}
