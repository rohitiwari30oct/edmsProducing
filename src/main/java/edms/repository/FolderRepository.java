package edms.repository;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.Workspace;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;
import javax.jcr.version.VersionManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.hibernate.Query;
import org.hibernate.SessionFactory;

import com.edms.documentmodule.ArrayOfFiles;
import com.edms.documentmodule.CopyDocResponse;
import com.edms.documentmodule.File;
import com.edms.documentmodule.ArrayOfFolders;
import com.edms.documentmodule.FilesAndFolders;
import com.edms.documentmodule.Folder;
import com.edms.documentmodule.FolderListReturn;
import com.edms.documentmodule.FolderVersionDetail;
import com.edms.documentmodule.GetRecycledDocsResponse;
import com.edms.documentmodule.MoveDocResponse;
import com.edms.documentmodule.RecentlyModifiedResponse;
import com.edms.documentmodule.RenameFolderRes;
import com.edms.documentmodule.SetSortOrderResponse;
import com.edms.entity.Quota;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import edms.core.Config;
import edms.core.DateChecker;
import edms.core.JcrRepositoryUtils;
import edms.core.LDAPConnection;
import edms.core.SessionWrapper;

@Component
public class FolderRepository {
	@Autowired
	private WebServiceConfig webServiceConfig;
	
	// private static Session jcrsession =jcrsession.jcrsession;
	/*
	 * @Autowired FolderService folderService;
	 */

	// @Autowired DefaultSpringSecurityContextSource contextSource;

	public SetSortOrderResponse setSortOrder(String sortOrder, String userid, String password) {
		SetSortOrderResponse response = new SetSortOrderResponse();
		Config.EDMS_SORT_ORDER = sortOrder;
		response.setSuccess(true);
		return response;
	}

	public FolderListReturn listFolder(String name, String userid, String password) {
		System.out.println("DateTime: " + new Date() + " ::::: " + "getting folders and files from = " + name
				+ " by user : " + userid);
		boolean flag = false;
		if (name.indexOf("/") == name.lastIndexOf("/")) {
			flag = true;
		}

		SessionWrapper sessions = JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		name = name.substring(0, name.indexOf("@") + 1).toLowerCase() + name.substring(name.indexOf("@") + 1);
		Assert.notNull(name);
		FolderListReturn folderList1 = new FolderListReturn();
		ArrayOfFolders folders = new ArrayOfFolders();
		try {
			javax.jcr.query.QueryManager queryManager;
			name = name.replace("'", "&apos;");
			name = name.replace("<", "&lt;");
			name = name.replace(">", "&gt;");
			queryManager = jcrsession.getWorkspace().getQueryManager();
			//			String expression = "select * from [edms:folder] AS s WHERE ISCHILDNODE(s,'" + name
			//					+ "')  and (CONTAINS(s.[edms:owner],'*" + userid + "*') or CONTAINS(s.[edms:author],'*" + userid + "*'))   ORDER BY s.[" + Config.EDMS_Sorting_Parameter
			//					+ "] ASC";
			String expression = "select * from [edms:folder] AS s WHERE ISCHILDNODE(s,'" + name
					+ "')    ORDER BY s.[" + Config.EDMS_Sorting_Parameter
					+ "] ASC";
			javax.jcr.query.Query query = queryManager.createQuery(expression, javax.jcr.query.Query.JCR_SQL2);
			javax.jcr.query.QueryResult result = query.execute();
			for (NodeIterator nit = result.getNodes(); nit.hasNext();) {
				Node node = nit.nextNode();
				Node root=node.getParent();
				
				if (root.hasProperty(Config.EDMS_OWNER)&&(!node.hasProperty(Config.EDMS_OWNER))) {
						node.setProperty(Config.EDMS_OWNER,
							root.getProperty(Config.EDMS_OWNER).getString());
				} else {
//						node.setProperty(Config.EDMS_OWNER,
//								"admin" + ",," + userid);
				}
						jcrsession.save();
				if(node.hasProperty(Config.EDMS_OWNER)&&node.hasProperty(Config.EDMS_AUTHOR)){
				if(node.getProperty(Config.EDMS_OWNER).getString().indexOf(userid)>=0||node.getProperty(Config.EDMS_AUTHOR).getString().indexOf(userid)>=0)
				{Folder folder = new Folder();
				folder = setProperties(node, folder, userid, password, jcrsession, name);
				folders.getFolderList().add(folder);
			}}else if(node.getPath().indexOf(userid)>=0){
				
				Folder folder = new Folder();
				folder = setProperties(node, folder, userid, password, jcrsession, name);
				folders.getFolderList().add(folder);
			}
			}
			folderList1.setFolderListResult(folders);
			folderList1.setSuccess(true);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// JcrRepositoryUtils.logout(sessionId);
		}
		return folderList1;
	}

	public FolderListReturn listFolderDAV(String name, String userid, String password) {
		System.out.println("DateTime: " + new Date() + " ::::: " + "getting folders and files from = " + name
				+ " by user : " + userid);
		boolean flag = false;
		if (name.indexOf("/") == name.lastIndexOf("/")) {
			flag = true;
		}

		SessionWrapper sessions = JcrRepositoryUtils.loginDAV(userid, password);
		Session jcrsession = sessions.getSession();
		name = name.substring(0, name.indexOf("@") + 1).toLowerCase() + name.substring(name.indexOf("@") + 1);
		Assert.notNull(name);
		FolderListReturn folderList1 = new FolderListReturn();
		ArrayOfFolders folders = new ArrayOfFolders();
		try {
			javax.jcr.query.QueryManager queryManager;
			name = name.replace("'", "&apos;");
			name = name.replace("<", "&lt;");
			name = name.replace(">", "&gt;");
			queryManager = jcrsession.getWorkspace().getQueryManager();
			String expression = "select * from [edms:folder] AS s WHERE ISCHILDNODE(s,'" + name
					+ "')and  (CONTAINS(s.[edms:owner],'*" + userid + "*') or CONTAINS(s.[edms:author],'*" + userid + "*'))   ORDER BY s.[" + Config.EDMS_Sorting_Parameter
					+ "] ASC";
			javax.jcr.query.Query query = queryManager.createQuery(expression, javax.jcr.query.Query.JCR_SQL2);
			javax.jcr.query.QueryResult result = query.execute();
			for (NodeIterator nit = result.getNodes(); nit.hasNext();) {
				Node node = nit.nextNode();
				Folder folder = new Folder();
				folder = setProperties(node, folder, userid, password, jcrsession, name);
				folders.getFolderList().add(folder);
			}
			folderList1.setFolderListResult(folders);
			folderList1.setSuccess(true);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// JcrRepositoryUtils.logout(sessionId);
		}
		return folderList1;
	}

	public String countFolder(String name, String userid, String password) {
		System.out.println(
				"DateTime: " + new Date() + " ::::: " + "Count Child folder from = " + name + " by user : " + userid);

		// DocumentConverter docConverter
		String results = "0";
		SessionWrapper sessions = JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		// String sessionId=sessions.getId();
		try {
			// System.out.println("DateTime: "+new Date()+" ::::: "+"userid in
			// list Folder is "+userid);
			javax.jcr.query.QueryManager queryManager;
			name = name.replace("'", "&apos;");
			name = name.replace("<", "&lt;");
			name = name.replace(">", "&gt;");
			name = name;
			queryManager = jcrsession.getWorkspace().getQueryManager();
			String expression = "select * from [edms:folder] AS s WHERE ISCHILDNODE(s,'" + name
					+ "') and  (CONTAINS(s.[edms:owner],'*" + userid + "*') or CONTAINS(s.[edms:author],'*" + userid + "*'))  ORDER BY s.[" + Config.EDMS_Sorting_Parameter
					+ "] ASC";
			// expression = "select * from [edms:folder] AS s WHERE NAME like
			// ['%san%']";
			javax.jcr.query.Query query = queryManager.createQuery(expression, javax.jcr.query.Query.JCR_SQL2);
			javax.jcr.query.QueryResult result = query.execute();
			// System.out.println("DateTime: "+new Date()+" ::::: "+expression);
			// System.out.println("DateTime: "+new Date()+" ::::: "+"size of
			// list in list folder "+result);
			results = Long.toString(result.getNodes().getSize());
			// for (NodeIterator nit = result.getNodes(); nit.hasNext();) {}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// JcrRepositoryUtils.logout(sessionId);
		}
		return results;
	}

	public String countFiles(String name, String userid, String password) {

		System.out.println("DateTime: " + new Date() + " ::::: " + "Count child Documents from = " + name
				+ " by user : " + userid);
		// DocumentConverter docConverter
		String results = "0";
		SessionWrapper sessions = JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		if (DateChecker.checkDateD()) {
			// String sessionId=sessions.getId();
			try {
				name = name.replace("'", "&apos;");
				name = name.replace("<", "&lt;");
				name = name.replace(">", "&gt;");
				// System.out.println("DateTime: "+new Date()+" ::::: "+"userid
				// in list Folder is "+userid);
				javax.jcr.query.QueryManager queryManager;
				queryManager = jcrsession.getWorkspace().getQueryManager();
				String expression = "select * from [edms:document] AS s WHERE ISCHILDNODE(s,'" + name
						+ "') and  (CONTAINS(s.[edms:owner],'*" + userid + "*') or CONTAINS(s.[edms:author],'*" + userid + "*'))  ORDER BY s.["
						+ Config.EDMS_Sorting_Parameter + "] ASC";
				// expression = "select * from [edms:folder] AS s WHERE NAME
				// like ['%san%']";
				javax.jcr.query.Query query = queryManager.createQuery(expression, javax.jcr.query.Query.JCR_SQL2);
				javax.jcr.query.QueryResult result = query.execute();
				// System.out.println("DateTime: "+new Date()+" :::::
				// "+expression);
				// System.out.println("DateTime: "+new Date()+" ::::: "+"size of
				// list in list folder "+result);
				results = Long.toString(result.getNodes().getSize());
				// for (NodeIterator nit = result.getNodes(); nit.hasNext();) {}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// JcrRepositoryUtils.logout(sessionId);
			}
		} else {
			DateChecker.listingFolder("", userid, password);
		}
		return results;
	}

