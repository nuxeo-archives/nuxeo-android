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
package org.nuxeo.android.simpleclient.listing;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.android.simpleclient.docviews.BaseDocumentViewActivity;
import org.nuxeo.android.simpleclient.docviews.DocumentViewActivity;
import org.nuxeo.android.simpleclient.docviews.NoteViewActivity;
import org.nuxeo.android.simpleclient.docviews.PictureViewActivity;
import org.nuxeo.android.simpleclient.listing.ui.DocumentItemViewWrapper;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.smartnsoft.droid4me.framework.DetailsProvider.BusinessViewWrapper;
import com.smartnsoft.droid4me.framework.DetailsProvider.ObjectEvent;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectUnavailableException;

public abstract class BaseDocumentListActivity extends BaseObjectListActivity {

    @Override
    public Intent handleEventOnListItem(Activity activity,
            Object viewAttributes, View view, Object obj,
            ObjectEvent objectEvent) {
        return handleDocumentEventOnListItem(activity, viewAttributes, view,
                (Document) obj, objectEvent);
    }

    public Intent handleDocumentEventOnListItem(Activity activity,
            Object viewAttributes, View view, Document doc,
            ObjectEvent objectEvent) {
        if (objectEvent == ObjectEvent.Clicked) {
            if (DocumentHelper.isFolderish(doc)) {
                return new Intent(activity, DocumentChildrenListActivity.class).putExtra(
                        BaseDocumentViewActivity.DOCUMENT_ID, doc.getId()).putExtra(
                        BaseDocumentViewActivity.DOCUMENT, doc);
            } else if ("Note".equals(doc.getType())) {
                return new Intent(activity, NoteViewActivity.class).putExtra(
                        BaseDocumentViewActivity.DOCUMENT_ID, doc.getId()).putExtra(
                        BaseDocumentViewActivity.DOCUMENT, doc);
            } else if ("Picture".equals(doc.getType())) {
                return new Intent(activity, PictureViewActivity.class).putExtra(
                        BaseDocumentViewActivity.DOCUMENT_ID, doc.getId()).putExtra(
                        BaseDocumentViewActivity.DOCUMENT, doc);
            } else {
                return new Intent(activity, DocumentViewActivity.class).putExtra(
                        BaseDocumentViewActivity.DOCUMENT_ID, doc.getId()).putExtra(
                        BaseDocumentViewActivity.DOCUMENT, doc);
            }
        }
        return null;
    }

    @Override
    public List<? extends BusinessViewWrapper<?>> retrieveBusinessObjectsList()
            throws BusinessObjectUnavailableException {

        // Fetch data from Nuxeo Server
        Documents docs = getDocuments(fromCache == false);
        fromCache = true;

        List<BusinessViewWrapper<?>> wrappers = new ArrayList<BusinessViewWrapper<?>>();

        for (Document document : docs) {
            wrappers.add(new DocumentItemViewWrapper(this, document));
        }
        return wrappers;
    }

    protected abstract Documents getDocuments(boolean refresh)
            throws BusinessObjectUnavailableException;
}
