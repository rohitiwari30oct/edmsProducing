package edms.core;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.GuestCredentials;
import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Workspace;
import javax.security.auth.Subject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.core.security.principal.AdminPrincipal;
import org.apache.jackrabbit.j2ee.SimpleWebdavServlet;
import org.apache.jackrabbit.jcr2spi.config.RepositoryConfig;
import org.apache.jackrabbit.rmi.remote.RemoteRepository;
import org.apache.jackrabbit.rmi.server.RemoteAdapterFactory;
import org.apache.jackrabbit.rmi.server.ServerAdapterFactory;

import edms.repository.FileRepository;
import edms.repository.FolderRepository;
 
/**
 * An utility class that manages the sessions. 
 * Simple implementation uses a transient repository
 * and allows any number of logins with the same id.
 * Session is wrapped a SessionWrapper class.
 * The session wrapper uniquely identifies each session with
 * an unique id. While it is advisable to call the logout(sessionid)
 * method to release the resources, finalization constructs will take care
 * of releasing resources anyways.
 * @author Boni.G
 *
 */
public class JcrRepositoryUtils {
	static Map<String, List<SessionWrapper>> currentSessions;
	static Log logger = LogFactory.getLog(JcrRepositoryUtils.class);
	static Repository aRepository;
	/**
	 * Use this static method to login to the repository.  The repository 
	 * currently uses the SimpleLoginModule that will authorize any tom, dick
	 * and even harry.  In a production environments it is advisable to wrap
	 * the suitable authentication mechanism as a JAAS module.  We will be
	 * discussing all these and more during the next few posts. 
	 * @param userName
	 * @param password
	 * @return
	 * @throws AlreadyBoundException 
	 * @throws RemoteException 
	 * @throws MalformedURLException 
	 */
	public static SessionWrapper adminlogin(final String userName, final String password) throws MalformedURLException, RemoteException, AlreadyBoundException{
		//	userName=userName.toLowerCase();
			final Repository repository = getRepositoryHandle();

			Session aSession = null;
			if (currentSessions == null){
				initializeSessionsMap();
				}
				//in case of same domain workspace only.
				try {
					
					
					
				List<SessionWrapper> sessions = currentSessions.get(userName+"@"+Config.EDMS_DOMAIN+password);
				if (sessions == null){
						
					
					aSession = repository.login( new SimpleCredentials(userName+"@"+Config.EDMS_DOMAIN, password.toCharArray()));
					JackrabbitSession js = ((JackrabbitSession) aSession);
					Subject subject = ((SessionImpl) js).getSubject();
					Subject combinedSubject=new Subject(false,subject.getPrincipals(),subject.getPublicCredentials(),subject.getPrivateCredentials());
					combinedSubject.getPrincipals().add(new AdminPrincipal("admin"));
					try {
						aSession = Subject.doAsPrivileged(combinedSubject,
								new PrivilegedExceptionAction<Session>() {
									public Session run()  {
										//Session ss = repository.login(new SimpleCredentials(userName+"@"+Config.EDMS_DOMAIN, password.toCharArray()),Config.EDMS_DOMAIN);
										Session ss = null;
										try {
											ss =repository.login();
										} catch (LoginException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (RepositoryException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										return ss;
									}
								}, AccessController.getContext());
					} catch (PrivilegedActionException e1) {
						// TODO Auto-generated catch block
						//e1.printStackTrace();
					}
					Workspace ws=aSession.getWorkspace();
					String[] workspaceNames = ws.getAccessibleWorkspaceNames();
						//  System.out.println(Arrays.asList(workspaceNames).contains(userName));
					if(Arrays.asList(workspaceNames).contains(Config.EDMS_DOMAIN)){
						//	JcrRepositorySession.createUser(userName, password, jcrsession,null);
						//	JcrRepositorySession.createUser(userName, password, jcrsession,null);
						}else{
						ws.createWorkspace(Config.EDMS_DOMAIN);
						//	JcrRepositorySession.createUser(userName, password, jcrsession,null);
						//	ws.createWorkspace(userName.substring(userName.indexOf('@')+1));
						}
					aSession.logout();
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					//aSession = repository.login( new GuestCredentials(),Config.EDMS_DOMAIN);
						//  aSession = repository.login( new SimpleCredentials(userName, password.toCharArray()),Config.EDMS_DOMAIN);
						//aSession = repository.login( new SimpleCredentials(userName+"@avi-oil.com", password.toCharArray()),Config.EDMS_DOMAIN);						
						aSession = repository.login( new SimpleCredentials(userName+"@"+Config.EDMS_DOMAIN, password.toCharArray()),Config.EDMS_DOMAIN);
						//SessionImpl si = (SessionImpl) jcrsession;
						System.out.println(aSession.getWorkspace().getName());
						 js = ((JackrabbitSession) aSession);
						 subject = ((SessionImpl) js).getSubject();
						//	Set<Principal> principals = new LinkedHashSet<Principal>();
						//	principals = subject.getPrincipals();
						 combinedSubject=new Subject(false,subject.getPrincipals(),subject.getPublicCredentials(),subject.getPrivateCredentials());
						combinedSubject.getPrincipals().add(new AdminPrincipal("admin"));
						try {
							aSession = Subject.doAsPrivileged(combinedSubject,
									new PrivilegedExceptionAction<Session>() {
										public Session run()  {
											//Session ss = repository.login(new SimpleCredentials(userName+"@"+Config.EDMS_DOMAIN, password.toCharArray()),Config.EDMS_DOMAIN);
											Session ss = null;
											try {
												ss =repository.login(Config.EDMS_DOMAIN);
											} catch (LoginException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											} catch (RepositoryException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
											return ss;
										}
									}, AccessController.getContext());
						} catch (PrivilegedActionException e1) {
							// TODO Auto-generated catch block
							//e1.printStackTrace();
						}
						sessions = Collections.synchronizedList(new ArrayList<SessionWrapper>());
						System.out.println(aSession.getWorkspace().getName());
						SessionWrapper wrappedSession = new SessionWrapper( aSession); 
						sessions.add(wrappedSession);
						currentSessions.put(userName+"@"+Config.EDMS_DOMAIN+password, sessions);
						return wrappedSession;
	}else{
		aSession=sessions.get(0).getSession();
		SessionWrapper wrappedSession = new SessionWrapper( aSession); 
		return wrappedSession;
	}
		} catch (RepositoryException e) {
			throw new RuntimeException(e);
				}
	}
	public synchronized static SessionWrapper adminloginDAV(final String userName, final String password) throws MalformedURLException, RemoteException, AlreadyBoundException{
		//	userName=userName.toLowerCase();
			final Repository repository = getRepositoryHandle();
			Session aSession = null;
			if (currentSessions == null){
				initializeSessionsMap();
				}
				//in case of same domain workspace only.
				try {
					
					
					
				List<SessionWrapper> sessions = currentSessions.get(userName+"@DAV"+Config.EDMS_DOMAIN+password);
				if (sessions == null){
						//aSession = repository.login( new GuestCredentials(),Config.EDMS_DOMAIN);
						//  aSession = repository.login( new SimpleCredentials(userName, password.toCharArray()),Config.EDMS_DOMAIN);
						//aSession = repository.login( new SimpleCredentials(userName+"@avi-oil.com", password.toCharArray()),Config.EDMS_DOMAIN);						
						aSession = repository.login( new SimpleCredentials(userName+"@"+Config.EDMS_DOMAIN, password.toCharArray()));
						JackrabbitSession js = ((JackrabbitSession) aSession);
						Subject subject = ((SessionImpl) js).getSubject();
						Subject combinedSubject=new Subject(false,subject.getPrincipals(),subject.getPublicCredentials(),subject.getPrivateCredentials());
						combinedSubject.getPrincipals().add(new AdminPrincipal("admin"));
						try {
							aSession = Subject.doAsPrivileged(combinedSubject,
									new PrivilegedExceptionAction<Session>() {
										public Session run()  {
											//Session ss = repository.login(new SimpleCredentials(userName+"@"+Config.EDMS_DOMAIN, password.toCharArray()),Config.EDMS_DOMAIN);
											Session ss = null;
											try {
												ss =repository.login();
											} catch (LoginException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											} catch (RepositoryException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
											return ss;
										}
									}, AccessController.getContext());
						} catch (PrivilegedActionException e1) {
							// TODO Auto-generated catch block
							//e1.printStackTrace();
						}
						Workspace ws=aSession.getWorkspace();
						String[] workspaceNames = ws.getAccessibleWorkspaceNames();
							//  System.out.println(Arrays.asList(workspaceNames).contains(userName));
						if(Arrays.asList(workspaceNames).contains("DAV")){
							//	JcrRepositorySession.createUser(userName, password, jcrsession,null);
							//	JcrRepositorySession.createUser(userName, password, jcrsession,null);
							}else{
							ws.createWorkspace("DAV");
							//	JcrRepositorySession.createUser(userName, password, jcrsession,null);
							//	ws.createWorkspace(userName.substring(userName.indexOf('@')+1));
							}
						aSession.logout();
						

						aSession = repository.login( new SimpleCredentials(userName+"@"+Config.EDMS_DOMAIN, password.toCharArray()),"DAV");
						//SessionImpl si = (SessionImpl) jcrsession;
						System.out.println(aSession.getWorkspace().getName());
						 js = ((JackrabbitSession) aSession);
						 subject = ((SessionImpl) js).getSubject();
						//	Set<Principal> principals = new LinkedHashSet<Principal>();
						//	principals = subject.getPrincipals();
						 combinedSubject=new Subject(false,subject.getPrincipals(),subject.getPublicCredentials(),subject.getPrivateCredentials());
						combinedSubject.getPrincipals().add(new AdminPrincipal("admin"));
						try {
							aSession = Subject.doAsPrivileged(combinedSubject,
									new PrivilegedExceptionAction<Session>() {
										public Session run()  {
											//Session ss = repository.login(new SimpleCredentials(userName+"@"+Config.EDMS_DOMAIN, password.toCharArray()),Config.EDMS_DOMAIN);
											Session ss = null;
											try {
												ss =repository.login("DAV");
											} catch (LoginException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											} catch (RepositoryException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
											return ss;
										}
									}, AccessController.getContext());
						} catch (PrivilegedActionException e1) {
							// TODO Auto-generated catch block
							//e1.printStackTrace();
						}
						sessions = Collections.synchronizedList(new ArrayList<SessionWrapper>());
						System.out.println(aSession.getWorkspace().getName());
						SessionWrapper wrappedSession = new SessionWrapper( aSession); 
						sessions.add(wrappedSession);
						currentSessions.put(userName+"@DAV"+Config.EDMS_DOMAIN+password, sessions);
						return wrappedSession;
	}else{
		aSession=sessions.get(0).getSession();
		SessionWrapper wrappedSession = new SessionWrapper( aSession); 
		return wrappedSession;
	}
		} catch (RepositoryException e) {
			throw new RuntimeException(e);
				}
	}
	public static SessionWrapper adminloginToWorkSpace(String userName, String password){
		try {
			userName=userName.toLowerCase();
			//in case of same domain workspace only.
			String domain=userName.substring(userName.indexOf('@')+1);
			Repository repository = Config.repository;
			Session aSession = null;
			if (currentSessions == null)
				{
				initializeSessionsMap();
				}
			List<SessionWrapper> sessions = currentSessions.get(Config.EDMS_ADMIN+userName);
			if (sessions == null){
					aSession=repository.login(new SimpleCredentials(Config.EDMS_ADMIN,Config.EDMS_ADMIN.toCharArray()),domain);
					//aSession=repository.login(new SimpleCredentials(userName,password.toCharArray()));
					sessions = Collections.synchronizedList(new ArrayList<SessionWrapper>());
					SessionWrapper wrappedSession = new SessionWrapper( aSession); 
					sessions.add(wrappedSession);
					currentSessions.put(Config.EDMS_ADMIN+userName, sessions);
					return wrappedSession;
				}else{
					aSession=sessions.get(0).getSession();
					SessionWrapper wrappedSession = new SessionWrapper( aSession); 
					return wrappedSession;
				}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
/*
	public static SessionWrapper login(String userName, String password){
		try {
		//	System.out.println("userid is "+userName);
			SessionWrapper wrapper=	adminlogin(userName, password);
			Session jcrsession=wrapper.getSession();
			Workspace ws=jcrsession.getWorkspace();
			String[] workspaceNames = ws.getAccessibleWorkspaceNames();
			//System.out.println(Arrays.asList(workspaceNames).contains(userName));
			if(Arrays.asList(workspaceNames).contains(userName)){
				//JcrRepositorySession.createUser(userName, password, jcrsession,null);
			}else{ 
				ws.createWorkspace(userName);
				//ws.createWorkspace(userName.substring(userName.indexOf('@')+1));
				JcrRepositorySession.createUser(userName, password, jcrsession,null);
				JcrRepositorySession.setPolicy(jcrsession, null, userName,null,  Privilege.JCR_ALL);		
			}
			//userName="admin";
			//password="admin";
			Repository repository = getRepositoryHandle();
			Session aSession = null;
			if (currentSessions == null){
				initializeSessionsMap();
				}
			List<SessionWrapper> sessions = currentSessions.get(userName);
			if (sessions == null){
				
			
				aSession=repository.login(new SimpleCredentials(userName,password.toCharArray()),userName);
				
				JcrRepositorySession.createFolder(userName,aSession);
				JcrRepositorySession.createFolder(userName+"/trash",aSession);
				//JcrRepositorySession.createFolder(userName+"/calendar",aSession);
				//JcrRepositorySession.createFolder(userName+"/Contacts",aSession);
				//JcrRepositorySession.createFolder(userName+"/Contacts/Perasonal Contacts",aSession);
				//JcrRepositorySession.createFolder(userName+"/SharedContacts",aSession);
				//JcrRepositorySession.createFolder(userName+"/SharedCalendars",aSession);
				
				//aSession=repository.login(new SimpleCredentials(userName,password.toCharArray()));
				sessions = Collections.synchronizedList(new ArrayList<SessionWrapper>());
				SessionWrapper wrappedSession = new SessionWrapper( aSession); 
				sessions.add(wrappedSession);
				currentSessions.put(userName, sessions);
				return wrappedSession;
				}else{
					aSession=sessions.get(0).getSession();
					SessionWrapper wrappedSession = new SessionWrapper( aSession); 
					return wrappedSession;
				}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	}
	*/
	

	public static SessionWrapper login(String userName, String password){
		try {
			userName=userName.toLowerCase();
			String domain=userName.substring(userName.indexOf('@')+1);
			Config.EDMS_DOMAIN=domain;
			if(userName.indexOf('@')>0)
			userName=userName.substring(0,userName.indexOf('@'));
			//	System.out.println("userid is "+userName);
			SessionWrapper wrapper=	adminlogin(userName, password);
			Session jcrsession=wrapper.getSession();
			Workspace ws=jcrsession.getWorkspace();
			String[] workspaceNames = ws.getAccessibleWorkspaceNames();
				//  System.out.println(Arrays.asList(workspaceNames).contains(userName));
			if(Arrays.asList(workspaceNames).contains(domain)){
				//	JcrRepositorySession.createUser(userName, password, jcrsession,null);
				//	JcrRepositorySession.createUser(userName, password, jcrsession,null);
				}else{
				ws.createWorkspace(domain);
				//	JcrRepositorySession.createUser(userName, password, jcrsession,null);
				//	ws.createWorkspace(userName.substring(userName.indexOf('@')+1));
				}
				//	userName="admin";
				//	password="admin";
			final Repository repository =Config.repository;
			Session aSession = null;
			if (currentSessions == null){
				initializeSessionsMap();
				}
			List<SessionWrapper> sessions = currentSessions.get(userName+"@"+Config.EDMS_DOMAIN+password);
			if (sessions == null){
					//	aSession=repository.login(new SimpleCredentials(userName.substring(0,userName.indexOf('@')),password.toCharArray()),domain);
					//	aSession=repository.login(new SimpleCredentials(userName,password.toCharArray()),domain);
				try {
					aSession = repository.login( new SimpleCredentials(userName, password.toCharArray()), domain);
					JackrabbitSession js = ((JackrabbitSession) jcrsession);
					Subject subject = ((SessionImpl) js).getSubject();
					Subject combinedSubject=new Subject(false,subject.getPrincipals(),subject.getPublicCredentials(),subject.getPrivateCredentials());
					combinedSubject.getPrincipals().add(new AdminPrincipal("admin"));
					try {
						aSession = Subject.doAsPrivileged(combinedSubject,
								new PrivilegedExceptionAction<Session>() {
									public Session run() throws Exception {
										Session ss = repository.login(Config.EDMS_DOMAIN);
										return ss;
									}
								}, AccessController.getContext());
					} catch (PrivilegedActionException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} catch (RepositoryException e) {
					e.printStackTrace();
				}
				//aSession.impersonate(new SimpleCredentials("admin","admin".toCharArray())); 
				JcrRepositorySession.createFolder(userName+"@"+domain,aSession,password);
				JcrRepositorySession.createFolder(userName+"@"+domain+"/trash",aSession,password);
				
				
				//JcrRepositorySession.createFolder(userName.substring(0,userName.indexOf('@')),aSession);
				//JcrRepositorySession.createFolder(userName.substring(0,userName.indexOf('@'))+"/trash",aSession);
				//JcrRepositorySession.createFolder(userName+"/calendar",aSession);
				//JcrRepositorySession.createFolder(userName+"/Contacts",aSession);
				//JcrRepositorySession.createFolder(userName+"/Contacts/Perasonal Contacts",aSession);
				//JcrRepositorySession.createFolder(userName+"/SharedContacts",aSession);
				//JcrRepositorySession.createFolder(userName+"/SharedCalendars",aSession);
				//aSession=repository.login(new SimpleCredentials(userName,password.toCharArray()));
				sessions = Collections.synchronizedList(new ArrayList<SessionWrapper>());
				SessionWrapper wrappedSession = new SessionWrapper( aSession); 
				sessions.add(wrappedSession);
				currentSessions.put(userName+"@"+Config.EDMS_DOMAIN+password, sessions);
				return wrappedSession;
				}else{
					aSession=sessions.get(0).getSession();
					System.out.println(aSession.getWorkspace().getName());
					System.out.println();
				//	SessionImpl si = (SessionImpl) jcrsession;
					/*JackrabbitSession js = ((JackrabbitSession) jcrsession);
					Subject subject = ((SessionImpl) js).getSubject();
					//Set<Principal> principals = new LinkedHashSet<Principal>();
					//principals = subject.getPrincipals();
					Subject combinedSubject=new Subject(false,subject.getPrincipals(),subject.getPublicCredentials(),subject.getPrivateCredentials());
					combinedSubject.getPrincipals().add(new AdminPrincipal("admin"));
					try {
						aSession = Subject.doAsPrivileged(combinedSubject,
								new PrivilegedExceptionAction<Session>() {
									public Session run() throws Exception {
										//Session ss = repository.login();
										Session ss = repository.login(Config.EDMS_DOMAIN);
										return ss;
									}
								}, AccessController.getContext());
					} catch (PrivilegedActionException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}*/
					//JcrRepositorySession.createFolder(userName.substring(0,userName.indexOf('@')),aSession);
					//JcrRepositorySession.createFolder(userName.substring(0,userName.indexOf('@'))+"/trash",aSession);
					JcrRepositorySession.createFolder(userName+"@"+domain,aSession,password);
					JcrRepositorySession.createFolder(userName+"@"+domain+"/trash",aSession,password);
					/*	fl.setPolicyForAllowTest("/",userName+"@"+domain,password);
					fl.setPolicyForDenyTest(userName+"@"+domain,password);
					fl.setPolicyForAllowTest("/"+userName+"@"+domain,userName+"@"+domain,password);*/
					SessionWrapper wrappedSession = new SessionWrapper(aSession); 
					return wrappedSession;
				}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	}
	public synchronized static SessionWrapper loginDAV(String userName, String password){
		try {
			userName=userName.toLowerCase();
			String domain=userName.substring(userName.indexOf('@')+1);
			Config.EDMS_DOMAIN=domain;
			if(userName.indexOf('@')>0)
			userName=userName.substring(0,userName.indexOf('@'));
			//	System.out.println("userid is "+userName);
			SessionWrapper wrapper=	adminloginDAV(userName, password);
			Session jcrsession=wrapper.getSession();
			
				//	userName="admin";
				//	password="admin";
			final Repository repository =Config.repository;
			Session aSession = null;
			if (currentSessions == null){
				initializeSessionsMap();
				}
			List<SessionWrapper> sessions = currentSessions.get(userName+"@DAV"+Config.EDMS_DOMAIN+password);
			if (sessions == null){
					//	aSession=repository.login(new SimpleCredentials(userName.substring(0,userName.indexOf('@')),password.toCharArray()),domain);
					//	aSession=repository.login(new SimpleCredentials(userName,password.toCharArray()),domain);
				try {
					aSession = repository.login( new SimpleCredentials(userName, password.toCharArray()), domain);
					//	SessionImpl si = (SessionImpl) jcrsession;	
					JackrabbitSession js = ((JackrabbitSession) jcrsession);
					Subject subject = ((SessionImpl) js).getSubject();
					//	Set<Principal> principals = new LinkedHashSet<Principal>();
					//	principals = subject.getPrincipals();
					Subject combinedSubject=new Subject(false,subject.getPrincipals(),subject.getPublicCredentials(),subject.getPrivateCredentials());
					combinedSubject.getPrincipals().add(new AdminPrincipal("admin"));
					try {
						aSession = Subject.doAsPrivileged(combinedSubject,
								new PrivilegedExceptionAction<Session>() {
									public Session run() throws Exception {
										//Session ss = repository.login();
										Session ss = repository.login("DAV");
										return ss;
									}
								}, AccessController.getContext());
					} catch (PrivilegedActionException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} catch (RepositoryException e) {
					e.printStackTrace();
				}
				//aSession.impersonate(new SimpleCredentials("admin","admin".toCharArray())); 
				JcrRepositorySession.createFolder(userName+"@"+domain,aSession,password);
				//JcrRepositorySession.createFolder(userName+"@"+domain+"/trash",aSession,password);
				
				
				//JcrRepositorySession.createFolder(userName.substring(0,userName.indexOf('@')),aSession);
				//JcrRepositorySession.createFolder(userName.substring(0,userName.indexOf('@'))+"/trash",aSession);
				//JcrRepositorySession.createFolder(userName+"/calendar",aSession);
				//JcrRepositorySession.createFolder(userName+"/Contacts",aSession);
				//JcrRepositorySession.createFolder(userName+"/Contacts/Perasonal Contacts",aSession);
				//JcrRepositorySession.createFolder(userName+"/SharedContacts",aSession);
				//JcrRepositorySession.createFolder(userName+"/SharedCalendars",aSession);
				//aSession=repository.login(new SimpleCredentials(userName,password.toCharArray()));
				sessions = Collections.synchronizedList(new ArrayList<SessionWrapper>());
				SessionWrapper wrappedSession = new SessionWrapper( aSession); 
				sessions.add(wrappedSession);
				currentSessions.put(userName+"@DAV"+Config.EDMS_DOMAIN+password, sessions);
				return wrappedSession;
				}else{
					aSession=sessions.get(0).getSession();
					System.out.println(aSession.getWorkspace().getName());
					System.out.println();
				//	SessionImpl si = (SessionImpl) jcrsession;
					/*JackrabbitSession js = ((JackrabbitSession) jcrsession);
					Subject subject = ((SessionImpl) js).getSubject();
					//Set<Principal> principals = new LinkedHashSet<Principal>();
					//principals = subject.getPrincipals();
					Subject combinedSubject=new Subject(false,subject.getPrincipals(),subject.getPublicCredentials(),subject.getPrivateCredentials());
					combinedSubject.getPrincipals().add(new AdminPrincipal("admin"));
					try {
						aSession = Subject.doAsPrivileged(combinedSubject,
								new PrivilegedExceptionAction<Session>() {
									public Session run() throws Exception {
										//Session ss = repository.login();
										Session ss = repository.login(Config.EDMS_DOMAIN);
										return ss;
									}
								}, AccessController.getContext());
					} catch (PrivilegedActionException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}*/
					//JcrRepositorySession.createFolder(userName.substring(0,userName.indexOf('@')),aSession);
					//JcrRepositorySession.createFolder(userName.substring(0,userName.indexOf('@'))+"/trash",aSession);
					JcrRepositorySession.createFolder(userName+"@"+domain,aSession,password);
				//	JcrRepositorySession.createFolder(userName+"@"+domain+"/trash",aSession,password);
					/*	fl.setPolicyForAllowTest("/",userName+"@"+domain,password);
					fl.setPolicyForDenyTest(userName+"@"+domain,password);
					fl.setPolicyForAllowTest("/"+userName+"@"+domain,userName+"@"+domain,password);*/
					SessionWrapper wrappedSession = new SessionWrapper(aSession); 
					return wrappedSession;
				}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	}
	
	/**
	 * Logout will release the resources being used by the session.
	 * @param sessionId
	 */
	public static void logout(String sessionId){
		if (currentSessions == null) return;
		Set<String> userNames = currentSessions.keySet();
		for (String aName : userNames){
			List<SessionWrapper> sessions = currentSessions.get(aName);
			if (logout(sessions, sessionId)){
				return;
			}
		}
	}
	/**
	 * The method that actually does the session logout.
	 * @param sessions
	 * @param sessionId
	 * @return
	 */
	private synchronized static boolean logout(List<SessionWrapper> sessions, String sessionId){
		if (sessions == null || sessionId == null || "".equals(sessionId)) return false;
		for (SessionWrapper aWrapper : sessions){
			if (sessionId.equals(aWrapper.getId())){
				sessions.remove(aWrapper);
				aWrapper.logout();
				aWrapper = null;
				return true;
			}
		}
		return false;
	}
	private static void initializeSessionsMap(){
		currentSessions = Collections.synchronizedMap(new HashMap<String, List<SessionWrapper>>());
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run() {
				logger.debug("Shutdown hook initializing the session cleanup...");
				releaseAllSessions();
				logger.debug("Session cleanup complete.........................");
			}
		});
	}
	
	private static void releaseAllSessions(){
		if (currentSessions == null) return;
		Set<String> userNames = currentSessions.keySet();
		for (String aName : userNames){
			List<SessionWrapper> sessions = currentSessions.get(aName);
			for (SessionWrapper sw : sessions){
				sw.logout();
				logger.debug("Shutdown Session................................:" + sw.getId());
				sw = null;
			}
		}
	}

	public static Repository getRepositoryHandle() throws MalformedURLException, RemoteException, AlreadyBoundException{
		Repository	repository =null;
			try {
			repository = JcrUtils.getRepository();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		
		/*try {
			repository = JcrUtils.getRepository("http://localhost:8082/jackrabbit-webapp/server");
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	
	
	Config.repository=repository;
	//repository=Config.repository;
		return repository;
	}
	public static Repository getUsernamePassword() throws MalformedURLException, RemoteException, AlreadyBoundException{
		Repository	repository =null;
			try {
			repository = JcrUtils.getRepository();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		
		/*try {
			repository = JcrUtils.getRepository("http://localhost:8082/jackrabbit-webapp/server");
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	
	
	Config.repository=repository;
	//repository=Config.repository;
		return repository;
	}
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		releaseAllSessions();
	}

	public static void processFileDir(String path) throws IOException {
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				CreateNode(listOfFiles[i], true);
			} else if (listOfFiles[i].isDirectory()) {
				CreateNode(listOfFiles[i], false);
				processFileDir(listOfFiles[i].getCanonicalPath());
			}
		}
}

private static void CreateNode(File file, boolean IsFile) throws IOException {
	
String stype = (IsFile?"File- ":"Dir- ");
if(IsFile){
	
	InputStream is = new FileInputStream(file);
	byte[] encodedBaseData = org.apache.commons.codec.binary.Base64.encodeBase64(IOUtils.toByteArray(is));
	String pp=file.getCanonicalPath();
	if(pp.lastIndexOf("\\"+file.getName())>=0)
	pp=pp.substring(0,pp.lastIndexOf("\\"+file.getName()));
	String path=pp;
	if(pp.lastIndexOf("/"+file.getName())>=0)
	path=path.substring(0,path.lastIndexOf("/"+file.getName()));
	//String path=file.getCanonicalPath().replace("\\"+file.getName(),"");
//	path=path.replace("/"+file.getName(),"");
	path=path.replace("\\", "/");
	path=path.replace(Config.EDMS_BULKUPLOAD_PATH, "/"+Config.EDMS_ADMINISTRATOR);
	long size=file.length();
	//String size="";
	
	 if (size >= 1073741824) {
         size= (size / 1073741824) ;
     }

     if (size >= 1048576) {
    	 size= (size / 1048576) ;
     }
     if (size >= 1024) {
    	 size= (size / 1024) ;
     }

   //    size= (size/ 4);
	
	
	
	//path=path.replace(Config.EDMS_BULKUPLOAD_PATH, "/"+Config.EDMS_ADMINISTRATOR+"/Information Technology/IT Vendor & Purchase Detail");
	new FileRepository().createFile(file.getName().trim(),path.trim() , Config.EDMS_ADMINISTRATOR,Config.EDMS_PASSWORD, "", "", encodedBaseData, size);
	}else{
		String pp=file.getCanonicalPath();
		if(pp.lastIndexOf("\\"+file.getName())>=0)
		pp=pp.substring(0,pp.lastIndexOf("\\"+file.getName()));
		String path=pp;
		if(pp.lastIndexOf("/"+file.getName())>=0)
		path=path.substring(0,path.lastIndexOf("/"+file.getName()));
		path=path.replace("\\", "/");
		path=path.replace(Config.EDMS_BULKUPLOAD_PATH, "/"+Config.EDMS_ADMINISTRATOR);
		//path=path.replace(Config.EDMS_BULKUPLOAD_PATH, "/"+Config.EDMS_ADMINISTRATOR+"/Information Technology/IT Vendor & Purchase Detail");
		new FolderRepository().createFolder(file.getName().trim(), path.trim(), Config.EDMS_ADMINISTRATOR, Config.EDMS_PASSWORD,"", "");
	}
//System.out.println(stype + file.getCanonicalPath());

}
}