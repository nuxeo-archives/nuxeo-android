package org.nuxeo.android.automationsample;

import org.nuxeo.android.activities.AbstractNetworkSettingsActivity;
import org.nuxeo.android.network.NuxeoNetworkStatus;
import org.nuxeo.ecm.automation.client.cache.InputStreamCacheManager;

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
    private CheckBox forceOfflineChk;
    private CheckBox networkReachable;
    private CheckBox serverReachable;
    private Button clearCacheButton;
    private Button refreshButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.offline_screen);

        forceOfflineChk = (CheckBox) findViewById(R.id.forceOfflineChk);
        networkReachable = (CheckBox) findViewById(R.id.networkReachableChk);
        serverReachable = (CheckBox) findViewById(R.id.serverReachableChk);
        cacheEntriesCount = (TextView) findViewById(R.id.cacheEntriesCount);
        clearCacheButton = (Button) findViewById(R.id.clearCacheBtn);
        refreshButton = (Button) findViewById(R.id.refreshBtn);

        forceOfflineChk.setOnCheckedChangeListener(this);
        clearCacheButton.setOnClickListener(this);
        refreshButton.setOnClickListener(this);

		super.onCreate(savedInstanceState);
	}

	@Override
	protected void updateOfflineDisplay(NuxeoNetworkStatus settings) {
		forceOfflineChk.setChecked(settings.isForceOffline());
		networkReachable.setChecked(settings.isNetworkReachable());
		serverReachable.setChecked(settings.isNuxeoServerReachable());
	}

	@Override
	protected void updateCacheInfoDisplay(InputStreamCacheManager cacheManager) {
		cacheEntriesCount.setText("Cache contains " + cacheManager.getEntryCount());
	}

	@Override
	public void onCheckedChanged(CompoundButton view, boolean checked) {
		if (view == forceOfflineChk) {
			getNuxeoContext().getNetworkStatus().setForceOffline(checked);
			updateOfflineDisplay(getNuxeoContext().getNetworkStatus());
		}
	}

	@Override
	public void onClick(View view) {
		if (view == clearCacheButton) {
			getNuxeoContext().getCacheManager().clear();
			updateCacheInfoDisplay(getNuxeoContext().getCacheManager());
		} else if (view == refreshButton) {
			refreshAll();
		}
	}

}
