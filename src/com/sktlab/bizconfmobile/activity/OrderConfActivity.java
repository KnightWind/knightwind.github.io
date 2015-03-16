package com.sktlab.bizconfmobile.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.text.Html;
import android.text.InputType;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.customview.SlipControlView;
import com.sktlab.bizconfmobile.customview.TextArrow;
import com.sktlab.bizconfmobile.customview.SlipControlView.OnContentChangeListener;
import com.sktlab.bizconfmobile.model.BridgeInfo;
import com.sktlab.bizconfmobile.model.ConfAccount;
import com.sktlab.bizconfmobile.model.Constant;
import com.sktlab.bizconfmobile.model.ContactItem;
import com.sktlab.bizconfmobile.model.AppointmentConf;
import com.sktlab.bizconfmobile.model.EmailContent;
import com.sktlab.bizconfmobile.model.NetAccessNumberDataLoader;
import com.sktlab.bizconfmobile.model.Participant;
import com.sktlab.bizconfmobile.model.factory.EmailContentFactory;
import com.sktlab.bizconfmobile.model.manager.AccountsManager;
import com.sktlab.bizconfmobile.model.manager.AppointmentConfManager;
import com.sktlab.bizconfmobile.model.manager.ContactManager;
import com.sktlab.bizconfmobile.model.sms.Threads;
import com.sktlab.bizconfmobile.net.NetOp;
import com.sktlab.bizconfmobile.parser.AccessNumNetParser;
import com.sktlab.bizconfmobile.util.CalendarUtil;
import com.sktlab.bizconfmobile.util.DateUtil;
import com.sktlab.bizconfmobile.util.FileUtil;
import com.sktlab.bizconfmobile.util.StringUtil;
import com.sktlab.bizconfmobile.util.Util;

public class OrderConfActivity extends BaseActivity {

	public static final String TAG = "OrderConfActivity";

	private String SENT_SMS_ACTION = "SENT_SMS_ACTION";
	private String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";
	public static final Uri SMS_URI = Uri.parse("content://sms/");
	
	public final int DIALOG_TYPE_DATE = 0;
	public final int DIALOG_TYPE_TIME = 1;

	public final int REQUEST_CODE_ACCESS_NUMBER = 1001;
	public final int REQUEST_CODE_REPEAT_PERIOD = 1002;

	public final int TIME_START = 0;
	public final int TIME_END = 1;

	//5 minutes gap of end time and start time
	public final long GAP_5_MIN = 5*60*1000;
	// one hour gap time for start date and end date
	//public final long GAP_TIME = 3600000L;
	public final long GAP_TIME = GAP_5_MIN;
	// To specified the operate time is start time or end time
	public int mTimeType = -1;
	// repeat period of the meeting
	public int mFreq = RepeatSelectionActivity.PERIOD_NONE;

	private Activity mActivity;
	private LayoutInflater mInflater;

	private long mAccountId;

	private Date mDate = new Date();
	private Date mStartDate = new Date(mDate.getTime());
	private Date mEndDate = new Date(GAP_TIME + this.mDate.getTime());

	private TimeZone mTimeZone = TimeZone.getDefault();

	private EditText mEtTitleName;
	private EditText mEtConfDate;

	private TextView mTvStartTime;
	private TextView mTvEndTime;

	private LinearLayout mLayoutTimeSelector;
	private TextArrow confPeriodArrow;

	private LinearLayout mLayoutRepeatCount;
	//private TextArrow mAccessNumberArrow;
	private EditText mEtRepeatCount;

	//private SlipControlView mSecurityCode;
	private EditText etMeetingNotes;
	private TextArrow mInvitePartyArrow;

	private TextView mSperatorLine;

	private LinearLayout mSelectedPartLayout;
	private OnClickListener mSecurityCodeListener;
	private OnClickListener mOnSelectedPartyClickListener;
	private DatePickerDialog.OnDateSetListener mDateSetListener;
	private TimePickerDialog.OnTimeSetListener mTimeSetListener;
	private View.OnClickListener mOnDateClick;

	//whether the conference security code is valid when SecurityCodeEnable
	private boolean isSecurityCodeValid = false;
	
	private String emailTemplet = "";
	
	private OnContentChangeListener securityCodeChangeListener = new OnContentChangeListener() {
		@Override
		public void OnContentChangeListener(EditText inputContent) {

			isSecurityCodeValid = false;
		}
	};
	
