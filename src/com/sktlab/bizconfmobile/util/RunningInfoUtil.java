package com.sktlab.bizconfmobile.util;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * 
 * @author wenjuan.li
 * 
 * 这个类主要用来获取当前手机运行的程序信息，是一个工具类。
 *
 */
public class RunningInfoUtil {
	
	public static final String TAG = "RunningInfoUtil";
	//读取最近运行的6个任务
	public static final int MAX_RECENT_TASKS = 8;
	
	/**
	 * 通过获取栈中即时的运行task信息，获得对应应用的包名
	 * @return
	 * 		List<String> 当前栈中，即时运行的task的包名
	 */
	public static List<String> getRunningAppsPackageNames(Context ctx){
		List<ActivityInfo> infoList = getCurrentRunningApps(ctx);
		List<String> packageNames = new ArrayList<String>();
		
		for( ActivityInfo info:infoList ){
			packageNames.add(info.packageName);
			//Util.GOTHEMEDEBUG("running app info pkgName = " + info.packageName);
			//Util.DEBUG("name = " + info.name);
		}
		
		return packageNames;
	}
	
	public static void startSpecifiedApp(Activity activity, String packageName) {
		
		String pName = packageName;
		
		List<ActivityInfo> infoList = getCurrentRunningApps(activity);
		
		String tmpPackageName = null;
		String tmpClassName = null;
		
		for( ActivityInfo info:infoList ){

			if (info.applicationInfo.packageName.contains(pName)) {
				
				tmpPackageName = info.applicationInfo.packageName;
				tmpClassName = info.name;
				
				break;
			}
			
//			Util.BIZ_CONF_DEBUG(TAG, "packageName:" + info.applicationInfo.packageName + 
//									  "className: " + info.name);
		}

		do{
			
			if ( Util.isEmpty(tmpClassName) || Util.isEmpty(tmpPackageName)) {
				
				//Util.BIZ_CONF_DEBUG(TAG, "sorry, running app's class name or package name is null, give up start it");
				break;
			}
			
			Intent intent = new Intent();
			ComponentName cn = new ComponentName(tmpPackageName, tmpClassName);
			intent.setComponent(cn);

			activity.startActivity(intent);
			
		}while(false);
		
	}
	
	public static ActivityInfo getCurrentRunningLauncher(Context ctx) {
        List<ActivityInfo> appList = new ArrayList<ActivityInfo>();
        ActivityInfo currentLauncherActivityInfo = null;
        final PackageManager pm = ctx.getPackageManager();
        final ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningTaskInfo> curentTasks = am.getRunningTasks(MAX_RECENT_TASKS);
        int numTasks = curentTasks.size();
        
        for (int i = 0; i < numTasks; ++i) {

            final ActivityManager.RunningTaskInfo info = curentTasks.get(i);
            Intent intent = new Intent();
            
            if (info.baseActivity != null) {
                intent.setComponent(info.baseActivity);
            }

            intent.setFlags((intent.getFlags() & ~Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED) | Intent.FLAG_ACTIVITY_NEW_TASK);
            final ResolveInfo resolveInfo = pm.resolveActivity(intent, 0);
            
            if (resolveInfo != null) {
                final ActivityInfo activityInfo = resolveInfo.activityInfo;
                appList.add(activityInfo);
            }
        }
        List<String> packageNames = new ArrayList<String>();
        
        for (ActivityInfo info : appList) {
            packageNames.add(info.applicationInfo.packageName);
        }

        Intent mainintent = new Intent(Intent.ACTION_MAIN, null);
        mainintent.addCategory(Intent.CATEGORY_HOME);
        
        if (pm != null) {
            List<ResolveInfo> mLauncherList = pm.queryIntentActivities(mainintent, 0);
            
            for(ActivityInfo info : appList){
            	
            	for(ResolveInfo launcherInfo : mLauncherList){
            		
            		if(info.applicationInfo.packageName.
            				equalsIgnoreCase(launcherInfo.activityInfo.packageName)){
            			return info;
            		}
            	}
            }                  
        }
        return null;
    }

