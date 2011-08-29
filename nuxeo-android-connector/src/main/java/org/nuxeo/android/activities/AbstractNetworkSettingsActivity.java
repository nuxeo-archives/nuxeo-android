package org.nuxeo.android.activities;

import org.nuxeo.android.network.NuxeoNetworkStatus;
import org.nuxeo.ecm.automation.client.cache.ResponseCacheManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public abstract class AbstractNetworkSettingsActivity extends BaseNuxeoActivity {

    protected Handler handler;

	@Override
	public void finish() {
		getNuxeoContext().getNetworkStatus().unregisterHandler(handler);
		super.finish();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		handler = new Handler() {
			@Override
			public void handleMessage (Message msg) {
				updateOfflineDisplay(getNuxeoContext().getNetworkStatus());
			}
		};

		getNuxeoContext().getNetworkStatus().registerHandler(handler);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshAll();
	}

	public void reset() {
		Runnable tester = new Runnable() {
			@Override
			public void run() {
				getNuxeoContext().getNetworkStatus().reset();
				updateOfflineDisplay(getNuxeoContext().getNetworkStatus());
			}
		};
		new Thread(tester).start();
	}

	protected void refreshAll() {
		updateOfflineDisplay(getNuxeoContext().getNetworkStatus());
		updateCacheInfoDisplay(getNuxeoContext().getCacheManager());
	}

	protected abstract void updateOfflineDisplay(NuxeoNetworkStatus settings);

	protected abstract void updateCacheInfoDisplay(ResponseCacheManager cacheManager);


}
