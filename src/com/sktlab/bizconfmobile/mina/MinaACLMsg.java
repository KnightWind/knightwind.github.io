package com.sktlab.bizconfmobile.mina;

import com.sktlab.bizconfmobile.model.OngoingConf;
import com.sktlab.bizconfmobile.model.ConferenceAttr;
import com.sktlab.bizconfmobile.model.manager.CommunicationManager;

/**
 * 
 * @author wenjuan.li
 *
 *used to parse the following str
 *
 *see API 6.3.13
 *16:16:49  RX: 
 * 336~1~BV.B.ACL~1~1~1~6
 * ~333~13661305367~61305367~~~0~1
 * ~2FC~15001377254~01377254~~~0~1
 * ~300~2016177387~16177387~~~0~1
 * ~338~6092363170~92363170~~~0~1
 * ~2F4~8968454624~68454624~~~0~1
 * ~32E~15110021861~10021861~~~0~1
 */
public class MinaACLMsg extends MinaMsg {

	public MinaACLMsg() {
		
		super();
	}
	
	public MinaACLMsg(String rcv) {
		
		super(rcv);
		
		generateConf();
	}
	
	private void generateConf() {

		do {

			if (index < 7) {

				break;
			}
			
			for (int i = 7;i < index;i++) {
				
				int confIndex = i % 7;
				
				OngoingConf conf = null;
				ConferenceAttr attr = null;
				
				if (confIndex == 0) {
				
					conf = new OngoingConf();
					attr = conf.getAttr();
					attr.setConfName(tokens[i+1]);
					
					//Util.BIZ_CONF_DEBUG(TAG, "conf name:" + tokens[i + 1]);
					
					CommunicationManager.getInstance().putActiveConference(conf);
				}else {
					
					int mapKeyIndex = i/7*7 + 1;
					
					//Util.BIZ_CONF_DEBUG(TAG, "mapKeyIndex: " + mapKeyIndex);
					
					conf = CommunicationManager.getInstance().getActiveConfByKey(tokens[mapKeyIndex]);
					attr = conf.getAttr();
				}				
				
				switch (confIndex) {

				case 0:
					attr.setConfId(tokens[i]);
					break;
				case 1:
					attr.setConfName(tokens[i]);
					break;
				case 2:
					attr.setConfKey(tokens[i]);
					break;
				case 3:
					attr.setGroupKey(tokens[i]);
					break;
				case 4:
					attr.setMainConfId(tokens[i]);
					break;
				case 5:
					attr.setSubConfNumber(tokens[i]);
					break;
				case 6:
					attr.setPartition(tokens[i]);
					break;
				}
			}
				
		} while (false);

	}
	
}
