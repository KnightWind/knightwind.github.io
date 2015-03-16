package com.sktlab.bizconfmobile.net;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sktlab.bizconfmobile.util.Util;

//import android.content.Context;
//import android.content.res.AssetManager;

public class DomXml {

	public final static String TAG = "DomXml";
	/**
	 * 根据key获取子节点
	 * @param item
	 * @param key
	 * @return
	 */
	public static Node getChildNode(Node item, String key){
		NodeList data = item.getChildNodes();
		for(int i=0;i<data.getLength();i++){
			Node temp = data.item(i);
			if(temp.getNodeName().equalsIgnoreCase(key))
				return temp;
		}
		return null;
	}
	
	/**
	 * 根据key获取子节点群
	 * @param item
	 * @param key
	 * @return
	 */
	public static NodeList getChildNodes(Node item, String key){
		NodeList data = item.getChildNodes();
		
		for(int i=0;i<data.getLength();i++){
			Node temp = data.item(i);
			if(!temp.getNodeName().equalsIgnoreCase(key)){
				item.removeChild(temp);
			}								
		}
		return data;
	}
	
	/**
	 * 根据key获取值
	 * @param item
	 * @param key
	 * @return
	 */
	public static String getChildText(Node item, String key){
		try{
			NodeList properties = item.getChildNodes();	
			for (int j=0;j<properties.getLength();j++){
	            Node property = properties.item(j);	            
	            String name = property.getNodeName();
	            if (name.equalsIgnoreCase(key)){
	                return property.getFirstChild().getNodeValue();
	            }
			}

		}catch(Exception e){
			
		}
		return null;
	}
	
	public static String getAttributeValue(Node item, String key){
		try{
			Element element = (Element)item;
			return element.getAttribute(key);
		}catch(Exception e){
			
		}
		return null;
	}
	
	/**
	 * 从xml String中构造Node
	 * @param xmlStr
	 * @return
	 */
	public static Element parseStr(String xmlStr) {
		Element root = null;
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setCoalescing(true);//可读CDATA
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom;
			
			dom = builder.parse(new ByteArrayInputStream(xmlStr.toString().getBytes("utf-8")));
			
			//Util.BIZ_CONF_DEBUG(TAG, "begin get Document Element");
			root = dom.getDocumentElement();
		}catch(Exception e){
//			Util.BIZ_CONF_DEBUG(TAG, "parse str:" + xmlStr 
//					+ " catch exception,msg:" + e.getMessage());
			e.printStackTrace();
		}
		return root;

	}
	
	/**
	 * 从xml文件中构造Node
	 * @param file
	 * @return
	 */
	public static Element parseFile(File file) {
		Element root = null;
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setCoalescing(true);//可读CDATA
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom;
			dom = builder.parse(new FileInputStream(file));
			root = dom.getDocumentElement();
		}catch(Exception e){
			e.printStackTrace();
		}
		return root;
	}
	
	/**
	 * 从xml文件中构造Node
	 * @param file
	 * @return
	 */
	public static Element parseStream(InputStream is) {
		Element root = null;
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setCoalescing(true);//可读CDATA
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom;
			dom = builder.parse(is);
			root = dom.getDocumentElement();
		}catch(Exception e){
			e.printStackTrace();
		}
		return root;
	}
	
	/**
	 * 从xml文件中构造Node
	 * @param file
	 * @return
	 * @throws IOException 
	 */
//	public static Element parseAssets(String str, Context ctx) {
//		Element root = null;
//		InputStream is = null;
//		try{
//			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//			factory.setCoalescing(true);//可读CDATA
//			DocumentBuilder builder = factory.newDocumentBuilder();
//			Document dom;
//			AssetManager am = ctx.getAssets(); 
//			is = am.open(str);
//			dom = builder.parse(is);
//			root = dom.getDocumentElement();
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally{
//			try {				
//				is.close();
//			} catch (IOException e) {
//			}
//		}
//		return root;
//	}
}
