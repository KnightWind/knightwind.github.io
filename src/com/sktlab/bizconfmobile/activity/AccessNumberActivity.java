package com.sktlab.bizconfmobile.activity;

import java.util.ArrayList;
import java.util.List;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.adapter.AccessNumberAdapter;
import com.sktlab.bizconfmobile.interfaces.DataLoader;
import com.sktlab.bizconfmobile.interfaces.ILoadingDialogCallback;
import com.sktlab.bizconfmobile.model.AccessNumber;
import com.sktlab.bizconfmobile.model.BridgeInfo;
import com.sktlab.bizconfmobile.model.Constant;
import com.sktlab.bizconfmobile.model.factory.DataLoaderFactory;
import com.sktlab.bizconfmobile.net.NetOp;
import com.sktlab.bizconfmobile.parser.AccessNumCSVParser;
import com.sktlab.bizconfmobile.parser.AccessNumNetParser;
import com.sktlab.bizconfmobile.util.FileUtil;
import com.sktlab.bizconfmobile.util.LoadingDialogUtil;
import com.sktlab.bizconfmobile.util.Util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class AccessNumberActivity extends BaseActivity implements ILoadingDialogCallback{
	
	private static final int LOADING_TIME_OUT = 10 * 1000;
	
	public static final String TAG = "AccessNumberActivity";
	public static final String KEY_ACCESS_NUMBER = "access_number";
	public static final String NOT_SELECTED_NUMBER = "null";
	
	private String inputConfCode = "";
	
	private ListView mLv;
	private AccessNumberAdapter mAdapter;
	private List<AccessNumber> mDatas;
	private int pos = -1;
	private Context ctx;
	private DataLoader loader;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ctx = this;
		
		setContentView(R.layout.activity_access_number);
		
		mLv = (ListView) findViewById(R.id.lv_access_number);	
		
		Intent intent = getIntent();		
		inputConfCode = intent.getStringExtra(Constant.KEY_OF_INPUT_CONF_CODE);
		
		final LoadingDialogUtil dialog = new LoadingDialogUtil(this, this);
		
		AppClass.getInstance().getService().submit(new Runnable() {
			
			@Override
			public void run() {
			
				dialog.showDialog(R.string.toast_loading_access_number, LOADING_TIME_OUT);
				
				mDatas = loadAccessNumber();
				
				if (null != mDatas && mDatas.size() > 0) {
				
					dialog.finishDialogSuccessDone();
				} else {
					
					dialog.finishDialogWithErrorMsg();
				}				
			}
		});
	}

	private void writeBridgeId2SharedPerference(BridgeInfo bridgeInfo) {
		
		if (bridgeInfo.isBridgeIdValid()) {
			
			Util.BIZ_CONF_DEBUG(TAG, "store conf2BridgeId: " + inputConfCode + " " + bridgeInfo.getBridgeId()+"   template"
					+bridgeInfo.templateType
					);
			Util.setSpInt(ctx, inputConfCode+"1", bridgeInfo.getBridgeId());
			Util.setSpInt(ctx, inputConfCode+"2", bridgeInfo.templateType);
			
		}
	}
	
	public List<AccessNumber> loadAccessNumber() {
		
		List<AccessNumber> list = null;
		
		BridgeInfo bridgeInfo = new BridgeInfo(inputConfCode);
		int bridgeId = bridgeInfo.getBridgeId();
		
		writeBridgeId2SharedPerference(bridgeInfo);
		
		if (Util.isNetworkAvailable(ctx)) {
			
			loader = DataLoaderFactory.getInstance().createLoaderFromNet(bridgeId,inputConfCode);
		}else {
			
			loader = DataLoaderFactory.getInstance().createLoader(bridgeId);
		}

		
		list = loader.getLoadedData();
		
		if (null != list && list.size() > 0 && FileUtil.isExistSDcard()) {
			
			writeCsvFileSaveState();
		}
		
		return list;
	}
	
	private void writeCsvFileSaveState() {
		
		String language = AccessNumNetParser.getLanguage();
		
		String key = inputConfCode + "_" + language;
		
		Util.setSpInt(ctx, key, 1);
	}
	
	@Override
	public void finish() {
		
		Intent data = new Intent();
		
		if(pos >= 0) {
			
			AccessNumber an = mDatas.get(pos);
			//AppClass.getInstance().setAccessNumber(an.getNumber());		
			String number = an.getNumber();
			
			data.putExtra(KEY_ACCESS_NUMBER, number);
		}else {
			
			data.putExtra(KEY_ACCESS_NUMBER, NOT_SELECTED_NUMBER);
		}
		
		setResult(RESULT_OK, data);
		
		super.finish();
	}

	@Override
	public void onSuccessDone() {
		
		if (null == mDatas) {
			
			mDatas = new ArrayList<AccessNumber>();
		}
		
		mAdapter = new AccessNumberAdapter(mDatas);
		
		mLv.setAdapter(mAdapter);
		mLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				pos = position;
				finish();
			}
		});
	}

	@Override
	public void onDoneWithError() {
		
		if (null != loader) {
			
			loader.showMsg();
		}

		finish();
	}		
	
}
