package edms.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;

import javax.annotation.PostConstruct;
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

import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.apache.jackrabbit.commons.cnd.ParseException;
import org.apache.jackrabbit.core.TransientRepository;

public class JcrRepositorySession {

	public static Session jcrsession = null;

	@PostConstruct
	public void initData() {
		System.out.println("only single time !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ");
		jcrsession=JcrRepositorySession.getSession();
	}
	public static Session getSession() {
		Repository repository = new TransientRepository();
		try {
			SimpleCredentials credential=new SimpleCredentials("admin",
					"admin".toCharArray());
			
			jcrsession = repository.login(credential, "default");

			//createFolder("santosh@avi-oil.com");
			//createFolder("santosh@avi-oil.com/trash");
			//jcrsession.getRootNode().getNode("santosh@avi-oil.com").remove();;
			//jcrsession.getRootNode().getNode("santosh@avi-oil.com/trash").remove();
			//jcrsession.save();
			//registerNamespace(jcrsession, jcrsession.getRootNode());
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
			//createUser(userid, "redhat", jcrsession, root);
		 	//setPolicy(jcrsession, root, userid,root.getPath(),  Privilege.JCR_ALL);
			//Workspace ws=jcrsession.getWorkspace();
			//ws.createWorkspace(userid);
			
			} catch (RepositoryException e) {
				e.printStackTrace();
				return null;
		}
		return jcrsession;
	}

	public static Node createFolder(String folderName) {
		Node folder = null;
		try {
			Node root = jcrsession.getRootNode();
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
			jcrsession.save();
		} catch (LoginException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
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
		} catch (RepositoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
