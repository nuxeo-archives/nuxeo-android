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

import java.io.FileNotFoundException;
import java.util.List;

import org.nuxeo.android.adapters.NuxeoDocumentCursor;
import org.nuxeo.android.adapters.UUIDMapper;
import org.nuxeo.android.context.NuxeoContext;
import org.nuxeo.android.documentprovider.DocumentProvider;
import org.nuxeo.android.documentprovider.LazyDocumentsList;
import org.nuxeo.android.download.FileDownloader;
import org.nuxeo.ecm.automation.client.android.AndroidAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.model.FileBlob;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.ProviderInfo;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;


/**
 *
 * For {@link NuxeoDocumentCursor}
 *
 * content://nuxeo/documents                  : access to all documents
 * content://nuxeo/documents/<UUID>           : access to document with given UUID
 *
 * content://nuxeo/<providername>             : access to documents in the given provider
 * content://nuxeo/<providername>/UUID        : access to document with UUID in the given provider
 *
 * For {@link NuxeoAssetCursor}
 *
 * content://nuxeo/icons/<subPath>            : access to small Nuxeo icon of the given path
 *
 * content://nuxeo/blobs/<UUID>               : access to main blog of the doc with the given UUID
 * content://nuxeo/blobs/<UUID>/<idx>         : access to blob [idx] of the doc with the given UUID
 * content://nuxeo/blobs/<UUID>/<subPath>     : access to blob in the field <subpath> of the doc with the given UUID
 *
 * content://nuxeo/uploads/<REQUESTID>            : access to tmp blob matching uploadId REQUESTID
 *
 * @author tiry
 *
 */

public abstract class AbstractNuxeoReadOnlyContentProvider extends ContentProvider {

	protected UUIDMapper mapper;

	protected UriMatcher uriMatcher;

	public static final String ALL_DOCUMENTS = "documents";
	public static final String ICONS = "icons";
	public static final String BLOBS = "blobs";
	public static final String UPLOAD = "uploads";
	public static final String PICTURES = "pictures";

