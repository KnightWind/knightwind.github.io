package com.sktlab.bizconfmobile.parser;

import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.sktlab.bizconfmobile.model.AccessNumber;

public class XMLSAXParser {

	public List<AccessNumber> getAccessNumberList(InputStream in) throws Exception{
		
		SAXParserFactory factory = SAXParserFactory.newInstance();		
		SAXParser parse = factory.newSAXParser();
		
		PListParser handler = new PListParser();
		parse.parse(in, handler);
		
		List<AccessNumber> list = handler.getList();
		
		return list;
	}
	
}