	private OnFocusChangeListener securityCodeFocusChangeListener = new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			
			if (hasFocus) {
				
				Util.shortToast(mActivity, R.string.toast_security_4_number);				
			}
		}
	};
	
	public void onStartTimeClicked(View v) {

		mTimeType = TIME_START;
		OrderConfActivity.this.showDialog(DIALOG_TYPE_TIME);
	}

	public void onEndTimeClicked(View v) {

		mTimeType = TIME_END;
		OrderConfActivity.this.showDialog(DIALOG_TYPE_TIME);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_order_conf);

		mActivity = this;
		
		mAccountId = getIntent()
				.getLongExtra(Constant.KEY_OF_CONF_ACCOUNT_ID, -1);
		
		initView();
		setShowRightButton(true);
	}

	public void setDate(Date newDate) {

		mDate = newDate;
	}

	public void initView() {

		mInflater = (LayoutInflater) mActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		LinearLayout titleLayout = (LinearLayout) findViewById(R.id.etv_conf_title);
		TextView titleToast = (TextView) titleLayout
				.findViewById(R.id.tv_prompt);
		mEtTitleName = (EditText) titleLayout.findViewById(R.id.et_input);

		titleToast.setText(R.string.toast_order_conf_title);

		// date selector
		LinearLayout timeLayout = (LinearLayout) findViewById(R.id.etv_conf_time);
		TextView timeToast = (TextView) timeLayout.findViewById(R.id.tv_prompt);
		timeToast.setText(R.string.toast_order_conf_date);
		mEtConfDate = (EditText) timeLayout.findViewById(R.id.et_input);

		// time selector
		mLayoutTimeSelector = (LinearLayout) findViewById(R.id.layout_time_selector);
		mTvStartTime = (TextView) mLayoutTimeSelector
				.findViewById(R.id.tv_order_conf_start_time);
		mTvEndTime = (TextView) mLayoutTimeSelector
				.findViewById(R.id.tv_order_conf_end_time);

		// conference period
		confPeriodArrow = (TextArrow) findViewById(R.id.conf_period_arrow);

		// conference repeat count
		mLayoutRepeatCount = (LinearLayout) findViewById(R.id.layout_repeat_count);
		TextView tvCount = (TextView) mLayoutRepeatCount
				.findViewById(R.id.tv_left_toast);
		tvCount.setText(R.string.toast_order_conf_count);
		mEtRepeatCount = (EditText) mLayoutRepeatCount
				.findViewById(R.id.et_content);
		mEtRepeatCount.setInputType(InputType.TYPE_CLASS_NUMBER);

		// dial in number
		//mAccessNumberArrow = (TextArrow) findViewById(R.id.access_number_arrow);

		// This set for the edit text can not be editable
		mEtConfDate.setFocusable(false);
		mEtConfDate.setClickable(true);		

		//mSecurityCode = (SlipControlView) findViewById(R.id.security_module);
		etMeetingNotes = (EditText) findViewById(R.id.et_meeting_note);
		mInvitePartyArrow = (TextArrow) findViewById(R.id.invite_party_module);
		mSperatorLine = (TextView) findViewById(R.id.sperator_line);
		mSelectedPartLayout = (LinearLayout) findViewById(R.id.layout_selected_parties_module);

		do {

			if (Util.isEmpty(mDate)) {
				break;
			}

			if (!Util.isEmpty(mEtConfDate)) {
				updateDateView();
			}

			if (!Util.isEmpty(mTvStartTime)) {
				updateStartTimeView();
			}

			if (!Util.isEmpty(mTvEndTime)) {
				updateEndTimeView();
			}
		} while (false);

		initListener();
	}

	public void initListener() {

		mOnDateClick = new View.OnClickListener() {
			public void onClick(View paramView) {
				OrderConfActivity.this.showDialog(DIALOG_TYPE_DATE);
			}
		};

		mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

				GregorianCalendar localGregorianCalendar = new GregorianCalendar();
				localGregorianCalendar
						.setTimeZone(OrderConfActivity.this.mTimeZone);
				localGregorianCalendar.setTime(OrderConfActivity.this.mDate);
				localGregorianCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
				localGregorianCalendar.set(Calendar.MINUTE, minute);
				OrderConfActivity.this
						.setDate(localGregorianCalendar.getTime());

				switch (mTimeType) {

				case TIME_START:

					mStartDate.setTime(mDate.getTime());
					OrderConfActivity.this.updateStartTimeView();
				
					break;

				case TIME_END:
					mEndDate.setTime(mDate.getTime());
					OrderConfActivity.this.updateEndTimeView();
					break;

				default:
					OrderConfActivity.this.updateStartTimeView();
					break;
				}
			}
		};

		mDateSetListener = new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {

				GregorianCalendar localGregorianCalendar = new GregorianCalendar();
				localGregorianCalendar.set(Calendar.YEAR, year);
				localGregorianCalendar.set(Calendar.MONTH, monthOfYear);
				localGregorianCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

				mDate = localGregorianCalendar.getTime();
				
				mStartDate.setYear(mDate.getYear());
				mStartDate.setMonth(mDate.getMonth());
				mStartDate.setDate(mDate.getDate());
				
				mEndDate.setYear(mDate.getYear());
				mEndDate.setMonth(mDate.getMonth());
				mEndDate.setDate(mDate.getDate());
				
				OrderConfActivity.this.updateDateView();
			}
		};

		mOnSelectedPartyClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {

				// Util.BIZ_CONF_DEBUG(TAG, "onItemClicked called");

				Participant item = (Participant) v.getTag();

				if (!Util.isEmpty(item)) {

					int conatactId = item.getContactId();
					int selectedAttrPos = item
							.getSelectedAttrPosInContactItem();

					ContactManager cm = ContactManager.getInstance();
					
					if (item.getSelectedAttrPosInContactItem() != -1) {
						
						cm.removeSelectedContact(item);

						ContactItem contact = cm.getContactById(conatactId);
						contact.setAttrSelectedState(selectedAttrPos, false);
					}else {
						
						cm.removeInputParty(item);
					}
					
					showSelectedParties();
				}
			}
		};

		mSecurityCodeListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if (checkSecurityCode()) {
					
					v.setEnabled(false);
					Util.requestFocus(mTitle);
				}
			}
		};
		
		mInvitePartyArrow.setOnArrowClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent();
				intent.setClass(mActivity, AddParticipantActivity.class);
				intent.putExtra(AddParticipantActivity.KEY_OF_SHOW_EMAIL_ADDRESS, true);
				
				mActivity.startActivity(intent);				
			}
		});

