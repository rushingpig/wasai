package com.seastar.wasai.Entity;

/**
* @ClassName: Response 
* @Description: 接口返回
* @author 杨腾
* @date 2015年4月7日 下午6:52:33
 */
public class Response {
	private String code;
	private String domain;
	private String message;
	private Object data;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public boolean isSuccess(){
		return "0".equals(code);
	}
}
