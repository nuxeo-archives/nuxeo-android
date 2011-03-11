package org.nuxeo.android.simpleclient.listing;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
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

        List<JSONObject> tasks = NuxeoAndroidServices.getInstance().getTasks(fromCache == false);

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
        // TODO Auto-generated method stub
        return null;
    }

}
