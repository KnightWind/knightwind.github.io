package com.sktlab.bizconfmobile.model.requesthandler;

import com.sktlab.bizconfmobile.mina.MinaACLMsg;
import com.sktlab.bizconfmobile.mina.MinaMsg;
import com.sktlab.bizconfmobile.mina.MinaUtil;
import com.sktlab.bizconfmobile.model.ConfControl;
import com.sktlab.bizconfmobile.model.OngoingConf;
import com.sktlab.bizconfmobile.model.ConfAccount;
import com.sktlab.bizconfmobile.model.LSSession;
import com.sktlab.bizconfmobile.model.manager.CommunicationManager;
import com.sktlab.bizconfmobile.util.Util;

public class AativeConfListHanlder extends RequestHandler {

	public static final String TAG = "ACLHanlder";
	
	@Override
	public void handleRequest(String request) {
		
		//Util.BIZ_CONF_DEBUG(TAG, "receive msg from server: " + request);
		
		if(request.contains(MinaUtil.MSG_ACTIVE_CONF_LIST)){
			
			//now user BV.B.APL to verify the conference code and password.
//			commManager.clearActiveConfs();
//			
//			//after this step, we store the active conference list in Communicationmanager's mActiveConfs
//			MinaMsg msg = new MinaACLMsg(request);				
//					
//			do {
//				
//				String bridgeNumber = msg.getMsgData(0);
//				
//				if (!LSSession.bridgeNumber.equalsIgnoreCase(bridgeNumber)) {
//					
//					Util.BIZ_CONF_DEBUG(TAG, "bridgeNumber not equal: " + bridgeNumber);
//					break;
//				}
//				
//				ConferenceAccount activeAccount = commManager.getActiveAccount();
//				
//				String confNumber = activeAccount.getConfCode();
//				
//				//Util.BIZ_CONF_DEBUG(TAG, "current active conf num: " + confNumber);
//				
//				boolean isConfInBridge = true;
//				
//				if (!commManager.getActiveConfs().containsKey(confNumber)) {
//					
//					Util.BIZ_CONF_DEBUG(TAG, "current active account code number is not active in bridge,please check");
//					
//					isConfInBridge = false;
//					
//					confControl.setServerLinkReady(false);
//					break;
//				}
//				
//				confControl.createACVSession();
//			}while(false);
//			
		}else if(!Util.isEmpty(successor)){
					
			this.successor.handleRequest(request);
		}
		
	}
}
