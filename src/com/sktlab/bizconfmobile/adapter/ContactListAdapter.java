package com.sktlab.bizconfmobile.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.activity.MainActivity;
import com.sktlab.bizconfmobile.activity.PhoneAndMail;
import com.sktlab.bizconfmobile.interfaces.IOnCheckedListener;
import com.sktlab.bizconfmobile.model.Alphabet;
import com.sktlab.bizconfmobile.model.ContactItem;
import com.sktlab.bizconfmobile.model.Participant;
import com.sktlab.bizconfmobile.util.Util;
import com.sktlab.bizconfmobile.util.ValidatorUtil;

public class ContactListAdapter extends BaseExpandableListAdapter implements OnItemLongClickListener , OnChildClickListener ,OnGroupClickListener  {
	
	public static int parent = -1;
	public static int child = -1;
	public static final int PHONEANDMAILACTIVITY=10;
	public static final int GALLARYACTIVITY = 1;
	private Context mCtx;
	private LayoutInflater mInflater;
	private List<Alphabet> alphabets;
	private IOnCheckedListener mCallback;
	private ImageView personHeader;
	private boolean isShowEmail = false;
	private Participant part;
	private ContactItem item;
	
	DisplayImageOptions options;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	public ContactListAdapter(Context ctx, List<Alphabet> data, boolean showEmail){
		
		mCtx = ctx;
		alphabets = data;
		isShowEmail = showEmail;
		
		mInflater = (LayoutInflater)mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.default_head)			// 设置图片下载期间显示的图片
		.showImageOnFail(R.drawable.default_head)		// 设置图片加载或解码过程中发生错误显示的图片	
		.cacheInMemory(true)						// 设置下载的图片是否缓存在内存中
		.cacheOnDisc(true)							// 设置下载的图片是否缓存在SD卡中
		.displayer(new RoundedBitmapDisplayer(20))	// 设置成圆角图片
		.build();
		imageLoader.init(ImageLoaderConfiguration.createDefault(mCtx));
	}
	
	public void setData(List<Alphabet> data) {
		
		if (null != alphabets) {
			
			alphabets.clear();
			alphabets = data;
			
			notifyDataSetChanged();
		}
	}
	@Override
	public int getGroupCount() {
		return alphabets.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		
		Alphabet item = alphabets.get(groupPosition);
					
		return item.getContacts().size();			
	}

	@Override
	public Object getGroup(int groupPosition) {
		
		return alphabets.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		
		return alphabets.get(groupPosition).getClickedAttr(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		
		GroupHolder groupHolder = null;
		
		if(convertView == null) {
			
			groupHolder = new GroupHolder();
			
			convertView = mInflater.inflate(R.layout.item_expandable_listview_group, null);
			
			groupHolder.ivGroupIndicator = (ImageView) convertView.findViewById(R.id.iv_group_indicator);
			groupHolder.tvPersonName = (TextView) convertView.findViewById(R.id.tv_person_name);
			
			convertView.setTag(groupHolder);
		}else {
			
			groupHolder = (GroupHolder)convertView.getTag();
		}
		
		if(!Util.isEmpty(groupHolder.ivGroupIndicator)){
			
			groupHolder.ivGroupIndicator.setVisibility(View.VISIBLE);
			
			//hide group indicator
			groupHolder.ivGroupIndicator.setImageResource(android.R.color.transparent);
						
//			if(isExpanded) {
//				
//				groupHolder.ivGroupIndicator.setImageResource(R.drawable.expand_group_open);
//			}else {
//				
//				groupHolder.ivGroupIndicator.setImageResource(R.drawable.expand_group_close);
//			}
		}

		groupHolder.tvPersonName.setText(alphabets.get(groupPosition).getAlp().toUpperCase());
		convertView.setTag(R.id.bt_confirm_all, -1);
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		
		ChildHodler childHolder = null;
		ContactItem item = alphabets.get(groupPosition).getContacts().get(childPosition);
		if(convertView == null) {
			
			childHolder = new ChildHodler();
			
			convertView = mInflater.inflate(R.layout.item_expandable_listview_child, null);
			childHolder.ctvPersonName = (CheckedTextView) convertView.findViewById(R.id.ctv_person_name);
			childHolder.ctvPersonArrow = (TextView) convertView.findViewById(R.id.ctv_person_arrow);
			childHolder.img = (ImageView) convertView.findViewById(R.id.ctv_person_head);
			convertView.setTag(childHolder);
		}else {
			
			childHolder = (ChildHodler)convertView.getTag();
		}
		
		if ((null != alphabets) && (groupPosition < alphabets.size())) {
			
			childHolder.ctvPersonName.setText(item.getName());
			childHolder.ctvPersonName.setChecked(item.isCheck());
			//childHolder.ctvPersonName.setChecked(item.getAttrSelectedState(childPosition));
			
			
		}
		if(isShowEmail){
			if ((item.getPhone().size()+item.getEmail().size())>1){
				childHolder.ctvPersonArrow.setVisibility(View.VISIBLE);
			} else {
				childHolder.ctvPersonArrow.setVisibility(View.GONE);
			}
		} else {
			if(item.getPhone().size()>1){
				childHolder.ctvPersonArrow.setVisibility(View.VISIBLE);
			} else {
				childHolder.ctvPersonArrow.setVisibility(View.GONE);
			}
		}
		convertView.setTag(R.id.bt_confirm_all, 1);
		convertView.setTag(R.id.bt_confirm_all1, childPosition);
		convertView.setTag(R.id.bt_for_confirm, groupPosition);
		
		
		if(null != item.getUri()){
			imageLoader.displayImage(item.getUri().toString(), childHolder.img, options);
		}else{
			childHolder.img.setImageResource(R.drawable.default_head);
		}
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		
		return true;
	}
	
	public IOnCheckedListener getCallback() {
		return mCallback;
	}

	public void setCallback(IOnCheckedListener mCallback) {
		this.mCallback = mCallback;
	}

	public class GroupHolder{
		
		private ImageView ivGroupIndicator;
		private TextView tvPersonName;
	}
	
	public class ChildHodler{
		
		private TextView tvPersonName;
		private CheckedTextView ctvPersonName;
		private TextView ctvPersonArrow;
		private ImageView img;
	}
		
	public void addPart(String info, int pos) {
		
		part.setSelectedAttrPosInContactItem(pos);
		
		if (ValidatorUtil.isNumberValid(info)) {
			
			item.setSelectedType(ContactItem.TYPE_PHONE_NUMBER);
			part.setPhone(info.replace("-", "w,,,,,"));
		} else {
			
			item.setSelectedType(ContactItem.TYPE_EMAIL_ADDRESS);
			part.setEmail(info);
		}
		
		mCallback.onChecked(true, part);
		notifyDataSetChanged();
	}	
	
	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, final int childPosition, long id) {
		
		CheckedTextView childCheckTextView = (CheckedTextView)v.findViewById(R.id.ctv_person_name);

		do{
			
			//Util.BIZ_CONF_DEBUG("xxx", "child clicked");
			
			if(Util.isEmpty(childCheckTextView)) {
				
				//Util.BIZ_CONF_DEBUG("xxx", "childCheckTextView is null");
				break;					
			}
			
			childCheckTextView.toggle();
			
			part = new Participant();
			
			item  = alphabets.get(groupPosition).getContacts().get(childPosition);
			item.setCheck(childCheckTextView.isChecked());
			String value = item.getName();
			
			part.setName(value);
			
			part.setContactId(item.getId());
			
			if((item.getPhone().size()+item.getEmail().size())>1){
				List<String> phones=item.getPhone();
				final List<String> list = new ArrayList<String>();
				list.addAll(phones);
				if(isShowEmail){
					list.addAll(item.getEmail());
				}else{
					if(item.getPhone().size()==1){
						part.setSelectedAttrPosInContactItem(0);
						boolean isChecked = childCheckTextView.isChecked();
							part.setPhone(item.getPhone().get(0));
						
						
						if(!Util.isEmpty(mCallback)) {
							
							mCallback.onChecked(isChecked, part);
						}	
						notifyDataSetChanged();
						break; 
					}
				}

				Intent intent = new Intent(mCtx,PhoneAndMail.class);
				Bundle b = new Bundle();
				
				b.putStringArrayList("pam",(ArrayList<String>) list);
				b.putString("name", item.getName());
				if(item.getUri() != null){
					b.putString("path", item.getUri().toString());
				}
				intent.putExtra("phoneAndMail", b);
				((Activity)mCtx).startActivityForResult(intent, PHONEANDMAILACTIVITY);
				
//				final Dialog d=new Dialog(mCtx,R.style.mydialog);
//				d.requestWindowFeature(Window.FEATURE_NO_TITLE);
//				d.getWindow().setBackgroundDrawableResource(R.drawable.input_single);
//				d.setContentView(R.layout.phone_and_mail);
//				ListView lv = (ListView) d.findViewById(R.id.phone_and_mail_lv);
//				lv.setAdapter(new ArrayAdapter<String>(mCtx,R.layout.simple_text1,R.id.sm_contact_index_tv,list));
//				
//				lv.setOnItemClickListener(new OnItemClickListener() {
//					@Override
//					public void onItemClick(AdapterView<?> arg0, View arg1,
//							int arg2, long arg3) {
//						
//						part.setSelectedAttrPosInContactItem(arg2);
//						if(ValidatorUtil.isNumberValid(list.get(arg2))){
//							item.setSelectedType(ContactItem.TYPE_PHONE_NUMBER);
//							part.setPhone(list.get(arg2));
//						}else{
//							item.setSelectedType(ContactItem.TYPE_EMAIL_ADDRESS);
//							part.setEmail(list.get(arg2));
//						}
//						d.dismiss();
//						
//						
//						if(!Util.isEmpty(mCallback)) {
//							
//							mCallback.onChecked(true, part);
//						}	
//						
//						notifyDataSetChanged();
//						
//					}
//				});
//				d.show();
			}else{
				part.setSelectedAttrPosInContactItem(0);
				if(isShowEmail){
					if(item.getEmail().size()==0&&item.getPhone().size()==1){
						String phoneNum = item.getPhone().get(0);
						part.setPhone(phoneNum.replace("-", "w,,,,,"));
					}else if(item.getEmail().size()==1&&item.getPhone().size()==0){
						part.setEmail(item.getEmail().get(0));
					}
				} else {
					String phoneNum = item.getPhone().get(0);
					part.setPhone(phoneNum.replace("-", "w,,,,,"));
				}
				
				boolean isChecked = childCheckTextView.isChecked();
				
				if(!Util.isEmpty(mCallback)) {
					
					mCallback.onChecked(isChecked, part);
				}	
				
				notifyDataSetChanged();
			}
			
//			switch(item.getSelectedType()) {
//				
//			case ContactItem.TYPE_PHONE_NUMBER:
//				part.setPhone(value);
//				break;
//				
//			case ContactItem.TYPE_EMAIL_ADDRESS:
//				part.setEmail(value);
//				break;
//			}
			
			
			
		}while(false);
				
		return false;
	}
	
	

	public boolean onItemLongClick(AdapterView<?> arg0, View view, int arg2,
			long arg3) {
        
        int isParent = (Integer)view.getTag(R.id.bt_confirm_all);
        
        if(isParent != -1){
        	child = (Integer)view.getTag(R.id.bt_confirm_all1);
            parent = (Integer)view.getTag(R.id.bt_for_confirm);
        	Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        	intent.setType("image/*");
        	Activity act = (Activity) mCtx;
        	act.startActivityForResult(intent,GALLARYACTIVITY);
        }
        
       return false;
	}

	@Override
	public boolean onGroupClick(ExpandableListView arg0, View arg1, int arg2,
			long arg3) {
		return false;
	}	
}
