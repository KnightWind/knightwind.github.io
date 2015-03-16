package com.sktlab.bizconfmobile.model.requesthandler;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.activity.AppClass;
import com.sktlab.bizconfmobile.mina.MinaMsg;
import com.sktlab.bizconfmobile.mina.MinaUtil;
import com.sktlab.bizconfmobile.model.ConfControl;
import com.sktlab.bizconfmobile.model.ConfAccount;
import com.sktlab.bizconfmobile.model.Participant;
import com.sktlab.bizconfmobile.model.manager.ContactManager;
import com.sktlab.bizconfmobile.util.Util;

public class PartyAttrRevHanlder extends RequestHandler {

	public static final String TAG = "PAHanlder";
	
	public static final String HOST_PORT = "Host Port";	
	public static final String GUEST_PORT = "Guest Port";
	public static final String PARTY_SPERATOR = "-";
	
	@Override
	public void handleRequest(String request) {
		
		//Util.BIZ_CONF_DEBUG(TAG, "PAHandler request: " + request);
		 /**
		  * 
		  *  RX: 69C~4~ACV.P.A~69F~2~0~0~2~0~~~~0~20130913165206000~~20130913165159000~0~~~~0~0
		  *  RX: 339~0~ACV.P.A~33A~1~0~wenjuan~018202932163~0~~~~~Shrine~0~1~0~0
		  *  RX: 339~0~ACV.P.A~33A~1~1~wenjuan~111#17969018202932163~33B~~~~~Shrine~0~1~0~0
		  */
		if (request.contains(MinaUtil.SEPARATOR + MinaUtil.MSG_ACV_P_A + MinaUtil.SEPARATOR)) {
			
			MinaMsg msg = new MinaMsg(request);
			
			String partyId = msg.getMsgData().get(0);
			
			String type = msg.getMsgData().get(1);
			
			do {
				//see api for more information
				if ("1".equals(type)){
					
					String isModerator = msg.getMsgData().get(12);
					String initialized = msg.getMsgData().get(2);
								
					if (!("1".equals(initialized))) {
						
						break;
					}
					
					String name = msg.getMsgData().get(3);
					String phoneNum = msg.getMsgData().get(4);
					
					Participant party = commManager.getPartyById(partyId);
						
					//Util.BIZ_CONF_DEBUG(TAG, "dialoutNum: " + dialOutNum + "phoneNumber: " + phoneNumber);
			
					if (null == party ) {
						
						ConfAccount activeAccount 
								= commManager.getActiveAccount();
		
						String dialOutNum = activeAccount.getDialOutNumber().replace("+", "00");
				
						if (dialOutNum.contains(phoneNum) || phoneNum.contains(dialOutNum)) {
							
							break;
						}
						
						Util.BIZ_CONF_DEBUG(TAG, "hi boy, here add a new party~");
						
						party = new Participant();
						
						party.setIdInConference(partyId);
						
						commManager.putParty(party);
					}
					
					if (Util.isEmpty(party.getName())) {
						
						String partyName = ContactManager
											.getContactNameByPhoneNumber(phoneNum);
						
						if (!partyName.endsWith(phoneNum)) {
							
							name = partyName;
						}
							
						party.setName(name);
					
					}
 					
					if (name.contains(GUEST_PORT)) {		
						
						do {
							
							//Util.BIZ_CONF_DEBUG(TAG, "guest partyId: " + partyId + "phoneNum: " + phoneNum);
							
							if (Util.isEmpty(phoneNum)) {
								
								break;
							}
							
							String [] tokens = phoneNum.split(PARTY_SPERATOR);
							
							if (tokens.length < 1) {
								
								party.setName(phoneNum);
								break;
							}
							
							party.setName(tokens[tokens.length - 1]);
							
							//Util.BIZ_CONF_DEBUG(TAG, "guest name: " +tokens[tokens.length - 1]);												
							commManager.notifyPartyChanged();
						}while(false);
						
					}
					
					party.setPhone(phoneNum);
					
					if (!"1".equals(isModerator)) {
						
						break;
					}
					
					party.setIsModerator(true);
					
					String moderatorName = AppClass.getInstance().getResources()
							.getString(R.string.user_moderator_name);
					
					//Util.BIZ_CONF_DEBUG(TAG, "set moderatorName, party Id:" + partyId);
					
					party.setName(moderatorName);
					
					ConfAccount account = 
							commManager.getActiveAccount();
										
					Participant currentUser = 
							contactManager.getCurrentUserObject();
					
					do{
						
						if (commManager.isTransferingPhone()) {
							
							break;
						}
						
						if (Util.isEmpty(account.getModeratorPw())) {
							
							break;
						}
						
						if (partyId.equals(currentUser.getIdInConference())) {
							//set phone here
							currentUser.setPhone(phoneNum);
							break;
						}
						
						if (!Util.isEmpty(currentUser.getIdInConference())) {
							
							Util.BIZ_CONF_DEBUG(TAG, "hi guy, here disconnect the current user");
							
							confControl.disconnectParty(currentUser
										.getIdInConference());
							commManager.removeParty(
										currentUser.getIdInConference());
							
							commManager.notifyPartyChanged();
						}
						
						 Util.BIZ_CONF_DEBUG(TAG, "hi boy, here set the current user information");
						 
						//currentUser.setName(name);
						currentUser.setName(moderatorName);
						currentUser.setPhone(phoneNum);
						currentUser.setIdInConference(partyId);

						// set web operator
						ConfControl.getInstance().moderatorOutCall(currentUser);
						
					}while(false);
								
					commManager.notifyPartyChanged();
				}
				
				if (!"2".equals(type)) {
					
					break;
				}
				
				//String location = msg.getMsgData().get(2);				
				String connectedState = msg.getMsgData().get(3);
				String disconnectReason = msg.getMsgData().get(4);
				
				//disconnect reason "2" is remote hang up, see api doc
				if ("0".equals(connectedState) && "2".equals(disconnectReason)) {
					
					commManager.removeParty(partyId);
					confControl.disconnectParty(partyId);
					Util.BIZ_CONF_DEBUG(TAG, "remote disconnect party id:" + partyId);
					commManager.notifyPartyChanged();
				}
				
				int state = Integer.valueOf(connectedState);
						
				if (state > 3) {
					
					Participant party = commManager.getPartyById(partyId);
					
					if (null != party) {
														
						if (state == 4) {
							
							party.setMuted(true);
						} else {
							
							party.setMuted(false);
						}
						
						commManager.notifyPartyChanged();
					}
					
					Participant destinateParty = contactManager.getDestinateParty();					
					
					if (null == destinateParty) {
						
						//Util.BIZ_CONF_DEBUG(TAG, "e,sorry, destinateParty is null ");						
						break;
					}
					
					String desPartyId = destinateParty.getIdInConference();
					
					//Util.BIZ_CONF_DEBUG(TAG, "desPartyId: " + desPartyId + " Phone number:" + destinateParty.getPhone());
					
					//Util.BIZ_CONF_DEBUG(TAG, "now add party id: " + partyId);
					
					if (!Util.isEmpty(desPartyId)
							&& partyId.equalsIgnoreCase(desPartyId)) {

						commManager.doTransfer(destinateParty.getPhone());
					}			
				}
			}while(false);	
					
		}else if(!Util.isEmpty(successor)){
			
			this.successor.handleRequest(request);
		}
	}
}
