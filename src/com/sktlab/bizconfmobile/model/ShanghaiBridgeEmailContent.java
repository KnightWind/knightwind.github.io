package com.sktlab.bizconfmobile.model;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.activity.AppClass;

public class ShanghaiBridgeEmailContent extends EmailContent{

	public ShanghaiBridgeEmailContent(AppointmentConf appointmentConf) {
		super(appointmentConf);
		
		globalAccessNumCH = "+86 21 6026 4000";
		globalAccessNumEN = "";
		local400AccessNumCH = "400 062 8686";
		local400AccessNumEN = "400 001 1122";
		local800AccessNumCH = "800 870 8686";
		local800AccessNumEN = "800 870 1122";
		accessNumListUrl = "http://online.bizconf.cn/accessNumber/1-1.htm";
		suffixOfCommandToast = AppClass.getInstance().getResources()
					.getString(R.string.shanghai_toast_suffix_of_command);
		serviceCommand = AppClass.getInstance().getResources()
					.getString(R.string.shanghai_service_command);
	}

}
