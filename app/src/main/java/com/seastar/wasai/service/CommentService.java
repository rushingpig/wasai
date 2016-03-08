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
import com.seastar.wasai.Entity.Comment;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.Response;
import com.seastar.wasai.utils.HttpSender;

/**
* @ClassName: CommentService 
* @Description: 评论业务类
* @author 杨腾
* @date 2015年4月8日 上午10:49:43
 */
public class CommentService {
	
	/**
	* @Title: addComment 
	* @Description: 增加评论
	* @param @param sourceType 评论类型[guide,item,focus]
	* @param @param sourceId 资源ID
	* @param @param commentInfo 评论内容
	* @param @return
	* @return Comment
	* @throws
	 */
	public Long addComment(String sourceType,Long sourceId,String commentInfo){
		String url = InterfaceConstant.COMMENT;
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("source_type", sourceType);
		params.put("source_id", String.valueOf(sourceId));
		params.put("comment_info", commentInfo);
		
		String json = HttpSender.doPost(url, params);
		ObjectMapper mapper = new ObjectMapper();
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
		Response response;
		try {
			JsonNode node = mapper.readTree(json);
			response = mapper.readValue(node.traverse(),Response.class);
			if(response.isSuccess()){
				JsonNode dataNode = node.get("data");
				return Long.parseLong(mapper.readValue(dataNode.traverse(), HashMap.class).get("comment_count").toString());  
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
	* @Title: getCommentList 
	* @Description: 获取评论列表
	* @param @param sourceType 评论类型[guide,item,focus]
	* @param @param sourceId 资源ID
	* @param @param lastId 上一条评论列表
	* @param @param limit 条数
	* @param @return
	* @return List<Comment>
	* @throws
	 */
	public List<Comment> getCommentList(String sourceType,Long sourceId,Long lastId,int limit){
		List<Comment> commentList = null;
		String url = InterfaceConstant.COMMENT + "/" + sourceType + "/" + sourceId + "/" + lastId + "/" + limit;
		String json = HttpSender.doGet(url, null);	
		ObjectMapper mapper = new ObjectMapper();
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
		Response response;
		try {
			JsonNode node = mapper.readTree(json);
			response = mapper.readValue(node.traverse(),Response.class);
			if(response.isSuccess()){
				JsonNode dataNode = node.get("data");
				commentList =  mapper.readValue(dataNode.traverse(), new TypeReference<List<Comment>>(){});  
			}
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return commentList;
	}
}
