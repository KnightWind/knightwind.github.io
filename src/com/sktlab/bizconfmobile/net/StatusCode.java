package com.sktlab.bizconfmobile.net;

public class StatusCode {

	//when the phone number verify success, will return this code
	public static final String TEL_VERIFY_CODE_SUCCESS = "100";
	
	//no account binds to the user input number
	public static final String NO_ACCOUNT_BINDING = "204";
	
	//There are accounts bind to the user input number
	public static final String ACCOUNTS_BINDING = "200";
	
	public static final String CONNECT_SUCCESS = "200";
	
	//test server down msg
	public static final String TEST_CAS_IS_DOWN = "999";
	
	//cas server down msg
	public static final String CAS_CONNECTED_FAIL_SIGNAL = "0~0~LS.ERR~RES";
}
