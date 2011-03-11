package org.nuxeo.android.simpleclient.listing.ui;

import android.content.Context;
import android.os.Handler;

public interface ObjectItemViewUpdater {

    public abstract void update(Context context, Handler handler, Object item);

}