package com.sktlab.bizconfmobile.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class GetFirstSpell {
	 public static String getFirstSpell(String chinese) {
         StringBuffer pybf = new StringBuffer();   
         char arr = chinese.trim().charAt(0);
         HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();   
         defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);   
         defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);   
         if (arr > 128) {   
             try {   
                     String[] temp = PinyinHelper.toHanyuPinyinStringArray(arr, defaultFormat);   
                     if (temp != null) {   
                             pybf.append(temp[0].charAt(0));   
                     }   
             } catch (BadHanyuPinyinOutputFormatCombination e) {   
                     e.printStackTrace();   
             }   
     } else {   
    	 	 if(arr >= 'A' && arr <= 'Z'){
    	 		 arr = (char)(arr+32);
    	 	 }
             pybf.append(arr);   
     }           
         String firstSpell=pybf.toString();
         if(firstSpell.isEmpty() || !new String(AlphbetToListUtil.ALPHABET).contains(firstSpell)){
        	 firstSpell = "#";
         }
         return firstSpell;   
 }   
}
