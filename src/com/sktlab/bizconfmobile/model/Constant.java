package com.sktlab.bizconfmobile.model;

public class Constant {
	
	//net connect timeout
	public static int CONNECT_TIMEOUT = 9000;
	//welcome screen show time
	public static int WELCOME_SCREEN_SHOW_TIME = 3000;
	//home
	public static final String TAB_TAG_HOME = "home";
	//history
	public static final String TAB_TAG_HISTORY = "history";
	//Calendar
	public static final String TAB_TAG_CALENDAR = "calendar";
	//service
	public static final String TAB_TAG_SERVICE = "service";
	//setting
	public static final String TAB_TAG_SETTING = "setting";
	//is phone has checked
	public static final String SP_SIMPLE_DATA_STORE = "simplebizconfdatastore";
	//the key of phone number check stored in sharedperference
	public static final String KEY_SP_PHONE_NUM_VER = "keyphonever";
	//the account had been downloaded from network,this just do when the user verify a phone number
	public static final String KEY_SP_ACCOUNT_DOWNLOADED = "keyaccountdownloaded";
	//should load data form database
	public static final String KEY_SP_IS_ALREADY_LOAD = "isalreadyloadaccount";
	//the key of the phone number which user had verified
	public static final String KEY_SP_VERIFIED_PHONE_NUM = "verifiedphonenumber";
	//the key of value stored in sharedpreference to indicate whether the phone loc had been loaded
	public static final String KEY_SP_PHONE_LOC_LOADED = "phoneLocLoaded";
	//whether the user had created account
	public static final String KEY_SP_IS_FIRST_CREATED_ACCOUNT = "isFirstCreatedAccount";
	
	public static final String KEY_SP_LOCATION_CODE = "locationCode";
	//key of verify code pass by intent
	public static final String KEY_VERIFY_PHONE_NUM = "verifyverifynumber";
	//key of edit conference account id
	public static final String KEY_OF_CONF_ACCOUNT_ID = "conf_account_id";
	
	public static final String KEY_OF_OPERATE_TYPE = "account_operate_type";
	//key of bridge information of a conference code
	public static final String KEY_OF_INPUT_CONF_CODE = "input_conf_code";
	
	public static final String KEY_OF_PHONE_LOC_CREATED = "is_phone_loc_db_created";
	//load account from network
	public static final String LOAD_ACCOUNT_FROM_NET = "loadformnet";
	//load account from database
	public static final String LOAD_ACCOUNT_FROM_DB = "loadformdb";
	
	public static String BASE_PRODUCT_WEB_URL = "http://106.120.238.134:8011/tel";
	
	public static String BASE_TEST_WEB_URL = "http://221.123.166.214:8011/tel";
	
	public static String BASE_WEB_URL = BASE_TEST_WEB_URL;
	//phone number verify url
	public static String URL_VERIFY_PHONE_NUMBER =  BASE_WEB_URL + "/meeting";
	
	//update url
	//public final static String UPDATE_URL = "http://api.ilovedeals.sg/app_release/latest?app_type=android-mobile";
	//public final static String UPDATE_URL = "http://192.168.1.105:8081/MyPHP/autoUpdate.php";
	public static String UPDATE_URL = BASE_WEB_URL + "/versionnmber?language=";
	
	//request bridge id through conference code URL
	//public final static String ACCESS_NUM_URL = "http://192.168.1.105:8081/MyPHP/accessNumber.php";
	public static String BRIDGE_NUM_URL = BASE_WEB_URL + "/bridgeinfo";
	
	public static String ACCESS_NUMBER_URL = BASE_WEB_URL + "/accessNumber";
	
	public static String EMAIL_TEMPLET_URL = BASE_WEB_URL + "/accessNumberTemplet";
	//access number list file name
	public static final String SHANG_HAI_PLIST_ACCESS_NUMBER_LIST_EN = "shang_hai_access_en.plist";
	public static final String SHANG_HAI_PLIST_ACCESS_NUMBER_LIST_CH = "shang_hai_access_ch.plist";
	public static final String BEI_JING_ACCESS_NUMBER_LIST_CH = "Beijing_access_num_ch.csv";
	public static final String BEI_JING_ACCESS_NUMBER_LIST_EN = "Beijing_access_num_en.csv";
	public static final String SHANG_HAI_CSV_ACCESS_NUMBER_LIST_CH = "Shanghai_access_num_ch.csv";
	public static final String SHANG_HAI_CSV_ACCESS_NUMBER_LIST_EN = "Shanghai_access_num_en.csv";
	
	public static final String CSV_FILE_SUFFIX = ".csv";
	public static final String BEI_JING_ACCESS_FILE_NAME = "Beijing_access_num_";
	public static final String SHANG_HAI_ACCESS_FILE_NAME = "Shanghai_access_num_";
	
	public static final String PHONE_LOC = "conf_callerloc.csv";
	
	//US English 
	public static final String LOCAL_EN = "en_us";
	//united kingdom
	public static final String LOCAL_UK = "en_gb";
	//Chinese
	public static final String LOCAL_ZH = "zh_cn";
	
	//the parameter pass to server as language
	public static final String SERVER_LANGUAGE_ZH = "CH";
	
	//the parameter pass to server as language
	public static final String SERVER_LANGUAGE_EN = "EN";
	
	public static final String CHINA_COUNTRY_CODE = "86";
	
	public static final String SHANG_HAI_LINK_ACCESS_NUM = "+8621 6026 4000";
	
	public static final String BEI_JING_LINK_ACCESS_NUM = "+861056294500";
	
	public static int LOAD_PIC_TYPE = 1;
	
	//number of text view to be show in a row
	//this used in order conference UI,to layout the selected participant
	public static final int NUM_PER_ROW = 2;
	//max length of a party's show text, the show text may be name, phone number
	// or email address
	public static final int MAX_LENGTH_OF_SHOW_TEXT = 14;
	
	public static final int FILE_LOAD_START = 21;
	public static final int FILE_LOAD_UPDATE = 22;
	public static final int FILE_LOAD_END = 23;
	
	public static final int SHANG_HAI_BRIDGE = 1;
	public static final int BEI_JING_BRIDGE = 2;
	public static final int BRIDGE_ID_NOT_IN_SP = -1;
	public static final int ERR_NO_CONF_CODE_RECORD = -2;
	public static final int ERR_REQUEST_BRIDGE_ID_TIME_OUT = -3;
	public static final int BRIDGE_TYPE_REPLUS = 16;
	public static final int BRIDGE_TYPE_GENESYS = 17;
}
