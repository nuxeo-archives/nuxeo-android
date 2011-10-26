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

import java.util.List;

import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

public interface TransientStateManager {

    void storeDocumentState(Document doc, OperationType opType);

    List<DocumentDeltaSet> getDeltaSets(List<String> ids, String listName);

    Documents mergeTransientState(Documents docs, boolean add, String listName);

    void flushTransientState(String uid);

    void flushTransientState();

    void markAsConflict(String uid);

    void markAsResolved(String uid);

    long getEntryCount();

}
