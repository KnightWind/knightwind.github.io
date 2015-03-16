package com.sktlab.bizconfmobile.net;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.activity.AppClass;
import com.sktlab.bizconfmobile.model.BridgeInfo;
import com.sktlab.bizconfmobile.model.ConfAccount;
import com.sktlab.bizconfmobile.model.Constant;
import com.sktlab.bizconfmobile.model.manager.AccountsManager;
import com.sktlab.bizconfmobile.util.MD5Util;
import com.sktlab.bizconfmobile.util.Util;

public class NetOp {
	
	public static final String TAG = "Net";
	
	public static boolean downloadAccount(){
		
		boolean isNoAccountBind = false;
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		
		String phoneNumber = Util.getSPString(AppClass.getInstance(),
				Constant.KEY_SP_VERIFIED_PHONE_NUM, "");
		
		params.add(new BasicNameValuePair("type", "teList"));
		params.add(new BasicNameValuePair("phoneNumber", phoneNumber));
		params.add(new BasicNameValuePair("ciphertext", MD5Util
				.Md5(phoneNumber + "bizconf")));
		
		HttpTools httpTools = new HttpTools(AppClass.getInstance());

		String rsp = httpTools.doPost(Constant.URL_VERIFY_PHONE_NUMBER,
				params);

		//Util.BIZ_CONF_DEBUG(TAG, "download account rsp: " + rsp);
		
		String statusCode = "";
		
		ArrayList<ConfAccount> downloadAccount 
					= new ArrayList<ConfAccount>();
		
		try {

			JSONObject jsonObj = new JSONObject(rsp);
			
			statusCode = jsonObj.getString("code");
			
			do {
				
				//Util.BIZ_CONF_DEBUG(TAG, "check phone number code " + statusCode);				
				if (StatusCode.NO_ACCOUNT_BINDING.contentEquals(statusCode) ) {
					
					//Util.shortToast(AppClass.getInstance(), R.string.toast_no_bind_account);
					isNoAccountBind = true;
					break;
				}
				
				JSONArray accountList = jsonObj.getJSONArray("list");
				
				int count = accountList.length();
				
				for(int i = 0; i < count; i++) {
					
					JSONObject obj = (JSONObject)accountList.get(i);
					
					String accessNumber = obj.getString("accessnumber");
					String confCode = obj.getString("meetingnumber");					
					String moderatorPw = obj.getString("hostpassword");
					
					accessNumber = accessNumber.replace(" ", "");
					confCode = confCode.replace(" ", "");
					moderatorPw = moderatorPw.replace(" ", "");
					
					if (accessNumber.startsWith(Constant.CHINA_COUNTRY_CODE)) {
						
						accessNumber = "+" + accessNumber;
					}
					
					Util.BIZ_CONF_DEBUG(TAG, "accessNumber: " + accessNumber);
					
					ConfAccount account = new ConfAccount();
					account.setAccessNumber(accessNumber);
					account.setConfCode(confCode);
					account.setModeratorPw(moderatorPw);
					
					AccountsManager.getInstance().addAccount(account);
					downloadAccount.add(account);
				}
				
				//insert download account to database
				AccountsManager.getInstance().insertAccountToDb(downloadAccount);
				
			}while(false);
			
		} catch (JSONException e) {

			e.printStackTrace();
		}
		
		return isNoAccountBind;
	}
	
	/**
	 * 
	 * @param verifyPhoneNum the number had been verified
	 * @param inputCode the check code user input
	 * @return status code 100 is success, others see the api doc
	 */
    public static String checkInputCode(String verifyPhoneNum, String inputCode) {
    	
    	List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("type", "telCheckCode"));
		params.add(new BasicNameValuePair("phoneNumber", verifyPhoneNum));
		params.add(new BasicNameValuePair("ciphertext", MD5Util
				.Md5(verifyPhoneNum + "bizconf")));
		params.add(new BasicNameValuePair("verifyCode", inputCode));
		HttpTools httpTools = new HttpTools(AppClass.getInstance());

		String rsp = httpTools.doPost(Constant.URL_VERIFY_PHONE_NUMBER,
				params);

		Util.BIZ_CONF_DEBUG(TAG, "rsp: " + rsp);
		
		String statusCode = "";
		
		try {

			JSONObject jsonObj = new JSONObject(rsp);

			statusCode = jsonObj.getString("code");

			Util.BIZ_CONF_DEBUG(TAG, "check phone number code " + statusCode);
		} catch (JSONException e) {

			e.printStackTrace();
		}
		
