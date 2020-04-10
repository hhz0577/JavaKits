package com.hhz.mongo.orm.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.hhz.kit.CheckedException;
import com.hhz.kit.ClassUtil;
import com.hhz.kit.StrUtil;
import com.hhz.mongo.orm.annotation.NoAdd;
import com.hhz.mongo.orm.pojo.QJoinWhere;
import com.hhz.mongo.orm.pojo.QOrder;
import com.hhz.mongo.orm.pojo.QWhere;

public class Utils {
	/**
	 * 转换Object到Map
	 * @param o Object
	 * @param isall 是否将为null的属性转换，false否，true是<br/> 
	 * 注解@NoAdd也不转换
	 * @return {@code Map} object
	 * @throws CheckedException
	 */
	public static Map<String,Object> convertObjectToMap(Object o,boolean isall) throws CheckedException
	{
		try 
		{
			boolean accessFlag = false;
			Map<String,Object> map = new HashMap<String, Object>();
			List<Field> fields = ClassUtil.getFields(o.getClass(),null,true);
			for(Field field : fields){
				accessFlag = field.isAccessible();  
		        if (!accessFlag) {  
		        	field.setAccessible(true);  
		        }
		        Object param = field.get(o);
		        if(!isall){
					if (param == null) {  
			        	continue;  
			        }
		        }
		        //处理注解
		        NoAdd noadd = field.getAnnotation(NoAdd.class);
		        if(noadd != null){
			        continue;
		        }
		        map.put(field.getName(), param);
		        field.setAccessible(false);
			}
			return map;
		}catch (Exception e) {
			e.printStackTrace();
			throw new CheckedException(e);
		}
	}
	/**
	 * 转换Document到Object<br/>
	 * 并将OBjectId对象转换为String对象
	 * @param doc {@code Document} object
	 * @param clazz 实体名
	 * @return {@code T} object
	 * @throws CheckedException
	 */
	public static <T> T DocToObj(Document doc,Class<T> clazz) throws CheckedException{
		try{
			if(doc == null) return ClassUtil.newInstance(clazz);
			T t = ClassUtil.newInstance(clazz);
			List<Field> fields = ClassUtil.getFields(clazz,null,true);
			boolean accessFlag = false;
			String key = "";
			for(Field field : fields){
				accessFlag = field.isAccessible();  
		        if (!accessFlag) {  
		        	field.setAccessible(true);  
		        }
		        key = field.getName();
		        if(doc.containsKey(key)){
		        	if(key.equals("_id")){//处理mongo ObjectId对象 
		        		field.set(t, ((ObjectId)doc.get("_id")).toString());
		        	}else{
		        		field.set(t, doc.get(key));
		        	}
		        }
		        field.setAccessible(false);
			}
			return t;
		}catch(Exception e){
			throw new CheckedException(e);
		}
	}
	/**
	 * 获得排序
	 * @param orderstr JSON数组[{\"direction\":\"DESC\",\"property\":\"name\"},{}] name:DESC,
	 * @return {@code Document} object
	 * @throws CheckedException
	 */
	public static Document getOrder(String orderstr) throws CheckedException{
		if(StrUtil.IsEmpty(orderstr)) return null;
		List<QOrder> qorders = new ArrayList<QOrder>();
		String[] orderbys = orderstr.split(",");
		for(String oby : orderbys){
			String[] items = oby.split(":");
			if(items.length <= 1) continue;
			if(!items[1].toUpperCase().equals("ASC")){
				if(!items[1].toUpperCase().equals("DESC")){
					continue;
				}
			}
			QOrder qorder = new QOrder();
			qorder.setProperty(items[0]);
			qorder.setDirection(items[1]);
			qorders.add(qorder);
		}
		Document orders = new Document();
		if(qorders.size() <= 0) return null;
		for(QOrder qorder : qorders){
			if(qorder.getDirection().toUpperCase().equals("ASC")){
				orders.append(qorder.getProperty(),1);
			}else{
				orders.append(qorder.getProperty(),-1);
			}
		}
		return orders;
	}
	public static Document getResultFields(String fieldstr){
		if(StrUtil.IsEmpty(fieldstr)) return null;
		Document fields = new Document();
		String[] fieldstrs = fieldstr.split(",");
		for(String s : fieldstrs){
			fields.append(s, 1);
		}
		return fields;
	}
	/**
	 * 获得条件
	 * @param filtrstr where条件字符 key#entityfield:op,key#entityfield:op 比如name:=,age:lt
	 * @param o 实体
	 * @return {@code Document} object
	 * @throws CheckedException
	 */
	public static Document getFilter(String filtrstr,Object o) throws CheckedException{
		try 
		{
			if (o == null) {
				throw new CheckedException("entity is null");  
			}
			Document filter = new Document();
			List<QJoinWhere> qwheres = getJoinOp(filtrstr);
			filter = parseJoinOP(qwheres,o);
			return filter;
			
		}catch (Exception e) {
			throw new CheckedException("获得条件，出错",e);
		}
	}
	
