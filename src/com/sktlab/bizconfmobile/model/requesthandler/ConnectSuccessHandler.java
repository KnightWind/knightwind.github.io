package com.sktlab.bizconfmobile.model.requesthandler;

import com.sktlab.bizconfmobile.net.ServerLinkSession;
import com.sktlab.bizconfmobile.net.StatusCode;
import com.sktlab.bizconfmobile.util.Util;

public class ConnectSuccessHandler extends RequestHandler {
	
	public static final String TAG = "ConnectSuccessHandler";
	
	@Override
	public void handleRequest(String request) {

		//Util.BIZ_CONF_DEBUG(TAG, "receive msg from server: " + request);
		
		String successSignal = "0~0~LS.VER~2.0";
		
		String failSignal = StatusCode.CAS_CONNECTED_FAIL_SIGNAL;
					
		if(ServerLinkSession.isUseTransferServerAddress) {
			
			successSignal = StatusCode.CONNECT_SUCCESS;
			failSignal = StatusCode.TEST_CAS_IS_DOWN;
		}
		
		do {
			
			//when receive fail signal, notify the wait thread
			if (request.equals(failSignal)) {
				
				confControl.setServerLinkReady(false);
				
				//Util.BIZ_CONF_DEBUG(TAG, "connect to server failed or sever can not work");
				break;
			}
			
			if(request.equals(successSignal)) {	
				
				confControl.setServerConnected(true);				
				confControl.createACCSession();
				break;
			}
			
			if (!confControl.isServerConnected()) {
				
				break;
			}
			
			if (!Util.isEmpty(successor)) {
				
				this.successor.handleRequest(request);
			}	
			
		}while(false);
		
	}
}
