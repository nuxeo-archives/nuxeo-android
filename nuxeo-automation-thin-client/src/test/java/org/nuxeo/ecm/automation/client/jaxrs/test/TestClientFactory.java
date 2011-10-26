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
package org.nuxeo.ecm.automation.client.jaxrs.test;

import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;

public class TestClientFactory {

    public static final String TEST_SERVER = "http://android.demo.nuxeo.com/nuxeo/site/automation";

    public static final String TEST_USER = "droidUser";

    public static final String TEST_PASSWORD = "nuxeo4android";

    public static HttpAutomationClient getClient() {
        return new HttpAutomationClient(TEST_SERVER);
    }

    public static Session getSession() {
        return getClient().getSession(TEST_USER, TEST_PASSWORD);
    }

}
