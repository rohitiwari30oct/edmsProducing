package hello;

import java.util.Properties;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerProperties;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;
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
		Resource[] resources = new Resource[2];
		resources[0]=new ClassPathResource("/com/edms/activitiautodeployment/autodeploy.LeaveRequest.bpmn20.xml");
		resources[1]=new ClassPathResource("/com/edms/activitiautodeployment/autodeploy.testProcess.bpmn20.xml");
		springProcessEngineConfiguration.setDeploymentResources(resources);
		springProcessEngineConfiguration.setDeploymentMode("single-resource");
		springProcessEngineConfiguration.setMailServerHost("smtp.gmail.com");
		springProcessEngineConfiguration.setMailServerPort(587);
		springProcessEngineConfiguration.setMailServerUseTLS(true);
		springProcessEngineConfiguration.setMailServerUsername("hinduonline2014@gmail.com");
		springProcessEngineConfiguration.setMailServerPassword("2014@honl");
		return springProcessEngineConfiguration;
	}
	
	@Bean
	public BasicDataSource dataSource(){
		BasicDataSource basicDataSource = new BasicDataSource();
		basicDataSource.setDriverClassName("com.mysql.jdbc.Driver");
		basicDataSource.setUrl("jdbc:mysql://localhost:3306/edms?useUnicode=true&characterEncoding=UTF-8");
		basicDataSource.setUsername("root");
		basicDataSource.setPassword("hh##8993Avi#00??");
		//basicDataSource.setUsername("edms");
		//basicDataSource.setPassword("Me8pHCL##??kk7890");
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
	
}
