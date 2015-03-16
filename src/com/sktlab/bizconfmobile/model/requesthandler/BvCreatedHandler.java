package com.sktlab.bizconfmobile.model.requesthandler;

import com.sktlab.bizconfmobile.mina.MinaMsg;
import com.sktlab.bizconfmobile.mina.MinaUtil;
import com.sktlab.bizconfmobile.model.OngoingConf;
import com.sktlab.bizconfmobile.model.ConfAccount;
import com.sktlab.bizconfmobile.util.Util;

public class BvCreatedHandler extends RequestHandler {
	
	public static final String TAG = "BVCreatedHandler";
	
	@Override
	public void handleRequest(String request) {
		
		//Util.BIZ_CONF_DEBUG(TAG, "receive msg from server: " + request);
		
		do{
			
			if(request.contains(MinaUtil.CREATE_BV_SESSION)) {
				
				MinaMsg msg = new MinaMsg(request);					
				
				confControl.setBVId(msg.getMsgData().get(1));
				
//				Util.BIZ_CONF_DEBUG(TAG, "acc id: " + confControl.getACCId()
//									+ " bv id: " + confControl.getBVId());								
								
				ConfAccount activeAccount = commManager.getActiveAccount();
				
				String confNumber = activeAccount.getConfCode();
				
				//Util.BIZ_CONF_DEBUG(TAG, "current active conf num: " + confNumber);
				confControl.activeConf(confNumber);
				//confControl.requestActiveConfList();
				break;
			}
			
			if (!confControl.isBVCreated()){
				
				break;
			}
			
			if (!Util.isEmpty(successor)) {
				
				this.successor.handleRequest(request);
			}
			
		}while(false);

	}

}
