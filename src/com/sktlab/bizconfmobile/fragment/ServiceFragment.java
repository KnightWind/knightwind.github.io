package com.sktlab.bizconfmobile.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.activity.AccessNumberActivity;
import com.sktlab.bizconfmobile.customview.TextArrow;
import com.sktlab.bizconfmobile.util.Util;

public class ServiceFragment extends Fragment {

	private Activity mActivity;
	private TextArrow mAccessNumber;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_service_main, container, false);
	}

	@Override
	public void onResume() {
		super.onResume();
		
		mActivity = getActivity();
		mAccessNumber = (TextArrow) mActivity.findViewById(R.id.service_world_access_number);
				
		mAccessNumber.setOnArrowClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Util.startActivity(mActivity, AccessNumberActivity.class);
			}
		});
		
		mAccessNumber.setVisibility(View.GONE);
	}
		
}
