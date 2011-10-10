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

package org.nuxeo.android.adapters;

import java.util.Date;

import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

public class DocumentAttributeResolver {

	public static final String ID = "uuid";
	public static final String NAME = "name";
	public static final String PATH = "path";
	public static final String ICONURI = "iconUri";
	public static final String STATUS = "status";

	public static Object get(Document doc, String attributeName) {
		if (ID.equalsIgnoreCase(attributeName)) {
			return doc.getId();
		}
		else if (NAME.equalsIgnoreCase(attributeName)) {
			return doc.getName();
		}
		else if (PATH.equalsIgnoreCase(attributeName)) {
			return doc.getPath();
		}
		else if (ICONURI.equalsIgnoreCase(attributeName)) {
			return doc.getIcon();
		}
		else if (STATUS.equalsIgnoreCase(attributeName)) {
			return doc.getStatusFlag().toString();
		}
		// XXX ...
		return doc.getProperties().map().get(attributeName);
	}

	public static void put(Document doc, String attributeName, Object value) {
		doc.getProperties().map().put(attributeName, value);
		doc.getDirtyFields().add(attributeName);
	}

	public static String getString(Document doc, String attributeName) {

		Object val = get(doc, attributeName);
		if (val==null || "null".equals(val)) {
			return "";
		}
		return val.toString();
	}

	public static Date getDate(Document doc, String attributeName) {
		return doc.getDate(attributeName);
	}
}
