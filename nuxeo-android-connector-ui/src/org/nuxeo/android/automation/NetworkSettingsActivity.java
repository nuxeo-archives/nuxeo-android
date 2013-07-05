/*
 * (C) Copyright 2011 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 */

package org.nuxeo.android.automation;

import org.nuxeo.android.activities.AbstractNetworkSettingsActivity;
import org.nuxeo.android.cache.blob.BlobStore;
import org.nuxeo.android.cache.blob.BlobStoreManager;
import org.nuxeo.android.network.NuxeoNetworkStatus;
import org.nuxeo.android.upload.FileUploader;
import org.nuxeo.ecm.automation.client.cache.DeferredUpdateManager;
import org.nuxeo.ecm.automation.client.cache.ResponseCacheManager;
import org.nuxeo.ecm.automation.client.cache.TransientStateManager;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class NetworkSettingsActivity extends AbstractNetworkSettingsActivity
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
        clearPendingButton = (Button) findViewById(R.id.clearPendingBtn);
        clearPendingButton.setOnClickListener(this);

        pendingUploadCount = (TextView) findViewById(R.id.pendingUploadCount);
        clearPendingUploadButton = (Button) findViewById(R.id.clearPendingUploadBtn);
        clearPendingUploadButton.setOnClickListener(this);

        transientstateCount = (TextView) findViewById(R.id.transientStateCount);
        cleartransientStateButton = (Button) findViewById(R.id.clearTransientStateBtn);
        cleartransientStateButton.setOnClickListener(this);

        iconCacheSize = (TextView) findViewById(R.id.iconCacheSize);
        clearIconCache = (Button) findViewById(R.id.clearIconCache);
        clearIconCache.setOnClickListener(this);

        blobCacheSize = (TextView) findViewById(R.id.blobCacheSize);
        clearBlobCache = (Button) findViewById(R.id.clearBlobCache);
        clearBlobCache.setOnClickListener(this);

        layoutCacheSize = (TextView) findViewById(R.id.layoutCacheSize);
        clearLayoutCache = (Button) findViewById(R.id.clearLayoutCache);
        clearLayoutCache.setOnClickListener(this);

        PictureCacheSize = (TextView)findViewById(R.id.pictureCacheSize);
        clearPictureCache = (Button)findViewById(R.id.clearPictureCache);
        clearPictureCache.setOnClickListener(this);
        
        allCacheSize = (TextView)findViewById(R.id.AllCacheSize);
        clearAllCache = (Button)findViewById(R.id.clearAllCache);
        clearAllCache.setOnClickListener(this);
        
        

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
        
        long allCashSize = cacheManager.getSize() + iconStore.getSize() + blobStore.getSize() + layoutStore.getSize() + pictureStore.getSize();
        allCacheSize.setText("All caches : " + allCashSize + "(b)");

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
            setResult(RESULT_OK);
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
            setResult(RESULT_OK);
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
            setResult(RESULT_OK);
        }
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return false;
    }

}
