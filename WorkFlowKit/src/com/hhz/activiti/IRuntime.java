package com.hhz.activiti;

import java.util.List;
import java.util.Map;

import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;

import com.hhz.kit.CheckedException;

public interface IRuntime {
	public String startProcInst(String processDefinitionKey) throws CheckedException;
	public String startProcInst(String processDefinitionKey,String businessKey,Map<String, Object> variables) throws CheckedException;
	
	public void addUserIdentityLink(String processInstanceId, String userId, String identityLinkType) throws CheckedException;
	public void addGroupIdentityLink(String processInstanceId, String groupId, String identityLinkType) throws CheckedException;
	
	public Map<String, Object> getVariables(String executionId) throws CheckedException;
	public Object getVariable(String executionId, String variableName) throws CheckedException;
	public <T> T getVariable(String executionId, String variableName, Class<T> variableClass) throws CheckedException;
	public boolean hasVariable(String executionId, String variableName) throws CheckedException;
	public void setVariable(String executionId, String variableName, Object value) throws CheckedException;
	public void setVariables(String executionId, Map<String, ? extends Object> variables) throws CheckedException;
	public void removeVariable(String executionId, String variableName) throws CheckedException;
	public Map<String, Object> getVariablesLocal(String executionId) throws CheckedException;
	public Object getVariableLocal(String executionId, String variableName) throws CheckedException;
	public <T> T getVariableLocal(String executionId, String variableName, Class<T> variableClass) throws CheckedException;
	public boolean hasVariableLocal(String executionId, String variableName) throws CheckedException;
	public void setVariableLocal(String executionId, String variableName, Object value) throws CheckedException;
	public void setVariablesLocal(String executionId, Map<String, ? extends Object> variables) throws CheckedException;
	public void removeVariableLocal(String executionId, String variableName) throws CheckedException;
	
	public ProcessInstance getProcInct(String processInstanceId) throws CheckedException;
	public List<IdentityLink> getIdentityLinksForProcessInstance(String instanceId);
	
	public void removeProcInst(String processInstanceId, String deleteReason) throws CheckedException;
}
