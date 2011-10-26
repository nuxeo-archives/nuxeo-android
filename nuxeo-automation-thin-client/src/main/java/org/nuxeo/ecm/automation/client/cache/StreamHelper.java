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

import java.io.IOException;
import java.io.InputStream;

public class StreamHelper {

    private static final int BUFFER_SIZE = 1024 * 64; // 64K

    private static final int MAX_BUFFER_SIZE = 1024 * 1024; // 64K

    private static final int MIN_BUFFER_SIZE = 1024 * 8; // 64K

    public static byte[] readBytes(InputStream in) throws IOException {
        byte[] buffer = createBuffer(in.available());
        int w = 0;
        try {
            int read = 0;
            int len;
            do {
                w += read;
                len = buffer.length - w;
                if (len <= 0) { // resize buffer
                    byte[] b = new byte[buffer.length + BUFFER_SIZE];
                    System.arraycopy(buffer, 0, b, 0, w);
                    buffer = b;
                    len = buffer.length - w;
                }
            } while ((read = in.read(buffer, w, len)) != -1);
        } finally {
            in.close();
        }
        if (buffer.length > w) { // compact buffer
            byte[] b = new byte[w];
            System.arraycopy(buffer, 0, b, 0, w);
            buffer = b;
        }
        return buffer;
    }

    private static byte[] createBuffer(int preferredSize) {
        if (preferredSize < 1) {
            preferredSize = BUFFER_SIZE;
        }
        if (preferredSize > MAX_BUFFER_SIZE) {
            preferredSize = MAX_BUFFER_SIZE;
        } else if (preferredSize < MIN_BUFFER_SIZE) {
            preferredSize = MIN_BUFFER_SIZE;
        }
        return new byte[preferredSize];
    }

}
