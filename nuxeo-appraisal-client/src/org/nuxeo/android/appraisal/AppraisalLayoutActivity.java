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

    protected Button cancelBtn;

    @Override
    protected ViewGroup getLayoutContainer() {
        return (ScrollView) findViewById(R.id.layoutContainer);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.createeditlayout);
        title = (TextView) findViewById(R.id.currentDocTitle);

        if (isEditMode()) {
            title.setText("Edit " + getCurrentDocument().getTitle() + " ("
                    + getCurrentDocument().getType() + ")");
        } else if (isCreateMode()) {
            title.setText("Create new " + getCurrentDocument().getType());
        } else {
            title.setText("View " + getCurrentDocument().getTitle() + " ("
                    + getCurrentDocument().getType() + ")");
        }

        saveBtn = (Button) findViewById(R.id.updateDocument);
        saveBtn.setOnClickListener(this);

        cancelBtn = (Button) findViewById(R.id.cancelDocument);
        cancelBtn.setOnClickListener(this);

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
        } else if (view == cancelBtn) {
            cancelUpdate();
        }
    }

    @Override
    protected void populateMenu(Menu menu) {
    }

}
