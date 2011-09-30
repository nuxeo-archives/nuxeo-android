package org.nuxeo.ecm.automation.client.jaxrs;

public class Dependency {

	public enum DependencyType {

		OPERATION_REQUEST("OperationRequest"), FILE_UPLOAD("FileUpload");

		private final String value;

		DependencyType(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		@Override
		public String toString() {
			return getValue();
		}

		public static DependencyType fromString(String value) {
		        for (DependencyType nit : DependencyType.values()) {
		            if (nit.getValue().equals(value)) {
		                return nit;
		            }
		        }
		        return null;
		}
	}

	protected DependencyType type;

	protected String token;

	public Dependency(DependencyType type, String token) {
		this.type = type;
		this.token = token;
	}

	public DependencyType getType() {
		return type;
	}

	public String getToken() {
		return token;
	}

}
