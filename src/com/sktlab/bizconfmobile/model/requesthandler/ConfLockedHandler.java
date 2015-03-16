package com.sktlab.bizconfmobile.model.requesthandler;

import com.sktlab.bizconfmobile.mina.MinaUtil;
import com.sktlab.bizconfmobile.model.manager.CommunicationManager;
import com.sktlab.bizconfmobile.util.Util;

public class ConfLockedHandler extends RequestHandler {

	public static final String TAG = "LockHandler";
	
	@Override
	public void handleRequest(String request) {
		
		//Util.BIZ_CONF_DEBUG(TAG, "receive msg from server: " + request);
		
		if (request.contains(MinaUtil.MSG_ACC_C_A_ALTER)) {
			
			//Util.BIZ_CONF_DEBUG(TAG, "receive lock conference msg");
			
			commManager.notifyConfChanged(CommunicationManager.LOCK);
		}else if(!Util.isEmpty(successor)){
			
			this.successor.handleRequest(request);
		}

	}

}
