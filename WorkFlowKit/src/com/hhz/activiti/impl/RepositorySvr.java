package com.hhz.activiti.impl;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;

import com.hhz.activiti.IRepository;
import com.hhz.kit.CheckedException;
import com.hhz.kit.StrUtil;

public class RepositorySvr implements IRepository{
	private RepositoryService repositoryService;

	public void setRepositoryService(RepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}
	
	/**
	 * 部署
	 * @param bpmnfile 业务归档名称
	 * @throws CheckedException
	 */
	public void deploy(String bpmnfile) throws CheckedException{
		_deploy(bpmnfile,"","");
	}
	/**
	 * 部署
	 * @param bpmnfile 业务归档名称
	 * @param name <process id="" name="Monthly financial report reminder process">
	 * @param category <definitions id="" targetNamespace="http://activiti.hhz.com/bpmn20" xmlns:activiti="" xmlns="">
	 * @throws CheckedException
	 */
	public void deploy(String bpmnfile,String name,String category) throws CheckedException{
		_deploy(bpmnfile,name,category);
	}
	/**
	 * 部署
	 * @param bpmnfile
	 * @return Deployment {@link Deployment}
	 * @throws CheckedException
	 */
	public Deployment actDeploy(String bpmnfile) throws CheckedException{
		return _deploy(bpmnfile,"","");
	}
	/**
	 * 部署
	 * @param bpmnfile
	 * @param name <process id="" name="Monthly financial report reminder process">
	 * @param category <definitions id="" targetNamespace="http://activiti.hhz.com/bpmn20" xmlns:activiti="" xmlns="">
	 * @return Deployment {@link Deployment}
	 * @throws CheckedException
	 */
	public Deployment actDeploy(String bpmnfile,String name,String category) throws CheckedException{
		return _deploy(bpmnfile,name,category);
	}
	/**
	 * 部署处理方法
	 * @param bpmnfile
	 * @param name
	 * @param category
	 * @return
	 * @throws CheckedException
	 */
	private Deployment _deploy(String bpmnfile,String name,String category) throws CheckedException{
		if(StrUtil.IsEmpty(bpmnfile)) throw new CheckedException("业务归档文件为空");
		return repositoryService.createDeployment().name(name).category(category).addClasspathResource(bpmnfile).deploy();
	}
	/**
	 * 通过一个压缩文件（支持Zip和Bar）部署业务归档
	 * @param path
	 * @param zipfile
	 * @throws CheckedException
	 */
	public void deployForZip(String path,String zipfile) throws CheckedException{
		_deployForZip(path,zipfile);
	}
	/**
	 * 通过一个压缩文件（支持Zip和Bar）部署业务归档
	 * @param path
	 * @param zipfile
	 * @return Deployment {@link Deployment}
	 * @throws CheckedException
	 */
	public Deployment actDeployForZip(String path,String zipfile) throws CheckedException{
		return _deployForZip(path,zipfile);
	}
	/**
	 * 部署处理方法
	 * @param path
	 * @param zipfile
	 * @return
	 * @throws CheckedException
	 */
	private Deployment _deployForZip(String path,String zipfile) throws CheckedException{
		if(StrUtil.IsEmpty(zipfile)) throw new CheckedException("业务归档文件为空");
		if(StrUtil.IsEmpty(path)) throw new CheckedException("业务归档目录为空");
		String barFileName = path + "/" + zipfile;
		try{
			ZipInputStream inputStream = new ZipInputStream(new FileInputStream(barFileName));
			Deployment deployment = repositoryService.createDeployment().name(zipfile).addZipInputStream(inputStream).deploy();
			inputStream.close();
			return deployment;
		}
		catch(Exception e){
			throw new CheckedException(e);
		}
	}
	/**
	 * 根据给定的部署业务，删除部署业务归档
	 * @param deploymentId
	 * @throws CheckedException if there are still runtime or history process instances or jobs
	 */
	public void removeDeloy(String deploymentId) throws CheckedException{
		_removeDeploy(deploymentId,false);
	}
	/**
	 * 根据给定的部署业务，级联删除部署业务归档
	 * @param deploymentId
	 * @param cascade 级联标志
	 * @throws CheckedException if there are still runtime or history process instances or jobs
	 */
	public void removeDeloy(String deploymentId,boolean cascade) throws CheckedException{
		_removeDeploy(deploymentId,cascade);
	}
	/**
	 * 删除部署业务归档处理方法
	 * @param deploymentId
	 * @param cascade
	 * @throws CheckedException
	 */
	private void _removeDeploy(String deploymentId,boolean cascade) throws CheckedException{
		if(StrUtil.IsEmpty(deploymentId)) throw new CheckedException("部署代码不能为空");
		try{
			repositoryService.deleteDeployment(deploymentId,cascade);
		}
		catch(RuntimeException e){
			throw new CheckedException(e);
		}
	}
	/**
	 * 获取流程定义图片资源
	 * @return InputStream 如果没有就为null
	 * @throws CheckedException
	 */
	public InputStream getImageStream(String deploymentId) throws CheckedException{
		if(StrUtil.IsEmpty(deploymentId)) throw new CheckedException("部署代码不能为空");
		try{
			ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId).singleResult();
			String diagramResourceName = processDefinition.getDiagramResourceName();
			if(StrUtil.IsEmpty(diagramResourceName)) return null;
			InputStream imageStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), diagramResourceName);
			return imageStream;
		}
		catch(ActivitiException e)
		{
			throw new CheckedException(e);
		}
	}
	/**
	 * 获得流程定义的KEY
	 * @param deploymentId
	 * @return
	 * @throws CheckedException
	 */
	public String getProcessDefinitionKey(String deploymentId) throws CheckedException{
		ProcessDefinition processDefinition = _getProcessDefinition(deploymentId);
		return processDefinition.getKey();
	}
	/**
	 * 获得流程定义的代码
	 * @param deploymentId
	 * @return
	 * @throws CheckedException
	 */
	public String getProcessDefinitionId(String deploymentId) throws CheckedException{
		ProcessDefinition processDefinition = _getProcessDefinition(deploymentId);
		return processDefinition.getId();
	}
	/**
	 * 获得流程定义
	 * @param deploymentId
	 * @return
	 * @throws CheckedException
	 */
	public ProcessDefinition getProcessDefinition(String deploymentId) throws CheckedException{
		ProcessDefinition processDefinition = _getProcessDefinition(deploymentId);
		return processDefinition;
	}
	/**
	 * 获得流程定义处理方法
	 * @param deploymentId
	 * @return
	 * @throws CheckedException
	 */
	private ProcessDefinition _getProcessDefinition(String deploymentId) throws CheckedException{
		if(StrUtil.IsEmpty(deploymentId)) throw new CheckedException("部署代码不能为空");
		try{
			ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId).singleResult();
			return processDefinition;
		}
		catch(ActivitiException e)
		{
			throw new CheckedException(e);
		}
	}
}
