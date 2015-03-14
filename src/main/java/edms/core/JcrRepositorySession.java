package edms.core;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.Principal;
import java.util.Date;
import java.util.NoSuchElementException;

import javax.jcr.LoginException;
import javax.jcr.NamespaceRegistry;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Workspace;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeIterator;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.AccessControlList;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.Privilege;

import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.principal.PrincipalManager;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.apache.jackrabbit.commons.cnd.ParseException;

public class JcrRepositorySession {

	public static Session jcrsession = null;
	public static Repository repository = null;
	

	
	public static Repository getRepository(){
		try {
			repository = JcrUtils.getRepository();
		} catch (RepositoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return repository;
	}
	public static Session getSession(String userid) {
		 getRepository();
		 
		 
		try {
		//	userid=(userid.split("@"))[0];
			SimpleCredentials credential=new SimpleCredentials("admin",
					"admin".toCharArray());
			
			jcrsession = repository.login(credential); 
			System.out.println("userid is : "+jcrsession.getUserID());
			Node root=jcrsession.getRootNode();
			//createUser("sanjay", "redhat", jcrsession, jcrsession.getRootNode());
			try{
			registerNamespace(jcrsession, jcrsession.getRootNode());
			}catch(Exception e){
			}
			/*if(jcrsession.getRootNode().hasNode("santosh@avi-oil.com"))
			jcrsession.getRootNode().getNode("santosh@avi-oil.com").remove();
			if(jcrsession.getRootNode().hasNode("santosh@avi-oil.com/trash"))
			jcrsession.getRootNode().getNode("santosh@avi-oil.com/trash").remove();
			if(jcrsession.getRootNode().hasNode("sanjay@avi-oil.com"))
			jcrsession.getRootNode().getNode("sanjay@avi-oil.com").remove();
			if(jcrsession.getRootNode().hasNode("sanjay@avi-oil.com/trash"))
			jcrsession.getRootNode().getNode("sanjay@avi-oil.com/trash").remove();
			if(jcrsession.getRootNode().hasNode("janak@avi-oil.com"))
			jcrsession.getRootNode().getNode("janak@avi-oil.com").remove();
			if(jcrsession.getRootNode().hasNode("janak@avi-oil.com/trash"))
			jcrsession.getRootNode().getNode("janak@avi-oil.com/trash").remove();*/
			//jcrsession.save();
			//removeUser(jcrsession, "santosh@avi-oil.com");
			//removeUser(jcrsession, "sanjay@avi-oil.com");
			//removeUser(jcrsession, "janak@avi-oil.com");
			//createFolder("santosh@avi-oil.com");
			//createFolder("santosh@avi-oil.com/trash");
			//createFolder("janak@avi-oil.com");
			//createFolder("janak@avi-oil.com/trash");
			//registerNamespace(jcrsession, jcrsession.getRootNode());
			//createFolder("sanjay@avi-oil.com");
			//createFolder("sanjay@avi-oil.com/trash");
			//createFolder("sanjay@avi-oil.com/sharedByOthers");
			//createFolder("sanjay@avi-oil.com/sharedCalendersByOthers");
			//removeUser(jcrsession, userid);
			//Node root=jcrsession.getRootNode();
			//setPolicy(jcrsession, root, userid,root.getPath(),  Privilege.JCR_ALL);
			//createFolder(userid);
			//createFolder(userid+"/trash");
			jcrsession.save();
			} catch (RepositoryException e) {
				e.printStackTrace();
				return null;
		}finally{
		//	jcrsession.logout();
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
			user = userManager.createUser(userid,password);
				
			
			/*root=jcrsession.getRootNode();
			setPolicy(jcrsession, root, userid,root.getPath(),  Privilege.JCR_ALL);
			Node userNode=	root.addNode(userid,Config.EDMS_FOLDER);
			userNode.setProperty(Config.USERS_READ,new String[]{userid});
			userNode.setProperty(Config.USERS_WRITE,new String[]{userid});
			userNode.setProperty(Config.USERS_DELETE,new String[]{userid});
			userNode.setProperty(Config.USERS_SECURITY,new String[]{userid});
			userNode.setProperty(Config.EDMS_KEYWORDS, new String[]{"user","root","node"});
			userNode.setProperty(Config.EDMS_AUTHOR,userid);
			userNode.setProperty(Config.EDMS_CREATIONDATE, (new Date()).toString());
			userNode.setProperty(Config.EDMS_MODIFICATIONDATE, (new Date()).toString());
			userNode.setProperty(Config.EDMS_NO_OF_FOLDERS, 0);
			userNode.setProperty(Config.EDMS_NO_OF_DOCUMENTS, 0);
			userNode.setProperty(Config.EDMS_RECYCLE_DOC, false);
			userNode.addMixin(JcrConstants.MIX_SHAREABLE);
			userNode.addMixin(JcrConstants.MIX_VERSIONABLE);*/
			jcrsession.save();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return user;
	}
	public static void setPolicy(Session jcrsession, Node root, String userid,
			String path,String permission) {
		SimpleCredentials credential=new SimpleCredentials("admin",
				"admin".toCharArray());
		try {
			Repository repository=JcrUtils.getRepository();
			jcrsession = repository.login(credential,userid); 
			
			//for first creation of user
			root=jcrsession.getRootNode();
			path=root.getPath();
			
			
			JackrabbitSession js = (JackrabbitSession) jcrsession;
			User user = ((User) js.getUserManager().getAuthorizable(userid));
		
			AccessControlManager aMgr = jcrsession.getAccessControlManager();
	
			// get supported privileges of any node
			/*Privilege[] privileges = aMgr
					.getSupportedPrivileges(path);
			for (int i = 0; i < privileges.length; i++) {
				//System.out.println(privileges[i]);
			}*/

			// get now applied privileges on a node 
			/*privileges = aMgr.getPrivileges(path);
			for (int i = 0; i < privileges.length; i++) {
				//System.out.println(privileges[i]);
			}*/
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
			// remove all existing entries
			for (AccessControlEntry e : acl.getAccessControlEntries()) {
				acl.removeAccessControlEntry(e);
			}
			/*	Group grp = ((Group) js.getUserManager().getAuthorizable(
					"top-management"));
				//System.out.println("group path is : " + grp.getID()
					+ " user id is : " + user.getID());
			*/
			// Group group=(Group)
			// js.getPrincipalManager().getGroupMembership(user.getPrincipal());
			// boolean isAdmin = user.isAdmin();
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
	
	
	public static Node createFolder(String folderName,Session jcrsession) {
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
			folder.setProperty(Config.EDMS_DESCRIPTION, "this is root folder");
			folder.setProperty(Config.EDMS_CREATIONDATE,(new Date()).toString());
			folder.setProperty(Config.EDMS_MODIFICATIONDATE,(new Date()).toString());
			folder.setProperty(Config.EDMS_ACCESSDATE,(new Date()).toString());
			folder.setProperty(Config.EDMS_DOWNLOADDATE,(new Date()).toString());
			folder.setProperty(Config.EDMS_RECYCLE_DOC, false);
			folder.setProperty(Config.EDMS_NO_OF_FOLDERS, 0);
			folder.setProperty(Config.EDMS_NO_OF_DOCUMENTS, 0);
			folder.addMixin(JcrConstants.MIX_SHAREABLE);
			folder.addMixin(JcrConstants.MIX_VERSIONABLE);
			jcrsession.save();}
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
