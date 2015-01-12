package hello;

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
import javax.jcr.security.Privilege;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;
import javax.jcr.version.VersionManager;
import javax.print.attribute.standard.MediaSize.NA;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;












import org.activiti.engine.impl.persistence.entity.UserIdentityManager;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlEntry;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlManager;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlPolicy;
import org.apache.jackrabbit.api.security.principal.PrincipalManager;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.apache.jackrabbit.commons.cnd.ParseException;
import org.apache.jackrabbit.core.TransientRepository;
import org.apache.jackrabbit.core.security.principal.EveryonePrincipal;
import org.apache.tika.metadata.Office;

import com.edms.file.File;
import com.edms.folder.ArrayOfFolders;
import com.edms.folder.Folder;
import com.edms.folder.FolderListReturn;
import com.edms.folder.RenameFolderRes;
import com.edms.folder.VersionDetail;

import org.neo4j.cypher.internal.compiler.v2_0.untilMatched;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import edms.core.Config;
import edms.core.JcrRepositorySession;

@Component
public class FolderRepository {
	private static Session jcrsession =JcrRepositorySession.jcrsession;
	/*
	 * @Autowired FolderService folderService;
	 */

	// @Autowired DefaultSpringSecurityContextSource contextSource;


	public FolderListReturn listFolder(String name, String userid) {
		Assert.notNull(name);
		FolderListReturn folderList1 = new FolderListReturn();
		ArrayOfFolders folders = new ArrayOfFolders();
		Node root = null;
		
		try {
			 	/*	String[] wwws=jcrsession.getWorkspace().getAccessibleWorkspaceNames();
				for (int i = 0; i < wwws.length; i++) {
					//System.out.println(wwws[i]);
				}	*/
					
				// 	removeUser(jcrsession, userid);
				//	createUser(userid, "redhat", jcrsession, root);
			 	//	setPolicy(jcrsession, root, userid,root.getPath(),  Privilege.JCR_ALL);
				//	Workspace ws=jcrsession.getWorkspace();
				//	ws.createWorkspace(userid);
			
					root = jcrsession.getRootNode();
					if (name.length() > 1) {
						if (!root.hasNode(userid)) {
							root=JcrRepositorySession.createFolder(userid);
							JcrRepositorySession.createFolder(userid+"/trash");
						} else {
							root = root.getNode(name.substring(1));
						}
					}
			for (NodeIterator nit = root.getNodes(); nit.hasNext();) {
				Node node = nit.nextNode();
			/*	boolean recycle=false;
				if(node.hasProperty(Config.EDMS_RECYCLE_DOC)){
				recycle=node.getProperty(Config.EDMS_RECYCLE_DOC).getBoolean();
				}if(!recycle){*/
					if (Config.EDMS_FOLDER.equals(node.getPrimaryNodeType().getName())) {
					if(node.getProperty(Config.EDMS_AUTHOR).getString().equals(userid))
					{
					Folder folder = new Folder();
					folder=setProperties(node,folder,userid);
					folders.getFolderList().add(folder);}
				}
					//}
			}
		} catch (LoginException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			
			e.printStackTrace();
		} 
		folderList1.setFolderListResult(folders);
		folderList1.setSuccess(true);
		return folderList1;
	}

