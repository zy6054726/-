package trt_ws.message.user;

public class User {
	/**
	 * 必填参数。用户账号
	 */
	private String account;
	
	/**
	 * 必填参数。用户密码
	 */
	private String pswd;
	
	/**
	 * 必填参数。合法的手机号码，号码间用英文逗号分隔
	 */
	private String mobile;
	
	/**
	 * 必填参数。短信内容，短信内容长度不能超过585个字符。
	 * 使用URL方式编码为UTF-8格式。短信内容超过70个字符（企信通是60个字符）时，
	 * 会被拆分成多条，然后以长短信的格式发送。
	 */
	private String msg;
	
	

}
