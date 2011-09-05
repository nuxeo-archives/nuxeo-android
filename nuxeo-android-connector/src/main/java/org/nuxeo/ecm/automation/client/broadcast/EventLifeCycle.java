package org.nuxeo.ecm.automation.client.broadcast;

public enum EventLifeCycle {

	SERVER("Server"), CLIENT("Client"), FAILED("Failed");

	private final String value;

	EventLifeCycle(String value) {
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
