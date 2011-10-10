/*
 * (C) Copyright 2011 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 */

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
