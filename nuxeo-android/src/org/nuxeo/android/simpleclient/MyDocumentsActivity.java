/*
 * (C) Copyright 2010-2011 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 */

package org.nuxeo.android.simpleclient;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.android.simpleclient.service.NuxeoAndroidServices;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.smartnsoft.droid4me.app.WrappedSmartListActivity;
import com.smartnsoft.droid4me.framework.DetailsProvider.BusinessViewWrapper;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectUnavailableException;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectsRetrievalAsynchronousPolicy;

public class MyDocumentsActivity extends WrappedSmartListActivity implements
        BusinessObjectsRetrievalAsynchronousPolicy {

    private final static class DocumentAttributes {

        private final TextView title;

        private final TextView desc;

        public DocumentAttributes(View view) {
            title = (TextView) view.findViewById(R.id.title);
            desc = (TextView) view.findViewById(R.id.desc);
        }

        public void update(Document doc) {
            title.setText(doc.getTitle());
            String descString = doc.getProperties().getString("dc:description","");
            if ("null".equals(descString)) {
            	descString="";
            }
            desc.setText(descString);
        }

    }

    private static final class DocumentWrapper extends
            BusinessViewWrapper<Document> {

        public DocumentWrapper(Document businessObject) {
            super(businessObject);
        }

        @Override
        protected View createNewView(Activity activity, Document businessObject) {
            return activity.getLayoutInflater().inflate(
                    R.layout.my_documents_document, null);
        }

        @Override
        protected Object extractNewViewAttributes(Activity activity, View view,
                Document businessObject) {
            return new DocumentAttributes(view);
        }

        @Override
        protected void updateView(Activity activity, Object viewAttributes,
                View view, Document businessObject, int position) {
            ((DocumentAttributes) viewAttributes).update(businessObject);
        }
    }

    public List<? extends BusinessViewWrapper<?>> retrieveBusinessObjectsList()
            throws BusinessObjectUnavailableException {

    	// Fetch data from Nuxeo Server
        Documents docs = NuxeoAndroidServices.getInstance().getAllDocuments();

        List<BusinessViewWrapper<?>> wrappers = new ArrayList<BusinessViewWrapper<?>>();

        for (Document document : docs) {
            wrappers.add(new DocumentWrapper(document));
        }
        return wrappers;
    }
}
