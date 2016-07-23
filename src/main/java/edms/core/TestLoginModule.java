package edms.core;

import java.io.IOException;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

public class TestLoginModule implements LoginModule {

	CallbackHandler callbackHandler=null;
	
	public TestLoginModule(String username,String password){
		callbackHandler=new CallbackHandler() {
			
			@Override
			public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
				
				
			}
		};
	}
	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
			Map<String, ?> options) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean login() throws LoginException {
		// TODO Auto-generated method stub
		 boolean returnValue = true;
			if (callbackHandler == null){
		        throw new LoginException("No callback handler supplied.");
		    }

		    Callback[] callbacks = new Callback[2];
		    callbacks[0] = new NameCallback("Username");
		    callbacks[1] = new PasswordCallback("Password", false);

		    try {
		        callbackHandler.handle(callbacks);
		        String userName = ((NameCallback) callbacks[0]).getName();
		        char [] passwordCharArray = ((PasswordCallback) callbacks[1]).getPassword();
		        String password = new String(passwordCharArray);
		        //--> authenticate if username is the same as password (yes, this is a somewhat simplistic approach :-)
		        returnValue = userName.equals(password);
		    } catch (IOException ioe)  {
		        ioe.printStackTrace();
		        throw new LoginException("IOException occured: "+ioe.getMessage());
		    } catch (UnsupportedCallbackException ucbe) {
		        ucbe.printStackTrace();
		        throw new LoginException("UnsupportedCallbackException encountered: "+ucbe.getMessage());
		    }

		    System.out.println("logged in");
		    return returnValue;
		
	}

	@Override
	public boolean commit() throws LoginException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean abort() throws LoginException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean logout() throws LoginException {
		// TODO Auto-generated method stub
		return false;
	}

}
