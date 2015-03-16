package com.sktlab.bizconfmobile.model;

import org.apache.mina.core.session.IoSession;

import com.sktlab.bizconfmobile.mina.MinaMsg;
import com.sktlab.bizconfmobile.mina.MinaUtil;
import com.sktlab.bizconfmobile.model.manager.OngoingConfManager;

public class BvSession extends BaseBusinessSession {
	
	/**
	 * Request a list of active conferences in current bridge This method just
	 * send a request message to server,the List data should be handle in
	 * ClientMinaHandler's msg receive method. for further use, the list should
	 * be stored in ConferenceManager
	 */
	public void requestActiveConfList(IoSession session) {

//		MinaMsg msg = new MinaMsg();
//
//		msg.clearMsgData();
//		msg.setSsnId(getId());
//		msg.setSeq(getSeqNum());
//		msg.setMsgId(MinaUtil.MSG_ACTIVE_CONF_LIST);
//
//		session.write(msg.toString());
		
		sendMsg(session, 
				MinaUtil.MSG_ACTIVE_CONF_LIST);
	}
	
	public void requestLiveConfList(IoSession session) {
		
		sendMsg(session, 
				MinaUtil.MSG_BV_B_APL,
				LSSession.bridgeNumber);
	}
}
