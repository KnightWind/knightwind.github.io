package com.sktlab.bizconfmobile.model.requesthandler;

import com.sktlab.bizconfmobile.mina.MinaMsg;
import com.sktlab.bizconfmobile.mina.MinaUtil;
import com.sktlab.bizconfmobile.util.Util;

public class PartyDeletedHandler extends RequestHandler {

	public static final String TAG = "PDelHandler";
	
	@Override
	public void handleRequest(String request) {
		
		//Util.BIZ_CONF_DEBUG(TAG, "receive msg from server: " + request);
		
		if (request.contains(MinaUtil.MSG_ACV_P_DEL)) {
			
			MinaMsg msg = new MinaMsg(request);
			
			String partyId = msg.getMsgData().get(0);
			
			commManager.removeParty(partyId);		
			
			contactManager.removePartiesInList(partyId);
//			Participant originalParty = ContactManager.getInstance().getOriginalParty();
//			Participant destinateParty = ContactManager.getInstance().getDestinateParty();
//			
//			if (partyId.equals(originalParty.getIdInConference())) {
//				
//				cm.moderatorOutCall(session,destinateParty);
//			}
		}else if(!Util.isEmpty(successor)){
			
			this.successor.handleRequest(request);
		}

	}

}
