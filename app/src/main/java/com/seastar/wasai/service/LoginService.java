package com.seastar.wasai.service;

import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.Response;
import com.seastar.wasai.utils.HttpSender;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginService {
    static String TAG = "LoginService";

    // 发送短信
    public boolean sendSms(String mob) {
        String url = InterfaceConstant.SENDSMS;
        Map<String, String> params = new HashMap<String, String>();
        params.put("mob", mob);

        String json = HttpSender.doPost(url, params);
        Log.v(TAG, "json:" + json);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        Response response;
        try {
            JsonNode node = mapper.readTree(json);
            response = mapper.readValue(node.traverse(), Response.class);
            if (response.isSuccess()) {
                JsonNode dataNode = node.get("data");
                return (boolean) mapper.readValue(dataNode.traverse(), Boolean.class);
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

    // 验证短信码
    /*
	 * return 0:匹配；1：不匹配；2：过期
	 */
    public int checkSmsCode(String mob, String smsCode) {
        String url = InterfaceConstant.CHECKSMSCODE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("mob", mob);
        params.put("sms_code", smsCode);
        String json = HttpSender.doPost(url, params);
        Log.v(TAG, "json:" + json);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        Response response;
        try {
            JsonNode node = mapper.readTree(json);
            response = mapper.readValue(node.traverse(), Response.class);
            if (response.isSuccess()) {
                JsonNode dataNode = node.get("data");
                return ((boolean) mapper.readValue(dataNode.traverse(), Boolean.class) ? 0 : 1);

            }
            String code = response.getCode();
            Log.v(TAG, "code:" + code);
            if (code.equals("10005")) {
                return 1;
            } else if (code.equals("10007")) {
                return 2;
            }
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 1;
    }

    // 检查帐号是否存在
	/*
	 * params:{
	    identify_id:   string (required)
	    identify_type: string (required)
	}
	 */
    public String checkIdExisted(String identifyId, String identifyType) {
        String url = InterfaceConstant.EXIST;
        Map<String, String> params = new HashMap<String, String>();
        Log.v(TAG, "identifyId:" + identifyId);
        Log.v(TAG, "identifyType:" + identifyType);
        params.put("identify_id", identifyId);
        params.put("identify_type", identifyType);
        String json = HttpSender.doPost(url, params);
        Log.v(TAG, "json:" + json);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        Response response;
        try {
            JsonNode node = mapper.readTree(json);
            response = mapper.readValue(node.traverse(), Response.class);
            if (response.isSuccess()) {
                JsonNode dataNode = node.get("data");
                return (String) mapper.readValue(dataNode.traverse(), String.class);
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

    // 修改密码
    public boolean alterPassword(String uuid, String pwd) {
        String url = InterfaceConstant.ALTERPWD;
        Map<String, String> params = new HashMap<String, String>();
        params.put("uuid", uuid);
        params.put("password", pwd);
        String json = HttpSender.doPost(url, params);
        Log.v(TAG, "json:" + json);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        Response response;
        try {
            JsonNode node = mapper.readTree(json);
            response = mapper.readValue(node.traverse(), Response.class);
            if (response.isSuccess()) {
                JsonNode dataNode = node.get("data");
                return (boolean) mapper.readValue(dataNode.traverse(), Boolean.class);
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
