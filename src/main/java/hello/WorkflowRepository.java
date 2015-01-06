package hello;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.activiti.engine.FormService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.edms.workflow.GroupTaskListReturn;
import com.edms.workflow.HashMap;
import com.edms.workflow.StartFormProperty;
import com.edms.workflow.TaskFormProperty;
import com.edms.workflow.UserTaskListReturn;

@Component
public class WorkflowRepository {

	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private IdentityService identityService;
	@Autowired
	private FormService formService;

	/*
	 * private static ProcessEngine processEngine; private static RuntimeService
	 * runtimeService; private static RepositoryService repositoryService;
	 * private static TaskService taskService; private static IdentityService
	 * identityService; private static FormService formService;
	 * 
	 * public static void setActivitiServices(){ processEngine =
	 * ProcessEngines.getDefaultProcessEngine(); runtimeService =
	 * processEngine.getRuntimeService(); repositoryService =
	 * processEngine.getRepositoryService(); taskService =
	 * processEngine.getTaskService(); identityService =
	 * processEngine.getIdentityService(); formService =
	 * processEngine.getFormService(); }
	 */

	public void authorizeUser(String uid) {
		identityService.setAuthenticatedUserId(uid);
	}

	public void startWorkflow(List<HashMap> hashMap, String processKey,
			String processName) {
		System.out.println("Number of process definitions: "
				+ repositoryService.createProcessDefinitionQuery().count());
		java.util.HashMap<String, Object> variables = new java.util.HashMap<String, Object>();
		for (HashMap hm : hashMap) {
			variables.put(hm.getKey(), hm.getValue());
		}
		ProcessInstance processInstance = runtimeService
				.startProcessInstanceByKey(processKey, variables);
		// Verify that we started a new process instance
		System.out.println("Number of process instances: "
				+ runtimeService.createProcessInstanceQuery().count());
		String id = processInstance.getProcessInstanceId();
		runtimeService.setProcessInstanceName(id, processName);
		System.out
				.println("The instance ID of the process created by the activiti= "
						+ id);
	}

