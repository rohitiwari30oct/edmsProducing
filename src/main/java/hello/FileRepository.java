package hello;

import javax.annotation.PostConstruct;
import javax.jcr.Binary;
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
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;
import javax.jcr.query.qom.FullTextSearch;
import javax.jcr.query.qom.QueryObjectModelFactory;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.AccessControlException;
import javax.jcr.security.AccessControlList;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.AccessControlPolicy;
import javax.jcr.security.Privilege;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;
import javax.print.attribute.standard.MediaSize.NA;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.activiti.engine.impl.persistence.entity.UserIdentityManager;
import org.apache.commons.io.IOUtils;
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

import org.apache.tika.Tika;
import org.apache.tika.metadata.Office;

import com.edms.documentmodule.ArrayOfFiles;
import com.edms.documentmodule.ArrayOfFolders;
import com.edms.documentmodule.ArrayOfVCFFiles;
import com.edms.documentmodule.CreateFileResponse;
import com.edms.documentmodule.EditFileRes;
import com.edms.documentmodule.EditFileResponse;
import com.edms.documentmodule.File;
import com.edms.documentmodule.FileListReturn;
import com.edms.documentmodule.FileVersionDetail;
import com.edms.documentmodule.FilesAndFolders;
import com.edms.documentmodule.Folder;
import com.edms.documentmodule.FolderVersionDetail;
import com.edms.documentmodule.RenameFileRes;
import com.edms.documentmodule.SearchDocByDateResponse;
import com.edms.documentmodule.SearchDocByLikeResponse;
import com.edms.documentmodule.SortByPropertyRes;
import com.edms.documentmodule.VCFFileAtt;
import com.edms.documentmodule.VCFFileListReturn;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import edms.core.Config;
import edms.core.JcrRepositorySession;
import edms.core.JcrRepositoryUtils;
import edms.core.SessionWrapper;
import ezvcard.*;
import ezvcard.io.text.VCardReader;
import ezvcard.property.*;

@Component
public class FileRepository {

	/*
	 * @Autowired FileService FileService;
	 */

	// @Autowired DefaultSpringSecurityContextSource contextSource;

	@PostConstruct
	public void initData() {
		System.out.println("only single time !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ");
		
		
		//MailUtils.getMatchingHeader(arg0, arg1);
		
		
		
		//JcrRepositorySession.getSession("admin");	
	}

	public FileListReturn listFile(String name, String userid) {
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		String sessionId=sessions.getId();
		Assert.notNull(name);
		FileListReturn FileList1 = new FileListReturn();
		ArrayOfFiles Files = new ArrayOfFiles();
		try {
			/*
			 * String[]
			 * wwws=jcrsession.getWorkspace().getAccessibleWorkspaceNames(); for
			 * (int i = 0; i < wwws.length; i++) {
			 * //System.out.println(wwws[i]); }
			 */
			// registerNamespace(jcrsession, root);
			// removeUser(jcrsession, userid);
			// createUser(userid, "redhat", jcrsession, root);
			// setPolicy(jcrsession, root, userid,root.getPath(),
			// Privilege.JCR_ALL);
			// Workspace ws=jcrsession.getWorkspace();
			// ws.createWorkspace(userid);

			/*
			 * root = jcrsession.getRootNode(); if (name.length() > 1) { if
			 * (!root.hasNode(userid)) {
			 * //root=jcrsession.createFile(userid);
			 * //jcrsession.createFile(userid+"/trash"); } else { root
			 * = root.getNode(name.substring(1)); } }
			 * root=jcrsession.getRootNode();
			 */javax.jcr.query.QueryManager queryManager;
			queryManager = jcrsession.getWorkspace().getQueryManager();
			String expression = "select * from [edms:document] AS s WHERE ISDESCENDANTNODE(s,'"
					+ name
					+ "') AND ISCHILDNODE(s,'"
					+ name
					+ "') ORDER BY [NAME(s),DESC]";
			expression = "select * from [edms:document] AS s WHERE ISCHILDNODE(s,'"
					+ name + "') ORDER BY [NAME(s),DESC]";
			expression = "select * from [edms:document] AS s WHERE ISCHILDNODE(s,'"
					+ name
					+ "') and [edms:author]='"
					+ userid
					+ "'  ORDER BY s.[jcr:created] ASC";
			javax.jcr.query.Query query = queryManager.createQuery(expression,
					javax.jcr.query.Query.JCR_SQL2);

			// query.setLimit(10);
			// query.setOffset(0);
			// Execute the query and get the results ...
			javax.jcr.query.QueryResult result = query.execute();

			for (NodeIterator nit = result.getNodes(); nit.hasNext();) {
				Node node = nit.nextNode();
				/*
				 * boolean recycle=false;
				 * if(node.hasProperty(Config.EDMS_RECYCLE_DOC)){
				 * recycle=node.getProperty
				 * (Config.EDMS_RECYCLE_DOC).getBoolean(); }if(!recycle){
				 */
				// if (node.getParent().isSame(root)) {
				// if(node.getProperty(Config.EDMS_AUTHOR).getString().equals(userid))
				// {
				File File = new File();
				File = setProperties(node, File, userid,jcrsession);
				Files.getFileList().add(File);
				// }
				// }
				// }
			}	FileList1.setFileListResult(Files);
		FileList1.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			//JcrRepositoryUtils.logout(sessionId);
		}
	
		return FileList1;
	}

