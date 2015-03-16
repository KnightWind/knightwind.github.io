package com.sktlab.bizconfmobile.activity;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.util.Util;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class RepeatSelectionActivity extends BaseActivity {

	public static final String SELECTED_REPEAT_PERIOD = "selectedRepeatPeriod";

	public static final int PERIOD_NONE = 1000;
	public static final int PERIOD_DAY = 1001;
	public static final int PERIOD_WEEK = 1002;
	public static final int PERIOD_MONTH = 1003;
	public static final int PERIOD_YEAR = 1004;

	private int mPeriod = PERIOD_NONE;
	private RadioGroup mRgGroup;
	
	//map period type to radio button id
	private SparseIntArray type2RadioId;
	
	public static SparseArray<String> type2String = new SparseArray<String>();
	
	public static String getPeriodString(int type) {
		
		AppClass ctx = AppClass.getInstance();
		
		if(type2String.size() == 0) {
			
			type2String.put(PERIOD_NONE, ctx.getString(R.string.noRepeat));
			type2String.put(PERIOD_DAY, ctx.getString(R.string.everyDay));
			type2String.put(PERIOD_WEEK, ctx.getString(R.string.everyWeek));
			type2String.put(PERIOD_MONTH, ctx.getString(R.string.everyMonth));
			type2String.put(PERIOD_YEAR, ctx.getString(R.string.everyYear));
		}
		
		//Util.BIZ_CONF_DEBUG(TAG, "type2String size: " + type2String.size());
		
		return type2String.get(type, ctx.getString(R.string.noRepeat));
	}
	
	private OnCheckedChangeListener mRgListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {

			int index = type2RadioId.indexOfValue(checkedId);
			
			if (index >= 0){
				
				mPeriod = type2RadioId.keyAt(index);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_repeat_selection);

		// show right button in title
		setShowRightButton(true);
		
		init();	
	}
	
	private void init() {
		type2RadioId = new SparseIntArray();
		
		mRgGroup = (RadioGroup) findViewById(R.id.rg_repeat_period);
		mRgGroup.setOnCheckedChangeListener(mRgListener);
		
		type2RadioId.put(PERIOD_NONE, R.id.radio_none);
		type2RadioId.put(PERIOD_DAY, R.id.radio_every_day);
		type2RadioId.put(PERIOD_WEEK, R.id.radio_every_week);
		type2RadioId.put(PERIOD_MONTH, R.id.radio_every_month);
		type2RadioId.put(PERIOD_YEAR, R.id.radio_every_year);
		
		handleIntent();
	}
	
	private void handleIntent() {
		
		Intent intent = getIntent();
		mPeriod = intent.getIntExtra(SELECTED_REPEAT_PERIOD,PERIOD_NONE);
		
		//Util.BIZ_CONF_DEBUG(TAG, "handle intent mPeriod: " + mPeriod);
		Integer checkedId = type2RadioId.get(mPeriod);
		
		if(!Util.isEmpty(checkedId)) {
			
			mRgGroup.check(checkedId);
		}		
	}
	
	@Override
	public void onRightButtonClicked(View v) {
		
		Intent data = new Intent();
		data.putExtra(SELECTED_REPEAT_PERIOD, mPeriod);
		setResult(RESULT_OK, data);
		
		finish();
	}

	@Override
	public void finish() {
		super.finish();
	}	
}
