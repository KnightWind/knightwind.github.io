package com.sktlab.bizconfmobile.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.util.CheckHideInput;

public class PasswdProtectActivity extends BaseActivity {
	private EditText passwd,confirm_Passwd;
	private Button confirm;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.passwd_protect_view);
		
		
		passwd=(EditText)findViewById(R.id.EditText_passwd);
		confirm_Passwd=(EditText)findViewById(R.id.EditText_confirm_passwd);
		confirm=(Button)findViewById(R.id.passwd_protect_confirm);
	}
	protected void onResume() {
		super.onResume();
		
		confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				CheckHideInput.checkInputMethod();
				String string_passwd=passwd.getText().toString().trim();
				String string_confirm_Passwd=confirm_Passwd.getText().toString().trim();
				if(!string_passwd.equals(string_confirm_Passwd)){
					Toast.makeText(PasswdProtectActivity.this, getString(R.string.passwd_is_not_equal), 0).show();
					return;
				}else if(string_confirm_Passwd.equals("")){
					Toast.makeText(PasswdProtectActivity.this, getString(R.string.passwd_is_not_null), 0).show();
				}else{
					//灏���ㄦ�疯�剧疆���瀵����瀛����sharedPreference涓�
					SharedPreferences sp=getApplicationContext().getSharedPreferences("protect_passwd", Context.MODE_PRIVATE);
					Editor edit=sp.edit();
					edit.putString("protect_passwd",string_passwd);
					edit.commit();
					Toast.makeText(PasswdProtectActivity.this, getString(R.string.passwd_protect_set_success), 0).show();
					onDestroy();
				}
			}
		});
		
	}
	@Override
	protected void onDestroy(){
		this.finish();
		super.onDestroy();
	}
	
}
