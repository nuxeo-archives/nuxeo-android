/*
 * (C) Copyright 2006-2008 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     bstefanescu
 */
package org.nuxeo.ecm.automation.client.jaxrs.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.net.Uri;

/**
 * A immutable document. You cannot modify documents. Documents are as they are
 * returned by the server. To modify documents use operations.
 * <p>
 * You need to create your own wrapper if you need to access the document
 * properties in a multi-level way. This is a flat representation of the
 * document.
 * <p>
 * Possible property value types:
 * <ul>
 * <li>String
 * <li>Number
 * <li>Date
 * <ul>
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * @author tiry
 */
public class Document extends DocRef implements Serializable {

	private static final String NEW_UUID_PREFIX = "NEW-";

	private static final long serialVersionUID = 1L;

	protected final String parentPath;

    protected final String type;

    protected final String state;

    protected final String lock;

    protected final String repoName;

    protected final PropertyMap properties;

    protected final List<String> dirtyFields = new ArrayList<String>();

    protected String name;

    /**
     * Reserved to framework. Should be only called by client framework when
     * unmarshalling documents.
     */
    public Document(String repoName, String id, String type, String path, String state,
            String lock, PropertyMap properties) {
        super(id);
        this.type = type;
        this.state = state;
        this.lock = lock;
        this.properties = properties == null ? new PropertyMap() : properties;
        this.repoName = repoName;
        // compute parent path and name
        if (path.endsWith("/")) {
        	path = path.substring(0, path.length()-1);
        }
        int idx = path.lastIndexOf("/");
        this.parentPath = path.substring(0, idx);
        this.name = path.substring(idx+1);
    }

    public Document(String parentPath, String name, String type) {
    	super(NEW_UUID_PREFIX + System.currentTimeMillis());
    	this.parentPath = parentPath;
    	this.state=null;
    	this.type=type;
    	this.lock=null;
    	this.properties = new PropertyMap();
    	this.repoName = null;
    	this.name=name;
    }

    public Document(String id, String path, String type, PropertyMap dirtyProps) {
    	super(id);
    	this.type=type;
        int idx = path.lastIndexOf("/");
        this.parentPath = path.substring(0, idx);
        this.name = path.substring(idx+1);
    	this.state=null;
    	this.properties = dirtyProps;
    	this.repoName = null;
    	this.lock=null;
    	dirtyFields.addAll(dirtyProps.map.keySet());
    }

    public String getName() {
    	return name;
    }

    public String getId() {
        return ref;
    }

    public String getInputType() {
        return "document";
    }

    public String getPath() {
        return parentPath + name;
    }

    public String getType() {
        return type;
    }

    public String getLock() {
        return lock;
    }

    public String getState() {
        return state;
    }

    public Date getLastModified() {
        return properties.getDate("dc:modified");
    }

    public String getTitle() {
        return properties.getString("dc:title");
    }

    public PropertyMap getProperties() {
        return properties;
    }

    public String getString(String key) {
        return properties.getString(key);
    }

    public Date getDate(String key) {
        return properties.getDate(key);
    }

    public Long getLong(String key) {
        return properties.getLong(key);
    }

    public Double getDouble(String key) {
        return properties.getDouble(key);
    }

    public String getString(String key, String defValue) {
        return properties.getString(key, defValue);
    }

    public Date getDate(String key, Date defValue) {
        return properties.getDate(key, defValue);
    }

    public Long getLong(String key, Long defValue) {
        return properties.getLong(key, defValue);
    }

    public Double getDouble(String key, Double defValue) {
        return properties.getDouble(key, defValue);
    }

    public void set(String key, String defValue) {
        properties.set(key, defValue);
        dirtyFields.add(key);
    }

    public void set(String key, Date defValue) {
        properties.set(key, defValue);
        dirtyFields.add(key);
    }

    public void set(String key, Long defValue) {
        properties.set(key, defValue);
        dirtyFields.add(key);
    }

    public void set(String key, Double defValue) {
        properties.set(key, defValue);
        dirtyFields.add(key);
    }

    public String getRelativeUrl() {
    	return "/nxpath/" + repoName + getPath() + "@view_documents";
    }

    public boolean isDirty() {
    	return dirtyFields.size()>0;
    }

    public PropertyMap getDirtyProperties() {
    	PropertyMap dirtyProps = new PropertyMap();
    	for (String key : dirtyFields) {
    		dirtyProps.map().put(key, properties.map().get(key));
    	}
    	return dirtyProps;
    }

    public String getDirtyPropertiesAsPropertiesString() {
    	return PropertiesHelper.toStringProperties(getDirtyProperties());
    }

    public DocumentStatus getStatusFlag() {
    	if (ref==null || ref.startsWith(NEW_UUID_PREFIX)) {
    		return DocumentStatus.NEW;
    	}
    	if (isDirty()) {
    		return DocumentStatus.UPDATED;
    	}
    	return DocumentStatus.SYNCHRONIZED;
    }

    public String getParentPath() {
    	return parentPath;
    }

	public String getRepoName() {
		return repoName;
	}

	public List<String> getDirtyFields() {
		return dirtyFields;
	}

	public Uri getIcon() {
		String icon = getString("common:icon");
		if (icon==null || "null".equals(icon)) {
			return null;
		}
		return Uri.parse("content://nuxeo/icons" + getString("common:icon"));
	}

	public Uri getBlob() {
		return getBlob(0);
	}

	public Uri getBlob(int idx) {
		return Uri.parse("content://nuxeo/blobs/" + getId() + "/" + idx);
	}

}
