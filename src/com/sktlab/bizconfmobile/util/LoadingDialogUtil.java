package com.sktlab.bizconfmobile.util;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.customview.LoadingDialog;
import com.sktlab.bizconfmobile.interfaces.ILoadingDialogCallback;

public class LoadingDialogUtil {
	
	public static final String TAG = "LoadingDialogUtil";
	
	private Activity mActivity;	
	private LoadingDialog mDialog;
	private ILoadingDialogCallback mCallback;
	
	private final int SHOW_DIALOG = 1;
	private final int CLOSE_DIALOG_SUCCESS = 2;
	private final int CLOSE_DIALOG_ERROR = 3;
	
	private final String KEY_OF_MSG = "msg";
	
	private Timer mTimer;
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			
			switch(msg.what) {
			
			case SHOW_DIALOG:
				
				mHandler.removeMessages(SHOW_DIALOG);
				
				Bundle data = msg.getData();
				
				String message = data.getString(KEY_OF_MSG);
				
				finishDialog();
				
				showLoadingDialog(message);
				break;
				
			case CLOSE_DIALOG_SUCCESS:
				
				cancleTimer();
				mHandler.removeMessages(CLOSE_DIALOG_SUCCESS);
				finishDialog(true);						
				break;
				
			case CLOSE_DIALOG_ERROR:	
				
				cancleTimer();
				mHandler.removeMessages(CLOSE_DIALOG_ERROR);
				finishDialog(false);									
				break;	
			}
		}	
	};
	
	public LoadingDialogUtil(Activity activity, ILoadingDialogCallback callback) {
		
		mActivity = activity;
		mDialog = new LoadingDialog(mActivity, R.layout.loading_dialog);
		setCallback(callback);
	}
	
	private void cancleTimer() {
		
		if (null != mTimer) {
			
			mTimer.cancel();
			mTimer = null;
		}
	}
	
	public void showDialog(String toastText) {
		
		Message msg = new Message();				
		Bundle data = new Bundle();
		data.putString(KEY_OF_MSG, toastText);
		msg.setData(data);
		msg.what = SHOW_DIALOG;
		
		mHandler.sendMessage(msg);
	}
	
	public void showDialog(int  strId) {
		
		String toastText = mActivity.getResources().getString(strId);
		
		showDialog(toastText);
	}
	
	/**
	 * 
	 * @param toastText
	 * @param timeOut  if specified time had consumed,but the dialog had not been dismiss,
	 * 					it will be dismissed auto
	 */
	public void showDialog(String toastText, long timeOut) {
		
		Message msg = new Message();				
		Bundle data = new Bundle();
		data.putString(KEY_OF_MSG, toastText);
		msg.setData(data);
		msg.what = SHOW_DIALOG;
		
		mHandler.sendMessage(msg);
		
		final long autoTimeOut = timeOut;
		
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				
				finishDialogWithErrorMsg(autoTimeOut);
			}
		}, timeOut);
		
	}
	
	public void showDialog(int strId, long timeOut) {
		
		String toastText = mActivity.getResources().getString(strId);
		
		showDialog(toastText, timeOut);
	}
	
	public void finishDialogSuccessDone(){
			
		mHandler.sendEmptyMessage(CLOSE_DIALOG_SUCCESS);
	}
	
	public void finishDialogWithErrorMsg() {
		
		mHandler.sendEmptyMessage(CLOSE_DIALOG_ERROR);
	}
	
	private void finishDialogWithErrorMsg(long timeOut) {
		
		Message msg = new Message();
		msg.what = CLOSE_DIALOG_ERROR;
		
		mHandler.sendMessageDelayed(msg, timeOut);
		//Util.BIZ_CONF_DEBUG(TAG, "loading dialog time out~ send msg to close dialog");
	}
	
	private void showLoadingDialog(String msg) {

		if (null != mDialog && !mDialog.isShow()) {
			
			mDialog.showDialog(R.id.tv_loading_dialog_msg, msg);
		}
	}

	private void finishDialog() {

		if (null != mDialog && mDialog.isShow()) {

			mDialog.dismissDialog();
		}
	}
	
	private void finishDialog(boolean isSuccess) {

		if (null != mDialog && mDialog.isShow()) {

			mDialog.dismissDialog();
			
			if(mCallback != null) {
				
				if (isSuccess) {
					
					mCallback.onSuccessDone();
				}else {
					
					mCallback.onDoneWithError();
				}			
			}
		}
	}

	public ILoadingDialogCallback getCallback() {
		return mCallback;
	}

	public void setCallback(ILoadingDialogCallback mCallback) {
		this.mCallback = mCallback;
	}
}
