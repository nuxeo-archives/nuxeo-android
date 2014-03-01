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
package org.nuxeo.ecm.automation.client.jaxrs.model;

import java.io.Serializable;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class DocRef implements OperationInput, Serializable {

    private static final long serialVersionUID = 1L;

    protected final String ref;

    public static DocRef newRef(String ref) {
        if (ref.startsWith("/")) {
            return new PathRef(ref);
        } else {
            return new IdRef(ref);
        }
    }

    public DocRef(String ref) {
        this.ref = ref;
    }

    @Override
    public String getInputType() {
        return "document";
    }

    @Override
    public String getInputRef() {
        return "doc:" + ref;
    }

    @Override
    public boolean isBinary() {
        return false;
    }

    @Override
    public String toString() {
        return ref;
    }

}
