/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2014 Paco Avila & Josep Llort
 * 
 * No bytes were intentionally harmed during the development of this application.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package edms.core;

import javax.jcr.Repository;

public class Config {

	public static String JCR_USERNAME="admin";
	public static String EDMS_ADMIN="admin";
	public static String JCR_PASSWORD="admin";
	public static String JCR_SYSTEM="jcr:system";
	public static final String EDMS_Sorting_Parameter = "edms:name";
	public static final String EDMS_FOLDER = "edms:folder";
	public static final String EDMS_NAME = "edms:name";
	public static final String EDMS_TITLE = "edms:title";
	public static final String EDMS_SIZE = "edms:size";
	public static final String EDMS_DOCUMENT = "edms:document";
	public static final String USERS_READ = "edms:authUsersRead";
	public static final String USERS_WRITE = "edms:authUsersWrite";
	public static final String USERS_DELETE = "edms:authUsersDelete";
	public static final String USERS_SECURITY = "edms:authUsersSecurity";
	public static final String GROUPS_READ = "edms:authRolesRead";
	public static final String GROUPS_WRITE = "edms:authRolesWrite";
	public static final String GROUPS_DELETE = "edms:authRolesDelete";
	public static final String GROUPS_SECURITY = "edms:authRolesSecurity";
	public static final String EDMS_KEYWORDS = "edms:keywords";
	public static final String EDMS_AUTHOR = "edms:author";
	public static final String EDMS_DESCRIPTION = "edms:description";
	public static final String EDMS_CREATIONDATE = "edms:created";
	public static final String EDMS_MODIFICATIONDATE = "edms:modified";
	public static final String EDMS_NO_OF_FOLDERS = "edms:no_of_folders";
	public static final String EDMS_NO_OF_DOCUMENTS = "edms:no_of_documents";
	public static final String EDMS_RECYCLE_DOC = "edms:recycle";
	public static final String EDMS_CONTENT = "edms:content";
	public static final String EDMS_RESTORATION_PATH = "edms:restorationPath";
	public static final String EDMS_OWNER = "edms:owner";
	public static final String EDMS_DOWNLOADDATE = "edms:downloadDate";
	public static final String EDMS_ACCESSDATE = "edms:accessDate";
	public static String EDMS_SORT_ORDER = "edms:created";
	//public static String EDMS_ADMINISTRATOR="sanjay@avi-oil.com";
	public static String EDMS_ADMINISTRATOR="";
	public static String EDMS_PASSWORD="google@2009";
	public static final String DEFAULT_USER_ROLE = "ROLE_USER";
	public static final Object DEFAULT_ADMIN_ROLE = "ROLE_ADMIN";
	public static final String EDMS_BULKUPLOAD_PATH = "/maildir/newAccount";
	public static Repository repository=null;
	//public static final String EDMS_BULKUPLOAD_PATH = "D:\\dghdg";
	public static final String EDMS_PATH = "edms:path";
	public static final String EDMS_GUEST = "guestforpubliclink";
	//public static  String EDMS_DOMAIN = "avi-oil.com";
	public static  String EDMS_DOMAIN = "silvereye.in";

	public static String LDAP_URL="ldap://mail.silvereye.in:389";
	public static String LDAP_DN="dc=silvereye,dc=in";
	public static String LDAP_RDN="ou=Users,domainName=silvereye.in,o=domains,dc=silvereye,dc=in";
	public static String LDAP_BASE="mail";
	public static void load(String userid,String password){
		JCR_USERNAME=userid;
		JCR_PASSWORD=password;
		JCR_SYSTEM="jcr:system";
	}
	
	
	
}
