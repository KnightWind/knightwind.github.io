package com.sktlab.bizconfmobile.model.db;

import android.content.ContentValues;

import com.sktlab.bizconfmobile.model.ConfHistory;

public class HistoryDBAdapter extends ObjectDBAdapter {
	
	public HistoryDBAdapter(String paramString, String[] paramArrayOfString) {
		
		super(paramString, paramArrayOfString);
	}

	@Override
	protected ContentValues createContentValues(Object history) {
		
		ContentValues localContentValues = new ContentValues();

		if ((history instanceof ConfHistory)) {
			
			ConfHistory localConferenceHistory = (ConfHistory) history;
			
			localContentValues.put(HistoryDbTable.KEY_ACCOUNT_ID,
					localConferenceHistory.getAccountID());
			
			localContentValues.put(HistoryDbTable.KEY_ACCOUNT_NAME,
					localConferenceHistory.getAccountName());
			
			localContentValues.put(HistoryDbTable.KEY_CONF_TITLE,
					localConferenceHistory.getTitle());
			
			localContentValues.put(HistoryDbTable.KEY_CONF_CODE,
					localConferenceHistory.getConfCode());
			
			localContentValues.put(HistoryDbTable.KEY_START_TIME, String
					.valueOf(localConferenceHistory.getStartDate().getTime()));
			
			localContentValues
					.put(HistoryDbTable.KEY_END_TIME, String
							.valueOf(localConferenceHistory.getEndDate()
									.getTime()));
		}

		return localContentValues;
	}
}