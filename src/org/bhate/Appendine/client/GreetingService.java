package org.bhate.Appendine.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
	List<String> greetServer(String name) throws IllegalArgumentException;

	List<String> getInputs();
	
	public void storeSuffix(String fix);
	
}
