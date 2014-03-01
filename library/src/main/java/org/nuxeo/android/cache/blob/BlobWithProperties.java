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

package org.nuxeo.android.cache.blob;

import org.nuxeo.ecm.automation.client.jaxrs.model.FileBlob;

import java.io.File;
import java.util.Properties;

public class BlobWithProperties extends FileBlob {

    protected final Properties properties;

    public BlobWithProperties(File file, String filename, String mimeType,
            Properties properties) {
        super(file, filename, mimeType);
        this.properties = properties;
    }

    public BlobWithProperties(File file, Properties properties) {
        super(file);
        this.properties = properties;
    }

    public String getProperty(String name) {
        if (properties != null) {
            return properties.getProperty(name);
        } else {
            return null;
        }
    }

}
