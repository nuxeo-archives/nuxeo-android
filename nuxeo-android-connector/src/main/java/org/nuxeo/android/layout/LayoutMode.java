package org.nuxeo.android.layout;

public enum LayoutMode {

	CREATE("create"), EDIT("EDIT"), VIEW("view");

	private final String value;

	LayoutMode(String value) {
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
