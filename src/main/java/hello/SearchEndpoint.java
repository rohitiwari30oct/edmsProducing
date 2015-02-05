package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.edms.documentmodule.SearchDocByDateRequest;
import com.edms.documentmodule.SearchDocByDateResponse;
import com.edms.documentmodule.SearchDocByLikeRequest;
import com.edms.documentmodule.SearchDocByLikeResponse;

@Endpoint
public class SearchEndpoint {

	private static final String NAMESPACE_URI = "http://edms.com/documentModule";

	private FileRepository fileRepository;
	
	@Autowired
	public SearchEndpoint(FileRepository fileRepository) {
		this.fileRepository = fileRepository;
	}
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "searchDocByLikeRequest")
	@ResponsePayload
	public SearchDocByLikeResponse searchDocByLikeRequest(@RequestPayload SearchDocByLikeRequest request) {
		SearchDocByLikeResponse response = new SearchDocByLikeResponse();
		response=fileRepository.searchDocByLike(request.getSearchParamValue(),request.getFolderPath(),request.getSearchParam(),request.getUserid());
		return response;
	}
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "searchDocByDateRequest")
	@ResponsePayload
	public SearchDocByDateResponse searchDocByDateResquest(@RequestPayload SearchDocByDateRequest request) {
		SearchDocByDateResponse response = new SearchDocByDateResponse();
		response=fileRepository.searchDocByDate(request.getSearchParamValue(),request.getFolderPath(),request.getSearchParam(),request.getUserid());
		return response;
	}

}
