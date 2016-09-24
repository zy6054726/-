package trt_ws.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Message {
	public static String post(String path, String params) throws Exception {
		BufferedReader in = null;
		PrintWriter out = null;
		HttpURLConnection httpConn = null;
		try {
			URL url = new URL(path);
			httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setRequestMethod("POST");
			httpConn.setDoInput(true);
			httpConn.setDoOutput(true);
			out = new PrintWriter(httpConn.getOutputStream());
			out.println(params);
			out.flush();
			if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				StringBuffer content = new StringBuffer();
				String tempStr = "";
				in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
				while ((tempStr = in.readLine()) != null) {
					content.append(tempStr);
				}
				return content.toString();
			} else {
				throw new Exception("请求出现了问题!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			in.close();
			out.close();
			httpConn.disconnect();
		}
		return null;
	}
	
	/**
	 * 错误消息返回
	 * @param response  
	 * @return  信息
	 */
	public static String response(String response){
		String msg = "";
		String str = response.substring(response.indexOf(",") + 1, response.lastIndexOf(",")+4);
		if(str.equals("101")){
			msg = "无此用户";
		}else
		if(str.equals("102")){
			msg = "密码错误";
		}else
		if(str.equals("103")){
			msg = "提交过快（提交速度超过流速限制）";
		}else
		if(str.equals("104")){
			msg = "系统忙（因平台侧原因，暂时无法处理提交的短信）";
		}else
		if(str.equals("105")){
			msg = "敏感短信（短信内容包含敏感词）";
		}else
		if(str.equals("106")){
			msg = "消息长度错（>536或<=0）";
		}else
		if(str.equals("107")){
			msg = "包含错误的手机号码";
		}else
		if(str.equals("108")){
			msg = "手机号码个数错（群发>50000或<=0;单发>200或<=0）";
		}else
		if(str.equals("109")){
			msg = "无发送额度（该用户可用短信数已使用完）";
		}else
		if(str.equals("110")){
			msg = "不在发送时间内";
		}else
		if(str.equals("111")){
			msg = "超出该账户当月发送额度限制";
		}else
		if(str.equals("112")){
			msg = "无此产品，用户没有订购该产品";
		}else
		if(str.equals("113")){
			msg = "extno格式错（非数字或者长度不对）";
		}else
		if(str.equals("115")){
			msg = "自动审核驳回";
		}else
		if(str.equals("116")){
			msg = "签名不合法，未带签名（用户必须带签名的前提下）";
		}else
		if(str.equals("117")){
			msg = "IP地址认证错,请求调用的IP地址不是系统登记的IP地址";
		}else
		if(str.equals("118")){
			msg = "用户没有相应的发送权限";
		}else
		if(str.equals("119")){
			msg = "用户已过期";
		}else{
			msg = "提交成功\n"+response.substring(response.indexOf(",") + 2);
		}
		
		return msg;
	}
	
	 /** 
     * 大陆号码或香港号码均可 
     */  
    public static boolean isPhoneLegal(String str)throws PatternSyntaxException {  
        return isChinaPhoneLegal(str)||isHKPhoneLegal(str) ; 
    }  
  
    /** 
     * 大陆手机号码11位数，匹配格式：前三位固定格式+后8位任意数 
     * 此方法中前三位格式有： 
     * 13+任意数 
     * 15+除4的任意数 
     * 18+除1和4的任意数 
     * 17+除9的任意数 
     * 147 
     */  
    public static boolean isChinaPhoneLegal(String str) throws PatternSyntaxException {  
        String regExp = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";  
        Pattern p = Pattern.compile(regExp);  
        Matcher m = p.matcher(str);  
        return m.matches();  
    }  
    
    /** 
     * 香港手机号码8位数，5|6|8|9开头+7位任意数 
     */  
    public static boolean isHKPhoneLegal(String str)throws PatternSyntaxException {  
        String regExp = "^(5|6|8|9)\\d{7}$";  
        Pattern p = Pattern.compile(regExp);  
        Matcher m = p.matcher(str);  
        return m.matches();  
    }  
	
	public static void main(String[] args) throws Exception {
//		String resMessage = Message.post("http://120.24.55.238/msg/HttpBatchSendSM",
//				"account=tongrentang&pswd=TRT-2014&mobile=18601968804&msg=" + URLEncoder.encode("是否正确", "UTF-8") 
//				+ "&needstatus=true&product=31592860");
//		System.out.println(isPhoneLegal("133291019")==true);
		 String regExp = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";  
	        Pattern p = Pattern.compile(regExp);  
	        System.out.println(p.matcher("18601968804").matches());  
//		String resMessage = Message.post("http://120.24.55.238/msg/QueryBalance",
//				"account=tongrentang&pswd=TRT-2014");
//		System.out.println(resMessage);
//		System.out.println(response(resMessage));
	}
	public String message(){
		return null;
	}
}
