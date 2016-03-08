package com.seastar.wasai.Entity;

import java.io.Serializable;
import java.util.Map;

/**
* @ClassName: SerializableMap 
* @Description: 自定义序列化map
* @author 杨腾
* @date 2015年4月15日 上午11:51:46
 */
public class SerializableMap implements Serializable {
	private static final long serialVersionUID = -3027730749470725471L;
	private Map<String, Object> map;

	public Map<String, Object> getMap() {
		return map;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}
}