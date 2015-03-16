package com.sktlab.bizconfmobile.model;

import java.io.InputStream;
import java.util.List;

import com.sktlab.bizconfmobile.parser.AccessNumCSVParser;
import com.sktlab.bizconfmobile.util.FileUtil;

public class BeijingAccessDataLoader extends AbstractDataLoader {
	
	public BeijingAccessDataLoader() {
		
		chineseSourceData = Constant.BEI_JING_ACCESS_NUMBER_LIST_CH;
		englishSourceData = Constant.BEI_JING_ACCESS_NUMBER_LIST_EN;
		
		csvFileNameInSdcard = FileUtil.CSV_FILE_PATH + NetAccessNumberDataLoader
				.getCsvFileName(Constant.BEI_JING_BRIDGE);
	}

}
