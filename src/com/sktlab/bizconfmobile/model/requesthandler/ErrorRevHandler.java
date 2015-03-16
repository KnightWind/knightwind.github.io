package com.sktlab.bizconfmobile.model.requesthandler;

import com.sktlab.bizconfmobile.mina.MinaMsg;
import com.sktlab.bizconfmobile.model.ConfControl;
import com.sktlab.bizconfmobile.model.manager.CommunicationManager;
import com.sktlab.bizconfmobile.util.Util;

public class ErrorRevHandler extends RequestHandler {
	
	public static final String TAG = "ErrorHandler";
	
	public static final String ACTIVE_CONF_ERROR = "-ACC.C.ACTIVATE";
	public static final String ERR_FIELD_2 = "~ERR_FIELD~2";
	
	public static final String INVALID_ACCOUNT = ACTIVE_CONF_ERROR + ERR_FIELD_2;

	@Override
	public void handleRequest(String request) {
	
		//Util.BIZ_CONF_DEBUG(TAG, "receive msg from server: " + request);
		
		MinaMsg msg = new MinaMsg(request);
		
		String msgId = msg.getMsgId();
		
		if (commManager.isOperateFailed(msgId)) {
			
			commManager.notifyOperateFailed();
			
			if (msgId.equalsIgnoreCase(ACTIVE_CONF_ERROR)) {
				
				confControl.setServerLinkReady(false);
			}
			
			return;
		}
		
		if(request.contains("ERR")){
			
			do{
				
				if (request.contains(INVALID_ACCOUNT)) {
					
					confControl.setServerLinkReady(false);
					break;
				}
				
			}while(false);
			
			//Util.BIZ_CONF_DEBUG(TAG, "receive error msg from server");
		}else if(!Util.isEmpty(successor)){
			
			this.successor.handleRequest(request);
		}	
	}

}
