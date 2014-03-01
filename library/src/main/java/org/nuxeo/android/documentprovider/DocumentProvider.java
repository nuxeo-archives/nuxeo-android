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

package org.nuxeo.android.documentprovider;

import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.Session;

import java.util.List;

public interface DocumentProvider {

    void registerNamedProvider(String name, OperationRequest fetchOperation,
            String pageParametrerName, boolean readOnly, boolean persistent,
            String exposedMimeType);

    void registerNamedProvider(Session session, String name, String nxql,
            int pageSize, boolean readOnly, boolean persistent,
            String exposedMimeType);

    void registerNamedProvider(LazyDocumentsList docList, boolean persistent);

    void unregisterNamedProvider(String name);

    LazyDocumentsList getReadOnlyDocumentsList(String name, Session session);

    LazyUpdatableDocumentsList getDocumentsList(String name, Session session);

    List<String> listProviderNames();

    boolean isRegistred(String name);

}
