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

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class Blobs extends ArrayList<Blob> implements OperationInput {

    private static final long serialVersionUID = 1L;

    public Blobs() {
    }

    public Blobs(int size) {
        super(size);
    }

    public Blobs(List<Blob> blobs) {
        super(blobs);
    }

    @Override
    public String getInputType() {
        return "bloblist";
    }

    @Override
    public String getInputRef() {
        return null;
    }

    @Override
    public boolean isBinary() {
        return true;
    }

}
