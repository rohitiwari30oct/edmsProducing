/*package hello;

import com.edms.folder.CreateFolderRequest;
import com.edms.folder.CreateFolderResponse;
import com.edms.folder.GetFolderByPathRequest;
import com.edms.folder.GetFolderByPathResponse;
import com.edms.folder.GetFolderRequest;
import com.edms.folder.GetFolderResponse;
import com.edms.folder.HasChildRequest;
import com.edms.folder.HasChildResponse;
import com.edms.folder.ShareFolderByPathRequest;
import com.edms.folder.ShareFolderByPathResponse;

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