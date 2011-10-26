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
package org.nuxeo.android.simpleclient.listing;

import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

public class DocumentHelper {

    // For now Facets are not integrated into JSON export ...
    protected static final String[] folderishTypes = { "Domain",
            "WorkspaceRoot", "Workspace", "SectionRoot", "Section",
            "TemplateRoot", "PictureBook", "Folder", "OrderedFolder",
            "UserWorkspace" };

    protected static boolean isFolderish(Document doc) {
        for (String fType : folderishTypes) {
            if (fType.equals(doc.getType())) {
                return true;
            }
        }
        return false;
    }
}
