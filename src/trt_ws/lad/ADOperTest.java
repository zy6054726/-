package trt_ws.lad;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.security.sasl.AuthenticationException;

public class ADOperTest {
	/**
	 * 从连接池中获取一个连接.
	 *
	 * @return LdapContext
	 * @throws NamingException
	 */
	public LdapContext getConnectionFromFool() throws NamingException {
		String keystore = "F:\\utrust\\jdk1.6.0\\jre\\lib\\security\\cacerts";
		System.setProperty("javax.net.ssl.trustStore", keystore);
		Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, "ldap://192.168.0.190:636");
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, "cn=Administrator,cn=Users,dc=all,dc=com");
		env.put(Context.SECURITY_CREDENTIALS, "123456");
		env.put(Context.SECURITY_PROTOCOL, "ssl");
		env.put("com.sun.jndi.ldap.connect.pool", "true");
		env.put("java.naming.referral", "follow");
		return new InitialLdapContext(env, null);
	}

	/**
	 * 校验用户登录.
	 *
	 * @param userDn
	 *            String
	 * @param password
	 *            String
	 * @return boolean
	 */
	public boolean authenticate(String userDn, String password) {
		LdapContext ctx = null;
		try {
			Control[] connCtls = new Control[] {};
			ctx = getConnectionFromFool();
			ctx.getRequestControls();
			ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, userDn);
			ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
			ctx.reconnect(connCtls);
			return true;
		} catch (AuthenticationException e) {
			return false;
		} catch (NamingException e) {
			return false;
		} finally {
			if (ctx != null) {
				try {
					ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, transientInstance.getAccountName());
					ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, transientInstance.getAccountPwd());
					ctx.reconnect(ctx.getConnectControls());
					ctx.close();
				} catch (NamingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ctx = null;
			}
		}
	}

	/**
	 * 添加用户.
	 *
	 * @param userDN
	 *            String用户DN
	 * @param userName
	 *            String 用户登录名
	 * @param userPwd
	 *            String 用户密码
	 * @return boolean 添加是否成功.
	 *
	 */
	public boolean addUser(String userDN, String userName, String userPwd) {
		LdapContext ctx = null;
		try {
			ctx = getConnectionFromFool();
			// Create attributes to be associated with the new user
			Attributes attrs = new BasicAttributes(true);
			// These are the mandatory attributes for a user object
			// Note that Win2K3 will automagically create a random
			// samAccountName if it is not present. (Win2K does not)
			attrs.put("objectClass", "user");
			attrs.put("sAMAccountName", userName);
			attrs.put("cn", userName);
			// some useful constants from lmaccess.h
			int UF_ACCOUNTDISABLE = 0x0002;
			int UF_PASSWD_NOTREQD = 0x0020;
			int UF_NORMAL_ACCOUNT = 0x0200;
			int UF_PASSWORD_EXPIRED = 0x800000;
			// Note that you need to create the user object before you can
			// set the password. Therefore as the user is created with no
			// password, user AccountControl must be set to the following
			// otherwise the Win2K3 password filter will return error 53
			// unwilling to perform.
			attrs.put("userAccountControl",
					Integer.toString(UF_NORMAL_ACCOUNT + UF_PASSWD_NOTREQD + UF_PASSWORD_EXPIRED + UF_ACCOUNTDISABLE));
			// Create the context
			ctx.createSubcontext(userDN, attrs);
			ModificationItem[] mods = new ModificationItem[2];
			// Replace the "unicdodePwd" attribute with a new value
			// Password must be both Unicode and a quoted string
			String newQuotedPassword = "\"" + userPwd + "\"";
			byte[] newUnicodePassword = newQuotedPassword.getBytes("UTF-16LE");
			mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
					new BasicAttribute("unicodePwd", newUnicodePassword));
			mods[1] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("userAccountControl",
					Integer.toString(UF_NORMAL_ACCOUNT + UF_PASSWORD_EXPIRED)));
			// Perform the update
			ctx.modifyAttributes(userDN, mods);
			mods = null;
			return true;
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ctx = null;
			}
		}
		return false;
	}

	/**
	 * 添加用户.
	 *
	 * @param userDN
	 *            String用户DN
	 * @param attrs
	 *            Attributes 用户属性
	 * @return boolean 添加是否成功.
	 *
	 */
	public boolean addUser(String userDN, Attributes attrs) {
		LdapContext ctx = null;
		try {
			ctx = getConnectionFromFool();
			String userName = (String) attrs.get("cn").get();
			if (userName == null || "".equals(userName)) {
				return false;
			}
			// Replace the "unicdodePwd" attribute with a new value
			// Password must be both Unicode and a quoted string
			if (attrs.get("objectClass") == null || attrs.get("objectClass").get() == null) {
				attrs.put("objectClass", "user");
			}
			if (attrs.get("sAMAccountName") == null || attrs.get("sAMAccountName").get() == null) {
				attrs.put("sAMAccountName", userName);
			}
			if (attrs.get("userAccountControl") == null || attrs.get("userAccountControl").get() == null) {
				int UF_ACCOUNTDISABLE = 0x0002;
				int UF_PASSWD_NOTREQD = 0x0020;
				int UF_NORMAL_ACCOUNT = 0x0200;
				int UF_PASSWORD_EXPIRED = 0x800000;
				attrs.put("userAccountControl", Integer
						.toString(UF_NORMAL_ACCOUNT + UF_PASSWD_NOTREQD + UF_PASSWORD_EXPIRED + UF_ACCOUNTDISABLE));
			}
			String userPwd = (String) attrs.get("unicodePwd").get();
			attrs.remove(pwd_index);
			// Create the context
			ctx.createSubcontext(userDN, attrs);
			// 添加用户密码
			if (userPwd != null) {
				int UF_NORMAL_ACCOUNT = 0x0200;
				int UF_PASSWORD_EXPIRED = 0x800000;
				ModificationItem[] mods = new ModificationItem[2];
				String newQuotedPassword = "\"" + userPwd + "\"";
				byte[] newUnicodePassword = newQuotedPassword.getBytes("UTF-16LE");
				mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
						new BasicAttribute(pwd_index, newUnicodePassword));
				mods[1] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("userAccountControl",
						Integer.toString(UF_NORMAL_ACCOUNT + UF_PASSWORD_EXPIRED)));
				// Perform the update
				ctx.modifyAttributes(userDN, mods);
			}
			return true;
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ctx = null;
			}
		}
		return false;
	}

	/**
	 * 修改用户信息.
	 *
	 * @param attrs
	 *            Attributes 需要修改的用户属性.
	 * @param userDN
	 *            String 用户DN
	 * @return
	 */
	public boolean modify(Attributes attrs, String userDN) {
		LdapContext ctx = null;
		try {
			ctx = getConnectionFromFool();
			attrs.remove(key_index);
			ctx.modifyAttributes(userDN, DirContext.REPLACE_ATTRIBUTE, attrs);
			return true;
		} catch (NamingException e) {
			System.err.println("Problem changing password: " + e);
		} catch (Exception e) {
			System.err.println("Problem: " + e);
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ctx = null;
			}
		}
		return false;
	}

	/**
	 * 删除用户.
	 *
	 * @param userDN
	 *            String 用户DN
	 * @return
	 */
	public boolean del(String userDN) {
		LdapContext ctx = null;
		try {
			ctx = getConnectionFromFool();
			ctx.destroySubcontext(userDN);
			return true;
		} catch (NamingException e) {
			System.err.println("Problem changing password: " + e);
		} catch (Exception e) {
			System.err.println("Problem: " + e);
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ctx = null;
			}
		}
		return false;
	}
}