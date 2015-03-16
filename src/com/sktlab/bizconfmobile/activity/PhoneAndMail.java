package com.sktlab.bizconfmobile.activity;

import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sktlab.bizconfmobile.R;

public class PhoneAndMail extends BaseActivity {
		private ListView pam;
		private TextView name;
		private ImageView head;
		private List<String> pam_info;
	protected void onCreate(Bundle savedInstanceState) {
			
			super.onCreate(savedInstanceState);
			setContentView(R.layout.phone_mail_view);
			pam = (ListView) findViewById(R.id.phone_and_mail_lv);
			name = (TextView) findViewById(R.id.phone_and_mail_tv);
			head = (ImageView) findViewById(R.id.phone_and_mail_iv);
			
			
			Intent phoneAndMail = getIntent();
			Bundle b=phoneAndMail.getBundleExtra("phoneAndMail");
			pam_info = b.getStringArrayList("pam");
			String name_string = b.getString("name");
			String path_string = b.getString("path");
			
			name.setText(name_string);
			if(null != path_string){
				head.setImageURI(Uri.parse(path_string));
			}
			
			pam.setAdapter(new ArrayAdapter<String>(this,R.layout.simple_text1,R.id.sm_contact_index_tv,pam_info));
			pam.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					Intent data = new Intent();
					data.putExtra("pom", pam_info.get(arg2));
					data.putExtra("pos", arg2);
					setResult(20, data);
					finish();
				}
			});
		}
	}