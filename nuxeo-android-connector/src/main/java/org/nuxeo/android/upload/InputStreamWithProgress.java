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

package org.nuxeo.android.upload;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.nuxeo.ecm.automation.client.android.UIAsyncCallback;

public class InputStreamWithProgress extends InputStream {

    protected final InputStream is;

    protected int length;

    protected int sent;

    protected UIAsyncCallback<Serializable> cb;

    public InputStreamWithProgress(InputStream is, int length,
            UIAsyncCallback<Serializable> cb) {
        this.is = is;
        this.length = length;
        this.cb = cb;
        this.sent = 0;
    }

    protected int notifyUpdate(int readSize) {
        if (cb != null) {
            sent += readSize;
            Float progress = 100 * ((float) sent / length);
            cb.notifyProgressChange(progress.intValue());
        }
        return readSize;
    }

    @Override
    public int available() throws IOException {
        return is.available();
    }

    @Override
    public void close() throws IOException {
        is.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        is.mark(readlimit);
    }

    @Override
    public boolean markSupported() {
        return is.markSupported();
    }

    @Override
    public synchronized void reset() throws IOException {
        is.reset();
    }

    @Override
    public long skip(long n) throws IOException {
        return is.skip(n);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return notifyUpdate(is.read(b, off, len));
    }

    @Override
    public int read(byte[] b) throws IOException {
        return notifyUpdate(is.read(b));
    }

    @Override
    public int read() throws IOException {
        return notifyUpdate(is.read());
    }

}
