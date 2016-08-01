package edms.repository;

import javax.annotation.PostConstruct;
import javax.jcr.AccessDeniedException;
import javax.jcr.Binary;
import javax.jcr.Item;
import javax.jcr.LoginException;
import javax.jcr.NamespaceRegistry;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.UnsupportedRepositoryOperationException;
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
import javax.jcr.version.VersionManager;
import javax.print.attribute.standard.MediaSize.NA;
import javax.security.auth.Subject;
import javax.sql.rowset.spi.SyncResolver;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.activiti.engine.impl.persistence.entity.UserIdentityManager;
import org.apache.commons.collections.bag.SynchronizedBag;
import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlEntry;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlManager;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlPolicy;
import org.apache.jackrabbit.api.security.principal.PrincipalIterator;
import org.apache.jackrabbit.api.security.principal.PrincipalManager;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.core.TransientRepository;
import org.apache.jackrabbit.core.security.principal.AdminPrincipal;
import org.apache.jackrabbit.rmi.remote.RemoteRepository;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.apache.jackrabbit.rmi.server.RemoteAdapterFactory;
import org.apache.jackrabbit.rmi.server.ServerAdapterFactory;
import org.apache.jackrabbit.util.Text;
import org.apache.tika.Tika;
import org.hibernate.Query;
import org.hibernate.SessionFactory;

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
import com.edms.documentmodule.SearchDocByDateResponse;
import com.edms.documentmodule.SearchDocByLikeResponse;
import com.edms.documentmodule.SortByPropertyRes;
import com.edms.documentmodule.VCFFileAtt;
import com.edms.documentmodule.VCFFileListReturn;
import com.edms.entity.Quota;
import com.edms.entity.SnTable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import edms.core.Config;
import edms.core.JcrRepositorySession;
import edms.core.JcrRepositoryUtils;
import edms.core.LDAPConnection;
import edms.core.SessionWrapper;
import ezvcard.*;
import ezvcard.io.text.VCardReader;
import ezvcard.property.*;

@Component
public class FileRepository{
	@Autowired
	private WebServiceConfig webServiceConfig;
	
