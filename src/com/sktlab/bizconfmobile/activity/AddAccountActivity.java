package com.sktlab.bizconfmobile.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.adapter.AccordAccessNumberAdapter;
import com.sktlab.bizconfmobile.customview.SlipControlView;
import com.sktlab.bizconfmobile.customview.SlipControlView.OnContentChangeListener;
import com.sktlab.bizconfmobile.fragment.HomeFragment;
import com.sktlab.bizconfmobile.model.AccessNumber;
import com.sktlab.bizconfmobile.model.ConfAccount;
import com.sktlab.bizconfmobile.model.Constant;
import com.sktlab.bizconfmobile.model.db.AccountsDbTable;
import com.sktlab.bizconfmobile.model.manager.AccountsManager;
import com.sktlab.bizconfmobile.net.NetOp;
import com.sktlab.bizconfmobile.parser.AccessNumNetParser;
import com.sktlab.bizconfmobile.util.AppLocationService;
import com.sktlab.bizconfmobile.util.CheckHideInput;
import com.sktlab.bizconfmobile.util.Util;
import com.sktlab.bizconfmobile.util.ValidatorUtil;


public class AddAccountActivity extends BaseActivity {
	
	public static final String TAG = "AddConfActivity";
	//add conference action
	public static final String ACTION_ADD_ACCOUNT = "com.sktlab.bizconfmobile.addconf.action";
	//edit conference action
	public static final String ACTION_EDIT_ACCOUNT = "com.sktlab.bizconfmobile.editconf.action";
	public boolean flag = false;
	public boolean flag1 = false;
	
	private Activity mCtx;
	private List<AccessNumber> accordAccessNumber;
	List<AccessNumber> accessNumbers ;
	
	private LinearLayout layoutConfCode;
	private LinearLayout layoutModeratorPw;
	private SharedPreferences sp;
	private SlipControlView dialOutModule;
	private SlipControlView securityCodeModule;
	
	private LinearLayout layoutAccessNumber;
	private LinearLayout layout_conf_detail;
	private LinearLayout layout_control_moudle;
	
	private TextView tvAccessNumber;
	private EditText etAccountName;
	private EditText etAccessNumber;
	private EditText etConfCode;
	private EditText etModeratorPw;
		
	private Button btConfirmAll;
	private Button btConfirmAll1;
	private LocationManager lm;
	private LocationListener mLocationListener01;
	private final int MODE_RESET = 0; 
	//add account mode
	private final int MODE_ADD_ACCOUNT = 1; 
	//edit account mode
	private final int MODE_EDIT_ACCOUNT = 2; 
	
	private int operatorMode = MODE_RESET;
	
	private long editAccountId = -1L;
	
	private ConfAccount editAccount;
	
	//whether the out call is valid when DialOutEnable
	private boolean isOutCallNumValid = false;
	//whether the conference security code is valid when SecurityCodeEnable
	private boolean isSecurityCodeValid = false;
	
	private int mCurrentOperateType = -1;
	
	private String inputConfCode = "";
		
	private OnClickListener dialOutModuleListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			String phoneNum = dialOutModule.getInputContent();
			dialOutModule.clear_Focus();
			btConfirmAll1.setFocusable(true);
			btConfirmAll1.setFocusableInTouchMode(true);
			btConfirmAll1.requestFocus();
			
