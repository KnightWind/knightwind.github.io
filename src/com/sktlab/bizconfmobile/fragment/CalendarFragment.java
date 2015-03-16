package com.sktlab.bizconfmobile.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.activity.ConferenceActivity;
import com.sktlab.bizconfmobile.customview.CalendarView;
import com.sktlab.bizconfmobile.customview.Cell;
import com.sktlab.bizconfmobile.interfaces.ILoadingDialogCallback;
import com.sktlab.bizconfmobile.model.ConfControl;
import com.sktlab.bizconfmobile.model.ConfAccount;
import com.sktlab.bizconfmobile.model.Constant;
import com.sktlab.bizconfmobile.model.AppointmentConf;
import com.sktlab.bizconfmobile.model.manager.AccountsManager;
import com.sktlab.bizconfmobile.model.manager.AppointmentConfManager;
import com.sktlab.bizconfmobile.model.manager.CommunicationManager;
import com.sktlab.bizconfmobile.util.CalendarUtil;
import com.sktlab.bizconfmobile.util.Util;

public class CalendarFragment extends Fragment 
		implements CalendarView.OnCellTouchListener, ILoadingDialogCallback,
					DialogInterface.OnClickListener{
	
	public static final String TAG = "CalendarFragment";
	
	private Activity mActivity;
	
	private Button mPreMonth;
	private Button mNextMonth;
	private TextView mTvDate;
	
	private CalendarView mCalendarView;
	
	private LinearLayout mConfRecords;
	
	private ConfAccount mSelectedAccount;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//MeetingManager.getInstance().loadMeetingFromDb();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.fragment_calendar_main, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		initView();
	}

	@Override
	public void onResume() {
		
		super.onResume();
		
		//initView();
	}
	
	public void initView() {
		
		mActivity = getActivity();
		
		mPreMonth = (Button) mActivity.findViewById(R.id.bt_pre_month);
		mNextMonth = (Button) mActivity.findViewById(R.id.bt_next_month);
		mTvDate = (TextView) mActivity.findViewById(R.id.tv_date);
		
		mCalendarView = (CalendarView)mActivity.findViewById(R.id.calendar);
		mCalendarView.setOnCellTouchListener(this);
		
		mConfRecords = (LinearLayout)mActivity.findViewById(R.id.conf_records);
			
		setTopDate();
		initListener();
		
		//show today's records
		Calendar calendar = Calendar.getInstance();
		
		showCellRecord(CalendarUtil.getFomatDateStr(calendar.getTimeInMillis()));
	}
	
	public LinearLayout generateRecordView() {
		
		LayoutInflater inflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		LinearLayout record = (LinearLayout)inflater.inflate(R.layout.item_conf_records, null);
		
		return record;
	}
	
	public void setTopDate() {
		
		mTvDate.setText(mCalendarView.getDisplayDateStr());
	}
	
	Handler mHandler = new Handler();
	
	public void initListener() {
		
		mPreMonth.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				updateMonth(false);
			}
		});
		
		mNextMonth.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				updateMonth(true);
			}
		});
	}
	
	private void updateMonth(boolean isNextMonth) {
		
		mConfRecords.removeAllViews();
		
		if(!Util.isEmpty(mCalendarView)) {
			
			if (isNextMonth) {
				
				mCalendarView.nextMonth();
			}else {
				
				mCalendarView.previousMonth();
			}			
			
			mHandler.post(new Runnable() {
				public void run() {
					setTopDate();
				}			
			});		
			
			Calendar calendar = Calendar.getInstance();	
			
			if (mCalendarView.getYear() == calendar.get(Calendar.YEAR) &&
					mCalendarView.getMonth() == calendar.get(Calendar.MONTH)) {
				
				showCellRecord(CalendarUtil.getFomatDateStr(calendar.getTimeInMillis()));
			}
		}
	}
	
	public void showCellRecord(String dateFomatStr){
		
		mConfRecords.removeAllViews();
		
		Util.BIZ_CONF_DEBUG(TAG, "touch cell date: " + dateFomatStr);
		
		ArrayList<AppointmentConf> meetings = 
				AppointmentConfManager.getInstance().getCellMeetings(dateFomatStr);
		
		if (!Util.isEmpty(meetings)) {
			
			for (AppointmentConf meeting : meetings) {
				
				LinearLayout meetingLayout = generateRecordView();
				TextView title = (TextView)meetingLayout.getChildAt(0);
				title.setText(meeting.getTitle());
				TextView confCode = (TextView)meetingLayout.getChildAt(1);
				
				final ConfAccount account = AccountsManager.getInstance()
							.getAccountById(Long.valueOf(meeting.getAccountId()));
				
				if (Util.isEmpty(account)) {
					
					confCode.setText(R.string.toast_conf_account_be_removed);
					mConfRecords.addView(meetingLayout);
					continue;
				}
				
				confCode.setText(account.getConfCode());				
				mConfRecords.addView(meetingLayout);
				
				//Util.BIZ_CONF_DEBUG(TAG, "add meeting record: " + meeting.getTitle());		
				meetingLayout.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						mSelectedAccount = account;
						showDialog();
					}
				});
			}
		}
	}
	
	public void showCellRecord(Cell cell) {
			
		String dateFomatStr = cell.getDateFomatStr();
		
		showCellRecord(dateFomatStr);
	}
	
	@Override
	public void onTouch(Cell cell) {
	
		showCellRecord(cell);
	}
	
	private void showDialog() {

		if (mSelectedAccount != null) {

			String confMsg = mSelectedAccount.getConfAccountName();
			
			if (Util.isEmpty(confMsg)) {
				
				confMsg = mSelectedAccount.getConfCode();
			}
			
			String msg = Util.replaceString(mActivity, R.string.toast_start_conf_msg, confMsg);
			
			new AlertDialog.Builder(mActivity)
					.setMessage(msg)
					.setTitle(mActivity.getResources().getString(R.string.app_name))
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setPositiveButton(android.R.string.ok, this)
					.setNegativeButton(android.R.string.cancel, this).show();
		}
	}
	
	/**
	 * connect to the server to start a conference
	 * @return
	 */
	private void connectToStartConf(){

		CommunicationManager.getInstance().setActiveAccount(mSelectedAccount);		
		ConfControl.getInstance().startConf(mActivity, this);
	}

	@Override
	public void onSuccessDone() {
			
		Intent intent = new Intent();

		intent.setClass(mActivity, ConferenceActivity.class);
		intent.putExtra(Constant.KEY_OF_CONF_ACCOUNT_ID,
				mSelectedAccount.getAccountId());
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		mActivity.startActivity(intent);	
	}

	@Override
	public void onDoneWithError() {

		Util.shortToast(mActivity, R.string.toast_start_conf_failed);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		
		if (which == DialogInterface.BUTTON_POSITIVE) {
        	
			if (CommunicationManager.getInstance().isTurn2HomePage()) {
				
				Util.shortToast(mActivity, R.string.toast_not_enter_conference);
			}else {
				
				connectToStartConf();     
			}
        }
	}
}
