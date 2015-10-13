---
layout: post
title: "基于websocket简单聊天室实现"
keywords: ["java", "websocket","html5"]
description: "websocket基本应用"
category: "technology"
tags: ["websocket", "java","html5"]
---
{% include JB/setup %}

随着html5的发展，越来越多的浏览器支持HTML5标准， websocket技术越来越广泛的应用于各种场景。


在这基于websocket开发了一个简单的聊天室的功能，初步探究了websocket技术的使用，总结一下下websocket的基本使用步骤


服务端代码如下:


服务端主要用到以下几个注解：


@ServerEndpoint 功能主要是将目前的类定义成一个websocket服务器端。和springMVC @Contoller类似


onOpen和onClose注解分别于连接建立,关闭时触发调用的方法


onMessage方法在服务端接受到消息时触发的方法，需要注意这几个方法的一些可选参数，可以是javax.websocket.Session

如果使用该参数，则会将当前回话Session注入。 如果定义了@PathParam 也可将@PathParam作为一可选参数注入到这几个方法中


在实际使用中是极其有用的

```java

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/websocket/{client-id}")
public class ServerWebSocket  {
	
	 private static final HashMap<String,Session> clients = new HashMap<String,Session>();  
	 
     
     @OnOpen  
     public void onOpen(Session session,@PathParam("client-id") String clientId) {  
//    	 session.addMessageHandler(new MessageHandler.Whole<String>() {
//
//			@Override
//			public void onMessage(String arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//    		 
//		}); 
    	 
    	 
    	 clients.put(clientId,session);  
    	 Set<String> ids = clients.keySet();
         try {
        	StringBuilder strs = new StringBuilder("");
        	strs.append("[");
        	for(String id:ids){
        		strs.append("\"");
        		strs.append(id);
        		strs.append("\"");
        		strs.append(",");
        	}
        	if(strs.indexOf(",") > -1){
        		strs.deleteCharAt(strs.lastIndexOf(","));
        	}
        	strs.append("]");
			for(Session s : clients.values()){
				s.getBasicRemote().sendText("init:{clients:"+strs.toString()+"}");
    		}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      
     }  
     
     @OnMessage  
     public void onMessage(String message,@PathParam("client-id") String clientId) {  
    	 
    	 String sendTo = getSendToClientId(message);
    	 if(sendTo == null || !clients.containsKey(sendTo)){
    		 
	    	 for (Session client : clients.values()) {  
	             try {  
	//                 client.getBasicRemote().sendObject(clientId+": "+message);              
	                 client.getBasicRemote().sendText(clientId+": " + message + "[群发]");
	             } catch (IOException e) {  
	                 e.printStackTrace();  
	//             } catch (EncodeException e) {  
	//                 e.printStackTrace();  
	             }  
	         }  
    	 }else{
    		 
    		 Session session = clients.get(sendTo);
    		 try {
				session.getBasicRemote().sendText(clientId+": " + getMsgBody(message) );
			} catch (IOException e) {
				e.printStackTrace();
			}
    	 }
     }  
     
     @OnClose  
     public void onClose(Session peer,@PathParam("client-id") String clientId) {  
    	 
    	 System.out.println("the clientId of "+clientId);
    	 clients.remove(clientId);
//    	 if(clients.containsValue(peer)){
//    		 for(Map.Entry<String, Session> entry:clients.entrySet()){
//    			 
//    			 if(entry.getValue() == peer){
//    				 clients.remove(entry.getKey());
//    			 }
//    		 }
//    	 }
     }  
     
     
     
     public String getSendToClientId(String msg){
    	 
    	 String[] strs = msg.split("\\|");
    	 
    	 if(strs.length > 1){
    		 
    		 return strs[1];
    	 }
    	 
    	 return null;
     }
     
     public String getMsgBody(String msg){
    	 
    	 String[] strs = msg.split("\\|");
    	 
    	 if(strs.length > 1){
    		 
    		 return strs[0];
    	 }
    	 
    	 return msg;
     }
	
}
```



