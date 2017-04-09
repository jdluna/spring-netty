package com.example.netty.core.j8583.parser;

import java.util.HashMap;
import java.util.Map;

public class MessageInfo {

	private int type;
	private Class<?> clazz;
	private Map<Integer, FieldInfo> fields = new HashMap<Integer, FieldInfo>();

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Map<Integer, FieldInfo> getFields() {
		return fields;
	}

	public void setFields(Map<Integer, FieldInfo> fields) {
		this.fields = fields;
	}
}
