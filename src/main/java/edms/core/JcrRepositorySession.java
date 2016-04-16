package edms.core;


import hello.FileRepository;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.jcr.NamespaceRegistry;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Workspace;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeIterator;
import javax.jcr.security.AccessControlList;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.Privilege;
import javax.security.auth.Subject;

import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.principal.PrincipalManager;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.core.security.principal.AdminPrincipal;

public class JcrRepositorySession {

	public static Session jcrsession = null;
	public static Repository repository = null;
	

	
	public static Repository getRepository(){
		try {
			//repository = JcrUtils.getRepository();
			repository = JcrUtils.getRepository("http://localhost:8090/server");
		} catch (RepositoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return repository;
	}
	public static Session getSession(String userid,String password) {
		 getRepository();
		try {
						jcrsession = repository.login(new SimpleCredentials("sanjay",
								password.toCharArray()), "default");
						SessionImpl si = (SessionImpl) jcrsession;
						JackrabbitSession js = ((JackrabbitSession) jcrsession);
						Subject subject = ((SessionImpl) js).getSubject();
						Set<Principal> principals = new LinkedHashSet<Principal>();
						principals = subject.getPrincipals();
						Subject combinedSubject=new Subject(false,subject.getPrincipals(),subject.getPublicCredentials(),subject.getPrivateCredentials());
						combinedSubject.getPrincipals().add(new AdminPrincipal("admin"));
						try {
							jcrsession = Subject.doAsPrivileged(combinedSubject,
									new PrivilegedExceptionAction<Session>() {
										public Session run() throws Exception {
											Session ss = repository.login(Config.EDMS_DOMAIN);
										//	Session ss = repository.login();
											return ss;
										}
									}, AccessController.getContext());
						} catch (PrivilegedActionException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
			try{
			registerNamespace(jcrsession, jcrsession.getRootNode());
			}catch(Exception e){
			}
			jcrsession.save();
			} catch (RepositoryException e) {
				e.printStackTrace();
				return null;
		}finally{
		}
		return jcrsession;
	}
	public static void removeUser(Session jcrsession, String userid) {
		JackrabbitSession js = (JackrabbitSession) jcrsession;
		UserManager userManager;
		try {
			userManager = js.getUserManager();
			User user = (User) userManager.getAuthorizable(userid);
			user.remove();
			jcrsession.save();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
		}
	}

	public static User createUser(String userid, String password, Session jcrsession,
			Node root) {
		User user = null;
		try {
			JackrabbitSession js = (JackrabbitSession) jcrsession;
			UserManager userManager = js.getUserManager();

			Authorizable auth=userManager.getAuthorizable(userid);
			if(auth==null){
			user = userManager.createUser(userid,password);		
			JcrRepositorySession.setPolicy(jcrsession, null, userid,null,  Privilege.JCR_ALL);
			}
			jcrsession.save();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return user;
	}
	public static void setPolicy(Session jcrsession, Node root, String userid,
			String path,String permission) {
		//to be removed when userwise workspaces impl
		String domain=userid.substring(userid.indexOf('@')+1);
		
		
		SimpleCredentials credential=new SimpleCredentials("admin","admin".toCharArray());
		try {
			Repository repository=JcrUtils.getRepository();
			jcrsession = repository.login(credential,domain); 
			//jcrsession=repository.login(credential,userid);
			
			
			//for first creation of user
			root=jcrsession.getRootNode();
			path=root.getPath();
			
			
			JackrabbitSession js = (JackrabbitSession) jcrsession;
			User user = ((User) js.getUserManager().getAuthorizable(userid));
		
			AccessControlManager aMgr = jcrsession.getAccessControlManager();
	
			String[] percol=permission.split(",");
			// create a privilege set with jcr:all
			Privilege[]	setprivilege = new Privilege[percol.length];
			for(int i=0;i<percol.length;i++){
				setprivilege[i]=aMgr.privilegeFromName(percol[i]);
			} 
			//privileges = new Privilege[] { aMgr.privilegeFromName(Privilege.JCR_READ) };
			AccessControlList acl;
			try {
				// get first applicable policy (for nodes w/o a policy)
				acl = (AccessControlList) aMgr.getApplicablePolicies(path).nextAccessControlPolicy();
			} catch (NoSuchElementException e) {
				//e.printStackTrace();
				// else node already has a policy, get that one
				acl = (AccessControlList) aMgr.getPolicies(path)[0];
			}
			
			PrincipalManager pMgr = js.getPrincipalManager();
			Principal principal = pMgr.getPrincipal(userid);
			
			acl.addAccessControlEntry(principal, setprivilege);
			// the policy must be re-set

			aMgr.setPolicy(path, acl);
			// and the session must be saved for the changes to be applied

			//System.out.println("before set policy on : "+path+" to user : "+userid);
			for (int i = 0; i < setprivilege.length; i++) {
				//System.out.println(setprivilege[i]);
			}
			jcrsession.save();
			jcrsession.refresh(true);
			setprivilege = aMgr.getPrivileges(path);
			//System.out.println("after set policy on : "+path+" to user : "+userid);
			for (int i = 0; i < setprivilege.length; i++) {
				//System.out.println(setprivilege[i]);
			}
		} catch (RepositoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}finally{
			jcrsession.logout();
		}
	}
	
	
	public static Node createFolder(String folderName,Session jcrsession,String password) {
		folderName=folderName.toLowerCase();
		Node folder = null;
		try {
			Node root = jcrsession.getRootNode();
			if(!root.hasNode(folderName)){
			folder = root.addNode(folderName, Config.EDMS_FOLDER);
			folder.setProperty(Config.USERS_READ, new String[] {Config.EDMS_ADMIN });
			folder.setProperty(Config.USERS_WRITE, new String[] { Config.EDMS_ADMIN });
			folder.setProperty(Config.USERS_DELETE, new String[] { Config.EDMS_ADMIN });
			folder.setProperty(Config.USERS_SECURITY, new String[] { Config.EDMS_ADMIN });
			folder.setProperty(Config.GROUPS_READ, new String[] {Config.EDMS_ADMIN  });
			folder.setProperty(Config.GROUPS_WRITE, new String[] {Config.EDMS_ADMIN  });
			folder.setProperty(Config.GROUPS_DELETE, new String[] { Config.EDMS_ADMIN });
			folder.setProperty(Config.GROUPS_SECURITY, new String[] { Config.EDMS_ADMIN });
			folder.setProperty(Config.EDMS_KEYWORDS, "root,folder".split(","));
			folder.setProperty(Config.EDMS_AUTHOR, "admin");
			folder.setProperty(Config.EDMS_OWNER, "admin");
			folder.setProperty(Config.EDMS_DESCRIPTION, "root folder");
			folder.setProperty(Config.EDMS_CREATIONDATE,(new Date()).toString());
			folder.setProperty(Config.EDMS_MODIFICATIONDATE,(new Date()).toString());
			folder.setProperty(Config.EDMS_ACCESSDATE,(new Date()).toString());
			folder.setProperty(Config.EDMS_DOWNLOADDATE,(new Date()).toString());
			folder.setProperty(Config.EDMS_RECYCLE_DOC, false);
			folder.setProperty(Config.EDMS_NO_OF_FOLDERS, 0);
			folder.setProperty(Config.EDMS_NO_OF_DOCUMENTS, 0);
			//folder.addMixin(JcrConstants.MIX_SHAREABLE);
			folder.addMixin(JcrConstants.MIX_VERSIONABLE);
			jcrsession.save();
			if(folderName.contains("trash")){
				folderName=folderName.substring(0,folderName.indexOf("/"));
/*				FileRepository fl=new FileRepository();
				fl.setPolicyForAllowTest("/",folderName,password,jcrsession);
				fl.setPolicyForDenyTest(folderName,password,jcrsession);
				fl.setPolicyForAllowTest("/"+folderName,folderName,password,jcrsession);*/
			}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return folder;
	}
	public static void registerNamespace(Session jcrsession, Node root) {
		try {
			////System.out.println("root is : " + root);
			Workspace ws = jcrsession.getWorkspace();
			//System.out.println("workspace is : " + ws.getName());
			NamespaceRegistry nr;
			nr = ws.getNamespaceRegistry();
			//System.out.println("namespace registry is : "
				//	+ nr.getPrefixes().length);
			for (String str : nr.getPrefixes()) {
				//System.out.println(str);
			}
			for (String str : nr.getURIs()) {
				//System.out.println(str);
			}
			for (NodeTypeIterator iterator = ws.getNodeTypeManager()
					.getAllNodeTypes(); iterator.hasNext();) {
				NodeType nodeType = (NodeType) iterator.next();
				//System.out.println(nodeType.getName());
			}
			nr.registerNamespace("edms", "http://www.edms.com/1.0");
			//System.out.println("in repository");
			InputStream is = ClassLoader.class
					.getResourceAsStream("/edms/module/jcr/CustomNodes.cnd");
			//System.out.println("Input Stream is : " + is);
			Reader cnd = new InputStreamReader(is);
			NodeType[] nodeTypes;
			nodeTypes = CndImporter.registerNodeTypes(cnd, jcrsession,false);
			//System.out.println(nodeTypes.length);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 

	}
	public static void logout(JcrRepositorySession jcr) {
		// TODO Auto-generated method stub
		jcrsession.logout();
	}
}
