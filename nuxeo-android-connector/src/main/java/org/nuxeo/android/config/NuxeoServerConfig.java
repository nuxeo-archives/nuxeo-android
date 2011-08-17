package org.nuxeo.android.config;

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

}
