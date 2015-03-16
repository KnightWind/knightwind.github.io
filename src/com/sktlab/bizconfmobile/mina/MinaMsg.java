package com.sktlab.bizconfmobile.mina;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.sktlab.bizconfmobile.util.CharSetUtil;
import com.sktlab.bizconfmobile.util.Util;

public class MinaMsg {
	
	public static final String TAG = "MinaMsg";
	
	protected String separator = "~";
	
	protected String ssnId;
	protected int seq;
	protected String msgId;
	protected ArrayList<String> msgData;
	protected int index = 0;	
	protected String[] tokens = null;
	
	public MinaMsg() {
		
		ssnId = "0";
		seq = 0;
		msgId = "null";
		msgData = new ArrayList<String>();
	}
	
	public MinaMsg(String rsp) {
		
		//Util.BIZ_CONF_DEBUG(TAG, "rsp: " + rsp);
		
		tokens = rsp.split(separator); 
		
		msgData = new ArrayList<String>();
		
		while (isIndexValid()) {
			
			assignValue();
			
			index++;
		}		
	}
	
	private boolean isIndexValid() {
		
		return index < tokens.length;
	}
	
	private void assignValue() {
		
		//Util.BIZ_CONF_DEBUG(TAG, "token[index]:" +  tokens[index] + " index:" + index);
		
		switch (index) {
		
		case 0:
			ssnId = tokens[index];
			break;
			
		case 1:
			seq = Integer.valueOf(tokens[index]);
			break;
			
		case 2:
			msgId = tokens[index];
			break;
			
		default:
			msgData.add(tokens[index]);
			//Util.BIZ_CONF_DEBUG(TAG, "add msg data:" + tokens[index]);
			break;
		}
	}
	
	public void clearMsgData() {
		
		if(!Util.isEmpty(msgData)) {
			
			msgData.clear();
		}
	}
	
	public MinaMsg appendMsgData(String data) {
	
		if(!Util.isEmpty(msgData)){
			
			msgData.add(data);
		}
		
		return this;
	}
	
	public String getSsnId() {
		return ssnId;
	}


	public void setSsnId(String ssnId) {
		this.ssnId = ssnId;
	}

	public int getSeq() {
		return seq;
	}


	public void setSeq(int seq) {
		this.seq = seq;
	}


	public String getMsgId() {
		return msgId;
	}


	public void setMsgId(String msgId) {
		
		this.msgId = msgId;
	}


	public ArrayList<String> getMsgData() {
		return msgData;
	}

	public String getMsgData(int index) {
		
		String data = null;
		
		if (null != msgData && index >= 0
				&& index < msgData.size()) {
			
			data = msgData.get(index);
		}
		
		return data;
	}
	
	public void setMsgData(ArrayList<String> msgData) {
		this.msgData = msgData;
	}

	/**
	 * to guarantee this method work right ,you should make sure the msg data's order correspond to customer's api doc
	 */
	@Override
	public String toString() {
		
		String msg = 
				ssnId + separator + 
				seq + separator + 
				msgId;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append(msg);
		
		for(String data : msgData) {
			
			sb.append(separator).append(data);
		}
		
		try {
			
			msg = CharSetUtil.toASCII(sb.toString());
			//msg = CharSetUtil.toUTF_8(sb.toString());
			
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
			//Util.BIZ_CONF_DEBUG(TAG, "get ascall msg error~");
			msg = sb.toString();
		}
		
		return msg;
	}
	
	
}
