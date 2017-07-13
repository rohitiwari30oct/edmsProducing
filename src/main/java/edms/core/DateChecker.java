package edms.core;

import java.util.Calendar;
import java.util.Date;


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
	{return null;}
}
