package com.sktlab.bizconfmobile.model.factory;

import com.sktlab.bizconfmobile.model.AppointmentConf;
import com.sktlab.bizconfmobile.model.BeijingBridgeEmailContent;
import com.sktlab.bizconfmobile.model.Constant;
import com.sktlab.bizconfmobile.model.EmailContent;
import com.sktlab.bizconfmobile.model.ShanghaiBridgeEmailContent;

public class EmailContentFactory {

	public static EmailContent createEmailContent(AppointmentConf appointConf, int bridge) {
		
		EmailContent emailContent = null;
		
		switch (bridge) {
		case Constant.SHANG_HAI_BRIDGE:
			
			emailContent = new ShanghaiBridgeEmailContent(appointConf);
			break;
		case Constant.BEI_JING_BRIDGE:
			
			emailContent = new BeijingBridgeEmailContent(appointConf);
			break;
		default:
			
			emailContent = new EmailContent(appointConf);
			break;
		}
		return emailContent;
	}
}
