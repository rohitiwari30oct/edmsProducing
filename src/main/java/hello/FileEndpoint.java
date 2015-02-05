package hello;

import com.edms.documentmodule.CreateFileRequest;
import com.edms.documentmodule.CreateFileResponse;
import com.edms.documentmodule.DeleteFileRequest;
import com.edms.documentmodule.DeleteFileResponse;
import com.edms.documentmodule.EditFileRequest;
import com.edms.documentmodule.EditFileResponse;
import com.edms.documentmodule.GetFileByPathRequest;
import com.edms.documentmodule.GetFileByPathResponse;
import com.edms.documentmodule.GetFileRequest;
import com.edms.documentmodule.GetFileResponse;
import com.edms.documentmodule.GetSharedFilesByPathRequest;
import com.edms.documentmodule.GetSharedFilesByPathResponse;
import com.edms.documentmodule.GetSharedFilesRequest;
import com.edms.documentmodule.GetSharedFilesResponse;
import com.edms.documentmodule.GetVCFFileRequest;
import com.edms.documentmodule.GetVCFFileResponse;
import com.edms.documentmodule.RecycleFileRequest;
import com.edms.documentmodule.RecycleFileResponse;
import com.edms.documentmodule.RenameFileRequest;
import com.edms.documentmodule.RenameFileResponse;
import com.edms.documentmodule.RestoreFileRequest;
import com.edms.documentmodule.RestoreFileResponse;
import com.edms.documentmodule.ShareFileByPathRequest;
import com.edms.documentmodule.ShareFileByPathResponse;
import com.edms.documentmodule.SortByPropertyRequest;
import com.edms.documentmodule.SortByPropertyResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class FileEndpoint {
	private static final String NAMESPACE_URI = "http://edms.com/documentModule";

	private FileRepository FileRepository;

	@Autowired
	public FileEndpoint(FileRepository FileRepository) {
		this.FileRepository = FileRepository;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getFileRequest")
	@ResponsePayload
	public GetFileResponse getFile(@RequestPayload GetFileRequest request) {
		GetFileResponse response = new GetFileResponse();
		response.setGetFilesByParentFile(FileRepository
				.listFile(request.getFilePath(),request.getUserid()));
		return response;
	}
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getSharedFilesRequest")
	@ResponsePayload
	public GetSharedFilesResponse getsharedFiles(@RequestPayload GetSharedFilesRequest request) {
		GetSharedFilesResponse response = new GetSharedFilesResponse();
		response.setGetSharedFiles(FileRepository
				.listSharedFile(request.getUserid()));
		return response;
	}
	/*@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getRecycledDocsRequest")
	@ResponsePayload
	public GetRecycledDocsResponse setGetRecycledDocs(@RequestPayload GetRecycledDocsRequest request) {
		GetRecycledDocsResponse response = new GetRecycledDocsResponse();
		response.setGetRecycledDocs(FileRepository
				.listRecycledDoc(request.getUserid(),request.getPath()));
		return response;
	}*/
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getSharedFilesByPathRequest")
	@ResponsePayload
	public GetSharedFilesByPathResponse getsharedFilesByPath(@RequestPayload GetSharedFilesByPathRequest request) {
		GetSharedFilesByPathResponse response = new GetSharedFilesByPathResponse();
		response.setGetSharedFilesByPath(FileRepository
				.listSharedFile(request.getUserid(),request.getPath()));
		return response;
	}


	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "createFileRequest")
	@ResponsePayload
	public CreateFileResponse createFile(
			@RequestPayload CreateFileRequest request) {
		CreateFileResponse response = FileRepository.createFile(
				request.getFileName(),request.getParentFile(),
				request.getUserid(),request.getKeywords(),request.getNotes(),request.getFileContent());
		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "editFileRequest")
	@ResponsePayload
	public EditFileResponse editFileRequest(
			@RequestPayload EditFileRequest request) {
		EditFileResponse response = FileRepository.editFile(
				request.getFileContent(),request.getFilePath(),
				request.getUserid());
		return response;
	}
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getFileByPathRequest")
	@ResponsePayload
	public GetFileByPathResponse getFileByPath(
			@RequestPayload GetFileByPathRequest request) {
		//System.out.println("in Endpoint");
		GetFileByPathResponse response = new GetFileByPathResponse();
		response.setFile(FileRepository.getFileByPath(
				request.getFilePath(),request.getUserid()));
		return response;
	}
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "shareFileByPathRequest")
	@ResponsePayload
	public ShareFileByPathResponse shareFileByPath(
			@RequestPayload ShareFileByPathRequest request) {
		ShareFileByPathResponse response = new ShareFileByPathResponse();
		response.setShareResponse(FileRepository.shareFileByPath(
				request.getFilePath(),request.getUserid(),request.getUsers(),request.getGroups(),request.getUserpermissions()
				,request.getGrouppermissions()));
		return response;
	}
	/*@PayloadRoot(namespace = NAMESPACE_URI, localPart = "assignSinglePermissionRequest")
	@ResponsePayload
	public AssignSinglePermissionResponse assignSinglePermissionRequest(
			@RequestPayload AssignSinglePermissionRequest request) {
		AssignSinglePermissionResponse response = new AssignSinglePermissionResponse();
		response.setAssignSinglePermissionResponse(FileRepository.assignSinglePermission(
				request.getFilePath(),request.getUserid(),request.getUser(),request.getValue()
				));
		return response;
	}*/
	/*@PayloadRoot(namespace = NAMESPACE_URI, localPart = "recycleFileRequest")
	@ResponsePayload
	public RecycleFileResponse recycleFileRequest(
			@RequestPayload RecycleFileRequest request) {
		RecycleFileResponse response = new RecycleFileResponse();
		response.setRecycleFileResponse(FileRepository.recycleFile(request.getFilePath(),request.getUserid()));
		return response;
	}*/
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteFileRequest")
	@ResponsePayload
	public DeleteFileResponse deleteFileRequest(
			@RequestPayload DeleteFileRequest request) {
		DeleteFileResponse response = new DeleteFileResponse();
		response.setDeleteFileResponse(FileRepository.deleteFile(request.getFilePath(),request.getUserid()));
		return response;
	}
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "restoreFileRequest")
	@ResponsePayload
	public RestoreFileResponse restoreFileRequest(
			@RequestPayload RestoreFileRequest request) {
		RestoreFileResponse response = new RestoreFileResponse();
		response.setRestoreFileResponse(FileRepository.restoreFile(request.getFilePath(),request.getUserid()));
		return response;
	}
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "renameFileRequest")
	@ResponsePayload
	public RenameFileResponse renameFileRequest(
			@RequestPayload RenameFileRequest request) {
		RenameFileResponse response = new RenameFileResponse();
		response.setRenameFileRes(FileRepository.renameFile(request.getOldName(),request.getNewName(),request.getUserid()));
		return response;
	}
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "sortByPropertyRequest")
	@ResponsePayload
	public SortByPropertyResponse sortByPropertyRequest(
			@RequestPayload SortByPropertyRequest request) {
		SortByPropertyResponse response = new SortByPropertyResponse();
		response.setSortByPropertyRes(FileRepository.sortByProperty(request.getPath(),request.getPropertyName(),request.getUserid()));
		return response;
	}
	

	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getVCFFileRequest")
	@ResponsePayload
	public GetVCFFileResponse getContactFileAtt(@RequestPayload GetVCFFileRequest request) {
		GetVCFFileResponse response = new GetVCFFileResponse();
		response.setGetVCFFilesByParentFile(FileRepository.getVCFFileAtt(request.getFilePath(), request.getUserid()));
		return response;
	}
	
}
