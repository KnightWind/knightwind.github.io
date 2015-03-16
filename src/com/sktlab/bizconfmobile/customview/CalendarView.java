package com.sktlab.bizconfmobile.customview;

import java.util.Calendar;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.activity.AppClass;
import com.sktlab.bizconfmobile.model.manager.AppointmentConfManager;
import com.sktlab.bizconfmobile.util.CalendarUtil;
import com.sktlab.bizconfmobile.util.Util;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.MonthDisplayHelper;
import android.view.MotionEvent;
import android.widget.ImageView;

public class CalendarView extends ImageView {

	public static final String TAG = "CalendarView";

	public final int CALENDAR_ROW = 6;
	public final int CALENDAR_COLUMN = 7;

	private int CELL_MARGIN_TOP = 0;
	private int CELL_MARGIN_LEFT = 0;

	private Calendar mRightNow = null;
	private Cell mToday = null;
	private Cell[][] mCells = new Cell[CALENDAR_ROW][CALENDAR_COLUMN];
	private OnCellTouchListener mOnCellTouchListener = null;
	private MonthDisplayHelper mHelper;

	// calendar background
	private int bgId;
	private int cellWidth;
	private int cellHeight;
	private int cellTextSize;
	private int calendarHeight;
	
	private int cellEdgeBg;
	private int cellTodayBg;
	private int cellTouchBg;
	private int cellTodayTouchBg;
	
	private int cellNormalTextColor;
	private int cellSelectedTextColor;
	
	private int displayYear;
	private int displayMonth;

	public interface OnCellTouchListener {
		public void onTouch(Cell cell);
	}

	public CalendarView(Context context) {
		this(context, null);
	}

