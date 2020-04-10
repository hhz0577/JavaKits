package com.hhz.mongo.orm.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.hhz.kit.CheckedException;
import com.hhz.kit.StrUtil;
import com.hhz.mongo.orm.util.Utils;
import com.mongodb.Block;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MapReduceIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

public class MongoDao {
	private MongoDatabase mdb;
	private MongoMan man;
	private MongoCollection<Document> mcol;
	/**
	 * 
	 * @param uristr MongoDB uri
	 * @param dbname 数据库名称
	 * @throws CheckedException
	 */
	public MongoDao(String uristr,String dbname) throws CheckedException{
		man = new MongoMan(uristr);
		mdb = man.getDataBase(dbname);
	}
	public MongoDao(String uristr,String dbname,String collectionName) throws CheckedException{
		man = new MongoMan(uristr);
		mdb = man.getDataBase(dbname);
		setCollection(collectionName);
	}
	/**
	 * 获得管理类，进行底层操作
	 * @return
	 */
	public MongoMan getMan(){
		return man;
	}
	/**
	 * 设置MongoDatabase
	 * @param dbname 数据库名称
	 * @throws CheckedException
	 */
	public void getDB(String dbname) throws CheckedException{
		mdb = man.getDataBase(dbname);
	}
	/**
	 * 获得数据库名称
	 * @return
	 * @throws CheckedException
	 */
	public String getDBName() throws CheckedException{
		hasDB();
		return mdb.getName();
	}
	/**
	 * 添加集合
	 * @param collectionName
	 * @throws CheckedException
	 */
	public void addCollection(String collectionName) throws CheckedException{
		hasDB();
		mdb.createCollection(collectionName);
	}
	/**
	 * 获得数据库里的所有集合名称
	 * @return
	 * @throws CheckedException
	 */
	public List<String> getCollectionNames() throws CheckedException{
		hasDB();
		try{
			List<String> cnames = new ArrayList<String>();
			MongoIterable<String> ite = mdb.listCollectionNames();
			for(Iterator<String> it = ite.iterator();it.hasNext();){
				cnames.add(it.next());
			}
			return cnames;
		}catch(Exception e){
			throw new CheckedException(e);
		}
	}
	/**
	 * 根据名称判断集合是否存在
	 * @param collectionName
	 * @return
	 * @throws CheckedException
	 */
	public boolean hasCollection(String collectionName) throws CheckedException{
		hasDB();
		List<String> cnames = getCollectionNames();
		for(String s : cnames){
			if(s.equals(collectionName)) return true;
		}
		return false;
	}
	/**
	 * 设置集合
	 * @param collectionName 集合名称
	 * @throws CheckedException
	 */
	public void setCollection(String collectionName) throws CheckedException{
		hasDB();
		if(!hasCollection(collectionName)){
			addCollection(collectionName);
		}
		mcol = mdb.getCollection(collectionName);
	}
	/**
	 * 执行数据命令
	 * @param cmdjson 命令JSON
	 * @return
	 */
	public String runCommand(String cmdjson){
		Document doc = Document.parse(cmdjson);
		return mdb.runCommand(doc).toJson();
	}
	
