package org.nuxeo.ecm.automation.client.jaxrs.model;

public enum DocumentStatus {

	UPDATED("updated"), NEW("new"), SYNCHRONIZED(""), CONFLICT("conflict");

	private final String value;

	DocumentStatus(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return getValue();
	}
}
