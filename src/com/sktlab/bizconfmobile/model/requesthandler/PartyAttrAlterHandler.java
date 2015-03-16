package com.sktlab.bizconfmobile.model.requesthandler;

import com.sktlab.bizconfmobile.mina.MinaUtil;
import com.sktlab.bizconfmobile.model.Participant;
import com.sktlab.bizconfmobile.util.Util;

public class PartyAttrAlterHandler extends RequestHandler {

	public static final String TAG = "PAlterHandler";
	
	@Override
	public void handleRequest(String request) {
		
		//Util.BIZ_CONF_DEBUG(TAG, "receive msg from server: " + request);
		
		if (request.contains(MinaUtil.MSG_P_ALTER)) {
			
			do{
				
				if (!commManager.isTransferingPhone()) {
					
					break;
				}
				
				Participant originalParty = contactManager.getOriginalParty();
				Participant destinateParty = contactManager.getDestinateParty();
				
				if (originalParty.isModerator()) {

					originalParty.setIsModerator(false);
					destinateParty.setIsModerator(true);

					confControl.alterPartyAttr(destinateParty,
							MinaUtil.MSG_P_HOST_CONTROL_LEVEL, "2");
					
					break;
				}

				originalParty.setIsModerator(true);
				confControl.disconnectWebOp(
							originalParty.getIdInConference());
				
			}while(false);

		}else if(!Util.isEmpty(successor)){
			
			this.successor.handleRequest(request);
		}
	}

}
