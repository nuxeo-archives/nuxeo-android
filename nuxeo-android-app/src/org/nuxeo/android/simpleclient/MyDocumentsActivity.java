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

import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartnsoft.droid4me.app.AppPublics.CommonActivity;
import com.smartnsoft.droid4me.app.WrappedSmartListActivity;
import com.smartnsoft.droid4me.ext.download.ImageDownloader;
import com.smartnsoft.droid4me.framework.DetailsProvider.BusinessViewWrapper;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectUnavailableException;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectsRetrievalAsynchronousPolicy;

public class MyDocumentsActivity extends WrappedSmartListActivity implements
		BusinessObjectsRetrievalAsynchronousPolicy {

	private final static class DocumentAttributes {

		private final TextView title;

		private final TextView description;

		private final ImageView icon;

		public DocumentAttributes(View view) {
			title = (TextView) view.findViewById(R.id.title);
			icon = (ImageView) view.findViewById(R.id.icon);
			description = (TextView) view.findViewById(R.id.description);
		}

		public void update(Document businessObject, Handler handler) {
			title.setText(businessObject.getTitle());
			description.setText(businessObject.getString("dc:description"));
			String iconUrl = SettingsActivity.getCurrentNuxeoURLPrefix() + "/"
					+ businessObject.getString("common:icon");
			ImageDownloader.getInstance().get(icon, iconUrl, null, handler,
					NuxeoAndroidApplication.CACHE_IMAGE_INSTRUCTIONS);
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
			Handler handler = ((CommonActivity) activity).getHandler();
			((DocumentAttributes) viewAttributes).update(businessObject,
					handler);
		}

	}

	public List<? extends BusinessViewWrapper<?>> retrieveBusinessObjectsList()
			throws BusinessObjectUnavailableException {

		String serverUrl = getPreferences().getString(
				SettingsActivity.PREF_SERVER_URL, "")
				+ SettingsActivity.PREF_SERVER_URL_SUFFIX;
		HttpAutomationClient client = new HttpAutomationClient(serverUrl);
		String login = getPreferences().getString(SettingsActivity.PREF_LOGIN,
				"");
		String password = getPreferences().getString(
				SettingsActivity.PREF_PASSWORD, "");
		Session session = client.getSession(login, password);
		Documents docs;
		try {
			docs = (Documents) session
					.newRequest("Document.Query")
					.set("query",
							"SELECT * FROM Document where ecm:mixinType != 'HiddenInNavigation'")
					.setHeader(
							org.nuxeo.ecm.automation.client.jaxrs.Constants.HEADER_NX_SCHEMAS,
							"*").execute();
		} catch (Exception e) {
			throw new BusinessObjectUnavailableException(e);
		}
		client.shutdown();

		List<BusinessViewWrapper<?>> wrappers = new ArrayList<BusinessViewWrapper<?>>();

		for (Document document : docs) {
			wrappers.add(new DocumentWrapper(document));
		}
		return wrappers;
	}
}
