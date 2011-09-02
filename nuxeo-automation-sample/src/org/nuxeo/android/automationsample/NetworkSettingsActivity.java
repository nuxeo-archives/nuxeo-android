package org.nuxeo.android.automationsample;

import org.nuxeo.android.activities.AbstractNetworkSettingsActivity;
import org.nuxeo.android.network.NuxeoNetworkStatus;
import org.nuxeo.ecm.automation.client.cache.DeferredUpdateManager;
import org.nuxeo.ecm.automation.client.cache.ResponseCacheManager;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class NetworkSettingsActivity extends AbstractNetworkSettingsActivity implements OnCheckedChangeListener, OnClickListener{

    private TextView cacheEntriesCount;
    private TextView cacheSize;
    private CheckBox forceOfflineChk;
    private CheckBox networkReachable;
    private CheckBox serverReachable;
    private Button clearCacheButton;
    private Button refreshButton;
    private TextView pendingCount;
    private Button execPendingButton;
    private Button clearPendingButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.offline_screen);

        forceOfflineChk = (CheckBox) findViewById(R.id.forceOfflineChk);
        networkReachable = (CheckBox) findViewById(R.id.networkReachableChk);
        serverReachable = (CheckBox) findViewById(R.id.serverReachableChk);
        cacheEntriesCount = (TextView) findViewById(R.id.cacheEntriesCount);
        cacheSize = (TextView) findViewById(R.id.cacheSize);
        clearCacheButton = (Button) findViewById(R.id.clearCacheBtn);
        refreshButton = (Button) findViewById(R.id.refreshBtn);

        forceOfflineChk.setOnCheckedChangeListener(this);
        clearCacheButton.setOnClickListener(this);
        refreshButton.setOnClickListener(this);

        pendingCount = (TextView) findViewById(R.id.pendingUpdatesCount);
        execPendingButton = (Button) findViewById(R.id.execPendingBtn);
        execPendingButton.setOnClickListener(this);
        clearPendingButton= (Button) findViewById(R.id.clearPendingBtn);
        clearPendingButton.setOnClickListener(this);

		super.onCreate(savedInstanceState);
	}

	@Override
	protected void updateOfflineDisplay(NuxeoNetworkStatus settings) {
		forceOfflineChk.setChecked(settings.isForceOffline());
		networkReachable.setChecked(settings.isNetworkReachable());
		serverReachable.setChecked(settings.isNuxeoServerReachable());
		execPendingButton.setEnabled(settings.canUseNetwork());
	}

	@Override
	protected void updateCacheInfoDisplay(ResponseCacheManager cacheManager, DeferredUpdateManager deferredUpdatetManager) {
		cacheEntriesCount.setText("Cache contains " + cacheManager.getEntryCount() + " entries");
		cacheSize.setText("Cache size : " + cacheManager.getSize() + "(bytes)" );
		pendingCount.setText(deferredUpdatetManager.getPendingRequestCount() + " pending updates");
	}

	@Override
	public void onCheckedChanged(CompoundButton view, boolean checked) {
		if (view == forceOfflineChk) {
			goOffline(checked);
		}
	}

	@Override
	public void onClick(View view) {
		if (view == clearCacheButton) {
			flushResponseCache();
		} else if (view == refreshButton) {
			resetNetworkStatusAndRefresh();
		} else if (view == execPendingButton) {
			executePendingUpdates();
		} else if (view == clearPendingButton) {
			flushDefferedUpdateManager();
		}
	}

}
