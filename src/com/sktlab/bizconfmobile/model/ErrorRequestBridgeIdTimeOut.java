package com.sktlab.bizconfmobile.model;

import java.util.List;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.activity.AppClass;
import com.sktlab.bizconfmobile.interfaces.DataLoader;
import com.sktlab.bizconfmobile.util.Util;

public class ErrorRequestBridgeIdTimeOut implements DataLoader {

	@Override
	public List getLoadedData() {
		
		return null;
	}

	@Override
	public void showMsg() {
		
		Util.shortToast(AppClass.getInstance(), R.string.toast_request_bridge_time_out);
	}

}
