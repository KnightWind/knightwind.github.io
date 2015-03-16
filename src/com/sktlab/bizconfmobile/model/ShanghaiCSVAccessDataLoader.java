package com.sktlab.bizconfmobile.model;

import com.sktlab.bizconfmobile.util.FileUtil;

public class ShanghaiCSVAccessDataLoader extends AbstractDataLoader {
	
	public ShanghaiCSVAccessDataLoader() {
		
		chineseSourceData = Constant.SHANG_HAI_CSV_ACCESS_NUMBER_LIST_CH;
		englishSourceData = Constant.SHANG_HAI_CSV_ACCESS_NUMBER_LIST_EN;
		
		csvFileNameInSdcard = FileUtil.CSV_FILE_PATH + NetAccessNumberDataLoader
					.getCsvFileName(Constant.SHANG_HAI_BRIDGE);
	}

}
