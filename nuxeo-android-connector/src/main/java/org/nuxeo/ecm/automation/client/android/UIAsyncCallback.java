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

package org.nuxeo.ecm.automation.client.android;

import java.io.Serializable;

import org.nuxeo.ecm.automation.client.jaxrs.AsyncCallbackWithProgress;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public abstract class UIAsyncCallback<T extends Serializable> extends Handler
        implements AsyncCallbackWithProgress<T> {

    protected static final int START = 1;

    protected static final int PROGRESS = 2;

    protected static final String PROGRESS_KEY = "progress";

    protected static final String EXECID_KEY = "executionId";

    protected static final String ERROR_KEY = "error";

    protected static final String DATA_KEY = "data";

    protected static final int END = 3;

    protected static final int CANCEL = 4;

    protected static final int ERROR = 5;

    @Override
    public void notifyProgressChange(int progress) {
        Log.i(UIAsyncCallback.class.getSimpleName(), "Update : " + progress
                + "%");
        Message msg = new Message();
        msg.what = PROGRESS;
        Bundle bundle = new Bundle();
        bundle.putInt(PROGRESS_KEY, progress);
        msg.setData(bundle);
        this.sendMessage(msg);
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == PROGRESS) {
            onProgressUpdate(msg.getData().getInt(PROGRESS_KEY));
        } else if (msg.what == START) {
            onStart();
        } else if (msg.what == ERROR) {
            onErrorUI(msg.getData().getString(EXECID_KEY),
                    (Throwable) msg.getData().getSerializable(ERROR_KEY));
        } else if (msg.what == END) {
            onSuccessUI(msg.getData().getString(EXECID_KEY),
                    (T) msg.getData().getSerializable(DATA_KEY));
        }
    }

    @Override
    public void notifyStart() {
        Message msg = new Message();
        msg.what = START;
        this.sendMessage(msg);
    }

    public void notifyError(String message) {
        Message msg = new Message();
        msg.what = ERROR;
        Bundle bundle = new Bundle();
        bundle.putString("error", message);
        msg.setData(bundle);
        this.sendMessage(msg);
    }

    @Override
    public void onError(String executionId, Throwable e) {
        Message msg = new Message();
        msg.what = ERROR;
        Bundle bundle = new Bundle();
        bundle.putString(EXECID_KEY, executionId);
        bundle.putSerializable(ERROR_KEY, e);
        Log.e(this.getClass().getSimpleName(), "Error catched by handler", e);
        msg.setData(bundle);
        this.sendMessage(msg);
    }

    @Override
    public void onSuccess(String executionId, T data) {
        Message msg = new Message();
        msg.what = END;
        Bundle bundle = new Bundle();
        bundle.putString(EXECID_KEY, executionId);
        bundle.putSerializable(DATA_KEY, data);
        msg.setData(bundle);
        this.sendMessage(msg);
    }

}
