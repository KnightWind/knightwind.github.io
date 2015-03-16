package com.sktlab.bizconfmobile.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.sktlab.bizconfmobile.model.AccessNumber;
import com.sktlab.bizconfmobile.model.NumberSegment;
import com.sktlab.bizconfmobile.util.Util;

public class AccessNumCSVParser extends AbstractCsvParser {

	public AccessNumCSVParser(InputStream inputStream) throws IOException {
		
		super(inputStream);
	}

	public AccessNumCSVParser(String fileName) throws IOException {
		
		super(fileName);
	}
	
	@Override
	public List parse() {
		
		List<AccessNumber> locSegments = new ArrayList<AccessNumber>();
		
		for (int i = 1; i < getRowNum(); i++) {
			
			AccessNumber accessNumber = new AccessNumber();
			
			String country = getString(i, 0);
			String number = getString(i, 1).trim();
			String numberType = getString(i, 2);
			
			//Util.BIZ_CONF_DEBUG(TAG, country + "**" + number + "**" + numberType);
			
			accessNumber.setCountry(country);
			accessNumber.setNumber(number);
			accessNumber.setNumberType(numberType);
			
			locSegments.add(accessNumber);
		}
		
		return locSegments;
	}

}
