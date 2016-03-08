package com.seastar.wasai.views.plugin;

/**
 * 第三方登录、分享的事件
 */
public interface PluginEvent {
	int RET_SUCCESS = 0;
	int RET_CANCEL = 1;
	int RET_ERROR = 2;
	int RET_APP_NOT_EXIST = 3;
	
	void actionResult(int status,Object data);
}
