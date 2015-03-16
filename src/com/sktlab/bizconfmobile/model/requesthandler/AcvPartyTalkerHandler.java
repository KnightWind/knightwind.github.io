package com.sktlab.bizconfmobile.model.requesthandler;

import com.sktlab.bizconfmobile.mina.MinaMsg;
import com.sktlab.bizconfmobile.mina.MinaUtil;
import com.sktlab.bizconfmobile.model.manager.CommunicationManager;
import com.sktlab.bizconfmobile.util.Util;

public class AcvPartyTalkerHandler extends RequestHandler {

	public static final String TAG = "ACVPTalkerHandler";
	
	@Override
	public void handleRequest(String request) {

		//Util.BIZ_CONF_DEBUG(TAG, "receive msg from server: " + request);
		
		if (request.contains(MinaUtil.MSG_ACV_P_TAKLER)) {
			
			MinaMsg msg = new MinaMsg(request);
			
			String partyId = msg.getMsgData().get(0);
			String partyState = msg.getMsgData().get(1);
			
			//Util.BIZ_CONF_DEBUG(TAG, "talker state change now");
					
			commManager.talkerStateChanged(partyId, partyState);
			
		}else if(!Util.isEmpty(successor)){
			
			this.successor.handleRequest(request);
		}
	}

}
