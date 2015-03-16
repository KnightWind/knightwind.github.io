package com.sktlab.bizconfmobile.mina;

public class MinaUtil {
	
	//Waiting time for connect to server, default is 30 seconds
	public static final int CONNECT_WAITING_TIME = 30000;

	public static final String SEPARATOR = "~";
	//msg id used to check the msg send from server is the first link session create msg
	public static final String MSG_LS_SL = "LS.SL";
	//create ACC session, when server return the ACC session id, we use this to check whether the msg is ACC create
	//success msg
	public static final String MSG_LS_CS = "LS.CS";
	public static final String MSG_LS_DS = "LS.DS";
	
	public static final String MSG_ACC = "ACC";
	//add a party to conference
	public static final String MSG_CREATE_PARTY = "ACC.P.CREATE";
	
	//service for a single party
	public static final String MSG_P_SERVICE= "ACC.P.OPSIG";
	/**
	 * 
	 * This is just used to out call the conference's host, mostly the moderator
	 * 
	 * Line 51: 16:17:12  TX: 335~4~ACC.O.AA~33A
	 */
	public static final String MSG_OUT_CALL = "ACC.O.AA";
	
	//request a conference's attribute
	public static final String MSG_CONF_ATTR = "ACC.A";
	
	/**
	 * 
	 * activate a conference
	 * 
	 * 	Line 20: 16:16:46  TX: 335~1~ACC.C.ACTIVATE~1~9195~0~0  
						---> 通过密码激活会议
						335~1~ACC.A~1~1~338~0~0~~0~1
						338为会议id
	 */
	public static final String MSG_ACTIVATE_CONF = "ACC.C.ACTIVATE";
		
	/**
	 * 
	 * associate a conference to ACC service
	 * 
	 * 	Line 27: 16:16:48  TX: 335~2~ACC.C.ASSOC~338
						--->关联一个具体的会议
	 */
	public static final String MSG_ASSOC_CONF = "ACC.C.ASSOC";
	
	//Destroy a conference
 	public static final String MSG_CONF_DESTORY = "ACC.C.DESTROY";
 	
 	
 	/**
 	 * 
 	 * 测试过程中发现，会议只有一个参会人时，发送的静音命令，服务器不处理
 	 * 
 	 * 	Line 197: 16:26:00  TX: 335~17~ACC.P.MODE.SELF~0~0~33C
		Line 203: 16:26:48  TX: 335~18~ACC.P.MODE.SELF~1~0~33C
						--->对某个人静音
						0：muted
						1：unmuted
 	 */
 	public static final String MSG_SELF_MUTE = "ACC.P.MODE.SELF";
 	
 	public static final String MSG_MUTE_PARTY = "ACC.P.MODE";
 	
 	/**
 	 * 
 	 * see 8.3.18 for more details
 	 * 
 	 * B51~15~ACC.C.MUTE~0~0
 	 * 
 	 * The following two "0"'s meaning: 
 	 * 	muteState muteParticipants
 	 * 
 	 */
 	public static final String MSG_CONF_MUTE = "ACC.C.MUTE";
 	
 	/**
 	 * 
 	 * Disconnect a party
 	 * 
 	 * 	Line 363: 16:30:56  TX: 34E~3~ACC.P.DESTROY~34B~1
						---> 挂断指定ID的联系人
							1: force disconnect
							0:can not be disconnected
							see API doc 8.3.21
 	 */
 	public static final String MSG_DISCONNECT_PARTY = "ACC.P.DESTROY";

 	/**
 	 * 
 	 * guest out call a party
 	 * 
 	 * Line 88: 16:17:39  TX: 335~6~ACC.P.CONNECT.GUEST~33C~0
 	 */
 	public static final String MSG_GUEST_OUT_CALL = "ACC.P.CONNECT.GUEST";	
 	
 	
 	public static final String MSG_P_MOVE = "ACC.P.MOVE";	
 	
 	public static final String MSG_O_DEL = "ACV.O.DEL";
 	/**
 	 * 
 	 * roll call 
 	 * 
 	 *	Line 127: 16:19:58  TX: 335~8~ACC.IVR.RollCall~338~0
						--->点名 
 	 */
 	public static final String MSG_ROLL_CALL = "ACC.IVR.RollCall";
 	public static final String MSG_ROLL_CALL_RSP = "ACC.IVR.ROLLCALL";
 	
 	/**
 	 * 
 	 * Record
 	 * 
 	 * 	Line 158: 16:22:31  TX: 335~11~ACC.C.RECORD~1
						--->开始录音
							0 -->stop record
							1 -->start record
							2 --> pause record
 	 */
 	public static final String MSG_RECORD = "ACC.C.RECORD";
 	
