/*package hello;

import com.edms.documentmodule.CreateFolderRequest;
import com.edms.documentmodule.CreateFolderResponse;
import com.edms.documentmodule.GetFolderByPathRequest;
import com.edms.documentmodule.GetFolderByPathResponse;
import com.edms.documentmodule.GetFolderRequest;
import com.edms.documentmodule.GetFolderResponse;
import com.edms.documentmodule.HasChildRequest;
import com.edms.documentmodule.HasChildResponse;
import com.edms.documentmodule.ShareFolderByPathRequest;
import com.edms.documentmodule.ShareFolderByPathResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class GroupEndpoint {
	private static final String NAMESPACE_URI = "http://edms.com/Folder";

	private FolderRepository folderRepository;

	@Autowired
	public GroupEndpoint(FolderRepository folderRepository) {
		this.folderRepository = folderRepository;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getFolderRequest")
	@ResponsePayload
	public GetFolderResponse getFolder(@RequestPayload GetFolderRequest request) {
		GetFolderResponse response = new GetFolderResponse();
		response.setGetFoldersByParentFolder(folderRepository
				.listFolder(request.getFolderPath(),request.getUserid()));
		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "hasChildRequest")
	@ResponsePayload
	public HasChildResponse hasChild(@RequestPayload HasChildRequest request) {
		HasChildResponse response = new HasChildResponse();
		response.setHasChild(folderRepository.hasChild(request.getFolderPath(),request.getUserid()));
		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "createFolderRequest")
	@ResponsePayload
	public CreateFolderResponse createFolder(
			@RequestPayload CreateFolderRequest request) {
		CreateFolderResponse response = new CreateFolderResponse();
		response.setFolder(folderRepository.createFolder(
				request.getFolderName(), request.getParentFolder(),
				request.getUserid(),request.getKeywords(),request.getNotes()));
		return response;
	}
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getFolderByPathRequest")
	@ResponsePayload
	public GetFolderByPathResponse getFolderByPath(
			@RequestPayload GetFolderByPathRequest request) {
		System.out.println("in Endpoint");
		GetFolderByPathResponse response = new GetFolderByPathResponse();
		response.setFolder(folderRepository.getFolderByPath(
				request.getFolderPath(),request.getUserid()));
		return response;
	}
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "shareFolderByPathRequest")
	@ResponsePayload
	public ShareFolderByPathResponse shareFolderByPath(
			@RequestPayload ShareFolderByPathRequest request) {
		System.out.println("in Endpoint");
		ShareFolderByPathResponse response = new ShareFolderByPathResponse();
		response.setIsShared(folderRepository.shareFolderByPath(
				request.getFolderPath(),request.getUserid(),request.getFolderPath()));
		return response;
	}
}
*/