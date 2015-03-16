package com.sktlab.bizconfmobile.model;

import org.apache.mina.core.session.IoSession;

import com.sktlab.bizconfmobile.mina.MinaMsg;
import com.sktlab.bizconfmobile.mina.MinaUtil;
import com.sktlab.bizconfmobile.model.manager.CommunicationManager;
import com.sktlab.bizconfmobile.util.Util;

public class AccSession extends BaseBusinessSession {

	public static final String TAG = "ACCSession";
	
	public static final  String ALLOW_CONF_LEVEL_PC = "AllowConfLevelPasscode";	
	public static final String CONF_LEVEL_PASS_CODE =  "ConfLevelPasscode";
	
	private boolean isSendMsgToActiveConf;
	private boolean isConfActived;	
	private boolean isSendMsgToAssocConf;
	private boolean isConfAssoc;
	private boolean isSetSecurityCode;
	
	public AccSession() {
		
		super();
		
		resetFuncState();
	}
	
	
	@Override
	public void reset() {
		
		super.reset();
		
		resetFuncState();
	}

	public void resetFuncState() {
		
		setConfActived(false);
		setSendMsgToActiveConf(false);
		setSendMsgToAssocConf(false);
		setConfAssoc(false);
		isSetSecurityCode = false;
	}
	/**
	 * 
	 * @param session
	 * @param conf
	 */
	public void assocWithConf(IoSession session, OngoingConf conf) {
		
		if (!isSendMsgToAssocConf()) {
		
			sendMsg(session,  
					MinaUtil.MSG_ASSOC_CONF,
					conf.getAttr().getConfId());
			
			setSendMsgToAssocConf(true);
		}		
	}
	
	public void muteParty(IoSession session,Participant party, int state,int playMessage) {
		
		sendMsg(session, 
				MinaUtil.MSG_MUTE_PARTY,
				String.valueOf(state),
				String.valueOf(playMessage),
				"1",
				party.getIdInConference());
	}
	
	public void selfMute(IoSession session,Participant party, int state,int playMessage){
		
		sendMsg(session, 
				MinaUtil.MSG_SELF_MUTE,
				String.valueOf(state),
				String.valueOf(playMessage),
				party.getIdInConference());
	}
	/**
	 * 
	 * @param session
	 * @param state 0 stop 1 start
	 */
	public void record(IoSession session, int state) {
		
		sendMsg(session, 
				MinaUtil.MSG_RECORD,
				String.valueOf(state));
	}
	/**
	 * roll call party's name
	 * @param session
	 */
	public void rollCall(IoSession session) {
	
		String confId = ConfControl.getInstance().getActiveConference().getAttr().getConfId();
		
		//Util.BIZ_CONF_DEBUG(TAG, "conf id: " + confId);
		
		sendMsg(session, MinaUtil.MSG_ROLL_CALL,confId,"0");
	}

	/**
	 * out call a guest user
	 * 
	 * @param party
	 */
	public void guestOutCallParty(IoSession session, Participant party) {
		
		String outCallPartyId = party.getIdInConference();
		
		//Util.BIZ_CONF_DEBUG(TAG,"out call party id: " + outCallPartyId);
		
		sendMsg(session, MinaUtil.MSG_GUEST_OUT_CALL,outCallPartyId, "0");
	}

	/**
	 * call moderator party
	 * 
	 * @param party
	 */
	public void transferWebOp(IoSession session, Participant party) {
		
		String outCallPartyId = party.getIdInConference();
		
		//Util.BIZ_CONF_DEBUG(TAG,"out call party id: " + outCallPartyId);
		
		sendMsg(session, MinaUtil.MSG_OUT_CALL,outCallPartyId, "1");
	}
	
	/**
	 * call moderator party
	 * 
	 * @param party
	 */
	public void moderatorOutCall(IoSession session, Participant party) {
		
		String outCallPartyId = party.getIdInConference();
		
		//Util.BIZ_CONF_DEBUG(TAG,"out call party id: " + outCallPartyId);
		
		sendMsg(session, MinaUtil.MSG_OUT_CALL,outCallPartyId);
	}
	
	public void activeConf(IoSession session, LSSession lsSession, String passcode) {

		// see the api doc for more information
		String startImmediately = "0";
		String partition = "0";
		
		if (!isSendMsgToActiveConf()) {
			
			sendMsg(session, MinaUtil.MSG_ACTIVATE_CONF,
					LSSession.bridgeNumber,
					passcode,
					startImmediately,
					partition);
			
			setSendMsgToActiveConf(true);
		}
	}
	
