package com.sktlab.bizconfmobile.activity;

import java.util.concurrent.ExecutorService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.interfaces.ILoadingDialogCallback;
import com.sktlab.bizconfmobile.model.Constant;
import com.sktlab.bizconfmobile.net.NetOp;
import com.sktlab.bizconfmobile.net.StatusCode;
import com.sktlab.bizconfmobile.util.LoadingDialogUtil;
import com.sktlab.bizconfmobile.util.Util;

public class VerifyCodeActivity extends BaseActivity implements ILoadingDialogCallback {
	
	private String verifyPhoneNum;
	private EditText etVerifyCode;
	private Activity mActivity;
	private LoadingDialogUtil dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_verify_phone_number);
		
		mActivity = this;
		etVerifyCode = (EditText)findViewById(R.id.et_input_verify_code);	
		Intent intent = getIntent();	
		verifyPhoneNum = intent.getStringExtra(Constant.KEY_VERIFY_PHONE_NUM);	
		dialog = new LoadingDialogUtil(this, this);
	}
	
	public void onConfirmClicked(View v){
		
		final String inputVerifyCode = etVerifyCode.getText().toString();
		
		ExecutorService service = AppClass.getInstance().getService();
		
		service.submit(new Runnable() {
			
			@Override
			public void run() {
				
				dialog.showDialog(mActivity.getString(R.string.toast_check_code));
				
				String statusCode = NetOp.checkInputCode(verifyPhoneNum, inputVerifyCode);
				
				if (statusCode.equalsIgnoreCase(StatusCode.TEL_VERIFY_CODE_SUCCESS)) {
					
					dialog.finishDialogSuccessDone();
				}else {
					
					dialog.finishDialogWithErrorMsg();
				}
			}
		});
	}

	@Override
	public void onSuccessDone() {
		
		Util.setSpBoolValue(this, Constant.KEY_SP_PHONE_NUM_VER, true);	
		Util.setSpStringValue(this, 
				Constant.KEY_SP_VERIFIED_PHONE_NUM, verifyPhoneNum);
		finish();
	}

	@Override
	public void onDoneWithError() {
		
		Util.shortToast(mActivity, R.string.toast_verify_code_error);
	}
	
	
}
