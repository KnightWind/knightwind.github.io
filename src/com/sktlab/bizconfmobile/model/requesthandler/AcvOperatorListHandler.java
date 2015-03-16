package com.sktlab.bizconfmobile.model.requesthandler;

import com.sktlab.bizconfmobile.mina.MinaMsg;
import com.sktlab.bizconfmobile.mina.MinaUtil;
import com.sktlab.bizconfmobile.model.ConfAccount;
import com.sktlab.bizconfmobile.model.Participant;
import com.sktlab.bizconfmobile.util.Util;

public class AcvOperatorListHandler extends RequestHandler {

	public static final String TAG = "ACVOLHandler";

	@Override
	public void handleRequest(String request) {

		if (request.contains(MinaUtil.MSG_ACV_OL)) {
			
			do{
				
				MinaMsg msg = new MinaMsg(request);
				
				String operatorCount = msg.getMsgData(2);
				
				ConfAccount account = commManager.getActiveAccount();							
				
				boolean isOutCallEnable = account.isDialOutEnable();
				
				if(!isOutCallEnable){
					
					break;
				}				
				
				if (!commManager.isModeratorAccount()) {
					
					Util.BIZ_CONF_DEBUG(TAG, "no need add moderator party");
					break;
				}
				
				try {
					
					int count = Integer.parseInt(operatorCount);
					
					if (count > 0) {
						
						break;
					}
										
				} catch (Exception e) {
					
				}
				
				Participant currentUser = contactManager.getCurrentUserObject();

				if (commManager
						.isPartyInConfByPhone(currentUser.getPhone())) {
					
					break;
				}
							
				if (!Util.isEmpty(currentUser.getIdInConference())) {
					
					Util.BIZ_CONF_DEBUG(TAG, "current user had been in conference,not add it again");
					break;
				}				
				
				Util.BIZ_CONF_DEBUG(TAG, "add moderator to conference");
				confControl.addPartyToConf(currentUser, true);
				
			}while(false);		
			
		} else if (!Util.isEmpty(successor)) {

			this.successor.handleRequest(request);
		}
	}

}
