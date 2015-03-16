package com.sktlab.bizconfmobile.customview;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.activity.AppClass;
import com.sktlab.bizconfmobile.util.Util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Cell {
	
	public static final String TAG = "Cell";
	
	protected Rect mBound = null;
	protected int mDayOfMonth = 1;	// from 1 to 31
	protected Paint mPaint = new Paint(Paint.SUBPIXEL_TEXT_FLAG |Paint.ANTI_ALIAS_FLAG);
	protected int dx, dy;
	//the cell background 
	protected Bitmap edageBg = null;
	//edage background rect
	protected Rect edage_src_Rect = null;	
	//check is this day is today
	protected boolean isToday = false;
	//is this cell was selected
	protected boolean isSelected = false;
	//when the cell was touched, show a selected background for this cell
	protected Bitmap touchBg;
	//the touch background rect
	protected Rect touch_bg_Rect;
	
	protected Bitmap markPic;
	protected Rect mark_Rect;
	
	protected boolean isRecord = false;

	//normal text color
	protected int normalTextColor = 0;
	//selected text color
	protected int selectedTextColor = 0;
	
	//the cell represent date
	//such as 2013-09-04
	protected String dateFomatStr;

	public Cell(int dayOfMon, Rect rect, float textSize,int edageBg,int touchBgId, boolean bold) {
		mDayOfMonth = dayOfMon;
		mBound = rect;
		mPaint.setTextSize(textSize/*26f*/);
		//mPaint.setColor(Color.BLACK);
		if(bold) mPaint.setFakeBoldText(true);
		
		dx = (int) mPaint.measureText(String.valueOf(mDayOfMonth)) / 2;
		dy = (int) (-mPaint.ascent() + mPaint.descent()) / 2;	
		
		setEdageBg(edageBg);	
		setTouchBg(touchBgId);
		setMarkPic(R.drawable.calendar_record_mark);
	}
	
	public Cell(int dayOfMon, Rect rect, float textSize, int edageBg,int touchBgId) {
		this(dayOfMon, rect, textSize, edageBg,touchBgId, true);
	}
	
	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		
		this.isSelected = isSelected;
		
		if (isSelected) {
			
			mPaint.setColor(selectedTextColor);
		}else {
			
			mPaint.setColor(normalTextColor);
		}	
	}

	public void setEdageBg(int resId) {
		
		edageBg = BitmapFactory.decodeResource(AppClass.getInstance().getResources(),resId);
		
		if (!Util.isEmpty(edageBg)) {
			
			edage_src_Rect = new Rect(0, 0, edageBg.getWidth(), edageBg.getHeight());
		}	
	}
	
	public void setMarkPic(int resId) {
		
		markPic = BitmapFactory.decodeResource(AppClass.getInstance().getResources(),resId);
		
		if (!Util.isEmpty(markPic)) {
			
			mark_Rect = new Rect(0, 0, markPic.getWidth(), markPic.getHeight());
		}
	}
	
	public void setTouchBg(int resId) {
		
		touchBg = BitmapFactory.decodeResource(AppClass.getInstance().getResources(),resId);
		
		if (!Util.isEmpty(touchBg)) {
			
			touch_bg_Rect = new Rect(0, 0, touchBg.getWidth(), touchBg.getHeight());
		}
	}
		
	public int getNormalTextColor() {
		return normalTextColor;
	}

	public void setNormalTextColor(int normalTextColor) {
		this.normalTextColor = normalTextColor;	
		
		mPaint.setColor(normalTextColor);
	}

	public int getSelectedTextColor() {
		return selectedTextColor;
	}

	public void setSelectedTextColor(int selectedTextColor) {
		this.selectedTextColor = selectedTextColor;
	}
	
	protected void draw(Canvas canvas) {
	
		if (!isSelected && !Util.isEmpty(edageBg) && !Util.isEmpty(edage_src_Rect)) {
			
			canvas.drawBitmap(edageBg, edage_src_Rect, mBound, mPaint);
		}		
		
		//draw touch background
		if(isSelected && !Util.isEmpty(touchBg) &&!Util.isEmpty(touch_bg_Rect)) {
			
			canvas.drawBitmap(touchBg, touch_bg_Rect, mBound, mPaint);
		}
		
		if (isToday){
			
			mPaint.setColor(selectedTextColor);
		}
		
		if(isRecord) {
			
			canvas.drawBitmap(markPic, mark_Rect, mBound, mPaint);
		}
				
		//Util.BIZ_CONF_DEBUG(TAG, "draw cell color: " + mPaint.getColor());
		
		canvas.drawText(String.valueOf(mDayOfMonth), mBound.centerX() - dx, mBound.centerY() + dy, mPaint);
	}
	
	public int getDayOfMonth() {
		return mDayOfMonth;
	}
	
	public boolean hitTest(int x, int y) {
		return mBound.contains(x, y); 
	}
	
	public Rect getBound() {
		return mBound;
	}
	
	public boolean isToday() {
		return isToday;
	}

	public void setToday(boolean isToday) {
		this.isToday = isToday;
	}
	
	public boolean isRecord() {
		return isRecord;
	}

	public void setRecord(boolean isRecord) {
		this.isRecord = isRecord;
	}

	public String getDateFomatStr() {
		return dateFomatStr;
	}

	public void setDateFomatStr(String dateFomatStr) {
		this.dateFomatStr = dateFomatStr;
	}

	public String toString() {
		return String.valueOf(mDayOfMonth)+"("+mBound.toString()+")";
	}	
}

