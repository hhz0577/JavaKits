package com.hhz.activiti.impl;

import java.util.List;
import java.util.Map;

import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.ActivitiTaskAlreadyClaimedException;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;

import com.hhz.activiti.ITask;
import com.hhz.kit.CheckedException;
import com.hhz.kit.ClassUtil;
import com.hhz.kit.StrUtil;

public class TaskSvr implements ITask{
	private TaskService taskService;
	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}
	
	/**
	 * 获得定义的连接
	 * @param taskId
	 * @return
	 */
	public List<IdentityLink> getIdentityLinksForTask(String taskId){
		return taskService.getIdentityLinksForTask(taskId);
	}
	/**
	 * 根据用户组获得任务列表
	 * @param candidateGroup
	 * @return
	 * @throws CheckedException
	 */
	public List<Task> getTaskListByGroup(String candidateGroup) throws CheckedException{
		if(StrUtil.IsEmpty(candidateGroup)) throw new CheckedException("用户组为空");
		return taskService.createTaskQuery().taskCandidateGroup(candidateGroup).list();
	}
	/**
	 * 根据用户获得任务列表
	 * @param assignee
	 * @return
	 * @throws CheckedException
	 */
	public List<Task> getTaskListByUser(String assignee) throws CheckedException{
		if(StrUtil.IsEmpty(assignee)) throw new CheckedException("用户为空");
		return taskService.createTaskQuery().taskAssignee(assignee).list();
	}
	/**
	 * 任务认领
	 * @param taskId
	 * @param userId
	 * @throws CheckedException  when the task doesn't exist And when the task is already claimed by another user
	 */
	public void taskClaim(String taskId,String userId) throws CheckedException{
		if(StrUtil.IsEmpty(taskId)) throw new CheckedException("任务代码为空");
		if(StrUtil.IsEmpty(userId)) throw new CheckedException("用户代码为空");
		try{
			taskService.claim(taskId, userId);
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
		catch(ActivitiTaskAlreadyClaimedException e){
			throw new CheckedException(e);
		}
	}
	/**
	 * 任务认领回滚
	 * @param taskId
	 * @throws CheckedException
	 */
	public void taskUnclaim(String taskId) throws CheckedException{
		if(StrUtil.IsEmpty(taskId)) throw new CheckedException("任务代码为空");
		try{
			taskService.unclaim(taskId);
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
	}
	/**
	 * 将任务委托给其他用户
	 * @param taskId
	 * @param userId
	 * @throws CheckedException
	 */
	public void delegateTask(String taskId,String userId) throws CheckedException{
		if(StrUtil.IsEmpty(taskId)) throw new CheckedException("任务代码为空");
		if(StrUtil.IsEmpty(userId)) throw new CheckedException("委托用户代码为空");
		try{
			taskService.delegateTask(taskId, userId);
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
	}
	/**
	 * 回复委托给其他用户的任务
	 * @param taskId
	 * @throws CheckedException
	 */
	public void resolveTask(String taskId) throws CheckedException{
		if(StrUtil.IsEmpty(taskId)) throw new CheckedException("任务代码为空");
		try{
			taskService.resolveTask(taskId);
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
	}
	/**
	 * 完成任务
	 * @param taskId
	 * @throws CheckedException
	 */
	public void taskComplete(String taskId) throws CheckedException{
		if(StrUtil.IsEmpty(taskId)) throw new CheckedException("任务代码为空");
		try{
			taskService.complete(taskId);
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
	}
	/**
	 * 完成任务 将任务参数传给下一个用户
	 * @param taskId
	 * @param variables 任务参数
	 * @throws CheckedException
	 */
	public void taskComplete(String taskId, Map<String, Object> variables) throws CheckedException{
		if(StrUtil.IsEmpty(taskId)) throw new CheckedException("任务代码为空");
		if(ClassUtil.IsNullObject(variables)) throw new CheckedException("任务参数为NULL");
		if(variables.size() <= 0) throw new CheckedException("任务参数为空");
		try{
			taskService.complete(taskId,variables);
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
	}
	/**
	 * 完成任务 将任务参数传给下一个用户  
	 * @param taskId
	 * @param variables
	 * @param localScope 如果为true 任务参数将存储在任务自己，以替换流程实例的范围
	 * @throws CheckedException
	 */
	public void taskComplete(String taskId, Map<String, Object> variables,boolean localScope) throws CheckedException{
		if(StrUtil.IsEmpty(taskId)) throw new CheckedException("任务代码为空");
		if(ClassUtil.IsNullObject(variables)) throw new CheckedException("任务参数为NULL");
		if(variables.size() <= 0) throw new CheckedException("任务参数为空");
		try{
			taskService.complete(taskId,variables,localScope);
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
	}
	/**
	 * 改变任务受让人
	 * @param taskId
	 * @param userId
	 * @throws CheckedException
	 */
	public void setTaskAssignee(String taskId, String userId) throws CheckedException{
		if(StrUtil.IsEmpty(taskId)) throw new CheckedException("任务代码为空");
		if(StrUtil.IsEmpty(userId)) throw new CheckedException("用户代码为空");
		try{
			taskService.setAssignee(taskId,userId);
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
	}
	/**
	 * 转移任务所有权给其他用户
	 * @param taskId
	 * @param userId
	 * @throws CheckedException
	 */
	public void setTaskOwner(String taskId, String userId) throws CheckedException{
		if(StrUtil.IsEmpty(taskId)) throw new CheckedException("任务代码为空");
		if(StrUtil.IsEmpty(userId)) throw new CheckedException("用户代码为空");
		try{
			taskService.setOwner(taskId,userId);
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
	}
	/**
	 * 给任务添加一个用户
	 * @param taskId
	 * @param userId
	 * @throws CheckedException
	 */
	public void addUser(String taskId, String userId) throws CheckedException{
		if(StrUtil.IsEmpty(taskId)) throw new CheckedException("任务代码为空");
		if(StrUtil.IsEmpty(userId)) throw new CheckedException("用户代码为空");
		try{
			taskService.addCandidateUser(taskId,userId);
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
	}
	/**
	 * 在任务中删除一个用户
	 * @param taskId
	 * @param userId
	 * @throws CheckedException
	 */
	public void deleteUser(String taskId, String userId) throws CheckedException{
		if(StrUtil.IsEmpty(taskId)) throw new CheckedException("任务代码为空");
		if(StrUtil.IsEmpty(userId)) throw new CheckedException("用户代码为空");
		try{
			taskService.deleteCandidateUser(taskId,userId);
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
	}
	/**
	 * 给任务添加一个用户组
	 * @param taskId
	 * @param groupId
	 * @throws CheckedException
	 */
	public void addGroup(String taskId, String groupId) throws CheckedException{
		if(StrUtil.IsEmpty(taskId)) throw new CheckedException("任务代码为空");
		if(StrUtil.IsEmpty(groupId)) throw new CheckedException("用户组代码为空");
		try{
			taskService.addCandidateGroup(taskId,groupId);
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
	}
	/**
	 * 在任务中删除一个用户组
	 * @param taskId
	 * @param groupId
	 * @throws CheckedException
	 */
	public void deleteGroup(String taskId, String groupId) throws CheckedException{
		if(StrUtil.IsEmpty(taskId)) throw new CheckedException("任务代码为空");
		if(StrUtil.IsEmpty(groupId)) throw new CheckedException("用户组代码为空");
		try{
			taskService.deleteCandidateGroup(taskId,groupId);
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
	}
	/**
	 * 获得子任务列表
	 * @param parentTaskId
	 * @return
	 * @throws CheckedException
	 */
	public List<Task> getSubTasks(String parentTaskId) throws CheckedException{
		if(StrUtil.IsEmpty(parentTaskId)) throw new CheckedException("父任务代码为空");
		try{
			return taskService.getSubTasks(parentTaskId);
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
	}
	//处理任务参数
	public void setTaskVariable(String taskId, String variableName, Object value) throws CheckedException{
		if(StrUtil.IsEmpty(taskId)) throw new CheckedException("任务代码不能为空");
		if(StrUtil.IsEmpty(variableName)) throw new CheckedException("要获得的参数名称不能为空");
		if(ClassUtil.IsNullObject(value)) throw new CheckedException("对象不能为NULL");
		try{
			taskService.setVariable(taskId,variableName,value);
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
	}
	public void setTaskVariables(String taskId, Map<String, ? extends Object> variables) throws CheckedException{
		if(StrUtil.IsEmpty(taskId)) throw new CheckedException("任务代码不能为空");
		if(ClassUtil.IsNullObject(variables)) throw new CheckedException("MAP对象不能为NULL");
		if(variables.size() <= 0) throw new CheckedException("MAP对象不能为空");
		try{
			taskService.setVariables(taskId,variables);
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
	}
	public Object getTaskVariable(String taskId, String variableName) throws CheckedException{
		if(StrUtil.IsEmpty(taskId)) throw new CheckedException("任务代码不能为空");
		if(StrUtil.IsEmpty(variableName)) throw new CheckedException("要获得的参数名称不能为空");
		try{
			return taskService.getVariable(taskId,variableName);
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
	}
	public <T> T getTaskVariable(String taskId, String variableName, Class<T> variableClass) throws CheckedException{
		if(StrUtil.IsEmpty(taskId)) throw new CheckedException("任务代码不能为空");
		if(StrUtil.IsEmpty(variableName)) throw new CheckedException("要获得的参数名称不能为空");
		if(StrUtil.IsEmpty(variableClass.getName())) throw new CheckedException("要获得的参数类名不能为空");
		try{
			return taskService.getVariable(taskId,variableName,variableClass);
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
	}
	public Map<String, Object> getTaskVariables(String taskId) throws CheckedException{
		if(StrUtil.IsEmpty(taskId)) throw new CheckedException("任务代码不能为空");
		try{
			return taskService.getVariables(taskId);
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
	}
	public boolean hasTaskVariable(String taskId, String variableName) throws CheckedException{
		if(StrUtil.IsEmpty(taskId)) throw new CheckedException("任务代码不能为空");
		if(StrUtil.IsEmpty(variableName)) throw new CheckedException("要获得的参数名称不能为空");
		return taskService.hasVariable(taskId,variableName);
	}
	public void removeTaskVariable(String taskId, String variableName) throws CheckedException{
		if(StrUtil.IsEmpty(taskId)) throw new CheckedException("任务代码不能为空");
		if(StrUtil.IsEmpty(variableName)) throw new CheckedException("要获得的参数名称不能为空");
		taskService.removeVariable(taskId,variableName);
	}
	//处理本地的任务参数
	public void setTaskVariableLocal(String taskId, String variableName, Object value) throws CheckedException{
		if(StrUtil.IsEmpty(taskId)) throw new CheckedException("任务代码不能为空");
		if(StrUtil.IsEmpty(variableName)) throw new CheckedException("要获得的参数名称不能为空");
		if(ClassUtil.IsNullObject(value)) throw new CheckedException("对象不能为NULL");
		try{
			taskService.setVariableLocal(taskId,variableName,value);
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
	}
	public void setTaskVariablesLocal(String taskId, Map<String, ? extends Object> variables) throws CheckedException{
		if(StrUtil.IsEmpty(taskId)) throw new CheckedException("任务代码不能为空");
		if(ClassUtil.IsNullObject(variables)) throw new CheckedException("MAP对象不能为NULL");
		if(variables.size() <= 0) throw new CheckedException("MAP对象不能为空");
		try{
			taskService.setVariablesLocal(taskId,variables);
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
	}
	public Object getTaskVariableLocal(String taskId, String variableName) throws CheckedException{
		if(StrUtil.IsEmpty(taskId)) throw new CheckedException("任务代码不能为空");
		if(StrUtil.IsEmpty(variableName)) throw new CheckedException("要获得的参数名称不能为空");
		try{
			return taskService.getVariableLocal(taskId,variableName);
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
	}
	public <T> T getTaskVariableLocal(String taskId, String variableName, Class<T> variableClass) throws CheckedException{
		if(StrUtil.IsEmpty(taskId)) throw new CheckedException("任务代码不能为空");
		if(StrUtil.IsEmpty(variableName)) throw new CheckedException("要获得的参数名称不能为空");
		if(StrUtil.IsEmpty(variableClass.getName())) throw new CheckedException("要获得的参数类名不能为空");
		try{
			return taskService.getVariableLocal(taskId,variableName,variableClass);
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
	}
	public Map<String, Object> getTaskVariablesLocal(String taskId) throws CheckedException{
		if(StrUtil.IsEmpty(taskId)) throw new CheckedException("任务代码不能为空");
		try{
			return taskService.getVariablesLocal(taskId);
		}
		catch(ActivitiObjectNotFoundException e){
			throw new CheckedException(e);
		}
	}
	public boolean hasTaskVariableLocal(String taskId, String variableName) throws CheckedException{
		if(StrUtil.IsEmpty(taskId)) throw new CheckedException("任务代码不能为空");
		if(StrUtil.IsEmpty(variableName)) throw new CheckedException("要获得的参数名称不能为空");
		return taskService.hasVariableLocal(taskId,variableName);
	}
	public void removeTaskVariableLocal(String taskId, String variableName) throws CheckedException{
		if(StrUtil.IsEmpty(taskId)) throw new CheckedException("任务代码不能为空");
		if(StrUtil.IsEmpty(variableName)) throw new CheckedException("要获得的参数名称不能为空");
		taskService.removeVariableLocal(taskId,variableName);
	}
}
