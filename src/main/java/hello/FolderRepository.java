package hello;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.Workspace;
import javax.jcr.version.Version;
import javax.jcr.version.VersionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.api.JackrabbitSession;

import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;

/*import org.apache.tika.metadata.Office;*/

import com.edms.documentmodule.ArrayOfFiles;
import com.edms.documentmodule.CopyDocResponse;
import com.edms.documentmodule.File;
import com.edms.documentmodule.ArrayOfFolders;
import com.edms.documentmodule.FilesAndFolders;
import com.edms.documentmodule.Folder;
import com.edms.documentmodule.FolderListReturn;
import com.edms.documentmodule.GetRecycledDocsResponse;
import com.edms.documentmodule.MoveDocResponse;
import com.edms.documentmodule.RecentlyModifiedResponse;
import com.edms.documentmodule.RenameFolderRes;
import com.edms.documentmodule.SetSortOrderResponse;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import edms.core.Config;
import edms.core.JcrRepositoryUtils;
import edms.core.SessionWrapper;

@Component
public class FolderRepository {
	//private static Session jcrsession =jcrsession.jcrsession;
	/*
	 * @Autowired FolderService folderService;
	 */

	// @Autowired DefaultSpringSecurityContextSource contextSource;

	public SetSortOrderResponse setSortOrder(String sortOrder,String userid){
		SetSortOrderResponse response=new SetSortOrderResponse();
		Config.EDMS_SORT_ORDER=sortOrder;
		response.setSuccess(true);
		return response;
	}
	
	
	
	
	

	

	public FolderListReturn listFolder(String name, String userid) {
		
	 //DocumentConverter docConverter
		
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		Assert.notNull(name);
		FolderListReturn folderList1 = new FolderListReturn();
		ArrayOfFolders folders = new ArrayOfFolders();
		try {
					//System.out.println("userid in list Folder is "+userid);
					javax.jcr.query.QueryManager queryManager;
					queryManager = jcrsession.getWorkspace().getQueryManager();
					String expression = "select * from [edms:folder] AS s WHERE ISCHILDNODE(s,'"+name+"') and CONTAINS(s.[edms:owner],'*"+userid+"* OR *"+Config.EDMS_ADMINISTRATOR+"*') ORDER BY s.["+Config.EDMS_Sorting_Parameter+"] ASC";
					//expression = "select * from [edms:folder] AS s WHERE NAME like ['%san%']";
				    javax.jcr.query.Query query = queryManager.createQuery(expression, javax.jcr.query.Query.JCR_SQL2);
				    javax.jcr.query.QueryResult result = query.execute();
					System.out.println(expression);
				//	System.out.println("size of list in list folder "+result);
			for (NodeIterator nit = result.getNodes(); nit.hasNext();) {
					Node node = nit.nextNode();
					Folder folder = new Folder();
					folder=setGeneralFolderProperties(node,folder,userid);
					folders.getFolderList().add(folder);
					}
			folderList1.setFolderListResult(folders);
			folderList1.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			//JcrRepositoryUtils.logout(sessionId);
		}
		return folderList1;
	}

	

	public String countFolder(String name, String userid) {
		
	 //DocumentConverter docConverter
		String results="0";
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		Assert.notNull(name);
		try {
					//System.out.println("userid in list Folder is "+userid);
					javax.jcr.query.QueryManager queryManager;
					queryManager = jcrsession.getWorkspace().getQueryManager();
					String expression = "select * from [edms:folder] AS s WHERE ISCHILDNODE(s,'"+name+"') and CONTAINS(s.[edms:owner],'*"+userid+"* OR *"+Config.EDMS_ADMINISTRATOR+"*') ORDER BY s.["+Config.EDMS_Sorting_Parameter+"] ASC";
					//expression = "select * from [edms:folder] AS s WHERE NAME like ['%san%']";
				    javax.jcr.query.Query query = queryManager.createQuery(expression, javax.jcr.query.Query.JCR_SQL2);
				    javax.jcr.query.QueryResult result = query.execute();
					System.out.println(expression);
				//	System.out.println("size of list in list folder "+result);
					results=Long.toString(result.getNodes().getSize());
			//for (NodeIterator nit = result.getNodes(); nit.hasNext();) {}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			//JcrRepositoryUtils.logout(sessionId);
		}
		return results;
	}
	public String countFiles(String name, String userid) {
		
		 //DocumentConverter docConverter
			String results="0";
			SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
			Session jcrsession = sessions.getSession();
			//String sessionId=sessions.getId();
			Assert.notNull(name);
			try {
						//System.out.println("userid in list Folder is "+userid);
						javax.jcr.query.QueryManager queryManager;
						queryManager = jcrsession.getWorkspace().getQueryManager();
						String expression = "select * from [edms:document] AS s WHERE ISCHILDNODE(s,'"+name+"') and CONTAINS(s.[edms:owner],'*"+userid+"* OR *"+Config.EDMS_ADMINISTRATOR+"*') ORDER BY s.["+Config.EDMS_Sorting_Parameter+"] ASC";
						//expression = "select * from [edms:folder] AS s WHERE NAME like ['%san%']";
					    javax.jcr.query.Query query = queryManager.createQuery(expression, javax.jcr.query.Query.JCR_SQL2);
					    javax.jcr.query.QueryResult result = query.execute();
						System.out.println(expression);
					//	System.out.println("size of list in list folder "+result);
						results=Long.toString(result.getNodes().getSize());
				//for (NodeIterator nit = result.getNodes(); nit.hasNext();) {}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				//JcrRepositoryUtils.logout(sessionId);
			}
			return results;
		}

