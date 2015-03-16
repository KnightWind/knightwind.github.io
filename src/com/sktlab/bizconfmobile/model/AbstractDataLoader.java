package com.sktlab.bizconfmobile.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import android.content.res.AssetManager;

import com.sktlab.bizconfmobile.activity.AppClass;
import com.sktlab.bizconfmobile.interfaces.DataLoader;
import com.sktlab.bizconfmobile.parser.AccessNumCSVParser;
import com.sktlab.bizconfmobile.parser.AccessNumNetParser;
import com.sktlab.bizconfmobile.util.Util;

public abstract class AbstractDataLoader implements DataLoader {

	protected String chineseSourceData = Constant.SHANG_HAI_PLIST_ACCESS_NUMBER_LIST_CH;
	protected String englishSourceData = Constant.SHANG_HAI_PLIST_ACCESS_NUMBER_LIST_EN;
	
	protected String csvFileNameInSdcard;
	
	@Override
	public List getLoadedData() {

		//List list = loadDataFromAssets();
		
		List list = loadDataFromSdcard();
		
		return list;
	}
	
	/**
	 * for 3.x version load from sdcard file
	 * @return
	 */
	public List loadDataFromSdcard() {
		
		List list = null;
		
		try {
			
			list = parseData(csvFileNameInSdcard);
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
				
		return list;
	}
	
	/**
	 * for version 1.x and 2.x load access number from assets folder
	 * @return
	 */
	public List loadDataFromAssets() {
		
		AssetManager am = AppClass.getInstance().getAssets();
		
		List list = null;
		
		if (!Util.isEmpty(am)) {
			
			InputStream in = null;
			
			try {
				
				//load file from assets folder
				Locale locale = Locale.getDefault();
				String language = String.format("%s_%s", 
										locale.getLanguage().toLowerCase(), 
										locale.getCountry().toLowerCase());		
				
				if (language.equalsIgnoreCase(Constant.LOCAL_ZH)) {

					in = am.open(chineseSourceData);
				} else {

					in = am.open(englishSourceData);
				}			
				
				list = parseData(in);
			
				if (null != in) {
					
					in.close();
				}				
			} catch (IOException e) {
				
				e.printStackTrace();
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
		
		return list;
	}
	
	public List parseData(InputStream in) throws Exception {
		
		AccessNumCSVParser parser = new AccessNumCSVParser(in);
		
		List list = parser.parse();
		
		return list;
	}
	
	public List parseData(String fileName) throws Exception {
		
		AccessNumCSVParser parser = new AccessNumCSVParser(fileName);
		
		List list = parser.parse();
		
		return list;
	}
	
	@Override
	public void showMsg() {
		// TODO Auto-generated method stub
	}	
}