	public Folder setProperties(Node node, Folder folder,String userid) {
		try {
			
		folder.setFolderName(node.getName());
		folder.setFolderPath(node.getPath());
		folder.setParentFolder(node.getParent().getName());

		if(node.hasProperty(Config.EDMS_AUTHOR))
		folder.setCreatedBy(node.getProperty(Config.EDMS_AUTHOR).getString());
		if(node.hasProperty(Config.EDMS_RECYCLE_DOC))
		folder.setRecycle(node.getProperty(Config.EDMS_RECYCLE_DOC).getBoolean());

		if(node.hasProperty(Config.EDMS_KEYWORDS)){
		Value[] actualUsers = node.getProperty(Config.EDMS_KEYWORDS).getValues();
		for (int i = 0; i < actualUsers.length; i++) {
			folder.getKeywords().add(actualUsers[i].getString());
		}
		}
		if(node.hasProperty(Config.EDMS_CREATIONDATE))
		folder.setCreationDate(node.getProperty(Config.EDMS_CREATIONDATE).getString());
		
		if(node.hasProperty(Config.EDMS_MODIFICATIONDATE))
		folder.setModificationDate(node.getProperty(Config.EDMS_MODIFICATIONDATE).getString());
		
		if(node.hasProperty(Config.EDMS_NO_OF_DOCUMENTS)){
			folder.setNoOfDocuments(node.getProperty(Config.EDMS_NO_OF_DOCUMENTS).getString());
		
		}
		if(node.hasProperty(Config.EDMS_NO_OF_FOLDERS)){
			folder.setNoOfFolders(node.getProperty(Config.EDMS_NO_OF_FOLDERS).getString());
		}
		if(node.hasProperty(Config.EDMS_DESCRIPTION))
		folder.setNotes(node.getProperty(Config.EDMS_DESCRIPTION).getString());
		
		/* start mapping permissions to edms folder */
			if(node.hasProperty(Config.USERS_READ)){
			Value[] actualUsers = node.getProperty(Config.USERS_READ).getValues();
		
			for (int i = 0; i < actualUsers.length; i++) {
				folder.getUserRead().add(actualUsers[i].getString());
			}	}
			if(node.hasProperty(Config.USERS_WRITE)){
			Value[] actualUsers = node.getProperty(Config.USERS_WRITE).getValues();
			for (int i = 0; i < actualUsers.length; i++) {
				folder.getUserWrite().add(actualUsers[i].getString());
			}	}
			if(node.hasProperty(Config.USERS_DELETE)){
			Value[] actualUsers = node.getProperty(Config.USERS_DELETE).getValues();
			for (int i = 0; i < actualUsers.length; i++) {
				folder.getUserDelete().add(actualUsers[i].getString());
			}	
			}	
			if(node.hasProperty(Config.USERS_SECURITY)){
			Value[] actualUsers = node.getProperty(Config.USERS_SECURITY).getValues();
			for (int i = 0; i < actualUsers.length; i++) {
				folder.getUserSecurity().add(actualUsers[i].getString());
			}	
			}	
			if(node.hasProperty(Config.GROUPS_READ)){
			Value[] actualUsers = node.getProperty(Config.GROUPS_READ).getValues();
			for (int i = 0; i < actualUsers.length; i++) {
				folder.getGroupRead().add(actualUsers[i].getString());
			}	
			}	
			if(node.hasProperty(Config.GROUPS_WRITE)){
			Value[] actualUsers = node.getProperty(Config.GROUPS_WRITE).getValues();
			for (int i = 0; i < actualUsers.length; i++) {
				folder.getGroupWrite().add(actualUsers[i].getString());
			}
			}	
			if(node.hasProperty(Config.GROUPS_DELETE)){
			Value[] actualUsers = node.getProperty(Config.GROUPS_DELETE).getValues();
			for (int i = 0; i < actualUsers.length; i++) {
				folder.getGroupDelete().add(actualUsers[i].getString());
			}	
			}	
			if(node.hasProperty(Config.GROUPS_SECURITY)){
			Value[] actualUsers = node.getProperty(Config.GROUPS_SECURITY).getValues();
			for (int i = 0; i < actualUsers.length; i++) {
				folder.getGroupSecurity().add(actualUsers[i].getString());
			}	
			}
			
			VersionHistory history = jcrsession.getWorkspace().getVersionManager().getVersionHistory(node.getPath());
			// To iterate over all versions
			VersionIterator versions = history.getAllVersions();
			while (versions.hasNext()) {
			  Version version = versions.nextVersion();
			  VersionDetail versionDetail=new VersionDetail();
			  versionDetail.setCreatedBy(userid);
			  versionDetail.setCreationDate(version.getCreated().getTime().toString());
			  versionDetail.setDetails(version.getIdentifier());
			  versionDetail.setVersionName(version.getName());
			  versionDetail.setVersionLabel(version.getParent().getName());
			  folder.getVersionsHistory().add(versionDetail);
			}
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return folder;
	}

	public Boolean hasChild(String folderPath, String userid) {

		Repository	repository =  new TransientRepository();
		//Session jcrsession = null;
		boolean flag = false;
		try {
		/*jcrsession = repository.login(new SimpleCredentials("admin",
					"admin".toCharArray()));*/
			/* jcrsession = repository.login(new SimpleCredentials(
			 userid,"redhat".toCharArray()));*/
			/*
			 * if (!node.getName().equals("jcr:system") &&
			 * (!node.getProperty(JcrConstants.JCR_PRIMARYTYPE)
			 * .getString().equals(JcrConstants.NT_RESOURCE))) {
			 */
			Node root = jcrsession.getRootNode();
			if (folderPath.length() > 1) {
				root = root.getNode(folderPath.substring(1));
			}
			flag = root.hasNodes();
			
		} catch (LoginException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} finally {
			//jcrsession.logout();
		}
		return flag;
	}

	public Folder createFolder(String folderName, String parentFolder,
			String userid, String keywords, String description) {
		Repository	repository =  new TransientRepository();
		Node folder = null;
		Folder folder1 = new Folder();
		try {
			/*jcrsession = repository.login(new SimpleCredentials("admin",
					"admin".toCharArray()));*/
			/*jcrsession = repository.login(new SimpleCredentials(
			 userid,"redhat".toCharArray()));*/
		
			Node root = jcrsession.getRootNode();
			/*if(userid!="admin"){
			root=root.getNode(userid);
			}*/
			if (parentFolder.length() > 1) {
				root = root.getNode(parentFolder.substring(1));
			}
			if(root.hasProperty(Config.USERS_WRITE)){
			Value[] actualUsers = root.getProperty(Config.USERS_WRITE).getValues();
			
			String newUsers = "";
			
			for (int i = 0; i < actualUsers.length; i++) {
				newUsers+=actualUsers[i].getString()+",";
			}
			if(newUsers.contains(userid)||root.getProperty(Config.EDMS_AUTHOR).getString().equals(userid)||(root.getName().equals(userid)&&(root.getProperty(Config.EDMS_AUTHOR).getString()).equals(Config.JCR_USERNAME))){
				
			folder = root.addNode(folderName, Config.EDMS_FOLDER);
			
			if(root.hasProperty(Config.USERS_READ)&&(!root.getProperty(Config.EDMS_AUTHOR).toString().equals("admin"))){
				Value[] actualUser = root.getProperty(Config.USERS_READ).getValues();
				String newUser="";
				for (int i = 0; i < actualUser.length; i++) {
					newUser+=actualUser[i].getString()+",";
				}
			folder.setProperty(Config.USERS_READ, newUser.split(","));
			}else{
				folder.setProperty(Config.USERS_READ, new String[]{});
			}
			if(root.hasProperty(Config.USERS_WRITE)&&!root.getProperty(Config.EDMS_AUTHOR).toString().equals("admin")){
				Value[] actualUser = root.getProperty(Config.USERS_WRITE).getValues();
				String newUser="";
				for (int i = 0; i < actualUser.length; i++) {
					newUser+=actualUser[i].getString()+",";
				}
			folder.setProperty(Config.USERS_WRITE, newUser.split(","));
			}else{
				folder.setProperty(Config.USERS_WRITE, new String[]{});
			}
			if(root.hasProperty(Config.USERS_DELETE)&&!root.getProperty(Config.EDMS_AUTHOR).toString().equals("admin")){
				Value[] actualUser = root.getProperty(Config.USERS_DELETE).getValues();
				String newUser="";
				for (int i = 0; i < actualUser.length; i++) {
					newUser+=actualUser[i].getString()+",";
				}
			folder.setProperty(Config.USERS_DELETE, newUser.split(","));
			}else{
				folder.setProperty(Config.USERS_DELETE, new String[]{});
			}
			if(root.hasProperty(Config.USERS_SECURITY)&&!root.getProperty(Config.EDMS_AUTHOR).toString().equals("admin")){
				Value[] actualUser = root.getProperty(Config.USERS_SECURITY).getValues();
				String newUser="";
				for (int i = 0; i < actualUser.length; i++) {
					newUser+=actualUser[i].getString()+",";
				}
			folder.setProperty(Config.USERS_SECURITY, newUser.split(","));
			}else{
				folder.setProperty(Config.USERS_SECURITY, new String[]{});
			}

			if(root.hasProperty(Config.GROUPS_READ)&&!root.getProperty(Config.EDMS_AUTHOR).toString().equals("admin")){
				Value[] actualUser = root.getProperty(Config.GROUPS_READ).getValues();
				String newUser="";
				for (int i = 0; i < actualUser.length; i++) {
					newUser+=actualUser[i].getString()+",";
				}
			folder.setProperty(Config.GROUPS_READ, newUser.split(","));
			}else{
				folder.setProperty(Config.GROUPS_READ, new String[]{});
			}

			if(root.hasProperty(Config.GROUPS_WRITE)&&!root.getProperty(Config.EDMS_AUTHOR).toString().equals("admin")){
				Value[] actualUser = root.getProperty(Config.GROUPS_WRITE).getValues();
				String newUser="";
				for (int i = 0; i < actualUser.length; i++) {
					newUser+=actualUser[i].getString()+",";
				}
			folder.setProperty(Config.GROUPS_WRITE, newUser.split(","));
			}else{
				folder.setProperty(Config.GROUPS_WRITE, new String[]{});
			}

			if(root.hasProperty(Config.GROUPS_DELETE)&&!root.getProperty(Config.EDMS_AUTHOR).toString().equals("admin")){
				Value[] actualUser = root.getProperty(Config.GROUPS_DELETE).getValues();
				String newUser="";
				for (int i = 0; i < actualUser.length; i++) {
					newUser+=actualUser[i].getString()+",";
				}
			folder.setProperty(Config.GROUPS_DELETE, newUser.split(","));
			}else{
				folder.setProperty(Config.GROUPS_DELETE, new String[]{});
			}

			if(root.hasProperty(Config.GROUPS_SECURITY)&&!root.getProperty(Config.EDMS_AUTHOR).toString().equals("admin")){
				Value[] actualUser = root.getProperty(Config.GROUPS_SECURITY).getValues();
				String newUser="";
				for (int i = 0; i < actualUser.length; i++) {
					newUser+=actualUser[i].getString()+",";
				}
			folder.setProperty(Config.GROUPS_SECURITY, newUser.split(","));
			}else{
				folder.setProperty(Config.GROUPS_SECURITY, new String[]{});
			}
			/*folder.setProperty(Config.USERS_READ,new String[]{userid});
			folder.setProperty(Config.USERS_WRITE,new String[]{userid});
			folder.setProperty(Config.USERS_DELETE,new String[]{userid});
			folder.setProperty(Config.USERS_SECURITY,new String[]{userid});*/
			folder.setProperty(Config.EDMS_KEYWORDS, keywords.split(","));
			if(root.getProperty(Config.EDMS_AUTHOR).getString().equals("admin")){

				folder.setProperty(Config.EDMS_AUTHOR,userid);
					
			}else{
			folder.setProperty(Config.EDMS_AUTHOR,root.getProperty(Config.EDMS_AUTHOR).getString());
			}folder.setProperty(Config.EDMS_DESCRIPTION, description);
			folder.setProperty(Config.EDMS_CREATIONDATE, (new Date()).toString());
			folder.setProperty(Config.EDMS_MODIFICATIONDATE, (new Date()).toString());
			folder.setProperty(Config.EDMS_RECYCLE_DOC, false);
			folder.setProperty(Config.EDMS_NO_OF_FOLDERS, 0);
			folder.setProperty(Config.EDMS_NO_OF_DOCUMENTS, 0);
			folder.addMixin(JcrConstants.MIX_SHAREABLE);
			folder.addMixin(JcrConstants.MIX_VERSIONABLE);
			
			jcrsession.save();
			jcrsession.getWorkspace().getVersionManager().checkin(root.getPath());
			root.setProperty(Config.EDMS_NO_OF_FOLDERS, Integer.parseInt(root.getProperty(Config.EDMS_NO_OF_FOLDERS).getString())+1);
			jcrsession.getWorkspace().getVersionManager().checkout(root.getPath());
			
			jcrsession.save();
			folder1=	setProperties(folder, folder1,userid);
			}else{
				//System.out.println("you have not permission to add child node");
			}}
		} catch (LoginException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} finally {
			//jcrsession.logout();
		}
		return folder1;
	}

	public Folder getFolderByPath(String folderPath, String userid) {
		Repository repository = new TransientRepository();
		//Session jcrsession = null;
		Folder folder1 = new Folder();
		File file1 = new File();
		try {
		/*	jcrsession = repository.login(new SimpleCredentials("admin",
					"admin".toCharArray()));*/
			/* jcrsession = repository.login(new SimpleCredentials(
			 userid,"redhat".toCharArray()));*/
		
			Node root = jcrsession.getRootNode();
			/*if(userid!="admin"){
				root=root.getNode(userid);
				}*/
			if (folderPath.length() > 1) {
				root = root.getNode(folderPath.substring(1));
			}
			folder1.setFolderName(root.getName().toString());
			folder1.setFolderPath(root.getPath().toString());
			file1.setFileName(root.getName().toString());
			file1.setFilePath(root.getPath().toString());
			if(root.getPath().toString().length()>1){
			if(folderPath.length()>1){
					if(root.getProperty(Config.EDMS_AUTHOR).getString().equals(userid)||(root.getName().equals(userid)&&(root.getProperty(Config.EDMS_AUTHOR).getString()).equals(Config.JCR_USERNAME)))
					{
							setProperties(root, folder1,userid);

						}
			}}
		} catch (LoginException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			//jcrsession.logout();
		}
		return folder1;
	}

	public void shareFolderRecursion(Node rt,String userpermissions,String grouppermissions,String users,String groups) {
		try {
			if(userpermissions.contains("1")){
				
				rt.setProperty(Config.USERS_READ, new String[]{users});
			}
			if(userpermissions.contains("2")){
				rt.setProperty(Config.USERS_WRITE, new String[]{users});
			}
			if(userpermissions.contains("4")){
				rt.setProperty(Config.USERS_DELETE, new String[]{users});
			}
			if(userpermissions.contains("true")){
				rt.setProperty(Config.USERS_SECURITY, new String[]{users});
			}
			
			if(grouppermissions.contains("1")){
				rt.setProperty(Config.GROUPS_READ, new String[]{groups});
			}
			if(grouppermissions.contains("2")){
				rt.setProperty(Config.GROUPS_WRITE, new String[]{groups});
			}
			if(grouppermissions.contains("4")){
				rt.setProperty(Config.GROUPS_DELETE, new String[]{groups});
			}
			if(grouppermissions.contains("true")){
				rt.setProperty(Config.GROUPS_SECURITY, new String[]{groups});
			}

			jcrsession.save();
			if(rt.hasNodes()){
				for (NodeIterator nit = rt.getNodes(); nit.hasNext();) {
					Node root=nit.nextNode();
					shareFolderRecursion(root, userpermissions, grouppermissions, users, groups);
				}
			}} catch (LoginException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void assignSinglePermissionRecursion(Node rt,String userid,String user,String value) {
		try {
			switch (value) {
			case "ur":
			{	Value[] actualUsers = rt.getProperty(Config.USERS_READ).getValues();
			String newUser="";
			for (int i = 0; i < actualUsers.length; i++) {
				newUser+=actualUsers[i].getString()+",";
			}
			//System.out.println(newUser.contains(user));
			if(!newUser.contains(user))
			{

				newUser+=user+",";
				rt.setProperty(Config.USERS_READ, new String[]{newUser});
				}
				jcrsession.save();
				break;
			}
			case "uw":{
				Value[] actualUsers = rt.getProperty(Config.USERS_WRITE).getValues();
				String newUser="";
				for (int i = 0; i < actualUsers.length; i++) {
					newUser+=actualUsers[i].getString()+",";
				}
				//System.out.println(newUser.contains(user));
				if(!newUser.contains(user))
				{

					newUser+=user+",";
					rt.setProperty(Config.USERS_WRITE, new String[]{newUser});
				}
				jcrsession.save();
				break;}
		case "ud":
				{Value[] actualUsers = rt.getProperty(Config.USERS_DELETE).getValues();
				String newUser="";
				for (int i = 0; i < actualUsers.length; i++) {
					newUser+=actualUsers[i].getString()+",";
				}
				//System.out.println(newUser.contains(user));
				if(!newUser.contains(user))
				{

					newUser+=user+",";
					rt.setProperty(Config.USERS_DELETE, new String[]{newUser});
				}
				jcrsession.save();
			break;}
		case "us":
				{Value[] actualUsers = rt.getProperty(Config.USERS_SECURITY).getValues();
				String newUser="";
				for (int i = 0; i < actualUsers.length; i++) {
					newUser+=actualUsers[i].getString()+",";
				}
				//System.out.println(newUser.contains(user));
				if(!newUser.contains(user))
				{
					newUser+=user+",";
					rt.setProperty(Config.USERS_SECURITY, new String[]{newUser});
				}
				jcrsession.save();
				break;}
		case "nur":
		{	
		Value[] actualUsers = rt.getProperty(Config.USERS_READ).getValues();
		String newUser="";
		for (int i = 0; i < actualUsers.length; i++) {
			newUser+=actualUsers[i].getString()+",";
		}
		//System.out.println(newUser.contains(user));
		if(newUser.contains(user))
			{
			newUser=newUser.replace(user, "");
			rt.setProperty(Config.USERS_READ, new String[]{newUser});
			jcrsession.save();
			}
		
		actualUsers = rt.getProperty(Config.USERS_WRITE).getValues();
		newUser="";
		for (int i = 0; i < actualUsers.length; i++) {
			newUser+=actualUsers[i].getString()+",";
		}
		//System.out.println(newUser.contains(user));
		if(newUser.contains(user))
			{
			newUser=newUser.replace(user, "");
			rt.setProperty(Config.USERS_WRITE, new String[]{newUser});
			jcrsession.save();
			}
		
		
		actualUsers = rt.getProperty(Config.USERS_DELETE).getValues();
		newUser="";
		for (int i = 0; i < actualUsers.length; i++) {
			newUser+=actualUsers[i].getString()+",";
		}
		//System.out.println(newUser.contains(user));
		if(newUser.contains(user))
			{
			newUser=newUser.replace(user, "");
			rt.setProperty(Config.USERS_DELETE, new String[]{newUser});
			jcrsession.save();
			}
		
		actualUsers = rt.getProperty(Config.USERS_SECURITY).getValues();
		newUser="";
		for (int i = 0; i < actualUsers.length; i++) {
			newUser+=actualUsers[i].getString()+",";
		}
		//System.out.println(newUser.contains(user));
		if(newUser.contains(user))
			{
			newUser=newUser.replace(user, "");
			rt.setProperty(Config.USERS_SECURITY, new String[]{newUser});
			jcrsession.save();
			}
			break;
		}
		case "nuw":{	
			Value[] actualUsers = rt.getProperty(Config.USERS_WRITE).getValues();
			String newUser="";
			for (int i = 0; i < actualUsers.length; i++) {
				newUser+=actualUsers[i].getString()+",";
			}
			//System.out.println(newUser.contains(user));
			if(newUser.contains(user))
				{
				newUser=newUser.replace(user, "");
				rt.setProperty(Config.USERS_WRITE, new String[]{newUser});
				jcrsession.save();
				}
				break;
			}
	case "nud":
			{	
				Value[] actualUsers = rt.getProperty(Config.USERS_DELETE).getValues();
				String newUser="";
				for (int i = 0; i < actualUsers.length; i++) {
					newUser+=actualUsers[i].getString()+",";
				}
				//System.out.println(newUser.contains(user));
				if(newUser.contains(user))
					{
					newUser=newUser.replace(user, "");
					rt.setProperty(Config.USERS_DELETE, new String[]{newUser});
					jcrsession.save();
					}
					break;
				}
	case "nus":
			{	
				Value[] actualUsers = rt.getProperty(Config.USERS_SECURITY).getValues();
				String newUser="";
				for (int i = 0; i < actualUsers.length; i++) {
					newUser+=actualUsers[i].getString()+",";
				}
				//System.out.println(newUser.contains(user));
				if(newUser.contains(user))
					{
					newUser=newUser.replace(user, "");
					rt.setProperty(Config.USERS_SECURITY, new String[]{newUser});
					jcrsession.save();
					}
					break;
				}
	case "gr":
	{Value[] actualUsers = rt.getProperty(Config.GROUPS_READ).getValues();
	String newUser="";
	for (int i = 0; i < actualUsers.length; i++) {
		newUser+=actualUsers[i].getString()+",";
	}
	//System.out.println(newUser.contains(user));
	if(!newUser.contains(user))
	{
		newUser+=user+",";
		rt.setProperty(Config.GROUPS_READ, new String[]{newUser});
	}
	jcrsession.save();
	break;}
	case "gw":{Value[] actualUsers = rt.getProperty(Config.GROUPS_WRITE).getValues();
	String newUser="";
	for (int i = 0; i < actualUsers.length; i++) {
		newUser+=actualUsers[i].getString()+",";
	}
	//System.out.println(newUser.contains(user));
	if(!newUser.contains(user))
	{
		newUser+=user+",";
		rt.setProperty(Config.GROUPS_WRITE, new String[]{newUser});
	}
	jcrsession.save();
	break;}
case "gd":
		{Value[] actualUsers = rt.getProperty(Config.GROUPS_DELETE).getValues();
		String newUser="";
		for (int i = 0; i < actualUsers.length; i++) {
			newUser+=actualUsers[i].getString()+",";
		}
		//System.out.println(newUser.contains(user));
		if(!newUser.contains(user))
		{
			newUser+=user+",";
			rt.setProperty(Config.GROUPS_DELETE, new String[]{newUser});
		}
		jcrsession.save();
		break;}
case "gs":
		{Value[] actualUsers = rt.getProperty(Config.GROUPS_SECURITY).getValues();
		String newUser="";
		for (int i = 0; i < actualUsers.length; i++) {
			newUser+=actualUsers[i].getString()+",";
		}
		//System.out.println(newUser.contains(user));
		if(!newUser.contains(user))
		{
			newUser+=user+",";
			rt.setProperty(Config.GROUPS_SECURITY, new String[]{newUser});
		}
		jcrsession.save();
		break;}
case "ngr":
{	
	Value[] actualUsers = rt.getProperty(Config.GROUPS_READ).getValues();
	String newUser="";
	for (int i = 0; i < actualUsers.length; i++) {
		newUser+=actualUsers[i].getString()+",";
	}
	//System.out.println(newUser.contains(user));
	if(newUser.contains(user))
		{
		newUser=newUser.replace(user, "");
		rt.setProperty(Config.GROUPS_READ, new String[]{newUser});
		jcrsession.save();
		}
		break;
	}
case "ngw":{	
	Value[] actualUsers = rt.getProperty(Config.GROUPS_WRITE).getValues();
	String newUser="";
	for (int i = 0; i < actualUsers.length; i++) {
		newUser+=actualUsers[i].getString()+",";
	}
	//System.out.println(newUser.contains(user));
	if(newUser.contains(user))
		{
		newUser=newUser.replace(user, "");
		rt.setProperty(Config.GROUPS_WRITE, new String[]{newUser});
		jcrsession.save();
		}
		break;
	}
case "ngd":
	{	
		Value[] actualUsers = rt.getProperty(Config.GROUPS_DELETE).getValues();
		String newUser="";
		for (int i = 0; i < actualUsers.length; i++) {
			newUser+=actualUsers[i].getString()+",";
		}
		//System.out.println(newUser.contains(user));
		if(newUser.contains(user))
			{
			newUser=newUser.replace(user, "");
			rt.setProperty(Config.GROUPS_DELETE, new String[]{newUser});
			jcrsession.save();
			}
			break;
		}
case "ngs":
	{	
		Value[] actualUsers = rt.getProperty(Config.GROUPS_SECURITY).getValues();
		String newUser="";
		for (int i = 0; i < actualUsers.length; i++) {
			newUser+=actualUsers[i].getString()+",";
		}
		//System.out.println(newUser.contains(user));
		if(newUser.contains(user))
			{
			newUser=newUser.replace(user, "");
			rt.setProperty(Config.GROUPS_SECURITY, new String[]{newUser});
			jcrsession.save();
			}
			break;
		}
	default:
	break;
	}
			
			if(rt.hasNodes()){
				for (NodeIterator nit = rt.getNodes(); nit.hasNext();) {
					Node root=nit.nextNode();
					assignSinglePermissionRecursion(root, userid,  user, value);
				}
			}} catch (LoginException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		public String assignSinglePermission(String folderPath, String userid,
				String user,String value) {
		try {
			Node root = jcrsession.getRootNode();
			if (folderPath.length() > 1) {
				root = root.getNode(folderPath.substring(1));
			}
			
			if(root.getProperty(Config.EDMS_AUTHOR).getString().equals(userid)){
						//System.out.println(":before assigning permissions like "+value +" to user : "+user+" on "+root.getName());
						assignSinglePermissionRecursion(root, userid, user, value);
						//System.out.println(":value contains n "+value.contains("n"));
						if(!value.contains("n")&&value.contains("r")){
							
						Workspace ws=jcrsession.getWorkspace();
						Node rty=jcrsession.getRootNode();
						if(!rty.hasNode(user+"/"+root.getName())){
						ws.clone(ws.getName(), root.getPath(), "/"+user+"/"+root.getName() , false);
						}
						else{
						//System.out.println("already exist");	
						}}
						else{
							if(value.contains("r")){
							Node remov=jcrsession.getRootNode().getNode(user+"/"+root.getName());
							remov.remove();
							}
						}
						jcrsession.save();
			}else{
				return "sorry you don't have permissions to share this folder";	
			}
		}
		catch (LoginException e) {
			e.printStackTrace();
			return "failure";
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "failure";
		} finally {
			//jcrsession.logout();
		}
		return "Success";
	}
	

		public String shareFolderByPath(String folderPath, String userid,
				String users,String groups,String userpermissions,String grouppermissions) {
		//Repository	repository =  new TransientRepository();
		//Folder folder1 = new Folder();
		//Session jcrsession=null;
		try {
			/*jcrsession = repository.login(new SimpleCredentials("admin",
					"admin".toCharArray()));*/
			/* jcrsession = repository.login(new SimpleCredentials(
			 userid,"redhat".toCharArray()));*/
		
			Node root = jcrsession.getRootNode();
			if (folderPath.length() > 1) {
				root = root.getNode(folderPath.substring(1));
			}
			
			if(root.getProperty(Config.EDMS_AUTHOR).getString().equals(userid)){
				shareFolderRecursion(root, userpermissions, grouppermissions, users, groups);
				
				Workspace ws=jcrsession.getWorkspace();
				
				
				for(String user:users.split(","))
				{
						if(!user.equals(userid)){
							Node rty=jcrsession.getRootNode();
							if(!rty.hasNode(user+"/"+root.getName())){
							ws.clone(ws.getName(), root.getPath(), "/"+user+"/"+root.getName() , false);
						}
						else{
						//System.out.println("already exist");	
						}
						}
				}
				jcrsession.save();
			}else{
				return "sorry you don't have permissions to share this folder";	
			}
		}
		catch (LoginException e) {
			e.printStackTrace();
			return "failure";
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "failure";
		} finally {
			//jcrsession.logout();
		}
		return "Success";
	}
	
	
	
	
	/*public boolean shareFolderByPath(Session jcrsession,String folderPath, String userid,
			String users,String groups,String userpermissions,String grouppermissions) {
		
		try {
			Node root = jcrsession.getRootNode();
			if(userid!="admin"){
				root=root.getNode(userid);
				}
				if (folderPath.length() > 1) {
				root = root.getNode(folderPath.substring(1));
			}
			//Workspace ws = jcrsession.getWorkspace();
			//System.out
					.println(repository.OPTION_SHAREABLE_NODES_SUPPORTED
							+ " shareable or not  "
							+ root.getPath().toString()
							+ "^^^^^^^^^^^^^^^^^ sharing to "
							+ "/santosh@avi-oil.com/"
							+ root.getPath()
									.toString()
									.substring(
											root.getPath().toString()
													.lastIndexOf("/")));//ws.clone(ws.getName(), "/sanjay", "/sanjay", false);
			//jcrsession.save();
		} catch (LoginException e) {
			e.printStackTrace();
			return false;
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}*/

	/**
	 * Convert a Value array to String array.
	 */
	public static String[] Value2String(Value[] values)
			throws ValueFormatException, IllegalStateException,
			javax.jcr.RepositoryException {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < values.length; i++) {
			list.add(values[i].getString());
		}
		return (String[]) list.toArray(new String[list.size()]);
	}

	public User createUser(String userid, String password, Session jcrsession,
			Node root) {
		User user = null;
		try {
			JackrabbitSession js = (JackrabbitSession) jcrsession;
			UserManager userManager = js.getUserManager();
			user = userManager.createUser(userid,password);
			root=jcrsession.getRootNode();
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
			userNode.addMixin(JcrConstants.MIX_VERSIONABLE);
			jcrsession.save();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return user;
	}
	
	public Group createGroup(String userid, String groupName, Session jcrsession,
			Node root) {
		User user = null;
		Group group=null;
		try {
			JackrabbitSession js = (JackrabbitSession) jcrsession;
			UserManager userManager = js.getUserManager();
			user = ((User) js.getUserManager().getAuthorizable(userid));
			group = userManager.createGroup(user.getPrincipal(),groupName);
			jcrsession.save();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return group;
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
		}
	}

	public String getPermissions(User user, String path,Session jcrsession) {
		String permissions="";
		try {
			
		Node	root = jcrsession.getRootNode();
		JackrabbitSession js = (JackrabbitSession) jcrsession;
		AccessControlManager aMgr;
			aMgr = jcrsession
					.getAccessControlManager();
		// get supported privileges of any node
		/* Privilege[] privileges = aMgr
				.getSupportedPrivileges(root.getPath());
		for (int i = 0; i < privileges.length; i++) {
			//System.out.println(privileges[i]);
		}*/

		// get now applied privileges on a node 
		Privilege[]	privileges = aMgr.getPrivileges(root.getPath());
		for (int i = 0; i < privileges.length; i++) {
			//System.out.println(privileges[i]);
			permissions+=privileges[i];
		}
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return permissions;
	}

	public void setPolicy(Session jcrsession, Node root, String userid,
			String path,String permission) {
		try {
			root = jcrsession.getRootNode();
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
		}
	}



	public FolderListReturn listSharedFolder(String userid) {
		FolderListReturn folderList1 = new FolderListReturn();
		ArrayOfFolders folders = new ArrayOfFolders();
		Node root = null;

		Repository	repository =  new TransientRepository();
	//	Session jcrsession = null;
		try {
			/*	jcrsession = repository.login(new SimpleCredentials("admin",
					"admin".toCharArray()));*/
			/* jcrsession = repository.login(new SimpleCredentials(
			 userid,"redhat".toCharArray()));*/
			 /*String[] wwws=		jcrsession.getWorkspace().getAccessibleWorkspaceNames();
				for (int i = 0; i < wwws.length; i++) {
					//System.out.println(wwws[i]);
				}	*/
				root = jcrsession.getRootNode();
				root=root.getNode(userid);
			for (NodeIterator nit = root.getNodes(); nit.hasNext();) {
				Node node = nit.nextNode();
				/*boolean recycle=false;
				if(node.hasProperty(Config.EDMS_RECYCLE_DOC)){
				recycle=node.getProperty(Config.EDMS_RECYCLE_DOC).getBoolean();
				}if(!recycle){*/
				if(!node.getProperty(Config.EDMS_AUTHOR).getString().equals(userid)){
				if (Config.EDMS_FOLDER.equals(node.getPrimaryNodeType().getName())) {

					if(node.hasProperty(Config.USERS_READ)){
					Value[] actualUsers = node.getProperty(Config.USERS_READ).getValues();
					String newUser="";
					for (int i = 0; i < actualUsers.length; i++) {
						newUser+=actualUsers[i].getString()+",";
					}
					//System.out.println("for node : "+node.getPath().toString()+" newUser contains "+node.getProperty(Config.EDMS_AUTHOR).getString()+" is "+newUser.contains(userid));
					if(newUser.contains(userid))
					{
					Folder folder = new Folder();
					folder=setProperties(node,folder,userid);
					folders.getFolderList().add(folder);}
				}}
			}}
			//}
		} catch (LoginException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} finally {
		//	jcrsession.logout();
		}
		folderList1.setFolderListResult(folders);
		folderList1.setSuccess(true);
		return folderList1;
	}
	public FolderListReturn listSharedFolder(String userid,String path) {
		FolderListReturn folderList1 = new FolderListReturn();
		ArrayOfFolders folders = new ArrayOfFolders();
		Node root = null;
		try {
				root = jcrsession.getRootNode();
				root = root.getNode(path.substring(1));
			for (NodeIterator nit = root.getNodes(); nit.hasNext();) {
				Node node = nit.nextNode();
				/*boolean recycle=false;
				if(node.hasProperty(Config.EDMS_RECYCLE_DOC)){
				recycle=node.getProperty(Config.EDMS_RECYCLE_DOC).getBoolean();
				}if(!recycle){*/
				if(!node.getProperty(Config.EDMS_AUTHOR).getString().equals(userid)){
				if (Config.EDMS_FOLDER.equals(node.getPrimaryNodeType().getName())) {

					if(node.hasProperty(Config.USERS_READ)){
					Value[] actualUsers = node.getProperty(Config.USERS_READ).getValues();
					String newUser="";
					for (int i = 0; i < actualUsers.length; i++) {
						newUser+=actualUsers[i].getString()+",";
					}
					//System.out.println("for node : "+node.getPath().toString()+" newUser contains "+node.getProperty(Config.EDMS_AUTHOR).getString()+" is "+newUser.contains(userid));
					if(newUser.contains(userid))
					{
					Folder folder = new Folder();
					folder=setProperties(node,folder,userid);
					folders.getFolderList().add(folder);}
				}}
			}}
			//}
		} catch (LoginException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} finally {
		//	jcrsession.logout();
		}
		folderList1.setFolderListResult(folders);
		folderList1.setSuccess(true);
		return folderList1;
	}
	

	public FolderListReturn listRecycledDoc(String userid,String path) {
		FolderListReturn folderList1 = new FolderListReturn();
		ArrayOfFolders folders = new ArrayOfFolders();
		folders=listRecycledDocRecursion(userid, "/"+userid+"/trash", folders);
		folderList1.setFolderListResult(folders);
		folderList1.setSuccess(true);
		return folderList1;
	}

	public ArrayOfFolders listRecycledDocRecursion(String userid,String path,ArrayOfFolders folders) {
		Node root = null;
		try {
				root = jcrsession.getRootNode();
				root = root.getNode(path.substring(1));
			for (NodeIterator nit = root.getNodes(); nit.hasNext();) {
				Node node = nit.nextNode();
			/*	boolean recycle=false;
				if(node.hasProperty(Config.EDMS_RECYCLE_DOC)){
				recycle=node.getProperty(Config.EDMS_RECYCLE_DOC).getBoolean();
				}
				if(recycle){
*/
					//System.out.println("recycled nodes are "+node.getPath());
					if(node.getProperty(Config.EDMS_AUTHOR).getString().equals(userid)){
						Folder folder = new Folder();
						folder=setProperties(node,folder,userid);
						folders.getFolderList().add(folder);
					//}
				/*}else if(node.hasNodes()){
					listRecycledDocRecursion(userid,node.getPath().toString(),folders);
				}*/}
			}
		} catch (LoginException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} finally {
		//	jcrsession.logout();
		}
		return folders;
	}

	public String recycleFolder(String folderPath,String userid )
	{
		String response="";
		try {
			Node root = jcrsession.getRootNode();
			root=root.getNode(folderPath.substring(1));
			if(root.hasProperty(Config.USERS_DELETE)){
				Value[] actualUsers = root.getProperty(Config.USERS_DELETE).getValues();
				String newUser="";
				for (int i = 0; i < actualUsers.length; i++) {
					newUser+=actualUsers[i].getString()+",";
				}
				if(newUser.contains(userid)||root.getProperty(Config.EDMS_AUTHOR).getString().equals(userid)){
			recycleFolderRecursion(root,userid);
			response= "true";
		}else{
			response="false";
		}
		}}catch (RepositoryException e) {
			response= "false";
			e.printStackTrace();
		}
		return response;
	}
	
	public void recycleFolderRecursion(Node root,String userid){
		try{
		Node parent=root.getParent();
		//root.setProperty(Config.EDMS_RECYCLE_DOC, true);
		root.setProperty(Config.EDMS_RESTORATION_PATH, root.getPath());
		jcrsession.save();
		jcrsession.getWorkspace().copy(root.getPath(), "/"+userid+"/trash/"+root.getName());
		jcrsession.save();
		root.setProperty(Config.USERS_READ, new String[]{});
		//root.removeShare();
		
		root.remove();
		jcrsession.save();
		int no_of_folders=Integer.parseInt(parent.getProperty(Config.EDMS_NO_OF_FOLDERS).getString());
		if(no_of_folders>0){
		parent.setProperty(Config.EDMS_NO_OF_FOLDERS,no_of_folders-1);
		}
		jcrsession.save();
		/*	if(root.hasNodes()){
			for (NodeIterator nit = root.getNodes(); nit.hasNext();) {
				Node node = nit.nextNode();
				recycleFolderRecursion(node,userid);
			}
		}*/
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		
	}
	public String deleteFolder(String folderPath,String userid )
	{
		String response="";
		try {
			Node root = jcrsession.getRootNode();
			root=root.getNode(folderPath.substring(1));
			root.remove();
			jcrsession.save();
			response= "success";
		} catch (RepositoryException e) {
			response= "Exception Occured";
		}
		return response;
	}

	public String restoreFolder(String folderPath, String userid) {
		String response="";
		try {
			Node root = jcrsession.getRootNode();
			root=root.getNode(folderPath.substring(1));
			restoreFolderRecursion(root,userid);
			response= "success";
		}catch (RepositoryException e) {
			response= "Exception Occured";
			e.printStackTrace();
		}
		return response;
	}
	

	public void restoreFolderRecursion(Node root,String userid){
		try{
			String parents=root.getProperty(Config.EDMS_RESTORATION_PATH).getString().substring(1);
			parents=parents.substring(0,parents.lastIndexOf("/"));
			//System.out.println("parent is "+parents);
			Node jcrRoot=jcrsession.getRootNode();
			Node parent;
		if(jcrRoot.hasNode(parents)){
			parent = jcrRoot.getNode(parents);
		}else{
			parent=createFolderRecursionWhenNotFound(parents,userid);
		}
		//root.setProperty(Config.EDMS_RECYCLE_DOC, false);
		//System.out.println(root.getPath()+" source to : "+root.getProperty(Config.EDMS_RESTORATION_PATH).getString());
		root.getSession().getWorkspace().copy(root.getPath(), root.getProperty(Config.EDMS_RESTORATION_PATH).getString());
		jcrsession.save();
		root.remove();
		jcrsession.save();
		int no_of_folders=Integer.parseInt(parent.getProperty(Config.EDMS_NO_OF_FOLDERS).getString());
		parent.setProperty(Config.EDMS_NO_OF_FOLDERS,no_of_folders+1);
		jcrsession.save();
		/*	if(root.hasNodes()){
			for (NodeIterator nit = root.getNodes(); nit.hasNext();) {
				Node node = nit.nextNode();
				restoreFolderRecursion(node);
			}
		}*/
	} catch (RepositoryException e) {
		e.printStackTrace();
	}
}
	public Node createFolderRecursionWhenNotFound(String folderName,String userid){
		Node folder = null;
		try {
			Node root = jcrsession.getRootNode();
			//System.out.println("in folder creation recursion "+folderName.substring(0,folderName.lastIndexOf("/")+1));
			String parent=folderName.substring(0,folderName.lastIndexOf("/"));
			if(root.hasNode(parent)){
				root=root.getNode(parent);
				folder = root.addNode(folderName.substring(folderName.lastIndexOf("/")+1), Config.EDMS_FOLDER);
				folder.setProperty(Config.USERS_READ, new String[] {  });
				folder.setProperty(Config.USERS_WRITE, new String[] {  });
				folder.setProperty(Config.USERS_DELETE, new String[] {  });
				folder.setProperty(Config.USERS_SECURITY, new String[] {  });
				folder.setProperty(Config.GROUPS_READ, new String[] {  });
				folder.setProperty(Config.GROUPS_WRITE, new String[] {  });
				folder.setProperty(Config.GROUPS_DELETE, new String[] {  });
				folder.setProperty(Config.GROUPS_SECURITY, new String[] {  });
				folder.setProperty(Config.EDMS_KEYWORDS, "root,folder".split(","));
				folder.setProperty(Config.EDMS_AUTHOR, userid);
				folder.setProperty(Config.EDMS_DESCRIPTION, "this is system created folder while restoration");
				folder.setProperty(Config.EDMS_CREATIONDATE,(new Date()).toString());
				folder.setProperty(Config.EDMS_MODIFICATIONDATE,(new Date()).toString());
				folder.setProperty(Config.EDMS_RECYCLE_DOC, false);
				folder.setProperty(Config.EDMS_NO_OF_FOLDERS, 0);
				folder.setProperty(Config.EDMS_NO_OF_DOCUMENTS, 0);
				folder.addMixin(JcrConstants.MIX_SHAREABLE);
				folder.addMixin(JcrConstants.MIX_VERSIONABLE);
				jcrsession.save();
			}else{
				createFolderRecursionWhenNotFound(folderName.substring(0,folderName.lastIndexOf("/")+1),userid);
			}
			
		} catch (LoginException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return folder;
	
	}

	public RenameFolderRes renameFolder(String oldfolderPath, String newFolderPath,String userid) {
		RenameFolderRes response=new RenameFolderRes();
		try {
			
			Node forVer=jcrsession.getRootNode().getNode(oldfolderPath.substring(1));
			//System.out.println(forVer.getProperty(Config.EDMS_AUTHOR).getString());
			if(forVer.getProperty(Config.EDMS_AUTHOR).getString().equals(userid)){
			jcrsession.getWorkspace().getVersionManager().checkin(forVer.getPath());
			jcrsession.move(oldfolderPath, oldfolderPath.substring(0,oldfolderPath.lastIndexOf("/")) + "/" + newFolderPath);
			jcrsession.save();	
			
			jcrsession.getWorkspace().getVersionManager().checkout(forVer.getPath());
			
			response.setResponse("Success");
			response.setSuccess(true);
		}else
		{

			   response.setResponse("Access Denied");
			   response.setSuccess(false);	
		}}
		catch (RepositoryException e) {
			   response.setResponse("Access Denied");
			   response.setSuccess(false);
			e.printStackTrace();
		}
		return response;
	}

	public String restoreVersion(String folderPath, String versionName,String userid) {
		String response="";
		try {
			VersionManager versionManager=jcrsession.getWorkspace().getVersionManager();
			Node forVer=jcrsession.getRootNode().getNode(folderPath.substring(1));
			//System.out.println(forVer.getProperty(Config.EDMS_AUTHOR).getString());
			if(forVer.getProperty(Config.EDMS_AUTHOR).getString().equals(userid)){
				Version version=versionManager.getVersionHistory(folderPath).getVersion(versionName);
			
			
			versionManager.restore(version, true);
			jcrsession.save();
			versionManager.checkout(forVer.getPath());
			response="success";
		}else
		{
			   response="Access Denied";
		}}
		catch (RepositoryException e) {
			   response="Access Denied";
			e.printStackTrace();
		}
		return response;
	}
	/*public static String[] usrValue2String(Value[] values, String usrId) throws ValueFormatException, IllegalStateException, javax.jcr.RepositoryException {
		ArrayList<String> list = new ArrayList<String>();
		
		for (int i=0; i<values.length; i++) {
			// Admin and System user is not propagated across the child nodes
			if (!values[i].getString().equals(Config.SYSTEM_USER) && 
					!values[i].getString().equals(Config.ADMIN_USER)) {
				list.add(values[i].getString());
			}
		}
		
		if (Config.USER_ASSIGN_DOCUMENT_CREATION) {
			// No add an user twice
			if (!list.contains(usrId)) {
				list.add(usrId);
			}
		}
		
		return (String[]) list.toArray(new String[list.size()]);
	}*/
}