	public Folder setProperties(Node node, Folder folder, String userid, String password, Session jcrsession,
			String par) {

		try {

			System.out.println("DateTime: " + new Date() + " ::::: " + "Setting property of = " + node.getPath()
					+ " by user : " + userid);
			folder.setFolderName(node.getName());
					//node.remove();
			if (node.hasProperty(Config.EDMS_OWNER)){
					String actualUsers1 = node.getProperty(Config.EDMS_OWNER).getString();
					if(actualUsers1.indexOf("admin,,")<0&&actualUsers1.indexOf("admin,")>=0){
						String newUser = actualUsers1.replace("admin,", "admin,,");
						node.setProperty(Config.EDMS_OWNER,  newUser );
						jcrsession.save();
							
					}
			}else{
				
			}
			// if(node.hasProperty(Config.EDMS_NAME)){
			// System.out.println("DateTime: "+new Date()+" ::::: "+"title of
			// doc is : "+node.getProperty(Config.EDMS_NAME).getString());
			// }
		
			if (node.hasProperty(Config.EDMS_AUTHOR))
				folder.setCreatedBy(node.getProperty(Config.EDMS_AUTHOR).getString());
			if (node.hasProperty(Config.EDMS_RECYCLE_DOC))
				folder.setRecycle(node.getProperty(Config.EDMS_RECYCLE_DOC).getBoolean());
			if (node.hasProperty(Config.EDMS_KEYWORDS)) {
				Value[] actualUsers = node.getProperty(Config.EDMS_KEYWORDS).getValues();
				for (int i = 0; i < actualUsers.length; i++) {
					folder.getKeywords().add(actualUsers[i].getString());
				}
			}
			if (node.hasProperty(Config.EDMS_CREATIONDATE))
				folder.setCreationDate(node.getProperty(Config.EDMS_CREATIONDATE).getString());

			if (node.hasProperty(Config.EDMS_MODIFICATIONDATE))
				folder.setModificationDate(node.getProperty(Config.EDMS_MODIFICATIONDATE).getString());

			// if(node.hasProperty(Config.EDMS_NO_OF_DOCUMENTS)){
			folder.setNoOfDocuments(countFiles(node.getPath(), userid, password));

			// }
			// if(node.hasProperty(Config.EDMS_NO_OF_FOLDERS)){
			folder.setNoOfFolders(countFolder(node.getPath(), userid, password));
			// }
			if (node.hasProperty(Config.EDMS_DESCRIPTION))
				folder.setNotes(node.getProperty(Config.EDMS_DESCRIPTION).getString());

			/* start mapping permissions to edms folder */
			
			if (node.hasProperty(Config.USERS_READ)) {
				Value[] actualUsers = node.getProperty(Config.USERS_READ).getValues();

				for (int i = 0; i < actualUsers.length; i++) {
					folder.getUserRead().add(actualUsers[i].getString());
				}
			}
			if (node.hasProperty(Config.USERS_WRITE)) {
				Value[] actualUsers = node.getProperty(Config.USERS_WRITE).getValues();
				for (int i = 0; i < actualUsers.length; i++) {
					folder.getUserWrite().add(actualUsers[i].getString());
				}
			}
			if (node.hasProperty(Config.USERS_DELETE)) {
				Value[] actualUsers = node.getProperty(Config.USERS_DELETE).getValues();
				for (int i = 0; i < actualUsers.length; i++) {
					folder.getUserDelete().add(actualUsers[i].getString());
				}
			}
			if (node.hasProperty(Config.USERS_SECURITY)) {
				Value[] actualUsers = node.getProperty(Config.USERS_SECURITY).getValues();
				for (int i = 0; i < actualUsers.length; i++) {
					folder.getUserSecurity().add(actualUsers[i].getString());
				}
			}
			if (node.hasProperty(Config.GROUPS_READ)) {
				Value[] actualUsers = node.getProperty(Config.GROUPS_READ).getValues();
				for (int i = 0; i < actualUsers.length; i++) {
					folder.getGroupRead().add(actualUsers[i].getString());
				}
			}
			if (node.hasProperty(Config.GROUPS_WRITE)) {
				Value[] actualUsers = node.getProperty(Config.GROUPS_WRITE).getValues();
				for (int i = 0; i < actualUsers.length; i++) {
					folder.getGroupWrite().add(actualUsers[i].getString());
				}
			}
			if (node.hasProperty(Config.GROUPS_DELETE)) {
				Value[] actualUsers = node.getProperty(Config.GROUPS_DELETE).getValues();
				for (int i = 0; i < actualUsers.length; i++) {
					folder.getGroupDelete().add(actualUsers[i].getString());
				}
			}
			if (node.hasProperty(Config.GROUPS_SECURITY)) {
				Value[] actualUsers = node.getProperty(Config.GROUPS_SECURITY).getValues();
				for (int i = 0; i < actualUsers.length; i++) {
					folder.getGroupSecurity().add(actualUsers[i].getString());
				}
			}

			folder.setParentFolder(node.getParent().getName());
			// folder.setFolderPath(par+"/"+node.getName());
			String pth=node.getPath();
			if(pth.indexOf(userid)<0){
			for (NodeIterator nit = node.getSharedSet(); nit.hasNext();) {
				Node root = nit.nextNode();
				if(root.getPath().indexOf(userid)>=0){
					pth=root.getPath();
				}
			}
			}
			folder.setFolderPath(pth);
			
		/*	if(node.getPath().indexOf("sadgfsdfg")>0&&userid.equals("jyoti@silvereye.in")){
				node.setProperty(Config.EDMS_OWNER, "admin,jyoti@silvereye.in");
				node.setProperty(Config.EDMS_AUTHOR, "jyoti@silvereye.in");
				jcrsession.save();
			}*/
			// folder.setFolderPath(node.getPath());
			VersionHistory history = jcrsession.getWorkspace().getVersionManager().getVersionHistory(node.getPath());
			// To iterate over all versions
			/*
			 * VersionIterator versions = history.getAllVersions(); while
			 * (versions.hasNext()) { Version version = versions.nextVersion();
			 * FolderVersionDetail versionDetail=new FolderVersionDetail();
			 * versionDetail.setCreatedBy(userid);
			 * versionDetail.setCreationDate(version.getCreated().getTime().
			 * toString()); String[] details=history.getVersionLabels(version);
			 * if(details.length>0){ versionDetail.setDetails(details[0]); }
			 * versionDetail.setVersionName(version.getName());
			 * versionDetail.setVersionLabel(version.getParent().getName());
			 * folder.getFolderVersionsHistory().add(versionDetail); }
			 */
			

		//	node.setProperty(Config.EDMS_OWNER,"admin,,demo1@avi-oil.com,,demo2@avi-oil.com");
		//	jcrsession.save();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return folder;
	}

	public Boolean hasChild(String folderPath, String userid, String password) {

		System.out.println("DateTime: " + new Date() + " ::::: " + "check child existance of = " + folderPath
				+ " by user : " + userid);
		folderPath = folderPath.replace("'", "&apos;");
		folderPath = folderPath.replace("<", "&lt;");
		folderPath = folderPath.replace(">", "&gt;");
		SessionWrapper sessions = JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		javax.jcr.query.QueryManager queryManager;
		boolean flag = false;
		try {
			queryManager = jcrsession.getWorkspace().getQueryManager();
			String expression = "select * from [edms:folder] AS s WHERE ISDESCENDANTNODE(s,'" + folderPath + "')";
			javax.jcr.query.Query query = queryManager.createQuery(expression, javax.jcr.query.Query.JCR_SQL2);
			javax.jcr.query.QueryResult result = query.execute();
			for (NodeIterator nit = result.getNodes(); nit.hasNext();) {
				flag = true;
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// JcrRepositoryUtils.logout(sessionId);
		}
		return flag;
	}

	public synchronized Folder createFolder(String folderName, String parentFolder, String userid, String password,
			String keywords, String description) {

		System.out.println("DateTime: " + new Date() + " ::::: " + "DateTime: " + new Date() + " ::::: "
				+ "Creating folder " + folderName + " in = " + parentFolder + " by user : " + userid);

		/*
		 * folderName=folderName.replace("'","");
		 * folderName=folderName.replace("<","");
		 * folderName=folderName.replace(">","");
		 * folderName=folderName.replace("&", "And");
		 */
		if (folderName.indexOf('.') >= 0 || folderName.indexOf('.') >= 0 || folderName.indexOf('@') >= 0
				|| folderName.indexOf(',') >= 0 || folderName.indexOf('*') >= 0 || folderName.indexOf('>') >= 0
				|| folderName.indexOf('<') >= 0 || folderName.indexOf(')') >= 0 || folderName.indexOf('(') >= 0) {
			return null;
		} else {
			SessionWrapper sessions = JcrRepositoryUtils.login(userid, password);
			Session jcrsession = sessions.getSession();

			// String sessionId=sessions.getId();
			Node folder = null;
			Folder folder1 = new Folder();
			try {
				/*
				 * jcrsession = repository.login(new
				 * SimpleCredentials(Config.EDMS_ADMIN,
				 * Config.EDMS_ADMIN.toCharArray()));
				 */
				/*
				 * jcrsession = repository.login(new SimpleCredentials(
				 * userid,password.toCharArray()));
				 */
				jcrsession.save();
				Node root = jcrsession.getRootNode();

				/*
				 * if(userid!=Config.EDMS_ADMIN){ root=root.getNode(userid); }
				 */
				if (parentFolder.length() > 1) {
					// System.out.println("DateTime: "+new Date()+" :::::
					// "+"parent folder when creating Folder : " +parentFolder);
					root = root.getNode(parentFolder.substring(1));
				}

				String folderNames = folderName;
				if (!root.hasNode(folderNames)) {
					while (root.hasNode(folderNames)) {
						folderNames += "-copy";
					}
					folderName = folderNames;
					if (root.hasProperty(Config.USERS_SECURITY)) {
						Value[] actualUsers = root.getProperty(Config.USERS_SECURITY).getValues();

						String newUsers = "";

						for (int i = 0; i < actualUsers.length; i++) {
							newUsers += actualUsers[i].getString()+",,";
						}
						if (newUsers.contains(userid) || root.getProperty(Config.EDMS_AUTHOR).getString().equals(userid)
								|| root.getProperty(Config.EDMS_OWNER).getString().contains(userid)
								|| (root.getName().equals(userid) && (root.getProperty(Config.EDMS_AUTHOR).getString())
										.equals(Config.JCR_USERNAME))) {
							jcrsession.save();

							/*
							 * Version version=jcrsession.getWorkspace().
							 * getVersionManager().checkin(root.getPath());
							 * System.out.println("DateTime: "+new Date()+
							 * " ::::: "+"Version of :"+root.getPath()+
							 * " has been created, Version name is : "
							 * +version.getName());
							 * jcrsession.getWorkspace().getVersionManager().
							 * checkout(root.getPath());
							 */
							folder = root.addNode(folderName, Config.EDMS_FOLDER);

							// root.setProperty(Config.EDMS_RESTORATION_PATH,
							// "lllllllllllllllllllll");
							// jcrsession.save();

							if (root.hasProperty(Config.USERS_READ)
									&& (!root.getProperty(Config.EDMS_AUTHOR).toString().equals(Config.EDMS_ADMIN))) {
								/*
								 * Value[] actualUser =
								 * root.getProperty(Config.USERS_READ).getValues
								 * (); String newUser=""; for (int i = 0; i <
								 * actualUser.length; i++) {
								 * newUser+=actualUser[i].getString()+","; }
								 */
								folder.setProperty(Config.USERS_READ, root.getProperty(Config.USERS_READ).getValues());
							} else {
								folder.setProperty(Config.USERS_READ, new String[] {});
							}
							if (root.hasProperty(Config.USERS_WRITE)
									&& !root.getProperty(Config.EDMS_AUTHOR).toString().equals(Config.EDMS_ADMIN)) {

								folder.setProperty(Config.USERS_WRITE,
										root.getProperty(Config.USERS_WRITE).getValues());
							} else {
								folder.setProperty(Config.USERS_WRITE, new String[] {});
							}
							if (root.hasProperty(Config.USERS_DELETE)
									&& !root.getProperty(Config.EDMS_AUTHOR).toString().equals(Config.EDMS_ADMIN)) {

								folder.setProperty(Config.USERS_DELETE,
										root.getProperty(Config.USERS_DELETE).getValues());
							} else {
								folder.setProperty(Config.USERS_DELETE, new String[] {});
							}
							if (root.hasProperty(Config.USERS_SECURITY)
									&& !root.getProperty(Config.EDMS_AUTHOR).toString().equals(Config.EDMS_ADMIN)) {

								folder.setProperty(Config.USERS_SECURITY,
										root.getProperty(Config.USERS_SECURITY).getValues());
							} else {
								folder.setProperty(Config.USERS_SECURITY, new String[] {});
							}

							/*
							 * if(root.hasProperty(Config.GROUPS_READ)&&!root.
							 * getProperty(Config.EDMS_AUTHOR).toString().equals
							 * (Config.EDMS_ADMIN)){ Value[] actualUser =
							 * root.getProperty(Config.GROUPS_READ).getValues();
							 * String newUser=""; for (int i = 0; i <
							 * actualUser.length; i++) {
							 * newUser+=actualUser[i].getString()+","; }
							 * folder.setProperty(Config.GROUPS_READ,
							 * newUser.split(",")); }else{
							 * folder.setProperty(Config.GROUPS_READ, new
							 * String[]{}); }
							 * 
							 * if(root.hasProperty(Config.GROUPS_WRITE)&&!root.
							 * getProperty(Config.EDMS_AUTHOR).toString().equals
							 * (Config.EDMS_ADMIN)){ Value[] actualUser =
							 * root.getProperty(Config.GROUPS_WRITE).getValues()
							 * ; String newUser=""; for (int i = 0; i <
							 * actualUser.length; i++) {
							 * newUser+=actualUser[i].getString()+","; }
							 * folder.setProperty(Config.GROUPS_WRITE,
							 * newUser.split(",")); }else{
							 * folder.setProperty(Config.GROUPS_WRITE, new
							 * String[]{}); }
							 * 
							 * if(root.hasProperty(Config.GROUPS_DELETE)&&!root.
							 * getProperty(Config.EDMS_AUTHOR).toString().equals
							 * (Config.EDMS_ADMIN)){ Value[] actualUser =
							 * root.getProperty(Config.GROUPS_DELETE).getValues(
							 * ); String newUser=""; for (int i = 0; i <
							 * actualUser.length; i++) {
							 * newUser+=actualUser[i].getString()+","; }
							 * folder.setProperty(Config.GROUPS_DELETE,
							 * newUser.split(",")); }else{
							 * folder.setProperty(Config.GROUPS_DELETE, new
							 * String[]{}); }
							 * 
							 * if(root.hasProperty(Config.GROUPS_SECURITY)&&!
							 * root.getProperty(Config.EDMS_AUTHOR).toString().
							 * equals(Config.EDMS_ADMIN)){ Value[] actualUser =
							 * root.getProperty(Config.GROUPS_SECURITY).
							 * getValues(); String newUser=""; for (int i = 0; i
							 * < actualUser.length; i++) {
							 * newUser+=actualUser[i].getString()+","; }
							 * folder.setProperty(Config.GROUPS_SECURITY,
							 * newUser.split(",")); }else{
							 * folder.setProperty(Config.GROUPS_SECURITY, new
							 * String[]{}); }
							 */
							/*
							 * folder.setProperty(Config.USERS_READ,new
							 * String[]{userid});
							 * folder.setProperty(Config.USERS_WRITE,new
							 * String[]{userid});
							 * folder.setProperty(Config.USERS_DELETE,new
							 * String[]{userid});
							 * folder.setProperty(Config.USERS_SECURITY,new
							 * String[]{userid});
							 */
							folder.setProperty(Config.EDMS_KEYWORDS, keywords.split(","));

							if (root.hasProperty(Config.EDMS_OWNER)) {
								// System.out.println("DateTime: "+new Date()+"
								// :::::
								// "+root.getProperty(Config.EDMS_OWNER).getString()+","+userid);
								if(root.getProperty(Config.EDMS_OWNER).getString().indexOf(userid)<0)
								folder.setProperty(Config.EDMS_OWNER,
										root.getProperty(Config.EDMS_OWNER).getString() + ",," + userid);
							} else {
								folder.setProperty(Config.EDMS_OWNER,
										"admin" + ",," + userid);
							}
							folder.setProperty(Config.EDMS_AUTHOR, userid);

							folder.setProperty(Config.EDMS_DESCRIPTION, description);
							SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ss.SSSZ");
							folder.setProperty(Config.EDMS_CREATIONDATE,
									(format.format(new Date())).toString().replace("+0530", "Z"));
							folder.setProperty(Config.EDMS_MODIFICATIONDATE, "");
							folder.setProperty(Config.EDMS_ACCESSDATE, "");
							folder.setProperty(Config.EDMS_DOWNLOADDATE, "");
							folder.setProperty(Config.EDMS_RECYCLE_DOC, false);
							folder.setProperty(Config.EDMS_NO_OF_FOLDERS, 0);
							folder.setProperty(Config.EDMS_NO_OF_DOCUMENTS, 0);
							// folder.addMixin(JcrConstants.MIX_SHAREABLE);
							folder.addMixin(JcrConstants.MIX_VERSIONABLE);
							// folder.setProperty(Config.EDMS_TITLE,folderName);
							folder.setProperty(Config.EDMS_NAME, folderName);
							jcrsession.save();
							root.setProperty(Config.EDMS_NO_OF_FOLDERS,
									Integer.parseInt(root.getProperty(Config.EDMS_NO_OF_FOLDERS).getString()) + 1);
							jcrsession.save();
							folder.setProperty(Config.EDMS_RESTORATION_PATH, folder.getPath());
							// System.out.println("DateTime: "+new Date()+"
							// ::::: "+"Owner of newly created folder is :
							// "+folder.getProperty(Config.EDMS_OWNER).getString());
							folder1 = setGeneralFolderProperties(folder, folder1, userid, password);
							// System.out.println("DateTime: "+new Date()+"
							// ::::: "+"folder path is : "+folder.getPath());
							jcrsession.save();
							Version version = jcrsession.getWorkspace().getVersionManager().checkin(folder.getPath());
							System.out.println("DateTime: " + new Date() + " ::::: " + "Version of :" + folder.getPath()
									+ " has been created, Version name is : " + version.getName());
							jcrsession.getWorkspace().getVersionManager().checkout(folder.getPath());

							// VersionManager
							// vman=jcrsession.getWorkspace().getVersionManager();
							// vman.checkout(folder.getPath());

							// vman.checkin(folder.getPath());

						} else {
							System.out.println("You have not permission to add child node");
						}

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// JcrRepositoryUtils.logout(sessionId);
			}
			return folder1;
		}
	}

	public synchronized Folder createFolderDAV(String folderName, String parentFolder, String userid, String password,
			String keywords, String description) {

		System.out.println("DateTime: " + new Date() + " ::::: " + "DateTime: " + new Date() + " ::::: "
				+ "Creating folder " + folderName + " in = " + parentFolder + " by user : " + userid);

		/*
		 * folderName=folderName.replace("'","");
		 * folderName=folderName.replace("<","");
		 * folderName=folderName.replace(">","");
		 * folderName=folderName.replace("&", "And");
		 */
		if (folderName.indexOf('.') >= 0 || folderName.indexOf('.') >= 0 || folderName.indexOf('@') >= 0
				|| folderName.indexOf(',') >= 0 || folderName.indexOf('*') >= 0 || folderName.indexOf('>') >= 0
				|| folderName.indexOf('<') >= 0 || folderName.indexOf(')') >= 0 || folderName.indexOf('(') >= 0) {
			return null;
		} else {
			SessionWrapper sessions = JcrRepositoryUtils.loginDAV(userid, password);
			Session jcrsession = sessions.getSession();

			// String sessionId=sessions.getId();
			Node folder = null;
			Folder folder1 = new Folder();
			try {
				/*
				 * jcrsession = repository.login(new
				 * SimpleCredentials(Config.EDMS_ADMIN,
				 * Config.EDMS_ADMIN.toCharArray()));
				 */
				/*
				 * jcrsession = repository.login(new SimpleCredentials(
				 * userid,password.toCharArray()));
				 */
				jcrsession.save();
				Node root = jcrsession.getRootNode();

				/*
				 * if(userid!=Config.EDMS_ADMIN){ root=root.getNode(userid); }
				 */
				if (parentFolder.length() > 1) {
					// System.out.println("DateTime: "+new Date()+" :::::
					// "+"parent folder when creating Folder : " +parentFolder);
					root = root.getNode(parentFolder.substring(1));
				}

				String folderNames = folderName;
				if (!root.hasNode(folderNames)) {
					while (root.hasNode(folderNames)) {
						folderNames += "-copy";
					}
					folderName = folderNames;
					if (root.hasProperty(Config.USERS_SECURITY)) {
						Value[] actualUsers = root.getProperty(Config.USERS_SECURITY).getValues();

						String newUsers = "";

						for (int i = 0; i < actualUsers.length; i++) {
							newUsers += actualUsers[i].getString()+",,";
						}
						if (newUsers.contains(userid) || root.getProperty(Config.EDMS_AUTHOR).getString().equals(userid)
								|| root.getProperty(Config.EDMS_OWNER).getString().contains(userid)
								|| (root.getName().equals(userid) && (root.getProperty(Config.EDMS_AUTHOR).getString())
										.equals(Config.JCR_USERNAME))) {
							jcrsession.save();

							/*
							 * Version version=jcrsession.getWorkspace().
							 * getVersionManager().checkin(root.getPath());
							 * System.out.println("DateTime: "+new Date()+
							 * " ::::: "+"Version of :"+root.getPath()+
							 * " has been created, Version name is : "
							 * +version.getName());
							 * jcrsession.getWorkspace().getVersionManager().
							 * checkout(root.getPath());
							 */
							folder = root.addNode(folderName, Config.EDMS_FOLDER);

							// root.setProperty(Config.EDMS_RESTORATION_PATH,
							// "lllllllllllllllllllll");
							// jcrsession.save();

							if (root.hasProperty(Config.USERS_READ)
									&& (!root.getProperty(Config.EDMS_AUTHOR).toString().equals(Config.EDMS_ADMIN))) {
								/*
								 * Value[] actualUser =
								 * root.getProperty(Config.USERS_READ).getValues
								 * (); String newUser=""; for (int i = 0; i <
								 * actualUser.length; i++) {
								 * newUser+=actualUser[i].getString()+","; }
								 */
								folder.setProperty(Config.USERS_READ, root.getProperty(Config.USERS_READ).getValues());
							} else {
								folder.setProperty(Config.USERS_READ, new String[] {});
							}
							if (root.hasProperty(Config.USERS_WRITE)
									&& !root.getProperty(Config.EDMS_AUTHOR).toString().equals(Config.EDMS_ADMIN)) {

								folder.setProperty(Config.USERS_WRITE,
										root.getProperty(Config.USERS_WRITE).getValues());
							} else {
								folder.setProperty(Config.USERS_WRITE, new String[] {});
							}
							if (root.hasProperty(Config.USERS_DELETE)
									&& !root.getProperty(Config.EDMS_AUTHOR).toString().equals(Config.EDMS_ADMIN)) {

								folder.setProperty(Config.USERS_DELETE,
										root.getProperty(Config.USERS_DELETE).getValues());
							} else {
								folder.setProperty(Config.USERS_DELETE, new String[] {});
							}
							if (root.hasProperty(Config.USERS_SECURITY)
									&& !root.getProperty(Config.EDMS_AUTHOR).toString().equals(Config.EDMS_ADMIN)) {

								folder.setProperty(Config.USERS_SECURITY,
										root.getProperty(Config.USERS_SECURITY).getValues());
							} else {
								folder.setProperty(Config.USERS_SECURITY, new String[] {});
							}

							/*
							 * if(root.hasProperty(Config.GROUPS_READ)&&!root.
							 * getProperty(Config.EDMS_AUTHOR).toString().equals
							 * (Config.EDMS_ADMIN)){ Value[] actualUser =
							 * root.getProperty(Config.GROUPS_READ).getValues();
							 * String newUser=""; for (int i = 0; i <
							 * actualUser.length; i++) {
							 * newUser+=actualUser[i].getString()+","; }
							 * folder.setProperty(Config.GROUPS_READ,
							 * newUser.split(",")); }else{
							 * folder.setProperty(Config.GROUPS_READ, new
							 * String[]{}); }
							 * 
							 * if(root.hasProperty(Config.GROUPS_WRITE)&&!root.
							 * getProperty(Config.EDMS_AUTHOR).toString().equals
							 * (Config.EDMS_ADMIN)){ Value[] actualUser =
							 * root.getProperty(Config.GROUPS_WRITE).getValues()
							 * ; String newUser=""; for (int i = 0; i <
							 * actualUser.length; i++) {
							 * newUser+=actualUser[i].getString()+","; }
							 * folder.setProperty(Config.GROUPS_WRITE,
							 * newUser.split(",")); }else{
							 * folder.setProperty(Config.GROUPS_WRITE, new
							 * String[]{}); }
							 * 
							 * if(root.hasProperty(Config.GROUPS_DELETE)&&!root.
							 * getProperty(Config.EDMS_AUTHOR).toString().equals
							 * (Config.EDMS_ADMIN)){ Value[] actualUser =
							 * root.getProperty(Config.GROUPS_DELETE).getValues(
							 * ); String newUser=""; for (int i = 0; i <
							 * actualUser.length; i++) {
							 * newUser+=actualUser[i].getString()+","; }
							 * folder.setProperty(Config.GROUPS_DELETE,
							 * newUser.split(",")); }else{
							 * folder.setProperty(Config.GROUPS_DELETE, new
							 * String[]{}); }
							 * 
							 * if(root.hasProperty(Config.GROUPS_SECURITY)&&!
							 * root.getProperty(Config.EDMS_AUTHOR).toString().
							 * equals(Config.EDMS_ADMIN)){ Value[] actualUser =
							 * root.getProperty(Config.GROUPS_SECURITY).
							 * getValues(); String newUser=""; for (int i = 0; i
							 * < actualUser.length; i++) {
							 * newUser+=actualUser[i].getString()+","; }
							 * folder.setProperty(Config.GROUPS_SECURITY,
							 * newUser.split(",")); }else{
							 * folder.setProperty(Config.GROUPS_SECURITY, new
							 * String[]{}); }
							 */
							/*
							 * folder.setProperty(Config.USERS_READ,new
							 * String[]{userid});
							 * folder.setProperty(Config.USERS_WRITE,new
							 * String[]{userid});
							 * folder.setProperty(Config.USERS_DELETE,new
							 * String[]{userid});
							 * folder.setProperty(Config.USERS_SECURITY,new
							 * String[]{userid});
							 */
							folder.setProperty(Config.EDMS_KEYWORDS, keywords.split(","));

							if (root.hasProperty(Config.EDMS_OWNER)) {
								// System.out.println("DateTime: "+new Date()+"
								// :::::
								// "+root.getProperty(Config.EDMS_OWNER).getString()+","+userid);
								if(root.getProperty(Config.EDMS_OWNER).getString().indexOf(userid)<0)
								folder.setProperty(Config.EDMS_OWNER,
										root.getProperty(Config.EDMS_OWNER).getString()+",," + userid);
							} else {
								folder.setProperty(Config.EDMS_OWNER,
										root.getProperty(Config.EDMS_AUTHOR).getString()+",," + userid);
							}
							folder.setProperty(Config.EDMS_AUTHOR, userid);

							folder.setProperty(Config.EDMS_DESCRIPTION, description);
							SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ss.SSSZ");
							folder.setProperty(Config.EDMS_CREATIONDATE,
									(format.format(new Date())).toString().replace("+0530", "Z"));
							folder.setProperty(Config.EDMS_MODIFICATIONDATE, "");
							folder.setProperty(Config.EDMS_ACCESSDATE, "");
							folder.setProperty(Config.EDMS_DOWNLOADDATE, "");
							folder.setProperty(Config.EDMS_RECYCLE_DOC, false);
							folder.setProperty(Config.EDMS_NO_OF_FOLDERS, 0);
							folder.setProperty(Config.EDMS_NO_OF_DOCUMENTS, 0);
							// folder.addMixin(JcrConstants.MIX_SHAREABLE);
							folder.addMixin(JcrConstants.MIX_VERSIONABLE);
							// folder.setProperty(Config.EDMS_TITLE,folderName);
							folder.setProperty(Config.EDMS_NAME, folderName);
							jcrsession.save();
							root.setProperty(Config.EDMS_NO_OF_FOLDERS,
									Integer.parseInt(root.getProperty(Config.EDMS_NO_OF_FOLDERS).getString()) + 1);
							jcrsession.save();
							folder.setProperty(Config.EDMS_RESTORATION_PATH, folder.getPath());
							// System.out.println("DateTime: "+new Date()+"
							// ::::: "+"Owner of newly created folder is :
							// "+folder.getProperty(Config.EDMS_OWNER).getString());
							folder1 = setGeneralFolderProperties(folder, folder1, userid, password);
							// System.out.println("DateTime: "+new Date()+"
							// ::::: "+"folder path is : "+folder.getPath());
							jcrsession.save();
							Version version = jcrsession.getWorkspace().getVersionManager().checkin(folder.getPath());
							System.out.println("DateTime: " + new Date() + " ::::: " + "Version of :" + folder.getPath()
									+ " has been created, Version name is : " + version.getName());
							jcrsession.getWorkspace().getVersionManager().checkout(folder.getPath());

							// VersionManager
							// vman=jcrsession.getWorkspace().getVersionManager();
							// vman.checkout(folder.getPath());

							// vman.checkin(folder.getPath());

						} else {
							System.out.println("You have not permission to add child node");
						}

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// JcrRepositoryUtils.logout(sessionId);
			}
			return folder1;
		}
	}

	public Folder getFolderByPath(String folderPath, String userid, String password) {

		System.out.println("DateTime: " + new Date() + " ::::: " + "Getting folder by Path = " + folderPath
				+ " by user : " + userid);
		SessionWrapper sessions = JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		Folder folder1 = new Folder();
		File file1 = new File();
		try {
			Node root = jcrsession.getRootNode();
			if (folderPath.length() > 1) {
				root = root.getNode(folderPath.substring(1));
			}
			folder1.setFolderName(root.getName().toString());
			folder1.setFolderPath(root.getPath().toString());
			file1.setFileName(root.getName().toString());
			file1.setFilePath(root.getPath().toString());
			if (root.getPath().toString().length() > 1) {
				if (folderPath.length() > 1) {
					setProperties(root, folder1, userid, password, jcrsession, root.getParent().getPath());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return folder1;
	}

	public Folder getFolderByPathDAV(String folderPath, String userid, String password) {

		System.out.println("DateTime: " + new Date() + " ::::: " + "Getting folder by Path = " + folderPath
				+ " by user : " + userid);
		SessionWrapper sessions = JcrRepositoryUtils.loginDAV(userid, password);
		Session jcrsession = sessions.getSession();
		Folder folder1 = new Folder();
		File file1 = new File();
		try {
			Node root = jcrsession.getRootNode();
			if (folderPath.length() > 1) {
				root = root.getNode(folderPath.substring(1));
			}
			folder1.setFolderName(root.getName().toString());
			folder1.setFolderPath(root.getPath().toString());
			file1.setFileName(root.getName().toString());
			file1.setFilePath(root.getPath().toString());
			if (root.getPath().toString().length() > 1) {
				if (folderPath.length() > 1) {
					setProperties(root, folder1, userid, password, jcrsession, root.getParent().getPath());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return folder1;
	}

	public void assignSinglePermissionRecursion(Node rt, String userid, String password, String user, String value,
			boolean cnt, String parVal) {
		SessionWrapper sessions = JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		// String sessionId=sessions.getId();
		try {
			System.out.println("DateTime: " + new Date() + " ::::: " + "Assigning permission " + value + " to user "
					+ user + " on = " + rt.getPath() + " by user : " + userid);
			jcrsession.getWorkspace().getVersionManager().checkout(rt.getPath());
			rt.addMixin(JcrConstants.MIX_SHAREABLE);

			if (cnt) {
				String val = "";
				Value[] actualUsers1 = rt.getProperty(Config.USERS_READ).getValues();
				String newUserRead = "";
				for (int i = 0; i < actualUsers1.length; i++) {
					newUserRead += actualUsers1[i].getString() + ",,";
				}
				if (newUserRead.contains(user)) {
					val = "ur";
				}
				actualUsers1 = rt.getProperty(Config.USERS_WRITE).getValues();
				String newUserWrite = "";
				for (int i = 0; i < actualUsers1.length; i++) {
					newUserWrite += actualUsers1[i].getString() + ",,";
				}
				if (newUserWrite.contains(user)) {
					val = "uw";
				}
				actualUsers1 = rt.getProperty(Config.USERS_SECURITY).getValues();
				String newUserMan = "";
				for (int i = 0; i < actualUsers1.length; i++) {
					newUserMan += actualUsers1[i].getString() + ",,";
				}
				if (newUserMan.contains(user)) {
					val = "us";
				}
				if (val.equals(parVal)/* ||(val.equals("") */) {

					if (value.equals("ur")) {
						Value[] actualUsers = rt.getProperty(Config.USERS_READ).getValues();
						String newUser = "";
						for (int i = 0; i < actualUsers.length; i++) {
							newUser += actualUsers[i].getString()+",,";
						}
						// System.out.println("DateTime: "+new Date()+" :::::
						// "+newUser.contains(user));
						if (!newUser.contains(user)) {
							newUser += user+",,";
							rt.setProperty(Config.USERS_READ, new String[] { newUser });
						}
						actualUsers = rt.getProperty(Config.USERS_SECURITY).getValues();
						newUser = "";
						for (int i = 0; i < actualUsers.length; i++) {
							newUser += actualUsers[i].getString()+",,";
						}
						// System.out.println("DateTime: "+new Date()+" :::::
						// "+newUser.contains(user));
						if (newUser.contains(user)) {
							newUser = newUser.replace(user+",,", "");
							rt.setProperty(Config.USERS_SECURITY, new String[] { newUser });
						}
						actualUsers = rt.getProperty(Config.USERS_WRITE).getValues();
						newUser = "";
						for (int i = 0; i < actualUsers.length; i++) {
							newUser += actualUsers[i].getString()+",,";
						}
						// System.out.println("DateTime: "+new Date()+" :::::
						// "+newUser.contains(user));
						if (newUser.contains(user)) {
							newUser = newUser.replace(user+",,", "");
							rt.setProperty(Config.USERS_WRITE, new String[] { newUser });
						}
						jcrsession.save();
					} else if (value.equals("uw")) {
						Value[] actualUsers = rt.getProperty(Config.USERS_READ).getValues();
						String newUser = "";
						for (int i = 0; i < actualUsers.length; i++) {
							newUser += actualUsers[i].getString()+",,";
						}
						if (!newUser.contains(user)) {
							newUser += user+",,";
							rt.setProperty(Config.USERS_READ, new String[] { newUser });
						}

						actualUsers = rt.getProperty(Config.USERS_WRITE).getValues();
						newUser = "";
						for (int i = 0; i < actualUsers.length; i++) {
							newUser += actualUsers[i].getString()+",,";
						}
						if (!newUser.contains(user)) {
							newUser += user+",,";
							rt.setProperty(Config.USERS_WRITE, new String[] { newUser });
						}
						actualUsers = rt.getProperty(Config.USERS_SECURITY).getValues();
						newUser = "";
						for (int i = 0; i < actualUsers.length; i++) {
							newUser += actualUsers[i].getString()+",,";
						}
						// System.out.println("DateTime: "+new Date()+" :::::
						// "+newUser.contains(user));
						if (newUser.contains(user)) {
							newUser = newUser.replace(user+",,", "");
							rt.setProperty(Config.USERS_SECURITY, new String[] { newUser });
						}
						jcrsession.save();
					} else if (value.equals("us")) {
						Value[] actualUsers = rt.getProperty(Config.USERS_READ).getValues();
						String newUser = "";
						for (int i = 0; i < actualUsers.length; i++) {
							newUser += actualUsers[i].getString()+",,";
						}
						// System.out.println("DateTime: "+new Date()+" :::::
						// "+newUser.contains(user));
						if (!newUser.contains(user)) {
							newUser += user+",,";
							rt.setProperty(Config.USERS_READ, new String[] { newUser });
						}

						actualUsers = rt.getProperty(Config.USERS_WRITE).getValues();
						newUser = "";
						for (int i = 0; i < actualUsers.length; i++) {
							newUser += actualUsers[i].getString()+",,";
						}
						// System.out.println("DateTime: "+new Date()+" :::::
						// "+newUser.contains(user));
						if (!newUser.contains(user)) {
							newUser += user+",,";
							rt.setProperty(Config.USERS_WRITE, new String[] { newUser });
						}
						actualUsers = rt.getProperty(Config.USERS_SECURITY).getValues();
						newUser = "";
						for (int i = 0; i < actualUsers.length; i++) {
							newUser += actualUsers[i].getString()+",,";
						}
						// System.out.println("DateTime: "+new Date()+" :::::
						// "+newUser.contains(user));
						if (!newUser.contains(user)) {
							newUser += user+",,";
							rt.setProperty(Config.USERS_SECURITY, new String[] { newUser });
						}
						jcrsession.save();
					}

				} else {
				}
			} else {

				if (value.equals("ur")) {
					Value[] actualUsers = rt.getProperty(Config.USERS_READ).getValues();
					String newUser = "";
					for (int i = 0; i < actualUsers.length; i++) {
						newUser += actualUsers[i].getString()+",,";
					}
					// System.out.println("DateTime: "+new Date()+" :::::
					// "+newUser.contains(user));
					if (!newUser.contains(user)) {
						newUser += user+",,";
						rt.setProperty(Config.USERS_READ, new String[] { newUser });
					}
					actualUsers = rt.getProperty(Config.USERS_SECURITY).getValues();
					newUser = "";
					for (int i = 0; i < actualUsers.length; i++) {
						newUser += actualUsers[i].getString()+",,";
					}
					// System.out.println("DateTime: "+new Date()+" :::::
					// "+newUser.contains(user));
					if (newUser.contains(user)) {
						newUser = newUser.replace(user+",,", "");
						rt.setProperty(Config.USERS_SECURITY, new String[] { newUser });
					}
					actualUsers = rt.getProperty(Config.USERS_WRITE).getValues();
					newUser = "";
					for (int i = 0; i < actualUsers.length; i++) {
						newUser += actualUsers[i].getString()+",,";
					}
					// System.out.println("DateTime: "+new Date()+" :::::
					// "+newUser.contains(user));
					if (newUser.contains(user)) {
						newUser = newUser.replace(user+",,", "");
						rt.setProperty(Config.USERS_WRITE, new String[] { newUser });
					}
					jcrsession.save();
				} else if (value.equals("uw")) {
					Value[] actualUsers = rt.getProperty(Config.USERS_READ).getValues();
					String newUser = "";
					for (int i = 0; i < actualUsers.length; i++) {
						newUser += actualUsers[i].getString()+",,";
					}
					// System.out.println("DateTime: "+new Date()+" :::::
					// "+newUser.contains(user));
					if (!newUser.contains(user)) {
						newUser += user+",,";
						rt.setProperty(Config.USERS_READ, new String[] { newUser });
					}

					actualUsers = rt.getProperty(Config.USERS_WRITE).getValues();
					newUser = "";
					for (int i = 0; i < actualUsers.length; i++) {
						newUser += actualUsers[i].getString()+",,";
					}
					// System.out.println("DateTime: "+new Date()+" :::::
					// "+newUser.contains(user));
					if (!newUser.contains(user)) {
						newUser += user+",,";
						rt.setProperty(Config.USERS_WRITE, new String[] { newUser });
					}
					actualUsers = rt.getProperty(Config.USERS_SECURITY).getValues();
					newUser = "";
					for (int i = 0; i < actualUsers.length; i++) {
						newUser += actualUsers[i].getString()+",,";
					}
					// System.out.println("DateTime: "+new Date()+" :::::
					// "+newUser.contains(user));
					if (newUser.contains(user)) {
						newUser = newUser.replace(user+",,", "");
						rt.setProperty(Config.USERS_SECURITY, new String[] { newUser });
					}
					jcrsession.save();
				} else if (value.equals("us")) {
					Value[] actualUsers = rt.getProperty(Config.USERS_READ).getValues();
					String newUser = "";
					for (int i = 0; i < actualUsers.length; i++) {
						newUser += actualUsers[i].getString() + ",,";
					}
					// System.out.println("DateTime: "+new Date()+" :::::
					// "+newUser.contains(user));
					if (!newUser.contains(user)) {
						newUser += user+",,";
						rt.setProperty(Config.USERS_READ, new String[] { newUser });
					}

					actualUsers = rt.getProperty(Config.USERS_WRITE).getValues();
					newUser = "";
					for (int i = 0; i < actualUsers.length; i++) {
						newUser += actualUsers[i].getString()+",,";
					}
					// System.out.println("DateTime: "+new Date()+" :::::
					// "+newUser.contains(user));
					if (!newUser.contains(user)) {
						newUser += user+",,";
						rt.setProperty(Config.USERS_WRITE, new String[] { newUser });
					}
					actualUsers = rt.getProperty(Config.USERS_SECURITY).getValues();
					newUser = "";
					for (int i = 0; i < actualUsers.length; i++) {
						newUser += actualUsers[i].getString()+",,";
					}
					// System.out.println("DateTime: "+new Date()+" :::::
					// "+newUser.contains(user));
					if (!newUser.contains(user)) {
						newUser += user+",,";
						rt.setProperty(Config.USERS_SECURITY, new String[] { newUser });
					}
					jcrsession.save();
				}
			}
			if (rt.hasNodes()) {
				for (NodeIterator nit = rt.getNodes(); nit.hasNext();) {
					Node root = nit.nextNode();
					if (Config.EDMS_FOLDER.equals(root.getPrimaryNodeType().getName())
							|| Config.EDMS_DOCUMENT.equals(root.getPrimaryNodeType().getName()))
						assignSinglePermissionRecursion(root, userid, password, user, value, true, parVal);
				}
			}
			// cnt=true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void assignSinglePermissionRecursionDAV(Node rt, String userid, String password, String user, String value,
			boolean cnt, String parVal) {
		SessionWrapper sessions = JcrRepositoryUtils.loginDAV(userid, password);
		Session jcrsession = sessions.getSession();
		// String sessionId=sessions.getId();
		try {
			System.out.println("DateTime: " + new Date() + " ::::: " + "Assigning permission " + value + " to user "
					+ user + " on = " + rt.getPath() + " by user : " + userid);
			jcrsession.getWorkspace().getVersionManager().checkout(rt.getPath());
			rt.addMixin(JcrConstants.MIX_SHAREABLE);

			if (cnt) {
				String val = "";
				Value[] actualUsers1 = rt.getProperty(Config.USERS_READ).getValues();
				String newUserRead = "";
				for (int i = 0; i < actualUsers1.length; i++) {
					newUserRead += actualUsers1[i].getString() + ",,";
				}
				if (newUserRead.contains(user)) {
					val = "ur";
				}
				actualUsers1 = rt.getProperty(Config.USERS_WRITE).getValues();
				String newUserWrite = "";
				for (int i = 0; i < actualUsers1.length; i++) {
					newUserWrite += actualUsers1[i].getString() + ",,";
				}
				if (newUserWrite.contains(user)) {
					val = "uw";
				}
				actualUsers1 = rt.getProperty(Config.USERS_SECURITY).getValues();
				String newUserMan = "";
				for (int i = 0; i < actualUsers1.length; i++) {
					newUserMan += actualUsers1[i].getString() + ",,";
				}
				if (newUserMan.contains(user)) {
					val = "us";
				}
				if (val.equals(parVal)/* ||(val.equals("") */) {

					if (value.equals("ur")) {
						Value[] actualUsers = rt.getProperty(Config.USERS_READ).getValues();
						String newUser = "";
						for (int i = 0; i < actualUsers.length; i++) {
							newUser += actualUsers[i].getString()+",,";
						}
						// System.out.println("DateTime: "+new Date()+" :::::
						// "+newUser.contains(user));
						if (!newUser.contains(user)) {
							newUser += user+",,";
							rt.setProperty(Config.USERS_READ, new String[] { newUser });
						}
						actualUsers = rt.getProperty(Config.USERS_SECURITY).getValues();
						newUser = "";
						for (int i = 0; i < actualUsers.length; i++) {
							newUser += actualUsers[i].getString()+",,";
						}
						// System.out.println("DateTime: "+new Date()+" :::::
						// "+newUser.contains(user));
						if (newUser.contains(user)) {
							newUser = newUser.replace(user+",,", "");
							rt.setProperty(Config.USERS_SECURITY, new String[] { newUser });
						}
						actualUsers = rt.getProperty(Config.USERS_WRITE).getValues();
						newUser = "";
						for (int i = 0; i < actualUsers.length; i++) {
							newUser += actualUsers[i].getString()+",,";
						}
						// System.out.println("DateTime: "+new Date()+" :::::
						// "+newUser.contains(user));
						if (newUser.contains(user)) {
							newUser = newUser.replace(user+",,", "");
							rt.setProperty(Config.USERS_WRITE, new String[] { newUser });
						}
						jcrsession.save();
					} else if (value.equals("uw")) {
						Value[] actualUsers = rt.getProperty(Config.USERS_READ).getValues();
						String newUser = "";
						for (int i = 0; i < actualUsers.length; i++) {
							newUser += actualUsers[i].getString()+",,";
						}
						// System.out.println("DateTime: "+new Date()+" :::::
						// "+newUser.contains(user));
						if (!newUser.contains(user)) {
							newUser += user+",,";
							rt.setProperty(Config.USERS_READ, new String[] { newUser });
						}

						actualUsers = rt.getProperty(Config.USERS_WRITE).getValues();
						newUser = "";
						for (int i = 0; i < actualUsers.length; i++) {
							newUser += actualUsers[i].getString()+",,";
						}
						// System.out.println("DateTime: "+new Date()+" :::::
						// "+newUser.contains(user));
						if (!newUser.contains(user)) {
							newUser += user+",,";
							rt.setProperty(Config.USERS_WRITE, new String[] { newUser });
						}
						actualUsers = rt.getProperty(Config.USERS_SECURITY).getValues();
						newUser = "";
						for (int i = 0; i < actualUsers.length; i++) {
							newUser += actualUsers[i].getString()+",,";
						}
						// System.out.println("DateTime: "+new Date()+" :::::
						// "+newUser.contains(user));
						if (newUser.contains(user)) {
							newUser = newUser.replace(user+",,", "");
							rt.setProperty(Config.USERS_SECURITY, new String[] { newUser });
						}
						jcrsession.save();
					} else if (value.equals("us")) {
						Value[] actualUsers = rt.getProperty(Config.USERS_READ).getValues();
						String newUser = "";
						for (int i = 0; i < actualUsers.length; i++) {
							newUser += actualUsers[i].getString()+",,";
						}
						// System.out.println("DateTime: "+new Date()+" :::::
						// "+newUser.contains(user));
						if (!newUser.contains(user)) {
							newUser += user+",,";
							rt.setProperty(Config.USERS_READ, new String[] { newUser });
						}

						actualUsers = rt.getProperty(Config.USERS_WRITE).getValues();
						newUser = "";
						for (int i = 0; i < actualUsers.length; i++) {
							newUser += actualUsers[i].getString()+",,";
						}
						// System.out.println("DateTime: "+new Date()+" :::::
						// "+newUser.contains(user));
						if (!newUser.contains(user)) {
							newUser += user+",,";
							rt.setProperty(Config.USERS_WRITE, new String[] { newUser });
						}
						actualUsers = rt.getProperty(Config.USERS_SECURITY).getValues();
						newUser = "";
						for (int i = 0; i < actualUsers.length; i++) {
							newUser += actualUsers[i].getString()+",,";
						}
						// System.out.println("DateTime: "+new Date()+" :::::
						// "+newUser.contains(user));
						if (!newUser.contains(user)) {
							newUser += user+",,";
							rt.setProperty(Config.USERS_SECURITY, new String[] { newUser });
						}
						jcrsession.save();
					}

				} else {
				}
			} else {

				if (value.equals("ur")) {
					Value[] actualUsers = rt.getProperty(Config.USERS_READ).getValues();
					String newUser = "";
					for (int i = 0; i < actualUsers.length; i++) {
						newUser += actualUsers[i].getString()+",,";
					}
					// System.out.println("DateTime: "+new Date()+" :::::
					// "+newUser.contains(user));
					if (!newUser.contains(user)) {
						newUser += user+",,";
						rt.setProperty(Config.USERS_READ, new String[] { newUser });
					}
					actualUsers = rt.getProperty(Config.USERS_SECURITY).getValues();
					newUser = "";
					for (int i = 0; i < actualUsers.length; i++) {
						newUser += actualUsers[i].getString()+",,";
					}
					// System.out.println("DateTime: "+new Date()+" :::::
					// "+newUser.contains(user));
					if (newUser.contains(user)) {
						newUser = newUser.replace(user+",,", "");
						rt.setProperty(Config.USERS_SECURITY, new String[] { newUser });
					}
					actualUsers = rt.getProperty(Config.USERS_WRITE).getValues();
					newUser = "";
					for (int i = 0; i < actualUsers.length; i++) {
						newUser += actualUsers[i].getString()+",,";
					}
					// System.out.println("DateTime: "+new Date()+" :::::
					// "+newUser.contains(user));
					if (newUser.contains(user)) {
						newUser = newUser.replace(user+",,", "");
						rt.setProperty(Config.USERS_WRITE, new String[] { newUser });
					}
					jcrsession.save();
				} else if (value.equals("uw")) {
					Value[] actualUsers = rt.getProperty(Config.USERS_READ).getValues();
					String newUser = "";
					for (int i = 0; i < actualUsers.length; i++) {
						newUser += actualUsers[i].getString()+",,";
					}
					// System.out.println("DateTime: "+new Date()+" :::::
					// "+newUser.contains(user));
					if (!newUser.contains(user)) {
						newUser += user+",,";
						rt.setProperty(Config.USERS_READ, new String[] { newUser });
					}

					actualUsers = rt.getProperty(Config.USERS_WRITE).getValues();
					newUser = "";
					for (int i = 0; i < actualUsers.length; i++) {
						newUser += actualUsers[i].getString()+",,";
					}
					// System.out.println("DateTime: "+new Date()+" :::::
					// "+newUser.contains(user));
					if (!newUser.contains(user)) {
						newUser += user+",,";
						rt.setProperty(Config.USERS_WRITE, new String[] { newUser });
					}
					actualUsers = rt.getProperty(Config.USERS_SECURITY).getValues();
					newUser = "";
					for (int i = 0; i < actualUsers.length; i++) {
						newUser += actualUsers[i].getString()+",,";
					}
					// System.out.println("DateTime: "+new Date()+" :::::
					// "+newUser.contains(user));
					if (newUser.contains(user)) {
						newUser = newUser.replace(user+",,", "");
						rt.setProperty(Config.USERS_SECURITY, new String[] { newUser });
					}
					jcrsession.save();
				} else if (value.equals("us")) {
					Value[] actualUsers = rt.getProperty(Config.USERS_READ).getValues();
					String newUser = "";
					for (int i = 0; i < actualUsers.length; i++) {
						newUser += actualUsers[i].getString()+",,";
					}
					// System.out.println("DateTime: "+new Date()+" :::::
					// "+newUser.contains(user));
					if (!newUser.contains(user)) {
						newUser += user+",,";
						rt.setProperty(Config.USERS_READ, new String[] { newUser });
					}

					actualUsers = rt.getProperty(Config.USERS_WRITE).getValues();
					newUser = "";
					for (int i = 0; i < actualUsers.length; i++) {
						newUser += actualUsers[i].getString()+",,";
					}
					// System.out.println("DateTime: "+new Date()+" :::::
					// "+newUser.contains(user));
					if (!newUser.contains(user)) {
						newUser += user+",,";
						rt.setProperty(Config.USERS_WRITE, new String[] { newUser });
					}
					actualUsers = rt.getProperty(Config.USERS_SECURITY).getValues();
					newUser = "";
					for (int i = 0; i < actualUsers.length; i++) {
						newUser += actualUsers[i].getString()+",,";
					}
					// System.out.println("DateTime: "+new Date()+" :::::
					// "+newUser.contains(user));
					if (!newUser.contains(user)) {
						newUser += user+",,";
						rt.setProperty(Config.USERS_SECURITY, new String[] { newUser });
					}
					jcrsession.save();
				}
			}
			if (rt.hasNodes()) {
				for (NodeIterator nit = rt.getNodes(); nit.hasNext();) {
					Node root = nit.nextNode();
					if (Config.EDMS_FOLDER.equals(root.getPrimaryNodeType().getName())
							|| Config.EDMS_DOCUMENT.equals(root.getPrimaryNodeType().getName()))
						assignSinglePermissionRecursion(root, userid, password, user, value, true, parVal);
				}
			}
			// cnt=true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized String assignSinglePermission(String folderPath, String userid, String password, String user,
			String value) {
		System.out.println("DateTime: " + new Date() + " ::::: " + "Assigning permission " + value + " to user " + user
				+ " on = " + folderPath + " by user : " + userid);
		if (folderPath.indexOf(user) < 0) {

			SessionWrapper sessions = JcrRepositoryUtils.login(userid, password);
			Session jcrsession = sessions.getSession();
			String[] users = user.split(",");
			String[] values = value.split(",");
			for (int j = 0; j < users.length; j++) {
				
				user = users[j];
				value = values[j];
				try {
					Node root = jcrsession.getRootNode();
					Node rty = jcrsession.getRootNode();
				
					if (folderPath.length() > 1) {
						root = root.getNode(folderPath.substring(1));
					}	if (!rty.hasNode(user + "/" + root.getName())) {
					Value[] actualUsers = root.getProperty(Config.USERS_SECURITY).getValues();
					String newUser = "";
					for (int i = 0; i < actualUsers.length; i++) {
						newUser += actualUsers[i].getString() + ",,";
					}
					if (newUser.contains(userid) || root.getProperty(Config.EDMS_AUTHOR).getString().equals(userid)
							|| root.getProperty(Config.EDMS_OWNER).getString().contains(userid)) {

						String val = "";
						Value[] actualUsers1 = root.getProperty(Config.USERS_READ).getValues();
						String newUserRead = "";
						for (int i = 0; i < actualUsers1.length; i++) {
							newUserRead += actualUsers1[i].getString() + ",,";
						}
						if (newUserRead.contains(user)) {
							val = "ur";
						}
						actualUsers1 = root.getProperty(Config.USERS_WRITE).getValues();
						String newUserWrite = "";
						for (int i = 0; i < actualUsers1.length; i++) {
							newUserWrite += actualUsers1[i].getString() + ",,";
						}
						if (newUserWrite.contains(user)) {
							val = "uw";
						}
						actualUsers1 = root.getProperty(Config.USERS_SECURITY).getValues();
						String newUserMan = "";
						for (int i = 0; i < actualUsers1.length; i++) {
							newUserMan += actualUsers1[i].getString() + ",,";
						}
						if (newUserMan.contains(user)) {
							val = "us";
						}

						assignSinglePermissionRecursion(root, userid, password, user, value, false, val);
						if (!value.contains("n")) {
							Workspace ws = jcrsession.getWorkspace();
							
								ws.clone(ws.getName(), root.getPath(), "/" + user + "/" + root.getName(), false);
								jcrsession.save();
							
						} else {
							/*
							 * if(value.contains("r")){ Node
							 * remov=jcrsession.getRootNode().getNode(user+"/"+
							 * root.getName()); remov.remove(); }
							 */
						}
						// jcrsession.save();
					} else {
						return "sorry you don't have permissions to share this folder";
					}} else {
						System.out.println("DateTime: " + new Date() + " ::::: " + "already exist");
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
				}
				
			
				
				
			}
			return "Success";
		} else {
			System.out.println("DateTime: " + new Date() + " ::::: " + "Access Denied");
			return "Access Denied";
		}
	}

	public synchronized String assignSinglePermissionDAV(String folderPath, String userid, String password, String user,
			String value) {
		System.out.println("DateTime: " + new Date() + " ::::: " + "Assigning permission " + value + " to user " + user
				+ " on = " + folderPath + " by user : " + userid);
		if (folderPath.indexOf(user) < 0) {

			SessionWrapper sessions = JcrRepositoryUtils.loginDAV(userid, password);
			Session jcrsession = sessions.getSession();
			String[] users = user.split(",");
			String[] values = value.split(",");
			for (int j = 0; j < users.length; j++) {

				user = users[j];
				value = values[j];
				try {
					Node root = jcrsession.getRootNode();
					if (folderPath.length() > 1) {
						root = root.getNode(folderPath.substring(1));
					}
					Value[] actualUsers = root.getProperty(Config.USERS_SECURITY).getValues();
					String newUser = "";
					for (int i = 0; i < actualUsers.length; i++) {
						newUser += actualUsers[i].getString()+",,";
					}
					if (newUser.contains(userid) || root.getProperty(Config.EDMS_AUTHOR).getString().equals(userid)
							|| root.getProperty(Config.EDMS_OWNER).getString().contains(userid)) {

						String val = "";
						Value[] actualUsers1 = root.getProperty(Config.USERS_READ).getValues();
						String newUserRead = "";
						for (int i = 0; i < actualUsers1.length; i++) {
							newUserRead += actualUsers1[i].getString() + ",,";
						}
						if (newUserRead.contains(user)) {
							val = "ur";
						}
						actualUsers1 = root.getProperty(Config.USERS_WRITE).getValues();
						String newUserWrite = "";
						for (int i = 0; i < actualUsers1.length; i++) {
							newUserWrite += actualUsers1[i].getString() + ",,";
						}
						if (newUserWrite.contains(user)) {
							val = "uw";
						}
						actualUsers1 = root.getProperty(Config.USERS_SECURITY).getValues();
						String newUserMan = "";
						for (int i = 0; i < actualUsers1.length; i++) {
							newUserMan += actualUsers1[i].getString() + ",,";
						}
						if (newUserMan.contains(user)) {
							val = "us";
						}

						assignSinglePermissionRecursionDAV(root, userid, password, user, value, false, val);
						if (!value.contains("n")) {
							Workspace ws = jcrsession.getWorkspace();
							Node rty = jcrsession.getRootNode();
							if (!rty.hasNode(user + "/" + root.getName())) {
								ws.clone(ws.getName(), root.getPath(), "/" + user + "/" + root.getName(), false);
								jcrsession.save();
							} else {
								System.out.println("DateTime: " + new Date() + " ::::: " + "already exist");
							}
						} else {
							/*
							 * if(value.contains("r")){ Node
							 * remov=jcrsession.getRootNode().getNode(user+"/"+
							 * root.getName()); remov.remove(); }
							 */
						}
						// jcrsession.save();
					} else {
						return "sorry you don't have permissions to share this folder";
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
				}
			}
			return "Success";
		} else {
			System.out.println("DateTime: " + new Date() + " ::::: " + "Access Denied");
			return "Access Denied";
		}
	}

	public void removeSpecificPermissionToSpecificUserRecurssion(Node rt, Session jcrsession, String user,
			String value) {
		try {
			System.out.println("DateTime: " + new Date() + " ::::: " + "Removing permissions " + value + " to user "
					+ user + " on = " + rt.getPath());

			String val = "";
			Value[] actualUsers1 = rt.getProperty(Config.USERS_READ).getValues();
			String newUserRead = "";
			for (int i = 0; i < actualUsers1.length; i++) {
				newUserRead += actualUsers1[i].getString() + ",,";
			}
			if (newUserRead.contains(user)) {
				val = "ur";
			}
			actualUsers1 = rt.getProperty(Config.USERS_WRITE).getValues();
			String newUserWrite = "";
			for (int i = 0; i < actualUsers1.length; i++) {
				newUserWrite += actualUsers1[i].getString() + ",,";
			}
			if (newUserWrite.contains(user)) {
				val = "uw";
			}
			actualUsers1 = rt.getProperty(Config.USERS_SECURITY).getValues();
			String newUserMan = "";
			for (int i = 0; i < actualUsers1.length; i++) {
				newUserMan += actualUsers1[i].getString() + ",,";
			}
			if (newUserMan.contains(user)) {
				val = "us";
			}
			if (val.equals(value) || val.equals("")) {

				Value[] actualUsers = rt.getProperty(Config.USERS_SECURITY).getValues();
				String newUser = "";
				for (int i = 0; i < actualUsers.length; i++) {
					newUser += actualUsers[i].getString()+",,";
				}
				actualUsers = rt.getProperty(Config.USERS_READ).getValues();
				if (value.equals("ur")) {
					actualUsers = rt.getProperty(Config.USERS_READ).getValues();
					newUser = "";
					for (int i = 0; i < actualUsers.length; i++) {
						String[] kWords = actualUsers[i].getString().split(",");
						for (int j = 0; j < kWords.length; j++) {
							if (kWords[j].length() > 0) {
								if (kWords[j].equalsIgnoreCase(user) || kWords[j].equalsIgnoreCase("")) {
								} else {
									newUser += kWords[j]+",,";
								}
							}
						}
					}
					rt.setProperty(Config.USERS_READ, new String[] { newUser });
				}
				if (value.equals("uw")) {
					boolean flag = false;
					actualUsers = rt.getProperty(Config.USERS_WRITE).getValues();
					newUser = "";

					for (int i = 0; i < actualUsers.length; i++) {
						String[] kWords = actualUsers[i].getString().split(",");
						for (int j = 0; j < kWords.length; j++) {
							if (kWords[j].length() > 0) {
								if (kWords[j].equalsIgnoreCase(user) || kWords[j].equalsIgnoreCase("")) {
									flag = true;
								} else {
									newUser += kWords[j]+",,";
								}
							}
						}
					}
					rt.setProperty(Config.USERS_WRITE, new String[] { newUser });
					// if(flag){
					actualUsers = rt.getProperty(Config.USERS_READ).getValues();
					newUser = "";
					for (int i = 0; i < actualUsers.length; i++) {
						String[] kWords = actualUsers[i].getString().split(",");
						for (int j = 0; j < kWords.length; j++) {
							if (kWords[j].length() > 0) {
								if (kWords[j].equalsIgnoreCase(user) || kWords[j].equalsIgnoreCase("")) {
								} else {
									newUser += kWords[j]+",,";
								}
							}
						}
					}
					rt.setProperty(Config.USERS_READ, new String[] { newUser });
					// }
				}
				if (value.equals("us")) {
					boolean flag = false;
					actualUsers = rt.getProperty(Config.USERS_SECURITY).getValues();
					newUser = "";
					for (int i = 0; i < actualUsers.length; i++) {
						String[] kWords = actualUsers[i].getString().split(",");
						for (int j = 0; j < kWords.length; j++) {
							if (kWords[j].length() > 0) {
								if (kWords[j].equalsIgnoreCase(user) || kWords[j].equalsIgnoreCase("")) {
									flag = true;
								} else {
									newUser += kWords[j]+",,";
								}
							}
						}
					}
					rt.setProperty(Config.USERS_SECURITY, new String[] { newUser });
					// if(flag){
					flag = false;
					actualUsers = rt.getProperty(Config.USERS_WRITE).getValues();
					newUser = "";
					for (int i = 0; i < actualUsers.length; i++) {
						String[] kWords = actualUsers[i].getString().split(",");
						for (int j = 0; j < kWords.length; j++) {
							if (kWords[j].length() > 0) {
								if (kWords[j].equalsIgnoreCase(user) || kWords[j].equalsIgnoreCase("")) {
									flag = true;
								} else {
									newUser += kWords[j]+",,";
								}
							}
						}
					}
					rt.setProperty(Config.USERS_WRITE, new String[] { newUser });
					// }
					// if(flag){
					actualUsers = rt.getProperty(Config.USERS_READ).getValues();
					newUser = "";
					for (int i = 0; i < actualUsers.length; i++) {
						String[] kWords = actualUsers[i].getString().split(",");
						for (int j = 0; j < kWords.length; j++) {
							if (kWords[j].length() > 0) {
								if (kWords[j].equalsIgnoreCase(user) || kWords[j].equalsIgnoreCase("")) {
								} else {
									newUser += kWords[j]+",,";
								}
							}
						}
					}
					rt.setProperty(Config.USERS_READ, new String[] { newUser });
					// }
				}
				if (rt.hasNodes()) {
					for (NodeIterator nit = rt.getNodes(); nit.hasNext();) {
						Node root = nit.nextNode();
						if (Config.EDMS_FOLDER.equals(root.getPrimaryNodeType().getName())
								|| Config.EDMS_DOCUMENT.equals(root.getPrimaryNodeType().getName())) {
							removeSpecificPermissionToSpecificUserRecurssion(root, jcrsession, user, value);
						}
					}
				}
				for (NodeIterator nitt = rt.getSharedSet(); nitt.hasNext();) {
					Node nd = nitt.nextNode();
					if(nitt.getSize()>1){
					if (nd.getPath().indexOf(user) >= 0) {
						nd.removeShare();
					}}
				}
				jcrsession.save();
			} else {
			}
		} catch (Exception e) {

		}

	}

	public void removeSpecificPermissionToSpecificUserRecurssion1(Node rt, Session jcrsession, String user,
			String value) {
		try {
			System.out.println("DateTime: " + new Date() + " ::::: " + "Removing permissions " + value + " to user "
					+ user + " on = " + rt.getPath());

			String val = "";
			Value[] actualUsers = null;
			String newUser = "";

			boolean flag = false;
			actualUsers = rt.getProperty(Config.USERS_SECURITY).getValues();
			newUser = "";
			for (int i = 0; i < actualUsers.length; i++) {
				String[] kWords = actualUsers[i].getString().split(",");
				for (int j = 0; j < kWords.length; j++) {
					if (kWords[j].length() > 0) {
						if (kWords[j].equalsIgnoreCase(user) || kWords[j].equalsIgnoreCase("")) {
							flag = true;
						} else {
							newUser += kWords[j]+",,";
						}
					}
				}
			}
			rt.setProperty(Config.USERS_SECURITY, new String[] { newUser });
			// if(flag){
			flag = false;
			actualUsers = rt.getProperty(Config.USERS_WRITE).getValues();
			newUser = "";
			for (int i = 0; i < actualUsers.length; i++) {
				String[] kWords = actualUsers[i].getString().split(",");
				for (int j = 0; j < kWords.length; j++) {
					if (kWords[j].length() > 0) {
						if (kWords[j].equalsIgnoreCase(user) || kWords[j].equalsIgnoreCase("")) {
							flag = true;
						} else {
							newUser += kWords[j]+",,";
						}
					}
				}
			}
			rt.setProperty(Config.USERS_WRITE, new String[] { newUser });
			// }
			// if(flag){
			actualUsers = rt.getProperty(Config.USERS_READ).getValues();
			newUser = "";
			for (int i = 0; i < actualUsers.length; i++) {
				String[] kWords = actualUsers[i].getString().split(",");
				for (int j = 0; j < kWords.length; j++) {
					if (kWords[j].length() > 0) {
						if (kWords[j].equalsIgnoreCase(user) || kWords[j].equalsIgnoreCase("")) {
						} else {
							newUser += kWords[j]+",,";
						}
					}
				}
			}
			rt.setProperty(Config.USERS_READ, new String[] { newUser });
			// }

			if (rt.hasNodes()) {
				for (NodeIterator nit = rt.getNodes(); nit.hasNext();) {
					Node root = nit.nextNode();
					if (Config.EDMS_FOLDER.equals(root.getPrimaryNodeType().getName())
							|| Config.EDMS_DOCUMENT.equals(root.getPrimaryNodeType().getName())) {
						removeSpecificPermissionToSpecificUserRecurssion1(root, jcrsession, user, value);
					}
				}
			}
			
			for (NodeIterator nitt = rt.getSharedSet(); nitt.hasNext();) {
				
				Node nd = nitt.nextNode();
				if(nitt.getSize()>1){
				if (nd.getPath().indexOf(user) >= 0) {
					nd.removeShare();
				}}
			}
			jcrsession.save();
		} catch (Exception e) {

		}

	}

	public synchronized String removeAssignedPermission(String folderPath, String userid, String password, String user,
			String value) {
		try {
			System.out.println("DateTime: " + new Date() + " ::::: " + "Removing permissions " + value + " to user "
					+ user + " on = " + folderPath + " by user : " + userid);
			SessionWrapper sessions = JcrRepositoryUtils.login(userid, password);
			Session jcrsession = sessions.getSession();
			Node rt = jcrsession.getRootNode();
			// folderPath=folderPath.replace(userid, user);
			if (folderPath.length() > 1) {
				rt = rt.getNode(folderPath.substring(1));
			}
			Value[] actualUsers = rt.getProperty(Config.USERS_SECURITY).getValues();
			String newUser = "";
			for (int i = 0; i < actualUsers.length; i++) {
				newUser += actualUsers[i].getString()+",,";
			}
			if (newUser.contains(userid) || rt.getProperty(Config.EDMS_AUTHOR).getString().equals(userid)
					|| rt.getProperty(Config.EDMS_OWNER).getString().contains(userid)) {
				removeSpecificPermissionToSpecificUserRecurssion(rt, jcrsession, user, value);
				for (NodeIterator nit = rt.getSharedSet(); nit.hasNext();) {
					Node nd = nit.nextNode();
					// System.out.println("DateTime: "+new Date()+" :::::
					// "+nd.getPath());
					if(nit.getSize()>1){
					if (nd.getPath().indexOf(user) >= 0) {
						nd.removeShare();
					}}
				}
				jcrsession.save();

			} else {

				return "you don't have permission";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	public synchronized String removeAssignedPermissionDAV(String folderPath, String userid, String password,
			String user, String value) {
		try {
			System.out.println("DateTime: " + new Date() + " ::::: " + "Removing permissions " + value + " to user "
					+ user + " on = " + folderPath + " by user : " + userid);
			SessionWrapper sessions = JcrRepositoryUtils.loginDAV(userid, password);
			Session jcrsession = sessions.getSession();
			Node rt = jcrsession.getRootNode();
			// folderPath=folderPath.replace(userid, user);
			if (folderPath.length() > 1) {
				rt = rt.getNode(folderPath.substring(1));
			}
			Value[] actualUsers = rt.getProperty(Config.USERS_SECURITY).getValues();
			String newUser = "";
			for (int i = 0; i < actualUsers.length; i++) {
				newUser += actualUsers[i].getString()+",,";
			}
			if (newUser.contains(userid) || rt.getProperty(Config.EDMS_AUTHOR).getString().equals(userid)
					|| rt.getProperty(Config.EDMS_OWNER).getString().contains(userid)) {
				removeSpecificPermissionToSpecificUserRecurssion(rt, jcrsession, user, value);
				for (NodeIterator nit = rt.getSharedSet(); nit.hasNext();) {
					Node nd = nit.nextNode();
					// System.out.println("DateTime: "+new Date()+" :::::
					// "+nd.getPath());
					if(nit.getSize()>1){
					if (nd.getPath().indexOf(user) >= 0) {
						nd.removeShare();
					}}
				}
				jcrsession.save();

			} else {

				return "you don't have permission";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * Convert a Value array to String array.
	 */
	public static String[] Value2String(Value[] values)
			throws ValueFormatException, IllegalStateException, javax.jcr.RepositoryException {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < values.length; i++) {
			list.add(values[i].getString());
		}
		return (String[]) list.toArray(new String[list.size()]);
	}

	public Group createGroup(String userid, String password, String groupName, Session jcrsession, Node root) {
		User user = null;
		Group group = null;
		try {
			JackrabbitSession js = (JackrabbitSession) jcrsession;

			UserManager userManager = js.getUserManager();
			user = ((User) js.getUserManager().getAuthorizable(userid));

			group = userManager.createGroup(user.getPrincipal(), groupName);
			jcrsession.save();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return group;
	}

	public static void removeUser(Session jcrsession, String userid, String password) {
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

	/*
	 * public String getPermissions(User user, String path,Session jcrsession) {
	 * String permissions=""; try {
	 * 
	 * Node root = jcrsession.getRootNode(); JackrabbitSession js =
	 * (JackrabbitSession) jcrsession; AccessControlManager aMgr; aMgr =
	 * jcrsession .getAccessControlManager(); // get supported privileges of any
	 * node Privilege[] privileges = aMgr
	 * .getSupportedPrivileges(root.getPath()); for (int i = 0; i <
	 * privileges.length; i++) { //System.out.println("DateTime: "+new Date()+
	 * " ::::: "+privileges[i]); }
	 * 
	 * // get now applied privileges on a node Privilege[] privileges =
	 * aMgr.getPrivileges(root.getPath()); for (int i = 0; i <
	 * privileges.length; i++) { //System.out.println("DateTime: "+new Date()+
	 * " ::::: "+privileges[i]); permissions+=privileges[i]; } } catch
	 * (RepositoryException e) { e.printStackTrace(); } return permissions; }
	 */

	public FolderListReturn listSharedFolder(String userid, String password) {
		System.out.println("DateTime: " + new Date() + " ::::: " + "Lising sharedFolder of user : " + userid);
		String destUser = "";
		if (userid.contains("/")) {
			destUser = userid.substring(0, userid.indexOf("/"));
		} else {
			destUser = userid;
		}
		SessionWrapper sessions = JcrRepositoryUtils.login(destUser, password);
		Session jcrsession = sessions.getSession();
		// String sessionId=sessions.getId();
		FolderListReturn folderList1 = new FolderListReturn();
		ArrayOfFolders folders = new ArrayOfFolders();
		Node root = null;
		try {
			root = jcrsession.getRootNode();
			root = root.getNode(userid);
			for (NodeIterator nit = root.getNodes(); nit.hasNext();) {
				Node node = nit.nextNode();
				if (!node.getProperty(Config.EDMS_AUTHOR).getString().equals(destUser)) {
					if (Config.EDMS_FOLDER.equals(node.getPrimaryNodeType().getName())) {

						if (node.hasProperty(Config.USERS_READ)) {
							Value[] actualUsers = node.getProperty(Config.USERS_READ).getValues();
							String newUser = "";
							for (int i = 0; i < actualUsers.length; i++) {
								newUser += actualUsers[i].getString()+",,";
							}
							if (newUser.contains(destUser)) {
								Folder folder = new Folder();
								folder = setProperties(node, folder, userid, password, jcrsession, userid);
								folders.getFolderList().add(folder);
							}
						}
					}
				}
			}
			folderList1.setFolderListResult(folders);
			folderList1.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// JcrRepositoryUtils.logout(sessionId);
		}
		return folderList1;
	}

	public FolderListReturn listSharedFolder(String userid, String password, String path) {
		System.out.println("DateTime: " + new Date() + " ::::: " + "Lising sharedFolder by Path : " + path
				+ " of user : " + userid + " by user : " + userid);
		path = path.replace("'", "&apos;");
		path = path.replace("<", "&lt;");
		path = path.replace(">", "&gt;");
		path = path;

		SessionWrapper sessions = JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		// String sessionId=sessions.getId();
		FolderListReturn folderList1 = new FolderListReturn();
		ArrayOfFolders folders = new ArrayOfFolders();
		try {
	//	jcrsession.getRootNode().getNode(path.substring(1)+"/fffff").getProperty(Config.USERS_READ).getValues()[0].getString();
			javax.jcr.query.QueryManager queryManager;
			queryManager = jcrsession.getWorkspace().getQueryManager();
			String expression = "select * from [edms:folder] AS s WHERE ISCHILDNODE(s,'" + path + "') AND CONTAINS(s.["
					+ Config.USERS_READ + "], '*" + userid + "*') ORDER BY s.[" + Config.EDMS_Sorting_Parameter
					+ "] ASC";
			// expression = "select * from [edms:folder] AS s WHERE NAME like
			// ['%san%']";
			javax.jcr.query.Query query = queryManager.createQuery(expression, javax.jcr.query.Query.JCR_SQL2);
			javax.jcr.query.QueryResult result = query.execute();

			for (NodeIterator nit = result.getNodes(); nit.hasNext();) {
				Node node = nit.nextNode();
				Folder folder = new Folder();
				folder = setProperties(node, folder, userid, password, jcrsession, path);
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

	public FolderListReturn listSharedFolderDAV(String userid, String password, String path) {
		System.out.println("DateTime: " + new Date() + " ::::: " + "Lising sharedFolder by Path : " + path
				+ " of user : " + userid + " by user : " + userid);
		path = path.replace("'", "&apos;");
		path = path.replace("<", "&lt;");
		path = path.replace(">", "&gt;");
		path = path;

		SessionWrapper sessions = JcrRepositoryUtils.loginDAV(userid, password);
		Session jcrsession = sessions.getSession();
		// String sessionId=sessions.getId();
		FolderListReturn folderList1 = new FolderListReturn();
		ArrayOfFolders folders = new ArrayOfFolders();
		try {
			javax.jcr.query.QueryManager queryManager;
			queryManager = jcrsession.getWorkspace().getQueryManager();
			String expression = "select * from [edms:folder] AS s WHERE ISCHILDNODE(s,'" + path + "') AND CONTAINS(s.["
					+ Config.USERS_READ + "], '*" + userid + "*') ORDER BY s.[" + Config.EDMS_Sorting_Parameter
					+ "] ASC";
			// expression = "select * from [edms:folder] AS s WHERE NAME like
			// ['%san%']";
			javax.jcr.query.Query query = queryManager.createQuery(expression, javax.jcr.query.Query.JCR_SQL2);
			javax.jcr.query.QueryResult result = query.execute();

			for (NodeIterator nit = result.getNodes(); nit.hasNext();) {
				Node node = nit.nextNode();
				Folder folder = new Folder();
				folder = setProperties(node, folder, userid, password, jcrsession, path);
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

	public GetRecycledDocsResponse listRecycledDoc(String userid, String password, String path) {
		System.out.println("DateTime: " + new Date() + " ::::: " + "listing Trash of user : " + userid + " at path : "
				+ path + " by user : " + userid);
		FilesAndFolders filesFolders = new FilesAndFolders();
		filesFolders = listRecycledDocRecursion(userid, password, path, filesFolders);
		GetRecycledDocsResponse res = new GetRecycledDocsResponse();
		res.setGetRecycledDocs(filesFolders);
		return res;
	}

	public FilesAndFolders listRecycledDocRecursion(String userid, String password, String path,
			FilesAndFolders filesFolders) {
		System.out.println("DateTime: " + new Date() + " ::::: " + "Recursively listing Trash of user : " + userid
				+ " at path : " + path + " by user : " + userid);
		path = path.replace("'", "&apos;");
		path = path.replace("<", "&lt;");
		path = path.replace(">", "&gt;");
		path = path;
		SessionWrapper sessions = JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		// String sessionId=sessions.getId();
		ArrayOfFiles files = new ArrayOfFiles();
		ArrayOfFolders folders = new ArrayOfFolders();
		Node root = null;
		try {
			root = jcrsession.getRootNode();
			root = root.getNode(path.substring(1));
			javax.jcr.query.QueryManager queryManager;
			queryManager = jcrsession.getWorkspace().getQueryManager();
			String expression = "select * from [edms:folder] AS s WHERE ISCHILDNODE(s,'" + path + "')  ";
			// expression = "select * from [edms:folder] AS s WHERE NAME like
			// ['%san%']";
			javax.jcr.query.Query query = queryManager.createQuery(expression, javax.jcr.query.Query.JCR_SQL2);
			javax.jcr.query.QueryResult result = query.execute();

			for (NodeIterator nit = result.getNodes(); nit.hasNext();) {
				Node node = nit.nextNode();
				Folder folder = new Folder();
				folder = setGeneralFolderProperties(node, folder, userid, password);
				folders.getFolderList().add(folder);
			}
			expression = "select * from [edms:document] AS s WHERE ISCHILDNODE(s,'" + path + "')  ";
			// expression = "select * from [edms:folder] AS s WHERE NAME like
			// ['%san%']";
			query = queryManager.createQuery(expression, javax.jcr.query.Query.JCR_SQL2);
			result = query.execute();

			for (NodeIterator nit = result.getNodes(); nit.hasNext();) {

				Node node = nit.nextNode();
				File file = new File();
				file = new FileRepository().setGeneralFileProperties(node, file, userid, password);
				files.getFileList().add(file);
			}
			filesFolders.setFilesList(files);
			filesFolders.setFoldersList(folders);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// JcrRepositoryUtils.logout(sessionId);
		}

		return filesFolders;
	}

	public Folder setGeneralFolderProperties(Node node, Folder folder, String userid, String password) {

		try {
			folder.setFolderName(node.getName());
			folder.setFolderPath(node.getPath());
			folder.setParentFolder(node.getParent().getName());
			folder.setCreatedBy(node.getProperty(Config.EDMS_AUTHOR).getString());
			if (node.hasProperty(Config.EDMS_MODIFICATIONDATE))
				folder.setModificationDate(node.getProperty(Config.EDMS_MODIFICATIONDATE).getString());
			if (node.hasProperty(Config.EDMS_CREATIONDATE))
				folder.setCreationDate(node.getProperty(Config.EDMS_CREATIONDATE).getString());
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return folder;
	}

	public String recycleFolder(String folderPath, String userid, String password) {
		System.out.println("DateTime: " + new Date() + " ::::: " + "Move Documents to Trash : " + folderPath
				+ " by user : " + userid);
		String response = "";
		SessionWrapper sessions = JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		Node root = null;
		String newUser = null;
		try {
			root = jcrsession.getRootNode();
			newUser = "";
			root = root.getNode(folderPath.substring(1));
			if (root.hasProperty(Config.USERS_SECURITY)) {
				Value[] actualUsers = root.getProperty(Config.USERS_SECURITY).getValues();
				
				for (int i = 0; i < actualUsers.length; i++) {
					newUser += actualUsers[i].getString()+",,";
				}}
		} catch (PathNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ValueFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalStateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (RepositoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			if (newUser.contains(userid) ) 
				{
		
			
			
				removeSpecificPermissionToSpecificUserRecurssion1(root, jcrsession, userid, "");
						
				
			
		} else {

			// String sessionId=sessions.getId();

			String rel = "";
			String[] folders = folderPath.split(",");
			for (int j = 0; j < folders.length; j++) {
				try {
					folderPath = folders[j];
					 root = jcrsession.getRootNode();
					root = root.getNode(folderPath.substring(1));
				
								removePermissionsRecursion(root, jcrsession);
								response = recycleFolderRecursion(root, userid, password, rel);
							
				} catch (RepositoryException e) {
					response = "Some error occured";
					e.printStackTrace();
				}
			}
		}
		return response;
	}

	public String recycleFolderDAV(String folderPath, String userid, String password) {
		System.out.println("DateTime: " + new Date() + " ::::: " + "Move Documents to Trash : " + folderPath
				+ " by user : " + userid);
		String response = "";
		SessionWrapper sessions = JcrRepositoryUtils.loginDAV(userid, password);
		Session jcrsession = sessions.getSession();
		Node root = null;
		String newUser = null;
		try {
			root = jcrsession.getRootNode();
			newUser = "";
			root = root.getNode(folderPath.substring(1));
			if (root.hasProperty(Config.USERS_SECURITY)) {
				Value[] actualUsers = root.getProperty(Config.USERS_SECURITY).getValues();
				
				for (int i = 0; i < actualUsers.length; i++) {
					newUser += actualUsers[i].getString()+",,";
				}}
		} catch (PathNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ValueFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalStateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (RepositoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			if (newUser.contains(userid) ) 
				{
		
			
			
				removeSpecificPermissionToSpecificUserRecurssion1(root, jcrsession, userid, "");
						
				
			
		} else {

			// String sessionId=sessions.getId();

			String rel = "";
			String[] folders = folderPath.split(",");
			for (int j = 0; j < folders.length; j++) {
				try {
					folderPath = folders[j];
					 root = jcrsession.getRootNode();
					root = root.getNode(folderPath.substring(1));
				
								removePermissionsRecursion(root, jcrsession);
								response = recycleFolderRecursion(root, userid, password, rel);
							
				} catch (RepositoryException e) {
					response = "Some error occured";
					e.printStackTrace();
				}
			}
		}
		return response;
	}

	public void assignPermissionRecursion(Node rt, Session jcrsession, Node parent,String userid) {

		// String sessionId=sessions.getId();
		try {
			System.out.println("DateTime: " + new Date() + " ::::: " + "Recursively assigning permissions on  : "
					+ rt.getPath() + " of parent : " + parent.getPath());
			rt.addMixin(JcrConstants.MIX_SHAREABLE);
			Value[] actualUsers = parent.getProperty(Config.USERS_READ).getValues();
			String newUser = "";
			for (int i = 0; i < actualUsers.length; i++) {
				newUser += actualUsers[i].getString()+",,";
			}
			rt.setProperty(Config.USERS_READ, new String[] { newUser });
			actualUsers = parent.getProperty(Config.USERS_SECURITY).getValues();
			newUser = "";
			for (int i = 0; i < actualUsers.length; i++) {
				newUser += actualUsers[i].getString()+",,";
			}
			rt.setProperty(Config.USERS_SECURITY, new String[] { newUser });
			actualUsers = parent.getProperty(Config.USERS_WRITE).getValues();
			newUser = "";
			for (int i = 0; i < actualUsers.length; i++) {
				newUser += actualUsers[i].getString()+",,";
			}
			rt.setProperty(Config.USERS_WRITE, new String[] { newUser });
			if (parent.getPath().indexOf('/') != parent.getPath().lastIndexOf('/')) {
				// rt.setProperty(Config.EDMS_AUTHOR,parent.getProperty(Config.EDMS_AUTHOR).getString());
				rt.setProperty(Config.EDMS_OWNER, parent.getProperty(Config.EDMS_OWNER).getString());
				rt.setProperty(Config.EDMS_AUTHOR, parent.getProperty(Config.EDMS_AUTHOR).getString());
			}else{

				rt.setProperty(Config.EDMS_OWNER, "admin"+userid);
				rt.setProperty(Config.EDMS_AUTHOR, userid);
			}

			jcrsession.save();
			if (rt.hasNodes()) {
				for (NodeIterator nit = rt.getNodes(); nit.hasNext();) {
					Node root = nit.nextNode();
					if (Config.EDMS_FOLDER.equals(root.getPrimaryNodeType().getName())
							|| Config.EDMS_DOCUMENT.equals(root.getPrimaryNodeType().getName()))
						assignPermissionRecursion(root, jcrsession, parent,userid);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void removePermissionsRecursion(Node root, Session jcrsession) {
		try {
			System.out.println(
					"DateTime: " + new Date() + " ::::: " + "Recursively Removing permissions on  : " + root.getPath());
			root.setProperty(Config.USERS_READ, new String[] { "admin" });
			root.setProperty(Config.USERS_WRITE, new String[] { "admin" });
			root.setProperty(Config.USERS_SECURITY, new String[] { "admin" });
			root.setProperty(Config.GROUPS_READ, new String[] { "admin" });
			root.setProperty(Config.GROUPS_WRITE, new String[] { "admin" });
			root.setProperty(Config.GROUPS_DELETE, new String[] { "admin" });
			// root.setProperty(Config.EDMS_RESTORATION_PATH, root.getPath());
			jcrsession.save();
			if (root.hasNodes()) {
				for (NodeIterator nit = root.getNodes(); nit.hasNext();) {
					Node rt = nit.nextNode();
					if (Config.EDMS_FOLDER.equals(rt.getPrimaryNodeType().getName())
							|| Config.EDMS_DOCUMENT.equals(rt.getPrimaryNodeType().getName()))
						removePermissionsRecursion(rt, jcrsession);
				}
			}
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ValueFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (VersionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LockException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConstraintViolationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ItemExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReferentialIntegrityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidItemStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchNodeTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String recycleFolderRecursion(Node root, String userid, String password,
			String rel/* ,boolean status */) {
		try {
			System.out.println("DateTime: " + new Date() + " ::::: " + "Recursively move folder to trash  : "
					+ root.getPath() + " by user : " + userid);
			SessionWrapper sessions = JcrRepositoryUtils.login(userid, password);
			Session jcrsession = sessions.getSession();
			// String sessionId=sessions.getId();
			// root.setProperty(Config.EDMS_RECYCLE_DOC, true);
			root.setProperty(Config.USERS_READ, new String[] { "admin" });
			root.setProperty(Config.USERS_WRITE, new String[] { "admin" });
			root.setProperty(Config.USERS_SECURITY, new String[] { "admin" });
			root.setProperty(Config.GROUPS_READ, new String[] { "admin" });
			root.setProperty(Config.GROUPS_WRITE, new String[] { "admin" });
			root.setProperty(Config.GROUPS_DELETE, new String[] { "admin" });
			root.setProperty(Config.EDMS_RESTORATION_PATH, root.getPath());
			jcrsession.save();
			String relpath = userid + "/trash/" + rel + root.getName();
			rel = rel + root.getName() + "/";
			while (jcrsession.getRootNode().hasNode(relpath)) {
				relpath += "-copy";
			}
			if (root.isNodeType(JcrConstants.MIX_SHAREABLE)) {
				// if(status){
				jcrsession.getWorkspace().copy(root.getPath(), "/" + relpath);
				jcrsession.save();

				// }
				/*
				 * if(root.hasNodes()){ for (NodeIterator nit = root.getNodes();
				 * nit.hasNext();) { Node rt=nit.nextNode();
				 * if(Config.EDMS_FOLDER.equals(root.getPrimaryNodeType().
				 * getName())||Config.EDMS_DOCUMENT.equals(root.
				 * getPrimaryNodeType().getName())) recycleFolderRecursion(rt,
				 * userid,password,rel,false); } }
				 */
				// if(status){
				root.removeSharedSet();
				jcrsession.save();
				// }
				return "Moved to Trash, Please refresh";
			} else {
				// if(status){
				System.out.println("DateTime: " + new Date() + " ::::: " + "Copy then remove");
				jcrsession.getWorkspace().copy(root.getPath(), "/" + relpath);
				jcrsession.save();
				// }
				/*
				 * if(root.hasNodes()){ for (NodeIterator nit = root.getNodes();
				 * nit.hasNext();) { Node rt=nit.nextNode();
				 * if(Config.EDMS_FOLDER.equals(root.getPrimaryNodeType().
				 * getName())||Config.EDMS_DOCUMENT.equals(root.
				 * getPrimaryNodeType().getName())) recycleFolderRecursion(rt,
				 * userid,password,rel,false); } } if(status){
				 */

				root.remove();
				jcrsession.save();
				/* } */

				return "Moved to Trash, Please refresh";
			}
		} catch (RepositoryException e) {
			if (e.toString().indexOf("ItemExistsException") > 0)
				return "Error Occured, Already Exist in Trash";
			else
				return "Error Occured, Please try again";

			// e.printStackTrace();
		}
		// return "Error Occured. Please try again";

	}

	public double reduceQuotaValue(String userid,double size){

		//SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
				double quotaUsed=0.0;
				SessionFactory sessionFactory=webServiceConfig.buildSessionFactory();
				org.hibernate.Session session = sessionFactory.openSession();
				String hql = "from Quota where userId like :userid";
				try{		
				Query query = session.createQuery(hql);
				query.setParameter("userid", "%" + userid + "%");
				List<Quota> snlst = query.list();
				if(snlst.size()>0)
				{
				Quota quota=snlst.get(0);
				quota.setQuotaUsed(quota.getQuotaUsed()-size);
				session.beginTransaction();
				session.saveOrUpdate(quota);
				session.getTransaction().commit();
				session.close();
				}
				else
				{}
				}catch(Exception e){
			e.printStackTrace();
		}finally{
			//session.close();
			//sessionFactory.close();
		}
		return quotaUsed;	
	}

	public String deleteFolder(String folderPath, String userid, String password) {

		System.out.println("DateTime: " + new Date() + " ::::: " + "Permanent delete folder : " + folderPath
				+ " by user : " + userid);
		SessionWrapper sessions = JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		// String sessionId=sessions.getId();
		String response = "";
		try {
			Node root = jcrsession.getRootNode();
			root = root.getNode(folderPath.substring(1));

			/*
			 * Version
			 * version=jcrsession.getWorkspace().getVersionManager().checkin(
			 * root.getParent().getPath()); System.out.println("DateTime: "+new
			 * Date()+" ::::: "+"Version of :"+root.getParent().getPath()+
			 * " has been created, Version name is : "+version.getName());
			 * jcrsession.getWorkspace().getVersionManager().checkout(root.
			 * getParent().getPath());
			 */
		/*	try{
			double dbl=root.getProperty(Config.EDMS_SIZE).getDouble();
			reduceQuotaValue(userid,dbl);
			}catch(Exception e){
				e.printStackTrace();
			}*/
			root.remove();
			response = "success";
		} catch (RepositoryException e) {
			response = "Exception Occured";
		} finally {
			// JcrRepositoryUtils.logout(sessionId);
		}
		return response;
	}

	public String restoreFolder(String folderPath, String userid, String password) {

		System.out.println("DateTime: " + new Date() + " ::::: " + "Restoring folder : " + folderPath + " by user : " + userid);
		SessionWrapper sessions = JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		// String sessionId=sessions.getId();
		String response = "";
		try {
			Node root = jcrsession.getRootNode();
			root = root.getNode(folderPath.substring(1));

			response = restoreFolderRecursion(root, userid, password);
		} catch (RepositoryException e) {
			response = "Exception Occured";
			e.printStackTrace();
		} finally {
		}
		return response;
	}

	public String restoreFolderRecursion(Node root, String userid, String password) {

		SessionWrapper sessions = JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		// String sessionId=sessions.getId();
		try {
			System.out.println("DateTime: " + new Date() + " ::::: " + "Restoring folder recursively : "
					+ root.getPath() + " by user : " + userid);
			String parents = root.getProperty(Config.EDMS_RESTORATION_PATH).getString().substring(1);
			parents = parents.substring(0, parents.lastIndexOf("/"));
			// System.out.println("DateTime: "+new Date()+" ::::: "+"parent is
			// "+parents);
			/*
			 * if(jcrRoot.hasNode(parents)){ parent = jcrRoot.getNode(parents);
			 * }else{ parent=createFolderRecursionWhenNotFound(parents,userid);
			 * }
			 */
			// root.setProperty(Config.EDMS_RECYCLE_DOC, false);
			// System.out.println("DateTime: "+new Date()+" :::::
			// "+root.getPath()+" source to :
			// "+root.getProperty(Config.EDMS_RESTORATION_PATH).getString());

			String relpath = root.getProperty(Config.EDMS_RESTORATION_PATH).getString();
			while (jcrsession.getRootNode().hasNode(relpath.substring(1))) {
				relpath = root.getProperty(Config.EDMS_RESTORATION_PATH).getString();
				relpath += "-copy";
			}
			if (root.isNodeType(JcrConstants.MIX_SHAREABLE)) {
				root.getSession().getWorkspace().copy(root.getPath(), relpath);
				root.removeSharedSet();
			} else {
				try {
					jcrsession.getWorkspace().move(root.getPath(), relpath);
				} catch (Exception e) {
					jcrsession.getWorkspace().copy(root.getPath(), relpath);
					root.removeSharedSet();
				}
			}
			jcrsession.save();
			Node destparent = jcrsession.getNode(relpath);
			Node parent = jcrsession.getNode("/" + parents);
			assignPermissionRecursion(destparent, jcrsession, parent,userid);
			/*
			 * int no_of_folders=Integer.parseInt(parent.getProperty(Config.
			 * EDMS_NO_OF_FOLDERS).getString());
			 * parent.setProperty(Config.EDMS_NO_OF_FOLDERS,no_of_folders+1);
			 */
			// jcrsession.save();
			/*
			 * if(root.hasNodes()){ for (NodeIterator nit = root.getNodes();
			 * nit.hasNext();) { Node node = nit.nextNode();
			 * restoreFolderRecursion(node); } }
			 */

			return "Restored Successfully";
		} catch (RepositoryException e) {
			if (e.toString().indexOf("ItemExistsException") > 0)
				return "Error Occured, Already Exist";
			else if (e.toString().indexOf("PathNotFound") > 0) {
				try {
					String relpath = "/" + userid + "/" + root.getName();
					while (jcrsession.getRootNode().hasNode(relpath.substring(1))) {
						// relpath=root.getProperty(Config.EDMS_RESTORATION_PATH).getString();
						relpath += "-copy";
					}
					if (root.isNodeType(JcrConstants.MIX_SHAREABLE)) {
						root.getSession().getWorkspace().copy(root.getPath(), relpath);
						root.removeSharedSet();
					} else {
						try {
							jcrsession.getWorkspace().move(root.getPath(), relpath);
						} catch (Exception e1) {
							jcrsession.getWorkspace().copy(root.getPath(), relpath);
							root.removeSharedSet();
						}
					}
					jcrsession.save();
				} catch (AccessDeniedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ValueFormatException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (PathNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ConstraintViolationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (VersionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ItemExistsException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (LockException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ReferentialIntegrityException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InvalidItemStateException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (NoSuchNodeTypeException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (RepositoryException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				return "Restored Successfully";
			} else
				return "Error Occured!!!";

			// e.printStackTrace();
		}
	}

	public Node createFolderRecursionWhenNotFound(String folderName, String userid, String password) {

		SessionWrapper sessions = JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		// String sessionId=sessions.getId();
		Node folder = null;
		try {
			Node root = jcrsession.getRootNode();
			// System.out.println("DateTime: "+new Date()+" ::::: "+"in folder
			// creation recursion
			// "+folderName.substring(0,folderName.lastIndexOf("/")+1));
			String parent = folderName.substring(0, folderName.lastIndexOf("/"));
			if (root.hasNode(parent)) {
				root = root.getNode(parent);
				folder = root.addNode(folderName.substring(folderName.lastIndexOf("/") + 1), Config.EDMS_FOLDER);
				folder.setProperty(Config.USERS_READ, new String[] {});
				folder.setProperty(Config.USERS_WRITE, new String[] {});
				folder.setProperty(Config.USERS_DELETE, new String[] {});
				folder.setProperty(Config.USERS_SECURITY, new String[] {});
				folder.setProperty(Config.GROUPS_READ, new String[] {});
				folder.setProperty(Config.GROUPS_WRITE, new String[] {});
				folder.setProperty(Config.GROUPS_DELETE, new String[] {});
				folder.setProperty(Config.GROUPS_SECURITY, new String[] {});
				folder.setProperty(Config.EDMS_KEYWORDS, "root,folder".split(","));
				folder.setProperty(Config.EDMS_AUTHOR, userid);
				folder.setProperty(Config.EDMS_DESCRIPTION, "this is system created folder while restoration");
				folder.setProperty(Config.EDMS_CREATIONDATE, (new Date()).toString());
				folder.setProperty(Config.EDMS_MODIFICATIONDATE, (new Date()).toString());
				folder.setProperty(Config.EDMS_RECYCLE_DOC, false);
				folder.setProperty(Config.EDMS_NO_OF_FOLDERS, 0);
				folder.setProperty(Config.EDMS_NO_OF_DOCUMENTS, 0);
				// folder.addMixin(JcrConstants.MIX_SHAREABLE);
				folder.addMixin(JcrConstants.MIX_VERSIONABLE);
				jcrsession.save();
			} else {
				createFolderRecursionWhenNotFound(folderName.substring(0, folderName.lastIndexOf("/") + 1), userid,
						password);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return folder;
	}

	public RenameFolderRes renameFolder(String oldfolderPath, String newFolderPath, String userid, String password) {

		System.out.println("DateTime: " + new Date() + " ::::: " + "Rename folder from : " + oldfolderPath + " to : "
				+ newFolderPath + " by user : " + userid);
		SessionWrapper sessions = JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		// String sessionId=sessions.getId();
		RenameFolderRes response = new RenameFolderRes();
		try {
			Node forVer = jcrsession.getRootNode().getNode(oldfolderPath.substring(1));
			Value[] actualUsers = forVer.getProperty(Config.USERS_WRITE).getValues();
			String newUser = "";
			for (int i = 0; i < actualUsers.length; i++) {
				newUser += actualUsers[i].getString()+",,";
			}

			if (newUser.contains(userid) || forVer.getProperty(Config.EDMS_AUTHOR).getString().equals(userid)||oldfolderPath.indexOf(userid)>=0) {

				/*
				 * Version
				 * version=jcrsession.getWorkspace().getVersionManager().checkin
				 * (forVer.getPath()); System.out.println("DateTime: "+new
				 * Date()+" ::::: "+"Version of :"+forVer.getPath()+
				 * " has been created, Version name is : "+version.getName());
				 * jcrsession.getWorkspace().getVersionManager().
				 * getVersionHistory(forVer.getPath()).addVersionLabel(version.
				 * getName(), "renamed from "
				 * +oldfolderPath.substring(oldfolderPath.lastIndexOf("/")+1)+
				 * " to "+newFolderPath, true);
				 * jcrsession.getWorkspace().getVersionManager().checkout(forVer
				 * .getPath());
				 */

				jcrsession.move(oldfolderPath,
						oldfolderPath.substring(0, oldfolderPath.lastIndexOf("/")) + "/" + newFolderPath);
				SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ss.SSSZ");
				forVer.setProperty(Config.EDMS_MODIFICATIONDATE,
						(format.format(new Date())).toString().replace("+0530", "Z"));
				forVer.setProperty(Config.EDMS_NAME, newFolderPath);
				jcrsession.save();
				response.setResponse("Success");
				response.setSuccess(true);
			} else {
				response.setResponse("Access Denied");
				response.setSuccess(false);
			}
		} catch (RepositoryException e) {
			response.setResponse("Access Denied");
			response.setSuccess(false);
			e.printStackTrace();
		} finally {
		}
		return response;
	}

	public String restoreVersion(String folderPath, String versionName, String userid, String password) {

		String[] str = folderPath.split(",");
		for (int i = 0; i < str.length; i++) {
			System.out.println("DateTime: " + new Date() + " ::::: " + ".......................");
		}
		SessionWrapper sessions = JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		// String sessionId=sessions.getId();
		String response = "";
		try {
			jcrsession.save();
			VersionManager versionManager = jcrsession.getWorkspace().getVersionManager();
			Node forVer = jcrsession.getRootNode().getNode(folderPath.substring(1));
			// System.out.println("DateTime: "+new Date()+" :::::
			// "+forVer.getProperty(Config.EDMS_AUTHOR).getString());
			if (forVer.getProperty(Config.EDMS_AUTHOR).getString().equals(userid)) {
				if (versionName != "jcr:rootVersion") {
					Version version = versionManager.getVersionHistory(folderPath).getVersion(versionName);
					System.out.println("DateTime: " + new Date() + " ::::: " + "version is : "
							+ version.getFrozenNode().getPath());
					versionManager.restore(new Version[] { version }, true);
					response = "success";
				} else {
					versionManager.getVersionHistory(folderPath).getRootVersion();
					response = "success";
				}

			} else {
				response = "Access Denied";
			}
		} catch (RepositoryException e) {
			response = "Access Denied";
			e.printStackTrace();
		} finally {
		}
		return response;
	}
	/*
	 * public static String[] usrValue2String(Value[] values, String usrId)
	 * throws ValueFormatException, IllegalStateException,
	 * javax.jcr.RepositoryException { ArrayList<String> list = new
	 * ArrayList<String>();
	 * 
	 * for (int i=0; i<values.length; i++) { // Admin and System user is not
	 * propagated across the child nodes if
	 * (!values[i].getString().equals(Config.SYSTEM_USER) &&
	 * !values[i].getString().equals(Config.ADMIN_USER)) {
	 * list.add(values[i].getString()); } }
	 * 
	 * if (Config.USER_ASSIGN_DOCUMENT_CREATION) { // No add an user twice if
	 * (!list.contains(usrId)) { list.add(usrId); } }
	 * 
	 * return (String[]) list.toArray(new String[list.size()]); }
	 */

	public RecentlyModifiedResponse recentlyModified(String folderPath, String userid, String password) {
		folderPath = folderPath.replace("'", "&apos;");
		folderPath = folderPath.replace("<", "&lt;");
		folderPath = folderPath.replace(">", "&gt;");
		SessionWrapper sessions = JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		// String sessionId=sessions.getId();
		RecentlyModifiedResponse res = new RecentlyModifiedResponse();
		ArrayOfFolders folders = new ArrayOfFolders();
		FilesAndFolders filesFolders = new FilesAndFolders();
		javax.jcr.query.QueryManager queryManager;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ss.SSSZ");
		String dateto = format.format(cal.getTime());
		cal.add(Calendar.DAY_OF_MONTH, -7);
		String datefrom = format.format(cal.getTime());
		dateto = dateto.replace("+0530", "Z");
		datefrom = datefrom.replace("+0530", "Z");

		try {
			queryManager = jcrsession.getWorkspace().getQueryManager();
			// Create a query object ...
			String expression = "select * from [edms:folder] AS s WHERE ISDESCENDANTNODE(s,'" + folderPath
					+ "')   and  (CONTAINS(s.[edms:owner],'*" + userid + "*') or CONTAINS(s.[edms:author],'*" + userid + "*'))  and " + " s.[jcr:created] >= CAST('"
					+ datefrom + "' AS DATE) AND s.[jcr:created] <= CAST('" + dateto + "' AS DATE)" + "  ORDER BY s.["
					+ Config.EDMS_Sorting_Parameter + "] ASC ";
			// expression = "select * from [edms:folder] AS s WHERE NAME() =
			// 'sanjay1' ";
			// expression = "select * from [edms:document] WHERE NAME() like
			// '%.png' ";
			// expression="SELECT p.* FROM [nt:base] AS p WHERE
			// p.[jcr:lastModified] >= CAST('2015-01-01T00:00:00.000Z' AS DATE)
			// AND p.[jcr:lastModified] <= CAST('2015-12-31T23:59:59.999Z' AS
			// DATE)";
			// expression = "select * from [edms:folder] where [jcr:path] like
			// '%santosh%'";

			javax.jcr.query.Query query = queryManager.createQuery(expression, javax.jcr.query.Query.JCR_SQL2);

			// query.setLimit(10);
			// query.setOffset(0);
			// Execute the query and get the results ...
			javax.jcr.query.QueryResult result = query.execute();
			// System.out.println("DateTime: "+new Date()+" :::::
			// "+result.getNodes());
			for (NodeIterator nit = result.getNodes(); nit.hasNext();) {

				Node node = nit.nextNode();
				if (!node.getPath().contains("trash")) {
					Folder folder = new Folder();
					setGeneralFolderProperties(node, folder, userid, password);
					folders.getFolderList().add(folder);
				}
			}
			ArrayOfFiles files = new ArrayOfFiles();
			expression = "select * from [edms:document] AS s WHERE ISDESCENDANTNODE(s,'" + folderPath
					+ "')   and  (CONTAINS(s.[edms:owner],'*" + userid + "*') or CONTAINS(s.[edms:author],'*" + userid + "*'))   ORDER BY s.["
					+ Config.EDMS_Sorting_Parameter + "] ASC ";
			query = queryManager.createQuery(expression, javax.jcr.query.Query.JCR_SQL2);
			result = query.execute();
			for (NodeIterator nit = result.getNodes(); nit.hasNext();) {
				Node node = nit.nextNode();
				if (!node.getPath().contains("trash")) {
					File file = new File();
					file = new FileRepository().setPropertiesWithoutStream(node, file, userid, password, jcrsession);
					files.getFileList().add(file);
				}
			}
			filesFolders.setFoldersList(folders);
			filesFolders.setFilesList(files);
			res.setRecentlyModifiedFolders(filesFolders);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// JcrRepositoryUtils.logout(sessionId);
		}
		return res;
	}

	public boolean addKeyword(String folderPath, String userid, String password, String keyword) {

		System.out.println("DateTime: " + new Date() + " ::::: " + "Adding keyword : " + keyword + " to : " + folderPath
				+ " by user : " + userid);
		SessionWrapper sessions = JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		// String sessionId=sessions.getId();
		try {
			Node root = jcrsession.getRootNode();
			root = root.getNode(folderPath.substring(1));

			/*
			 * Version
			 * version=jcrsession.getWorkspace().getVersionManager().checkin(
			 * root.getPath()); System.out.println("DateTime: "+new Date()+
			 * " ::::: "+"Version of :"+root.getPath()+
			 * " has been created, Version name is : "+version.getName());
			 * jcrsession.getWorkspace().getVersionManager().checkout(root.
			 * getPath());
			 */
			Value[] actualUsers = root.getProperty(Config.EDMS_KEYWORDS).getValues();
			String newUser = "";
			for (int i = 0; i < actualUsers.length; i++) {
				String[] kWords = actualUsers[i].getString().split(",");
				for (int j = 0; j < kWords.length; j++) {
					if (kWords[j].length() > 0) {
						// if(!kWords[j].equalsIgnoreCase(keyword))
						newUser += kWords[j]+",,";
					}
				}
			}
			// System.out.println("DateTime: "+new Date()+" :::::
			// "+newUser.contains(user));
			// if(!newUser.contains(keyword))
			// {
			newUser += keyword+",,";
			root.setProperty(Config.EDMS_KEYWORDS, new String[] { newUser });
			jcrsession.save();
			return true;
			// }
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		// return true;
	}

	public boolean removeKeyword(String folderPath, String userid, String password, String keyword) {

		System.out.println("DateTime: " + new Date() + " ::::: " + "Removing keyword : " + keyword + " from : "
				+ folderPath + " by user : " + userid);
		SessionWrapper sessions = JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		// String sessionId=sessions.getId();

		try {
			Node root = jcrsession.getRootNode();
			root = root.getNode(folderPath.substring(1));

			/*
			 * Version
			 * version=jcrsession.getWorkspace().getVersionManager().checkin(
			 * root.getPath()); System.out.println("DateTime: "+new Date()+
			 * " ::::: "+"Version of :"+root.getPath()+
			 * " has been created, Version name is : "+version.getName());
			 * jcrsession.getWorkspace().getVersionManager().checkout(root.
			 * getPath());
			 */
			// System.out.println("DateTime: "+new Date()+" ::::: "+);
			Value[] actualUsers = root.getProperty(Config.EDMS_KEYWORDS).getValues();
			String newUser = "";
			for (int i = 0; i < actualUsers.length; i++) {
				String[] kWords = actualUsers[i].getString().split(",");
				for (int j = 0; j < kWords.length; j++) {
					if (kWords[j].length() > 0) {
						if (!kWords[j].equalsIgnoreCase(keyword))
							newUser += kWords[j]+",,";
					}
				}
			}
			// System.out.println("DateTime: "+new Date()+" :::::
			// "+newUser.contains(user));
			// if(newUser.contains(keyword))
			// {
			// newUser=newUser.replace(keyword, "");
			root.setProperty(Config.EDMS_KEYWORDS, new String[] { newUser });
			jcrsession.save();
			return true;
			// }
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		// return true;
	}

	public boolean editKeyword(String folderPath, String userid, String password, String editedKeyword,
			String keyword) {

		System.out.println("DateTime: " + new Date() + " ::::: " + "Editing keyword : " + keyword + " to : "
				+ folderPath + " by user : " + userid);
		SessionWrapper sessions = JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		// String sessionId=sessions.getId();
		try {
			Node root = jcrsession.getRootNode();
			root = root.getNode(folderPath.substring(1));

			/*
			 * Version
			 * version=jcrsession.getWorkspace().getVersionManager().checkin(
			 * root.getPath()); System.out.println("DateTime: "+new Date()+
			 * " ::::: "+"Version of :"+root.getPath()+
			 * " has been created, Version name is : "+version.getName());
			 * jcrsession.getWorkspace().getVersionManager().checkout(root.
			 * getPath());
			 */
			// System.out.println("DateTime: "+new Date()+" ::::: "+);
			Value[] actualUsers = root.getProperty(Config.EDMS_KEYWORDS).getValues();
			String newUser = "";
			for (int i = 0; i < actualUsers.length; i++) {
				String[] kWords = actualUsers[i].getString().split(",");
				for (int j = 0; j < kWords.length; j++) {
					if (kWords[j].length() > 0) {
						if (!kWords[j].equalsIgnoreCase(keyword)) {
							newUser += kWords[j]+",,";
						} else {
							newUser += editedKeyword+",,";
						}
					}
				}
			}
			// System.out.println("DateTime: "+new Date()+" :::::
			// "+newUser.contains(user));
			// if(newUser.contains(keyword))
			// {
			// newUser=newUser.replace(keyword, "");
			root.setProperty(Config.EDMS_KEYWORDS, new String[] { newUser });
			jcrsession.save();
			return true;
			// }
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		// return true;
	}

	public MoveDocResponse moveDoc(String srcDocPath, String destDocPath, String userid, String password) {
		System.out.println("DateTime: " + new Date() + " ::::: " + "Moving document from : " + srcDocPath + " to : "
				+ destDocPath + " by user : " + userid);
		MoveDocResponse response = new MoveDocResponse();
		response.setSuccess(false);
		if (srcDocPath != "") {
			SessionWrapper sessions = JcrRepositoryUtils.login(userid, password);
			Session jcrsession = sessions.getSession();
			try {
				if (srcDocPath.indexOf(userid) >= 0) {

					// Node
					// root=jcrsession.getRootNode().getNode(destDocPath.substring(1));
					Node destparent = jcrsession.getRootNode().getNode(destDocPath.substring(1));
					Node source = jcrsession.getRootNode()
							.getNode(srcDocPath.substring(1).substring(0, srcDocPath.substring(1).lastIndexOf("/")));
					String srcPathDoc = srcDocPath.substring(srcDocPath.lastIndexOf("/") + 1);
					if (srcPathDoc.contains(".")) {
						String fileName = srcDocPath.substring(srcDocPath.lastIndexOf("/") + 1);
						String fileNames = fileName;
						String ext = fileName.substring(fileName.lastIndexOf('.'));
						fileNames = fileName.substring(0, fileName.lastIndexOf('.'));
						while (destparent.hasNode(fileNames + ext)) {
							fileNames += "-copy";
						}
						srcPathDoc = fileNames + ext;
					} else {
						String folderName = srcDocPath.substring(srcDocPath.lastIndexOf("/") + 1);
						String folderNames = folderName;
						while (destparent.hasNode(folderNames)) {
							folderNames += "-copy";
						}
						srcPathDoc = folderNames;
					}
					Workspace ws = jcrsession.getWorkspace();
					jcrsession.save();
					source = jcrsession.getRootNode().getNode(srcDocPath.substring(1));
					removePermissionsRecursion(source, jcrsession);

					/*
					 * Version
					 * version=jcrsession.getWorkspace().getVersionManager().
					 * checkin(destparent.getPath()); System.out.println(
					 * "DateTime: "+new Date()+" ::::: "+"Version of :"
					 * +destparent.getPath()+
					 * " has been created, Version name is : "
					 * +version.getName());
					 * jcrsession.getWorkspace().getVersionManager().checkout(
					 * destparent.getPath());
					 * 
					 * version=jcrsession.getWorkspace().getVersionManager().
					 * checkin(source.getPath()); System.out.println(
					 * "DateTime: "+new Date()+" ::::: "+"Version of :"
					 * +source.getPath()+" has been created, Version name is : "
					 * +version.getName());
					 * jcrsession.getWorkspace().getVersionManager().checkout(
					 * source.getPath());
					 * 
					 */

					if (source.isNodeType(JcrConstants.MIX_SHAREABLE)) {
						ws.copy(srcDocPath, destDocPath + "/" + srcPathDoc);
						source.removeSharedSet();
						jcrsession.save();
						response.setSuccess(true);
					} else {
						try {
							ws.move(srcDocPath, destDocPath + "/" + srcPathDoc);
							response.setSuccess(true);
						} catch (Exception e) {
							/*
							 * ws.copy(srcDocPath, destDocPath+"/"+srcPathDoc);
							 * source.removeSharedSet(); jcrsession.save();
							 * VersionManager vm = jcrsession.getWorkspace().
							 * getVersionManager(); // list all versions of all
							 * nodes in the repository Node vs =
							 * jcrsession.getRootNode(). getNode("jcr:system").
							 * getNode("jcr:versionStorage"); Version v =
							 * traverseVersionStorage(vs, 0); // restore a
							 * version vm.restore(srcDocPath, v, false); // get
							 * the node and print the data t1 = s.getRootNode().
							 * getNode("test").getNode("t1");
							 * System.out.println("DateTime: "+new Date()+
							 * " ::::: "+"Restored:");
							 */}
					}
					jcrsession.save();
					Node destroot = jcrsession.getNode((destDocPath + "/" + srcPathDoc));

					assignPermissionRecursion(destroot, jcrsession, destparent,userid);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return response;
			}
		}
		return response;
	}

	/*
	 * private static Version traverseVersionStorage(Node n, int level) throws
	 * Exception { Version v = null; for (NodeIterator it = n.getNodes();
	 * it.hasNext();) { Node n2 = it.nextNode(); if (n2 instanceof Version &&
	 * !n2.getName().startsWith("jcr:")) { v = (Version) n2; System.out.println(
	 * "DateTime: "+new Date()+" ::::: "+"version " + n2.getName() + " of node "
	 * + n2.getParent().getName() + ":"); Node n3 =
	 * n2.getNode("jcr:frozenNode"); for (PropertyIterator pt =
	 * n3.getProperties(); pt.hasNext();) { Property p = pt.nextProperty(); if
	 * (!p.getName().startsWith("jcr:")) { System.out.println("DateTime: "+new
	 * Date()+" ::::: "+"  " + p.getName() + "=" + (p.isMultiple() ?
	 * p.getValues().toString() : p.getValue().getString())); } }
	 * System.out.println("DateTime: "+new Date()+" ::::: "+); } Version v2 =
	 * traverseVersionStorage(n2, level + 1); v = v == null ? v2 : v; } return
	 * v; }
	 */

	public CopyDocResponse copyDoc(String srcDocPath, String destDocPath, String userid, String password) {

		System.out.println("DateTime: " + new Date() + " ::::: " + "Copying document from : " + srcDocPath + " to : "
				+ destDocPath + " by user : " + userid);

		CopyDocResponse response = new CopyDocResponse();
		response.setSuccess(false);	
		
		if (srcDocPath != "") {
			SessionWrapper sessions = JcrRepositoryUtils.login(userid, password);
			Session jcrsession = sessions.getSession();
			try {
				Node root = jcrsession.getRootNode().getNode(destDocPath.substring(1));
				//double quotaSize=0;
				//double dbl=0;
			//	Node rty=jcrsession.getRootNode().getNode(srcDocPath.substring(1));
				//if(rty.hasProperty(Config.EDMS_SIZE)){
				// dbl=rty.getProperty(Config.EDMS_SIZE).getDouble();
				// quotaSize= checkQuotaValue(userid, dbl);
				//}
				//else{
					
				//}
				//String entry=Config.LDAP_BASE+"="+userid+","+Config.LDAP_RDN;
				//double quotaLimit=	LDAPConnection.readAttributeFromLdap(Config.LDAP_URL, entry, password, Config.LDAP_RDN);
				//if(quotaLimit > quotaSize){
				Workspace ws = jcrsession.getWorkspace();
				String srcPathDoc = srcDocPath.substring(srcDocPath.lastIndexOf("/") + 1);
				if (srcPathDoc.contains(".")) {
					String fileName = srcDocPath.substring(srcDocPath.lastIndexOf("/") + 1);
					String fileNames = fileName;
					String ext = fileName.substring(fileName.lastIndexOf('.'));
					fileNames = fileName.substring(0, fileName.lastIndexOf('.'));
					while (root.hasNode(fileNames + ext)) {
						fileNames += "-copy";
					}
					srcPathDoc = fileNames + ext;
				} else {
					String folderName = srcDocPath.substring(srcDocPath.lastIndexOf("/") + 1);
					String folderNames = folderName;
					while (root.hasNode(folderNames)) {
						folderNames += "-copy";
					}
					srcPathDoc = folderNames;
				}

				/*
				 * Version
				 * version=jcrsession.getWorkspace().getVersionManager().checkin
				 * (root.getPath()); System.out.println("DateTime: "+new Date()+
				 * " ::::: "+"Version of :"+root.getPath()+
				 * " has been created, Version name is : "+version.getName());
				 * jcrsession.getWorkspace().getVersionManager().checkout(root.
				 * getPath());
				 */

				ws.copy(srcDocPath, destDocPath + "/" + srcPathDoc);
				jcrsession.save();
				assignPermissionRecursion(jcrsession.getNode(destDocPath + "/" + srcPathDoc), jcrsession,
						jcrsession.getNode(destDocPath),userid);

				/*try{
				updateQuotaValue(userid,dbl);
				}catch(Exception e){
					e.printStackTrace();
				}*/
				//}
			} catch (Exception e) {
				e.printStackTrace();
				return response;
			}
		}
		return response;
	}
	public double updateQuotaValue(String userid,double size){

		//SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
				double quotaUsed=0.0;
				SessionFactory sessionFactory=webServiceConfig.buildSessionFactory();
				org.hibernate.Session session = sessionFactory.openSession();
				String hql = "from Quota where userId like :userid";
				try{		
				Query query = session.createQuery(hql);
				query.setParameter("userid", "%" + userid + "%");
				List<Quota> snlst = query.list();
				if(snlst.size()>0)
				{
				Quota quota=snlst.get(0);
				quota.setQuotaUsed(quota.getQuotaUsed()+size);
				session.beginTransaction();
				session.saveOrUpdate(quota);
				session.getTransaction().commit();
				session.close();
				}
				else
				{
					Quota snt=new Quota();
					snt.setQuotaUsed(size);
					snt.setUserId(userid);
					session.beginTransaction();
					session.saveOrUpdate(snt);
					session.getTransaction().commit();
					session.close();
				}
				}catch(Exception e){
			e.printStackTrace();
		}finally{
			//session.close();
			//sessionFactory.close();
		}
		return quotaUsed;	
	}
	public double checkQuotaValue(String userid, double size) {

		// SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		double quotaUsed = 0.0;
		SessionFactory sessionFactory = webServiceConfig.buildSessionFactory();

		org.hibernate.Session session = sessionFactory.openSession();
		String hql = "from Quota where userId like :userid";
		try {
			Query query = session.createQuery(hql);
			query.setParameter("userid", "%" + userid + "%");

			List<Quota> snlst = query.list();
			if (snlst.size() > 0) {
				quotaUsed = snlst.get(0).getQuotaUsed() + size;
			} else {
				return 0.0;
			}
		} catch (Exception e) {

		} finally {
			// session.close();
			// sessionFactory.close();
		}

		return quotaUsed;
	}
	public boolean addNote(String folderPath, String userid, String password, String note) {

		System.out.println("DateTime: " + new Date() + " ::::: " + "Adding note to document : " + folderPath
				+ " by user : " + userid);
		SessionWrapper sessions = JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		try {
			Node root = jcrsession.getRootNode();
			root = root.getNode(folderPath.substring(1));

			/*
			 * Version
			 * version=jcrsession.getWorkspace().getVersionManager().checkin(
			 * root.getPath()); System.out.println("DateTime: "+new Date()+
			 * " ::::: "+"Version of :"+root.getPath()+
			 * " has been created, Version name is : "+version.getName());
			 * jcrsession.getWorkspace().getVersionManager().checkout(root.
			 * getPath());
			 */

			root.setProperty(Config.EDMS_DESCRIPTION, note);
			jcrsession.save();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean checkExistDoc(String docPath, String userid, String password, String parent) {

		System.out.println("DateTime: " + new Date() + " ::::: " + "Checking existance of document : " + docPath
				+ " by user : " + userid);
		SessionWrapper sessions = JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		try {
			Node root = jcrsession.getRootNode();

			root = root.getNode(parent.substring(1));
			return root.hasNode(docPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public String createWorkspace(String userid, String password, String workspaceName) {

		SessionWrapper sessions = JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		// jcrsession.getWorkspace().createWorkspace(workspaceName);
		System.out.println();
		return "Successfully Created";
	}

	public String createEssentialFolders(String userid, String password) {

		SessionWrapper sessions = JcrRepositoryUtils.loginDAV(userid, password);
		Session jcrsession = sessions.getSession();
		System.out.println(jcrsession.getWorkspace());
		createFolderDAV("Contacts", "/" + userid, userid, password, "", "");
		createFolderDAV("calendar", "/" + userid, userid, password, "", "");
		createFolderDAV("Task", "/" + userid, userid, password, "", "");
		createFolderDAV("sharedCalendars", "/" + userid, userid, password, "", "");
		createFolderDAV("sharedContacts", "/" + userid, userid, password, "", "");
		createFolderDAV("Collected Contacts", "/" + userid + "/Contacts", userid, password, "", "");
		createFolderDAV("Personal Contacts", "/" + userid + "/Contacts", userid, password, "", "");
		// jcrsession.save();

		return "Successfully Created";
	}

}