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
package org.nuxeo.android.simpleclient.listing;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.android.simpleclient.docviews.BaseDocumentViewActivity;
import org.nuxeo.android.simpleclient.docviews.DocumentViewActivity;
import org.nuxeo.android.simpleclient.listing.ui.TaskItemViewWrapper;
import org.nuxeo.android.simpleclient.service.NuxeoAndroidServices;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.smartnsoft.droid4me.framework.DetailsProvider.BusinessViewWrapper;
import com.smartnsoft.droid4me.framework.DetailsProvider.ObjectEvent;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectUnavailableException;

public class TaskListActivity extends BaseObjectListActivity {

    @Override
    public List<? extends BusinessViewWrapper<?>> retrieveBusinessObjectsList()
            throws BusinessObjectUnavailableException {

        List<JSONObject> tasks = NuxeoAndroidServices.getInstance().getTasks(
                fromCache == false);

        List<BusinessViewWrapper<?>> wrappers = new ArrayList<BusinessViewWrapper<?>>();

        for (JSONObject task : tasks) {
            wrappers.add(new TaskItemViewWrapper(this, task));
        }
        return wrappers;

    }

    @Override
    public Intent handleEventOnListItem(Activity activity,
            Object viewAttributes, View view, Object obj,
            ObjectEvent objectEvent) {

        JSONObject task = (JSONObject) obj;

        if (task.has("docref")) {
            String docRef;
            try {
                docRef = task.getString("docref");
                return new Intent(activity, DocumentViewActivity.class).putExtra(
                        BaseDocumentViewActivity.DOCUMENT_ID, docRef);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

        } else {
            return null;
        }
    }

}
