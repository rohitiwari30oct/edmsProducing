package edms.repository;

import com.edms.documentmodule.AddKeywordRequest;
import com.edms.documentmodule.AddKeywordResponse;
import com.edms.documentmodule.AddNotesRequest;
import com.edms.documentmodule.AddNotesResponse;
import com.edms.documentmodule.AssignSinglePermissionDAVRequest;
import com.edms.documentmodule.AssignSinglePermissionRequest;
import com.edms.documentmodule.AssignSinglePermissionResponse;
import com.edms.documentmodule.CheckDocExistRequest;
import com.edms.documentmodule.CopyDocRequest;
import com.edms.documentmodule.CopyDocResponse;
import com.edms.documentmodule.CreateEssentialFoldersRequest;
import com.edms.documentmodule.CreateEssentialFoldersResponse;
import com.edms.documentmodule.CreateFolderDAVRequest;
import com.edms.documentmodule.CreateFolderRequest;
import com.edms.documentmodule.CreateFolderResponse;
import com.edms.documentmodule.CreateWorkspaceRequest;
import com.edms.documentmodule.CreateWorkspaceResponse;
import com.edms.documentmodule.DeleteFolderRequest;
import com.edms.documentmodule.DeleteFolderResponse;
import com.edms.documentmodule.EditKeywordRequest;
import com.edms.documentmodule.EditKeywordResponse;
import com.edms.documentmodule.GetFolderByPathDAVRequest;
import com.edms.documentmodule.GetFolderByPathRequest;
import com.edms.documentmodule.GetFolderByPathResponse;
import com.edms.documentmodule.GetFolderRequest;
import com.edms.documentmodule.GetFolderDAVRequest;
import com.edms.documentmodule.GetFolderResponse;
import com.edms.documentmodule.GetFolderResponseDAV;
import com.edms.documentmodule.GetRecycledDocsRequest;
import com.edms.documentmodule.GetRecycledDocsResponse;
import com.edms.documentmodule.GetSharedFoldersByPathDAVRequest;
import com.edms.documentmodule.GetSharedFoldersByPathRequest;
import com.edms.documentmodule.GetSharedFoldersByPathResponse;
import com.edms.documentmodule.GetSharedFoldersRequest;
import com.edms.documentmodule.GetSharedFoldersResponse;
import com.edms.documentmodule.HasChildRequest;
import com.edms.documentmodule.HasChildResponse;
import com.edms.documentmodule.MoveDocRequest;
import com.edms.documentmodule.MoveDocResponse;
import com.edms.documentmodule.RecentlyModifiedRequest;
import com.edms.documentmodule.RecentlyModifiedResponse;
import com.edms.documentmodule.RecycleFolderDAVRequest;
import com.edms.documentmodule.RecycleFolderRequest;
import com.edms.documentmodule.RecycleFolderResponse;
import com.edms.documentmodule.RemoveAssignedPermissionDAVRequest;
import com.edms.documentmodule.RemoveAssignedPermissionRequest;
import com.edms.documentmodule.RemoveAssignedPermissionResponse;
import com.edms.documentmodule.RemoveKeywordRequest;
import com.edms.documentmodule.RemoveKeywordResponse;
import com.edms.documentmodule.RemoveNotesRequest;
import com.edms.documentmodule.RemoveNotesResponse;
import com.edms.documentmodule.RenameFolderRequest;
import com.edms.documentmodule.RenameFolderResponse;
import com.edms.documentmodule.RestoreFolderRequest;
import com.edms.documentmodule.RestoreFolderResponse;
import com.edms.documentmodule.RestoreVersionRequest;
import com.edms.documentmodule.RestoreVersionResponse;
import com.edms.documentmodule.SetSortOrderRequest;
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

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "createWorkspaceRequest")
	@ResponsePayload
	public CreateWorkspaceResponse createWorkspace(@RequestPayload CreateWorkspaceRequest request) {
		CreateWorkspaceResponse response = new CreateWorkspaceResponse();
		response.setResponseMessage(folderRepository
				.createWorkspace(request.getUserid(),request.getPassword(),request.getWorkspaceName()));
		return response;
	}
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "createEssentialFoldersRequest")
	@ResponsePayload
	public CreateEssentialFoldersResponse createEssentialFolders(@RequestPayload CreateEssentialFoldersRequest request) {
		CreateEssentialFoldersResponse response = new CreateEssentialFoldersResponse();
		response.setResponseMessage(folderRepository
				.createEssentialFolders(request.getUserid(),request.getPassword()));
		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getFolderRequest")
	@ResponsePayload
	public GetFolderResponse getFolder(@RequestPayload GetFolderRequest request) {
		GetFolderResponse response = new GetFolderResponse();
		response.setGetFoldersByParentFolder(folderRepository
				.listFolder(request.getFolderPath(),request.getUserid(),request.getPassword()));
		return response;
	}
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getFolderDAVRequest")
	@ResponsePayload
	public GetFolderResponse getFolderDAV(@RequestPayload GetFolderDAVRequest request) {
		GetFolderResponse response = new GetFolderResponse();
		response.setGetFoldersByParentFolder(folderRepository
				.listFolderDAV(request.getFolderPath(),request.getUserid(),request.getPassword()));
		return response;
	}
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getSharedFoldersRequest")
	@ResponsePayload
	public GetSharedFoldersResponse getsharedFolders(@RequestPayload GetSharedFoldersRequest request) {
		GetSharedFoldersResponse response = new GetSharedFoldersResponse();
		response.setGetSharedFolders(folderRepository
				.listSharedFolder(request.getUserid(),request.getPassword()));
		return response;
	}
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getRecycledDocsRequest")
	@ResponsePayload
	public GetRecycledDocsResponse setGetRecycledDocs(@RequestPayload GetRecycledDocsRequest request) {
		GetRecycledDocsResponse response = new GetRecycledDocsResponse();
		response=folderRepository
				.listRecycledDoc(request.getUserid(),request.getPassword(),request.getPath());
		return response;
	}
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getSharedFoldersByPathRequest")
	@ResponsePayload
	public GetSharedFoldersByPathResponse getsharedFoldersByPath(@RequestPayload GetSharedFoldersByPathRequest request) {
		GetSharedFoldersByPathResponse response = new GetSharedFoldersByPathResponse();
		response.setGetSharedFoldersByPath(folderRepository
				.listSharedFolder(request.getUserid(),request.getPassword(),request.getPath()));
		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getSharedFoldersByPathDAVRequest")
	@ResponsePayload
	public GetSharedFoldersByPathResponse getsharedFoldersByPathDAV(@RequestPayload GetSharedFoldersByPathDAVRequest request) {
		GetSharedFoldersByPathResponse response = new GetSharedFoldersByPathResponse();
		response.setGetSharedFoldersByPath(folderRepository
				.listSharedFolderDAV(request.getUserid(),request.getPassword(),request.getPath()));
		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "hasChildRequest")
	@ResponsePayload
	public HasChildResponse hasChild(@RequestPayload HasChildRequest request) {
		HasChildResponse response = new HasChildResponse();
		response.setHasChild(folderRepository.hasChild(request.getFolderPath(),request.getUserid(),request.getPassword()));
		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "createFolderRequest")
	@ResponsePayload
	public CreateFolderResponse createFolder(
			@RequestPayload CreateFolderRequest request) {
		CreateFolderResponse response = new CreateFolderResponse();
		response.setFolder(folderRepository.createFolder(
				request.getFolderName(), request.getParentFolder(),
				request.getUserid(),request.getPassword(),request.getKeywords(),request.getNotes()));
		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "createFolderDAVRequest")
	@ResponsePayload
	public CreateFolderResponse createFolderDAV(
			@RequestPayload CreateFolderDAVRequest request) {
		CreateFolderResponse response = new CreateFolderResponse();
		response.setFolder(folderRepository.createFolderDAV(
				request.getFolderName(), request.getParentFolder(),
				request.getUserid(),request.getPassword(),request.getKeywords(),request.getNotes()));
		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getFolderByPathRequest")
	@ResponsePayload
	public GetFolderByPathResponse getFolderByPath(
			@RequestPayload GetFolderByPathRequest request) {
	//	System.out.println("in Endpoint");
		GetFolderByPathResponse response = new GetFolderByPathResponse();
		response.setFolder(folderRepository.getFolderByPath(
				request.getFolderPath(),request.getUserid(),request.getPassword()));
		return response;
	}
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getFolderByPathDAVRequest")
	@ResponsePayload
	public GetFolderByPathResponse getFolderByPathDAV(
			@RequestPayload GetFolderByPathDAVRequest request) {
	//	System.out.println("in Endpoint");
		GetFolderByPathResponse response = new GetFolderByPathResponse();
		response.setFolder(folderRepository.getFolderByPathDAV(
				request.getFolderPath(),request.getUserid(),request.getPassword()));
		return response;
	}
	

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "assignSinglePermissionRequest")
	@ResponsePayload
	public AssignSinglePermissionResponse assignSinglePermissionRequest(
			@RequestPayload AssignSinglePermissionRequest request) {
		AssignSinglePermissionResponse response = new AssignSinglePermissionResponse();
		response.setAssignSinglePermissionResponse(folderRepository.assignSinglePermission(
				request.getFolderPath(),request.getUserid(),request.getPassword(),request.getUser(),request.getValue()
				));
		return response;
	}
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "assignSinglePermissionDAVRequest")
	@ResponsePayload
	public AssignSinglePermissionResponse assignSinglePermissionDAVRequest(
			@RequestPayload AssignSinglePermissionDAVRequest request) {
		AssignSinglePermissionResponse response = new AssignSinglePermissionResponse();
		response.setAssignSinglePermissionResponse(folderRepository.assignSinglePermissionDAV(
				request.getFolderPath(),request.getUserid(),request.getPassword(),request.getUser(),request.getValue()
				));
		return response;
	}
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "removeAssignedPermissionRequest")
	@ResponsePayload
	public RemoveAssignedPermissionResponse removeAssignedPermissionRequest(
			@RequestPayload RemoveAssignedPermissionRequest request) {
		RemoveAssignedPermissionResponse response = new RemoveAssignedPermissionResponse();
		response.setAssignSinglePermissionResponse(folderRepository.removeAssignedPermission(
				request.getFolderPath(),request.getUserid(),request.getPassword(),request.getUser(),request.getValue()
				));
		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "removeAssignedPermissionDAVRequest")
	@ResponsePayload
	public RemoveAssignedPermissionResponse removeAssignedPermissionDAVRequest(
			@RequestPayload RemoveAssignedPermissionDAVRequest request) {
		RemoveAssignedPermissionResponse response = new RemoveAssignedPermissionResponse();
		response.setAssignSinglePermissionResponse(folderRepository.removeAssignedPermissionDAV(
				request.getFolderPath(),request.getUserid(),request.getPassword(),request.getUser(),request.getValue()
				));
		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "recycleFolderRequest")
	@ResponsePayload
	public RecycleFolderResponse recycleFolderRequest(
			@RequestPayload RecycleFolderRequest request) {
		RecycleFolderResponse response = new RecycleFolderResponse();
		response.setRecycleFolderResponse(folderRepository.recycleFolder(request.getFolderPath(),request.getUserid(),request.getPassword()));
		
		return response;
	}
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "recycleFolderDAVRequest")
	@ResponsePayload
	public RecycleFolderResponse recycleFolderDAVRequest(
			@RequestPayload RecycleFolderDAVRequest request) {
		RecycleFolderResponse response = new RecycleFolderResponse();
		response.setRecycleFolderResponse(folderRepository.recycleFolderDAV(request.getFolderPath(),request.getUserid(),request.getPassword()));
		
		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "addKeywordRequest")
	@ResponsePayload
	public AddKeywordResponse addKeywordRequest(
			@RequestPayload AddKeywordRequest request) {
		AddKeywordResponse response = new AddKeywordResponse();
		response.setSuccess(folderRepository.addKeyword(request.getFolderPath(),request.getUserid(),request.getPassword(),request.getKeyword()));
		return response;
	}
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "removeKeywordRequest")
	@ResponsePayload
	public RemoveKeywordResponse removeKeywordRequest(
			@RequestPayload RemoveKeywordRequest request) {
		RemoveKeywordResponse response = new RemoveKeywordResponse();
		response.setSuccess(folderRepository.removeKeyword(request.getFolderPath(),request.getUserid(),request.getPassword(),request.getKeyword()));
		return response;
	}
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "editKeywordRequest")
	@ResponsePayload
	public EditKeywordResponse editKeywordRequest(
			@RequestPayload EditKeywordRequest request) {
		EditKeywordResponse response = new EditKeywordResponse();
		response.setSuccess(folderRepository.editKeyword(request.getFolderPath(),request.getUserid(),request.getPassword(),request.getKeyword(),request.getEditedKeyword()));
		return response;
	}
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "addNotesRequest")
	@ResponsePayload
	public AddNotesResponse addNotesRequest(
			@RequestPayload AddNotesRequest request) {
		AddNotesResponse response = new AddNotesResponse();
		response.setSuccess(folderRepository.addNote(request.getFolderPath(),request.getUserid(),request.getPassword(),request.getNote()));
		return response;
	}
