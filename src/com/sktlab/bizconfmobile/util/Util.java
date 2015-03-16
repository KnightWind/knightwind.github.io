package com.sktlab.bizconfmobile.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Movie;
import android.graphics.Paint;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.activity.AppClass;
import com.sktlab.bizconfmobile.activity.WelcomeActivity;
import com.sktlab.bizconfmobile.model.Constant;
import com.sktlab.bizconfmobile.model.LSSession;
import com.sktlab.bizconfmobile.model.SharedPreferenceOperator;
import com.sktlab.bizconfmobile.model.manager.NumSegmentManager;

public class Util {

	public static int scrWidth;
	public static int scrHeight;
	public static int runCount;// 游戏运行计数器，需要用到计数器的可以从这里统一取得
	public static final int OPPO_LAUNCHER_TYPE = 0;
	public static final int GO_LAUNCHER_TYPE = 1;
	public static final int OTHER_LAUNCHER_TYPE = 2;

	private static boolean bizConfDebug = true;

	public static final String TAG = "biz conf";

	//old version should use 021
	public static final String SHANG_HAI_AREA_CODE = "21";
	public static final String BEI_JING_AREA_CODE = "10";
	
	public static void BIZ_CONF_DEBUG(String tag, String log) {

		if (bizConfDebug) {
			Log.d(TAG, "---" + tag + "---" + log);
		}
	}

	public static void DEBUG(String log) {

		if (bizConfDebug) {

			Log.d(TAG, log);
		}
	}
	
	public static String getLanguage() {
		
		Locale locale = Locale.getDefault();
		String language = String.format("%s_%s", 
								locale.getLanguage().toLowerCase(), 
								locale.getCountry().toLowerCase());
		
		String paramLanguage = Constant.SERVER_LANGUAGE_ZH;
		
		if (language.equalsIgnoreCase(Constant.LOCAL_EN)) {

			paramLanguage = Constant.SERVER_LANGUAGE_EN;
		} 
		
		return paramLanguage;
	}
	
	/** 
	 * 返回当前程序版本名 
	 */  
	public static String getVersionName(Context context) {  
		
	    String versionName = "";  
	    int versionCode = -1;
	    
	    try {  
	        // ---get the package info---  
	        PackageManager pm = context.getPackageManager();  
	        PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);  
	        versionName = pi.versionName; 
	        versionCode = pi.versionCode;
	        
	        //Util.BIZ_CONF_DEBUG(TAG, "current version code: " + versionCode);
	        
	        if (versionName == null || versionName.length() <= 0) {  
	            return "";  
	        }  
	        
	    } catch (Exception e) {  
	        //Util.BIZ_CONF_DEBUG(TAG,"Exception" + e.getMessage());  
	    }  
	    
	    return versionName;  
	}  
	
