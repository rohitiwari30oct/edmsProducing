package hello;

import com.edms.folder.AssignSinglePermissionRequest;
import com.edms.folder.AssignSinglePermissionResponse;
import com.edms.folder.CreateFolderRequest;
import com.edms.folder.CreateFolderResponse;
import com.edms.folder.DeleteFolderRequest;
import com.edms.folder.DeleteFolderResponse;
import com.edms.folder.GetFolderByPathRequest;
import com.edms.folder.GetFolderByPathResponse;
import com.edms.folder.GetFolderRequest;
import com.edms.folder.GetFolderResponse;
import com.edms.folder.GetRecycledDocsRequest;
import com.edms.folder.GetRecycledDocsResponse;
import com.edms.folder.GetSharedFoldersByPathRequest;
import com.edms.folder.GetSharedFoldersByPathResponse;
import com.edms.folder.GetSharedFoldersRequest;
import com.edms.folder.GetSharedFoldersResponse;
import com.edms.folder.HasChildRequest;
import com.edms.folder.HasChildResponse;
import com.edms.folder.RecycleFolderRequest;
import com.edms.folder.RecycleFolderResponse;
import com.edms.folder.RenameFolderRequest;
import com.edms.folder.RenameFolderResponse;
import com.edms.folder.RestoreFolderRequest;
import com.edms.folder.RestoreFolderResponse;
import com.edms.folder.RestoreVersionRequest;
import com.edms.folder.RestoreVersionResponse;
import com.edms.folder.ShareFolderByPathRequest;
import com.edms.folder.ShareFolderByPathResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class FolderEndpoint {
	private static final String NAMESPACE_URI = "http://edms.com/Folder";

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
		response.setGetRecycledDocs(folderRepository
				.listRecycledDoc(request.getUserid(),request.getPath()));
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
}
