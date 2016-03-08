package com.seastar.wasai.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.seastar.wasai.Entity.Guide;
import com.seastar.wasai.Entity.GuideTags;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.Response;
import com.seastar.wasai.utils.HttpSender;

/**
 * @ClassName: GuideService
 * @Description: 导购业务类
 * @author 杨腾
 * @date 2015年4月7日 下午5:49:49
 */
public class GuideService {
	static String TAG = "GuideService";

	public List<Guide> getGuideShortList(Long lastId, int isHot, int limit) {
		List<Guide> guideList = null;
		String url = InterfaceConstant.GUIDE_SHORT_LIST+ "/" + lastId + "/" + isHot + "/" + limit;
		String json = HttpSender.doGet(url, null);
		ObjectMapper mapper = new ObjectMapper();
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
		Response response;
		try {
			JsonNode node = mapper.readTree(json);
			response = mapper.readValue(node.traverse(), Response.class);
			if (response.isSuccess()) {
				JsonNode dataNode = node.get("data");
				guideList = mapper.readValue(dataNode.traverse(), new TypeReference<List<Guide>>() {
				});
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

	/**
	 * @Title: getGuideDetail
	 * @Description: 获取导购详情
	 * @param @param guideId 导购ID
	 * @param @return
	 * @return Guide
	 * @throws
	 */
	public Guide getGuideDetail(Long guideId) {
		String url = InterfaceConstant.GUIDE + "/" + guideId;
		String json = HttpSender.doGet(url, null);
		
		Log.v(TAG,"DATA:"+json);
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
		Response response;
		try {
			JsonNode node = mapper.readTree(json);
			response = mapper.readValue(node.traverse(), Response.class);
			if (response.isSuccess()) {
				JsonNode dataNode = node.get("data");
				
				return mapper.readValue(dataNode.traverse(), Guide.class);
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