	/**
	 * 在集合上添加索引
	 * @param indexname 索引名称
	 * @throws CheckedException
	 */
	public void addIndex(String indexname) throws CheckedException{
		hasCol();
		mcol.createIndex(new Document(indexname, 1));
	}
	/**
	 * 删除索引
	 * @param indexname
	 * @throws CheckedException
	 */
	public void removeIndex(String indexname) throws CheckedException{
		hasCol();
		mcol.dropIndex(indexname);
	}
	/**
	 * 获得所有索引名称
	 * @return
	 * @throws CheckedException
	 */
	public List<String> getIndex() throws CheckedException{
		hasCol();
		List<String> indexs = new ArrayList<String>();
		ListIndexesIterable<Document> indexes = mcol.listIndexes();  
		MongoCursor<Document> cursor = indexes.iterator();  
		while (cursor.hasNext()) {  
		      Document index = cursor.next(); 
		      indexs.add(index.toJson());
		}  
		cursor.close();
		return indexs;
	}
	/**
	 * 条件一个文档
	 * @param o 实体
	 * @throws CheckedException
	 */
	public void addOneData(Object o) throws CheckedException{
		hasCol();
		Map<String,Object> map = Utils.convertObjectToMap(o,true);
		Document doc = new Document(map);
		mcol.insertOne(doc);
	}
	/**
	 * 条件多文档
	 * @param os 实体列表
	 * @throws CheckedException
	 */
	public void addData(List<Object> os) throws CheckedException{
		for(Object o : os){
			addOneData(o);
		}
	}
	/**
	 * 修改一个文档
	 * @param filter 所要使用实体的属性的列表  key#entityfield:op 参考 {@code QWhere} object
	 * @param o filter and update object
	 * @throws CheckedException
	 */
	public long updateOneData(String filter,Object o) throws CheckedException{
		hasCol();
		try{
			Document filterdoc = Utils.getFilter(filter, o);
			Map<String,Object> map = Utils.convertObjectToMap(o,false);
			Document updatedoc = new Document(map);
			UpdateResult result= mcol.updateOne(filterdoc, new Document("$set",updatedoc));
			return result.getModifiedCount();
		}catch(Exception e){
			throw new CheckedException(e);
		}
	}
	/**
	 * 修改一个文档
	 * @param id
	 * @param o update object
	 * @throws CheckedException
	 */
	public long updateDataById(String id,Object o) throws CheckedException{
		hasCol();
		try{
			ObjectId _id = new ObjectId(id);
			Map<String,Object> map = Utils.convertObjectToMap(o,false);
			Document updatedoc = new Document(map);
			UpdateResult result= mcol.updateMany(Filters.eq("_id",_id), new Document("$set",updatedoc));
			return result.getModifiedCount();
		}catch(Exception e){
			throw new CheckedException(e);
		}
	}
	/**
	 * 修改多文档
	 * @param filter 所要使用实体的属性的列表  key#entityfield:op 参考 {@code QWhere} object
	 * @param o filter and update object
	 * @throws CheckedException
	 */
	public long updateData(String filter,Object o) throws CheckedException{
		hasCol();
		try{
			Document filterdoc = Utils.getFilter(filter, o);
			Map<String,Object> map = Utils.convertObjectToMap(o,false);
			Document updatedoc = new Document(map);
			UpdateResult result= mcol.updateMany(filterdoc, new Document("$set",updatedoc));
			return result.getModifiedCount();
		}catch(Exception e){
			throw new CheckedException(e);
		}
	}
	/**
	 * 删除一个文档
	 * @param filter 所要使用实体的属性的列表  key#entityfield:op 参考 {@code QWhere} object
	 * @param o filter object
	 * @return 删除文档数量
	 * @throws CheckedException
	 */
	public long deleteOneData(String filter,Object o) throws CheckedException{
		hasCol();
		try{
			Document filterdoc = Utils.getFilter(filter, o);
			DeleteResult result = mcol.deleteOne(filterdoc);
			return result.getDeletedCount();
		}catch(Exception e){
			throw new CheckedException(e);
		}
	}
	/**
	 * 删除一个文档
	 * @param id
	 * @return 删除文档数量
	 * @throws CheckedException
	 */
	public long deleteDataById(String id) throws CheckedException{
		hasCol();
		try{
			ObjectId _id = new ObjectId(id);
			DeleteResult result = mcol.deleteOne(Filters.eq("_id",_id));
			return result.getDeletedCount();
		}catch(Exception e){
			throw new CheckedException(e);
		}
	}
	/**
	 * 删除多文档
	 * @param filter 所要使用实体的属性的列表  key#entityfield:op 参考 {@code QWhere} object
	 * @param o filter object
	 * @return 删除文档数量
	 * @throws CheckedException
	 */
	public long deleteData(String filter,Object o) throws CheckedException{
		hasCol();
		try{
			Document filterdoc = Utils.getFilter(filter, o);
			DeleteResult result = mcol.deleteMany(filterdoc);
			return result.getDeletedCount();
		}catch(Exception e){
			throw new CheckedException(e);
		}
	}
	/**
	 * 获得文档总数
	 * @return 文档数量
	 * @throws CheckedException
	 */
	public long findDataCount() throws CheckedException{
		hasCol();
		return mcol.countDocuments();
	}
	/**
	 * 获得文档总数
	 * @param filter 所要使用实体的属性的列表  key#entityfield:op 参考 {@code QWhere} object
	 * @param o filter object
	 * @return 文档数量
	 * @throws CheckedException
	 */
	public long findDataCount(String filter,Object o) throws CheckedException{
		hasCol();
		Document filterdoc = Utils.getFilter(filter, o);//Document.parse(filter);
		return mcol.countDocuments(filterdoc);
	}
	/**
	 * 获得一个文档实体
	 * @param clazz 实体类
	 * @param fields 返回字段，如果为null或empty就返回所有字段
	 * @return T
	 * @throws CheckedException
	 */
	public <T> T findOneData(Class<T> clazz,String fields) throws CheckedException{
		hasCol();
		Document doc = _find(null,null,fields,null).first();
		return Utils.DocToObj(doc, clazz);
	}
	/**
	 * 获得一个文档实体
	 * @param filter 所要使用实体的属性的列表  key#entityfield:op 参考 {@code QWhere} object
	 * @param o filter object
	 * @param clazz 实体类
	 * @param fields 返回字段，如果为null或empty就返回所有字段
	 * @return T
	 * @throws CheckedException
	 */
	public <T> T findOneData(String filter,Object o,Class<T> clazz,String fields) throws CheckedException{
		hasCol();
		Document doc = _find(filter,o,fields,null).first();
		return Utils.DocToObj(doc, clazz);
	}
	/**
	 * 获得一个排序后的文档实体
	 * @param filter 所要使用实体的属性的列表  key#entityfield:op 参考 {@code QWhere} object
	 * @param o filter object
	 * @param orderstr 排序控制字符 [{"direction":"DESC|ASC","property":"name"},..] 参考 {@code QOrder} object
	 * @param clazz 实体类
	 * @param fields 返回字段，如果为null或empty就返回所有字段
	 * @return T
	 * @throws CheckedException
	 */
	public <T> T findOneData(String filter,Object o,String orderstr,Class<T> clazz,String fields) throws CheckedException{
		hasCol();
		Document doc = _find(filter,o,fields,orderstr).first();
		return Utils.DocToObj(doc, clazz);
	}
	/**
	 * 获得文档实体
	 * @param clazz 返回实体类
	 * @param fields 返回字段，如果为null或empty就返回所有字段
	 * @return List<T>
	 * @throws CheckedException
	 */
	public <T> List<T> findData(Class<T> clazz,String fields) throws CheckedException{
		hasCol();
		FindIterable<Document> iter = _find(null,null,fields,null);
		MongoCursor<Document> cursor = iter.iterator();
		List<T> results = _getObjectByCursor(cursor, clazz);
		return results;
	}
	/**
	 * 获得排序后文档实体
	 * @param orderstr 排序控制字符 [{"direction":"DESC|ASC","property":"name"},..] 参考 {@code QOrder} object
	 * @param clazz 返回实体类
	 * @param fields 返回字段，如果为null或empty就返回所有字段
	 * @return List<T>
	 * @throws CheckedException
	 */
	public <T> List<T> findData(String orderstr,Class<T> clazz,String fields) throws CheckedException{
		hasCol();
		FindIterable<Document> iter = _find(null,null,fields,orderstr);
		MongoCursor<Document> cursor = iter.iterator();
		List<T> results = _getObjectByCursor(cursor, clazz);
		return results;
	}
	/**
	 * 获得文档实体
	 * @param filter 所要使用实体的属性的列表  key#entityfield:op 参考 {@code QWhere} object
	 * @param o filter object
	 * @param clazz 返回实体类
	 * @param fields 返回字段，如果为null或empty就返回所有字段
	 * @return List<T>
	 * @throws CheckedException
	 */
	public <T> List<T> findData(String filter,Object o,Class<T> clazz,String fields) throws CheckedException{
		hasCol();
		FindIterable<Document> iter = _find(filter,o,fields,null);
		MongoCursor<Document> cursor = iter.iterator();
		List<T> results = _getObjectByCursor(cursor, clazz);
		return results;
	}
	/**
	 * 获得排序后文档实体
	 * @param filter 所要使用实体的属性的列表  key#entityfield:op 参考 {@code QWhere} object
	 * @param o filter object
	 * @param orderstr 排序控制字符 [{"direction":"DESC|ASC","property":"name"},..] 参考 {@code QOrder} object
	 * @param clazz 返回实体类
	 * @param fields 返回字段，如果为null或empty就返回所有字段
	 * @return List<T>
	 * @throws CheckedException
	 */
	public <T> List<T> findData(String filter,Object o,String orderstr,Class<T> clazz,String fields) throws CheckedException{
		hasCol();
		FindIterable<Document> iter = _find(filter,o,fields,orderstr);
		MongoCursor<Document> cursor = iter.iterator();
		List<T> results = _getObjectByCursor(cursor, clazz);
		return results;
	}
	/**
	 * 获得分页文档实体
	 * @param filter 所要使用实体的属性的列表  key#entityfield:op 参考 {@code QWhere} object
	 * @param o filter object
	 * @param orderstr 排序控制字符 [{"direction":"DESC|ASC","property":"name"},..] 参考 {@code QOrder} object
	 * @param page 页数 从0开始
	 * @param size 每页的数据 size必须大于0
	 * @param clazz 返回实体类
	 * @param fields 返回字段，如果为null或empty就返回所有字段
	 * @return List<T>
	 * @throws CheckedException
	 */
	public <T> List<T> findDataPage(String filter,Object o,String orderstr,int page,int size,Class<T> clazz,String fields) throws CheckedException{
		hasCol();
		if(size <= 0) throw new CheckedException("参数不合法，size 必须大于  0");
		if(page <= 0) throw new CheckedException("参数不合法，page 必须大于  0");
		FindIterable<Document> iter = _find(filter,o,fields,orderstr);
		int skip = (page - 1) * size;
		iter = iter.skip(skip).limit(size);
		MongoCursor<Document> cursor = iter.iterator();
		List<T> results = _getObjectByCursor(cursor, clazz);
		return results;
	}
	/**
	 * 去重查询
	 * @param fieldname 去重字段名称
	 * @param filter 所要使用实体的属性的列表  key#entityfield:op 参考 {@code QWhere} object
	 * @param o filter object
	 * @param clazz 返回实体类
	 * @return
	 * @throws CheckedException
	 */
	public <T> List<T> distinctData(String fieldname,String filter,Object o,Class<T> clazz) throws CheckedException{
		Document filterdoc = Utils.getFilter(filter, o);
		DistinctIterable<Document> diter = mcol.distinct(fieldname, filterdoc, Document.class);
		MongoCursor<Document> cursor = diter.iterator();
		List<T> results = _getObjectByCursor(cursor, clazz);
		return results;
	}
	/**
	 * MapRedure
	 * @param mapFunction map JS函数
	 * @param reduceFunction reduce JS函数
	 * @param clazz 返回实体类
	 * @return
	 * @throws CheckedException
	 */
	public <T> List<T> mapRedureData(String mapFunction,String reduceFunction,Class<T> clazz) throws CheckedException{
		MapReduceIterable<Document> mriter = mcol.mapReduce(mapFunction, reduceFunction, Document.class);
		MongoCursor<Document> cursor = mriter.iterator();
		List<T> results = _getObjectByCursor(cursor, clazz);
		return results;
	}
	public void dd(){

	}
	/**
	 * 获得数据
	 * @param filter 所要使用实体的属性的列表  key#entityfield:op 参考 {@code QWhere} object
	 * @param o filter object
	 * @param fields 返回字段，如果为null或empty就返回所有字段
	 * @param order 排序控制字符 [{"direction":"DESC|ASC","property":"name"},..] 参考 {@code QOrder} object
	 * @return
	 * @throws CheckedException
	 */
	private FindIterable<Document> _find(String filter,Object o,String fields,String order) throws CheckedException{
		Document filterdoc = null;
		Document sortdoc = null;
		Document fielddoc = null;
		if(!StrUtil.IsEmpty(filter)){
			filterdoc = Utils.getFilter(filter, o);
		}
		if(!StrUtil.IsEmpty(fields)){
			fielddoc = Utils.getResultFields(fields);
		}
		if(!StrUtil.IsEmpty(order)){
			sortdoc =  Utils.getOrder(order);
		}
		FindIterable<Document> iter = null;
		if(filterdoc == null){
			iter = mcol.find(Document.class);
		}
		else{
			iter = mcol.find(filterdoc,Document.class);
		}
		if(fielddoc != null){
			iter = iter.projection(fielddoc);
		}
		if(sortdoc != null){
			iter = iter.sort(sortdoc); 
		}
		return iter;
	}
	
