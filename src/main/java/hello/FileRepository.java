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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.Principal;
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

import com.edms.file.ArrayOfFiles;
import com.edms.file.File;
import com.edms.file.FileListReturn;
import com.edms.file.RenameFileRes;

import org.neo4j.cypher.internal.compiler.v2_0.untilMatched;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import edms.core.Config;
import edms.core.JcrRepositorySession;

@Component
public class FileRepository {
	private static Session jcrsession =JcrRepositorySession.jcrsession;
	/*
	 * @Autowired FileService FileService;
	 */

	// @Autowired DefaultSpringSecurityContextSource contextSource;

	@PostConstruct
	public void initData() {
		System.out.println("only single time !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ");
		jcrsession=JcrRepositorySession.getSession();
	}

	public FileListReturn listFile(String name, String userid) {
		Assert.notNull(name);
		FileListReturn FileList1 = new FileListReturn();
		ArrayOfFiles Files = new ArrayOfFiles();
		Node root = null;
		
		try {
			 	/*	String[] wwws=jcrsession.getWorkspace().getAccessibleWorkspaceNames();
				for (int i = 0; i < wwws.length; i++) {
					System.out.println(wwws[i]);
				}	*/
				//	registerNamespace(jcrsession, root);
				// 	removeUser(jcrsession, userid);
				//	createUser(userid, "redhat", jcrsession, root);
			 	//	setPolicy(jcrsession, root, userid,root.getPath(),  Privilege.JCR_ALL);
				//	Workspace ws=jcrsession.getWorkspace();
				//	ws.createWorkspace(userid);
			
					root = jcrsession.getRootNode();
					if (name.length() > 1) {
						if (!root.hasNode(userid)) {
							//root=JcrRepositorySession.createFile(userid);
							//JcrRepositorySession.createFile(userid+"/trash");
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
					if (Config.EDMS_DOCUMENT.equals(node.getPrimaryNodeType().getName())) {
					if(node.getProperty(Config.EDMS_AUTHOR).getString().equals(userid))
					{
					File File = new File();
					File=setProperties(node,File,userid);
					Files.getFileList().add(File);}
				}
					//}
			}
		} catch (LoginException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			jcrsession.logout();
			e.printStackTrace();
		} 
		FileList1.setFileListResult(Files);
		FileList1.setSuccess(true);
		return FileList1;
	}

	public File setProperties(Node node, File file,String userid) {
		try {
			
		file.setFileName(node.getName());
		file.setFilePath(node.getPath());
		file.setParentFile(node.getParent().getName());

		if(node.hasProperty(Config.EDMS_AUTHOR))
		file.setCreatedBy(node.getProperty(Config.EDMS_AUTHOR).getString());
		if(node.hasProperty(Config.EDMS_RECYCLE_DOC))
		file.setRecycle(node.getProperty(Config.EDMS_RECYCLE_DOC).getBoolean());

		if(node.hasProperty(Config.EDMS_KEYWORDS)){
		Value[] actualUsers = node.getProperty(Config.EDMS_KEYWORDS).getValues();
		for (int i = 0; i < actualUsers.length; i++) {
			file.getKeywords().add(actualUsers[i].getString());
		}
		}
		if(node.hasProperty(Config.EDMS_CREATIONDATE))
		file.setCreationDate(node.getProperty(Config.EDMS_CREATIONDATE).getString());
		
		if(node.hasProperty(Config.EDMS_MODIFICATIONDATE))
		file.setModificationDate(node.getProperty(Config.EDMS_MODIFICATIONDATE).getString());
		
	/*	if(node.hasProperty(Config.EDMS_NO_OF_DOCUMENTS)){
			file.setNoOfDocuments(node.getProperty(Config.EDMS_NO_OF_DOCUMENTS).getString());
		
		}
		if(node.hasProperty(Config.EDMS_NO_OF_FileS)){
			file.setNoOfFiles(node.getProperty(Config.EDMS_NO_OF_FileS).getString());
		}*/
		if(node.hasProperty(Config.EDMS_DESCRIPTION))
		file.setNotes(node.getProperty(Config.EDMS_DESCRIPTION).getString());
		
		/* start mapping permissions to edms File */
			if(node.hasProperty(Config.USERS_READ)){
			Value[] actualUsers = node.getProperty(Config.USERS_READ).getValues();
		
			for (int i = 0; i < actualUsers.length; i++) {
				file.getUserRead().add(actualUsers[i].getString());
			}	}
			if(node.hasProperty(Config.USERS_WRITE)){
			Value[] actualUsers = node.getProperty(Config.USERS_WRITE).getValues();
			for (int i = 0; i < actualUsers.length; i++) {
				file.getUserWrite().add(actualUsers[i].getString());
			}	}
			if(node.hasProperty(Config.USERS_DELETE)){
			Value[] actualUsers = node.getProperty(Config.USERS_DELETE).getValues();
			for (int i = 0; i < actualUsers.length; i++) {
				file.getUserDelete().add(actualUsers[i].getString());
			}	
			}	
			if(node.hasProperty(Config.USERS_SECURITY)){
			Value[] actualUsers = node.getProperty(Config.USERS_SECURITY).getValues();
			for (int i = 0; i < actualUsers.length; i++) {
				file.getUserSecurity().add(actualUsers[i].getString());
			}	
			}	
			if(node.hasProperty(Config.GROUPS_READ)){
			Value[] actualUsers = node.getProperty(Config.GROUPS_READ).getValues();
			for (int i = 0; i < actualUsers.length; i++) {
				file.getGroupRead().add(actualUsers[i].getString());
			}	
			}	
			if(node.hasProperty(Config.GROUPS_WRITE)){
			Value[] actualUsers = node.getProperty(Config.GROUPS_WRITE).getValues();
			for (int i = 0; i < actualUsers.length; i++) {
				file.getGroupWrite().add(actualUsers[i].getString());
			}
			}	
			if(node.hasProperty(Config.GROUPS_DELETE)){
			Value[] actualUsers = node.getProperty(Config.GROUPS_DELETE).getValues();
			for (int i = 0; i < actualUsers.length; i++) {
				file.getGroupDelete().add(actualUsers[i].getString());
			}	
			}	
			if(node.hasProperty(Config.GROUPS_SECURITY)){
			Value[] actualUsers = node.getProperty(Config.GROUPS_SECURITY).getValues();
			for (int i = 0; i < actualUsers.length; i++) {
				file.getGroupSecurity().add(actualUsers[i].getString());
			}	
			}
			
			VersionHistory history = jcrsession.getWorkspace().getVersionManager().getVersionHistory(node.getPath());
			// To iterate over all versions
			VersionIterator versions = history.getAllVersions();
			System.out.println("versions of : "+node.getName());
			while (versions.hasNext()) {
			  Version version = versions.nextVersion();
			  System.out.println(version.getCreated().getTime());
			  System.out.println(version.getPath());
			}
			/*node.checkout();
			Version mySpecificVersion = history.getVersion("1.0");
			jcrsession.getWorkspace().getVersionManager().restore(node.getParent().getPath()+"/new6",mySpecificVersion, true);
			node.checkin();*/
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file;
	}

	public Boolean hasChild(String FilePath, String userid) {

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
			if (FilePath.length() > 1) {
				root = root.getNode(FilePath.substring(1));
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


	public File createFile(String fileName, String parentFile,
			String userid, String keywords, String description,String is) {
		Node file = null;
		File file1 = new File();
		try {
			/*jcrsession = repository.login(new SimpleCredentials("admin",
					"admin".toCharArray()));*/
			/*jcrsession = repository.login(new SimpleCredentials(
			 userid,"redhat".toCharArray()));*/
		
			Node root = jcrsession.getRootNode();
			/*if(userid!="admin"){
			root=root.getNode(userid);
			}*/
			if (parentFile.length() > 1) {
				root = root.getNode(parentFile.substring(1));
			}
			if(root.hasProperty(Config.USERS_WRITE)){
			Value[] actualUsers = root.getProperty(Config.USERS_WRITE).getValues();
			
			String newUsers = "";
			
			for (int i = 0; i < actualUsers.length; i++) {
				newUsers+=actualUsers[i].getString()+",";
			}
			if(newUsers.contains(userid)||root.getProperty(Config.EDMS_AUTHOR).getString().equals(userid)||(root.getName().equals(userid)&&(root.getProperty(Config.EDMS_AUTHOR).getString()).equals(Config.JCR_USERNAME))){
				
			file = root.addNode(fileName, Config.EDMS_DOCUMENT);
			
			if(root.hasProperty(Config.USERS_READ)&&(!root.getProperty(Config.EDMS_AUTHOR).toString().equals("admin"))){
				Value[] actualUser = root.getProperty(Config.USERS_READ).getValues();
				String newUser="";
				for (int i = 0; i < actualUser.length; i++) {
					newUser+=actualUser[i].getString()+",";
				}
			file.setProperty(Config.USERS_READ, newUser.split(","));
			}else{
				file.setProperty(Config.USERS_READ, new String[]{});
			}
			if(root.hasProperty(Config.USERS_WRITE)&&!root.getProperty(Config.EDMS_AUTHOR).toString().equals("admin")){
				Value[] actualUser = root.getProperty(Config.USERS_WRITE).getValues();
				String newUser="";
				for (int i = 0; i < actualUser.length; i++) {
					newUser+=actualUser[i].getString()+",";
				}
			file.setProperty(Config.USERS_WRITE, newUser.split(","));
			}else{
				file.setProperty(Config.USERS_WRITE, new String[]{});
			}

			if(root.hasProperty(Config.USERS_DELETE)&&!root.getProperty(Config.EDMS_AUTHOR).toString().equals("admin")){
				Value[] actualUser = root.getProperty(Config.USERS_DELETE).getValues();
				String newUser="";
				for (int i = 0; i < actualUser.length; i++) {
					newUser+=actualUser[i].getString()+",";
				}
			file.setProperty(Config.USERS_DELETE, newUser.split(","));
			}else{
				file.setProperty(Config.USERS_DELETE, new String[]{});
			}

			if(root.hasProperty(Config.USERS_SECURITY)&&!root.getProperty(Config.EDMS_AUTHOR).toString().equals("admin")){
				Value[] actualUser = root.getProperty(Config.USERS_SECURITY).getValues();
				String newUser="";
				for (int i = 0; i < actualUser.length; i++) {
					newUser+=actualUser[i].getString()+",";
				}
			file.setProperty(Config.USERS_SECURITY, newUser.split(","));
			}else{
				file.setProperty(Config.USERS_SECURITY, new String[]{});
			}

			if(root.hasProperty(Config.GROUPS_READ)&&!root.getProperty(Config.EDMS_AUTHOR).toString().equals("admin")){
				Value[] actualUser = root.getProperty(Config.GROUPS_READ).getValues();
				String newUser="";
				for (int i = 0; i < actualUser.length; i++) {
					newUser+=actualUser[i].getString()+",";
				}
			file.setProperty(Config.GROUPS_READ, newUser.split(","));
			}else{
				file.setProperty(Config.GROUPS_READ, new String[]{});
			}

			if(root.hasProperty(Config.GROUPS_WRITE)&&!root.getProperty(Config.EDMS_AUTHOR).toString().equals("admin")){
				Value[] actualUser = root.getProperty(Config.GROUPS_WRITE).getValues();
				String newUser="";
				for (int i = 0; i < actualUser.length; i++) {
					newUser+=actualUser[i].getString()+",";
				}
			file.setProperty(Config.GROUPS_WRITE, newUser.split(","));
			}else{
				file.setProperty(Config.GROUPS_WRITE, new String[]{});
			}

			if(root.hasProperty(Config.GROUPS_DELETE)&&!root.getProperty(Config.EDMS_AUTHOR).toString().equals("admin")){
				Value[] actualUser = root.getProperty(Config.GROUPS_DELETE).getValues();
				String newUser="";
				for (int i = 0; i < actualUser.length; i++) {
					newUser+=actualUser[i].getString()+",";
				}
			file.setProperty(Config.GROUPS_DELETE, newUser.split(","));
			}else{
				file.setProperty(Config.GROUPS_DELETE, new String[]{});
			}

			if(root.hasProperty(Config.GROUPS_SECURITY)&&!root.getProperty(Config.EDMS_AUTHOR).toString().equals("admin")){
				Value[] actualUser = root.getProperty(Config.GROUPS_SECURITY).getValues();
				String newUser="";
				for (int i = 0; i < actualUser.length; i++) {
					newUser+=actualUser[i].getString()+",";
				}
			file.setProperty(Config.GROUPS_SECURITY, newUser.split(","));
			}else{
				file.setProperty(Config.GROUPS_SECURITY, new String[]{});
			}
			
			
			/*file.setProperty(Config.USERS_READ,new String[]{userid});
			file.setProperty(Config.USERS_WRITE,new String[]{userid});
			file.setProperty(Config.USERS_DELETE,new String[]{userid});
			file.setProperty(Config.USERS_SECURITY,new String[]{userid});*/
			file.setProperty(Config.EDMS_KEYWORDS, keywords.split(","));
			if(root.getProperty(Config.EDMS_AUTHOR).getString().equals("admin")){

				file.setProperty(Config.EDMS_AUTHOR,userid);
					
			}else{
			file.setProperty(Config.EDMS_AUTHOR,root.getProperty(Config.EDMS_AUTHOR).getString());
			}file.setProperty(Config.EDMS_DESCRIPTION, description);
			file.setProperty(Config.EDMS_CREATIONDATE, (new Date()).toString());
			file.setProperty(Config.EDMS_MODIFICATIONDATE, (new Date()).toString());
			file.setProperty(Config.EDMS_RECYCLE_DOC, false);
			//file.setProperty(Config.EDMS_NO_OF_FileS, 0);
			//file.setProperty(Config.EDMS_NO_OF_DOCUMENTS, 0);
			file.addMixin(JcrConstants.MIX_SHAREABLE);
			file.addMixin(JcrConstants.MIX_VERSIONABLE);
			
			
			InputStream iss=IOUtils.toInputStream(is);
			Tika tika = new Tika();
			// FileOutputStream out = null;
			try {
				// out = new FileOutputStream(new File('D:/edms
				// project/spring-tool-suite-3.6.0.RELEASE-e4.4-win32-x86_64/sts-bundle/sts-3.6.0.RELEASE/repository.xml'));
				// IOUtils.copy(in, out);
				String mimeType = tika.detect(iss);
				System.out.println(mimeType);
			} catch (Exception e) {
				System.err.println(e);
			}
			ValueFactory valueFactory = jcrsession.getValueFactory();   
			Binary myBinary = valueFactory.createBinary(iss);            
			file.addMixin("mix:referenceable");   
			Node resNode = file.addNode("jcr:content", "nt:resource");   
			resNode.setProperty("jcr:mimeType", "");   
			resNode.setProperty("jcr:data", myBinary);   
			Calendar lastModified = Calendar.getInstance();   
			lastModified.setTimeInMillis(lastModified.getTimeInMillis());   
			resNode.setProperty("jcr:lastModified", lastModified);   
			jcrsession.save(); 
			
			file1=	setProperties(file, file1,userid);
			}else{
				System.out.println("you have not permission to add child node");
			}}
		} catch (LoginException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} finally {
			//jcrsession.logout();
		}
		return file1;
	}

	public File getFileByPath(String FilePath, String userid) {
		Repository repository = new TransientRepository();
		//Session jcrsession = null;
		File File1 = new File();
		try {
		/*	jcrsession = repository.login(new SimpleCredentials("admin",
					"admin".toCharArray()));*/
			/* jcrsession = repository.login(new SimpleCredentials(
			 userid,"redhat".toCharArray()));*/
		
			Node root = jcrsession.getRootNode();
			/*if(userid!="admin"){
				root=root.getNode(userid);
				}*/
			if (FilePath.length() > 1) {
				root = root.getNode(FilePath.substring(1));
			}
			File1.setFileName(root.getName().toString());
			File1.setFilePath(root.getPath().toString());
			if(root.getPath().toString().length()>1){
			if(FilePath.length()>1){
					if(root.getProperty(Config.EDMS_AUTHOR).getString().equals(userid)||(root.getName().equals(userid)&&(root.getProperty(Config.EDMS_AUTHOR).getString()).equals(Config.JCR_USERNAME)))
					{
						setProperties(root, File1,userid);
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
		return File1;
	}

	public void shareFileRecursion(Node rt,String userpermissions,String grouppermissions,String users,String groups) {
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
					shareFileRecursion(root, userpermissions, grouppermissions, users, groups);
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
			System.out.println(newUser.contains(user));
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
				System.out.println(newUser.contains(user));
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
				System.out.println(newUser.contains(user));
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
				System.out.println(newUser.contains(user));
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
		System.out.println(newUser.contains(user));
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
		System.out.println(newUser.contains(user));
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
		System.out.println(newUser.contains(user));
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
		System.out.println(newUser.contains(user));
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
			System.out.println(newUser.contains(user));
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
				System.out.println(newUser.contains(user));
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
				System.out.println(newUser.contains(user));
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
	System.out.println(newUser.contains(user));
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
	System.out.println(newUser.contains(user));
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
		System.out.println(newUser.contains(user));
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
		System.out.println(newUser.contains(user));
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
	System.out.println(newUser.contains(user));
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
	System.out.println(newUser.contains(user));
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
		System.out.println(newUser.contains(user));
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
		System.out.println(newUser.contains(user));
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
		public String assignSinglePermission(String FilePath, String userid,
				String user,String value) {
		try {
			Node root = jcrsession.getRootNode();
			if (FilePath.length() > 1) {
				root = root.getNode(FilePath.substring(1));
			}
			
			if(root.getProperty(Config.EDMS_AUTHOR).getString().equals(userid)){
						System.out.println(":before assigning permissions like "+value +" to user : "+user+" on "+root.getName());
						assignSinglePermissionRecursion(root, userid, user, value);
						System.out.println(":value contains n "+value.contains("n"));
						if(!value.contains("n")&&value.contains("r")){
							
						Workspace ws=jcrsession.getWorkspace();
						Node rty=jcrsession.getRootNode();
						if(!rty.hasNode(user+"/"+root.getName())){
						ws.clone(ws.getName(), root.getPath(), "/"+user+"/"+root.getName() , false);
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
				return "sorry you don't have permissions to share this File";	
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
	

		public String shareFileByPath(String FilePath, String userid,
				String users,String groups,String userpermissions,String grouppermissions) {
		//Repository	repository =  new TransientRepository();
		//File File1 = new File();
		//Session jcrsession=null;
		try {
			/*jcrsession = repository.login(new SimpleCredentials("admin",
					"admin".toCharArray()));*/
			/* jcrsession = repository.login(new SimpleCredentials(
			 userid,"redhat".toCharArray()));*/
		
			Node root = jcrsession.getRootNode();
			if (FilePath.length() > 1) {
				root = root.getNode(FilePath.substring(1));
			}
			
			if(root.getProperty(Config.EDMS_AUTHOR).getString().equals(userid)){
				shareFileRecursion(root, userpermissions, grouppermissions, users, groups);
				
				Workspace ws=jcrsession.getWorkspace();
				
				
				for(String user:users.split(","))
				{
						if(!user.equals(userid)){
							Node rty=jcrsession.getRootNode();
							if(!rty.hasNode(user+"/"+root.getName())){
							ws.clone(ws.getName(), root.getPath(), "/"+user+"/"+root.getName() , false);
						}
						else{
						System.out.println("already exist");	
						}
						}
				}
				jcrsession.save();
			}else{
				return "sorry you don't have permissions to share this File";	
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
	
	
	
	
	/*public boolean shareFileByPath(Session jcrsession,String FilePath, String userid,
			String users,String groups,String userpermissions,String grouppermissions) {
		
		try {
			Node root = jcrsession.getRootNode();
			if(userid!="admin"){
				root=root.getNode(userid);
				}
				if (FilePath.length() > 1) {
				root = root.getNode(FilePath.substring(1));
			}
			//Workspace ws = jcrsession.getWorkspace();
			System.out
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
			Node userNode=	root.addNode(userid,Config.EDMS_DOCUMENT);
			userNode.setProperty(Config.USERS_READ,new String[]{userid});
			userNode.setProperty(Config.USERS_WRITE,new String[]{userid});
			userNode.setProperty(Config.USERS_DELETE,new String[]{userid});
			userNode.setProperty(Config.USERS_SECURITY,new String[]{userid});
			userNode.setProperty(Config.EDMS_KEYWORDS, new String[]{"user","root","node"});
			userNode.setProperty(Config.EDMS_AUTHOR,userid);
			userNode.setProperty(Config.EDMS_CREATIONDATE, (new Date()).toString());
			userNode.setProperty(Config.EDMS_MODIFICATIONDATE, (new Date()).toString());
			//userNode.setProperty(Config.EDMS_NO_OF_FileS, 0);
			//userNode.setProperty(Config.EDMS_NO_OF_DOCUMENTS, 0);
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
			System.out.println(privileges[i]);
		}*/

		// get now applied privileges on a node 
		Privilege[]	privileges = aMgr.getPrivileges(root.getPath());
		for (int i = 0; i < privileges.length; i++) {
			System.out.println(privileges[i]);
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
				System.out.println(privileges[i]);
			}*/

			// get now applied privileges on a node 
			/*privileges = aMgr.getPrivileges(path);
			for (int i = 0; i < privileges.length; i++) {
				System.out.println(privileges[i]);
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
				System.out.println("group path is : " + grp.getID()
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

			System.out.println("before set policy on : "+path+" to user : "+userid);
			for (int i = 0; i < setprivilege.length; i++) {
				System.out.println(setprivilege[i]);
			}
			
			jcrsession.save();
			jcrsession.refresh(true);
			setprivilege = aMgr.getPrivileges(path);
			System.out.println("after set policy on : "+path+" to user : "+userid);
			for (int i = 0; i < setprivilege.length; i++) {
				System.out.println(setprivilege[i]);
			}
			
		} catch (RepositoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public static void registerNamespace(Session jcrsession, Node root) {
		try {
			//System.out.println("root is : " + root);
			Workspace ws = jcrsession.getWorkspace();
			System.out.println("workspace is : " + ws.getName());
			NamespaceRegistry nr;
			nr = ws.getNamespaceRegistry();
			System.out.println("namespace registry is : "
					+ nr.getPrefixes().length);
			for (String str : nr.getPrefixes()) {
				System.out.println(str);
			}
			for (String str : nr.getURIs()) {
				System.out.println(str);
			}
			for (NodeTypeIterator iterator = ws.getNodeTypeManager()
					.getAllNodeTypes(); iterator.hasNext();) {
				NodeType nodeType = (NodeType) iterator.next();
				System.out.println(nodeType.getName());
			}
			nr.registerNamespace("edms", "http://www.edms.com/1.0");
			System.out.println("in repository");
			InputStream is = ClassLoader.class
					.getResourceAsStream("/edms/module/jcr/CustomNodes.cnd");
			System.out.println("Input Stream is : " + is);
			Reader cnd = new InputStreamReader(is);
			NodeType[] nodeTypes;
			nodeTypes = CndImporter.registerNodeTypes(cnd, jcrsession,false);
			System.out.println(nodeTypes.length);
		} catch (RepositoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public FileListReturn listSharedFile(String userid) {
		FileListReturn FileList1 = new FileListReturn();
		ArrayOfFiles Files = new ArrayOfFiles();
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
					System.out.println(wwws[i]);
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
				if (Config.EDMS_DOCUMENT.equals(node.getPrimaryNodeType().getName())) {

					if(node.hasProperty(Config.USERS_READ)){
					Value[] actualUsers = node.getProperty(Config.USERS_READ).getValues();
					String newUser="";
					for (int i = 0; i < actualUsers.length; i++) {
						newUser+=actualUsers[i].getString()+",";
					}
					System.out.println("for node : "+node.getPath().toString()+" newUser contains "+node.getProperty(Config.EDMS_AUTHOR).getString()+" is "+newUser.contains(userid));
					if(newUser.contains(userid))
					{
					File File = new File();
					File=setProperties(node,File,userid);
					Files.getFileList().add(File);}
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
		FileList1.setFileListResult(Files);
		FileList1.setSuccess(true);
		return FileList1;
	}
	public FileListReturn listSharedFile(String userid,String path) {
		FileListReturn FileList1 = new FileListReturn();
		ArrayOfFiles Files = new ArrayOfFiles();
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
				if (Config.EDMS_DOCUMENT.equals(node.getPrimaryNodeType().getName())) {

					if(node.hasProperty(Config.USERS_READ)){
					Value[] actualUsers = node.getProperty(Config.USERS_READ).getValues();
					String newUser="";
					for (int i = 0; i < actualUsers.length; i++) {
						newUser+=actualUsers[i].getString()+",";
					}
					System.out.println("for node : "+node.getPath().toString()+" newUser contains "+node.getProperty(Config.EDMS_AUTHOR).getString()+" is "+newUser.contains(userid));
					if(newUser.contains(userid))
					{
					File File = new File();
					File=setProperties(node,File,userid);
					Files.getFileList().add(File);}
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
		FileList1.setFileListResult(Files);
		FileList1.setSuccess(true);
		return FileList1;
	}
	

	public FileListReturn listRecycledDoc(String userid,String path) {
		FileListReturn FileList1 = new FileListReturn();
		ArrayOfFiles Files = new ArrayOfFiles();
		Files=listRecycledDocRecursion(userid, "/"+userid+"/trash", Files);
		FileList1.setFileListResult(Files);
		FileList1.setSuccess(true);
		return FileList1;
	}

	public ArrayOfFiles listRecycledDocRecursion(String userid,String path,ArrayOfFiles Files) {
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
					System.out.println("recycled nodes are "+node.getPath());
					if(node.getProperty(Config.EDMS_AUTHOR).getString().equals(userid)){
						File File = new File();
						File=setProperties(node,File,userid);
						Files.getFileList().add(File);
					//}
				/*}else if(node.hasNodes()){
					listRecycledDocRecursion(userid,node.getPath().toString(),Files);
				}*/}
			}
		} catch (LoginException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} finally {
		//	jcrsession.logout();
		}
		return Files;
	}

	public String recycleFile(String FilePath,String userid )
	{
		String response="";
		try {
			Node root = jcrsession.getRootNode();
			root=root.getNode(FilePath.substring(1));
			if(root.hasProperty(Config.USERS_DELETE)){
				Value[] actualUsers = root.getProperty(Config.USERS_DELETE).getValues();
				String newUser="";
				for (int i = 0; i < actualUsers.length; i++) {
					newUser+=actualUsers[i].getString()+",";
				}
				if(newUser.contains(userid)||root.getProperty(Config.EDMS_AUTHOR).getString().equals(userid)){
			recycleFileRecursion(root,userid);
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
	
	public void recycleFileRecursion(Node root,String userid){
		try{
		Node parent=root.getParent();
		//root.setProperty(Config.EDMS_RECYCLE_DOC, true);
		root.setProperty(Config.EDMS_RESTORATION_PATH, root.getPath());
		jcrsession.save();
		jcrsession.getWorkspace().copy(root.getPath(), "/"+userid+"/trash/"+root.getName());
		jcrsession.save();
		root.remove();
		jcrsession.save();
		int no_of_Files=Integer.parseInt(parent.getProperty(Config.EDMS_NO_OF_FOLDERS).getString());
		if(no_of_Files>0){
		parent.setProperty(Config.EDMS_NO_OF_FOLDERS,no_of_Files-1);
		}
		jcrsession.save();
		/*	if(root.hasNodes()){
			for (NodeIterator nit = root.getNodes(); nit.hasNext();) {
				Node node = nit.nextNode();
				recycleFileRecursion(node,userid);
			}
		}*/
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		
	}
	public String deleteFile(String FilePath,String userid )
	{
		String response="";
		try {
			Node root = jcrsession.getRootNode();
			root=root.getNode(FilePath.substring(1));
			root.remove();
			jcrsession.save();
			response= "success";
		} catch (RepositoryException e) {
			response= "Exception Occured";
		}
		return response;
	}

	public String restoreFile(String FilePath, String userid) {
		String response="";
		try {
			Node root = jcrsession.getRootNode();
			root=root.getNode(FilePath.substring(1));
			restoreFileRecursion(root,userid);
			response= "success";
		}catch (RepositoryException e) {
			response= "Exception Occured";
			e.printStackTrace();
		}
		return response;
	}
	

	public void restoreFileRecursion(Node root,String userid){
		try{
			String parents=root.getProperty(Config.EDMS_RESTORATION_PATH).getString().substring(1);
			parents=parents.substring(0,parents.lastIndexOf("/"));
			System.out.println("parent is "+parents);
			Node jcrRoot=jcrsession.getRootNode();
			Node parent;
		if(jcrRoot.hasNode(parents)){
			parent = jcrRoot.getNode(parents);
		}else{
			parent=createFileRecursionWhenNotFound(parents,userid);
		}
		//root.setProperty(Config.EDMS_RECYCLE_DOC, false);
		System.out.println(root.getPath()+" source to : "+root.getProperty(Config.EDMS_RESTORATION_PATH).getString());
		root.getSession().getWorkspace().copy(root.getPath(), root.getProperty(Config.EDMS_RESTORATION_PATH).getString());
		jcrsession.save();
		root.remove();
		jcrsession.save();
		int no_of_Files=Integer.parseInt(parent.getProperty(Config.EDMS_NO_OF_FOLDERS).getString());
		parent.setProperty(Config.EDMS_NO_OF_FOLDERS,no_of_Files+1);
		jcrsession.save();
		/*	if(root.hasNodes()){
			for (NodeIterator nit = root.getNodes(); nit.hasNext();) {
				Node node = nit.nextNode();
				restoreFileRecursion(node);
			}
		}*/
	} catch (RepositoryException e) {
		e.printStackTrace();
	}
}
	public Node createFileRecursionWhenNotFound(String FileName,String userid){
		Node file = null;
		try {
			Node root = jcrsession.getRootNode();
			System.out.println("in File creation recursion "+FileName.substring(0,FileName.lastIndexOf("/")+1));
			String parent=FileName.substring(0,FileName.lastIndexOf("/"));
			if(root.hasNode(parent)){
				root=root.getNode(parent);
				file = root.addNode(FileName.substring(FileName.lastIndexOf("/")+1), Config.EDMS_DOCUMENT);
				file.setProperty(Config.USERS_READ, new String[] {  });
				file.setProperty(Config.USERS_WRITE, new String[] {  });
				file.setProperty(Config.USERS_DELETE, new String[] {  });
				file.setProperty(Config.USERS_SECURITY, new String[] {  });
				file.setProperty(Config.GROUPS_READ, new String[] {  });
				file.setProperty(Config.GROUPS_WRITE, new String[] {  });
				file.setProperty(Config.GROUPS_DELETE, new String[] {  });
				file.setProperty(Config.GROUPS_SECURITY, new String[] {  });
				file.setProperty(Config.EDMS_KEYWORDS, "root,File".split(","));
				file.setProperty(Config.EDMS_AUTHOR, userid);
				file.setProperty(Config.EDMS_DESCRIPTION, "this is system created File while restoration");
				file.setProperty(Config.EDMS_CREATIONDATE,(new Date()).toString());
				file.setProperty(Config.EDMS_MODIFICATIONDATE,(new Date()).toString());
				file.setProperty(Config.EDMS_RECYCLE_DOC, false);
				//file.setProperty(Config.EDMS_NO_OF_FileS, 0);
				//file.setProperty(Config.EDMS_NO_OF_DOCUMENTS, 0);
				file.addMixin(JcrConstants.MIX_SHAREABLE);
				file.addMixin(JcrConstants.MIX_VERSIONABLE);
				jcrsession.save();
			}else{
				createFileRecursionWhenNotFound(FileName.substring(0,FileName.lastIndexOf("/")+1),userid);
			}
			
		} catch (LoginException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return file;
	
	}

	public RenameFileRes renameFile(String oldFilePath, String newFilePath,String userid) {
		RenameFileRes response=new RenameFileRes();
		try {
			
			Node forVer=jcrsession.getRootNode().getNode(oldFilePath.substring(1));
			System.out.println(forVer.getProperty(Config.EDMS_AUTHOR).getString());
			if(forVer.getProperty(Config.EDMS_AUTHOR).getString().equals(userid)){
			forVer.checkin();
			jcrsession.move(oldFilePath, oldFilePath.substring(0,oldFilePath.lastIndexOf("/")) + "/" + newFilePath);
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
		}
		return response;
	}
	
}