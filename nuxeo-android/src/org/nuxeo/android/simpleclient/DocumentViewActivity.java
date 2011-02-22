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
 *     Jocelyn Girard, Edouard Mercier, Thierry Delprat
 *
 */

package org.nuxeo.android.simpleclient;

import org.nuxeo.android.simpleclient.service.NuxeoAndroidServices;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartnsoft.droid4me.app.AppPublics.BroadcastListenerProvider;
import com.smartnsoft.droid4me.app.AppPublics.SendLoadingIntent;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectUnavailableException;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectsRetrievalAsynchronousPolicy;

public final class DocumentViewActivity extends BaseDocumentViewActivity
        implements BusinessObjectsRetrievalAsynchronousPolicy,
        SendLoadingIntent, BroadcastListenerProvider,
        NuxeoAndroidApplication.TitleBarShowHomeFeature,
        NuxeoAndroidApplication.TitleBarRefreshFeature {

    private TextView description;

    private TextView title;

    private LinearLayout layout;

    @Override
    public void onRetrieveDisplayObjects() {

        setContentView(R.layout.document_view_layout);
        layout = (LinearLayout) findViewById(R.id.documentLayout);
        title = (TextView) findViewById(R.id.title);
        description = (TextView) findViewById(R.id.description);
    }

    @Override
    public void onRetrieveBusinessObjects()
            throws BusinessObjectUnavailableException {
        fetchDocument(false);
    }

    @Override
    public void onFulfillDisplayObjects() {

        if (document != null) {
            title.setText(document.getTitle());
            description.setText(document.getString("dc:description", ""));
            for (String key : document.getProperties().getKeys()) {
                final TextView textView = new TextView(this);

                if (key != null) {
                    try {
                        final String value = document.getString(key, "");
                        if (value != null) {
                            textView.setText(key + " => " + value);
                            final int padding = getResources().getDimensionPixelSize(
                                    R.dimen.defaultPadding);
                            textView.setPadding(padding, padding, padding,
                                    padding);
                            layout.addView(textView);
                        }
                    } catch (Exception exception) {
                        if (log.isWarnEnabled()) {
                            log.warn("NULL", exception);
                        }
                    }
                }
            }
        }

    }

    @Override
    protected String getSchemas() {
        return NuxeoAndroidServices.DEFAULT_SCHEMAS;
    }

}
