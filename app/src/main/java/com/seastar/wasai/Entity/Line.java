package com.seastar.wasai.Entity;

import java.io.Serializable;

public class Line implements Serializable{
	private static final long serialVersionUID = 7119994214345871845L;
	private String line1;
	private String line2;
	private String line3;
	private String line4;

	public String getLine1() {
		return line1;
	}

	public void setLine1(String line1) {
		this.line1 = line1;
	}

	public String getLine2() {
		return line2;
	}

	public void setLine2(String line2) {
		this.line2 = line2;
	}

	public String getLine3() {
		return line3;
	}

	public void setLine3(String line3) {
		this.line3 = line3;
	}

	public String getLine4() {
		return line4;
	}

	public void setLine4(String line4) {
		this.line4 = line4;
	}
}