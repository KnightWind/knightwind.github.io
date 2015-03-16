package com.sktlab.bizconfmobile.mina;

import com.sktlab.bizconfmobile.model.OngoingConf;
import com.sktlab.bizconfmobile.model.ConferenceAttr;
import com.sktlab.bizconfmobile.model.manager.CommunicationManager;
import com.sktlab.bizconfmobile.util.Util;

public class MinaAPLMsg extends MinaMsg {
	
	public MinaAPLMsg() {
		
		super();
	}
	
	public MinaAPLMsg(String rcv) {
		
		super(rcv);
		
		generateConf();
	}
	
	private int getLiveConfCount() {
		
		int count = -1;
		
		try {
			
			count = Integer.parseInt(getMsgData(3));
		} catch (Exception e) {
			
			Util.BIZ_CONF_DEBUG(TAG, "number format exception for live conference count");
		}
		
		return count;
	}
	/**
	 * 
	 * 70A~1~BV.B.APL~1~1~1~7~708~5885~1311288471~11288471~~1383917604~1~6FA~6298~7978174543~78174543~~1383917507~1~4B8~1861~15110021861~10021861~~1383909190~1~10F~0155~4464736488~64736488~~1383648356~1~111~1111~4641903565~41903565~~1383729805~1~113~0357~5282521808~82521808~~1383729977~1~115~4464~4464736487~64736487~~1383766457~1
	 */
	private void generateConf() {

		do {
			
			int liveConfCount = getLiveConfCount();
						
			if (liveConfCount <= 0) {
				
				break;
			}
			
			int msgDataIndexOfLiveConfId = 4;
			int msgDataIndexOfLiveConfHostCode = 5;
			int msgDataIndexOfLiveConfGuestCode = 6;
			
			int msgInfoCountOfALiveConf = 7;
			
			for (int i = 0; i < liveConfCount; i++) {
				
				OngoingConf liveConf = new OngoingConf();
				ConferenceAttr attr = liveConf.getAttr();
				attr.setConfId(getMsgData(msgDataIndexOfLiveConfId));
				attr.setHostCode(getMsgData(msgDataIndexOfLiveConfHostCode));
				attr.setGuestCode(getMsgData(msgDataIndexOfLiveConfGuestCode));				
				
				CommunicationManager.getInstance().putLiveConf(liveConf);
				
				msgDataIndexOfLiveConfId += msgInfoCountOfALiveConf;
				msgDataIndexOfLiveConfHostCode += msgInfoCountOfALiveConf;
				msgDataIndexOfLiveConfGuestCode += msgInfoCountOfALiveConf;
			}
			
//			for (int i = 7;i < index;i++) {
//				
//				int confIndex = i % 7;
//				
//				Conference liveConf = null;
//				
//				switch (confIndex) {
//
//				case 0:
//					
//					liveConf = new Conference();
//					ConferenceAttr attr = liveConf.getAttr();
//					attr.setConfId(tokens[i+1]);
//					
//					//Util.BIZ_CONF_DEBUG(TAG, "conf name:" + tokens[i + 1]);			
//					CommunicationManager.getInstance().putLiveConf(liveConf);
//					break;
//					
//				case 1:				
//					
//					liveConf = 
//							CommunicationManager.getInstance().getLiveConfByKey(tokens[i - 1]);
//					
//					if (null != liveConf) {
//						
//						liveConf.getAttr().setHostCode(tokens[i]);
//					}
//					break;
//					
//				case 2:
//
//					liveConf = 
//							CommunicationManager.getInstance().getLiveConfByKey(tokens[i - 2]);
//					
//					if (null != liveConf) {
//						
//						liveConf.getAttr().setGuestCode(tokens[i]);
//					}
//					break;
//				}
//			}
				
		} while (false);

	}
}
