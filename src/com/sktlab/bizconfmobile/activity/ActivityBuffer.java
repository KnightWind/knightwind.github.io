package com.sktlab.bizconfmobile.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

public class ActivityBuffer {

	List<Activity> activityList;
	
	Activity currentActiveActivity = null;
	
	public Activity getCurrentActiveActivity() {
		return currentActiveActivity;
	}
	
	public void setCurrentActiveActivity(Activity currentActiveActivity) {		
		this.currentActiveActivity = currentActiveActivity;
	}

	private static class abHolder{
		
		private static ActivityBuffer instance = new ActivityBuffer();
	}

	public static ActivityBuffer getInstance() {

		return abHolder.instance;
	}

	private ActivityBuffer() {
		
		activityList = new ArrayList<Activity>();
	}

	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	public void remove(Activity activity) {
		activityList.remove(activity);
	}

	public void clear() {
	
		for(Activity activity : activityList) {
			
			if (null != activity && !activity.isFinishing()) {
				
				activity.finish();
			}			
		}
		
		activityList.clear();
		
		System.gc();
	}

}
