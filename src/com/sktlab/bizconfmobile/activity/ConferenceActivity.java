package com.sktlab.bizconfmobile.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.LinearLayout;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.adapter.PartyListAdapter;
import com.sktlab.bizconfmobile.interfaces.ILoadingDialogCallback;
import com.sktlab.bizconfmobile.mina.MinaUtil;
import com.sktlab.bizconfmobile.model.BridgeInfo;
import com.sktlab.bizconfmobile.model.ConfAccount;
import com.sktlab.bizconfmobile.model.ConfControl;
import com.sktlab.bizconfmobile.model.ConfHistory;
import com.sktlab.bizconfmobile.model.Constant;
import com.sktlab.bizconfmobile.model.Participant;
import com.sktlab.bizconfmobile.model.manager.CommunicationManager;
import com.sktlab.bizconfmobile.model.manager.ConfHistoryManager;
import com.sktlab.bizconfmobile.model.manager.ContactManager;
import com.sktlab.bizconfmobile.util.LoadingDialogUtil;
import com.sktlab.bizconfmobile.util.Util;
import com.sktlab.bizconfmobile.util.ValidatorUtil;

public class ConferenceActivity extends BaseActivity implements OnClickListener,
								ILoadingDialogCallback{
	
	public static final String TAG = "ConferenceActivity";
	
	public static final String NEED_UPDATE_CONF_STATE_FILTER = "com.sktlab.bizconf.conf.state.change.msg";
	
	private Activity mActivity;
	private Context mCtx;
	private ConfAccount mConfAccount;
	private ExpandableListView mElvParticipantlist;
	private PartyListAdapter mAdapter;
	
	private ConfHistory mHistory;
	
	private LinearLayout mLayoutForModerator1;
	private LinearLayout mLayoutForModerator2;
	private LinearLayout mLayoutForGuest;
	
	private CheckedTextView mCtvHfState;
	private CheckedTextView mCtvSelfMute;
	
	private CheckedTextView mCtvOpearteModule;
	
	private LoadingDialogUtil mLoadingDialog;
	
	private AlertDialog leaveConfDialog;
	
	private Button back ;
	private Button leave;
	private Button finish;
	private Button cancel;
	private Dialog backPressedDialog;
	
	private ArrayList<Participant> newMuteParties;
	
	private final int LEAVE_CONF_DIALOG = 1001;
	private final int OTHER_FUNC_DIALOG = 1002;
	private final int PHONE_TRANSFER_DIALOG = 1003;
	
	private int mDialogType = LEAVE_CONF_DIALOG;
	
	private int mOperateModule = -1;
	private Participant mMuteOPParty = null;
	
	private ConfChangeReceiver mConfChangeReceiver;
	
	private EditText mEtPhoneTransfer = null;
	
	private boolean isModeratorLeaveConf;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_conference_main);
		
		mConfChangeReceiver = new ConfChangeReceiver();
		
		newMuteParties = new ArrayList<Participant>();
				
		registerReceiver(mConfChangeReceiver, new IntentFilter(NEED_UPDATE_CONF_STATE_FILTER));
		
		setShowRightButton(true);
		setRightBtText(getResources().getString(R.string.main_tab_service));
		
		initView(); 
		
		//when user not enable out call or the network not available, call the service number to join the conference
		callToJoinConf();
	}
	
	public void callToJoinConf() {
		
		if (!Util.isNetworkReadyForConf(mCtx)
				|| (null != mConfAccount && !mConfAccount.isDialOutEnable())) {
	
			new BridgeInfo(mConfAccount.getConfCode());
			
			do {
				
				String phoneNumber = "";
				
				String model = android.os.Build.MODEL;
				
				String waitingTime = ",,";
				
				if (model.equals("SCH-I939")) {
					
					waitingTime = ",,,,,,,,";
				}
				
				String accessNumber = mConfAccount.getAccessNumber();
				
				if (accessNumber.startsWith("86")) {
					
					accessNumber = "+" + accessNumber;
				}
				if(BridgeInfo.templateType==Constant.BRIDGE_TYPE_REPLUS){
					
					//BRIDGE_TYPE_RESEXPRESS format:
					//doublePwd == 0 && 
					//templateType == BRIDGE_TYPE_REPLUS
					/*主持人入会是电话播出
					接入号码,,*主持人密码*
					参会者是电话播出
					接入号码,,*会议号码*
					*/
					
					if (CommunicationManager.getInstance().isModeratorAccount()) {
						
						Participant currentUser = ContactManager.getInstance().getCurrentUserObject();
						
						//moderator had been in conference, not call to join conference just manage conference.
						if (!Util.isEmpty(currentUser.getIdInConference())) {
							
							break;
						}
						if(BridgeInfo.doublePwd==0){
							
							Util.BIZ_CONF_DEBUG(TAG, "resexpress moderator call now");
							phoneNumber = accessNumber + waitingTime + mConfAccount.getModeratorPw() + "#";
						}else{
							Util.BIZ_CONF_DEBUG(TAG, "replus moderator call now");
							phoneNumber = accessNumber + waitingTime
									+ mConfAccount.getConfCode() + "#" + "," + "*" 
									+ mConfAccount.getModeratorPw() + "#";
						}
						
						
					}else {
						if(BridgeInfo.doublePwd==0){
							Util.BIZ_CONF_DEBUG(TAG, "resexpress guest call now");
							phoneNumber = accessNumber + waitingTime + mConfAccount.getConfCode() + "#";
						}else{
							
							Util.BIZ_CONF_DEBUG(TAG, "replus guest call now");
							phoneNumber = accessNumber + waitingTime
									+ mConfAccount.getConfCode() + "#";
						}
					}				
					
				}else if(BridgeInfo.templateType==Constant.BRIDGE_TYPE_GENESYS){
					if (CommunicationManager.getInstance().isModeratorAccount()) {
						
						Participant currentUser = ContactManager.getInstance().getCurrentUserObject();
						
						//moderator had been in conference, not call to join conference just manage conference.
						if (!Util.isEmpty(currentUser.getIdInConference())) {
							
							break;
						}
						/*
						 * 原来主持人入会是电话播出
								接入号码,,会议号码#,*主持人密码#
								
								参会者是电话播出
								接入号码,,会议号码#
								
								现在如果是replus时则不变，
								
								如果是Genesys则改为
								
								主持人入会是电话播出
								接入号码,,*会议号码*,*主持人密码*
								
								参会者是电话播出
								接入号码,,*会议号码*
						 * */
						phoneNumber = accessNumber + waitingTime+"*"
								+ mConfAccount.getConfCode() + "*" + "," + "*"
								+ mConfAccount.getModeratorPw() + "*";
						
					}else {
						phoneNumber = accessNumber + waitingTime+"*"
								+ mConfAccount.getConfCode() + "*";
					}		
				}
				
//				else if(BridgeInfo.templateType==Constant.BRIDGE_TYPE_RESEXPRESS){
//					if (CommunicationManager.getInstance().isModeratorAccount()) {
//						/*主持人入会是电话播出
//						接入号码,,主持人密码#
//						参会者是电话播出
//						接入号码,,会议号码#
//						*/
//						phoneNumber = accessNumber + waitingTime+"*"
//								+ mConfAccount.getModeratorPw() + "*";
//					}else{
//						phoneNumber = accessNumber + waitingTime+"*"
//								+ mConfAccount.getConfCode() + "*";
//								
//					}
//					
//				}
				
				else{
					//when the confcode is not connect to the net 
					return;
				}
				
				BridgeInfo.templateType=0;
				
				
				//phoneNumber = HURLEncoder.encode(phoneNumber);
				try {
					
					phoneNumber = URLEncoder.encode(phoneNumber, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					//Util.BIZ_CONF_DEBUG(TAG, "catch error: " + e.getMessage());
					e.printStackTrace();
				}
				
				//Util.BIZ_CONF_DEBUG(TAG, "dial number:" + phoneNumber);

				Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
						+ phoneNumber));

				mActivity.startActivity(callIntent);
				
			}while(false);			
		}
	}
	
	private void initView() {
		
		mActivity = this;
		mCtx = this;
		
		mLoadingDialog = new LoadingDialogUtil(mActivity, this);		
		mConfAccount = CommunicationManager.getInstance().getActiveAccount();
		
		if (!Util.isEmpty(mConfAccount)) {
			
			setTitleName(mConfAccount.getConfAccountName());
		}else {
			
			Util.BIZ_CONF_DEBUG(TAG, "memory recyled");
			AppClass.getInstance().setNeed2Exit(true);
			
			this.finish();
			return;
		}
		
		mLayoutForModerator1 = (LinearLayout) findViewById(R.id.layout_for_moderator_1);
		mLayoutForModerator2 = (LinearLayout) findViewById(R.id.layout_for_moderator_2);
		mLayoutForGuest = (LinearLayout) findViewById(R.id.layout_for_guest);
		
		if (!Util.isEmpty(mConfAccount.getModeratorPw())) {
			
			mCtvSelfMute = (CheckedTextView) mLayoutForModerator1.findViewById(R.id.rb_self_mute);
			mCtvHfState = (CheckedTextView) mLayoutForModerator1.findViewById(R.id.rb_open_hf);
		}else {
			
			mCtvSelfMute = (CheckedTextView) mLayoutForGuest.findViewById(R.id.ctv_guest_self_mute);
			mCtvHfState = (CheckedTextView)  mLayoutForGuest.findViewById(R.id.ctv_guest_hf);
		}
		
		do{
			
			if (Util.isEmpty(mConfAccount)) {
				
				break;
			}
			
			if (Util.isEmpty(mConfAccount.getModeratorPw())) {
				
				mLayoutForModerator1.setVisibility(View.GONE);
				mLayoutForModerator2.setVisibility(View.GONE);
				break;
			}
			
			mLayoutForGuest.setVisibility(View.GONE);
		
		}while(false);
		
		mElvParticipantlist = (ExpandableListView) findViewById(R.id.elv_participant_list);

		final List<Participant> data 
					= CommunicationManager.getInstance().getAllParties();
				
		mAdapter = new PartyListAdapter(this,data);
		
		mElvParticipantlist.setAdapter(mAdapter);
		mElvParticipantlist.setOnGroupExpandListener(new OnGroupExpandListener() {
			
			@Override
			public void onGroupExpand(int groupPosition) {	
				
				int totalGroup = data.size();
				
				for (int i = 0; i < totalGroup; i++) {
					
					if (i != groupPosition) {
						
						mElvParticipantlist.collapseGroup(i);
					}
				}
			}
		});
		mAdapter.setHolder(this);
		
		mHistory = new ConfHistory();
		
		if (null != mConfAccount) {
			
			mHistory.setAccountID(String.valueOf(mConfAccount.getAccountId()));
			mHistory.setAccountName(mConfAccount.getConfAccountName());
			mHistory.setConfCode(mConfAccount.getConfCode());
		}
		
		ConfHistoryManager chm = ConfHistoryManager.getInstance();
		 
		chm.addHistory(mHistory);
	}
	
	@Override
	protected void onResume() {
		
		super.onResume();
		
		Util.BIZ_CONF_DEBUG(TAG, "current taskId: " + this.getTaskId());
		
		isModeratorLeaveConf = false;
		
		CommunicationManager.getInstance().setInConfManageScreen(true);
		CommunicationManager.getInstance().setTurn2HomePage(false);
		if (null != mAdapter) {
			
			mAdapter.notifyDataSetChanged();
		}		
		
		if (Util.isHFOpen()) {
			
			//Util.BIZ_CONF_DEBUG(TAG, "spkeaker on");
			
			mCtvHfState.setChecked(true);
		}else {
			
			mCtvHfState.setChecked(false);
		}
		
	}

	@Override
	protected void onStop() {

		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		finishConfState();			
	}
		
	@Override
	public void finish() {
		
		super.finish();
	}

	public void showWaittingDialog(int operateModule) {
		
//		mLoadingDialog.showDialog(R.string.toast_operate_waitting, MinaUtil.CONNECT_WAITING_TIME);			
//		mOperateModule = operateModule;
		showWaittingDialog(operateModule, MinaUtil.CONNECT_WAITING_TIME);
	}
	
	public void showWaittingDialog(int operateModule, int timeout) {
		
		mLoadingDialog.showDialog(R.string.toast_operate_waitting, timeout);	
		mOperateModule = operateModule;
	}

	public void onHfClicked(CheckedTextView hf) {
		
		if(hf.isChecked()) {
			
			Util.OpenSpeaker();
		}else {
			
			Util.CloseSpeaker();
		}	
	}
	
	public void onRollCallClicked(CheckedTextView rollCall) {		
		
		do {
			
			if (!Util.isNetworkReadyForConf(mCtx)) {
				
				//Util.shortToast(mCtx, R.string.conf_call_name_no_net);
				Util.shortToast(mCtx, R.string.conf_no_net_function_toast);
				break;
			}
			
//			if(rollCall.isChecked()) {
				
			showWaittingDialog(CommunicationManager.ROLL_CALL);				
			ConfControl.getInstance().rollCall();
//			}else {
//				
//			}	
			
		}while(false);
	}
	
	public void onRecordClicked(CheckedTextView record) {
		
		do{
			
			if (!Util.isNetworkReadyForConf(mCtx)) {
				
				//Util.shortToast(mCtx, R.string.conf_record_no_net);
				Util.shortToast(mCtx, R.string.conf_no_net_function_toast);
				break;
			}
			
			mCtvOpearteModule = record;
			
			showWaittingDialog(CommunicationManager.RECORD);
			
			if(record.isChecked()) {
				
				ConfControl.getInstance().record(1);
			}else {
				
				ConfControl.getInstance().record(0);
			}	
		}while(false);		
	}
	
	public void onLockClicked(CheckedTextView lock) {
		
		do{
			
			if (!Util.isNetworkReadyForConf(mCtx)) {
				
				//Util.shortToast(mCtx, R.string.conf_lock_no_net);
				Util.shortToast(mCtx, R.string.conf_no_net_function_toast);
				break;
			}
			
			mCtvOpearteModule = lock;
			
			showWaittingDialog(CommunicationManager.LOCK);
			
			if(lock.isChecked()) {
				
				ConfControl.getInstance().lockConf(1);
			}else {
				
				ConfControl.getInstance().lockConf(0);
			}	
		}while(false);
		
	}
	
	/**
	 * Mute all party except moderator
	 * 
	 * @param muteAll
	 */
	public void onMuteAllClicked(CheckedTextView muteAll) {
		
		do {
			
			if (!Util.isNetworkReadyForConf(mCtx)) {
				
				//Util.shortToast(mCtx, R.string.conf_mute_all_no_net);
				Util.shortToast(mCtx, R.string.conf_no_net_function_toast);
				break;
			}
			
			mCtvOpearteModule = muteAll;
			
			showWaittingDialog(CommunicationManager.CONF_MUTE);
			
			if(muteAll.isChecked()) {
				
				ConfControl.getInstance().allMute(1,0);			
				muteAllParty();
			}else {
				
				ConfControl.getInstance().allMute(0,0);
				unMuteAllParty();
			}				
			
		}while(false);
	}
	
	public void onAddParticipant() {
		
		do {
			
			if (!Util.isNetworkReadyForConf(mCtx)) {
				
				//Util.shortToast(mCtx, R.string.conf_transfer_no_net);
				Util.shortToast(mCtx, R.string.conf_no_net_function_toast);
				break;
			}
			
			Util.startActivity(mCtx, AddParticipantActivity.class);
		}while(false);
	}
	
	/**
	 * If there is only one party in the conference, the party's location is not 
	 * for use self mute command, so when send self mute command to server, always
	 * failed, if their is only one party
	 * 
	 * @param selfMute
	 */
	public void onSelfMuteClicked(CheckedTextView selfMute) {

		do {

			if (!Util.isNetworkReadyForConf(mCtx)) {

				//Util.shortToast(mCtx, R.string.conf_mute_self_no_net);
				Util.shortToast(mCtx, R.string.conf_no_net_function_toast);
				break;
			}

			List<Participant> data = CommunicationManager.getInstance().getAllParties();
	
			if (null == data || data.size() < 2) {
				
				selfMute.toggle();
				Util.shortToast(mCtx, R.string.toast_mute_self_unable);
				break;
			}
	
			mCtvOpearteModule = selfMute;

			Participant currentUser = ContactManager.getInstance()
					.getCurrentUserObject();
			
			Participant party = CommunicationManager.getInstance()
					.getPartyById(currentUser.getIdInConference());
			
			if (null == party) {
				
				selfMute.setChecked(false);
				//Util.shortToast(mCtx, R.string.toast_self_mute_disenable);
				Util.shortToast(mCtx, R.string.conf_no_net_function_toast);
				break;
			}
			
			setMutedParty(party);

			if (selfMute.isChecked()) {

				ConfControl.getInstance().muteParty(party, 0, 1);
				party.setMuted(true);
			} else {
				
				party.setMuted(false);
				ConfControl.getInstance().muteParty(party, 1, 1);
			}

			mAdapter.notifyDataSetChanged();
		} while (false);
	}
	
	/**
	 * See transferPhone(String phoneNum) for more information about work flow
	 * 
	 * 
	 * @param phoneTransfer
	 */
	public void onPhoneTransferClicked(CheckedTextView phoneTransfer) {

		do {

			if (!Util.isNetworkReadyForConf(mCtx)) {

				//Util.shortToast(mCtx, R.string.conf_transfer_no_net);
				Util.shortToast(mCtx, R.string.conf_no_net_function_toast);
				break;
			}
			
//			Util.shortToast(mCtx, R.string.toast_fixed_line);
//	
//			mEtPhoneTransfer = new EditText(mCtx);
//			mEtPhoneTransfer.setInputType(InputType.TYPE_CLASS_PHONE);
//			
//			mDialogType = PHONE_TRANSFER_DIALOG;
			
//			AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
//
//			builder.setTitle(R.string.conf_transfer)
//					.setIcon(android.R.drawable.ic_dialog_info)
//					.setView(mEtPhoneTransfer)
//					.setPositiveButton(R.string.ok,this)
//					.setNegativeButton(R.string.cancel, this).show();
			
			final Dialog d=new Dialog(this);
			
			d.requestWindowFeature(Window.FEATURE_NO_TITLE);
			//d.requestWindowFeature(Window.)
			d.getWindow().setBackgroundDrawableResource(R.drawable.input_single);
			d.setContentView(R.layout.phone_transfer_dialog);
			final EditText et1=(EditText)d.findViewById(R.id.phone_transfer_et1);
			final EditText et2=(EditText)d.findViewById(R.id.phone_transfer_et2);
			Button   cancel= (Button) d.findViewById(R.id.phone_transfer_cancel);
			Button   ok= (Button) d.findViewById(R.id.phone_transfer_ok);
			
			cancel.setOnClickListener(new android.view.View.OnClickListener() {
				public void onClick(View v) {
					d.dismiss();
					
				}
			});
			
			ok.setOnClickListener(new android.view.View.OnClickListener() {
				public void onClick(View v) {

					String phoneNumber = et1.getText().toString().trim();
					if(phoneNumber.startsWith("+")){
						phoneNumber = phoneNumber.replace("+", "00");
					}
					String dvPhoneNumber=et2.getText().toString().trim();
					boolean isValid = ValidatorUtil.isNumberValid(phoneNumber);
					if (isValid) {
						if("".equals(dvPhoneNumber)){
							String phoneNum = et1.getText().toString();
							transferPhone(phoneNum);
							d.dismiss();
						}else{
							String phoneNum= phoneNumber+"w,,,,,"+dvPhoneNumber;
							transferPhone(phoneNum);
							d.dismiss();
						}
						
					}
					
				}
			});
		
			d.show();
			
		} while (false);
	}
	
	/**
	 * Work Flow for moderator transfer:
	 * 1.guest call the input party to conference
	 * 2.when receive the party had been connected(connect state must be 4 or 5), modify the 
	 * moderator's hostctrllevel to 0
	 * 3.when receive modify hostctrllevel success, start modify the input party's hostctrllevel to 2
	 * 4.when receive modify moderator's hostctrllevel to 2 success, begin to remove current moderator's 
	 * web operator
	 * use following command:
	 * 1>ACC.P.MOVE~0~0~1~partyID直接断开当前web op语音连接。
	 * 2> we can also use this command: 2. ACC.O.TA去掉现有的web op. 
	 * 5.When we receive the remove web operator signal, specify new web operator
	 * use following command:
	 * ACC.O.AA
	 * 6.When we receive new web operator assign success, disconnect original party
	 * 
	 * After this step, transfer phone success.
	 * 
	 * @param phoneNum the number to transfer 
	 */
	private void transferPhone(String phoneNum) {

		Participant inputParty = new Participant();
				///ContactManager.getInstance().getDestinateParty();

		inputParty.setName(ContactManager.getContactNameByPhoneNumber(phoneNum));
		inputParty.setPhone(phoneNum);
		
		ContactManager.getInstance().setDestinateParty(inputParty);
			
		if (CommunicationManager.getInstance().isModeratorAccount()) {
			
			//Util.BIZ_CONF_DEBUG(TAG, "set input party as moderator");
			
			ConfControl.getInstance().addPartyToConf(inputParty, true);
		}else {
			
			ConfControl.getInstance().addPartyToConf(inputParty, false);
		}
		
		Participant originalParty = ContactManager.getInstance().getOriginalParty();
		
		if (Util.isEmpty(originalParty.getPhone())) {
			
			Participant currentUser = ContactManager.getInstance().getCurrentUserObject();
			
			ContactManager.getInstance().setOriginalParty(currentUser);      
		}		 
		
		CommunicationManager.getInstance().setTransferingPhone(true);
		
		showWaittingDialog(CommunicationManager.PHONE_TRANSFER, MinaUtil.CONNECT_WAITING_TIME * 2);
	}
	
	@SuppressLint("NewApi")
	public void onOtherFunClicked(CheckedTextView otherFunc){
		
		mDialogType = OTHER_FUNC_DIALOG;
		
		new AlertDialog.Builder(this).setMessage(R.string.toast_enter_inCall_screen)
		.setTitle(R.string.toast_conf_alert_title)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setPositiveButton(R.string.ok, this)
		.setNegativeButton(android.R.string.cancel, this)
		.show();
	}
	
	public void setMutedParty (Participant party) {
			
		mMuteOPParty = party;
		showWaittingDialog(CommunicationManager.MUTE_PARTY);
	}
	
	public void onRadioClick(View v) {
		
		int id = v.getId();
		
		CheckedTextView ctv = null;
		
		if(v instanceof CheckedTextView) {
			
			ctv = (CheckedTextView) v;			
		}else {
			
			return;
		}		
		
		if (Util.isNetworkReadyForConf(mCtx) 
				|| (id == R.id.rb_open_hf) 
				|| (id == R.id.ctv_guest_hf) ) {
			
			ctv.toggle();
		}
		
		switch (id) {

		case R.id.rb_open_hf:
		case R.id.ctv_guest_hf:
			onHfClicked(ctv);		
			break;
		case R.id.rb_self_mute:
		case R.id.ctv_guest_self_mute:
			onSelfMuteClicked(ctv);
			break;
		case R.id.rb_mute_all:
			onMuteAllClicked(ctv);
			break;
			
		case R.id.rb_call_name:
			onRollCallClicked(ctv);
			break;
			
		case R.id.rb_record:
			onRecordClicked(ctv);
			break;
			
		case R.id.rb_add_participant:
			onAddParticipant();
			break;
			
		case R.id.rb_lock:
			onLockClicked(ctv);
			break;
			
		case R.id.rb_phone_transfer:
		case R.id.ctv_guest_transfer:
			onPhoneTransferClicked(ctv);
			break;
			
		case R.id.rb_other_function:
			
			onOtherFunClicked(ctv);
			break;
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		
		switch (which) {
		
		case DialogInterface.BUTTON_POSITIVE:
			
			if (mDialogType == LEAVE_CONF_DIALOG) {
				
				leaveConf();	
			}
			
			if (mDialogType == PHONE_TRANSFER_DIALOG) {
				
				do {
					
					if (null == mEtPhoneTransfer) {
						
						break;
					}
					
					String phoneNumber = mEtPhoneTransfer.getText().toString();

					boolean isValid = ValidatorUtil.isNumberValid(phoneNumber);
					
					if (!isValid) {

						//Util.shortToast(mCtx,R.string.toast_phone_num_invalid);
						break;
					}

					String phoneNum = mEtPhoneTransfer.getText().toString();

					transferPhone(phoneNum);
				} while (false);
				
				mEtPhoneTransfer = null;
			}
			break;
			
		case DialogInterface.BUTTON_NEUTRAL:
			
			if (mDialogType == LEAVE_CONF_DIALOG) {
				
				finishConf();
			}		
			break;
			
		case DialogInterface.BUTTON_NEGATIVE:
			
			if (mDialogType == PHONE_TRANSFER_DIALOG) {
				
				mEtPhoneTransfer = null;
			}
			break;		
		}
	}
	
	/**
	 * This function is similar to phone transfer
	 * 
	 * we not need guest out call party, just start from step 2
	 * 
	 * @param party transfer party
	 */
	private void transferModerator(Participant party) {
		
		isModeratorLeaveConf = true;
		
		ContactManager.getInstance().setDestinateParty(party);
				
		Participant originalParty = ContactManager.getInstance().getOriginalParty();
		
		if (Util.isEmpty(originalParty.getPhone())) {
			
			Participant currentUser = ContactManager.getInstance().getCurrentUserObject();
			
			ContactManager.getInstance().setOriginalParty(currentUser);      
		}		 
		
		ConfControl.getInstance().alterPartyAttr(originalParty, MinaUtil.MSG_P_HOST_CONTROL_LEVEL, "0");
		
		//CommunicationManager.getInstance().setTransferingModerator(true);
		CommunicationManager.getInstance().setTransferingPhone(true);
		
		showWaittingDialog(CommunicationManager.PHONE_TRANSFER, MinaUtil.CONNECT_WAITING_TIME * 2);
	}
	
	/**
	 * leave conference code here
	 */
	private void leaveConf() {
		
		do {			
			
			final ArrayList<Participant> parties = 
					new ArrayList<Participant>(
							CommunicationManager.getInstance().getActiveParties().values());
			
			final int size = parties.size();
					
			if (!Util.isNetworkReadyForConf(mCtx)
					|| !CommunicationManager.getInstance().isModeratorAccount() 
					|| size < 2) {
				
				
				finishConf();
				break;
			}
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			builder.setTitle(this.getResources().getString(R.string.toast_transfer_moderator));			
			
			String[] arrayParties = new String[size];
			final Participant[] showParties = new Participant[size];
			
			String phone = ContactManager.getInstance().getCurrentUserObject().getPhone();
			
			//Util.BIZ_CONF_DEBUG(TAG, "transfer moderator current user's phone:" + phone);
			
			int i = 0;
			
			for (Participant party : parties) {
				
				if (!party.getPhone().contains(phone)) {
					
					showParties[i] = party;
					arrayParties[i] = party.getName();
					i++;
				}				
			}
			 
			if (i < size) {
				
				arrayParties[i] = getResources().getString(R.string.cancel);
			}
			
			builder.setItems(arrayParties,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

							if (which != size - 1) {

								transferModerator(showParties[which]);
							}
						}
					});

			builder.create().show();
		}while(false);
	}
	
	/**
	 * finish conference code here
	 */
	private void finishConf() {
		
		CommunicationManager.getInstance().setInConfManageScreen(false);
		
		if (!CommunicationManager.getInstance().isTurn2HomePage()) {
					
			Intent intent=new Intent(this,MainActivity.class);
			
			this.startActivity(intent);
			
			Util.BIZ_CONF_DEBUG(TAG, "send intent to launch Main activity");
		}
		
		finish();
		
		//finishConfState();		
	}

	private void finishConfState() {
		
		if (null != leaveConfDialog && leaveConfDialog.isShowing()) {
			
			leaveConfDialog.dismiss();
		}
		
		if (null != mLoadingDialog) {
		
			mLoadingDialog.finishDialogSuccessDone();
		}
			
		unregisterReceiver(mConfChangeReceiver);
		
		if (null != mHistory) {
			
			mHistory.setEndDate(new Date());
			ConfHistoryManager.getInstance().updateDbRecord(mHistory);
		}	
		
		Util.BIZ_CONF_DEBUG(TAG, "reset data now");
		ConfControl.getInstance().disconnectToServer();
	}
	
	/**
	 * whether user click any function radios
	 * @return
	 */
	private boolean isUserClickRadio() {
		
		boolean isClicked = false;
		
		if (mOperateModule > CommunicationManager.CONF_ENDED) {
			
			isClicked = true;
		}
		
		return isClicked;
	}
	
	private boolean isFinishDialog(int changeType) {
		
		boolean isFinish = false;
		
		if (changeType == mOperateModule) {
			
			isFinish = true;
		}
		
		//add for party list mute party
		if (changeType == CommunicationManager.MUTE_PARTY 
				&& mOperateModule == CommunicationManager.PARTY_LIST_MODULE ) {
			
			isFinish = true;
		}
		
		return isFinish;
	}
	
	/**
	 * 
	 * Add party receiver
	 * 
	 * @author wenjuan.li
	 *
	 */
	public class ConfChangeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
				
			int changeType = intent.getIntExtra(CommunicationManager.KEY_CONF_STATE_CHANGE, -1);
			
			//Util.BIZ_CONF_DEBUG(TAG, "receive party changed: " + changeType);
			
			if (isUserClickRadio() && changeType == CommunicationManager.OPERATE_FAILED) {
				
				//Util.BIZ_CONF_DEBUG(TAG, "receive operate_failed msg");
				mLoadingDialog.finishDialogWithErrorMsg();
				return;
			}
			
			if (isFinishDialog(changeType)) {
				
				//Util.BIZ_CONF_DEBUG(TAG, "finish dialog");				
				mLoadingDialog.finishDialogSuccessDone();
			}
			
			switch (changeType) {

			case CommunicationManager.PARTY_CHANGED: 
				
				List<Participant> data = new ArrayList<Participant>();
				
				//Do not user mapParty.values to get the participant, because when we delete a 
				//party by its key, the map only delete the key, the value already in it, this will
				//cause display error.
				HashMap<String, Participant> mapParty = CommunicationManager.getInstance().getActiveParties();
				
				Set<String> keys = mapParty.keySet();
				
				Participant currentUser = ContactManager.getInstance().getCurrentUserObject();
				
				String userPartyId = currentUser.getIdInConference();
				
				for (String key : keys) {
					
					Participant party = mapParty.get(key);
								
					if (key.equals(userPartyId) && null != mCtvSelfMute) {
						
						mCtvSelfMute.setChecked(party.isMuted());
						currentUser.setMuted(party.isMuted());
					}
					
					data.add(party);
				}
				
				mAdapter.setData(data);	
				break;

			case CommunicationManager.CONF_ENDED:
				
				finishConf();
				break;
			
			case CommunicationManager.NETWORK_READY:
				
				reConnectToNet();
				break;
				
			default:
				break;
			}
		}	
	}
	
	public static boolean isReConnecting = false;

	public void reConnectToNet() {
		
		do {
			
			if (isReConnecting) {
				
				break;
			}
			
			if (ConfControl.getInstance().isServerLinkReady()) {
				
				break;
			}
			
			if (ConfControl.getInstance().isServerConnected()) {
				
				ConfControl.getInstance().closeLinkSession();
			}
			
			isReConnecting = true;
			ConfControl.getInstance().startConf(this, this);
			
		}while(false);	
	}
	
	public void setOperateModule(int operateModule) {
		
		mOperateModule = operateModule;
	}
	
	@Override
	public void onSuccessDone() {
		
		if (isReConnecting) {
			
			isReConnecting = false;
			
			return;
		}
		
		switch (mOperateModule) {
		
		case CommunicationManager.PHONE_TRANSFER:
			
			do {
				if (isModeratorLeaveConf) {
					
					CommunicationManager.getInstance().setModeratorLeaveConference(true);
					finishConf();
					break;
				}
				
				if (CommunicationManager.getInstance().isShowManualHangUpMsg()) {
					
					CommunicationManager.getInstance().setShowManualHangUpMsg(false);
					Util.longToast(AppClass.getInstance(), R.string.toast_manual_hang_up_original);
					break;
				}	
			}while(false);
					
			break;
			
		case CommunicationManager.PARTY_LIST_MODULE:
			
			if (!Util.isEmpty(mMuteOPParty) && mMuteOPParty.isModerator()) {
				
				mCtvSelfMute.setChecked(mMuteOPParty.isMuted());					
			}		
			break;
		}
		
	}

	@Override
	public void onDoneWithError() {
		
		if (isReConnecting) {
			
			isReConnecting = false;
			
			return;
		}
		
		switch (mOperateModule) {
		
		case CommunicationManager.MUTE_PARTY:
			
			if (!Util.isEmpty(mMuteOPParty)) {
				
				mMuteOPParty.setMuted(!mMuteOPParty.isMuted());
				mAdapter.notifyDataSetChanged();
			}		
			mCtvOpearteModule.toggle();
			break;		
			
		case CommunicationManager.CONF_MUTE:
			rollbackAllMuteState();
			mCtvOpearteModule.toggle();
			break;
			
		case CommunicationManager.LOCK:		
		case CommunicationManager.RECORD:
			mCtvOpearteModule.toggle();
			break;
			
		case CommunicationManager.PHONE_TRANSFER:
			
			Participant destinateParty = 
				ContactManager.getInstance().getDestinateParty();
			
			//retry to set the web operator of current meeting
			if (null != mConfAccount && !Util.isEmpty(mConfAccount.getModeratorPw())) {
				
				ConfControl.getInstance().transferWebOp(destinateParty);
			}
						
			if (null != destinateParty) {
				
				destinateParty.setIdInConference("null");
			}
			
			CommunicationManager.getInstance().setTransferingPhone(false);
			
			if (CommunicationManager.getInstance().isShowManualHangUpMsg()) {
				
				CommunicationManager.getInstance().setShowManualHangUpMsg(false);
			}
			break;
			
		case CommunicationManager.PARTY_LIST_MODULE:
			
			if (!Util.isEmpty(mMuteOPParty)) {
				
				mMuteOPParty.setMuted(!mMuteOPParty.isMuted());
				
				if (mMuteOPParty.isModerator()) {
					
					mCtvSelfMute.setChecked(mMuteOPParty.isMuted());
				}
				
				mAdapter.notifyDataSetChanged();
			}		
			break;
		}
		
		Util.shortToast(mCtx, R.string.toast_operate_time_out);
	}
	
	private void unMuteAllParty() {

		newMuteParties.clear();

		List<Participant> parties = CommunicationManager.getInstance()
				.getAllParties();

		for (Participant party : parties) {
			
			if (!party.isModerator()) {
				
				party.setMuted(false);
				newMuteParties.add(party);
			}
		}

		mAdapter.notifyDataSetChanged();
	}
	
	private void muteAllParty() {
		
		newMuteParties.clear();
		
		List<Participant> parties = CommunicationManager.getInstance().getAllParties();
		
		for (Participant party : parties) {
			
			if (!party.isModerator() && !party.isMuted()) {
				
				party.setMuted(true);
				newMuteParties.add(party);
			}
		}
		
		mAdapter.notifyDataSetChanged();
	}
	
	private void rollbackAllMuteState() {
		
		for (Participant party : newMuteParties) {

				party.setMuted(!party.isMuted());
		}
		
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onImgHomeClicked(View v) {
		
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		
		backPressedDialog=new Dialog(this);
		backPressedDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//d.requestWindowFeature(Window.)
		
		backPressedDialog.getWindow().setBackgroundDrawableResource(R.drawable.input_single);;
		backPressedDialog.setContentView(R.layout.leave_conf_dialog);
		back = (Button) backPressedDialog.findViewById(R.id.conf_bt_back);
		leave = (Button) backPressedDialog.findViewById(R.id.conf_bt_leave);
		finish = (Button) backPressedDialog.findViewById(R.id.conf_bt_finish);
		cancel = (Button) backPressedDialog.findViewById(R.id.conf_bt_cancel);
		
		
		if (Util.isEmpty(mConfAccount.getModeratorPw())) {
			
			finish.setVisibility(View.GONE);
		}
		
		dialogInit();
		
		backPressedDialog.show();
//	mDialogType = LEAVE_CONF_DIALOG;
		
//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setTitle(getResources().getString(R.string.app_name))
//				.setPositiveButton(R.string.toast_leave_conf, this)
//				.setNegativeButton(android.R.string.cancel, this);
//		
//		if(CommunicationManager.getInstance().isModeratorAccount()) {
//			
//			builder.setNeutralButton(R.string.toast_finish_conf, this);
//		}
//		
//		leaveConfDialog = builder.show();
	}
	
	private void dialogInit() {
		back.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				//startNewActivity();
				backPressedDialog.dismiss();
				turn2HomePage();
				return true;
			}
		});
		
		leave.setOnClickListener(new android.view.View.OnClickListener() {
			public void onClick(View v) {
				backPressedDialog.dismiss();
				leaveConf();	
			}
		});
		finish.setOnClickListener(new android.view.View.OnClickListener() {
			public void onClick(View v) {
				backPressedDialog.dismiss();
				finishConf();
			}
		});
		cancel.setOnClickListener(new android.view.View.OnClickListener() {
			public void onClick(View v) {
				backPressedDialog.dismiss();
			}
		});
		
	}

	private void turn2HomePage(){
		
		CommunicationManager.getInstance().setInConfManageScreen(false);
		CommunicationManager.getInstance().setTurn2HomePage(true);
		Intent intent=new Intent(this,MainActivity.class);
		startActivity(intent);
	}

	@Override
	public void onRightButtonClicked(View v) {
		
		do {
			
			if (!Util.isNetworkReadyForConf(mCtx)){
				
				//Util.shortToast(mCtx, R.string.conf_service_no_net);
				Util.shortToast(mCtx, R.string.conf_no_net_function_toast);
				break;
			}
			
			String currentPartyId = ContactManager.getInstance()
					.getCurrentUserObject().getIdInConference();
			
			if (Util.isEmpty(currentPartyId)) {
				
				//Util.longToast(mCtx, R.string.toast_party_service_disenable);
				Util.shortToast(mCtx, R.string.conf_no_net_function_toast);
				break;
			}
			
			ConfControl.getInstance().requestPartyService(currentPartyId, 1);
		} while (false);
		
	}	
}
