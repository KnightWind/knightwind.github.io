package com.sktlab.bizconfmobile.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sktlab.bizconfmobile.model.manager.CommunicationManager;
import com.sktlab.bizconfmobile.util.Util;

public class NetworkReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		boolean isNetworkReady = Util.isNetworkReadyForConf(context);
		
		if (isNetworkReady) {
			
			CommunicationManager.getInstance().notifyNetWorkReady();
		}
		
		
	}

}
