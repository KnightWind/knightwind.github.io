package com.sktlab.bizconfmobile.model.requesthandler;

import com.sktlab.bizconfmobile.mina.MinaMsg;
import com.sktlab.bizconfmobile.mina.MinaUtil;
import com.sktlab.bizconfmobile.model.ConfControl;
import com.sktlab.bizconfmobile.model.OngoingConf;
import com.sktlab.bizconfmobile.model.manager.CommunicationManager;
import com.sktlab.bizconfmobile.util.Util;

public class LiveConfRemovedHanlder extends RequestHandler {

	public static final String TAG = "LCRHanlder";
	
	@Override
	public void handleRequest(String request) {
		
		//Util.BIZ_CONF_DEBUG(TAG, "receive msg from server: " + request);
		
		if (request.contains(MinaUtil.MSG_LIVE_CONF_REMOVED)) {
			
			MinaMsg msg = new MinaMsg(request);
			
			String confId = msg.getMsgData().get(1);
			
			OngoingConf activeConf = confControl.getActiveConference();
			
			if (!Util.isEmpty(confId) && confId.equalsIgnoreCase(activeConf.getAttr().getConfId())) {
				
				commManager.notifyConfEnded();
			}
		}else if(!Util.isEmpty(successor)){
			
			this.successor.handleRequest(request);
		}
	}

}
