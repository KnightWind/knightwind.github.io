package com.sktlab.bizconfmobile.model.requesthandler;

import com.sktlab.bizconfmobile.mina.MinaUtil;
import com.sktlab.bizconfmobile.model.Participant;
import com.sktlab.bizconfmobile.util.Util;

public class DisconnectedWebOperatorHandler extends RequestHandler {

	public static final String TAG = "DisconnWebOpHandler";
	
	@Override
	public void handleRequest(String request) {

		//Util.BIZ_CONF_DEBUG(TAG, "receive msg from server: " + request);
		
		if (request.contains(MinaUtil.MSG_O_DEL)) {

			//Util.BIZ_CONF_DEBUG(TAG, "receive move party msg");
			do{
				Participant originalParty = contactManager.getOriginalParty();			
			
				String oPartyId = originalParty.getIdInConference();
				
				if (Util.isEmpty(oPartyId)) {
					
					break;
				}		
				
				Participant destinateParty = contactManager.getDestinateParty();
				
				if (null == destinateParty) {
					
					break;
				}
				
				String dPartyId = destinateParty.getIdInConference();
				
				if (Util.isEmpty(dPartyId)) {
					
					break;
				}
				
				if (oPartyId.equals(dPartyId)) {
					
					break;
				}
				
				commManager.disconnectOrignalParty();
				confControl.transferWebOp(destinateParty);		
				
			}while(false);
				
		} else if (!Util.isEmpty(successor)) {

			this.successor.handleRequest(request);
		}
	}

}
