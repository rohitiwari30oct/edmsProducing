package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.edms.workflow.GetAttachmentRequest;
import com.edms.workflow.GetAttachmentResponse;
import com.edms.workflow.GetAuthorizeUserRequest;
import com.edms.workflow.GetClaimTaskRequest;
import com.edms.workflow.GetContinueTaskRequest;
import com.edms.workflow.GetFetchGroupTaskRequest;
import com.edms.workflow.GetFetchGroupTaskResponse;
import com.edms.workflow.GetFetchUserTaskFromHistoryRequest;
import com.edms.workflow.GetFetchUserTaskFromHistoryResponse;
import com.edms.workflow.GetFetchUserTaskRequest;
import com.edms.workflow.GetFetchUserTaskResponse;
import com.edms.workflow.GetGenerateProcessImageRequest;
import com.edms.workflow.GetGenerateProcessImageResponse;
import com.edms.workflow.GetStartFormDataRequest;
import com.edms.workflow.GetStartFormDataResponse;
import com.edms.workflow.GetStartWorkflowRequest;
import com.edms.workflow.GetTaskFormDataFromHistoryRequest;
import com.edms.workflow.GetTaskFormDataFromHistoryResponse;
import com.edms.workflow.GetTaskFormDataRequest;
import com.edms.workflow.GetTaskFormDataResponse;
import com.edms.workflow.GetWorkFlowSnRequest;
import com.edms.workflow.GetWorkFlowSnResponse;
import com.edms.workflow.UploadAttachmentRequest;
import com.edms.workflow.UploadAttachmentResponse;
import com.edms.workflow.UserTaskListReturn;

@Endpoint
public class WorkflowEndpoint {
	
	private static final String NAMESPACE_URI = "http://edms.com/Workflow";
	
	private WorkflowRepository workflowRepository;
	
	@Autowired
	public WorkflowEndpoint (WorkflowRepository workflowRepository){
		this.workflowRepository = workflowRepository;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "uploadAttachmentRequest")
	@ResponsePayload
	public UploadAttachmentResponse uploadAttachment(@RequestPayload UploadAttachmentRequest request) {
		UploadAttachmentResponse response=	workflowRepository.uploadAttachment(request.getUserId(),request.getProcessInstId(),request.getFileName(),request.getFileContent());
		return response;
	}
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAttachmentRequest")
	@ResponsePayload
	public GetAttachmentResponse getAttachment(@RequestPayload GetAttachmentRequest request) {
		GetAttachmentResponse response=	workflowRepository.getAttachment(request.getUserId(),request.getAttachmentID());
		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAuthorizeUserRequest")
	@ResponsePayload
	public void authorizeUserRequest(@RequestPayload GetAuthorizeUserRequest request) {
		workflowRepository.authorizeUser(request.getUserId());
	}
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getStartWorkflowRequest")
	@ResponsePayload
	public void startWorkflowRequest(@RequestPayload GetStartWorkflowRequest request){
		workflowRepository.startWorkflow(request.getVariables(), request.getProcessKey(), request.getProcessName());
	}
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getGenerateProcessImageRequest")
	@ResponsePayload
	public GetGenerateProcessImageResponse generateProcessImageRequest(@RequestPayload GetGenerateProcessImageRequest request){
		GetGenerateProcessImageResponse response = new GetGenerateProcessImageResponse();
		response.setImage(workflowRepository.generateProcessImage(request.getProcessKey()));
		return response;
	}
		@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getFetchUserTaskRequest")
		@ResponsePayload
		public GetFetchUserTaskResponse fetchUserTaskRequest(@RequestPayload GetFetchUserTaskRequest request){
			GetFetchUserTaskResponse response = new GetFetchUserTaskResponse();
			response.setUserTaskListReturn(workflowRepository.fetchUserTask(request.getEmployeeId()));
			return response;
		}

		@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getFetchUserTaskFromHistoryRequest")
		@ResponsePayload
		public GetFetchUserTaskFromHistoryResponse fetchUserTaskFromHistoryRequest(@RequestPayload GetFetchUserTaskFromHistoryRequest request){
			GetFetchUserTaskFromHistoryResponse response = new GetFetchUserTaskFromHistoryResponse();
			response.setUserTaskListReturn(workflowRepository.fetchUserTaskFromHistory(request.getEmployeeId()));
			return response;
		}
		
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getFetchGroupTaskRequest")
	@ResponsePayload
	public GetFetchGroupTaskResponse fetchGroupTaskRequest(@RequestPayload GetFetchGroupTaskRequest request){
		GetFetchGroupTaskResponse response = new GetFetchGroupTaskResponse();
		response.setGroupTaskListReturn(workflowRepository.fetchGroupTasks(request.getDepartmentId()));
		return response;
	}
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getContinueTaskRequest")
	@ResponsePayload
	public void continueTaskRequest(@RequestPayload GetContinueTaskRequest request){
		workflowRepository.continueTask(request.getTaskVariables(), request.getTaskId());
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getTaskFormDataFromHistoryRequest")
	@ResponsePayload
	public GetTaskFormDataFromHistoryResponse taskFormDataFromHistoryRequest(@RequestPayload GetTaskFormDataFromHistoryRequest request){
		GetTaskFormDataFromHistoryResponse response = new GetTaskFormDataFromHistoryResponse();
		response.setTaskFormData(workflowRepository.getTaskFormDataFromHistory(request.getTaskId()));
		return response;
	}
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getTaskFormDataRequest")
	@ResponsePayload
	public GetTaskFormDataResponse taskFormDataRequest(@RequestPayload GetTaskFormDataRequest request){
		GetTaskFormDataResponse response = new GetTaskFormDataResponse();
		response.setTaskFormData(workflowRepository.getTaskFormData(request.getTaskId()));
		return response;
	}
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getStartFormDataRequest")
	@ResponsePayload
	public GetStartFormDataResponse startFormDataRequest(@RequestPayload GetStartFormDataRequest request){
		GetStartFormDataResponse response = new GetStartFormDataResponse();
		response.setStartFormData(workflowRepository.getStartFormData(request.getProcessKey()));
		return response;
	}
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getClaimTaskRequest")
	@ResponsePayload
	public void claimTaskRequest(@RequestPayload GetClaimTaskRequest request){
		workflowRepository.claimTask(request.getTaskId(), request.getUserId());
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getWorkFlowSnRequest")
	@ResponsePayload
	public GetWorkFlowSnResponse getWorkFlowSnRequest(@RequestPayload GetWorkFlowSnRequest request){
		GetWorkFlowSnResponse response=new GetWorkFlowSnResponse();
		response.setWorkFlowSN( workflowRepository.getWorkFlowSnNo(request.getWorkFlowFormName()));
		return response;
	}

}