			do{
				if(!ValidatorUtil.isNumberValid(phoneNum)&&!ValidatorUtil.isNumberCodeValid(phoneNum)) {
					
					Util.shortToast(mCtx, R.string.toast_outcall_num_invalid);
					Util.requestFocus(dialOutModule.getInputView());
					break;
				}
				
				v.setEnabled(false);
				isOutCallNumValid = true;				
				//Util.shortToast(mCtx, R.string.toast_fixed_line);
			}while(false);
		}
	};
	
	private OnClickListener securityCodeModuleListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
		
			if (checkSecurityCode()) {
				
				v.setEnabled(false);
				Util.requestFocus(mTitle);
			}
		}
	};
	
	private boolean checkSecurityCode() {
		
		String securityCode = securityCodeModule.getInputContent();

		do {

			if (securityCode == null || securityCode.equalsIgnoreCase("")) {

				//Util.shortToast(mCtx, R.string.toast_security_code_null);
				Util.requestFocus(securityCodeModule.getInputView());
				break;
			}
			
			if(securityCode.length() != 4) {
				
				//Util.shortToast(mCtx, R.string.toast_security_4_number);
				Util.requestFocus(securityCodeModule.getInputView());
				break;
			}

			isSecurityCodeValid = true;
		} while (false);
		
		return isSecurityCodeValid;
	}
	
	private OnContentChangeListener outCallNumChangeListener = new OnContentChangeListener() {
		@Override
		public void OnContentChangeListener(EditText inputContent) {
			
			//Util.shortToast(mCtx, R.string.toast_fixed_line);
			isOutCallNumValid = false;
		}
	};
	
	private OnFocusChangeListener outCallInputFocusChangeListener = new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			
			if (hasFocus) {
				
				EditText et = dialOutModule.getInputView();
				et.setInputType(InputType.TYPE_CLASS_PHONE);
				
				if(flag==false){
					Util.shortToast(mCtx, R.string.toast_fixed_line);
					flag = true ;
				}
				
			}		
		}
	};
	
	private OnFocusChangeListener securityCodeFocusChangeListener = new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			
			if (hasFocus) {
				
				Util.shortToast(mCtx, R.string.toast_security_4_number);				
			}
		}
	};
	private OnContentChangeListener securityCodeChangeListener = new OnContentChangeListener() {
		@Override
		public void OnContentChangeListener(EditText inputContent) {

			isSecurityCodeValid = false;
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_add_conf_account);
		
		mCtx = this;
		
		initView();		
	}	
	
	public String requestLocationCode(Location location) {

		List<Address> addresses = null;
		String code = "";
		Geocoder gcd = new Geocoder(this);

		if (null != location) {

			try {
				
				addresses = 
						gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
				
				if (addresses != null && addresses.size() > 0) {
					
					code = addresses.get(0).getCountryCode();
				}			
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		
		if (code.equals("")) {
			
			code = "CN";
		}
		
		Util.BIZ_CONF_DEBUG(TAG, "location code:" + code);

		return code;
	}
	
	private void loadAccessNumbers(){
				
		try {
			
			accessNumbers = new AccessNumNetParser(inputConfCode).parse();			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getCountry(List<AccessNumber> list, String code) {
		
		String countryName = "";
		
		for (int i = 0; i < list.size(); i++) {
			
			if (list.get(i).getCountrycode().equals(code)) {
				
				countryName = list.get(i).getCountry();				
				break;
			}
		}
		
		return countryName;
	}
	
	private List<AccessNumber> getAccordAccessNumber(){
		
		List<AccessNumber> accordAccessNumber = new ArrayList<AccessNumber>();

		//Not add the global access number
//		if(accessNumbers.size() != 0){
//			
//			accordAccessNumber.add(accessNumbers.get(0));
//		}
		
		for(int i=1;i<accessNumbers.size();i++){
			
			if(accessNumbers.get(i).getCountrycode().equals(locationCode)){
				
				accordAccessNumber.add(accessNumbers.get(i));
			}
		}
		return accordAccessNumber;
	}
	
	private void showlocationDialog() {
		
		final Dialog locationDialog = new Dialog(this);
		
		locationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		locationDialog.setContentView(R.layout.location_change);
		
		ListView lv = (ListView) locationDialog
				.findViewById(R.id.location_change_lv);
		
		TextView tv = (TextView) locationDialog
				.findViewById(R.id.location_change_tv);
		
		Button cancel = (Button) locationDialog
				.findViewById(R.id.location_change_cancel);
		
		Button ok = (Button) locationDialog
				.findViewById(R.id.location_change_ok);

		cancel.setOnClickListener(new android.view.View.OnClickListener() {
			public void onClick(View v) {
				locationDialog.dismiss();
			}
		});

		ok.setOnClickListener(new android.view.View.OnClickListener() {
			public void onClick(View v) {
				locationDialog.dismiss();
			}
		});

		//Toast.makeText(this, accessNumbers.size() + "", 0).show();
		
		do{
		
			if (null == accessNumbers) {
				
				break;
			}
			
			String country = getCountry(accessNumbers, locationCode);
			
			if (country.equals("")) {
				
				break;
			}
			
			String notice = Util.replaceString(mCtx, R.string.location_change_toast, country);
			
			tv.setText(notice);
			
			accordAccessNumber = getAccordAccessNumber();
			
			lv.setAdapter(new AccordAccessNumberAdapter(accordAccessNumber,this));
			
			lv.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					String number = accordAccessNumber.get(arg2).getNumber();
					setAccessNumber(number);
					locationDialog.dismiss();
				}
			});

			locationDialog.show();
		}while(false);
		
	}
	
	Handler locationHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			showlocationDialog();
		}				
	};
	
	private void detectLocationChange(){
		
		String storedLocationCode = Util.getSPString(AppClass.getInstance(), Constant.KEY_SP_LOCATION_CODE, "");	
		
		do{
			
			if (locationCode.equals("")) {

				Util.BIZ_CONF_DEBUG(TAG,"sorry,cherry,locationCode is empty");
				break;
			}
			
			Util.setSpStringValue(AppClass.getInstance(), Constant.KEY_SP_LOCATION_CODE, locationCode);
			
			boolean isFirstCreated = Util.getSPBool(AppClass.getInstance(), Constant.KEY_SP_IS_FIRST_CREATED_ACCOUNT, true);
			
			if(!storedLocationCode.equals(locationCode) || isFirstCreated) {
						
				loadAccessNumbers();		
				locationHandler.sendEmptyMessage(0);
			}
			
		}while(false);		
	}
	
	private String locationCode = "CN";
	
	private void checkLocation(){
		
		Util.BIZ_CONF_DEBUG(TAG, "check code");
		
		do{
			
			AppLocationService locationService = new AppLocationService(mCtx);
			
			if (!locationService.isLocationEnable()) {
				
				//Util.shortToast(AppClass.getInstance(), R.string.open_location_service);
				break;
			}
			
			final Location location = new AppLocationService(mCtx).getLocation(LocationManager.GPS_PROVIDER);
			
			AppClass.getInstance().getService().submit(new Runnable() {
				
				@Override
				public void run() {
					
					Util.BIZ_CONF_DEBUG(TAG, "run method");
					
					if (null != location) {
						
						Util.BIZ_CONF_DEBUG(TAG, "longitude: " + location.getLongitude() + " location latitude:" + location.getLatitude());
						
						locationCode = requestLocationCode(location);											
					}
					
					detectLocationChange();
				}
			});
		}while(false);
	}
	
	
	@Override
	protected void onResume() {
		
		super.onResume();
		//Toast.makeText(mCtx, getCode(new AppLocationService(mCtx).getLocation(LocationManager.GPS_PROVIDER)), 0).show();
		
		initAccessNumArrowView();
	}

	private void initAccessNumArrowView() {
		
		if (isShowAccessNumDisclosureArrow()) {
			
			showDisclosureArrowOfAccessNumber();
		}else {
			
			hideArrowAndShowCursorOfAccessNum();
		}
	}

	private boolean isShowAccessNumDisclosureArrow() {

		boolean isShow = false;

		do {

			if (Util.isNetworkAvailable(mCtx)) {

				isShow = true;
				break;
			}
			
			String userInputConfCode = etConfCode.getText().toString();
			
			int confCodeBridgeInfo = 
					Util.getSPInt(mCtx, userInputConfCode,Constant.BRIDGE_ID_NOT_IN_SP);
			
			String csvFileKey = userInputConfCode + "_" + AccessNumNetParser.getLanguage();
			
			int csvFileState = Util.getSPInt(mCtx, csvFileKey, 0);
			
			if (confCodeBridgeInfo != Constant.BRIDGE_ID_NOT_IN_SP
					&& csvFileState != 0) {

				isShow = true;
				break;
			}

		} while (false);

		return isShow;
	}

	private void hideArrowAndShowCursorOfAccessNum() {
		
//		etAccessNumber.setCursorVisible(true);	
//		etAccessNumber.setCompoundDrawables(null, null, null, null);
		etAccessNumber.setVisibility(View.VISIBLE);
		tvAccessNumber.setVisibility(View.GONE);
	}

	private void showDisclosureArrowOfAccessNumber() {
		
//		Drawable disclosureArrow = mCtx.getResources().getDrawable(R.drawable.disclosure_arrow);		
//		disclosureArrow.setBounds(0, 0, disclosureArrow.getIntrinsicWidth(), disclosureArrow.getIntrinsicHeight());
//		
//		etAccessNumber.setCursorVisible(false);	
//		etAccessNumber.setCompoundDrawables(null, null, disclosureArrow, null);
//		etAccessNumber.setCompoundDrawablePadding(5);
		tvAccessNumber.setVisibility(View.VISIBLE);
		etAccessNumber.setVisibility(View.GONE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(resultCode == RESULT_OK) {
						
			String accessNumber = data.getExtras().getString(AccessNumberActivity.KEY_ACCESS_NUMBER);
			
			if(!Util.isEmpty(accessNumber)) {
				
				setAccessNumber(accessNumber);
			}
			
			//Util.requestFocus(layoutAccessNumber);
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void setAccessNumber(String accessNumber) {
		
		tvAccessNumber.setText(accessNumber);
		etAccessNumber.setText(accessNumber);
	}
	
	private boolean isAccountNameValid() {
		
		boolean isValid = true;
		
		if(Util.isEmpty(etAccountName.getText())){
			
			Util.shortToast(mCtx, R.string.toast_account_name_null);
			Util.requestFocus(etAccountName);
			isValid = false;
		}
		
		return isValid;
	}
	
	private boolean isAccessNumberValid() {
		
		boolean isValid = true;

		do{
			
			if(Util.isEmpty(etAccessNumber.getText())){
				
				Util.shortToast(mCtx, R.string.toast_dial_in_num_null);				
				//Util.requestFocus(etAccessNumber);				
				isValid = false;
				break;
			}
			
			if (isConfCodeChanged()) {
				
				Util.shortToast(mCtx, R.string.toast_conf_code_changed);
				clearSelectedAccessNumber();
				clearConfCodeViewFocus();
				isValid = false;	
				break;
			}
		}while(false);
		
		return isValid;
	}

	private void clearConfCodeViewFocus() {
		
		etConfCode.clearFocus();
	}

	private void clearSelectedAccessNumber() {
		
		etAccessNumber.setText("");
		tvAccessNumber.setText("");
	}
	
	private boolean isConfCodeValid() {
		
		boolean isValid = true;
		
		do{
			if(Util.isEmpty(etConfCode.getText())){
				
				Util.shortToast(mCtx, R.string.toast_conf_code_null);
				Util.requestFocus(etConfCode);
				isValid = false;
				break;
			}
			
			String confCode = etConfCode.getText().toString();
			
			if (confCode.length() < 2 || confCode.length() > 18) {
				
				Util.shortToast(mCtx, Util.replaceString(mCtx, 
						R.string.toast_conf_code_num_error,
						getResources().getString(R.string.conf_code)));
				
				Util.requestFocus(etConfCode);
				isValid = false;
				break;
			}
			
		}while(false);
		
		return isValid;
	}
	
	private boolean isOutCallNumberValid() {
		
		boolean isValid = true;

		boolean isDialOutEnable = dialOutModule.getSlipState();
		
		if(isDialOutEnable && !isOutCallNumValid) {
			
			Util.shortToast(mCtx, R.string.toast_request_verify_dial_out_num);
			Util.requestFocus(dialOutModule.getInputView());
			isValid = false;
		}
		
		return isValid;
	}
	
	private boolean isSecurityCodeValid() {
		
		boolean isValid = true;
		
		do{
			int moduleVisiableState = securityCodeModule.getVisibility();
			
			if (moduleVisiableState == View.GONE || 
					moduleVisiableState == View.INVISIBLE) {
				
				break;
			}
			
			boolean isSecurityCodeEnable = securityCodeModule.getSlipState();
			
			if(isSecurityCodeEnable && !checkSecurityCode()) {
				
				//Util.shortToast(mCtx, R.string.toast_request_verify_security_code);
				Util.requestFocus(securityCodeModule.getInputView());
				isValid = false;
				break;
			}			
			
		}while(false);
		
		return isValid;
	}
	
	private boolean checkAllUserInput() {
		
		boolean isAllReady = false;
		
		do{
			if(!isAccountNameValid()){

				break;
			}
			
			if(!isConfCodeValid()){

				break;
			}
						
			if (!isModeratorPwValid()) {
				
				break;
			}
			
			if(!isAccessNumberValid()){

				break;
			}
			
			if(!isOutCallNumberValid()) {
				
				break;
			}
			
//			if (!isSecurityCodeValid()) {
//				
//				break;
//			}
					
			isAllReady = true;
			
		}while(false);
		
		return isAllReady;
	}
	
	private boolean isModeratorPwValid() {
		
		boolean isModeratorPwOk = false;
		
		do {
			
			if (operatorMode == MODE_EDIT_ACCOUNT) {
				
				if (null != editAccount && Util.isEmpty(editAccount.getModeratorPw())) {
					
					isModeratorPwOk = true;
					break;
				}			
			}
			
			//when the user add normal account, the moderator password is not need
			if((mCurrentOperateType != HomeFragment.TAB_JOIN_CONF) 
					&& Util.isEmpty(etModeratorPw.getText())){
				
				Util.shortToast(mCtx, R.string.toast_moderator_pw_null);
				Util.requestFocus(etModeratorPw);
				break;
			}
			
			String moderatorPw = etModeratorPw.getText().toString();
			
			if((mCurrentOperateType != HomeFragment.TAB_JOIN_CONF) 
					&& (moderatorPw.length() < 2 || moderatorPw.length() > 18)){
				
				Util.shortToast(mCtx, Util.replaceString(mCtx, 
						R.string.toast_conf_code_num_error,
						getResources().getString(R.string.moderator_pw)));
				
				Util.requestFocus(etModeratorPw);
				break;
			}
			
			isModeratorPwOk = true;
		}while(false);		
		
		return isModeratorPwOk;
	}
	
	private void startAccessNumberActivity(String inputConfCode) {
		
		Intent intent = new Intent();
		intent.setClass(mCtx, AccessNumberActivity.class);
		
		intent.putExtra(Constant.KEY_OF_INPUT_CONF_CODE, etConfCode.getText().toString());
		mCtx.startActivityForResult(intent, 0);
	}
	
	private boolean isConfCodeChanged() {
		
		boolean isChanged = true;
			
		if (inputConfCode.equalsIgnoreCase(etConfCode.getText().toString())) {
			
			isChanged = false;
		}
		
		return isChanged;
	}
	
	private void selectOrInputAccessNumber() {
				
		do{
			initAccessNumArrowView();
			
			if (!isConfCodeValid()) {
			
				Util.BIZ_CONF_DEBUG(TAG, "conf code invalid");
				break;
			}
			
			if (isShowAccessNumDisclosureArrow()) {
				
				Util.requestFocus(layoutAccessNumber);
				startAccessNumberActivity(inputConfCode);
				etAccessNumber.clearFocus();
				break;
			}
			
			Util.shortToast(mCtx, R.string.toast_manual_input_access_num);
			Util.requestFocus(etAccessNumber);
			etAccessNumber.performClick();
		}while(false);
			
	}
	
	private void initListener() {
		
		dialOutModule.setBtClickListener(dialOutModuleListener);
		dialOutModule.setContentChangeListener(outCallNumChangeListener);
		dialOutModule.setOnContentFocusChangeListener(outCallInputFocusChangeListener);
				
		securityCodeModule.setBtClickListener(securityCodeModuleListener);
		securityCodeModule.setContentChangeListener(securityCodeChangeListener);
		securityCodeModule.setOnContentFocusChangeListener(securityCodeFocusChangeListener);
		
		etConfCode.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				
				if (!hasFocus) {
					
					if (isConfCodeChanged()) {
						
						//If first initialize the inputConfCode, not clear the access number
						if (!Util.isEmpty(inputConfCode)) {
							
							clearSelectedAccessNumber();
						}
						
						inputConfCode = etConfCode.getText().toString();						
					}
					
					checkLocation();
					
					initAccessNumArrowView();
					Util.BIZ_CONF_DEBUG(TAG, "etConfCode lose focus, store input confcode: " + inputConfCode);
				}
			}
		});
		
		layoutAccessNumber.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				layoutAccessNumber.clearFocus();			
				selectOrInputAccessNumber();
			}
		});
		
		tvAccessNumber.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialOutModule.clear_Focus();
				layoutAccessNumber.clearFocus();				
				selectOrInputAccessNumber();
			}
		});
		
