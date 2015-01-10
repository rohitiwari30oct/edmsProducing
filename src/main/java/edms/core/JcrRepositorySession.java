package edms.core;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import org.apache.jackrabbit.JcrConstants;
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
			jcrsession = repository.login(new SimpleCredentials("admin",
					"admin".toCharArray()));
		//	createFolder("santosh@avi-oil.com");
		//	createFolder("sanjay@avi-oil.com/trash");
		//	createFolder("sanjay@avi-oil.com/sharedByOthers");
		//	createFolder("sanjay@avi-oil.com/sharedCalendersByOthers");
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
			folder.setProperty(Config.EDMS_DESCRIPTION, "this is root folder");
			folder.setProperty(Config.EDMS_CREATIONDATE,(new Date()).toString());
			folder.setProperty(Config.EDMS_MODIFICATIONDATE,(new Date()).toString());
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
}
