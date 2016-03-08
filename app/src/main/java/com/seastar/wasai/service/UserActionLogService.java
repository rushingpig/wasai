package com.seastar.wasai.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.Response;
import com.seastar.wasai.utils.HttpSender;
/**
 * 日志
 * @author Jamie
 *
 */
public class UserActionLogService {
	public boolean log(String level,String message){
		String url = InterfaceConstant.USER_ACTION_LOG;
		Map<String,String> params = new HashMap<String,String>();
		params.put("level", level);
		params.put("message", message);
		String json = HttpSender.doPost(url, params);
		ObjectMapper mapper = new ObjectMapper();
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
		Response response;
		try {
			JsonNode node = mapper.readTree(json);
			response = mapper.readValue(node.traverse(),Response.class);
			if(response.isSuccess()){
				JsonNode dataNode = node.get("data");
				return (boolean)mapper.readValue(dataNode.traverse(), Boolean.class);
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
	
}
