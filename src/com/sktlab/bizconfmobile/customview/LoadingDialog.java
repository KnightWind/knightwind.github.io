package com.sktlab.bizconfmobile.customview;

import com.sktlab.bizconfmobile.util.Util;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

public class LoadingDialog {
	
	public static final String TAG = "LoadingDialog";
	
	//private static LoadingDialog mInstance;
	private Activity mActivity;
	private WindowManager mWindowManager;
	private LayoutParams mDialogViewLayoutPara;
	private View mDialog;
	private boolean mIsShow;
	
//	public static LoadingDialog getInstance(Activity ctx, int layoutId){
//		
//		if(null == mInstance){
//			
//			mInstance = new LoadingDialog(ctx,layoutId);		
//		}
//			
//		return mInstance;		
//	}
	
	public LoadingDialog(Activity ctx, int layoutId){
		mActivity = ctx;
		
		mDialog = LayoutInflater.from(ctx).inflate(layoutId, null);
		init();
	}
	
	private void init(){
		
		mWindowManager = mActivity.getWindowManager();
		mDialogViewLayoutPara = new LayoutParams();
		mDialogViewLayoutPara.width = LayoutParams.WRAP_CONTENT;
		mDialogViewLayoutPara.height = LayoutParams.WRAP_CONTENT;
		mDialogViewLayoutPara.format = PixelFormat.RGBA_8888;	
		mDialogViewLayoutPara.type = LayoutParams.TYPE_SYSTEM_ERROR;
		mDialogViewLayoutPara.gravity = Gravity.CENTER;
		//mHideViewLayoutPara.flags = 1280;	
		mDialogViewLayoutPara.flags = LayoutParams.FLAG_DISMISS_KEYGUARD;
	}
	
	public synchronized void showDialog(){
		
		if(null != mDialog && !mIsShow){
			mWindowManager.addView(mDialog, mDialogViewLayoutPara);
		}
		mIsShow = true;
	}
	
	public synchronized void showDialog(int msgWidgetId, String msg){
		
		TextView tvMsg = (TextView)mDialog.findViewById(msgWidgetId);
		
		if(tvMsg != null) {
			
			tvMsg.setText(msg);
		}
		
		try {
			
			if(null != mDialog && !mIsShow){
				mWindowManager.addView(mDialog, mDialogViewLayoutPara);
			}
			
			
			mIsShow = true;
		} catch (Exception e) {
			e.printStackTrace();
			
			//Util.BIZ_CONF_DEBUG(TAG, "loading dialog error");
		}
	}
	
	public synchronized void dismissDialog(){
		
		try {
			
			if(null != mDialog && mIsShow){
				mWindowManager.removeView(mDialog);
			}
		} catch (Exception e) {
			e.printStackTrace();
			
			//Util.BIZ_CONF_DEBUG(TAG, "dismiss dialog error");
		}
		mIsShow = false;
	}
	
	public boolean isShow(){
		
		return mIsShow;
	}
}