	/**
	 * active a conference whose conference account is the user clicked
	 * 
	 * The conference is based on the user clicked conference account
	 */
	public void activeConf(IoSession session, LSSession lsSession) {

		ConfAccount account = CommunicationManager.getInstance()
				.getActiveAccount();
		
//		String moderatorPw = account.getModeratorPw();	
//		activeConf(session, lsSession, moderatorPw);
		
		String confCode = account.getConfCode();	
		activeConf(session, lsSession, confCode);
	}

	/**
	 * add party to current active conference which start by this app
	 * 
	 * @param party
	 * @param isModerator
	 */
	public void addPartyToConf(IoSession session, Participant party,
			boolean isModerator) {

		addPartyToConf(session, party, isModerator, ConfControl
				.getInstance().getActiveConference().getAttr().getConfId());
	}

	/**
	 * add a party to the specified conference
	 * 
	 * @param party
	 * @param isModerator
	 * @param confId
	 */
	public void addPartyToConf(IoSession session, Participant party,
			boolean isModerator, String confId) {

		String host = "0";

		if (isModerator) {

			host = "1";
		}
		
		//use the party's phone number as its name to the CAS server. This can prevent party's name's encode problem.
		sendMsg(session, MinaUtil.MSG_CREATE_PARTY,
				confId,
				party.getPhone(),
				Util.getFormatPhoneNum(party.getPhone()),
				host,
				"~~~~~~");
	}

	/**
	 * erase conference in bridge
	 * 
	 * @param session
	 *            link session
	 * @param conf
	 *            conference to destory
	 */
	public void destoryConf(IoSession session, OngoingConf conf) {
		
		sendMsg(session, MinaUtil.MSG_CONF_DESTORY,
					conf.getAttr().getConfId(),
					"3");
	}

	/**
	 * disconnect a party with the conference
	 * 
	 * @param session
	 *            link session with server
	 * @param party
	 *            party to be disconnected
	 */
	public void disconnectParty(IoSession session, String partyId) {
		
		String forceDisconnect = "1";
		
		sendMsg(session, 
				MinaUtil.MSG_DISCONNECT_PARTY,
				partyId,
				forceDisconnect);
	}
	
	public void lock(IoSession session, OngoingConf conf,int state) {
		
		sendMsg(session, MinaUtil.MSG_ACC_C_A_ALTER,
						conf.getAttr().getConfId(),
						"ConfSecure",
						String.valueOf(state));
	}
	
	public void confMute(IoSession session,int muteState, int muteParticipants){
		
		sendMsg(session, 
					MinaUtil.MSG_CONF_MUTE,
					String.valueOf(muteState),
					String.valueOf(muteParticipants));
	}
	
	public void alterPartyAttr(IoSession session, String partyId,String fieldName, String value) {
		
		sendMsg(session, 
				MinaUtil.MSG_P_ALTER,
				partyId,
				fieldName,
				value);
	}
	
	public void requestPartyService(IoSession session, String partyId, String signal) {
		
		sendMsg(session, 
				MinaUtil.MSG_P_SERVICE,
				partyId,
				signal);
	}
	
	public void disconnectWebOp(IoSession session, String partyId) {
		
		sendMsg(session, 
				MinaUtil.MSG_P_MOVE,
				"0",
				"0",
				"1",
				partyId);
	}
	
	public boolean isSendMsgToActiveConf() {
		return isSendMsgToActiveConf;
	}

	public void setSendMsgToActiveConf(boolean isSendMsgToActiveConf) {
		this.isSendMsgToActiveConf = isSendMsgToActiveConf;
	}

	public boolean isConfActived() {
		return isConfActived;
	}

	public void setConfActived(boolean isConfActived) {
		this.isConfActived = isConfActived;
	}


	public boolean isSendMsgToAssocConf() {
		return isSendMsgToAssocConf;
	}


	public void setSendMsgToAssocConf(boolean isSendMsgToAssocConf) {
		this.isSendMsgToAssocConf = isSendMsgToAssocConf;
	}


	public boolean isConfAssoc() {
		return isConfAssoc;
	}

	public void setConfAssoc(boolean isConfAssoc) {
		this.isConfAssoc = isConfAssoc;
	}
	
	public void setSecurityCode(IoSession session, String confId, String value) {
		
		if (!isSetSecurityCode) {
			
			isSetSecurityCode = true;
			sendMsg(session, 
					MinaUtil.MSG_ACC_C_A_ALTER,
					confId,
					CONF_LEVEL_PASS_CODE,
					value);
		}		
	}
	
	public void alterFieldValue(IoSession session, String confId, String fieldName, String value) {
		
		sendMsg(session, 
				MinaUtil.MSG_ACC_C_A_ALTER,
				confId,
				fieldName,
				value);
	}
}
