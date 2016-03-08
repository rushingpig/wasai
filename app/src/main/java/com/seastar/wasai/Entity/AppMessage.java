package com.seastar.wasai.Entity;
/**
 * @author Jamie
 */
public class AppMessage {
	public String session_id = "";
	public String visitor_source = "";
	public String network_env ="";
	public double latitude ;
	public double longitude;
	public int wasai_version;
	public long start_timestamp;
	public AppMessageChild terminal_info;
	
}
