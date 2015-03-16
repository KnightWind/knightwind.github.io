package com.sktlab.bizconfmobile.model.db;

import android.content.ContentValues;

import com.sktlab.bizconfmobile.model.ConfAccount;

public class AccountsDBAdapter extends ObjectDBAdapter {
	
	public static final String TAG = "AccountsDBAdapter";
	
	public AccountsDBAdapter(String paramString, String[] paramArrayOfString) {
		
		super(paramString, paramArrayOfString);
	}

	@Override
	protected ContentValues createContentValues(Object paramObject) {
		
		ContentValues localContentValues = new ContentValues();
		
		if ((paramObject instanceof ConfAccount)) {
			ConfAccount localConfAccount = (ConfAccount) paramObject;
			
			localContentValues.put(AccountsDbTable.KEY_ACCOUNT_NAME, localConfAccount.getConfAccountName());
			localContentValues.put(AccountsDbTable.KEY_CONFERENCE_CODE, localConfAccount.getConfCode());
			localContentValues.put(AccountsDbTable.KEY_DIALINNUMBER, localConfAccount.getAccessNumber());
			localContentValues.put(AccountsDbTable.KEY_MODERATOR_PW,localConfAccount.getModeratorPw());
			
			int isActive = 0;
			
			if (localConfAccount.isUseDefaultAccessNum()) {
				
				isActive = 1;
			}
			
			localContentValues.put(AccountsDbTable.KEY_ISACTIVE, isActive);
			
			int isDialOutEnable = 0;
			
			if (localConfAccount.isDialOutEnable()) {
				
				isDialOutEnable = 1;
			}
			
			localContentValues.put(AccountsDbTable.KEY_DIAL_OUT_ENABLE, isDialOutEnable);
			
			//Util.BIZ_CONF_DEBUG(TAG, "insert or update dialoutNumber:" + localConfAccount.getDialOutNumber());
			
			localContentValues.put(AccountsDbTable.KEY_DIAL_OUT_NUMBER, localConfAccount.getDialOutNumber());
			
			int isSecurityCodeEnable = 0;
			
			if (localConfAccount.isSecurityCodeEnable()) {
				
				isSecurityCodeEnable = 1;
			}		
			
			localContentValues.put(AccountsDbTable.KEY_SECURITY_CODE_ENABLE, isSecurityCodeEnable);
			localContentValues.put(AccountsDbTable.KEY_SECURITY_CODE, localConfAccount.getSecurityCode());			
		}
		
		return localContentValues;
	}
}
