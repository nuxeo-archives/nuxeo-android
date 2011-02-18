package org.nuxeo.android.simpleclient;

import org.nuxeo.ecm.automation.client.cache.CacheAwareHttpAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectUnavailableException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

public class NuxeoServiceProvider implements OnSharedPreferenceChangeListener {

	protected static NuxeoServiceProvider instance;

	public static NuxeoServiceProvider instance(Context appContext) {
		if (instance==null) {
			instance = new NuxeoServiceProvider(appContext);
		}
		return instance;
	}

	protected HttpAutomationClient client;

	protected Session session;

	// XXX TODO Not used for now
	protected int pageSize = 30;
	protected int cacheRetentionInSecondes = 5 * 60;

	protected String userLogin;

	public NuxeoServiceProvider(Context appContext) {

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(appContext);
		prefs.registerOnSharedPreferenceChangeListener(this);
		initOnPrefs(prefs);
	}

	public void initOnPrefs(SharedPreferences prefs) {

		if (client!=null) {
			release();
		}

		String serverUrl = prefs.getString(SettingsActivity.PREF_SERVER_URL, "") + SettingsActivity.PREF_SERVER_URL_SUFFIX;
		userLogin = prefs.getString(SettingsActivity.PREF_LOGIN,"");
        String password = prefs.getString(SettingsActivity.PREF_PASSWORD, "");

        client = new CacheAwareHttpAutomationClient(serverUrl, null); // XXX plug on a real cache provider
        session = client.getSession(userLogin, password);

	}

	public void release() {
		if (client!=null) {
			client.shutdown();
			client=null;
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key) {
		// XXX should filter on keys
		initOnPrefs(sharedPreferences);
	}

	public Documents getMyDocuments() throws BusinessObjectUnavailableException {
		String query = "SELECT * FROM Document WHERE dc:contributors = '" + userLogin +
					"'? AND ecm:mixinType !='Folderish' AND ecm:mixinType != 'HiddenInNavigation' " +
					" AND ecm:isCheckedInVersion = 0 AND ecm:isProxy = 0 " +
					" AND ecm:currentLifeCycleState != 'deleted'" +
					" ORDER BY dc:modified desc";
		return queryDocuments(query);
	}

	public Documents getLastPublishedDocuments() throws BusinessObjectUnavailableException {
		String query = "SELECT * FROM Document WHERE " +
		               " ecm:mixinType !='Folderish' " +
		                " AND ecm:mixinType != 'HiddenInNavigation' " +
		                " AND ecm:isCheckedInVersion = 0 AND ecm:isProxy = 1" +
					    " ORDER BY dc:modified desc";
		return queryDocuments(query);
	}

	public Documents getAllDocuments() throws BusinessObjectUnavailableException {
		String query = "SELECT * FROM Document where " +
        				"ecm:mixinType != 'HiddenInNavigation' " +
        				"AND dc:title!='' ";
		return queryDocuments(query);
	}

	public Documents queryFullText(String pattern) throws BusinessObjectUnavailableException {
		String query = "SELECT * FROM Document WHERE ecm:fulltext LIKE '" + pattern + "' " +
		               " AND ecm:mixinType !='HiddenInNavigation' " +
		                " AND ecm:isCheckedInVersion = 0 " +
		                " AND ecm:currentLifeCycleState != 'deleted'";
		return queryDocuments(query);
	}

	public Documents getMyWorklistContent() throws BusinessObjectUnavailableException {
		// XXX need to add an Server Side operation for that
		return new Documents();
	}

	public Documents getSavedSerach(String savedQueryName) throws BusinessObjectUnavailableException {
		// XXX need to add an Server Side operation for that
		return new Documents();
	}


	public Documents queryDocuments(String nxql) throws BusinessObjectUnavailableException {
		Documents docs;
        try {
            docs = (Documents) session.newRequest("Document.Query").set(
                    "query", nxql)
                    .setHeader("X-NXDocumentProperties", "dublincore,common")
                    .execute();
        } catch (Exception e) {
            throw new BusinessObjectUnavailableException(e);
        }
        return docs;
	}
}
