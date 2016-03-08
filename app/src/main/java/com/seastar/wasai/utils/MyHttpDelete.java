package com.seastar.wasai.utils;

import java.net.URI;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

/**
* @ClassName: MyHttpDelete 
* @Description: 扩展HttpDelete
* @author 杨腾
* @date 2015年4月8日 上午9:07:12
 */
public class MyHttpDelete extends HttpEntityEnclosingRequestBase {

	public static final String METHOD_NAME = "DELETE";

	public String getMethod() {
		return METHOD_NAME;
	}

	public MyHttpDelete(final String uri) {
		super();
		setURI(URI.create(uri));
	}

	public MyHttpDelete(final URI uri) {
		super();
		setURI(uri);
	}

	public MyHttpDelete() {
		super();
	}

}