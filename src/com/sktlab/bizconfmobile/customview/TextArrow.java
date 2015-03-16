package com.sktlab.bizconfmobile.customview;

import com.sktlab.bizconfmobile.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TextArrow extends LinearLayout {
	
	private LayoutInflater mInflater;
	private RelativeLayout container;
	
	private TextView tvLeftToast;
	private TextView tvCenterContent;
	private ImageView ivArrow;
	
	//background resource id
	private int bgId;
	private int leftLabelStrId;
	private int centerLabelStrId;
	private int rightImgSrcId;
	
	public TextArrow(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextArrow);
		
		int count = a.getIndexCount();
		
		//get the specified recourse id in xml file		
		for(int i = 0;i < count; i++) {
			
			int attr = a.getIndex(i);
			switch(attr) {
			
			case R.styleable.TextArrow_background:
				bgId = a.getResourceId(attr, 0);
				break;
				
			case R.styleable.TextArrow_leftLabelText:
				leftLabelStrId = a.getResourceId(attr, 0);
				break;
				
			case R.styleable.TextArrow_centerLabelText:
				centerLabelStrId = a.getResourceId(attr, 0);
				break;
				
			case R.styleable.TextArrow_rightImgSrc:
				rightImgSrcId = a.getResourceId(attr, 0);
				break;		
			}
		}
		a.recycle();
		
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		container = (RelativeLayout) mInflater.inflate(R.layout.item_conf_period, null);
		
		tvLeftToast = (TextView) container.findViewById(R.id.tv_left_toast);
		tvCenterContent = (TextView) container.findViewById(R.id.tv_center_content);
		ivArrow = (ImageView) container.findViewById(R.id.iv_right_arrow);
		
		if (leftLabelStrId != 0) {
			
			tvLeftToast.setText(leftLabelStrId);
		}
		
		if (centerLabelStrId != 0) {
			
			tvCenterContent.setText(centerLabelStrId);
		}
		
		if (rightImgSrcId != 0) {
			
			ivArrow.setImageResource(rightImgSrcId);
		}				
		
		if (bgId != 0) {
			
			container.setBackgroundResource(bgId);
		} else {
			
			container.setBackgroundResource(android.R.color.transparent);
		}
		
		addView(container);
	}
	
	public void setOnArrowClickListener(OnClickListener listener){
		
		container.setOnClickListener(listener);
	}
	
	public void setCenterContentText(String content) {
		
		tvCenterContent.setText(content);
	}
	
	public String getCenterConentText() {
		
		return tvCenterContent.getText().toString();
	}
	
	public void setRightArrowLabel(int resId) {
		
		ivArrow.setImageResource(resId);
	}
}
