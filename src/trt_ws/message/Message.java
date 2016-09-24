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
				throw new Exception("�������������!");
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
	 * ������Ϣ����
	 * @param response  
	 * @return  ��Ϣ
	 */
	public static String response(String response){
		String msg = "";
		String str = response.substring(response.indexOf(",") + 1, response.lastIndexOf(",")+4);
		if(str.equals("101")){
			msg = "�޴��û�";
		}else
		if(str.equals("102")){
			msg = "�������";
		}else
		if(str.equals("103")){
			msg = "�ύ���죨�ύ�ٶȳ����������ƣ�";
		}else
		if(str.equals("104")){
			msg = "ϵͳæ����ƽ̨��ԭ����ʱ�޷������ύ�Ķ��ţ�";
		}else
		if(str.equals("105")){
			msg = "���ж��ţ��������ݰ������дʣ�";
		}else
		if(str.equals("106")){
			msg = "��Ϣ���ȴ�>536��<=0��";
		}else
		if(str.equals("107")){
			msg = "����������ֻ�����";
		}else
		if(str.equals("108")){
			msg = "�ֻ����������Ⱥ��>50000��<=0;����>200��<=0��";
		}else
		if(str.equals("109")){
			msg = "�޷��Ͷ�ȣ����û����ö�������ʹ���꣩";
		}else
		if(str.equals("110")){
			msg = "���ڷ���ʱ����";
		}else
		if(str.equals("111")){
			msg = "�������˻����·��Ͷ������";
		}else
		if(str.equals("112")){
			msg = "�޴˲�Ʒ���û�û�ж����ò�Ʒ";
		}else
		if(str.equals("113")){
			msg = "extno��ʽ�������ֻ��߳��Ȳ��ԣ�";
		}else
		if(str.equals("115")){
			msg = "�Զ���˲���";
		}else
		if(str.equals("116")){
			msg = "ǩ�����Ϸ���δ��ǩ�����û������ǩ����ǰ���£�";
		}else
		if(str.equals("117")){
			msg = "IP��ַ��֤��,������õ�IP��ַ����ϵͳ�Ǽǵ�IP��ַ";
		}else
		if(str.equals("118")){
			msg = "�û�û����Ӧ�ķ���Ȩ��";
		}else
		if(str.equals("119")){
			msg = "�û��ѹ���";
		}else{
			msg = "�ύ�ɹ�\n"+response.substring(response.indexOf(",") + 2);
		}
		
		return msg;
	}
	
	 /** 
     * ��½�������ۺ������ 
     */  
    public static boolean isPhoneLegal(String str)throws PatternSyntaxException {  
        return isChinaPhoneLegal(str)||isHKPhoneLegal(str) ; 
    }  
  
    /** 
     * ��½�ֻ�����11λ����ƥ���ʽ��ǰ��λ�̶���ʽ+��8λ������ 
     * �˷�����ǰ��λ��ʽ�У� 
     * 13+������ 
     * 15+��4�������� 
     * 18+��1��4�������� 
     * 17+��9�������� 
     * 147 
     */  
    public static boolean isChinaPhoneLegal(String str) throws PatternSyntaxException {  
        String regExp = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";  
        Pattern p = Pattern.compile(regExp);  
        Matcher m = p.matcher(str);  
        return m.matches();  
    }  
    
    /** 
     * ����ֻ�����8λ����5|6|8|9��ͷ+7λ������ 
     */  
    public static boolean isHKPhoneLegal(String str)throws PatternSyntaxException {  
        String regExp = "^(5|6|8|9)\\d{7}$";  
        Pattern p = Pattern.compile(regExp);  
        Matcher m = p.matcher(str);  
        return m.matches();  
    }  
	
	public static void main(String[] args) throws Exception {
//		String resMessage = Message.post("http://120.24.55.238/msg/HttpBatchSendSM",
//				"account=tongrentang&pswd=TRT-2014&mobile=18601968804&msg=" + URLEncoder.encode("�Ƿ���ȷ", "UTF-8") 
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
