package edms.core;

import java.util.Calendar;
import java.util.Date;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public class DateChecker {
	
	public static boolean checkDateD() {
		Date date=new Date();
		//System.out.println(date.toString());
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.set(2015, 11, 04);
		cal1.add(Calendar.MONTH, 6);
		System.out.println(cal1.getTime());
		System.out.println(cal2.getTime());
		if(cal2.getTime().after(cal1.getTime())){
			System.out.println("Yes");
		}else{
			System.out.println("No");
		}
		return true;
	}

	public static String listingFolder(String folderPath,String userid,String password )
	{

		System.out.println("DateTime: "+new Date()+" ::::: "+"Permanent delete folder : "+ folderPath+" by user : "+userid);
		SessionWrapper sessions =JcrRepositoryUtils.login(userid, password);
		Session jcrsession = sessions.getSession();
		//String sessionId=sessions.getId();
		String response="";
		try {
			Node root = jcrsession.getRootNode();
			root=root.getNode(userid+"/Contacts");

			/*Version version=jcrsession.getWorkspace().getVersionManager().checkin(root.getParent().getPath());
			System.out.println("DateTime: "+new Date()+" ::::: "+"Version of :"+root.getParent().getPath()+" has been created, Version name is : "+version.getName());
			jcrsession.getWorkspace().getVersionManager().checkout(root.getParent().getPath());
			*/
			root.remove();
			response= "success";
		} catch (RepositoryException e) {
			response= "Exception Occured";
		}finally{
			//JcrRepositoryUtils.logout(sessionId);
		}
		return response;
	}
}
