package com.sktlab.bizconfmobile.fragment;

import java.util.TreeSet;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ListView;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.adapter.HistoryAdapter;
import com.sktlab.bizconfmobile.customview.TopSelectorView;
import com.sktlab.bizconfmobile.customview.TopSelectorView.OnSelectorChangeListener;
import com.sktlab.bizconfmobile.model.ConfHistory;
import com.sktlab.bizconfmobile.model.manager.ConfHistoryManager;
import com.sktlab.bizconfmobile.util.Util;

public class HistoryFragment extends Fragment implements OnSelectorChangeListener{
	
	public static final String TAG = "HistoryFragment";
	
	private Activity mActivity;
	private ListView mLvHistory;
	private TopSelectorView mTopSelector;
	private TreeSet<ConfHistory> mDatas;
	private HistoryAdapter mAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.fragment_history_main, container, false);
	}

	@Override
	public void onResume() {
		
		super.onResume();
		
		mActivity = getActivity();
		
		ConfHistoryManager chm = ConfHistoryManager.getInstance();
		
		mDatas = chm.getHistory(ConfHistoryManager.ASCEND_ORDER);
		
		mLvHistory = (ListView) mActivity.findViewById(R.id.lv_history_list);
		
		mAdapter = new HistoryAdapter(mActivity, mDatas);
		
		mLvHistory.setAdapter(mAdapter);
		
		mTopSelector = (TopSelectorView) mActivity.findViewById(R.id.history_top_selector);
		
		mTopSelector.setOnSelectedChangeListener(this);
	}

	@Override
	public void onLeftSelected() {

		mAdapter.setData(ConfHistoryManager.getInstance()
				.getHistory(ConfHistoryManager.ASCEND_ORDER));
	}

	@Override
	public void onRightSelected() {

		mAdapter.setData(ConfHistoryManager.getInstance()
				.getHistory(ConfHistoryManager.DESCEND_ORDER));
	}	
}