客户端代码如下：

```html
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">  
    <html xmlns="http://www.w3.org/1999/xhtml">  
       
    <head>  
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />  
    <title>Test</title>  
    <link href="bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" />  
    <script src="http://code.jquery.com/jquery-1.9.1.js"></script>  
    
    </head>  
    <body>  
        <div class="container">  
            <h1> WebSocket Test</h1>  
            <div>  
                <span id="status" class="label label-important">Not Connected</span>   
            </div>
			<div class="onlineinfos">
				在线用户：
				
			</div>
			
			<div>
				发送消息给：
				<select id="users">
					<option>all</option>
				<select>
				</br>
				<textarea id="content">
					
				
				</textarea>
				</br>
				<button id="sendbtn" onclick="sendMessage();">发送</button>
			</div>
            
			<br/>  
			
			
			<div id="msgarea">
			
				
			</div>
			<!--
            <label style="display:inline-block">Message: </label><input type="text" id="message"  />  
            <button id="send" class="btn btn-primary" onclick="sendMessage()">Send</button>  
       
            <table id="received_messages" class="table table-striped">  
                <tr>  
				
                    <th>#</th>  
                    <th>Sender</th>  
                    <th>Message</th>  
                </tr>  
            </table> --> 
        </div>  
    </body>  
    </html>  
      
 
  <script  type="text/javascript">
  
 
  
    var URL = "ws://localhost:8080/jkkpweb/websocket/";  
	URL += "client-"+ Math.random();
    var websocket;  
       
    $(document).ready(function(){  
        connect();    
    });  
       
    function connect(){
	
		try{
				websocket = new WebSocket(URL);  
				websocket.onopen = function(evnt) { onOpen(evnt) };  
				websocket.onmessage = function(evnt) { onMessage(evnt) };  
				websocket.onerror = function(evnt) { onError(evnt) };  
		}catch(e){
			
			alert("your bowers not support websockt");
		}
           
    }  
    function sendMessage() {         
		var msg = $("#content").val();
		if($("#users").val() != 'all'){
			msg += "|"+$("#users").val();
		}
	   websocket.send(msg);  
	   
	    $("#msgarea").append("<p> 我：  "+$("#content").val()+"</p>");
		$("#msgarea").append("<br/>");
    }  
    function onOpen() {  
        updateStatus("connected")  
    }  
    function onMessage(evnt) {  
        if (typeof evnt.data == "string") {  
			if(evnt.data.indexOf("init:") == 0){
				var str = evnt.data;
				var json = str.substring(5);
				//alert("json === " + json);				
				var initObj = eval("("+json+")");
				if(initObj.clients.length > 0){
					var html = "";
					$(".onlineinfos").empty().append("当前在线人数：");
					$("#users").empty().append("<option value=\"all\">all</option>");
					for(var i = 0; i<initObj.clients.length; i++){
					
						$(".onlineinfos").append("<label>"+initObj.clients[i]+"</label>");
						
						$("#users").append("<option value=\""+initObj.clients[i]+"\">"+initObj.clients[i]+"</option>")
					}
				
				}
				
			}else{
				 //$("#received_messages").append(  
                 //           $('<tr/>')  
                 //           .append($('<td/>').text("1"))  
                 //           .append($('<td/>').text(evnt.data.substring(0,evnt.data.indexOf(":"))))  
                 //           .append($('<td/>').text(evnt.data.substring(evnt.data.indexOf(":")+1))));  
				 
				 $("#msgarea").append("<p>"+evnt.data+"</p>");
				 $("#msgarea").append("<br/>");
			}
           
        }   
    }  
    function onError(evnt) {  
        alert('ERROR: ' + evnt.data);  
    }  
    function updateStatus(status){  
        if(status == "connected"){  
            $("#status").removeClass (function (index, css) {  
               return (css.match (/\blabel-\S+/g) || []).join(' ')  
            });  
            $("#status").text(status).addClass("label-success");  
        }  
    } 

  </script> 	

```