	public String generateProcessImage(String processKey) {
		System.out.println("in generate image *****************************");
		String deploymentId = null;
		String diagramResourceName = null;
		String imageString = null;
		List<ProcessDefinition> processDefinition = repositoryService
				.createProcessDefinitionQuery().list();
		for (ProcessDefinition pd : processDefinition) {
			deploymentId = pd.getDeploymentId();
			diagramResourceName = pd.getDiagramResourceName();
			if (pd.getKey().equalsIgnoreCase(processKey)) {
				/*
				 * ProcessDefinitionEntity pde1 = (ProcessDefinitionEntity)
				 * repositoryService.createProcessDefinitionQuery().
				 * .processDefinitionId
				 * (pi.getProcessDefinitionId()).singleResult(); List<String>
				 * activities =
				 * runtimeService.getActiveActivityIds(processInstId);
				 * System.out.println("******* isGraphicalNotationDefined? " +
				 * pde.isGraphicalNotationDefined());
				 * System.out.println("******* diagramResourceName:  " +
				 * pde.getDiagramResourceName());
				 */// returns resource name from database
				InputStream is = repositoryService.getResourceAsStream(
						deploymentId, diagramResourceName);
				BufferedImage buffImg;
				try {
					buffImg = ImageIO.read(is);
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ImageIO.write(buffImg, "png", bos);
					byte[] imageBytes = bos.toByteArray();
					String encodedImage = org.apache.commons.codec.binary.Base64
							.encodeBase64String(imageBytes);
					imageString = "data:image/png;base64," + encodedImage;
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return imageString;
	}

	public UserTaskListReturn fetchUserTask(String empid) {
		System.out.println("using task assignee :-");
		List<Task> tasks = taskService.createTaskQuery().taskAssignee(empid)
				.list();
		System.out.println("assigne task size=" + tasks.size());
		UserTaskListReturn userTaskListReturn = new UserTaskListReturn();
		for (Task tk : tasks) {
			com.edms.workflow.Task tsk = new com.edms.workflow.Task();
			tsk.setAssignee(tk.getAssignee());
			tsk.setCategory(tk.getCategory());
			tsk.setDescription(tk.getDescription());
			tsk.setFormKey(tk.getFormKey());
			tsk.setId(tk.getId());
			tsk.setName(tk.getName());
			tsk.setProcessInstanceId(tk.getProcessInstanceId());
			userTaskListReturn.getUserTaskList().add(tsk);
		}
		return userTaskListReturn;
	}

	public GroupTaskListReturn fetchGroupTasks(String deptid) {
		List<Task> tasks = taskService.createTaskQuery()
				.taskCandidateGroup(deptid).list();
		GroupTaskListReturn groupTaskListReturn = new GroupTaskListReturn();
		for (Task tk : tasks) {
			com.edms.workflow.Task tsk = new com.edms.workflow.Task();
			tsk.setAssignee(tk.getAssignee());
			tsk.setCategory(tk.getCategory());
			tsk.setDescription(tk.getDescription());
			tsk.setFormKey(tk.getFormKey());
			tsk.setId(tk.getId());
			tsk.setName(tk.getName());
			tsk.setProcessInstanceId(tk.getProcessInstanceId());
			groupTaskListReturn.getGroupTaskList().add(tsk);
		}
		return groupTaskListReturn;
	}

	public void continueTask(List<HashMap> hashMap, String taskid) {
		System.out
				.println("in continue task &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& taskid= "
						+ taskid);
		System.out.println("sizew ====="+hashMap.size());
		java.util.HashMap<String, Object> taskVariables = new java.util.HashMap<String, Object>();
		for (HashMap hm : hashMap) {
			taskVariables.put(hm.getKey(), hm.getValue());
		}
		taskService.complete(taskid, taskVariables);
		System.out.println(taskid + " completed!");
	}

	public com.edms.workflow.TaskFormData getTaskFormData(String taskId) {
		System.out.println("taskid in service ******************* " + taskId);
		TaskFormData taskFormData = formService.getTaskFormData(taskId);
		System.out.println("task form data ============== " + taskFormData);
		System.out.println("form key = " + taskFormData.getFormKey());
		System.out.println("form properties size = "
				+ taskFormData.getFormProperties().size());
		List<org.activiti.engine.form.FormProperty> formProperties = taskFormData
				.getFormProperties();
		com.edms.workflow.TaskFormData tFormData = new com.edms.workflow.TaskFormData();
		tFormData.setFormKey(taskFormData.getFormKey());
		for (org.activiti.engine.form.FormProperty fp : formProperties) {
			TaskFormProperty formProp = new TaskFormProperty();
			formProp.setId(fp.getId());
			formProp.setName(fp.getName());
			formProp.setValue(fp.getValue());
			tFormData.getTaskFormProperties().add(formProp);
		}
		return tFormData;
	}

	public com.edms.workflow.StartFormData getStartFormData(String processKey) {
		String processDefinitionId = null;
		List<ProcessDefinition> processDefinition = repositoryService
				.createProcessDefinitionQuery().list();
		for (ProcessDefinition pd : processDefinition) {
			if (pd.getKey().equalsIgnoreCase(processKey)) {
				processDefinitionId = pd.getId();
			}
		}
		StartFormData startFormData = formService
				.getStartFormData(processDefinitionId);
		List<org.activiti.engine.form.FormProperty> formProperties = startFormData
				.getFormProperties();
		com.edms.workflow.StartFormData sFormData = new com.edms.workflow.StartFormData();
		sFormData.setFormKey(startFormData.getFormKey());
		for (org.activiti.engine.form.FormProperty fp : formProperties) {
			StartFormProperty formProp = new StartFormProperty();
			formProp.setId(fp.getId());
			formProp.setName(fp.getName());
			formProp.setValue(fp.getValue());
			sFormData.getStartFormProperties().add(formProp);
		}
		return sFormData;
	}

	public void claimTask(String taskId, String userId) {
		taskService.claim(taskId, userId);
	}

}