 	/**
 	 * 
 	 * lock conference
 	 * 
 	 * 	Line 178: 16:23:53  TX: 335~15~ACC.C.A.ALTER~338~ConfSecure~1
	Line 183: 16:24:09  TX: 335~16~ACC.C.A.ALTER~338~ConfSecure~0
						--->338  conference id
							1:Lock conference 
							0:UnLock conference
							other values see the API doc
 	 */
 	public static final String MSG_ACC_C_A_ALTER = "ACC.C.A.ALTER";
 	
 	
 	/**
 	 * This is used to change party's attribute
 	 * 
 	 * 16:53:23  RX: 369~16~ACV.P.A~36A~2~1~5~0~0~~~~0~20130813163151000~~20130813163144000~0~~~~0~0
	 * 16:53:37  TX: 359~25~ACC.P.A.ALTER~36A~HostCtrlLevel~0
	 * 16:53:37  RX: 359~25~ACC.P.A.ALTER
	 * 16:53:38  RX: 369~16~ACV.P.A~36A~1~1~wen~111#17969018202932163~36B~~~~~Shrine~0~0~0~0
	 *
 	 */
 	public static final String MSG_P_ALTER = "ACC.P.A.ALTER";
 	
 	public static final String MSG_P_HOST_CONTROL_LEVEL = "HostCtrlLevel";
 	
	public static final String MSG_BV = "BV";
	
	/**
	 * 
	 * request activate conference list
	 * 
	 * 	Line 29: 16:16:48  TX: 336~1~BV.B.ACL
						--->请求活动会议列表
	 */
	public static final String MSG_ACTIVE_CONF_LIST = "BV.B.ACL";
	
	/**
	 * 
	 * This message is sent when the active conference is removed from bridge.
	 * the reason may be the moderator leave the conference or customer manual end the 
	 * conference
	 * 
	 * 137~1~BV.B.LC.DEL~1~127~7971~5685406258~85406258~~1378975448~1
	 */
	public static final String MSG_LIVE_CONF_REMOVED = "BV.B.LC.DEL";
	
	/**
	 * 1E12~1~BV.B.AP.ADD~1~1E14~7530~5211100458~11100458~~1382551972~1
	 */
	public static final String MSG_BV_B_AP_ADD = "BV.B.AP.ADD";
	
	public static final String MSG_BV_B_APL = "BV.B.APL";
	
	public static final String MSG_ACV = "ACV";
	
	public static final String MSG_ACV_A = "ACV.A";
	
	public static final String MSG_ACV_SA = "ACV.SA";
	
	public static final String MSG_ACV_SA_ALTER = "ACV.SA.ALTER";
	
	public static final String MSG_ACV_P_A = "ACV.P.A";
	/**
	 * 
	 * see api 7.3.13 for more detail
	 * had add a party to conference success,this msg is send from the server
	 * 
	 * ACV.P.ADD
	 * receive msg from server: 1CA~1~ACV.P.ADD~1CB~18202932163
	 * 
	 * ACV.P.A
	 * 16:30:50  RX: 350~0~ACV.P.A~34B~1~1~wem~018202932163~0~~~~~Shrine~0~1~0~0
	 */
 	public static final String MSG_ACV_P_ADD = "ACV.P.ADD";
 	
 	/**
 	 * request the participant list of the conference which the acv bind to
 	 * 
 	 * request format:
 	 * 350~0~ACV.PL
 	 * 
 	 * response format:
 	 * 350~0~ACV.PL~1~1~2~34B~wem~34C~luo
 	 */
 	public static final String MSG_ACV_P_LIST = "ACV.PL";
 	
 	/**
 	 * This message is send from server when a party is removed from the conference
 	 * 
 	 * format: 1AE~1~ACV.P.DEL~1B4
 	 */
 	public static final String MSG_ACV_P_DEL = "ACV.P.DEL";
 	
 	public static final String MSG_ACV_OL = "ACV.OL";
 	
 	public static final String MSG_ACV_P_TAKLER = "ACV.P.TALKER";
 	
 	//when add a party success, server return msg,if type is 1,this msg give the 
 	//information of the party, 2 is the location related information
 	public static final String MSG_ACV_PARTY_ATTR = "1";
 	public static final String MSG_ACV_PARTY_LOCATION = "2";
 	
	public static final String CREATE_ACC_SESSION = MSG_LS_CS + SEPARATOR + MSG_ACC;
	public static final String CREATE_BV_SESSION = MSG_LS_CS + SEPARATOR + MSG_BV;
	public static final String CREATE_ACV_SESSION = MSG_LS_CS + SEPARATOR + MSG_ACV;
	
	public static String generateRequest(int ssnId,int seq, String msg) {
		
		String request = null;
		
		
		
		return request;
	}
}
