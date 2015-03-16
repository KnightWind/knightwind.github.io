package com.sktlab.bizconfmobile.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.model.AccessNumber;

public class AccordAccessNumberAdapter extends BaseAdapter {
	private List<AccessNumber> accordAccessNumber;
	private Activity mActivity;
	private LayoutInflater mInflater;
	
	
	public AccordAccessNumberAdapter(List<AccessNumber> accordAccessNumber,Activity activity){
		this.accordAccessNumber = accordAccessNumber;
		this.mActivity = activity;
		init();
	}
	
	public List<AccessNumber> getAccordAccessNumber() {
		return accordAccessNumber;
	}

	public void setAccordAccessNumber(List<AccessNumber> accordAccessNumber) {
		this.accordAccessNumber = accordAccessNumber;
		this.notifyDataSetChanged();
	}
	
	public void init() {
		
		mInflater = (LayoutInflater) mActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	
	public int getCount() {
		return accordAccessNumber.size();
	}

	public Object getItem(int arg0) {
		return null;
	}
	
	
	public long getItemId(int arg0) {
		return 0;
	}
	
	
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		convertView = mInflater.inflate(R.layout.location_change_list, null);
		TextView numberType = (TextView) convertView.findViewById(R.id.location_change_number_type);
		TextView number = (TextView) convertView.findViewById(R.id.location_change_number);
		numberType.setText(accordAccessNumber.get(arg0).getNumberType());
		number.setText(accordAccessNumber.get(arg0).getNumber());
		return convertView;
	}

}
