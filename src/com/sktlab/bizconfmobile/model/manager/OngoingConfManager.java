package com.sktlab.bizconfmobile.model.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.apache.mina.core.session.IoSession;

import android.app.Activity;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.activity.AppClass;
import com.sktlab.bizconfmobile.interfaces.IConfControl;
import com.sktlab.bizconfmobile.interfaces.ILoadingDialogCallback;
import com.sktlab.bizconfmobile.mina.MinaUtil;
import com.sktlab.bizconfmobile.model.AccSession;
import com.sktlab.bizconfmobile.model.AcvSession;
import com.sktlab.bizconfmobile.model.BridgeInfo;
import com.sktlab.bizconfmobile.model.BvSession;
import com.sktlab.bizconfmobile.model.ConfAccount;
import com.sktlab.bizconfmobile.model.ConfControl;
import com.sktlab.bizconfmobile.model.Constant;
import com.sktlab.bizconfmobile.model.OngoingConf;
import com.sktlab.bizconfmobile.model.LSSession;
import com.sktlab.bizconfmobile.model.Participant;
import com.sktlab.bizconfmobile.net.ServerLinkSession;
import com.sktlab.bizconfmobile.util.LoadingDialogUtil;
import com.sktlab.bizconfmobile.util.Util;


public class OngoingConfManager implements IConfControl{
	
	public static final String TAG = "ConferenceManager";
	
	private ServerLinkSession serverLSSession;
	
	private LSSession lsSession;
	private AccSession accSession;
	private BvSession bvSession;
	private AcvSession acvSession;
	
	private LoadingDialogUtil dialog;
	
	private OngoingConf mActiveConference;
	
	public OngoingConfManager(){

		
		mActiveConference = new OngoingConf();
		
		serverLSSession = new ServerLinkSession();
		
		lsSession = new LSSession();
		accSession = new AccSession();
		bvSession = new BvSession();
		acvSession = new AcvSession();
	}
	
	public ServerLinkSession getServerLSSession() {
		return serverLSSession;
	}

	public void setServerLSSession(ServerLinkSession serverLSSession) {
		this.serverLSSession = serverLSSession;
	}
	
	/**
	 * start the conference user clicked, the account must be valid
	 * @return
	 */
	public void startConf(Activity activity, ILoadingDialogCallback callback){
		
		dialog = new LoadingDialogUtil(activity, callback);
		
		ExecutorService service = AppClass.getInstance().getService();
		
		//final ILoadingDialogCallback callBackFun = callback;
		service.submit(new Runnable() {
			
			@Override
			public void run() {
				
				dialog.showDialog(R.string.toast_connecting_start_conf, MinaUtil.CONNECT_WAITING_TIME);
				
				do{
				
					ConfAccount account = CommunicationManager.getInstance().getActiveAccount();
					
					String confCode = account.getConfCode();
					
					BridgeInfo bridgeInfo = new BridgeInfo(confCode);
					
					if (!bridgeInfo.isBridgeIdValid()){
						
						break;
					}
					
					int bridgeId = bridgeInfo.getBridgeId();
					
					LSSession.bridgeNumber = String.valueOf(bridgeId);
					
					if (account.isUseDefaultAccessNum()) {
						
						//save this conference code's bridge information
						Util.setSpInt(AppClass.getInstance(), confCode, bridgeId);
						
						if (Util.isEmpty(account.getAccessNumber())) {
							
							if (bridgeId == Constant.SHANG_HAI_BRIDGE) {
								
								account.setAccessNumber(Constant.SHANG_HAI_LINK_ACCESS_NUM);
							}else if (bridgeId == Constant.BEI_JING_BRIDGE) {
								
								account.setAccessNumber(Constant.BEI_JING_LINK_ACCESS_NUM);
							}
						}
												
						if (!Util.isNetworkReadyForConf(AppClass.getInstance())) {
							
							dialog.finishDialogSuccessDone();
							return;
						}
					}
					
					connectToServer();					
					//ConfControl.waitObj();							
					
				}while(false);
				
//				if(getServerLSSession().isReady()) {
//					
//					Util.BIZ_CONF_DEBUG(TAG, "connect to server success");
//					dialog.finishDialogSuccessDone();
//				}else {
//					
//					Util.BIZ_CONF_DEBUG(TAG, "connect to server fail");
//					dialog.finishDialogWithErrorMsg();	
//				}
				
			}
		});

	}
	
