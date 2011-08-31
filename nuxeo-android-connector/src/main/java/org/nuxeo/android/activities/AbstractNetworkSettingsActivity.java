package org.nuxeo.android.activities;

import org.nuxeo.android.broadcast.NuxeoBroadcastMessages;
import org.nuxeo.android.network.NuxeoNetworkStatus;
import org.nuxeo.ecm.automation.client.cache.ResponseCacheManager;
import org.nuxeo.ecm.automation.client.pending.DeferredUpdatetManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public abstract class AbstractNetworkSettingsActivity extends BaseNuxeoActivity {

    protected BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context ctx, Intent intent) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					updateOfflineDisplay(getNuxeoContext().getNetworkStatus());
				}
			});
		}
    };

	@Override
	protected void onResume() {
		registerReceiver(receiver, new IntentFilter(NuxeoBroadcastMessages.NUXEO_SERVER_CONNECTIVITY_CHANGED));
		refreshAll();
		super.onResume();
	}

	@Override
	protected void onPause() {
		unregisterReceiver(receiver);
		super.onPause();
	}

	public void reset(final Runnable afterReset) {
		Runnable tester = new Runnable() {
			@Override
			public void run() {
				getNuxeoContext().getNetworkStatus().reset();
				if (afterReset!=null) {
					runOnUiThread(afterReset);
				}
			}
		};
		new Thread(tester).start();
	}

	protected void refreshAll() {
		updateOfflineDisplay(getNuxeoContext().getNetworkStatus());
		updateCacheInfoDisplay(getNuxeoContext().getCacheManager(), getNuxeoContext().getDeferredUpdatetManager());
	}

	protected void resetAndRefresh() {
		reset(new Runnable() {
			@Override
			public void run() {
				refreshAll();
			}
		});
	}

	protected abstract void updateOfflineDisplay(NuxeoNetworkStatus settings);

	protected abstract void updateCacheInfoDisplay(ResponseCacheManager cacheManager, DeferredUpdatetManager deferredUpdatetManager);

	protected void flushCache() {
		getNuxeoContext().getCacheManager().clear();
		updateCacheInfoDisplay(getNuxeoContext().getCacheManager(), getNuxeoContext().getDeferredUpdatetManager());
	}

	protected void goOffline(boolean offline) {
		getNuxeoContext().getNetworkStatus().setForceOffline(offline);
		updateOfflineDisplay(getNuxeoContext().getNetworkStatus());
	}
}
