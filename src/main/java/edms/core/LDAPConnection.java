package edms.core;

import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.edms.entity.SnTable;

public class LDAPConnection {
	public static  DirContext getConnection(String url,String username,String password)
	{
		    DirContext ctx=null;
		    try
		    {
			Hashtable env = new Hashtable();
			env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			env.put(Context.PROVIDER_URL, url); // LDAP host and base
			env.put("java.naming.ldap.attributes.binary", "jpegPhoto");
			// LDAP authentication options
			env.put(Context.SECURITY_AUTHENTICATION, "simple");
			env.put(Context.SECURITY_PRINCIPAL, username);
			env.put(Context.SECURITY_CREDENTIALS, password);
			ctx = new InitialDirContext(env);
		    }
		    catch(Exception e)
		    {
		    	System.out.print(e.toString());
		    	e.printStackTrace();
		    }
		    return ctx;
	}
	
	public static void closeConn(DirContext ctx)
	{
		try
	    {
	   if(ctx!=null)
	   {
	     ctx.close();
	   }
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    	//System.out.print(e.toString());
	    	
	    }
	}
	
	public static double readAttributeFromLdap(String ldapurl, String  username, String password, String base){
		try {
			DirContext ctx=LDAPConnection.getConnection(ldapurl, username , password);

			//String entry="mail="+username+","+base;
				Attribute	qLimit = ctx.getAttributes(username).get("mailQuota");
				
			 System.out.println(qLimit.get());
			ctx.close();
			return Double.parseDouble(qLimit.get().toString());
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	
	
}
