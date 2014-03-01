/*
 * (C) Copyright 2006-2011 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
package org.nuxeo.ecm.automation.client.jaxrs.adapters;

import org.nuxeo.ecm.automation.client.jaxrs.AdapterFactory;
import org.nuxeo.ecm.automation.client.jaxrs.Session;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class DocumentServiceFactory implements AdapterFactory<DocumentService> {

    @Override
    public Class<?> getAcceptType() {
        return Session.class;
    }

    @Override
    public Class<DocumentService> getAdapterType() {
        return DocumentService.class;
    }

    @Override
    public DocumentService getAdapter(Object toAdapt) {
        return new DocumentService((Session) toAdapt);
    }

}
