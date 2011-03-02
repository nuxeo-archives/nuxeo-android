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

package org.nuxeo.android.simpleclient.listing;

import org.nuxeo.android.simpleclient.provider.DocumentsSearchProvider;
import org.nuxeo.android.simpleclient.service.NuxeoAndroidServices;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;

import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectUnavailableException;

/**
 * @author Nuxeo & Smart&Soft
 * @since 2011.02.18
 */
public final class DocumentsSearchActivity extends BaseDocumentListActivity {

    private String queryText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String action = getIntent().getAction();
        if (Intent.ACTION_SEARCH.equals(action) == true) {
            onSearch(getIntent());
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onNewIntent(Intent newIntent) {
        super.onNewIntent(newIntent);

        final String action = newIntent.getAction();
        if (Intent.ACTION_SEARCH.equals(action) == true) {
            onSearch(getIntent());
        }
    }

    private void onSearch(Intent intent) {
        queryText = intent.getStringExtra(SearchManager.QUERY);
        final SearchRecentSuggestions suggestions = new SearchRecentSuggestions(
                this, DocumentsSearchProvider.AUTHORITY,
                DocumentsSearchProvider.MODE);
        // We remember the search
        suggestions.saveRecentQuery(queryText, null);
    }

    protected Documents getDocuments(boolean refresh)
            throws BusinessObjectUnavailableException {
        return NuxeoAndroidServices.getInstance().queryFullText(queryText,
                refresh);
    }

}
