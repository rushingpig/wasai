package com.seastar.wasai.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import com.seastar.wasai.views.extendedcomponent.MyApplication;

/**
 * @ClassName: HttpSender
 * @Description: 请求工具类
 * @author 杨腾
 * @date 2015年4月7日 下午4:38:46
 */
public class HttpSender {

	public static final int CONNECTION_TIMEOUT = 5000;
	public static final int SOCKET_TIMEOUT = 15000;

	/**
	 * @Title: get
	 * @Description: GET方法获取服务端的数据
	 * @param @param url
	 * @param @return JSON
	 * @param @throws URISyntaxException
	 * @param @throws ClientProtocolException
	 * @param @throws IOException
	 * @return String
	 * @throws
	 */
	public static String doGet(String url, Map<String, String> params) {
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, SOCKET_TIMEOUT);
		DefaultHttpClient client = new DefaultHttpClient(httpParams);

		HttpGet httpGet = new HttpGet(url);
		String result = null;
		try {
			result = getDefaultJsonString();
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		try {
			Map<String, String> headerMap = getHeader();
			for (String key : headerMap.keySet()) {
				httpGet.setHeader(key, headerMap.get(key));
			}
			HttpResponse response = client.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				result = EntityUtils.toString(response.getEntity(), "utf-8");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (client != null) {
				client.getConnectionManager().shutdown();
				client = null;
			}
		}
		return result;
	}

	/**
	 * @Title: doPost
	 * @Description: 发送POST请求
	 * @param @param url
	 * @param @param params
	 * @param @param charset
	 * @param @return
	 * @return String
	 * @throws
	 */
	public static String doPost(String url, Map<String, String> tempParams) {
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, SOCKET_TIMEOUT);
		DefaultHttpClient client = new DefaultHttpClient(httpParams);
		HttpPost httpPost = new HttpPost(url);
		String result = null;
		try {
			result = getDefaultJsonString();
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		try {
			Map<String, String> headerMap = getHeader();
			for (String key : headerMap.keySet()) {
				httpPost.setHeader(key, headerMap.get(key));
			}
			LinkedList<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
			for (String key : tempParams.keySet()) {
				params.add(new BasicNameValuePair(key, tempParams.get(key)));
			}
			httpPost.setEntity(new UrlEncodedFormEntity(params, "utf-8"));

			HttpResponse response = client.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				result = EntityUtils.toString(response.getEntity(), "utf-8");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (client != null) {
				client.getConnectionManager().shutdown();
				client = null;
			}
		}
		return result;
	}

	/**
	 * @Title: doPost
	 * @Description: 发送PUT请求
	 * @param @param url
	 * @param @param params
	 * @param @param charset
	 * @param @return
	 * @return String
	 * @throws
	 */
	public static String doPut(String url, Map<String, String> tempParams) {
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, SOCKET_TIMEOUT);
		DefaultHttpClient client = new DefaultHttpClient(httpParams);
		HttpPut httpPut = new HttpPut(url);
		String result = null;
		try {
			result = getDefaultJsonString();
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		try {
			Map<String, String> headerMap = getHeader();
			for (String key : headerMap.keySet()) {
				httpPut.setHeader(key, headerMap.get(key));
			}
			LinkedList<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
			for (String key : tempParams.keySet()) {
				params.add(new BasicNameValuePair(key, tempParams.get(key)));
			}
			httpPut.setEntity(new UrlEncodedFormEntity(params, "utf-8"));

			HttpResponse response = client.execute(httpPut);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				result = EntityUtils.toString(response.getEntity(), "utf-8");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (client != null) {
				client.getConnectionManager().shutdown();
				client = null;
			}
		}
		return result;
	}

	/**
	 * @Title: doPost
	 * @Description: 发送DELETE请求
	 * @param @param url
	 * @param @param params
	 * @param @param charset
	 * @param @return
	 * @return String
	 * @throws
	 */
	public static String doDelete(String url, Map<String, String> tempParams) {
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, SOCKET_TIMEOUT);
		DefaultHttpClient client = new DefaultHttpClient(httpParams);
		MyHttpDelete httpDelete = new MyHttpDelete(url);
		String result = null;
		try {
			result = getDefaultJsonString();
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		try {
			Map<String, String> headerMap = getHeader();
			for (String key : headerMap.keySet()) {
				httpDelete.setHeader(key, headerMap.get(key));
			}

			LinkedList<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
			for (String key : tempParams.keySet()) {
				params.add(new BasicNameValuePair(key, tempParams.get(key)));
			}
			httpDelete.setEntity(new UrlEncodedFormEntity(params, "utf-8"));

			HttpResponse response = client.execute(httpDelete);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				result = EntityUtils.toString(response.getEntity(), "utf-8");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (client != null) {
				client.getConnectionManager().shutdown();
				client = null;
			}
		}
		return result;
	}
	
	/**
	* @Title: uploadBitmap 
	* @Description: 上传图片
	* @param @param url
	* @param @param formName
	* @param @param bitmap
	* @param @return
	* @return String
	* @throws
	 */
	public static String uploadBitmap(String url,String formName,Bitmap bitmap){
		String result = null;
		try {
			result = getDefaultJsonString();
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, SOCKET_TIMEOUT);
		DefaultHttpClient httpclient = new DefaultHttpClient(httpParams);

		final HttpPost httpPost = new HttpPost(url);
		final MultipartEntity multipartEntity = new MultipartEntity();

		ByteArrayOutputStream photoBao = new ByteArrayOutputStream();
		boolean successCompress = bitmap.compress(CompressFormat.JPEG, 100, photoBao);
		ByteArrayBody byteArrayBody = new ByteArrayBody(photoBao.toByteArray(),"image/jpeg", "avatar.jpg");
		if (!successCompress) {
			return null;
		}
		multipartEntity.addPart(formName, byteArrayBody);
		httpPost.setEntity(multipartEntity);
		HttpResponse httpResponse;
		try {
			Map<String, String> headerMap = getHeader();
			for (String key : headerMap.keySet()) {
				httpPost.setHeader(key, headerMap.get(key));
			}
			httpResponse = httpclient.execute(httpPost);
			final int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				result = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
			} 
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (httpclient != null) {
				httpclient.getConnectionManager().shutdown();
				httpclient = null;
			}
		}
		return result;
	}

	public static Map<String, String> getHeader() {
		Map<String, String> header = new HashMap<String, String>();
		header.put("sessionid", MyApplication.getSessionId());
		return header;
	}
	
	
	public static String getDefaultJsonString() throws JSONException{
		String str = null;
		JSONObject node = new JSONObject();
		
		node.put("data", "");
		node.put("message", "1");
		node.put("domain", "public");
		node.put("code", "1");
		
		str = node.toString();
		return str;
	}
}
