package trt_ws.lad;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

public class LdAPTest {
	public static void main(String[] args) {
		Properties env = new Properties();
		String adminName = "cn=Administrator,cn=Users,DC=sfty,DC=com";// username@domain
		String adminPassword = "Trtjk123";// password
		String ldapURL = "ldap://10.8.154.10:389";// ip:port
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.SECURITY_AUTHENTICATION, "simple");// "none","simple","strong"
		env.put(Context.SECURITY_PRINCIPAL, adminName);
		env.put(Context.SECURITY_CREDENTIALS, adminPassword);
		env.put(Context.PROVIDER_URL, ldapURL);
		try {
			LdapContext ctx = new InitialLdapContext(env, null);
			SearchControls searchCtls = new SearchControls();
			searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String searchFilter = "(&(objectCategory=person)(objectClass=user)(name=*))";
			String searchBase = "ou=IT共享中心,DC=sfty,DC=com";
//			String searchBase = "DC=sfty,DC=com";
//			 String returnedAtts[] = {"memberOf"};
			String returnedAtts[] = { "msExchRBACPolicyLink" };
			searchCtls.setReturningAttributes(returnedAtts);
			NamingEnumeration<SearchResult> answer = ctx.search(searchBase, searchFilter, searchCtls);
			int i = 0;
			while (answer.hasMoreElements()) {
				SearchResult sr = (SearchResult) answer.next();
//				System.out.println("第" + (i + 1) + "条：" + sr.getAttributes());
//				System.out.println("第" + (i + 1) + "条：" + sr.getName());
				System.out.println("第" + (i + 1) + "条：" + sr.getNameInNamespace());
				i++;
			}
			ctx.close();
		} catch (NamingException e) {
			e.printStackTrace();
			System.err.println("Problem searching directory: " + e);
		}
	}
}