package com.example.netty.core.j8583.parser.vo;

import com.solab.iso8583.IsoType;

public class FieldInfo {

	private int num;
	private IsoType type;
	private String attr;
	private Integer length;

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public IsoType getType() {
		return type;
	}

	public void setType(IsoType type) {
		this.type = type;
	}

	public String getAttr() {
		return attr;
	}

	public void setAttr(String attr) {
		this.attr = attr;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	@Override
	public String toString() {
		return "FieldInfo [num=" + num + ", type=" + type + ", attr=" + attr + ", length=" + length + "]";
	}
}
