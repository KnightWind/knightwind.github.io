package com.sktlab.bizconfmobile.model.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import android.content.Intent;
import com.sktlab.bizconfmobile.activity.AppClass;
import com.sktlab.bizconfmobile.activity.ConferenceActivity;
import com.sktlab.bizconfmobile.mina.MinaUtil;
import com.sktlab.bizconfmobile.model.ConfControl;
import com.sktlab.bizconfmobile.model.OngoingConf;
import com.sktlab.bizconfmobile.model.ConfAccount;
import com.sktlab.bizconfmobile.model.Participant;
import com.sktlab.bizconfmobile.util.Util;

public class CommunicationManager {

	public static final String TAG = "CommunicationManager";
	
	public static final String KEY_CONF_STATE_CHANGE = "conf_state_change_key";
	
	public static final int PARTY_CHANGED = 0;
	public static final int CONF_ENDED = 1;
	public static final int ROLL_CALL = 2;
	public static final int RECORD = 3;
	public static final int LOCK = 4;
	public static final int CONF_MUTE = 5;
	public static final int MUTE_PARTY = 6;
	public static final int PHONE_TRANSFER = 7;
	//if the network connected
	public static final int NETWORK_READY = 8;
	//party list module
	public static final int PARTY_LIST_MODULE = 9;
	//server send operate error message
	public static final int OPERATE_FAILED = 10;
	
	private ConfAccount mActiveAccount;
	//private Conference mActiveConference;

	// used to store the active conference in current bridge
	private HashMap<String, OngoingConf> mActiveConfs;
	
	// used to store the active conference in current bridge
	private HashMap<String, OngoingConf> mLiveConfs;
		
	//is now transfering phone
	private boolean isTransferingPhone = false;
	//if user start conference user phone call, then enter app and he is a guest user
	//when he transfer phone, show msg to him
	private boolean isShowManualHangUpMsg = false;
	//whether the moderator click to leave conference
	private boolean isModeratorLeaveConference = false;
	//whether the user had been in conference management screen
	private boolean isInConfManageScreen = false;
	//whether user click link to start conference
	private boolean isLinkStartConf = false;
	
	private boolean isTurn2HomePage = false;
	
	//client send msg id
	private String clientSendMsgId = "null";
	/**
	 * 
	 * This store the participants of the conference which is going now,
	 * this is mostly used when user join a conference, he is not the moderator,
	 * so he didn't know how many people join this conference, we can use this data structure
	 * to get those people.
	 */
	private HashMap<String, Participant> mActiveParties;
	
	/**
	 * This data structure store the party id which had been outcalled by the moderator,
	 * this mostly used in start a conference, so the moderator can add participant to a 
	 * conference, when add a participant, if we out call it default.
	 */
	private ArrayList<Participant> outCallPartys;
	
	private static class InstanceHolder {

		private final static CommunicationManager instance = new CommunicationManager();
	}

	private CommunicationManager() {
		
		mActiveConfs = new HashMap<String, OngoingConf>();
		outCallPartys = new ArrayList<Participant>();
		mActiveParties = new HashMap<String, Participant>();
		mLiveConfs = new HashMap<String, OngoingConf>();
	}
	
	public static CommunicationManager getInstance() {

		return InstanceHolder.instance;
	}
	
	public boolean isOperateFailed(String msgId) {
		
		boolean isFailed = false;
		
		String failMsgId = "-" + clientSendMsgId;
		
		if (failMsgId.equalsIgnoreCase(msgId)) {
			
			isFailed = true;
		}
		
		return isFailed;
	}
	
	public boolean isPartyInConfByPhone(String phoneNum) {
		
		synchronized(mActiveParties) {

			boolean isInConf = false;
			
			do {
				
				if (Util.isEmpty(phoneNum)) {
					
					break;
				}
				
				Set<String> keys = mActiveParties.keySet();
				
				for (String key: keys) {
					
					Participant party = mActiveParties.get(key);
					
					if (party.getPhone().contains(phoneNum) || phoneNum.contains(party.getPhone())
							|| party.getName().contains(phoneNum)
							|| phoneNum.contains(party.getName())) {
						
						isInConf = true;
						break;
					}
				}
			}while (false);
				
			return isInConf;
		}
	}
	
	public boolean isPartyInConfById(String partyId) {
		
		synchronized(mActiveParties) {
			return mActiveParties.containsKey(partyId);
		}
	}
	
	public boolean isModeratorAccount() {
		
		return !Util.isEmpty(mActiveAccount.getModeratorPw());
	}
	