		return statusCode;
    }
    
    /**
     * 
     * @param phoneNumber the number to verify
     * @return status code 100 is success, others see the api doc
     */
    public static String sendNumberToVerify(String phoneNumber) {
    	
    	List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("type", "telVerifyCode"));
		params.add(new BasicNameValuePair("phoneNumber", phoneNumber));
		params.add(new BasicNameValuePair("ciphertext", MD5Util
				.Md5(phoneNumber + "bizconf")));

		HttpTools httpTools = new HttpTools(AppClass.getInstance());

		String rsp = httpTools.doPost(Constant.URL_VERIFY_PHONE_NUMBER,
				params);

		Util.BIZ_CONF_DEBUG(TAG, "check phone number rsp: " + rsp);
		
		String statusCode = "";
		
		do {
			
			if (Util.isEmpty(rsp)) {
				
				break;
			}
			
			try {

				JSONObject jsonObj = new JSONObject(rsp);
				statusCode = jsonObj.getString("code");

				//Util.BIZ_CONF_DEBUG(TAG, "status code: " + statusCode);
			} catch (JSONException e) {

				e.printStackTrace();
			}
		}while(false);
		
		return statusCode;
    }  
    
    public static int requestBridgeID(String confCode) {
    	
    	Util.BIZ_CONF_DEBUG(TAG,"send conf code:" + confCode);
    	
    	int bridgeId = Constant.ERR_REQUEST_BRIDGE_ID_TIME_OUT;
    	
    	List<NameValuePair> params = new ArrayList<NameValuePair>();
    	
		params.add(new BasicNameValuePair("meetingNumber", confCode));

		HttpTools httpTools = new HttpTools(AppClass.getInstance());

		String rsp = httpTools.doPost(Constant.BRIDGE_NUM_URL,params);

		Util.BIZ_CONF_DEBUG(TAG, "request bridge info rsp: " + rsp);
		
		if (!Util.isEmpty(rsp)) {
			
			bridgeId = parseBridgeIdFromJsonRsp(rsp);
		}
					 	
    	return bridgeId;
    }
    
	private static int parseBridgeIdFromJsonRsp(String rsp) {
		
		int bridgeId = Constant.ERR_REQUEST_BRIDGE_ID_TIME_OUT;
		
		try {

			JSONObject jsonObj = new JSONObject(rsp);	
			
			String status = jsonObj.getString("status");
			
			if(status.equalsIgnoreCase("fail")) {
				
				bridgeId = Constant.ERR_NO_CONF_CODE_RECORD;
			}else if (status.equalsIgnoreCase("success")) {
				
				String meetingNumber = jsonObj.getString("meetingnumber");
				
				String id = jsonObj.getString("bridge");			
				
				
				String type = jsonObj.getString("templatetype");
				
				String pwd = jsonObj.getString("doublepwd");
				/*
				 * for the last developer's code is  bad,
				 * 	 so i set the type value to the BridgeInfo
				 * */
				
				Util.BIZ_CONF_DEBUG(TAG, "bridge Id: " + id);		
				Util.BIZ_CONF_DEBUG(TAG, "templatetype: " + type);
				bridgeId = Integer.valueOf(id);
				
				BridgeInfo.templateType = Integer.valueOf(type);
				BridgeInfo.doublePwd = Integer.valueOf(pwd);
				
				//add "1" "3" is only to build new sharedPreference,beacause it has buffer in last version
				Util.setSpInt(AppClass.getInstance(),meetingNumber+"1", bridgeId);
				Util.setSpInt(AppClass.getInstance(),meetingNumber+"3", BridgeInfo.templateType);
				Util.setSpInt(AppClass.getInstance(),meetingNumber+"4", BridgeInfo.doublePwd);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return bridgeId;
	}
	
    public static String requestAccessNumber(String confCode, String language) {
    	
    	Util.BIZ_CONF_DEBUG(TAG,"request access number:" + confCode);
    	
    	List<NameValuePair> params = new ArrayList<NameValuePair>();
    	
		params.add(new BasicNameValuePair("meetingNumber", confCode));
		params.add(new BasicNameValuePair("language", language));
		
		HttpTools httpTools = new HttpTools(AppClass.getInstance());

		String rsp = httpTools.doPost(Constant.ACCESS_NUMBER_URL,params);

		Util.BIZ_CONF_DEBUG(TAG, "request bridge info rsp: " + rsp);
	 	
    	return rsp;
    }
    
    public static String requestEmailTemplet(String confCode, String language) {
    	
    	Util.BIZ_CONF_DEBUG(TAG,"request email templet:" + confCode);
    	
    	List<NameValuePair> params = new ArrayList<NameValuePair>();
    	
		params.add(new BasicNameValuePair("meetingNumber", confCode));
		params.add(new BasicNameValuePair("language", language));
		
		HttpTools httpTools = new HttpTools(AppClass.getInstance());

		String rsp = httpTools.doPost(Constant.EMAIL_TEMPLET_URL,params);

		Util.BIZ_CONF_DEBUG(TAG, "request email templet rsp: " + rsp);
	 	
    	return rsp;
    }
    
    public static String requestLocationCode(Location location) {
    	
    	String rsp = "";
    	
    	List<NameValuePair> params = new ArrayList<NameValuePair>();
    	
    	String latLng = location.getLatitude() + "," + location.getLongitude();
    	
		params.add(new BasicNameValuePair("latlng", latLng));
		params.add(new BasicNameValuePair("language", "zh-CN"));
		params.add(new BasicNameValuePair("sensor", "true"));
		
		HttpTools httpTools = new HttpTools(AppClass.getInstance());
		
		String googleMapApiUrl = "http://maps.google.com/maps/api/geocode/json";
		
    	rsp = httpTools.doPost(googleMapApiUrl, params);
    	
    	return rsp;
    }
}
