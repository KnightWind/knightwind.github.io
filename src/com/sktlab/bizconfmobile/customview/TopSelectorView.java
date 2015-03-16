package com.sktlab.bizconfmobile.customview;

import com.sktlab.bizconfmobile.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class TopSelectorView extends LinearLayout {

	private LayoutInflater mInflater;
	private LinearLayout container;

	private int leftSelectorBg;
	private int rightSelectorBg;
	private int normalTextColor;
	private int selectedTextColor;
	private int textSize;
	private int viewBg;
	private int leftText;
	private int rightText;
	
	private boolean isLeftSelected = true;
	private RadioGroup rbGroup;
	private RadioButton rbLeft;
	private RadioButton rbRight;
	
	private OnSelectorChangeListener listener = null;
	
	public interface OnSelectorChangeListener {
		
		public void onLeftSelected();
		public void onRightSelected();
	}
	
	public TopSelectorView(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.TopSelectorView);

		int count = a.getIndexCount();

		// get the specified recourse id in xml file
		for (int i = 0; i < count; i++) {

			int attr = a.getIndex(i);
			switch (attr) {

			case R.styleable.TopSelectorView_leftSelectorBg:
				leftSelectorBg = a.getResourceId(attr, 0);
				break;
			case R.styleable.TopSelectorView_rightSelectorBg:
				rightSelectorBg = a.getResourceId(attr, 0);
				break;
			case R.styleable.TopSelectorView_normalTextColor:
				normalTextColor = a.getColor(attr, 0);
				break;

			case R.styleable.TopSelectorView_selectedTextColor:
				selectedTextColor = a.getColor(attr, 0);
				break;

			case R.styleable.TopSelectorView_textSize:
				textSize = a.getDimensionPixelSize(attr, 0);
				break;

			case R.styleable.TopSelectorView_viewBg:
				viewBg = a.getResourceId(attr, 0);
				break;
				
			case R.styleable.TopSelectorView_leftSelectorText:
				leftText = a.getResourceId(attr, 0);
				break;
				
			case R.styleable.TopSelectorView_rightSelectorText:
				rightText = a.getResourceId(attr, 0);
				break;
				
			case R.styleable.TopSelectorView_isLeftChecked:
				isLeftSelected = a.getBoolean(attr, true);
				break;
			}
			
		}
		a.recycle();

		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		container = (LinearLayout) mInflater.inflate(R.layout.item_top_selector, null);
		
		rbGroup = (RadioGroup) container.findViewById(R.id.top_view_rg);
		rbLeft = (RadioButton) container.findViewById(R.id.selector_left);
		rbRight = (RadioButton) container.findViewById(R.id.selector_right);
		
		if (viewBg != 0) {
			
			container.setBackgroundResource(viewBg);
		}
		
		if (leftSelectorBg != 0) {
			
			rbLeft.setBackgroundResource(leftSelectorBg);
		}
		
		if (rightSelectorBg != 0) {
			
			rbRight.setBackgroundResource(rightSelectorBg);
		}
		
		if (leftText != 0) {
			
			rbLeft.setText(leftText);
		}
		
		if (rightText != 0) {
			
			rbRight.setText(rightText);
		}
		
		if (textSize != 0) {
			
			rbLeft.setTextSize(textSize);
			rbRight.setTextSize(textSize);
		}
		
		if (normalTextColor != 0) {
			
			rbLeft.setTextColor(normalTextColor);
			rbRight.setTextColor(normalTextColor);
		}
		
		if (isLeftSelected) {
			
			rbGroup.check(rbLeft.getId());
			
			if (selectedTextColor != 0) {
				
				rbLeft.setTextColor(selectedTextColor);
			}
		} else {
			
			rbGroup.check(rbRight.getId());
			
			if (selectedTextColor != 0) {
				
				rbRight.setTextColor(selectedTextColor);
			}
		}
		
		rbGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
								
				switch(checkedId) {
				
				case R.id.selector_left :
					
					if (selectedTextColor != 0) {
						
						rbLeft.setTextColor(selectedTextColor);
						rbRight.setTextColor(normalTextColor);						
					}			
					
					if (listener != null) {
						
						listener.onLeftSelected();
					}
					break;
					
				case R.id.selector_right :
					
					if (selectedTextColor != 0) {
						
						rbRight.setTextColor(selectedTextColor);
						rbLeft.setTextColor(normalTextColor);
					}
					
					if (listener != null) {
						
						listener.onRightSelected();
					}
					break;
				}
			}
		});

		addView(container);
		
		//rbLeft.setChecked(true);
	}
	
	public void setOnSelectedChangeListener(OnSelectorChangeListener listener) {
		
		this.listener = listener;
	}
}
