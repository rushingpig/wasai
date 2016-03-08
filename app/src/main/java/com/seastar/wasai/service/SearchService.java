package com.seastar.wasai.service;

import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.seastar.wasai.Entity.Guide;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.Item;
import com.seastar.wasai.Entity.KeywordEntity;
import com.seastar.wasai.Entity.Response;
import com.seastar.wasai.utils.HttpSender;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by Jamie on 2015/6/9.
 */
public class SearchService {
    public List<KeywordEntity> getkeywordList(String hot,int limit) {
        List<KeywordEntity> list = null;
        String url = InterfaceConstant.KEYWORD_LIST + "/" + hot + "/" + limit;
        String json = HttpSender.doGet(url, null);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        Response response;
        try {
            JsonNode node = mapper.readTree(json);
            response = mapper.readValue(node.traverse(), Response.class);
            if (response.isSuccess()) {
                JsonNode dataNode = node.get("data");
                list = mapper.readValue(dataNode.traverse(), new TypeReference<List<KeywordEntity>>() {
                });
            }
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }


    public List<Guide> getGuideList(String type,String keyword,Long offset,int limit) {
        List<Guide> guideList = null;
        try {
            keyword = URLEncoder.encode(keyword,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = InterfaceConstant.SEARCH_LIST + "/" + type + "/" + keyword + "/" + offset + "/" + limit;
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


    public List<Item> getProductList(String type,String keyword,Long offset,int limit) {
        List<Item> guideList = null;
        try {
            keyword = URLEncoder.encode(keyword,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = InterfaceConstant.SEARCH_LIST + "/" + type + "/" + keyword + "/" + offset + "/" + limit;
        String json = HttpSender.doGet(url, null);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        Response response;
        try {
            JsonNode node = mapper.readTree(json);
            response = mapper.readValue(node.traverse(), Response.class);
            if (response.isSuccess()) {
                JsonNode dataNode = node.get("data");
                guideList = mapper.readValue(dataNode.traverse(), new TypeReference<List<Item>>() {
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
}