	public File setProperties(Node node, File file, String userid,Session jcrsession) {
		
		try {
		
			file.setFileName(node.getName());
			file.setFilePath(node.getPath());
			file.setParentFolder(node.getParent().getName());
			/*if (node.hasProperty(Config.EDMS_NAME)) {
				System.out.println("title of doc is : "
						+ node.getProperty(Config.EDMS_NAME).getString());
			}*/
			if(node.hasNode(Config.EDMS_CONTENT)){
			Node ntResourceNode = node.getNode("edms:content");
			InputStream is = ntResourceNode.getProperty("jcr:data").getBinary()
					.getStream();
			
			try {
				byte[] imageBytes = IOUtils.toByteArray(is);
				String encodedImage = org.apache.commons.codec.binary.Base64
						.encodeBase64String(imageBytes);
				file.setFileContent(encodedImage);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			}
			
			if (node.hasProperty(Config.EDMS_AUTHOR))
				file.setCreatedBy(node.getProperty(Config.EDMS_AUTHOR)
						.getString());
			if (node.hasProperty(Config.EDMS_RECYCLE_DOC))
				file.setRecycle(node.getProperty(Config.EDMS_RECYCLE_DOC)
						.getBoolean());

			if (node.hasProperty(Config.EDMS_KEYWORDS)) {
				Value[] actualUsers = node.getProperty(Config.EDMS_KEYWORDS)
						.getValues();
				for (int i = 0; i < actualUsers.length; i++) {
					file.getKeywords().add(actualUsers[i].getString());
				}
			}
			if (node.hasProperty(Config.EDMS_CREATIONDATE))
				file.setCreationDate(node.getProperty(Config.EDMS_CREATIONDATE)
						.getString());

			if (node.hasProperty(Config.EDMS_MODIFICATIONDATE))
				file.setModificationDate(node.getProperty(
						Config.EDMS_MODIFICATIONDATE).getString());

			/*
			 * if(node.hasProperty(Config.EDMS_NO_OF_DOCUMENTS)){
			 * file.setNoOfDocuments
			 * (node.getProperty(Config.EDMS_NO_OF_DOCUMENTS).getString());
			 * 
			 * } if(node.hasProperty(Config.EDMS_NO_OF_FileS)){
			 * file.setNoOfFiles
			 * (node.getProperty(Config.EDMS_NO_OF_FileS).getString()); }
			 */
			if (node.hasProperty(Config.EDMS_DESCRIPTION))
				file.setNotes(node.getProperty(Config.EDMS_DESCRIPTION)
						.getString());

			/* start mapping permissions to edms File */
			if (node.hasProperty(Config.USERS_READ)) {
				Value[] actualUsers = node.getProperty(Config.USERS_READ)
						.getValues();

				for (int i = 0; i < actualUsers.length; i++) {
					file.getUserRead().add(actualUsers[i].getString());
				}
			}
			if (node.hasProperty(Config.USERS_WRITE)) {
				Value[] actualUsers = node.getProperty(Config.USERS_WRITE)
						.getValues();
				for (int i = 0; i < actualUsers.length; i++) {
					file.getUserWrite().add(actualUsers[i].getString());
				}
			}
			if (node.hasProperty(Config.USERS_DELETE)) {
				Value[] actualUsers = node.getProperty(Config.USERS_DELETE)
						.getValues();
				for (int i = 0; i < actualUsers.length; i++) {
					file.getUserDelete().add(actualUsers[i].getString());
				}
			}
			if (node.hasProperty(Config.USERS_SECURITY)) {
				Value[] actualUsers = node.getProperty(Config.USERS_SECURITY)
						.getValues();
				for (int i = 0; i < actualUsers.length; i++) {
					file.getUserSecurity().add(actualUsers[i].getString());
				}
			}
			if (node.hasProperty(Config.GROUPS_READ)) {
				Value[] actualUsers = node.getProperty(Config.GROUPS_READ)
						.getValues();
				for (int i = 0; i < actualUsers.length; i++) {
					file.getGroupRead().add(actualUsers[i].getString());
				}
			}
			if (node.hasProperty(Config.GROUPS_WRITE)) {
				Value[] actualUsers = node.getProperty(Config.GROUPS_WRITE)
						.getValues();
				for (int i = 0; i < actualUsers.length; i++) {
					file.getGroupWrite().add(actualUsers[i].getString());
				}
			}
			if (node.hasProperty(Config.GROUPS_DELETE)) {
				Value[] actualUsers = node.getProperty(Config.GROUPS_DELETE)
						.getValues();
				for (int i = 0; i < actualUsers.length; i++) {
					file.getGroupDelete().add(actualUsers[i].getString());
				}
			}
			if (node.hasProperty(Config.GROUPS_SECURITY)) {
				Value[] actualUsers = node.getProperty(Config.GROUPS_SECURITY)
						.getValues();
				for (int i = 0; i < actualUsers.length; i++) {
					file.getGroupSecurity().add(actualUsers[i].getString());
				}
			}

			VersionHistory history = jcrsession.getWorkspace().getVersionManager().getVersionHistory(node.getPath());
			// To iterate over all versions
			VersionIterator versions = history.getAllVersions();
			while (versions.hasNext()) {
			  Version version = versions.nextVersion();
			  FileVersionDetail versionDetail=new FileVersionDetail();
			  versionDetail.setCreatedBy(userid);
			  versionDetail.setCreationDate(version.getCreated().getTime().toString());
			  String[] details=history.getVersionLabels(version);
			  if(details.length>0){
			  versionDetail.setDetails(details[0]);
			  }  versionDetail.setVersionName(version.getName());
			  versionDetail.setVersionLabel(version.getParent().getName());
			  file.getFileVersionsHistory().add(versionDetail);
			}
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file;
	}
	
	File setPropertiesWithoutStream(Node node, File file, String userid,Session jcrsession){

		
		try {
		
			file.setFileName(node.getName());
			file.setFilePath(node.getPath());
			file.setParentFolder(node.getParent().getName());
			/*if (node.hasProperty(Config.EDMS_NAME)) {
				System.out.println("title of doc is : "
						+ node.getProperty(Config.EDMS_NAME).getString());
			}*/
			if(node.hasNode(Config.EDMS_CONTENT)){/*
			Node ntResourceNode = node.getNode("edms:content");
			InputStream is = ntResourceNode.getProperty("jcr:data").getBinary()
					.getStream();
			
			try {
				byte[] imageBytes = IOUtils.toByteArray(is);
				String encodedImage = org.apache.commons.codec.binary.Base64
						.encodeBase64String(imageBytes);
				file.setFileContent(encodedImage);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			*/}
			
			if (node.hasProperty(Config.EDMS_AUTHOR))
				file.setCreatedBy(node.getProperty(Config.EDMS_AUTHOR)
						.getString());
			if (node.hasProperty(Config.EDMS_RECYCLE_DOC))
				file.setRecycle(node.getProperty(Config.EDMS_RECYCLE_DOC)
						.getBoolean());

			if (node.hasProperty(Config.EDMS_KEYWORDS)) {
				Value[] actualUsers = node.getProperty(Config.EDMS_KEYWORDS)
						.getValues();
				for (int i = 0; i < actualUsers.length; i++) {
					file.getKeywords().add(actualUsers[i].getString());
				}
			}
			if (node.hasProperty(Config.EDMS_CREATIONDATE))
				file.setCreationDate(node.getProperty(Config.EDMS_CREATIONDATE)
						.getString());

			if (node.hasProperty(Config.EDMS_MODIFICATIONDATE))
				file.setModificationDate(node.getProperty(
						Config.EDMS_MODIFICATIONDATE).getString());

			/*
			 * if(node.hasProperty(Config.EDMS_NO_OF_DOCUMENTS)){
			 * file.setNoOfDocuments
			 * (node.getProperty(Config.EDMS_NO_OF_DOCUMENTS).getString());
			 * 
			 * } if(node.hasProperty(Config.EDMS_NO_OF_FileS)){
			 * file.setNoOfFiles
			 * (node.getProperty(Config.EDMS_NO_OF_FileS).getString()); }
			 */
			if (node.hasProperty(Config.EDMS_DESCRIPTION))
				file.setNotes(node.getProperty(Config.EDMS_DESCRIPTION)
						.getString());

			/* start mapping permissions to edms File */
			if (node.hasProperty(Config.USERS_READ)) {
				Value[] actualUsers = node.getProperty(Config.USERS_READ)
						.getValues();

				for (int i = 0; i < actualUsers.length; i++) {
					file.getUserRead().add(actualUsers[i].getString());
				}
			}
			if (node.hasProperty(Config.USERS_WRITE)) {
				Value[] actualUsers = node.getProperty(Config.USERS_WRITE)
						.getValues();
				for (int i = 0; i < actualUsers.length; i++) {
					file.getUserWrite().add(actualUsers[i].getString());
				}
			}
			if (node.hasProperty(Config.USERS_DELETE)) {
				Value[] actualUsers = node.getProperty(Config.USERS_DELETE)
						.getValues();
				for (int i = 0; i < actualUsers.length; i++) {
					file.getUserDelete().add(actualUsers[i].getString());
				}
			}
			if (node.hasProperty(Config.USERS_SECURITY)) {
				Value[] actualUsers = node.getProperty(Config.USERS_SECURITY)
						.getValues();
				for (int i = 0; i < actualUsers.length; i++) {
					file.getUserSecurity().add(actualUsers[i].getString());
				}
			}
			if (node.hasProperty(Config.GROUPS_READ)) {
				Value[] actualUsers = node.getProperty(Config.GROUPS_READ)
						.getValues();
				for (int i = 0; i < actualUsers.length; i++) {
					file.getGroupRead().add(actualUsers[i].getString());
				}
			}
			if (node.hasProperty(Config.GROUPS_WRITE)) {
				Value[] actualUsers = node.getProperty(Config.GROUPS_WRITE)
						.getValues();
				for (int i = 0; i < actualUsers.length; i++) {
					file.getGroupWrite().add(actualUsers[i].getString());
				}
			}
			if (node.hasProperty(Config.GROUPS_DELETE)) {
				Value[] actualUsers = node.getProperty(Config.GROUPS_DELETE)
						.getValues();
				for (int i = 0; i < actualUsers.length; i++) {
					file.getGroupDelete().add(actualUsers[i].getString());
				}
			}
			if (node.hasProperty(Config.GROUPS_SECURITY)) {
				Value[] actualUsers = node.getProperty(Config.GROUPS_SECURITY)
						.getValues();
				for (int i = 0; i < actualUsers.length; i++) {
					file.getGroupSecurity().add(actualUsers[i].getString());
				}
			}

			VersionHistory history = jcrsession.getWorkspace().getVersionManager().getVersionHistory(node.getPath());
			// To iterate over all versions
			VersionIterator versions = history.getAllVersions();
			while (versions.hasNext()) {
			  Version version = versions.nextVersion();
			  FileVersionDetail versionDetail=new FileVersionDetail();
			  versionDetail.setCreatedBy(userid);
			  versionDetail.setCreationDate(version.getCreated().getTime().toString());
			  String[] details=history.getVersionLabels(version);
			  if(details.length>0){
			  versionDetail.setDetails(details[0]);
			  }  versionDetail.setVersionName(version.getName());
			  versionDetail.setVersionLabel(version.getParent().getName());
			  file.getFileVersionsHistory().add(versionDetail);
			}
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file;
	
	}
	File setGeneralFileProperties(Node node, File file, String userid) {
		
		try {
			file.setFileName(node.getName());
		file.setFilePath(node.getPath());
		file.setParentFolder(node.getParent().getName());
		if(node.hasProperty(Config.EDMS_SIZE)){
		file.setFileSize(node.getProperty(Config.EDMS_SIZE).getLong());
		}
		file.setModificationDate(node.getProperty(Config.EDMS_MODIFICATIONDATE).getString());
		if(node.hasNode(Config.EDMS_CONTENT)){/*
			Node ntResourceNode = node.getNode("edms:content");
			InputStream is = ntResourceNode.getProperty("jcr:data").getBinary()
					.getStream();
			
			try {
				byte[] imageBytes = IOUtils.toByteArray(is);
				String encodedImage = org.apache.commons.codec.binary.Base64
						.encodeBase64String(imageBytes);
				file.setFileContent(encodedImage);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			*/}
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
		}
		return file;
	}

	public Boolean hasChild(String FilePath, String userid) {
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		String sessionId=sessions.getId();
		boolean flag = false;
		try {
			/*
			 * jcrsession = repository.login(new
			 * SimpleCredentials(Config.EDMS_ADMIN,
			 * Config.EDMS_ADMIN.toCharArray()));
			 */
			/*
			 * jcrsession = repository.login(new SimpleCredentials(
			 * userid,"redhat".toCharArray()));
			 */
			/*
			 * if (!node.getName().equals("jcr:system") &&
			 * (!node.getProperty(JcrConstants.JCR_PRIMARYTYPE)
			 * .getString().equals(JcrConstants.NT_RESOURCE))) {
			 */
			Node root = jcrsession.getRootNode();
			if (FilePath.length() > 1) {
				root = root.getNode(FilePath.substring(1));
			}
			flag = root.hasNodes();

		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			//JcrRepositoryUtils.logout(sessionId);
		}
		return flag;
	}

	public CreateFileResponse createFile(String fileName, String parentFile,
			String userid, String keywords, String description, String is) {
		CreateFileResponse response = new CreateFileResponse();

		
		/*
		
		InputStream iss = null;
		try {
			iss = new FileInputStream(new java.io.File("D:/1.png"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
		InputStream inputStream = iss;
		OutputStream outputStream = null;
	 
		try {
			// read this file into InputStream	
		//	inputStream=IOUtils.toInputStream(IOUtils.toString(is));
			// write the inputStream to a FileOutputStream
			outputStream = 
	                    new FileOutputStream(new java.io.File("D:/3.png"));
	 
			int read = 0;
			byte[] bytes = new byte[4096];
	 
			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
	 
			System.out.println("Done!");
	 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (outputStream != null) {
				try {
					// outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	 
			}
		}
		
		byte[] imageBytes = null;
		InputStream iss1 = null;
		try {
			iss1 = new FileInputStream(new java.io.File("D:/1.png"));
			
			byte[] buffer = new byte[4096];
			int bytesRead = iss1.read(buffer);
			String page = new String(buffer, 0, bytesRead, "UTF-8");
			System.out.println(page);
			
			
			imageBytes = IOUtils.toByteArray(iss1);
			imageBytes=org.apache.commons.codec.binary.Base64.decodeBase64(org.apache.commons.codec.binary.Base64
					.encodeBase64URLSafeString(imageBytes));
			String issss=IOUtils.toString(imageBytes);
			iss1=IOUtils.toInputStream(issss);	
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}

		
		InputStream inputStream1=null;
		OutputStream outputStream1 = null;
					try {
						inputStream1=iss1;
						// read this file into InputStream	
					//	inputStream=IOUtils.toInputStream(IOUtils.toString(is));
						// write the inputStream to a FileOutputStream
						outputStream1 = 
				                    new FileOutputStream(new java.io.File("D:/4.png"));
				 
						int read = 0;
						byte[] bytes = new byte[4096];
				 
						while ((read = inputStream1.read(bytes)) != -1) {
							outputStream1.write(bytes, 0, read);
						}
				 
						System.out.println("Done!");
				 
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if (inputStream1 != null) {
							try {
								inputStream1.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						if (outputStream1 != null) {
							try {
								// outputStream.flush();
								outputStream1.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
				 
						}
					}
		
		
*/		
		
		
		
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		String sessionId=sessions.getId();
		
		
		Node file = null;
		File file1 = new File();
		try {

			Node root = jcrsession.getRootNode();
			if (parentFile.length() > 1) {
				root = root.getNode(parentFile.substring(1));
			}
			if (root.hasProperty(Config.USERS_WRITE)) {
				Value[] actualUsers = root.getProperty(Config.USERS_WRITE)
						.getValues();

				String newUsers = "";

				for (int i = 0; i < actualUsers.length; i++) {
					newUsers += actualUsers[i].getString() + ",";
				}
				if (newUsers.contains(userid)
						|| root.getProperty(Config.EDMS_AUTHOR).getString()
								.equals(userid)
						|| (root.getName().equals(userid) && (root
								.getProperty(Config.EDMS_AUTHOR).getString())
								.equals(Config.JCR_USERNAME))) {
					jcrsession.save();
					Version version = jcrsession.getWorkspace()
							.getVersionManager().checkin(root.getPath());
					System.out.println(version.getName());
					jcrsession
							.getWorkspace()
							.getVersionManager()
							.getVersionHistory(root.getPath())
							.addVersionLabel(version.getName(),
									"new child named " + fileName + " added",
									true);
					jcrsession.getWorkspace().getVersionManager()
							.checkout(root.getPath());

					file = root.addNode(fileName, Config.EDMS_DOCUMENT);

					if (root.hasProperty(Config.USERS_READ)
							&& (!root.getProperty(Config.EDMS_AUTHOR)
									.toString().equals(Config.EDMS_ADMIN))) {
						Value[] actualUser = root
								.getProperty(Config.USERS_READ).getValues();
						String newUser = "";
						for (int i = 0; i < actualUser.length; i++) {
							newUser += actualUser[i].getString() + ",";
						}
						file.setProperty(Config.USERS_READ, newUser.split(","));
					} else {
						file.setProperty(Config.USERS_READ, new String[] {});
					}
					if (root.hasProperty(Config.USERS_WRITE)
							&& !root.getProperty(Config.EDMS_AUTHOR).toString()
									.equals(Config.EDMS_ADMIN)) {
						Value[] actualUser = root.getProperty(
								Config.USERS_WRITE).getValues();
						String newUser = "";
						for (int i = 0; i < actualUser.length; i++) {
							newUser += actualUser[i].getString() + ",";
						}
						file.setProperty(Config.USERS_WRITE, newUser.split(","));
					} else {
						file.setProperty(Config.USERS_WRITE, new String[] {});
					}

					if (root.hasProperty(Config.USERS_DELETE)
							&& !root.getProperty(Config.EDMS_AUTHOR).toString()
									.equals(Config.EDMS_ADMIN)) {
						Value[] actualUser = root.getProperty(
								Config.USERS_DELETE).getValues();
						String newUser = "";
						for (int i = 0; i < actualUser.length; i++) {
							newUser += actualUser[i].getString() + ",";
						}
						file.setProperty(Config.USERS_DELETE,
								newUser.split(","));
					} else {
						file.setProperty(Config.USERS_DELETE, new String[] {});
					}

					if (root.hasProperty(Config.USERS_SECURITY)
							&& !root.getProperty(Config.EDMS_AUTHOR).toString()
									.equals(Config.EDMS_ADMIN)) {
						Value[] actualUser = root.getProperty(
								Config.USERS_SECURITY).getValues();
						String newUser = "";
						for (int i = 0; i < actualUser.length; i++) {
							newUser += actualUser[i].getString() + ",";
						}
						file.setProperty(Config.USERS_SECURITY,
								newUser.split(","));
					} else {
						file.setProperty(Config.USERS_SECURITY, new String[] {});
					}

					if (root.hasProperty(Config.GROUPS_READ)
							&& !root.getProperty(Config.EDMS_AUTHOR).toString()
									.equals(Config.EDMS_ADMIN)) {
						Value[] actualUser = root.getProperty(
								Config.GROUPS_READ).getValues();
						String newUser = "";
						for (int i = 0; i < actualUser.length; i++) {
							newUser += actualUser[i].getString() + ",";
						}
						file.setProperty(Config.GROUPS_READ, newUser.split(","));
					} else {
						file.setProperty(Config.GROUPS_READ, new String[] {});
					}

					if (root.hasProperty(Config.GROUPS_WRITE)
							&& !root.getProperty(Config.EDMS_AUTHOR).toString()
									.equals(Config.EDMS_ADMIN)) {
						Value[] actualUser = root.getProperty(
								Config.GROUPS_WRITE).getValues();
						String newUser = "";
						for (int i = 0; i < actualUser.length; i++) {
							newUser += actualUser[i].getString() + ",";
						}
						file.setProperty(Config.GROUPS_WRITE,
								newUser.split(","));
					} else {
						file.setProperty(Config.GROUPS_WRITE, new String[] {});
					}

					if (root.hasProperty(Config.GROUPS_DELETE)
							&& !root.getProperty(Config.EDMS_AUTHOR).toString()
									.equals(Config.EDMS_ADMIN)) {
						Value[] actualUser = root.getProperty(
								Config.GROUPS_DELETE).getValues();
						String newUser = "";
						for (int i = 0; i < actualUser.length; i++) {
							newUser += actualUser[i].getString() + ",";
						}
						file.setProperty(Config.GROUPS_DELETE,
								newUser.split(","));
					} else {
						file.setProperty(Config.GROUPS_DELETE, new String[] {});
					}

					if (root.hasProperty(Config.GROUPS_SECURITY)
							&& !root.getProperty(Config.EDMS_AUTHOR).toString()
									.equals(Config.EDMS_ADMIN)) {
						Value[] actualUser = root.getProperty(
								Config.GROUPS_SECURITY).getValues();
						String newUser = "";
						for (int i = 0; i < actualUser.length; i++) {
							newUser += actualUser[i].getString() + ",";
						}
						file.setProperty(Config.GROUPS_SECURITY,
								newUser.split(","));
					} else {
						file.setProperty(Config.GROUPS_SECURITY,
								new String[] {});
					}

					file.setProperty(Config.EDMS_KEYWORDS, keywords.split(","));
					file.setProperty(Config.EDMS_NAME, fileName);
					if (root.getProperty(Config.EDMS_AUTHOR).getString()
							.equals(Config.EDMS_ADMIN)) {

						file.setProperty(Config.EDMS_AUTHOR, userid);

					} else {
						file.setProperty(Config.EDMS_AUTHOR,
								root.getProperty(Config.EDMS_AUTHOR)
										.getString());
					}
						file.setProperty(Config.EDMS_DESCRIPTION, is);
					SimpleDateFormat format = new SimpleDateFormat(
							"YYYY-MM-dd'T'HH:mm:ss.SSSZ");

					file.setProperty(Config.EDMS_CREATIONDATE,
							(format.format(new Date())).toString().replace("+0530", "Z"));
					file.setProperty(Config.EDMS_MODIFICATIONDATE,
							"");
					file.setProperty(Config.EDMS_ACCESSDATE,
							"");
					file.setProperty(Config.EDMS_DOWNLOADDATE,
							"");
					file.setProperty(Config.EDMS_RECYCLE_DOC, false);
					file.addMixin(JcrConstants.MIX_SHAREABLE);
					file.addMixin(JcrConstants.MIX_VERSIONABLE);
					String iss;
					InputStream	isss=null;
					byte[] encodedImage = org.apache.commons.codec.binary.Base64
							.decodeBase64(is);
						try {
							isss=IOUtils.toInputStream(IOUtils.toString(encodedImage));
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

						try {
							file.setProperty(Config.EDMS_SIZE,isss.available() );
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					ValueFactory valueFactory = jcrsession.getValueFactory();
					Binary myBinary = valueFactory.createBinary(isss);
					file.addMixin("mix:referenceable");
					Node resNode = file
							.addNode("edms:content", "edms:resource");
					Tika tika = new Tika();
					String mimeType;
					try {
						mimeType = tika.detect(isss);
						System.out.println("MimeType of the Uploading File is "+mimeType);
						resNode.setProperty("jcr:mimeType", mimeType);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					resNode.setProperty("jcr:data",myBinary);
					Calendar lastModified = Calendar.getInstance();
					lastModified
							.setTimeInMillis(lastModified.getTimeInMillis());
					resNode.setProperty("jcr:lastModified", lastModified);
					jcrsession.save();
					root.setProperty(
							Config.EDMS_NO_OF_DOCUMENTS,
							Integer.parseInt(root.getProperty(
									Config.EDMS_NO_OF_DOCUMENTS).getString()) + 1);
					jcrsession.save();
					file1 = setProperties(file, file1, userid,jcrsession);
					response.setFile(file1);
					response.setSuccess(true);
				} else {
					response.setSuccess(false);
					System.out.println("you have not permission to add child node");
				}
			}
		} catch (Exception e) {
			response.setSuccess(false);
			e.printStackTrace();
		}finally{
			//JcrRepositoryUtils.logout(sessionId);
		}
		return response;
	}

	public File getFileByPath(String FilePath, String userid) {
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		String sessionId=sessions.getId();
		Repository repository = new TransientRepository();
		// Session jcrsession = null;
		File File1 = new File();
		try {
			Node root = jcrsession.getRootNode();
			if (FilePath.length() > 1) {
				root = root.getNode(FilePath.substring(1));
			}
			SimpleDateFormat format = new SimpleDateFormat(
					"YYYY-MM-dd'T'HH:mm:ss.SSSZ");
			/*jcrsession.getWorkspace().getVersionManager().checkout(root.getPath());
			
			root.setProperty(Config.EDMS_ACCESSDATE,
					(format.format(new Date())).toString().replace("+0530", "Z"));
			root.setProperty(Config.EDMS_DOWNLOADDATE,
					(format.format(new Date())).toString().replace("+0530", "Z"));
			jcrsession.save();*/
			File1.setFileName(root.getName().toString());
			File1.setFilePath(root.getPath().toString());
			if (root.getPath().toString().length() > 1) {
				if (FilePath.length() > 1) {
					if (root.getProperty(Config.EDMS_AUTHOR).getString()
							.equals(userid)
							|| (root.getName().equals(userid) && (root
									.getProperty(Config.EDMS_AUTHOR)
									.getString()).equals(Config.JCR_USERNAME))) {
						setProperties(root, File1, userid,jcrsession);
						//System.out.println(File1.getFileContent());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			//JcrRepositoryUtils.logout(sessionId);
		}
		return File1;
	}

	public void shareFileRecursion(Node rt, String userpermissions,
			String grouppermissions, String users, String groups) {
		try {
			if (userpermissions.contains("1")) {

				rt.setProperty(Config.USERS_READ, new String[] { users });
			}
			if (userpermissions.contains("2")) {
				rt.setProperty(Config.USERS_WRITE, new String[] { users });
			}
			if (userpermissions.contains("4")) {
				rt.setProperty(Config.USERS_DELETE, new String[] { users });
			}
			if (userpermissions.contains("true")) {
				rt.setProperty(Config.USERS_SECURITY, new String[] { users });
			}

			if (grouppermissions.contains("1")) {
				rt.setProperty(Config.GROUPS_READ, new String[] { groups });
			}
			if (grouppermissions.contains("2")) {
				rt.setProperty(Config.GROUPS_WRITE, new String[] { groups });
			}
			if (grouppermissions.contains("4")) {
				rt.setProperty(Config.GROUPS_DELETE, new String[] { groups });
			}
			if (grouppermissions.contains("true")) {
				rt.setProperty(Config.GROUPS_SECURITY, new String[] { groups });
			}

			//jcrsession.save();
			if (rt.hasNodes()) {
				for (NodeIterator nit = rt.getNodes(); nit.hasNext();) {
					Node root = nit.nextNode();
					shareFileRecursion(root, userpermissions, grouppermissions,
							users, groups);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void assignSinglePermissionRecursion(Node rt, String userid,
			String user, String value) {
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		String sessionId=sessions.getId();
		try {
			if(value.equals("ur")){
				Value[] actualUsers = rt.getProperty(Config.USERS_READ)
						.getValues();
				String newUser = "";
				for (int i = 0; i < actualUsers.length; i++) {
					newUser += actualUsers[i].getString() + ",";
				}
				// System.out.println(newUser.contains(user));
				if (!newUser.contains(user)) {

					newUser += user + ",";
					rt.setProperty(Config.USERS_READ, new String[] { newUser });
				}
				jcrsession.save();
			}else if(value.equals("uw")){
				
				
				Value[] actualUsers = rt.getProperty(Config.USERS_READ)
						.getValues();
				String newUser = "";
				for (int i = 0; i < actualUsers.length; i++) {
					newUser += actualUsers[i].getString() + ",";
				}
				// System.out.println(newUser.contains(user));
				if (!newUser.contains(user)) {

					newUser += user + ",";
					rt.setProperty(Config.USERS_READ, new String[] { newUser });
				}
			
				
				
				 actualUsers = rt.getProperty(Config.USERS_WRITE)
						.getValues();
				 newUser = "";
				for (int i = 0; i < actualUsers.length; i++) {
					newUser += actualUsers[i].getString() + ",";
				}
				// System.out.println(newUser.contains(user));
				if (!newUser.contains(user)) {

					newUser += user + ",";
					rt.setProperty(Config.USERS_WRITE, new String[] { newUser });
				}
				jcrsession.save();
			}
			
			
			/*
			switch (value) {
			
			case "ur": {
				Value[] actualUsers = rt.getProperty(Config.USERS_READ)
						.getValues();
				String newUser = "";
				for (int i = 0; i < actualUsers.length; i++) {
					newUser += actualUsers[i].getString() + ",";
				}
				// System.out.println(newUser.contains(user));
				if (!newUser.contains(user)) {

					newUser += user + ",";
					rt.setProperty(Config.USERS_READ, new String[] { newUser });
				}
				jcrsession.save();
				break;
			}
			case "uw": {
				Value[] actualUsers = rt.getProperty(Config.USERS_WRITE)
						.getValues();
				String newUser = "";
				for (int i = 0; i < actualUsers.length; i++) {
					newUser += actualUsers[i].getString() + ",";
				}
				// System.out.println(newUser.contains(user));
				if (!newUser.contains(user)) {

					newUser += user + ",";
					rt.setProperty(Config.USERS_WRITE, new String[] { newUser });
				}
				jcrsession.save();
				break;
			}
			case "ud": {
				Value[] actualUsers = rt.getProperty(Config.USERS_DELETE)
						.getValues();
				String newUser = "";
				for (int i = 0; i < actualUsers.length; i++) {
					newUser += actualUsers[i].getString() + ",";
				}
				// System.out.println(newUser.contains(user));
				if (!newUser.contains(user)) {

					newUser += user + ",";
					rt.setProperty(Config.USERS_DELETE,
							new String[] { newUser });
				}
				jcrsession.save();
				break;
			}
			case "us": {
				Value[] actualUsers = rt.getProperty(Config.USERS_SECURITY)
						.getValues();
				String newUser = "";
				for (int i = 0; i < actualUsers.length; i++) {
					newUser += actualUsers[i].getString() + ",";
				}
				// System.out.println(newUser.contains(user));
				if (!newUser.contains(user)) {
					newUser += user + ",";
					rt.setProperty(Config.USERS_SECURITY,
							new String[] { newUser });
				}
				jcrsession.save();
				break;
			}
			case "nur": {
				Value[] actualUsers = rt.getProperty(Config.USERS_READ)
						.getValues();
				String newUser = "";
				for (int i = 0; i < actualUsers.length; i++) {
					newUser += actualUsers[i].getString() + ",";
				}
				// System.out.println(newUser.contains(user));
				if (newUser.contains(user)) {
					newUser = newUser.replace(user, "");
					rt.setProperty(Config.USERS_READ, new String[] { newUser });
					jcrsession.save();
				}

				actualUsers = rt.getProperty(Config.USERS_WRITE).getValues();
				newUser = "";
				for (int i = 0; i < actualUsers.length; i++) {
					newUser += actualUsers[i].getString() + ",";
				}
				// System.out.println(newUser.contains(user));
				if (newUser.contains(user)) {
					newUser = newUser.replace(user, "");
					rt.setProperty(Config.USERS_WRITE, new String[] { newUser });
					jcrsession.save();
				}

				actualUsers = rt.getProperty(Config.USERS_DELETE).getValues();
				newUser = "";
				for (int i = 0; i < actualUsers.length; i++) {
					newUser += actualUsers[i].getString() + ",";
				}
				// System.out.println(newUser.contains(user));
				if (newUser.contains(user)) {
					newUser = newUser.replace(user, "");
					rt.setProperty(Config.USERS_DELETE,
							new String[] { newUser });
					jcrsession.save();
				}

				actualUsers = rt.getProperty(Config.USERS_SECURITY).getValues();
				newUser = "";
				for (int i = 0; i < actualUsers.length; i++) {
					newUser += actualUsers[i].getString() + ",";
				}
				// System.out.println(newUser.contains(user));
				if (newUser.contains(user)) {
					newUser = newUser.replace(user, "");
					rt.setProperty(Config.USERS_SECURITY,
							new String[] { newUser });
					jcrsession.save();
				}
				break;
			}
			case "nuw": {
				Value[] actualUsers = rt.getProperty(Config.USERS_WRITE)
						.getValues();
				String newUser = "";
				for (int i = 0; i < actualUsers.length; i++) {
					newUser += actualUsers[i].getString() + ",";
				}
				// System.out.println(newUser.contains(user));
				if (newUser.contains(user)) {
					newUser = newUser.replace(user, "");
					rt.setProperty(Config.USERS_WRITE, new String[] { newUser });
					jcrsession.save();
				}
				break;
			}
			case "nud": {
				Value[] actualUsers = rt.getProperty(Config.USERS_DELETE)
						.getValues();
				String newUser = "";
				for (int i = 0; i < actualUsers.length; i++) {
					newUser += actualUsers[i].getString() + ",";
				}
				// System.out.println(newUser.contains(user));
				if (newUser.contains(user)) {
					newUser = newUser.replace(user, "");
					rt.setProperty(Config.USERS_DELETE,
							new String[] { newUser });
					jcrsession.save();
				}
				break;
			}
			case "nus": {
				Value[] actualUsers = rt.getProperty(Config.USERS_SECURITY)
						.getValues();
				String newUser = "";
				for (int i = 0; i < actualUsers.length; i++) {
					newUser += actualUsers[i].getString() + ",";
				}
				// System.out.println(newUser.contains(user));
				if (newUser.contains(user)) {
					newUser = newUser.replace(user, "");
					rt.setProperty(Config.USERS_SECURITY,
							new String[] { newUser });
					jcrsession.save();
				}
				break;
			}
			case "gr": {
				Value[] actualUsers = rt.getProperty(Config.GROUPS_READ)
						.getValues();
				String newUser = "";
				for (int i = 0; i < actualUsers.length; i++) {
					newUser += actualUsers[i].getString() + ",";
				}
				// System.out.println(newUser.contains(user));
				if (!newUser.contains(user)) {
					newUser += user + ",";
					rt.setProperty(Config.GROUPS_READ, new String[] { newUser });
				}
				jcrsession.save();
				break;
			}
			case "gw": {
				Value[] actualUsers = rt.getProperty(Config.GROUPS_WRITE)
						.getValues();
				String newUser = "";
				for (int i = 0; i < actualUsers.length; i++) {
					newUser += actualUsers[i].getString() + ",";
				}
				// System.out.println(newUser.contains(user));
				if (!newUser.contains(user)) {
					newUser += user + ",";
					rt.setProperty(Config.GROUPS_WRITE,
							new String[] { newUser });
				}
				jcrsession.save();
				break;
			}
			case "gd": {
				Value[] actualUsers = rt.getProperty(Config.GROUPS_DELETE)
						.getValues();
				String newUser = "";
				for (int i = 0; i < actualUsers.length; i++) {
					newUser += actualUsers[i].getString() + ",";
				}
				// System.out.println(newUser.contains(user));
				if (!newUser.contains(user)) {
					newUser += user + ",";
					rt.setProperty(Config.GROUPS_DELETE,
							new String[] { newUser });
				}
				jcrsession.save();
				break;
			}
			case "gs": {
				Value[] actualUsers = rt.getProperty(Config.GROUPS_SECURITY)
						.getValues();
				String newUser = "";
				for (int i = 0; i < actualUsers.length; i++) {
					newUser += actualUsers[i].getString() + ",";
				}
				// System.out.println(newUser.contains(user));
				if (!newUser.contains(user)) {
					newUser += user + ",";
					rt.setProperty(Config.GROUPS_SECURITY,
							new String[] { newUser });
				}
				jcrsession.save();
				break;
			}
			case "ngr": {
				Value[] actualUsers = rt.getProperty(Config.GROUPS_READ)
						.getValues();
				String newUser = "";
				for (int i = 0; i < actualUsers.length; i++) {
					newUser += actualUsers[i].getString() + ",";
				}
				// System.out.println(newUser.contains(user));
				if (newUser.contains(user)) {
					newUser = newUser.replace(user, "");
					rt.setProperty(Config.GROUPS_READ, new String[] { newUser });
					jcrsession.save();
				}
				break;
			}
			case "ngw": {
				Value[] actualUsers = rt.getProperty(Config.GROUPS_WRITE)
						.getValues();
				String newUser = "";
				for (int i = 0; i < actualUsers.length; i++) {
					newUser += actualUsers[i].getString() + ",";
				}
				// System.out.println(newUser.contains(user));
				if (newUser.contains(user)) {
					newUser = newUser.replace(user, "");
					rt.setProperty(Config.GROUPS_WRITE,
							new String[] { newUser });
					jcrsession.save();
				}
				break;
			}
			case "ngd": {
				Value[] actualUsers = rt.getProperty(Config.GROUPS_DELETE)
						.getValues();
				String newUser = "";
				for (int i = 0; i < actualUsers.length; i++) {
					newUser += actualUsers[i].getString() + ",";
				}
				// System.out.println(newUser.contains(user));
				if (newUser.contains(user)) {
					newUser = newUser.replace(user, "");
					rt.setProperty(Config.GROUPS_DELETE,
							new String[] { newUser });
					jcrsession.save();
				}
				break;
			}
			case "ngs": {
				Value[] actualUsers = rt.getProperty(Config.GROUPS_SECURITY)
						.getValues();
				String newUser = "";
				for (int i = 0; i < actualUsers.length; i++) {
					newUser += actualUsers[i].getString() + ",";
				}
				// System.out.println(newUser.contains(user));
				if (newUser.contains(user)) {
					newUser = newUser.replace(user, "");
					rt.setProperty(Config.GROUPS_SECURITY,
							new String[] { newUser });
					jcrsession.save();
				}
				break;
			}
			default:
				break;
			}*/

			if (rt.hasNodes()) {
				for (NodeIterator nit = rt.getNodes(); nit.hasNext();) {
					Node root = nit.nextNode();
					assignSinglePermissionRecursion(root, userid, user, value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			//JcrRepositoryUtils.logout(sessionId);
		}
	}

	public String assignSinglePermission(String FilePath, String userid,
			String user, String value) {
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		String sessionId=sessions.getId();
		try {
			Node root = jcrsession.getRootNode();
			if (FilePath.length() > 1) {
				root = root.getNode(FilePath.substring(1));
			}

			if (root.getProperty(Config.EDMS_AUTHOR).getString().equals(userid)) {
				// System.out.println(":before assigning permissions like "+value
				// +" to user : "+user+" on "+root.getName());
				assignSinglePermissionRecursion(root, userid, user, value);
				// System.out.println(":value contains n "+value.contains("n"));
				if (!value.contains("n") && value.contains("r")) {

					Workspace ws = jcrsession.getWorkspace();
					Node rty = jcrsession.getRootNode();
					if (!rty.hasNode(user + "/" + root.getName())) {
						ws.clone(ws.getName(), root.getPath(), "/" + user + "/"
								+ root.getName(), false);
					} else {
						// System.out.println("already exist");
					}
				} else {
					if (value.contains("r")) {
						Node remov = jcrsession.getRootNode().getNode(
								user + "/" + root.getName());
						remov.remove();
					}
				}
				jcrsession.save();
			} else {
				return "sorry you don't have permissions to share this File";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "failure";
		} finally{
			//JcrRepositoryUtils.logout(sessionId);
		}
		return "Success";
	}

	public String shareFileByPath(String FilePath, String userid, String users,
			String groups, String userpermissions, String grouppermissions) {
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		String sessionId=sessions.getId();
		// Repository repository = new TransientRepository();
		// File File1 = new File();
		// Session jcrsession=null;
		try {
			/*
			 * jcrsession = repository.login(new
			 * SimpleCredentials(Config.EDMS_ADMIN,
			 * Config.EDMS_ADMIN.toCharArray()));
			 */
			/*
			 * jcrsession = repository.login(new SimpleCredentials(
			 * userid,"redhat".toCharArray()));
			 */

			Node root = jcrsession.getRootNode();
			if (FilePath.length() > 1) {
				root = root.getNode(FilePath.substring(1));
			}

			if (root.getProperty(Config.EDMS_AUTHOR).getString().equals(userid)) {
				shareFileRecursion(root, userpermissions, grouppermissions,
						users, groups);

				Workspace ws = jcrsession.getWorkspace();

				for (String user : users.split(",")) {
					if (!user.equals(userid)) {
						Node rty = jcrsession.getRootNode();
						if (!rty.hasNode(user + "/" + root.getName())) {
							ws.clone(ws.getName(), root.getPath(), "/" + user
									+ "/" + root.getName(), false);
						} else {
							// System.out.println("already exist");
						}
					}
				}
				jcrsession.save();
			} else {
				return "sorry you don't have permissions to share this File";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "failure";
		}finally{
			//JcrRepositoryUtils.logout(sessionId);
		}
		return "Success";
	}

	/*
	 * public boolean shareFileByPath(Session jcrsession,String FilePath, String
	 * userid, String users,String groups,String userpermissions,String
	 * grouppermissions) {
	 * 
	 * try { Node root = jcrsession.getRootNode();
	 * if(userid!=Config.EDMS_ADMIN){ root=root.getNode(userid); } if
	 * (FilePath.length() > 1) { root = root.getNode(FilePath.substring(1)); }
	 * //Workspace ws = jcrsession.getWorkspace(); //System.out
	 * .println(repository.OPTION_SHAREABLE_NODES_SUPPORTED +
	 * " shareable or not  " + root.getPath().toString() +
	 * "^^^^^^^^^^^^^^^^^ sharing to " + "/santosh@avi-oil.com/" +
	 * root.getPath() .toString() .substring( root.getPath().toString()
	 * .lastIndexOf("/")));//ws.clone(ws.getName(), "/sanjay", "/sanjay",
	 * false); //jcrsession.save(); } catch (LoginException e) {
	 * e.printStackTrace(); return false; } catch (RepositoryException e) { //
	 * TODO Auto-generated catch block e.printStackTrace(); return false; }
	 * return true; }
	 */

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

	/*public User createUser(String userid, String password, Session jcrsession,
			Node root) {
		User user = null;
		try {
			JackrabbitSession js = (JackrabbitSession) jcrsession;
			UserManager userManager = js.getUserManager();
			user = userManager.createUser(userid, password);
			root = jcrsession.getRootNode();
			setPolicy(jcrsession, root, userid, root.getPath(),
					Privilege.JCR_ALL);
			Node userNode = root.addNode(userid, Config.EDMS_DOCUMENT);
			userNode.setProperty(Config.USERS_READ, new String[] { userid });
			userNode.setProperty(Config.USERS_WRITE, new String[] { userid });
			userNode.setProperty(Config.USERS_DELETE, new String[] { userid });
			userNode.setProperty(Config.USERS_SECURITY, new String[] { userid });
			userNode.setProperty(Config.EDMS_KEYWORDS, new String[] { "user",
					"root", "node" });
			userNode.setProperty(Config.EDMS_AUTHOR, userid);
			userNode.setProperty(Config.EDMS_CREATIONDATE,
					(new Date()).toString());
			userNode.setProperty(Config.EDMS_MODIFICATIONDATE,
					(new Date()).toString());
			// userNode.setProperty(Config.EDMS_NO_OF_FileS, 0);
			// userNode.setProperty(Config.EDMS_NO_OF_DOCUMENTS, 0);
			userNode.setProperty(Config.EDMS_RECYCLE_DOC, false);
			userNode.addMixin(JcrConstants.MIX_SHAREABLE);
			userNode.addMixin(JcrConstants.MIX_VERSIONABLE);
			jcrsession.save();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return user;
	}*/

	public Group createGroup(String userid, String groupName,
			Session jcrsession, Node root) {
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
		}finally{
		}
		return group;
	}


	/*public String getPermissions(User user, String path, Session jcrsession) {
		String permissions = "";
		try {

			Node root = jcrsession.getRootNode();
			JackrabbitSession js = (JackrabbitSession) jcrsession;
			AccessControlManager aMgr;
			aMgr = jcrsession.getAccessControlManager();
			// get supported privileges of any node
			
			 * Privilege[] privileges = aMgr
			 * .getSupportedPrivileges(root.getPath()); for (int i = 0; i <
			 * privileges.length; i++) { //System.out.println(privileges[i]); }
			 

			// get now applied privileges on a node
			Privilege[] privileges = aMgr.getPrivileges(root.getPath());
			for (int i = 0; i < privileges.length; i++) {
				// System.out.println(privileges[i]);
				permissions += privileges[i];
			}
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return permissions;
	}*/

	public void setPolicy(Session jcrsession, Node root, String userid,
			String path, String permission) {
		try {
			root = jcrsession.getRootNode();
			JackrabbitSession js = (JackrabbitSession) jcrsession;
			User user = ((User) js.getUserManager().getAuthorizable(userid));

			AccessControlManager aMgr = jcrsession.getAccessControlManager();

			// get supported privileges of any node
			/*
			 * Privilege[] privileges = aMgr .getSupportedPrivileges(path); for
			 * (int i = 0; i < privileges.length; i++) {
			 * //System.out.println(privileges[i]); }
			 */

			// get now applied privileges on a node
			/*
			 * privileges = aMgr.getPrivileges(path); for (int i = 0; i <
			 * privileges.length; i++) { //System.out.println(privileges[i]); }
			 */
			String[] percol = permission.split(",");
			// create a privilege set with jcr:all
			Privilege[] setprivilege = new Privilege[percol.length];
			for (int i = 0; i < percol.length; i++) {
				setprivilege[i] = aMgr.privilegeFromName(percol[i]);
			}
			// privileges = new Privilege[] {
			// aMgr.privilegeFromName(Privilege.JCR_READ) };
			AccessControlList acl;
			try {
				// get first applicable policy (for nodes w/o a policy)
				acl = (AccessControlList) aMgr.getApplicablePolicies(path)
						.nextAccessControlPolicy();
			} catch (NoSuchElementException e) {
				// e.printStackTrace();
				// else node already has a policy, get that one
				acl = (AccessControlList) aMgr.getPolicies(path)[0];
			}
			// remove all existing entries
			for (AccessControlEntry e : acl.getAccessControlEntries()) {
				acl.removeAccessControlEntry(e);
			}
			/*
			 * Group grp = ((Group) js.getUserManager().getAuthorizable(
			 * "top-management")); //System.out.println("group path is : " +
			 * grp.getID() + " user id is : " + user.getID());
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

			// System.out.println("before set policy on : "+path+" to user : "+userid);
			for (int i = 0; i < setprivilege.length; i++) {
				// System.out.println(setprivilege[i]);
			}

			jcrsession.save();
			jcrsession.refresh(true);
			setprivilege = aMgr.getPrivileges(path);
			// System.out.println("after set policy on : "+path+" to user : "+userid);
			for (int i = 0; i < setprivilege.length; i++) {
				// System.out.println(setprivilege[i]);
			}

		} catch (RepositoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	

	public FileListReturn listSharedFile(String userid) {
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		String sessionId=sessions.getId();
		FileListReturn FileList1 = new FileListReturn();
		ArrayOfFiles Files = new ArrayOfFiles();
		Node root = null;

		Repository repository = new TransientRepository();
		// Session jcrsession = null;
		try {
			/*
			 * jcrsession = repository.login(new
			 * SimpleCredentials(Config.EDMS_ADMIN,
			 * Config.EDMS_ADMIN.toCharArray()));
			 */
			/*
			 * jcrsession = repository.login(new SimpleCredentials(
			 * userid,"redhat".toCharArray()));
			 */
			/*
			 * String[] wwws=
			 * jcrsession.getWorkspace().getAccessibleWorkspaceNames(); for (int
			 * i = 0; i < wwws.length; i++) { //System.out.println(wwws[i]); }
			 */
			root = jcrsession.getRootNode();
			root = root.getNode(userid);
			for (NodeIterator nit = root.getNodes(); nit.hasNext();) {
				Node node = nit.nextNode();
				/*
				 * boolean recycle=false;
				 * if(node.hasProperty(Config.EDMS_RECYCLE_DOC)){
				 * recycle=node.getProperty
				 * (Config.EDMS_RECYCLE_DOC).getBoolean(); }if(!recycle){
				 */
				if (!node.getProperty(Config.EDMS_AUTHOR).getString()
						.equals(userid)) {
					if (Config.EDMS_DOCUMENT.equals(node.getPrimaryNodeType()
							.getName())) {

						if (node.hasProperty(Config.USERS_READ)) {
							Value[] actualUsers = node.getProperty(
									Config.USERS_READ).getValues();
							String newUser = "";
							for (int i = 0; i < actualUsers.length; i++) {
								newUser += actualUsers[i].getString() + ",";
							}
							// System.out.println("for node : "+node.getPath().toString()+" newUser contains "+node.getProperty(Config.EDMS_AUTHOR).getString()+" is "+newUser.contains(userid));
							if (newUser.contains(userid)) {
								File File = new File();
								File = setProperties(node, File, userid,jcrsession);
								Files.getFileList().add(File);
							}
						}
					}
				}
			}
			// }FileList1.setFileListResult(Files);
		FileList1.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			//JcrRepositoryUtils.logout(sessionId);
		}
		
		return FileList1;
	}

	public FileListReturn listSharedFile(String userid, String path) {
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		String sessionId=sessions.getId();
		FileListReturn FileList1 = new FileListReturn();
		ArrayOfFiles Files = new ArrayOfFiles();
		Node root = null;
		try {
			root = jcrsession.getRootNode();
			root = root.getNode(path.substring(1));
			for (NodeIterator nit = root.getNodes(); nit.hasNext();) {
				Node node = nit.nextNode();
				/*
				 * boolean recycle=false;
				 * if(node.hasProperty(Config.EDMS_RECYCLE_DOC)){
				 * recycle=node.getProperty
				 * (Config.EDMS_RECYCLE_DOC).getBoolean(); }if(!recycle){
				 */
				if (!node.getProperty(Config.EDMS_AUTHOR).getString()
						.equals(userid)) {
					if (Config.EDMS_DOCUMENT.equals(node.getPrimaryNodeType()
							.getName())) {

						if (node.hasProperty(Config.USERS_READ)) {
							Value[] actualUsers = node.getProperty(
									Config.USERS_READ).getValues();
							String newUser = "";
							for (int i = 0; i < actualUsers.length; i++) {
								newUser += actualUsers[i].getString() + ",";
							}
							// System.out.println("for node : "+node.getPath().toString()+" newUser contains "+node.getProperty(Config.EDMS_AUTHOR).getString()+" is "+newUser.contains(userid));
							if (newUser.contains(userid)) {
								File File = new File();
								File = setProperties(node, File, userid,jcrsession);
								Files.getFileList().add(File);
							}
						}
					}
				}
			}
			// }FileList1.setFileListResult(Files);
		FileList1.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			//JcrRepositoryUtils.logout(sessionId);
		}
		
		return FileList1;
	}


/*	public String recycleFile(String FilePath, String userid) {
		String response = "";
		try {
			Node root = jcrsession.getRootNode();
			root = root.getNode(FilePath.substring(1));
			if (root.hasProperty(Config.USERS_DELETE)) {
				Value[] actualUsers = root.getProperty(Config.USERS_DELETE)
						.getValues();
				String newUser = "";
				for (int i = 0; i < actualUsers.length; i++) {
					newUser += actualUsers[i].getString() + ",";
				}
				if (newUser.contains(userid)
						|| root.getProperty(Config.EDMS_AUTHOR).getString()
								.equals(userid)) {
					recycleFileRecursion(root, userid);
					response = "true";
				} else {
					response = "false";
				}
			}
		} catch (RepositoryException e) {
			response = "false";
			e.printStackTrace();
		}
		return response;
	}

	public void recycleFileRecursion(Node root, String userid) {
		try {
			Node parent = root.getParent();
			// root.setProperty(Config.EDMS_RECYCLE_DOC, true);
			root.setProperty(Config.EDMS_RESTORATION_PATH, root.getPath());
			jcrsession.save();
			jcrsession.getWorkspace().copy(root.getPath(),
					"/" + userid + "/trash/" + root.getName());
			jcrsession.save();
			root.remove();
			jcrsession.save();
			int no_of_Files = Integer.parseInt(parent.getProperty(
					Config.EDMS_NO_OF_DOCUMENTS).getString());
			if (no_of_Files > 0) {
				parent.setProperty(Config.EDMS_NO_OF_DOCUMENTS, no_of_Files - 1);
			}
			jcrsession.save();
			
			 * if(root.hasNodes()){ for (NodeIterator nit = root.getNodes();
			 * nit.hasNext();) { Node node = nit.nextNode();
			 * recycleFileRecursion(node,userid); } }
			 
		} catch (RepositoryException e) {
			e.printStackTrace();
		}

	}*/

	public String deleteFile(String FilePath, String userid) {
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		String sessionId=sessions.getId();
		String response = "";
		try {
			Node root = jcrsession.getRootNode();
			root = root.getNode(FilePath.substring(1));
			root.remove();
			jcrsession.save();
			response = "success";
		} catch (RepositoryException e) {
			response = "Exception Occured";
		}finally{
			//JcrRepositoryUtils.logout(sessionId);
		}
		return response;
	}

	public String restoreFile(String FilePath, String userid) {
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		String sessionId=sessions.getId();
		String response = "";
		try {
			Node root = jcrsession.getRootNode();
			root = root.getNode(FilePath.substring(1));
			restoreFileRecursion(root, userid);
			response = "success";
		} catch (RepositoryException e) {
			response = "Exception Occured";
			e.printStackTrace();
		}finally{
			//JcrRepositoryUtils.logout(sessionId);
		}
		return response;
	}

	public void restoreFileRecursion(Node root, String userid) {
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		String sessionId=sessions.getId();		try {
			String parents = root.getProperty(Config.EDMS_RESTORATION_PATH)
					.getString().substring(1);
			parents = parents.substring(0, parents.lastIndexOf("/"));
			// System.out.println("parent is "+parents);
			Node jcrRoot = jcrsession.getRootNode();
			Node parent;
			if (jcrRoot.hasNode(parents)) {
				parent = jcrRoot.getNode(parents);
			} else {
				parent = createFileRecursionWhenNotFound(parents, userid);
			}
			// root.setProperty(Config.EDMS_RECYCLE_DOC, false);
			// System.out.println(root.getPath()+" source to : "+root.getProperty(Config.EDMS_RESTORATION_PATH).getString());
			root.getSession()
					.getWorkspace()
					.copy(root.getPath(),
							root.getProperty(Config.EDMS_RESTORATION_PATH)
									.getString());
			jcrsession.save();
			root.remove();
			jcrsession.save();
			int no_of_Files = Integer.parseInt(parent.getProperty(
					Config.EDMS_NO_OF_DOCUMENTS).getString());
			parent.setProperty(Config.EDMS_NO_OF_DOCUMENTS, no_of_Files + 1);
			jcrsession.save();
			/*
			 * if(root.hasNodes()){ for (NodeIterator nit = root.getNodes();
			 * nit.hasNext();) { Node node = nit.nextNode();
			 * restoreFileRecursion(node); } }
			 */
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}

	public Node createFileRecursionWhenNotFound(String FileName, String userid) {
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		String sessionId=sessions.getId();
		Node file = null;
		try {
			Node root = jcrsession.getRootNode();
			// System.out.println("in File creation recursion "+FileName.substring(0,FileName.lastIndexOf("/")+1));
			String parent = FileName.substring(0, FileName.lastIndexOf("/"));
			if (root.hasNode(parent)) {
				root = root.getNode(parent);
				file = root.addNode(
						FileName.substring(FileName.lastIndexOf("/") + 1),
						Config.EDMS_DOCUMENT);
				file.setProperty(Config.USERS_READ, new String[] {});
				file.setProperty(Config.USERS_WRITE, new String[] {});
				file.setProperty(Config.USERS_DELETE, new String[] {});
				file.setProperty(Config.USERS_SECURITY, new String[] {});
				file.setProperty(Config.GROUPS_READ, new String[] {});
				file.setProperty(Config.GROUPS_WRITE, new String[] {});
				file.setProperty(Config.GROUPS_DELETE, new String[] {});
				file.setProperty(Config.GROUPS_SECURITY, new String[] {});
				file.setProperty(Config.EDMS_KEYWORDS, "root,File".split(","));
				file.setProperty(Config.EDMS_AUTHOR, userid);
				file.setProperty(Config.EDMS_DESCRIPTION,
						"this is system created File while restoration");
				file.setProperty(Config.EDMS_CREATIONDATE,
						(new Date()).toString());
				file.setProperty(Config.EDMS_MODIFICATIONDATE,
						(new Date()).toString());
				file.setProperty(Config.EDMS_RECYCLE_DOC, false);
				// file.setProperty(Config.EDMS_NO_OF_FileS, 0);
				// file.setProperty(Config.EDMS_NO_OF_DOCUMENTS, 0);
				file.addMixin(JcrConstants.MIX_SHAREABLE);
				file.addMixin(JcrConstants.MIX_VERSIONABLE);
				jcrsession.save();
			} else {
				createFileRecursionWhenNotFound(
						FileName.substring(0, FileName.lastIndexOf("/") + 1),
						userid);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;

	}

	public RenameFileRes renameFile(String oldFilePath, String newFilePath,
			String userid) {
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		String sessionId=sessions.getId();
		RenameFileRes response = new RenameFileRes();
		try {

			Node forVer = jcrsession.getRootNode().getNode(
					oldFilePath.substring(1));
			// System.out.println(forVer.getProperty(Config.EDMS_AUTHOR).getString());
			if (forVer.getProperty(Config.EDMS_AUTHOR).getString()
					.equals(userid)) {Version version=jcrsession.getWorkspace().getVersionManager().checkin(forVer.getPath());
					jcrsession.getWorkspace().getVersionManager().getVersionHistory(forVer.getPath()).addVersionLabel(version.getName(), "renamed from "+oldFilePath.substring(oldFilePath.lastIndexOf("/")+1)+" to "+newFilePath, true);
					jcrsession.getWorkspace().getVersionManager().checkout(forVer.getPath());
					jcrsession.move(oldFilePath,
							oldFilePath.substring(0, oldFilePath.lastIndexOf("/"))
									+ "/" + newFilePath);SimpleDateFormat format = new SimpleDateFormat(
						"YYYY-MM-dd'T'HH:mm:ss.SSSZ");
					forVer.setProperty(Config.EDMS_MODIFICATIONDATE,(format.format(new Date())).toString().replace("+0530", "Z") );
					jcrsession.save();	
					response.setResponse("Success");
					response.setSuccess(true);} else {

				response.setResponse("Access Denied");
				response.setSuccess(false);
			}
		} catch (RepositoryException e) {
			response.setResponse("Access Denied");
			response.setSuccess(false);
			e.printStackTrace();
		}finally{
			//JcrRepositoryUtils.logout(sessionId);
		}
		return response;
	}

	/* vfc */
	public VCFFileListReturn getVCFFileAtt(String fdrname, String userid) {	
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		String sessionId=sessions.getId();
		Assert.notNull(fdrname);
		VCFFileListReturn FileList1 = new VCFFileListReturn();
		ArrayOfVCFFiles Files = new ArrayOfVCFFiles();
		Node root = null;
		boolean status = true;
		try {
			root = jcrsession.getRootNode();
			if (fdrname.length() > 1) {
				if (!root.hasNode(userid)) {
					// root=jcrsession.createFile(userid);
					// jcrsession.createFile(userid+"/trash");
				} else {
					root = root.getNode(fdrname.substring(1));
				}
			}
			javax.jcr.query.QueryManager queryManager;
			queryManager = jcrsession.getWorkspace().getQueryManager();
			String expression = "select * from [edms:document] AS s WHERE ISCHILDNODE(s,'"
					+ fdrname
					+ "') and [edms:author]='"
					+ userid
					+ "' ORDER BY [NAME(s),DESC]";
			// expression =
			// "select * from [edms:folder] AS s WHERE NAME like ['%san%']";
			javax.jcr.query.Query query = queryManager.createQuery(expression,
					javax.jcr.query.Query.JCR_SQL2);
			javax.jcr.query.QueryResult result = query.execute();

			for (NodeIterator nit = result.getNodes(); nit.hasNext();) {
				Node node = nit.nextNode();

				if (Config.EDMS_DOCUMENT.equals(node.getPrimaryNodeType()
						.getName())) {
					if (node.getProperty(Config.EDMS_AUTHOR).getString()
							.equals(userid)) {
						VCFFileAtt vcfflatt = new VCFFileAtt();
						Node ntResourceNode = node.getNode("edms:content");
						InputStream is = ntResourceNode.getProperty("jcr:data")
								.getBinary().getStream();
						// InputStream
						// fs=(FileInputStream)IOUtils.toInputStream(node.getProperty(Config.EDMS_DESCRIPTION).getString());
						String photo = "";
						String name = "";
						String email = "";
						int email_cnt = 0;
						String phone = "";
						int phone_cnt = 0;
						String dept = "";
						String addr = "";
						int addr_cnt = 0;
						// String destFilePath = "D:/newfile223.vcf";
						// OutputStream output = response.getOutputStream();
						// FileOutputStream output = new
						// FileOutputStream(destFilePath);
						/*
						 * byte[] buffer = new byte[4096];
						 * 
						 * int byteRead;
						 * 
						 * while ((byteRead = is.read(buffer)) != -1) {
						 * output.write(buffer, 0, byteRead); } output.close();
						 */
						// InputStream fs=new FileInputStream(new
						// java.io.File("D:/newfile223.vcf"));
						VCardReader vcardReader = new VCardReader(is);
						// System.out.println(is);
						VCard vcard = Ezvcard.parse(is).first();
						FormattedName fn = vcard.getFormattedName();
						name = fn.getValue();
						List<Email> elst = vcard.getEmails();

						for (Email em : elst) {

							if (email_cnt == 0) {
								email = em.getValue();
							}
							email_cnt++;

							/*System.out.println("**************email type="
									+ em.getTypes() + ": email="
									+ em.getValue());*/
						}

						List<Telephone> moblst = vcard.getTelephoneNumbers();
						for (Telephone tel : moblst) {
							if (phone_cnt == 0) {
								phone = tel.getText();
							}
							phone_cnt++;
						}

						List<Address> addre = vcard.getAddresses();
						for (Address ad : addre) {
							if (addr_cnt == 0) {
								if (ad.getStreetAddress() != null
										&& !(ad.getStreetAddress().equals(""))) {
									if (addr.equals("") || addr == null) {
										addr = ad.getStreetAddress();
									} else {
										addr = addr + ", "
												+ ad.getStreetAddress();
									}
								}
								if (ad.getLocality() != null
										&& !(ad.getLocality().equals(""))) {
									if (addr.equals("") || addr == null) {
										addr = ad.getLocality();
									} else {
										addr = addr + ", " + ad.getLocality();
									}
								}
								if (ad.getRegion() != null
										&& !(ad.getRegion().equals(""))) {
									if (addr.equals("") || addr == null) {
										addr = ad.getRegion();
									} else {
										addr = addr + ", " + ad.getRegion();
									}
								}
								if (ad.getCountry() != null
										&& !(ad.getCountry().equals(""))) {
									if (addr.equals("") || addr == null) {
										addr = ad.getCountry();
									} else {
										addr = addr + ", " + ad.getCountry();
									}
								}
								if (ad.getPostalCode() != null
										&& !(ad.getPostalCode().equals(""))) {
									if (addr.equals("") || addr == null) {
										addr = ad.getPostalCode();
									} else {
										addr = addr + ", " + ad.getPostalCode();
									}
								}

							}
							addr_cnt++;

							

							// System.out.println("Address:"+ad.getCountry()+" type:"+ad.getTypes());
							// System.out.println("Address:"+ad.getStreetAddress()+" type:"+ad.getTypes());
						}
						if (email_cnt > 1) {
							email_cnt--;
							email = email + "(+" + email_cnt + ")";
						}
						if (phone_cnt > 1) {
						//	System.out.println("*************name=" + name
						//			+ "--- contact=" + phone_cnt);
							phone_cnt--;
							phone = phone + "(+" + phone_cnt + ")";
						}
						if (addr_cnt > 1) {
							addr_cnt--;
							addr = addr + "(+" + addr_cnt + ")";
						}
						// System.out.println(vcard.getFormattedName().getValue());
						// System.out.println(vcard.getStructuredName().getFamily());
						// System.out.println("--------------------------------");

						vcardReader.close();
						System.out.println(vcard.getTitles().size());
						if(vcard.getTitles().size()>0){
						System.out.println(vcard.getTitles().get(0).getValue().toString());
						vcfflatt.setContactDept(vcard.getTitles().get(0).getValue().toString());
						}else{
							vcfflatt.setContactDept("");
						}
						
						vcfflatt.setContactAddress(addr);
						//vcfflatt.setContactDept(dept);
						vcfflatt.setContactEmail(email);
						vcfflatt.setContactName(name);
						vcfflatt.setContactPhone(phone);
						vcfflatt.setContactPhoto(photo);
						vcfflatt.setContactFileName(node.getName());
						Files.getVCFFileList().add(vcfflatt);
						System.out.println(node.getName());

					}
				}

			}	FileList1.setVCFFileListResult(Files);
		FileList1.setVCFSuccess(status);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			//JcrRepositoryUtils.logout(sessionId);
		}
	
		return FileList1;
	}

	public SortByPropertyRes sortByProperty(String path, String propertyName,
			String userid) {
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		String sessionId=sessions.getId();
		SortByPropertyRes sortByProperty = new SortByPropertyRes();
		// Obtain the query manager for the session via the workspace ...
		javax.jcr.query.QueryManager queryManager;
		try {
			queryManager = jcrsession.getWorkspace().getQueryManager();

			// Create a query object ...
			String expression = "select * from [edms:folder] AS s WHERE ISDESCENDANTNODE(s,'/sanjay@avi-oil.com/Contacts') ";
			expression = "select * from [edms:folder] AS s WHERE NAME() = 'sanjay1' ";
			// expression =
			// "select * from [edms:document] WHERE NAME() like '%.png' ";
			// expression="SELECT p.* FROM [nt:base] AS p WHERE p.[jcr:lastModified] >= CAST('2015-01-01T00:00:00.000Z' AS DATE) AND p.[jcr:lastModified] <= CAST('2015-12-31T23:59:59.999Z' AS DATE)";
			// expression =
			// "select * from [edms:folder] where [jcr:path] like '%santosh%'";

			javax.jcr.query.Query query = queryManager.createQuery(expression,
					javax.jcr.query.Query.JCR_SQL2);

			// query.setLimit(10);
			// query.setOffset(0);
			// Execute the query and get the results ...
			javax.jcr.query.QueryResult result = query.execute();
			// System.out.println(result.getNodes());
			for (NodeIterator nit = result.getNodes(); nit.hasNext();) {
				Node node = nit.nextNode();
				System.out.println("node name is : "
						+ node.getName()
						+ " and path is : "
						+ node.getPath()
						+ " modification date is : "
						+ node.getProperty(Config.EDMS_MODIFICATIONDATE)
								.getString());

			}
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			//JcrRepositoryUtils.logout(sessionId);
		}
		return null;
	}

	public SearchDocByLikeResponse searchDocByLike(String searchParamValue,
			String folderPath, String searchParam, String userid) {
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		String sessionId=sessions.getId();
		SearchDocByLikeResponse searchDocResponse = new SearchDocByLikeResponse();
		ArrayOfFolders folders = new ArrayOfFolders();
		ArrayOfFiles files = new ArrayOfFiles();
		FilesAndFolders filesFolders=new FilesAndFolders();
		javax.jcr.query.QueryManager queryManager;
		try {
			queryManager = jcrsession.getWorkspace().getQueryManager();
			// Create a query object ...
			String expression = "select * from [edms:folder] AS s WHERE ISDESCENDANTNODE(s,'"
					+ folderPath
					+ "') AND  ["
					+ searchParam
					+ "] like '%"
					+ searchParamValue + "%'";
			javax.jcr.query.Query query = queryManager.createQuery(expression,
					javax.jcr.query.Query.JCR_SQL2);
			javax.jcr.query.QueryResult result = query.execute();

			for (NodeIterator nit = result.getNodes(); nit.hasNext();) {
				Node node = nit.nextNode();
				Folder folder = new Folder();
				new FolderRepository().setProperties(node, folder, userid,jcrsession);
				folders.getFolderList().add(folder);
			}
			expression = "select * from [edms:document] AS s WHERE ISDESCENDANTNODE(s,'"
					+ folderPath
					+ "') AND  ["
					+ searchParam
					+ "]  like '%"
					+ searchParamValue + "%'";
			query = queryManager.createQuery(expression,
					javax.jcr.query.Query.JCR_SQL2);
			result = query.execute();
			for (NodeIterator nit = result.getNodes(); nit.hasNext();) {
				Node node = nit.nextNode();
				File file = new File();
				new FileRepository().setProperties(node, file, userid,jcrsession);
				files.getFileList().add(file);
			}
			filesFolders.setFilesList(files);
			filesFolders.setFoldersList(folders);
			searchDocResponse.setSearchedFolders(filesFolders);
		} catch (Exception e) {
			e.printStackTrace();
			return searchDocResponse;
		}finally{
			//JcrRepositoryUtils.logout(sessionId);
		}
		return searchDocResponse;
	}

	public SearchDocByDateResponse searchDocByDate(String searchParamValue,
			String folderPath, String searchParam, String userid) {
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		String sessionId=sessions.getId();
		SearchDocByDateResponse searchDocResponse = new SearchDocByDateResponse();
		ArrayOfFolders folders = new ArrayOfFolders();
		ArrayOfFiles files = new ArrayOfFiles();
		FilesAndFolders filesFolders=new FilesAndFolders();
		javax.jcr.query.QueryManager queryManager;
		try {
			queryManager = jcrsession.getWorkspace().getQueryManager();
			// Create a query object ...
			String expression = "";

			Calendar cal = Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat(
					"YYYY-MM-dd'T'HH:mm:ss.SSSZ");
			format.format(cal.getTime());
			Date before = cal.getTime();
			System.out.println(before);
			System.out.println("current date : "
					+ format.format(before).substring(0,
							format.format(before).lastIndexOf("+")) + "Z");
			cal.add(Calendar.MONTH, -1);
			cal.add(Calendar.DATE, -20);
			Date dateafter = cal.getTime();

			System.out.println("one month before date : "
					+ format.format(dateafter).substring(0,
							format.format(dateafter).lastIndexOf("+")) + "Z");
			expression = "SELECT * FROM [edms:folder] AS p WHERE ISDESCENDANTNODE(p,'"
					+ folderPath
					+ "') AND ["
					+ searchParam
					+ "] like  '%"
					+ searchParamValue + "%'" + "" + "";
			/*
			 * + "AND p.[edms:modified] >= CAST('" +
			 * format.format(dateafter).replace("+0530", "Z") +
			 * "' AS DATE) AND p.[edms:modified] <= CAST('" +
			 * format.format(before).replace("+0530", "Z")+"' AS DATE)";
			 */

			javax.jcr.query.Query query = queryManager.createQuery(expression,
					javax.jcr.query.Query.JCR_SQL2);
			javax.jcr.query.QueryResult result = query.execute();

			for (NodeIterator nit = result.getNodes(); nit.hasNext();) {
				Node node = nit.nextNode();
				Folder folder = new Folder();
				new FolderRepository().setProperties(node, folder, userid,jcrsession);
				folders.getFolderList().add(folder);
			}
			expression = "select * from [edms:document] AS s WHERE ISDESCENDANTNODE(s,'"
					+ folderPath
					+ "') AND ["
					+ searchParam
					+ "] like  '%"
					+ searchParamValue + "%'";
			query = queryManager.createQuery(expression,
					javax.jcr.query.Query.JCR_SQL2);
			result = query.execute();
			for (NodeIterator nit = result.getNodes(); nit.hasNext();) {
				Node node = nit.nextNode();
				File file = new File();
				new FileRepository().setProperties(node, file, userid,jcrsession);
				files.getFileList().add(file);
			}
			filesFolders.setFilesList(files);
			filesFolders.setFoldersList(folders);
			searchDocResponse.setSearchedFolders(filesFolders);
			} catch (Exception e) {
			e.printStackTrace();
			return searchDocResponse;
		}finally{
			//JcrRepositoryUtils.logout(sessionId);
		}
		return searchDocResponse;
	}

	public EditFileResponse editFile(String fileContent, String filePath,
			String userid) {
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, "redhat");
		Session jcrsession = sessions.getSession();
		String sessionId=sessions.getId();
		EditFileResponse res = new EditFileResponse();
		EditFileRes response=new EditFileRes();
		try {

			Node forVer = jcrsession.getRootNode().getNode(
					filePath.substring(1));
			// System.out.println(forVer.getProperty(Config.EDMS_AUTHOR).getString());
			if (forVer.getProperty(Config.EDMS_AUTHOR).getString()
					.equals(userid)) {
				forVer.checkin();
				forVer.checkout();
				Node content=forVer.getNode("edms:content");
				InputStream	isss=null;
				byte[] encodedImage = org.apache.commons.codec.binary.Base64
						.decodeBase64(fileContent);
					try {
						isss=IOUtils.toInputStream(IOUtils.toString(encodedImage));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				ValueFactory valueFactory = jcrsession.getValueFactory();
				Binary myBinary = valueFactory.createBinary(isss);
				
				content.setProperty("jcr:data", myBinary);
				jcrsession.save();

				response.setResponse("Success");
				response.setSuccess(true);
			} else {

				response.setResponse("Access Denied");
				response.setSuccess(false);
			}res.setEditFileRes(response);
		} catch (RepositoryException e) {
			response.setResponse("Access Denied");
			response.setSuccess(false);
			e.printStackTrace();
		}finally{
			//JcrRepositoryUtils.logout(sessionId);
		}
		
		return res;
	}

}