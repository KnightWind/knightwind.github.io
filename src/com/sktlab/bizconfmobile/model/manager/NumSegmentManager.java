package com.sktlab.bizconfmobile.model.manager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.res.AssetManager;
import android.database.Cursor;

import com.sktlab.bizconfmobile.activity.AppClass;
import com.sktlab.bizconfmobile.model.Constant;
import com.sktlab.bizconfmobile.model.NumberSegment;
import com.sktlab.bizconfmobile.model.PhoneNumLocationSegment;
import com.sktlab.bizconfmobile.model.PhoneNumLocInfo;
import com.sktlab.bizconfmobile.model.db.ShangHaiNumSegmentDBAdapter;
import com.sktlab.bizconfmobile.model.db.ShangHaiNumSegmentDbTable;
import com.sktlab.bizconfmobile.parser.NumberSegmentCSVParser;
import com.sktlab.bizconfmobile.util.Util;

public class NumSegmentManager {
	
	public static final String TAG = "NumSegmentManager";
	
	private String beijingData = "(174,1453,16,29,010)|(175,1851,300,421,010)|(177,1861,422,429,010)|(178,1300,100,129,010)|(179,1300,190,199,010)|(180,1301,100,129,010)|(181,1301,180,189,010)|(182,1302,0,9,010)|(183,1302,100,129,010)|(184,1302,190,199,010)|(185,1303,100,119,010)|(186,1304,100,129,010)|(187,1305,100,199,010)|(188,1307,10,19,010)|(189,1307,110,119,010)|(190,1312,0,49,010)|(191,1312,100,143,010)|(192,1312,145,173,010)|(193,1312,175,199,010)|(194,1312,470,479,010)|(195,1312,650,653,010)|(196,1312,655,673,010)|(197,1312,675,683,010)|(198,1312,685,693,010)|(199,1312,695,699,010)|(200,1314,100,149,010)|(201,1314,600,699,010)|(202,1316,100,199,010)|(203,1316,420,429,010)|(204,1316,730,739,010)|(205,1316,750,759,010)|(206,1322,10,19,010)|(207,1324,0,49,010)|(208,1324,70,82,010)|(209,1324,89,199,010)|(210,1326,0,3,010)|(211,1326,5,13,010)|(212,1326,15,23,010)|(213,1326,25,33,010)|(214,1326,35,49,010)|(215,1326,100,199,010)|(216,1326,310,323,010)|(217,1326,325,333,010)|(218,1326,335,349,010)|(219,1326,400,453,010)|(220,1326,455,459,010)|(221,1326,900,913,010)|(222,1326,915,923,010)|(223,1326,925,933,010)|(224,1326,935,953,010)|(225,1326,955,983,010)|(226,1326,985,993,010)|(227,1326,995,999,010)|(228,1450,0,99,010)|(229,1451,0,399,010)|(230,1452,0,39,010)|(231,1453,0,29,010)|(232,1550,100,129,010)|(233,1551,0,79,010)|(234,1560,0,139,010)|(235,1561,100,199,010)|(236,1562,490,499,010)|(237,1563,400,489,010)|(238,1564,600,699,010)|(239,1565,0,99,010)|(240,1565,200,299,010)|(241,1569,970,999,010)|(242,1850,0,139,010)|(243,1850,180,194,010)|(244,1851,0,199,010)|(246,1851,500,519,010)|(247,1851,800,899,010)|(248,1860,0,139,010)|(249,1860,180,199,010)|(250,1861,0,299,010)|(251,1861,380,389,010)|(252,1861,400,429,010)|(253,1861,800,899,010)|(254,1814,650,659,010)|(256,1800,100,139,010)|(257,1801,0,19,010)|(258,1804,650,659,010)|(259,1890,100,139,010)|(260,1891,0,199,010)|(261,1530,0,39,010)|(262,1530,100,139,010)|(263,1531,100,199,010)|(264,1531,300,399,010)|(265,1532,100,199,010)|(266,1533,0,29,010)|(267,1533,104,104,010)|(268,1533,108,109,010)|(269,1534,0,19,010)|(270,1535,101,105,010)|(271,1330,100,139,010)|(272,1331,100,159,010)|(273,1332,100,119,010)|(274,1333,100,119,010)|(275,1334,100,119,010)|(276,1335,740,749,010)|(277,1336,600,699,010)|(278,1337,10,19,010)|(279,1337,160,179,010)|(280,1338,100,149,010)|(281,1339,150,199,010)|(283,1881,300,319,010)|(284,1340,100,119,010)|(285,1342,600,649,010)|(286,1343,630,699,010)|(287,1343,900,999,010)|(288,1346,630,679,010)|(289,1348,860,889,010)|(290,1350,100,139,010)|(291,1351,100,109,010)|(292,1352,0,299,010)|(293,1355,200,299,010)|(294,1358,150,199,010)|(295,1360,100,139,010)|(296,1361,100,139,010)|(297,1362,100,139,010)|(298,1364,100,139,010)|(299,1365,100,139,010)|(300,1366,100,139,010)|(301,1367,100,139,010)|(302,1368,100,159,010)|(303,1368,300,339,010)|(304,1368,350,369,010)|(305,1369,100,159,010)|(306,1369,300,339,010)|(307,1369,350,369,010)|(308,1369,910,929,010)|(309,1370,100,139,010)|(310,1371,600,699,010)|(311,1371,750,899,010)|(312,1372,0,9,010)|(313,1380,100,139,010)|(314,1381,0,199,010)|(315,1390,100,139,010)|(316,1391,0,199,010)|(317,1470,100,199,010)|(318,1471,0,9,010)|(319,1500,100,139,010)|(320,1501,0,159,010)|(321,1510,100,119,010)|(322,1510,150,169,010)|(323,1511,0,29,010)|(324,1511,690,699,010)|(325,1511,790,799,010)|(326,1512,0,9,010)|(327,1520,100,169,010)|(328,1521,0,99,010)|(329,1570,100,169,010)|(330,1571,0,9,010)|(331,1571,100,149,010)|(332,1571,280,299,010)|(333,1571,880,889,010)|(334,1572,470,479,010)|(335,1572,660,669,010)|(336,1572,730,739,010)|(337,1580,100,169,010)|(338,1581,0,159,010)|(339,1590,100,159,010)|(340,1591,20,119,010)|(341,1820,100,169,010)|(342,1821,0,119,010)|(343,1830,100,169,010)|(344,1831,0,149,010)|(345,1870,100,169,010)|(346,1871,0,29,010)|(347,1880,0,19,010)|(348,1880,100,149,010)|(349,1881,0,179,010)|(352,1888,880,889,010)";
	
