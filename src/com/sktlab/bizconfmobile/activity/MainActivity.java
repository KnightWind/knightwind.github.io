package com.sktlab.bizconfmobile.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.lurencun.service.autoupdate.AppUpdate;
import com.lurencun.service.autoupdate.AppUpdateService;
import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.fragment.CalendarFragment;
import com.sktlab.bizconfmobile.fragment.DummyTabContent;
import com.sktlab.bizconfmobile.fragment.HistoryFragment;
import com.sktlab.bizconfmobile.fragment.HomeFragment;
import com.sktlab.bizconfmobile.fragment.ServiceFragment;
import com.sktlab.bizconfmobile.fragment.SettingFragment;
import com.sktlab.bizconfmobile.interfaces.ILoadingDialogCallback;
import com.sktlab.bizconfmobile.model.ConfAccount;
import com.sktlab.bizconfmobile.model.ConfControl;
import com.sktlab.bizconfmobile.model.Constant;
import com.sktlab.bizconfmobile.model.impl.VersionUpdateImpl;
import com.sktlab.bizconfmobile.model.manager.AccountsManager;
import com.sktlab.bizconfmobile.model.manager.CommunicationManager;
import com.sktlab.bizconfmobile.model.manager.LogcatFileManager;
import com.sktlab.bizconfmobile.parser.VersionUpdateJSONParser;
import com.sktlab.bizconfmobile.util.AppLocationService;
import com.sktlab.bizconfmobile.util.CheckHideInput;
import com.sktlab.bizconfmobile.util.Util;
import com.umeng.analytics.MobclickAgent;

/**
 * @Description:
 * 
 * @Author wenjuan.li
 * 
 * @date 2013-07-23
 * 
 * @version V1.0
 */

public class MainActivity extends FragmentActivity implements ILoadingDialogCallback{
	
	public static final String TAG = "MainActivity";
	
	public static final String LAUNCH_ACTION = "com.sktlab.bizconf.MainActivity";
	public static String local_passwd;
	public static int count=0;
	public static boolean flag=false;
	private TabHost tabHost;
	private TabWidget tabWidget;
	private HomeFragment homeFragment;
	private CalendarFragment calendarFragment;
	private HistoryFragment historyFragment;
	private ServiceFragment serviceFragment;
	private SettingFragment settingFragment;
	private android.support.v4.app.FragmentTransaction ft;

	private RelativeLayout tabIndicatorHome;
	private RelativeLayout tabIndicatorCalendar;
	private RelativeLayout tabIndicatorHistory;
	private RelativeLayout tabIndicatorService;
	private RelativeLayout tabIndicatorSetting;
	
	private AppUpdate appUpdate;
	
	public final int TAB_HOME = 0;
	public final int TAB_CALENDAR = 1;
	public final int TAB_HISTORY = 2;
	public final int TAB_SERVICE = 3;
	public final int TAB_SETTING = 4;
	
	private boolean isCheckedUpdate = false;
	private boolean isLinkToStartConf = false;
	
	private String shangHaiLinkAccessNumber = "+8621 6026 4000";
	private String beijingLinkAccessNumber = "+861056294500";
	
	private TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {
		@Override
		public void onTabChanged(String tabId) {

			detecthAllFragment();
			
			int currentTabId = tabHost.getCurrentTab();
			
			switch(currentTabId) {

			case TAB_HOME:
				addFragmentHome();
				break;
			case TAB_CALENDAR:
				addFragmentCalendar();
				break;
			case TAB_HISTORY:
				addFragmentHistory();
				break;
			case TAB_SERVICE:
				addFragmentService();
				break;
			case TAB_SETTING:
				addFragmentSetting();
				break;
			default:
				addFragmentHome();
				break;
			}
			
			ft.commit();					
		}

	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		ActivityBuffer mActivityBuffer = ActivityBuffer.getInstance();
		mActivityBuffer.addActivity(this);	
		
		setContentView(R.layout.activity_main);
		
		initAutoUpdateService();		
		initTabHost();			
		
		checkLocationServiceEnable();
		
		//startLogcatManager();
		Util.BIZ_CONF_DEBUG(TAG, "current APP version: " + Util.getVersionName(this));
		Util.BIZ_CONF_DEBUG(TAG, "current taskId: " + this.getTaskId());
		linkToStartConf();
	}
	
	
	
	
	
		
	
	
	
	private void startLogcatManager() {
		// TODO Auto-generated method stub
		 LogcatFileManager.getInstance().start("/sdcard/bizlog");
	}









	//
	private void checkLocationServiceEnable() {
		
		AppLocationService locationService = new AppLocationService(this);
		
		if (!locationService.isLocationEnable()) {
			
			Util.shortToast(AppClass.getInstance(), R.string.open_location_service);
		}
	}
	
