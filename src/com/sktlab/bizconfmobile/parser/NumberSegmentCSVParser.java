package com.sktlab.bizconfmobile.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.sktlab.bizconfmobile.model.AccessNumber;
import com.sktlab.bizconfmobile.model.NumberSegment;
import com.sktlab.bizconfmobile.util.Util;

public class NumberSegmentCSVParser extends AbstractCsvParser {
	
	public static final String TAG = "NumberSegmentCSVParser";
	
	public NumberSegmentCSVParser(InputStream inputStream) throws IOException {
		super(inputStream);
	}

	public NumberSegmentCSVParser(String fileName) throws IOException {
		super(fileName);
	}
	
	@Override
	public List parse() {
		
		List<NumberSegment> locSegments = new ArrayList<NumberSegment>();
		
		for (int i = 1; i < getRowNum() - 1; i++) {

			String preFix = getString(i, 1);

			String startLoc = getString(i, 2);
			String endLoc = getString(i, 3);
			
			//Util.BIZ_CONF_DEBUG(TAG, preFix + "    " + startLoc + "    " + endLoc);
			
			NumberSegment segment = new NumberSegment(preFix, startLoc, endLoc);
			
			locSegments.add(segment);
		}
		
		return locSegments;
	}

}
