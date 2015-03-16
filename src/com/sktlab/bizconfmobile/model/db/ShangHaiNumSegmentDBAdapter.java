package com.sktlab.bizconfmobile.model.db;

import android.content.ContentValues;
import android.database.Cursor;
import com.sktlab.bizconfmobile.model.NumberSegment;

public class ShangHaiNumSegmentDBAdapter extends ObjectDBAdapter {
	
	public static final String TAG = "NumSegmentDBAdapter";
	
	public ShangHaiNumSegmentDBAdapter(String paramString, String[] paramArrayOfString) {
		
		super(paramString, paramArrayOfString);
	}

	@Override
	protected ContentValues createContentValues(Object segment) {
		
		ContentValues cv = new ContentValues();

		if ((segment instanceof NumberSegment)) {
			
			NumberSegment numSegment = (NumberSegment) segment;
			
			cv.put(ShangHaiNumSegmentDbTable.KEY_PREFIX,numSegment.getPreFix());
			
			cv.put(ShangHaiNumSegmentDbTable.KEY_START,numSegment.getStart());
			
			cv.put(ShangHaiNumSegmentDbTable.KEY_END,numSegment.getEnd());
		}

		return cv;
	}
	
	public Cursor getSegment(int preFix) {
		
		if ((this.m_db != null) && (this.m_db.isOpen())) {		
			
			String[] arrayOfString = new String[1 + this.m_columns.length];
			System.arraycopy(this.m_columns, 0, arrayOfString, 0,
					this.m_columns.length);
			
			arrayOfString[(-1 + arrayOfString.length)] = "_id";
			
			return this.m_db.query(this.m_dbTable, arrayOfString, 
					ShangHaiNumSegmentDbTable.KEY_PREFIX + "=" + preFix, null, null, null, null);
		}
		
		//Util.BIZ_CONF_DEBUG(TAG, "db can not open");
		
		return null;
	}
}