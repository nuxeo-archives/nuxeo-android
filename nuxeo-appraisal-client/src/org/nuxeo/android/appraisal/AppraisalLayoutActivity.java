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

package org.nuxeo.android.appraisal;

import org.nuxeo.android.activities.BaseDocumentLayoutActivity;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

public class AppraisalLayoutActivity extends BaseDocumentLayoutActivity
        implements View.OnClickListener {

    protected TextView title;

    protected Button saveBtn;

    @Override
    protected ViewGroup getLayoutContainer() {
        return (ScrollView) findViewById(R.id.layoutContainer);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.createeditlayout);
        title = (TextView) findViewById(R.id.currentDocTitle);
        Document currentDocument = getCurrentDocument();
        		
        					
        if (isEditMode()) {
			setTitle("Edit " + currentDocument.getTitle() + " ("
					+ currentDocument.getType() + ")");
		} else if (isCreateMode()) {
			setTitle("Create new " + currentDocument.getType());
		} else {
			setTitle("View " + currentDocument.getTitle() + " (" + currentDocument.getType() + ")");
        }
        title.setText(currentDocument.getName());
        title.setVisibility(View.VISIBLE);

        saveBtn = (Button) findViewById(R.id.updateDocument);
        saveBtn.setOnClickListener(this);

        buildLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getMode() == LayoutMode.VIEW) {
            saveBtn.setVisibility(View.GONE);
        } else {
            saveBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == saveBtn) {
            saveDocument();
        }
    }

    @Override
    protected void populateMenu(Menu menu) {
    }

}
