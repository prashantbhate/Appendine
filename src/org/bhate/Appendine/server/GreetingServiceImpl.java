package org.bhate.Appendine.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.bhate.Appendine.client.GreetingService;
import org.bhate.Appendine.shared.FieldVerifier;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {

	@SuppressWarnings("unchecked")
	public List<String> getInputs() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		String query = "select from " + Input.class.getName()
				+ " order by date desc";
		List<Input> inputs = (List<Input>) pm.newQuery(query).execute();
		inputs = (List<Input>) pm.newQuery(query).execute();
		Set<String> result = new LinkedHashSet<String>(inputs.size());
		for (Input input : inputs) {
			result.add(input.getInput());
		}
		return new ArrayList<String>(result);
	}

	public void storeSuffix(String fix) {
		if (!FieldVerifier.isValidSuffix(fix)) {
			throw new IllegalArgumentException("not a valid suffix");
		}
		fix = escapeHtml(fix);
		PersistenceManager pm = PMF.get().getPersistenceManager();
		String query = "select from " + Suffix.class.getName()
				+ " where fix==:suffix";
		Query newQuery = pm.newQuery(query );
		List result = (List) newQuery.execute(fix);
		if (result.size() > 0) {
			throw new RuntimeException("suffix already exists...");
		}
		pm.makePersistent(new Suffix(fix));
	}

	public List<String> greetServer(String input)
			throws IllegalArgumentException {
		input = validate(input);
		String source = getThreadLocalRequest().getRemoteAddr() + ":"
				+ getThreadLocalRequest().getRemoteAddr();
		storeInput(input, source);

		List<String> words = new ArrayList<String>();
		List<Suffix> appendents = getSuffixList();
		for (Suffix appendent : appendents) {
			String s = intelicat(input, appendent.getFix());
			if (appendent.isSpecial()) {
				s = "<b>" + s + "</b>";
			}
			words.add(s);
		}
		return words;
	}

	private String validate(String input) {
		// Verify that the input is valid.
		if (!FieldVerifier.isValidName(input)) {
			// If the input is not valid, throw an IllegalArgumentException back
			// to
			// the client.
			throw new IllegalArgumentException(
					"need a valid name with least four characters in it [ Hint: name is (a-zA-Z) but not too big too ]");
		}

		// Escape data from the client to avoid cross-site script
		// vulnerabilities.
		input = escapeHtml(input.trim());
		return input;
	}

	private void storeInput(String input, String source) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		pm.makePersistent(new Input(input, source));
	}

	private void addPrefixes() {
		String[] appendents = { "able", "ac", "acious", "act", "ad", "ade",
				"age", "agogy", "akshi", "al", "ality", "an", "ance", "ancy",
				"anna", "ant", "appa", "ar", "arch", "archy", "ard", "ary",
				"ate", "athlon", "ation", "ative", "atory", "avva", "bound",
				"cide", "city", "colony", "cy", "cycle", "dom", "ect",
				"ectomy", "ed", "ee", "eer", "eme", "en", "ence", "ency",
				"endra", "ent", "eo", "eous", "er", "ergy", "ern", "ery",
				"escent", "ese", "esh", "eshwar", "eshwari", "esque", "ess",
				"est", "etic", "fare", "fic", "ful", "fullessness", "fulness",
				"fy", "gon", "gry", "halli", "holic", "hood", "ia", "iable",
				"ial", "ian", "iant", "iate", "ible", "ibly", "ic", "ical",
				"icious", "ics", "id", "ier", "iferous", "ify", "il", "ile",
				"illion", "ing", "ion", "ious", "isation", "ise", "ish", "ism",
				"ist", "istic", "ite", "ition", "itive", "itude", "ity", "ium",
				"ive", "ization", "ize", "kaant", "kaanti", "kumar", "kumari",
				"land", "less", "lessness", "like", "ling", "ly", "man",
				"ment", "meter", "metry", "mony", "most", "naath", "nagar",
				"nesia", "ness", "nomy", "ocracy", "ography", "oid", "oji",
				"ologist", "ology", "onomy", "ophobia", "or", "ory", "ose",
				"osis", "our", "ous", "palya", "phone", "raj", "raju", "rian",
				"s", "sandra", "scope", "sh", "ship", "shire", "sion", "some",
				"ster", "t", "th", "tion", "tude", "ty", "uary", "ulent",
				"ward", "wise", "wright", "y",

		};
		LinkedHashSet<String> h = new LinkedHashSet<String>();
		Collections.addAll(h, appendents);
		for (String appendent : h) {
			// pm.makePersistent(new Suffix(appendent));
		}
	}

	@SuppressWarnings("unchecked")
	private List<Suffix> getSuffixList() {

		PersistenceManager pm = PMF.get().getPersistenceManager();
		String query = "select from " + Suffix.class.getName()
				+ " order by fix desc";
		List<Suffix> suffixes = (List<Suffix>) pm.newQuery(query).execute();
		suffixes = (List<Suffix>) pm.newQuery(query).execute();
		return suffixes;
	}

	public String intelicat(String s1, String s2) {
		String result = s1;
		int s1L = s1.length();
		int s2L = s2.length();
		int a = s1L > s2L ? s1L - s2L : 0;
		int n = Math.min(s1L, s2L);
		while (n > 0) {
			int i = a;
			while (i < s1L && i < n + a && s1.charAt(i) == s2.charAt(i - a)) {
				i++;
			}
			if (i == s1L)
				break;
			a++;
			n--;
		}

		result += s2.substring(n);
		return result;
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html
	 *            the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}
}