	/**
	 * 获取当前运行的应用程序信息，并不一定是栈中最新的应用信息，如果要即时信息，请使用
	 * getCurrentRunningApps()
	 * @return
	 * 		List<ApplicationInfo> 用户运行的程序列表
	 */
	public static List<ApplicationInfo> getRunningApps(Context ctx) {
		PackageManager pm = ctx.getPackageManager();
		List<ApplicationInfo> appList =new ArrayList<ApplicationInfo> ();
        List<ActivityManager.RunningAppProcessInfo> procList = getRunningAppProcessesList(ctx);
        if ((procList == null) || (procList.size() == 0)) {
            return appList;
        }
        // Retrieve running processes from ActivityManager
        for (ActivityManager.RunningAppProcessInfo appProcInfo : procList) {
            if ((appProcInfo != null)  && (appProcInfo.pkgList != null)){
                int size = appProcInfo.pkgList.length;
                for (int i = 0; i < size; i++) {
                    ApplicationInfo appInfo = null;
                    try {
                        appInfo = pm.getApplicationInfo(appProcInfo.pkgList[i], 
                                PackageManager.GET_UNINSTALLED_PACKAGES);
                    } catch (NameNotFoundException e) {
                       Util.DEBUG("ThemlocalActivity Error retrieving ApplicationInfo for pkg:"+appProcInfo.pkgList[i]);
                       continue;
                    }
                    if(appInfo != null) {
                        appList.add(appInfo);
                    }
                }
            }
        }
		return appList;
	}
	/**
	 * 获取当前运行栈中，正在运行的程序列表，包括Launcher应用。
	 * @return
	 */
	public static List<ActivityInfo> getCurrentRunningApps(Context ctx) {
		List<ActivityInfo> appList =new ArrayList<ActivityInfo> ();
		
		final PackageManager pm = ctx.getPackageManager();
        final ActivityManager am = (ActivityManager)
                ctx.getSystemService(Context.ACTIVITY_SERVICE);
    
        final List<ActivityManager.RunningTaskInfo> curentTasks =
        			am.getRunningTasks(MAX_RECENT_TASKS);

        int numTasks = curentTasks.size();
        for (int i = 0; i < numTasks; ++i) {
            
        	final ActivityManager.RunningTaskInfo info = curentTasks.get(i);
        	
            Intent intent = new Intent();
            if (info.baseActivity != null) {
                intent.setComponent(info.baseActivity);
            }

            intent.setFlags((intent.getFlags()&~Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            final ResolveInfo resolveInfo = pm.resolveActivity(intent, 0);
            if (resolveInfo != null) {
                final ActivityInfo activityInfo = resolveInfo.activityInfo;
                appList.add(activityInfo);
            }
        }
		
		return appList;
	}
	
	/**
	 * 获取用户最近运行的应用，屏蔽掉了launcher应用，如果需要获取Launcher应用，
	 * 注释掉homeinfo相关代码即可。
	 * @return
	 * 		List<ApplicationInfo>	用户最近使用的应用程序列表
	 */
	public static List<ApplicationInfo> getRecentRunApps(Context ctx) {
		List<ApplicationInfo> appList =new ArrayList<ApplicationInfo> ();
		
		final PackageManager pm = ctx.getPackageManager();
        final ActivityManager am = (ActivityManager)
                ctx.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RecentTaskInfo> recentTasks =
                am.getRecentTasks(MAX_RECENT_TASKS, ActivityManager.RECENT_IGNORE_UNAVAILABLE);
        
        ActivityInfo homeInfo = 
            new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
                    .resolveActivityInfo(pm, 0);
        
        int numTasks = recentTasks.size();
        for (int i = 0; i < numTasks; ++i) {
            final ActivityManager.RecentTaskInfo info = recentTasks.get(i);
        	
            Intent intent = new Intent(info.baseIntent);
            if (info.origActivity != null) {
                intent.setComponent(info.origActivity);
            }

            //Skip the current home activity.
            if (homeInfo != null) {
                if (homeInfo.packageName.equals(
                        intent.getComponent().getPackageName())
                        && homeInfo.name.equals(
                                intent.getComponent().getClassName())) {
                    continue;
                }
            }

            intent.setFlags((intent.getFlags()&~Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            final ResolveInfo resolveInfo = pm.resolveActivity(intent, 0);
            if (resolveInfo != null) {
                final ActivityInfo activityInfo = resolveInfo.activityInfo;
                appList.add(activityInfo.applicationInfo);
            }
        }
		
		return appList;
	}
	/**
	 * 获得当前正在运行的应用程序进程信息
	 * @return
	 * 		List<ActivityManager.RunningAppProcessInfo>
	 */
	public static List<ActivityManager.RunningAppProcessInfo> getRunningAppProcessesList(Context ctx) {
        ActivityManager am = (ActivityManager)ctx.getSystemService(Context.ACTIVITY_SERVICE);
        return am.getRunningAppProcesses();
    }
	
	
	public static void setPreferredLauncher(Context ctx, String pkgName, String launcherClassName) {
		
		List<ResolveInfo> launcherList= new ArrayList<ResolveInfo>();
		
		Intent mainintent = new Intent(Intent.ACTION_MAIN,null);
		mainintent.addCategory(Intent.CATEGORY_HOME);
		
		launcherList = ctx.getPackageManager().queryIntentActivities(mainintent, 0);
		
		ComponentName component = new ComponentName(
				pkgName, launcherClassName);

		IntentFilter mFilter = new IntentFilter("android.intent.action.MAIN");
		mFilter.addCategory("android.intent.category.HOME");
		mFilter.addCategory("android.intent.category.DEFAULT");

		ComponentName[] arrayOfComponentName = new ComponentName[launcherList
				.size()];
		for (int i = 0; i < launcherList.size(); i++) {
			String packageName = launcherList.get(i).activityInfo.packageName;
			String className = launcherList.get(i).activityInfo.name;
			
			ctx.getPackageManager().clearPackagePreferredActivities(
					packageName);
			ComponentName componentName = new ComponentName(packageName,
					className);
			arrayOfComponentName[i] = componentName;
		}

		ctx.getPackageManager().addPreferredActivity(mFilter,
				IntentFilter.MATCH_CATEGORY_EMPTY, arrayOfComponentName,
				component);
		
		List<ComponentName> prefActList = new ArrayList<ComponentName>();
			List<IntentFilter> intentList = new ArrayList<IntentFilter>();	
			
			ctx.getPackageManager().getPreferredActivities(intentList, prefActList, pkgName);
		
		for(ComponentName comName : prefActList){
			
			//Util.BIZ_CONF_DEBUG(TAG,"applyLauncher --- componentName = " + comName.getPackageName());
		}
		
	}
}
