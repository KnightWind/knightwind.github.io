package com.sktlab.bizconfmobile.model.requesthandler;

import com.sktlab.bizconfmobile.mina.MinaMsg;
import com.sktlab.bizconfmobile.mina.MinaUtil;
import com.sktlab.bizconfmobile.model.ConferenceAttr;
import com.sktlab.bizconfmobile.util.Util;

public class ActiveConfHandler extends RequestHandler {
	
	public static final String TAG = "ActiveConfHandler";
	
	@Override
	public void handleRequest(String request) {
		
		//Util.BIZ_CONF_DEBUG(TAG, "receive msg from server: " + request);
		
		do {
			
			if(request.contains(MinaUtil.MSG_ACTIVATE_CONF)) {
				
				confControl.setConfActived(true);
				
				MinaMsg msg = new MinaMsg(request);
				
				ConferenceAttr activeConfAttr = confControl.getActiveConference().getAttr();
				
				activeConfAttr.setConfId(msg.getMsgData().get(0));

				confControl.assocWithConf();			
				//confControl.requestLiveConfList();
				break;
			}
			
			if (!confControl.isConfActived()) {
				
				break;
			}
			
			if (!Util.isEmpty(successor)) {
				
				this.successor.handleRequest(request);
			}
			
		}while (false);

	}
}
