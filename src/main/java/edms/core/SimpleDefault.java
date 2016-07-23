/*package edms.core;

import java.security.Principal;
import java.util.Set;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.security.AccessControlException;
import javax.jcr.security.AccessControlPolicy;
import javax.jcr.security.Privilege;

import org.apache.jackrabbit.api.security.authorization.PrivilegeManager;
import org.apache.jackrabbit.core.id.ItemId;
import org.apache.jackrabbit.core.security.AMContext;
import org.apache.jackrabbit.core.security.AbstractAccessControlManager;
import org.apache.jackrabbit.core.security.AccessManager;
import org.apache.jackrabbit.core.security.authorization.AccessControlProvider;
import org.apache.jackrabbit.core.security.authorization.WorkspaceAccessManager;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;

public class SimpleDefault extends AbstractAccessControlManager
implements AccessManager {

	@Override
	public AccessControlPolicy[] getEffectivePolicies(Set<Principal> principals)
			throws AccessDeniedException, AccessControlException,
			UnsupportedRepositoryOperationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasPrivileges(String absPath, Set<Principal> principals,
			Privilege[] privileges) throws PathNotFoundException,
			AccessDeniedException, RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Privilege[] getPrivileges(String absPath, Set<Principal> principals)
			throws PathNotFoundException, AccessDeniedException,
			RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasPrivileges(String absPath, Privilege[] privileges)
			throws PathNotFoundException, RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Privilege[] getPrivileges(String absPath)
			throws PathNotFoundException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccessControlPolicy[] getEffectivePolicies(String absPath)
			throws PathNotFoundException, AccessDeniedException,
			RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(AMContext context) throws AccessDeniedException, Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(AMContext context, AccessControlProvider acProvider,
			WorkspaceAccessManager wspAccessMgr) throws AccessDeniedException,
			Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void checkPermission(ItemId id, int permissions)
			throws AccessDeniedException, ItemNotFoundException,
			RepositoryException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void checkPermission(Path absPath, int permissions)
			throws AccessDeniedException, RepositoryException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void checkRepositoryPermission(int permissions)
			throws AccessDeniedException, RepositoryException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isGranted(ItemId id, int permissions)
			throws ItemNotFoundException, RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isGranted(Path absPath, int permissions)
			throws RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isGranted(Path parentPath, Name childName, int permissions)
			throws RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canRead(Path itemPath, ItemId itemId)
			throws RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canAccess(String workspaceName) throws RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void checkInitialized() throws IllegalStateException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void checkPermission(String absPath, int permission)
			throws AccessDeniedException, PathNotFoundException,
			RepositoryException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected PrivilegeManager getPrivilegeManager() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void checkValidNodePath(String absPath)
			throws PathNotFoundException, RepositoryException {
		// TODO Auto-generated method stub
		
	}

}
*/