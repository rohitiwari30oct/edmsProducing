/*package hello;

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

import io.spring.guides.gs_producing_web_service.Country;
import io.spring.guides.gs_producing_web_service.Currency;

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
import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.apache.jackrabbit.commons.cnd.ParseException;
import org.apache.jackrabbit.core.TransientRepository;
import org.apache.jackrabbit.core.security.principal.EveryonePrincipal;
import org.apache.tika.metadata.Office;

import com.edms.folder.ArrayOfFolders;
import com.edms.folder.Folder;
import com.edms.folder.FolderListReturn;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import edms.core.Config;

@Component
public class GroupRepository {
	private static final List<Country> countries = new ArrayList<Country>();

	@Autowired
	FolderService folderService;

//	@Autowired DefaultSpringSecurityContextSource contextSource;
	
	@PostConstruct
	public void initData() {
		
		
		
		
		Country spain = new Country();
		spain.setName("Spain");
		spain.setCapital("Madrid");
		spain.setCurrency(Currency.EUR);
		spain.setPopulation(46704314);

		countries.add(spain);

		Country poland = new Country();
		poland.setName("Poland");
		poland.setCapital("Warsaw");
		poland.setCurrency(Currency.PLN);
		poland.setPopulation(38186860);

		countries.add(poland);

		Country uk = new Country();
		uk.setName("United Kingdom");
		uk.setCapital("London");
		uk.setCurrency(Currency.GBP);
		uk.setPopulation(63705000);

		countries.add(uk);
	}
	public FolderListReturn listFolder(String name,String userid) {
		Assert.notNull(name);
		FolderListReturn folderList1 = new FolderListReturn();
		ArrayOfFolders folders = new ArrayOfFolders();
		Node root=null;
		//System.out.println(contextSource.getBaseLdapName());
		
		
		
		Repository repository = new TransientRepository();
		Session jcrsession=null;
		try {
			System.out.println(userid);
			jcrsession = repository.login(new SimpleCredentials("admin","admin".toCharArray()));
			
			
						jcrsession = repository.login(new SimpleCredentials(
								userid,"redhat".toCharArray()));
						//Workspace ws=jcrsession.getWorkspace();
						//ws.createWorkspace(userid);
						
						System.out.println("principal is : "+jcrsession.getUserID());
						root =jcrsession.getRootNode();
						JackrabbitSession js = (JackrabbitSession) jcrsession;
						User user = ((User) js.getUserManager().getAuthorizable(userid));
						//System.out.println("user path is : "+user.getPath());
						AccessControlManager accessControlManager=jcrsession.getAccessControlManager();
						Privilege[] privileges=			accessControlManager.getSupportedPrivileges(root.getPath());
						for (int i = 0; i < privileges.length; i++) {
							System.out.println(privileges[i]);
						}
						AccessControlManager aMgr = jcrsession.getAccessControlManager();
						privileges=	aMgr.getPrivileges(root.getPath());
						System.out.println("privilleges");
							for (int i = 0; i < privileges.length; i++) {
								System.out.println(privileges[i]);
							}
						// create a privilege set with jcr:all
						privileges = new Privilege[] { aMgr.privilegeFromName(Privilege.JCR_ALL) };
						AccessControlList acl;
						try {
						    // get first applicable policy (for nodes w/o a policy)
						    acl = (AccessControlList) aMgr.getApplicablePolicies(path).nextAccessControlPolicy();
						} catch (NoSuchElementException e) {
						    // else node already has a policy, get that one
						    acl = (AccessControlList) aMgr.getPolicies(path)[0];
						}
						// remove all existing entries
						for (AccessControlEntry e : acl.getAccessControlEntries()) {
						    acl.removeAccessControlEntry(e);
						}
						// add a new one for the special "everyone" principal
						JackrabbitSession js = (JackrabbitSession) jcrsession;
						User user = ((User) js.getUserManager().getAuthorizable(userid));
						System.out.println("user path is : "+user.getPath());
						//Group grp=js.getUserManager().createGroup("top-management");
						Group grp= ((Group) js.getUserManager().getAuthorizable("top-management"));
						System.out.println("group path is : "+grp.getID()+" user id is : "+user.getID());
						//	Group group=(Group) js.getPrincipalManager().getGroupMembership(user.getPrincipal());
						boolean isAdmin = user.isAdmin();
						acl.addAccessControlEntry(user.getPrincipal(), privileges);
						// the policy must be re-set
						aMgr.setPolicy(path, acl);
						// and the session must be saved for the changes to be applied
						jcrsession.save();
				System.out.println("root before add user "+root.getPath().toString());
			// usual entry point into the Jackrabbit API
			JackrabbitSession js = (JackrabbitSession) jcrsession;
			User user = ((User) js.getUserManager().getAuthorizable(jcrsession.getUserID()));
			boolean isAdmin = user.isAdmin();
			System.out.println(isAdmin);
			final UserManager userManager = js.getUserManager();
			user = (User) userManager.getAuthorizable("sanjay@avi-oil.com");
			user.remove();
			jcrsession.save();
			user = userManager.createUser("sanjay@avi-oil.com", "redhat");
			jcrsession.save();
			root =jcrsession.getRootNode();
			System.out.println("root after add user "+root.getPath().toString());
				//root=root.getNode(userid);
			//root=root.getNode(userid);
			System.out.println("root is : "+root);
			jcrsession.getUserID();
			Workspace ws =	jcrsession.getWorkspace();
			System.out.println("workspace is : "+ws);
				NamespaceRegistry nr=ws.getNamespaceRegistry();
				System.out.println("namespace registry is : "+nr.getPrefixes().length);
				for(String str:nr.getPrefixes()){
					System.out.println(str);
				}
				for(String str:nr.getURIs()){
					System.out.println(str);
				}
		for (NodeTypeIterator iterator =ws.getNodeTypeManager().getAllNodeTypes(); iterator.hasNext();) {
			
			NodeType nodeType=(NodeType) iterator.next();
			System.out.println(nodeType.getName());
		
		}		
		nr.registerNamespace("edms", "http://www.edms.com/1.0");
				System.out.println("in repository");
				 try { InputStream is = ClassLoader.class.getResourceAsStream("/edms/module/jcr/CustomNodes.cnd");

					System.out.println("Input Stream is : "+is);
				 Reader cnd = new InputStreamReader(is);
				
					NodeType[] nodeTypes = CndImporter.registerNodeTypes(cnd, jcrsession);
					System.out.println(nodeTypes.length);
				} catch (ParseException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			if (name.length() > 1) {
				
				if(!root.hasNode(name.substring(1)))
				{
					Node folder=	root.addNode(userid, "edms:folder");
					root.setProperty("edms:keywords", "userrootfolder".split(","));
					root.setProperty("edms:author", jcrsession.getUserID());
					root.setProperty("edms:description", "userrootfolder");
					jcrsession.save();
				}else{
					root = root.getNode(name.substring(1));
				//}
			}
			Node hello = root.addNode("hello");
			jcrsession.save();
			for (NodeIterator nit =  root.getNodes(); nit
					.hasNext();) {
				Node node = nit.nextNode();
				System.out.println("node type is : "+node.getPrimaryNodeType().getName());
				if("edms:folder".equals(node.getPrimaryNodeType().getName())){
				Folder folder = new Folder();
				folder.setFolderName(node.getName());
				System.out.println("path is : " + node.getPath());
				System.out.println("created by : " + node.getProperty("edms:author").getString());
				folder.setFolderPath(node.getPath());
				folders.getFolderList().add(folder);}
			}
		} catch (LoginException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		finally{
			System.out.println("logout");
			jcrsession.logout();
		}
		folderList1.setFolderListResult(folders);
		folderList1.setSuccess(true);
		return folderList1;
	}
	
	
	public Boolean hasChild(String folderPath,String userid) {
		Repository repository = new TransientRepository();
		Session jcrsession=null;
		boolean flag=false;
		try {
			jcrsession = repository.login(new SimpleCredentials(
					Config.JCR_USERNAME,Config.JCR_PASSWORD.toCharArray()));
			if (!node.getName().equals("jcr:system")
					&& (!node.getProperty(JcrConstants.JCR_PRIMARYTYPE)
							.getString().equals(JcrConstants.NT_RESOURCE))) {
			Node root = jcrsession.getRootNode();
			if (folderPath.length() > 1) {
				root = root.getNode(folderPath.substring(1));
			}
			flag=root.hasNodes();
		} catch (LoginException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}finally{
			jcrsession.logout();
		}
		
		return flag;
	}
	public Folder createFolder(String folderName,String parentFolder,String userid,String keywords,String description) {
		Repository repository = new TransientRepository();
		Session jcrsession=null;
		Node folder=null;
		Folder folder1=new Folder();
		try {
			jcrsession = repository.login(new SimpleCredentials(userid,"redhat".toCharArray()));
			Node root = jcrsession.getRootNode();
			if(parentFolder.length()>1){
			root=root.getNode(parentFolder.substring(1));
			}
			System.out.println(jcrsession.hasPermission(root.getPath(), "add_node"));
			System.out.println("parent folder is : "+parentFolder);
			folder = root.addNode(folderName,"edms:folder");
			//	Value[] values=keywords.split(",");
			//	String[] value=Value2String(values);
			folder.setProperty("edms:keywords", keywords.split(","));
			folder.setProperty("edms:author", jcrsession.getUserID());
			folder.setProperty("edms:description", description);
			folder.addMixin(JcrConstants.MIX_SHAREABLE);
			//folder.setProperty("edms:note", notes);
			folder1.setCreationDate(folder.getProperty(Property.JCR_CREATED).getString());
			//folder1.setModificationDate(folder.getProperty(Property.JCR_LAST_MODIFIED_BY).getString());
			System.out.println("folder path is : "+folder.getPath().toString());
			folder1.setFolderName(folder.getName().toString());
			folder1.setFolderPath(folder.getPath().toString());
			folder1.setNotes(description);
			folder1.setCreatedBy(folder.getProperty("edms:author").toString());
			//folder1.set
			for (String string : values) {
				folder1.getKeywords().add(string);
			}
			jcrsession.save();

		} catch (LoginException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			
			e.printStackTrace();
		}finally{
			jcrsession.logout();
		}
		return folder1;
	}
	public Folder getFolderByPath(String folderPath,String userid) {
		System.out.println("in repository");
		Repository repository = new TransientRepository();
		Session jcrsession=null;
		Folder folder1=new Folder();
		try {
			jcrsession = repository.login(new SimpleCredentials(userid,"redhat".toCharArray()));
			
			Node root = jcrsession.getRootNode();
			if(folderPath.length()>1){
			root=root.getNode(folderPath.substring(1));
			}
			//folder.setProperty("edms:note", notes);
			//folder1.setCreationDate(root.getProperty(Property.JCR_CREATED).getString());
			//folder1.setModificationDate(folder.getProperty(Property.JCR_LAST_MODIFIED_BY).getString());
			System.out.println("folder path is : "+root.getPath().toString());
			folder1.setFolderName(root.getName().toString());
			folder1.setFolderPath(root.getPath().toString());
			folder1.setNotes(root.getProperty("edms:description").toString());
			folder1.setCreatedBy(root.getProperty("edms:author").toString());
			
	} catch (LoginException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (RepositoryException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}finally{
		jcrsession.logout();
	}
		return folder1;
		}
	public boolean shareFolderByPath(String folderPath,String userid,String shareWithFolder) {
		Repository repository = new TransientRepository();
		Session jcrsession=null;
		Folder folder1=new Folder();
		try {
			jcrsession = repository.login(new SimpleCredentials(Config.JCR_USERNAME,Config.JCR_PASSWORD.toCharArray()));
			Node root = jcrsession.getRootNode();
			if(folderPath.length()>1){
			root=root.getNode(folderPath.substring(1));
			}
			Workspace ws=jcrsession.getWorkspace();
			System.out.println(repository.OPTION_SHAREABLE_NODES_SUPPORTED+" shareable or not  "+root.getPath().toString()+"^^^^^^^^^^^^^^^^^ sharing to "+"/santosh@avi-oil.com/"+root.getPath().toString().substring(root.getPath().toString().lastIndexOf("/")));
			ws.clone(ws.getName(),folderPath , shareWithFolder, false);
			
			
	} catch (LoginException e) {
		e.printStackTrace();
		return false;
	} catch (RepositoryException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return false;
	}finally{
		jcrsession.logout();
	}
		return true;
		}
	*//**
	 * Convert a Value array to String array.
	 *//*
	public static String[] Value2String(Value[] values) throws ValueFormatException, IllegalStateException, javax.jcr.RepositoryException {
		ArrayList<String> list = new ArrayList<String>();
		
		for (int i=0; i<values.length; i++) {
				list.add(values[i].getString()); 
		}
		
		return (String[]) list.toArray(new String[list.size()]);
	}
}
*/