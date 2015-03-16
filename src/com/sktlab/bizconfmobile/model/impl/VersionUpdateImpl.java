package com.sktlab.bizconfmobile.model.impl;

import android.content.Context;

import com.lurencun.service.autoupdate.AppUpdateService;
import com.lurencun.service.autoupdate.DisplayDelegate;
import com.lurencun.service.autoupdate.Version;
import com.lurencun.service.autoupdate.internal.VersionDialogListener;
import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.model.NewVersionFoundDialog;
import com.sktlab.bizconfmobile.util.FileUtil;
import com.sktlab.bizconfmobile.util.Util;

public class VersionUpdateImpl implements DisplayDelegate, VersionDialogListener{
	
	private Context ctx;
	//whether show the toast of current version is latest verison.
	private boolean isShowToast;
	
	public VersionUpdateImpl(Context ctx) {
		
		this.ctx = ctx;
		this.isShowToast = true;
	}
	
	public VersionUpdateImpl(Context ctx, boolean isShowToast) {
		
		this.ctx = ctx;
		this.setShowToast(isShowToast);
	}
	
	@Override
	public void showFoundLatestVersion(Version version) {
		
		NewVersionFoundDialog dialog = new NewVersionFoundDialog(ctx, version, this);
		dialog.show();
	}

	@Override
	public void showIsLatestVersion() {
		
		if (isShowToast()) {
			
			Util.shortToast(ctx, R.string.toast_is_latest_version);
		}		
	}

	@Override
	public void doUpdate(boolean laterOnWifi) {
		
		if (FileUtil.isExistSDcard()) {
			
			FileUtil.createFileDir(FileUtil.SD_DOWNLOAD_PATH);
			AppUpdateService.getVersionDialogListener(ctx).doUpdate(laterOnWifi);
		}else {
			
			Util.shortToast(ctx, R.string.toast_storage_access_denied);
		}
	}

	@Override
	public void doIgnore() {
		
		AppUpdateService.getVersionDialogListener(ctx).doIgnore();
	}

	public boolean isShowToast() {
		return isShowToast;
	}

	public void setShowToast(boolean isShowToast) {
		this.isShowToast = isShowToast;
	}

	@Override
	public void showNetworkUnavaiable() {
		
		if (isShowToast()) {
			
			Util.longToast(ctx, R.string.toast_network_unavailable);
		}
	}	
}
