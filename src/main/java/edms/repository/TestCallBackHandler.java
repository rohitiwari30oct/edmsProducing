package edms.repository;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

public class TestCallBackHandler implements CallbackHandler{
	public String name;
	public String password;
	
	public TestCallBackHandler(String name,String password){
		this.name=name;
		this.password=password;
	}

	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof NameCallback) {
                    NameCallback nameCallback = (NameCallback) callbacks[i];
                    nameCallback.setName(name);
            } else if (callbacks[i] instanceof PasswordCallback) {
                    PasswordCallback passwordCallback = (PasswordCallback) callbacks[i];
                    passwordCallback.setPassword(password.toCharArray());
            } else {
                    throw new UnsupportedCallbackException(callbacks[i], "The submitted Callback is unsupported");
            }
    }
		
	}
	
	
}
