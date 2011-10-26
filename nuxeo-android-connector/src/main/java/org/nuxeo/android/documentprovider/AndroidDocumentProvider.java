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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nuxeo.android.cache.sql.DocumentProviderTableWrapper;
import org.nuxeo.android.cache.sql.SQLStateManager;
import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.Session;

public class AndroidDocumentProvider implements DocumentProvider {

    protected final SQLStateManager sqlStateManager;

    protected final Map<String, LazyDocumentsList> documentLists = new HashMap<String, LazyDocumentsList>();

    public AndroidDocumentProvider(SQLStateManager sqlStateManager) {
        this.sqlStateManager = sqlStateManager;
        sqlStateManager.registerWrapper(new DocumentProviderTableWrapper());
    }

    protected DocumentProviderTableWrapper getTableWrapper() {
        return (DocumentProviderTableWrapper) sqlStateManager.getTableWrapper(DocumentProviderTableWrapper.TBLNAME);
    }

    @Override
    public LazyDocumentsList getReadOnlyDocumentsList(String name,
            Session session) {

        LazyDocumentsList provider = documentLists.get(name);
        if (provider == null) {
            provider = getStoredProvider(session, name);
        }
        return provider;
    }

    @Override
    public LazyUpdatableDocumentsList getDocumentsList(String name,
            Session session) {
        LazyDocumentsList provider = getReadOnlyDocumentsList(name, session);
        if (provider != null
                && LazyUpdatableDocumentsList.class.isAssignableFrom(provider.getClass())) {
            return (LazyUpdatableDocumentsList) provider;
        }
        return null;
    }

    @Override
    public void registerNamedProvider(LazyDocumentsList docList,
            boolean persistent) {
        documentLists.put(docList.getName(), docList);
        if (persistent) {
            storeProvider(docList.getName(), docList);
        }
    }

    public void registerNamedProvider(Session session, String name,
            String nxql, int pageSize, boolean readOnly, boolean persistent,
            String exposedMimeType) {
        LazyDocumentsList docList = null;
        if (readOnly) {
            docList = new LazyDocumentsListImpl(session, nxql, null, null,
                    null, pageSize);
        } else {
            docList = new LazyUpdatableDocumentsListImpl(session, nxql, null,
                    null, null, pageSize);
        }
        docList.setName(name);
        if (exposedMimeType != null) {
            docList.setExposedMimeType(exposedMimeType);
        }
        registerNamedProvider(docList, persistent);
    }

    public void registerNamedProvider(String name,
            OperationRequest fetchOperation, String pageParametrerName,
            boolean readOnly, boolean persistent, String exposedMimeType) {
        LazyDocumentsList docList = null;
        if (readOnly) {
            docList = new LazyDocumentsListImpl(fetchOperation,
                    pageParametrerName);
        } else {
            docList = new LazyUpdatableDocumentsListImpl(fetchOperation,
                    pageParametrerName);
        }
        docList.setName(name);
        if (exposedMimeType != null) {
            docList.setExposedMimeType(exposedMimeType);
        }
        registerNamedProvider(docList, persistent);
    }

    @Override
    public void unregisterNamedProvider(String name) {
        documentLists.remove(name);
        removeStoredProvider(name);
    }

    protected void removeStoredProvider(String name) {
        getTableWrapper().deleteEntry(name);
    }

    protected LazyDocumentsList getStoredProvider(Session session, String name) {
        return getTableWrapper().getStoredProvider(session, name);
    }

    protected void storeProvider(String name, LazyDocumentsList docList) {
        getTableWrapper().storeProvider(name, docList);
    }

    public List<String> listProviderNames() {
        List<String> names = new ArrayList<String>(documentLists.keySet());
        return names;
    }

    public boolean isRegistred(String name) {
        return documentLists.containsKey(name); // XXX check DB too
    }
}