	private List<PhoneNumLocationSegment> prefix2LocSeg = new ArrayList<PhoneNumLocationSegment>();
	
	private ShangHaiNumSegmentDBAdapter shangHaiNumSegmentDbAdapter;
	
	private static class InstanceHolder {

		private final static NumSegmentManager instance = new NumSegmentManager();
	}

	private NumSegmentManager() {		
		
		shangHaiNumSegmentDbAdapter = new ShangHaiNumSegmentDBAdapter(ShangHaiNumSegmentDbTable.SEGMENT_DB_TABLE,
						ShangHaiNumSegmentDbTable.getAllColumns());
	}
	
	public static NumSegmentManager getInstance() {

		return InstanceHolder.instance;
	}
	
	public void loadPhoneLocSeg() {
	
		loadBeijingPhoneLocSeg();
		
		List<NumberSegment> list = loadShangHaiPhoneLocSeg();
		
		writeShangHaiPhoneNumSegToDb(list);
	}

	private List<NumberSegment> loadShangHaiPhoneLocSeg() {
		
		List<NumberSegment> list = null;
		
		if (!isShangHaiPhoneLocSegLoaded()) {
				
			list = parseShangHaiPhoneLocSegFromCSV();
		}
		
		return list;
	}

	private List<NumberSegment> parseShangHaiPhoneLocSegFromCSV() {
		
		Util.BIZ_CONF_DEBUG(TAG, "load shanghai phone number location segments");
		
		List<NumberSegment> list = null;
		
		try {

			AssetManager am = AppClass.getInstance().getAssets();

			if (!Util.isEmpty(am)) {

				InputStream in = am.open(Constant.PHONE_LOC);

				if (null != in) {
					
					NumberSegmentCSVParser parser = new NumberSegmentCSVParser(in);
					
					list = parser.getParedObj();
					
					in.close();
				}
			}
			
		} catch (IOException e) {

			e.printStackTrace();
		} catch (Exception e) {

			e.printStackTrace();
		}
		
		return list;
	}
	
