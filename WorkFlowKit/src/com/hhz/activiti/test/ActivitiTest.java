package com.hhz.activiti.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.hhz.activiti.IRepository;
import com.hhz.activiti.IRuntime;
import com.hhz.activiti.ITask;
import com.hhz.activiti.impl.RepositorySvr;
import com.hhz.activiti.impl.RuntimeSvr;
import com.hhz.activiti.impl.TaskSvr;
import com.hhz.kit.CheckedException;

public class ActivitiTest {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("ActivitiContext.xml");
		@SuppressWarnings("unused")
		IRepository repositorySvr = (RepositorySvr)context.getBean("repositorySvr");
		@SuppressWarnings("unused")
		IRuntime runtimeSvr = (RuntimeSvr)context.getBean("runtimeSvr");
		ITask taskSvr = (TaskSvr)context.getBean("taskSvr");
		ActivitiTest test = new ActivitiTest();
		try{
//			test.deloy(repositorySvr);
//			test.remove(repositorySvr);
//			test.startProcInct(runtimeSvr,repositorySvr);
//			test.removeProcInct(runtimeSvr);
//			test.getProcInct(runtimeSvr);
//			test.taskCalm(taskSvr);
//			test.taskComplete(taskSvr);
//			test.taskCalmformanagement(taskSvr);
			test.taskCompleteformanagement(taskSvr);
		}
		catch(CheckedException e){
			e.printStackTrace();
		}
	}
	public void deloy(IRepository svr) throws CheckedException{
		svr.deploy("com/hhz/activiti/design/FinancialReportProcess.bpmn20.xml.bpmn","Monthly financial report reminder process","http://activiti.hhz.com/bpmn20");
		System.out.println("Deloyment Success");
	}
	public void remove(IRepository svr) throws CheckedException{
		svr.removeDeloy("12501");
		System.out.println("Remove Deloyment Success");
	}
	public void startProcInct(IRuntime svr,IRepository rsvr) throws CheckedException{
		String processDefinitionKey = rsvr.getProcessDefinitionKey("1");
//		System.out.println(processDefinitionKey);
//		String processinctid = svr.startProcInst(processDefinitionKey);
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("employeeName", "Kermit");
		variables.put("numberOfDays", new Integer(4));
		variables.put("vacationMotivation", "I'm really tired!");

		String processinctid = svr.startProcInst(processDefinitionKey,"mouse",variables);
		//svr.addGroupIdentityLink(processinctid, "accountancy", "candidate");
		System.out.println("Starts a new process instance in the latest version of the process,实例代码：" + processinctid);
	}
	public void getProcInct(IRuntime svr) throws CheckedException{
//		System.out.println(processDefinitionKey);
		ProcessInstance pi = svr.getProcInct("5001");
		System.out.println("getId:" + pi.getId());
		System.out.println("getActivityId:" + pi.getActivityId());
		System.out.println("getProcessInstanceId:" + pi.getProcessInstanceId());
		System.out.println("getParentId:" + pi.getParentId());
		System.out.println("getTenantId:" + pi.getTenantId());
		System.out.println("getProcessDefinitionId:" + pi.getProcessDefinitionId());
		System.out.println("getProcessDefinitionName:" + pi.getProcessDefinitionName());
		System.out.println("getProcessDefinitionKey:" + pi.getProcessDefinitionKey());
		System.out.println("getProcessDefinitionVersion:" + pi.getProcessDefinitionVersion());
		System.out.println("getDeploymentId:" + pi.getDeploymentId());
		System.out.println("getBusinessKey:" + pi.getBusinessKey());
		System.out.println("isSuspended:" + pi.isSuspended());
		System.out.println("getProcessVariables:" + svr.getVariables(pi.getId()));
		List<IdentityLink> ilink = svr.getIdentityLinksForProcessInstance(pi.getId());
		for(IdentityLink link : ilink)
		{
			System.out.println("link:" + link);
		}
	}
	public void removeProcInct(IRuntime svr) throws CheckedException{
//		System.out.println(processDefinitionKey);
		svr.removeProcInst("5001","测试");
		System.out.println("Delete process instance by processinctid");
	}
	public void taskCalm(ITask svr) throws CheckedException{
		List<Task> tasks = svr.getTaskListByGroup("accountancy");
		for (Task task : tasks) {
		      System.out.println("Following task is available for accountancy group: " + task.getName());
		      svr.taskClaim(task.getId(), "fozzie");
		}

		System.out.println("taskClaim Success is UserID:fozzie");
	}
	public void taskComplete(ITask svr) throws CheckedException{
		List<Task> tasks = svr.getTaskListByUser("fozzie");
		for (Task task : tasks) {
			System.out.println("Task for fozzie: " + task.getName());
		      svr.taskComplete(task.getId());
		}
		tasks = svr.getTaskListByUser("fozzie");
		System.out.println("Number of tasks for fozzie: " + tasks.size());

	}
	public void taskCalmformanagement(ITask svr) throws CheckedException{
		List<Task> tasks = svr.getTaskListByGroup("management");
		for (Task task : tasks) {
		      System.out.println("Following task is available for management group: " + task.getName());
		      svr.taskClaim(task.getId(), "kermit");
		}

		System.out.println("taskClaim Success is UserID:kermit");
	}
	public void taskCompleteformanagement(ITask svr) throws CheckedException{
		List<Task> tasks = svr.getTaskListByUser("kermit");
		for (Task task : tasks) {
			System.out.println("Task for kermit: " + task.getName());
		      svr.taskComplete(task.getId());
		}
		tasks = svr.getTaskListByUser("kermit");
		System.out.println("Number of tasks for kermit: " + tasks.size());

	}
}
