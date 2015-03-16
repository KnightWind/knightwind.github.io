package com.sktlab.bizconfmobile.mina;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.sktlab.bizconfmobile.util.CharSetUtil;

/**
 * 使用Mina2.x发送报文的工具类
 * @create Oct 3, 2012 12:42:21 PM
 * @author 玄玉<http://blog.csdn/net/jadyer>
 * @version v1.1
 * @history v1.1-->编码器和解码器中的字符处理,升级为Mina2.x提供的<code>putString()</code>方法来处理
 */
public class MinaUtilDemo {
	private MinaUtilDemo(){}
	
	/**
	 * 发送TCP消息
	 * @see default timeout of the connect is 1 minute
	 * @see 该方法与远程主机间通信的协议报文为"GB18030"编码后的byte[]
	 * @param message   待发送报文的中文字符串形式
	 * @param ipAddress 远程主机的IP地址
	 * @param port      远程主机的端口号
	 * @return 远程主机响应报文的字符串形式,若对方未响应or响应为null则返回<code>""</code>空字符串
	 */
	public static String sendTCPMessage(String message, String ipAddress, int port){
		IoConnector connector = new NioSocketConnector();
		connector.setHandler(new ClientHandler(message));
		connector.getFilterChain().addLast("codec", 
				new ProtocolCodecFilter(
						new DefaultEncode(CharSetUtil.US_ASCII), 
						new DefaultDecode(CharSetUtil.US_ASCII)));
		ConnectFuture connectFuture = connector.connect(new InetSocketAddress(ipAddress, port));
		connectFuture.awaitUninterruptibly();           //等待连接成功,相当于将异步执行转为同步执行
		IoSession session = connectFuture.getSession(); //获取连接成功后的会话对象
		session.getConfig().setUseReadOperation(true);  //设置IoSession的read()方法为可用,默认为false
		ReadFuture readFuture = session.read();         //因其内部使用BlockingQueue,故Server端用之可能会内存泄漏,但Client端可适当用之
		readFuture.awaitUninterruptibly();              //Wait until the message is received
		Object respData = readFuture.getMessage();      //Get the received message
		return respData==null ? "":respData.toString(); //Returns the received message
	}
	
	
	private static class ClientHandler extends IoHandlerAdapter {
		private String message;
		public ClientHandler(String message){
			this.message = message;
		}
		@Override
		public void sessionOpened(IoSession session) throws Exception {
			session.write(message); //ClientHandler会将message传到DefaultEncode,然后将报文发出
		}
		@Override
		public void messageReceived(IoSession session, Object message) throws Exception {
			session.close(false); //关闭IoSession,该操作是异步的,true为立即关闭,false为所有写操作都flush后关闭
			session.getService().dispose(); //IoSession.close()仅仅是关闭了TCP的连接通道,并未关闭Client端程序
		}
	}
	
	
	private static class DefaultEncode extends ProtocolEncoderAdapter {
		private final String charset;
		public DefaultEncode(String charset){
			this.charset = charset;
		}
		@Override
		public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
//			IoBuffer buffer = IoBuffer.allocate(100).setAutoExpand(true);
//			buffer.put(message.toString().getBytes(charset));
//			buffer.flip(); //limit=position,position=0
//			out.write(buffer);
			/**
			 * 上面的注释内容,作用效果与下面相同,推荐使用Mina2.x提供的putString()
			 */
			IoBuffer buffer = IoBuffer.allocate(100).setAutoExpand(true);
			buffer.putString(message.toString(), Charset.forName(charset).newEncoder());
			buffer.flip();
			out.write(buffer);
		}
	}
	
	
	private static class DefaultDecode extends ProtocolDecoderAdapter {
		private final String charset;
		public DefaultDecode(String charset){
			this.charset = charset;
		}
		@Override
		public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
//			if(in.hasArray()){
//				System.out.println(LogUtil.formatTenToHexDataWithAscii(in.array(), 0, in.limit()));
//			}
//			byte[] len = new byte[6]; //假设报文长度固定为6
//			in.get(len, 0, 6);
//			String msgLen = new String(len);
//			byte[] body = new byte[Integer.parseInt(msgLen)-6];
//			in.get(body);                               //读取IoBuffer中的所有剩余字节到byte[]中
//			String msgBody = new String(body, charset); //报文中可能含中文,故指定解码字符集
//			//接收到报文长度-->000158
//			//接收到完整报文-->00015800000000订单结果通知:商户系统未成功接收到通知,需要继续通知                                                  4263351542024162852720121019004138201210191`
//			System.out.println("接收到报文长度-->" + msgLen);
//			System.out.println("接收到完整报文-->" + msgLen + msgBody);
//			out.write(msgLen + msgBody);
			/**
			 * 上面的注释内容,作用效果与下面相同,推荐使用Mina2.x提供的putString()
			 */
			IoBuffer buffer = IoBuffer.allocate(158)/*.setAutoExpand(true)*/;
			while(in.hasRemaining()){ //判断position和limit之间是否有元素
				buffer.put(in.get()); //get()读取此缓冲区当前position的字节,然后position+1
			}
			buffer.flip();
			out.write(buffer.getString(Charset.forName(charset).newDecoder()));
		}
	}
}
