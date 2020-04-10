package com.hhz.mongo.orm.impl;

import com.hhz.kit.CheckedException;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;

public class MongoMan {
	private MongoClient mclient;
	
	public MongoMan(String uristr) throws CheckedException{
		try{
			MongoClientURI uri = new MongoClientURI(uristr);
			mclient = new MongoClient(uri);
		}catch(MongoException e){
			throw new CheckedException(e);
		}
	}
	public String getAddressInfo(){
		return mclient.getConnectPoint();
	}
	public MongoDatabase getDataBase(String databaseName) throws CheckedException{
		try{
			return mclient.getDatabase(databaseName);
		}catch(IllegalArgumentException e){
			throw new CheckedException(e);
		}
	}
	public void dropDataBase(String databaseName) throws CheckedException{
		try{
			mclient.dropDatabase(databaseName);
			
		}catch(MongoException e){
			throw new CheckedException(e);
		}
	}
	public void Close(){
		if(mclient != null){
			mclient.close();
			mclient = null;
		}
	}
	
}
