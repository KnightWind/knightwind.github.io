package com.sktlab.bizconfmobile.model;

import org.apache.mina.core.session.IoSession;

import com.sktlab.bizconfmobile.mina.MinaMsg;
import com.sktlab.bizconfmobile.mina.MinaUtil;
import com.sktlab.bizconfmobile.util.Util;

public abstract class BaseBusinessSession {
	
	public static final String TAG = "BaseBusinessSession";
	//session id
	private String id;
	//sequence number
	private int seqNum;
	//whether the service is create
	private boolean isCreated;
	//if the create message had been sent to server, it is true
	private boolean isCreating;
	
	public BaseBusinessSession() {
	
		id = "0";
		seqNum = 1;
		setCreated(false);
		setCreating(false);
	}
	
	/**
	 * 
	 * @param session
	 * @param prams the parameters must be follow a message's order: ssnid seqNum msgId [msgData]
	 */
	public void sendMsg(IoSession session, String ... prams) {
		
		MinaMsg msg = new MinaMsg();
		
		msg.clearMsgData();
		
		do{
			
			if (prams.length < 1) {
				
				//Util.BIZ_CONF_DEBUG(TAG, "ACC msg length less than 1, not right, please check");
				break;
			}
			
			msg.setSsnId(getId());
			msg.setSeq(Integer.valueOf(getSeqNum()));
			
			int index = 0;
			
			for (String str : prams) {

				switch (index) {				
					
				case 0:
					msg.setMsgId(str);
					break;
					
				default:
					msg.appendMsgData(str);
					break;
				}
				
				index++;
			}
			
			if (Util.isEmpty(session)) {
				
				break;
			}
			
			session.write(msg.toString());
			
		}while(false);	
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getSeqNum() {
		return seqNum++;
	}

	public void setSeqNum(int seqNum) {
		this.seqNum = seqNum;
	}

	public boolean isCreated() {
		return isCreated;
	}

	public void setCreated(boolean isCreated) {
		this.isCreated = isCreated;
	}	
	
	public void reset() {
		
		id = "0";
		seqNum = 1;
		setCreated(false);
		setCreating(false);
	}

	public boolean isCreating() {
		return isCreating;
	}

	public void setCreating(boolean isCreating) {
		this.isCreating = isCreating;
	}
}
