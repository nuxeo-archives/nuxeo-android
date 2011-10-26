/*
 * (C) Copyright 2010-2011 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 */

package org.nuxeo.android.simpleclient;

import org.nuxeo.android.simpleclient.ui.TitleBarDiscarded;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.Window;

import com.smartnsoft.droid4me.app.SmartSplashScreenActivity;
import com.smartnsoft.droid4me.framework.LifeCycle;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectUnavailableException;

/**
 * The first activity displayed while the application is loading.
 * 
 * @author Nuxeo & Smart&Soft
 * @since 2011.02.17
 */
public final class NuxeoAndroidSplashScreenActivity extends
        SmartSplashScreenActivity implements
        LifeCycle.BusinessObjectsRetrievalAsynchronousPolicy, TitleBarDiscarded {

    private final static int MISSING_SD_CARD_DIALOG_ID = 0;

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == NuxeoAndroidSplashScreenActivity.MISSING_SD_CARD_DIALOG_ID) {
            return new AlertDialog.Builder(this).setIcon(
                    android.R.drawable.ic_dialog_alert).setTitle(
                    R.string.applicationName).setMessage(
                    R.string.NuxeoAndroidSplashScreen_dialogMessage_noSdCard).setPositiveButton(
                    android.R.string.ok, new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).create();
        }
        return super.onCreateDialog(id);
    }

    @Override
    protected boolean requiresExternalStorage() {
        return false;
    }

    @Override
    protected void onNoExternalStorage() {
        showDialog(NuxeoAndroidSplashScreenActivity.MISSING_SD_CARD_DIALOG_ID);
    }

    @Override
    protected Class<? extends Activity> getNextActivity() {
        return HomeActivity.class;
    }

    @Override
    protected void onRetrieveDisplayObjectsCustom() {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(LayoutInflater.from(this).inflate(
                R.layout.nuxeoandroid_splash_screen, null));
        setProgressBarIndeterminateVisibility(true);
    }

    @Override
    protected void onRetrieveBusinessObjectsCustom()
            throws BusinessObjectUnavailableException {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException exception) {
            if (log.isErrorEnabled()) {
                log.error(
                        "An interruption occurred while displaying the splash screen",
                        exception);
            }
        }
        markAsInitialized();
    }

}
