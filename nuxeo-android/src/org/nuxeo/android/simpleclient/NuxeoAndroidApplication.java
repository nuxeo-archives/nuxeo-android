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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;

import com.smartnsoft.droid4me.app.ActivityController;
import com.smartnsoft.droid4me.cache.Persistence;
import com.smartnsoft.droid4me.ext.app.SmartApplication;
import com.smartnsoft.droid4me.ext.cache.DbPersistence;
import com.smartnsoft.droid4me.ext.download.AdvancedImageDownloader;
import com.smartnsoft.droid4me.ext.download.ImageDownloader;

/**
 * The entry point of the application.
 *
 * @author Nuxeo & Smart&Soft
 * @since 2011.02.17
 */
public final class NuxeoAndroidApplication extends SmartApplication {

    public static class CacheInstructions extends
            AdvancedImageDownloader.AdvancedAbstractInstructions {

        @Override
        public InputStream getInputStream(String imageUid, Object imageSpecs,
                String url,
                ImageDownloader.InputStreamDownloadInstructor downloadInstructor)
                throws IOException {
            return Persistence.getInstance(1).getRawInputStream(url);
        }

        @Override
        public InputStream onInputStreamDownloaded(String imageUid,
                Object imageSpecs, String url, InputStream inputStream) {
            return Persistence.getInstance(1).cacheInputStream(url, inputStream);
        }

    }

    public final static ImageDownloader.Instructions CACHE_IMAGE_INSTRUCTIONS = new NuxeoAndroidApplication.CacheInstructions();

    @Override
    protected int getLogLevel() {
        return Constants.LOG_LEVEL;
    }

    @Override
    protected SmartApplication.I18N getI18N() {
        return new SmartApplication.I18N(getText(R.string.problem),
                getText(R.string.unavailableItem),
                getText(R.string.unavailableService),
                getText(R.string.connectivityProblem),
                getText(R.string.unhandledProblem));
    }

    @Override
    protected SmartApplication.I18NExt getI18NExt() {
        return new SmartApplication.I18NExt(
                getString(R.string.applicationName),
                getText(R.string.dialogButton_unhandledProblem),
                getString(R.string.progressDialogMessage_unhandledProblem));
    }

    @Override
    protected String getLogReportRecipient() {
        return Constants.REPORT_LOG_RECIPIENT_EMAIL;
    }

    @Override
    public void onCreateCustom() {
        super.onCreateCustom();

        // We initialize the persistence
        final String directoryName = getPackageManager().getApplicationLabel(
                getApplicationInfo()).toString();
        final File contentsDirectory = new File(
                Environment.getExternalStorageDirectory(), directoryName);
        Persistence.CACHE_DIRECTORY_PATHS = new String[] {
                contentsDirectory.getAbsolutePath(),
                contentsDirectory.getAbsolutePath() };
        DbPersistence.FILE_NAMES = new String[] {
                DbPersistence.DEFAULT_FILE_NAME,
                DbPersistence.DEFAULT_FILE_NAME };
        DbPersistence.TABLE_NAMES = new String[] { "data", "images" };
        Persistence.CACHES_COUNT = 2;
        Persistence.IMPLEMENTATION_FQN = DbPersistence.class.getName();

        // We set the ImageDownloader instances
        ImageDownloader.IMPLEMENTATION_FQN = AdvancedImageDownloader.class.getName();
        ImageDownloader.INSTANCES_COUNT = 1;
        ImageDownloader.MAX_MEMORY_IN_BYTES = new long[] { 3 * 1024 * 1024 };
        ImageDownloader.LOW_LEVEL_MEMORY_WATER_MARK_IN_BYTES = new long[] { 1 * 1024 * 1024 };
        ImageDownloader.USE_REFERENCES = new boolean[] { false };
        ImageDownloader.RECYCLE_BITMAP = new boolean[] { false };
    }

    @Override
    protected ActivityController.Redirector getActivityRedirector() {
        return new ActivityController.Redirector() {
            public Intent getRedirection(Activity activity) {
                if (NuxeoAndroidSplashScreenActivity.isInitialized(NuxeoAndroidSplashScreenActivity.class) == false) {
                    // We re-direct to the splash screen activity if the
                    // application has not been yet initialized
                    if (activity.getComponentName() == null
                            || activity.getComponentName().getClassName().equals(
                                    NuxeoAndroidSplashScreenActivity.class.getName()) == true) {
                        return null;
                    } else {
                        return new Intent(activity,
                                NuxeoAndroidSplashScreenActivity.class);
                    }
                }
                // redirect to settings screen if prefs are not set
                if ("".equals(getPreferences().getString(SettingsActivity.PREF_SERVER_URL, ""))) {
                	if (activity.getComponentName() == null
                            || activity.getComponentName().getClassName().equals(
                            		SettingsActivity.class.getName()) == true) {
                        return null;
                    } else {
                    	return new Intent(activity,SettingsActivity.class);
                    }
                }
                return null;
            }
        };
    }

    @Override
    protected ActivityController.Interceptor getActivityInterceptor() {
        return new ActivityController.Interceptor() {
            public void onLifeCycleEvent(Activity activity,
                    ActivityController.Interceptor.InterceptorEvent event) {
            }
        };
    }

    @Override
    protected ActivityController.ExceptionHandler getExceptionHandler() {
        return super.getExceptionHandler();
    }

}
