package com.sktlab.bizconfmobile.mina;

import java.nio.charset.Charset;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.sktlab.bizconfmobile.net.ServerLinkSession;
import com.sktlab.bizconfmobile.util.Util;

public class DataEncoderEx extends ProtocolEncoderAdapter {
	
	public static final String TAG = "DataEncoderEx";
	
	@Override
	public void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {
		
		Charset charSet = Charset.forName(ServerLinkSession.CHAR_SET);
		
		Util.BIZ_CONF_DEBUG(TAG, "encode msg: " + message.toString());
		//System.out.println(message);
		IoBuffer buf = IoBuffer.allocate(100).setAutoExpand(true);
		String strOut = message.toString();
		//buf.putInt(strOut.getBytes(charSet).length);
		buf.putString(strOut, charSet.newEncoder());
		buf.flip();
		out.write(buf);
	}

}