	public Folder setProperties(Node node, Folder folder,String userid,Session jcrsession) {
		try {
			
		folder.setFolderName(node.getName());
		//if(node.hasProperty(Config.EDMS_NAME)){
			//System.out.println("title of doc is : "+node.getProperty(Config.EDMS_NAME).getString());
//		}
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
		
		//if(node.hasProperty(Config.EDMS_NO_OF_DOCUMENTS)){
			folder.setNoOfDocuments(countFiles(node.getPath(), userid));
		
		//}
		//if(node.hasProperty(Config.EDMS_NO_OF_FOLDERS)){
			folder.setNoOfFolders(countFolder(node.getPath(), userid));
		//}
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

			folder.setParentFolder(node.getParent().getName());
			folder.setFolderPath(node.getPath());
		/*	VersionHistory history = jcrsession.getWorkspace().getVersionManager().getVersionHistory(node.getPath());
			// To iterate over all versions
			VersionIterator versions = history.getAllVersions();
			while (versions.hasNext()) {
			  Version version = versions.nextVersion();
			  FolderVersionDetail versionDetail=new FolderVersionDetail();
			  versionDetail.setCreatedBy(userid);
			  versionDetail.setCreationDate(version.getCreated().getTime().toString());
			  String[] details=history.getVersionLabels(version);
			  if(details.length>0){
			  versionDetail.setDetails(details[0]);
			  }  versionDetail.setVersionName(version.getName());
			  versionDetail.setVersionLabel(version.getParent().getName());
			  folder.getFolderVersionsHistory().add(versionDetail);
			}*/
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return folder;
	}

	public Boolean hasChild(String folderPath, String userid) {

		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		javax.jcr.query.QueryManager queryManager;
		boolean flag = false;
		try {
			queryManager = jcrsession.getWorkspace().getQueryManager();
			// Create a query object ...
			String expression = "select * from [edms:folder] AS s WHERE ISDESCENDANTNODE(s,'"
					+ folderPath
					+ "')";
			//System.out.println(expression);
			javax.jcr.query.Query query = queryManager.createQuery(expression,
					javax.jcr.query.Query.JCR_SQL2);
			javax.jcr.query.QueryResult result = query.execute();
			for (NodeIterator nit = result.getNodes(); nit.hasNext();) {
				flag=true;
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			//JcrRepositoryUtils.logout(sessionId);
		}
		return flag;
	}

	public Folder createFolder(String folderName, String parentFolder,
			String userid, String keywords, String description) {
		if(folderName.indexOf('.')>=0||folderName.indexOf('.')>=0
				||folderName.indexOf('@')>=0||folderName.indexOf(',')>=0
				||folderName.indexOf('*')>=0||folderName.indexOf('>')>=0
				||folderName.indexOf('<')>=0||folderName.indexOf(')')>=0
				||folderName.indexOf('(')>=0){
				return null;
		}else{			
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		Node folder = null;
		Folder folder1 = new Folder();
		try {
			/*jcrsession = repository.login(new SimpleCredentials(Config.EDMS_ADMIN,
					Config.EDMS_ADMIN.toCharArray()));*/
			/*jcrsession = repository.login(new SimpleCredentials(
			 userid,"redhat".toCharArray()));*/
		
			Node root = jcrsession.getRootNode();
			/*if(userid!=Config.EDMS_ADMIN){
			root=root.getNode(userid);
			}*/
			if (parentFolder.length() > 1) {
				root = root.getNode(parentFolder.substring(1));
			}
			String folderNames=folderName;
			while(root.hasNode(folderNames)){
				folderNames+="-copy";
			}
			folderName=folderNames;
			if(root.hasProperty(Config.USERS_WRITE)){
			Value[] actualUsers = root.getProperty(Config.USERS_WRITE).getValues();
			
			String newUsers = "";
			
			for (int i = 0; i < actualUsers.length; i++) {
				newUsers+=actualUsers[i].getString()+",";
			}
			if(newUsers.contains(userid)||root.getProperty(Config.EDMS_AUTHOR).getString().equals(userid)||root.getProperty(Config.EDMS_OWNER).getString().contains(userid)||root.getProperty(Config.EDMS_OWNER).getString().contains(""+Config.EDMS_ADMINISTRATOR+"")||(root.getName().equals(userid)&&(root.getProperty(Config.EDMS_AUTHOR).getString()).equals(Config.JCR_USERNAME))){
			jcrsession.save();
	/*			Version version=jcrsession.getWorkspace().getVersionManager().checkin(root.getPath());
			jcrsession.getWorkspace().getVersionManager().getVersionHistory(root.getPath()).addVersionLabel(version.getName(), "added new folder named "+folderName+"", true);
			jcrsession.getWorkspace().getVersionManager().checkout(root.getPath());*/
			folder = root.addNode(folderName, Config.EDMS_FOLDER);
			
			if(root.hasProperty(Config.USERS_READ)&&(!root.getProperty(Config.EDMS_AUTHOR).toString().equals(Config.EDMS_ADMIN))){
				Value[] actualUser = root.getProperty(Config.USERS_READ).getValues();
				String newUser="";
				for (int i = 0; i < actualUser.length; i++) {
					newUser+=actualUser[i].getString()+",";
				}
			folder.setProperty(Config.USERS_READ, newUser.split(","));
			}else{
				folder.setProperty(Config.USERS_READ, new String[]{});
			}
			if(root.hasProperty(Config.USERS_WRITE)&&!root.getProperty(Config.EDMS_AUTHOR).toString().equals(Config.EDMS_ADMIN)){
				Value[] actualUser = root.getProperty(Config.USERS_WRITE).getValues();
				String newUser="";
				for (int i = 0; i < actualUser.length; i++) {
					newUser+=actualUser[i].getString()+",";
				}
			folder.setProperty(Config.USERS_WRITE, newUser.split(","));
			}else{
				folder.setProperty(Config.USERS_WRITE, new String[]{});
			}
			if(root.hasProperty(Config.USERS_DELETE)&&!root.getProperty(Config.EDMS_AUTHOR).toString().equals(Config.EDMS_ADMIN)){
				Value[] actualUser = root.getProperty(Config.USERS_DELETE).getValues();
				String newUser="";
				for (int i = 0; i < actualUser.length; i++) {
					newUser+=actualUser[i].getString()+",";
				}
			folder.setProperty(Config.USERS_DELETE, newUser.split(","));
			}else{
				folder.setProperty(Config.USERS_DELETE, new String[]{});
			}
			if(root.hasProperty(Config.USERS_SECURITY)&&!root.getProperty(Config.EDMS_AUTHOR).toString().equals(Config.EDMS_ADMIN)){
				Value[] actualUser = root.getProperty(Config.USERS_SECURITY).getValues();
				String newUser="";
				for (int i = 0; i < actualUser.length; i++) {
					newUser+=actualUser[i].getString()+",";
				}
			folder.setProperty(Config.USERS_SECURITY, newUser.split(","));
			}else{
				folder.setProperty(Config.USERS_SECURITY, new String[]{});
			}

			if(root.hasProperty(Config.GROUPS_READ)&&!root.getProperty(Config.EDMS_AUTHOR).toString().equals(Config.EDMS_ADMIN)){
				Value[] actualUser = root.getProperty(Config.GROUPS_READ).getValues();
				String newUser="";
				for (int i = 0; i < actualUser.length; i++) {
					newUser+=actualUser[i].getString()+",";
				}
			folder.setProperty(Config.GROUPS_READ, newUser.split(","));
			}else{
				folder.setProperty(Config.GROUPS_READ, new String[]{});
			}

			if(root.hasProperty(Config.GROUPS_WRITE)&&!root.getProperty(Config.EDMS_AUTHOR).toString().equals(Config.EDMS_ADMIN)){
				Value[] actualUser = root.getProperty(Config.GROUPS_WRITE).getValues();
				String newUser="";
				for (int i = 0; i < actualUser.length; i++) {
					newUser+=actualUser[i].getString()+",";
				}
			folder.setProperty(Config.GROUPS_WRITE, newUser.split(","));
			}else{
				folder.setProperty(Config.GROUPS_WRITE, new String[]{});
			}

			if(root.hasProperty(Config.GROUPS_DELETE)&&!root.getProperty(Config.EDMS_AUTHOR).toString().equals(Config.EDMS_ADMIN)){
				Value[] actualUser = root.getProperty(Config.GROUPS_DELETE).getValues();
				String newUser="";
				for (int i = 0; i < actualUser.length; i++) {
					newUser+=actualUser[i].getString()+",";
				}
			folder.setProperty(Config.GROUPS_DELETE, newUser.split(","));
			}else{
				folder.setProperty(Config.GROUPS_DELETE, new String[]{});
			}

			if(root.hasProperty(Config.GROUPS_SECURITY)&&!root.getProperty(Config.EDMS_AUTHOR).toString().equals(Config.EDMS_ADMIN)){
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
			
			if(root.hasProperty(Config.EDMS_OWNER)){
				System.out.println(root.getProperty(Config.EDMS_OWNER).getString()+","+userid);
			folder.setProperty(Config.EDMS_OWNER,root.getProperty(Config.EDMS_OWNER).getString()+","+userid);
			}else{
				folder.setProperty(Config.EDMS_OWNER,root.getProperty(Config.EDMS_AUTHOR).getString()+","+userid);
			}
				folder.setProperty(Config.EDMS_AUTHOR,userid);
			
			folder.setProperty(Config.EDMS_DESCRIPTION, description);
			SimpleDateFormat format = new SimpleDateFormat(
					"YYYY-MM-dd'T'HH:mm:ss.SSSZ");
			folder.setProperty(Config.EDMS_CREATIONDATE,
					(format.format(new Date())).toString().replace("+0530", "Z"));
			folder.setProperty(Config.EDMS_MODIFICATIONDATE,
					"");
			folder.setProperty(Config.EDMS_ACCESSDATE,
					"");
			folder.setProperty(Config.EDMS_DOWNLOADDATE,
					"");
			folder.setProperty(Config.EDMS_RECYCLE_DOC, false);
			folder.setProperty(Config.EDMS_NO_OF_FOLDERS, 0);
			folder.setProperty(Config.EDMS_NO_OF_DOCUMENTS, 0);
			//folder.addMixin(JcrConstants.MIX_SHAREABLE);
			folder.addMixin(JcrConstants.MIX_VERSIONABLE);

			//folder.setProperty(Config.EDMS_TITLE,folderName);
			folder.setProperty(Config.EDMS_NAME,folderName);
			jcrsession.save();
			root.setProperty(Config.EDMS_NO_OF_FOLDERS, Integer.parseInt(root.getProperty(Config.EDMS_NO_OF_FOLDERS).getString())+1);
			jcrsession.save();
			//	System.out.println("Owner of newly created folder is : "+folder.getProperty(Config.EDMS_OWNER).getString());
			folder1=	setGeneralFolderProperties(folder, folder1,userid);
			}else{
				System.out.println("you have not permission to add child node");
			}}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//JcrRepositoryUtils.logout(sessionId);
		}
		return folder1;
		}
	}

	public Folder getFolderByPath(String folderPath, String userid) {
	
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		// AccessControlManager acm = jcrsession.getAccessControlManager();
		
		// AccessControlList acl = getList(acm, node.getPath());

		Folder folder1 = new Folder();
		File file1 = new File();
		try {
		/*	jcrsession = repository.login(new SimpleCredentials(Config.EDMS_ADMIN,
					Config.EDMS_ADMIN.toCharArray()));*/
			/* jcrsession = repository.login(new SimpleCredentials(
			 userid,"redhat".toCharArray()));*/
		
			Node root = jcrsession.getRootNode();
			/*if(userid!=Config.EDMS_ADMIN){
				root=root.getNode(userid);
				}*/
			
			if (folderPath.length() > 1) {
				root = root.getNode(folderPath.substring(1));
			}
			
			//SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ss.SSSZ");
			
			//jcrsession.getWorkspace().getVersionManager().checkout(root.getPath());
			/*root.setProperty(Config.EDMS_ACCESSDATE,
					(format.format(new Date())).toString().replace("+0530", "Z"));
			root.setProperty(Config.EDMS_DOWNLOADDATE,
					(format.format(new Date())).toString().replace("+0530", "Z"));*/
			//jcrsession.save();
			
			folder1.setFolderName(root.getName().toString());
			folder1.setFolderPath(root.getPath().toString());
			file1.setFileName(root.getName().toString());
			file1.setFilePath(root.getPath().toString());
			if(root.getPath().toString().length()>1){
			if(folderPath.length()>1){
					//if(root.getProperty(Config.EDMS_AUTHOR).getString().equals(userid)||(root.getName().equals(userid)&&(root.getProperty(Config.EDMS_AUTHOR).getString()).equals(Config.JCR_USERNAME)))
					//{
							setProperties(root, folder1,userid,jcrsession);
				//	}
			}}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//JcrRepositoryUtils.logout(sessionId);
		}
		return folder1;
	}

	
	public void assignSinglePermissionRecursion(Node rt,String userid,String user,String value) {
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		try {
			rt.addMixin(JcrConstants.MIX_SHAREABLE);
			if(value.equals("ur"))
			{
				Value[] actualUsers = rt.getProperty(Config.USERS_READ).getValues();
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
				actualUsers = rt.getProperty(Config.USERS_SECURITY).getValues();
				newUser="";
				for (int i = 0; i < actualUsers.length; i++) {
					newUser+=actualUsers[i].getString()+",";
				}
				//System.out.println(newUser.contains(user));
				if(newUser.contains(user))
				{
					newUser=newUser.replace(user+",","");
					rt.setProperty(Config.USERS_SECURITY, new String[]{newUser});
				}
				actualUsers = rt.getProperty(Config.USERS_WRITE).getValues();
				newUser="";
				for (int i = 0; i < actualUsers.length; i++) {
					newUser+=actualUsers[i].getString()+",";
				}
				//System.out.println(newUser.contains(user));
				if(newUser.contains(user))
				{
					newUser=newUser.replace(user+",","");
					rt.setProperty(Config.USERS_WRITE, new String[]{newUser});
				}
				FileRepository.setPolicyForTestToFolder(user,rt.getPath());
				jcrsession.save();
				}else if(value.equals("uw")){
				Value[] actualUsers = rt.getProperty(Config.USERS_READ).getValues();
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

				actualUsers = rt.getProperty(Config.USERS_WRITE).getValues();
				newUser="";
				for (int i = 0; i < actualUsers.length; i++) {
					newUser+=actualUsers[i].getString()+",";
				}
				//System.out.println(newUser.contains(user));
				if(!newUser.contains(user))
				{
					newUser+=user+",";
					rt.setProperty(Config.USERS_WRITE, new String[]{newUser});
				}
				actualUsers = rt.getProperty(Config.USERS_SECURITY).getValues();
				newUser="";
				for (int i = 0; i < actualUsers.length; i++) {
					newUser+=actualUsers[i].getString()+",";
				}
				//System.out.println(newUser.contains(user));
				if(newUser.contains(user))
				{
					newUser=newUser.replace(user+",","");
					rt.setProperty(Config.USERS_SECURITY, new String[]{newUser});
				}
				jcrsession.save();
			}else if(value.equals("us")){
				Value[] actualUsers = rt.getProperty(Config.USERS_READ).getValues();
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
				
				actualUsers = rt.getProperty(Config.USERS_WRITE).getValues();
				newUser="";
				for (int i = 0; i < actualUsers.length; i++) {
					newUser+=actualUsers[i].getString()+",";
				}
				//System.out.println(newUser.contains(user));
				if(!newUser.contains(user))
				{
				newUser+=user+",";
				rt.setProperty(Config.USERS_WRITE, new String[]{newUser});
				}
				actualUsers = rt.getProperty(Config.USERS_SECURITY).getValues();
				newUser="";
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
			}
			if(rt.hasNodes()){
				for (NodeIterator nit = rt.getNodes(); nit.hasNext();) {
					Node root=nit.nextNode();
					if(Config.EDMS_FOLDER.equals(root.getPrimaryNodeType().getName())||Config.EDMS_DOCUMENT.equals(root.getPrimaryNodeType().getName()))
					assignSinglePermissionRecursion(root, userid,  user, value);
				}
			}} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	//accross different workspaces
	/*
	public String assignSinglePermission(String folderPath, String userid,
				String user,String value) {
			String destUser="";
			if(user.contains("/")){
			destUser=user.substring(0,user.indexOf("/"));
			}else{
				destUser=user;
			}
			SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
			Session jcrsession = sessions.getSession();
			//String sessionId=sessions.getId();
		try {
			Node root = jcrsession.getRootNode();
			if (folderPath.length() > 1) {
				root = root.getNode(folderPath.substring(1));
			}
			
			if(root.getProperty(Config.EDMS_AUTHOR).getString().equals(userid)){
						//System.out.println(":before assigning permissions like "+value +" to user : "+user+" on "+root.getName());
						assignSinglePermissionRecursion(root, userid, destUser, value);
						//System.out.println(":value contains n "+value.contains("n"));
						if(!value.contains("n")){
						Session destSession=(JcrRepositoryUtils.adminloginToWorkSpace(destUser, "redhat")).getSession();
						Workspace ws=destSession.getWorkspace();
						//String[] names=ws.getAccessibleWorkspaceNames();
						System.out.println(ws.getName());
						Node rty=destSession.getRootNode();
						if(!rty.hasNode(user+"/"+root.getName())){
					//	Session session=JcrRepositorySession.getSession(user);
					//	JcrRepositorySession.setPolicy(session,session.getRootNode(),userid,session.getRootNode().getPath(),Privilege.JCR_ALL);
						
							ws.clone(userid, root.getPath(), "/"+user+"/"+root.getName() , false);
							destSession.save();
						}
						else{
						System.out.println("already exist");	
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
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//JcrRepositoryUtils.logout(sessionId);
		}
		return "Success";
	}
	*/

	public String assignSinglePermission(String folderPath, String userid,
			String user,String value) {
		/*String destUser="";
		if(user.contains("/")){
		destUser=user.substring(0,user.indexOf("/"));
		}else{
			destUser=user;
		}*/
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
	try {
		Node root = jcrsession.getRootNode();
		if (folderPath.length() > 1) {
			root = root.getNode(folderPath.substring(1));
		}
		Value[] actualUsers = root.getProperty(Config.USERS_SECURITY).getValues();
		String newUser="";
		for (int i = 0; i < actualUsers.length; i++) {
			newUser+=actualUsers[i].getString()+",";
		}
		//System.out.println(newUser.contains(user));
		if(newUser.contains(user)||root.getProperty(Config.EDMS_AUTHOR).getString().equals(userid)||root.getProperty(Config.EDMS_OWNER).getString().contains(userid)||root.getProperty(Config.EDMS_OWNER).getString().contains(""+Config.EDMS_ADMINISTRATOR+"")){
					//System.out.println(":before assigning permissions like "+value +" to user : "+user+" on "+root.getName());
					assignSinglePermissionRecursion(root, userid, user, value);
					//System.out.println(":value contains n "+value.contains("n"));
					if(!value.contains("n")){
					Session destSession=(JcrRepositoryUtils.login(user, "redhat")).getSession();
					Workspace ws=destSession.getWorkspace();
					//String[] names=ws.getAccessibleWorkspaceNames();
					//System.out.println(ws.getName());
					Node rty=destSession.getRootNode();
					if(!rty.hasNode(user+"/"+root.getName())){
					//	Session session=JcrRepositorySession.getSession(user);
					//	JcrRepositorySession.setPolicy(session,session.getRootNode(),userid,session.getRootNode().getPath(),Privilege.JCR_ALL);
					ws.clone(ws.getName(), root.getPath(), "/"+user+"/"+root.getName() , false);
					destSession.save();
					}
					else{
					System.out.println("already exist");	
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
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		//JcrRepositoryUtils.logout(sessionId);
	}
	return "Success";
}


		
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
			e.printStackTrace();
		}
	}

	/*public String getPermissions(User user, String path,Session jcrsession) {
		String permissions="";
		try {
			
		Node	root = jcrsession.getRootNode();
		JackrabbitSession js = (JackrabbitSession) jcrsession;
		AccessControlManager aMgr;
			aMgr = jcrsession
					.getAccessControlManager();
		// get supported privileges of any node
		 Privilege[] privileges = aMgr
				.getSupportedPrivileges(root.getPath());
		for (int i = 0; i < privileges.length; i++) {
			//System.out.println(privileges[i]);
		}

		// get now applied privileges on a node 
		Privilege[]	privileges = aMgr.getPrivileges(root.getPath());
		for (int i = 0; i < privileges.length; i++) {
			//System.out.println(privileges[i]);
			permissions+=privileges[i];
		}
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return permissions;
	}
*/
	



	public FolderListReturn listSharedFolder(String userid) {
		String destUser="";
		if(userid.contains("/")){
		destUser=userid.substring(0,userid.indexOf("/"));
		}else{
			destUser=userid;
		}
		SessionWrapper sessions =JcrRepositoryUtils.login(destUser, "redhat");
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		FolderListReturn folderList1 = new FolderListReturn();
		ArrayOfFolders folders = new ArrayOfFolders();
		Node root = null;
		try {
				root = jcrsession.getRootNode();
				root=root.getNode(userid);
			for (NodeIterator nit = root.getNodes(); nit.hasNext();) {
				Node node = nit.nextNode();
				if(!node.getProperty(Config.EDMS_AUTHOR).getString().equals(destUser)){
				if (Config.EDMS_FOLDER.equals(node.getPrimaryNodeType().getName())) {

					if(node.hasProperty(Config.USERS_READ)){
					Value[] actualUsers = node.getProperty(Config.USERS_READ).getValues();
					String newUser="";
					for (int i = 0; i < actualUsers.length; i++) {
						newUser+=actualUsers[i].getString()+",";
					}
					if(newUser.contains(destUser))
					{
					Folder folder = new Folder();
					folder=setProperties(node,folder,userid,jcrsession);
					folders.getFolderList().add(folder);}
				}}
			}}
			folderList1.setFolderListResult(folders);
			folderList1.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			//JcrRepositoryUtils.logout(sessionId);
		}
		return folderList1;
	}
	public FolderListReturn listSharedFolder(String userid,String path) {
		
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		FolderListReturn folderList1 = new FolderListReturn();
		ArrayOfFolders folders = new ArrayOfFolders();
		try {
			javax.jcr.query.QueryManager queryManager;
			queryManager = jcrsession.getWorkspace().getQueryManager();
			String expression = "select * from [edms:folder] AS s WHERE ISCHILDNODE(s,'"+path+"') AND CONTAINS(s.["+Config.USERS_READ+"], '*"
					+ userid + "*') ORDER BY s.["+Config.EDMS_Sorting_Parameter+"] ASC";
			//expression = "select * from [edms:folder] AS s WHERE NAME like ['%san%']";
		    javax.jcr.query.Query query = queryManager.createQuery(expression, javax.jcr.query.Query.JCR_SQL2);
		    javax.jcr.query.QueryResult result = query.execute();
			
			for (NodeIterator nit = result.getNodes(); nit.hasNext();) {
					Node node = nit.nextNode();
					Folder folder = new Folder();
					folder=setProperties(node,folder,userid,jcrsession);
					folders.getFolderList().add(folder);
			}
			folderList1.setFolderListResult(folders);
			folderList1.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return folderList1;
	}
	

	public GetRecycledDocsResponse listRecycledDoc(String userid,String path) {
		FilesAndFolders filesFolders = new FilesAndFolders();
		filesFolders=listRecycledDocRecursion(userid, path,filesFolders);
		GetRecycledDocsResponse res=new GetRecycledDocsResponse();
		res.setGetRecycledDocs(filesFolders);
		return res;
	}

	public FilesAndFolders listRecycledDocRecursion(String userid,String path,FilesAndFolders filesFolders) {
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		ArrayOfFiles files = new ArrayOfFiles();
		ArrayOfFolders folders=new ArrayOfFolders();
		Node root = null;
		try {
				root = jcrsession.getRootNode();
				root = root.getNode(path.substring(1));
				javax.jcr.query.QueryManager queryManager;
				queryManager = jcrsession.getWorkspace().getQueryManager();
				String expression = "select * from [edms:folder] AS s WHERE ISCHILDNODE(s,'"+path+"')  and CONTAINS(s.[edms:owner],'*"+userid+"* OR *"+Config.EDMS_ADMINISTRATOR+"*') ";
				//expression = "select * from [edms:folder] AS s WHERE NAME like ['%san%']";
			    javax.jcr.query.Query query = queryManager.createQuery(expression, javax.jcr.query.Query.JCR_SQL2);
			    javax.jcr.query.QueryResult result = query.execute();
				
		for (NodeIterator nit = result.getNodes(); nit.hasNext();) {
					Node node = nit.nextNode();
							Folder folder = new Folder();
							folder=setGeneralFolderProperties(node,folder,userid);
							folders.getFolderList().add(folder);
					}
		expression = "select * from [edms:document] AS s WHERE ISCHILDNODE(s,'"+path+"')  and CONTAINS(s.[edms:owner],'*"+userid+"* OR *"+Config.EDMS_ADMINISTRATOR+"*') ";
		//expression = "select * from [edms:folder] AS s WHERE NAME like ['%san%']";
	    query = queryManager.createQuery(expression, javax.jcr.query.Query.JCR_SQL2);
	    result = query.execute();
		
	    for (NodeIterator nit = result.getNodes(); nit.hasNext();) {

			Node node = nit.nextNode();
					File file = new File();
					file=new FileRepository().setGeneralFileProperties(node,file,userid);
					files.getFileList().add(file);
			}
		filesFolders.setFilesList(files);
		filesFolders.setFoldersList(folders);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//JcrRepositoryUtils.logout(sessionId);
		}
		
		return filesFolders;
	}

	public Folder setGeneralFolderProperties(Node node, Folder folder, String userid) {
		
		try {
		folder.setFolderName(node.getName());
		folder.setFolderPath(node.getPath());
		folder.setParentFolder(node.getParent().getName());
		folder.setCreatedBy(node.getProperty(Config.EDMS_AUTHOR).getString());
		if(node.hasProperty(Config.EDMS_MODIFICATIONDATE))
			folder.setModificationDate(node.getProperty(Config.EDMS_MODIFICATIONDATE).getString());
		if(node.hasProperty(Config.EDMS_CREATIONDATE))
			folder.setCreationDate(node.getProperty(Config.EDMS_CREATIONDATE).getString());
			
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return folder;
	}


	public String recycleFolder(String folderPath,String userid )
	{
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		String response="";
		try {
			Node root = jcrsession.getRootNode();
			root=root.getNode(folderPath.substring(1));
			if(root.hasProperty(Config.USERS_WRITE)){
				Value[] actualUsers = root.getProperty(Config.USERS_WRITE).getValues();
				String newUser="";
				for (int i = 0; i < actualUsers.length; i++) {
					newUser+=actualUsers[i].getString()+",";
				}
				if(newUser.contains(userid)||root.getProperty(Config.EDMS_AUTHOR).getString().equals(userid)||root.getProperty(Config.EDMS_OWNER).getString().contains(userid)||root.getProperty(Config.EDMS_OWNER).getString().contains(""+Config.EDMS_ADMINISTRATOR+"")){
			recycleFolderRecursion(root,userid);
			response= "true";
		}else{
			response="false";
		}
		}}catch (RepositoryException e) {
			response= "false";
			e.printStackTrace();
		}finally{
			//JcrRepositoryUtils.logout(sessionId);
		}
		return response;
	}
	
	public void recycleFolderRecursion(Node root,String userid){
		try{
			SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
			Session jcrsession = sessions.getSession();
			//String sessionId=sessions.getId();
			//root.setProperty(Config.EDMS_RECYCLE_DOC, true);
			/*root.setProperty(Config.USERS_READ, new String[]{});
			root.setProperty(Config.USERS_WRITE, new String[]{});
			root.setProperty(Config.USERS_SECURITY, new String[]{});
			root.setProperty(Config.GROUPS_READ, new String[]{});
			root.setProperty(Config.GROUPS_WRITE, new String[]{});
			root.setProperty(Config.GROUPS_DELETE, new String[]{});*/
			root.setProperty(Config.EDMS_RESTORATION_PATH, root.getPath());
			
			jcrsession.save();
			String relpath=userid+"/trash/"+root.getName();
			while(jcrsession.getRootNode().hasNode(relpath)){
			relpath=userid+"/trash/"+root.getName();
			relpath+="-copy";
			}
			System.out.println(root.isNodeType(JcrConstants.MIX_SHAREABLE));
			if(root.isNodeType(JcrConstants.MIX_SHAREABLE)){
				jcrsession.getWorkspace().copy(root.getPath(), "/"+relpath);
				root.removeSharedSet();
			}else{
				try{
			jcrsession.getWorkspace().move(root.getPath(), "/"+relpath);
				}catch(Exception e){

					jcrsession.getWorkspace().copy(root.getPath(), "/"+relpath);
					root.removeSharedSet();
				
					}
				}
		//root.removeShare();
	/*	if(flag){
		int no_of_folders=Integer.parseInt(parent.getProperty(Config.EDMS_NO_OF_FOLDERS).getString());
		if(no_of_folders>0){
		parent.setProperty(Config.EDMS_NO_OF_FOLDERS,no_of_folders-1);
		}}else{
			int no_of_Files = Integer.parseInt(parent.getProperty(
					Config.EDMS_NO_OF_DOCUMENTS).getString());
			if (no_of_Files > 0) {
				parent.setProperty(Config.EDMS_NO_OF_DOCUMENTS, no_of_Files - 1);
			}
			
		}*/
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
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		String response="";
		try {
			Node root = jcrsession.getRootNode();
			root=root.getNode(folderPath.substring(1));
			root.remove();
			jcrsession.save();
			response= "success";
		} catch (RepositoryException e) {
			response= "Exception Occured";
		}finally{
			//JcrRepositoryUtils.logout(sessionId);
		}
		return response;
	}

	public String restoreFolder(String folderPath, String userid) {
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		String response="";
		try {
			Node root = jcrsession.getRootNode();
			root=root.getNode(folderPath.substring(1));
			restoreFolderRecursion(root,userid);
			response= "success";
		}catch (RepositoryException e) {
			response= "Exception Occured";
			e.printStackTrace();
		}finally{
		}
		return response;
	}
	

	public void restoreFolderRecursion(Node root,String userid){
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		try{
			String parents=root.getProperty(Config.EDMS_RESTORATION_PATH).getString().substring(1);
			parents=parents.substring(0,parents.lastIndexOf("/"));
			//System.out.println("parent is "+parents);
/*		if(jcrRoot.hasNode(parents)){
			parent = jcrRoot.getNode(parents);
		}else{
			parent=createFolderRecursionWhenNotFound(parents,userid);
		}*/
		//root.setProperty(Config.EDMS_RECYCLE_DOC, false);
		//System.out.println(root.getPath()+" source to : "+root.getProperty(Config.EDMS_RESTORATION_PATH).getString());
		
		String relpath=root.getProperty(Config.EDMS_RESTORATION_PATH).getString();
		while(jcrsession.getRootNode().hasNode(relpath.substring(1))){
			relpath=root.getProperty(Config.EDMS_RESTORATION_PATH).getString();
			relpath+="-copy";
		}
		if(root.isNodeType(JcrConstants.MIX_SHAREABLE)){
			root.getSession().getWorkspace().copy(root.getPath(), relpath);		
			root.removeSharedSet();
			}else{
				try{
					jcrsession.getWorkspace().move(root.getPath(), relpath);
					}catch(Exception e){
							jcrsession.getWorkspace().copy(root.getPath(), relpath);
							root.removeSharedSet();
							}
			}
		jcrsession.save();
	/*	int no_of_folders=Integer.parseInt(parent.getProperty(Config.EDMS_NO_OF_FOLDERS).getString());
		parent.setProperty(Config.EDMS_NO_OF_FOLDERS,no_of_folders+1);*/
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
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
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
				//folder.addMixin(JcrConstants.MIX_SHAREABLE);
				folder.addMixin(JcrConstants.MIX_VERSIONABLE);
				jcrsession.save();
			}else{
				createFolderRecursionWhenNotFound(folderName.substring(0,folderName.lastIndexOf("/")+1),userid);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
		}
		return folder;
	}

	public RenameFolderRes renameFolder(String oldfolderPath, String newFolderPath,String userid) {
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		RenameFolderRes response=new RenameFolderRes();
		try {
			Node forVer=jcrsession.getRootNode().getNode(oldfolderPath.substring(1));
			Value[] actualUsers = forVer.getProperty(Config.USERS_WRITE).getValues();
			String newUser="";
			for (int i = 0; i < actualUsers.length; i++) {
				newUser+=actualUsers[i].getString()+",";
			}
		
			if(newUser.contains(userid)||forVer.getProperty(Config.EDMS_AUTHOR).getString().equals(userid)){
				Version version=jcrsession.getWorkspace().getVersionManager().checkin(forVer.getPath());
				jcrsession.getWorkspace().getVersionManager().getVersionHistory(forVer.getPath()).addVersionLabel(version.getName(), "renamed from "+oldfolderPath.substring(oldfolderPath.lastIndexOf("/")+1)+" to "+newFolderPath, true);
				jcrsession.getWorkspace().getVersionManager().checkout(forVer.getPath());
				jcrsession.move(oldfolderPath, oldfolderPath.substring(0,oldfolderPath.lastIndexOf("/")) + "/" + newFolderPath);
				SimpleDateFormat format = new SimpleDateFormat(
					"YYYY-MM-dd'T'HH:mm:ss.SSSZ");
				forVer.setProperty(Config.EDMS_MODIFICATIONDATE,(format.format(new Date())).toString().replace("+0530", "Z") );
				forVer.setProperty(Config.EDMS_NAME, newFolderPath);
				jcrsession.save();	
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
		}finally{
		}
		return response;
	}

	public String restoreVersion(String folderPath, String versionName,String userid) {
		
		String[] str=folderPath.split(",");
		for(int i=0;i<str.length;i++){
			System.out.println(".......................");
		}
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		String response="";
		try {
			jcrsession.save();
			VersionManager versionManager=jcrsession.getWorkspace().getVersionManager();
			Node forVer=jcrsession.getRootNode().getNode(folderPath.substring(1));
			//System.out.println(forVer.getProperty(Config.EDMS_AUTHOR).getString());
			if(forVer.getProperty(Config.EDMS_AUTHOR).getString().equals(userid)){
				if(versionName!="jcr:rootVersion"){
					Version version=versionManager.getVersionHistory(folderPath).getVersion(versionName);
					System.out.println("version is : "+version.getFrozenNode().getPath());
					versionManager.restore(new Version[]{version}, true);
					response="success";
				}else{
					versionManager.getVersionHistory(folderPath).getRootVersion();
					response="success";
				}
				
		}else
		{
			   response="Access Denied";
		}}
		catch (RepositoryException e) {
			   response="Access Denied";
			e.printStackTrace();
		}finally{
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

	public RecentlyModifiedResponse recentlyModified(String folderPath, String userid) {	
	SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
	Session jcrsession = sessions.getSession();
	//String sessionId=sessions.getId();
		RecentlyModifiedResponse res=new RecentlyModifiedResponse();
		ArrayOfFolders folders=new ArrayOfFolders();
		FilesAndFolders filesFolders=new FilesAndFolders();
		  javax.jcr.query.QueryManager queryManager;
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat(
					"YYYY-MM-dd'T'HH:mm:ss.SSSZ");
			String dateto=format.format(cal.getTime());
			cal.add(Calendar.DAY_OF_MONTH, -7);
			String datefrom=format.format(cal.getTime());
			dateto=dateto.replace("+0530", "Z");
			datefrom=datefrom.replace("+0530", "Z");
			
			try {
			queryManager = jcrsession.getWorkspace().getQueryManager();
				//Create a query object ...
		    String expression = "select * from [edms:folder] AS s WHERE ISDESCENDANTNODE(s,'"+folderPath+"')   and CONTAINS(s.[edms:owner],'*"+userid+"* OR *"+Config.EDMS_ADMINISTRATOR+"*') and "
		    		+ " s.[jcr:created] >= CAST('"+datefrom+"' AS DATE) AND s.[jcr:created] <= CAST('"+dateto+"' AS DATE)"
		    		+ "  ORDER BY s.["+Config.EDMS_Sorting_Parameter+"] ASC ";
		        //expression = "select * from [edms:folder] AS s WHERE NAME() = 'sanjay1' ";
		        //expression = "select * from [edms:document] WHERE NAME() like '%.png' ";
		        //expression="SELECT p.* FROM [nt:base] AS p WHERE p.[jcr:lastModified] >= CAST('2015-01-01T00:00:00.000Z' AS DATE) AND p.[jcr:lastModified] <= CAST('2015-12-31T23:59:59.999Z' AS DATE)";
				//expression = "select * from [edms:folder] where [jcr:path] like '%santosh%'";
		         
	        javax.jcr.query.Query query = queryManager.createQuery(expression, javax.jcr.query.Query.JCR_SQL2);
	        
	        //	query.setLimit(10);
	        // 	query.setOffset(0);
	        // 	Execute the query and get the results ...
	        javax.jcr.query.QueryResult result = query.execute();
	        //   System.out.println(result.getNodes());
	        for (NodeIterator nit = result.getNodes(); nit.hasNext();) {
	        	
				Node node = nit.nextNode();
				if(!node.getPath().contains("trash"))
				{Folder folder=new Folder();
				setGeneralFolderProperties(node, folder, userid);
				folders.getFolderList().add(folder);
	        }}
	        ArrayOfFiles files=new ArrayOfFiles();
	        expression = "select * from [edms:document] AS s WHERE ISDESCENDANTNODE(s,'"+folderPath+"')   and CONTAINS(s.[edms:owner],'*"+userid+"* OR *"+Config.EDMS_ADMINISTRATOR+"*')  ORDER BY s.["+Config.EDMS_Sorting_Parameter+"] ASC ";
	        query = queryManager.createQuery(expression, javax.jcr.query.Query.JCR_SQL2);
	        result = query.execute();
        for (NodeIterator nit = result.getNodes(); nit.hasNext();) {
			Node node = nit.nextNode();
			if(!node.getPath().contains("trash"))
			{File file=new File();
			file=new FileRepository().setPropertiesWithoutStream(node, file, userid, jcrsession);
			files.getFileList().add(file);
        }}
		filesFolders.setFoldersList(folders);
		filesFolders.setFilesList(files);
		res.setRecentlyModifiedFolders(filesFolders);
			}catch(Exception e){
		e.printStackTrace();
	}finally{
		//JcrRepositoryUtils.logout(sessionId);
	}
			return res;		
	}

	public boolean addKeyword(String folderPath, String userid, String keyword) {
		
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		try {
			Node root = jcrsession.getRootNode();
			root=root.getNode(folderPath.substring(1));
			Value[] actualUsers = root.getProperty(Config.EDMS_KEYWORDS).getValues();
			String newUser="";
			for (int i = 0; i < actualUsers.length; i++) {
				String[] kWords=actualUsers[i].getString().split(",");
				for (int j = 0; j < kWords.length; j++) {
					if(kWords[j].length()>0){
						//if(!kWords[j].equalsIgnoreCase(keyword))
						newUser+=kWords[j]+",";
					}
				}
			}
			//	System.out.println(newUser.contains(user));
			//	if(!newUser.contains(keyword))
			//	{
			newUser+=keyword+",";
			root.setProperty(Config.EDMS_KEYWORDS, new String[]{newUser});
			jcrsession.save();
			return true;
			//	}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	//	return true;
	}
	public boolean removeKeyword(String folderPath, String userid, String keyword) {
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		
		try {
			Node root = jcrsession.getRootNode();
			root=root.getNode(folderPath.substring(1));
			//System.out.println();
		Value[] actualUsers = root.getProperty(Config.EDMS_KEYWORDS).getValues();
		String newUser="";
		for (int i = 0; i < actualUsers.length; i++) {
			String[] kWords=actualUsers[i].getString().split(",");
			for (int j = 0; j < kWords.length; j++) {
				if(kWords[j].length()>0){
					if(!kWords[j].equalsIgnoreCase(keyword))
					newUser+=kWords[j]+",";
				}
			}
		}
			//System.out.println(newUser.contains(user));
			//if(newUser.contains(keyword))
			//{
			//newUser=newUser.replace(keyword, "");
			root.setProperty(Config.EDMS_KEYWORDS, new String[]{newUser});
			jcrsession.save();
			return true;
			//}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
			//return true;
	}




	public boolean editKeyword(String folderPath, String userid,
			String editedKeyword, String keyword) {
		
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		try {
			Node root = jcrsession.getRootNode();
			root=root.getNode(folderPath.substring(1));
		System.out.println();
		Value[] actualUsers = root.getProperty(Config.EDMS_KEYWORDS).getValues();
		String newUser="";
		for (int i = 0; i < actualUsers.length; i++) {
			String[] kWords=actualUsers[i].getString().split(",");
			for (int j = 0; j < kWords.length; j++) {
				if(kWords[j].length()>0){
					if(!kWords[j].equalsIgnoreCase(keyword))
						{
							newUser+=kWords[j]+",";
						}
					else
						{
							newUser+=editedKeyword+",";
						}
				}
			}
		}
			//System.out.println(newUser.contains(user));
			//if(newUser.contains(keyword))
			//{
			//newUser=newUser.replace(keyword, "");
			root.setProperty(Config.EDMS_KEYWORDS, new String[]{newUser});
			jcrsession.save();
			return true;
			//}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		//return true;
	}
	
	
	public MoveDocResponse moveDoc(String srcDocPath, String destDocPath,
			String userid) {
		MoveDocResponse response=new MoveDocResponse();
		response.setSuccess(false);
		if(srcDocPath!=""){
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		try{
			Node root=jcrsession.getRootNode().getNode(destDocPath.substring(1));
			Node source=jcrsession.getRootNode().getNode(srcDocPath.substring(1).substring(0,srcDocPath.substring(1).lastIndexOf("/")));
			String srcPathDoc=srcDocPath.substring(srcDocPath.lastIndexOf("/")+1);
			if(srcPathDoc.contains(".")){
				String fileName=srcDocPath.substring(srcDocPath.lastIndexOf("/")+1);
				String fileNames=fileName;
				String ext=fileName.substring(fileName.lastIndexOf('.'));
				fileNames=fileName.substring(0,fileName.lastIndexOf('.'));
				while(root.hasNode(fileNames+ext)){
					fileNames+="-copy";
				}
				srcPathDoc=fileNames+ext;
			}else{
				String folderName=srcDocPath.substring(srcDocPath.lastIndexOf("/")+1);
				String folderNames=folderName;
				while(root.hasNode(folderNames)){
					folderNames+="-copy";
				}
				srcPathDoc=folderNames;
			}
			Workspace ws=jcrsession.getWorkspace();
			/*
			if(srcPathDoc.contains(".")){
				root.setProperty(Config.EDMS_NO_OF_DOCUMENTS, Integer.parseInt(root.getProperty(Config.EDMS_NO_OF_DOCUMENTS).getString())+1);
				if(Integer.parseInt(source.getProperty(Config.EDMS_NO_OF_DOCUMENTS).getString())>0){
					source.setProperty(Config.EDMS_NO_OF_DOCUMENTS, Integer.parseInt(source.getProperty(Config.EDMS_NO_OF_DOCUMENTS).getString())-1);
				}else{
					source.setProperty(Config.EDMS_NO_OF_DOCUMENTS, "0");
				}
			}else{
				root.setProperty(Config.EDMS_NO_OF_FOLDERS, Integer.parseInt(root.getProperty(Config.EDMS_NO_OF_FOLDERS).getString())+1);
				source.setProperty(Config.EDMS_NO_OF_FOLDERS, Integer.parseInt(source.getProperty(Config.EDMS_NO_OF_FOLDERS).getString())-1);
			}
			*/
			jcrsession.save();
			//	ws.move(srcAbsPath, destAbsPath);
			source=jcrsession.getRootNode().getNode(srcDocPath.substring(1));
			if(source.isNodeType(JcrConstants.MIX_SHAREABLE)){
				ws.copy(srcDocPath, destDocPath+"/"+srcPathDoc);
				source.removeSharedSet();
			}else{
					try{
					ws.move(srcDocPath, destDocPath+"/"+srcPathDoc);
					}catch(Exception e){
						ws.copy(srcDocPath, destDocPath+"/"+srcPathDoc);
						source.removeSharedSet();
					}
			}
			jcrsession.save();
		}
		catch(Exception e){
			e.printStackTrace();
			return response;
		}}
		return response;
	}
	public CopyDocResponse copyDoc(String srcDocPath, String destDocPath,
			String userid) {
		
		CopyDocResponse response=new CopyDocResponse();
		response.setSuccess(false);
		if(srcDocPath!=""){
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		try{
			Node root=jcrsession.getRootNode().getNode(destDocPath.substring(1));
			Workspace ws=jcrsession.getWorkspace();
			String srcPathDoc=srcDocPath.substring(srcDocPath.lastIndexOf("/")+1);
			if(srcPathDoc.contains(".")){
				String fileName=srcDocPath.substring(srcDocPath.lastIndexOf("/")+1);
				String fileNames=fileName;
				String ext=fileName.substring(fileName.lastIndexOf('.'));
				fileNames=fileName.substring(0,fileName.lastIndexOf('.'));
				while(root.hasNode(fileNames+ext)){
					fileNames+="-copy";
				}
				srcPathDoc=fileNames+ext;
			}else{
			String folderName=srcDocPath.substring(srcDocPath.lastIndexOf("/")+1);
			String folderNames=folderName;
			while(root.hasNode(folderNames)){
				folderNames+="-copy";
			}
			srcPathDoc=folderNames;
			}
			ws.copy(srcDocPath, destDocPath+"/"+srcPathDoc);
			jcrsession.save();
			/*if(srcPathDoc.contains(".")){
				root.setProperty(Config.EDMS_NO_OF_DOCUMENTS, Integer.parseInt(root.getProperty(Config.EDMS_NO_OF_DOCUMENTS).getString())+1);
				}else{
			root.setProperty(Config.EDMS_NO_OF_FOLDERS, Integer.parseInt(root.getProperty(Config.EDMS_NO_OF_FOLDERS).getString())+1);
			}*/
			jcrsession.save();
		}
		catch(Exception e){
			e.printStackTrace();
			return response;
		}}
		return response;
	}
	public boolean addNote(String folderPath, String userid, String note) {
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		try {
			Node root = jcrsession.getRootNode();
			root=root.getNode(folderPath.substring(1));
			root.setProperty(Config.EDMS_DESCRIPTION, note);
			jcrsession.save();
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}