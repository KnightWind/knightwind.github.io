package com.sktlab.bizconfmobile.receiver;

import com.sktlab.bizconfmobile.util.Util;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class PhoneStatReceiver extends BroadcastReceiver {

	public static final String TAG = "PhoneStatReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
			//Util.BIZ_CONF_DEBUG(TAG, "呼出……OUTING");
		}
		
		if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
			
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Service.TELEPHONY_SERVICE);
			
			switch (tm.getCallState()) {
			
			case TelephonyManager.CALL_STATE_RINGING:
				//Util.BIZ_CONF_DEBUG(TAG, "电话状态……RINGING");
				break;
				
			case TelephonyManager.CALL_STATE_OFFHOOK:
				//Util.BIZ_CONF_DEBUG(TAG, "电话状态……OFFHOOK");
				break;
				
			case TelephonyManager.CALL_STATE_IDLE:
				//Util.BIZ_CONF_DEBUG(TAG, "电话状态……IDLE");
				break;
			}
		}

	}

}