	public void disconnectOrignalParty() {
		
		Participant originalParty = ContactManager.getInstance().getOriginalParty();			
		CommunicationManager.getInstance().removeParty(originalParty.getIdInConference());
		
		if (!Util.isEmpty(originalParty.getIdInConference())) {
			
			ConfControl.getInstance().disconnectParty(originalParty);
		}		
		
		Participant destinateParty = ContactManager.getInstance().getDestinateParty();
		
		ContactManager.getInstance().setOriginalParty(destinateParty);	
		ContactManager.getInstance().setCurrentUser(destinateParty);
		
		if (isModeratorAccount()) {
			
			originalParty.setIsModerator(true);
			destinateParty.setIsModerator(false);
		}				
	}
	
	public ArrayList<Participant> getAllParties() {
		
		synchronized(mActiveParties) {
			ArrayList<Participant> parties = new ArrayList<Participant>();
			
			Set<String> keys = mActiveParties.keySet();
			
			for (String key : keys) {
				
				parties.add(mActiveParties.get(key));
			}
			
			return parties;
		}
	}
	
	public HashMap<String, Participant> getActiveParties() {
		
		synchronized(mActiveParties) {
		
			HashMap<String, Participant> parties = new HashMap<String, Participant>(mActiveParties);
			
			return parties;
		}		
	}
	
	public Participant getPartyById(String partyId) {
		
		Participant specifidParty = null;
		
		synchronized(mActiveParties) {
			
			if (mActiveParties.containsKey(partyId)) {
			
				specifidParty = mActiveParties.get(partyId);
			}
		
			return specifidParty;
		}
	}
	
	public void putParty(Participant party) {

		synchronized(mActiveParties) {
			do {
	
				if (Util.isEmpty(party) || Util.isEmpty(party.getIdInConference())) {
	
					break;
				}
	
				if (mActiveParties.containsKey(party.getIdInConference())) {
	
					break;
				}
	
				Util.BIZ_CONF_DEBUG(TAG,"add party id:" + party.getIdInConference());
	
				mActiveParties.put(party.getIdInConference(), party);
	
				notifyConfChanged(PARTY_CHANGED);
			} while (false);
		}
	}
	
	public void removeParty(String key) {
		
		synchronized(mActiveParties) {
			do {
				
				Participant party = mActiveParties.remove(key);
						
				//Util.BIZ_CONF_DEBUG(TAG, "remove key: " + key);
				
				if (null != party) {
					
					//Util.BIZ_CONF_DEBUG(TAG, "remove party num: " + party.getPhone());
					
					ContactManager.getInstance().removeSelectedContact(party.getPhone());
				}
				
				//Util.BIZ_CONF_DEBUG(TAG, "party size: " + mActiveParties.size());			
				notifyConfChanged(PARTY_CHANGED);
			}while(false);	
		}
	}
	
	public void talkerStateChanged(String partyId, String state) {
		
		synchronized(mActiveParties) {
			
			do {
				
				//Util.BIZ_CONF_DEBUG(TAG, "talker state changed, partyId: " + partyId + "state: " + state);
				
				if (!mActiveParties.containsKey(partyId)) {
					
					break;
				}
				
				Participant party = mActiveParties.get(partyId);
				
				if (state.endsWith("1")) {
					
					party.setTalking(true);
				}else {
					
					party.setTalking(false);
				}
						
				notifyConfChanged(PARTY_CHANGED);
			}while(false);	
		}
	}
	
	public ConfAccount getActiveAccount() {
		return mActiveAccount;
	}

	public void setActiveAccount(ConfAccount mActiveAccount) {
		this.mActiveAccount = mActiveAccount;
	}

	public OngoingConf getActiveConfByKey(String key) {

		if (mActiveConfs.containsKey(key)) {

			return mActiveConfs.get(key);
		}

		return null;
	}

	public void putActiveConference(OngoingConf conf) {

		if (!Util.isEmpty(conf)) {

			String key = conf.getAttr().getConfName();

			//Util.BIZ_CONF_DEBUG(TAG, "active conf key: " + key);

			if (!Util.isEmpty(key) && !mActiveConfs.containsKey(key)) {

				mActiveConfs.put(key, conf);
			} else {

				//Util.BIZ_CONF_DEBUG(TAG,"active conference's conf Id is null or had been in map");
			}

		} else {

			//Util.BIZ_CONF_DEBUG(TAG, "active conference is null");
		}
	}

	public HashMap<String, OngoingConf> getActiveConfs() {
		return mActiveConfs;
	}

	public void setActiveConfs(HashMap<String, OngoingConf> mActiveConfs) {

		this.mActiveConfs = mActiveConfs;
	}

	public void clearActiveConfs() {

		mActiveConfs.clear();
	}
	
	public void putLiveConf(OngoingConf conf) {
		
		if (!Util.isEmpty(conf)) {

			String key = conf.getAttr().getConfId();

			Util.BIZ_CONF_DEBUG(TAG, "live conf key: " + key);

			if (!Util.isEmpty(key) && !mLiveConfs.containsKey(key)) {

				mLiveConfs.put(key, conf);
			} 
		}
	}
	
