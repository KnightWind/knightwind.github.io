package com.sktlab.bizconfmobile.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

	public static String Md5(String plainText) {
		
		String result = "error";
		
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			
			result = buf.toString();
			//System.out.println("result: " + result);// 32位的加密
		} catch (NoSuchAlgorithmException e) {

			e.printStackTrace();
		}
		
		return result;
	}
}
