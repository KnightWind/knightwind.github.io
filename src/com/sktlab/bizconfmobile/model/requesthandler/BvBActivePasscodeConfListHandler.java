package com.sktlab.bizconfmobile.model.requesthandler;

import java.util.ArrayList;

import com.sktlab.bizconfmobile.mina.MinaACLMsg;
import com.sktlab.bizconfmobile.mina.MinaAPLMsg;
import com.sktlab.bizconfmobile.mina.MinaMsg;
import com.sktlab.bizconfmobile.mina.MinaUtil;
import com.sktlab.bizconfmobile.model.OngoingConf;
import com.sktlab.bizconfmobile.model.ConfAccount;
import com.sktlab.bizconfmobile.model.LSSession;
import com.sktlab.bizconfmobile.util.Util;

public class BvBActivePasscodeConfListHandler extends RequestHandler {

	public static final String TAG = "BVBLCLHandler";
	private  static boolean isSendAgain = false; //is  msg  a second msg
	
	public BvBActivePasscodeConfListHandler(){
		isSendAgain = false;
	}
	@Override
	public synchronized void handleRequest(String request) {
		
		/**
		 * 
		 * 
		 * 1E12~1~BV.B.AP.ADD~1~1E14~7530~5211100458~11100458~~1382551972~1
		 */
		if (request.contains(MinaUtil.MSG_BV_B_APL)) {
			
			Util.BIZ_CONF_DEBUG(TAG, "receive BV_B_APL msg");
			
			//for feature use
			MinaMsg msg = new MinaAPLMsg(request);
			
			boolean isVerifySuccess = false;
			
			do {
				
				String bridgeNumber = msg.getMsgData(0);
				
				if (!LSSession.bridgeNumber.equalsIgnoreCase(bridgeNumber)) {
					
					Util.BIZ_CONF_DEBUG(TAG, "bridgeNumber not equal: " + bridgeNumber);
					break;
				}
				
				ConfAccount activeAccount = commManager.getActiveAccount();
				
				if (null == activeAccount) {
					
					Util.BIZ_CONF_DEBUG(TAG, "active account is null ");
					break;
				}
								
				String userModeratorPw = activeAccount.getModeratorPw();
				String userConfCode = activeAccount.getConfCode();
				
				String rcvModeratorPw = "";
				String rcvConfCode = "";
				
				OngoingConf activeConf = confControl.getActiveConference();
				
				String activeConfId = activeConf.getAttr().getConfId();
				
				System.out.println("activeConfId:"+activeConfId);
				
				Util.BIZ_CONF_DEBUG(TAG, "active conference id: " + activeConfId);
				
				OngoingConf liveConf = commManager.getLiveConfByKey(activeConfId);
				
//				if(null == liveConf){
//					try {
//						Thread.sleep(5000);
//						liveConf = commManager.getLiveConfByKey(activeConfId);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
				
				if (null == liveConf) {
					
					Util.BIZ_CONF_DEBUG(TAG, "not get the live conference ");
					return;
				}
				
				rcvModeratorPw = liveConf.getAttr().getHostCode();
				rcvConfCode = liveConf.getAttr().getGuestCode();
				
				if (!userConfCode.equalsIgnoreCase(rcvConfCode)) {
					
					Util.BIZ_CONF_DEBUG(TAG, "confcode not right");
					break;
				}
				
				if (commManager.isModeratorAccount()
						&& !userModeratorPw.equalsIgnoreCase(rcvModeratorPw)) {
					Util.BIZ_CONF_DEBUG(TAG, "userModeratorPw:"+userModeratorPw+" rcvModePw"+rcvModeratorPw);
					Util.BIZ_CONF_DEBUG(TAG, "moderator password not right");
					break;
				}
				
				isVerifySuccess = true;
				confControl.createACVSession();
				isSendAgain = true;
				
			}while(false);
			
			if (!isVerifySuccess) {
				
				Util.BIZ_CONF_DEBUG(TAG, "sorry conference code or password not verify success");
				confControl.setServerLinkReady(false);
//				String forError = "null";				
//				Integer error = Integer.valueOf(forError);
			}		
		}else if(!Util.isEmpty(successor)){
			
			this.successor.handleRequest(request);
		}
	}

}
