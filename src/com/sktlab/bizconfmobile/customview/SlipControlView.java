package com.sktlab.bizconfmobile.customview;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.activity.AppClass;
import com.sktlab.bizconfmobile.customview.SlipButton.OnSwitchListener;
import com.sktlab.bizconfmobile.util.Util;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SlipControlView extends LinearLayout {
	
	private LayoutInflater mInflater;
		
	private LinearLayout container;
	
	private LinearLayout toastModule;
	private LinearLayout inputModule;
	
	private SlipButton slipBt;	
	private EditText etInput;
	private TextView tvToast;
	private Button btConfirm;
	
	private OnContentChangeListener contentChangeListener;
	
	private int tmBgIdImShow;
	private int tmBgIdImHide;
	private int imBgId;
	
	private int toastStrId;
	
	private boolean sbState;
	
	public SlipControlView(Context context, AttributeSet attrs) {
		super(context, attrs);
			
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SlipControlView);
		
		int count = a.getIndexCount();
		
		//get the specified recourse id in xml file		
		for(int i = 0;i < count; i++) {
			
			int attr = a.getIndex(i);
			switch(attr) {
			
			case R.styleable.SlipControlView_tmBgImHide:
				tmBgIdImHide = a.getResourceId(attr, 0);
				break;
			case R.styleable.SlipControlView_tmBgImShow:
				tmBgIdImShow = a.getResourceId(attr, 0);
				break;
			case R.styleable.SlipControlView_inputModuleBg:
				imBgId = a.getResourceId(attr, 0);
				break;
				
			case R.styleable.SlipControlView_toastStr:
				toastStrId = a.getResourceId(attr, 0);
				break;
				
			case R.styleable.SlipControlView_isSlipOn:
				sbState = a.getBoolean(attr, false);
				break;
			}
		}
		a.recycle();
		
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		container = (LinearLayout) mInflater.inflate(R.layout.custom_slip_control_view, this);
		
		toastModule = (LinearLayout) container.findViewById(R.id.layout_toast);
		
		tvToast = (TextView) toastModule.findViewById(R.id.tv_toast);
		
		if (toastStrId != 0) {
			
			tvToast.setText(toastStrId);
		}
			
		slipBt = (SlipButton) toastModule.findViewById(R.id.sb_state);
		
		inputModule = (LinearLayout) container.findViewById(R.id.layout_verify);
		etInput = (EditText) inputModule.findViewById(R.id.et_input_content);
		btConfirm = (Button) inputModule.findViewById(R.id.bt_input_confirm);
		
		etInput.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
				btConfirm.setEnabled(true);
				
				if (null != contentChangeListener) {
					
					contentChangeListener.OnContentChangeListener(etInput);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		
		slipBt.setOnSwitchListener(new OnSwitchListener() {		
			@Override
			public void onSwitched(boolean isSwitchOn) {
				
				if(isSwitchOn) {
					toastModule.setBackgroundResource(tmBgIdImShow);
					inputModule.setVisibility(View.VISIBLE);
				} else {
					toastModule.setBackgroundResource(tmBgIdImHide);
					inputModule.setVisibility(View.GONE);
				}
			}
		});
		
		slipBt.setSwitchState(sbState);
		
		if (imBgId != 0) {
			
			inputModule.setBackgroundResource(imBgId);
		}	
	}
	
	public void setBtEnable(boolean enabled) {
		
		if (null != btConfirm) {
			
			btConfirm.setEnabled(enabled);
		}
	}
	
	public void setBtClickListener(OnClickListener btClickListener) {

		final OnClickListener listener = btClickListener;

		btConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				etInput.clearFocus();
				
				InputMethodManager imm = 
						(InputMethodManager)AppClass.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);  
				boolean isOpen=imm.isActive();
				
				if (isOpen) {
					
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);  
				}
				
				if (null != listener) {
					listener.onClick(v);
				}
			}
		});
	}
	
	public void setOnContentFocusChangeListener(OnFocusChangeListener listener) {
		
		if (null != listener) {
			
			etInput.setOnFocusChangeListener(listener);
		}
	}
	public void clear_Focus(){
		etInput.clearFocus();
	}
	public String getInputContent() {
		
		return etInput.getText().toString();
	}
	
	public void setInputContent(String content) {
		
		etInput.setText(content);
	}
	
	public boolean getSlipState() {
		
		return slipBt.getSwitchState();
	}
	
	public void setToastStr(int strId) {
		
		tvToast.setText(strId);
	}
	
	public void setToastStr(String str) {
		
		tvToast.setText(str);
	}
	
	public void setSlipState(boolean state) {
		
		slipBt.setSwitchState(state);
		//invalidate();
	}
	
	public EditText getInputView() {
		
		return etInput;
	}
	
	public void setContentChangeListener(OnContentChangeListener listener) {
		
		contentChangeListener = listener;
	}
	
	public void setInputModuleBg(int resId) {
		
		inputModule.setBackgroundResource(resId);
	}
	
	public void setToastModuleBg(int resId) {
		
		toastModule.setBackgroundResource(resId);
	}
	
	public void setToastModuleShowBg(int resId) {
		
		tmBgIdImShow = resId;
	}
	
	public void setToastModuleHideBg(int resId) {
		
		tmBgIdImHide = resId;
	}

	public interface OnContentChangeListener {
		
		public void OnContentChangeListener(EditText inputContent);
	}
}
