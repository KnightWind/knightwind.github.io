package com.sktlab.bizconfmobile.adapter;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.activity.AppClass;
import com.sktlab.bizconfmobile.activity.ConferenceActivity;
import com.sktlab.bizconfmobile.model.ConfControl;
import com.sktlab.bizconfmobile.model.ConfAccount;
import com.sktlab.bizconfmobile.model.Participant;
import com.sktlab.bizconfmobile.model.manager.CommunicationManager;
import com.sktlab.bizconfmobile.model.manager.ContactManager;
import com.sktlab.bizconfmobile.util.Util;

public class PartyListAdapter extends BaseExpandableListAdapter {
	
	private Context mCtx;
	private PartyListAdapter mMe;
	private LayoutInflater mInflater;
	private List<Participant> mParticipants;
	private ConferenceActivity mHolder;
	
	public PartyListAdapter(Context ctx,List<Participant> data){
		
		mMe = this;
		mCtx = ctx;
		mParticipants = data;
		mInflater = (LayoutInflater)mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void setData(List<Participant> data) {
		
		mParticipants.clear();
		mParticipants.addAll(data);
		notifyDataSetChanged();
	}
	
	@Override
	public int getGroupCount() {
		return mParticipants.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		
		return mParticipants.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		
		return null;
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
			
			groupHolder.tvPersonName = (TextView) convertView.findViewById(R.id.tv_person_name);			
			
			convertView.setTag(groupHolder);
		}else {
			
			groupHolder = (GroupHolder)convertView.getTag();
		}
		
		Participant party = mParticipants.get(groupPosition);
		
		String name = party.getName().replace("w,,,,,", "-");
		String phone = party.getPhone().replace("w,,,,,", "-");
		
		if (party.isMuted()) {
			
			Drawable muteState = mCtx.getResources().getDrawable(R.drawable.home_conf_party_mute);		
			muteState.setBounds(0, 0, 36, 35);
			//use this method draw a picture on the right of the text			
			groupHolder.tvPersonName.setCompoundDrawables(null, null, muteState, null);
			
		}else {
			
			if (party.isTalking()) {
				
				Drawable talking = mCtx.getResources().getDrawable(R.drawable.party_talking);		
				talking.setBounds(0, 0, 36, 35);
				//use this method draw a picture on the right of the text			
				groupHolder.tvPersonName.setCompoundDrawables(null, null, talking, null);
			}else {
				
				groupHolder.tvPersonName.setCompoundDrawables(null, null, null, null);
			}
		}	
		
		do{
			
			if(!Util.isEmpty(name)) {
				
				groupHolder.tvPersonName.setText(name);
				break;
			}
			
			if(!Util.isEmpty(phone)) {
				
				groupHolder.tvPersonName.setText(phone);
				break;
			}			
		}while(false);
				
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		
		ChildHodler childHolder = null;
		
		if(convertView == null) {
			
			childHolder = new ChildHodler();
			
			convertView = mInflater.inflate(R.layout.item_expand_listview_participant_child, null);		
			childHolder.ctvDisconnnect = (CheckedTextView) convertView.findViewById(R.id.ctv_disconnect);
			childHolder.ctvMute = (CheckedTextView) convertView.findViewById(R.id.ctv_mute);
			childHolder.ctvRename = (CheckedTextView) convertView.findViewById(R.id.ctv_rename);
			
			convertView.setTag(childHolder);
		}else {
			
			childHolder = (ChildHodler)convertView.getTag();
		}
			
		final Participant item = mParticipants.get(groupPosition);
		
		if (item.isMuted()) {
			
			childHolder.ctvMute.setChecked(true);
		}else {
			
			childHolder.ctvMute.setChecked(false);
		}
		
		ConfAccount activeAccount = CommunicationManager.getInstance().getActiveAccount();
		
		if (Util.isEmpty(activeAccount.getModeratorPw())) {
			
			childHolder.ctvDisconnnect.setEnabled(false);
			childHolder.ctvMute.setEnabled(false);
			
			childHolder.ctvDisconnnect.setVisibility(View.GONE);
			childHolder.ctvMute.setVisibility(View.GONE);
		}else{
			
			childHolder.ctvDisconnnect.setEnabled(true);
			childHolder.ctvMute.setEnabled(true);
			
			childHolder.ctvDisconnnect.setVisibility(View.VISIBLE);
			childHolder.ctvMute.setVisibility(View.VISIBLE);
		}
		
		childHolder.ctvDisconnnect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				do{
					
					if (!Util.isNetworkReadyForConf(mCtx)) {
						
						Util.shortToast(mCtx, R.string.toast_network_not_ready);
						break;
					}
					
					ConfControl.getInstance().disconnectParty(item);
					mParticipants.remove(item);
					ContactManager.getInstance().removeSelectedContact(item);
					mMe.notifyDataSetChanged();
				}while(false);
			}
		});
		
		
		childHolder.ctvMute.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				do {
					
					if (!Util.isNetworkReadyForConf(mCtx)) {
						
						Util.shortToast(mCtx, R.string.toast_network_not_ready);
						break;
					}
					
					List<Participant> data = CommunicationManager.getInstance().getAllParties();
					
					if (null == data || data.size() < 2) {

						Util.shortToast(mCtx, R.string.toast_mute_self_unable);
						break;
					}
					
					CheckedTextView ctv = (CheckedTextView) v;
					
					//ConfControl.getInstance().muteParty(item, state, playMessage);
					ctv.toggle();
					
					if(ctv.isChecked()) {
						
						ConfControl.getInstance().muteParty(item, 0, 1);
						item.setMuted(true);
					}else {
						
						item.setMuted(false);
						ConfControl.getInstance().muteParty(item, 1, 1);
					}	
					
					mMe.notifyDataSetChanged();
					
					if (!Util.isEmpty(mHolder)) {
						
						mHolder.setMutedParty(item);
						mHolder.setOperateModule(CommunicationManager.PARTY_LIST_MODULE);
					}
				}while(false);
			}
		});
		childHolder.ctvRename.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