	public void startConfSuccessCallback() {
		
		if (null != dialog) {
			
			dialog.finishDialogSuccessDone();
		}
	}
	
	public void startConfFailCallback() {
		
		if (null != dialog) {
			
			dialog.finishDialogWithErrorMsg();
		}
	}
	/**
	 * add party to current active conference which start by this app
	 * @param party
	 * @param isModerator
	 */
	public void addPartyToConf(Participant party, boolean isModerator){
		
		addPartyToConf(party, isModerator, mActiveConference.getAttr().getConfId());
	}
	
	//add a party to the specified conference
	public void addPartyToConf(Participant party, boolean isModerator, String confId){
		
		accSession.addPartyToConf(serverLSSession.getSession(), party, isModerator, confId);
	}
	
	private void connectToServer() {
		
		serverLSSession.connect();
	}
	
	public void reset(){
		
		//Util.BIZ_CONF_DEBUG(TAG, "reset all variable");
		
		lsSession.reset();
		accSession.reset();
		bvSession.reset();
		acvSession.reset();
	}
	
	public void closeSession() {
		
		if (getServerLSSession().isConnected() 
				&& !Util.isEmpty(getServerLSSession().getSession())
				&& !getServerLSSession().getSession().isClosing()){
			
			//Util.BIZ_CONF_DEBUG(TAG, "closing session now");

			//disconnect the party in the conference	
			IoSession session = getServerLSSession().getSession();
			
			do{
						
				//if start conference failed, just close business session
				if (!ConfControl.getInstance().isServerLinkReady()) {
									
					if (!getServerLSSession().getSession().isClosing() 
							&& !Util.isEmpty(mActiveConference.getAttr().getConfId())) {
						//not destroy conference if start conference failed
						//getAccSession().destoryConf(session, mActiveConference);
					}	
					
					closeBusinessSession(session);					
					
					break;
				}
				
				Participant currentUser = ContactManager.getInstance().getCurrentUserObject();
				
				/**
				 * if the party is not moderator, just disconnect himself
				 */
				if (!CommunicationManager.getInstance().isModeratorAccount()) {
					
					String userPartyId = currentUser.getIdInConference();
					
					if (!Util.isEmpty(userPartyId)){
						
						getAccSession().disconnectParty(session, userPartyId);
					}
					
					closeBusinessSession(session);
					break;
				}
				
				/**
				 * If moderator leave conference, just close business session
				 */
				if (CommunicationManager.getInstance().isModeratorLeaveConference()) {
					
					closeBusinessSession(session);
					break;
				}
				
				HashMap<String, Participant> partyMap = CommunicationManager.getInstance().getActiveParties();
				
				Set<String> keys = partyMap.keySet();
				
				ArrayList<Participant> parties = new ArrayList<Participant>();
				
				for (String key : keys) {
					
					parties.add(partyMap.get(key));					
				}
				//Collection<Participant> parties = partyMap.values();				
				//Util.BIZ_CONF_DEBUG(TAG, "party size: " + parties.size());
				
				for(Participant party: parties) {
					
					String idInConf = party.getIdInConference();
					
					if(Util.isEmpty(idInConf)) {
						
						//Util.BIZ_CONF_DEBUG(TAG, "party id is null: " + party.getPhone());
						continue;
					}
					
					getAccSession().disconnectParty(session, idInConf);
					
					//Util.BIZ_CONF_DEBUG(TAG, "disconnect party: " + party.getIdInConference());
				}
				
				if (!Util.isEmpty(mActiveConference.getAttr().getConfId())) {
					
					getAccSession().destoryConf(session, mActiveConference);
				}		
				
				closeBusinessSession(session);				
			}while(false);	
		}
		
		getServerLSSession().close();
		//reset sequence number and session id
		reset();
	}
	
	private void closeBusinessSession(IoSession session) {
		
		getLsSession().closeSession(session, getAcvSession());
		getLsSession().closeSession(session, getAccSession());
		getLsSession().closeSession(session, getBvSession());
	}
	
	public OngoingConf getActiveConference() {
		return mActiveConference;
	}
	
