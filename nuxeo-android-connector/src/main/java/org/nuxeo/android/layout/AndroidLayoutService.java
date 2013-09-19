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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nuxeo.android.download.FileDownloader;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;

public class AndroidLayoutService implements NuxeoLayoutService {

    protected static final String DOCTYPE_PREFIX = "doctype:";

    protected Map<String, LayoutDefinition> definitions = new HashMap<String, LayoutDefinition>();

    protected final FileDownloader downloader;

    public AndroidLayoutService(FileDownloader downloader) {
        this.downloader = downloader;
    }

    protected FileDownloader getFileDownloader() {
        return downloader;
    }

    protected static final List<String> builtinTypes = Arrays.asList(new String[] {
            "File", "Folder", "Workspace", "Domain", "Note", "Picture" });

    protected String getLayoutDefinitionModeForType(LayoutMode mode,
            String docType) {
        if (builtinTypes.contains(docType)) {
            return "edit";
        } else {
            return mode.toString();
        }
    }

    @Override
    public NuxeoLayout getLayout(Fragment fragment, Document doc, ViewGroup parent,
            LayoutMode mode) {
        return getLayout(fragment, doc, parent, mode, null);
    }
    

    public NuxeoLayout getLayout(Activity ctx, Document doc, ViewGroup parent,
            LayoutMode mode) {
    	String nullString = null;
        return getLayout(ctx, doc, parent, mode, nullString);
    }

    @Override
    public NuxeoLayout getLayout(Activity ctx, Document doc, ViewGroup parent,
            LayoutMode mode, String layoutName) {
        boolean useDocType = false;
        String defMode = null;
        if (layoutName == null) {
            useDocType = true;
            defMode = getLayoutDefinitionModeForType(mode, doc.getType());
            layoutName = DOCTYPE_PREFIX + doc.getType() + defMode;
        }
        LayoutDefinition def = definitions.get(layoutName);
        if (def == null) {
            if (useDocType) {
                def = loadLayout(doc.getType(), true, defMode);
            } else {
                def = loadLayout(layoutName, false, null);
            }
        }
        if (def != null) {
            return def.buildLayout(ctx, doc, parent, mode);
        } else {
            return null;
        }
    }
    
    @Override
    public NuxeoLayout getLayout(Fragment fragment, Document doc, ViewGroup parent,
            LayoutMode mode, String layoutName) {
        boolean useDocType = false;
        String defMode = null;
        if (layoutName == null) {
            useDocType = true;
            defMode = getLayoutDefinitionModeForType(mode, doc.getType());
            layoutName = DOCTYPE_PREFIX + doc.getType() + defMode;
        }
        LayoutDefinition def = definitions.get(layoutName);
        if (def == null) {
            if (useDocType) {
                def = loadLayout(doc.getType(), true, defMode);
            } else {
                def = loadLayout(layoutName, false, null);
            }
        }
        if (def != null) {
            return def.buildLayout(fragment, doc, parent, mode);
        } else {
            return null;
        }
    }

    protected LayoutDefinition loadLayout(String layoutName, boolean docType,
            String defMode) {

        String jsonLayout = getFileDownloader().getLayoutDefinition(layoutName,
                docType, defMode);
        if (jsonLayout == null) {
            return null;
        }
        LayoutDefinition layoutDefinition = parse(jsonLayout);
        if (layoutDefinition != null) {
            String key = layoutName;
            if (docType) {
                key = DOCTYPE_PREFIX + key + defMode;
            }
            definitions.put(key, layoutDefinition);
        }
        return layoutDefinition;
    }

    protected LayoutDefinition parse(String jsonLayoutDef) {
        return LayoutDefinition.fromJSON(jsonLayoutDef);
    }

    @Override
    public NuxeoLayout parseLayoutDefinition(String definition, Fragment fragment,
            Document doc, ViewGroup parent, LayoutMode mode) {
        LayoutDefinition layoutDefinition = parse(definition);
        return layoutDefinition.buildLayout(fragment, doc, parent, mode);
    }
    

    public NuxeoLayout parseLayoutDefinition(String definition, Activity ctx,
            Document doc, ViewGroup parent, LayoutMode mode) {
        LayoutDefinition layoutDefinition = parse(definition);
        return layoutDefinition.buildLayout(ctx, doc, parent, mode);
    }

}
