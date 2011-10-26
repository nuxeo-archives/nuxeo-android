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

import java.util.Collection;
import java.util.Iterator;

import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import android.net.Uri;

public interface LazyDocumentsList {

    public abstract Documents getCurrentPage();

    public abstract Documents getFirstPage();

    public abstract Documents fetchAndChangeCurrentPage(int targetPage);

    public abstract int getCurrentPosition();

    public abstract int setCurrentPosition(int position);

    public abstract Document getCurrentDocument();

    public abstract int getPageCount();

    public abstract int getLoadedPageCount();

    public abstract Integer getLoadingPagesCount();

    public abstract Iterator<Document> getIterator();

    public abstract Collection<Documents> getLoadedPages();

    public abstract int getCurrentSize();

    public abstract void registerListener(DocumentsListChangeListener listener);

    public abstract void unregisterListener(DocumentsListChangeListener listener);

    public abstract Document getDocument(int index);

    public void refreshAll();

    public String getName();

    public boolean isReadOnly();

    public OperationRequest getFetchOperation();

    public String getPageParameterName();

    public void setName(String name);

    public boolean isFullyLoaded();

    public void setExposedMimeType(String exposedMimeType);

    public String getExposedMimeType();

    public Uri getContentUri();

}