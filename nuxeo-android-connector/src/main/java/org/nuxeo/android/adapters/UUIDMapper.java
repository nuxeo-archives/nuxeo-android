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

package org.nuxeo.android.adapters;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

public class UUIDMapper {

    protected ConcurrentHashMap<String, Long> UUID2IDs = new ConcurrentHashMap<String, Long>();

    protected Long lastID = new Long(0);

    public Long getIdentifier(Document doc) {
        if (doc == null) {
            throw new UnsupportedOperationException(
                    "Can not not map a null Document");
        }
        if (doc.getId() == null) {
            throw new UnsupportedOperationException(
                    "Can not not map a Document with null UUID");
        }
        return getIdentifier(doc.getId());
    }

    private Long generate(String UUID) {
        lastID += 1;
        return lastID;
    }

    public Long getIdentifier(String UUID) {
        if (!UUID2IDs.containsKey(UUID)) { // avoid generate non continuous IDs
                                           // ...
            UUID2IDs.putIfAbsent(UUID, generate(UUID));
        }
        return UUID2IDs.get(UUID);
    }

    public String resolveIdentifier(Long id) {
        for (Entry<String, Long> entry : UUID2IDs.entrySet()) {
            if (entry.getValue().equals(id)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void bind(Documents docs) {
        for (Document doc : docs) {
            getIdentifier(doc);
        }
    }

    public void release(Documents docs) {
        for (Document doc : docs) {
            UUID2IDs.remove(doc.getId());
        }
    }
}
