package org.nuxeo.android.simpleclient.listing.ui;

import org.json.JSONObject;
import org.nuxeo.android.simpleclient.R;
import org.nuxeo.android.simpleclient.listing.BaseObjectListActivity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.smartnsoft.droid4me.framework.DetailsProvider.BusinessViewWrapper;
import com.smartnsoft.droid4me.framework.DetailsProvider.ObjectEvent;

public class TaskItemViewWrapper extends BusinessViewWrapper<JSONObject> {

    protected final BaseObjectListActivity baseObjectListActivity;

    public TaskItemViewWrapper(BaseObjectListActivity baseDocumentListActivity,
            JSONObject businessObject) {
        super(businessObject);
        this.baseObjectListActivity = baseDocumentListActivity;
    }

    @Override
    protected View createNewView(Activity activity, JSONObject businessObjectClass) {
        return activity.getLayoutInflater().inflate(
                R.layout.taskitem_view, null);
    }

    @Override
    protected Object extractNewViewAttributes(Activity activity, View view,
            JSONObject businessObjectClass) {
        return new TaskItemAttributeUpdater(view);
    }

    @Override
    protected void updateView(Activity activity, Object viewAttributes, View view,
            JSONObject businessObject, int position) {
                ((ObjectItemViewUpdater) viewAttributes).update(activity,
                        this.baseObjectListActivity.getHandler(), businessObject);
            }

    @Override
    public Intent computeIntent(Activity activity, Object viewAttributes, View view,
            JSONObject task, ObjectEvent objectEvent) {
                Intent intent = this.baseObjectListActivity.handleEventOnListItem(activity, viewAttributes, view, task, objectEvent);
                if (intent!=null) {
                    return intent;
                }
                return this.computeIntent(activity, view, objectEvent);
            }
}
