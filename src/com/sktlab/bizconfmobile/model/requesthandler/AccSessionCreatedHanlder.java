package com.sktlab.bizconfmobile.model.requesthandler;

import com.sktlab.bizconfmobile.mina.MinaMsg;
import com.sktlab.bizconfmobile.mina.MinaUtil;
import com.sktlab.bizconfmobile.util.Util;


public class AccSessionCreatedHanlder extends RequestHandler {

	public static final String TAG = "ACCCreatedhanlder";
	
	@Override
	public void handleRequest(String request) {
		
		//Util.BIZ_CONF_DEBUG(TAG, "receive msg from server: " + request);
		
		do{
			
			if(request.contains(MinaUtil.CREATE_ACC_SESSION)) {
				
				MinaMsg msg = new MinaMsg(request);
				
				confControl.setACCId(msg.getMsgData().get(1));
				
				confControl.createBVSession();	
				break;
			}
			
			if (!confControl.isACCCreated()){
				
				break;
			}
			
			if (!Util.isEmpty(successor)) {
				
				this.successor.handleRequest(request);
			}
						
		}while(false);
		
	}
}
