package hello;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.persistence.entity.HistoricDetailVariableInstanceUpdateEntity;
import org.activiti.engine.impl.persistence.entity.HistoricFormPropertyEntity;
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
import com.edms.workflowhistory.HistDetVrblInstUpdateEntity;
import com.edms.workflowhistory.HistDetVrblInstUpdateEntityReturn;
import com.edms.workflowhistory.HistTaskInstList;
import com.edms.workflowhistory.HistTaskInstListReturn;

@Component
public class WorkflowHistoryRepository {
	
	@Autowired
	private HistoryService historyService;
	
	public HistDetVrblInstUpdateEntityReturn getProcessVariables(String processInstId) {
		System.out.println("in process variable request %%%%%%%%%%%%%%%%%");
		HistDetVrblInstUpdateEntityReturn varUpdateEntityreturn = new HistDetVrblInstUpdateEntityReturn();
		List<HistoricDetail> historicDetail = historyService.createHistoricDetailQuery().processInstanceId(processInstId).orderByProcessInstanceId().desc().list();
		
		HistoricDetail hd;
		if((historicDetail.size())==3){
			//hd = historicDetail.get(0);
			for(HistoricDetail hdt:historicDetail){
				if (hdt instanceof HistoricFormPropertyEntity) {
					HistoricFormPropertyEntity formEntity = (HistoricFormPropertyEntity) hdt;
					System.out.println("form property id = "+formEntity.getPropertyId()+"property value  ="+formEntity.getPropertyValue());
				} else if (hdt instanceof HistoricDetailVariableInstanceUpdateEntity) {
					HistoricDetailVariableInstanceUpdateEntity varEntity = (HistoricDetailVariableInstanceUpdateEntity) hdt;
					System.out.println(String.format("variable->, key: %s, value: %s", varEntity.getName(), varEntity.getValue()));
					if(varEntity.getName().equals("xmlStringForm")){
						HistDetVrblInstUpdateEntity hdv = new HistDetVrblInstUpdateEntity();
						hdv.setName(varEntity.getName());
						hdv.setValue(varEntity.getValue());
					varUpdateEntityreturn.setHistDetVrblInstUpdateEntity(hdv);
						return varUpdateEntityreturn;
					}
				}
			}
		} else {
			
			hd = historicDetail.get(0);
			
			if (hd instanceof HistoricFormPropertyEntity) {
				HistoricFormPropertyEntity formEntity = (HistoricFormPropertyEntity) hd;
				System.out.println("form property id = "+formEntity.getPropertyId()+"property value  ="+formEntity.getPropertyValue());
			} else if (hd instanceof HistoricDetailVariableInstanceUpdateEntity) {
				HistoricDetailVariableInstanceUpdateEntity varEntity = (HistoricDetailVariableInstanceUpdateEntity) hd;
				System.out.println(String.format("variable->, key: %s, value: %s", varEntity.getName(), varEntity.getValue()));
				HistDetVrblInstUpdateEntity hdv = new HistDetVrblInstUpdateEntity();
				hdv.setName(varEntity.getName());
				hdv.setValue(varEntity.getValue());
			varUpdateEntityreturn.setHistDetVrblInstUpdateEntity(hdv);
					return varUpdateEntityreturn;
			}
		}
		return null;
	}

	public HistTaskInstListReturn getHistoryTaskInstance(String taskId) {
		List<HistoricTaskInstance> histTaskInstt = historyService.createHistoricTaskInstanceQuery().taskId(taskId).list();
		System.out.println("historic task instance size = "+histTaskInstt.size());
		HistTaskInstListReturn histTaskInstListReturn = new HistTaskInstListReturn();
		for (HistoricTaskInstance hti:histTaskInstt){
			System.out.println("process instance id = "+hti.getProcessInstanceId());
			HistTaskInstList ht = new HistTaskInstList();
			ht.setProcessInstanceId(hti.getProcessInstanceId());
			histTaskInstListReturn.getHistTaskInstList().add(ht);
		}
		return histTaskInstListReturn;
	}
}
