package com.sktlab.bizconfmobile.model;

import com.sktlab.bizconfmobile.activity.AppClass;
import com.sktlab.bizconfmobile.net.NetOp;
import com.sktlab.bizconfmobile.util.Util;

public class BridgeInfo {
	public static int  doublePwd = -1;  
	public static int  templateType=0;
	private String confCode = "";
	private int bridgeId = Constant.ERR_NO_CONF_CODE_RECORD;
	
	public BridgeInfo(String confCode) {
		
		this.confCode = confCode;
		
		requestBridgeId();
	}
	
	private void requestBridgeId() {

		// Util.BIZ_CONF_DEBUG(TAG, "input conference code: " + conferenceCode);
		do {
			
			if (Util.isSpContainsKey(AppClass.getInstance(), confCode+"3")) {
				//+3 only make sure the key unique,do this because the local has buffer

				bridgeId = Util.getSPInt(AppClass.getInstance(),
						confCode+"1", Constant.BRIDGE_ID_NOT_IN_SP);
				templateType = Util.getSPInt(AppClass.getInstance(),
						confCode+"3", Constant.BRIDGE_ID_NOT_IN_SP);
				doublePwd = Util.getSPInt(AppClass.getInstance(),
						confCode+"4", Constant.BRIDGE_ID_NOT_IN_SP);
				break;
			}

			bridgeId = NetOp.requestBridgeID(confCode);
		} while (false);
	}
	
	public boolean isBridgeIdValid() {
		
		return bridgeId > 0;
	}
	
	public int getBridgeId() {
		
		return bridgeId;
	}
}
