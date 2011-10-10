package org.nuxeo.android.automationsample;

import org.nuxeo.android.activities.AbstractNetworkSettingsActivity;
import org.nuxeo.android.cache.blob.BlobStore;
import org.nuxeo.android.cache.blob.BlobStoreManager;
import org.nuxeo.android.network.NuxeoNetworkStatus;
import org.nuxeo.android.upload.FileUploader;
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
    private TextView pendingUploadCount;
    private Button clearPendingUploadButton;
    private TextView iconCacheSize;
    private Button clearIconCache;
    private TextView blobCacheSize;
    private Button clearBlobCache;
    private TextView layoutCacheSize;
    private Button clearLayoutCache;

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

        pendingUploadCount = (TextView) findViewById(R.id.pendingUploadCount);
        clearPendingUploadButton= (Button) findViewById(R.id.clearPendingUploadBtn);
        clearPendingUploadButton.setOnClickListener(this);

        iconCacheSize = (TextView) findViewById(R.id.iconCacheSize);
        clearIconCache = (Button) findViewById(R.id.clearIconCache);
        clearIconCache.setOnClickListener(this);

        blobCacheSize = (TextView) findViewById(R.id.blobCacheSize);
        clearBlobCache = (Button) findViewById(R.id.clearBlobCache);
        clearBlobCache.setOnClickListener(this);

        layoutCacheSize = (TextView) findViewById(R.id.layoutCacheSize);
        clearLayoutCache = (Button) findViewById(R.id.clearLayoutCache);
        clearLayoutCache.setOnClickListener(this);

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
	protected void updateCacheInfoDisplay(ResponseCacheManager cacheManager, DeferredUpdateManager deferredUpdatetManager, BlobStoreManager blobStoreManager, FileUploader fileUploader) {
		cacheEntriesCount.setText("Cache contains " + cacheManager.getEntryCount() + " entries");
		cacheSize.setText("Cache size : " + cacheManager.getSize() + "(b)" );
		pendingCount.setText(deferredUpdatetManager.getPendingRequestCount() + " pending updates");

		pendingUploadCount.setText(fileUploader.getPendingUploadCount() + " pending upload");

		BlobStore iconStore = blobStoreManager.getBlobStore("icons");
		iconCacheSize.setText("Icons cache size : " + iconStore.getSize() + "(b)");

		BlobStore blobStore = blobStoreManager.getBlobStore("blobs");
		blobCacheSize.setText("Blobs cache size : " + blobStore.getSize() + "(b)");

		BlobStore layoutStore = blobStoreManager.getBlobStore("layouts");
		layoutCacheSize.setText("Layouts cache size : " + layoutStore.getSize() + "(b)");

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
			flushDeferredUpdateManager();
		} else if (view == clearPendingUploadButton) {
			flushPendingUploads();
		} else if (view == clearIconCache) {
			flushBlobStore("icons");
		} else if (view == clearBlobCache) {
			flushBlobStore("blobs");
		} else if (view == clearLayoutCache) {
			flushBlobStore("layouts");
		}
	}

	@Override
	protected boolean requireAsyncDataRetrieval() {
		return false;
	}

}
