package com.sktlab.bizconfmobile.model.db;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class BillingReferenceDbTable
{
  public static final String BILLING_DB_TABLE = "billingref";
  public static final String KEY_ACCOUNTID = "accountid";
  public static final String KEY_FIRSTNAME = "firstname";
  public static final String KEY_LASTNAME = "lastname";
  public static final String KEY_REFCODE = "refcode";
  private static final String TABLE_CREATE = "create table billingref (_id integer primary key autoincrement, firstname text, lastname text, refcode text,accountid integer not null);";

  public static String[] getAllColumns()
  {
    return new String[] { "firstname", "lastname", "refcode", "accountid" };
  }

  public static void onCreate(SQLiteDatabase paramSQLiteDatabase)
  {
    try
    {
      paramSQLiteDatabase.execSQL("create table billingref (_id integer primary key autoincrement, firstname text, lastname text, refcode text,accountid integer not null);");
      return;
    }
    catch (SQLException localSQLException)
    {
    }
  }

  public static void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
  {
	  paramSQLiteDatabase
		.execSQL("DROP TABLE IF EXISTS " + BILLING_DB_TABLE);
	  onCreate(paramSQLiteDatabase);	
  }
}
