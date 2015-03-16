package com.sktlab.bizconfmobile.adapter;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.activity.AppClass;
import com.sktlab.bizconfmobile.model.AccessNumber;
import com.sktlab.bizconfmobile.util.Util;

public class AccessNumberAdapter extends BaseAdapter {
	
	public static final String TAG = "AccessNumberAdapter";
	
	private LayoutInflater mInflater;
	private List<AccessNumber> mAccessNumberDatas;
	    
	public AccessNumberAdapter(List<AccessNumber> datas) {
		
		mAccessNumberDatas = datas;
		mInflater = (LayoutInflater) AppClass.getInstance()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	
	}
	
	
	public void setData(ArrayList<AccessNumber> datas) {
		
		mAccessNumberDatas = datas;
	}
	
	@Override
	public int getCount() {
		
		return mAccessNumberDatas.size();
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
			
			convertView = mInflater.inflate(R.layout.item_access_number_layout, null);
			
			vh = new ViewHolder();
			
			vh.tvNumber = (TextView) convertView.findViewById(R.id.tv_number);
			vh.tvNumberType = (TextView) convertView.findViewById(R.id.tv_number_type);
			vh.tvCountry = (TextView) convertView.findViewById(R.id.tv_country);
			
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		
		AccessNumber an = mAccessNumberDatas.get(position);
		
		if(!Util.isEmpty(an)) {
			
			vh.tvNumber.setText(an.getNumber());
			vh.tvNumberType.setText(an.getNumberType());
			vh.tvCountry.setText(an.getCountry());
		}
		
		return convertView;
	}
	
	class ViewHolder {
		private TextView tvCountry;
		private TextView tvNumber;
		private TextView tvNumberType;
	}

}
