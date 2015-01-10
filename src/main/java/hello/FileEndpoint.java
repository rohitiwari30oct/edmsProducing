package hello;

import com.edms.file.AssignSinglePermissionRequest;
import com.edms.file.AssignSinglePermissionResponse;
import com.edms.file.CreateFileRequest;
import com.edms.file.CreateFileResponse;
import com.edms.file.DeleteFileRequest;
import com.edms.file.DeleteFileResponse;
import com.edms.file.GetFileByPathRequest;
import com.edms.file.GetFileByPathResponse;
import com.edms.file.GetFileRequest;
import com.edms.file.GetFileResponse;
import com.edms.file.GetRecycledDocsRequest;
import com.edms.file.GetRecycledDocsResponse;
import com.edms.file.GetSharedFilesByPathRequest;
import com.edms.file.GetSharedFilesByPathResponse;
import com.edms.file.GetSharedFilesRequest;
import com.edms.file.GetSharedFilesResponse;
import com.edms.file.HasChildRequest;
import com.edms.file.HasChildResponse;
import com.edms.file.RecycleFileRequest;
import com.edms.file.RecycleFileResponse;
import com.edms.file.RenameFileRequest;
import com.edms.file.RenameFileResponse;
import com.edms.file.RestoreFileRequest;
import com.edms.file.RestoreFileResponse;
import com.edms.file.ShareFileByPathRequest;
import com.edms.file.ShareFileByPathResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class FileEndpoint {
	private static final String NAMESPACE_URI = "http://edms.com/File";

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
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getRecycledDocsRequest")
	@ResponsePayload
	public GetRecycledDocsResponse setGetRecycledDocs(@RequestPayload GetRecycledDocsRequest request) {
		GetRecycledDocsResponse response = new GetRecycledDocsResponse();
		response.setGetRecycledDocs(FileRepository
				.listRecycledDoc(request.getUserid(),request.getPath()));
		return response;
	}
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getSharedFilesByPathRequest")
	@ResponsePayload
	public GetSharedFilesByPathResponse getsharedFilesByPath(@RequestPayload GetSharedFilesByPathRequest request) {
		GetSharedFilesByPathResponse response = new GetSharedFilesByPathResponse();
		response.setGetSharedFilesByPath(FileRepository
				.listSharedFile(request.getUserid(),request.getPath()));
		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "hasChildRequest")
	@ResponsePayload
	public HasChildResponse hasChild(@RequestPayload HasChildRequest request) {
		HasChildResponse response = new HasChildResponse();
		response.setHasChild(FileRepository.hasChild(request.getFilePath(),request.getUserid()));
		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "createFileRequest")
	@ResponsePayload
	public CreateFileResponse createFile(
			@RequestPayload CreateFileRequest request) {
		CreateFileResponse response = new CreateFileResponse();
		response.setFile(FileRepository.createFile(
				request.getFileName(), request.getParentFile(),
				request.getUserid(),request.getKeywords(),request.getNotes(),request.getFileContent()));

		return response;
	}
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getFileByPathRequest")
	@ResponsePayload
	public GetFileByPathResponse getFileByPath(
			@RequestPayload GetFileByPathRequest request) {
		System.out.println("in Endpoint");
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
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "assignSinglePermissionRequest")
	@ResponsePayload
	public AssignSinglePermissionResponse assignSinglePermissionRequest(
			@RequestPayload AssignSinglePermissionRequest request) {
		AssignSinglePermissionResponse response = new AssignSinglePermissionResponse();
		response.setAssignSinglePermissionResponse(FileRepository.assignSinglePermission(
				request.getFilePath(),request.getUserid(),request.getUser(),request.getValue()
				));
		return response;
	}
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "recycleFileRequest")
	@ResponsePayload
	public RecycleFileResponse recycleFileRequest(
			@RequestPayload RecycleFileRequest request) {
		RecycleFileResponse response = new RecycleFileResponse();
		response.setRecycleFileResponse(FileRepository.recycleFile(request.getFilePath(),request.getUserid()));
		return response;
	}
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
}
