package com.sktlab.bizconfmobile.model.manager;

import java.util.ArrayList;

import android.database.Cursor;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.activity.AppClass;
import com.sktlab.bizconfmobile.model.ConfAccount;
import com.sktlab.bizconfmobile.model.db.AccountsDBAdapter;
import com.sktlab.bizconfmobile.model.db.AccountsDbTable;
import com.sktlab.bizconfmobile.util.Util;
/**
 * 
 * This is a help class to operate account database
 * 
 * @author wenjuan.li
 *
 */
public class AccountsManager{

	public static final String TAG = "AccountManager";

	private ArrayList<ConfAccount> mAccounts;
	private ArrayList<ConfAccount> mModeratorAccounts;
	private ArrayList<ConfAccount> mGuestAccounts;
	
	private AccountsDBAdapter mAccountDbAdapter;
	
	private AccountsManager() {
		
		mAccounts = new ArrayList<ConfAccount>();
		mModeratorAccounts = new ArrayList<ConfAccount>();
		mGuestAccounts = new ArrayList<ConfAccount>();
		
		mAccountDbAdapter = new AccountsDBAdapter(AccountsDbTable.ACCOUNTS_DB_TABLE, 
				AccountsDbTable.getAllColumns());
	}
	
	private static class InstanceHolder {

		private static AccountsManager instance = new AccountsManager();
	}
	
	public static AccountsManager getInstance() {

		return InstanceHolder.instance;
	}
	
	public ConfAccount getAccountById(Long id) {

		ConfAccount specificAccount = null;

		for (ConfAccount account : mAccounts) {

			if (account.getAccountId() == id) {

				specificAccount = account;
				break;
			}
		}

		return specificAccount;
	}

	public void insertAccountToDb(ArrayList<ConfAccount> accounts) {
				
		if (null != accounts) {
			
			mAccountDbAdapter.open();
			
			for (ConfAccount account : accounts) {
				
				long id = mAccountDbAdapter.insertObject(account);
				account.setAccountId(id);
			}
			
			mAccountDbAdapter.close();
		}		
	}
	
	public void insertAccountToDb(ConfAccount account) {

		if (null != account) {

			mAccountDbAdapter.open();

			long id = mAccountDbAdapter.insertObject(account);
			account.setAccountId(id);

			mAccountDbAdapter.close();
		}
	}
	
	public void updateAccountInDb(long accountId, ConfAccount account) {
		
		if (null != account) {
			
			mAccountDbAdapter.open();

			boolean result = mAccountDbAdapter.updateObject(accountId, account);
			
			//Util.BIZ_CONF_DEBUG(TAG, "update account info in db result: " + result);
			mAccountDbAdapter.close();
		}
	}
	
	public void deleteAccountInDb(long accountId) {
		
		mAccountDbAdapter.open();
		
		boolean rmResult = mAccountDbAdapter.deleteObject(accountId);
		
		if	(rmResult) {
			
			Util.shortToast(AppClass.getInstance(), R.string.toast_del_account_success);
		}
		
		mAccountDbAdapter.close();
	}
	/**
	 * 
	 * @param rmAccount
	 * @return index of the account be removed
	 */
	public int removeAccount(ConfAccount rmAccount) {

		boolean isSuccess = false;

		int indexOfRmObject = mAccounts.indexOf(rmAccount);

		isSuccess = mAccounts.remove(rmAccount);

		do{
			
			if (!isSuccess) {
				
				indexOfRmObject = -1;
				//Util.BIZ_CONF_DEBUG(TAG, "remove object failed~please check");
				break;
			}		
			
			if(mModeratorAccounts.contains(rmAccount)) {
				
				indexOfRmObject = mModeratorAccounts.indexOf(rmAccount);
				mModeratorAccounts.remove(rmAccount);
			}
			
			if(mGuestAccounts.contains(rmAccount)){
				
				indexOfRmObject = mGuestAccounts.indexOf(rmAccount);
				mGuestAccounts.remove(rmAccount);
			}
		}while(false);
		
		return indexOfRmObject;
	}

	public ArrayList<ConfAccount> getAccounts() {

		return mAccounts;
	}
	
	/**
	 * in my design this method is called in WeclomeActivity's loadData method,after called,
	 * the accounts has load all account stored in database
	 * @param data
	 */
	public void setAccounts(ArrayList<ConfAccount> data) {
		
		clearAllAccount();
		
		mAccounts = data;
		
		for(ConfAccount account : mAccounts) {
			
			if(!account.getModeratorPw().equalsIgnoreCase(AccountsDbTable.NORMAL_ACCOUNT_MODERATOR_PW)){
				
				mModeratorAccounts.add(account);
			}else {
				
				mGuestAccounts.add(account);
			}
		}
	}
	
	private void addAccountToSeparateType(int index,ConfAccount account) {
		
		if(!Util.isEmpty(account)) {
			
			if(account.getModeratorPw().equalsIgnoreCase(AccountsDbTable.NORMAL_ACCOUNT_MODERATOR_PW)) {
				
				if(index >= 0) {
					
					mGuestAccounts.add(index,account);
				}else {
					
					mGuestAccounts.add(account);
				}
				
			}else {
				
				if(index >= 0) {
					
					mModeratorAccounts.add(index,account);
				}else {
					
					mModeratorAccounts.add(account);
				}
			}
		}
	}
	
	/**
	 * add account at the specified position
	 * @param account
	 * @param index
	 */
	public void addAccount(ConfAccount account, int index) {
		
		try {
			
			if(!Util.isEmpty(account)){
				
				mAccounts.add(index, account);			
				addAccountToSeparateType(index,account);
			}
			
		} catch (IndexOutOfBoundsException e) {
			
			e.printStackTrace();
			//Util.BIZ_CONF_DEBUG(TAG, "index out of bound exception~");
		}	
	}
	
