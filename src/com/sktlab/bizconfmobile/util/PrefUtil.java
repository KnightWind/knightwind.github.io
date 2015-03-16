package com.sktlab.bizconfmobile.util;


import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

//import com.oppo.reader.online.download.DownloadEntity;
//import com.oppo.reader.online.net.NetworkHelper;
//import com.oppo.reader.online.service.StatisticalService;

/**
 * <p>
 * Title: PrefUtil
 * </p>
 * <p>
 * Description: PrefUtil
 * </p>
 * <p>
 * Copyright (c) 2010 www.oppo.com Inc. All rights reserved.
 * </p>
 * <p>
 * Company: OPPO
 * </p>
 * 
 * @author Jason.Lee
 * @version 1.0
 * 
 */

public class PrefUtil {
    public static final String P_USER_ACCOUNT = "pref.user.account";
    public static final String P_USER_PASSWORD = "pref.user.password";
    public static final String P_REMEMBER_PASSWORD = "pref.remember.password";
    public static final String P_USERNAME = "pref.username";
    public static final String P_PASSWORD = "pref.password";
    public static final String P_ISACTIVE = "pref.isActive";
    public static final String P_ISLOGIN = "pref.isLogin";
    public static final String P_SCREEN_SIZE = "pref.screen.size";
    public static final String P_OS_VERSION = "pref.os.version";
    public static final String P_MOBILE_NAME = "pref.mobile.name";
    public static final String P_METRICS_WIDTHPIXELS = "pref.metrics.widthpixels";
    public static final String P_METRICS_SCALEDDENSITY = "pref.metrics.scaleddensity";
    public static final String P_UPGRADE_NUM = "pref.upgrade.num";
    public static final String P_BLOG_USERNAME_PASSWORD = "pref.blog.username.password";
    public static final String P_BLOG_NAME = "pref.blog.name";
    public static final String P_LAST_UPLOAD_DATE = "pref.last.date";
    public static final String NEW_VERSION_CODE = "new.version.code";
    public static final String NEW_VERSION_NAME = "new.version.name";
    public static final String NEW_DOWNLOAD_URL = "new.download.url";
	public static final String NEW_UPGRADE_LEVEL = "new.upgrade.level";
    public static final String NEW_UPGRADE_COMMENT = "new.upgrade.comment";
    public static final String P_CLEAR_CACHE_EXIT = "pref.clear.cache.exit";
    public static final String P_IMEI = "pref.imei";

    /**
     * 帐号中心相关的属性
     */
    public static final String ACCOUNT_TYPE = "com.oppo.usercenter";
    public static final String USERDATA_UID = "user.data.uid";
    public static final String USERDATA_PASSWORD = "user.data.password";
    public static final String USERDATA_NICK_NAME = "user.data.nickname";
    public static final String USERDATA_MOBILE = "user.data.mobile";
    public static final String USERDATA_POINT = "user.data.point";
    public static final String USERDATA_BALANCE = "user.data.balance";
    public static final String USERDATA_PICTURE = "user.data.picture";
    public static final String USERDATA_MAIN_ACCOUNT = "SystemAccount";
    public static final String FLAG_USERDATA_MAIN_ACCOUNT = "true";
    public static final String FLAG_USERDATA_SUB_ACCOUNT = "false";
   // private static HashMap<String, DownloadEntity> upgradeMap = null;
    public static SharedPreferences sPref;
    private static Account sAccount;
    //    private static AccountManager sAccountManager;
    private static Context sContext;
    private static Intent uploadIntent;

    public static final String DEFAULT_ACCOUNT = "anonymous@oppo.com";
    
