package com.sktlab.bizconfmobile.model.requesthandler;

import com.sktlab.bizconfmobile.mina.MinaMsg;
import com.sktlab.bizconfmobile.mina.MinaUtil;
import com.sktlab.bizconfmobile.model.ConfAccount;
import com.sktlab.bizconfmobile.model.Participant;
import com.sktlab.bizconfmobile.model.manager.CommunicationManager;
import com.sktlab.bizconfmobile.model.manager.ContactManager;
import com.sktlab.bizconfmobile.util.Util;

public class PartyAddedHandler extends RequestHandler {
	
	public static final String TAG = "PAddHandler";
	
	@Override
	public void handleRequest(String request) {
		
		//Util.BIZ_CONF_DEBUG(TAG, "receive msg from server: " + request);
		
		/**
		 * receive msg from server: 414~2~ACV.P.ADD~418~18202932163
		 */
		if (request.contains(MinaUtil.MSG_ACV_P_ADD)) {
			
			confControl.setServerLinkReady(true);				
			
			MinaMsg msg = new MinaMsg(request);
			
			String acvSsnId = msg.getSsnId();
			
			confControl.setACVId(acvSsnId);
			
			//Util.BIZ_CONF_DEBUG(TAG, " ACV SsnId: " + acvSsnId);
			
			String partyId = msg.getMsgData().get(0);
			
			String phoneNumber = msg.getMsgData().get(1);
			
			do {
				
				//This party had been add in conference party list
				if (commManager.isPartyInConfById(partyId)) {
					
					break;
				}
				
				if (commManager.isPartyInConfByPhone(phoneNumber)) {
					
					//Util.BIZ_CONF_DEBUG(TAG, "this party already in conference, disconnect it");
					confControl.disconnectParty(partyId);
					break;
				}
				
				//Util.BIZ_CONF_DEBUG(TAG, "parse party number from server:" + phoneNumber);		
				Participant party = contactManager.getPartyByPhone(phoneNumber);				
				
				//to indicate that this party is received from server,so no need to out call
				boolean isReceiveParty = false;
				
				if (null == party) {					
					
					party = new Participant();
					party.setName(ContactManager.getContactNameByPhoneNumber(phoneNumber));
					party.setPhone(phoneNumber);						
					
					isReceiveParty = true;
					//Util.BIZ_CONF_DEBUG(TAG, "a guest participant added to conference, partyNumber:" + phoneNumber);
				}
				
				party.setIdInConference(partyId);
				
				Participant communicationParty = new Participant(party);
				
				commManager.putParty(communicationParty);
				
				party.setIdInConference(partyId);												
				
				if (isReceiveParty) {
					
					//if moderator leave conference and transfer his leader authority, 
					// when he start the conference again, should guest call him.
					ConfAccount activeAccount 
							= commManager.getActiveAccount();
					
					String dialOutNum = activeAccount.getDialOutNumber().replace("+", "00");
					
					//Util.BIZ_CONF_DEBUG(TAG, "dialoutNum: " + dialOutNum + "phoneNumber: " + phoneNumber);
					
					if (!Util.isEmpty(dialOutNum) && 
							(dialOutNum.contains(phoneNumber) || phoneNumber.contains(dialOutNum))) {
						
						//customer's requirement changed, not call him, just enter the management screen
						//confControl.guestOutCallParty(party);	
						
						Util.BIZ_CONF_DEBUG(TAG, "moderator already in party, disconnect him haha");
//						commManager.removeParty(partyId);
//						confControl.disconnectParty(partyId);
//						
//						commManager.notifyPartyChanged();
					}
					break;
				}
				
				if(party.isModerator()) {
					
					//Util.BIZ_CONF_DEBUG(TAG, "current party is moderator, phone: " +  party.getPhone());
					confControl.moderatorOutCall(party);
				}else {
					
					//Util.BIZ_CONF_DEBUG(TAG, "current party is guest, phone: " +  party.getPhone());
					confControl.guestOutCallParty(party);					
				}
				
			}while(false);		

		}else if(!Util.isEmpty(successor)){
			
			this.successor.handleRequest(request);
		}
	}
	
}