//	public static String getFormatPhoneNum(String phoneNumber) {
//		
//		String phoneNum = phoneNumber.replaceAll(" ", "").replace("+", "00");
//		String formatPhoneNum = phoneNum;
//		
//		do {
//			
//			//cell phone check
//			if (phoneNum.startsWith("1")) {
//				
//				if (isNeedAddZeroBeforePhone(phoneNum)) {
//					
//					formatPhoneNum = "0" + phoneNum;
//				}
//				
//				break;
//			}
//			
//			if (isNeedRemoveAreaCode(phoneNum)) {
//				
//				formatPhoneNum = phoneNum.substring(3, phoneNum.length());				
//				//Util.BIZ_CONF_DEBUG(TAG, "format fixed phone number: " + formatPhoneNum);				
//				break;
//			}
//			
//		}while(false);
//				
//		return formatPhoneNum;
//	}
	
	public static String getFormatPhoneNum(String phoneNumber) {

		boolean startWith00OrAdd = false;

		StringBuilder finalPhoneNumber = new StringBuilder();
		
		phoneNumber = phoneNumber.replaceAll(" ", "");
		
		do {

			if (phoneNumber.startsWith("+")) {

				startWith00OrAdd = true;
				phoneNumber = phoneNumber.replace("+", "");
			}

			if (phoneNumber.startsWith("00")) {

				startWith00OrAdd = true;
				phoneNumber = phoneNumber.substring(2, phoneNumber.length());
			}

			if (phoneNumber.startsWith("0")) {

				phoneNumber = phoneNumber.substring(1, phoneNumber.length());
			}

			if (startWith00OrAdd && !phoneNumber.startsWith("86")) {

				finalPhoneNumber.append("00").append(phoneNumber);
				break;
			}

			if (phoneNumber.startsWith("86")) {

				phoneNumber = phoneNumber.substring(2, phoneNumber.length());
			}

			if (phoneNumber.startsWith("1") && !phoneNumber.startsWith("10")) {
				
				if (isNeedAddZeroBeforePhone(phoneNumber)) {
					
					phoneNumber = "0" + phoneNumber;
				}
				
				finalPhoneNumber.append(phoneNumber);
				break;
			}
			
			if (isNeedRemoveAreaCode(phoneNumber)) {
				
				phoneNumber = phoneNumber.substring(2, phoneNumber.length());
				
				finalPhoneNumber.append(phoneNumber);								
				break;
			}
			
			finalPhoneNumber.append("0").append(phoneNumber);
		} while (false);
		
		Util.BIZ_CONF_DEBUG(TAG, "format fixed phone number: " + finalPhoneNumber.toString());
		return finalPhoneNumber.toString();
	}
	
	private static boolean isNeedRemoveAreaCode(String phoneNumber) {
		
		boolean isNeed = false;
		
		do{
		
			if (isShangHaiBridge() && phoneNumber.startsWith(SHANG_HAI_AREA_CODE)) {
				
				isNeed = true;
				break;
			}
			
			if (isBeijingBridge() && phoneNumber.startsWith(BEI_JING_AREA_CODE)) {
				
				isNeed = true;
				break;
			}
		}while(false);
		
		return isNeed;
	}
	
	private static boolean isNeedAddZeroBeforePhone(String phoneNumber) {
		
		boolean isNeed = true;
		
		do{
			
			if (isShangHaiBridge()) {
				
				isNeed = !isShangHaiNumber(phoneNumber);				
				break;
			}
			
			if (isBeijingBridge()) {
				
				isNeed = !isBeijingNumber(phoneNumber);
				break;
			}
			
		}while(false);
		
		return isNeed;
	}
	
	private static boolean isShangHaiBridge() {
		
		return LSSession.bridgeNumber.equals(String.valueOf(Constant.SHANG_HAI_BRIDGE));
	}
	
	private static boolean isBeijingBridge() {
		
		return LSSession.bridgeNumber.equals(String.valueOf(Constant.BEI_JING_BRIDGE));
	}
	
	/**
	 * Check a number location, if it is not shang hai number, we should add "0"
	 * before it for out call
	 * @param number
	 * @return
	 */
	private static boolean isShangHaiNumber(String number) {
		
		boolean isShangHaiNumber = 
				NumSegmentManager.getInstance().isShangHaiNum(number);
			
		return isShangHaiNumber;
	}
	
	public static boolean isBeijingNumber(String number) {
		
		return NumSegmentManager.getInstance().isBeijingNumber(number);
	}
	
	public static DisplayMetrics getScreenSize() {
		
		final DisplayMetrics metrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) AppClass.getInstance()
				.getApplicationContext().getSystemService(
						Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(metrics);
		
		return metrics;
	}
	
	public static void startActivityForResult(Activity activity, Class<?> cls, int requestCode) {

		Intent intent = new Intent();
		intent.setClass(activity, cls);

		activity.startActivityForResult(intent, requestCode);
	}
	
	public static void startActivity(Context ctx, Class<?> cls) {

		Intent intent = new Intent();
		intent.setClass(ctx, cls);

		ctx.startActivity(intent);
	}
	
	public static void startActivity(Context ctx, Class<?> cls, List<Integer> flags) {

		Intent intent = new Intent();
		intent.setClass(ctx, cls);
		
		if(!Util.isEmpty(flags) && flags.size() > 0) {
			
			for(int flag : flags) {
				
				intent.setFlags(flag);
			}
		}
		
		ctx.startActivity(intent);
	}
	
	public static boolean isNetworkReadyForConf(Context context) {

		boolean isNetworkReady = false;

		do {
			
			isNetworkReady = Util.isNetworkAvailable(context);					
			
			//no need to check 3G or wifi network, if network avaiable, return true
			if (true) {
				
				break;
			}
			
			if (!Util.isNetworkAvailable(context)) {

				//Util.shortToast(context, R.string.toast_network_unavailable);
				break;
			}

			if (Util.isWifiConnected(context)) {

				isNetworkReady = true;
				break;
			}

			if (!Util.isFastMobileNetwork(context)) {

				//Util.shortToast(context, R.string.toast_network_not_fast);
				break;
			}

			isNetworkReady = true;

		} while (false);

		return isNetworkReady;
	}

	/**
	 * give focus to a fixed view
	 * 
	 * @param v
	 */
	public static void requestFocus(View v) {

		v.setFocusable(true);
		v.setFocusableInTouchMode(true);
		v.requestFocus();
		v.requestFocusFromTouch();
	}

	public static void shortToast(Context ctx, String msg) {

		//Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
		showToast(ctx, msg, Toast.LENGTH_SHORT);
	}

	public static void shortToast(Context ctx, int strId) {

		//Toast.makeText(ctx, ctx.getString(strId), Toast.LENGTH_SHORT).show();
		shortToast(ctx, ctx.getString(strId));
	}

	public static boolean getSPBool(Context ctx, String key,boolean defaultValue) {

		boolean value = 
				new SharedPreferenceOperator(ctx).getBoolean(key,defaultValue);

		return value;
	}

	public static void setSpBoolValue(Context ctx, String key, boolean value) {

		new SharedPreferenceOperator(ctx).putBoolean(key, value);
	}

	public static void setSpStringValue(Context ctx, String key, String value) {

		new SharedPreferenceOperator(ctx).putString(key, value);
	}

	public static String getSPString(Context ctx, String key,String defaultValue) {

		String value = 
				new SharedPreferenceOperator(ctx).getString(key,defaultValue);

		return value;
	}

	public static void setSpInt(Context ctx, String key, int value) {

		new SharedPreferenceOperator(ctx).putInt(key, value);
	}

	public static int getSPInt(Context ctx, String key, int defaultValue) {

		int value = 
				new SharedPreferenceOperator(ctx).getInt(key, defaultValue);

		return value;
	}
	
	public static boolean isSpContainsKey(Context ctx, String key) {
		
		return new SharedPreferenceOperator(ctx).contains(key);
	}
	
	public static String getIMEI(Context mContext) {
		TelephonyManager telephonyManager = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = telephonyManager.getDeviceId();
		return imei;
	}

	public static boolean getBoolean(String value) {
		if (getInt(value) == 0) {
			return false;
		} else {
			return true;
		}
	}

	public static double getDouble(String value) {
		if (value == null)
			return 0.0;

		try {
			return Double.parseDouble(value.trim());
		} catch (NumberFormatException e) {
			return 0.0;
		}
	}

	public static float getFloat(String value) {
		if (value == null)
			return 0f;

		try {
			return Float.parseFloat(value.trim());
		} catch (NumberFormatException e) {
			return 0f;
		}
	}

	public static int getInt(int radix, String value) {
		return getInt(radix, value, 0);
	}

	public static int getInt(int radix, String value, int faultValue) {
		if (value == null) {
			return faultValue;
		}

		try {
			/*
			 * Integer.parseInt("ffffffff", 16) will fail, have to use Long...,
			 * -_-
			 */
			return (int) Long.parseLong(value.trim(), radix);
		} catch (NumberFormatException e) {
			return faultValue;
		}
	}

	/**
	 * <p>
	 * Parse int value from string
	 * </p>
	 * 
	 * @param value
	 *            string
	 * @return int value
	 */
	public static int getInt(String value) {
		return getInt(10, value);
	}

	public static int getInt(String value, int faultValue) {
		return getInt(10, value, faultValue);
	}

	public static long getLong(String value) {
		if (value == null)
			return 0L;

		try {
			return Long.parseLong(value.trim());
		} catch (NumberFormatException e) {
			return 0L;
		}
	}

	/**
	 * <p>
	 * Get string in UTF-8 encoding
	 * </p>
	 * 
	 * @param b
	 *            byte array
	 * @return string in utf-8 encoding, or empty if the byte array is not
	 *         encoded with UTF-8
	 */
	public static String getUTF8String(byte[] b) {
		if (b == null)
			return "";
		return getUTF8String(b, 0, b.length);
	}

	/**
	 * <p>
	 * Get string in UTF-8 encoding
	 * </p>
	 */
	public static String getUTF8String(byte[] b, int start, int length) {
		if (b == null) {
			return "";
		} else {
			try {
				return new String(b, start, length, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				return "";
			}
		}
	}

	/**
	 * 绘制旋转图片，以图片中心来旋转
	 * 
	 * @param c
	 * @param bmp
	 * @param x
	 * @param y
	 * @param degree
	 * @param p
	 */
	public static void drawRotateBitmap(Canvas c, Bitmap bmp, float x, float y,
			float degree, Paint p) {
		if (bmp == null) {
			return;
		} else {
			// matirx版效果更好些，但是处理更慢些
			// Matrix matrix = new Matrix();
			// matrix.setRotate(degree);
			// Bitmap resizeBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
			// bmp.getHeight(), matrix, true);
			// drawBitmap(c, resizeBmp, x, y, p);
			c.save();
			c.rotate(degree, x + (bmp.getWidth() >> 1), y
					+ (bmp.getHeight() >> 1));
			drawBitmap(c, bmp, x, y, p);
			c.restore();
		}
	}

	/**
	 * 以图片中心做缩放
	 * 
	 * @param c
	 * @param bmp
	 * @param x
	 * @param y
	 * @param zoomSize
	 * @param p
	 */
	public static void drawScaleBitmap(Canvas c, Bitmap bmp, float x, float y,
			float zoomSize, Paint p) {
		if (bmp == null) {
			return;
		} else {
			if (zoomSize == 1) {
				drawBitmap(c, bmp, x, y, p);
				return;
			}
			// Matrix matrix = new Matrix();
			// matrix.postScale(zoomSize, zoomSize);
			// Bitmap resizeBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
			// bmp.getHeight(), matrix, true);
			// int coordw = (int)(bmp.getWidth()*zoomSize - bmp.getWidth())>>1;
			// int coordh = (int)(bmp.getHeight()*zoomSize -
			// bmp.getHeight())>>1;
			// drawBitmap(c, resizeBmp, x-coordw, y-coordh, p);

			c.save();
			c.scale(zoomSize, zoomSize, x + (bmp.getWidth() >> 1),
					y + (bmp.getHeight() >> 1));
			drawBitmap(c, bmp, x, y, p);
			c.restore();
		}
	}

	/**
	 * 缩放sd卡上的图片
	 * 
	 * @param path
	 * @param newWidth
	 * @return
	 */
	public static Bitmap resizePicFile(String path, int newWidth) {
		Bitmap bitmap = getBitmapFromFile(path);
		if (bitmap == null) {
			return null;
		}
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		if (width <= newWidth)
			return bitmap;
		float temp = ((float) height) / ((float) width);
		int newHeight = (int) ((newWidth) * temp);
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		// bitmap.recycle();
		Bitmap2File(bitmap, path);
		return bitmap;
	}

	public static Bitmap getBitmapFromFile(String path) {
		Bitmap rs = null;
		try {
			rs = BitmapFactory.decodeFile(path);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError er) {
			er.printStackTrace();
		}
		if (rs == null)
			removeFile(path);
		return rs;
	}

	/** 将Bitmap转成文件 */
	public static void Bitmap2File(Bitmap bitmap, String filePath) {

		try {
			File path = new File(filePath);
			FileOutputStream fos = new FileOutputStream(path);
			bitmap.compress(CompressFormat.JPEG, 90, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	/**
	 * 绘制半透明图。
	 * 
	 * @param c
	 * @param bmp
	 * @param x
	 * @param y
	 * @param alpha
	 *            0-255
	 * @param p
	 */
	public static void drawAlphaBitmap(Canvas c, Bitmap bmp, float x, float y,
			int alpha, Paint p) {
		if (bmp == null) {
			return;
		}
		p.setAlpha(alpha);
		drawBitmap(c, bmp, x, y, p);
	}

	/**
	 * 统一的绘制Bitmap的接口
	 * 
	 * @param c
	 * @param bmp
	 * @param x
	 *            9999 居中，8888底部
	 * @param y
	 *            9999 居中，8888底部
	 * @param p
	 */
	public static void drawBitmap(Canvas c, Bitmap bmp, float x, float y,
			Paint p) {
		if (bmp == null) {
			DEBUG("no BMP1:" + bmp);
			return;
		} else {
			if (x == 9999) {
				x = (scrWidth - bmp.getWidth()) >> 1;
			} else if (x == 8888) {
				x = scrWidth - bmp.getWidth();
			}
			if (y == 9999) {
				y = (scrHeight - bmp.getHeight()) >> 1;
			} else if (y == 8888) {
				y = scrHeight - bmp.getHeight();
			}
			c.drawBitmap(bmp, x, y, p);
		}
	}

	/**
	 * 绘制图片剪辑。
	 * 
	 * @param c
	 * @param bmp
	 * @param clipx
	 *            相对于图片左上角坐标(0,0)
	 * @param clipy
	 * @param clipw
	 * @param cliph
	 * @param x
	 *            图片要绘制在屏幕上的位置
	 * @param y
	 * @param p
	 */
	public static void drawRegion(Canvas c, Bitmap bmp, float clipx,
			float clipy, int clipw, int cliph, float x, float y, Paint p) {
		if (bmp == null) {
			DEBUG("no BMP2:" + bmp);
			return;
		} else {
			c.save();
			c.clipRect(x, y, x + clipw, y + cliph);
			c.drawBitmap(bmp, x - clipx, y - clipy, p);
			c.restore();
		}
	}

	/**
	 * 统一的绘制Movie的接口
	 * 
	 * @param c
	 * @param mov
	 * @param x
	 * @param y
	 */
	public static void drawMovie(Canvas c, Movie mov, float x, float y) {
		if (mov == null) {
			DEBUG("no MOV:" + mov);
			return;
		} else {
			mov.draw(c, x, y);
		}
	}

	/**
	 * 绘制图片数字
	 * 
	 * @param c
	 * @param bmp
	 *            数字图片：0123456789-（的图片，每个数字等宽）
	 * @param num
	 *            要画的数字
	 * @param x
	 *            9999，x轴居中；8888底部
	 * @param y
	 *            9999，y轴居中；8888底部
	 */
	public static void drawImageNum(Canvas c, Bitmap bmp, int num, float x,
			float y, Paint p) {
		int numWidth = bmp.getWidth() / 11;

		boolean minus = false;
		if (num < 0) {
			minus = true;
			num = Math.abs(num);
		}

		int bit = bitsOfNum(num);

		if (x == 9999) {
			if (minus) {
				x = (scrWidth - numWidth * (bit + 1)) >> 1;
			} else {
				x = (scrWidth - numWidth * bit) >> 1;
			}
		} else if (x == 8888) {
			if (minus) {
				x = scrWidth - numWidth * (bit + 1);
			} else {
				x = scrWidth - numWidth * bit;
			}
		}
		if (y == 9999) {
			y = (scrHeight - bmp.getHeight()) >> 1;
		} else if (y == 8888) {
			y = scrHeight - bmp.getHeight();
		}

		// 画负号
		if (minus) {
			drawRegion(c, bmp, 10 * numWidth, 0, numWidth, bmp.getHeight(), x,
					y, p);
		}

		for (int i = bit; i > 0; i--) {
			int pow = (int) Math.pow(10, i - 1);
			int n = num / (pow);
			num %= pow;
			if (minus) {
				drawRegion(c, bmp, n * numWidth, 0, numWidth, bmp.getHeight(),
						x + (bit - i + 1) * numWidth, y, p);
			} else {
				drawRegion(c, bmp, n * numWidth, 0, numWidth, bmp.getHeight(),
						x + (bit - i) * numWidth, y, p);
			}
		}
	}

	// 算一个数字有多少位
	private static int bitsOfNum(int num) {
		int result = 1;
		while (num / 10 != 0) {
			result++;
			num /= 10;
		}
		return result;
	}

	/**
	 * 判断矩形重叠
	 * 
	 * @param ax
	 * @param ay
	 * @param aw
	 * @param ah
	 * @param bx
	 * @param by
	 * @param bw
	 * @param bh
	 * @return
	 */
	public static boolean overlap(float ax, float ay, int aw, int ah, float bx,
			float by, int bw, int bh) {
		boolean result = false;

		if (ax <= bx + bw && ax + aw >= bx && ay <= by + bh && ay + ah >= by) {
			result = true;
		} else {
			result = false;
		}

		return result;
	}

	public static void longToast(Context ctx, String str) {
		
		if (isEmpty(str)) {
			
			str = "null";
		}
		
		showToast(ctx, str, Toast.LENGTH_LONG);
		//Toast.makeText(ctx, str, Toast.LENGTH_LONG).show();
	}
	
	public static void longToast(Context ctx, int strId) {

		//Toast.makeText(ctx, ctx.getString(strId), Toast.LENGTH_LONG).show();
		longToast(ctx, ctx.getString(strId));
	}
	
	private static void showToast(Context ctx, String content, int duration) {
		  
		 TextView textView = new TextView(ctx);  
		 textView.setBackgroundColor(Color.BLACK);  
		 textView.setTextColor(Color.WHITE);  
		 textView.setPadding(10, 10, 10, 10);  
		 textView.setText(content);  
		 Toast toastView = new Toast(ctx);  
		 toastView.setDuration(duration);  
		 toastView.setGravity(Gravity.CENTER, 0, 0);  
		 toastView.setView(textView);  
		 toastView.show(); 
	}
	
	public static String getStringFromR(Context ctx, int i) {
		if (ctx == null)
			return "null";
		String rs = ctx.getResources().getString(i);
		return rs;
	}

	/**
	 * 判断网络是否连接
	 * 
	 * @param ctx
	 * @return
	 */
	public static boolean isNetworkAvailable(Context ctx) {

		boolean isConnectNewwork = false;
		
		Context context = ctx.getApplicationContext();
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		do{
			
			if (null == connectivity) {				
				break;
			}
			
			NetworkInfo wifiInfo = connectivity
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

			NetworkInfo mobInfo = connectivity
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			
			if ((null != wifiInfo && wifiInfo.isConnected())
					|| (null != mobInfo && mobInfo.isConnected())) {
				isConnectNewwork = true;
			}
		}while(false);
		
		return isConnectNewwork;
	}

	/**
	 * 是否wifi网络
	 * 
	 * @param ctx
	 * @return
	 */
	public static boolean isWifiConnected(Context ctx) {

		Context context = ctx.getApplicationContext();
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo wifiInfo = connectivity
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		return wifiInfo.isConnected();
	}

	/** 没有网络 */
	public static final int NETWORKTYPE_INVALID = 0;
	/** wap网络 */
	public static final int NETWORKTYPE_WAP = 1;
	/** 2G网络 */
	public static final int NETWORKTYPE_2G = 2;
	/** 3G和3G以上网络，或统称为快速网络 */
	public static final int NETWORKTYPE_3G = 3;
	/** wifi网络 */
	public static final int NETWORKTYPE_WIFI = 4;

	/**
	 * 是否是快速网络，3G或者以上网速的网络
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isFastMobileNetwork(Context context) {

		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		switch (telephonyManager.getNetworkType()) {
		case TelephonyManager.NETWORK_TYPE_1xRTT:
			return false; // ~ 50-100 kbps
		case TelephonyManager.NETWORK_TYPE_CDMA:
			return false; // ~ 14-64 kbps
		case TelephonyManager.NETWORK_TYPE_EDGE:
			return false; // ~ 50-100 kbps
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
			return true; // ~ 400-1000 kbps
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
			return true; // ~ 600-1400 kbps
		case TelephonyManager.NETWORK_TYPE_GPRS:
			return false; // ~ 100 kbps
		case TelephonyManager.NETWORK_TYPE_HSDPA:
			return true; // ~ 2-14 Mbps
		case TelephonyManager.NETWORK_TYPE_HSPA:
			return true; // ~ 700-1700 kbps
		case TelephonyManager.NETWORK_TYPE_HSUPA:
			return true; // ~ 1-23 Mbps
		case TelephonyManager.NETWORK_TYPE_UMTS:
			return true; // ~ 400-7000 kbps
		case TelephonyManager.NETWORK_TYPE_EHRPD:
			return true; // ~ 1-2 Mbps
		case TelephonyManager.NETWORK_TYPE_EVDO_B:
			return true; // ~ 5 Mbps
		case TelephonyManager.NETWORK_TYPE_HSPAP:
			return true; // ~ 10-20 Mbps
		case TelephonyManager.NETWORK_TYPE_IDEN:
			return false; // ~25 kbps
		case TelephonyManager.NETWORK_TYPE_LTE:
			return true; // ~ 10+ Mbps
		case TelephonyManager.NETWORK_TYPE_UNKNOWN:
			return false;
		default:
			return false;
		}
	}

	/**
	 * 获取网络状态，wifi,wap,2g,3g.
	 * 
	 * @param context
	 *            上下文
	 * @return int 网络状态 {@link #NETWORKTYPE_2G},{@link #NETWORKTYPE_3G},          *
	 *         {@link #NETWORKTYPE_INVALID},{@link #NETWORKTYPE_WAP}*
	 *         <p>
	 *         {@link #NETWORKTYPE_WIFI}
	 */
	public static int getNetWorkType(Context context) {

		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();

		int netWorkType = NETWORKTYPE_INVALID;

		if (networkInfo != null && networkInfo.isConnected()) {
			String type = networkInfo.getTypeName();

			if (type.equalsIgnoreCase("WIFI")) {
				netWorkType = NETWORKTYPE_WIFI;
			} else if (type.equalsIgnoreCase("MOBILE")) {
				String proxyHost = android.net.Proxy.getDefaultHost();

				netWorkType = isEmpty(proxyHost) ? (isFastMobileNetwork(context) ? NETWORKTYPE_3G
						: NETWORKTYPE_2G)
						: NETWORKTYPE_WAP;
			}
		} else {
			netWorkType = NETWORKTYPE_INVALID;
		}

		return netWorkType;
	}

	/**
	 * 判断是否是3G网络,可以用此方法做判断，但是判断的不是很全面
	 * 
	 * @param ctx
	 * @return
	 */
	public static boolean is3GConnect(Context ctx) {

		boolean is3GConnect = false;

		ConnectivityManager connectMgr = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo info = connectMgr.getActiveNetworkInfo();

		if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {

			int type = info.getSubtype();

			switch (type) {
			// 联通3G网络
			case TelephonyManager.NETWORK_TYPE_UMTS:
			case TelephonyManager.NETWORK_TYPE_HSDPA:
				// 电信3G网络
			case TelephonyManager.NETWORK_TYPE_EVDO_0:
			case TelephonyManager.NETWORK_TYPE_EVDO_A:
			case TelephonyManager.NETWORK_TYPE_EVDO_B:

				is3GConnect = true;
				break;

			// 移动2G网络
			case TelephonyManager.NETWORK_TYPE_GPRS:
			case TelephonyManager.NETWORK_TYPE_EDGE:
			case TelephonyManager.NETWORK_TYPE_CDMA:
				is3GConnect = false;
				break;

			default:
				break;
			}
		}

		return is3GConnect;
	}

	/**
	 * 
	 * @param o
	 * @return
	 */
	public static boolean isEmpty(Object o) {
		if (o == null || o.toString().equals("") || o.toString().equals("null"))
			return true;
		return false;
	}

	public static int readDimen(Context ctx, int resId) {
		int rs = ctx.getResources().getDimensionPixelSize(resId);
		return rs;
	}

	public static String replaceString(Context ctx, int i, Object... args) {
		String format = getStringFromR(ctx, i);
		String rs = String.format(format, args);
		return rs;
	}

	/**
	 * 从图片url中获得图片名
	 * 
	 * @param url
	 * @return
	 */
	public static String getPicNameFromUrlWithSuff(String url) {
		// String str =
		// "http://www.cocplay.com/upload_files/article/2/201003/1_rnazx__12699103296453904.jpg";
		String str = url;
		String[] s = str.split("\\/");
		str = s[s.length - 1];

		// s = str.split("\\.");
		// str = s[0];
		return str;
	}

	/**
	 * @param path
	 * @return
	 */
	public static String renameString(String path) {
		if (path == null) {
			return null;
		}
		return path.replace(".png", ".a").replace(".jpg", ".b")
				.replace(".gif", ".c").replace(".mp3", ".d");
	}

	/**
	 * 删除目录(含文件)
	 * 
	 * @param path
	 */
	public static void removeFile(File path) {
		try {
			if (path.isDirectory()) {
				File[] child = path.listFiles();
				if ((child != null) && (child.length != 0)) {
					for (int i = 0; i < child.length; i++) {
						removeFile(child[i]);
						child[i].delete();
					}
				}
			}
			path.delete();
		} catch (Exception e) {

		}
	}

	public static void removeFile(String path) {
		if (Util.isEmpty(path))
			return;
		removeFile(new File(path));
	}

	public static void simulateHomePress(Context ctx) {

		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addCategory(Intent.CATEGORY_HOME);
		ctx.startActivity(intent);
	}

	public static String Utf8URLencode(String text) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if ((c >= 0) && (c <= 255)) {
				result.append(c);
			} else {
				byte[] b = new byte[0];
				try {
					b = Character.toString(c).getBytes("UTF-8");
				} catch (Exception ex) {
				}
				for (int j = 0; j < b.length; j++) {
					int k = b[j];
					if (k < 0)
						k += 256;
					result.append("%" + Integer.toHexString(k).toUpperCase());
				}
			}
		}
		return result.toString();
	}

	public static Bitmap scaleImg(Bitmap bm, int newWidth, int newHeight) {
		// 图片源
		// Bitmap bm = BitmapFactory.decodeStream(getResources()
		// .openRawResource(id));
		// 获得图片的宽高
		int width = bm.getWidth();
		int height = bm.getHeight();
		// 设置想要的大小
		int newWidth1 = newWidth;
		int newHeight1 = newHeight;
		// 计算缩放比例
		float scaleWidth = ((float) newWidth1) / width;
		float scaleHeight = ((float) newHeight1) / height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// 得到新的图片
		Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
				true);
		return newbm;
	}
	
	/**
	 * 判断麦克风是否打开
	 * @return
	 */
	public static boolean isHFOpen(){
		
		AudioManager audioManager = (AudioManager) AppClass.getInstance()
						.getSystemService(Context.AUDIO_SERVICE);
		
		boolean isHfOn = false;
		
		if (audioManager != null) {
			if (audioManager.isSpeakerphoneOn()) {
				
				isHfOn = true;
			}
		}
		
		return isHfOn;
	}
	
	// 打开扬声器
	public static void OpenSpeaker() {

		Context ctx = AppClass.getInstance();
		
		try {
			AudioManager audioManager = (AudioManager) ctx
					.getSystemService(Context.AUDIO_SERVICE);
			audioManager.setMode(AudioManager.ROUTE_SPEAKER);
	
			if (!audioManager.isSpeakerphoneOn()) {
				audioManager.setSpeakerphoneOn(true);

/*				audioManager
						.setStreamVolume(
								AudioManager.STREAM_VOICE_CALL,
								audioManager
										.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
								AudioManager.STREAM_VOICE_CALL);*/
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	// 关闭扬声器
	public static void CloseSpeaker() {

		Context ctx = AppClass.getInstance();

		try {
			AudioManager audioManager = (AudioManager) ctx
					.getSystemService(Context.AUDIO_SERVICE);

			int currVolume = audioManager
					.getStreamVolume(AudioManager.STREAM_VOICE_CALL);

			if (audioManager != null) {
				if (audioManager.isSpeakerphoneOn()) {
					audioManager.setSpeakerphoneOn(false);
					audioManager.setStreamVolume(
							AudioManager.STREAM_VOICE_CALL, currVolume,
							AudioManager.STREAM_VOICE_CALL);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Toast.makeText(context,"close speaker",Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * 柔化效果(高斯模糊)
	 * 
	 * @param bmp
	 * @return
	 */
	public static Bitmap blurImageAmeliorate(Bitmap bmp) {
		long start = System.currentTimeMillis();
		// 高斯矩阵
		int[] gauss = new int[] { 1, 2, 1, 2, 4, 2, 1, 2, 1 };

		int width = bmp.getWidth();
		int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);

		int pixR = 0;
		int pixG = 0;
		int pixB = 0;

		int pixColor = 0;

		int newR = 0;
		int newG = 0;
		int newB = 0;

		int delta = 16; // 值越小图片会越亮，越大则越暗

		int idx = 0;
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		for (int i = 1, length = height - 1; i < length; i++) {
			for (int k = 1, len = width - 1; k < len; k++) {
				idx = 0;
				for (int m = -1; m <= 1; m++) {
					for (int n = -1; n <= 1; n++) {
						pixColor = pixels[(i + m) * width + k + n];
						pixR = Color.red(pixColor);
						pixG = Color.green(pixColor);
						pixB = Color.blue(pixColor);

						newR = newR + (int) (pixR * gauss[idx]);
						newG = newG + (int) (pixG * gauss[idx]);
						newB = newB + (int) (pixB * gauss[idx]);
						idx++;
					}
				}

				newR /= delta;
				newG /= delta;
				newB /= delta;

				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));

				pixels[i * width + k] = Color.argb(255, newR, newG, newB);

				newR = 0;
				newG = 0;
				newB = 0;
			}
		}

		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		long end = System.currentTimeMillis();
		Log.d("may", "used time=" + (end - start));
		return bitmap;
	}
}
