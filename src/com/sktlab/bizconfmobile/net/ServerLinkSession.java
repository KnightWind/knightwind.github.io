package com.sktlab.bizconfmobile.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.sktlab.bizconfmobile.mina.ClientMinaHandler;
import com.sktlab.bizconfmobile.mina.MessageCodecFactory;
import com.sktlab.bizconfmobile.mina.ServerHandler;
import com.sktlab.bizconfmobile.util.Util;

public class ServerLinkSession {
	
	public static final String TAG = "ServerLinkSession";
	
	public static boolean isUseTransferServerAddress = true;
	
	public static boolean isUseDNSName = false;
	
	public static String DNS_ADDRESS = "mobile1.bizconf.cn";
	
	public static int DNS_PORT = 8080;
	
	public static String CAS_IP_ADDRESS =  "106.120.238.134";
	public static int CAS_PORT = 25200;
	
	//Test address and port
	//public static String TRANSFER_SERVER_ADDRESS =  "221.123.166.214";
	public static String TRANSFER_SERVER_ADDRESS =  "106.120.238.134";
	//private final String TEST_ADDRESS =  "192.168.1.201";
	//private final String TEST_ADDRESS =  "192.168.1.14";
	
	public static int TRANSFER_SERVER_PORT = 8080;
	//private final int TEST_PORT = 25300;
	//public static int TRANSFER_SERVER_PORT = 25200;
	
	public static final String CHAR_SET = "UTF-8";
	//public static final String CHAR_SET = "US-ASCII";
	
	// mina connector
	private NioSocketConnector mConnector;
	
	private IoSession mSession;

	//check whether client had receive success signal
	private boolean isConnected = false;
	
	private boolean isReady;

	public ServerLinkSession() {

		isReady = false;
	}

	public void connect() {

		if (!isReady) {

			//Util.BIZ_CONF_DEBUG(TAG, "connecting to server now~");

			mConnector = new NioSocketConnector();

			DefaultIoFilterChainBuilder chain = mConnector.getFilterChain();

			TextLineCodecFactory factory = new TextLineCodecFactory(
					Charset.forName(CHAR_SET));

			factory.setDecoderMaxLineLength(Integer.MAX_VALUE);
			factory.setEncoderMaxLineLength(Integer.MAX_VALUE);

			chain.addLast("codec", new ProtocolCodecFilter(factory));
			//chain.addLast("codec", new ProtocolCodecFilter(new MessageCodecFactory()));
			
			mConnector.setHandler(new ClientMinaHandler());
			mConnector.setConnectTimeoutCheckInterval(30);

			ConnectFuture cf = null;

			if (!isUseTransferServerAddress) {

				cf = mConnector.connect(new InetSocketAddress(CAS_IP_ADDRESS, CAS_PORT));
				
				Util.BIZ_CONF_DEBUG(TAG, "connect cas IP:" + CAS_IP_ADDRESS);
			} else if (isUseDNSName){
				
				mConnector.connect(new InetSocketAddress(DNS_ADDRESS,DNS_PORT));
				Util.BIZ_CONF_DEBUG(TAG, "connect DNS IP:" + DNS_ADDRESS);
			} else {
				
				cf = mConnector.connect(new InetSocketAddress(TRANSFER_SERVER_ADDRESS,
						TRANSFER_SERVER_PORT));
				Util.BIZ_CONF_DEBUG(TAG, "connect transfer server IP:" + TRANSFER_SERVER_ADDRESS);
			}

			// 等待连接成功,相当于将异步执行转为同步执行
			//isConnected = cf.awaitUninterruptibly(2, TimeUnit.SECONDS);
			cf.awaitUninterruptibly(10, TimeUnit.SECONDS);
			
			mSession = cf.getSession();

			if (Util.isEmpty(mSession)) {
				
				isReady = false;
				
				//Util.BIZ_CONF_DEBUG(TAG, "session is null, sorry~");
			} else {

				//Util.BIZ_CONF_DEBUG(TAG, "connect to server success");
			}
		}
	}

	public void close() {
		
		setReady(false);
		setConnected(false);
		
		if (!Util.isEmpty(mSession) && !mSession.isClosing()) {
			
			//Util.BIZ_CONF_DEBUG(TAG, "closing session now");
			
			// 关闭IoSession,该操作是异步的,true为立即关闭,false为所有写操作都flush后关闭
			mSession.close(false);
			// IoSession.close()仅仅是关闭了TCP的连接通道,并未关闭Client端程序
			mSession.getService().dispose();
		}		
	}

	public IoSession getSession() {
		return mSession;
	}

	public void setSession(IoSession mSession) {
		this.mSession = mSession;
	}

	public boolean isReady() {
		return isReady;
	}

	public void setReady(boolean isConnected) {
		this.isReady = isConnected;
	}	
	
	public boolean isConnected() {
		return isConnected;
	}

	public void setConnected(boolean isReceiveSuccessSignal) {
		this.isConnected = isReceiveSuccessSignal;
	}
	
	/**
	 * This method just for test,because our customer's server always disconnect
	 */
	private void createServer() {
		
		IoAcceptor acceptor=new NioSocketAcceptor(); 
        
        try {
			
        	DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
    		
    		TextLineCodecFactory factory = 
    				new TextLineCodecFactory(Charset.forName(CHAR_SET));
    		
    		factory.setDecoderMaxLineLength(Integer.MAX_VALUE);
    		factory.setEncoderMaxLineLength(Integer.MAX_VALUE);
    		
    		chain.addLast("codec", new ProtocolCodecFilter(factory));
    		
			acceptor.setHandler(new ServerHandler()); 
			acceptor.bind(new InetSocketAddress(CAS_IP_ADDRESS,CAS_PORT));
			
			//Util.BIZ_CONF_DEBUG(TAG, "server create success");
			
		} catch (IOException e) {
			System.out.println("cat io exception");
			e.printStackTrace();
			
			//Util.BIZ_CONF_DEBUG(TAG, "create server catch exception");
		}      
	}
}