	private void initAutoUpdateService() {
		appUpdate = AppUpdateService.getAppUpdate(this);
		VersionUpdateImpl versionUpdateImpl = new VersionUpdateImpl(this,false);	
		appUpdate.setDisplayDelegate(versionUpdateImpl);
	}
	
	@Override
	public void onAttachFragment(Fragment fragment) {
		super.onAttachFragment(fragment);		
		//Util.BIZ_CONF_DEBUG(TAG, "onAttachFragment called");
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		appUpdate.callOnPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if(!flag){
			flag=true;
			getLocalPasswd();
			if(!local_passwd.equals(""))
			passwdcheck();
		}
	
		do {
		
			if (CommunicationManager.getInstance().isInConfManageScreen()) {

				Util.BIZ_CONF_DEBUG(TAG, "in conference screen");

				Util.startActivity(this, ConferenceActivity.class);
				break;
			}
			
			appUpdate.callOnResume();
			
			if (!isLinkToStartConf){
				
				Util.BIZ_CONF_DEBUG(TAG, "check for update now");
				checkForUpdate();
			}
			
			//MobclickAgent.setDebugMode( true );		
			MobclickAgent.onResume(this);
			
			checkMemoryRecycle();					
		}while(false);
	}


	public void getLocalPasswd(){
		SharedPreferences sp=this.getSharedPreferences("protect_passwd", Context.MODE_PRIVATE);
		local_passwd=sp.getString("protect_passwd", "");
	}

	private void passwdcheck() {
		
		final Dialog d = new Dialog(this, R.style.mydialog);

		d.setContentView(R.layout.passwd_protect_check);

		final EditText passwd = (EditText) d
				.findViewById(R.id.passwd_check_edit);

		final TextView alert = (TextView) d.findViewById(R.id.passwd_check_tv);

		Button confirm = (Button) d.findViewById(R.id.passwd_check_bt);

		d.setOnKeyListener(new OnKeyListener() {

			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {

				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getRepeatCount() == 0) {
					
					return true;
				} else {
					
					return false;
				}
			}
		});
		
		d.setCancelable(false);
			
