package com.sktlab.bizconfmobile.parser;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.lewis.util.Util;
import com.lurencun.service.autoupdate.ResponseParser;
import com.lurencun.service.autoupdate.Version;
import com.sktlab.bizconfmobile.util.DateUtil;

/**
 * App should impliment this interface for custom JSON datas
 * @author wenjuan.li
 *
 */
public class VersionUpdateJSONParser implements ResponseParser{
	
	public static final String TAG = "SimpleJSONParser";
	
    @Override
    public Version parser(String response) {
        Version version = null;
       // if (in != null && in.startsWith("\ufeff")) { in = in.substring(1);
        if(response.startsWith("\ufeff")){
        	response = response.substring(1);
        }
    
        try{
        	Util.debug(TAG, "update version response: " + response);
        	
        	JSONObject versionObject = new JSONObject(response);
       	 	String status = versionObject.getString("status");
            
            if (status.equalsIgnoreCase("success")) {
            	
            	String versionName = versionObject.getString("AndroidVersion");
            	
            	String[] versionCodes = versionName.split("[.]");
            	System.out.println(versionCodes.length);
            	int versionCode =(Integer.parseInt(versionCodes[0]))*10000+Integer.parseInt(versionCodes[1])*100+Integer.parseInt(versionCodes[2]);
            	//int versionCode = Integer.parseInt(versionName.substring(versionName.lastIndexOf(".") + 1));
                String downloadUrl = versionObject.getString("AndroidAddress");
            	
                String feature = versionObject.getString("updateInfo");
                
                Util.debug(TAG, "versionCode: " + versionCode + "downloadUrl: " + downloadUrl);
                
                String updateTime = DateUtil.getFormatString(new Date(), DateUtil.YY_MM_DD_HH_MM_SS);
                
                String appName = "BizConfMobile.apk";
                version = new Version(versionCode,versionName, appName, feature,downloadUrl, updateTime);
            }
       	   	                  
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
			// TODO: handle exception
		}
        return version;
    }

}
