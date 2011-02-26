package org.nuxeo.android.simpleclient;

import org.nuxeo.android.simpleclient.service.NuxeoAndroidServices;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.smartnsoft.droid4me.app.SmartActivity;
import com.smartnsoft.droid4me.framework.LifeCycle;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectUnavailableException;

public class OfflineSettingsActivity extends
        SmartActivity<NuxeoAndroidApplication.TitleBarAggregate> implements
        LifeCycle.BusinessObjectsRetrievalAsynchronousPolicy,
        NuxeoAndroidApplication.TitleBarShowHomeFeature {

    private int operationCount;
    private long cacheSize;
    private boolean offline;
    private TextView operationCountView;
    private TextView cacheSizeView;
    private TextView offlineView;
    private Button refreshOpAction;
    private Button clearCacheAction;

    @Override
    public void onFulfillDisplayObjects() {
        cacheSizeView.setText("Current cache size : " + (cacheSize/1024) + " KB");
        operationCountView.setText(operationCount + " operation definitions (cached)");
        if (offline) {
            offlineView.setText("Nuxeo Client is currently offline");
        } else {
            offlineView.setText("Nuxeo Client is currently online");
        }
    }

    @Override
    public void onRetrieveBusinessObjects()
            throws BusinessObjectUnavailableException {
        operationCount = NuxeoAndroidServices.getInstance().getKnownOperationsCount();
        offline = NuxeoAndroidServices.getInstance().isOfflineMode();
        cacheSize = NuxeoAndroidServices.getInstance().getCacheSize();

    }

    @Override
    public void onRetrieveDisplayObjects() {
        setContentView(R.layout.offline_screen);
        operationCountView = (TextView) findViewById(R.id.operationCount);
        cacheSizeView = (TextView) findViewById(R.id.cacheSize);
        offlineView = (TextView) findViewById(R.id.offlineStatus);

        refreshOpAction = (Button) findViewById(R.id.refetchOperations);
        refreshOpAction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                NuxeoAndroidServices.getInstance().refreshOperationCache();
            }
        });

        clearCacheAction = (Button) findViewById(R.id.clearCache);
        clearCacheAction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                NuxeoAndroidServices.getInstance().flushCache();
            }
        });
    }

    @Override
    public void onSynchronizeDisplayObjects() {
        operationCount = NuxeoAndroidServices.getInstance().getKnownOperationsCount();
        offline = NuxeoAndroidServices.getInstance().isOfflineMode();
        cacheSize = NuxeoAndroidServices.getInstance().getCacheSize();
    }

}
