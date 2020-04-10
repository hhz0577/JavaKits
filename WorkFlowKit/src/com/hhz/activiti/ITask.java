package com.hhz.activiti;

import java.util.List;
import java.util.Map;

import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;

import com.hhz.kit.CheckedException;

public interface ITask {
	public List<IdentityLink> getIdentityLinksForTask(String taskId);
	public List<Task> getTaskListByGroup(String candidateGroup) throws CheckedException;
	public List<Task> getTaskListByUser(String assignee) throws CheckedException;
	public void taskClaim(String taskId,String userId) throws CheckedException;
	public void taskUnclaim(String taskId) throws CheckedException;
	public void delegateTask(String taskId,String userId) throws CheckedException;
	public void resolveTask(String taskId) throws CheckedException;
	public void taskComplete(String taskId) throws CheckedException;
	public void taskComplete(String taskId, Map<String, Object> variables) throws CheckedException;
	public void taskComplete(String taskId, Map<String, Object> variables,boolean localScope) throws CheckedException;
	public void setTaskAssignee(String taskId, String userId) throws CheckedException;
	public void setTaskOwner(String taskId, String userId) throws CheckedException;
	public void addUser(String taskId, String userId) throws CheckedException;
	public void deleteUser(String taskId, String userId) throws CheckedException;
	public void addGroup(String taskId, String groupId) throws CheckedException;
	public void deleteGroup(String taskId, String userId) throws CheckedException;
	
	public List<Task> getSubTasks(String parentTaskId) throws CheckedException;
	
	public void setTaskVariable(String taskId, String variableName, Object value) throws CheckedException;
	public void setTaskVariables(String taskId, Map<String, ? extends Object> variables) throws CheckedException;
	public Object getTaskVariable(String taskId, String variableName) throws CheckedException;
	public <T> T getTaskVariable(String taskId, String variableName, Class<T> variableClass) throws CheckedException;
	public Map<String, Object> getTaskVariables(String taskId) throws CheckedException;
	public boolean hasTaskVariable(String taskId, String variableName) throws CheckedException;
	public void removeTaskVariable(String taskId, String variableName) throws CheckedException;
	
	public void setTaskVariableLocal(String taskId, String variableName, Object value) throws CheckedException;
	public void setTaskVariablesLocal(String taskId, Map<String, ? extends Object> variables) throws CheckedException;
	public Object getTaskVariableLocal(String taskId, String variableName) throws CheckedException;
	public <T> T getTaskVariableLocal(String taskId, String variableName, Class<T> variableClass) throws CheckedException;
	public Map<String, Object> getTaskVariablesLocal(String taskId) throws CheckedException;
	public boolean hasTaskVariableLocal(String taskId, String variableName) throws CheckedException;
	public void removeTaskVariableLocal(String taskId, String variableName) throws CheckedException;
}
