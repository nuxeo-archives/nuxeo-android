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

import org.nuxeo.ecm.automation.client.jaxrs.RemoteException;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class SampleThree {

    public static void main(String[] args) throws Exception {
        try {
            // create the client
            HttpAutomationClient client = new HttpAutomationClient(
                    "http://localhost:8080/nuxeo/site/automation");
            // get an Administrator session
            Session session = client.getSession("Administrator",
                    "Administrator");
            // get the /default-domain/workspaces document
            Document doc = (Document) session.newRequest("Document.Fetch").set(
                    "value", "/default-domain/workspaces").execute();
            System.out.println(doc);
            System.out.println(doc.getTitle());

            // create a new workspace (inside /default-domain/workspaces)
            Document myWs = (Document) session.newRequest("Document.Create").setInput(
                    doc).set("type", "Workspace").set("name", "MyWorkspace").set(
                    "properties",
                    "dc:title=My Workspace\ndc:description=This is my workspace").execute();
            System.out.println(myWs.getTitle());

            // now list the children in /default-domain/workspaces
            Documents docs = (Documents) session.newRequest(
                    "Document.GetChildren").setInput(doc).execute();
            System.out.println(docs);

            // list children titles
            for (Document d : docs) {
                System.out.println(d.getTitle() + " at " + d.getLastModified());
            }

            // shutdown the client
            client.shutdown();
        } catch (RemoteException e) {
            e.printStackTrace();
            System.out.println(e.getRemoteStackTrace());
        }
    }

}