	/**
	 * add account at the end of arraylist
	 * @param account
	 * @return
	 */
	public boolean addAccount(ConfAccount account) {

		boolean isAddSuccess = false;

		if (!Util.isEmpty(account)) {

			isAddSuccess = mAccounts.add(account);
			addAccountToSeparateType(-1,account);
		}

		return isAddSuccess;
	}
	/**
	 * 
	 * add account in header of the arraylist
	 * @param account
	 * @return
	 */
	public boolean addAccountInHeader(ConfAccount account) {

		boolean isAddSuccess = false;

		if (!Util.isEmpty(account)) {

			mAccounts.add(0, account);
			addAccountToSeparateType(-1,account);
			isAddSuccess = true;
		}

		return isAddSuccess;
	}
	
	/**
	 * This method used to reset all variable to start state
	 */
	public void reset() {
		
		clearAllAccount();
	}
	/**
	 * clear all account
	 */
	public void clearAllAccount() {

		if (!Util.isEmpty(mAccounts)) {

			mAccounts.clear();
		}
		
		if(!Util.isEmpty(mModeratorAccounts)) {
			mModeratorAccounts.clear();
		}
		
		if(!Util.isEmpty(mGuestAccounts)) {
			
			mGuestAccounts.clear();
		}
	}

	public ArrayList<ConfAccount> getModeratorAccounts() {
			
		return mModeratorAccounts;
	}

	public void setModeratorAccounts(ArrayList<ConfAccount> mModeratorAccounts) {
		this.mModeratorAccounts = mModeratorAccounts;
	}

	public ArrayList<ConfAccount> getGuestAccounts() {
		
		return mGuestAccounts;
	}
	
	public ConfAccount getGuestAccountByConfCode(String confCode) {
		
		ConfAccount account = null;
		
		for (ConfAccount acc : mGuestAccounts) {
			
			if (acc.getConfCode().equalsIgnoreCase(confCode)) {
				
				account = acc;
				break;
			}
		}
		
		return account;
	}

	public void setGuestAccounts(ArrayList<ConfAccount> mGuestAccounts) {
		this.mGuestAccounts = mGuestAccounts;
	}
	
	public void loadAccountFromDb(){
		
		clearAllAccount();
		
		mAccountDbAdapter.open();
		
		Cursor cursor = mAccountDbAdapter.fetchAllObjects(null, null);   			   				
		
		while (cursor.moveToNext()) {

			int id = cursor.getInt(cursor
					.getColumnIndex(AccountsDbTable.KEY_ID));
			String accountName = cursor.getString(cursor
					.getColumnIndex(AccountsDbTable.KEY_ACCOUNT_NAME));
			String confCode = cursor
					.getString(cursor
							.getColumnIndex(AccountsDbTable.KEY_CONFERENCE_CODE));
			String dialInNumber = cursor.getString(cursor
					.getColumnIndex(AccountsDbTable.KEY_DIALINNUMBER));
			String dialOutNumber = cursor
					.getString(cursor
							.getColumnIndex(AccountsDbTable.KEY_DIAL_OUT_NUMBER));
			String moderatorPw = cursor.getString(cursor
					.getColumnIndex(AccountsDbTable.KEY_MODERATOR_PW));
			String securityCode = cursor.getString(cursor
					.getColumnIndex(AccountsDbTable.KEY_SECURITY_CODE));

			int dialOutEnable = cursor
					.getInt(cursor
							.getColumnIndex(AccountsDbTable.KEY_DIAL_OUT_ENABLE));
			int isActive = cursor.getInt(cursor
					.getColumnIndex(AccountsDbTable.KEY_ISACTIVE));
			int securityCodeEnable = cursor
					.getInt(cursor
							.getColumnIndex(AccountsDbTable.KEY_SECURITY_CODE_ENABLE));

			boolean isDialOutEnable = false;
			boolean isSecurityCodeEnable = false;
			boolean activeState = false;
			
			if (dialOutEnable == 1) {

				isDialOutEnable = true;
			}

			if (securityCodeEnable == 1) {

				isSecurityCodeEnable = true;
			}
			
			if (isActive == 1) {
				
				activeState = true;
			}
			
			ConfAccount account = new ConfAccount(accountName,
						dialInNumber, confCode, dialOutNumber,
						securityCode, isDialOutEnable,
						isSecurityCodeEnable, moderatorPw);
			
			account.setAccountId(id);
			account.setUseDefaultAccessNum(activeState);
			
			//Util.BIZ_CONF_DEBUG(TAG, "load account name" + account.getConfAccountName());
			
			addAccount(account);
		};
		
		if (cursor != null) {
			
			cursor.close();
		}
		
		mAccountDbAdapter.close();	
		//Util.BIZ_CONF_DEBUG(TAG, "load db account size: " + mAccounts.size());
	}
	
	public boolean isMemoryRecyled() {
		
		boolean isRecyled = false;
		
		mAccountDbAdapter.open();
		
		Cursor cursor = mAccountDbAdapter.fetchAllObjects(null, null); 
		
		int dbAccountSize = cursor.getCount();
		
		int accountSize = mAccounts.size();
		
		//Util.BIZ_CONF_DEBUG(TAG, "accountSize: " + accountSize + "dbAccountSize: " + dbAccountSize);
		if(accountSize == 0 && dbAccountSize > 0){
			
			isRecyled = true;
		}
		
		mAccountDbAdapter.close();
		
		return isRecyled;
	}
}