	public void setActiveConference(OngoingConf mActiveConference) {
		this.mActiveConference = mActiveConference;
	}
	
	public LSSession getLsSession() {
		return lsSession;
	}

	public void setLsSession(LSSession lsSession) {
		this.lsSession = lsSession;
	}

	public AccSession getAccSession() {
		return accSession;
	}

	public void setAccSession(AccSession accSession) {
		this.accSession = accSession;
	}

	public BvSession getBvSession() {
		return bvSession;
	}

	public void setBvSession(BvSession bvSession) {
		this.bvSession = bvSession;
	}

	public AcvSession getAcvSession() {
		return acvSession;
	}

	public void setAcvSession(AcvSession acvSession) {
		this.acvSession = acvSession;
	}

	@Override
	public void hfControl() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void selfMute() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void allMute(int muteState, int muteParticipant) {
		
		getAccSession().confMute(getServerLSSession().getSession(), muteState, muteParticipant);
	}

	@Override
	public void rollCall() {
		
		getAccSession().rollCall(getServerLSSession().getSession());
	}

	@Override
	public void record(int state) {
		
		getAccSession().record(getServerLSSession().getSession(), state);
	}

	@Override
	public void lockConf(int state) {

		getAccSession().lock(getServerLSSession().getSession(), 
							getActiveConference(), 
							state);
	}

	@Override
	public void otherFunc() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void muteParty(Participant party,  int state, int playMessage) {
	
		//getAccSession().selfMute(getServerLSSession().getSession(), party,state, playMessage);
		getAccSession().muteParty(getServerLSSession().getSession(), party,state, playMessage);
	}

	@Override
	public void disconnectParty(Participant party) {
	
		getAccSession().disconnectParty(getServerLSSession().getSession(), party.getIdInConference());
	}			
	
	public void disconnectParty(String partyId) {
		
		getAccSession().disconnectParty(getServerLSSession().getSession(), partyId);
	}	
	
	public void createAccSession() {
		
		if (!accSession.isCreated() && !accSession.isCreating()) {
			
			lsSession.createSession(serverLSSession.getSession(), MinaUtil.MSG_ACC);
			accSession.setCreating(true);
		}
	}
	
	public void createBvSession() {
		
		if (!bvSession.isCreated() && !bvSession.isCreating()) {
			
			lsSession.createSession(serverLSSession.getSession(), MinaUtil.MSG_BV);
			bvSession.setCreating(true);
		}
	}
	
	public void createAcvSession() {
		
		if (!acvSession.isCreated() && !acvSession.isCreating()) {
			
			lsSession.createACVSession(serverLSSession.getSession(), mActiveConference.getAttr().getConfId());
			acvSession.setCreating(true);
		}		
	}
	
	public boolean isAccCreated() {
		
		return accSession.isCreated();
	}
	
	public boolean isBvCreated() {
		
		return bvSession.isCreated();
	}
	
	public boolean isAcvCreated() {
		
		return acvSession.isCreated();
	}
	
	public void setAccCreated(boolean isCreated) {
		
		accSession.setCreated(isCreated);
	}
	
	public void setBvCreated(boolean isCreated) {
		
		bvSession.setCreated(isCreated);
	}
	
	public void setAcvCreated(boolean isCreated) {
		
		acvSession.setCreated(isCreated);
	}
	
	public void setAccId(String id) {

		accSession.setId(id);
		accSession.setCreated(true);
	}

	public void setBvId(String id) {

		bvSession.setId(id);
		bvSession.setCreated(true);
	}

	public void setAcvId(String id) {

		acvSession.setId(id);
		acvSession.setCreated(true);
	}
	
	public String getAccId() {

		return accSession.getId();
	}

	public String getBvId() {

		return bvSession.getId();
	}

	public String getAcvId() {

		return acvSession.getId();
	}
	
	public void setLinkReady(boolean isReady) {
		
		serverLSSession.setReady(isReady);
		
		if (isReady) {
			
			startConfSuccessCallback();
		}else {
			
			startConfFailCallback();
		}
		//ConfControl.notifyObj();
	}
	
	public boolean isLinkReady() {
		
		return serverLSSession.isReady();
	}
	
	public void requestActiveConfList() {
		
		if (bvSession.isCreated()) {
			
			bvSession.requestActiveConfList(serverLSSession.getSession());
		}
		
	}
	
