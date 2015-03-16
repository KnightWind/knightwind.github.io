package com.sktlab.bizconfmobile.model.requesthandler;

import com.sktlab.bizconfmobile.mina.MinaMsg;
import com.sktlab.bizconfmobile.mina.MinaUtil;
import com.sktlab.bizconfmobile.util.Util;

public class AcvSaHanlder extends RequestHandler {

	public static final String TAG = "ACVSAHanlder";
	
	@Override
	public void handleRequest(String request) {

		//Util.BIZ_CONF_DEBUG(TAG, "receive msg from server: " + request);
		//RX: 339~0~ACV.A~2~1~0~0~0~0:0~0~1~1~0~0~0~0~1~0~0~0~~0~1~0~1~0~0~0~~0~~0~0~~~1~60923631709195~16~0~~T~T~~~0~1~1~0
		//24 25
		//AllowConfLevelPasscode ConfLevelPasscode
		if (request.contains(MinaUtil.MSG_ACV_SA)) {
			
			MinaMsg msg = new MinaMsg(request);
			
			String acvSsnId = msg.getSsnId();
			
			confControl.setACVId(acvSsnId);
			
			confControl.alterTalkersEnable(1);
			//confControl.requestPList();
		}else if(!Util.isEmpty(successor)){
			
			this.successor.handleRequest(request);
		}
	}
}