	/**
	 * 根据游标获得数据
	 * @param cursor {@code MongoCursor<Document>}
	 * @param clazz {@code Class}
	 * @return {@code T List<Class<T>>}
	 * @throws CheckedException
	 */
	private <T> List<T> _getObjectByCursor(MongoCursor<Document> cursor,Class<T> clazz) throws CheckedException{
		if(cursor == null) return null;
		List<T> results = new ArrayList<T>();
		try{
			while(cursor.hasNext()){
				Document  _doc = cursor.next();
				T t = Utils.DocToObj(_doc, clazz);
				results.add(t);
			}
		}finally{
			cursor.close();
		}
		return results;
	}
	/**
	 * 根据forEach获得数据 <br/>
	 * @deprecated Replaced by {@link MongoDao#_getObjectByCursor}
	 * @param iter {@code MongoIterable<Document>}
	 * @param clazz {@code Class}
	 * @return {@code T List<Class<T>>}
	 * @throws CheckedException
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private <T> List<T>  _getObjetcByForEach(MongoIterable<Document> iter,Class<T> clazz) throws CheckedException{
		List<Document> docs = new ArrayList<Document>();
		List<T> results = new ArrayList<T>();
		iter.forEach(new Block<Document >() {
			public void apply(Document  _doc) {
				docs.add(_doc);
			};
		});
		for(Document doc : docs){
			T t = Utils.DocToObj(doc, clazz);
			results.add(t);
		}
		return results;
	}
	/**
	 * 获得一个文档实体
	 * @param id
	 * @param clazz 实体类
	 * @return T
	 * @throws CheckedException
	 */
	public <T> T findDataById(String id,Class<T> clazz) throws CheckedException{
		try{
			ObjectId _id = new ObjectId(id);
			Document doc = (Document)mcol.find(Filters.eq("_id",_id)).first();
			T t = Utils.DocToObj(doc, clazz);
			return t;
		}catch(IllegalArgumentException e){
			throw new CheckedException(e);
		}
	}
	/**
	 * 验证MongoDatabase
	 * @throws CheckedException
	 */
	private void hasDB() throws CheckedException{
		if(mdb == null){
			throw new CheckedException("MongoDatabase is empty");
		}
	}
	/**
	 * 验证MongoCollection
	 * @throws CheckedException
	 */
	private void hasCol() throws CheckedException{
		if(mcol == null){
			throw new CheckedException("MongoCollection is empty");
		}
	}
}
