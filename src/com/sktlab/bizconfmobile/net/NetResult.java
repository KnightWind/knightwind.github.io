package com.sktlab.bizconfmobile.net;

import java.util.HashMap;

public class NetResult {
	public String url="";//请求的url
	
	public String content="";//返回的内容，一般为xml或者json文本
	public String message="";//返回的消息
	public int code = -1;//返回码
	public int statusCode = 0;//http返回码
	public long timeout;
	public HashMap<String,Object> requestParams = new HashMap<String,Object>();//请求的参数
	public HashMap<String,Object> responseParams = new HashMap<String,Object>();//返回的参数
	
	
	//这个方法其实是为了兼容前台面对不同服务器统一传参的 转换参数用
	//比如前台一个需要获取下一本书，如果要连两个服务器，一个要求用当前书的id做参数，一个要求用当前书的上架时间做参数	
	//不管如何，参数都是放进一个map里，前台页面中，把两个参数都丢进去,有空值也无所谓。
	//这个map传到实现的各自server类中，各自取各自参数，不会乱。
	//这一点其实是我发现 新浪微博和腾讯微博，同样类型的方法，两家却要求截然不同的参数，的一个处理方案。
	public String getStringValueOfRequest(String key){
		if (requestParams==null)
			return "null";
		if (!requestParams.containsKey(key))
			return "no key";
		return (String)(requestParams.get(key));
	};

	public String getStringValueOfResponse(String key){
		if (responseParams==null)
			return "null";
		if (!responseParams.containsKey(key))
			return "no key";
		return (String)(responseParams.get(key));
	}
	
	public void setRequestItem(String key, Object object){
		requestParams.put(key, object);
	}
	
	public void setResponseItem(String key, Object object){
		responseParams.put(key, object);
	}

	public Object getList(){
		return responseParams.get("list");
	}
	
	public void setList(Object value){
		responseParams.put("list", value);
	}
	
	public void setReady(String url, HashMap<String,Object> params){
		this.url = url;
		this.requestParams = params;
	}
}