//		etAccessNumber.setOnFocusChangeListener(new OnFocusChangeListener() {
//			
//			@Override
//			public void onFocusChange(View v, boolean hasFocus) {
//				
//				if (hasFocus) {
//					selectOrInputAccessNumber();
//				}
//			}
//		});		
		
		btConfirmAll.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
					do{
						if(!checkAllUserInput()){
							
							break;
						}
						
						writeAccount2Db();
						
						//set the user had created account,then will not show location prefer dialog
						Util.setSpBoolValue(AppClass.getInstance(), Constant.KEY_SP_IS_FIRST_CREATED_ACCOUNT, false);
						
						finish();													
					}while(false);
					
					//CheckHideInput.checkInputMethod();										
			}		
		});
		
		btConfirmAll1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				do{
					
					if(isOutCallNumberValid()&&isSecurityCodeValid()){
						
						layout_control_moudle.setVisibility(View.GONE);
						layout_conf_detail.setVisibility(View.VISIBLE);
						flag1 = false;
						
						if(!checkAllUserInput()){
							
							break;
						}
						
						if(operatorMode==2){
							
							writeAccount2Db();
							Toast.makeText(AddAccountActivity.this, getString(R.string.save_success), 0).show();
						}						
					}
					
					Util.requestFocus(btConfirmAll);
					//CheckHideInput.checkInputMethod();
				}while(false);							
			}
		});
	}
	
	private void writeAccount2Db() {
		
		ConfAccount account = generateAccount();
		
		switch (operatorMode) {

		case MODE_ADD_ACCOUNT:
			
			boolean isAdded = AccountsManager.getInstance().addAccount(account);
			
			if (isAdded) {
				
				AccountsManager.getInstance().insertAccountToDb(account);
			}
			
			break;
			
		case MODE_EDIT_ACCOUNT:
			
			AccountsManager accountManager = AccountsManager.getInstance();		
			ConfAccount rmAccount = accountManager.getAccountById(editAccountId);	
			int index = accountManager.removeAccount(rmAccount);
			
			//because we have delete the old account which has a id corresponding to database _id,
			//so we should set the new account id as the old
			account.setAccountId(editAccountId);			
			accountManager.addAccount(account,index);		
			//Util.BIZ_CONF_DEBUG(TAG, "edit dial out number" + account.getDialOutNumber());
			//Util.BIZ_CONF_DEBUG(TAG, "editAccountId" + editAccountId);
			
			//mAccountDb.updateObject(editAccountId, account);
			accountManager.updateAccountInDb(editAccountId, account);
			break;
			
		default:
			break;
		}
	}
	
	private ConfAccount generateAccount() {
		
		ConfAccount account = new ConfAccount();
		
		account.setConfAccountName(etAccountName.getText().toString());
		account.setAccessNumber(etAccessNumber.getText().toString());
		account.setConfCode(etConfCode.getText().toString());
		
		switch(mCurrentOperateType) {
		case HomeFragment.TAB_START_CONF:
		case HomeFragment.TAB_ORDER_CONF:
			//moderator account
			account.setModeratorPw(etModeratorPw.getText().toString());
			break;
		
		case AccountSettingActivity.SETTING_ACCOUNT_LIST:
			
			if (null != editAccount && !Util.isEmpty(editAccount.getModeratorPw())) {
				
				account.setModeratorPw(etModeratorPw.getText().toString());
			}else {
				
				account.setModeratorPw(AccountsDbTable.NORMAL_ACCOUNT_MODERATOR_PW);
			}
			break;
			
		case HomeFragment.TAB_JOIN_CONF:
		default:
			//normal account
			account.setModeratorPw(AccountsDbTable.NORMAL_ACCOUNT_MODERATOR_PW);
			break;
		}		
				
		account.setDialOutEnable(dialOutModule.getSlipState());
		account.setSecurityCodeEnable(securityCodeModule.getSlipState());
		
		//when close dial out enable ,clear the value previous
		if(!dialOutModule.getSlipState()){
			dialOutModule.setInputContent("");
		}
		
		//when close security enable ,clear the value previous
		if(!securityCodeModule.getSlipState()){
			securityCodeModule.setInputContent("");
		}
		account.setDialOutNumber(dialOutModule.getInputContent().replace("-", "w,,,,,"));
		account.setSecurityCode(securityCodeModule.getInputContent());
		
		return account;
	}
	
	private void initView() {
			
		dialOutModule = (SlipControlView) findViewById(R.id.dial_out_module);
		securityCodeModule = (SlipControlView) findViewById(R.id.security_code_module);
		
		layout_conf_detail = (LinearLayout)	findViewById(R.id.layout_conf_detail);
		layout_control_moudle = (LinearLayout) findViewById(R.id.layout_control_moudle);
		
		LinearLayout accountName = (LinearLayout) findViewById(R.id.etv_account_name);
		accountName.setBackgroundResource(R.drawable.input_top);
		
		TextView tvAccountName = (TextView) accountName.findViewById(R.id.tv_prompt);
		tvAccountName.setText(R.string.account_name);
		
		etAccountName = (EditText) accountName.findViewById(R.id.et_input);
		
		layoutAccessNumber = (LinearLayout) findViewById(R.id.layout_access_number);
		etAccessNumber = (EditText) layoutAccessNumber.findViewById(R.id.et_center_content);
		tvAccessNumber = (TextView) layoutAccessNumber.findViewById(R.id.tv_center_content);
		
		LinearLayout layoutAdvanced = (LinearLayout) findViewById(R.id.layout_access_advanced);
		EditText etAdvanced = (EditText) layoutAdvanced.findViewById(R.id.et_center_content);
		TextView tvAdvanced = (TextView) layoutAdvanced.findViewById(R.id.tv_left_toast);
		etAdvanced.setFocusable(false);
		tvAdvanced.setText(getString(R.string.add_account_advanced));
		layoutAdvanced.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				layout_conf_detail.setVisibility(View.GONE);
				layout_control_moudle.setVisibility(View.VISIBLE);
				flag1 = true;
			}
		});
				
		layoutConfCode = (LinearLayout) findViewById(R.id.etv_conf_code);
		layoutConfCode.setBackgroundResource(R.drawable.input_center);
				
		TextView tvConfCode = (TextView) layoutConfCode.findViewById(R.id.tv_prompt);
		tvConfCode.setText(R.string.conf_code);
		
		etConfCode = (EditText) layoutConfCode.findViewById(R.id.et_input);
		etConfCode.setInputType(InputType.TYPE_CLASS_NUMBER);
		
		layoutModeratorPw = (LinearLayout) findViewById(R.id.etv_moderator_pw);
		layoutModeratorPw.setBackgroundResource(R.drawable.input_bottom);
		
		TextView tvMPw = (TextView) layoutModeratorPw.findViewById(R.id.tv_prompt);
		tvMPw.setText(R.string.moderator_pw);
		
		etModeratorPw = (EditText) layoutModeratorPw.findViewById(R.id.et_input);
		etModeratorPw.setInputType(InputType.TYPE_CLASS_NUMBER);
		etModeratorPw.setTransformationMethod(PasswordTransformationMethod.getInstance());
		btConfirmAll = (Button) findViewById(R.id.bt_confirm_all);
		btConfirmAll1 = (Button) findViewById(R.id.bt_confirm_all1);
			
		initListener();
		
		do{
		
			Intent intent = getIntent();
			
			mCurrentOperateType = intent.getIntExtra(Constant.KEY_OF_OPERATE_TYPE, 
							HomeFragment.TAB_JOIN_CONF);
			
			String action = intent.getAction();
			
			if(mCurrentOperateType == HomeFragment.TAB_JOIN_CONF){
				
				layoutModeratorPw.setVisibility(View.GONE);
				layoutConfCode.setBackgroundResource(R.drawable.input_bottom);
				dialOutModule.setToastStr(R.string.allow_to_outside_call_guest);
				
				securityCodeModule.setVisibility(View.GONE);
				//dialOutModule.setBackgroundResource(R.drawable.input_single);
				dialOutModule.setToastModuleBg(R.drawable.input_single);
				dialOutModule.setToastModuleHideBg(R.drawable.input_single);
				dialOutModule.setInputModuleBg(R.drawable.input_bottom);
			}
			
			if(action.equalsIgnoreCase(ACTION_EDIT_ACCOUNT)) {
				
				operatorMode = MODE_EDIT_ACCOUNT;
				initEditContent();
				
				checkLocation();				
				break;
			}
			
			if(action.equalsIgnoreCase(ACTION_ADD_ACCOUNT)) {
				
				operatorMode = MODE_ADD_ACCOUNT;									
				break;
			}
			
		}while(false);	
	}	
	
	private void initEditContent() {
		
		editAccountId = getIntent().getLongExtra(Constant.KEY_OF_CONF_ACCOUNT_ID, -1);
		
		editAccount = AccountsManager.getInstance().getAccountById(editAccountId);
		
		if(!Util.isEmpty(editAccount)){
			
			if(Util.isEmpty(editAccount.getModeratorPw())) {
				
				layoutModeratorPw.setVisibility(View.GONE);
				layoutConfCode.setBackgroundResource(R.drawable.input_bottom);
				dialOutModule.setToastStr(R.string.allow_to_outside_call_guest);
				
				securityCodeModule.setVisibility(View.GONE);
				//dialOutModule.setBackgroundResource(R.drawable.input_single);
				dialOutModule.setToastModuleBg(R.drawable.input_single);
				dialOutModule.setToastModuleHideBg(R.drawable.input_single);
				dialOutModule.setInputModuleBg(R.drawable.input_bottom);
			}else {
				
				layoutModeratorPw.setVisibility(View.VISIBLE);
				layoutConfCode.setBackgroundResource(R.drawable.input_center);
				
				//old code for show security code
				//securityCodeModule.setVisibility(View.VISIBLE);
				//dialOutModule.setInputModuleBg(R.drawable.input_center);
				//dialOutModule.setToastModuleHideBg(R.drawable.input_top);
				//dialOutModule.setToastModuleBg(R.drawable.input_top);
				
				dialOutModule.setToastModuleBg(R.drawable.input_single);
				dialOutModule.setToastModuleHideBg(R.drawable.input_single);
				dialOutModule.setInputModuleBg(R.drawable.input_bottom);
			}
			
			String accountName = editAccount.getConfAccountName();
			String accessNumber = editAccount.getAccessNumber();
			String confCode = editAccount.getConfCode();
			String moderatorPw = editAccount.getModeratorPw();
			String dialOutNumber = editAccount.getDialOutNumber().replace("w,,,,,", "-");
			String securityCode = editAccount.getSecurityCode();
			
			if(!Util.isEmpty(accountName)) {
				etAccountName.setText(accountName);
			}
			
			if(!Util.isEmpty(accessNumber)) {
				
				setAccessNumber(accessNumber);
			}
			
			if(!Util.isEmpty(confCode)) {
				
				etConfCode.setText(confCode);
				inputConfCode = confCode;
			}
			
			if(!Util.isEmpty(moderatorPw)) {
				
				etModeratorPw.setText(moderatorPw);
			}
			
			if(editAccount.isDialOutEnable() ){
				
				dialOutModule.setSlipState(true);
				
				dialOutModule.setInputContent(dialOutNumber);
				
				dialOutModule.setBtEnable(false);
				isOutCallNumValid = true;
			}
			
			if(editAccount.isSecurityCodeEnable() ){
				
//				securityCodeModule.setSlipState(true);
//				
//				if(!Util.isEmpty(securityCode)) {
//					
//					securityCodeModule.setInputContent(securityCode);
//					securityCodeModule.setBtEnable(false);
//					isSecurityCodeValid = true;
//				}
			}
		}
	}
	@Override
	public void onImgHomeClicked(View v) {
		
		//CheckHideInput.checkInputMethod();
		
		if(flag1==false){
			super.onImgHomeClicked(v);
		}
		if(isOutCallNumberValid()&&isSecurityCodeValid()){
			if(flag1 == true){
					layout_control_moudle.setVisibility(View.INVISIBLE);
					layout_conf_detail.setVisibility(View.VISIBLE);
					flag1 = false;
					if(operatorMode==2){
						writeAccount2Db();
						Toast.makeText(AddAccountActivity.this, getString(R.string.save_success), 0).show();
					}
				}	
		}
		
		
	}
}
