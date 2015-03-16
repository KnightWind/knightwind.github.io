package com.sktlab.bizconfmobile.model;

import org.apache.mina.core.session.IoSession;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.activity.AppClass;
import com.sktlab.bizconfmobile.interfaces.IConfControl;
import com.sktlab.bizconfmobile.interfaces.ILoadingDialogCallback;
import com.sktlab.bizconfmobile.mina.MinaUtil;
import com.sktlab.bizconfmobile.model.manager.CommunicationManager;
import com.sktlab.bizconfmobile.model.manager.OngoingConfManager;
import com.sktlab.bizconfmobile.model.manager.ContactManager;
import com.sktlab.bizconfmobile.util.Util;

public class ConfControl implements IConfControl {

	public static final String TAG = "ConfControl";

	private OngoingConfManager mConfManager;
	
	public static final Object obj = new Object();

	private static class InstanceHolder {

		private final static ConfControl instance = new ConfControl();
	}

	private ConfControl() {

		mConfManager = new OngoingConfManager();
	}

	public static ConfControl getInstance() {

		return InstanceHolder.instance;
	}

	@Override
	public void startConf(Activity activity, ILoadingDialogCallback callback) {
		
		do{
		
			ConfAccount account = CommunicationManager.getInstance().getActiveAccount();
			
			if (!account.isUseDefaultAccessNum() && !Util.isNetworkReadyForConf(activity)) {									
				
				if (account.isDialOutEnable() || 
						(CommunicationManager.getInstance().isModeratorAccount() &&
							account.isSecurityCodeEnable())) {
					
					Util.shortToast(AppClass.getInstance(), R.string.wifi_toast);
					break;
				}
				
				callback.onSuccessDone();
				break;
			}
			
			mConfManager.closeSession();		
			mConfManager.startConf(activity, callback);
		}while(false);		
	}

	@Override
	public void hfControl() {
		
	}

	@Override
	public void selfMute() {

	}

	@Override
	public void allMute(int muteState, int muteParticipant) {
		
		mConfManager.allMute(muteState, muteParticipant);
	}

	@Override
	public void rollCall() {
		
		mConfManager.rollCall();
	}

	/**
	 * 
	 * @param session
	 * @param state 0 stop 1 start
	 */
	@Override
	public void record(int state) {
	
		mConfManager.record(state);
	}

	@Override
	public void lockConf(int state) {
		
		mConfManager.lockConf(state);
	}

	@Override
	public void otherFunc() {
		// TODO Auto-generated method stub

	}
	
	/**
	 * add party to current active conference which start by this app
	 * @param party
	 * @param isModerator
	 */
	public void addPartyToConf(Participant party, boolean isModerator){
		
		mConfManager.addPartyToConf(party, isModerator);
	}
	
	//add a party to the specified conference
	public void addPartyToConf(Participant party, boolean isModerator, String confId){
		
		mConfManager.addPartyToConf(party, isModerator, confId);
	}
	
	public void setServerSession(IoSession session) {
		
		mConfManager.getServerLSSession().setSession(session);
	}
	
	public IoSession getServerSession() {
		
		return mConfManager.getServerLSSession().getSession();
	}
	
	public void disconnectToServer() {
			
		//must call before reset all variables
		mConfManager.closeSession();
		
		CommunicationManager.getInstance().reset();			
		//we just reset the sequence number and session id
		//ConferenceManager.getInstance().reset();
		//when conference finished, clear the selected participants stored in ContactsManager;
		ContactManager.getInstance().reset();
		
		//notifyObj();
	}
	
	public void closeLinkSession() {
		
		mConfManager.closeSession();
	}
	
//	public ConferenceManager getConfManager() {
//		
//		return mConfManager;
//	}
	
	public OngoingConf getActiveConference() {
		
		return mConfManager.getActiveConference();
	}

	public void setActiveConference(OngoingConf mActiveConference) {
		
		mConfManager.setActiveConference(mActiveConference);
	}

//	public static void notifyObj() {
//
//		synchronized (obj) {
//
//			obj.notifyAll();
//		}
//	}
//
//	public static void waitObj() {
//
//		synchronized (obj) {
//
//			try {
//				obj.wait(MinaUtil.CONNECT_WAITING_TIME);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//	}

