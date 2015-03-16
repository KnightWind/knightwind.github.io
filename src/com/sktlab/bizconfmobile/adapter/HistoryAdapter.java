package com.sktlab.bizconfmobile.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.model.ConfHistory;
import com.sktlab.bizconfmobile.util.DateUtil;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HistoryAdapter extends BaseAdapter {

	private Activity mActivity;
	private ArrayList<ConfHistory> mDatas;
	private LayoutInflater mInflater;
		
	public HistoryAdapter(Activity activity,ArrayList<ConfHistory> datas) {
		
		mActivity = activity;
		mDatas = datas;
		
		init();
	}
	
	public HistoryAdapter(Activity activity,TreeSet<ConfHistory> datas) {
		
		mActivity = activity;
		
		mDatas = new ArrayList<ConfHistory>();
		
		initData(datas);		
		init();
	}
	
	public void setData(TreeSet<ConfHistory> datas) {
		
		initData(datas);
		notifyDataSetChanged();
	}
	
	public void initData(TreeSet<ConfHistory> datas) {
		
		mDatas.clear();
		
		Iterator<ConfHistory> iterator = datas.iterator();
		
		while(iterator.hasNext()) {
			
			mDatas.add(iterator.next());			
		}
	}
	
	public void init() {
	
		mInflater = (LayoutInflater) mActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		
		return mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		
		return null;
	}

	@Override
	public long getItemId(int position) {
		
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	
		ViewHolder vh = null;
		
		if (convertView == null) {
			
			convertView = mInflater.inflate(R.layout.item_history_list, null);
			
			vh = new ViewHolder();
			
			vh.tvAccountName = (TextView)convertView
						.findViewById(R.id.tv_history_account_name);
			
			vh.tvConfDate = (TextView)convertView
					.findViewById(R.id.tv_history_conf_date);
			
			vh.tvConfCode = (TextView)convertView
					.findViewById(R.id.tv_history_conf_code);
		
			vh.tvConfTime = (TextView)convertView
				.findViewById(R.id.tv_history_conf_time);
		
			convertView.setTag(vh);
			
		} else {
			
			vh = (ViewHolder) convertView.getTag();
		}
		
		ConfHistory history = mDatas.get(position);
		
		if( null != history) {
			
			vh.tvAccountName.setText(history.getAccountName());
			
			String date = DateUtil.getFormatString(history.getStartDate(), DateUtil.YY_MM_DD_E);
			vh.tvConfDate.setText(date);
			
			vh.tvConfCode.setText(history.getConfCode());
			
			String startTime = DateUtil.getFormatString(history.getStartDate(), DateUtil.HH_MM_24);
			String endTime = DateUtil.getFormatString(history.getEndDate(), DateUtil.HH_MM_24);
			
			String timeStr = startTime + "-" + endTime;
			
			vh.tvConfTime.setText(timeStr);
		}
		
		return convertView;
	}
		
	private class ViewHolder{
		
		private TextView tvAccountName;
		private TextView tvConfDate;
		private TextView tvConfCode;
		private TextView tvConfTime;
	}
}
