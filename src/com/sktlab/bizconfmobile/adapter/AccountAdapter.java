package com.sktlab.bizconfmobile.adapter;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.activity.AccountSettingActivity;
import com.sktlab.bizconfmobile.activity.AddAccountActivity;
import com.sktlab.bizconfmobile.activity.AppClass;
import com.sktlab.bizconfmobile.activity.ConferenceActivity;
import com.sktlab.bizconfmobile.activity.OrderConfActivity;
import com.sktlab.bizconfmobile.fragment.HomeFragment;
import com.sktlab.bizconfmobile.interfaces.ILoadingDialogCallback;
import com.sktlab.bizconfmobile.model.ConfControl;
import com.sktlab.bizconfmobile.model.ConfAccount;
import com.sktlab.bizconfmobile.model.Constant;
import com.sktlab.bizconfmobile.model.AppointmentConf;
import com.sktlab.bizconfmobile.model.manager.AccountsManager;
import com.sktlab.bizconfmobile.model.manager.CommunicationManager;
import com.sktlab.bizconfmobile.model.manager.AppointmentConfManager;
import com.sktlab.bizconfmobile.util.CalendarUtil;
import com.sktlab.bizconfmobile.util.LoadingDialogUtil;
import com.sktlab.bizconfmobile.util.Util;

