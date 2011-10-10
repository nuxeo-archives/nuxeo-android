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

package org.nuxeo.android.cache.blob;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.nuxeo.ecm.automation.client.android.AndroidResponseCacheManager;

import android.content.Context;
import android.util.Log;

public class BlobStoreManager {

	protected final File rootDir;

	protected final Map<String, BlobStore> stores = new HashMap<String, BlobStore>();

	public BlobStoreManager(Context context) {

		File dir = context.getExternalCacheDir();
		if (dir==null) {
			Log.w(AndroidResponseCacheManager.class.getSimpleName(), "No external directory accessible, using main storage");
			dir = context.getFilesDir();
		}
		rootDir =dir;
	}

	public BlobStore getBlobStore(String dir) {
		BlobStore store = stores.get(dir);
		if (store==null) {
			File storageDir = getTargetDir(dir);
			store = new BlobStore(storageDir);
			stores.put(dir, store);
		}
		return store;
	}

	protected File getTargetDir(String dir) {
		File targetDir = new File(rootDir,dir);
		if (!targetDir.exists()) {
			targetDir.mkdir();
		}
		return targetDir;
	}

	public Map<String, BlobStore> getStores() {
		return stores;
	}

}
