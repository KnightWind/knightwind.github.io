package com.sktlab.bizconfmobile.activity;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Application;

import com.sktlab.bizconfmobile.fragment.HomeFragment;
import com.sktlab.bizconfmobile.model.Constant;
import com.sktlab.bizconfmobile.net.ServerLinkSession;
import com.sktlab.bizconfmobile.util.Handler_Properties;
import com.sktlab.bizconfmobile.util.Util;

public class AppClass extends Application {

	public static final String TAG = "AppClass";
	
	public  static boolean flag=false;

	private static final int THEAD_POOL_SIZE = 5;
		
	//when  need background execute thread, we use this thread pool
	private ExecutorService service = null;
		
	private boolean isPassNumberVerify = false;
	//this is used to mark the user current select in the home page,such as start conference,
	//order conference or join conference
	private int currentTab = -1;
	
	//whether APP need exit
	private boolean need2Exit = false;
	
	//the conference code get from the link which to launch our App
	private String confCodeInEmail = "";
	private String accessNumInEmail = "";
	
	private boolean isGenerateLogFile = false;
	
	private static class InstanceHolder {

		private static AppClass instance = null;
	}
	
	/**
	 * get the signal instance of the application
	 * @return
	 */
	public static AppClass getInstance() {

		if (InstanceHolder.instance == null) {
			
			InstanceHolder.instance = new AppClass();		
			InstanceHolder.instance.init();
		}

		return InstanceHolder.instance;
	}

	public void onCreate() {
		super.onCreate();

		InstanceHolder.instance = this;
		
		init();
	}
	
	public void init() {

		String casAddress = getConf("cas_address");
		String casPort = getConf("cas_port");		
		String transferAddress = getConf("transfer_address");
		String transferPort = getConf("transfer_port");		
		String baseProductWebUrl = getConf("base_product_web_url");
		String baseTestWebUrlIp = getConf("base_test_web_url_ip");
		String baseTestWebUrlDns = getConf("base_test_web_url_dns");
		String isLogging = getConf("is_generate_log_file");
		String dnsAddress = getConf("dns_address");
		String dnsPort = getConf("dns_port");
		
		String isUseTransferAddress = getConf("is_use_transfer_address");
		boolean isUseDnsAddress = getBooleanValue(getConf("is_use_dns_address"));
		
		if ("true".equals(isLogging)) {
			
			setGenerateLogFile(true);
		}
		
		ServerLinkSession.isUseTransferServerAddress = getBooleanValue(isUseTransferAddress);
		ServerLinkSession.isUseDNSName = isUseDnsAddress;
		
		if (null != casAddress) {
			
			ServerLinkSession.CAS_IP_ADDRESS = casAddress;
		}
		
		if (null != casPort) {
			
			ServerLinkSession.CAS_PORT = Integer.valueOf(casPort);
		}
		
		if (null != transferAddress) {
			
			ServerLinkSession.TRANSFER_SERVER_ADDRESS = transferAddress;
		}
		
		if (null != transferPort) {
			
			ServerLinkSession.TRANSFER_SERVER_PORT = Integer.valueOf(transferPort);
		}
		
		if (null != dnsAddress) {
			
			ServerLinkSession.DNS_ADDRESS = dnsAddress;
		}
		
		if (null != dnsPort) {
			
			ServerLinkSession.DNS_PORT = Integer.valueOf(dnsPort);
		}
		
		if (null != baseProductWebUrl) {
			
			Constant.BASE_PRODUCT_WEB_URL = baseProductWebUrl;
		}
		
		if (isUseDnsAddress && null != baseTestWebUrlIp) {

				Constant.BASE_TEST_WEB_URL = baseTestWebUrlDns;		
		}else if (null != baseTestWebUrlIp) {

				Constant.BASE_TEST_WEB_URL = baseTestWebUrlIp;
		}		
		
		resetWebUrls();
		
		Util.BIZ_CONF_DEBUG(TAG, "base test web url:" + Constant.BASE_TEST_WEB_URL);
		
		// initialize service pool
		int cpuNums = Runtime.getRuntime().availableProcessors();
		setService(Executors.newFixedThreadPool(cpuNums * THEAD_POOL_SIZE));

		currentTab = HomeFragment.TAB_START_CONF;	
	}
	
	private void resetWebUrls() {
		
		Constant.BASE_WEB_URL = Constant.BASE_TEST_WEB_URL;
		
		Constant.URL_VERIFY_PHONE_NUMBER =  Constant.BASE_WEB_URL + "/meeting";		
		Constant.UPDATE_URL = Constant.BASE_WEB_URL + "/versionnmber?language=";
		Constant.BRIDGE_NUM_URL = Constant.BASE_WEB_URL + "/bridgeinfo";		
		Constant.ACCESS_NUMBER_URL = Constant.BASE_WEB_URL + "/accessNumber";		
		Constant.EMAIL_TEMPLET_URL = Constant.BASE_WEB_URL + "/accessNumberTemplet";
	}
	
	public boolean getBooleanValue(String config) {
		
		boolean booleanValue = false;
		
		if (null != config && "true".equals(config)) {
			
			booleanValue = true;
		}
		
		return booleanValue;
	}
	
	public String getConf(String key) {
		
		Properties properties = Handler_Properties.loadConfigAssets("config.properties");
		
		String value = null;
		
		if (properties != null && properties.containsKey(key)) {
			
			value = properties.get(key).toString();
		}
		
		return value;
	}
	
	public void reset(){
		
		currentTab = HomeFragment.TAB_START_CONF;
		isPassNumberVerify = false;
		
//		AccountsManager.getInstance().reset();
//		ContactManager.getInstance().reset();
	}
	
	public ExecutorService getService() {
		return service;
	}

	public void setService(ExecutorService service) {
		this.service = service;
	}

	public int getCurrentTab() {
		return currentTab;
	}

	public void setCurrentTab(int currentTab) {
		this.currentTab = currentTab;
	}

	public boolean isPassNumberVerify() {
		return isPassNumberVerify;
	}

	public void setPassNumberVerify(boolean isPassNumberVerify) {
		this.isPassNumberVerify = isPassNumberVerify;
	}

	public boolean isNeed2Exit() {
		return need2Exit;
	}

	public void setNeed2Exit(boolean need2Exit) {
		this.need2Exit = need2Exit;
	}

	public String getLinkConfCode() {
		return confCodeInEmail;
	}

	public void setLinkConfCode(String linkConfCode) {
		this.confCodeInEmail = linkConfCode;
	}

	public String getAccessNumInEmail() {
		return accessNumInEmail;
	}

	public void setAccessNumInEmail(String accessNumInEmail) {
		this.accessNumInEmail = accessNumInEmail;
	}

	public boolean isGenerateLogFile() {
		return isGenerateLogFile;
	}

	public void setGenerateLogFile(boolean isGenerateLogFile) {
		this.isGenerateLogFile = isGenerateLogFile;
	}
}
