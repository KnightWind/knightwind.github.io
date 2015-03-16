package com.sktlab.bizconfmobile.model.phone;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.sktlab.bizconfmobile.util.Util;

import android.os.Vibrator;
import android.telephony.TelephonyManager;

public class PhoneListenThread implements Runnable {  
	
	public static final String TAG = "PhoneListenThread";
    //振动器  
    Vibrator mVibrator;  
    //电话服务  
    TelephonyManager telManager;  
    
    public PhoneListenThread(Vibrator mVibrator, TelephonyManager telManager) {  
        this.mVibrator = mVibrator;  
        this.telManager = telManager;  
    }  
    @Override  
    public void run() {  
        //获取当前话机状态  
        int callState = telManager.getCallState();  
        Util.BIZ_CONF_DEBUG(TAG, "开始.........." + Thread.currentThread().getName());  
        //记录拨号开始时间  
        long threadStart = System.currentTimeMillis();  
        Process process;  
        InputStream inputstream;  
        BufferedReader bufferedreader;  
        try {  
        	Util.BIZ_CONF_DEBUG(TAG, "in try catch");
        	/********TEST CODE*******/
//        	ArrayList<String> commandLine = new ArrayList<String>();   
//            commandLine.add( "logcat");    
//            commandLine.add("-d");
//            commandLine.add( "-v");   
//            commandLine.add( "time");   
//            commandLine.add( "-b");   
//            commandLine.add( "radio");   
//            process = Runtime.getRuntime().exec( commandLine.toArray( new String[commandLine.size()]));  
//        	 String command = "logcat -d -v time -s tag:W";
//        	 process = Runtime.getRuntime().exec( command );
//             BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(process.getInputStream()), 1024);  
//             String line = bufferedReader.readLine();  
//             StringBuffer log = new StringBuffer();
//             
//             int i = 0;
//            while ( line != null) {   
//                log.append(line);   
//                log.append("\n");
//                i++;
//                
//                if (i > 5) {
//                	break;
//                }
//            }   
//            
//            Util.BIZ_CONF_DEBUG(TAG, "log: " + log.toString());
            
        	/********TEST CODE*******/
        	 
        	
            process = Runtime.getRuntime().exec("logcat -v");  
            
            Util.BIZ_CONF_DEBUG(TAG, "exe log cat");
            
            inputstream = process.getInputStream();  
            InputStreamReader inputstreamreader = new InputStreamReader(  
                    inputstream);  
            bufferedreader = new BufferedReader(inputstreamreader);  
            String str = "";  
            long dialingStart = 0;  
            boolean enableVibrator = false;  
            boolean isAlert = false;  
            
            Util.BIZ_CONF_DEBUG(TAG, "before while loop"); 
          
            while ((str = bufferedreader.readLine()) != null) {  
            	
            	//Util.BIZ_CONF_DEBUG(TAG, "begin while loop");
            	
                //如果话机状态从摘机变为空闲,销毁线程  
                if (callState == TelephonyManager.CALL_STATE_OFFHOOK  
                        && telManager.getCallState() == TelephonyManager.CALL_STATE_IDLE) {  
                    break;  
                }  
                // 线程运行1分钟自动销毁  
                if (System.currentTimeMillis() - threadStart > 60000) {  
                    break;  
                }  
               // Util.BIZ_CONF_DEBUG(TAG, Thread.currentThread().getName() + ":"   + str);  
                // 记录GSM状态DIALING  
                if (str.contains("GET_CURRENT_CALLS")  
                        && str.contains("DIALING")) {  
                    // 当DIALING开始并且已经经过ALERTING或者首次DIALING  
                    if (!isAlert || dialingStart == 0) {  
                        //记录DIALING状态产生时间  
                        dialingStart = System.currentTimeMillis();  
                        isAlert = false;  
                    }  
                    continue;  
                }  
                if (str.contains("GET_CURRENT_CALLS")  
                        && str.contains("ALERTING")&&!enableVibrator) {  
                      
                    long temp = System.currentTimeMillis() - dialingStart;  
                    isAlert = true;  
                    //这个是关键,当第一次DIALING状态的时间,与当前的ALERTING间隔时间在1.5秒以上并且在20秒以内的话  
                    //那么认为下次的ACTIVE状态为通话接通.  
                    if (temp > 1500 && temp < 20000) {  
                        enableVibrator = true;  
                        Util.BIZ_CONF_DEBUG(TAG, "间隔时间....." + temp + "....."  
                                + Thread.currentThread().getName());  
                    }  
                    continue;  
                }  
                if (str.contains("GET_CURRENT_CALLS") && str.contains("ACTIVE")  
                        && enableVibrator) {  
                	
                	Util.BIZ_CONF_DEBUG(TAG, "接通啦~~~~");
                    mVibrator.vibrate(100);  
                    enableVibrator = false;  
                    break;  
                }  
            }  
            Util.BIZ_CONF_DEBUG(TAG, "结束.........."  
                    + Thread.currentThread().getName());  
        } catch (Exception e) {  
        	
        	Util.BIZ_CONF_DEBUG(TAG, "catch exception: " + e.getMessage());
            // TODO: handle exception  
        }  
        
        Util.BIZ_CONF_DEBUG(TAG, "method ended");
    }  
}  