/*	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "removeNotesRequest")
	@ResponsePayload
	public RemoveNotesResponse removeNotesRequest(
			@RequestPayload RemoveNotesRequest request) {
		RemoveNotesResponse response = new RemoveNotesResponse();
		response.setSuccess(folderRepository.removeNote(request.getFolderPath(),request.getUserid(),request.getPassword(),request.getNote()));
		return response;
	}*/
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteFolderRequest")
	@ResponsePayload
	public DeleteFolderResponse deleteFolderRequest(
			@RequestPayload DeleteFolderRequest request) {
		DeleteFolderResponse response = new DeleteFolderResponse();
		response.setDeleteFolderResponse(folderRepository.deleteFolder(request.getFolderPath(),request.getUserid(),request.getPassword()));
		return response;
	}
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "restoreFolderRequest")
	@ResponsePayload
	public RestoreFolderResponse restoreFolderRequest(
			@RequestPayload RestoreFolderRequest request) {
		RestoreFolderResponse response = new RestoreFolderResponse();
		response.setRestoreFolderResponse(folderRepository.restoreFolder(request.getFolderPath(),request.getUserid(),request.getPassword()));
		return response;
	}
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "renameFolderRequest")
	@ResponsePayload
	public RenameFolderResponse renameFolderRequest(
			@RequestPayload RenameFolderRequest request) {
		RenameFolderResponse response = new RenameFolderResponse();
		response.setRenameFolderRes(folderRepository.renameFolder(request.getOldName(),request.getNewName(),request.getUserid(),request.getPassword()));
		return response;
	}
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "restoreVersionRequest")
	@ResponsePayload
	public RestoreVersionResponse restoreVersionRequest(
			@RequestPayload RestoreVersionRequest request) {
		RestoreVersionResponse response = new RestoreVersionResponse();
		response.setRestoreVersionResponse(folderRepository.restoreVersion(request.getFolderPath(),request.getVersionName(),request.getUserid(),request.getPassword()));
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
		response=folderRepository.recentlyModified(request.getFolderPath(),request.getUserid(),request.getPassword());
		return response;
	}
	
	


	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "moveDocRequest")
	@ResponsePayload
	public MoveDocResponse moveDoc(
			@RequestPayload MoveDocRequest request) {
		MoveDocResponse response = new MoveDocResponse();
		response=folderRepository.moveDoc(request.getSrcDocPath(),request.getDestDocPath(),request.getUserid(),request.getPassword());
		return response;
	}
	
	

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "copyDocRequest")
	@ResponsePayload
	public CopyDocResponse copyDoc(
			@RequestPayload CopyDocRequest request) {
		CopyDocResponse response = new CopyDocResponse();
		response=folderRepository.copyDoc(request.getSrcDocPath(),request.getDestDocPath(),request.getUserid(),request.getPassword());
		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "setSortOrderRequest")
	@ResponsePayload
	public com.edms.documentmodule.SetSortOrderResponse SetSortOrderResponse(
			@RequestPayload SetSortOrderRequest request) {
		com.edms.documentmodule.SetSortOrderResponse response = new com.edms.documentmodule.SetSortOrderResponse();
		response=folderRepository.setSortOrder(request.getSortOrder(),request.getUserid(),request.getPassword());
		return response;
	}
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "checkDocExistRequest")
	@ResponsePayload
	public com.edms.documentmodule.CheckDocExistResponse checkDocExistRequest(
			@RequestPayload CheckDocExistRequest request) {
		com.edms.documentmodule.CheckDocExistResponse response = new com.edms.documentmodule.CheckDocExistResponse();
		response.setSuccess(folderRepository.checkExistDoc(request.getDocPath(),request.getUserid(),request.getPassword(),request.getParent()));
		return response;
	}
	
}
