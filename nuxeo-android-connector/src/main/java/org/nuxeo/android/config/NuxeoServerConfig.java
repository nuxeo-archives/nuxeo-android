package org.nuxeo.android.config;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

public class NuxeoServerConfig {

	protected String serverBaseUrl = "http://10.0.2.2:8080/nuxeo/"; // http://android.demo.nuxeo.com/nuxeo/

	protected String login = "Administrator"; // "droidUser";

	protected String password = "Administrator"; // "nuxeo4android";

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getServerBaseUrl() {
		return serverBaseUrl;
	}

	public void setServerBaseUrl(String serverBaseUrl) {
		if (!serverBaseUrl.endsWith("/")) {
			serverBaseUrl = serverBaseUrl + "/";
		}
		this.serverBaseUrl = serverBaseUrl;
	}

	public String getAutomationUrl() {
		return serverBaseUrl + "site/automation";
	}

	public String getHost() {
		String url = getServerBaseUrl();
		URI uri;
		try {
			uri = new URI(url);
		} catch (URISyntaxException e) {
			return null;
		}
		return uri.getHost();
	}

	public int getHostIP() {
		return getIPasInt(getHost());
	}

	protected  int getIPasInt(String hostname) {
		if (hostname==null) {
			return -1;
		}
	    InetAddress inetAddress;
	    try {
	        inetAddress = InetAddress.getByName(hostname);
	    } catch (UnknownHostException e) {
	        return -1;
	    }
	    byte[] addrBytes;
	    int addr;
	    addrBytes = inetAddress.getAddress();
	    addr = ((addrBytes[3] & 0xff) << 24)
	            | ((addrBytes[2] & 0xff) << 16)
	            | ((addrBytes[1] & 0xff) << 8)
	            |  (addrBytes[0] & 0xff);
	    return addr;
	}
}
