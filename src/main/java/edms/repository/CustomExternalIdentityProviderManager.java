//package edms.repository;
//
//
//
//import java.io.File;
//import java.io.IOException;
//import java.security.Principal;
//import java.security.PrivilegedActionException;
//import java.security.PrivilegedExceptionAction;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.Set;
//
//import javax.jcr.AccessDeniedException;
//import javax.jcr.Credentials;
//import javax.jcr.LoginException;
//import javax.jcr.Node;
//import javax.jcr.Repository;
//import javax.jcr.RepositoryException;
//import javax.jcr.Session;
//import javax.jcr.SimpleCredentials;
//import javax.jcr.UnsupportedRepositoryOperationException;
//import javax.jcr.ValueFactory;
//import javax.security.auth.Subject;
//import javax.security.auth.callback.Callback;
//import javax.security.auth.callback.CallbackHandler;
//import javax.security.auth.callback.UnsupportedCallbackException;
//import javax.security.auth.login.AppConfigurationEntry;
//import javax.security.auth.login.Configuration;
//import javax.security.auth.login.LoginContext;
//
//import org.apache.jackrabbit.api.JackrabbitRepository;
//import org.apache.jackrabbit.api.JackrabbitSession;
//import org.apache.jackrabbit.api.security.user.Authorizable;
//import org.apache.jackrabbit.api.security.user.UserManager;
//import org.apache.jackrabbit.oak.Oak;
//import org.apache.jackrabbit.oak.api.CommitFailedException;
//import org.apache.jackrabbit.oak.api.Root;
//import org.apache.jackrabbit.oak.jcr.Jcr;
//import org.apache.jackrabbit.oak.plugins.document.DocumentMK;
//import org.apache.jackrabbit.oak.plugins.document.DocumentNodeStore;
//import org.apache.jackrabbit.oak.plugins.segment.SegmentNodeStore;
//import org.apache.jackrabbit.oak.plugins.segment.file.FileStore;
//
//import edms.core.LdapIdentityProvider;
//import edms.core.TestLoginModule;
//
//import org.apache.jackrabbit.oak.security.authentication.ldap.impl.LdapProviderConfig;
//import org.apache.jackrabbit.oak.security.authentication.token.TokenLoginModule;
//import org.apache.jackrabbit.oak.security.authentication.user.LoginModuleImpl;
//import org.apache.jackrabbit.oak.spi.security.OpenSecurityProvider;
//import org.apache.jackrabbit.oak.spi.security.authentication.GuestLoginModule;
//import org.apache.jackrabbit.oak.spi.security.authentication.SystemSubject;
//import org.apache.jackrabbit.oak.spi.security.authentication.external.ExternalIdentityException;
//import org.apache.jackrabbit.oak.spi.security.authentication.external.ExternalIdentityProvider;
//import org.apache.jackrabbit.oak.spi.security.authentication.external.ExternalIdentityProviderManager;
//import org.apache.jackrabbit.oak.spi.security.authentication.external.ExternalIdentityRef;
//import org.apache.jackrabbit.oak.spi.security.authentication.external.ExternalUser;
//import org.apache.jackrabbit.oak.spi.security.authentication.external.SyncContext;
//import org.apache.jackrabbit.oak.spi.security.authentication.external.SyncException;
//import org.apache.jackrabbit.oak.spi.security.authentication.external.SyncHandler;
//import org.apache.jackrabbit.oak.spi.security.authentication.external.SyncManager;
//import org.apache.jackrabbit.oak.spi.security.authentication.external.SyncResult;
//import org.apache.jackrabbit.oak.spi.security.authentication.external.SyncedIdentity;
//import org.apache.jackrabbit.oak.spi.security.authentication.external.basic.DefaultSyncConfig;
//import org.apache.jackrabbit.oak.spi.security.authentication.external.impl.DefaultSyncHandler;
//import org.apache.jackrabbit.oak.spi.security.authentication.external.impl.ExternalIDPManagerImpl;
//import org.apache.jackrabbit.oak.spi.security.authentication.external.impl.ExternalLoginModule;
//import org.apache.jackrabbit.oak.spi.security.authentication.external.impl.SyncManagerImpl;
//import org.apache.jackrabbit.oak.spi.security.authentication.external.impl.jmx.SyncMBeanImpl;
//import org.apache.jackrabbit.oak.spi.whiteboard.Whiteboard;
//import org.apache.jackrabbit.value.ValueFactoryImpl;
//import org.springframework.stereotype.Component;
//
//import com.mongodb.DB;
//import com.mongodb.MongoClient;
//
//import edms.core.CustomSyncClass;
//@Component
//public class CustomExternalIdentityProviderManager  implements ExternalIdentityProviderManager{
//
//	@Override
//	public ExternalIdentityProvider getProvider(String name) {
//		// TODO Auto-generated method stub
//		
//		//org.apache.jackrabbit.oak.spi.security.authentication.external.impl.ExternalLoginModule loginModule=new ExternalLoginModule(osgiConfig)
//		
//		
//		
//		
//		ExternalIdentityProviderManager manager= new ExternalIdentityProviderManager() {
//			
//			@Override
//			public ExternalIdentityProvider getProvider(String name) {
//
//				LdapProviderConfig cfg = new LdapProviderConfig()
//
//						.setName("ldap")
//
//						.setHostname("192.168.1.199")
//
//						.setPort(389)
//
//						.setBindDN("cn=vmail,dc=silvereye,dc=in")
//
//						.setBindPassword("MpUpUk&&&??9898&&&")
//
//						.setGroupMemberAttribute("member")
//
//						.setSearchTimeout(10000)
//
//						.setUseSSL(false);
//
//						cfg.getUserConfig()
//
//						.setIdAttribute("mail")
//
//						.setBaseDN("o=domains,dc=silvereye,dc=in")
//
//						.setObjectClasses("mailUser");
//
//						cfg.getGroupConfig()
//
//						.setBaseDN("o=domains,dc=silvereye,dc=in")
//
//						.setObjectClasses("mailList");
//				
//				ExternalIdentityProvider provider=new LdapIdentityProvider(cfg);
//				
//				return provider;
//			}
//		};
//		
//		
//		
//		SyncManager syncManager=new SyncManager() {
//			
//			@Override
//			public SyncHandler getSyncHandler(String name) {
//
//				 DefaultSyncConfig syncConfig = new DefaultSyncConfig();
//
//				 Map<String, String> mapping = new HashMap<String, String>();
//
//				 mapping.put("rep:externalId","displayname");
//
//				 mapping.put("profile/name", "cn");
//
//				 mapping.put("profile/email", "mail");
//
//				 syncConfig.user().setPropertyMapping(mapping);
//
//				 syncConfig.user().setMembershipNestingDepth(1);
//				 DefaultSyncHandler synchandler=new DefaultSyncHandler(syncConfig);
//				return synchandler;
//			}
//		};
//		
//		//try {
//					
//
//				//Register IDP
//				//Oak oak = new Oak().with(new OpenSecurityProvider());
//				
//				
//				
//				DB db = new MongoClient("192.168.1.199", 27017).getDB("oak");
//			    DocumentNodeStore ns = new DocumentMK.Builder().setMongoDB(db).getNodeStore();
//			    Repository repository = new Jcr(new Oak(ns)).createRepository();
//			    //Root root=oak.createRoot();
//			    //ExternalIdentityRef userref=	provider.getUser("rohit@silvereye.in").getExternalId();
//			   // Repository repository = new Jcr(oak).createRepository();
//
//			 
//			   // Repository repository = new Jcr(new SegmentNodeStore(fs)).createRepository();
//			    
//			    
//			    
//			    CustomSyncClass sync= new CustomSyncClass(repository, syncManager, "default", manager, "ldap");	
//			    String[]	 syncedUsers=  sync.syncAllExternalUsers();
//			    //String[]	 syncedUsers1=  sync.syncAllExternalUsers();
//			    //String[] syncU=sync.syncAllUsers(true);
//			    Session session =null;
//			   
//				try {
//					final CallbackHandler callbackhandler=new TestCallBackHandler("rohit@silvereye.in", "Google@2009");
//					
//				session = repository.login(new Credentials() {
//				});
//					JackrabbitSession js=(JackrabbitSession)session;
//					UserManager userManager=js.getUserManager();
//					Authorizable user1=userManager.getAuthorizable("rohit@silvereye.in");
//					session = repository.login(new SimpleCredentials("rohit@silvereye.in", "Google@2009".toCharArray()));
//					System.out.println();
//				} catch (AccessDeniedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (LoginException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (UnsupportedRepositoryOperationException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (RepositoryException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (javax.security.auth.login.LoginException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//	//	System.out.println();
//		System.out.println(sync.getSyncHandlerName());
//		
//		/* Session jsession =null;
//		 
//		 try {
//			 jsession = Subject.doAs(SystemSubject.INSTANCE, new PrivilegedExceptionAction<Session>() {
//                 @Override
//                 public Session run() throws LoginException, RepositoryException {
//                     if (repo instanceof JackrabbitRepository) {
//                         // this is to bypass GuestCredentials injection in the "AbstractSlingRepository2"
//                         return ((JackrabbitRepository) repo).login(null, null, null);
//                     } else {
//                         return repo.login(null, null);
//                     }
//                 }
//             });
//         } catch (PrivilegedActionException e) {
//             try {
//				throw new SyncException(e);
//			} catch (SyncException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//         }
//		 
//		 Whiteboard whiteBoard = oak.getWhiteboard();
//
//		 whiteBoard.register(ExternalIdentityProvider.class, provider, Collections.<String, Object>emptyMap());
//
//
//
//		 //Define Sync properties
//
//		 DefaultSyncConfig syncConfig = new DefaultSyncConfig();
//
//		 Map<String, String> mapping = new HashMap<String, String>();
//
//		 mapping.put("rep:externalId","displayname");
//
//		 mapping.put("profile/name", "cn");
//
//		 mapping.put("profile/email", "mail");
//
//		 syncConfig.user().setPropertyMapping(mapping);
//
//		 syncConfig.user().setMembershipNestingDepth(1);
//		 
//		 
//		 whiteBoard.register(SyncHandler.class, new DefaultSyncHandler(syncConfig), Collections.<String, Object>emptyMap());
//
//
//
//		 whiteBoard.register(SyncManager.class, new SyncManagerImpl(whiteBoard), Collections.emptyMap());
//
//		 whiteBoard.register(ExternalIdentityProviderManager.class, new ExternalIDPManagerImpl(whiteBoard), Collections.emptyMap());
//
//		 SyncManager syncManager= new SyncManagerImpl(whiteBoard);
//		 System.out.println(syncManager.getSyncHandler(name));
//		 
//		 Session session =null;
//		
//			 session = repo.login(
//			            new SimpleCredentials("admin", "admin".toCharArray()));
//		 JackrabbitSession js=(JackrabbitSession)jsession;
//		 UserManager userManager=js.getUserManager();
//	
//			DefaultSyncConfig config=new DefaultSyncConfig();
//			DefaultSyncConfig.Group group=config.group();
//			group.setExpirationTime(86400000);
//			group.setAutoMembership("Group");
//			
//
//			DefaultSyncConfig.User user=config.user();
//			user.setExpirationTime(86400000);
//			user.setMembershipExpirationTime(86400000);
//			user.setAutoMembership("User");
//			user.setMembershipNestingDepth(0);
//			
//			config.setName("ldapConfig");
//			ValueFactory valueFactory=js.getValueFactory();
//			DefaultSyncHandler synchandler=new DefaultSyncHandler(config);
//			SyncContext syncContext=null;
//			try {
//				 syncContext= synchandler.createContext(provider, userManager, valueFactory);
//				 syncContext.setForceUserSync(true);
//				 syncContext.setForceGroupSync(true);
//				 System.out.println(synchandler.REP_LAST_SYNCED);
//					js.save();
//					try {
//						root.commit();
//					} catch (CommitFailedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//			} catch (SyncException e) {
//				e.printStackTrace();
//			}
//			try {
//				
//				
//				
//				
//				
//				
//				
//				
//				
//				
//				
//			SyncResult result=	syncContext.sync(userref.getId());
//			js.save();
//			System.out.println(result.getStatus());
//			Authorizable user1=userManager.getAuthorizable(userref.getId());
//			System.out.println();
//			} catch (SyncException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//			Iterator<SyncedIdentity> identities=	synchandler.listIdentities(userManager);
//			for (Iterator iterator = identities; iterator.hasNext();) {
//				SyncedIdentity type = (SyncedIdentity) iterator.next();
//				System.out.println(type.getId());
//			}
//			
//			
//		
//		} catch (ExternalIdentityException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (LoginException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (RepositoryException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		return provider;
//	}*/
//		  return null;
//	}
//	  
//
//
//}