public class AccountAdapter extends BaseAdapter 
				implements DialogInterface.OnClickListener,
								ILoadingDialogCallback{
	
	public static final String TAG = "ConfAccountAdapter";
	
	private int mLayoutId;
	private Activity mActivity;
	private LayoutInflater mInflater;
	private ArrayList<ConfAccount> mConfAccountDatas;
	
	
	private SparseArray<LinearLayout> map;
	private SparseArray<View> maps;
	
	private ConfAccount mSelectedAccount;

	private int mFunctionType = -1;
	
	private ListView mLv;
	//reset dialog type
	private static final int DIALOG_TYPE_RESET = 0;
	// start conference dialog type
	private static final int DIALOG_TYPE_START_CONF = 1;
	//delete conference dialog type
	private static final int DIALOG_TYPE_DEL_ACCOUNT = 2;
	
	private int mDialogType = DIALOG_TYPE_START_CONF;

	public void setmSelectedAccount(ConfAccount mSelectedAccount) {
		this.mSelectedAccount = mSelectedAccount;
	}
	
	public AccountAdapter(Activity ctx,int layoutId) {
		
		mActivity = ctx;
		mLayoutId = layoutId;
		mConfAccountDatas = new ArrayList<ConfAccount>();	
		
		init();
	}
	
	public AccountAdapter(Activity ctx,int layoutId, ArrayList<ConfAccount> list) {
		
		mActivity = ctx;
		mLayoutId = layoutId;
		mConfAccountDatas = list;
		
		init();
	}
	
	private void init(){
		
		mInflater = (LayoutInflater) mActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		map = new SparseArray<LinearLayout>();
		maps = new SparseArray<View>();
	}
	
	public void setData(ArrayList<ConfAccount> datas) {
		
		mConfAccountDatas = datas;
		mDialogType = DIALOG_TYPE_START_CONF;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		
		return mConfAccountDatas.size();
	}

	@Override
	public Object getItem(int position) {
		
		return null;
	}

	@Override
	public long getItemId(int position) {
	
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder vh = null;
		
		if (convertView== null) {
			
			convertView = mInflater.inflate(mLayoutId, null);
			
			vh = new ViewHolder();
			
			vh.layoutTvs = (LinearLayout)convertView
						.findViewById(R.id.layout_account_info);
			
			vh.tvConfAccountName = (TextView)convertView
						.findViewById(R.id.tv_conf_name);
			vh.tvConfCode = (TextView)convertView
						.findViewById(R.id.tv_conf_code);
			
			vh.layoutBts = (LinearLayout)convertView
						.findViewById(R.id.layout_bt_module);
			
			vh.btEdit = (Button) convertView
						.findViewById(R.id.bt_edit);
			vh.btStart = (Button) convertView
					.findViewById(R.id.bt_start);
			vh.btDel = (Button) convertView
					.findViewById(R.id.bt_del);
			convertView.setTag(vh);
		}else{
			vh = (ViewHolder) convertView.getTag();
		}
		
		if (mConfAccountDatas == null) {
			
			return null;
		}
		
		final ConfAccount confAccount = mConfAccountDatas.get(position);
		
		if (confAccount == null) {
			
			return null;
		}
		vh.layoutBts.setVisibility(View.VISIBLE);
		//when refresh list view ,we should hide the button layout in the first time
		vh.layoutBts.setVisibility(View.GONE);
		
		String ConfAccountName = confAccount.getConfAccountName();
		String ConfCode = confAccount.getConfCode();
				
		vh.tvConfAccountName.setText(ConfAccountName);
		vh.tvConfCode.setText(ConfCode);
		
		map.put(position, vh.layoutBts);
		
		final int pos = position;
		
		final Button fuc = vh.btStart;
		
		vh.layoutTvs.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				switch (mFunctionType) {

				case HomeFragment.TAB_JOIN_CONF:
					fuc.setText(R.string.toast_in_button_join);
					break;
					
				case HomeFragment.TAB_START_CONF:
					fuc.setText(R.string.begin_conf);
					break;
				}

				isShowFunctionBts(pos);
			}
		});
		
		vh.layoutTvs.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				
				mSelectedAccount = confAccount;
				doWhenLongClickAccount();
					
				Util.BIZ_CONF_DEBUG(TAG, "account list long clicked in start conf page");

				return true;
			}
		});
		
		vh.btEdit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				boolean isOngoingAccount = isOngoingAccount();
				
				if (isOngoingAccount) {
					
					Util.shortToast(mActivity, R.string.toast_not_edit_account);
				}else {
					
					mSelectedAccount = confAccount;				
					startEditAccountActivity();
				}
			}
		});
		
		vh.btStart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				CommunicationManager cm = CommunicationManager.getInstance();
				
				if (cm.isTurn2HomePage()) {
					
					Util.shortToast(mActivity, R.string.toast_long_click_enter_conf);
				}else {
					
					mSelectedAccount = confAccount;
					
					mDialogType = DIALOG_TYPE_START_CONF;
					showDialog();
				}
			}
		});
		
		final LinearLayout layoutButtons = vh.layoutBts;
		
		vh.btDel.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			boolean isOngoingAccount = isOngoingAccount();
			
			if (isOngoingAccount) {
				
				Util.shortToast(mActivity, R.string.toast_not_del_account);
			}else {
				
				mDialogType = DIALOG_TYPE_DEL_ACCOUNT;
				
				mSelectedAccount = confAccount;
				
				deleteAccount();		
				layoutButtons.setVisibility(View.GONE);
			}
		}
		});
		
		//System.out.println("aaaaaaaaafinal"+convertView+"aaaaaa"+pos);
		return convertView;
	}
	
	/**
	 * whether to show the function buttons
	 * @param pos
	 */
	private void isShowFunctionBts(int pos){
		
		LinearLayout layout = map.get(pos);
		mSelectedAccount = mConfAccountDatas.get(pos);
		
		do {
			
			if (mFunctionType == AccountSettingActivity.SETTING_ACCOUNT_LIST) {
				
				startEditAccountActivity();
				break;
			}
			
			//order conference module always hide the functions module
			if(mFunctionType == HomeFragment.TAB_ORDER_CONF) {
				
				startOrderConfActivity();
				break;
			}
			
			Util.BIZ_CONF_DEBUG(TAG, "click pos:" + pos + "visibility:" + layout.getVisibility());
			
			if(layout.getVisibility() == View.GONE) {
														
				int totalSize = map.size();
				
				for ( int i = 0; i < totalSize;i++) {
					
					LinearLayout accountLayout = map.get(i);
					
					if (i != pos) {
						
						accountLayout.setVisibility(View.GONE);
					}else {
						
						accountLayout.setVisibility(View.VISIBLE);
					}
				}
				
				layout.setVisibility(View.VISIBLE);
				
				if ((null != mLv) && (pos == mConfAccountDatas.size() - 1)) {
					
					//Util.BIZ_CONF_DEBUG(TAG, "bottom clicked");
					
					mLv.setSelection(mLv.getBottom());
				}		
				
				Util.BIZ_CONF_DEBUG(TAG, "reset layout status click pos:" + pos + "visibility:" + layout.getVisibility());
				break;
			}
				
			layout.setVisibility(View.GONE);			
		}while(false);
	}
	
	private void doWhenLongClickAccount() {
		
		if (isOngoingAccount()) {		
				
			startConferenceActivity();
		}
	}
	
	private boolean isOngoingAccount() {
		
		boolean result = false;
		
		do{
			CommunicationManager cm = CommunicationManager.getInstance();
			
			if (!cm.isTurn2HomePage()) {
				
				break;
			}
			
			ConfAccount activeConfAccount = cm.getActiveAccount();
			
			if (null != mSelectedAccount && null != activeConfAccount 
					&& mSelectedAccount.getConfCode()
						.equals(activeConfAccount.getConfCode())
					&& mSelectedAccount.getConfAccountName()
						.equals(activeConfAccount.getConfAccountName())
					&& mSelectedAccount.getModeratorPw()
						.equals(activeConfAccount.getModeratorPw())) {
				
				result = true;
			}
			
		}while(false);
		
		return result;
	}
	
	private void startConferenceActivity() {
		
		Util.BIZ_CONF_DEBUG(TAG, "go to conference view from main~");
		
		Intent intent = new Intent();
		intent.setClass(mActivity, ConferenceActivity.class);
		
		mActivity.startActivity(intent);
	}
	
	/**
	 * 
	 */
	private void startOrderConfActivity(){
		
		CommunicationManager.getInstance().setActiveAccount(mSelectedAccount);
		
		Intent intent = new Intent();
		intent.setClass(mActivity, OrderConfActivity.class);
		intent.putExtra(Constant.KEY_OF_CONF_ACCOUNT_ID, mSelectedAccount.getAccountId());
		
		mActivity.startActivity(intent);
	}
	
	/**
	 * start edit account activity
	 */
	private void startEditAccountActivity() {
		
		Intent intent = new Intent();
		intent.putExtra("advanced", '1');
		intent.setClass(mActivity, AddAccountActivity.class);
		intent.setAction(AddAccountActivity.ACTION_EDIT_ACCOUNT);
		intent.putExtra(Constant.KEY_OF_CONF_ACCOUNT_ID, mSelectedAccount.getAccountId());
		intent.putExtra(Constant.KEY_OF_OPERATE_TYPE, mFunctionType);
		
		mActivity.startActivity(intent);
	}
	
	private boolean haveScheduleMeetings() {
		
		ArrayList<AppointmentConf> meetings = AppointmentConfManager.getInstance()
				.getAllMeetings();

		boolean haveSchedule = false;

		for (AppointmentConf meeting : meetings) {

			if (mSelectedAccount != null && meeting.getAccountId().equals(
					String.valueOf(mSelectedAccount.getAccountId()))) {

				haveSchedule = true;
				//Util.BIZ_CONF_DEBUG(TAG, "delete account had records");
				break;
			}
		}
		
		return haveSchedule;
	}
	/**
	 * delete the selected account
	 */
	private void deleteAccount() {

		showDialog();
	}
	
	/**
	 * show a dialog to user to confirm the start conference operation
	 */
	private void showDialog() {
		
		do {

			if (null == mSelectedAccount) {
				break;
			}

			String title = mSelectedAccount.getConfAccountName();

			if (mDialogType == DIALOG_TYPE_START_CONF) {
				
				switch (mFunctionType) {

				case HomeFragment.TAB_JOIN_CONF:
					
					generateDialog(
							mActivity.getResources().getString(R.string.app_name),
							Util.replaceString(mActivity,
									R.string.toast_join_other_conference, title));
					break;
					
				case HomeFragment.TAB_START_CONF:
					
					generateDialog(
							mActivity.getResources().getString(R.string.app_name),
							Util.replaceString(mActivity,
									R.string.toast_start_conf_msg, title));
					break;
				}
				break;
			}

			if (mDialogType == DIALOG_TYPE_DEL_ACCOUNT) {
				
				boolean haveSchedule = haveScheduleMeetings();
				
				if (haveSchedule) {

					generateDialog(
							mActivity.getResources().getString(R.string.del),
							mActivity.getResources().getString(
									R.string.toast_del_account_msg));
					break;
				}

				generateDialog(
						mActivity.getResources().getString(R.string.del),
						Util.replaceString(mActivity,
								R.string.toast_del_no_schedule_account, title));

				break;
			}
		} while (false);
	}
	
	private void generateDialog(String title, String msg) {
		
		new AlertDialog.Builder(mActivity)
		.setTitle(title)
		.setMessage(msg)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setPositiveButton(android.R.string.ok, this)
		.setNegativeButton(android.R.string.cancel, this).show();
	}
	
	/**
	 * connect to the server to start a conference
	 * @return
	 */
	private void connectToStartConf(){

		CommunicationManager.getInstance().setActiveAccount(mSelectedAccount);
		ConfControl.getInstance().startConf(mActivity, this);
	}
	
	/**
	 * when user selected a operation in the confirm to start conference dialog
	 */
	@Override
	public void onClick(DialogInterface dialog, int which) {
		
		if (which == DialogInterface.BUTTON_POSITIVE) {
			
        	switch(mDialogType) {
        	
        		case DIALOG_TYPE_START_CONF:
        			
        			connectToStartConf();
        			break;
        			
        		case DIALOG_TYPE_DEL_ACCOUNT:
        			
        			removeMeetings();
        			break;
        			
        		default:
        			break;
        	}
        }
        
       //mDialogType = DIALOG_TYPE_RESET;
	}
	
	public void removeMeetings() {
		
		boolean haveSchedule = haveScheduleMeetings();
		
		if (!haveSchedule) {
			
			this.onSuccessDone();
			
			return;
		}
		
		final LoadingDialogUtil dialog 
				= new LoadingDialogUtil(mActivity, this);
		
		ExecutorService service = AppClass.getInstance().getService();
		
		service.submit(new Runnable() {
			
			@Override
			public void run() {
				
				dialog.showDialog(R.string.toast_being_delete_order_meeting, 30000);
				
				//All meetings user ordered
				ArrayList<AppointmentConf> meetings = AppointmentConfManager.getInstance().getAllMeetings();
				
				Hashtable<String, ArrayList<AppointmentConf>> dayToMeetings 
							= AppointmentConfManager.getInstance().getDayToMeetings();
				
				ArrayList<String> deleteKeys = new ArrayList<String>();
				
				ArrayList<Long> meetingIds = new ArrayList<Long>();
				
				Set<String> keys = dayToMeetings.keySet();
				
				for (String key : keys) {
										
					ArrayList<AppointmentConf> dayMeetings = dayToMeetings.get(key);
					
					ArrayList<AppointmentConf> tempMeetings = new ArrayList<AppointmentConf>(dayMeetings);
					
					for (AppointmentConf meeting : tempMeetings) {
						
						if (meeting.getAccountId().equals(
								String.valueOf(mSelectedAccount.getAccountId()))) {
							
							//Util.BIZ_CONF_DEBUG(TAG, "delete meeting id: " + meeting.getId());
							
							if (dayMeetings.contains(meeting)) {
								
								dayMeetings.remove(meeting);
							}
						
							meetings.remove(meeting);
							
							meetingIds.add(meeting.getId());
							
							CalendarUtil.deleteEvent(Long.valueOf(meeting.getEventId()));
						}
					}
					
					//if all meetings of a day had been deleted, remove the key-map from dayMeetings.
					if (dayMeetings.isEmpty()) {
						
						deleteKeys.add(key);
					}
				}
				
				for (String key : deleteKeys) {
					
					//Util.BIZ_CONF_DEBUG(TAG, "delete meeting keys now~");
					
					dayToMeetings.remove(key);
				}
				
				AppointmentConfManager.getInstance().deleteMeetingsInDb(meetingIds);
							
				dialog.finishDialogSuccessDone();
			}
		});
	}
	
	class ViewHolder {
		private TextView tvConfAccountName;
		private TextView tvConfCode;
		private LinearLayout layoutTvs;
		private Button btEdit;
		private Button btStart;
		private Button btDel;
		private LinearLayout layoutBts;
	}
	
	/**
	 * connect to server success to start a conference, including initialize of the session~
	 * when everything is done, this method will be called to enter the conference operate view
	 */
	@Override
	public void onSuccessDone() {

		do {
			
			if (mDialogType == DIALOG_TYPE_DEL_ACCOUNT) {
				
				//delete account in database
				int index = AccountsManager.getInstance().removeAccount(mSelectedAccount);

				if (index != -1) {

					AccountsManager.getInstance().deleteAccountInDb(
							mSelectedAccount.getAccountId());
				}
				
				notifyDataSetChanged();
				break;
			}
			
			Util.BIZ_CONF_DEBUG(TAG, "start enter conference activity");
			
			Intent intent = new Intent();

			intent.setClass(mActivity, ConferenceActivity.class);
			intent.putExtra(Constant.KEY_OF_CONF_ACCOUNT_ID,
					mSelectedAccount.getAccountId());
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			
			mActivity.startActivity(intent);
		}while(false);	
	}
	
	/**
	 * connect to server error to start a conference, can show some message here to remind the user
	 */
	@Override
	public void onDoneWithError() {
		
		do {
			
			if (mDialogType == DIALOG_TYPE_DEL_ACCOUNT) {
				
				Util.shortToast(mActivity, R.string.toast_del_event_time_out);
				break;
			}
			
			ConfControl.getInstance().disconnectToServer();			
			Util.shortToast(mActivity, R.string.toast_start_conf_failed);
		}while(false);
	}
	
	/**
	 * get the type of current operation, there are three selection: Start Conference; Order Conference; Join Conference
	 * @return
	 */
	public int getFunctionType() {
		return mFunctionType;
	}

	public void setFunctionType(int mFunctionType) {
		this.mFunctionType = mFunctionType;
	}

	public ListView getLv() {
		return mLv;
	}

	public void setLv(ListView mLv) {
		this.mLv = mLv;
	}
}
