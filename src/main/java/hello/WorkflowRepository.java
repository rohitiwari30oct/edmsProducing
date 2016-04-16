package hello;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.persistence.entity.HistoricDetailVariableInstanceUpdateEntity;
import org.activiti.engine.impl.persistence.entity.HistoricFormPropertyEntity;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Task;
import org.apache.commons.io.IOUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.service.ServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.edms.workflow.GetAttachmentResponse;
import com.edms.entity.SnTable;
import com.edms.workflow.GroupTaskListReturn;
import com.edms.workflow.HashMap;
import com.edms.workflow.StartFormProperty;
import com.edms.workflow.TaskFormProperty;
import com.edms.workflow.UploadAttachmentResponse;
import com.edms.workflow.UserTaskListReturn;
import com.edms.workflowhistory.HistDetVrblInstUpdateEntity;
import com.edms.workflowhistory.HistDetVrblInstUpdateEntityReturn;

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
	@Autowired
	private WebServiceConfig webServiceConfig;
	@Autowired
	private HistoryService historyService;
	

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
		// identityService.checkPassword("sanjay", "redhat");
		// System.out.println();
	}

	public UploadAttachmentResponse uploadAttachment(String uid, String id, String fileName, byte[] bs) {
		UploadAttachmentResponse res=new UploadAttachmentResponse();
		byte[] decodedBaseData=org.apache.commons.codec.binary.Base64.decodeBase64(bs);
		InputStream	isss= new ByteArrayInputStream(decodedBaseData);
		Attachment attachment = taskService.createAttachment("binaryType", null, id, "attachment","description",isss);
		attachment.setDescription("description");
		attachment.setName(fileName);
		taskService.saveAttachment(attachment);
		res.setEmployeeId(attachment.getId());
		return res;
	}
	public GetAttachmentResponse getAttachment(String uid, String id) {
		GetAttachmentResponse res=new GetAttachmentResponse();
		//System.out.println(id + " attachment.");
		InputStream in = taskService.getAttachmentContent(id);
		//taskService.get
		Attachment att=taskService.getAttachment(id);

		byte[] imageBytes = null;
		try {
			imageBytes = IOUtils.toByteArray(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] encodedBaseData=org.apache.commons.codec.binary.Base64.encodeBase64(imageBytes);
		res.setFileContent(encodedBaseData);
		res.setFileName(att.getName());
		System.out.println();
		return res;
	}
	public void startWorkflow(List<HashMap> hashMap, String processKey, String processName)  {
		System.out
				.println(" Number of process definitions: " + repositoryService.createProcessDefinitionQuery().count());
		
		java.util.HashMap<String, Object> variables = new java.util.LinkedHashMap<String, Object>();
		for (HashMap hm : hashMap) {
			variables.put(hm.getKey(), hm.getValue());
		}
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processKey, variables);
		System.out.println("Number of process instances: " + runtimeService.createProcessInstanceQuery().count());
		String id = processInstance.getProcessInstanceId();
		runtimeService.setProcessInstanceName(id, processName);
		System.out.println("The instance ID of the process created by the activiti= " + id);

	}

	public String generateProcessImage(String processKey) {
		System.out.println("in generate image *****************************");
		String deploymentId = null;
		String diagramResourceName = null;
		String imageString = null;
		List<ProcessDefinition> processDefinition = repositoryService.createProcessDefinitionQuery().list();
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
				 * pde.isGraphicalNotationDefined()); System.out.println(
				 * "******* diagramResourceName:  " +
				 * pde.getDiagramResourceName());
				 */// returns resource name from database
				InputStream is = repositoryService.getResourceAsStream(deploymentId, diagramResourceName);
				BufferedImage buffImg;
				try {
					buffImg = ImageIO.read(is);
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ImageIO.write(buffImg, "png", bos);
					byte[] imageBytes = bos.toByteArray();
					String encodedImage = org.apache.commons.codec.binary.Base64.encodeBase64String(imageBytes);
					imageString = "data:image/png;base64," + encodedImage;
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return imageString;
	}

	public UserTaskListReturn fetchUserTaskFromHistory(String empid) {
		/*List<HistoricProcessInstance> processes = historyService.createHistoricProcessInstanceQuery()
				  .finished()
				  .involvedUser(empid).list();*/
		System.out.println("using task assignee :- " + empid);
		List<HistoricTaskInstance> tasks=new ArrayList<HistoricTaskInstance>();
		try {
			tasks = historyService.createHistoricTaskInstanceQuery()
					  				  .finished().taskCandidateUser(empid).includeTaskLocalVariables().includeProcessVariables().list();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			tasks = historyService.createHistoricTaskInstanceQuery()
	  				  .finished().taskCandidateUser(empid).list();
		}
		ArrayList<String> processList=new ArrayList<String>();
		List<HistoricVariableInstance> tasks1 = historyService.createHistoricVariableInstanceQuery().variableName("employeeID").list();
		String processInstId="";
		UserTaskListReturn userTaskListReturn = new UserTaskListReturn();
		for (HistoricVariableInstance historicVariableInstance : tasks1) {
			if(historicVariableInstance.getValue()!=null&&historicVariableInstance.getValue().equals(empid)){
				processInstId=historicVariableInstance.getProcessInstanceId();
		List<HistoricTaskInstance> tasks2=historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstId).includeTaskLocalVariables().includeProcessVariables().list();
		if(tasks2.size()>0){
			HistoricTaskInstance tk=(HistoricTaskInstance)tasks2.get(tasks2.size()-1);
					com.edms.workflow.Task tsk = new com.edms.workflow.Task();
					if(!processList.contains(tk.getProcessInstanceId())){
						if(tk.getProcessVariables().get("employeeID")!=null)
						tsk.setAssignee((String)tk.getProcessVariables().get("employeeID"));
						else
							tsk.setAssignee("--");
						if(tk.getProcessVariables().get("filledDaete")!=null)
							tsk.setCategory((String)tk.getProcessVariables().get("filledDaete"));
							else
								tsk.setCategory("--");
								if(tk.getProcessVariables().get("formSStatus")!=null)
									tsk.setDescription((String)tk.getProcessVariables().get("formSStatus"));
								else
									tsk.setDescription(tk.getDescription());
						tsk.setFormKey(tk.getFormKey());
						tsk.setId(tk.getId());
						tsk.setName(tk.getName());
						tsk.setProcessInstanceId(tk.getProcessInstanceId());
				/*	tsk.setAssignee(tk.getAssignee());
					tsk.setCategory(tk.getCategory());
					tsk.setDescription(tk.getDescription());
					tsk.setFormKey(tk.getFormKey());
					tsk.setId(tk.getId());
					tsk.setName(tk.getName());
					tsk.setProcessInstanceId(tk.getProcessInstanceId());*/
					userTaskListReturn.getUserTaskList().add(tsk);
					processList.add(tk.getProcessInstanceId());}
		}
			}
		}
				System.out.println();
		/*
		for (HistoricProcessInstance historicProcessInstance : tasks1) {

		List<HistoricDetail> historicDetail = historyService.createHistoricDetailQuery().processInstanceId(historicProcessInstance.getId()).orderByProcessInstanceId().desc().list();
		
		HistoricDetail hd;
		int i=0;int c=0; 
		for (HistoricDetail historicDetail2 : historicDetail) {
			HistoricDetailVariableInstanceUpdateEntity entity=(HistoricDetailVariableInstanceUpdateEntity)historicDetail2;
			
			if(entity.getName().equals("xmlStringForm")){
				//break;
				c=i;
			}
			i++;
		}
		HistoricDetail hdt=historicDetail.get(c);
		if (hdt instanceof HistoricFormPropertyEntity) {
				HistoricFormPropertyEntity formEntity = (HistoricFormPropertyEntity) hdt;
			} else if (hdt instanceof HistoricDetailVariableInstanceUpdateEntity) {
				HistoricDetailVariableInstanceUpdateEntity varEntity = (HistoricDetailVariableInstanceUpdateEntity) hdt;
					HistDetVrblInstUpdateEntity hdv = new HistDetVrblInstUpdateEntity();
					hdv.setName(varEntity.getName());
					hdv.setValue(varEntity.getValue());
			}
		com.edms.workflow.Task tsk = new com.edms.workflow.Task();
		tsk.setAssignee("");
		tsk.setCategory("");
		tsk.setDescription(tk.getDescription());
		tsk.setFormKey(tk.getFormKey());
		tsk.setId(tk.getId());
		tsk.setName(tk.getName());
		tsk.setProcessInstanceId(tk.getProcessInstanceId());
		userTaskListReturn.getUserTaskList().add(tsk);
		
		
		
		}
		
		HistoricProcessInstance hst=tasks1.get(0);
		Map<String,Object> sl=hst.getProcessVariables();*/
		System.out.println("assigne task size = " + tasks.size());
		// List<Attachment>
		// lst=taskService.getProcessInstanceAttachments(tasks.get(0).getProcessInstanceId());
		for (HistoricTaskInstance tk : tasks) {
			com.edms.workflow.Task tsk = new com.edms.workflow.Task();
			if(!processList.contains(tk.getProcessInstanceId())){
				if(tk.getProcessVariables().get("employeeID")!=null)
				tsk.setAssignee((String)tk.getProcessVariables().get("employeeID"));
				else
					tsk.setAssignee("--");
				if(tk.getProcessVariables().get("filledDaete")!=null)
					tsk.setCategory((String)tk.getProcessVariables().get("filledDaete"));
					else
						tsk.setCategory("--");
						if(tk.getProcessVariables().get("formSStatus")!=null)
							tsk.setDescription((String)tk.getProcessVariables().get("formSStatus"));
						else
							tsk.setDescription(tk.getDescription());
				tsk.setFormKey(tk.getFormKey());
				tsk.setId(tk.getId());
				tsk.setName(tk.getName());
				tsk.setProcessInstanceId(tk.getProcessInstanceId());
		/*	tsk.setAssignee(tk.getAssignee());
			tsk.setCategory(tk.getCategory());
			tsk.setDescription(tk.getDescription());
			tsk.setFormKey(tk.getFormKey());
			tsk.setId(tk.getId());
			tsk.setName(tk.getName());
			tsk.setProcessInstanceId(tk.getProcessInstanceId());*/
			userTaskListReturn.getUserTaskList().add(tsk);
			processList.add(tk.getProcessInstanceId());}
}
		return userTaskListReturn;
	}
	
	public UserTaskListReturn fetchUserTask(String empid) {
		System.out.println("using task assignee :- " + empid);
		List<Task> tasks = taskService.createTaskQuery().taskCandidateOrAssigned(empid).includeTaskLocalVariables().includeProcessVariables().list();
		
		System.out.println("assigne task size = " + tasks.size());
		UserTaskListReturn userTaskListReturn = new UserTaskListReturn();
		
		
		
		// List<Attachment>
		// lst=taskService.getProcessInstanceAttachments(tasks.get(0).getProcessInstanceId());
		for (Task tk : tasks) {
			com.edms.workflow.Task tsk = new com.edms.workflow.Task();
			
			//tsk.setAssignee(tk.getAssignee());

			if(tk.getProcessVariables().get("employeeID")!=null)
			tsk.setAssignee((String)tk.getProcessVariables().get("employeeID"));
			else
				tsk.setAssignee("--");
			if(tk.getProcessVariables().get("filledDaete")!=null)
				tsk.setCategory((String)tk.getProcessVariables().get("filledDaete"));
				else
					tsk.setCategory("--");
			//tsk.setCategory(tk.getCategory());

			if(tk.getProcessVariables().get("formSStatus")!=null)
				tsk.setDescription((String)tk.getProcessVariables().get("formSStatus"));
			else
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
		List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup(deptid).list();
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
		System.out.println("in continue task &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& taskid= " + taskid);
		System.out.println("sizew =====" + hashMap.size());
		java.util.HashMap<String, Object> taskVariables = new java.util.LinkedHashMap<String, Object>();
		//tring processId=		taskService.getTaskEvents(taskid).get(0).getProcessInstanceId();
		for (HashMap hm : hashMap) {
				taskVariables.put(hm.getKey(), hm.getValue());
		}
		try{
		taskService.complete(taskid, taskVariables);
		}catch(Exception e){
			e.printStackTrace();
			taskService.complete(taskid, taskVariables);
		}
		System.out.println(taskid + " completed!");
	}

	public com.edms.workflow.TaskFormData getTaskFormData(String taskId) {
		
	
		System.out.println("taskid in service ******************* " + taskId);
		TaskFormData taskFormData = formService.getTaskFormData(taskId);
		System.out.println("task form data ============== " + taskFormData);
		System.out.println("form key = " + taskFormData.getFormKey());
		System.out.println("form properties size = " + taskFormData.getFormProperties().size());
		List<org.activiti.engine.form.FormProperty> formProperties = taskFormData.getFormProperties();
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
	public com.edms.workflow.TaskFormData getTaskFormDataFromHistory(String taskId) {
		List<HistoricVariableInstance> list = historyService.createHistoricVariableInstanceQuery().executionId(taskId)
			  .list();

	com.edms.workflow.TaskFormData tFormData = new com.edms.workflow.TaskFormData();
	
	for (HistoricVariableInstance historicDetail : list) {
		if(historicDetail.getVariableName().indexOf("xmlStringForm")>=0){
			TaskFormProperty formProp = new TaskFormProperty();
			formProp.setId(historicDetail.getId());
			formProp.setName(historicDetail.getVariableName());
			formProp.setValue(historicDetail.getValue().toString());
			
		tFormData.getTaskFormProperties().add(formProp);
		}
	    //System.out.println(historicDetail.getVariableName() + " = " + historicDetail.getValue());
	}
	List<HistoricTaskInstance> list1 = historyService.createHistoricTaskInstanceQuery().executionId(taskId).list();
	
	
	for (HistoricTaskInstance historicDetail : list1) {
		
		
	   // HistoricDetailVariableInstanceUpdateEntity variable = (HistoricDetailVariableInstanceUpdateEntity) historicDetail;
		if(historicDetail.getExecutionId().equals(taskId)){
			
String formkey=historicDetail.getFormKey();
if(formkey.indexOf("IOMForm")>=0){
	tFormData.setFormKey("handleIOMFormHistory");
}

if(formkey.indexOf("PurchaseRequisitionApplicationForm")>=0){
	tFormData.setFormKey("handlePurchaseRequisitionApplicationFormHistory");
}
if(formkey.indexOf("PurchaseRequisitionApplicationFormHO")>=0){
	tFormData.setFormKey("handlePurchaseRequisitionApplicationFormHOHistory");
}
if(formkey.indexOf("CashPaymentVoucher")>=0){
	tFormData.setFormKey("handleCashPaymentVoucherFormHistory");
}
if(formkey.indexOf("ravelExpensesReimbursement")>=0){
	tFormData.setFormKey("travelExpensesReimbursementHistory");
}
if(formkey.indexOf("edicalExpensesReimbursement")>=0){
	tFormData.setFormKey("medicalExpensesReimbursementHistory");
}
if(formkey.indexOf("eaveApplicationForm")>=0){
	tFormData.setFormKey("leaveApplicationFormHistory");
}

		}
	  //  System.out.println(historicDetail.getFormKey() + " = " + historicDetail.getFormKey());
	   // historicDetail.getVariableName().
	}
	System.out.println("");
	
/*		System.out.println("taskid in service ******************* " + taskId);
	TaskFormData taskFormData = formService.getTaskFormData(taskId);
	System.out.println("task form data ============== " + taskFormData);
	System.out.println("form key = " + taskFormData.getFormKey());
	System.out.println("form properties size = " + taskFormData.getFormProperties().size());
	List<org.activiti.engine.form.FormProperty> formProperties = taskFormData.getFormProperties();
	tFormData = new com.edms.workflow.TaskFormData();
	tFormData.setFormKey(taskFormData.getFormKey());
	for (org.activiti.engine.form.FormProperty fp : formProperties) {
		TaskFormProperty formProp = new TaskFormProperty();
		formProp.setId(fp.getId());
		formProp.setName(fp.getName());
		formProp.setValue(fp.getValue());
		tFormData.getTaskFormProperties().add(formProp);
	}*/
	return tFormData;
	
	}
	
	public com.edms.workflow.StartFormData getStartFormData(String processKey) {
		String processDefinitionId = null;
		List<ProcessDefinition> processDefinition = repositoryService.createProcessDefinitionQuery().list();
		for (ProcessDefinition pd : processDefinition) {
			if (pd.getKey().equalsIgnoreCase(processKey)) {
				processDefinitionId = pd.getId();
			}
		}
		StartFormData startFormData = formService.getStartFormData(processDefinitionId);
		List<org.activiti.engine.form.FormProperty> formProperties = startFormData.getFormProperties();
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

	public String getWorkFlowSnNo(String formName) {
		
		int snno=0;
		//SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		
		SessionFactory sessionFactory=webServiceConfig.buildSessionFactory();
				
				Session session = sessionFactory.openSession();
				String hql = "from SnTable where formName like :keyword";
				 
		try{		
				Query query = session.createQuery(hql);
				query.setParameter("keyword", "%" + formName + "%");
				 
				 List<SnTable> snlst = query.list();
				 
				
				if(snlst.size()>0)
				{
					for(SnTable st: snlst)
					{
						snno=(st.getFormNo()+1);
						st.setFormNo(snno);
						session.beginTransaction();
						session.saveOrUpdate(st);
						session.getTransaction().commit();
						session.close();
						break;
					}
				}
				else
				{
					SnTable snt=new SnTable();
					snno=1;
					snt.setFormNo(snno);
					snt.setFormName(formName);
					session.beginTransaction();
					session.saveOrUpdate(snt);
					session.getTransaction().commit();
					session.close();
				}
				}catch(Exception e){
			
		}finally{
			//session.close();
			//sessionFactory.close();
		}
				/*Session session = sessionFactory.openSession();
				session.beginTransaction();
				
				SnTable snt=new SnTable();
				snt.setFormName("hii");
				snt.setFormNo(1);
				session.save(snt);
				session.getTransaction().commit();*/
		
		return ""+snno;
	}
}
