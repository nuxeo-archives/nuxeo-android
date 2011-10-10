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

package org.nuxeo.android.broadcast;


public class NuxeoBroadcastMessages {

	public static final String NUXEO_SETTINGS_CHANGED = "NuxeoSettingsChanged";

	public static final String NUXEO_SERVER_CONNECTIVITY_CHANGED = "NuxeoServerConnectivityChanged";

	public static final String DOCUMENT_CREATED_CLIENT = "NuxeoDocumentCreatedClient";

	public static final String DOCUMENT_CREATED_SERVER = "NuxeoDocumentCreatedServer";

	public static final String DOCUMENT_CREATED_FAILED = "NuxeoDocumentCreatedFailer";

	public static final String DOCUMENT_UPDATED_CLIENT = "NuxeoDocumentUpdatedClient";

	public static final String DOCUMENT_UPDATED_SERVER = "NuxeoDocumentUpdatedServer";

	public static final String DOCUMENT_UPDATED_FAILED = "NuxeoDocumentUpdatedFailed";

	public static final String DOCUMENT_DELETED_CLIENT = "NuxeoDocumentDeletedClient";

	public static final String DOCUMENT_DELETED_SERVER = "NuxeoDocumentDeletedServer";

	public static final String DOCUMENT_DELETED_FAILED = "NuxeoDocumentDeletedFailed";

	public static final String EXTRA_DOCUMENT_PAYLOAD_KEY = "NuxeoDocument";

	public static final String EXTRA_REQUESTID_PAYLOAD_KEY = "RequestId";

	public static final String EXTRA_SOURCEDOCUMENTSLIST_PAYLOAD_KEY = "SourceDocumentsList";

}
