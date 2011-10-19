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

package org.nuxeo.android.activities;

import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.android.layout.NuxeoLayout;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.DocumentStatus;
import org.nuxeo.ecm.automation.client.jaxrs.model.IdRef;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

public abstract class BaseDocumentLayoutActivity extends BaseNuxeoActivity {

	public static final String DOCUMENT = "document";

	public static final String MODE = "mode";

	protected Document currentDocument;

	protected boolean requireAsyncFetch = true;

	public static final int MNU_SWITCH_EDIT = 1000;

	public static final int MNU_SWITCH_VIEW = 1001;

	protected NuxeoLayout layout;

    public BaseDocumentLayoutActivity() {
        super();
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (isCreateMode()) {
			// can not refresh from the server a not yet existing document
			requireAsyncFetch = false;
		} else {
			if (getCurrentDocument().getStatusFlag()!=DocumentStatus.SYNCHRONIZED) {
				// do not refresh if local update
				requireAsyncFetch = false;
			}
		}
	}

	protected abstract ViewGroup getLayoutContainer();

	protected void buildLayout() {
		layout = getAutomationClient().getLayoutService().getLayout(this, getCurrentDocument(), getLayoutContainer(), getMode());
		//layout = getAutomationClient().getLayoutService().parseLayoutDefinition(StaticLayouts.STUDIO_LAYOUT, this, getCurrentDocument(), getLayoutContainer(), getMode());
	}

	protected NuxeoLayout getLayout() {
		if (layout==null) {
			buildLayout();
		}
		return layout;
	}

	protected LayoutMode getMode() {
		return (LayoutMode) getIntent().getExtras().get(MODE);
	}

	protected boolean isCreateMode() {
		return getMode()==LayoutMode.CREATE;
	}

	protected boolean isEditMode() {
		return getMode()==LayoutMode.EDIT;
	}

	protected Document getCurrentDocument() {
		if (currentDocument == null) {
			currentDocument = (Document) getIntent().getExtras().get(DOCUMENT);
		}
		return currentDocument;
	}

	@Override
	protected void onNuxeoDataRetrieved(Object data) {
		currentDocument = (Document) data;
		if (layout==null) {
		    Toast.makeText(this,
		            "Unable to get Layout",
		            Toast.LENGTH_SHORT).show();
		} else {
			layout.refreshFromDocument(currentDocument);
		    Toast.makeText(this,
		            "Refreshed document",
		            Toast.LENGTH_SHORT).show();
		}
	    requireAsyncFetch=false;
	}

	@Override
	protected Object retrieveNuxeoData() throws Exception {
		Document refreshedDocument = getNuxeoContext().getDocumentManager().getDocument(new IdRef(getCurrentDocument().getId()), true);
		return refreshedDocument;
	}

	@Override
	protected boolean requireAsyncDataRetrieval() {
		return requireAsyncFetch;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (getLayout()!=null) {
			layout.onActivityResult(requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}


	protected void saveDocument() {
		Document doc = getCurrentDocument();
		getLayout().applyChanges(doc);
		setResult(RESULT_OK, new Intent().putExtra(DOCUMENT, doc));
		this.finish();
	}

	protected void cancelUpdate() {
		Document doc = getCurrentDocument();
		setResult(RESULT_CANCELED, new Intent().putExtra(DOCUMENT, doc));
		this.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		populateMenu(menu);
		if (LayoutMode.VIEW==getMode()) {
			menu.add(Menu.NONE, MNU_SWITCH_EDIT, 0, "Switch to Edit");
		}
		if (LayoutMode.EDIT==getMode()) {
			menu.add(Menu.NONE, MNU_SWITCH_VIEW, 0, "Switch to View");
		}
		return super.onCreateOptionsMenu(menu);
	}

	protected abstract void populateMenu(Menu menu);

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MNU_SWITCH_EDIT:
			restart(MODE, LayoutMode.EDIT);
			return true;
		case MNU_SWITCH_VIEW:
			restart(MODE, LayoutMode.VIEW);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}