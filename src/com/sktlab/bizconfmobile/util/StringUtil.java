package com.sktlab.bizconfmobile.util;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class StringUtil {

	public static final String s_SYS_NEWLINE = System.getProperty("line.separator");
    
    /***
     * 统计字符串中中文，英文，数字，空格等字符个数
     * @param str 需要统计的字符串
     */
    public static int getStrLengthInChar(String str) {
    	
        /**中文字符 */
       int chCharacter = 0;
        
        /**英文字符 */
        int enCharacter = 0;
        
        /**空格 */
        int spaceCharacter = 0;
        
        /**数字 */
        int numberCharacter = 0;
        
        /**其他字符 */
        int otherCharacter = 0;
        
    	int length = -1;
    	
        if (Util.isEmpty(str)) {
            System.out.println("字符串为空");
            return length;
        }
        
        for (int i = 0; i < str.length(); i++) {
        	
            char tmp = str.charAt(i);
            
            if ((tmp >= 'A' && tmp <= 'Z') || (tmp >= 'a' && tmp <= 'z')) {
            	
                enCharacter ++;
                length++;
            } else if ((tmp >= '0') && (tmp <= '9')) {
            	
                numberCharacter ++;
                length++;
            } else if (tmp ==' ') {
            	
                spaceCharacter ++;
                length++;
            } else if (isChinese(tmp)) {
            	
                chCharacter ++;
                length += 2;
            } else {
            	
                otherCharacter ++;
                length++;
            }
        }
        
        return length;
    }
    
    /***
     * 判断字符是否为中文
     * @param ch 需要判断的字符
     * @return 中文返回true，非中文返回false
     */
    public static boolean isChinese(char ch) {
        //获取此字符的UniCodeBlock
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(ch);
        //  GENERAL_PUNCTUATION 判断中文的“号  
        //  CJK_SYMBOLS_AND_PUNCTUATION 判断中文的。号  
        //  HALFWIDTH_AND_FULLWIDTH_FORMS 判断中文的，号 
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS 
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B 
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS 
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            //System.out.println(ch + " 是中文");
            return true;
        }
        return false;
    }
	
	public static String flattenStringArray(String[] paramArrayOfString,
			String paramString) {
		StringBuilder localStringBuilder = new StringBuilder();
		String str = "";
		int i = paramArrayOfString.length;
		for (int j = 0; j < i; j++) {
			localStringBuilder.append(str);
			localStringBuilder.append(paramArrayOfString[j]);
			str = paramString;
		}
		return localStringBuilder.toString();
	}

	public static String formatMessage(String paramString,
			Object[] paramArrayOfObject) {
		StringBuilder localStringBuilder = new StringBuilder();
		formatMessage(localStringBuilder, paramString, paramArrayOfObject);
		return localStringBuilder.toString();
	}

	public static void formatMessage(StringBuilder paramStringBuilder,
			String paramString, Object[] paramArrayOfObject) {
		if (paramArrayOfObject == null)
			paramArrayOfObject = new Object[0];
		int i = 0;
		if (i < paramArrayOfObject.length) {
			String str1 = "{" + i + "}";
			if (paramArrayOfObject[i] != null)
				;
			for (String str2 = paramArrayOfObject[i].toString();; str2 = "null") {
				paramString = paramString.replace(str1, str2);
				i++;
				break;
			}
		}
		paramStringBuilder.append(paramString);
	}

	public static String getTabs(int paramInt) {
		if (paramInt < 0)
			throw new IllegalArgumentException("iCount must be positive");
		if (paramInt == 0)
			return "";
		if (paramInt == 1)
			return "\t";
		if (paramInt == 2)
			return "\t\t";
		if (paramInt == 3)
			return "\t\t\t";
		StringBuilder localStringBuilder = new StringBuilder(paramInt);
		while (paramInt > 0) {
			localStringBuilder.append('\t');
			paramInt--;
		}
		return localStringBuilder.toString();
	}

	public static final boolean hasChars(String paramString) {
		return (paramString != null) && (paramString.length() > 0);
	}

	public static int indexOf(String paramString1, String paramString2)
			throws PatternSyntaxException {
		if (paramString1 == null)
			throw new IllegalArgumentException("str cannot be null");
		if (paramString2 == null)
			throw new IllegalArgumentException("strRegex cannot be null");
		int i = -1;
		Matcher localMatcher = Pattern.compile(paramString2).matcher(
				paramString1);
		if (localMatcher.find())
			i = localMatcher.start();
		return i;
	}

	public static final boolean isAlphaNumeric(String paramString) {
		boolean bool1 = hasChars(paramString);
		boolean bool2 = false;
		if (bool1)
			bool2 = paramString.matches("[a-zA-Z0-9]*");
		return bool2;
	}

	public static final boolean isNumeric(String paramString) {
		int i = 0;
		if (paramString != null) {
			int j = paramString.length();
			i = 0;
			if (j <= 0)
				;
		}
		try {
			Double localDouble = Double
					.valueOf(Double.parseDouble(paramString));
			if (!localDouble.isNaN()) {
				boolean bool = localDouble.isInfinite();
				if (!bool) {
					return true;
				}
			}
			return false;
		} catch (NumberFormatException localNumberFormatException) {
		}
		return false;
	}

	public static String left(String paramString, int paramInt) {
		if (paramInt < 0)
			throw new IllegalArgumentException(
					"iCount must be a positive integer");
		String str = null;
		int i;
		if (paramString != null) {
			i = paramString.length();
			if (paramInt == 0)
				str = "";
		} else {
			return str;
		}
		if (paramInt >= i)
			return paramString;
		return paramString.substring(0, paramInt);
	}

	public static String padTail(String paramString, int paramInt,
			char paramChar) {
		if (paramString == null)
			paramString = "";
		int i = paramString.length();
		if (i < paramInt) {
			StringBuilder localStringBuilder = new StringBuilder(paramString);
			for (int j = paramInt - i; j > 0; j--)
				localStringBuilder.append(paramChar);
			paramString = localStringBuilder.toString();
		}
		return paramString;
	}

	public static boolean parseBoolean(String paramString)
			throws NumberFormatException {
		if (paramString == null)
			paramString = "<null>";
		String str = paramString.trim();
		if ((str.equalsIgnoreCase("y")) || (str.equalsIgnoreCase("yes"))
				|| (str.equalsIgnoreCase("t"))
				|| (str.equalsIgnoreCase("true"))
				|| (str.equalsIgnoreCase("1")))
			return true;
		if ((str.equalsIgnoreCase("n")) || (str.equalsIgnoreCase("no"))
				|| (str.equalsIgnoreCase("f"))
				|| (str.equalsIgnoreCase("false"))
				|| (str.equalsIgnoreCase("0")))
			return false;
		throw new NumberFormatException("Cannot parse boolean: " + str);
	}

	public static String removeWhitespace(String paramString) {
		if (paramString == null)
			return null;
		return paramString.replaceAll("\\s", "");
	}

	public static String right(String paramString, int paramInt) {
		if (paramInt < 0)
			throw new IllegalArgumentException(
					"iCount must be a positive integer");
		String str = null;
		int i;
		if (paramString != null) {
			i = paramString.length();
			if (paramInt == 0)
				str = "";
		} else {
			return str;
		}
		if (paramInt >= i)
			return paramString;
		return paramString.substring(i - paramInt, i);
	}

	public static String sanitizeForXML(String paramString) {
		if ((paramString == null) || (paramString.length() < 1))
			return paramString;
		StringBuilder localStringBuilder = new StringBuilder();
		StringCharacterIterator localStringCharacterIterator = new StringCharacterIterator(
				paramString);
		int i = localStringCharacterIterator.current();
		if (i != 65535) {
			if (i == 60)
				localStringBuilder.append("&lt;");
			while (true) {
				i = localStringCharacterIterator.next();

				if (i == CharacterIterator.DONE) {

					break;
				}

				if (i == 62) {
					localStringBuilder.append("&gt;");
					continue;
				}
				if (i == 38) {
					localStringBuilder.append("&amp;");
					continue;
				}
				if (i == 34) {
					localStringBuilder.append("&quot;");
					continue;
				}
				if (i == 39) {
					localStringBuilder.append("&apos;");
					continue;
				}
				localStringBuilder.append(i);
			}
		}
		return localStringBuilder.toString();
	}

}