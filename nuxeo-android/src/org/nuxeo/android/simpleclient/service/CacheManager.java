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
package org.nuxeo.android.simpleclient.service;

import java.io.File;
import java.io.InputStream;
import java.util.Date;

import org.nuxeo.ecm.automation.client.cache.CacheEntry;
import org.nuxeo.ecm.automation.client.cache.InputStreamCacheManager;

import com.smartnsoft.droid4me.bo.Business.InputAtom;
import com.smartnsoft.droid4me.cache.Persistence;

public class CacheManager implements InputStreamCacheManager {

    protected static final String DELIMITER = "__";

    public void flushCache() {
        Persistence.getInstance(0).clear();
    }

    public long getSize() {
        long size = 0;
        String fsPath = Persistence.getInstance(0).getStorageDirectoryPath();
        File dataDir = new File(fsPath);
        for (File file : dataDir.listFiles()) {
            if (!file.isDirectory() && file.getName().endsWith(".db")) {
                size += file.length();
            }
        }
        return size;
    }

    @Override
    public InputStream addToCache(String key, CacheEntry entry) {
        return Persistence.getInstance(0).flushInputStream(key, wrap(entry)).inputStream;
    }

    protected InputAtom wrap(CacheEntry entry) {
        String ctx = entry.getCtype() + DELIMITER + entry.getDisp();
        return new InputAtom(new Date(), entry.getInputStream(), ctx);
    }

    protected CacheEntry unwrap(InputAtom atom) {
        if (atom == null) {
            return null;
        }
        if (atom.inputStream == null || atom.context == null) {
            return null;
        }

        String[] parts = ((String) atom.context).split(DELIMITER);
        String ctype = parts[0];
        if ("null".equals(ctype)) {
            ctype = null;
        }
        String disp = parts[1];
        if ("null".equals(disp)) {
            disp = null;
        }
        CacheEntry entry = new CacheEntry(ctype, disp, atom.inputStream);
        return entry;
    }

    @Override
    public CacheEntry getFromCache(String key) {
        if (key == null) {
            return null;
        }
        return unwrap(Persistence.getInstance(0).extractInputStream(key));
    }

}
