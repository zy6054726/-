package trt_ws.lad;

import java.io.IOException;
import java.util.Hashtable;
import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.StartTlsRequest;
import javax.naming.ldap.StartTlsResponse;

public class ADOpersion {
	private LdapContext ctx = null;

	private String baseName = ",OU=IT共享中心,DC=sfty,DC=com";

	public ADOpersion() {
		try {
			Hashtable<String, String> ldapEnv = new Hashtable<String, String>();
			ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY,
					"com.sun.jndi.ldap.LdapCtxFactory");
			ldapEnv.put(Context.PROVIDER_URL, "ldap://10.8.154.10:389");
			ldapEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
			ldapEnv.put(Context.SECURITY_PRINCIPAL,
					"cn=Administrator,cn=Users,DC=sfty,DC=com");
			ldapEnv.put(Context.SECURITY_CREDENTIALS,"Trtjk123"); // 密码
			// ldapEnv.put(Context.SECURITY_PROTOCOL, "ssl");
			ctx = new InitialLdapContext(ldapEnv, null);
		} catch (Exception e) {
			System.out.println(" bind error: " + e);
			e.printStackTrace();
		}
	}

	/**
	 * 创建一个新的用户
	 * 
	 * @param username
	 * @param surname
	 * @param givenName
	 */
	public void createNew(String userName) {
		try {

			String entryDN = "CN=OIMGroup,CN=Users,DC=ssodev,DC=com";
			// Create attributes to be associated with the new user
			String dn="uid="+userName +baseName; 
			 
			BasicAttributes attrs = new BasicAttributes();
			BasicAttribute objectclass = new BasicAttribute("objectClass");
			objectclass.add("top");
			objectclass.add("person");
			// objectclass.add("organizationalPerson");
			// objectclass.add("inetOrgPerson");
			attrs.put(objectclass);
			attrs.put("cn", "Barbara Jensen");
			attrs.put("sn", "Jensen");
			attrs.put("givenName", "Barbara");
			// attrs.put("title", "manager, product development");
			attrs.put("uid", "bjensen");
			attrs.put("mail", "zhangsan@abc.com");

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

			attrs.put(
					"userAccountControl",
					Integer.toString(UF_NORMAL_ACCOUNT + UF_PASSWD_NOTREQD
							+ UF_PASSWORD_EXPIRED + UF_ACCOUNTDISABLE));

			// Create the context
			ctx.createSubcontext(dn, attrs);
			System.out.println("Created disabled account for: " + userName);

			// now that we've created the user object, we can set the
			// password and change the userAccountControl
			// and because password can only be set using SSL/TLS
			// lets use StartTLS

			StartTlsResponse tls = (StartTlsResponse)ctx.extendedOperation(new StartTlsRequest());
			tls.negotiate();

			// set password is a ldap modfy operation
			// and we'll update the userAccountControl
			// enabling the acount and force the user to update ther password
			// the first time they login
			ModificationItem[] mods = new ModificationItem[2];

			// Replace the "unicdodePwd" attribute with a new value
			// Password must be both Unicode and a quoted string
			String newQuotedPassword = "\"Password2000\"";
			byte[] newUnicodePassword = newQuotedPassword.getBytes("UTF-16LE");

			mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
					new BasicAttribute("unicodePwd", newUnicodePassword));
			mods[1] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
					new BasicAttribute("userAccountControl",
							Integer.toString(UF_NORMAL_ACCOUNT
									+ UF_PASSWORD_EXPIRED)));

			// Perform the update
			ctx.modifyAttributes(userName, mods);
			System.out.println("Set password & updated userccountControl");

			// now add the user to a group.

			try {
				ModificationItem member[] = new ModificationItem[1];
				member[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE,
						new BasicAttribute("member", userName));

				ctx.modifyAttributes(entryDN, member);
				System.out.println("Added user to group: " + entryDN);
			} catch (NamingException e) {
				System.err.println("Problem adding user to group: " + e);
			}
			// Could have put tls.close() prior to the group modification
			// but it seems to screw up the connection or context ?
			tls.close();
			ctx.close();
		} catch (NamingException e) {
			System.err.println("Problem creating object: " + e);
		} catch (IOException e) {
			System.err.println("Problem creating object: " + e);
		}
	}
	
	public void addUser() throws IOException {
		String entryDN = "CN=OIMGroup,CN=Users,DC=ssodev,DC=com";
	    String parentDN = ",CN=Users,DC=ssodev,DC=com";
	    String childName = "xiaolizi";
	    String userName = "CN=" + childName + parentDN;
	    Attribute cn = new BasicAttribute("cn", childName);
	    Attribute oc = new BasicAttribute("objectclass");
	    oc.add("top");
	    oc.add("person");
	    try {
	        Attributes entry = new BasicAttributes(true);
	        entry.put(cn);
	        entry.put(oc);
	        ctx.createSubcontext(userName, entry);
	        System.out.println("Add User: added entry" + entry +".");

	        /*
			ModificationItem[] mods = new ModificationItem[1];
			String newQuotedPassword = "\"Password2000\"";
			byte[] newUnicodePassword = newQuotedPassword.getBytes("UTF-16LE");

			mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
					new BasicAttribute("unicodePwd", newUnicodePassword));
			ctx.modifyAttributes(userName, mods);
			*/
			
			
			ModificationItem member[] = new ModificationItem[1];
			member[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE,
					new BasicAttribute("member", userName));
			ctx.modifyAttributes(entryDN, member);
	    } catch (NamingException e) {
	        System.err.println("Add User: error add entry" + e);
	    }
	}

	/**
	 * 更新用户
	 * 
	 * @param username
	 */
	public void update(String username) {
		try {
			System.out.println("updating...\n");
			ModificationItem[] mods = new ModificationItem[7];
			mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
					new BasicAttribute("msRTCSIP-PrimaryHomeServer", "CN=Lc Services,CN=Microsoft,CN=1:1,CN=Pools,CN=RTC Service,CN=Services,CN=Configuration,DC=SFTY,DC=COM"));
			mods[1] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
					new BasicAttribute("msRTCSIP-PrimaryUserAddress", "sip:TRTJK_02@sfty.COM"));
			mods[2] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
					new BasicAttribute("msRTCSIP-UserEnabled", "TRUE"));
			mods[3] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
					new BasicAttribute("msRTCSIP-OptionFlags", "257"));
			mods[4] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
					new BasicAttribute("msRTCSIP-UserPolicies", "0=1806201608;0=1806201607"));
			mods[5] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
					new BasicAttribute("description", "hello"));
			mods[6] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
					new BasicAttribute("msRTCSIP-DeploymentLocator", "SRV:"));	

			ctx.modifyAttributes("cn=" + username + baseName, mods);
		} catch (Exception e) {
			System.out.println(" update error: " + e);
			System.exit(-1);
		}
	}
	
	
	/**
	 * 更新用户属性
	 * 
	 * @param username
	 */
	public void updateAttr(String username) {
		try {
			System.out.println("updating...\n");
			ModificationItem[] mods = new ModificationItem[3];
			//待更新属性列表
			mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
					new BasicAttribute("msRTCSIP-UserEnabled", "TRUE"));
			mods[1] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
					new BasicAttribute("msRTCSIP-OptionFlags", "2305"));
			mods[2] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
					new BasicAttribute("msRTCSIP-UserPolicies", "0=1714706691"));
			
			
			//更新用户属性信息
			ctx.modifyAttributes("cn=" + username + baseName, mods);
		} catch (Exception e) {
			System.out.println(" update error: " + e);
			System.exit(-1);
		}
	}

	/**
	 * 修改密码
	 * 
	 * @param username
	 * @param password
	 */
	public void updatePassword(String username, String password) {
		try {
			System.out.println("updating password...\n");
			String quotedPassword = "\"" + password + "\"";
			/*
			char unicodePwd[] = quotedPassword.toCharArray();
			byte pwdArray[] = new byte[unicodePwd.length * 2];
			for (int i = 0; i < unicodePwd.length; i++) {
				pwdArray[i * 2 + 1] = (byte) (unicodePwd[i] >>> 8);
				pwdArray[i * 2 + 0] = (byte) (unicodePwd[i] & 0xff);
			}
			System.out.print("encoded password: ");
			for (int i = 0; i < pwdArray.length; i++) {
				System.out.print(pwdArray[i] + " ");
			}
			*/
			byte[] newUnicodePassword = quotedPassword.getBytes("UTF-16LE");
			System.out.println();
			ModificationItem[] mods = new ModificationItem[1];
			mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
					new BasicAttribute("unicodePwd", newUnicodePassword));
			ctx.modifyAttributes("cn=" + username + baseName, mods);
			
			Attribute memberGroup = new BasicAttribute("memberOf","MAME_OF_THE_GROUP");
			Attributes attrs = new BasicAttributes();
			attrs.put(memberGroup);
			
			
		} catch (Exception e) {
			System.out.println("update password error: " + e);
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * 登陆认证
	 * 
	 * @param userDn
	 * @param password
	 * @return boolean
	 */
	public boolean authenticate(String userDn, String password) {
		try {
			Control[] connCtls = new Control[] {};
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
					ctx.close();
				} catch (NamingException e) {
					e.printStackTrace();
				}
				ctx = null;
			}
		}
	}

	/**
	 * 删除用户.
	 * 
	 * @param userDN 用户DN
	 * @return
	 */
	public boolean del(String userDN) {
		try {
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
					e.printStackTrace();
				}
				ctx = null;
			}
		}
		return false;
	}

	public static void main(String[] args) {
		ADOpersion adt = new ADOpersion();
		adt.update("TRTJK_02");
	}
}