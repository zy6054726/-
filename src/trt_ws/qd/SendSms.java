/* 
 * Copyright (C), 2004-2011, 涓変簲浜掕仈绉戞妧鑲′唤鏈夐檺鍏徃
 * File Name: sms.SendSms.java
 * Encoding UTF-8 
 * Version: 1.0 
 * Date: 2012-12-7
 * History:
 * 1. Date: 2012-12-7
 *    Author: chengke1
 *    Modification: 鏂板缓
 * 2. ...
 */
package trt_ws.qd;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

/**
 * @author chengke1@35.cn
 *
 * @vesion Revison:0.01 2012-12-7
 */
public class SendSms {

	/**
	 * @description 函数的目的/功能
	 * @param args
	 * @author xucy
	 */
	public static void main(String[] args) {
		try {
			sendSMSDemo();
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void sendSMSDemo() throws HttpException, IOException{
    	HttpClient httpClient = new HttpClient();
    	String url = "http://120.24.55.238/msg/HttpBatchSendSM";
    	PostMethod postMethod = new PostMethod(url);
    	postMethod.addRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
    	String UserName = "";
    	String password = "";
    	String TimeStamp = "";
    	NameValuePair[] data = {
    	new NameValuePair("UserName",UserName),
    	new NameValuePair("TimeStamp", TimeStamp),
    	new NameValuePair("Password", password),
    	new NameValuePair("MobileNumber", "13632795153"),
    	new NameValuePair("MsgIdentify", ""),
    	new NameValuePair("MsgContent", "接口短信发送测试。即时短信发送")};
    	postMethod.setRequestBody(data);
    	System.out.println(postMethod.getRequestEntity().toString());
    	int statusCode = httpClient.executeMethod(postMethod);
    	System.out.println(statusCode);
    	String soapRequestData = postMethod.getResponseBodyAsString();
    	System.out.println(soapRequestData);
	}
}
