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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.nuxeo.android.simpleclient.service.NuxeoAndroidServices;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartnsoft.droid4me.app.AppPublics;
import com.smartnsoft.droid4me.app.WrappedSmartListActivity;
import com.smartnsoft.droid4me.app.AppPublics.BroadcastListener;
import com.smartnsoft.droid4me.download.ImageDownloader;
import com.smartnsoft.droid4me.framework.DetailsProvider.BusinessViewWrapper;
import com.smartnsoft.droid4me.framework.DetailsProvider.ObjectEvent;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectUnavailableException;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectsRetrievalAsynchronousPolicy;

public class MyDocumentsActivity extends
        WrappedSmartListActivity<NuxeoAndroidApplication.TitleBarAggregate>
        implements BusinessObjectsRetrievalAsynchronousPolicy,
        AppPublics.SendLoadingIntent, AppPublics.BroadcastListenerProvider,
        NuxeoAndroidApplication.TitleBarShowHomeFeature,
        NuxeoAndroidApplication.TitleBarRefreshFeature {


    private final static class DocumentAttributes {

        private final TextView title;

        private final TextView desc;

        private final ImageView icon;

        protected final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");

        public DocumentAttributes(View view) {
            title = (TextView) view.findViewById(R.id.title);
            desc = (TextView) view.findViewById(R.id.desc);
            icon = (ImageView) view.findViewById(R.id.icon);
        }

        public void update(Context context, Handler handler, Document doc) {
            title.setText(doc.getTitle());
            String descString = doc.getProperties().getString("dc:description",
                    "");
            if ("null".equals(descString)) {
                descString = "";
            }
            if ("".equals(descString)) {
                descString = "<b>Type </b>: " + doc.getType();
                descString += "&nbsp;<b> State </b>: " + doc.getState();
                descString += "&nbsp;<b> Modified </b>: " + dateFormat.format(doc.getLastModified());
                desc.setText(Html.fromHtml(descString));
            } else {
                desc.setText(descString);
            }

            final String serverUrl = context.getSharedPreferences(
                    "org.nuxeo.android.simpleclient_preferences", 0).getString(
                    SettingsActivity.PREF_SERVER_URL, "");
            String urlImage = serverUrl + (serverUrl.endsWith("/") ? "" : "/")
                    + doc.getString("common:icon", "");

            ImageDownloader.getInstance().get(icon, urlImage, null, handler,
                    NuxeoAndroidApplication.CACHE_IMAGE_INSTRUCTIONS);

        }
    }

    private final class DocumentWrapper extends BusinessViewWrapper<Document> {

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
            ((DocumentAttributes) viewAttributes).update(activity,
                    getHandler(), businessObject);
        }

        @Override
        public Intent computeIntent(Activity activity, Object viewAttributes,
                View view, Document doc, ObjectEvent objectEvent) {
            if (objectEvent == ObjectEvent.Clicked) {
                if ("Note".equals(doc.getType())) {
                    return new Intent(activity, NoteViewActivity.class).putExtra(
                            BaseDocumentViewActivity.DOCUMENT_ID, doc.getId()).putExtra(BaseDocumentViewActivity.DOCUMENT, doc);
                }
                else if ("Picture".equals(doc.getType())) {
                    return new Intent(activity, PictureViewActivity.class).putExtra(
                            BaseDocumentViewActivity.DOCUMENT_ID, doc.getId()).putExtra(BaseDocumentViewActivity.DOCUMENT, doc);
                }
                else {
                    return new Intent(activity, DocumentViewActivity.class).putExtra(
                            BaseDocumentViewActivity.DOCUMENT_ID, doc.getId()).putExtra(BaseDocumentViewActivity.DOCUMENT, doc);
                }
            }
            return super.computeIntent(activity, view, objectEvent);
        }

    }

    private boolean fromCache = true;

    public BroadcastListener getBroadcastListener() {
        return new AppPublics.LoadingBroadcastListener(this, true) {
            @Override
            protected void onLoading(boolean isLoading) {
                getAggregate().getAttributes().toggleRefresh(isLoading);
            }
        };
    }

    public List<? extends BusinessViewWrapper<?>> retrieveBusinessObjectsList()
            throws BusinessObjectUnavailableException {

        // Fetch data from Nuxeo Server
        Documents docs = getDocuments(fromCache == false);
        fromCache = true;

        List<BusinessViewWrapper<?>> wrappers = new ArrayList<BusinessViewWrapper<?>>();

        for (Document document : docs) {
            wrappers.add(new DocumentWrapper(document));
        }
        return wrappers;
    }

    protected Documents getDocuments(boolean refresh)
            throws BusinessObjectUnavailableException {
        return NuxeoAndroidServices.getInstance().getMyDocuments(refresh);
    }

    @Override
    public void onFulfillDisplayObjects() {
        super.onFulfillDisplayObjects();

        getSmartListView().getListView().setEmptyView(
                getLayoutInflater().inflate(R.layout.empty_list_view, null));
    }

    @Override
    public void onTitleBarRefresh() {
        fromCache = false;
        refreshBusinessObjectsAndDisplayAndNotifyBusinessObjectsChanged(false);
    }

}
