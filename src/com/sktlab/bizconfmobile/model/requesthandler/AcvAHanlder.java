package com.sktlab.bizconfmobile.model.requesthandler;

import com.sktlab.bizconfmobile.mina.MinaMsg;
import com.sktlab.bizconfmobile.mina.MinaUtil;
import com.sktlab.bizconfmobile.model.AccSession;
import com.sktlab.bizconfmobile.model.AcvSession;
import com.sktlab.bizconfmobile.model.ConfControl;
import com.sktlab.bizconfmobile.model.ConfAccount;
import com.sktlab.bizconfmobile.model.manager.CommunicationManager;
import com.sktlab.bizconfmobile.util.Util;

public class AcvAHanlder extends RequestHandler {

	public static final String TAG = "ACVAHanlder";
	
	@Override
	public void handleRequest(String request) {

		//Util.BIZ_CONF_DEBUG(TAG, "receive msg from server: " + request);
		//RX: 339~0~ACV.A~2~1~0~0~0~0:0~0~1~1~0~0~0~0~1~0~0~0~~0~1~0~1~0~0~0~~0~~0~0~~~1~60923631709195~16~0~~T~T~~~0~1~1~0
		//24 25
		//AllowConfLevelPasscode ConfLevelPasscode
		if (request.contains(MinaUtil.SEPARATOR + MinaUtil.MSG_ACV_A + MinaUtil.SEPARATOR)) {
			
			MinaMsg msg = new MinaMsg(request);
			
			String type = msg.getMsgData().get(0);
			
			do {
				
				if (!type.equals("2")) {
					
					break;
				}
				
				String state = msg.getMsgData().get(1);
				
				if (!state.equals("1")) {
					
					break;
				}
				
				//String allowConfLevelPasscode = msg.getMsgData().get(24);
				
//				String callFlow = msg.getMsgData(33);
//				
//				Util.BIZ_CONF_DEBUG(TAG, "callFlow:" + callFlow);
//				
//				ConferenceAccount account = 
//						commManager.getActiveAccount();
//						
//				String confCode = account.getConfCode();
//					
//				String moderatorPw = account.getModeratorPw();
//								
//				if (!commManager.isModeratorAccount() 
//						&& !Util.isEmpty(callFlow) 
//						&& callFlow.length() > confCode.length()) {
//										
//					moderatorPw = callFlow.substring(confCode.length());	
//					Util.BIZ_CONF_DEBUG(TAG, "guest user and parse its moderator passcode");
//				}
//				
//				String userInfo = confCode + moderatorPw;
//				Util.BIZ_CONF_DEBUG(TAG, "confCode:" + confCode);
//				Util.BIZ_CONF_DEBUG(TAG, "moderatorPw:" + moderatorPw);
//				
//				Util.BIZ_CONF_DEBUG(TAG, "userInfo:" + userInfo);
//				
//				if (!userInfo.equalsIgnoreCase(callFlow)){
//					
//					Util.BIZ_CONF_DEBUG(TAG, "ACV.A catch not match conf code and pw");
//					confControl.setServerLinkReady(false);
//					break;
//				}
//				
				ConfAccount account = 
						commManager.getActiveAccount();
						
				String securityCode = account.getSecurityCode();
				
				if (commManager.isModeratorAccount() 
						&& account.isSecurityCodeEnable()) {
					//not send security code
					//confControl.setSecurityCode(securityCode);
				}							
				
				confControl.requestPList();
			}while(false);

		}else if(!Util.isEmpty(successor)){
			
			this.successor.handleRequest(request);
		}
	}
}
