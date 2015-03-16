package com.sktlab.bizconfmobile.model;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceOperator {
	
	private Context ctx;
	private SharedPreferences sp;
	
	public SharedPreferenceOperator(Context ctx) {
		
		this.ctx = ctx;
		init();
	}

	private void init() {
		
		sp = ctx.getSharedPreferences(
				Constant.SP_SIMPLE_DATA_STORE, Context.MODE_PRIVATE);
	}
	
	public void putBoolean(String key, boolean value){
		
		sp.edit().putBoolean(key, value).commit();
	}
	
	public void putString(String key, String value) {
		
		sp.edit().putString(key, value).commit();
	}
	
	public void putInt(String key, int value) {
		
		sp.edit().putInt(key, value).commit();
	}
	
	public boolean getBoolean(String key, boolean defaultValue) {
		
		return sp.getBoolean(key, defaultValue);
	}
	
	public String getString(String key, String defaultValue) {
		
		return sp.getString(key, defaultValue);
	}
	
	public int getInt(String key, int defaultValue) {
		
		return sp.getInt(key, defaultValue);
	}
	
	public boolean contains(String key) {
		
		return sp.contains(key);
	}
}
