package org.nuxeo.android.automation.fragments;

import org.nuxeo.android.automation.R;
import org.nuxeo.android.cache.blob.BlobStore;
import org.nuxeo.android.cache.blob.BlobStoreManager;
import org.nuxeo.android.fragments.AbstractNetworkSettingsFragment;
import org.nuxeo.android.network.NuxeoNetworkStatus;
import org.nuxeo.android.upload.FileUploader;
import org.nuxeo.ecm.automation.client.cache.DeferredUpdateManager;
import org.nuxeo.ecm.automation.client.cache.ResponseCacheManager;
import org.nuxeo.ecm.automation.client.cache.TransientStateManager;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class NetworkSettingsFragment extends AbstractNetworkSettingsFragment
		implements OnCheckedChangeListener, OnClickListener {

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

    private TextView transientstateCount;

    private Button cleartransientStateButton;

    private TextView iconCacheSize;

    private Button clearIconCache;

    private TextView blobCacheSize;

    private Button clearBlobCache;

    private TextView layoutCacheSize;

    private Button clearLayoutCache;

    private TextView PictureCacheSize;

    private Button clearPictureCache;

    private TextView allCacheSize;

    private Button clearAllCache;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
    	View v = inflater.inflate(R.layout.offline_screen, container, false);

        forceOfflineChk = (CheckBox) v.findViewById(R.id.forceOfflineChk);
        networkReachable = (CheckBox) v.findViewById(R.id.networkReachableChk);
        serverReachable = (CheckBox) v.findViewById(R.id.serverReachableChk);
        cacheEntriesCount = (TextView) v.findViewById(R.id.cacheEntriesCount);
        cacheSize = (TextView) v.findViewById(R.id.cacheSize);
        clearCacheButton = (Button) v.findViewById(R.id.clearCacheBtn);
        refreshButton = (Button) v.findViewById(R.id.refreshBtn);

        forceOfflineChk.setOnCheckedChangeListener(this);
        clearCacheButton.setOnClickListener(this);
        refreshButton.setOnClickListener(this);

        pendingCount = (TextView) v.findViewById(R.id.pendingUpdatesCount);
        execPendingButton = (Button) v.findViewById(R.id.execPendingBtn);
        execPendingButton.setOnClickListener(this);
        clearPendingButton = (Button) v.findViewById(R.id.clearPendingBtn);
        clearPendingButton.setOnClickListener(this);

        pendingUploadCount = (TextView) v.findViewById(R.id.pendingUploadCount);
        clearPendingUploadButton = (Button) v.findViewById(R.id.clearPendingUploadBtn);
        clearPendingUploadButton.setOnClickListener(this);

        transientstateCount = (TextView) v.findViewById(R.id.transientStateCount);
        cleartransientStateButton = (Button) v.findViewById(R.id.clearTransientStateBtn);
        cleartransientStateButton.setOnClickListener(this);

        iconCacheSize = (TextView) v.findViewById(R.id.iconCacheSize);
        clearIconCache = (Button) v.findViewById(R.id.clearIconCache);
        clearIconCache.setOnClickListener(this);

        blobCacheSize = (TextView) v.findViewById(R.id.blobCacheSize);
        clearBlobCache = (Button) v.findViewById(R.id.clearBlobCache);
        clearBlobCache.setOnClickListener(this);

        layoutCacheSize = (TextView) v.findViewById(R.id.layoutCacheSize);
        clearLayoutCache = (Button) v.findViewById(R.id.clearLayoutCache);
        clearLayoutCache.setOnClickListener(this);

        PictureCacheSize = (TextView)v.findViewById(R.id.pictureCacheSize);
        clearPictureCache = (Button)v.findViewById(R.id.clearPictureCache);
        clearPictureCache.setOnClickListener(this);
        
        allCacheSize = (TextView)v.findViewById(R.id.AllCacheSize);
        clearAllCache = (Button)v.findViewById(R.id.clearAllCache);
        clearAllCache.setOnClickListener(this);
        
        return v;
    }

    @Override
    protected void updateOfflineDisplay(NuxeoNetworkStatus settings) {
        forceOfflineChk.setChecked(settings.isForceOffline());
        networkReachable.setChecked(settings.isNetworkReachable());
        serverReachable.setChecked(settings.isNuxeoServerReachable());
        execPendingButton.setEnabled(settings.canUseNetwork());
    }

    @Override
    protected void updateCacheInfoDisplay(ResponseCacheManager cacheManager,
            DeferredUpdateManager deferredUpdatetManager,
            BlobStoreManager blobStoreManager, FileUploader fileUploader,
            TransientStateManager stateManager) {
        cacheEntriesCount.setText("Cache contains "
                + cacheManager.getEntryCount() + " entries");
        cacheSize.setText("Cache size : " + cacheManager.getSize() + "(b)");
        pendingCount.setText(deferredUpdatetManager.getPendingRequestCount()
                + " pending updates");

        pendingUploadCount.setText(fileUploader.getPendingUploadCount()
                + " pending upload");

        BlobStore iconStore = blobStoreManager.getBlobStore("icons");
        iconCacheSize.setText("Icons cache size : " + iconStore.getSize()
                + "(b)");

        BlobStore blobStore = blobStoreManager.getBlobStore("blobs");
        blobCacheSize.setText("Blobs cache size : " + blobStore.getSize()
                + "(b)");

        BlobStore layoutStore = blobStoreManager.getBlobStore("layouts");
        layoutCacheSize.setText("Layouts cache size : " + layoutStore.getSize()
                + "(b)");

        transientstateCount.setText("Transient objects :  "
                + stateManager.getEntryCount());
        

        BlobStore pictureStore = blobStoreManager.getBlobStore("pictures");
        PictureCacheSize.setText("Pictures cache size : " + pictureStore.getSize()
                + "(b)");
        
        long allCashSize = iconStore.getSize() + blobStore.getSize() + layoutStore.getSize() + pictureStore.getSize();
        allCacheSize.setText("All blobs caches : " + allCashSize + "(b)");

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
            getActivity().setResult(Activity.RESULT_OK);
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
            getActivity().setResult(Activity.RESULT_OK);
        } else if (view == clearBlobCache) {
            flushBlobStore("blobs");
        } else if (view == clearLayoutCache) {
            flushBlobStore("layouts");
        } else if (view == cleartransientStateButton) {
            flushTransientState();
        } else if (view == clearPictureCache) {
            flushBlobStore("pictures");
        } else if (view == clearAllCache) {
        	flushResponseCache();
            flushBlobStore("icons");
            flushBlobStore("blobs");
            flushBlobStore("layouts");
            flushBlobStore("pictures");
            getActivity().setResult(Activity.RESULT_OK);
        }
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return false;
    }
}
