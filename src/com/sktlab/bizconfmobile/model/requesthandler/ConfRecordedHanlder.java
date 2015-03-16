package com.sktlab.bizconfmobile.model.requesthandler;

import com.sktlab.bizconfmobile.mina.MinaUtil;
import com.sktlab.bizconfmobile.model.manager.CommunicationManager;
import com.sktlab.bizconfmobile.util.Util;

public class ConfRecordedHanlder extends RequestHandler {

	public static final String TAG = "RecordHanlder";
	
	@Override
	public void handleRequest(String request) {
		
		//Util.BIZ_CONF_DEBUG(TAG, "receive msg from server: " + request);
		
		if (request.contains(MinaUtil.MSG_RECORD)) {
			
			//Util.BIZ_CONF_DEBUG(TAG, "receive record msg");
			
			commManager.notifyConfChanged(
					CommunicationManager.RECORD);
		}else if(!Util.isEmpty(successor)){
			
			this.successor.handleRequest(request);
		}
	}

}
