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
package org.nuxeo.ecm.automation.client.cache;

import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpConnector;
import org.nuxeo.ecm.automation.client.jaxrs.spi.Connector;

public class CacheAwareHttpAutomationClient extends HttpAutomationClient {

    protected InputStreamCacheManager cacheManager;

    public CacheAwareHttpAutomationClient(String url,
            InputStreamCacheManager cacheManager) {
        super(url);
        this.cacheManager = cacheManager;
    }

    @Override
    protected Connector newConnector() {
        HttpConnector con = new HttpConnector(http);
        if (cacheManager != null) {
            con.setCacheManager(cacheManager);
        }
        return con;
    }
}
