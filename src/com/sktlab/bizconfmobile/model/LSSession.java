package com.sktlab.bizconfmobile.model;

import org.apache.mina.core.session.IoSession;

import com.sktlab.bizconfmobile.mina.MinaMsg;
import com.sktlab.bizconfmobile.mina.MinaUtil;

public class LSSession extends BaseBusinessSession {
	
	//Bridge number 
	public static String bridgeNumber = "1";
	
	public LSSession() {
		
	}
	
	public void createSession(IoSession session, String sessionType) {
		
		sendMsg(session, 
					MinaUtil.MSG_LS_CS,
					sessionType);
	}
	
	public void createACVSession(IoSession session, String confId) {
		
		sendMsg(session, 
				MinaUtil.MSG_LS_CS,
				MinaUtil.MSG_ACV,
				bridgeNumber,
				confId);
	}
	
	public void closeSession(IoSession session, BaseBusinessSession busySession) {
		
		do {
			
			if (!isCreated()) {
				
				break;
			}
			
			if (!busySession.isCreated()) {
				
				break;
			}
			
			if (session.isClosing()){
				
				break;
			}
			
			sendMsg(session, 
					MinaUtil.MSG_LS_DS,
					busySession.getId());
			
		}while(false);
	}
}