	public void activeConf() {
		
		if (accSession.isCreated()) {
			
			accSession.activeConf(serverLSSession.getSession(), lsSession);
		}
	}
	
	public void activeConf(String passcode) {
		
		if (accSession.isCreated()) {
			
			accSession.activeConf(serverLSSession.getSession(), lsSession, passcode);
		}
	}
	
	public void assocWithConf() {
		
		if (accSession.isCreated()) {
			
			accSession.assocWithConf(serverLSSession.getSession(), mActiveConference);
		}
	}
	
	public boolean isRequestPList() {
		
		boolean isRequest = false;
		
		if (acvSession.isCreated()) {
			
			isRequest = acvSession.isRequestPartyList();
		}
		
		return isRequest;
	}
	
	public void requestPList() {
		
		if (acvSession.isCreated()) {
			
			acvSession.requestPList(serverLSSession.getSession());
		}
	}
	
	public void setServerConnected(boolean isRev) {
		
		serverLSSession.setConnected(isRev);
		lsSession.setCreated(true);
	}
	
	public boolean isServerConnected() {
		
		return serverLSSession.isConnected();
	}
	
	public void transferWebOp(Participant party) {
		
		if (accSession.isCreated()) {
			
			accSession.transferWebOp(serverLSSession.getSession(), party);
		}
	}
	
	public void moderatorOutCall(Participant party) {
		
		if (accSession.isCreated()) {
			
			accSession.moderatorOutCall(serverLSSession.getSession(), party);
		}
	}
	
	public void guestOutCallParty(Participant party) {
		
		if (accSession.isCreated()) {
			
			accSession.guestOutCallParty(serverLSSession.getSession(), party);
		}
	}
	
	public void setConfActived(boolean value) {
		
		if (accSession.isCreated()) {
			
			accSession.setConfActived(value);
		}
	}
	
	public boolean isConfActived() {
		
		if (accSession.isCreated()) {
			
			return accSession.isConfActived();
		}
		
		return false;
	}
	
	public void setConfAssoced(boolean value) {
		
		if (accSession.isCreated()) {
			
			accSession.setConfAssoc(value);
		}
	}
	
	public boolean isConfAssoced() {
		
		if (accSession.isCreated()) {
			
			return accSession.isConfAssoc();
		}
		
		return false;
	}
	
	public void alterPartyAttr(String partyId, String fieldName, String value) {
		
		if (accSession.isCreated()) {
			
			accSession.alterPartyAttr(serverLSSession.getSession(), partyId, fieldName, value);
		}
	}
	
	public void requestPartyAttr(String partyId) {
		
		if (acvSession.isCreated()) {
			
			acvSession.requestPartyAttr(serverLSSession.getSession(), partyId);
		}
	}
	
	public void alterTalkerState(int state) {
		
		if (acvSession.isCreated()) {
			
			acvSession.alterTalkersEnable(serverLSSession.getSession(), 
					String.valueOf(state));
		}
	}
	
	public void setSecurityCode(String value) {
		
		String confId = mActiveConference.getAttr().getConfId();
		
		if (accSession.isCreated() && !Util.isEmpty(confId)) {
			
			accSession.setSecurityCode(
					serverLSSession.getSession(), 
					mActiveConference.getAttr().getConfId(),
					value);
		}
	}

	public void alterConfFieldValue(String fieldName, String value) {
		
		String confId = mActiveConference.getAttr().getConfId();
		
		if (accSession.isCreated() && !Util.isEmpty(confId)) {
			
			accSession.alterFieldValue(
					serverLSSession.getSession(), 
					confId,
					fieldName,
					value);
		}
	}
	
	public void requestPartyService(String partyId, int signal) {
		
		if (accSession.isCreated()) {
			
			accSession.requestPartyService(
					serverLSSession.getSession(), 
					partyId,
					String.valueOf(signal));
		}
	}
	
	public void disconnectWebOp(String partyId) {
		
		if (accSession.isCreated()) {
			
			accSession.disconnectWebOp(
					serverLSSession.getSession(), 
					partyId);
		}
	}
	
	public void requestLiveConfList() {
		
		if (bvSession.isCreated()) {
			
			bvSession.requestLiveConfList(serverLSSession.getSession());
		}
	}
}
