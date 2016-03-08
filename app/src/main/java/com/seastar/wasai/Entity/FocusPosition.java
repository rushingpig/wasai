package com.seastar.wasai.Entity;

import java.io.Serializable;


/**
* @ClassName: FocusPosition 
* @Description: 卖点
* @author 杨腾
* @date 2015年5月26日 下午3:24:21
 */
public class FocusPosition implements Serializable{
	private static final long serialVersionUID = -6048640241614762037L;
	private int temp;
	private String center;
	private String color;
	private Line lines;
	public int getTemp() {
		return temp;
	}
	public void setTemp(int temp) {
		this.temp = temp;
	}
	public String getCenter() {
		return center;
	}
	public void setCenter(String center) {
		this.center = center;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public Line getLines() {
		return lines;
	}
	public void setLines(Line lines) {
		this.lines = lines;
	}
}
