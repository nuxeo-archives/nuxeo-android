/*
 * (C) Copyright 2006-2008 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     bstefanescu
 */
package org.nuxeo.ecm.automation.client.jaxrs.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.nuxeo.ecm.automation.client.cache.CacheAwareHttpAutomationClient;
import org.nuxeo.ecm.automation.client.cache.CacheEntry;
import org.nuxeo.ecm.automation.client.cache.InputStreamCacheManager;
import org.nuxeo.ecm.automation.client.jaxrs.RemoteException;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class SampleQuery {


    private static class DummyCacheManager implements InputStreamCacheManager {

        Map<String, CacheEntry> cache = new HashMap<String, CacheEntry>();

        @Override
        public InputStream addToCache(String key, CacheEntry entry) {

            File file = new File("/tmp/cache" + key);
            try {
                OutputStream out = new FileOutputStream(file);
                InputStream in = entry.getInputStream();

                byte[] buffer = new byte[255];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                cache.put(key, entry);
                out.close();
                return new FileInputStream(file);
            }
            catch (Exception e) {
                //NOP
                return entry.getInputStream();
            }
        }

        @Override
        public CacheEntry getFromCache(String key) {

            CacheEntry entry = cache.get(key);
            if (entry!=null) {
                File file = new File("/tmp/cache" + key);
                InputStream is;
                try {
                    is = new FileInputStream(file);
                    entry.setInputStream(is);
                } catch (FileNotFoundException e) {
                }
                return entry;
            }
            return null;
        }

    }

    public static void main(String[] args) throws Exception {
        try {

            HttpAutomationClient client = new CacheAwareHttpAutomationClient("http://10.213.2.104:8080/nuxeo/site/automation", new DummyCacheManager());
            Session session = client.getSession("Administrator", "Administrator");

            Documents docs = (Documents) session.newRequest("Document.Query").set("query", "SELECT * FROM Document").execute();

            for (Document doc : docs) {
                System.out.println(doc.getId());
            }

            System.out.println("from cache");

            docs = (Documents) session.newRequest("Document.Query").set("query", "SELECT * FROM Document").execute();
            for (Document doc : docs) {
                System.out.println(doc.getId());
            }

            System.out.println("force refresh");

            docs = (Documents) session.newRequest("Document.Query").set("query", "SELECT * FROM Document").execute(true,true);
            for (Document doc : docs) {
                System.out.println(doc.getId());
            }

            System.out.println("no refresh");

            docs = (Documents) session.newRequest("Document.Query").set("query", "SELECT * FROM Document").execute();
            for (Document doc : docs) {
                System.out.println(doc.getId());
            }


//            HttpAutomationClient client = new HttpAutomationClient(
//                    "http://localhost:8080/nuxeo/site/automation");
//            Session session = client.getSession("Administrator",
//                    "Administrator");
//            DocumentService rs = session.getAdapter(DocumentService.class);
//            Documents docs = rs.query("SELECT * from Workspace");
//            System.out.println(docs);
//            for (Document d : docs) {
//                System.out.println(d.getTitle() + " at " + d.getLastModified());
//            }
            client.shutdown();
        } catch (RemoteException e) {
            e.printStackTrace();
            System.out.println(e.getRemoteStackTrace());
        }
    }

}
