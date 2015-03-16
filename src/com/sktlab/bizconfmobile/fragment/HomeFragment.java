package com.sktlab.bizconfmobile.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.activity.AddAccountActivity;
import com.sktlab.bizconfmobile.activity.AppClass;
import com.sktlab.bizconfmobile.activity.OrderConfActivity;
import com.sktlab.bizconfmobile.activity.VerifyCodeActivity;
import com.sktlab.bizconfmobile.adapter.AccountAdapter;
import com.sktlab.bizconfmobile.interfaces.ILoadingDialogCallback;
import com.sktlab.bizconfmobile.mina.MinaUtil;
import com.sktlab.bizconfmobile.model.ConfAccount;
import com.sktlab.bizconfmobile.model.ConfControl;
import com.sktlab.bizconfmobile.model.Constant;
import com.sktlab.bizconfmobile.model.manager.AccountsManager;
import com.sktlab.bizconfmobile.model.manager.CommunicationManager;
import com.sktlab.bizconfmobile.net.NetOp;
import com.sktlab.bizconfmobile.net.StatusCode;
import com.sktlab.bizconfmobile.util.LoadingDialogUtil;
import com.sktlab.bizconfmobile.util.Util;
import com.sktlab.bizconfmobile.util.ValidatorUtil;

public class HomeFragment extends Fragment implements ILoadingDialogCallback,
					OnCheckedChangeListener{

	public static final String TAG = "HomeFragment";

	private Activity mActivity;

	private RadioGroup rgFunctionModule;

	// the module check user phone number
	private LinearLayout layoutCheckNumber;

	// confirm to check phone number
	private Button btConfirm;
	private Button btPass;
	// add a conference account
	private Button btAddAccount;
	// edit text for user to input phone number
	private EditText etPhoneNumber;
	// list view for conference account
	private ListView lvAccount;
	//adapter for conference account
	private AccountAdapter mAccountAdapter;
	//layout module for conference 		
	private LinearLayout layoutConfAccount;

	private LoadingDialogUtil mDialog;

	private ArrayList<ConfAccount> datas;
	// whether the phone number had been checked
	private boolean isPhoneVerify;
	
	//whether the phone number bind conference account
	private boolean isNoAccountBind = false;
	// verify code from the server
	private String verifyPhoneNum = null;

	// this used to do different thing in method OnSuccessDone and
	// onDoneWithError
	private final int DIALOG_TYPE_RESET = 0;
	private final int DIALOG_TYPE_VERIFY_PHONE_NUM = 1;
	private final int DIALOG_TYPE_DOWNLOAD_ACCOUNT = 2;
	
	private int mDialogType = DIALOG_TYPE_RESET;
	
	//start conference module
	public static final int TAB_START_CONF = 11;
	//order conference module
	public static final int TAB_ORDER_CONF = 12;
	//join conference module
	public static final int TAB_JOIN_CONF = 13;
	
	private int mCurrentTab = TAB_START_CONF;
	
	public static final String KEY_CURRENT_TAB = "key_add_account_type";
	

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		//Util.BIZ_CONF_DEBUG(TAG, "onAttach called");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.fragment_home_main, container, false);
	}

	@Override
	public void onResume() {

		super.onResume();
		
		//Util.BIZ_CONF_DEBUG(TAG, "onResume called");
		
		init();
	}

	public void startVerifyActivity() {

		Intent intent = new Intent();
		intent.setClass(mActivity, VerifyCodeActivity.class);
		intent.putExtra(Constant.KEY_VERIFY_PHONE_NUM, verifyPhoneNum);

		mActivity.startActivity(intent);
	}

	public void init() {

		mActivity = getActivity();
		
		mDialog = new LoadingDialogUtil(mActivity, this);

		datas = new ArrayList<ConfAccount>();

		rgFunctionModule = (RadioGroup) mActivity
				.findViewById(R.id.rg_function_moudle);
		
		rgFunctionModule.setOnCheckedChangeListener(this);
		
		btConfirm = (Button) mActivity.findViewById(R.id.bt_confirm);
		btPass = (Button) mActivity.findViewById(R.id.bt_pass);
		btAddAccount = (Button) mActivity
				.findViewById(R.id.bt_add_conf_account);

		// editText for user to input phone number
		etPhoneNumber = (EditText) mActivity.findViewById(R.id.et_input_number);

		layoutCheckNumber = (LinearLayout) mActivity
				.findViewById(R.id.layout_check_number);
		layoutConfAccount = (LinearLayout) mActivity
				.findViewById(R.id.layout_conf_account);

		lvAccount = (ListView) mActivity.findViewById(R.id.lv_download_conf);
		
		Button b1= (Button) mActivity.findViewById(R.id.rb_start_conf);
		Button b2= (Button) mActivity.findViewById(R.id.rb_order_conf);
		b1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				List<ConfAccount> accounts=AccountsManager.getInstance().getModeratorAccounts();
				
				if (accounts.size()==1 
						&& !CommunicationManager.getInstance().isTurn2HomePage()){
					
					Util.BIZ_CONF_DEBUG(TAG, "start conference for only one account");
					ConfAccount account=(ConfAccount) accounts.get(0);
					CommunicationManager.getInstance().setActiveAccount(account);
					mAccountAdapter.setmSelectedAccount(account);
					ConfControl.getInstance().startConf(mActivity, mAccountAdapter);
				}
			}
		});
		
		b2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				List<ConfAccount> accounts=AccountsManager.getInstance().getModeratorAccounts();
				if(accounts.size()==1){
					ConfAccount account=(ConfAccount) accounts.get(0);
					CommunicationManager.getInstance().setActiveAccount(account);
					Intent intent = new Intent();
					intent.setClass(mActivity, OrderConfActivity.class);
					intent.putExtra(Constant.KEY_OF_CONF_ACCOUNT_ID, account.getAccountId());
					
					mActivity.startActivity(intent);
				}
			}
		});
		btConfirm.setOnClickListener(homeBtnClickListener);
		btPass.setOnClickListener(homeBtnClickListener);
		btAddAccount.setOnClickListener(homeBtnClickListener);
		// is the user has done the phone number check
		//isPhoneVerify = Util.getSPBool(mActivity,Constant.KEY_SP_PHONE_NUM_VER, false);
		//delete download account through phone number
		isPhoneVerify = true;
		do{
			
			if (isPhoneVerify || AppClass.getInstance().isPassNumberVerify()) {

				//Util.BIZ_CONF_DEBUG(TAG, "isPhoneVerify: " + isPhoneVerify);
				showAccountView();
				break;
			} 

			//show number verify view
			layoutCheckNumber.setVisibility(View.VISIBLE);
			layoutConfAccount.setVisibility(View.GONE);
		}while(false);
	}
	
	/**
	 * add account for the user
	 */
	private void addConfAccount() {

		Intent intent = new Intent();

		intent.setClass(mActivity, AddAccountActivity.class);
		intent.setAction(AddAccountActivity.ACTION_ADD_ACCOUNT);
		intent.putExtra(Constant.KEY_OF_OPERATE_TYPE, mCurrentTab);
		mActivity.startActivity(intent);
	}

	private void showAccountView() {

		layoutCheckNumber.setVisibility(View.GONE);

		boolean isLoadFormNetSuccess = Util.getSPBool(mActivity,
				Constant.KEY_SP_ACCOUNT_DOWNLOADED, false);
		
		do{
			
			if(AppClass.getInstance().isPassNumberVerify() || isLoadFormNetSuccess){
								
				showAccountList();
				break;
			}
			
			loadAccount(Constant.LOAD_ACCOUNT_FROM_NET);
			
		}while(false);
				
	}

	private void loadAccount(String loadWay) {

		ExecutorService service = AppClass.getInstance().getService();

		service.submit(new Runnable() {

			@Override
			public void run() {

				mDialogType = DIALOG_TYPE_DOWNLOAD_ACCOUNT;

				mDialog.showDialog(mActivity
						.getString(R.string.downloading_moderator_account),
						MinaUtil.CONNECT_WAITING_TIME);

				String verifiedPhoneNum = 
						Util.getSPString(mActivity, 
								Constant.KEY_SP_VERIFIED_PHONE_NUM, 
								"null");
				
				do{
					
					if (Util.isEmpty(verifiedPhoneNum)) {
						
						break;
					}
					
					Util.setSpBoolValue(mActivity, Constant.KEY_SP_ACCOUNT_DOWNLOADED, true);
					isNoAccountBind = NetOp.downloadAccount();		
					//Util.BIZ_CONF_DEBUG(TAG, "load network account success");
				}while(false);				

				mDialog.finishDialogSuccessDone();
			}
		});
	}

	/**
	 * this method will verify the phone number in server
	 * 
	 * @param number
	 * @return
	 */
	private boolean numberVerificate(String number) {

		boolean isValid = false;

		final String phoneNumber = number;
		// ....

		ExecutorService service = AppClass.getInstance().getService();

		service.submit(new Runnable() {

			@Override
			public void run() {

				mDialogType = DIALOG_TYPE_VERIFY_PHONE_NUM;

				mDialog.showDialog(mActivity
						.getString(R.string.being_authenticated));

				Util.BIZ_CONF_DEBUG(TAG, "begin verify phone number: "+ phoneNumber);

				String statusCode = NetOp.sendNumberToVerify(phoneNumber);
				
				if (Util.isEmpty(statusCode) || 
						!statusCode.equalsIgnoreCase(
								StatusCode.TEL_VERIFY_CODE_SUCCESS)) {
					
					mDialog.finishDialogWithErrorMsg();
				} else {
					
					verifyPhoneNum = phoneNumber;
					mDialog.finishDialogSuccessDone();
				}
			}
		});

		return isValid;
	}
	
	/**
	 * show account related with the checked phone number
	 */
	private void showAccountList() {

		layoutConfAccount.setVisibility(View.VISIBLE);

		//Util.BIZ_CONF_DEBUG(TAG, "currentTab::" + mCurrentTab );
		switch(mCurrentTab) {
		case TAB_START_CONF:
		case TAB_ORDER_CONF:	
			datas = AccountsManager.getInstance().getModeratorAccounts();
			break;
		case TAB_JOIN_CONF:
			datas = AccountsManager.getInstance().getGuestAccounts();
			break;
		}
		
		//Util.BIZ_CONF_DEBUG(TAG, "accounts size:" + datas.size());
		
		mAccountAdapter = new AccountAdapter(mActivity,
				R.layout.item_conf_account_layout, datas);
		
		mAccountAdapter.setFunctionType(mCurrentTab);
		mAccountAdapter.setLv(lvAccount);
		
		lvAccount.setAdapter(mAccountAdapter);
	}

	private View.OnClickListener homeBtnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {

			case R.id.bt_confirm:

				String phoneNum = etPhoneNumber.getText().toString();

				do {
					if (!ValidatorUtil.isCellPhoneValid(phoneNum)) {

						break;
					}

					if (!Util.isNetworkReadyForConf(mActivity)) {
						
						Util.shortToast(mActivity, R.string.toast_network_not_ready);
						break;
					}

					numberVerificate(phoneNum);

				} while (false);

				break;

			case R.id.bt_add_conf_account:
				addConfAccount();
				break;

			case R.id.bt_pass:
				
				AppClass.getInstance().setPassNumberVerify(true);
				showAccountView();
				break;
			}
		}
	};

	@Override
	public void onSuccessDone() {

		switch (mDialogType) {

		case DIALOG_TYPE_VERIFY_PHONE_NUM:
			startVerifyActivity();
			break;
			
		case DIALOG_TYPE_DOWNLOAD_ACCOUNT:
			
			if (isNoAccountBind) {
				
				Util.longToast(AppClass.getInstance(),R.string.toast_no_bind_account);
			}
			
			showAccountList();
			break;
			
		case DIALOG_TYPE_RESET:
			break;
			
		default:
			break;
		}

		mDialogType = DIALOG_TYPE_RESET;
	}

	@Override
	public void onDoneWithError() {

		switch (mDialogType) {

		case DIALOG_TYPE_VERIFY_PHONE_NUM:
			Util.shortToast(mActivity, R.string.toast_phone_num_verify);
			break;
		case DIALOG_TYPE_DOWNLOAD_ACCOUNT:
			Util.shortToast(mActivity, R.string.toast_download_account_failed);
			showAccountList();
			break;
		case DIALOG_TYPE_RESET:
			break;
		default:
			break;
		}

		mDialogType = DIALOG_TYPE_RESET;
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		
		if(!Util.isEmpty(group) && checkedId > -1) {
			
			boolean hasGetType = false;
			
			switch(checkedId) {
			case R.id.rb_start_conf:
				mCurrentTab = TAB_START_CONF;
				hasGetType = true;			
				
			case R.id.rb_order_conf:
				
				if(!hasGetType){
					mCurrentTab = TAB_ORDER_CONF;
				}		
				
				btAddAccount.setText(R.string.add_moderator_account);
				
				if(!Util.isEmpty(mAccountAdapter)) {
					mAccountAdapter.setData(AccountsManager.getInstance().getModeratorAccounts());
				}
				
				break;
				
			case R.id.rb_join_conf:
				btAddAccount.setText(R.string.add_normal_conf);
				mCurrentTab = TAB_JOIN_CONF;
				
				if(!Util.isEmpty(mAccountAdapter)) {
					mAccountAdapter.setData(AccountsManager.getInstance().getGuestAccounts());
				}
				
				break;
			}			
			
			//Util.BIZ_CONF_DEBUG(TAG, "current function type: " + mFunctionType);
			AppClass.getInstance().setCurrentTab(mCurrentTab);
			
			if(!Util.isEmpty(mAccountAdapter)) {
				
				mAccountAdapter.setFunctionType(mCurrentTab);
				mAccountAdapter.notifyDataSetChanged();
			}
		}
	}
}
