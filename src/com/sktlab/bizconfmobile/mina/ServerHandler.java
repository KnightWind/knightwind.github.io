package com.sktlab.bizconfmobile.mina;


import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.sktlab.bizconfmobile.util.CharSetUtil;

public class ServerHandler extends IoHandlerAdapter { 
	
	public ServerHandler() {

	}
	
    @Override
	public void sessionCreated(IoSession session) throws Exception {
		super.sessionCreated(session);
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		super.sessionOpened(session);
		String lsVer = CharSetUtil.toASCII("0~0~LS.VER~2.0");		
		String slInfo = CharSetUtil.toASCII("0~0~LS.SL~1~1~3~BV~Bridge View~3.00~ACV~Active Conference View~5.00~ACC~Active Conference Control~4.10");
		session.write(lsVer);
		session.write(slInfo);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		super.sessionClosed(session);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		super.sessionIdle(session, status);
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		super.messageSent(session, message);
	}

	@Override 
    public void messageReceived(IoSession session, Object message) 
            throws Exception { 
          
    	String request = CharSetUtil.toASCII(message.toString());
    	
    	MinaMsg msg = new MinaMsg(request);
    	
    	do{
    		
    		msg.clearMsgData();
    		
    		
    		if(request.contains(CharSetUtil.toASCII(MinaUtil.CREATE_ACC_SESSION))){
    			
    			int rspAccSsnId = 335;
    			msg.appendMsgData(MinaUtil.MSG_ACC);		
    			msg.appendMsgData(String.valueOf(rspAccSsnId));
    			
    			session.write(msg.toString());
    			break;
    		}
    		
    		if(request.contains(CharSetUtil.toASCII(MinaUtil.CREATE_BV_SESSION))){
    			
    			int rspBvSssnId = 336;
    			msg.appendMsgData(MinaUtil.MSG_BV);
    			msg.appendMsgData(String.valueOf(rspBvSssnId));
    			
    			session.write(msg.toString());
    			break;
    		}
    		
    	}while(false);
    	
    } 
     
    @Override 
    public void exceptionCaught(IoSession session, Throwable cause) 
            throws Exception { 
        System.out.println("cat exception" +cause); 
    } 
 
} 
