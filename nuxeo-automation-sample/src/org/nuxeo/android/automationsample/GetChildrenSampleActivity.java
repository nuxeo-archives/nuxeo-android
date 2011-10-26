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

package org.nuxeo.android.automationsample;

import org.nuxeo.android.activities.BaseDocumentLayoutActivity;
import org.nuxeo.android.documentprovider.LazyUpdatableDocumentsList;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import android.content.Intent;

public class GetChildrenSampleActivity extends BaseSampleDocumentsListActivity {

    public static final String ROOT_UUID_PARAM = "rootDocId";

    public static final String ROOT_DOC_PARAM = "rootDoc";

    protected Document root;

    protected String getRootUUID() {
        if (getRoot() != null) {
            return getRoot().getId();
        }
        if (getIntent().getExtras() != null) {
            return getIntent().getExtras().getString(ROOT_UUID_PARAM);
        }
        return null;
    }

    protected Document getRoot() {
        if (getIntent().getExtras() != null) {
            Object param = getIntent().getExtras().get(ROOT_DOC_PARAM);
            if (param != null && param instanceof Document) {
                root = (Document) param;
            }
        }
        return root;
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        if (getRoot() == null) {
            if (getRootUUID() == null) {
                root = getNuxeoContext().getDocumentManager().getRootDocument();
            } else {
                root = getNuxeoContext().getDocumentManager().getDocument(
                        getRootUUID());
            }
        }
        // let base class fetch the list
        return super.retrieveNuxeoData();
    }

    @Override
    protected LazyUpdatableDocumentsList fetchDocumentsList(byte cacheParam)
            throws Exception {
        Documents docs = getNuxeoContext().getDocumentManager().query(
                "select * from Document where ecm:mixinType != \"HiddenInNavigation\" AND ecm:isCheckedInVersion = 0 AND ecm:parentId=? order by dc:modified desc",
                new String[] { getRootUUID() }, null, null, 0, 10, cacheParam);
        if (docs != null) {
            return docs.asUpdatableDocumentsList();
        }
        throw new RuntimeException("fetch Operation did return null");
    }

    @Override
    protected void onListItemClicked(int listItemPosition) {
        Document selectedDocument = getDocumentsList().getDocument(
                listItemPosition);
        if (selectedDocument.isFolder()) {
            startActivity(new Intent(getApplicationContext(), this.getClass()).putExtra(
                    ROOT_DOC_PARAM, selectedDocument));
        } else {
            startActivity(new Intent(this, getEditActivityClass()).putExtra(
                    BaseDocumentLayoutActivity.DOCUMENT, selectedDocument).putExtra(
                    BaseDocumentLayoutActivity.MODE, LayoutMode.VIEW));
        }
    }

    @Override
    protected Document initNewDocument(String type) {
        return new Document(getRoot().getPath(), "newAndroidDoc", type);
    }

}
