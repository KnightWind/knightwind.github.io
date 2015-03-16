package com.sktlab.bizconfmobile.model.requesthandler;

import com.sktlab.bizconfmobile.mina.MinaMsg;
import com.sktlab.bizconfmobile.mina.MinaUtil;
import com.sktlab.bizconfmobile.model.ConfAccount;
import com.sktlab.bizconfmobile.util.Util;

public class BvBApAddedHandler extends RequestHandler {
	
	public static final String TAG = "BVBApAddHandler";
	
	@Override
	public void handleRequest(String request) {

		/**
		 * 
		 * 
		 * 1E12~1~BV.B.AP.ADD~1~1E14~7530~5211100458~11100458~~1382551972~1
		 */
		if (request.contains(MinaUtil.MSG_BV_B_AP_ADD)) {
			
			//for feature use
//			MinaMsg msg = new MinaMsg(request);
//			
//			ConferenceAccount activeAccount = commManager.getActiveAccount();
//			
//			do {
//				
//				if (null == activeAccount) {
//					
//					break;
//				}
//				
//				if (!commManager.isModeratorAccount()) {
//					
//					break;
//				}
//				
//				String rcvModeratorPw = msg.getMsgData(2);
//				String rcvConfCode = msg.getMsgData(3);
//				
//				String userModeratorPw = activeAccount.getModeratorPw();
//				String userConfCode = activeAccount.getConfCode();
//				
//				if (userConfCode.equalsIgnoreCase(rcvConfCode)
//						&& !userModeratorPw.equalsIgnoreCase(rcvModeratorPw)) {
//					
//					Util.BIZ_CONF_DEBUG(TAG, "BV.B.AP.ADD send failed msg");
//					confControl.setServerLinkReady(false);
//				}
//			}while(false);

		}else if(!Util.isEmpty(successor)){
			
			this.successor.handleRequest(request);
		}
	}

}
