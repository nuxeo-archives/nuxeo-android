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

package org.nuxeo.android.contentprovider;

import org.nuxeo.android.documentprovider.LazyDocumentsList;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NuxeoDynamicProvidersRegistry {

    protected final static Map<String, LazyDocumentsList> documentsLists = new ConcurrentHashMap<String, LazyDocumentsList>();

    public static void registerNamedProvider(String name,
            LazyDocumentsList docList) {
        documentsLists.put(name, docList);
    }

    public static void unregisterNamedProvider(String name) {
        documentsLists.remove(name);
    }

    public static LazyDocumentsList getNamedProvider(String name) {
        return documentsLists.get(name);
    }

}