	public CalendarView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CalendarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.CalendarView);

		int count = a.getIndexCount();

		// get the specified recourse id in xml file
		for (int i = 0; i < count; i++) {

			int attr = a.getIndex(i);
			switch (attr) {

			case R.styleable.CalendarView_bgId:
				bgId = a.getResourceId(attr, 0);
				break;

			case R.styleable.CalendarView_cellHeight:
				cellHeight = a.getDimensionPixelSize(attr, 0);
				break;

			case R.styleable.CalendarView_cellWidth:
				cellWidth = a.getDimensionPixelSize(attr, 0);
				break;

			case R.styleable.CalendarView_textsize:
				cellTextSize = a.getDimensionPixelSize(attr, 0);
				break;

			case R.styleable.CalendarView_calendarHeight:
				calendarHeight = a.getDimensionPixelSize(attr, 0);
				break;
				
			case R.styleable.CalendarView_cellEdgeBg:
				cellEdgeBg = a.getResourceId(attr, 0);
				break;
				
			case R.styleable.CalendarView_cellTodayBg:
				cellTodayBg = a.getResourceId(attr, 0);
				break;
				
			case R.styleable.CalendarView_cellTodayTouchBg:
				cellTodayTouchBg = a.getResourceId(attr, 0);
				break;
				
			case R.styleable.CalendarView_cellTouchBg:
				cellTouchBg = a.getResourceId(attr, 0);
				break;				
				
			case R.styleable.CalendarView_cellNormalTextColor:
				cellNormalTextColor = a.getColor(attr, 0);
				break;	
				
			case R.styleable.CalendarView_cellSelectedTextColor:
				cellSelectedTextColor = a.getColor(attr, 0);
				break;	
			}
						
		}
		a.recycle();

		initCalendarView();
	}

	private void initCalendarView() {
		
		mRightNow = Calendar.getInstance();

		Resources res = getResources();

		// set background
		if (bgId != 0) {

			setBackgroundResource(bgId);
		} else {

			setBackgroundResource(R.drawable.calendar_bg);
		}
		
		if (cellEdgeBg == 0) {
			
			cellEdgeBg = R.drawable.calendar_cell_background;
		}
		
		if (cellTouchBg == 0) {
			
			cellTouchBg = R.drawable.calendar_cell_touch;
		}
		
		if (cellTodayBg == 0) {
			
			cellTodayBg = R.drawable.calendar_today;
		}
		
		if (cellTodayTouchBg == 0) {
			
			cellTodayTouchBg = R.drawable.calendar_today_touch;
		}
		
		if (cellWidth == 0) {

			cellWidth = Util.getScreenSize().widthPixels / CALENDAR_COLUMN;
		}

		if (cellHeight == 0) {

			cellHeight = calendarHeight / CALENDAR_ROW;
		}

		mHelper = new MonthDisplayHelper(mRightNow.get(Calendar.YEAR),
				mRightNow.get(Calendar.MONTH));

		displayYear = mHelper.getYear();
		displayMonth = mHelper.getMonth();
	}

	private class _calendar {
		public int day;
		public boolean thisMonth;

		public _calendar(int d, boolean b) {
			day = d;
			thisMonth = b;
		}

		public _calendar(int d) {
			this(d, false);
		}
	};
	private void initCells() {
		
		_calendar tmp[][] = new _calendar[CALENDAR_ROW][CALENDAR_COLUMN];

		for (int i = 0; i < tmp.length; i++) {
			int n[] = mHelper.getDigitsForRow(i);
			for (int d = 0; d < n.length; d++) {
				if (mHelper.isWithinCurrentMonth(i, d))
					tmp[i][d] = new _calendar(n[d], true);
				else
					tmp[i][d] = new _calendar(n[d]);

			}
		}

		Calendar today = Calendar.getInstance();
		int thisDay = 0;
		mToday = null;
		if (mHelper.getYear() == today.get(Calendar.YEAR)
				&& mHelper.getMonth() == today.get(Calendar.MONTH)) {
			thisDay = today.get(Calendar.DAY_OF_MONTH);
		}
		// build cells
		Rect Bound = new Rect(CELL_MARGIN_LEFT, CELL_MARGIN_TOP, cellWidth
				+ CELL_MARGIN_LEFT, cellHeight + CELL_MARGIN_TOP);
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(Calendar.YEAR, mHelper.getYear());
		calendar.set(Calendar.MONTH, mHelper.getMonth());
		
		for (int week = 0; week < mCells.length; week++) {
			for (int day = 0; day < mCells[week].length; day++) {
				
				if (tmp[week][day].thisMonth) {
					
					calendar.set(Calendar.DAY_OF_MONTH, tmp[week][day].day);
					
					//boolean hasRecord = CalendarManager.getInstance().mapDayToConf(calendar.getTimeInMillis());
					boolean hasRecord = AppointmentConfManager.getInstance().hasRecord(calendar.getTimeInMillis());
					
					String dateFomat = CalendarUtil.getFomatDateStr(calendar.getTimeInMillis());
					
					mCells[week][day] = 
								new Cell(tmp[week][day].day, 
											new Rect(Bound), cellTextSize, cellEdgeBg, cellTouchBg);		
					
					mCells[week][day].setNormalTextColor(cellNormalTextColor);
					mCells[week][day].setSelectedTextColor(cellSelectedTextColor);
					mCells[week][day].setRecord(hasRecord);
					mCells[week][day].setDateFomatStr(dateFomat);
					
				} else {
					
					mCells[week][day] = new GrayCell(tmp[week][day].day,
											new Rect(Bound), cellTextSize, cellEdgeBg, cellEdgeBg);
				}
				
				// move to next column
				Bound.offset(cellWidth, 0); 

				// get today
				if (tmp[week][day].day == thisDay && tmp[week][day].thisMonth) {
					
					mToday = mCells[week][day];
					
					mToday.setToday(true);
					mToday.setEdageBg(cellTodayBg);
					mToday.setTouchBg(cellTodayTouchBg);
					//mDecoration.setBounds(mToday.getBound());
				}
			}
			Bound.offset(0, cellHeight); // move to next row and first column
			Bound.left = CELL_MARGIN_LEFT;
			Bound.right = CELL_MARGIN_LEFT + cellWidth;
		}
	}

	@Override
	public void onLayout(boolean changed, int left, int top, int right,
			int bottom) {

		android.util.Log.d(TAG, "left=" + left);
		
		initCells();
		super.onLayout(changed, left, top, right, bottom);
	}

	public void setTimeInMillis(long milliseconds) {

		mRightNow.setTimeInMillis(milliseconds);
		initCells();
		this.invalidate();
	}

	public int getYear() {
		return mHelper.getYear();
	}

	public int getMonth() {
		return mHelper.getMonth();
	}

	public void nextMonth() {

		mHelper.nextMonth();
		displayYear = mHelper.getYear();
		displayMonth = mHelper.getMonth();

		initCells();
		invalidate();
		// Util.BIZ_CONF_DEBUG(TAG, "nextMonth: " + mHelper.getMonth());
	}

	public void previousMonth() {

		mHelper.previousMonth();

		displayYear = mHelper.getYear();
		displayMonth = mHelper.getMonth();
		initCells();
		invalidate();
	}
	
	public Cell getTodayCell() {
		
		return mToday;
	}
	
	public boolean firstDay(int day) {

		return day == 1;
	}

	public boolean lastDay(int day) {

		return mHelper.getNumberOfDaysInMonth() == day;
	}

	public void goToday() {

		Calendar cal = Calendar.getInstance();
		mHelper = new MonthDisplayHelper(cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH));
		initCells();
		invalidate();
	}

	public String getDisplayDateStr() {

		return Util.replaceString(getContext(), R.string.toast_top_date,
				displayYear, displayMonth + 1);
	}

	public Calendar getCurrentDate() {

		return mRightNow;
	}
	
	public void clearSelected() {

		for (Cell[] week : mCells) {
			for (Cell day : week) {

				day.setSelected(false);
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		boolean isBreak = false;
		
		for (Cell[] week : mCells) {
			
			if (isBreak) {
				
				break;
			}
			
			for (Cell day : week) {
				
				if (day.hitTest((int) event.getX(), (int) event.getY())) {
					
					if (day instanceof GrayCell) {
						
						isBreak = true;
						break;
					}
					
					if (mOnCellTouchListener != null) {
						
						mOnCellTouchListener.onTouch(day);
					}					
					
					clearSelected();
					
					day.setSelected(true);
					
					invalidate();
				}
			}
		}				
		
		return super.onTouchEvent(event);
	}

	public void setOnCellTouchListener(OnCellTouchListener p) {

		mOnCellTouchListener = p;
	}

	@Override
	protected void onDraw(Canvas canvas) {

		// draw background
		super.onDraw(canvas);
		
		// draw cells
		for (Cell[] week : mCells) {
			for (Cell day : week) {
				
				//Util.BIZ_CONF_DEBUG(TAG, "day index: " + day.mDayOfMonth);			
				day.draw(canvas);
			}
		}
		
	}

	private class GrayCell extends Cell {

		public GrayCell(int dayOfMon, Rect rect, float s, int edageBg, int touchBg) {
			
			super(dayOfMon, rect, s, edageBg, touchBg);
			
			//Util.BIZ_CONF_DEBUG(TAG, "gray cell dayOfMon: " + dayOfMon);					
			
			mPaint.setColor(Color.GRAY);
		}
		
		//must have this empty method, use this method to hide parent's realization
		@Override
		public void setSelected(boolean isSelected) {
			
			
		}		
		
	}
}
