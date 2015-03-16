package com.sktlab.bizconfmobile.model;

import org.apache.mina.core.session.IoSession;

import com.sktlab.bizconfmobile.mina.MinaUtil;
import com.sktlab.bizconfmobile.util.Util;

public class AcvSession extends BaseBusinessSession {
	
	public static final String TAG = "ACVSession";
	
	private String updateTalkerState = "TalkerUpdatesEnabled";
	
	private boolean isSetTalkerUpdate = false;
	private boolean isRequestPList = false;
	
	public void requestPList(IoSession session) {
		
		if (!isRequestPList) {
			
			isRequestPList = true;
			
			Util.BIZ_CONF_DEBUG(TAG, "isRequestPList: " + isRequestPList);
			
			sendMsg(session, 
					MinaUtil.MSG_ACV_P_LIST);
		}		
	}
	
	public void requestPartyAttr(IoSession session, String partyId) {
		
		sendMsg(session, 
				MinaUtil.MSG_ACV_P_A,
				partyId);
	}
	
	public void alterTalkersEnable(IoSession session, String talkerState) {
		
		if (!isSetTalkerUpdate) {
			
			isSetTalkerUpdate = true;
			sendMsg(session, 
					MinaUtil.MSG_ACV_SA_ALTER,
					updateTalkerState,
					talkerState);
		}
	}

	public boolean isRequestPartyList() {
		
		return isRequestPList;
	}
	
	@Override
	public void reset() {
		super.reset();
		isSetTalkerUpdate = false;
		isRequestPList = false;
	}
	
}
