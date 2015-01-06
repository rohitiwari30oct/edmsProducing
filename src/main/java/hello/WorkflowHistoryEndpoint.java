package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.edms.workflowhistory.GetHistoryTaskInstanceRequest;
import com.edms.workflowhistory.GetHistoryTaskInstanceResponse;
import com.edms.workflowhistory.GetProcessVariablesRequest;
import com.edms.workflowhistory.GetProcessVariablesResponse;

@Endpoint
public class WorkflowHistoryEndpoint {
	
	private static final String NAMESPACE_URI = "http://edms.com/WorkflowHistory";
	
	private WorkflowHistoryRepository workflowHistoryRepository;
	
	@Autowired
	public WorkflowHistoryEndpoint (WorkflowHistoryRepository workflowHistoryRepository){
		this.workflowHistoryRepository = workflowHistoryRepository;
	}
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getProcessVariablesRequest")
	@ResponsePayload
	public GetProcessVariablesResponse processVariablesRequest(@RequestPayload GetProcessVariablesRequest request) {
		GetProcessVariablesResponse response = new GetProcessVariablesResponse();
		response.setHistDetVrblInstUpdateEntityReturn(workflowHistoryRepository.getProcessVariables(request.getProcessInstId()));
		return response;
	}
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getHistoryTaskInstanceRequest")
	@ResponsePayload
	public GetHistoryTaskInstanceResponse historyTaskInstanceRequest(@RequestPayload GetHistoryTaskInstanceRequest request) {
		GetHistoryTaskInstanceResponse response = new GetHistoryTaskInstanceResponse();
		response.setHistTaskInstListReturn(workflowHistoryRepository.getHistoryTaskInstance(request.getTaskId()));
		return response;
	}
	

}
