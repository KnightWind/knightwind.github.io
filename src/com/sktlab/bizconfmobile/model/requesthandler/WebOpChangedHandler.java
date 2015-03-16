package com.sktlab.bizconfmobile.model.requesthandler;

import com.sktlab.bizconfmobile.util.Util;

public class WebOpChangedHandler extends RequestHandler {
	
	public static final String TAG = "WebOpChangeHandler";
	
	private final String WEB_OP = "Web Operator";
	
	@Override
	public void handleRequest(String request) {
		
		//Util.BIZ_CONF_DEBUG(TAG, "receive msg from server: " + request);
		
		if (request.contains(WEB_OP) && commManager.isTransferingPhone()) {
			
			//Util.BIZ_CONF_DEBUG(TAG, "receive web op change msg ");
			commManager.notifyPhoneTransfered();
			
		}else if(!Util.isEmpty(successor)) {
			
			this.successor.handleRequest(request);
		}
	}
}
