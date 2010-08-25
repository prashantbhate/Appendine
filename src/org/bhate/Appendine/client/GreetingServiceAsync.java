package org.bhate.Appendine.client;

import java.util.List;

import org.bhate.Appendine.server.Input;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	void greetServer(String input, AsyncCallback<List<String>> callback)
			throws IllegalArgumentException;

	void getInputs(AsyncCallback<List<String>> callback);

	void storeSuffix(String fix, AsyncCallback<Void> callback);
}