	public void writeShangHaiPhoneNumSegToDb(List<NumberSegment> segments) {
		
		if (null != segments) {
			
			shangHaiNumSegmentDbAdapter.open();
			
			for (NumberSegment segment : segments) {
				
				//Util.BIZ_CONF_DEBUG(TAG, "prefix: " + segment.getPreFix() + "start: " + segment.getStart() + "end: " + segment.getEnd());
				shangHaiNumSegmentDbAdapter.insertObject(segment);	
			}
			
			Util.setSpBoolValue(AppClass.getInstance(), Constant.KEY_SP_PHONE_LOC_LOADED, true);
			
			shangHaiNumSegmentDbAdapter.close();
		}

	}
	
	public boolean isShangHaiNum(String phoneNumber) {
		
		boolean isShangHaiNum = false;
		
		shangHaiNumSegmentDbAdapter.open();
		
		PhoneNumLocInfo seg = new PhoneNumLocInfo(phoneNumber);
		
		//Util.BIZ_CONF_DEBUG(TAG, "phone prifix: " + seg.getPreFix());
		//Util.BIZ_CONF_DEBUG(TAG, "phone loc: " + seg.getLoc());		
		Cursor cursor = shangHaiNumSegmentDbAdapter.getSegment(seg.getPreFix());
		
		if (cursor != null && cursor.moveToFirst()) {
			
			do{
				
				int start = cursor.getInt(cursor.getColumnIndex(ShangHaiNumSegmentDbTable.KEY_START));
				int end = cursor.getInt(cursor.getColumnIndex(ShangHaiNumSegmentDbTable.KEY_END));
				
				int loc = seg.getLoc();
				
				//Util.BIZ_CONF_DEBUG(TAG, "loc: " + loc + " start:" + start + " end: " + end);			
				isShangHaiNum = (loc >= start) && (loc <= end);
				
				if (isShangHaiNum) {
					
					break;
				}
				
			}while(cursor.moveToNext());		
		}
		
		if (null != cursor) {
			
			cursor.close();
		}
		
		shangHaiNumSegmentDbAdapter.close();
		
		return isShangHaiNum;
	}
	
	public boolean isBeijingNumber(String phoneNumber) {
		
		boolean isBeijingNum = false;
		
		PhoneNumLocInfo phoneInfo = new PhoneNumLocInfo(phoneNumber);		
		int numberPrefix = phoneInfo.getPreFix();
		
		loadBeijingPhoneLocSeg();
		
		if (null != prefix2LocSeg && prefix2LocSeg.size() > 0) {
			
			Util.BIZ_CONF_DEBUG(TAG, "find this phone loc in map: " + phoneNumber);
			
			for(int i = 0; i < prefix2LocSeg.size(); i++) {
				
				PhoneNumLocationSegment seg = prefix2LocSeg.get(i);
				
				if (null != seg && seg.getPrifex() == numberPrefix 
								&& seg.isNumberInSegment(phoneNumber)) {
					
					Util.BIZ_CONF_DEBUG(TAG, "in number segment, area: " + seg.getArea());
					
					isBeijingNum = seg.getArea().equals("010");
					break;
				}
				
			}
			
		}
		
		Util.BIZ_CONF_DEBUG(TAG, phoneNumber + "is beijing number: " +  isBeijingNum);
		return isBeijingNum;	
	}
	
	public void loadBeijingPhoneLocSeg() {
		
		if (null != prefix2LocSeg && prefix2LocSeg.size() == 0) {
			
			String[] values = beijingData.split("\\|");
	        
			Util.BIZ_CONF_DEBUG(TAG, "hi,boy, load beijing phone location");
			
			for(String value : values) {
				
				//Util.BIZ_CONF_DEBUG(TAG, "insert data: " + value);				
				PhoneNumLocationSegment seg = new PhoneNumLocationSegment(value);
				
				if (null != prefix2LocSeg) {
					
					boolean isAdded = prefix2LocSeg.add(seg);

					//Util.BIZ_CONF_DEBUG(TAG, "isAdded:" + isAdded);	
				}
			}
		}
	}
	public boolean isShangHaiPhoneLocSegLoaded() {
		
		boolean isLoaded = 
				Util.getSPBool(AppClass.getInstance(), Constant.KEY_SP_PHONE_LOC_LOADED, false);
		
		return isLoaded;
	}
	
	public void setShangHaiPhoneLocSegHadLoaded(boolean isLoaded) {
		
		Util.setSpBoolValue(AppClass.getInstance(), Constant.KEY_SP_PHONE_LOC_LOADED, isLoaded);
	}
}
