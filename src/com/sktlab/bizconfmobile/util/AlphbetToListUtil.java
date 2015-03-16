package com.sktlab.bizconfmobile.util;


import java.util.ArrayList;
import java.util.List;

public class AlphbetToListUtil {
	public static final char[] ALPHABET={'a','b','c','d','e','f','g','h','i','j','k','l'
		,'m','n','o','p','q','r','s','t','u','v','w','x','y','z','#'};
	
	public static  List<String> getAlphbet(){
		
		List<String> list=new ArrayList<String>();
		for(int i=0;i<ALPHABET.length;i++){
			list.add(ALPHABET[i]+"");
		}
		return list;
	}
}
