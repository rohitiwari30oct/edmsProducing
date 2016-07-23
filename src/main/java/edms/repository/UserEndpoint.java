package edms.repository;

import com.edms.user.GetUsersListRequest;
import com.edms.user.GetUsersListResponse;

import com.edms.user.LoginRequest;
import com.edms.user.LoginResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class UserEndpoint {
	private static final String NAMESPACE_URI = "http://edms.com/User";

	private UserRepository userRepository;

	@Autowired
	public UserEndpoint(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "loginRequest")
	@ResponsePayload
	public LoginResponse loginRequest(@RequestPayload LoginRequest request) {
		LoginResponse response = new LoginResponse();
		response.setSuccess(userRepository
				.login(request.getUserid(),request.getPassword()));
		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getUsersListRequest")
	@ResponsePayload
	public GetUsersListResponse getUsersListRequest(@RequestPayload GetUsersListRequest request) {
		GetUsersListResponse response = new GetUsersListResponse();
		response=userRepository
				.getUsersListResponse(request.getUserid(),request.getPassword());
		return response;
	}


}
