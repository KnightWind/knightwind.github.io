package com.sktlab.bizconfmobile.mina;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.sktlab.bizconfmobile.activity.AppClass;
import com.sktlab.bizconfmobile.model.ConfControl;
import com.sktlab.bizconfmobile.model.manager.CommunicationManager;
import com.sktlab.bizconfmobile.model.requesthandler.HandleRequest;
import com.sktlab.bizconfmobile.util.CharSetUtil;
import com.sktlab.bizconfmobile.util.Util;

public class ClientMinaHandler extends IoHandlerAdapter {
	
	public static final String TAG = "ClientMinaHandler";
	
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		
		super.sessionCreated(session);
		System.out.println("sessionCreated~");
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		super.sessionOpened(session);

		System.out.println("sessionOpened~");
		
		ConfControl.getInstance().setServerSession(session);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		super.sessionClosed(session);
		
		System.out.println("session closed~");
		
		ConfControl.getInstance().disconnectToServer();
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		super.sessionIdle(session, status);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {		
		super.exceptionCaught(session, cause);
		
		Util.BIZ_CONF_DEBUG(TAG, "与" + session.getRemoteAddress() + "通信过程中出现错误:[" + cause.getMessage() + "]..连接即将关闭....");
						
		if (CommunicationManager.getInstance().isInConfManageScreen() 
				&& Util.isNetworkReadyForConf(AppClass.getInstance())) {
			
			CommunicationManager.getInstance().notifyNetWorkReady();
		}else {
			
			ConfControl.getInstance().disconnectToServer();
		}
	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		
		super.messageReceived(session, message);
		
		Util.BIZ_CONF_DEBUG(TAG, "client rcv msg from server: " + message.toString());
		
		String rcv = message.toString();	
		rcv = CharSetUtil.toASCII(rcv);
		
		HandleRequest hr = HandleRequest.getInstance();
		hr.handle(rcv);			
	}
	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		
		super.messageSent(session, message);
		
		Util.BIZ_CONF_DEBUG(TAG, "client send msg to server: " + message.toString());
		
		MinaMsg msg = new MinaMsg(message.toString());
		
		//Util.BIZ_CONF_DEBUG(TAG, "send msgId: " + msg.getMsgId());
		
		CommunicationManager.getInstance().setClientSendMsgId(msg.getMsgId());
	}
}
