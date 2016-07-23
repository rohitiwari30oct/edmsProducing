package edms.repository;


import java.util.Properties;



import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.ldap.LDAPConfigurator;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.apache.commons.dbcp.BasicDataSource;

/*import org.apache.jackrabbit.oak.spi.security.authentication.external.ExternalIdentityProvider;

import org.apache.jackrabbit.oak.spi.security.authentication.external.SyncHandler;*/

import org.hibernate.SessionFactory;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerProperties;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;
import org.springframework.core.io.Resource;

@EnableWs
@Configuration
@EnableTransactionManagement
public class WebServiceConfig extends WsConfigurerAdapter {
	
	Properties hibernateProperties(){
		return new Properties(){
			{
				setProperty("hibernate.hbm2ddl.auto", "update");
				setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
				setProperty("hibernate.show_sql", "true");
			}
		};
	} 



	
	@Bean
	public SessionFactory buildSessionFactory() {
		try {
			
			return sessionFactory().getObject();

		} catch (Throwable ex) {
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}
	
	@Bean
	public FreeMarkerProperties freeMarkerConfigurer() {
	    FreeMarkerProperties  setting=new FreeMarkerProperties();
	    setting.setCheckTemplateLocation(false);
	    return setting;
	}
	
	@Bean
	public SpringProcessEngineConfiguration processEngineConfiguration(){
		SpringProcessEngineConfiguration springProcessEngineConfiguration = new SpringProcessEngineConfiguration();
		springProcessEngineConfiguration.setDatabaseType("mysql");
		springProcessEngineConfiguration.setDataSource(dataSource());
		springProcessEngineConfiguration.setTransactionManager(transactionManager());
		springProcessEngineConfiguration.setDatabaseSchemaUpdate("true");
		springProcessEngineConfiguration.setJobExecutorActivate(true);
		springProcessEngineConfiguration.setHistory("full");
		Resource[] resources = new Resource[7];
		resources[0]=new ClassPathResource("/com/edms/activitiautodeployment/autodeploy.LeaveRequest.bpmn20.xml");
		resources[1]=new ClassPathResource("/com/edms/activitiautodeployment/autodeploy.interOfficeMemo.bpmn20.xml");
		resources[2]=new ClassPathResource("/com/edms/activitiautodeployment/autodeploy.PurchaseRequisitionRequest.bpmn20.xml");
		resources[3]=new ClassPathResource("/com/edms/activitiautodeployment/autodeploy.CashPaymentVoucher.bpmn20.xml");
		resources[4]=new ClassPathResource("/com/edms/activitiautodeployment/autodeploy.PurchaseRequisitionRequestHO.bpmn20.xml");
		resources[5]=new ClassPathResource("/com/edms/activitiautodeployment/autodeploy.TravelReimbursement.bpmn20.xml");
		resources[6]=new ClassPathResource("/com/edms/activitiautodeployment/autodeploy.MedicalReimbursement.bpmn20.xml");
		springProcessEngineConfiguration.setDeploymentResources(resources);
	/*
		springProcessEngineConfiguration.setDeploymentMode("single-resource");
		springProcessEngineConfiguration.setMailServerHost("180.151.10.150");
		springProcessEngineConfiguration.setMailServerPort(25);
		springProcessEngineConfiguration.setMailServerUseTLS(false);
		springProcessEngineConfiguration.setMailServerUsername("sanjay@avi-oil.com");
		springProcessEngineConfiguration.setMailServerPassword("google@2009");*/

		/*springProcessEngineConfiguration.setDeploymentMode("single-resource");
		springProcessEngineConfiguration.setMailServerHost("mail.avi-oil.com");
		springProcessEngineConfiguration.setMailServerPort(587);
		springProcessEngineConfiguration.setMailServerUseTLS(true);
		springProcessEngineConfiguration.setMailServerUsername("demo1@avi-oil.com");
		springProcessEngineConfiguration.setMailServerPassword("google@2009");*/
		
		springProcessEngineConfiguration.setDeploymentMode("single-resource");
		springProcessEngineConfiguration.setMailServerHost("127.0.0.1");
		springProcessEngineConfiguration.setMailServerPort(25);
		springProcessEngineConfiguration.setMailServerUseTLS(false);
		springProcessEngineConfiguration.setMailServerUsername("no-reply@avi-oil.com");
	 	
		/*	
		springProcessEngineConfiguration.setDeploymentMode("single-resource");
		springProcessEngineConfiguration.setMailServerHost("mail.silvereye.in");
		springProcessEngineConfiguration.setMailServerPort(587);
		springProcessEngineConfiguration.setMailServerUseTLS(true);
		springProcessEngineConfiguration.setMailServerUsername("nirbhay@silvereye.in");
		springProcessEngineConfiguration.setMailServerPassword("google@2009");*/
		/*
		springProcessEngineConfiguration.setDeploymentMode("single-resource");
        springProcessEngineConfiguration.setMailServerHost("smtp.gmail.com");
        springProcessEngineConfiguration.setMailServerPort(587);
        springProcessEngineConfiguration.setMailServerUseTLS(true);
        springProcessEngineConfiguration.setMailServerUsername("hinduonline2014@gmail.com");
        springProcessEngineConfiguration.setMailServerPassword("2014@honl");*/
		//springProcessEngineConfiguration.setMailServerPassword("google@2009");
		//List<ProcessEngineConfigurator> listConfig=new ArrayList<ProcessEngineConfigurator>();
		//listConfig.add(getConfiguration());
		//springProcessEngineConfiguration.setConfigurators(listConfig);
		return springProcessEngineConfiguration;
	}
	
	@Bean
	public BasicDataSource dataSource() {
		BasicDataSource basicDataSource = new BasicDataSource();
		
		basicDataSource.setDriverClassName("com.mysql.jdbc.Driver");
		basicDataSource.setUrl("jdbc:mysql://localhost:3306/edms?useUnicode=true&characterEncoding=UTF-8");

			
		basicDataSource.setUsername("root");
		basicDataSource.setPassword("");
		

		//basicDataSource.setUsername("root");
		//basicDataSource.setPassword("HPDELL6789##67ibmcisco");
		
		//for aims
		//basicDataSource.setUsername("root");
		//basicDataSource.setPassword("Med##??1975#");
		
		
		// for lls
		// basicDataSource.setUsername("root");
		// basicDataSource.setPassword("IBM773300&&&8591");
		
		// basicDataSource.setUsername("root");
		// basicDataSource.setPassword("hello67##89HRM");

		/* basicDataSource.setUsername("root");
		 basicDataSource.setPassword("hh##8993Avi#00??");*/
		
		// basicDataSource.setUsername("edms");
		// basicDataSource.setPassword("Me8pHCL##??kk7890");
		return basicDataSource;
	}
	
	 @Bean
	 public HibernateTransactionManager transactionManager() {
	      HibernateTransactionManager txManager = new HibernateTransactionManager();
	      txManager.setSessionFactory(sessionFactory().getObject());
	      return txManager;
	   }
	
	/* @Bean
	 @Autowired
	 public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
	      HibernateTransactionManager txManager = new HibernateTransactionManager();
	      txManager.setSessionFactory(sessionFactory);
	      return txManager;
	   }*/
	
	@Bean
	public LocalSessionFactoryBean sessionFactory(){
		LocalSessionFactoryBean localSessionFactoryBean = new LocalSessionFactoryBean();
		localSessionFactoryBean.setDataSource(dataSource());
		Resource configLocation = new ClassPathResource("hibernate.cfg.xml");
		localSessionFactoryBean.setConfigLocation(configLocation);
		localSessionFactoryBean.setHibernateProperties(hibernateProperties());
		return localSessionFactoryBean;
	}

	
	/*@Bean
	public WebdavConfig webdavService() throws Exception{
		WebdavConfig simpleWebdavServlet=new WebdavConfig();
		return simpleWebdavServlet;
	}*/
/*	@Bean
	public JCRWebdavServerServlet webdavJCRService() throws Exception{
		JCRWebdavServerServlet simpleWebdavServlet=new JCRWebdavServerServlet() {
			
			@Override
			protected Repository getRepository() {
				// TODO Auto-generated method stub
				try {
					return JcrUtils.getRepository();
				} catch (RepositoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		};
		DavLocatorFactory davLocatorFact=new DavLocatorFactoryImpl("/jackrabbit/repository");
		simpleWebdavServlet.setLocatorFactory(davLocatorFact);
		return simpleWebdavServlet;
	}*/
	@Bean
	public RepositoryService repositoryService(ProcessEngineFactoryBean pefb) throws Exception{
		return pefb.getObject().getRepositoryService();
	}
 
	@Bean
	public RuntimeService runtimeService(ProcessEngineFactoryBean pefb) throws Exception {
		return pefb.getObject().getRuntimeService();
	}
 
	@Bean
	public HistoryService historyService(ProcessEngineFactoryBean pefb) throws Exception {
		return pefb.getObject().getHistoryService();
	}
 
	@Bean
	public IdentityService identityService(ProcessEngineFactoryBean pefb) throws Exception {
		return pefb.getObject().getIdentityService();
	}
 
	@Bean
	public FormService formService(ProcessEngineFactoryBean pefb) throws Exception {
		return pefb.getObject().getFormService();
	}
 
	@Bean
	public TaskService taskService(ProcessEngineFactoryBean pefb) throws Exception {
		return pefb.getObject().getTaskService();
	}

	@Bean
	public ProcessEngineFactoryBean processEngine(){
		ProcessEngineFactoryBean processEngineFactoryBean = new ProcessEngineFactoryBean();
		processEngineFactoryBean.setProcessEngineConfiguration(processEngineConfiguration());
		return processEngineFactoryBean;
	}	
/*	@Bean
	public ProcessEngineFactoryBean processEngine(){
		ProcessEngineFactoryBean processEngineFactoryBean = new ProcessEngineFactoryBean();
		processEngineFactoryBean.setProcessEngineConfiguration(processEngineConfiguration());
		return processEngineFactoryBean;
	}*/	
	
	@Bean LDAPConfigurator getConfiguration(){
		LDAPConfigurator ldapConfigurator=new LDAPConfigurator();
		ldapConfigurator.setBaseDn("dc=avi-oil,dc=com");
		ldapConfigurator.setServer("ldap://192.168.1.199");
		ldapConfigurator.setPort(389);
		ldapConfigurator.setUser("uid=sanjay,ou=Users,dc=avi-oil,dc=com");
		ldapConfigurator.setPassword("redhat");
		ldapConfigurator.setUserIdAttribute("uid");
		ldapConfigurator.setQueryUserByUserId("(&(objectClass=inetOrgPerson)(uid={0}))");
		return ldapConfigurator;
		
	}
	
	@Bean
	public ServletRegistrationBean dispatcherServlet(ApplicationContext applicationContext) {
		MessageDispatcherServlet servlet = new MessageDispatcherServlet();
		servlet.setApplicationContext(applicationContext);
		servlet.setTransformWsdlLocations(true);
		return new ServletRegistrationBean(servlet, "/ws/*");
	}

	@Bean(name="documentModule")
	public DefaultWsdl11Definition defaultWsdlDefinition(XsdSchema documentModuleSchema){
		DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
		wsdl11Definition.setPortTypeName("DocumentModulePort");
		wsdl11Definition.setLocationUri("/ws");
		wsdl11Definition.setTargetNamespace("http://edms.com/documentModule");
		wsdl11Definition.setSchema(documentModuleSchema);
		return wsdl11Definition;
	}
	@Bean
	public XsdSchema documentModuleSchema() {
		return new SimpleXsdSchema(new ClassPathResource("documentModule.xsd"));
	}
	
	@Bean(name="workflow")
	public DefaultWsdl11Definition defaultWsdlWorkflowDefinition(XsdSchema workflowSchema){
		DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
		wsdl11Definition.setPortTypeName("WorkflowPort");
		wsdl11Definition.setLocationUri("/ws");
		wsdl11Definition.setTargetNamespace("http://edms.com/Workflow");
		wsdl11Definition.setSchema(workflowSchema);
		return wsdl11Definition;
	}
	
	@Bean
	public XsdSchema workflowSchema() {
		return new SimpleXsdSchema(new ClassPathResource("workflow.xsd"));
	}

	@Bean(name="workflowHistory")
	public DefaultWsdl11Definition defaultWsdlWorkflowHistDefinition(XsdSchema workflowHistorySchema){
		DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
		wsdl11Definition.setPortTypeName("WorkflowHistoryPort");
		wsdl11Definition.setLocationUri("/ws");
		wsdl11Definition.setTargetNamespace("http://edms.com/WorkflowHistory");
		wsdl11Definition.setSchema(workflowHistorySchema);
		return wsdl11Definition;
	}
	
	@Bean
	public XsdSchema workflowHistorySchema() {
		return new SimpleXsdSchema(new ClassPathResource("workflowHistory.xsd"));
	}

	@Bean(name="user")
	public DefaultWsdl11Definition defaultWsdlLogin(XsdSchema userSchema){
		DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
		wsdl11Definition.setPortTypeName("UserPort");
		wsdl11Definition.setLocationUri("/ws");
		wsdl11Definition.setTargetNamespace("http://edms.com/user");
		wsdl11Definition.setSchema(userSchema);
		return wsdl11Definition;
	}
	
	@Bean
	public XsdSchema userSchema() {
		return new SimpleXsdSchema(new ClassPathResource("user.xsd"));
	}
	
	
/*	@Bean
	public RepositoryStartupServlet getRepositoryStartupServlet(){
		RepositoryStartupServlet repositoryStartupServlet=new RepositoryStartupServlet();
		ServletConfig servletConfig= new 
		repositoryStartupServlet.init();
	}*/

/*@Bean 
public DefaultSpringSecurityContextSource getContextSource()
{
	DefaultSpringSecurityContextSource contextSource=new DefaultSpringSecurityContextSource("ldap://192.168.1.199:389/dc=avi-oil,dc=com");
	contextSource.setUserDn("cn=admin,dc=avi-oil,dc=com");
	contextSource.setPassword("redhat");
	return contextSource;
}
@Bean 
public LdapAuthenticationProvider getLdapAuthenticationProvider()
{
	LdapAuthenticationProvider authProvider=new LdapAuthenticationProvider(getBindAuthenticator(),getDefaultLdapAuthoritiesPopulator());
	return authProvider;
}
	

@Bean 
public BindAuthenticator getBindAuthenticator()
{
	BindAuthenticator authProvider=new BindAuthenticator(getContextSource());
	String[] pattern="uid={0},ou=users".split(",");
	authProvider.setUserDnPatterns(pattern);
	return authProvider;
}

@Bean 
public DefaultLdapAuthoritiesPopulator getDefaultLdapAuthoritiesPopulator()
{
	DefaultLdapAuthoritiesPopulator authPopulator=new DefaultLdapAuthoritiesPopulator(getContextSource(),"ou=Users");
	authPopulator.setGroupRoleAttribute("ou");
	return authPopulator;
}
*/
	
	
	/*@Bean
	public ExternalIdentityProvider getManager(){
		return new CustomExternalIdentityProviderManager().getProvider("ldap");
	}
	@Bean
	public SyncHandler getSyncHandler(){
		return new ExternalSyncManager().getSyncHandler("ldap");
	}
		
*/
	
}
