package org.nuxeo.android.fragments;

import org.nuxeo.android.broadcast.NuxeoBroadcastMessages;
import org.nuxeo.android.cache.blob.BlobStoreManager;
import org.nuxeo.android.network.NuxeoNetworkStatus;
import org.nuxeo.android.upload.FileUploader;
import org.nuxeo.ecm.automation.client.android.AndroidAutomationClient;
import org.nuxeo.ecm.automation.client.cache.DeferredUpdateManager;
import org.nuxeo.ecm.automation.client.cache.ResponseCacheManager;
import org.nuxeo.ecm.automation.client.cache.TransientStateManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;

public abstract class AbstractNetworkSettingsFragment extends BaseNuxeoFragment {

	public AbstractNetworkSettingsFragment() {
	}


    protected BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateOfflineDisplay(getNuxeoContext().getNetworkStatus());
                }
            });
        }
    };

    @Override
    public void onResume() {
    	getActivity().registerReceiver(receiver, new IntentFilter(
                NuxeoBroadcastMessages.NUXEO_SERVER_CONNECTIVITY_CHANGED));
        refreshAll();
        super.onResume();
    }

    @Override
    public void onPause() {
    	getActivity().unregisterReceiver(receiver);
        super.onPause();
    }

    protected void resetNetworkStatus(final Runnable afterReset) {
        Runnable tester = new Runnable() {
            @Override
            public void run() {
                getNuxeoContext().getNetworkStatus().reset();
                if (afterReset != null) {
                	getActivity().runOnUiThread(afterReset);
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
        updateCacheInfoDisplay(client.getResponseCacheManager(),
                client.getDeferredUpdatetManager(),
                client.getBlobStoreManager(), client.getFileUploader(),
                client.getTransientStateManager());
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
        if (dum.getPendingRequestCount() > 0) {
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

    protected abstract void updateCacheInfoDisplay(
            ResponseCacheManager cacheManager,
            DeferredUpdateManager deferredUpdateManager,
            BlobStoreManager blobStoreManager, FileUploader fileUploader,
            TransientStateManager stateManager);

    protected void flushResponseCache() {
        getAutomationClient().getResponseCacheManager().clear();
        fireUpdateCacheInfoDisplay();
    }

    protected void flushDeferredUpdateManager() {
        getAutomationClient().getDeferredUpdatetManager().purgePendingUpdates();
        getAutomationClient().getTransientStateManager().flushTransientState();
        fireUpdateCacheInfoDisplay();
    }

    protected void flushPendingUploads() {
        getAutomationClient().getFileUploader().purgePendingUploads();
        fireUpdateCacheInfoDisplay();
    }

    protected void flushBlobStore(String name) {
        getAutomationClient().getBlobStoreManager().getBlobStore(name).clear();
        fireUpdateCacheInfoDisplay();
    }

    protected void flushTransientState() {
        getAutomationClient().getTransientStateManager().flushTransientState();
        fireUpdateCacheInfoDisplay();
    }

    protected void goOffline(boolean offline) {
        getNuxeoContext().getNetworkStatus().setForceOffline(offline);
        updateOfflineDisplay(getNuxeoContext().getNetworkStatus());
    }

}
