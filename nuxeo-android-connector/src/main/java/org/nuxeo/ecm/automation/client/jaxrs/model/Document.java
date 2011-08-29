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
import java.util.Map;

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

	private static final long serialVersionUID = 1L;

	protected final String path;

    protected final String type;

    protected final String state;

    protected final String lock;

    protected final String repoName;

    protected final PropertyMap properties;

    protected final List<String> dirtyFields = new ArrayList<String>();

    /**
     * Reserved to framework. Should be only called by client framework when
     * unmarshalling documents.
     */
    public Document(String repoName, String id, String type, String path, String state,
            String lock, PropertyMap properties) {
        super(id);
        this.path = path;
        this.type = type;
        this.state = state;
        this.lock = lock;
        this.properties = properties == null ? new PropertyMap() : properties;
        this.repoName = repoName;
    }

    public String getId() {
        return ref;
    }

    public String getInputType() {
        return "document";
    }

    public String getPath() {
        return path;
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
    	return "/nxpath/" + repoName + path + "@view_documents";
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

    public String getStatusFlag() {
    	if (isDirty()) {
    		return "U";
    	}
    	if (ref==null) {
    		return "N";
    	}
    	return "";
    }
}