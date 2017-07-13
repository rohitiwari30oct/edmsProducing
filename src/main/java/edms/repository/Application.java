package edms.repository;


import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.ServletListenerRegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.DispatcherServlet;
//import org.springframework.ldap.core.ContextSource;
//import org.springframework.security.ldap.DefaultSpringSecurityContextSource;

@ComponentScan
@EnableAutoConfiguration
public class Application extends SpringBootServletInitializer {

	/*@Override
	    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
	        return application.sources(Application.class);
	    }
	
	
 	@Bean
    public WebdavConfig dispatcherServlet() {
        return new WebdavConfig();
    }
*/
	
	/*@Bean
	public ServletListenerRegistrationBean<EventListener> registerListener() {
		DerbyShutdown registerListener = new DerbyShutdown();
		ServletListenerRegistrationBean<EventListener> list=new ServletListenerRegistrationBean<EventListener>();
		list.setListener(registerListener);
	    return list;
	}
    @Bean
    public ServletRegistrationBean dispatcherServletRegistration2() {
    	RepositoryStartupServlet repStartup=new RepositoryStartupServlet();
        ServletRegistrationBean registration = new ServletRegistrationBean(repStartup);
        Map<String,String> params = new HashMap<String,String>();
        params.put("bootstrap-config", "jackrabbit/bootstrap.properties");
        registration.setInitParameters(params);
        registration.setLoadOnStartup(2);
        return registration;
    }
    @Bean
    public ServletRegistrationBean dispatcherServletRegistration1() {
    	RepositoryAccessServlet repStartup=new RepositoryAccessServlet();
        ServletRegistrationBean registration = new ServletRegistrationBean(repStartup);
        Map<String,String> params = new HashMap<String,String>();
        params.put("bootstrap-config", "jackrabbit/bootstrap.properties");
        registration.setInitParameters(params);
        registration.setLoadOnStartup(3);
        return registration;
    }
    

    @Bean
    public ServletRegistrationBean dispatcherServletRegistration() {
    	SimpleWebdavServlet simpleWebdavServlet=new SimpleWebdavServlet();
        ServletRegistrationBean registration = new ServletRegistrationBean(simpleWebdavServlet);
        registration.addUrlMappings("/repository/*");
        Map<String,String> params = new HashMap<String,String>();
        params.put("resource-path-prefix", "/repository");
        params.put("resource-config", "/WEB-INF/config.xml");
        registration.setInitParameters(params);
        registration.setLoadOnStartup(4);
        return registration;
    }
    @Bean
    public ServletRegistrationBean dispatcherServletRegistration3() {
    	JcrRemotingServlet simpleWebdavServlet=new JcrRemotingServlet();
        ServletRegistrationBean registration = new ServletRegistrationBean(simpleWebdavServlet);
        registration.addUrlMappings("/server/*");
        Map<String,String> params = new HashMap<String,String>();
        params.put("protectedhandlers-config", "/WEB-INF/protectedHandlers.properties");
        params.put("batchread-config", "/WEB-INF/batchread.properties");
        params.put("missing-auth-mapping", "");
        registration.setInitParameters(params);
        registration.setLoadOnStartup(5);
        return registration;
    }
    @Bean
    public ServletRegistrationBean dispatcherServletRegistration4() {
    	RemoteBindingServlet simpleWebdavServlet=new RemoteBindingServlet();
        ServletRegistrationBean registration = new ServletRegistrationBean(simpleWebdavServlet);
        registration.addUrlMappings("/rmi");
        return registration;
    }*/
	/*
	 @Override
	    public void onStartup(ServletContext servletContext) throws ServletException {
	        
	        servletContext.addServlet("dispatchServlet", DispatcherServlet.class.getName());
	        
	        super.onStartup(servletContext);
	    }
	*/
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		
	}
}