	/**
	 * 解析where到   {@code QWhere} object
	 * @param wherestr
	 * @return List<{@code QWhere}>
	 * @throws CheckedException
	 */
	private static List<QWhere> getOp(String wherestr) throws CheckedException{
		String[] wherestrs = wherestr.split(";");
		List<QWhere> qwhere = new ArrayList<QWhere>();
		String keys = "";
		int index=0;
		int keyindex = 0;
		for(String ws : wherestrs){
			QWhere qw = new QWhere();
			keyindex = 0;
			index = ws.indexOf(":");
			keys = ws.substring(0, index);
			if(keys.contains("#")){
				keyindex = keys.indexOf("#");
				qw.setKey(keys.substring(0, keyindex));
				qw.setEntityfield(keys.substring(keyindex + 1, keys.length()));
			}else{
				qw.setKey(keys);
				qw.setEntityfield(keys);
			}
			qw.setOp(ws.substring(index + 1, ws.length()));
			qwhere.add(qw);
		}
		return qwhere;
	}
	private static List<QJoinWhere> getJoinOp(String wherestr) throws CheckedException{
		String[] wherestrs = wherestr.split(",");
		List<QJoinWhere> qwhere = new ArrayList<QJoinWhere>();
		for(String ws : wherestrs){
			QJoinWhere qjoin = new QJoinWhere();
			String[] joins = ws.split("&");
			qjoin.setJkey(joins[0]);
			List<QWhere> qwheres= getOp(joins[1]);
			qjoin.setWheres(qwheres);
			qwhere.add(qjoin);
		}
		return qwhere;
	}
	/**
	 * 解析where操作<br/>
	 * regex as like<br/>
	 * in as 包含<br/>
	 * gt as ><br/>
	 * gte as >=<br/>
	 * lt as <<br/>
	 * lte as <=<br/>
	 * @param ct {@code Document} object
	 * @param qwhere {@code QWhere} object
	 * @param v {@code Object} object
	 * @return {@code Document} object
	 */
	private static Document parseOP(QWhere qwhere,Object v){
		Document ct = new Document();
		if(qwhere.getOp().equals("=")){
			ct.append(qwhere.getKey(), v);
		}else if(qwhere.getOp().equals("regex")){
			ct.append(qwhere.getKey(), new Document("$regex", v));
		}else if(qwhere.getOp().equals("in")){
			ct.append(qwhere.getKey(), new Document("$in", v));
		}else if(qwhere.getOp().equals("gt")){
			ct.append(qwhere.getKey(), new Document("$gt", v));
		}else if(qwhere.getOp().equals("gte")){
			ct.append(qwhere.getKey(), new Document("$gte", v));
		}else if(qwhere.getOp().equals("lt")){
			ct.append(qwhere.getKey(), new Document("$lt", v));
		}else if(qwhere.getOp().equals("lte")){
			ct.append(qwhere.getKey(), new Document("$lte", v));
		}
		return ct;
	}
	
	private static Document parseJoinOP(List<QJoinWhere> qwheres,Object v) throws CheckedException{
		Document ct = new Document();
		for(QJoinWhere joinw : qwheres){
			List<Document> docs = new ArrayList<Document>();
			for(QWhere qw : joinw.getWheres()){
				Object o = getObjvalue(qw.getEntityfield(),v);
				Document tmpct = parseOP(qw,o);
				docs.add(tmpct);
			}
			ct.append("$" + joinw.getJkey(), docs);
		}
		return ct;
	}
	private static Object getObjvalue(String fieldname,Object v) throws CheckedException{
		try{
			boolean accessFlag = false;
			Object result = new Object() ;
			List<Field> fields = ClassUtil.getFields(v.getClass(),null,true);
			for(Field field : fields){
				accessFlag = field.isAccessible();  
		        if (!accessFlag) {  
		        	field.setAccessible(true);  
		        }
		        Object param = field.get(v);
		        if (param == null) {  
		        	continue;
		        }
		        if (param instanceof Integer) {
		        	int value = ((Integer) param).intValue();
		        	if(field.getName().toLowerCase().equals(fieldname.toLowerCase())){
		        		result = value;
		        	}
		        }else if (param instanceof String) {
		        	String value = (String) param;
		        	if(field.getName().toLowerCase().equals(fieldname.toLowerCase())){
		        		result = value;
		        	}
		        }else if (param instanceof Date) {
		        	Date value = (Date) param;
		        	if(field.getName().toLowerCase().equals(fieldname.toLowerCase())){
		        		result = value;
		        	}
		        }else if (param instanceof Boolean) {
		        	boolean value = ((Boolean) param).booleanValue();
		        	if(field.getName().toLowerCase().equals(fieldname.toLowerCase())){
		        		result = value;
		        	}
		        }else if (param instanceof Double) {
		        	Double value = ((Double) param).doubleValue();
		        	if(field.getName().toLowerCase().equals(fieldname.toLowerCase())){
		        		result = value;
		        	}
		        }else if (param instanceof Float) {
		        	Float value = ((Float) param).floatValue();
		        	if(field.getName().toLowerCase().equals(fieldname.toLowerCase())){
		        		result = value;
		        	}
		        }else if (param instanceof Long) {
		        	Long value = ((Long) param).longValue();
		        	if(field.getName().toLowerCase().equals(fieldname.toLowerCase())){
		        		result = value;
		        	}
		        }else if (param instanceof ObjectId) {
		        	ObjectId value = (ObjectId) param;
		        	if(field.getName().toLowerCase().equals(fieldname.toLowerCase())){
		        		result = value;
		        	}
		        }
		        field.setAccessible(false);
			}
			return result;
		}catch (Exception e) {
			throw new CheckedException("获得条件，出错",e);
		}
	}
	
}
