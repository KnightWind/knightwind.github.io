package com.sktlab.bizconfmobile.model;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.activity.AppClass;

public class BeijingBridgeEmailContent extends EmailContent{

	public BeijingBridgeEmailContent(AppointmentConf appointmentConf) {
		super(appointmentConf);
		
		globalAccessNumCH = "8610 5629 4500" + AppClass.getInstance().getResources().getString(R.string.global_access_number_speaker_ch);
		globalAccessNumEN = "8610 5629 4533" + AppClass.getInstance().getResources().getString(R.string.global_access_number_speaker_en);
		local400AccessNumCH = "400 066 8787";
		local400AccessNumEN = "400 096 1166";
		local800AccessNumCH = "800 870 8787";
		local800AccessNumEN = "800 870 1166";
		accessNumListUrl = "http://online.bizconf.cn/accessNumber/2-2.htm";
		suffixOfCommandToast = AppClass.getInstance().getResources()
					.getString(R.string.beijing_toast_suffix_of_command);
		serviceCommand = AppClass.getInstance().getResources()
					.getString(R.string.beijing_service_command);
	}

}