	public OngoingConf getLiveConfByKey(String key) {
		
		if (mLiveConfs.containsKey(key)) {

			return mLiveConfs.get(key);
		}

		return null;
	}
	
	public void notifyOperateFailed() {
		
		if (isInConfManageScreen) {
			
			notifyConfChanged(OPERATE_FAILED);
		}
		
	}
	
	public void notifyNetWorkReady() {
		
		notifyConfChanged(NETWORK_READY);
	}
	
	public void notifyConfEnded() {
	
		notifyConfChanged(CONF_ENDED);
	}
	
	public void notifyPartyChanged() {
		
		notifyConfChanged(PARTY_CHANGED);
	}
	
	public void notifyConfChanged(int type) {
		
		Intent intent = new Intent(ConferenceActivity.NEED_UPDATE_CONF_STATE_FILTER);
		
		intent.putExtra(KEY_CONF_STATE_CHANGE, type);
		
		AppClass.getInstance().sendBroadcast(intent);
		
		//Util.BIZ_CONF_DEBUG(TAG, "send conf change, type: " + type);
	}
	
	public void notifyPhoneTransfered() {		
		
		setTransferingPhone(false);
		notifyConfChanged(PHONE_TRANSFER);	
		
		//When transfer phone success, set destined party id to null
		Participant destinateParty = 
				ContactManager.getInstance().getDestinateParty();
		destinateParty.setIdInConference("null");
		//Util.BIZ_CONF_DEBUG(TAG, "phone transfer msg pass to receiver");
	}
	
	public void doTransfer(String phoneNumber) {
		
		ContactManager cm = ContactManager.getInstance();
		ConfControl cControl = ConfControl.getInstance();
		
		Participant originalParty = cm.getOriginalParty();
		Participant destinateParty = cm.getDestinateParty();
		String destinatePhone = destinateParty.getPhone();
		
		do {
			
			if (Util.isEmpty(destinatePhone) || Util.isEmpty(phoneNumber) 
					|| !phoneNumber.contains(destinatePhone)) {
				
				break;
			}
			
			if(!isModeratorAccount()) {
				
				if (Util.isEmpty(originalParty.getIdInConference())) {
					
					//Util.BIZ_CONF_DEBUG(TAG, "show manual hang up msg");
					
					setShowManualHangUpMsg(true);
					
				}else {
					
					cControl.disconnectParty(originalParty);
				}			
				
				ContactManager.getInstance().setOriginalParty(destinateParty);
				ContactManager.getInstance().setCurrentUser(destinateParty);
				
				notifyPhoneTransfered();
			}else {
				
				//ConfControl.getInstance().disconnectWebOp(originalParty.getIdInConference());
				cControl.alterPartyAttr(originalParty, MinaUtil.MSG_P_HOST_CONTROL_LEVEL, "0");
			}
			
		}while(false);
	}
	
	public void reset() {
		
		Util.BIZ_CONF_DEBUG(TAG, "reset value now");
		
		outCallPartys.clear();
		mActiveConfs.clear();
		mActiveParties.clear();
		mLiveConfs.clear();
		
		isShowManualHangUpMsg = false;
		isTransferingPhone = false;
		isModeratorLeaveConference = false;
		isInConfManageScreen = false;
		isTurn2HomePage = false;
		isLinkStartConf = false;
	}
	
	public boolean isTransferingPhone() {
		return isTransferingPhone;
	}

	public void setTransferingPhone(boolean isTransferingPhone) {
		this.isTransferingPhone = isTransferingPhone;
	}
	
	public boolean isShowManualHangUpMsg() {
		return isShowManualHangUpMsg;
	}

	public void setShowManualHangUpMsg(boolean isShowManualHangUpMsg) {
		this.isShowManualHangUpMsg = isShowManualHangUpMsg;
	}

	public boolean isModeratorLeaveConference() {
		return isModeratorLeaveConference;
	}

	public void setModeratorLeaveConference(boolean isModeratorLeaveConference) {
		this.isModeratorLeaveConference = isModeratorLeaveConference;
	}

	public boolean isInConfManageScreen() {
		return isInConfManageScreen;
	}

	public void setInConfManageScreen(boolean isInConfManageScreen) {
		this.isInConfManageScreen = isInConfManageScreen;
	}

	public String getClientSendMsgId() {
		return clientSendMsgId;
	}

	public void setClientSendMsgId(String clientSendMsgId) {
		this.clientSendMsgId = clientSendMsgId;
	}

	public boolean isLinkStartConf() {
		return isLinkStartConf;
	}

	public void setLinkStartConf(boolean isLinkStartConf) {
		this.isLinkStartConf = isLinkStartConf;
	}

	public boolean isTurn2HomePage() {
		return isTurn2HomePage;
	}

	public void setTurn2HomePage(boolean isTurn2HomePage) {
		this.isTurn2HomePage = isTurn2HomePage;
	}
}