    public static void init(Context context) {
        if (null == sPref) {
            if (null == sContext) {
                sContext = context;
                sPref = PreferenceManager.getDefaultSharedPreferences(sContext);
            }
        }
        //        PrefUtil.registerAccount();
        //initAccount();
        final DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) sContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        setWidthPixels(metrics.widthPixels);
        setScaledDensity(metrics.scaledDensity);
        final String screenSize = metrics.heightPixels + "*" + metrics.widthPixels;
        setResolution(sContext, screenSize);
        setOSVersionName(sContext, Build.VERSION.SDK_INT);
        setMobileName(sContext, Build.MODEL);
        TelephonyManager telephonyManager = (TelephonyManager) sContext.getSystemService(Context.TELEPHONY_SERVICE);
        setIMEI(sContext, telephonyManager.getDeviceId());
        //upgradeMap = new HashMap<String, DownloadEntity>();
        //upload();
    }

    public static void clear() {
        //        unregisterAccount();
        if (null != uploadIntent) {
            sContext.stopService(uploadIntent);
            uploadIntent = null;
        }

        sContext = null;
        sPref = null;
        sAccount = null;
        //        sOnAccountsUpdateListener = null;
//        upgradeMap.clear();
//        upgradeMap = null;

    }

    public static Context getContext() {
        return sContext;
    }

    public static Editor getEditor() {
        return sPref == null ? null : sPref.edit();
    }

    /*
    private static String decrypt(String encValue) {
        if (TextUtils.isEmpty(encValue))
            return "";

        byte[] bytes = Base64.decodeBase64(U.getUTF8Bytes(encValue));
        if (bytes == null)
            return "";

        Crypter crypter = new Crypter();
        bytes = crypter.decrypt(bytes, DigestUtils.md5(DigestUtils.md5("7U727ALEWH8".getBytes())));
        if (bytes == null)
            return "";

        return U.getUTF8String(bytes);
    }

    private static String encrypt(String value) {
        if (value == null)
            return null;

        byte[] bytes = U.getUTF8Bytes(value);
        Crypter crypter = new Crypter();
        bytes = crypter.encrypt(bytes, DigestUtils.md5(DigestUtils.md5("7U727ALEWH8".getBytes())));
        bytes = Base64.encodeBase64(bytes);

        return U.getUTF8String(bytes);
    }
    */

