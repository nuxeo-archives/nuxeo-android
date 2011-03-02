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
 *
 */

package org.nuxeo.android.simpleclient.menus;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.protocol.HTTP;
import org.nuxeo.android.simpleclient.R;
import org.nuxeo.android.simpleclient.ui.TitleBarAggregate;
import org.nuxeo.android.simpleclient.ui.TitleBarShowHomeFeature;

import android.webkit.WebView;

import com.smartnsoft.droid4me.app.SmartActivity;
import com.smartnsoft.droid4me.framework.LifeCycle;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectUnavailableException;
import com.smartnsoft.droid4me.ws.WebServiceCaller;

/**
 * The "about" screen.
 *
 * @author Nuxeo & Smart&Soft
 * @since 2011.02.17
 */
public final class AboutActivity extends
        SmartActivity<TitleBarAggregate> implements
        LifeCycle.BusinessObjectsRetrievalAsynchronousPolicy,
        TitleBarShowHomeFeature {

    private String content;

    private WebView webView;

    public void onRetrieveDisplayObjects() {
        setContentView(R.layout.about);
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setSupportMultipleWindows(false);
        webView.getSettings().setSupportZoom(false);
    }

    public void onRetrieveBusinessObjects()
            throws BusinessObjectUnavailableException {
        final InputStream inputStream = getResources().openRawResource(
                R.raw.about);
        try {
            content = WebServiceCaller.getString(inputStream);
        } catch (IOException exception) {
            throw new BusinessObjectUnavailableException(exception);
        }
    }

    public void onFulfillDisplayObjects() {
        webView.loadDataWithBaseURL("file:///android_asset/", content,
                "text/html", HTTP.UTF_8, null);
    }

    public void onSynchronizeDisplayObjects() {
    }

}
