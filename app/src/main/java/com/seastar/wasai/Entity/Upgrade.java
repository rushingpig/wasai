package com.seastar.wasai.Entity;

public class Upgrade {
	private boolean needUpdate;
	private int ver;
	private String verString;
	private String url;
	private String description;
	private String logo;
	private String osRequire;
	private int force;
    
    public boolean getNeedUpdate() {
		return needUpdate;
	}
	public void setNeedUpdate(boolean needUpdate) {
		this.needUpdate = needUpdate;
	}
	public int getVer() {
		return ver;
	}
	public void setVer(int ver) {
		this.ver = ver;
	}
	public String getVerString() {
		return verString;
	}
	public void setVerString(String verString) {
		this.verString = verString;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getOsRequire() {
		return osRequire;
	}
	public void setOsRequire(String osRequire) {
		this.osRequire = osRequire;
	}
	public void setForce(int force){
		this.force = force;
	}
	public int getForce(){
		return this.force;
	}
}
