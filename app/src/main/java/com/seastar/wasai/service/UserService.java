package com.seastar.wasai.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.Response;
import com.seastar.wasai.Entity.Upgrade;
import com.seastar.wasai.Entity.User;
import com.seastar.wasai.utils.HttpSender;
import com.seastar.wasai.views.extendedcomponent.MyApplication;

/**
 * @ClassName: UserService
 * @Description: 用户业务类
 * @author 杨腾
 * @date 2015年4月8日 上午11:45:08
 */
public class UserService {
	static String TAG = "UserService";

	/**
	* @Title: updateNickName 
	* @Description: 修改昵称
	* @param @param nickName
	* @param @return
	* @return Long
	* @throws
	 */
	public boolean updateNickName(String nickName){
		String url = InterfaceConstant.UPDATE_NICKNAME;
		Map<String,String> params = new HashMap<String,String>();
		params.put("nickname", nickName);
		String json = HttpSender.doPost(url, params);
		ObjectMapper mapper = new ObjectMapper();
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
		Response response;
		try {
			JsonNode node = mapper.readTree(json);
			response = mapper.readValue(node.traverse(),Response.class);
			if(response.isSuccess()){
				JsonNode dataNode = node.get("data");
				return mapper.readValue(dataNode.traverse(), Boolean.class);
			}
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	} 
	
	/**
	* @Title: updateNickName 
	* @Description: 修改昵称
	* @param @param nickName
	* @param @return
	* @return Long
	* @throws
	 */
	public String uploadAvatar(Bitmap bitmap){
		String url = InterfaceConstant.UPLOAD_AVATAR;
		String json = HttpSender.uploadBitmap(url, "avatar", bitmap);
		ObjectMapper mapper = new ObjectMapper();
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
		Response response;
		try {
			JsonNode node = mapper.readTree(json);
			response = mapper.readValue(node.traverse(),Response.class);
			if(response.isSuccess()){
				JsonNode dataNode = node.get("data");
				return mapper.readValue(dataNode.traverse(), String.class);
			}
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	} 
}
