package com.sktlab.bizconfmobile.activity;

import java.util.ArrayList;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.adapter.AccountAdapter;
import com.sktlab.bizconfmobile.customview.TopSelectorView;
import com.sktlab.bizconfmobile.customview.TopSelectorView.OnSelectorChangeListener;
import com.sktlab.bizconfmobile.model.ConfAccount;
import com.sktlab.bizconfmobile.model.manager.AccountsManager;

import android.os.Bundle;
import android.widget.ListView;

public class AccountSettingActivity extends BaseActivity implements OnSelectorChangeListener{
	
	private AccountAdapter mAccountAdapter;
	private ListView lvAccount;
	private TopSelectorView topSelectorView;
	private ArrayList<ConfAccount> datas;
	
	public static final int SETTING_ACCOUNT_LIST = 101;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_account_setting);

		lvAccount = (ListView) findViewById(R.id.lv_setting_account_list);

		topSelectorView = (TopSelectorView) findViewById(R.id.account_setting_top_selector);

		topSelectorView.setOnSelectedChangeListener(this);
		
		datas = AccountsManager.getInstance().getModeratorAccounts();
		
		mAccountAdapter = new AccountAdapter(this,
				R.layout.item_conf_account_layout, datas);
		
		mAccountAdapter.setFunctionType(SETTING_ACCOUNT_LIST);
		
		lvAccount.setAdapter(mAccountAdapter);
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (null != mAccountAdapter) {
			
			mAccountAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onLeftSelected() {
		
		//Util.BIZ_CONF_DEBUG(TAG, "on left selected");
		
		mAccountAdapter.setData(AccountsManager.getInstance().getModeratorAccounts());
		//mAccountAdapter.setFunctionType(HomeFragment.TAB_START_CONF);
		mAccountAdapter.notifyDataSetChanged();
	}

	@Override
	public void onRightSelected() {

		//Util.BIZ_CONF_DEBUG(TAG, "on right selected");

		mAccountAdapter.setData(AccountsManager.getInstance().getGuestAccounts());		
		//mAccountAdapter.setFunctionType(HomeFragment.TAB_JOIN_CONF);
		mAccountAdapter.notifyDataSetChanged();
	}	
}
