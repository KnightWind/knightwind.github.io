package com.sktlab.bizconfmobile.activity;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.model.ConfAccount;
import com.sktlab.bizconfmobile.model.Constant;
import com.sktlab.bizconfmobile.model.manager.AccountsManager;
import com.sktlab.bizconfmobile.model.manager.AppointmentConfManager;
import com.sktlab.bizconfmobile.model.manager.CommunicationManager;
import com.sktlab.bizconfmobile.model.manager.ConfHistoryManager;
import com.sktlab.bizconfmobile.model.manager.LogcatFileManager;
import com.sktlab.bizconfmobile.model.manager.NumSegmentManager;
import com.sktlab.bizconfmobile.util.FileUtil;
import com.sktlab.bizconfmobile.util.Util;
import com.umeng.analytics.MobclickAgent;

/**
 * @Description: welcom screen
 * 
 * @Author wenjuan.li
 * 
 * @date 2013-07-23
 * 
 * @version V1.0
 */
public class WelcomeActivity extends Activity {
	
	
	public static final String TAG = "WelcomeActivity"; 
	
	public static final String LAUNCH_ACTION 
				= "com.sktlab.bizconfmobile.launch.welcomeactivity";
	
	public static final int SIGNAL_SIZE = 3;
	
	public static boolean passwdIsRight = true;
	
	public static boolean flag=true;
	
	private final int SHOW_MAIN_ACTIVITY = 1;
	
	private long beginTime = 0;
	
	//This variable used to monitor the clear memory operation of the user,when clear memory,our app should restart
	//we add some object to it in welcomeActivity
	public static List<ConfAccount> signal =  null;
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {

			case SHOW_MAIN_ACTIVITY:

				startMainActivity();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setFullScreen();
		setContentView(R.layout.welcomelogo);
		
		startLogcatManager();
		
		if (!CommunicationManager.getInstance().isInConfManageScreen()
				&&!CommunicationManager.getInstance().isTurn2HomePage()) {
			
			initLinkData();
		}
	}
	
	private void startLogcatManager() {
        
		if (AppClass.getInstance().isGenerateLogFile()) {
			
			String folderPath = null;
	        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {// save in SD card first 
	            
	        	FileUtil.createFileDir(FileUtil.LOG_PATH);
	        	folderPath = FileUtil.LOG_PATH;
	        } else {// If the SD card does not exist, save in the directory of application.
	 
	            folderPath = this.getFilesDir().getAbsolutePath() + File.separator + "BDT-Logcat";  
	        }
	        
	        LogcatFileManager.getInstance().start(folderPath);  
		}
    } 
	
	@Override
	protected void onResume() {
		super.onResume();
		
		MobclickAgent.onResume(this);
		do{
			if (CommunicationManager.getInstance().isInConfManageScreen() 
					|| CommunicationManager.getInstance().isTurn2HomePage()) {

				Util.BIZ_CONF_DEBUG(TAG, "in conference screen");
				
				Util.longToast(this, R.string.toast_not_enter_conference);
				Intent confIntent = new Intent();
				confIntent.setClass(this, ConferenceActivity.class);
				this.startActivity(confIntent);
				finish();
				break;
			}
			init();
			AppClass.getInstance().setNeed2Exit(false);
		}while(false);
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private void initLinkData() {
		
		do {
			Uri data = getIntent().getData();
			
			if (null == data) {
				
				break;
			}
			
			String scheme = data.getScheme(); 
			String host = data.getHost(); 
			
			Util.BIZ_CONF_DEBUG(TAG, "scheme: " + scheme + "host: " + host);
			
			List<String> params = data.getPathSegments();
			
			if (null == params) {
				
				break;
			}
			
			CommunicationManager.getInstance().setLinkStartConf(true);
			
			String confCode = params.get(0); 			
			
			AppClass.getInstance().setLinkConfCode(confCode);
			
			Util.BIZ_CONF_DEBUG(TAG, "confCode: " + confCode);
			
			if (params.size() > 1) {
				
				AppClass.getInstance().setAccessNumInEmail(params.get(1));
				
				Util.BIZ_CONF_DEBUG(TAG, "access number in email: " + params.get(1));
			}
									
		}while(false);
	}
	
	private void init(){	
		
		loadData();	
	}

	public void loadData() {

		beginTime = System.currentTimeMillis();
		
		ExecutorService service = AppClass.getInstance().getService();

		service.submit(new Runnable() {

			@Override
			public void run() {
				//This can only be called after ModelManager.initInstance()	
				//ModelManager.getInstance().addPhoneLocDb();
				try {
					AccountsManager.getInstance().loadAccountFromDb();
					AppointmentConfManager.getInstance().loadMeetingFromDb();	
					ConfHistoryManager.getInstance().loadHistoryFromDb();
					NumSegmentManager.getInstance().loadPhoneLocSeg();
				} catch (SQLException e) {
					
					Util.BIZ_CONF_DEBUG(TAG, "load data encounter exception:" + e.getMessage());
				}
				
				Long usedTime = System.currentTimeMillis() - beginTime;

				Long sleepTime = Constant.WELCOME_SCREEN_SHOW_TIME - usedTime;

				if (sleepTime <= 0) {

					mHandler.sendEmptyMessage(SHOW_MAIN_ACTIVITY);
				} else {

					mHandler.sendEmptyMessageDelayed(SHOW_MAIN_ACTIVITY, sleepTime);
				}
			}
		});
	}
	
	
	
	private void startMainActivity() {
		
		//Intent mainIntent = new Intent(WelcomeActivity.this, MainActivity.class);
		Intent mainIntent = new Intent();
		mainIntent.setAction(MainActivity.LAUNCH_ACTION);
		mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		WelcomeActivity.this.startActivity(mainIntent);
		WelcomeActivity.this.finish();
//		Intent testIntent = new Intent();
//		testIntent.setPackage("com.sktlab.bizconfmobile");
//		
//		Util.BIZ_CONF_DEBUG(TAG, "testIntentUri:" + testIntent.toUri(Intent.URI_INTENT_SCHEME));
	}
	private void setFullScreen(){
		requestWindowFeature(1);
		getWindow().setFlags(1024, 1024);
	}
}
