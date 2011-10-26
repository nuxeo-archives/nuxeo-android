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

import org.nuxeo.android.documentprovider.LazyUpdatableDocumentsList;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

public class SimpleDocumentsListSampleActivity extends
        BaseSampleDocumentsListActivity {

    @Override
    protected LazyUpdatableDocumentsList fetchDocumentsList(byte cacheParam)
            throws Exception {
        Documents docs = getNuxeoContext().getDocumentManager().query(
                "select * from Document where ecm:mixinType != \"HiddenInNavigation\" AND ecm:isCheckedInVersion = 0 order by dc:modified desc",
                null, null, null, 0, 10, cacheParam);
        if (docs != null) {
            return docs.asUpdatableDocumentsList();
        }
        throw new RuntimeException("fetch Operation did return null");
    }

}
