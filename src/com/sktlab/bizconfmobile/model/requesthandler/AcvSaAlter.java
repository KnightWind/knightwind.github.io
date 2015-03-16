package com.sktlab.bizconfmobile.model.requesthandler;

import com.sktlab.bizconfmobile.mina.MinaMsg;
import com.sktlab.bizconfmobile.mina.MinaUtil;
import com.sktlab.bizconfmobile.util.Util;


public class AcvSaAlter extends RequestHandler {

	public static final String TAG = "AcvSaAlter";
	
	@Override
	public void handleRequest(String request) {

		//Util.BIZ_CONF_DEBUG(TAG, "receive msg from server: " + request);
		
		if (request.contains(MinaUtil.MSG_ACV_SA_ALTER)) {
			
			MinaMsg msg = new MinaMsg(request);
			
			String acvSsnId = msg.getSsnId();
			
			confControl.setACVId(acvSsnId);

		}else if(!Util.isEmpty(successor)){
			
			this.successor.handleRequest(request);
		}
	}

}
