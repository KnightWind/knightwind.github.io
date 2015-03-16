package com.sktlab.bizconfmobile.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sktlab.bizconfmobile.model.AccessNumber;
import com.sktlab.bizconfmobile.model.Constant;
import com.sktlab.bizconfmobile.net.NetOp;

public class AccessNumNetParser {

	private String confCode;
	
	public AccessNumNetParser(String confCode) throws IOException {
		
		this.confCode = confCode;
	}
	
	public List<AccessNumber> parse() {
				
		String paramLang = getLanguage();
		
		String rsp = NetOp.requestAccessNumber(confCode, paramLang);
		
		List<AccessNumber> numbers = parseResponse(rsp);
		
		return numbers;
	}
	
	public List<AccessNumber> parseResponse(String rsp) {
		
		List<AccessNumber> numbers = new ArrayList<AccessNumber>();
		
		try {
			
			JSONObject jsonObj = new JSONObject(rsp);
			
			JSONArray accessArray = jsonObj.getJSONArray("data");
			
			for (int i = 0; i < accessArray.length(); i++) {  
				
			    JSONObject temp = (JSONObject) accessArray.get(i);
			    String countrycode = temp.getString("countrycode");
			    String contryName = temp.getString("countryname");
			    String number = temp.getString("number").replace(" ", "");  
			    String info = temp.getString("info");
			    
			    AccessNumber accessNumber = new AccessNumber();
			    accessNumber.setCountrycode(countrycode);
			    accessNumber.setCountry(contryName);
			    accessNumber.setNumber(number);
			    accessNumber.setNumberType(info);
			    
			    numbers.add(accessNumber);
			}  
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return numbers;
	}
	
	public static String getLanguage() {
		
		Locale locale = Locale.getDefault();
		String language = String.format("%s_%s", 
								locale.getLanguage().toLowerCase(), 
								locale.getCountry().toLowerCase());
		
		String paramLanguage = Constant.SERVER_LANGUAGE_ZH;
		
		if (language.equalsIgnoreCase(Constant.LOCAL_EN)) {

			paramLanguage = Constant.SERVER_LANGUAGE_EN;
		} 
		
		return paramLanguage;
	}
}