	protected static final int ALL_DOCUMENTS_PROVIDER = 0;
	protected static final int ANY_DOCUMENT_PROVIDER = 1;
	protected static final int ICONS_PROVIDER = 2;
	protected static final int BLOBS_PROVIDER = 3;
	protected static final int DOCUMENTS_PROVIDER = 4;
	protected static final int DOCUMENT_PROVIDER = 5;
	protected static final int PICTURES_PROVIDER = 6;
	protected static final int UPLOAD_PROVIDER = 7;

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		throw new UnsupportedOperationException("Not implemented");
	}



	@Override
	public void attachInfo(Context context, ProviderInfo info) {
		NuxeoContentProviderConfig.init(info);
		initMapper();
		super.attachInfo(context, info);

	}

	@Override
	public boolean onCreate() {
		// don't init the NuxeoSession now !!!
		mapper = new UUIDMapper();
		return true;
	}

	protected void initMapper() {
		String authority = NuxeoContentProviderConfig.getAuthority();
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(authority, ALL_DOCUMENTS, ALL_DOCUMENTS_PROVIDER);
		uriMatcher.addURI(authority, ALL_DOCUMENTS + "/*", ANY_DOCUMENT_PROVIDER);
		uriMatcher.addURI(authority, ICONS + "/*", ICONS_PROVIDER);
		uriMatcher.addURI(authority, ICONS + "/*/*", ICONS_PROVIDER);
		uriMatcher.addURI(authority, ICONS + "/*/*/*", ICONS_PROVIDER);
		uriMatcher.addURI(authority, ICONS + "/*/*/*/*", ICONS_PROVIDER);
		uriMatcher.addURI(authority, BLOBS + "/*", BLOBS_PROVIDER);
		uriMatcher.addURI(authority, BLOBS + "/*/#", BLOBS_PROVIDER);
		uriMatcher.addURI(authority, BLOBS + "/*/*", BLOBS_PROVIDER);
		uriMatcher.addURI(authority, BLOBS + "/*/*/*", BLOBS_PROVIDER);
		uriMatcher.addURI(authority, BLOBS + "/*/*/*/*", BLOBS_PROVIDER);
		uriMatcher.addURI(authority, PICTURES+ "/*/*", PICTURES_PROVIDER);
		uriMatcher.addURI(authority, PICTURES+ "/*", PICTURES_PROVIDER);
		uriMatcher.addURI(authority, "*", DOCUMENTS_PROVIDER);
		uriMatcher.addURI(authority, "*/*", DOCUMENT_PROVIDER);
		uriMatcher.addURI(authority, UPLOAD + "/*", UPLOAD_PROVIDER);
	}

	protected UriMatcher getMatcher() {
		if (uriMatcher==null) {
			// lazy init because we need to have the provider registred before we know the target authority ...
			initMapper();
		}
		return uriMatcher;
	}

	protected LazyDocumentsList resolveDocumentProvider(Uri uri) {

		DocumentProvider providerService = ((AndroidAutomationClient)getSession().getClient()).getDocumentProvider();
		List<String> segments = uri.getPathSegments();
		if (segments.size()<1) {
			return null;
		}
		String providerName = segments.get(0);
		LazyDocumentsList docList = providerService.getReadOnlyDocumentsList(providerName, getSession());
		return docList;
	}

	@Override
	public Cursor query(Uri uri, String[] columns, String selection,
			String[] selectionArgs, String sortOrder) {

		//Log.i("NuxeoContentProvider", "called on query with uri : " + uri.toString());
		//Log.i("NuxeoContentProvider", "Match=> " + uriMatcher.match(uri));

		int match = getMatcher().match(uri);
		switch (match)
		{
			case ALL_DOCUMENTS_PROVIDER:
				String nxql = buildNXQLQuery(selection, selectionArgs, sortOrder);
				return buildCursor(nxql, selection, selectionArgs, sortOrder);
			case DOCUMENTS_PROVIDER :
				LazyDocumentsList docList = resolveDocumentProvider(uri);
				if (docList!=null) {
					return new NuxeoDocumentCursor(docList);
				}
		}
		return null;
	}

	protected Session getSession() {
		return NuxeoContext.get(getContext()).getSession();
	}

	protected AndroidAutomationClient getClient() {
		return (AndroidAutomationClient)getSession().getClient();
	}

	protected NuxeoDocumentCursor buildCursor(String nxql, String selection,String[] selectionArgs, String sortOrder) {
		return new NuxeoDocumentCursor(getSession(),nxql, selectionArgs, sortOrder,getSchemas(), getDefaultPageSize(), mapper, false);
	}

	protected abstract int getDefaultPageSize();

	protected String getSchemas() {
		return "common,dublincore";
	}

	protected String buildNXQLQuery(String selection,
			String[] selectionArgs, String sortOrder) {

		String nxql = "select * from Document ";
		if (selection!=null) {
			nxql = nxql + " where " + selection;
		}

		if (sortOrder!=null && !sortOrder.equals("")) {
			nxql = nxql + " order by " + sortOrder;
		}

		return nxql;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public String getType(Uri uri) {

		//Log.i("NuxeoContentProvider", "called on getType with uri : " + uri.toString());
		//Log.i("NuxeoContentProvider", "Match=> " + uriMatcher.match(uri));

		String mimeType = null;

		int match = getMatcher().match(uri);

		switch (match) {
			case BLOBS_PROVIDER :
			case ICONS_PROVIDER :
			case PICTURES_PROVIDER :
			case UPLOAD_PROVIDER :
				FileBlob blob = resolveBlob(uri);
				if (blob!=null) {
					mimeType = blob.getMimeType();
				}
				break;
			case DOCUMENTS_PROVIDER :
				LazyDocumentsList docList = resolveDocumentProvider(uri);
				if (docList!=null) {
					String mt = docList.getExposedMimeType();
					if (mt==null) {
						mt="org.nuxeo.document";
					}
					//return "vnd.android.cursor.item/" + mt;
					mimeType =  "vnd.android.cursor.dir/" + mt;
				}
				break;
			case DOCUMENT_PROVIDER :
				LazyDocumentsList docList2 = resolveDocumentProvider(uri);
				if (docList2!=null) {
					String mt = docList2.getExposedMimeType();
					if (mt==null) {
						mt="org.nuxeo.document";
					}
					mimeType =  "vnd.android.cursor.item/" + mt;
				}
				break;
		}
		Log.i("NuxeoContentProvider", "==> " + mimeType);
		return mimeType;
	}

	@Override
	public AssetFileDescriptor openAssetFile(Uri uri, String mode)
			throws FileNotFoundException {
		//Log.i("NuxeoContentProvider", "called on openAssetFile with uri : " + uri.toString());
		return super.openAssetFile(uri, mode);
	}

	protected FileBlob resolveBlob(Uri uri) {
		String resourceType = uri.getPathSegments().get(0);
		FileDownloader downloader = getClient().getFileDownloader();

		if (resourceType.equals("icons")) {

			String subPath = uri.getEncodedPath().toString();
			subPath = subPath.replaceFirst("/icons", "");
			FileBlob iconFile = downloader.getIcon(subPath);
			if (iconFile!=null) {
				return iconFile;
			}
		}
		else if (resourceType.equals("uploads")) {
			FileBlob tmpBlob =  getClient().getFileUploader().getBlob(uri.getLastPathSegment());
			if (tmpBlob.getMimeType()!=null && tmpBlob.getMimeType().startsWith("image")) {
				return BitmapSampler.sampleBitmapFile(getContext(), tmpBlob);
			}
		}
		else if (resourceType.equals("blobs")) {
			String uid = uri.getPathSegments().get(1);
			FileBlob blob = null;
			String suffix = null;
			Integer idx = null;
			if (uri.getPathSegments().size()>2) {
				suffix = uri.getPathSegments().get(2);
				try {
					idx = Integer.parseInt(suffix);
					blob = downloader.getBlob(uid, idx);
				} catch (NumberFormatException e) {
					idx = uri.toString().indexOf(uid);
					String subPath = uri.toString().substring(idx + uid.length()+1);
					blob = downloader.getBlob(uid, subPath);
				}
			} else {
				blob = downloader.getBlob(uid);
			}
			if (blob!=null) {
				return blob;
			}
		}
		else if (resourceType.equals("pictures")) {
			String uid = uri.getPathSegments().get(1);
			FileBlob blob = null;
			String format = "Medium";
			Integer idx = null;
			if (uri.getPathSegments().size()>2) {
				format = uri.getPathSegments().get(2);
			}
			blob = downloader.getPicture(uid, format);
			if (blob!=null) {
				return blob;
			}
		}
		return null;
	}

	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode)
			throws FileNotFoundException {

		FileBlob blob = resolveBlob(uri);
		if (blob!=null) {
			return ParcelFileDescriptor.open(blob.getFile(), ParcelFileDescriptor.MODE_READ_ONLY);
		}
		return null;
	}

}
