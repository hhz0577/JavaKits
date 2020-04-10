package com.hhz.activiti;

import java.io.InputStream;

import org.activiti.engine.repository.Deployment;

import com.hhz.kit.CheckedException;

public interface IRepository{
	public void deploy(String bpmnfile) throws CheckedException;
	public void deploy(String bpmnfile,String name,String category) throws CheckedException;
	public Deployment actDeploy(String bpmnfile) throws CheckedException;
	public Deployment actDeploy(String bpmnfile,String name,String category) throws CheckedException;
	public void deployForZip(String path,String zipfile) throws CheckedException;
	public Deployment actDeployForZip(String path,String zipfile) throws CheckedException;
	
	public void removeDeloy(String deploymentId) throws CheckedException;
	public void removeDeloy(String deploymentId,boolean cascade) throws CheckedException;
	
	public InputStream getImageStream(String deploymentId) throws CheckedException;
	
	public String getProcessDefinitionKey(String deploymentId) throws CheckedException;
	public String getProcessDefinitionId(String deploymentId) throws CheckedException;
}
