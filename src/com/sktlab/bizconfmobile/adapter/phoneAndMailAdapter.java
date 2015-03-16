package com.sktlab.bizconfmobile.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.activity.PhoneAndMail;
import com.sktlab.bizconfmobile.model.ConfHistory;

public class phoneAndMailAdapter extends BaseAdapter {

	private Activity mActivity;
	private ArrayList<String> mDatas;
	private LayoutInflater mInflater;
	private int mLocation;
	
	public phoneAndMailAdapter(Activity activity,ArrayList<String> datas,int location) {
		
		mActivity = activity;
		this.mLocation=location;
		
		mDatas = datas;
		init();
	}
	public phoneAndMailAdapter(Activity activity,ArrayList<String> datas) {
		
		this(activity, datas, -1);
	}
	
	public phoneAndMailAdapter(Activity activity,TreeSet<String> datas,int location) {
		this.mLocation = location;
		mActivity = activity;
		mDatas = new ArrayList<String>();
		initData(datas);		
		init();
	}
	
	public phoneAndMailAdapter(Activity activity,TreeSet<String> datas) {
		this(activity, datas, -1);
	}
	
	public void setData(TreeSet<String> datas) {
		
		initData(datas);
		notifyDataSetChanged();
	}
	
	public void initData(TreeSet<String> datas) {
		
		mDatas.clear();
		
		Iterator<String> iterator = datas.iterator();
		
		while(iterator.hasNext()) {
			
			mDatas.add(iterator.next());			
		}
	}
	
	@Override
	public int getCount() {
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}
	
	public void init() {
		
		mInflater = (LayoutInflater) mActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if( null != convertView){
			convertView = mInflater.inflate(R.layout.simple_text2, null);
			TextView  phoneAndMail = (TextView) convertView.findViewById(R.id.sm_contact_index_tv);
			TextView arrow = (TextView) convertView.findViewById(R.id.phone_and_mail_arrow);
			phoneAndMail.setText(mDatas.get(position));
			if(mLocation == position){
				arrow.setVisibility(View.VISIBLE);
			}else{
				arrow.setVisibility(View.INVISIBLE);
			}
		}
		return null;
	}

	
}