	public static Session GetSession( String userid,String password,final Repository repository) throws PrivilegedActionException {
		Session session = null;
		try {
			session = repository.login( new SimpleCredentials(userid, password.toCharArray()));
			SessionImpl si = (SessionImpl) session;
			JackrabbitSession js = ((JackrabbitSession) session);
			Subject subject = ((SessionImpl) js).getSubject();
			Set<Principal> principals = new LinkedHashSet<Principal>();
			principals = subject.getPrincipals();
			Subject combinedSubject=new Subject(false,subject.getPrincipals(),subject.getPublicCredentials(),subject.getPrivateCredentials());
			combinedSubject.getPrincipals().add(new AdminPrincipal("admin"));
			try {
				session = Subject.doAsPrivileged(combinedSubject,
						new PrivilegedExceptionAction<Session>() {
							public Session run() throws Exception {
								Session ss = repository.login();
								//Session ss = repository.login();
								return ss;
							}
						}, AccessController.getContext());
			} catch (PrivilegedActionException e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return session;
	}
	public static void setPolicyForDenyTest(String userid,String password,Session session) throws PrivilegedActionException, AccessDeniedException, UnsupportedRepositoryOperationException, RepositoryException{
		try{
		//  usual entry point into the Jackrabbit API
		//	SessionWrapper sessions =JcrRepositoryUtils.login(userid, password);
		//	Session jcrsession = sessions.getSession();
		 
		//	JcrRepositorySession.createFolder(userid, session);
		JackrabbitSession js = (JackrabbitSession) session;
		// get user/principal for whom to read/set ACLs
		// Note: the ACL security API works using Java Principals as high-level abstraction and does not
		// assume the users are actually stored in the JCR with the Jackrabbit UserManagement; an example
		// are external users provided by a custom LoginModule via LDAP
		PrincipalManager pMgr = js.getPrincipalManager();
		Principal principal = pMgr.getPrincipal(userid.substring(0,userid.lastIndexOf("@")));

		// get the Jackrabbit access control manager
		JackrabbitAccessControlManager acMgr = (JackrabbitAccessControlManager) session.getAccessControlManager();
		JackrabbitAccessControlPolicy[] ps = acMgr.getPolicies(principal); // or getApplicablePolicies()
		if(ps.length==0){
			ps = acMgr.getApplicablePolicies(principal);
		}
		JackrabbitAccessControlList list = (JackrabbitAccessControlList) ps[0];
		// list entries
		AccessControlEntry[] entries =  list.getAccessControlEntries();
		for (int i = 0; i < entries.length; i++) {
			list.removeAccessControlEntry(entries[i]);
		}
		// add entry
		Privilege[] privileges = new Privilege[] { acMgr.privilegeFromName(Privilege.JCR_ALL) };
		Map<String, Value> restrictions = new HashMap<String, Value>();
		ValueFactory vf = session.getValueFactory();
		restrictions.put("rep:nodePath", vf.createValue("/", PropertyType.PATH));
		restrictions.put("rep:glob", vf.createValue("*"));
		boolean b=list.addEntry(principal, privileges, false /* allow or deny */, restrictions);
		System.out.println("DateTime: "+new Date()+" ::::: "+"deny to / is : "+b);
		// reorder entries
		//list.orderBefore(entry, entry2);
		// finally set policy again & save
		acMgr.setPolicy(list.getPath(), list);
		session.save();
		}catch(Exception e){
			e.printStackTrace();
		}finally{}
	}

	public static void setPolicyForAllowTest(String path,String userid,String password,Session session) throws PrivilegedActionException, AccessDeniedException, UnsupportedRepositoryOperationException, RepositoryException{
		try{
		JackrabbitSession js = (JackrabbitSession) session;
		System.out.println("DateTime: "+new Date()+" :::: "+session.getWorkspace().getName());
		PrincipalManager pMgr = js.getPrincipalManager();
		Principal principal = pMgr.getPrincipal(userid);
		JackrabbitAccessControlManager acMgr = (JackrabbitAccessControlManager) session.getAccessControlManager();
		JackrabbitAccessControlPolicy[] ps = acMgr.getPolicies(principal); // or getApplicablePolicies()
		if(ps.length==0){
			ps = acMgr.getApplicablePolicies(principal);
		}
		JackrabbitAccessControlList list = (JackrabbitAccessControlList) ps[0];
		// list entries
		AccessControlEntry[] entries =  list.getAccessControlEntries();
		for (int i = 0; i < entries.length; i++) {
			list.removeAccessControlEntry(entries[i]);
		}
		System.out.println();
		// add entry
		Privilege[] privileges = new Privilege[] { acMgr.privilegeFromName(Privilege.JCR_ALL) };
		Map<String, Value> restrictions = new HashMap<String, Value>();
		ValueFactory vf = session.getValueFactory();
		restrictions.put("rep:nodePath", vf.createValue(path, PropertyType.PATH));
		restrictions.put("rep:glob", vf.createValue("*"));
		boolean b=list.addEntry(principal, privileges, true /* allow or deny */, restrictions);
		System.out.println("DateTime: "+new Date()+" ::::: "+"allow to "+path +" is : "+b);
		acMgr.setPolicy(list.getPath(), list);
		session.save();
		}catch(Exception e){
			e.printStackTrace();
		}finally{}
	}
	public static int count=0;
	private static Version traverseVersionStorage(Node n, int level) throws Exception {
		Version v = null;
		for (NodeIterator it = n.getNodes(); it.hasNext();) {
			Node n2 = it.nextNode();
			if (n2 instanceof Version && !n2.getName().startsWith("jcr:")) {
				v = (Version) n2;
				System.out.println("DateTime: "+new Date()+" ::::: "+"version " + n2.getName() + " of node "+ n2.getParent().getName() + ":");
				Node n3 = n2.getNode("jcr:frozenNode");
						count++;
				for (PropertyIterator pt = n3.getProperties(); pt.hasNext();) {
					Property p = pt.nextProperty();
					if (!p.getName().startsWith("jcr:")) {
						System.out.println("DateTime: "+new Date()+" ::::: "+"  "	+ p.getName()	+ "="+(p.isMultiple() ? p.getValues().toString(): p.getValue().getString()));
					}
				}
				try{
					SessionWrapper sessions =JcrRepositoryUtils.login("sanjay@avi-oil.com", "redhat");
					Session session = sessions.getSession();
					VersionManager vman=session.getWorkspace().getVersionManager();
					Node parent=n3.getParent();
					System.out.println("DateTime: "+new Date()+" ::::: "+parent.getName());
					VersionHistory versionHistory = vman.getVersionHistory(n3.getPath());
					versionHistory.removeVersion(n3.getName());
					//if(n3.hasProperty(Config.EDMS_RESTORATION_PATH))
					//vman.restore(n3.getProperty(Config.EDMS_RESTORATION_PATH).getString(), v, true);
					parent.remove();
					session.save();
					}catch(Exception e ){
						e.printStackTrace();
					}
				//System.out.println("DateTime: "+new Date()+" ::::: "+);
			}
			Version v2 = traverseVersionStorage(n2, level + 1);
			v = v == null ? v2 : v;
		}
		return v;
	}
	
	
	@PostConstruct
	public void initData() throws Exception {
	//	Repository rep=JcrRepositoryUtils.getRepositoryHandle();
	//	GetSession("nirbhay@silvereye.in", "google@2009", JcrRepositoryUtils.getRepositoryHandle());
	//	SessionWrapper sessions =JcrRepositoryUtils.login("sanjay@avi-oil.com", "redhat");
	//	Session session = sessions.getSession();
		/*VersionManager vman=session.getWorkspace().getVersionManager();*/
		//Node root=session.getNode("/sanjay@avi-oil.com");

		//Node ntt=root.getNode("dff");
		//vman.checkout("/sanjay@avi-oil.com/dff");
		//ntt.setProperty(Config.EDMS_RESTORATION_PATH, "Cmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm");
		///session.save();
		//vman.checkin("/sanjay@avi-oil.com/dff");
		//ntt.remove();
		//session.save();
		/*	
		 Node vs = session.getRootNode().getNode("jcr:system").getNode("jcr:versionStorage");
		Version v = traverseVersionStorage(vs, 0);*/
	//	Repository repository=		JcrRepositoryUtils.getRepositoryHandle();
		/*
		
			String password=Config.EDMS_PASSWORD;
			Session session=null;
			
			session = GetSession(username,password,JcrRepositorySession.getRepository());
			
			
			setPolicyForAllowTest("/","edms@avi-oil.com",password,session);
			setPolicyForDenyTest("edms@avi-oil.com",password,session);
			setPolicyForAllowTest("/edms@avi-oil.com","edms@avi-oil.com",password,session);
			
			setPolicyForAllowTest("/","sanjay@avi-oil.com",password,session);
			setPolicyForDenyTest("sanjay@avi-oil.com",password,session);
			setPolicyForAllowTest("/sanjay@avi-oil.com","sanjay@avi-oil.com",password,session);
			
			setPolicyForAllowTest("/","santosh@avi-oil.com",password,session);
			setPolicyForDenyTest("santosh@avi-oil.com",password,session);
			setPolicyForAllowTest("/santosh@avi-oil.com","santosh@avi-oil.com",password,session);
			
			setPolicyForAllowTest("/","administrator@avi-oil.com",password,session);
			setPolicyForDenyTest("administrator@avi-oil.com",password,session);
			setPolicyForAllowTest("/administrator@avi-oil.com","administrator@avi-oil.com",password,session);
			
			setPolicyForAllowTest("/","kamal@avi-oil.com",password,session);
			setPolicyForDenyTest("kamal@avi-oil.com",password,session);
			setPolicyForAllowTest("/kamal@avi-oil.com","kamal@avi-oil.com",password,session);
			
			setPolicyForAllowTest("/","rahul@avi-oil.com",password,session);
			setPolicyForDenyTest("rahul@avi-oil.com",password,session);
			setPolicyForAllowTest("/rahul@avi-oil.com","rahul@avi-oil.com",password,session);
			
			setPolicyForAllowTest("/","monica@avi-oil.com",password,session);
			setPolicyForDenyTest("monica@avi-oil.com",password,session);
			setPolicyForAllowTest("/monica@avi-oil.com","monica@avi-oil.com",password,session);
			
			setPolicyForAllowTest("/","arun@avi-oil.com",password,session);
			setPolicyForDenyTest("arun@avi-oil.com",password,session);
			setPolicyForAllowTest("/arun@avi-oil.com","arun@avi-oil.com",password,session);
			
			setPolicyForAllowTest("/","krishan@avi-oil.com",password,session);
			setPolicyForDenyTest("krishan@avi-oil.com",password,session);
			setPolicyForAllowTest("/krishan@avi-oil.com","krishan@avi-oil.com",password,session);
			
			setPolicyForAllowTest("/","bhavna@avi-oil.com",password,session);
			setPolicyForDenyTest("bhavna@avi-oil.com",password,session);
			setPolicyForAllowTest("/bhavna@avi-oil.com","bhavna@avi-oil.com",password,session);
			
			setPolicyForAllowTest("/","amit.kumar@avi-oil.com",password,session);
			setPolicyForDenyTest("amit.kumar@avi-oil.com",password,session);
			setPolicyForAllowTest("/amit.kumar@avi-oil.com","amit.kumar@avi-oil.com",password,session);
			
			setPolicyForAllowTest("/","ambika@avi-oil.com",password,session);
			setPolicyForDenyTest("ambika@avi-oil.com",password,session);
			setPolicyForAllowTest("/ambika@avi-oil.com","ambika@avi-oil.com",password,session);




//usual entry point into the Jackrabbit API
Repository repository=		JcrRepositoryUtils.getRepositoryHandle();
		Session	jcrsession = repository.login( new SimpleCredentials("sanjay@avi-oil.com", "redhat".toCharArray()),"avi-oil.com");
*

SessionWrapper sessions =JcrRepositoryUtils.login("sanjay@avi-oil.com", "redhat");
Session session = sessions.getSession();
VersionManager vman=session.getWorkspace().getVersionManager();
Node root=session.getNode("/sanjay@avi-oil.com");

Node ntt=root.getNode("dff");
vman.checkout("/sanjay@avi-oil.com/dff");
ntt.setProperty(Config.EDMS_RESTORATION_PATH, "Cmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm");
session.save();
vman.checkin("/sanjay@avi-oil.com/dff");
ntt.remove();
session.save();
Node vs = session.getRootNode().getNode("jcr:system").getNode("jcr:versionStorage");
Version v = traverseVersionStorage(vs, 0);
// restore a version
//vman.restore("/sanjay@avi-oil.com/ABC", v, false);
//Node nty=session.getNode("/sanjay@avi-oil.com/ret");
System.out.println("DateTime: "+new Date()+" ::::: "+"Restored: "  );
//traverseVersionStorage(vs,  0);
System.out.println("DateTime: "+new Date()+" ::::: "+"..................................................................."+count);
for (NodeIterator nit = root.getNodes(); nit.hasNext();) {
Node rt=nit.nextNode();
	System.out.println("DateTime: "+new Date()+" ::::: "+rt.getPath());
	vman.restore("/sanjay@avi-oil.com/dff"+rt.getName(),rt.getBaseVersion(),true);

}

//Item root=jcrsession.getItem("/sanjay@avi-oil.com/ret/x");

String x="x";
String expression = "select * from [nt:frozenNode] ";

		String expression = "SELECT * FROM [nt:frozenNode] ";
		
		//AS s WHERE ISCHILDNODE(s,'"+name+"')and CONTAINS(s.[edms:owner],'*"+userid+"*')  ORDER BY s.["+Config.EDMS_Sorting_Parameter+"] ASC
		//expression = "select * from [edms:folder] AS s WHERE NAME like ['%san%']";
	   javax.jcr.query.Query query =  session.getWorkspace().getQueryManager().createQuery(expression, javax.jcr.query.Query.JCR_SQL2);
	    javax.jcr.query.QueryResult result = query.execute();
	//	System.out.println("DateTime: "+new Date()+" ::::: "+expression);
	//	System.out.println("DateTime: "+new Date()+" ::::: "+"size of list in list folder "+result);
		//NodeIterator nit2 =result.getNodes();
				                VersionManager vm = session.getWorkspace().getVersionManager();

				        		for (NodeIterator it = result.getNodes(); it.hasNext();) {

				    			Node n2 = it.nextNode();
				    			if (n2 instanceof Version && !n2.getName().startsWith("jcr:")) {
				    			Version	v = (Version) n2;
				    				System.out.println("DateTime: "+new Date()+" ::::: "+"version " + n2.getName() + " of node "+ n2.getParent().getName() + ":");
				    				Node n3 = n2.getNode("jcr:frozenNode");
				    						count++;
				    				for (PropertyIterator pt = n3.getProperties(); pt.hasNext();) {
				    					Property p = pt.nextProperty();
				    					if (!p.getName().startsWith("jcr:")) {
				    						//if((p.isMultiple() ? p.getValues().toString(): p.getValue().getString()).indexOf("Hacking")>=0){
				    						System.out.println("DateTime: "+new Date()+" ::::: "+"  "	+ p.getName()	+ "="+(p.isMultiple() ? p.getValues().toString(): p.getValue().getString()));
				    						
				    						//}
				    					}
				    				}
				    				try{
				    				//	SessionWrapper sessions =JcrRepositoryUtils.login("sanjay@avi-oil.com", "redhat");
				    				//	Session session = sessions.getSession();
				    				//	VersionManager vman=session.getWorkspace().getVersionManager();
				    					vman.restore("/sanjay@avi-oil.com/"+n3.getProperty(Config.EDMS_NAME).getString(), v, true);
				    				session.save();
				    					//	return v;
				    					}catch(Exception e ){
				    						
				    					}
				    				System.out.println("DateTime: "+new Date()+" ::::: "+);
				    			}
				    			Version v2 = traverseVersionStorage(n2,  1);
				    		
				        		}
				                
				                //    while (nit2.hasNext()) {
				             //       Node node = nit2.nextNode();
				             //       Version v = traverseVersionStorage(nit, 0);
				                    Node nd=session.getNodeByIdentifier(node.getUUID());
				                    
				                    System.out.println("DateTime: "+new Date()+" ::::: "+);
				                    if(node.hasProperty(Config.EDMS_NAME))
				                   { 
					                   
				                    		// Node nd=node.getBaseVersion();
				                    	//	if(node.isNodeType(JcrConstants.MIX_VERSIONABLE)){
				                    			System.out.println("DateTime: "+new Date()+" ::::: "+String.format("Processing file stored at %s ... and node id is %s", node.getPath(),node.getProperty(Config.EDMS_NAME).getString()));
					                    		try{
				                    			Version vh = (Version)node.getParent();
					                    		vm.restore("/sanjay@avi-oil.com/ret/"+node.getProperty(Config.EDMS_NAME).getString(),vh, true);
					                    		session.save();
					                    		}catch(Exception e ){
					                    			
					                    		}
					                    		//}
				                    }
				               //     }
				                    //   node.remove();
				                 //   session.save();
				                    for (VersionIterator vit = vh.getAllVersions();vit.hasNext(); ) {
				                        Version v = vit.nextVersion();
				                        String vname = v.getName();
				                       // if (!"jcr:rootVersion".equals(vname)) { // NOI18N
				                          //  vh.removeVersion(vname);
				                     
				                            System.out.println("DateTime: "+new Date()+" ::::: "+String.format(  " Deleted version %s", vname)); // NOI18N
				                        // }
				                    }
				                //}		
		
		
		
		
//root.restore(v1,"x", true);
		setPolicyForDeny("janak@avi-oil.com");
		setPolicyForTest("janak@avi-oil.com");
		setPolicyForSystem("janak@avi-oil.com","jcr:system");
	
	//setPolicyForSystem("janak@avi-oil.com","rep:policy");
	//JcrRepositorySession.getSession("sanjay");	
	
 		setPolicyForAllowTest("/","sanjay@avi-oil.com");
		setPolicyForDenyTest("sanjay@avi-oil.com");
		setPolicyForAllowTest("/sanjay@avi-oil.com","sanjay@avi-oil.com");
	

		//setPolicyForDeny("sanjay");
		//setPolicyForTest("sanjay");
		//setPolicyForSystem("sanjay","jcr:system");
		//setPolicyForSystem("sanjay","rep:policy");
		
		
		
		
		
		setPolicyForDeny("santosh@avi-oil.com");
		setPolicyForTest("santosh@avi-oil.com");
		setPolicyForSystem("santosh@avi-oil.com","jcr:system");
		//setPolicyForSystem("santosh@avi-oil.com","rep:policy");
		
		setPolicyForDeny("shibu@avi-oil.com");
		setPolicyForTest("shibu@avi-oil.com");
		setPolicyForSystem("shibu@avi-oil.com","jcr:system");
		//setPolicyForSystem("shibu@avi-oil.com","rep:policy");

		Repository	repository =JcrRepositoryUtils.getRepositoryHandle();
		try {
			// Start the RMI registry
			//Registry reg = LocateRegistry.createRegistry(1100);
			Repository repository =new URLRemoteRepository("http://localhost:8081/jackrabbit-webapp/repository");
			System.out.println("DateTime: "+new Date()+" ::::: "+);
			RemoteAdapterFactory factory = new ServerAdapterFactory();
			RemoteRepository remote = factory.getRemoteRepository(repository);
			Naming.bind("jackrabbit", remote);  // Make the RMI binding using java.rmi.Naming
			// Bind the repository reference to the registry
			ServerAdapterFactory factory = new ServerAdapterFactory();
			RemoteRepository remote = factory.getRemoteRepository(repository);
			reg.rebind("jackrabbit", remote);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	*/
		/*try {
			JcrRepositoryUtils.processFileDir(Config.EDMS_BULKUPLOAD_PATH);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	*/
		try{
		Session sessions =GetSession("admin", "admin",JcrRepositoryUtils.getRepositoryHandle());
		JcrRepositorySession.registerNamespace(sessions, sessions.getRootNode());
		//System.exit(0);
		}catch(Exception e){
			
		}
	}

	public FileListReturn listFile(String name, String userid,String password) {
		System.out.println("DateTime: "+new Date()+" ::::: "+"getting files from = "+name+" by user : "+userid);
		name=name.replace("'","&apos;");
		name=name.replace("<","&lt;");
		name=name.replace(">","&gt;");
		//name=name;
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		Assert.notNull(name);
		FileListReturn FileList1 = new FileListReturn();
		ArrayOfFiles Files = new ArrayOfFiles();
		try {
			
			javax.jcr.query.QueryManager queryManager;
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
					+ "')  and  (CONTAINS(s.[edms:owner],'*" + userid + "*') or CONTAINS(s.[edms:author],'*" + userid + "*'))    ORDER BY s.["+Config.EDMS_Sorting_Parameter+"] ASC";
			expression = "select * from [edms:document] AS s WHERE ISCHILDNODE(s,'"
					+ name
					+ "')      ORDER BY s.["+Config.EDMS_Sorting_Parameter+"] ASC";
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
				Node root=node.getParent();
				
				if (root.hasProperty(Config.EDMS_OWNER)&&(!node.hasProperty(Config.EDMS_OWNER))) {
						node.setProperty(Config.EDMS_OWNER,
							root.getProperty(Config.EDMS_OWNER).getString());
						jcrsession.save();
				} else {
//					node.setProperty(Config.EDMS_OWNER,
//							"admin" + ",," + userid);
			}
				if(node.hasProperty(Config.EDMS_OWNER)&&node.hasProperty(Config.EDMS_AUTHOR)){
					if(node.getProperty(Config.EDMS_OWNER).getString().indexOf(userid)>=0||node.getProperty(Config.EDMS_AUTHOR).getString().indexOf(userid)>=0)
					{
				File File = new File();
				File = setProperties(node, File, userid,password,jcrsession);
				Files.getFileList().add(File);
				}}else if(node.getPath().indexOf(userid)>=0){
					File File = new File();
					File = setProperties(node, File, userid,password,jcrsession);
					Files.getFileList().add(File);
					}
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
	public FileListReturn listFileDAV(String name, String userid,String password) {
		System.out.println("DateTime: "+new Date()+" ::::: "+"getting files from = "+name+" by user : "+userid);
		name=name.replace("'","&apos;");
		name=name.replace("<","&lt;");
		name=name.replace(">","&gt;");
		//name=name;
		SessionWrapper sessions =JcrRepositoryUtils.loginDAV(userid, password);
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		Assert.notNull(name);
		FileListReturn FileList1 = new FileListReturn();
		ArrayOfFiles Files = new ArrayOfFiles();
		try {
			
			javax.jcr.query.QueryManager queryManager;
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
					+ "')  and CONTAINS(s.[edms:owner],'*"+userid+"*')  ORDER BY s.["+Config.EDMS_Sorting_Parameter+"] ASC";
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
				File = setProperties(node, File, userid,password,jcrsession);
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
	public FileListReturn listFileWithOutStream(String name, String userid,String password) {
		System.out.println("DateTime: "+new Date()+" ::::: "+"getting files from = "+name+" by user : "+userid);
		name=name.replace("'","&apos;");
		name=name.replace("<","&lt;");
		name=name.replace(">","&gt;");
		//name=name;
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		Assert.notNull(name);
		FileListReturn FileList1 = new FileListReturn();
		ArrayOfFiles Files = new ArrayOfFiles();
		name=name.replace("'","&apos;");
		try {
			
			
			javax.jcr.query.QueryManager queryManager;
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
					+ "')    ORDER BY s.["+Config.EDMS_Sorting_Parameter+"] ASC";
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
				Node root=node.getParent();
				
				if (root.hasProperty(Config.EDMS_OWNER)&&(!node.hasProperty(Config.EDMS_OWNER))) {
						node.setProperty(Config.EDMS_OWNER,
							root.getProperty(Config.EDMS_OWNER).getString());
						jcrsession.save();
				} else {}
				/*
				if(node.hasProperty(Config.EDMS_OWNER)&&node.hasProperty(Config.EDMS_AUTHOR)){
					if(node.getProperty(Config.EDMS_OWNER).getString().indexOf(userid)>=0||node.getProperty(Config.EDMS_AUTHOR).getString().indexOf(userid)>=0)
					{
				File File = new File();
				File = setPropertiesWithoutStream(node, File, userid,password,jcrsession);
				Files.getFileList().add(File);
					}}else if(node.getPath().indexOf(userid)>=0){*/
						File File = new File();
						File = setProperties(node, File, userid,password,jcrsession);
						Files.getFileList().add(File);
						//}
				
				// }
				// }
				// }
			}	
		FileList1.setFileListResult(Files);
		FileList1.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			//JcrRepositoryUtils.logout(sessionId);
		}
	
		return FileList1;
	}
	public FileListReturn listFileWithOutStreamDAV(String name, String userid,String password) {
		System.out.println("DateTime: "+new Date()+" ::::: "+"getting files from = "+name+" by user : "+userid);
		name=name.replace("'","&apos;");
		name=name.replace("<","&lt;");
		name=name.replace(">","&gt;");
		//name=name;
		SessionWrapper sessions =JcrRepositoryUtils.loginDAV(userid, password);
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		Assert.notNull(name);
		FileListReturn FileList1 = new FileListReturn();
		ArrayOfFiles Files = new ArrayOfFiles();
		name=name.replace("'","&apos;");
		try {
			
			
			javax.jcr.query.QueryManager queryManager;
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
					+ "')  and CONTAINS(s.[edms:owner],'*"+userid+"*')  ORDER BY s.["+Config.EDMS_Sorting_Parameter+"] ASC";
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
				File = setPropertiesWithoutStream(node, File, userid,password,jcrsession);
				Files.getFileList().add(File);
				// }
				// }
				// }
			}	
		FileList1.setFileListResult(Files);
		FileList1.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			//JcrRepositoryUtils.logout(sessionId);
		}
	
		return FileList1;
	}

	public File setProperties(Node node, File file, String userid,String password,Session jcrsession) throws IOException {
		
		try {

			System.out.println("DateTime: "+new Date()+" ::::: "+"Setting properties of = "+node.getPath()+" by user : "+userid);
			file.setFileName(node.getName());
			file.setFilePath(node.getPath());
			file.setParentFolder(node.getParent().getName());
			/*if (node.hasProperty(Config.EDMS_NAME)) {
				System.out.println("DateTime: "+new Date()+" ::::: "+"title of doc is : "
						+ node.getProperty(Config.EDMS_NAME).getString());
			}*/
			if(node.hasNode(Config.EDMS_CONTENT)){
			Node ntResourceNode = node.getNode("edms:content");
			Binary binary=ntResourceNode.getProperty("jcr:data").getBinary();
			InputStream is = binary.getStream();
			try {
				byte[] imageBytes = IOUtils.toByteArray(is);
				byte[] encodedBaseData=org.apache.commons.codec.binary.Base64
						.encodeBase64(imageBytes);
				file.setFileContent(encodedBaseData);
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}finally{
				
				if(binary!=null)
				binary.dispose();
				if(is!=null)
				is.close();
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
	
	File setPropertiesWithoutStream(Node node, File file, String userid,String password,Session jcrsession){

		
		try {

			System.out.println("DateTime: "+new Date()+" ::::: "+"Setting properties of = "+node.getPath()+" by user : "+userid);
			file.setFileName(node.getName());
			file.setFilePath(node.getPath());
			file.setParentFolder(node.getParent().getName());
			/*if (node.hasProperty(Config.EDMS_NAME)) {
				System.out.println("DateTime: "+new Date()+" ::::: "+"title of doc is : "
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

			if (node.hasProperty(Config.EDMS_OWNER)){
				String actualUsers1 = node.getProperty(Config.EDMS_OWNER).getString();
				if(actualUsers1.indexOf("admin,,")<0&&actualUsers1.indexOf("admin,")>=0){
					String newUser = actualUsers1.replace("admin,", "admin,,");
					node.setProperty(Config.EDMS_OWNER,  newUser );
					jcrsession.save();
						
				}
		}
			
			if (node.hasProperty(Config.EDMS_AUTHOR))
				file.setCreatedBy(node.getProperty(Config.EDMS_AUTHOR)
						.getString());
			if (node.hasProperty(Config.EDMS_SIZE))
				file.setFileSize(node.getProperty(Config.EDMS_SIZE)
						.getLong());
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
			
/*
			if(node.getPath().indexOf("sadgfsdfg")>0&&userid.equals("jyoti@silvereye.in")){
				node.setProperty(Config.EDMS_OWNER, "admin,jyoti@silvereye.in");
				node.setProperty(Config.EDMS_AUTHOR, "jyoti@silvereye.in");
				jcrsession.save();
			}*/
			Workspace work=jcrsession.getWorkspace();
			VersionManager verManager=work.getVersionManager();
			VersionHistory history=verManager.getVersionHistory(node.getPath());
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
	File setGeneralFileProperties(Node node, File file, String userid,String password) {
		
		try {
			System.out.println("DateTime: "+new Date()+" ::::: "+"Setting general properties of = "+node.getPath()+" by user : "+userid);
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

	public Boolean hasChild(String FilePath, String userid,String password) {

		System.out.println("DateTime: "+new Date()+" ::::: "+"Getting child existance of = "+FilePath+" by user : "+userid);
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		boolean flag = false;
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
	public synchronized  CreateFileResponse  createFile(String fileName, String parentFile,
			String userid,String password, String keywords, String description, byte[] is,long filesize) {
		CreateFileResponse response = new CreateFileResponse();
	//	String entry=Config.LDAP_BASE+"="+userid+","+Config.LDAP_RDN;
	//	double quotaLimit=	LDAPConnection.readAttributeFromLdap(Config.LDAP_URL, entry, password, Config.LDAP_RDN);
	//	double quotaSize= checkQuotaValue(userid, filesize);
		//if(quotaLimit > quotaSize){
		System.out.println("DateTime: "+new Date()+" ::::: "+"Uploading file of = "+fileName+" in : "+parentFile+" by user : "+userid);
		if(is!=null){
		byte[] decodedBaseData=org.apache.commons.codec.binary.Base64.decodeBase64(is);
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		Node file = null;
		File file1 = new File();
		try {
			Node root = jcrsession.getRootNode();
			if (parentFile.length() > 1) {
				System.out.println("DateTime: "+new Date()+" ::::: "+"parent folder when creating File : "+parentFile);
				parentFile=parentFile.trim();
				root = root.getNode(parentFile.substring(1));
			}
			//int count=0;
			String fileNames=fileName;
			String ext=fileName.substring(fileName.lastIndexOf('.'));
			fileNames=fileName.substring(0,fileName.lastIndexOf('.'));
			if(!root.hasNode(fileNames+ext)){
			while(root.hasNode(fileNames+ext)){
			//	count++;
				fileNames+="-copy";
			}
			fileName=fileNames+ext;
			if(root.hasProperty(Config.USERS_SECURITY)) {
				Value[] actualUsers = root.getProperty(Config.USERS_SECURITY).getValues();
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
					/*		Version version = jcrsession.getWorkspace()
							.getVersionManager().checkin(root.getPath());
					System.out.println("DateTime: "+new Date()+" ::::: "+version.getName());
					jcrsession
							.getWorkspace()
							.getVersionManager()
							.getVersionHistory(root.getPath())
							.addVersionLabel(version.getName(),
									"new child named " + fileName + " added",
									true);*/

					/*Version version=jcrsession.getWorkspace().getVersionManager().checkin(root.getPath());
					System.out.println("DateTime: "+new Date()+" ::::: "+"Version of :"+root.getPath()+" has been created, Version name is : "+version.getName());
					jcrsession.getWorkspace().getVersionManager().checkout(root.getPath());
					*/
					file = root.addNode(fileName, Config.EDMS_DOCUMENT);
					
					if (root.hasProperty(Config.USERS_READ)
							&& (!root.getProperty(Config.EDMS_AUTHOR)
									.toString().equals(Config.EDMS_ADMIN))) {
						Value[] actualUser = root
								.getProperty(Config.USERS_READ).getValues();
						String newUser = "";
						for (int i = 0; i < actualUser.length; i++) {
							newUser += actualUser[i].getString() + ",,";
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
							newUser += actualUser[i].getString() + ",,";
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
							newUser += actualUser[i].getString() + ",,";
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
							newUser += actualUser[i].getString() + ",,";
						}
						file.setProperty(Config.USERS_SECURITY,
								newUser.split(","));
					} else {
						file.setProperty(Config.USERS_SECURITY, new String[] {});
					}

					if (root.hasProperty(Config.GROUPS_READ)
							&& !root.getProperty(Config.EDMS_AUTHOR).toString()
									.equals(Config.EDMS_ADMIN)) {
						Value[] actualUser = root.getProperty(Config.GROUPS_READ).getValues();
						String newUser = "";
						for (int i = 0; i < actualUser.length; i++) {
							newUser += actualUser[i].getString() + ",,";
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
							newUser += actualUser[i].getString() + ",,";
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
							newUser += actualUser[i].getString() + ",,";
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
							newUser += actualUser[i].getString() + ",,";
						}
						file.setProperty(Config.GROUPS_SECURITY,
								newUser.split(","));
					} else {
						file.setProperty(Config.GROUPS_SECURITY,
								new String[] {});
					}

					file.setProperty(Config.EDMS_KEYWORDS, keywords.split(","));
					file.setProperty(Config.EDMS_NAME, fileName);
					if(root.hasProperty(Config.EDMS_OWNER)){
						//System.out.println("DateTime: "+new Date()+" ::::: "+root.getProperty(Config.EDMS_OWNER).getString());
						file.setProperty(Config.EDMS_OWNER,root.getProperty(Config.EDMS_OWNER).getString()+","+userid);
					}else{
						file.setProperty(Config.EDMS_OWNER,root.getProperty(Config.EDMS_AUTHOR).getString());
					}
					
					
				/*	if (root.getProperty(Config.EDMS_AUTHOR).getString()
							.equals(Config.EDMS_ADMIN)) {*/

					file.setProperty(Config.EDMS_AUTHOR, userid);
					
					//file.setProperty(Config.EDMS_PATH, root.getPath()+"/"+fileName);

					/*} else {
						file.setProperty(Config.EDMS_AUTHOR,
								root.getProperty(Config.EDMS_AUTHOR)
										.getString());
					}*/
						//file.setProperty(Config.EDMS_DESCRIPTION, is);
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

					file.setProperty(Config.EDMS_SIZE, filesize);
					//file.addMixin(JcrConstants.MIX_SHAREABLE);
					file.addMixin(JcrConstants.MIX_VERSIONABLE);
					//String iss;
					InputStream	isss= new ByteArrayInputStream(decodedBaseData);

						/*try {
							System.out.println("DateTime: "+new Date()+" ::::: "+isss.available());
							file.setProperty(Config.EDMS_SIZE,isss.available() );
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}*/
					ValueFactory valueFactory = jcrsession.getValueFactory();
					Binary myBinary = valueFactory.createBinary(isss);
					file.addMixin("mix:referenceable");
					Node resNode = file
							.addNode(Config.EDMS_CONTENT, "edms:resource");
					Tika tika = new Tika();
					String mimeType;
					try {
						mimeType = tika.detect(isss);
						//System.out.println("DateTime: "+new Date()+" ::::: "+"MimeType of the Uploading File is "+mimeType);
						resNode.setProperty("jcr:mimeType", mimeType);
						resNode.setProperty("jcr:data",myBinary);
					} catch (IOException e) {
						e.printStackTrace();
					}finally{
						myBinary.dispose();
						isss.close();
					}
					
					Calendar lastModified = Calendar.getInstance();
					lastModified
							.setTimeInMillis(lastModified.getTimeInMillis());
					resNode.setProperty("jcr:lastModified", lastModified);
					jcrsession.save();
					/*root.setProperty(
							Config.EDMS_NO_OF_DOCUMENTS,
							Integer.parseInt(root.getProperty(
									Config.EDMS_NO_OF_DOCUMENTS).getString()) + 1);*/
					jcrsession.save();
					file1 = setPropertiesWithoutStream(file, file1, userid,password,jcrsession);
					response.setFile(file1);
					response.setSuccess(true);
					Version version=jcrsession.getWorkspace().getVersionManager().checkin(file.getPath());
					System.out.println("DateTime: "+new Date()+" ::::: "+"Version of :"+file.getPath()+" has been created, Version name is : "+version.getName());
					jcrsession.getWorkspace().getVersionManager().checkout(file.getPath());
					
					//updateQuotaValue(userid, filesize);
				} else {
					response.setSuccess(false);
					System.out.println("you have not permission to add child node");
				}
			}}
		} catch (Exception e) {
			response.setSuccess(false);
			e.printStackTrace();
		}finally{
			
			//JcrRepositoryUtils.logout(sessionId);
		}
}
		
		/*}else{
			response.setSuccess(false);
			return response;
		}*/
		return response;
	}
	public synchronized  CreateFileResponse  createFileDAV(String fileName, String parentFile,
			String userid,String password, String keywords, String description, byte[] is,long filesize) {
		System.out.println("DateTime: "+new Date()+" ::::: "+"Uploading file of = "+fileName+" in : "+parentFile+" by user : "+userid);
		CreateFileResponse response = new CreateFileResponse();
		if(is!=null){
		byte[] decodedBaseData=org.apache.commons.codec.binary.Base64.decodeBase64(is);
		SessionWrapper sessions =JcrRepositoryUtils.loginDAV(userid, password);
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		Node file = null;
		File file1 = new File();
		try {
			Node root = jcrsession.getRootNode();
			if (parentFile.length() > 1) {
				System.out.println("DateTime: "+new Date()+" ::::: "+"parent folder when creating File : "+parentFile);
				parentFile=parentFile.trim();
				root = root.getNode(parentFile.substring(1));
			}
			String fileNames=fileName;
			String ext=fileName.substring(fileName.lastIndexOf('.'));
			fileNames=fileName.substring(0,fileName.lastIndexOf('.'));
			if(!root.hasNode(fileNames+ext)){
			while(root.hasNode(fileNames+ext)){
				fileNames+="-copy";
			}
			fileName=fileNames+ext;
			if(root.hasProperty(Config.USERS_SECURITY)) {
				Value[] actualUsers = root.getProperty(Config.USERS_SECURITY).getValues();
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
					/*		Version version = jcrsession.getWorkspace()
							.getVersionManager().checkin(root.getPath());
					System.out.println("DateTime: "+new Date()+" ::::: "+version.getName());
					jcrsession
							.getWorkspace()
							.getVersionManager()
							.getVersionHistory(root.getPath())
							.addVersionLabel(version.getName(),
									"new child named " + fileName + " added",
									true);*/

					/*Version version=jcrsession.getWorkspace().getVersionManager().checkin(root.getPath());
					System.out.println("DateTime: "+new Date()+" ::::: "+"Version of :"+root.getPath()+" has been created, Version name is : "+version.getName());
					jcrsession.getWorkspace().getVersionManager().checkout(root.getPath());
					*/
					file = root.addNode(fileName, Config.EDMS_DOCUMENT);
					
					if (root.hasProperty(Config.USERS_READ)
							&& (!root.getProperty(Config.EDMS_AUTHOR)
									.toString().equals(Config.EDMS_ADMIN))) {
						Value[] actualUser = root
								.getProperty(Config.USERS_READ).getValues();
						String newUser = "";
						for (int i = 0; i < actualUser.length; i++) {
							newUser += actualUser[i].getString() + ",,";
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
							newUser += actualUser[i].getString() + ",,";
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
							newUser += actualUser[i].getString() + ",,";
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
							newUser += actualUser[i].getString() + ",,";
						}
						file.setProperty(Config.USERS_SECURITY,
								newUser.split(","));
					} else {
						file.setProperty(Config.USERS_SECURITY, new String[] {});
					}

					if (root.hasProperty(Config.GROUPS_READ)
							&& !root.getProperty(Config.EDMS_AUTHOR).toString()
									.equals(Config.EDMS_ADMIN)) {
						Value[] actualUser = root.getProperty(Config.GROUPS_READ).getValues();
						String newUser = "";
						for (int i = 0; i < actualUser.length; i++) {
							newUser += actualUser[i].getString() + ",,";
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
							newUser += actualUser[i].getString() + ",,";
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
							newUser += actualUser[i].getString() + ",,";
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
							newUser += actualUser[i].getString() + ",,";
						}
						file.setProperty(Config.GROUPS_SECURITY,
								newUser.split(","));
					} else {
						file.setProperty(Config.GROUPS_SECURITY,
								new String[] {});
					}

					file.setProperty(Config.EDMS_KEYWORDS, keywords.split(","));
					file.setProperty(Config.EDMS_NAME, fileName);
					if(root.hasProperty(Config.EDMS_OWNER)){
						//System.out.println("DateTime: "+new Date()+" ::::: "+root.getProperty(Config.EDMS_OWNER).getString());
						file.setProperty(Config.EDMS_OWNER,root.getProperty(Config.EDMS_OWNER).getString()+","+userid);
					}else{
						file.setProperty(Config.EDMS_OWNER,root.getProperty(Config.EDMS_AUTHOR).getString());
					}
					
					
				/*	if (root.getProperty(Config.EDMS_AUTHOR).getString()
							.equals(Config.EDMS_ADMIN)) {*/

					file.setProperty(Config.EDMS_AUTHOR, userid);
					
					//file.setProperty(Config.EDMS_PATH, root.getPath()+"/"+fileName);

					/*} else {
						file.setProperty(Config.EDMS_AUTHOR,
								root.getProperty(Config.EDMS_AUTHOR)
										.getString());
					}*/
						//file.setProperty(Config.EDMS_DESCRIPTION, is);
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

					file.setProperty(Config.EDMS_SIZE, filesize);
					//file.addMixin(JcrConstants.MIX_SHAREABLE);
					file.addMixin(JcrConstants.MIX_VERSIONABLE);
					//String iss;
					InputStream	isss= new ByteArrayInputStream(decodedBaseData);

						/*try {
							System.out.println("DateTime: "+new Date()+" ::::: "+isss.available());
							file.setProperty(Config.EDMS_SIZE,isss.available() );
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}*/
					ValueFactory valueFactory = jcrsession.getValueFactory();
					Binary myBinary = valueFactory.createBinary(isss);
					file.addMixin("mix:referenceable");
					Node resNode = file
							.addNode(Config.EDMS_CONTENT, "edms:resource");
					Tika tika = new Tika();
					String mimeType;
					try {
						mimeType = tika.detect(isss);
						//System.out.println("DateTime: "+new Date()+" ::::: "+"MimeType of the Uploading File is "+mimeType);
						resNode.setProperty("jcr:mimeType", mimeType);
						resNode.setProperty("jcr:data",myBinary);
					} catch (IOException e) {
						e.printStackTrace();
					}finally{
						myBinary.dispose();
						isss.close();
					}
					
					Calendar lastModified = Calendar.getInstance();
					lastModified
							.setTimeInMillis(lastModified.getTimeInMillis());
					resNode.setProperty("jcr:lastModified", lastModified);
					jcrsession.save();
					/*root.setProperty(
							Config.EDMS_NO_OF_DOCUMENTS,
							Integer.parseInt(root.getProperty(
									Config.EDMS_NO_OF_DOCUMENTS).getString()) + 1);*/
					jcrsession.save();
					file1 = setPropertiesWithoutStream(file, file1, userid,password,jcrsession);
					response.setFile(file1);
					response.setSuccess(true);
					Version version=jcrsession.getWorkspace().getVersionManager().checkin(file.getPath());
					System.out.println("DateTime: "+new Date()+" ::::: "+"Version of :"+file.getPath()+" has been created, Version name is : "+version.getName());
					jcrsession.getWorkspace().getVersionManager().checkout(file.getPath());
					
					
				} else {
					response.setSuccess(false);
					System.out.println("you have not permission to add child node");
				}
			}}
		} catch (Exception e) {
			response.setSuccess(false);
			e.printStackTrace();
		}finally{
			
			//JcrRepositoryUtils.logout(sessionId);
		}
}
		return response;
	}

	public synchronized File getFileByPath(String FilePath, String userid,String password) {

		System.out.println("DateTime: "+new Date()+" ::::: "+"Getting file by Path = "+FilePath+" by user : "+userid);
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
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
					if (root.hasProperty(Config.USERS_READ)) {
						Value[] actualUsers = root.getProperty(
								Config.USERS_READ).getValues();
						String newUser = "";
						for (int i = 0; i < actualUsers.length; i++) {
							newUser += actualUsers[i].getString() + ",";
						}
					
					
					/*if (root.getProperty(Config.EDMS_AUTHOR).getString()
							.equals(userid)
							||root.getProperty(Config.EDMS_OWNER).getString()
							.contains(userid)
							||newUser.contains(userid)||newUser.contains(Config.EDMS_GUEST)) {*/
					
						setProperties(root, File1, userid,password,jcrsession);
						//System.out.println("DateTime: "+new Date()+" ::::: "+File1.getFileContent());
					//}
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
	public synchronized File getFileByPathDAV(String FilePath, String userid,String password) {

		System.out.println("DateTime: "+new Date()+" ::::: "+"Getting file by Path = "+FilePath+" by user : "+userid);
		SessionWrapper sessions =JcrRepositoryUtils.loginDAV(userid, password);
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
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
					if (root.hasProperty(Config.USERS_READ)) {
						Value[] actualUsers = root.getProperty(
								Config.USERS_READ).getValues();
						String newUser = "";
						for (int i = 0; i < actualUsers.length; i++) {
							newUser += actualUsers[i].getString() + ",";
						}
					
					
				/*	if (root.getProperty(Config.EDMS_AUTHOR).getString()
							.equals(userid)
							|| (root.getName().equals(userid) && (root
									.getProperty(Config.EDMS_AUTHOR)
									.getString()).equals(Config.JCR_USERNAME))||newUser.contains(userid)) {
				*/		
						setProperties(root, File1, userid,password,jcrsession);
						//System.out.println("DateTime: "+new Date()+" ::::: "+File1.getFileContent());
					/*}*/
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
	public File getFileByPathWithOutStream(String FilePath, String userid,String password) {

		System.out.println("DateTime: "+new Date()+" ::::: "+"Getting file by Path = "+FilePath+" by user : "+userid);
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
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
					if (root.hasProperty(Config.USERS_READ)) {
						Value[] actualUsers = root.getProperty(
								Config.USERS_READ).getValues();
						String newUser = "";
						for (int i = 0; i < actualUsers.length; i++) {
							newUser += actualUsers[i].getString() + ",";
						}
						// System.out.println("DateTime: "+new Date()+" ::::: "+"for node : "+node.getPath().toString()+" newUser contains "+node.getProperty(Config.EDMS_AUTHOR).getString()+" is "+newUser.contains(userid));
				/*	if (root.getProperty(Config.EDMS_AUTHOR).getString()
							.equals(userid)||root.getProperty(Config.EDMS_OWNER).getString()
							.contains(userid)
							|| (root.getName().equals(userid) && (root
									.getProperty(Config.EDMS_AUTHOR)
									.getString()).equals(Config.JCR_USERNAME))||newUser.contains(userid)) {*/
						setPropertiesWithoutStream(root, File1, userid,password,jcrsession);
						//System.out.println("DateTime: "+new Date()+" ::::: "+File1.getFileContent());
					/*}*/
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

	/*

	public void assignSinglePermissionRecursion(Node rt, String userid,String password,
			String user, String value) {
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		try {
			if(value.equals("ur")){
				Value[] actualUsers = rt.getProperty(Config.USERS_READ)
						.getValues();
				String newUser = "";
				for (int i = 0; i < actualUsers.length; i++) {
					newUser += actualUsers[i].getString() + ",";
				}
				// System.out.println("DateTime: "+new Date()+" ::::: "+newUser.contains(user));
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
				// System.out.println("DateTime: "+new Date()+" ::::: "+newUser.contains(user));
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
				// System.out.println("DateTime: "+new Date()+" ::::: "+newUser.contains(user));
				if (!newUser.contains(user)) {

					newUser += user + ",";
					rt.setProperty(Config.USERS_WRITE, new String[] { newUser });
				}
				jcrsession.save();
			}
			
			

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

	public String assignSinglePermission(String FilePath, String userid,String password,
			String user, String value) {
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		try {
			Node root = jcrsession.getRootNode();
			if (FilePath.length() > 1) {
				root = root.getNode(FilePath.substring(1));
			}

			if (root.getProperty(Config.EDMS_AUTHOR).getString().equals(userid)) {
				// System.out.println("DateTime: "+new Date()+" ::::: "+":before assigning permissions like "+value
				// +" to user : "+user+" on "+root.getName());
				assignSinglePermissionRecursion(root, userid, user, value);
				// System.out.println("DateTime: "+new Date()+" ::::: "+":value contains n "+value.contains("n"));
				if (!value.contains("n") && value.contains("r")) {

					Workspace ws = jcrsession.getWorkspace();
					Node rty = jcrsession.getRootNode();
					if (!rty.hasNode(user + "/" + root.getName())) {
						ws.clone(ws.getName(), root.getPath(), "/" + user + "/"
								+ root.getName(), false);
					} else {
						// System.out.println("DateTime: "+new Date()+" ::::: "+"already exist");
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

	/*public User createUser(String userid,String password, String password, Session jcrsession,
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

	public Group createGroup(String userid,String password, String groupName,
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
			 * privileges.length; i++) { //System.out.println("DateTime: "+new Date()+" ::::: "+privileges[i]); }
			 

			// get now applied privileges on a node
			Privilege[] privileges = aMgr.getPrivileges(root.getPath());
			for (int i = 0; i < privileges.length; i++) {
				// System.out.println("DateTime: "+new Date()+" ::::: "+privileges[i]);
				permissions += privileges[i];
			}
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return permissions;
	}*/

	public void setPolicy(Session jcrsession, Node root, String userid,String password,
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
			 * //System.out.println("DateTime: "+new Date()+" ::::: "+privileges[i]); }
			 */

			// get now applied privileges on a node
			/*
			 * privileges = aMgr.getPrivileges(path); for (int i = 0; i <
			 * privileges.length; i++) { //System.out.println("DateTime: "+new Date()+" ::::: "+privileges[i]); }
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
			 * "top-management")); //System.out.println("DateTime: "+new Date()+" ::::: "+"group path is : " +
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

			// System.out.println("DateTime: "+new Date()+" ::::: "+"before set policy on : "+path+" to user : "+userid);
			for (int i = 0; i < setprivilege.length; i++) {
				// System.out.println("DateTime: "+new Date()+" ::::: "+setprivilege[i]);
			}

			jcrsession.save();
			jcrsession.refresh(true);
			setprivilege = aMgr.getPrivileges(path);
			// System.out.println("DateTime: "+new Date()+" ::::: "+"after set policy on : "+path+" to user : "+userid);
			for (int i = 0; i < setprivilege.length; i++) {
				// System.out.println("DateTime: "+new Date()+" ::::: "+setprivilege[i]);
			}

		} catch (RepositoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}



	public FileListReturn listSharedFile(String userid,String password) {
		System.out.println("DateTime: "+new Date()+" ::::: "+"Getting Shared files of user = "+userid+" by user : "+userid);
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		FileListReturn FileList1 = new FileListReturn();
		ArrayOfFiles Files = new ArrayOfFiles();
		Node root = null;

		// Session jcrsession = null;
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
			/*
			 * String[] wwws=
			 * jcrsession.getWorkspace().getAccessibleWorkspaceNames(); for (int
			 * i = 0; i < wwws.length; i++) { //System.out.println("DateTime: "+new Date()+" ::::: "+wwws[i]); }
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
							// System.out.println("DateTime: "+new Date()+" ::::: "+"for node : "+node.getPath().toString()+" newUser contains "+node.getProperty(Config.EDMS_AUTHOR).getString()+" is "+newUser.contains(userid));
							if (newUser.contains(userid)) {
								File File = new File();
								File = setProperties(node, File, userid,password,jcrsession);
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

	public FileListReturn listSharedFileWithOutStream(String userid,String password) {
		System.out.println("DateTime: "+new Date()+" ::::: "+"Getting Shared files of user = "+userid+" by user : "+userid);
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		FileListReturn FileList1 = new FileListReturn();
		ArrayOfFiles Files = new ArrayOfFiles();
		Node root = null;

		// Session jcrsession = null;
		try {
			/*javax.jcr.query.QueryManager queryManager;
			queryManager = jcrsession.getWorkspace().getQueryManager();
			String expression = "select * from [edms:folder] AS s WHERE ISCHILDNODE(s,'"+path+"') AND CONTAINS(s.["+Config.USERS_READ+"], '*"
					+ userid + "*') ORDER BY s.["+Config.EDMS_Sorting_Parameter+"] ASC";
			//expression = "select * from [edms:folder] AS s WHERE NAME like ['%san%']";
		    javax.jcr.query.Query query = queryManager.createQuery(expression, javax.jcr.query.Query.JCR_SQL2);
		    javax.jcr.query.QueryResult result = query.execute();*/
			
			root = jcrsession.getRootNode();
			root = root.getNode(userid);
			for (NodeIterator nit = root.getNodes(); nit.hasNext();) {
				Node node = nit.nextNode();
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
							if (newUser.contains(userid)) {
								File File = new File();
								File = setPropertiesWithoutStream(node, File, userid,password,jcrsession);
								Files.getFileList().add(File);
							}
						}
					}
				}
			}
		FileList1.setFileListResult(Files);
		FileList1.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
		}
		return FileList1;
	}


	public FileListReturn listSharedFile(String userid,String password, String path) {
		System.out.println("DateTime: "+new Date()+" ::::: "+"Getting Shared files of user = "+userid+" by path "+path+" by user : "+userid);
		path=path.replace("'","&apos;");
		path=path.replace("<","&lt;");
		path=path.replace(">","&gt;");
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		FileListReturn FileList1 = new FileListReturn();
		ArrayOfFiles files = new ArrayOfFiles();
		try {
			javax.jcr.query.QueryManager queryManager;
			queryManager = jcrsession.getWorkspace().getQueryManager();
			String expression = "select * from [edms:document] AS s WHERE ISCHILDNODE(s,'"+path+"') AND CONTAINS(s.["+Config.USERS_READ+"], '*"
					+ userid + "*') ORDER BY s.["+Config.EDMS_Sorting_Parameter+"] ASC";
			//expression = "select * from [edms:folder] AS s WHERE NAME like ['%san%']";
		    javax.jcr.query.Query query = queryManager.createQuery(expression, javax.jcr.query.Query.JCR_SQL2);
		    javax.jcr.query.QueryResult result = query.execute();
			
			for (NodeIterator nit = result.getNodes(); nit.hasNext();) {
					Node node = nit.nextNode();
					File file = new File();
					file=setPropertiesWithoutStream(node, file, userid,password, jcrsession);
					files.getFileList().add(file);
			}
			FileList1.setFileListResult(files);
			FileList1.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			//JcrRepositoryUtils.logout(sessionId);
		}
		
		return FileList1;
	}

	public FileListReturn listSharedFileWithContent(String userid,String password, String path) {
		System.out.println("DateTime: "+new Date()+" ::::: "+"Getting Shared files of user = "+userid+" by path "+path+" by user : "+userid);
		path=path.replace("'","&apos;");
		path=path.replace("<","&lt;");
		path=path.replace(">","&gt;");
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		FileListReturn FileList1 = new FileListReturn();
		ArrayOfFiles files = new ArrayOfFiles();
		try {
			javax.jcr.query.QueryManager queryManager;
			queryManager = jcrsession.getWorkspace().getQueryManager();
			String expression = "select * from [edms:document] AS s WHERE ISCHILDNODE(s,'"+path+"') AND CONTAINS(s.["+Config.USERS_READ+"], '*"
					+ userid + "*') ORDER BY s.["+Config.EDMS_Sorting_Parameter+"] ASC";
			//expression = "select * from [edms:folder] AS s WHERE NAME like ['%san%']";
		    javax.jcr.query.Query query = queryManager.createQuery(expression, javax.jcr.query.Query.JCR_SQL2);
		    javax.jcr.query.QueryResult result = query.execute();
			
			for (NodeIterator nit = result.getNodes(); nit.hasNext();) {
					Node node = nit.nextNode();
					File file = new File();
					file=setProperties(node, file, userid,password, jcrsession);
					files.getFileList().add(file);
			}
			FileList1.setFileListResult(files);
			FileList1.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			//JcrRepositoryUtils.logout(sessionId);
		}
		
		return FileList1;
	}
	public FileListReturn listSharedFileWithContentDAV(String userid,String password, String path) {
		System.out.println("DateTime: "+new Date()+" ::::: "+"Getting Shared files of user = "+userid+" by path "+path+" by user : "+userid);
		path=path.replace("'","&apos;");
		path=path.replace("<","&lt;");
		path=path.replace(">","&gt;");
		SessionWrapper sessions =JcrRepositoryUtils.loginDAV(userid, password);
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		FileListReturn FileList1 = new FileListReturn();
		ArrayOfFiles files = new ArrayOfFiles();
		try {
			javax.jcr.query.QueryManager queryManager;
			queryManager = jcrsession.getWorkspace().getQueryManager();
			String expression = "select * from [edms:document] AS s WHERE ISCHILDNODE(s,'"+path+"') AND CONTAINS(s.["+Config.USERS_READ+"], '*"
					+ userid + "*') ORDER BY s.["+Config.EDMS_Sorting_Parameter+"] ASC";
			//expression = "select * from [edms:folder] AS s WHERE NAME like ['%san%']";
		    javax.jcr.query.Query query = queryManager.createQuery(expression, javax.jcr.query.Query.JCR_SQL2);
		    javax.jcr.query.QueryResult result = query.execute();
			
			for (NodeIterator nit = result.getNodes(); nit.hasNext();) {
					Node node = nit.nextNode();
					File file = new File();
					file=setProperties(node, file, userid,password, jcrsession);
					files.getFileList().add(file);
			}
			FileList1.setFileListResult(files);
			FileList1.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			//JcrRepositoryUtils.logout(sessionId);
		}
		
		return FileList1;
	}

	
	public FileListReturn listSharedFileWithOutStream(String userid,String password, String path) {
		System.out.println("DateTime: "+new Date()+" ::::: "+"Getting Shared files of user = "+userid+" by path "+path+" by user : "+userid);
		path=path.replace("'","&apos;");
		path=path.replace("<","&lt;");
		path=path.replace(">","&gt;");
		
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		FileListReturn FileList1 = new FileListReturn();
		ArrayOfFiles files = new ArrayOfFiles();
		
		try {
			javax.jcr.query.QueryManager queryManager;
			queryManager = jcrsession.getWorkspace().getQueryManager();
			String expression = "select * from [edms:document] AS s WHERE ISCHILDNODE(s,'"+path+"') AND CONTAINS(s.["+Config.USERS_READ+"], '*"
					+ userid + "*') ORDER BY s.["+Config.EDMS_Sorting_Parameter+"] ASC";
			//expression = "select * from [edms:folder] AS s WHERE NAME like ['%san%']";
		    javax.jcr.query.Query query = queryManager.createQuery(expression, javax.jcr.query.Query.JCR_SQL2);
		    javax.jcr.query.QueryResult result = query.execute();
			
			for (NodeIterator nit = result.getNodes(); nit.hasNext();) {
					Node node = nit.nextNode();
					File file = new File();
					file=setPropertiesWithoutStream(node, file, userid,password, jcrsession);
					files.getFileList().add(file);
			}
			FileList1.setFileListResult(files);
			FileList1.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			//JcrRepositoryUtils.logout(sessionId);
		}
		
		return FileList1;
	}


/*	public String recycleFile(String FilePath, String userid,String password) {
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

	public void recycleFileRecursion(Node root, String userid,String password) {
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

	public String deleteFile(String FilePath, String userid,String password) {

		FilePath=FilePath.replace("'","&apos;");
		FilePath=FilePath.replace("<","&lt;");
		FilePath=FilePath.replace(">","&gt;");
		FilePath=FilePath;
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
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
	public String deleteFileDAV(String FilePath, String userid,String password) {

		FilePath=FilePath.replace("'","&apos;");
		FilePath=FilePath.replace("<","&lt;");
		FilePath=FilePath.replace(">","&gt;");
		FilePath=FilePath;
		SessionWrapper sessions =JcrRepositoryUtils.loginDAV(userid, password);
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
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

	public String restoreFile(String FilePath, String userid,String password) {

		FilePath=FilePath.replace("'","&apos;");
		FilePath=FilePath.replace("<","&lt;");
		FilePath=FilePath.replace(">","&gt;");
		FilePath=FilePath;
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		String response = "";
		try {
			Node root = jcrsession.getRootNode();
			root = root.getNode(FilePath.substring(1));
			restoreFileRecursion(root, userid,password);
			response = "success";
		} catch (RepositoryException e) {
			response = "Exception Occured";
			e.printStackTrace();
		}finally{
			//JcrRepositoryUtils.logout(sessionId);
		}
		return response;
	}

	public void restoreFileRecursion(Node root, String userid,String password) {
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();		
		try {
			String parents = root.getProperty(Config.EDMS_RESTORATION_PATH)
					.getString().substring(1);
			parents = parents.substring(0, parents.lastIndexOf("/"));
			// System.out.println("DateTime: "+new Date()+" ::::: "+"parent is "+parents);
			Node jcrRoot = jcrsession.getRootNode();
			Node parent;
			if (jcrRoot.hasNode(parents)) {
				parent = jcrRoot.getNode(parents);
			} else {
				parent = createFileRecursionWhenNotFound(parents, userid,password);
			}
			// root.setProperty(Config.EDMS_RECYCLE_DOC, false);
			// System.out.println("DateTime: "+new Date()+" ::::: "+root.getPath()+" source to : "+root.getProperty(Config.EDMS_RESTORATION_PATH).getString());
			root.getSession()
					.getWorkspace()
					.copy(root.getPath(),
							root.getProperty(Config.EDMS_RESTORATION_PATH)
									.getString());
			jcrsession.save();
			root.remove();
			jcrsession.save();
		/*	int no_of_Files = Integer.parseInt(parent.getProperty(
					Config.EDMS_NO_OF_DOCUMENTS).getString());
			parent.setProperty(Config.EDMS_NO_OF_DOCUMENTS, no_of_Files + 1);*/
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

	public Node createFileRecursionWhenNotFound(String FilePath, String userid,String password) {

	
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		Node file = null;
		try {
			Node root = jcrsession.getRootNode();
			// System.out.println("DateTime: "+new Date()+" ::::: "+"in File creation recursion "+FileName.substring(0,FileName.lastIndexOf("/")+1));
			String parent = FilePath.substring(0, FilePath.lastIndexOf("/"));
			if (root.hasNode(parent)) {
				root = root.getNode(parent);
				file = root.addNode(
						FilePath.substring(FilePath.lastIndexOf("/") + 1),
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
			//	file.addMixin(JcrConstants.MIX_SHAREABLE);
				file.addMixin(JcrConstants.MIX_VERSIONABLE);
				jcrsession.save();
			} else {
				createFileRecursionWhenNotFound(
						FilePath.substring(0, FilePath.lastIndexOf("/") + 1),
						userid,password);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;

	}

/*	public RenameFileRes renameFile(String oldFilePath, String newFilePath,
			String userid,String password) {
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		RenameFileRes response = new RenameFileRes();
		try {

			Node forVer = jcrsession.getRootNode().getNode(
					oldFilePath.substring(1));
			// System.out.println("DateTime: "+new Date()+" ::::: "+forVer.getProperty(Config.EDMS_AUTHOR).getString());
			if (forVer.getProperty(Config.EDMS_AUTHOR).getString()
					.equals(userid)) {Version version=jcrsession.getWorkspace().getVersionManager().checkin(forVer.getPath());
					jcrsession.getWorkspace().getVersionManager().getVersionHistory(forVer.getPath()).addVersionLabel(version.getName(), "renamed from "+oldFilePath.substring(oldFilePath.lastIndexOf("/")+1)+" to "+newFilePath, true);
					jcrsession.getWorkspace().getVersionManager().checkout(forVer.getPath());
					jcrsession.move(oldFilePath,
							oldFilePath.substring(0, oldFilePath.lastIndexOf("/"))
									+ "/" + newFilePath);SimpleDateFormat format = new SimpleDateFormat(
						"YYYY-MM-dd'T'HH:mm:ss.SSSZ");
									forVer.setProperty(Config.EDMS_MODIFICATIONDATE,(format.format(new Date())).toString().replace("+0530", "Z") );
									forVer.setProperty(Config.EDMS_NAME, newFilePath);
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
*/
	/* vfc */
	public VCFFileListReturn getVCFFileAtt(String fdrname, String userid,String password) {	
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		Assert.notNull(fdrname);
		VCFFileListReturn FileList1 = new VCFFileListReturn();
		ArrayOfVCFFiles Files = new ArrayOfVCFFiles();
		Node root = null;
		boolean status = true;
		InputStream is=null;
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
					+ "')  ORDER BY [NAME(s),DESC]";
			// expression =
			// "select * from [edms:folder] AS s WHERE NAME like ['%san%']";
			javax.jcr.query.Query query = queryManager.createQuery(expression,
					javax.jcr.query.Query.JCR_SQL2);
			javax.jcr.query.QueryResult result = query.execute();

			for (NodeIterator nit = result.getNodes(); nit.hasNext();) {
				Node node = nit.nextNode();

				if (Config.EDMS_DOCUMENT.equals(node.getPrimaryNodeType()
						.getName())) {
					/*if (node.getProperty(Config.EDMS_AUTHOR).getString()
							.equals(userid)) {*/
						VCFFileAtt vcfflatt = new VCFFileAtt();
						Node ntResourceNode = node.getNode("edms:content");
						is = ntResourceNode.getProperty("jcr:data")
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
						// System.out.println("DateTime: "+new Date()+" ::::: "+is);
						VCard vcard = Ezvcard.parse(is).first();
						FormattedName fn = vcard.getFormattedName();
						name = fn.getValue();
						List<Email> elst = vcard.getEmails();

						for (Email em : elst) {

							if (email_cnt == 0) {
								email = em.getValue();
							}
							email_cnt++;

							/*System.out.println("DateTime: "+new Date()+" ::::: "+"**************email type="
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

							

							// System.out.println("DateTime: "+new Date()+" ::::: "+"Address:"+ad.getCountry()+" type:"+ad.getTypes());
							// System.out.println("DateTime: "+new Date()+" ::::: "+"Address:"+ad.getStreetAddress()+" type:"+ad.getTypes());
						}
						if (email_cnt > 1) {
							email_cnt--;
							email = email + "(+" + email_cnt + ")";
						}
						if (phone_cnt > 1) {
						//	System.out.println("DateTime: "+new Date()+" ::::: "+"*************name=" + name
						//			+ "--- contact=" + phone_cnt);
							phone_cnt--;
							phone = phone + "(+" + phone_cnt + ")";
						}
						if (addr_cnt > 1) {
							addr_cnt--;
							addr = addr + "(+" + addr_cnt + ")";
						}
						// System.out.println("DateTime: "+new Date()+" ::::: "+vcard.getFormattedName().getValue());
						// System.out.println("DateTime: "+new Date()+" ::::: "+vcard.getStructuredName().getFamily());
						// System.out.println("DateTime: "+new Date()+" ::::: "+"--------------------------------");

						vcardReader.close();
						System.out.println("DateTime: "+new Date()+" ::::: "+vcard.getTitles().size());
						if(vcard.getTitles().size()>0){
						System.out.println("DateTime: "+new Date()+" ::::: "+vcard.getTitles().get(0).getValue().toString());
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
						System.out.println("DateTime: "+new Date()+" ::::: "+node.getName());

					}
				/*}*/

			}	FileList1.setVCFFileListResult(Files);
		FileList1.setVCFSuccess(status);
		//System.out.println("DateTime: "+new Date()+" ::::: "+);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(is!=null)
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//JcrRepositoryUtils.logout(sessionId);
		}
	
		return FileList1;
	}
	public VCFFileListReturn getVCFFileAttDAV(String fdrname, String userid,String password) {	
		SessionWrapper sessions =JcrRepositoryUtils.loginDAV(userid, password);
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		Assert.notNull(fdrname);
		VCFFileListReturn FileList1 = new VCFFileListReturn();
		ArrayOfVCFFiles Files = new ArrayOfVCFFiles();
		Node root = null;
		boolean status = true;
		InputStream is=null;
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
					+ "')  ORDER BY [NAME(s),DESC]";
			// expression =
			// "select * from [edms:folder] AS s WHERE NAME like ['%san%']";
			javax.jcr.query.Query query = queryManager.createQuery(expression,
					javax.jcr.query.Query.JCR_SQL2);
			javax.jcr.query.QueryResult result = query.execute();

			for (NodeIterator nit = result.getNodes(); nit.hasNext();) {
				Node node = nit.nextNode();

				if (Config.EDMS_DOCUMENT.equals(node.getPrimaryNodeType()
						.getName())) {
					/*if (node.getProperty(Config.EDMS_AUTHOR).getString()
							.equals(userid)) {*/
						VCFFileAtt vcfflatt = new VCFFileAtt();
						Node ntResourceNode = node.getNode("edms:content");
						is = ntResourceNode.getProperty("jcr:data")
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
						// System.out.println("DateTime: "+new Date()+" ::::: "+is);
						VCard vcard = Ezvcard.parse(is).first();
						FormattedName fn = vcard.getFormattedName();
						name = fn.getValue();
						List<Email> elst = vcard.getEmails();

						for (Email em : elst) {

							if (email_cnt == 0) {
								email = em.getValue();
							}
							email_cnt++;

							/*System.out.println("DateTime: "+new Date()+" ::::: "+"**************email type="
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

							

							// System.out.println("DateTime: "+new Date()+" ::::: "+"Address:"+ad.getCountry()+" type:"+ad.getTypes());
							// System.out.println("DateTime: "+new Date()+" ::::: "+"Address:"+ad.getStreetAddress()+" type:"+ad.getTypes());
						}
						if (email_cnt > 1) {
							email_cnt--;
							email = email + "(+" + email_cnt + ")";
						}
						if (phone_cnt > 1) {
						//	System.out.println("DateTime: "+new Date()+" ::::: "+"*************name=" + name
						//			+ "--- contact=" + phone_cnt);
							phone_cnt--;
							phone = phone + "(+" + phone_cnt + ")";
						}
						if (addr_cnt > 1) {
							addr_cnt--;
							addr = addr + "(+" + addr_cnt + ")";
						}
						// System.out.println("DateTime: "+new Date()+" ::::: "+vcard.getFormattedName().getValue());
						// System.out.println("DateTime: "+new Date()+" ::::: "+vcard.getStructuredName().getFamily());
						// System.out.println("DateTime: "+new Date()+" ::::: "+"--------------------------------");

						vcardReader.close();
						System.out.println("DateTime: "+new Date()+" ::::: "+vcard.getTitles().size());
						if(vcard.getTitles().size()>0){
						System.out.println("DateTime: "+new Date()+" ::::: "+vcard.getTitles().get(0).getValue().toString());
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
						System.out.println("DateTime: "+new Date()+" ::::: "+node.getName());

					}
				/*}*/

			}	FileList1.setVCFFileListResult(Files);
		FileList1.setVCFSuccess(status);
		//System.out.println("DateTime: "+new Date()+" ::::: "+);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(is!=null)
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//JcrRepositoryUtils.logout(sessionId);
		}
	
		return FileList1;
	}

	public SortByPropertyRes sortByProperty(String path, String propertyName,
			String userid,String password) {

		path=path.replace("'","&apos;");
		path=path.replace("<","&lt;");
		path=path.replace(">","&gt;");
		path=path;
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
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
			// System.out.println("DateTime: "+new Date()+" ::::: "+result.getNodes());
			for (NodeIterator nit = result.getNodes(); nit.hasNext();) {
				Node node = nit.nextNode();
				System.out.println("DateTime: "+new Date()+" ::::: "+"node name is : "
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
			String folderPath, String searchParam, String userid,String password) {

		System.out.println("DateTime: "+new Date()+" ::::: "+"Search documents by = "+searchParam+" of value "+searchParamValue+" in folder "+folderPath+" by user : "+userid);
		folderPath=folderPath.replace("'","&apos;");
		folderPath=folderPath.replace("<","&lt;");
		folderPath=folderPath.replace(">","&gt;");
		searchParam=searchParam.replace("<","&lt;");
		searchParam=searchParam.replace(">","&gt;");

		//searchParamValue=searchParamValue.replace("-"," ");
		searchParamValue=searchParamValue.replace("'","&apos;");
		searchParamValue=searchParamValue.replace("<","&lt;");
		searchParamValue=searchParamValue.replace(">","&gt;");
//		searchParamValue="BP SBID 2014-2015-0004-03-04-2014-SBID-6";
		
		//searchParamValue="1--2";
		//searchParamValue=Text.escapeIllegalJcrChars(searchParamValue).replace("-", "\\-") ;
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		SearchDocByLikeResponse searchDocResponse = new SearchDocByLikeResponse();
		ArrayOfFolders folders = new ArrayOfFolders();
		ArrayOfFiles files = new ArrayOfFiles();
		FilesAndFolders filesFolders=new FilesAndFolders();
		javax.jcr.query.QueryManager queryManager;
		try {
			queryManager = jcrsession.getWorkspace().getQueryManager();
			// Create a query object ...
			
			//searchParamValue="BP SBID-2014-2015-0004-03-04-2014 SBID";
			String expression = "select * from [edms:folder] AS s WHERE ISDESCENDANTNODE(s,'"
					+ folderPath
					+ "') AND CONTAINS(s.["+searchParam+"], '*"
					+ searchParamValue + "*')  and  (CONTAINS(s.[edms:owner],'*"+userid+"*') or  CONTAINS(s.["
					+ Config.USERS_READ + "], '*" + userid + "*') )  ";
			if(searchParamValue.indexOf(':')>=0){
				String[] searchParamValues=searchParamValue.split(":");
				expression = "select * from [edms:folder] AS s WHERE ISDESCENDANTNODE(s,'"
			+ folderPath
			+ "') AND CONTAINS(s.["+searchParam+"], '*"
			+ searchParamValues[1]+"*')  and (CONTAINS(s.[edms:owner],'*"+userid+"*') or  CONTAINS(s.["
					+ Config.USERS_READ + "], '*" + userid + "*') ) ";

			}
			
			//System.out.println("DateTime: "+new Date()+" ::::: "+expression);
			
			javax.jcr.query.Query query = queryManager.createQuery(expression,
					javax.jcr.query.Query.JCR_SQL2);
			javax.jcr.query.QueryResult result = query.execute();

			
			
			for (NodeIterator nit = result.getNodes(); nit.hasNext();) {
				Node node = nit.nextNode();
				if((node.getPath().indexOf("/trash")<0))
				{
				Folder folder = new Folder();
				new FolderRepository().setProperties(node, folder, userid,password,jcrsession,folderPath);
				folders.getFolderList().add(folder);}
			}
			
			expression = "select * from [edms:document] AS s WHERE ISDESCENDANTNODE(s,'"
					+ folderPath
					+ "') AND CONTAINS(s.["+searchParam+"], '*"
					+ searchParamValue + "*')     and (CONTAINS(s.[edms:owner],'*"+userid+"*') or  CONTAINS(s.["
					+ Config.USERS_READ + "], '*" + userid + "*') )  ";
			
			if(searchParamValue.indexOf(':')>=0){
				String[] searchParamValues=searchParamValue.split(":");
				expression = "select * from [edms:document] AS s WHERE ISDESCENDANTNODE(s,'"
						+ folderPath
						+ "') AND CONTAINS(s.["+searchParam+"], '*"
						+ searchParamValues[1]+"*')   and (CONTAINS(s.[edms:owner],'*"+userid+"*') or  CONTAINS(s.["
					+ Config.USERS_READ + "], '*" + userid + "*') )  ";

						}
			
			query = queryManager.createQuery(expression,
					
					javax.jcr.query.Query.JCR_SQL2);
			result = query.execute();
			for (NodeIterator nit = result.getNodes(); nit.hasNext();) {
				Node node = nit.nextNode();
				if(!node.getPath().contains("trash"))
				{
				File file = new File();
				new FileRepository().setPropertiesWithoutStream(node, file, userid,password,jcrsession);
				files.getFileList().add(file);}
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
			String folderPath, String searchParam, String userid,String password) {
		folderPath=folderPath.replace("'","&apos;");
		folderPath=folderPath.replace("<","&lt;");
		folderPath=folderPath.replace(">","&gt;");
		folderPath=folderPath;
	
		searchParam=searchParam.replace("'","&apos;");
		searchParam=searchParam.replace("<","&lt;");
		searchParam=searchParam.replace(">","&gt;");
		searchParam=searchParam;
		
		searchParamValue=searchParamValue.replace("'","&apos;");
		searchParamValue=searchParamValue.replace("<","&lt;");
		searchParamValue=searchParamValue.replace(">","&gt;");
		searchParamValue=searchParamValue;
		

		SessionWrapper sessions =JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		SearchDocByDateResponse searchDocResponse = new SearchDocByDateResponse();
		ArrayOfFolders folders = new ArrayOfFolders();
		ArrayOfFiles files = new ArrayOfFiles();
		FilesAndFolders filesFolders=new FilesAndFolders();
		javax.jcr.query.QueryManager queryManager;
		try {
			queryManager = jcrsession.getWorkspace().getQueryManager();
			// Create a query object ...
			String expression = "";
		//	searchParam=Config.EDMS_CREATIONDATE;
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat(
					"YYYY-MM-dd'T'HH:mm:ss.SSSZ");
			format.format(cal.getTime());
			Date before = cal.getTime();
			//	System.out.println("DateTime: "+new Date()+" ::::: "+before);
			//	System.out.println("DateTime: "+new Date()+" ::::: "+"current date : "+ format.format(before).substring(0,format.format(before).lastIndexOf("+")) + "Z");
			cal.add(Calendar.MONTH, -1);
			cal.add(Calendar.DATE, -20);
			Date dateafter = cal.getTime();
			/*System.out.println("DateTime: "+new Date()+" ::::: "+"one month before date : "
					+ format.format(dateafter).substring(0,
							format.format(dateafter).lastIndexOf("+")) + "Z");*/
			expression = "SELECT * FROM [edms:folder] AS p WHERE ISDESCENDANTNODE(p,'"
					+ folderPath
					+ "') AND CONTAINS(p.["+searchParam+"], '*"
					+ searchParamValue + "*')";
			
			
			expression = "SELECT * FROM [edms:folder] AS p WHERE ISDESCENDANTNODE(p,'"
					+ folderPath
					+ "') AND ["
					+ searchParam
					+ "] like '%"
					+ searchParamValue + "%'   and CONTAINS(p.[edms:owner],'*"+userid+"*') ";
			/* CONTAINS(s.[edms:owner],'*"+userid+"*') 
			 * + "AND p.[edms:modified] >= CAST('" +
			 * format.format(dateafter).replace("+0530", "Z") +
			 * "' AS DATE) AND p.[edms:modified] <= CAST('" +
			 * format.format(before).replace("+0530", "Z")+"' AS DATE)";
			 */

			javax.jcr.query.Query query = queryManager.createQuery(expression,
					javax.jcr.query.Query.JCR_SQL2);
			query.setLimit(15);
			javax.jcr.query.QueryResult result = query.execute();

			for (NodeIterator nit = result.getNodes(); nit.hasNext();) {
				Node node = nit.nextNode();
				if(!node.getPath().contains("trash"))
				{Folder folder = new Folder();
				new FolderRepository().setProperties(node, folder, userid,password,jcrsession,folderPath);
				folders.getFolderList().add(folder);}
			}
			expression = "select * from [edms:document] AS s WHERE ISDESCENDANTNODE(s,'"
					+ folderPath
					+ "') AND CONTAINS(s.["+searchParam+"], '*"
					+ searchParamValue + "*')";
			
			expression = "select * from [edms:document] AS s WHERE ISDESCENDANTNODE(s,'"
					+ folderPath
					+ "') AND ["
					+ searchParam
					+ "] like '%"
					+ searchParamValue + "%'  and CONTAINS(s.[edms:owner],'*"+userid+"*') ";
			query = queryManager.createQuery(expression,
					javax.jcr.query.Query.JCR_SQL2);
			query.setLimit(15);
			result = query.execute();
			for (NodeIterator nit = result.getNodes(); nit.hasNext();) {
				Node node = nit.nextNode();
				if(!node.getPath().contains("trash"))
				{File file = new File();
				new FileRepository().setPropertiesWithoutStream(node, file, userid,password,jcrsession);
				files.getFileList().add(file);}
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

	public EditFileResponse editFile(byte[] fileContent, String filePath,
			String userid,String password) {
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		EditFileResponse res = new EditFileResponse();
		EditFileRes response=new EditFileRes();
		try {
			Node forVer = jcrsession.getRootNode().getNode(
					filePath.substring(1));
			if(forVer.hasProperty(Config.USERS_SECURITY)) {
				Value[] actualUsers = forVer.getProperty(Config.USERS_SECURITY).getValues();
				String newUsers = "";
				for (int i = 0; i < actualUsers.length; i++) {
					newUsers += actualUsers[i].getString() + ",";
				}
				if (newUsers.contains(userid)
						|| forVer.getProperty(Config.EDMS_AUTHOR).getString()
								.equals(userid)
						|| (forVer.getName().equals(userid) && (forVer
								.getProperty(Config.EDMS_AUTHOR).getString())
								.equals(Config.JCR_USERNAME))) {
				Node content=forVer.getNode("edms:content");
				InputStream	isss=null;
				byte[] encodedImage = org.apache.commons.codec.binary.Base64
						.decodeBase64(fileContent);
					isss=new ByteArrayInputStream(encodedImage);
				ValueFactory valueFactory = jcrsession.getValueFactory();
				Binary myBinary = valueFactory.createBinary(isss);
				content.setProperty("jcr:data", myBinary);
				jcrsession.save();
				response.setResponse("Success");
				response.setSuccess(true);
			} else {
				response.setResponse("Access Denied");
				response.setSuccess(false);
			}
			}else {
				response.setResponse("Access Denied");
				response.setSuccess(false);
			}
				
				res.setEditFileRes(response);
		} catch (RepositoryException e) {
			response.setResponse("Access Denied");
			response.setSuccess(false);
			e.printStackTrace();
		}finally{
			//JcrRepositoryUtils.logout(sessionId);
		}
		return res;
	}
	public EditFileResponse editFileDAV(byte[] fileContent, String filePath,
			String userid,String password) {
		SessionWrapper sessions =JcrRepositoryUtils.loginDAV(userid, password);
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		EditFileResponse res = new EditFileResponse();
		EditFileRes response=new EditFileRes();
		try {
			Node forVer = jcrsession.getRootNode().getNode(
					filePath.substring(1));
			if(forVer.hasProperty(Config.USERS_SECURITY)) {
				Value[] actualUsers = forVer.getProperty(Config.USERS_SECURITY).getValues();
				String newUsers = "";
				for (int i = 0; i < actualUsers.length; i++) {
					newUsers += actualUsers[i].getString() + ",";
				}
				if (newUsers.contains(userid)
						|| forVer.getProperty(Config.EDMS_AUTHOR).getString()
								.equals(userid)
						|| (forVer.getName().equals(userid) && (forVer
								.getProperty(Config.EDMS_AUTHOR).getString())
								.equals(Config.JCR_USERNAME))) {
				Node content=forVer.getNode("edms:content");
				InputStream	isss=null;
				byte[] encodedImage = org.apache.commons.codec.binary.Base64
						.decodeBase64(fileContent);
					isss=new ByteArrayInputStream(encodedImage);
				ValueFactory valueFactory = jcrsession.getValueFactory();
				Binary myBinary = valueFactory.createBinary(isss);
				content.setProperty("jcr:data", myBinary);
				jcrsession.save();
				response.setResponse("Success");
				response.setSuccess(true);
			} else {
				response.setResponse("Access Denied");
				response.setSuccess(false);
			}
			}else {
				response.setResponse("Access Denied");
				response.setSuccess(false);
			}
				
				res.setEditFileRes(response);
		} catch (RepositoryException e) {
			response.setResponse("Access Denied");
			response.setSuccess(false);
			e.printStackTrace();
		}finally{
			//JcrRepositoryUtils.logout(sessionId);
		}
		return res;
	}
	

	public String setPublicLink(
			String filePath,String userid,String password,String guestid){
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		File File1 = new File();
		try {
			Node root = jcrsession.getRootNode();
			if (filePath.length() > 1) {
				root = root.getNode(filePath.substring(1));
			}
			File1.setFileName(root.getName().toString());
			File1.setFilePath(root.getPath().toString());
			if (root.getPath().toString().length() > 1) {
				if (filePath.length() > 1) {
					if (root.hasProperty(Config.USERS_READ)) {
						Value[] actualUsers = root.getProperty(
								Config.USERS_READ).getValues();
						String newUser = "";
						for (int i = 0; i < actualUsers.length; i++) {
							if(actualUsers[i].getString().indexOf(Config.EDMS_GUEST)<0){
							if(actualUsers[i].getString().equals("")||actualUsers[i].getString()==""){}else
								{newUser += actualUsers[i].getString() + ",,";}
							}
						}
						newUser=newUser+Config.EDMS_GUEST+",,";
					/*if (root.getProperty(Config.EDMS_AUTHOR).getString().equals(userid)
							||root.getProperty(Config.EDMS_OWNER).getString().equals(userid)
							||newUser.contains(userid)) {*/
						root.setProperty(Config.USERS_READ, newUser.split(",,"));
						jcrsession.save();
					//}
					}
				}
			}
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "failure";
		}finally{
			//JcrRepositoryUtils.logout(sessionId);
		}
	}
	public String removePublicLink(
			String filePath,String userid,String password,String guestid){
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		File File1 = new File();
		try {
			Node root = jcrsession.getRootNode();
			if (filePath.length() > 1) {
				root = root.getNode(filePath.substring(1));
			}
			File1.setFileName(root.getName().toString());
			File1.setFilePath(root.getPath().toString());
			if (root.getPath().toString().length() > 1) {
				if (filePath.length() > 1) {
					if (root.hasProperty(Config.USERS_READ)) {
						Value[] actualUsers = root.getProperty(
								Config.USERS_READ).getValues();
						String newUser = "";
						for (int i = 0; i < actualUsers.length; i++) {
							if(actualUsers[i].getString().indexOf(Config.EDMS_GUEST)<0){
							if(actualUsers[i].getString().equals("")||actualUsers[i].getString()==""){}else
								{newUser += actualUsers[i].getString() + ",,";}
							}
						}
					//	newUser=newUser+Config.EDMS_GUEST+",,";
					/*if (root.getProperty(Config.EDMS_AUTHOR).getString().equals(userid)
							||root.getProperty(Config.EDMS_OWNER).getString().equals(userid)
							||newUser.contains(userid)) {*/
						root.setProperty(Config.USERS_READ, newUser.split(",,"));
						jcrsession.save();
					//}
					}
				}
			}
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "failure";
		}finally{
			//JcrRepositoryUtils.logout(sessionId);
		}
	}
	public String getPublicLinkDoc(
			String userid,String password,String filePath){
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		File File1 = new File();
		try {
			Node root = jcrsession.getRootNode();
			if (filePath.length() > 1) {
				root = root.getNode(filePath.substring(1));
			}
			File1.setFileName(root.getName().toString());
			File1.setFilePath(root.getPath().toString());
			if (root.getPath().toString().length() > 1) {
				if (filePath.length() > 1) {
					if (root.hasProperty(Config.USERS_READ)) {
						Value[] actualUsers = root.getProperty(
								Config.USERS_READ).getValues();
						String newUser = "";
						for (int i = 0; i < actualUsers.length; i++) {
							if(actualUsers[i].getString().indexOf(Config.EDMS_GUEST)<0)
							{
								if(actualUsers[i].getString().equals("")||actualUsers[i].getString()==""){}else
									{newUser += actualUsers[i].getString() + ",,";}
								}
						}
						newUser=newUser+Config.EDMS_GUEST+",,";
					if (root.getProperty(Config.EDMS_AUTHOR).getString().equals(userid)
							||root.getProperty(Config.EDMS_OWNER).getString().equals(userid)
							||newUser.contains(userid)) {
						root.setProperty(Config.USERS_READ, newUser.split(",,"));
					}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			//JcrRepositoryUtils.logout(sessionId);
		}
		return "Success";
	}
}