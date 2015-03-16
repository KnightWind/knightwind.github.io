package com.sktlab.bizconfmobile.model.requesthandler;

import com.sktlab.bizconfmobile.activity.ConferenceActivity;
import com.sktlab.bizconfmobile.mina.MinaMsg;
import com.sktlab.bizconfmobile.mina.MinaPListMsg;
import com.sktlab.bizconfmobile.mina.MinaUtil;
import com.sktlab.bizconfmobile.model.ConfAccount;
import com.sktlab.bizconfmobile.model.Participant;
import com.sktlab.bizconfmobile.util.Util;

public class PartyListInConfHandler extends RequestHandler {

	public static final String TAG = "PListHandler";
	
	@Override
	public void handleRequest(String request) {
				
		//Util.BIZ_CONF_DEBUG(TAG, "receive msg from server: " + request);
		
		if (request.contains(MinaUtil.MSG_ACV_P_LIST)) {
						
			do{
				
				//just used first start conference.
				if (confControl.isServerLinkReady()) {
					
					break;
				}
				
				//if not send request party list command, not deal with it.
				if (!confControl.isRequestPList()) {
					
					Util.BIZ_CONF_DEBUG(TAG, "receive plist, not send request");
					break;
				}
				
				//test
				//confControl.requestLiveConfList();
				
				Util.BIZ_CONF_DEBUG(TAG, "receive plist,enter conference management screen");
				
				boolean isReconnectNetwork = false;
				
				if (ConferenceActivity.isReConnecting) {
					
					isReconnectNetwork = true;
				}
				
				confControl.setServerLinkReady(true);
				
				MinaMsg msg = new MinaPListMsg(request);	
				
				ConfAccount account = commManager.getActiveAccount();
				
				boolean isGuest = Util.isEmpty(account.getModeratorPw());								
				
				boolean isOutCallEnable = account.isDialOutEnable();
				
				if(!isOutCallEnable){
					
					break;
				}
				
				if (isReconnectNetwork) {
					
					//Util.BIZ_CONF_DEBUG(TAG, "network reconnected, not add new party");
					break;
				}
				
				Participant currentUser = contactManager.getCurrentUserObject();

				if (commManager
						.isPartyInConfByPhone(currentUser.getPhone())) {
					
					break;
				}
							
				if (!Util.isEmpty(currentUser.getIdInConference())) {
					
					Util.BIZ_CONF_DEBUG(TAG, "current user had been in conference,not add it again");
					break;
				}				
				
				//Util.BIZ_CONF_DEBUG(TAG, "add new party to conf: " + currentUser.getPhone());
				
				if(isGuest) {
					
					confControl.addPartyToConf(currentUser, false);
					break;
				}
				
				//Util.BIZ_CONF_DEBUG(TAG, "add moderator to conference");
				//confControl.addPartyToConf(currentUser, true);
				
			}while(false);
			
		}else if(!Util.isEmpty(successor)){
			
			this.successor.handleRequest(request);
		}
	}
}
