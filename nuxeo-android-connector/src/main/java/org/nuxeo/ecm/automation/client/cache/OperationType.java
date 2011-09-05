package org.nuxeo.ecm.automation.client.cache;

public enum OperationType {

	UPDATE("Update"), CREATE("Create"), DELETE("Delete");

	private final String value;

	OperationType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static OperationType fromString(String value) {
        for (OperationType nit : OperationType.values()) {
            if (nit.getValue().equals(value)) {
                return nit;
            }
        }
        return null;
    }

	@Override
	public String toString() {
		return getValue();
	}
}
