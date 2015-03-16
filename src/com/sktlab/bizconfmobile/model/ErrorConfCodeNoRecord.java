package com.sktlab.bizconfmobile.model;

import java.util.List;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.activity.AppClass;
import com.sktlab.bizconfmobile.interfaces.DataLoader;
import com.sktlab.bizconfmobile.util.Util;

public class ErrorConfCodeNoRecord implements DataLoader {

	public static final String TAG = "ErrorConfCodeNoRecord";
	
	@Override
	public List getLoadedData() {
		
		return null;
	}

	@Override
	public void showMsg() {
		
		Util.BIZ_CONF_DEBUG(TAG, "show error msg");		
		Util.shortToast(AppClass.getInstance(), R.string.toast_error_conf_code);
	}

}