	/**
	 * state 0 muted
	 * 		 1 unmuted
	 * 
	 * playMessage 0 do not play message to muted/unMuted party
	 * 			   1 play message to it
	 */
	@Override
	public void muteParty(Participant party,  int state, int playMessage) {
	
		mConfManager.muteParty(party, state, playMessage);
	}

	@Override
	public void disconnectParty(Participant party) {
	
		mConfManager.disconnectParty(party);
	}
	
	public void alterPartyAttr(Participant party, String fieldName, String value) {
		
		mConfManager.alterPartyAttr(party.getIdInConference(), fieldName, value);
	}
	
	public void requestPartyAttr(Participant party) {
		
		mConfManager.requestPartyAttr(party.getIdInConference());
	}
	
	public String getACCId() {
		
		return mConfManager.getAccId();
	}
	
	public String getBVId() {
		
		return mConfManager.getBvId();
	}
	
	public String getACVId() {
		
		return mConfManager.getAcvId();
	}
	
	public void setACCId(String accId) {
		
		mConfManager.setAccId(accId);
	}
	
	public void setBVId(String bvId) {
		
		mConfManager.setBvId(bvId);
	}
	public void setACVId(String acvId) {
	
		mConfManager.setAcvId(acvId);
	}
	
	public void createACCSession() {
		
		mConfManager.createAccSession();
	}
	
	public void createBVSession() {
		
		mConfManager.createBvSession();
	}
	
	public void createACVSession() {
		
		mConfManager.createAcvSession();
	}
	
	public boolean isACCCreated() {
		
		return mConfManager.isAccCreated();
	}
	
	public boolean isBVCreated() {
		
		return mConfManager.isBvCreated();
	}
	
	public boolean isACVCreated() {
		
		return mConfManager.isAcvCreated();
	}
	
	public void requestActiveConfList() {
		
		mConfManager.requestActiveConfList();
	}
	
	public void requestLiveConfList() {
		
		mConfManager.requestLiveConfList();
	}
	
	public void activeConf() {
		
		mConfManager.activeConf();
	}
	
	public void activeConf(String password) {
		
		mConfManager.activeConf(password);
	}
	
	public void setConfActived(boolean isActived) {
		
		mConfManager.setConfActived(isActived);
	}
	
	public boolean isConfActived() {
		
		return mConfManager.isConfActived();
	}
	
	public void assocWithConf() {
		
		mConfManager.assocWithConf();
	}
	
	public void setConfAssoced(boolean isAssoced) {
		
		mConfManager.setConfAssoced(isAssoced);
	}
	
	public boolean isConfAssoced() {
		
		return mConfManager.isConfAssoced();
	}
	
	public boolean isRequestPList() {
		
		return mConfManager.isRequestPList();
	}
	
	public void requestPList() {
		
		mConfManager.requestPList();
	}
	
	public void setServerLinkReady(boolean isReady) {
		
		mConfManager.setLinkReady(isReady);		
	}
	
	public void transferWebOp(Participant party) {
		
		mConfManager.transferWebOp(party);
	}
	
	public void moderatorOutCall(Participant party) {
		
		mConfManager.moderatorOutCall(party);
	}
	
	public void guestOutCallParty(Participant party) {
		
		mConfManager.guestOutCallParty(party);
	}
	
	public void disconnectParty(String partyId) {
		
		mConfManager.disconnectParty(partyId);
	}
	
	public void setServerConnected(boolean isConnected) {
		
		mConfManager.setServerConnected(isConnected);
	}
	
	public boolean isServerConnected() {
		
		return mConfManager.isServerConnected();
	}
	
	public boolean isServerLinkReady() {
		
		return mConfManager.isLinkReady();
	}
	
	public void alterTalkersEnable(int state) {
		
		mConfManager.alterTalkerState(state);
	}
	
	public void setSecurityCode(String value) {
		
		mConfManager.setSecurityCode(value);
	}
	
	public void alterConfFieldValue(String fieldName, String value) {
		
		mConfManager.alterConfFieldValue(fieldName, value);
	}
	
	public void requestPartyService(String partyId, int signal) {
		
		mConfManager.requestPartyService(partyId, signal);
	}
	
	public void disconnectWebOp(String partyId) {
		
		mConfManager.disconnectWebOp(partyId);
	}
}
