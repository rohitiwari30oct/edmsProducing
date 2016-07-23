package edms.core;

import java.util.UUID;

import javax.jcr.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SessionWrapper {
	Session aSession;
	private String sessionId;
	private static Log logger = LogFactory.getLog(SessionWrapper.class);
	protected SessionWrapper(Session session){
		this.aSession = session;
		sessionId = UUID.randomUUID().toString();
	}
	public Session getSession(){
		return aSession;
	}
	public String getId(){
		return sessionId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((sessionId == null) ? 0 : sessionId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SessionWrapper other = (SessionWrapper) obj;
		if (sessionId == null) {
			if (other.sessionId != null)
				return false;
		} else if (!sessionId.equals(other.sessionId))
			return false;
		return true;
	}
	public void logout() {
		aSession.logout();
	}
}