//				final EditText etForInput = new EditText(mCtx);  
//
//		        new AlertDialog.Builder(mCtx).setTitle(R.string.bt_rename)  
//		                .setIcon(android.R.drawable.ic_dialog_info).setView(etForInput)  
//		                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {  
//		                    @Override  
//		                    public void onClick(DialogInterface arg0, int arg1) {  
//		                    	
//		                    	do {
//		                    		
//		                    		if (Util.isEmpty(etForInput.getText().toString())) {
//		                    			
//		                    			Util.shortToast(mCtx, R.string.toast_input_name_not_null);
//			                    		break;
//			                    	}
//		                    		
//			                    	item.setName(etForInput.getText().toString());
//			                    	
//			                    	mMe.notifyDataSetChanged();
//		                    	}while(false);
//		                    	
//		                    }  
//		                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//							
//							@Override
//							public void onClick(DialogInterface dialog, int which) {
//
//							}
//						}).show();
				final Dialog d=new Dialog(mCtx);
				d.requestWindowFeature(Window.FEATURE_NO_TITLE);
				//d.requestWindowFeature(Window.)
				BitmapDrawable bd=new BitmapDrawable();
				bd.setAlpha(0);
				d.getWindow().setBackgroundDrawable(bd);
				d.setContentView(R.layout.party_rename);
				final EditText et = (EditText) d.findViewById(R.id.party_rename_et1);
				Button cancel = (Button) d.findViewById(R.id.party_rename_bt1);
				Button ok = (Button) d.findViewById(R.id.party_rename_bt2);
				
				cancel.setOnClickListener(new OnClickListener() {
					
					public void onClick(View arg0) {
						d.dismiss();
					}
				});
				
				ok.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						
						do {
							
							if (Util.isEmpty(et.getText().toString().trim())) {
	                			
	                			Util.shortToast(mCtx, R.string.toast_input_name_not_null);
	                			break;
	                    	}
							
	                    	item.setName(et.getText().toString().trim());
	                    	mMe.notifyDataSetChanged();
	                    	d.dismiss();	                    	
						}while(false);
					}
				});
				d.show();
			}
		});
		
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		
		return true;
	}

	public ConferenceActivity getHolder() {
		return mHolder;
	}

	public void setHolder(ConferenceActivity mHolder) {
		this.mHolder = mHolder;
	}

	public class GroupHolder{
		
		private TextView tvPersonName;
	}
	
	public class ChildHodler{
		
		private CheckedTextView ctvDisconnnect;
		private CheckedTextView ctvMute;
		private CheckedTextView ctvRename;
	}
}
