package com.sktlab.bizconfmobile.model.db;

import android.content.ContentValues;

public class UnUsedDbAdapter extends ObjectDBAdapter
{
  public UnUsedDbAdapter(String paramString, String[] paramArrayOfString)
  {
    super(paramString, paramArrayOfString);
  }

  @Override
protected ContentValues createContentValues(Object paramObject)
  {
    ContentValues localContentValues = new ContentValues();

    return localContentValues;
  }
}
