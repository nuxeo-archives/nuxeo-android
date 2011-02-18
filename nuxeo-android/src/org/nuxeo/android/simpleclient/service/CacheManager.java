package org.nuxeo.android.simpleclient.service;

import java.io.InputStream;
import java.util.Date;

import org.nuxeo.ecm.automation.client.cache.CacheEntry;
import org.nuxeo.ecm.automation.client.cache.InputStreamCacheManager;

import com.smartnsoft.droid4me.bo.Business.InputAtom;
import com.smartnsoft.droid4me.cache.Persistence;

public class CacheManager implements InputStreamCacheManager {

	protected static final String DELIMITER = "__";

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
		CacheEntry entry = new CacheEntry(parts[0], parts[1], atom.inputStream);
		return entry;
	}

	@Override
	public CacheEntry getFromCache(String key) {
		if (key==null) {
			return null;
		}
		return unwrap(Persistence.getInstance(0).extractInputStream(key));
	}

}
