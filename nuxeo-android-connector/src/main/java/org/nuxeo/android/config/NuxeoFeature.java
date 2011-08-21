package org.nuxeo.android.config;

public class NuxeoFeature {

	protected final String featureId;

	protected final String[] neededOperationIds;

	protected Boolean enabled;

	NuxeoFeature (String featureId, String operationId) {
		this.featureId=featureId;
		neededOperationIds = new String[]{operationId};
	}

	NuxeoFeature (String featureId, String[] operationIds) {
		this.featureId=featureId;
		neededOperationIds = operationIds;
	}


	public Boolean isEnabled() {
		if (enabled==null) {
			// TEST

		}
		return enabled;
	}






}
