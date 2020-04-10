package com.hhz.mongo.orm.pojo;

import java.util.List;

public class QJoinWhere {
	private String jkey = "";
	private List<QWhere> wheres;
	public String getJkey() {
		return jkey;
	}
	public void setJkey(String jkey) {
		this.jkey = jkey;
	}
	public List<QWhere> getWheres() {
		return wheres;
	}
	public void setWheres(List<QWhere> wheres) {
		this.wheres = wheres;
	}
}
