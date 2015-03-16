package com.sktlab.bizconfmobile.activity;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.util.Util;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class AboutUsActivity extends BaseActivity {

	private TextView mTvAboutUs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_about_us);
		
		mTvAboutUs = (TextView) findViewById(R.id.tv_about_us);
	
		mTvAboutUs.setText(Html.fromHtml(getResources().getString(R.string.toast_about_us)));		
		mTvAboutUs.setMovementMethod(LinkMovementMethod.getInstance());
		
		mTvAboutUs.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return false;
			}
		});
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
	
}
