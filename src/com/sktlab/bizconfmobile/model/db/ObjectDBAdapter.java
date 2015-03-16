package com.sktlab.bizconfmobile.model.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sktlab.bizconfmobile.activity.AppClass;

/**
 * 
 * @author wenjuan.li
 *
 */
public abstract class ObjectDBAdapter {
	
	private static final String TAG = "ObjectDBAdapter";
	
	protected String[] m_columns;
	protected SQLiteDatabase m_db;
	private GlobalDBHelper m_dbHelper;
	protected String m_dbTable;

	public ObjectDBAdapter(String paramString, String[] paramArrayOfString) {
		this.m_dbTable = paramString;
		this.m_columns = paramArrayOfString;
		this.m_dbHelper = new GlobalDBHelper(AppClass.getInstance());
	}

	public void close() {
		if ((this.m_db != null) && (this.m_db.isOpen())) {
			this.m_dbHelper.close();
			this.m_db = null;
		}
	}

	protected abstract ContentValues createContentValues(Object paramObject);

	public boolean deleteAllObjects() {
		if ((this.m_db != null) && (this.m_db.isOpen())) {
			int i = this.m_db.delete(this.m_dbTable, null, null);

			if (i > 0) {

				return true;
			}

		}
//		Log.w("ObjectDBAdapter",
//				"deleteAllObjects-database is null or not open!");
		return false;
	}

	public boolean deleteManyObjects(String paramString) {
		
		if ((this.m_db != null) && (this.m_db.isOpen())) {
			
			int i = this.m_db.delete(this.m_dbTable, paramString, null);

			if (i > 0){
				
				return true;
			}

		}
//		Log.w("ObjectDBAdapter",
//				"deleteManyObjects-database is null or not open!");
		return false;
	}

	public boolean deleteManyObjects(String paramString,
			String[] paramArrayOfString) {
		
		if ((this.m_db != null) && (this.m_db.isOpen())
				&& (paramString != null)) {
			
			int i = this.m_db.delete(this.m_dbTable, paramString,
					paramArrayOfString);
			
			if (i > 0){
				
				return true;
			}		
		}
//		Log.w("ObjectDBAdapter",
//				"deleteManyObjects-database is null or not open!");
		return false;
	}

	public boolean deleteObject(long paramLong) {
		if ((this.m_db != null) && (this.m_db.isOpen())) {
			
			int i = this.m_db.delete(this.m_dbTable, "_id=" + paramLong, null);

			if (i > 0){
				
				return true;
			}			
		}
//		Log.w("ObjectDBAdapter", "deleteObject-database is null or not open!");
		return false;
	}

	public Cursor fetchAllObjects(String selection, String order) {
		
		if ((this.m_db != null) && (this.m_db.isOpen())) {
			
			String[] arrayOfString = new String[1 + this.m_columns.length];
			
			System.arraycopy(this.m_columns, 0, arrayOfString, 0,this.m_columns.length);
			
			arrayOfString[(-1 + arrayOfString.length)] = "_id";
			
			return this.m_db.query(this.m_dbTable, arrayOfString, selection,null, null, null, order);
		}
		Log.w("ObjectDBAdapter","fetchAllObjects-database is null or not open!");
		return null;
	}

	public Cursor fetchObject(long paramLong) throws SQLException {
		
		if ((this.m_db != null) && (this.m_db.isOpen())) {
			
			String[] arrayOfString = new String[1 + this.m_columns.length];
			System.arraycopy(this.m_columns, 0, arrayOfString, 0,
					this.m_columns.length);
			
			arrayOfString[(-1 + arrayOfString.length)] = "_id";
			Cursor localCursor = this.m_db.query(true, this.m_dbTable,
					arrayOfString, "_id=" + paramLong, null, null, null, null,
					null);
			if (localCursor != null)
				localCursor.moveToFirst();
			return localCursor;
		}
//		Log.w("ObjectDBAdapter", "fetchObject-database is null or not open!");
		return null;
	}

	public long insertObject(Object paramObject) {
		
		ContentValues localContentValues = createContentValues(paramObject);
		
		if ((this.m_db != null) && (this.m_db.isOpen())){
			
			return this.m_db.insert(this.m_dbTable, null, localContentValues);
		}
			
//		Log.w("ObjectDBAdapter", "insertObject-database is null or not open!");
		return -1L;
	}

	public ObjectDBAdapter open() throws SQLException {
		
		try {
			
			if ((this.m_db == null) || (!this.m_db.isOpen())){
				
				this.m_db = this.m_dbHelper.getWritableDatabase();
			}
				
			return this;
			
		} catch (Exception localException) {
			this.m_db = null;
		}
		
		return this;
	}

	public ObjectDBAdapter read() throws SQLException {
		
		try {
			
			this.m_db = this.m_dbHelper.getReadableDatabase();
			return this;
		} catch (Exception localException) {
			this.m_db = null;
		}
		
		return this;
	}

	public boolean updateObject(long id, Object updateValue) {
		
		ContentValues localContentValues = createContentValues(updateValue);
		
		if ((this.m_db != null) && (this.m_db.isOpen())) {
			
			int i = this.m_db.update(this.m_dbTable, localContentValues, "_id="
					+ id, null);
			
			if (i > 0) {
				
				return true;
			}		
		}
		//Util.BIZ_CONF_DEBUG("ObjectDBAdapter", "updateObject-database is null or not open!");
		return false;
	}

	public boolean updateObjectProperties(long id, ContentValues values) {
		
		if ((this.m_db != null) && (this.m_db.isOpen())) {
			
			int i = this.m_db.update(this.m_dbTable, values, "_id=" + id, null);
	
			if (i > 0) {
				
				return true;
			}				
		}

		return false;
	}
}