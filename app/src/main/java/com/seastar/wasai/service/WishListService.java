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
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.Response;
import com.seastar.wasai.Entity.UserWishMap;
import com.seastar.wasai.utils.HttpSender;
import com.seastar.wasai.utils.StringUtil;

/**
* @ClassName: WishListService 
* @Description: 商品收藏业务类
* @author 杨腾
* @date 2015年4月8日 上午11:21:26
 */
public class WishListService {
	/**
	* @Title: addWish 
	* @Description: 添加商品收藏
	* @param @param wishListName 收藏夹名称
	* @param @param itemId 商品ID
	* @param @return
	* @return int
	* @throws
	 */
	public int addWish(String wishListName, long itemId){
		String url = InterfaceConstant.WISH;
		Map<String,String> params = new HashMap<String,String>();
		params.put("name", wishListName);
		params.put("item_id", String.valueOf(itemId));
		String json = HttpSender.doPost(url, params);
		ObjectMapper mapper = new ObjectMapper();
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
		Response response;
		try {
			JsonNode node = mapper.readTree(json);
			response = mapper.readValue(node.traverse(),Response.class);
			if(response.isSuccess()){
				JsonNode dataNode = node.get("data");
				return (Integer)mapper.readValue(dataNode.traverse(), HashMap.class).get("favorite_count");
			}
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	* @Title: deleteWish 
	* @Description: 取消收藏
	* @param @param wishListName
	* @param @param itemId
	* @param @return
	* @return int
	* @throws
	 */
	public int deleteWish(String wishListName, long itemId){
		String url = InterfaceConstant.WISH;
		Map<String,String> params = new HashMap<String,String>();
		params.put("name", wishListName);
		params.put("item_id", String.valueOf(itemId));
		String json = HttpSender.doDelete(url, params);
		ObjectMapper mapper = new ObjectMapper();
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
		Response response;
		try {
			JsonNode node = mapper.readTree(json);
			response = mapper.readValue(node.traverse(),Response.class);
			if(response.isSuccess()){
				JsonNode dataNode = node.get("data");
				return (Integer)mapper.readValue(dataNode.traverse(), HashMap.class).get("favorite_count");
			}
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	* @Title: getUserWishMapList 
	* @Description: 获取传入的商品是否被用户喜欢
	* @param @param itemIds e.g. 1_2_3
	* @param @return
	* @return List<UserWishMap>
	* @throws
	 */
	public List<UserWishMap> getUserWishMapList(String itemIds){
		List<UserWishMap> userWishMapList = null;
		if(StringUtil.isNotEmpty(itemIds)){
			String url = InterfaceConstant.WISHLIST + "/" + itemIds;
			String json = HttpSender.doGet(url, null);	
			ObjectMapper mapper = new ObjectMapper();
			mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
			Response response;
			try {
				JsonNode node = mapper.readTree(json);
				response = mapper.readValue(node.traverse(),Response.class);
				if(response.isSuccess()){
					JsonNode dataNode = node.get("data");
					userWishMapList =  mapper.readValue(dataNode.traverse(), new TypeReference<List<UserWishMap>>(){});  
				}
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return userWishMapList;
	}
}
