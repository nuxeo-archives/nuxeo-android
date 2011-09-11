package org.nuxeo.android.activities;

import org.nuxeo.android.broadcast.NuxeoBroadcastMessages;
import org.nuxeo.android.cache.blob.BlobStoreManager;
import org.nuxeo.android.network.NuxeoNetworkStatus;
import org.nuxeo.ecm.automation.client.android.AndroidAutomationClient;
import org.nuxeo.ecm.automation.client.cache.DeferredUpdateManager;
import org.nuxeo.ecm.automation.client.cache.ResponseCacheManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;

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

	protected void resetNetworkStatus(final Runnable afterReset) {
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
		fireUpdateCacheInfoDisplay();
	}

	protected void fireUpdateCacheInfoDisplay() {
		AndroidAutomationClient client = getAutomationClient();
		updateCacheInfoDisplay(client.getResponseCacheManager(), client.getDeferredUpdatetManager(), client.getBlobStoreManager());
	}

	protected void resetNetworkStatusAndRefresh() {
		resetNetworkStatus(new Runnable() {
			@Override
			public void run() {
				refreshAll();
			}
		});
	}

	protected void executePendingUpdates() {
		DeferredUpdateManager dum = getAutomationClient().getDeferredUpdatetManager();
		if (dum.getPendingRequestCount()>0) {
			dum.executePendingRequests(getNuxeoSession(), new Handler() {
				@Override
				public void handleMessage(Message msg) {
					refreshAll();
					super.handleMessage(msg);
				}
			});
		}
	}

	protected abstract void updateOfflineDisplay(NuxeoNetworkStatus settings);

	protected abstract void updateCacheInfoDisplay(ResponseCacheManager cacheManager, DeferredUpdateManager deferredUpdateManager, BlobStoreManager blobStoreManager);

	protected void flushResponseCache() {
		getAutomationClient().getResponseCacheManager().clear();
		fireUpdateCacheInfoDisplay();
	}

	protected void flushDefferedUpdateManager() {
		getAutomationClient().getDeferredUpdatetManager().purgePendingUpdates();
		fireUpdateCacheInfoDisplay();
	}

	protected void flushBlobStore(String name) {
		getAutomationClient().getBlobStoreManager().getBlobStore(name).clear();
		fireUpdateCacheInfoDisplay();
	}

	protected void goOffline(boolean offline) {
		getNuxeoContext().getNetworkStatus().setForceOffline(offline);
		updateOfflineDisplay(getNuxeoContext().getNetworkStatus());
	}
}