//    private static void initAccount() {
//        if (null == sAccount) {
//            AccountManager accountManager = AccountManager.get(sContext);
//            Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
//
//            for (Account account : accounts) {
//
//                String flag = accountManager.getUserData(account, USERDATA_MAIN_ACCOUNT);
//
//                if (!U.isEmpty(flag) && FLAG_USERDATA_MAIN_ACCOUNT.equals(flag)) {
//                    sAccount = account;
//                    setIsLogin(sContext, true);
//                    setUserAccount(sContext, account.name);
//                    break;
//                }
//            }
//        }
//    }

    //    private static OnAccountsUpdateListener sOnAccountsUpdateListener = new OnAccountsUpdateListener() {
    //
    //        @Override
    //        public void onAccountsUpdated(Account[] accounts) {
    //
    //            for (Account account : accounts) {
    //                String flag = sAccountManager.getUserData(account, USERDATA_MAIN_ACCOUNT);
    //
    //                if ((null != flag) && FLAG_USERDATA_MAIN_ACCOUNT.equals(flag)) {
    //                    setIsLogin(sContext, true);
    //                    setUserAccount(sContext, account.name);
    //                    break;
    //                }
    //            }
    //
    //        }
    //
    //    };

    //    public static void registerAccount() {
    //        initAccountManager();
    //        sAccountManager.addOnAccountsUpdatedListener(sOnAccountsUpdateListener, new Handler(), true);
    //    }
    //
    //    public static void unregisterAccount() {
    //        sAccountManager.removeOnAccountsUpdatedListener(sOnAccountsUpdateListener);
    //    }

    //    private static void fetchMainAccount() {
    //        initAccountManager();
    //        Account[] accounts = sAccountManager.getAccountsByType(ACCOUNT_TYPE);
    //
    //        for (Account account : accounts) {
    //            String flag = sAccountManager.getUserData(account, USERDATA_MAIN_ACCOUNT);
    //
    //            if ((null != flag) && FLAG_USERDATA_MAIN_ACCOUNT.equals(flag)) {
    //                setIsLogin(sContext, true);
    //                setUserAccount(sContext, account.name);
    //                break;
    //            }
    //        }
    //
    //        if ((null == accounts) || ((null != accounts) && (0 == accounts.length))) {
    //            setIsLogin(sContext, false);
    //            setUserAccount(sContext, null);
    //        }
    //    }

    /*
    public static void setUserAccount(Context context, String account) {
        Editor edit = sPref.edit();

        if (U.isNoEmpty(account)) {
            edit.putString(P_USER_ACCOUNT, encrypt(account));
        }

        edit.commit();
    }

    public static String getUserAccount() {
        //initAccount();
        final String account = sPref.getString(P_USER_ACCOUNT, null);

        if (U.isEmpty(account)) {
            return "";
        } else {
            return decrypt(account);
        }
    }

    public static void setPassword(Context context, String password) {
        Editor edit = sPref.edit();

        if (!TextUtils.isEmpty(password)) {
            edit.putString(P_USER_ACCOUNT, encrypt(password));
        }

        edit.commit();
    }

    public static String getUserPassword(Context context) {
        final String password = sPref.getString(P_USER_ACCOUNT, null);

        if (null == password) {
            return "";
        } else {
            return decrypt(password);
        }
    }

    public static String getLastUploadDate() {
        final String lastModified = sPref.getString(P_LAST_UPLOAD_DATE, "19700000");
        return lastModified;
    }

    public static void setUploadDate(String uploadDate) {
        Editor edit = sPref.edit();
        edit.putString(P_LAST_UPLOAD_DATE, uploadDate).commit();
    }

    public static void setAccountAndPassword(Context context, String username, String password) {
        Editor edit = sPref.edit();

        if (!TextUtils.isEmpty(username)) {
            edit.putString(P_USER_ACCOUNT, encrypt(username));
        }

        if (!TextUtils.isEmpty(password)) {
            edit.putString(P_USER_PASSWORD, encrypt(password));
        }

        edit.commit();
    }

    public static boolean shouldRememberPassword(Context context) {
        return sPref.getBoolean(P_REMEMBER_PASSWORD, false);
    }

    public static void setRememberPassword(Context context, boolean remember) {
        Editor edit = sPref.edit();
        edit.putBoolean(P_REMEMBER_PASSWORD, remember);
        edit.commit();
    }

    public static void setUsername(Context context, String username) {
        Editor edit = sPref.edit();
        if (!TextUtils.isEmpty(username))
            edit.putString(P_USERNAME, encrypt(username));
        edit.commit();
    }

    public static String getUsername(Context context) {
        final String username = sPref.getString(P_USERNAME, null);
        if (username == null)
            return DEFAULT_ACCOUNT;
        else {
            return decrypt(username);
        }
    }
*/
    /**
     * 是否为激活用户
     * 
     * @param context
     * @return true, 激活用户; false, 非激活用户
     */
    public static boolean isActive(Context context) {
        return sPref.getBoolean(P_ISACTIVE, false);
    }

    /**
     * 设置是否为激活用户
     * 
     * @param context
     * @param isActive
     */
    public static void setIsActive(Context context, boolean isActive) {
        Editor edit = sPref.edit();
        edit.putBoolean(P_ISACTIVE, isActive);
        edit.commit();
    }

    /**
     * 是否登录
     * 
     * @param context
     * @return true, 登录; false, 未登录
     */
    public static boolean isLogin() {
        //initAccount();
        //        fetchMainAccount();
        return sPref.getBoolean(P_ISLOGIN, false);
    }

    /**
     * 设置是否登录
     * 
     * @param context
     * @param isActive
     */
    public static void setIsLogin(Context context, boolean isLogin) {
        Editor edit = sPref.edit();
        edit.putBoolean(P_ISLOGIN, isLogin);
        edit.commit();
    }

    private static void setIMEI(Context context, String imei) {
    	if (sPref == null) {
			init(context);
		}
        Editor edit = sPref.edit();
        edit.putString(P_IMEI, imei);
        edit.commit();
    }

    public static String getIMEI() {
        return sPref.getString(P_IMEI, "");
    }

    /**
     * 得到屏幕大小
     * 
     * @param context
     * @return 默认值为HVGA
     */
    public static String getResolution(Context ctx) {
    	if (sPref == null) {
			init(ctx);
		}
        return sPref.getString(P_SCREEN_SIZE, "HVGA");
    }

    /**
     * 设置屏幕大小
     * 
     * @param context
     * @param screenSize
     */
    private static void setResolution(Context context, String screenSize) {
        // String result = "HVGA";
        // if (screenSize.equalsIgnoreCase("800480")) {
        // result = "WVGA800";
        // } else if (screenSize.equalsIgnoreCase("854480")) {
        // result = "WVGA854";
        // } else if (screenSize.equalsIgnoreCase("640480")) {
        // result = "VGA";
        // } else if (screenSize.equalsIgnoreCase("480320")) {
        // result = "HVGA";
        // } else if (screenSize.equalsIgnoreCase("400240")) {
        // result = "WQVGA400";
        // } else if (screenSize.equalsIgnoreCase("432240")) {
        // result = "WQVGA432";
        // } else if (screenSize.equalsIgnoreCase("320240")) {
        // result = "QVGA";
        // }
        Editor edit = sPref.edit();
        edit.putString(P_SCREEN_SIZE, screenSize);
        edit.commit();
    }

    /**
     * 得到机器型号
     * 
     * @param context
     * @return 默认值为DEFAULT
     */
    public static String getMobileName(Context ctx) {
    	if (sPref == null) {
			init(ctx);
		}
        return sPref.getString(P_MOBILE_NAME, "DEFAULT");
    }

    /**
     * 设置机器型号
     * 
     * @param context
     * @param mobileName
     */
    public static void setMobileName(Context context, String mobileName) {
        Editor edit = sPref.edit();
        edit.putString(P_MOBILE_NAME, mobileName);
        edit.commit();
    }

    /**
     * @param context
     * @return OS版本号
     */
    public static int getOSVersionName(Context ctx) {
    	if (sPref == null) {
			init(ctx);
		}
        return sPref.getInt(P_OS_VERSION, 8);
    }

    /**
     * 设置OS版本号
     * 
     * @param context
     * @param osVersion
     */
    public static void setOSVersionName(Context context, int osVersion) { 
    	if (sPref == null) {
			init(context);
		}
    	
        Editor edit = sPref.edit();
        edit.putInt(P_OS_VERSION, osVersion);
        edit.commit();
    }

    private static void setWidthPixels(int widthPixels) {
        final Editor edit = sPref.edit();
        edit.putInt(P_METRICS_WIDTHPIXELS, widthPixels);
        edit.commit();
    }

    public static int getWidthPixels() {
        return sPref.getInt(P_METRICS_WIDTHPIXELS, 0);
    }

    private static void setScaledDensity(float scaledDensity) {
        final Editor editor = sPref.edit();
        editor.putFloat(P_METRICS_SCALEDDENSITY, scaledDensity);
        editor.commit();
    }

    public static float getScaledDensity() {
        return sPref.getFloat(P_METRICS_SCALEDDENSITY, 1);
    }

    /**
     * @param context
     * @return 可更新数
     */
    public static int getUpgradeNum(Context context) {
        return sPref.getInt(P_UPGRADE_NUM, 0);
    }

    /**
     * 设置可更新数
     * 
     * @param context
     * @param num
     */
    public static void setUpgradeNum(Context context, int num) {
        Editor edit = sPref.edit();
        edit.putInt(P_UPGRADE_NUM, num);
        edit.commit();
    }

    /*
    public static void setUsernameAndPassword(Context context, int id, String username, String password) {
        Editor edit = sPref.edit();
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            edit.putString(P_BLOG_USERNAME_PASSWORD + "." + id, encrypt(username + ":" + password));
        }
        edit.commit();
    }

    public static String getUsernameAndPassword(Context context, int id) {
        String value = sPref.getString(P_BLOG_USERNAME_PASSWORD + "." + id, null);
        if (null == value) {
            return "";
        } else {
            return decrypt(value);
        }
    }
    */

    public static void setBlogName(Context context, int id, String name) {
        Editor edit = sPref.edit();
        if (!TextUtils.isEmpty(name)) {
            edit.putString(P_BLOG_NAME + "." + id, name);
        }
        edit.commit();
    }

    public static String getBlogName(Context context, int id) {
        return sPref.getString(P_BLOG_NAME + "." + id, null);
    }

    public static void setNewVersionCode(Context context, int versionCode) {
        Editor edit = sPref.edit();
        edit.putInt(NEW_VERSION_CODE, versionCode);
        edit.commit();
    }

    public static int getNewVersionCode(Context ctx) {
		if (null == sPref) {
			init(ctx);
		}
		return sPref.getInt(NEW_VERSION_CODE, 0);
    }

    public static void setNewVersionName(Context context, String versionName) {
        Editor edit = sPref.edit();
        if (!TextUtils.isEmpty(versionName)) {
            edit.putString(NEW_VERSION_NAME, versionName);
        }
        edit.commit();
    }

    public static String getNewVersionName(Context context) {
        return sPref.getString(NEW_VERSION_NAME, null);
    }

    public static void setNewDownloadUrl(Context context, String url) {
        Editor edit = sPref.edit();
        if (!TextUtils.isEmpty(url)) {
            edit.putString(NEW_DOWNLOAD_URL, url);
        }
        edit.commit();
    }

    public static String getNewDownloadUrl(Context context) {
        return sPref.getString(NEW_DOWNLOAD_URL, null);
    }
    
	public static void setUpgradeLevel(Context context, int level) {
		if (null == sPref) {
			init(context);
		}
		Editor edit = sPref.edit();
		edit.putInt(NEW_UPGRADE_LEVEL, level);
		edit.commit();
	}

    public static void setUpgradeComment(Context context, String comment) {
        Editor edit = sPref.edit();
        if (!TextUtils.isEmpty(comment)) {
            edit.putString(NEW_UPGRADE_COMMENT, comment);
        }
        edit.commit();
    }
    /*
	public static int getUpgradeLevel(Context context) {
		if (null == sPref) {
			init(context);
		}
		return sPref.getInt(NEW_UPGRADE_LEVEL, SelfUpgradeInfo.FLAG_NEW);
	}
	*/

    public static String getUpgradeComment(Context context) {
        return sPref.getString(NEW_UPGRADE_COMMENT, null);
    }

    public static void setClearCacheFlag(Context context, boolean flag) {
        Editor edit = sPref.edit();
        edit.putBoolean(P_CLEAR_CACHE_EXIT, flag);
        edit.commit();
    }

    public static boolean getClearCacheFlag(Context context) {
        return sPref.getBoolean(P_CLEAR_CACHE_EXIT, false);
    }

//    public static HashMap<String, DownloadEntity> getUpradeMap() {
//        return upgradeMap;
//    }

//    private static void upload() {
//        String lastUploadDate = PrefUtil.getLastUploadDate();
//        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
//        String current = format.format(new Date(System.currentTimeMillis()));
//
//        try {
//
//            if (NetworkHelper.isWifiWorking() && !StatisticalService.hasUpload
//                    && Integer.parseInt(current) > Integer.parseInt(lastUploadDate)) {
//                uploadIntent = new Intent(sContext, StatisticalService.class);
//                sContext.startService(uploadIntent);
//                PrefUtil.setUploadDate(current);
//            }
//
//        } catch (Exception e) {
//            LogUtil.e(Constants.TAG, e.getMessage());
//        }
//    }

}
