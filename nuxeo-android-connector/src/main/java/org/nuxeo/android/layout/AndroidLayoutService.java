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

package org.nuxeo.android.layout;

import java.util.HashMap;
import java.util.Map;

import org.nuxeo.android.download.FileDownloader;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.app.Activity;
import android.view.ViewGroup;

public class AndroidLayoutService implements NuxeoLayoutService {

	protected static final String DOCTYPE_PREFIX ="doctype:";

	protected Map<String, LayoutDefinition> definitions = new HashMap<String, LayoutDefinition>();

	protected final FileDownloader downloader;

	public AndroidLayoutService(FileDownloader downloader) {
		this.downloader = downloader;
	}

	protected FileDownloader getFileDownloader() {
		return downloader;
	}

	@Override
	public NuxeoLayout getLayout(Activity ctx, Document doc, ViewGroup parent, LayoutMode mode) {
		return getLayout(ctx, doc, parent, mode, null);
	}

	@Override
	public NuxeoLayout getLayout(Activity ctx, Document doc, ViewGroup parent, LayoutMode mode, String layoutName) {
		boolean useDocType=false;
		if (layoutName==null) {
			useDocType = true;
			layoutName = DOCTYPE_PREFIX + doc.getType();
		}
		LayoutDefinition def = definitions.get(layoutName);
		if (def==null) {
			if (useDocType) {
				def = loadLayout(doc.getType(), true);
			} else {
				def = loadLayout(layoutName, false);
			}
		}
		if (def!=null) {
			return def.buildLayout(ctx, doc, parent, mode);
		} else {
			return null;
		}
	}

	protected LayoutDefinition loadLayout(String layoutName, boolean docType) {

		String jsonLayout=getFileDownloader().getLayoutDefinition(layoutName, docType);
		if (jsonLayout==null) {
			return null;
		}
		LayoutDefinition layoutDefinition = parse(jsonLayout);
		if (layoutDefinition!=null) {
			String key = layoutName;
			if (docType) {
				key = DOCTYPE_PREFIX + key;
			}
			definitions.put(key, layoutDefinition);
		}
		return layoutDefinition;
	}

	protected LayoutDefinition parse(String jsonLayoutDef) {
		return LayoutDefinition.fromJSON(jsonLayoutDef);
	}

	@Override
	public NuxeoLayout parseLayoutDefinition(String definition, Activity ctx, Document doc, ViewGroup parent, LayoutMode mode) {
		LayoutDefinition layoutDefinition = parse(definition);
		return layoutDefinition.buildLayout(ctx, doc, parent, mode);
	}

}
