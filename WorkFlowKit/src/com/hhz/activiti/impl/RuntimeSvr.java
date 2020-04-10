package com.hhz.activiti.impl;

import java.util.List;
import java.util.Map;

import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.IdentityLinkType;

import com.hhz.activiti.IRuntime;
import com.hhz.kit.CheckedException;
import com.hhz.kit.ClassUtil;
import com.hhz.kit.StrUtil;

public class RuntimeSvr implements IRuntime{
	private RuntimeService runtimeService;
	public void setRuntimeService(RuntimeService runtimeService) {
		this.runtimeService = runtimeService;
	}
	/**
	 * 开始一个新的流程实例
	 * @param processDefinitionKey
	 * @return 返回流程实例代码
	 * @throws CheckedException
	 */
	public String startProcInst(String processDefinitionKey) throws CheckedException{
		return _startProcInst(processDefinitionKey,"",null);
	}
	/**
	  * 开始一个新的流程实例
	 * @param processDefinitionKey
	 * @param businessKey
	 * @param variables
	 * @return 返回流程实例代码
	 * @throws CheckedException
	 */
	public String startProcInst(String processDefinitionKey,String businessKey,Map<String, Object> variables) throws CheckedException{
		return _startProcInst(processDefinitionKey,businessKey,variables);
	}
	private String _startProcInst(String processDefinitionKey,String businessKey,Map<String, Object> variables) throws CheckedException{
		if(StrUtil.IsEmpty(processDefinitionKey)) throw new CheckedException("流程定义的key不能为空");
		ProcessInstance instance = null;
		try{
			if(StrUtil.IsEmpty(businessKey)){
				if(ClassUtil.IsNullObject(variables))
					instance = runtimeService.startProcessInstanceByKey(processDefinitionKey);
				else
					instance = runtimeService.startProcessInstanceByKey(processDefinitionKey,variables);
			}
			else{
				if(ClassUtil.IsNullObject(variables))
					instance = runtimeService.startProcessInstanceByKey(processDefinitionKey,businessKey);
				else
					instance = runtimeService.startProcessInstanceByKey(processDefinitionKey,businessKey,variables);
			}
			return instance.getId();
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
	}
	/**
	 * Involves a user with a process instance. The type of identity link is
	 * defined by the given identityLinkType.
	 * @param processInstanceId
	 * @param userId
	 * @param identityLinkType
	 * @throws CheckedException
	 */
	public void addUserIdentityLink(String processInstanceId, String userId, String identityLinkType) throws CheckedException{
		if(StrUtil.IsEmpty(processInstanceId)) throw new CheckedException("流程实例代码不能为空");
		if(StrUtil.IsEmpty(userId)) throw new CheckedException("用户代码不能为空");
		if(StrUtil.IsEmpty(identityLinkType)) throw new CheckedException("定义连接类型不能为空");
		String identityType = _checkIdentityLinkType(identityLinkType);
		try{
			runtimeService.addUserIdentityLink(processInstanceId,userId,identityType);
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
	}
	/**
	 * Involves a group with a process instance. The type of identityLink is defined by the
	 * given identityLink.
	 * @param processInstanceId
	 * @param groupId
	 * @param identityLinkType
	 * @throws CheckedException
	 */
	public void addGroupIdentityLink(String processInstanceId, String groupId, String identityLinkType) throws CheckedException{
		if(StrUtil.IsEmpty(processInstanceId)) throw new CheckedException("流程实例代码不能为空");
		if(StrUtil.IsEmpty(groupId)) throw new CheckedException("用户组代码不能为空");
		if(StrUtil.IsEmpty(identityLinkType)) throw new CheckedException("定义连接类型不能为空");
		String identityType = _checkIdentityLinkType(identityLinkType);
		try{
			runtimeService.addGroupIdentityLink(processInstanceId,groupId,identityType);
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
	}
	private String _checkIdentityLinkType(String identityLinkType) throws CheckedException{
		String identityType = identityLinkType.toLowerCase();
		if(identityType.equals(IdentityLinkType.ASSIGNEE))
			identityType = IdentityLinkType.ASSIGNEE;
		else if(identityType.equals(IdentityLinkType.CANDIDATE))
			identityType = IdentityLinkType.CANDIDATE;
		else if(identityType.equals(IdentityLinkType.OWNER))
			identityType = IdentityLinkType.OWNER;
		else if(identityType.equals(IdentityLinkType.STARTER))
			identityType = IdentityLinkType.STARTER;
		else if(identityType.equals(IdentityLinkType.PARTICIPANT))
			identityType = IdentityLinkType.PARTICIPANT;
		else
			throw new CheckedException("定义连接类型不在范围内");
		return identityType;
	}
	/**
	 * 根据活动的任务中的执行的代码，获得创建的流程实例的初始赋予的MAP (including parent scopes)
	 * @param executionId
	 * @return
	 * @throws CheckedException
	 */
	public Map<String, Object> getVariables(String executionId) throws CheckedException{
		if(StrUtil.IsEmpty(executionId)) throw new CheckedException("任务执行的代码不能为空");
		try{
			return runtimeService.getVariables(executionId);
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
	}
	public Object getVariable(String executionId, String variableName) throws CheckedException{
		if(StrUtil.IsEmpty(executionId)) throw new CheckedException("任务执行的代码不能为空");
		if(StrUtil.IsEmpty(variableName)) throw new CheckedException("要获得的参数名称不能为空");
		try{
			return runtimeService.getVariable(executionId,variableName);
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
	}
	public <T> T getVariable(String executionId, String variableName, Class<T> variableClass) throws CheckedException{
		if(StrUtil.IsEmpty(executionId)) throw new CheckedException("任务执行的代码不能为空");
		if(StrUtil.IsEmpty(variableName)) throw new CheckedException("要获得的参数名称不能为空");
		if(StrUtil.IsEmpty(variableClass.getName())) throw new CheckedException("要获得的参数类名不能为空");
		try{
			return runtimeService.getVariable(executionId,variableName,variableClass);
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
	}
	public boolean hasVariable(String executionId, String variableName) throws CheckedException{
		if(StrUtil.IsEmpty(executionId)) throw new CheckedException("任务执行的代码不能为空");
		if(StrUtil.IsEmpty(variableName)) throw new CheckedException("要获得的参数名称不能为空");
		return runtimeService.hasVariable(executionId,variableName);
	}
	public void setVariable(String executionId, String variableName, Object value) throws CheckedException{
		if(StrUtil.IsEmpty(executionId)) throw new CheckedException("任务执行的代码不能为空");
		if(StrUtil.IsEmpty(variableName)) throw new CheckedException("要获得的参数名称不能为空");
		if(ClassUtil.IsNullObject(value)) throw new CheckedException("对象不能为NULL");
		try{
			runtimeService.setVariable(executionId,variableName,value);
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
	}
	public void setVariables(String executionId, Map<String, ? extends Object> variables) throws CheckedException{
		if(StrUtil.IsEmpty(executionId)) throw new CheckedException("任务执行的代码不能为空");
		if(ClassUtil.IsNullObject(variables)) throw new CheckedException("MAP对象不能为NULL");
		if(variables.size() <= 0) throw new CheckedException("MAP对象不能为空");
		try{
			runtimeService.setVariables(executionId,variables);
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
	}
	public void removeVariable(String executionId, String variableName) throws CheckedException{
		if(StrUtil.IsEmpty(executionId)) throw new CheckedException("任务执行的代码不能为空");
		if(StrUtil.IsEmpty(variableName)) throw new CheckedException("要获得的参数名称不能为空");
		runtimeService.removeVariable(executionId,variableName);
	}
	/**
	 * 根据活动的任务中的执行的代码，获得创建的流程实例的初始赋予的MAP (including parent scopes)
	 * @param executionId
	 * @return
	 * @throws CheckedException
	 */
	public Map<String, Object> getVariablesLocal(String executionId) throws CheckedException{
		if(StrUtil.IsEmpty(executionId)) throw new CheckedException("任务执行的代码不能为空");
		try{
			return runtimeService.getVariablesLocal(executionId);
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
	}
	public Object getVariableLocal(String executionId, String variableName) throws CheckedException{
		if(StrUtil.IsEmpty(executionId)) throw new CheckedException("任务执行的代码不能为空");
		if(StrUtil.IsEmpty(variableName)) throw new CheckedException("要获得的参数名称不能为空");
		try{
			return runtimeService.getVariableLocal(executionId,variableName);
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
	}
	public <T> T getVariableLocal(String executionId, String variableName, Class<T> variableClass) throws CheckedException{
		if(StrUtil.IsEmpty(executionId)) throw new CheckedException("任务执行的代码不能为空");
		if(StrUtil.IsEmpty(variableName)) throw new CheckedException("要获得的参数名称不能为空");
		if(StrUtil.IsEmpty(variableClass.getName())) throw new CheckedException("要获得的参数类名不能为空");
		try{
			return runtimeService.getVariableLocal(executionId,variableName,variableClass);
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
	}
	public boolean hasVariableLocal(String executionId, String variableName) throws CheckedException{
		if(StrUtil.IsEmpty(executionId)) throw new CheckedException("任务执行的代码不能为空");
		if(StrUtil.IsEmpty(variableName)) throw new CheckedException("要获得的参数名称不能为空");
		return runtimeService.hasVariableLocal(executionId,variableName);
	}
	public void setVariableLocal(String executionId, String variableName, Object value) throws CheckedException{
		if(StrUtil.IsEmpty(executionId)) throw new CheckedException("任务执行的代码不能为空");
		if(StrUtil.IsEmpty(variableName)) throw new CheckedException("要获得的参数名称不能为空");
		if(ClassUtil.IsNullObject(value)) throw new CheckedException("对象不能为NULL");
		try{
			runtimeService.setVariableLocal(executionId,variableName,value);
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
	}
	public void setVariablesLocal(String executionId, Map<String, ? extends Object> variables) throws CheckedException{
		if(StrUtil.IsEmpty(executionId)) throw new CheckedException("任务执行的代码不能为空");
		if(ClassUtil.IsNullObject(variables)) throw new CheckedException("MAP对象不能为NULL");
		if(variables.size() <= 0) throw new CheckedException("MAP对象不能为空");
		try{
			runtimeService.setVariablesLocal(executionId,variables);
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
	}
	public void removeVariableLocal(String executionId, String variableName) throws CheckedException{
		if(StrUtil.IsEmpty(executionId)) throw new CheckedException("任务执行的代码不能为空");
		if(StrUtil.IsEmpty(variableName)) throw new CheckedException("要获得的参数名称不能为空");
		runtimeService.removeVariableLocal(executionId,variableName);
	}
	/**
	 * 根据流程实例代码获得流程实例
	 * @param processInstanceId
	 * @return
	 * @throws CheckedException
	 */
	public ProcessInstance getProcInct(String processInstanceId) throws CheckedException{
		if(StrUtil.IsEmpty(processInstanceId)) throw new CheckedException("任务执行的代码不能为空");
		try{
			return runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
	}
	/**
	 * 获得定义的连接
	 * @param instanceId
	 * @return
	 */
	public List<IdentityLink> getIdentityLinksForProcessInstance(String instanceId){
		return runtimeService.getIdentityLinksForProcessInstance(instanceId);
	}
	/**
	 * 删除一个流程实例
	 * @param processInstanceId
	 * @param deleteReason
	 * @throws CheckedException  when no process instance is found with the given id
	 */
	public void removeProcInst(String processInstanceId, String deleteReason) throws CheckedException{
		if(StrUtil.IsEmpty(processInstanceId)) throw new CheckedException("流程实例代码不能为空");
		if(StrUtil.IsEmpty(processInstanceId)) throw new CheckedException("删除原因不能为空");
		try{
			runtimeService.deleteProcessInstance(processInstanceId,deleteReason);
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
	}
}
