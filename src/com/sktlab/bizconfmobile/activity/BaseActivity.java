package com.sktlab.bizconfmobile.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lurencun.service.autoupdate.AppUpdate;
import com.lurencun.service.autoupdate.AppUpdateService;
import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.model.impl.VersionUpdateImpl;
import com.sktlab.bizconfmobile.model.manager.AccountsManager;
import com.sktlab.bizconfmobile.util.Util;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends Activity {
	
	public static final String TAG = "BaseActivity";
	
	private ActivityBuffer mActivityBuffer;
	protected TextView mTitle;
	protected String mTitleName;
	protected String mRightBtText;
	
	protected Button btRight;
	protected boolean isShowRightButton = false;
	
	protected AppUpdate appUpdate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mActivityBuffer = ActivityBuffer.getInstance();
		mActivityBuffer.addActivity(this);		
		mTitleName = getString(R.string.title_name_of_activity);	
		mRightBtText = getString(R.string.title_finish);
		
		appUpdate = AppUpdateService.getAppUpdate(this);
		VersionUpdateImpl versionUpdateImpl = new VersionUpdateImpl(this,false);	
		appUpdate.setDisplayDelegate(versionUpdateImpl);
	}

	@Override
	protected void onResume() {		
		super.onResume();
		
		MobclickAgent.onResume(this);
		appUpdate.callOnResume();
		
		do {
			
			if (AppClass.getInstance().isNeed2Exit() 
					|| AccountsManager.getInstance().isMemoryRecyled()) {
				
				Util.BIZ_CONF_DEBUG(TAG, "memory recyled");
				
				AppClass.getInstance().setNeed2Exit(true);				
				finish();
				mActivityBuffer.clear();			
				break;
			}
			
			mTitle = getTitleView();
			btRight = getRightBt();
			
			//Util.BIZ_CONF_DEBUG(TAG, "memory not cleared");

			if (!Util.isEmpty(mTitle)) {

				mTitle.setText(mTitleName);
			}
			
			if (!Util.isEmpty(btRight)) {
				
				btRight.setText(mRightBtText);
				
				//Util.BIZ_CONF_DEBUG(TAG, "right button not null");
				if (isShowRightButton) {
					
					//Util.BIZ_CONF_DEBUG(TAG, "show right button");
					
					btRight.setVisibility(View.VISIBLE);
				} else {

					btRight.setVisibility(View.GONE);
				}
			}
		} while (false);
		
	}
	@Override
	protected void onPause() {
		super.onPause();
		
		MobclickAgent.onPause(this);
		appUpdate.callOnPause();
	}

	/**
	 * include title layout and id is title
	 * must be this style
	 * 
	 *   <include
	 *   android:id="@+id/title"
	 *   layout="@layout/title_layout" />           
	 * @return
	 */
	public TextView getTitleView() {
		
		RelativeLayout title = (RelativeLayout)findViewById(R.id.title);
		
		TextView tvTitle = null;
		
		if(!Util.isEmpty(title)) {
			
			tvTitle = (TextView) title.findViewById(R.id.title_name);
		}
		
		return tvTitle;
	}
	
	public Button getRightBt() {
		
		RelativeLayout title = (RelativeLayout)findViewById(R.id.title);
		
		Button bt = null;
		
		if(!Util.isEmpty(title)) {
			
			bt = (Button) title.findViewById(R.id.bt_right);
		}
		
		return bt;
	}
	
	//subclass should call this method before onResume method
	public void setTitleName(String title){
		
		mTitleName = title;
	}
	
	public void setRightBtText(String text) {
		
		mRightBtText = text;
	}
	
	public boolean isShowRightButton() {
		return isShowRightButton;
	}
	
	//if subclass want to show right button of the title, it should call this method and set the value to true
	public void setShowRightButton(boolean isShowRightButton) {
		this.isShowRightButton = isShowRightButton;
	}

	public void onImgHomeClicked(View v) {
		
		this.finish();
	}	
	
	public void onRightButtonClicked(View v) {
		
	}
}
