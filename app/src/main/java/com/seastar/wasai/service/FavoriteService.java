package com.seastar.wasai.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.seastar.wasai.Entity.Favorite;
import com.seastar.wasai.Entity.Guide;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.Response;
import com.seastar.wasai.utils.HttpSender;

/**
* @ClassName: FavoriteService 
* @Description: 导购收藏业务类
* @author 杨腾
* @date 2015年4月8日 上午11:07:58
 */
public class FavoriteService {
	
	/**
	* @Title: addFavorite 
	* @Description: 添加收藏
	* @param @param guideId 导购ID
	* @param @return
	* @return int 收藏数
	* @throws
	 */
	public Long addFavorite(Long guideId){
		String url = InterfaceConstant.FAVORITE;
		Map<String,String> params = new HashMap<String,String>();
		params.put("guide_id", String.valueOf(guideId));
		
		String json = HttpSender.doPost(url, params);
		ObjectMapper mapper = new ObjectMapper();
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
		Response response;
		try {
			JsonNode node = mapper.readTree(json);
			response = mapper.readValue(node.traverse(),Response.class);
			if(response.isSuccess()){
				JsonNode dataNode = node.get("data");
				return Long.parseLong(mapper.readValue(dataNode.traverse(), HashMap.class).get("favorite_count").toString());
			}
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1l;
	}
	
	/**
	* @Title: deleteFavorite 
	* @Description: 取消收藏
	* @param @param guideId 导购ID
	* @param @return
	* @return int 收藏数
	* @throws
	 */
	public Long deleteFavorite(Long guideId){
		String url = InterfaceConstant.FAVORITE;
		Map<String,String> params = new HashMap<String,String>();
		params.put("guide_id", String.valueOf(guideId));
		
		String json = HttpSender.doDelete(url, params);
		ObjectMapper mapper = new ObjectMapper();
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
		Response response;
		try {
			JsonNode node = mapper.readTree(json);
			response = mapper.readValue(node.traverse(),Response.class);
			if(response.isSuccess()){
				JsonNode dataNode = node.get("data");
				return Long.parseLong(mapper.readValue(dataNode.traverse(), HashMap.class).get("favorite_count").toString());
			}
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1l;
	}
	
	/**
	* @Title: getFavoriteList 
	* @Description: 获取收藏列表
	* @param @param lastId 上一条ID
	* @param @param limit 条数
	* @param @return
	* @return List<Favorite>
	* @throws
	 */
	public List<Favorite> getFavoriteList(Long lastId,int limit){
		List<Favorite> favoriteList = null;
		String url = InterfaceConstant.FAVORITE + "/" + lastId + "/" + limit;
		String json = HttpSender.doGet(url, null);	
		ObjectMapper mapper = new ObjectMapper();
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
		Response response;
		try {
			JsonNode node = mapper.readTree(json);
			response = mapper.readValue(node.traverse(),Response.class);
			if(response.isSuccess()){
				JsonNode dataNode = node.get("data");
				favoriteList =  mapper.readValue(dataNode.traverse(), new TypeReference<List<Favorite>>(){});  
			}
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return favoriteList;
		
	}
	
	/**
	* @Title: getFavoriteIds 
	* @Description: 根据导购ID获取该用户是否喜欢
	* @param @param guideIds （e.g. 1_2_3）
	* @param @return
	* @return List<Guide>
	* @throws
	 */
	public List<Guide> getFavoriteIds(String guideIds){
		List<Guide> guideList = null;
		String url = InterfaceConstant.FAVORITE_GUIDE + "/" + guideIds;
		String json = HttpSender.doGet(url, null);	
		ObjectMapper mapper = new ObjectMapper();
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
		Response response;
		try {
			JsonNode node = mapper.readTree(json);
			response = mapper.readValue(node.traverse(),Response.class);
			if(response.isSuccess()){
				JsonNode dataNode = node.get("data");
				guideList =  mapper.readValue(dataNode.traverse(), new TypeReference<List<Guide>>(){});  
			}
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return guideList;
	}
}
