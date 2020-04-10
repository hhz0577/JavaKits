package com.hhz.mongo.orm.pojo;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hhz.kit.fasterxml.jsonadapter.HhzDateDeserializer;
import com.hhz.kit.fasterxml.jsonadapter.HhzDateSerializer;
import com.hhz.kit.fasterxml.jsonadapter.HhzDateTimeDeserializer;
import com.hhz.kit.fasterxml.jsonadapter.HhzDateTimeSerializer;
import com.hhz.mongo.orm.annotation.NoAdd;

/**
 * 测试类
 * @author Administrator
 *
 */
public class Employee {
	@NoAdd
	private String _id;
	private String name;
	private Integer age;
	@JsonSerialize(using = HhzDateSerializer.class)
	@JsonDeserialize(using = HhzDateDeserializer.class)
	private Date birth;
	private String address;
	private String tel;
	@JsonSerialize(using = HhzDateTimeSerializer.class)
	@JsonDeserialize(using = HhzDateTimeDeserializer.class)
	private Date insertdate;
	
	@NoAdd
	private Integer page;
	@NoAdd
	private Integer size;
	
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public Integer getPage() {
		return page;
	}
	public void setPage(Integer page) {
		this.page = page;
	}
	public Integer getSize() {
		return size;
	}
	public void setSize(Integer size) {
		this.size = size;
	}
	public Date getInsertdate() {
		return insertdate;
	}
	public void setInsertdate(Date insertdate) {
		this.insertdate = insertdate;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public Date getBirth() {
		return birth;
	}
	public void setBirth(Date birth) {
		this.birth = birth;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
}
