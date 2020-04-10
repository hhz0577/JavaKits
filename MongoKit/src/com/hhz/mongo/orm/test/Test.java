package com.hhz.mongo.orm.test;

import java.util.List;

import com.hhz.kit.CheckedException;
import com.hhz.kit.FasterJacksonUtil;
import com.hhz.mongo.orm.impl.MongoDao;
import com.hhz.mongo.orm.pojo.Employee;

public class Test {
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			String uri = "mongodb://hhz:mouse@192.168.2.240:27017";
			MongoDao dao = new MongoDao(uri, "ynaddress","bzdz");
			dao.setCollection("employee");
			Employee em = new Employee();
			em.setName("hhzmouse111");
			em.setAge(40);
			String fields = "name,age";
			String filter = "name:=";
			dao.addOneData(em);
////			dao.addOneData(em);
//			String query = "name:=";
//			long count = dao.deleteData(query, em);
//			System.out.println(count);
////			dao.updateData(query, em);
			String orderstr = "[{\"direction\":\"DESC\",\"property\":\"name\"}] ";
//			List<Employee> ems = dao.findData(query, em, Employee.class);
//			Employee ee = dao.findOneData(null,em,orderstr,Employee.class,fields);
//			System.out.println(FasterJacksonUtil.ObjectToJSON(ee));
			List<Employee> ems = dao.findData(Employee.class,fields);
			for(Employee ee : ems){
				System.out.println(FasterJacksonUtil.ObjectToJSON(ee));
			}
		}catch(CheckedException e){
			e.printStackTrace();
		}
	}

}
