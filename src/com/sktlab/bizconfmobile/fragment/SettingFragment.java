package com.sktlab.bizconfmobile.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.lurencun.service.autoupdate.AppUpdate;
import com.lurencun.service.autoupdate.AppUpdateService;
import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.activity.AboutUsActivity;
import com.sktlab.bizconfmobile.activity.AccountSettingActivity;
import com.sktlab.bizconfmobile.activity.PasswdProtectActivity;
import com.sktlab.bizconfmobile.activity.VersionStatementActivity;
import com.sktlab.bizconfmobile.customview.TextArrow;
import com.sktlab.bizconfmobile.model.Constant;
import com.sktlab.bizconfmobile.model.impl.VersionUpdateImpl;
import com.sktlab.bizconfmobile.parser.VersionUpdateJSONParser;
import com.sktlab.bizconfmobile.util.Util;

public class SettingFragment extends Fragment{	
	private Activity mActivity;
	
	private TextArrow accountSetting;
	private TextArrow passwdProtect;
	private TextArrow versionStatement;
	private TextArrow checkForUpdates;
	private TextArrow aboutUs;
	
	private AppUpdate appUpdate;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mActivity = getActivity();		
		appUpdate = AppUpdateService.getAppUpdate(mActivity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_setting_main, container, false);
	}

	@Override
	public void onResume() {
		super.onResume();
		
		VersionUpdateImpl versionUpdateImpl = new VersionUpdateImpl(mActivity);
		
		versionUpdateImpl.setShowToast(true);
		
		appUpdate.setDisplayDelegate(versionUpdateImpl);
		//appUpdate.callOnResume();
		
		accountSetting = (TextArrow)mActivity.findViewById(R.id.setting_account_set);
		passwdProtect  = (TextArrow)mActivity.findViewById(R.id.setting_passwd_protect);
		versionStatement = (TextArrow)mActivity.findViewById(R.id.setting_version_statement);
		checkForUpdates = (TextArrow)mActivity.findViewById(R.id.setting_check_update);
		aboutUs = (TextArrow)mActivity.findViewById(R.id.setting_about_us);
		
		accountSetting.setOnArrowClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Util.startActivity(mActivity, AccountSettingActivity.class);
			}
		});
		
		passwdProtect.setOnArrowClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Util.startActivity(mActivity, PasswdProtectActivity.class);
			}
		});
		
		versionStatement.setOnArrowClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Util.startActivity(mActivity, VersionStatementActivity.class);
			}
		});
		
		checkForUpdates.setOnArrowClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (Util.isNetworkAvailable(mActivity)) {
					
					Util.shortToast(mActivity, R.string.toast_connect_to_check_update);				
					appUpdate.checkLatestVersion(Constant.UPDATE_URL + Util.getLanguage(), 
							new VersionUpdateJSONParser());
				}else {
					
					Util.longToast(mActivity, R.string.toast_network_unavailable);
				}
			}
		});
		
		aboutUs.setOnArrowClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Util.startActivity(mActivity, AboutUsActivity.class);
			}
		});
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onPause() {
		super.onPause();
		
		//appUpdate.callOnPause();
	}
}