		confirm.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				CheckHideInput.checkInputMethod();
				String passwdString=passwd.getText().toString().trim();
				if(passwdString.equals(local_passwd)){
					count=0;
					d.dismiss();
				}else{
					if(++count==3) System.exit(0);
					Toast.makeText(MainActivity.this,getString(R.string.passwd_is_wrong), 0).show();
					alert.setText(getString(R.string.passwd_protect_error_alert));
				}
			}
		});
		d.show();
	}
		
	private void checkForUpdate() {
		
		if (!isCheckedUpdate) {
			
			String language = Util.getLanguage();
			
			String url = Constant.UPDATE_URL + language;
			
			appUpdate.checkLatestVersion(url, new VersionUpdateJSONParser());
			
			isCheckedUpdate = true;
		}		
	}
	
	private void checkMemoryRecycle() {
		do {
		
			//Util.BIZ_CONF_DEBUG(TAG, "need2Exit: " + AppClass.getInstance().isNeed2Exit());
			
			if (AppClass.getInstance().isNeed2Exit() 
					|| AccountsManager.getInstance().isMemoryRecyled()) {

				Util.longToast(this, R.string.toast_memory_recycled);
				Util.BIZ_CONF_DEBUG(TAG, "memory recyled");
				AppClass.getInstance().setNeed2Exit(true);
				this.finish();	
				
				Util.startActivity(this, WelcomeActivity.class);
				break;
			}		
					
		}while(false);
	}
	
	@Override
	protected void onDestroy() {
		
		//here clear all started activities
		//ActivityBuffer.getInstance().clear();
		flag=false;
		super.onDestroy();
	}

	public void linkToStartConf() {

		do {
				
			if (!CommunicationManager.getInstance().isLinkStartConf()) {
				
				break;
			}
			
			Util.BIZ_CONF_DEBUG(TAG, "start conference from link~");
			
			isLinkToStartConf = true;
			
			String confCode = AppClass.getInstance().getLinkConfCode();
			
			//When user click a link in email to join a conference, he or she can only join the conference
			//as a guest, so we just search the guest accounts to confirm whether the user store the conference
			// account whose conference code is equal to the link conference code.
			ConfAccount account = AccountsManager.getInstance()
					.getGuestAccountByConfCode(confCode);

			if (null == account) {
				
				String accessNumInEmail = AppClass.getInstance().getAccessNumInEmail();
				
				if (Util.isEmpty(accessNumInEmail) 
						&& !Util.isNetworkAvailable(this) 
						&& !Util.isSpContainsKey(this, confCode)) {
					
					Util.longToast(this, R.string.toast_network_unavailable);
					break;
				}
				
				account = createLinkConfAccount(confCode);
			}

			CommunicationManager.getInstance().setActiveAccount(account);
			ConfControl.getInstance().startConf(this, this);

			CommunicationManager.getInstance().setLinkStartConf(false);
		} while (false);

	}

	private ConfAccount createLinkConfAccount(String confCode) {
		
		ConfAccount account = new ConfAccount();
		account.setConfAccountName("Guest");
		account.setConfCode(confCode);
		
		String accessNum = AppClass.getInstance().getAccessNumInEmail();
		
		account.setAccessNumber(accessNum);
		
		if (Util.isEmpty(accessNum)) {
						
			account.setUseDefaultAccessNum(true);
		}	
		
		AppClass.getInstance().setAccessNumInEmail("");
		
		return account;
	}
	
	private String getAccessNumberFromSp(String confCode) {
		
		int bridgeId = Util.getSPInt(this, confCode, Constant.BRIDGE_ID_NOT_IN_SP);
		
		String accessNum = "";
		
		if (bridgeId == Constant.SHANG_HAI_BRIDGE) {
			
			accessNum = shangHaiLinkAccessNumber;
		}else if (bridgeId == Constant.BEI_JING_BRIDGE) {
			
			accessNum = beijingLinkAccessNumber;
		}
		
		return accessNum;
	}
	
	private void detecthAllFragment() {
		
		android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
		homeFragment = (HomeFragment) fm
				.findFragmentByTag(Constant.TAB_TAG_HOME);
		calendarFragment = (CalendarFragment) fm
				.findFragmentByTag(Constant.TAB_TAG_CALENDAR);
		
		historyFragment = (HistoryFragment)fm
				.findFragmentByTag(Constant.TAB_TAG_HISTORY);
		serviceFragment = (ServiceFragment) fm
				.findFragmentByTag(Constant.TAB_TAG_SERVICE);
		settingFragment = (SettingFragment) fm
				.findFragmentByTag(Constant.TAB_TAG_SETTING);
		ft = fm.beginTransaction();
		
		/** 濡����瀛����Detaches��� */
		if (homeFragment != null)
			ft.detach(homeFragment);

		/** 濡����瀛����Detaches��� */
		if (calendarFragment != null)
			ft.detach(calendarFragment);
		
		/** 濡����瀛����Detaches��� */
		if (historyFragment != null)
			ft.detach(historyFragment);
		
		/** 濡����瀛����Detaches��� */
		if (serviceFragment != null)
			ft.detach(serviceFragment);

		/** 濡����瀛����Detaches��� */
		if (settingFragment != null)
			ft.detach(settingFragment);
	}
	
	public void addFragmentHome() {

		if (homeFragment == null) {
			
			ft.add(R.id.realtabcontent, new HomeFragment(),
					Constant.TAB_TAG_HOME);
		} else {
			ft.attach(homeFragment);
		}
	}

	public void addFragmentCalendar() {

		if (calendarFragment == null) {
			ft.add(R.id.realtabcontent, new CalendarFragment(),
					Constant.TAB_TAG_CALENDAR);
		} else {
			ft.attach(calendarFragment);
		}
	}
	
	public void addFragmentHistory() {

		if (historyFragment == null) {
			ft.add(R.id.realtabcontent, new HistoryFragment(),
					Constant.TAB_TAG_HISTORY);
		} else {
			ft.attach(historyFragment);
		}
	}
	
	public void addFragmentService() {

		if (serviceFragment == null) {
			ft.add(R.id.realtabcontent, new ServiceFragment(),
					Constant.TAB_TAG_SERVICE);
		} else {
			ft.attach(serviceFragment);
		}
	}

	public void addFragmentSetting() {

		if (settingFragment == null) {
			ft.add(R.id.realtabcontent, new SettingFragment(),
					Constant.TAB_TAG_SETTING);
		} else {
			ft.attach(settingFragment);
		}
	}
	
	public RelativeLayout generateTabIndicator(TabWidget tw, int imgId, int strId) {
		
		RelativeLayout indicator = (RelativeLayout) LayoutInflater.from(this).inflate(
				R.layout.tab_indicator, tw, false);
		TextView tvTab1 = (TextView) indicator.getChildAt(1);
		ImageView ivTab1 = (ImageView) indicator.getChildAt(0);
		ivTab1.setBackgroundResource(imgId);
		tvTab1.setText(strId);
		
		return indicator;
	}
	
	public void initTabHost() {

		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabWidget = (TabWidget) findViewById(android.R.id.tabs);
		
		LinearLayout layout = (LinearLayout) tabHost.getChildAt(0);
		TabWidget tw = (TabWidget) layout.getChildAt(1);

		tabIndicatorHome = generateTabIndicator(tw,R.drawable.selector_mood_home, R.string.main_tab_home);	
		tabIndicatorCalendar = generateTabIndicator(tw,R.drawable.selector_mood_calendar, R.string.main_tab_calendar);	
		tabIndicatorHistory = generateTabIndicator(tw,R.drawable.selector_mood_history, R.string.main_tab_history);	
		tabIndicatorService = generateTabIndicator(tw,R.drawable.selector_mood_service, R.string.main_tab_service);
		tabIndicatorSetting = generateTabIndicator(tw,R.drawable.selector_mood_setting, R.string.main_tab_setting);
				
		tabHost.setup();
		tabHost.setOnTabChangedListener(tabChangeListener);
		
		int currentTab = tabHost.getCurrentTab();
		
		tabHost.setCurrentTab(0);
		tabHost.clearAllTabs();
		
		initTab();
		
		tabHost.setCurrentTab(currentTab);
	}

	public void addTabSpec(String tag, RelativeLayout indicator) {
		
		TabHost.TabSpec tSpecHome = tabHost.newTabSpec(tag);
		tSpecHome.setIndicator(indicator);
		tSpecHome.setContent(new DummyTabContent(getBaseContext()));
		tabHost.addTab(tSpecHome);
	}
	
	public void initTab() {
		
		addTabSpec(Constant.TAB_TAG_HOME, tabIndicatorHome);
		addTabSpec(Constant.TAB_TAG_CALENDAR, tabIndicatorCalendar);
		addTabSpec(Constant.TAB_TAG_HISTORY, tabIndicatorHistory);
		addTabSpec(Constant.TAB_TAG_SERVICE, tabIndicatorService);
		addTabSpec(Constant.TAB_TAG_SETTING, tabIndicatorSetting);
	}
	
	private DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {

			switch (which) {

			case DialogInterface.BUTTON_POSITIVE:
				
				//here clear all started activities
				//ActivityBuffer.getInstance().clear();
				
				CommunicationManager cm = CommunicationManager.getInstance();
				
				if (cm.isTurn2HomePage()) {
					
					cm.notifyConfEnded();
				}
				
				MainActivity.this.finish();
				
				resetAppState();
				break;

			case DialogInterface.BUTTON_NEGATIVE:

				break;
			}
		}
	};
	
	private void resetAppState() {
		
		isCheckedUpdate = false;	
		
		Util.BIZ_CONF_DEBUG(TAG, "reset data now");
		//reset the app's all data
		AppClass.getInstance().reset();		
		CommunicationManager.getInstance().setLinkStartConf(false);
		stopLogcatManager();
	}
	
	@Override
	public void onBackPressed() {
		
		int titleStrId = R.string.confirm_exit;
		
		CommunicationManager cm = CommunicationManager.getInstance();
		
		if (cm.isTurn2HomePage()) {
			
			titleStrId = R.string.confirm_exit_and_end_conf;
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getResources().getString(R.string.app_name))
				.setMessage(getResources().getString(titleStrId))
				.setPositiveButton(R.string.ok, listener)
				.setNegativeButton(android.R.string.cancel, listener);
		
		builder.show();	
	}
	
	@Override
	public void startActivity(Intent intent) {
		
		 try {
			 
		        super.startActivity(intent);
		        
		    } catch (ActivityNotFoundException e) {
		        /*
		         * Probably an no email client broken. This is not perfect,
		         * but better than crashing the whole application.
		         */
		    	Util.longToast(this, R.string.toast_no_email_client);
		        //super.startActivity(Intent.createChooser(intent, null));
		    }
	}
	
	
	@Override
	protected void onNewIntent(Intent intent) {

		super.onNewIntent(intent);
		setIntent(intent);

		Util.BIZ_CONF_DEBUG(TAG, "onNewIntent");
		Util.BIZ_CONF_DEBUG(TAG, "onNewIntent current taskId: " + this.getTaskId());
		
		do {
			
			if (CommunicationManager.getInstance().isTurn2HomePage()) {
				
				Util.BIZ_CONF_DEBUG(TAG, "turn to main activity from conference activity");
				break;
			}
			
			if (CommunicationManager.getInstance().isInConfManageScreen()) {

				Util.BIZ_CONF_DEBUG(TAG, "in conference screen");

				Util.startActivity(this, ConferenceActivity.class);
				break;
			}

			linkToStartConf();
		} while (false);

	}
    
    private void stopLogcatManager() {
    	
    	if (AppClass.getInstance().isGenerateLogFile()) {
    		
            LogcatFileManager.getInstance().stop();
    	} 
    }

	@Override
	public void onSuccessDone() {
		
		Intent intent = new Intent();
		intent.setClass(this, ConferenceActivity.class);
		//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		this.startActivity(intent);	
	}

	@Override
	public void onDoneWithError() {
		
		Util.shortToast(this, R.string.toast_start_conf_failed);
	}
	
}
