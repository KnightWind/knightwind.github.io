package com.sktlab.bizconfmobile.model.manager;

import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;

import android.database.Cursor;
import android.database.SQLException;

import com.sktlab.bizconfmobile.model.ConfHistory;
import com.sktlab.bizconfmobile.model.ConfAccount;
import com.sktlab.bizconfmobile.model.db.HistoryDbTable;
import com.sktlab.bizconfmobile.model.db.HistoryDBAdapter;
import com.sktlab.bizconfmobile.model.db.HistoryDbTable;
import com.sktlab.bizconfmobile.util.Util;

public class ConfHistoryManager {
	
	public static final String TAG = "ConfHistoryManager";
	
	//descend sort the history
	public static final int DESCEND_ORDER = 0;
	
	//ascend sort the history
	public static final int ASCEND_ORDER = 1;
	
	private TreeSet<ConfHistory> mHistory;

	private HistoryDBAdapter mHistoryDBAdapter;
	
	private static class holder {
		
		private static ConfHistoryManager instance 
						= new ConfHistoryManager();
	}
	
	private ConfHistoryManager() {
		
		mHistory = new TreeSet<ConfHistory>(new HistoryComparator(ASCEND_ORDER));
				
		mHistoryDBAdapter = new HistoryDBAdapter(
					HistoryDbTable.HISTORY_DB_TABLE,
					HistoryDbTable.getAllColumns());
	}
	
	public static ConfHistoryManager getInstance() {
		
		return holder.instance;
	}

	private boolean checkHistoryExistance(ConfHistory history) {
		
		boolean isExist = this.mHistory.contains(history);

		return isExist;
	}

	public long addHistory(ConfHistory confHistory) {
		
		long historyId = -1L;
		
		do {
			
			if(confHistory == null 
					|| checkHistoryExistance(confHistory)){
				
				break;
			}
			
			try {
				
				this.mHistoryDBAdapter.open();
				
				historyId = this.mHistoryDBAdapter.insertObject(confHistory);

				if (historyId != -1L) {
					
					confHistory.setHistoryId(historyId);
					
					this.mHistory.add(confHistory);
				}
				
			} catch (SQLException localSQLException) {
				
			} finally {
				
				this.mHistoryDBAdapter.close();
			}
			
		}while(false);
				
		return historyId;
	}

	public boolean clearHistory() {
		return true;
	}

	public TreeSet<ConfHistory> getHistory() {
		
		return this.mHistory;
	}

	public TreeSet<ConfHistory> getHistory(int type) {
		
		TreeSet<ConfHistory> tmp = null;
		
		if (type == ASCEND_ORDER) {
			
			tmp =  mHistory;
		}else {
			
			tmp = new TreeSet<ConfHistory>(new HistoryComparator(DESCEND_ORDER));
			tmp.addAll(mHistory);
		}
		
		return tmp;
	}
	
	public ConfHistory getHistoryById(long id) {
		
		Iterator localIterator = this.mHistory.iterator();
		
		ConfHistory localHistory;
		
		while (true) {
			
			boolean bool = localIterator.hasNext();
			
			localHistory = null;
			
			if (!bool)
				break;
			ConfHistory localConferenceHistory = (ConfHistory) localIterator
					.next();
			if (localConferenceHistory.getHistoryId() != id)
				continue;
			localHistory = localConferenceHistory;
		}
		return localHistory;
	}

	public void loadHistoryFromDb() throws SQLException {

		mHistory.clear();

		mHistoryDBAdapter.open();

		Cursor cursor = mHistoryDBAdapter.fetchAllObjects(null, null);

		while (cursor.moveToNext()) {

			int id = cursor
					.getInt(cursor.getColumnIndex(HistoryDbTable.KEY_ID));
			String accountId = cursor.getString(cursor
					.getColumnIndex(HistoryDbTable.KEY_ACCOUNT_ID));

			String accountName = cursor.getString(cursor
					.getColumnIndex(HistoryDbTable.KEY_ACCOUNT_NAME));

			String confCode = cursor.getString(cursor
					.getColumnIndex(HistoryDbTable.KEY_CONF_CODE));

			String title = cursor.getString(cursor
					.getColumnIndex(HistoryDbTable.KEY_CONF_TITLE));

			String endTime = cursor.getString(cursor
					.getColumnIndex(HistoryDbTable.KEY_END_TIME));

			String startTime = cursor.getString(cursor
					.getColumnIndex(HistoryDbTable.KEY_START_TIME));

			Date startDate = new Date(Long.valueOf(startTime));
			Date endDate = new Date(Long.valueOf(endTime));

			ConfHistory history = new ConfHistory();

			history.setHistoryId(id);
			history.setAccountID(accountId);
			history.setAccountName(accountName);
			history.setTitle(title);
			history.setConfCode(confCode);
			history.setStartDate(startDate);
			history.setEndDate(endDate);

			mHistory.add(history);
		};

		if (cursor != null) {

			cursor.close();
		}

		mHistoryDBAdapter.close();
	}

	public void updateDbRecord(ConfHistory history) {
		
		try {
			
			this.mHistoryDBAdapter.open();
			
			boolean isSuccess = this.mHistoryDBAdapter.updateObject(history.getHistoryId(), history);

			//Util.BIZ_CONF_DEBUG(TAG, "update history in db result: " + isSuccess);
			
		} catch (SQLException localSQLException) {
			
		} finally {
			
			this.mHistoryDBAdapter.close();
		}
	}
	
	/**
	 * maybe we should give a parameters to determine the sort method
	 * 
	 * @author
	 * 
	 */
	public class HistoryComparator implements Comparator<ConfHistory> {
		
		private int orderType;
		
		public HistoryComparator(int type) {
			
			orderType = type;
		}

		public int compare(ConfHistory history1,ConfHistory history2) {
			
			Date localDate1 = history1.getStartDate();
			Date localDate2 = history2.getStartDate();
			
			if (history1.equals(history2)) {
				
				return 0;
			}
			
			int result = localDate2.compareTo(localDate1);
			
			do{
				
				if (orderType == DESCEND_ORDER) {
					
					break;
				}
					
				result = -result;				
				
			}while(false);
			
			return result;
		}
	}
}
