package com.sktlab.bizconfmobile.model.requesthandler;

import com.sktlab.bizconfmobile.mina.MinaUtil;
import com.sktlab.bizconfmobile.util.Util;

public class AssocConfHandler extends RequestHandler {

	public static final String TAG = "AssocConfHandler";
	
	@Override
	public void handleRequest(String request) {
	
		//Util.BIZ_CONF_DEBUG(TAG, "receive msg from server: " + request);
		
		do{
			
			if (request.contains(MinaUtil.MSG_ASSOC_CONF)) {
				
				confControl.setConfAssoced(true);
				
				//confControl.requestActiveConfList();
				confControl.requestLiveConfList();
				break;
			}
			
			if (!confControl.isConfAssoced()) {
				
				break;
			}
						
			if (!Util.isEmpty(successor)) {
				
				this.successor.handleRequest(request);
			}
			
		}while(false);

	}

}
