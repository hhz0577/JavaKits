package com.hhz.mongo.orm.pojo;

public class QWhere {
	private String key = "";
	private String op = "";
	private String entityfield = "";
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getOp() {
		return op;
	}
	public void setOp(String op) {
		this.op = op;
	}
	public String getEntityfield() {
		return entityfield;
	}
	public void setEntityfield(String entityfield) {
		this.entityfield = entityfield;
	}
}
