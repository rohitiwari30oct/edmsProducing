package hello;

import com.edms.documentmodule.AddKeywordRequest;
import com.edms.documentmodule.AddKeywordResponse;
import com.edms.documentmodule.AssignSinglePermissionRequest;
import com.edms.documentmodule.AssignSinglePermissionResponse;
import com.edms.documentmodule.CreateFolderRequest;
import com.edms.documentmodule.CreateFolderResponse;
import com.edms.documentmodule.DeleteFolderRequest;
import com.edms.documentmodule.DeleteFolderResponse;
import com.edms.documentmodule.GetFolderByPathRequest;
import com.edms.documentmodule.GetFolderByPathResponse;
import com.edms.documentmodule.GetFolderRequest;
import com.edms.documentmodule.GetFolderResponse;
import com.edms.documentmodule.GetRecycledDocsRequest;
import com.edms.documentmodule.GetRecycledDocsResponse;
import com.edms.documentmodule.GetSharedFoldersByPathRequest;
import com.edms.documentmodule.GetSharedFoldersByPathResponse;
import com.edms.documentmodule.GetSharedFoldersRequest;
import com.edms.documentmodule.GetSharedFoldersResponse;
import com.edms.documentmodule.HasChildRequest;
import com.edms.documentmodule.HasChildResponse;
import com.edms.documentmodule.RecentlyModifiedRequest;
import com.edms.documentmodule.RecentlyModifiedResponse;
import com.edms.documentmodule.RecycleFolderRequest;
import com.edms.documentmodule.RecycleFolderResponse;
import com.edms.documentmodule.RemoveKeywordRequest;
import com.edms.documentmodule.RemoveKeywordResponse;
import com.edms.documentmodule.RenameFolderRequest;
import com.edms.documentmodule.RenameFolderResponse;
import com.edms.documentmodule.RestoreFolderRequest;
import com.edms.documentmodule.RestoreFolderResponse;
import com.edms.documentmodule.RestoreVersionRequest;
import com.edms.documentmodule.RestoreVersionResponse;
import com.edms.documentmodule.ShareFolderByPathRequest;
import com.edms.documentmodule.ShareFolderByPathResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class FolderEndpoint {
	private static final String NAMESPACE_URI = "http://edms.com/documentModule";

	private FolderRepository folderRepository;

	@Autowired
	public FolderEndpoint(FolderRepository folderRepository) {
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
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getSharedFoldersRequest")
	@ResponsePayload
	public GetSharedFoldersResponse getsharedFolders(@RequestPayload GetSharedFoldersRequest request) {
		GetSharedFoldersResponse response = new GetSharedFoldersResponse();
		response.setGetSharedFolders(folderRepository
				.listSharedFolder(request.getUserid()));
		return response;
	}
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getRecycledDocsRequest")
	@ResponsePayload
	public GetRecycledDocsResponse setGetRecycledDocs(@RequestPayload GetRecycledDocsRequest request) {
		GetRecycledDocsResponse response = new GetRecycledDocsResponse();
		response=folderRepository
				.listRecycledDoc(request.getUserid(),request.getPath());
		return response;
	}
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getSharedFoldersByPathRequest")
	@ResponsePayload
	public GetSharedFoldersByPathResponse getsharedFoldersByPath(@RequestPayload GetSharedFoldersByPathRequest request) {
		GetSharedFoldersByPathResponse response = new GetSharedFoldersByPathResponse();
		response.setGetSharedFoldersByPath(folderRepository
				.listSharedFolder(request.getUserid(),request.getPath()));
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
	//	System.out.println("in Endpoint");
		GetFolderByPathResponse response = new GetFolderByPathResponse();
		response.setFolder(folderRepository.getFolderByPath(
				request.getFolderPath(),request.getUserid()));
		return response;
	}
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "shareFolderByPathRequest")
	@ResponsePayload
	public ShareFolderByPathResponse shareFolderByPath(
			@RequestPayload ShareFolderByPathRequest request) {
		ShareFolderByPathResponse response = new ShareFolderByPathResponse();
		response.setShareResponse(folderRepository.shareFolderByPath(
				request.getFolderPath(),request.getUserid(),request.getUsers(),request.getGroups(),request.getUserpermissions()
				,request.getGrouppermissions()));
		return response;
	}
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "assignSinglePermissionRequest")
	@ResponsePayload
	public AssignSinglePermissionResponse assignSinglePermissionRequest(
			@RequestPayload AssignSinglePermissionRequest request) {
		AssignSinglePermissionResponse response = new AssignSinglePermissionResponse();
		response.setAssignSinglePermissionResponse(folderRepository.assignSinglePermission(
				request.getFolderPath(),request.getUserid(),request.getUser(),request.getValue()
				));
		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "recycleFolderRequest")
	@ResponsePayload
	public RecycleFolderResponse recycleFolderRequest(
			@RequestPayload RecycleFolderRequest request) {
		RecycleFolderResponse response = new RecycleFolderResponse();
		response.setRecycleFolderResponse(folderRepository.recycleFolder(request.getFolderPath(),request.getUserid()));
		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "addKeywordRequest")
	@ResponsePayload
	public AddKeywordResponse addKeywordRequest(
			@RequestPayload AddKeywordRequest request) {
		AddKeywordResponse response = new AddKeywordResponse();
		response.setSuccess(folderRepository.addKeyword(request.getFolderPath(),request.getUserid(),request.getKeyword()));
		return response;
	}
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "removeKeywordRequest")
	@ResponsePayload
	public RemoveKeywordResponse removeKeywordRequest(
			@RequestPayload RemoveKeywordRequest request) {
		RemoveKeywordResponse response = new RemoveKeywordResponse();
		response.setSuccess(folderRepository.removeKeyword(request.getFolderPath(),request.getUserid(),request.getKeyword()));
		return response;
	}
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteFolderRequest")
	@ResponsePayload
	public DeleteFolderResponse deleteFolderRequest(
			@RequestPayload DeleteFolderRequest request) {
		DeleteFolderResponse response = new DeleteFolderResponse();
		response.setDeleteFolderResponse(folderRepository.deleteFolder(request.getFolderPath(),request.getUserid()));
		return response;
	}
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "restoreFolderRequest")
	@ResponsePayload
	public RestoreFolderResponse restoreFolderRequest(
			@RequestPayload RestoreFolderRequest request) {
		RestoreFolderResponse response = new RestoreFolderResponse();
		response.setRestoreFolderResponse(folderRepository.restoreFolder(request.getFolderPath(),request.getUserid()));
		return response;
	}
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "renameFolderRequest")
	@ResponsePayload
	public RenameFolderResponse renameFolderRequest(
			@RequestPayload RenameFolderRequest request) {
		RenameFolderResponse response = new RenameFolderResponse();
		response.setRenameFolderRes(folderRepository.renameFolder(request.getOldName(),request.getNewName(),request.getUserid()));
		return response;
	}
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "restoreVersionRequest")
	@ResponsePayload
	public RestoreVersionResponse restoreVersionRequest(
			@RequestPayload RestoreVersionRequest request) {
		RestoreVersionResponse response = new RestoreVersionResponse();
		response.setRestoreVersionResponse(folderRepository.restoreVersion(request.getFolderPath(),request.getVersionName(),request.getUserid()));
		if(response.getRestoreVersionResponse().equals("success")){
		response.setSuccess(true);}else{
			response.setSuccess(false);
		}
		return response;
	}
	

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "recentlyModifiedRequest")
	@ResponsePayload
	public RecentlyModifiedResponse recentlyModifiedRequest(
			@RequestPayload RecentlyModifiedRequest request) {
		RecentlyModifiedResponse response = new RecentlyModifiedResponse();
		response=folderRepository.recentlyModified(request.getFolderPath(),request.getUserid());
		return response;
	}
	
	
	
	
	
	
	
}