//		mAccessNumberArrow.setOnArrowClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//
//				Util.startActivityForResult(mActivity,
//						AccessNumberActivity.class, REQUEST_CODE_ACCESS_NUMBER);
//			}
//		});

		mTvStartTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				onStartTimeClicked(v);
			}
		});

		mTvEndTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				onEndTimeClicked(v);
			}
		});

		confPeriodArrow.setOnArrowClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.setClass(mActivity, RepeatSelectionActivity.class);
				intent.putExtra(RepeatSelectionActivity.SELECTED_REPEAT_PERIOD,
						mFreq);

				mActivity.startActivityForResult(intent,
						REQUEST_CODE_REPEAT_PERIOD);
			}
		});
		
		mEtConfDate.setOnClickListener(mOnDateClick);
		
//		mSecurityCode.setBtClickListener(mSecurityCodeListener);
//		mSecurityCode.setContentChangeListener(securityCodeChangeListener);
//		mSecurityCode.setOnContentFocusChangeListener(securityCodeFocusChangeListener);
	}

	private boolean checkSecurityCode() {
		
		//String securityCode = mSecurityCode.getInputContent();

		do {

//			if (securityCode == null || securityCode.equalsIgnoreCase("")) {
//
//				//Util.shortToast(mCtx, R.string.toast_security_code_null);
//				Util.requestFocus(mSecurityCode.getInputView());
//				break;
//			}
			
//			if(securityCode.length() != 4) {
//				
//				//Util.shortToast(mCtx, R.string.toast_security_4_number);
//				Util.requestFocus(mSecurityCode.getInputView());
//				break;
//			}

			isSecurityCodeValid = true;
		} while (false);
		
		return isSecurityCodeValid;
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		do {

			if (Util.isEmpty(mLayoutRepeatCount)) {
				break;
			}

			if (mFreq != RepeatSelectionActivity.PERIOD_NONE) {

				mLayoutRepeatCount.setVisibility(View.VISIBLE);
				break;
			}

			mLayoutRepeatCount.setVisibility(View.GONE);

		} while (false);

		showSelectedParties();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// when order meeting finished, clear the selected participants stored
		// in ContactsManager;
		ContactManager.getInstance().clearSelectedParties();
	}

	public void showSelectedParties() {

		// This method give a realization of fixed width layout
		ArrayList<Participant> participants =
				ContactManager.getInstance().getAllSelectedContacts();		
		
		int partCount = participants.size();

		if (partCount > 0) {

			mSperatorLine.setVisibility(View.VISIBLE);
			mSelectedPartLayout.setVisibility(View.VISIBLE);
		} else {
			mSperatorLine.setVisibility(View.GONE);
			mSelectedPartLayout.setVisibility(View.GONE);
		}
		// remove all views added before
		mSelectedPartLayout.removeAllViews();

		// get the number of layout we should create
		int needInflateViewNum = partCount / Constant.NUM_PER_ROW + 1;

		// when the participant number is 0, 8, 12...we should minus 1,but for 1
		// or 2 or 7..we should not minus 1
		if (partCount % Constant.NUM_PER_ROW == 0) {

			needInflateViewNum = needInflateViewNum - 1;
		}

		// add the layout view we should show later
		for (int i = 0; i < needInflateViewNum; i++) {

			mSelectedPartLayout.addView(getParticipantListView());
		}

		// the row corresponding to the layout view and participant
		int row = 0;

		int pos = 0;

		for (int i = 0; i < partCount; i++, pos++) {

			Participant participant = participants.get(i);

			String showText = participant.getName();

			if (Util.isEmpty(showText)) {

				showText = participant.getPhone();
			}

			if (Util.isEmpty(showText)) {

				showText = participant.getEmail();
			}

			// get the row number of the participant
			row = pos / Constant.NUM_PER_ROW;

			// get the column index of the participant should show in the row
			int index = pos - Constant.NUM_PER_ROW * row;

			// if the show text too long, this participant should occupy one row
			if (StringUtil.getStrLengthInChar(showText) 
							> Constant.MAX_LENGTH_OF_SHOW_TEXT) {

				mSelectedPartLayout.addView(getParticipantListView());
				pos = pos + Constant.NUM_PER_ROW - 1;

				if (index > 0) {
					// when change row, the pos should add too
					row++;
					pos++;
				}
			}
			//Util.BIZ_CONF_DEBUG(TAG, "showText.length(): " + showText.length());
			//Util.BIZ_CONF_DEBUG(TAG, "pos: " + pos + " index: " + index + " row: " + row);

			LinearLayout item = (LinearLayout) mSelectedPartLayout
					.getChildAt(row);

			// show the specified participant
			TextView tvName = (TextView) item.getChildAt(index);
			tvName.setText(showText);
			tvName.setVisibility(View.VISIBLE);
			tvName.setOnClickListener(mOnSelectedPartyClickListener);

			// set the participant as tag of the text view, we will use it when
			// user click the textview
			tvName.setTag(participant);
		}
	}

	public LinearLayout getParticipantListView() {

		LinearLayout view = (LinearLayout) mInflater.inflate(
				R.layout.item_participant_list, null);

		return view;
	}
	
	public void sendSms(AppointmentConf meeting) {

		String[] receiver = ContactManager.getInstance().getSelectedPhones();
		
		//Util.BIZ_CONF_DEBUG(TAG, "selected phones: " + receiver.toString());
		
		if (receiver.length > 0) {
			
			Set<String>addr=new HashSet<String>();
	        
	         for(int i=0;i<receiver.length;i++){
	         	addr.add(receiver[i]); 	
	         }
	         
	         String body = generateSmsBody(meeting);
	         
	         long id=Threads.getOrCreateThreadId(this, addr);
	         
	         sendSMS(addr,body,id);
	         
		}
	}
	
	private String generateEmailBody(AppointmentConf appointConf, int bridgeId) {
		
		if (null == appointConf) {
			
			return "";
		}
		
		EmailContent emailContent = EmailContentFactory.createEmailContent(appointConf, bridgeId);
		
		return emailContent.generateEmailBody();
//		String shanghaiGlobalAccessNumber = "+86 21 6026 4000";
//		String beijingGlobalAccessNumberCHSpeaker = "8610 5629 4500";
//		String beijingGlobalAccessNumberENSpeaker = "8610 5629 4533";
//		
//		String shanghaiLocal400AccessNumCHSpeaker = "400 062 8686";
//		String shanghaiLocal400AccessNumENSpeaker = "400 001 1122";
//		
//		String beijingLocal400AccessNumCHSpeaker = "400 066 8787";
//		String beijingLocal400AccessNumENSpeaker = "400 096 1166";
//		
//		String shanghaiLocal800AccessNumCHSpeaker = "800 870 8686";
//		String shanghaiLocal800AccessNumENSpeaker = "800 870 1122";
//		
//		String beijingLocal800AccessNumCHSpeaker = "800 870 8787";
//		String beijingLocal800AccessNumENSpeaker = "800 870 1166";
//		
//		String shanghaiAccessNumListUrl = "http://online.bizconf.cn/accessNumber/1-1.htm";
//		
//		String beijingAccessNumListUrl = "http://online.bizconf.cn/accessNumber/2-2.htm";
//		
//		String globalAccessNumCH = beijingGlobalAccessNumberCHSpeaker 
//							+ getResources().getString(R.string.global_access_number_speaker_ch);
//		String globalAccessNumEN = beijingGlobalAccessNumberENSpeaker
//							+ getResources().getString(R.string.global_access_number_speaker_en);
//		
//		String local400AccessNumCH = beijingLocal400AccessNumCHSpeaker;
//		String local400AccessNumEN = beijingLocal400AccessNumENSpeaker;
//		String local800AccessNumCH = beijingLocal800AccessNumCHSpeaker;
//		String local800AccessNumEN = beijingLocal800AccessNumENSpeaker;
//		String accessNumListUrl = beijingAccessNumListUrl;
//		//需要加两个字符串提示
//		String suffixOfCommandToast = getResources().getString(R.string.beijing_toast_suffix_of_command);
//		String serviceCommand = getResources().getString(R.string.beijing_service_command);
//		
//		if (bridgeId == Constant.SHANG_HAI_BRIDGE) {
//			
//			globalAccessNumCH = shanghaiGlobalAccessNumber;
//			globalAccessNumEN = "";
//			local400AccessNumCH = shanghaiLocal400AccessNumCHSpeaker;
//			local400AccessNumEN = shanghaiLocal400AccessNumENSpeaker;
//			local800AccessNumCH = shanghaiLocal800AccessNumCHSpeaker;
//			local800AccessNumEN = shanghaiLocal800AccessNumENSpeaker;
//			accessNumListUrl = shanghaiAccessNumListUrl;
//			suffixOfCommandToast = getResources().getString(R.string.shanghai_toast_suffix_of_command);
//			serviceCommand = getResources().getString(R.string.shanghai_service_command);
//		}
//		
//		ConfAccount account = 
//					AccountsManager.getInstance().getAccountById(mAccountId);
//		
//		String confCode = "";
//		String securityCode = "";
//		
//		if (!Util.isEmpty(account)) {
//
//			confCode = account.getConfCode();
//			
//			if (account.isSecurityCodeEnable()) {
//				
//				securityCode = account.getSecurityCode();
//			}			
//		}				
//		
//		String confDate = DateUtil.getFormatString(
//							new Date(appointConf.getStartTime()), 
//							DateUtil.YY_MM_DD);
//		String time = DateUtil.getFormatString(
//					new Date(appointConf.getStartTime()), DateUtil.HH_MM_24)
//					+ "-"
//					+ DateUtil.getFormatString(
//					new Date(appointConf.getEndTime()), DateUtil.HH_MM_24);
//		String title = appointConf.getTitle();
//		
//		String inviteeName = account.getConfAccountName();
//		
//		String content = appointConf.getNote();
//		
//		String linkAddr = "http://bizconf.mobile.com/" + account.getConfCode();
//		
//		String iphoneAddr = "bizconf://mobile/" + account.getConfCode();
//		
//		String htmlUrl = 
//						"<HTML><HEAD>" +
//						"<META content=\"text/html; charset=gb2312\" http-equiv=Content-Type>" +
//						"<META name=GENERATOR content=\"MSHTML 9.00.8112.16496\"></HEAD>" +
//						"<BODY style=\"MARGIN: 10px\">" +
//						"<p>Android: </p>" +
//						"<DIV><A href=\"" + linkAddr + "\">" + linkAddr + "</A></DIV>" + 						
//						"<p>iPhone: </p>" +
//						"<DIV><A href=\"" + iphoneAddr + "\">" + iphoneAddr + "</A></DIV>" + 					 					 
//						"<DIV><FONT size=3 face=\"Times New Roman\"></FONT></DIV></BODY></HTML>";	
//		
//		String body = 
//				Util.replaceString(this, 
//						R.string.email_html_content, 
//						content,
//						inviteeName,
//						confDate,
//						time,	
//						title,
//						htmlUrl,
//						globalAccessNumCH,
//						globalAccessNumEN,
//						local400AccessNumCH,
//						local400AccessNumEN,
//						local800AccessNumCH,
//						local800AccessNumEN,
//						accessNumListUrl,
//						confCode,
//						suffixOfCommandToast,
//						securityCode,
//						serviceCommand
//						);
//		
//		return body;
	}
	
	private String generateSmsBody(AppointmentConf meeting) {
		
		if (null == meeting) {
			
			return "";
		}
		
		Participant currentUser = 
				ContactManager.getInstance().getCurrentUserObject();
		
		ConfAccount account = 
					AccountsManager.getInstance().getAccountById(mAccountId);
		
		String accountName = "";
		String accessNum = "";
		String confCode = "";
		String securityCode = "";
		String confDate = "";
		String time = "";
		String title = "";
		
		if (!Util.isEmpty(account)) {
			
			accountName = account.getConfAccountName();
			accessNum = account.getAccessNumber();
			confCode = account.getConfCode();
			securityCode = account.getSecurityCode();
			confDate = 
					DateUtil.getFormatString(
							new Date(meeting.getStartTime()), 
							DateUtil.YY_MM_DD);
			
			time = DateUtil.getFormatString(
						new Date(meeting.getStartTime()), DateUtil.HH_MM_24)
					+ "-"
					+ DateUtil.getFormatString(
						new Date(meeting.getEndTime()), DateUtil.HH_MM_24);
			
			title = meeting.getTitle();
		}
		
		String body = 
				Util.replaceString(this, 
						R.string.sms_content, 
						accountName,
						accessNum,
						confCode,
						securityCode,
						confDate,
						time,
						title
						);
		
		return body;
	}
	
	public void sendSMS(Set<String> phone, String body, long threadId) {
		
		SmsManager msg = SmsManager.getDefault();
		Intent send = new Intent(SENT_SMS_ACTION);
		// 短信发送广播
		PendingIntent sendPI = PendingIntent.getBroadcast(this, 0, send, 0);
		Intent delive = new Intent(DELIVERED_SMS_ACTION);
		// 发送结果广播
		PendingIntent deliverPI = PendingIntent
				.getBroadcast(this, 0, delive, 0);
		// 将数据插入数据库
		ContentValues cv = new ContentValues();
			
		for (String pno : phone) {
			
			if(body.length() <= 70) {
				
				msg.sendTextMessage(pno, null, body, sendPI, deliverPI);
				
				cv.put("thread_id", threadId);
				cv.put("date", System.currentTimeMillis());
				cv.put("body", body);
				cv.put("read", 0);
				cv.put("type", 2);
				cv.put("address", pno);
				this.getContentResolver().insert(SMS_URI, cv);
            }else{
                // SmsManger 类中 divideMessage 会将信息按每70 字分割
                List<String> smsDivs = msg.divideMessage(body);
                
                for(String sms : smsDivs) {
                	
                	msg.sendTextMessage(pno, null, sms, sendPI, deliverPI);    
                	
                	cv.put("thread_id", threadId);
        			cv.put("date", System.currentTimeMillis());
        			cv.put("body", sms);
        			cv.put("read", 0);
        			cv.put("type", 2);
        			cv.put("address", pno);
        			this.getContentResolver().insert(SMS_URI, cv);
                }
            }						
		}
	}
	
	public void sendEmail(AppointmentConf appointConf, int bridgeId) {
	
		//Util.BIZ_CONF_DEBUG(TAG, "emails: " + receiver);
		
		do{
			
			if (!Util.isNetworkAvailable(mActivity)) {
				
				Util.shortToast(mActivity, R.string.network_not_activated);
				break;
			}
			
			String[] receiver = ContactManager.getInstance().getSelectedEmails();
			
			if (receiver.length < 1) {
				
				break;
			}

			String content = generateEmailBody(appointConf, bridgeId);		
			
			Util.BIZ_CONF_DEBUG(TAG, "email content:" + content);			
			
			Intent intent = new Intent(Intent.ACTION_SEND);
			
			intent.putExtra(android.content.Intent.EXTRA_SUBJECT, appointConf.getTitle()); //subject
			intent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(content)); // content		
			intent.putExtra(android.content.Intent.EXTRA_EMAIL, receiver); // receiver		
			intent.setType("text/html");
			
			if (!Util.isEmpty(icsFile)) {
				
				intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(icsFile)); // 添加附件，附件为file对象
			}
			
			startActivity(Intent.createChooser(intent, 
						getResources().getString(
								R.string.chooser_title))); // 调用系统的mail客户端进行发送
		}while(false);
	}
	
	private File icsFile = null;
	
	@Override
	public void onRightButtonClicked(View v) {
		
		do {
			
			if (isTitleEmpty()) {
				
				break;
			}
			
			if (!isRepeatCountValid()) {
				
				Util.BIZ_CONF_DEBUG(TAG, "repeat count invalid");
				break;
			}
			
			if (isContentEmpty()) {
				
				Util.BIZ_CONF_DEBUG(TAG, "content invalid");
				break;
			}
			
			if (!isDateValid()) {
				
				Util.BIZ_CONF_DEBUG(TAG, "date invalid");
				break;
			}
			
			final AppointmentConf appointConf = generateMeeting();
			
			icsFile = generateICSFile(appointConf);
			
			if (null == icsFile) {
				
				Util.BIZ_CONF_DEBUG(TAG, "icsfile invalid");
				Util.shortToast(this, R.string.toast_storage_access_denied);
				break;
			}
					
			ExecutorService service = AppClass.getInstance().getService();
			
			final int repeatTimes = getRepeatCount(mEtRepeatCount.getText().toString());
			
			if (null != service) {
				
				service.submit(new Runnable() {
					
					@Override
					public void run() {
						
						ConfAccount account = 
								AccountsManager.getInstance().getAccountById(mAccountId);
						
						BridgeInfo bridgeInfo = new BridgeInfo(account.getConfCode());
						int bridgeId = bridgeInfo.getBridgeId();
						
						Util.BIZ_CONF_DEBUG(TAG, "bridgeId: " + bridgeId);
						
						String[] receiver = ContactManager.getInstance().getSelectedEmails();
						
						do {
							
							if (receiver.length > 0) {
								
								emailTemplet = requestEmailTemplet(account.getConfCode());
								
								Util.BIZ_CONF_DEBUG(TAG, "emailTemplet: " + emailTemplet);
								
								if ("".equals(emailTemplet)) {
									
									handler.sendEmptyMessage(Constant.ERR_REQUEST_BRIDGE_ID_TIME_OUT);
									break;
								} else if ("502".equals(emailTemplet) || "503".equals(emailTemplet)) {
									
									handler.sendEmptyMessage(Constant.ERR_NO_CONF_CODE_RECORD);
									break;
								}
							}
							
							handler.sendEmptyMessage(bridgeId);
							
							if (bridgeId > 0) {							
								
								sendSms(appointConf);							
								//sendEmail(icsFile, appointConf, bridgeId);							
								writeScheduleToCalendar(appointConf, repeatTimes);							
								
							}
							
						}while(false);																		
					}
				});
			}
								
			//Util.BIZ_CONF_DEBUG(TAG, "date description: " + meeting.getDate());	
			//finish();
		}while(false);
		
	}
	
	private String requestEmailTemplet(String confCode) {
		
		String language = AccessNumNetParser.getLanguage();
		
		String rsp = NetOp.requestEmailTemplet(confCode, language);
		
		//Util.BIZ_CONF_DEBUG(TAG, "email templet rsp:" + emailTemplet);
		String templet = "";
		
		try {
			
			JSONObject jsonObj = new JSONObject(rsp);	
			
			String status = jsonObj.getString("status");
			
			if ("success".equals(status)) {
				
				templet = jsonObj.getString("emailModel");				
				templet = templet.substring(templet.indexOf("<div"));
				
				templet = templet.replace("</body></html>", "")
						 	.replaceAll("<li>", "<p>")
						 	.replaceAll("</li>", "</p>");	
													
			}else if ("fail".equals(status)) {
				
				templet = jsonObj.getString("code");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return templet;
	}
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			
			case Constant.ERR_NO_CONF_CODE_RECORD:
				
				Util.shortToast(mActivity, R.string.no_email_templet);
				break;
			
			case Constant.ERR_REQUEST_BRIDGE_ID_TIME_OUT:
				
				Util.shortToast(mActivity, R.string.network_connection_failed);
				break;
			
			case Constant.SHANG_HAI_BRIDGE:
			case Constant.BEI_JING_BRIDGE:
				
				AppointmentConf appointConf = generateMeeting();
				
				appointConf.setEmailTemplet(emailTemplet);
				
				sendEmail(appointConf, msg.what);
				mActivity.finish();
				break;
			default:
				break;
			}			
		}
				
	};
	private File generateICSFile(AppointmentConf appointConf) {
		
		File icsFile = null;
		
		try {
			
			icsFile = FileUtil.createEvent(appointConf);					
		} catch (Exception e) {
			
			//Util.longToast(this, R.string.toast_no_email_client);		
			if (e.getMessage().toString().contains("Permission")) {
				
				Util.shortToast(this, R.string.toast_storage_access_denied);
			}
			e.printStackTrace();
		}
		
		return icsFile;
	}
	
	private boolean isDateValid() {
		
		boolean isValid = true;
		
		Date currentDate = new Date();			
		currentDate.setSeconds(0);
		
		do{
			
			if (mStartDate.compareTo(currentDate) < 0 ) {
				
				Util.shortToast(mActivity, R.string.toast_start_time_less_than_now);
				isValid = false;
				break;
			}
			
			//if (mEndDate.compareTo(mStartDate) <= 0) {
			if (mEndDate.getTime() - mStartDate.getTime() < GAP_5_MIN) {	
				Util.shortToast(mActivity, R.string.toast_end_time_less_than_start);
				isValid = false;
				break;
			}
			
		}while(false);
		
		return isValid;
	}
	
	private boolean isContentEmpty() {
		
		boolean isEmpty = false;
		
		String notes = etMeetingNotes.getText().toString();
		
		if (Util.isEmpty(notes)) {
			
			Util.shortToast(mActivity, R.string.toast_content_not_be_null);
			Util.requestFocus(etMeetingNotes);
			isEmpty = true;
		}
		
		return isEmpty;
	}
	
	private boolean isRepeatCountValid() {
		
		boolean isValid = false;
		
		if (!isRepeatCountEmpty()) {
			
			isValid = true;
		}		
		
		String repeatCount = mEtRepeatCount.getText().toString();
		
		int counts = getRepeatCount(repeatCount);
		
		if (counts > 100) {
			
			Util.shortToast(mActivity, R.string.toast_repeat_count_more_than_100);
			Util.requestFocus(mEtRepeatCount);	
			
			isValid = false;
		}
		
		return isValid;
	}
	
	private int getRepeatCount(String repeatCount) {
		
		int counts = 0;
		
		try {
			
			if (mFreq != RepeatSelectionActivity.PERIOD_NONE) {

				counts = Integer.valueOf(repeatCount);
			}
							
		} catch (Exception e) {
			
			e.printStackTrace();				
			//Util.BIZ_CONF_DEBUG(TAG, "repeat count format exception");
		}
		
		return counts;
	}
	
	private boolean isRepeatCountEmpty() {
		
		boolean isEmpty = false;
		
		String repeatCount = mEtRepeatCount.getText().toString();
		
		if ((mLayoutRepeatCount.getVisibility() == View.VISIBLE)
				&& Util.isEmpty(repeatCount)) {
			
			Util.shortToast(mActivity, R.string.toast_repeat_count_not_be_null);
			Util.requestFocus(mEtRepeatCount);	
			
			isEmpty = true;
		}
		
		return isEmpty;
	}
	
	private boolean isTitleEmpty(){
		
		boolean isEmpty = false;
		
		String title = mEtTitleName.getText().toString();
		
		if (Util.isEmpty(title)) {
			
			Util.shortToast(mActivity, R.string.toast_title_not_be_null);
			Util.requestFocus(mEtTitleName);
			isEmpty = true;
		}
		
		return isEmpty;
	}
	
	private void writeScheduleToCalendar(AppointmentConf sourceMeeting, int counts) {
		
		int i = 0;
		
		do {
			
			AppointmentConf meeting = new AppointmentConf(sourceMeeting);
			
			//remove repeat rule
			meeting.setFreq(RepeatSelectionActivity.PERIOD_NONE);
			
			if (i > 0){

				generateRepeatDate();
				
				meeting.setStartTime(mStartDate.getTime());		
				meeting.setEndTime(mEndDate.getTime());
				
				String date = CalendarUtil.getFomatDateStr(mStartDate.getTime());
				meeting.setDate(date);
				
				//Util.BIZ_CONF_DEBUG(TAG, "insert a repeat event, date: " + date);				
			}
			
			CalendarUtil.insertEvent(meeting);
			
			boolean result = AppointmentConfManager.getInstance().addMeeting(meeting);
			
			if (result) {
				
				AppointmentConfManager.getInstance().insertMeetingToDb(meeting);
			}
			
			i++;
			
			if (mFreq == RepeatSelectionActivity.PERIOD_NONE) {

				break;
			}
		} while (i < counts);
	}
	
	private AppointmentConf generateMeeting() {
	
		AppointmentConf meeting = new AppointmentConf();
		
		meeting.setAccountId(String.valueOf(mAccountId));
		meeting.setTitle(mEtTitleName.getText().toString());	
		//Util.BIZ_CONF_DEBUG(TAG, "start date str: " + str);
		
		mStartDate.setSeconds(0);
		mEndDate.setSeconds(0);
		
		meeting.setFreq(mFreq);
		meeting.setRepeatCount(mEtRepeatCount.getText().toString());
		meeting.setStartTime(mStartDate.getTime());		
		meeting.setEndTime(mEndDate.getTime());
		//meeting.setAccessNumber(mAccessNumberArrow.getCenterConentText());
		//meeting.setSecurityCode(mSecurityCode.getInputContent());
		meeting.setNote(etMeetingNotes.getText().toString());
		meeting.setDate(mEtConfDate.getText().toString());
		
		String[] emailReceiver =ContactManager.getInstance().getSelectedEmails();
		
		if (null != emailReceiver) {
			
			for(int i = 0; i < emailReceiver.length; i++) {
				
				meeting.addEmail(emailReceiver[i]);
			}
		}
		
		
		String[] phonesReceiver = ContactManager.getInstance().getSelectedPhones();
		
		if (null != phonesReceiver) {
			
			for(int i = 0; i < phonesReceiver.length; i++) {
				
				meeting.addPhone(phonesReceiver[i]);
			}
		}
		
		return meeting;
	}
	
	private void generateRepeatDate() {
		
		Calendar startDate = new GregorianCalendar();
		Calendar endDate = new GregorianCalendar();
		
		startDate.setTime(mStartDate);
		endDate.setTime(mEndDate);
				
		switch (mFreq) {

		case RepeatSelectionActivity.PERIOD_DAY:			
			startDate.add(Calendar.DAY_OF_YEAR, 1);
			endDate.add(Calendar.DAY_OF_YEAR, 1);
			break;
		case RepeatSelectionActivity.PERIOD_WEEK:
			startDate.add(Calendar.WEEK_OF_YEAR, 1);
			endDate.add(Calendar.WEEK_OF_YEAR, 1);
			break;
		case RepeatSelectionActivity.PERIOD_MONTH:
			startDate.add(Calendar.MONTH, 1);
			endDate.add(Calendar.MONTH, 1);
			break;
		case RepeatSelectionActivity.PERIOD_YEAR:
			startDate.add(Calendar.YEAR, 1);
			endDate.add(Calendar.YEAR, 1);
			break;
		}
		
		mStartDate.setTime(startDate.getTimeInMillis());
		mEndDate.setTime(endDate.getTimeInMillis());
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {

		case REQUEST_CODE_ACCESS_NUMBER:

			if (resultCode == RESULT_OK) {

				String accessNumber = data.getExtras().getString(
						AccessNumberActivity.KEY_ACCESS_NUMBER);

//				if (Util.isEmpty(accessNumber)) {
//
//					mAccessNumberArrow.setCenterContentText(this.getResources()
//							.getString(R.string.none));
//				} else {
//
//					mAccessNumberArrow.setCenterContentText(accessNumber);
//				}
			}
			break;

		case REQUEST_CODE_REPEAT_PERIOD:

			do {

				if (resultCode != RESULT_OK) {
					break;
				}

				if (Util.isEmpty(confPeriodArrow)) {
					break;
				}

				mFreq = data.getIntExtra(
						RepeatSelectionActivity.SELECTED_REPEAT_PERIOD,
						RepeatSelectionActivity.PERIOD_NONE);

				confPeriodArrow.setCenterContentText(RepeatSelectionActivity
						.getPeriodString(mFreq));

			} while (false);

			break;
		default:
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void setTitleName(String title) {
		super.setTitleName(title);
	}

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		GregorianCalendar localGregorianCalendar = new GregorianCalendar();
		localGregorianCalendar.setTime(this.mDate);
		localGregorianCalendar.setTimeZone(this.mTimeZone);
		switch (id) {
		default:
			return null;
		case DIALOG_TYPE_DATE:

			return new DatePickerDialog(this, this.mDateSetListener,
					localGregorianCalendar.get(Calendar.YEAR),
					localGregorianCalendar.get(Calendar.MONTH),
					localGregorianCalendar.get(Calendar.DAY_OF_MONTH));

		case DIALOG_TYPE_TIME:
			int day = localGregorianCalendar.get(Calendar.HOUR_OF_DAY);
			int minute = localGregorianCalendar.get(Calendar.MINUTE);

			return new TimePickerDialog(this, this.mTimeSetListener, day,
					minute, true);
		}
	}

	@Override
	@Deprecated
	protected void onPrepareDialog(int paramInt, Dialog paramDialog, Bundle args) {
		
		GregorianCalendar localGregorianCalendar = new GregorianCalendar();
		localGregorianCalendar.setTime(this.mDate);
		localGregorianCalendar.setTimeZone(this.mTimeZone);
		
		switch (paramInt) {
		
		default:
			return;
		case DIALOG_TYPE_DATE:
			((DatePickerDialog) paramDialog).updateDate(
					localGregorianCalendar.get(Calendar.YEAR),
					localGregorianCalendar.get(Calendar.MONTH),
					localGregorianCalendar.get(Calendar.DAY_OF_MONTH));
			return;
			
		case DIALOG_TYPE_TIME:
			int hour = localGregorianCalendar.get(Calendar.HOUR_OF_DAY);
			int minute = localGregorianCalendar.get(Calendar.MINUTE);
			((TimePickerDialog) paramDialog).updateTime(hour, minute);
			return;
		}
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
	
	private void updateDateView() {
//		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
//				this.mDateFormatStr);
//		localSimpleDateFormat.setTimeZone(this.mTimeZone);
//		String str = localSimpleDateFormat.format(this.mDate);
		
		String str = DateUtil.getFormatString(mDate, DateUtil.YY_MM_DD);
		
		this.mEtConfDate.setText(str);		
		//Util.BIZ_CONF_DEBUG(TAG, "date str: " + str);
	}

	private void updateStartTimeView() {

		String str = DateUtil.getFormatString(mStartDate, DateUtil.A_HH_MM);
		this.mTvStartTime.setText(str);
	}

	private void updateEndTimeView() {
		
		String str = DateUtil.getFormatString(mEndDate, DateUtil.A_HH_MM);
		this.mTvEndTime.setText(str);
	}
}
