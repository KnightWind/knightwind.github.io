package com.sktlab.bizconfmobile.util;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import com.sktlab.bizconfmobile.activity.AppClass;

public class CheckHideInput {
	public static void checkInputMethod(){
		InputMethodManager imm = 
				(InputMethodManager)AppClass.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);  
		boolean isOpen=imm.isActive();
		
		if (isOpen) {
			
			//Util.shortToast(AppClass.getInstance(), "softkeyboard open");
			
			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);  
		}
		
		
	}
}
