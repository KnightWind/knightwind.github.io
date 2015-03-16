package com.sktlab.bizconfmobile.model;

import java.io.InputStream;
import java.util.List;

import com.sktlab.bizconfmobile.parser.XMLSAXParser;

public class ShanghaiPlistAccessDataLoader extends AbstractDataLoader {

	public ShanghaiPlistAccessDataLoader() {
		
		chineseSourceData = Constant.SHANG_HAI_PLIST_ACCESS_NUMBER_LIST_CH;
		englishSourceData = Constant.SHANG_HAI_PLIST_ACCESS_NUMBER_LIST_EN;
	}
	
	@Override
	public List parseData(InputStream in) throws Exception {
		
		XMLSAXParser parser =  new XMLSAXParser();
		
		List list = parser.getAccessNumberList(in);
		
		return list;
	}

}
