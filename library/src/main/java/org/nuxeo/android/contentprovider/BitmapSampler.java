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
package org.nuxeo.android.contentprovider;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.nuxeo.android.cache.blob.BlobStoreManager;
import org.nuxeo.ecm.automation.client.jaxrs.model.FileBlob;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class BitmapSampler {

    public static final int REQUIRED_SIZE = 100;

    public static FileBlob sampleBitmapFile(Context ctx, FileBlob blob) {

        // XXX cleanup file ...
        Bitmap sampled = decodeFile(blob.getFile());
        try {
            File dir = BlobStoreManager.getRootCacheDir(ctx);
            String filename = "sampled-" + blob.getFileName() + ".jpg";
            File sampledFile = new File(dir, filename);
            if (!sampledFile.exists()) {
                FileOutputStream out = new FileOutputStream(sampledFile);
                sampled.compress(Bitmap.CompressFormat.JPEG, 90, out);
            }
            return new FileBlob(sampledFile, sampledFile.getName(),
                    "image/jpeg");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    // taken from
    // http://stackoverflow.com/questions/477572/android-strange-out-of-memory-issue/823966#823966
    protected static Bitmap decodeFile(File f) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }
}
