package com.sktlab.bizconfmobile.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sktlab.bizconfmobile.R;

public class CustomImageButton extends LinearLayout {

	public CustomImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mButtonImage = new ImageView(context); 
	    mButtonText = new TextView(context); 
	    
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomImageButton);    
		CharSequence text = a.getText(R.styleable.CustomImageButton_android_text);    
		if(text != null) mButtonText.setText(text);    
		
		//float size = a.getFloat(R.styleable.CustomImageButton_android_textSize, 20);
		float size = a.getDimensionPixelSize(R.styleable.CustomImageButton_android_textSize, 20);
		mButtonText.setTextSize(size);
		Drawable drawable = a.getDrawable(R.styleable.CustomImageButton_android_src);    
		if(drawable != null) mButtonImage.setImageDrawable(drawable);    
		a.recycle(); 	
	 
	    setImgDrawable(drawable); 
	    mButtonImage.setPadding(0, 0, 0, 0); 
	 
	    setText(text); 
	    setTextColor(0xFF000000); 
	    mButtonText.setPadding(0, 0, 0, 0); 
	 
	    //设置本布局的属性 
	    setClickable(true);  //可点击 
	    setFocusable(true);  //可聚焦 
	    setBackgroundResource(android.R.color.transparent);  //布局才用普通按钮的背景 
	    setOrientation(LinearLayout.VERTICAL);  //垂直布局 
	     
	    //首先添加Image，然后才添加Text 
	    //添加顺序将会影响布局效果 
	    addView(mButtonImage); 
	    addView(mButtonText); 
	}

	// ----------------public method----------------------------- 
	  /* 
	   * setImageResource方法 
	   */ 
	  public void setImageResource(int resId) { 
	    mButtonImage.setImageResource(resId); 
	  } 
	 
	  public void setImgDrawable(Drawable src) {
		 
		  mButtonImage.setImageDrawable(src);
	  }
	  
	  /* 
	   * setText方法 
	   */ 
	  public void setText(int resId) { 
	    mButtonText.setText(resId); 
	  } 
	  
	  public void setText(CharSequence buttonText) { 
	    mButtonText.setText(buttonText); 
	  } 
	 
	  /* 
	   * setTextColor方法 
	   */ 
	  public void setTextColor(int color) { 
	    mButtonText.setTextColor(color); 
	  } 
	 
	  // ----------------private attribute----------------------------- 
	  private ImageView mButtonImage = null; 
	  private TextView mButtonText = null; 
}
