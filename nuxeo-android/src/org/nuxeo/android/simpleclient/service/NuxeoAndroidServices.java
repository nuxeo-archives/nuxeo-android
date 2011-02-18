/*
 * (C) Copyright 2010-2011 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 */

package org.nuxeo.android.simpleclient.service;

import org.nuxeo.android.simpleclient.Constants;
import org.nuxeo.android.simpleclient.DocumentViewActivity;
import org.nuxeo.android.simpleclient.SettingsActivity;
import org.nuxeo.ecm.automation.client.cache.CacheAwareHttpAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectUnavailableException;
import com.smartnsoft.droid4me.ws.WebServiceCaller;

/**
 * A single point of access to the web services.
 *
 * @author Nuxeo & Smart&Soft
 * @since 2011.02.17
 */
public final class NuxeoAndroidServices extends WebServiceCaller implements
        OnSharedPreferenceChangeListener {

    private static volatile NuxeoAndroidServices instance;

    // We accept the "out-of-order writes" case
    public static NuxeoAndroidServices getInstance() {
        if (instance == null) {
            synchronized (NuxeoAndroidServices.class) {
                if (instance == null) {
                    instance = new NuxeoAndroidServices();
                }
            }
        }
        return instance;
    }

    private NuxeoAndroidServices() {
    }

    public static void init(Context appContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(appContext);
        NuxeoAndroidServices me = getInstance();
        me.initOnPrefs(prefs);
        prefs.registerOnSharedPreferenceChangeListener(me);
    }

    protected HttpAutomationClient client;

    protected Session session;

    // XXX TODO Not used for now
    protected int pageSize = 30;

    protected int cacheRetentionInSecondes = 5 * 60;

    protected String userLogin;

    protected String password;

    protected void initOnPrefs(SharedPreferences prefs) {

        if (client != null) {
            release();
        }
        String serverUrl = prefs.getString(SettingsActivity.PREF_SERVER_URL, "")
                + SettingsActivity.PREF_SERVER_URL_SUFFIX;
        userLogin = prefs.getString(SettingsActivity.PREF_LOGIN, "");
        password = prefs.getString(SettingsActivity.PREF_PASSWORD, "");
        client = new CacheAwareHttpAutomationClient(serverUrl,
                new CacheManager());
        // client = new CacheAwareHttpAutomationClient(serverUrl, null);
    }

    protected Session getSession() {
        if (session == null) {
            session = client.getSession(userLogin, password);
        } else {
            if (session.isOffline()) {
                // try to reconnect
                session = client.getSession(userLogin, password);
            }
        }
        return session;
    }

    public void release() {
        if (client != null) {
            client.shutdown();
            client = null;
            session = null;
        }
    }

    public Documents getMyDocuments(boolean refresh)
            throws BusinessObjectUnavailableException {
        String query = "SELECT * FROM Document WHERE dc:contributors = '"
                + userLogin
                + "' AND ecm:mixinType !='Folderish' AND ecm:mixinType != 'HiddenInNavigation' "
                + " AND ecm:isCheckedInVersion = 0 AND ecm:isProxy = 0 "
                + " AND ecm:currentLifeCycleState != 'deleted'"
                + " ORDER BY dc:modified desc";
        return queryDocuments(query, refresh, true);
    }

    public Documents getLastPublishedDocuments(boolean refresh)
            throws BusinessObjectUnavailableException {
        String query = "SELECT * FROM Document WHERE "
                + " ecm:mixinType !='Folderish' "
                + " AND ecm:mixinType != 'HiddenInNavigation' "
                + " AND ecm:isCheckedInVersion = 0 AND ecm:isProxy = 1"
                + " ORDER BY dc:modified desc";
        return queryDocuments(query, refresh, true);
    }

    public Documents getAllDocuments(boolean refresh)
            throws BusinessObjectUnavailableException {
        String query = "SELECT * FROM Document where "
                + "ecm:mixinType != 'HiddenInNavigation' "
                + "AND dc:title!='' ";
        return queryDocuments(query, refresh, true);
    }

    public Documents queryFullText(String pattern, boolean refresh)
            throws BusinessObjectUnavailableException {
        String query = "SELECT * FROM Document WHERE ecm:fulltext LIKE '"
                + pattern + "' " + " AND ecm:mixinType !='HiddenInNavigation' "
                + " AND ecm:isCheckedInVersion = 0 "
                + " AND ecm:currentLifeCycleState != 'deleted'";
        return queryDocuments(query, refresh, true);
    }

    public Documents getMyWorklistContent()
            throws BusinessObjectUnavailableException {
        // XXX need to add an Server Side operation for that
        return new Documents();
    }

    public Documents getSavedSerach(String savedQueryName)
            throws BusinessObjectUnavailableException {
        // XXX need to add an Server Side operation for that
        return new Documents();
    }

    public Documents queryDocuments(String nxql, boolean refresh,
            boolean allowCaching) throws BusinessObjectUnavailableException {
        Documents docs;
        try {
            docs = (Documents) getSession().newRequest("Document.Query").set(
                    "query", nxql).setHeader("X-NXDocumentProperties",
                    "dublincore,common").execute(refresh, allowCaching);
        } catch (Exception e) {
            throw new BusinessObjectUnavailableException(e);
        }
        return docs;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
        // XXX should filter on keys
        initOnPrefs(sharedPreferences);
    }

    @Override
    protected String getUrlEncoding() {
        return Constants.WEBSERVICES_HTML_ENCODING;
    }

    public Document getDocument(int sourceActivity, String docId)
            throws BusinessObjectUnavailableException {
        Documents documents = getAllDocuments(false);

        if (sourceActivity == DocumentViewActivity.MY_DOCUMENT)
            for (Document document : documents) {
                if (docId.equals(document.getId()))
                    return document;
            }
        return null;
    }

}
