package com.sktlab.bizconfmobile.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.sktlab.bizconfmobile.model.AccessNumber;
import com.sktlab.bizconfmobile.util.Util;

/** 
 * .plist配置文件的解析器 
 * 支持array 
 * <plist version="1.0"> 
 *  <array> 
 *    <dict> 
 *      ... 
 *    </dict> 
 *    ... 
 *  </array> 
 * </plist version="1.0"> 
 *  
 * 支持Map 
 * <plist version="1.0"> 
 * <dict> 
 *  <id>key</id> 
 *  <array> 
 *    <dict> 
 *      ... 
 *    </dict> 
 *    ... 
 *  </array> 
 *  ... 
 * </dict>  
 * </plist version="1.0"> 
 *  
 * @author chen_weihua 
 * 
 */  
 public class PListParser extends DefaultHandler {  
	 
	 private final String DICT_TAG = "dict"; 
	 private final String ELEMENT_COUNTRY = "Country"; 
	 private final String ELEMENT_NUMBER = "Number"; 
	 private final String ELEMENT_NUM_TYPE = "NumberType"; 
	 
	 private final String KEY_OF_PLIST = "key";
	 private final String VALUE_OF_PLIST = "string";
	 
	 private String key;
     private List<AccessNumber> accessNumberList;
     private AccessNumber accessNumber;
     private String elementTag;
     private boolean elementKeyBegin = false;
     private boolean elementValueBegin = false;
     
     //use this method to get the list datas
     public List<AccessNumber> getList() {
    	
    	 return accessNumberList;
     }

	@Override
	public void startDocument() throws SAXException {
		
		accessNumberList  = new ArrayList<AccessNumber>(); 
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		
		if(DICT_TAG.equals(localName)) {
			accessNumber = new AccessNumber();
			
		}
		
		if(KEY_OF_PLIST.equals(localName)) {
			
			elementKeyBegin = true;
		}
		
		if(VALUE_OF_PLIST.equals(localName)) {
			
			elementValueBegin = true;
		}
		
		elementTag = localName;	
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {

		if (length <= 0) {
			
			return;
		}

		String data = new String(ch, start, length);

		if (elementKeyBegin) {

			key = data;
		}

		if (elementValueBegin) {
			
			do{
				
				if(Util.isEmpty(accessNumber)) {
					break;
				}
				
				if (ELEMENT_COUNTRY.equals(key)) {
					
					accessNumber.setCountry(data);
					break;
				}
				
				if (ELEMENT_NUMBER.equals(key)) {
					
					accessNumber.setNumber(data);
					break;
				}
				
				if (ELEMENT_NUM_TYPE.equals(key)) {
					
					accessNumber.setNumberType(data);
					break;
				}
				
			}while(false);
			
		}

		if (!Util.isEmpty(accessNumber) && !Util.isEmpty(elementTag)) {

		}

	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		
		if(DICT_TAG.equals(localName)) {
			
			if(!Util.isEmpty(accessNumber)) {
				
				accessNumberList.add(accessNumber);
			}
			
			accessNumber = null;
		}
		
		if(KEY_OF_PLIST.equals(localName)) {
			
			elementKeyBegin = false;
		}
		
		if(VALUE_OF_PLIST.equals(localName)) {
			
			elementValueBegin = false;
		}
		
		elementTag = null;
	}
     
	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.endDocument();
	} 
 }