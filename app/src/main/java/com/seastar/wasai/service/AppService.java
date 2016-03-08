package com.seastar.wasai.service;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.Response;
import com.seastar.wasai.Entity.Upgrade;
import com.seastar.wasai.utils.HttpSender;

/**
 * @ClassName: AppService
 * @Description:
 * @author 杨腾
 * @date 2015年5月13日 下午2:57:07
 */
public class AppService {
	
	public Upgrade getVersion(String appType) {
		Upgrade upgrade = null;
		String url = InterfaceConstant.GET_UPDATE_VERSION + "/" + appType;
		String json = HttpSender.doGet(url, null);
		ObjectMapper mapper = new ObjectMapper();
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
		Response response;
		try {
			JsonNode node = mapper.readTree(json);
			response = mapper.readValue(node.traverse(), Response.class);
			if (response.isSuccess()) {
				JsonNode dataNode = node.get("data");
				upgrade = mapper.readValue(dataNode.traverse(), Upgrade.class);
			}
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return upgrade;
	}
}
