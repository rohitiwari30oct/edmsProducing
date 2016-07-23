package edms.repository;

import javax.annotation.PostConstruct;
import javax.jcr.LoginException;
import javax.jcr.NamespaceRegistry;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.ValueFormatException;
import javax.jcr.Workspace;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeIterator;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.AccessControlList;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.AccessControlPolicy;
import javax.jcr.security.Privilege;
import javax.print.attribute.standard.MediaSize.NA;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;






import com.edms.user.GetUsersListResponse;

import org.springframework.stereotype.Component;

import edms.core.JcrRepositorySession;
import edms.core.JcrRepositoryUtils;
import edms.core.SessionWrapper;

@Component
public class UserRepository {

	
	@PostConstruct
	public void initData() {
		
	}
	
	public boolean login(String userid,String password) {
		//jcr.jcrsession = JcrRepositorySession.getSession(userid,jcr);
		return false;
	}

	public GetUsersListResponse getUsersListResponse(String userid,String password) {
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		GetUsersListResponse response=new GetUsersListResponse();
		try {
					javax.jcr.query.QueryManager queryManager;
					queryManager = jcrsession.getWorkspace().getQueryManager();
					String expression = "select * from [edms:folder] AS s WHERE ISCHILDNODE(s,'/') ORDER BY s.[jcr:created] ASC";
					//	expression = "select * from [edms:folder] AS s WHERE NAME like ['%san%']";
				    javax.jcr.query.Query query = queryManager.createQuery(expression, javax.jcr.query.Query.JCR_SQL2);
				    javax.jcr.query.QueryResult result = query.execute();
					
			for (NodeIterator nit = result.getNodes(); nit.hasNext();) {
					Node node = nit.nextNode();
					//	response.getUsersList().add(node.getName().substring(0,node.getName().lastIndexOf('@')));
					String name=node.getName();
					if(!response.getUsersList().contains(name.toLowerCase()))
					response.getUsersList().add(node.getName().toLowerCase());
					}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			//JcrRepositoryUtils.logout(sessionId);
		}
		return response;
	}
}
