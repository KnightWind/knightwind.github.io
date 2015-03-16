package com.sktlab.bizconfmobile.mina;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.activity.AppClass;
import com.sktlab.bizconfmobile.model.ConfControl;
import com.sktlab.bizconfmobile.model.ConfAccount;
import com.sktlab.bizconfmobile.model.Participant;
import com.sktlab.bizconfmobile.model.manager.CommunicationManager;
import com.sktlab.bizconfmobile.model.manager.ContactManager;
import com.sktlab.bizconfmobile.model.requesthandler.PartyAttrRevHanlder;
import com.sktlab.bizconfmobile.util.Util;

/**
 * 
 * @author wenjuan.li
 *
 */
public class MinaPListMsg extends MinaMsg {
	

	public MinaPListMsg() {
		
		super();
	}
	
	public MinaPListMsg(String rcv) {
		
		super(rcv);
		
		generateParty();
	}
	
	/**
	 * 
	 * RX: 350~0~ACV.PL~1~1~2~
	 * 34B~wem~
	 * 34C~luo
	 */
	private void generateParty() {
		

		do {

			if (index < 6) {

				break;
			}
			
			for (int i = 6;i < index;i++) {
				
				int confIndex = i % 2;
				
				Participant party = null;
				
				if (confIndex == 0) {
				
					party = new Participant();
					
					party.setIdInConference(tokens[i]);
					CommunicationManager.getInstance().putParty(party);					
					//Util.BIZ_CONF_DEBUG(TAG, "add active party ID:" + tokens[i]);
				}else {
					
					party = CommunicationManager.getInstance().getActiveParties().get(tokens[i -1]);
					
					if(!Util.isEmpty(party)) {
						
						String partyName = tokens[i];
						//party.setName(partyName);		
						
						party.setName(ContactManager.getContactNameByPhoneNumber(partyName));	
						//party.setPhone(phoneNumber);
						
						//Util.BIZ_CONF_DEBUG(TAG, "add active party name:" + tokens[i]);
						
						if (partyName.contains(PartyAttrRevHanlder.HOST_PORT)) {
							
							setCurrentUserToModerator(party);
						}
					}
				}								
			}				
		} while (false);

	}
	
	private void setCurrentUserToModerator(Participant party) {
		
		ConfAccount account = 
				CommunicationManager.getInstance().getActiveAccount();
			
		Participant currentUser = 
				ContactManager.getInstance().getCurrentUserObject();
		
		do{
			
			if (Util.isEmpty(account.getModeratorPw())) {
				
				break;
			}
			
			if (party.getIdInConference().equals(currentUser.getIdInConference())) {
				
				break;
			}
			
			if (!Util.isEmpty(currentUser.getIdInConference())) {

				ConfControl.getInstance().disconnectParty(currentUser.getIdInConference());
				CommunicationManager.getInstance().removeParty(currentUser.getIdInConference());
				CommunicationManager.getInstance().notifyPartyChanged();
			}
			
			// Util.BIZ_CONF_DEBUG(TAG, "hi boy, active party list set the current user to moderator");
			 
			 String moderatorName = AppClass.getInstance().getResources()
						.getString(R.string.user_moderator_name);
			 
			//currentUser.setName(name);
			currentUser.setName(moderatorName);
			currentUser.setIdInConference(party.getIdInConference());

			// set web operator
			ConfControl.getInstance().moderatorOutCall(currentUser);			
		}while(false);
	}
	
}
