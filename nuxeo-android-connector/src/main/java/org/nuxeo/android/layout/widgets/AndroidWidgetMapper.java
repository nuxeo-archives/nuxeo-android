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

package org.nuxeo.android.layout.widgets;

import java.util.HashMap;
import java.util.Map;

import org.nuxeo.android.layout.WidgetDefinition;

import android.util.Log;

public class AndroidWidgetMapper {

	protected static AndroidWidgetMapper instance = null;

	protected final static Map<String, Class <? extends AndroidWidgetWrapper>> wrappers = new HashMap<String, Class <? extends AndroidWidgetWrapper>>();

	protected AndroidWidgetMapper() {
		registerDefaultWrappers();
	}

	public static AndroidWidgetMapper getInstance() {
		if (instance==null) {
			instance = new AndroidWidgetMapper();
		}
		return instance;
	}

	public static void registerWidgetWrapper(String type,  Class <? extends AndroidWidgetWrapper> wrapperClass) {
		wrappers.put(type, wrapperClass);
	}

	protected void registerDefaultWrappers() {
		registerWidgetWrapper("text", TextWidgetWrapper.class);
		registerWidgetWrapper("readonlytext", ReadOnlyTextWidgetWrapper.class);
		registerWidgetWrapper("date", DateWidgetWrapper.class);
		registerWidgetWrapper("datetime", DateWidgetWrapper.class);
		registerWidgetWrapper("selectOneDirectory", SpinnerWidgetWrapper.class);
		registerWidgetWrapper("selectManyDirectory", SpinnerMultiWidgetWrapper.class);
		registerWidgetWrapper("blob", BlobWidgetWrapper.class);
		registerWidgetWrapper("textarea", TextAreaWidgetWrapper.class);
		registerWidgetWrapper("richtext_with_mimetype", RichTextWidgetWrapper.class);
		registerWidgetWrapper("checkbox", CheckBoxWidgetWrapper.class);
	}

	protected String getAndroidWidgetType(WidgetDefinition wDef) {

		if (wrappers.keySet().contains(wDef.getType())) {
			return wDef.getType();
		}

		if ("template".equals(wDef.getType())) {
			String templateName = wDef.getProperties().optString("template");
			if (templateName!=null) {
				if (templateName.contains("extended_file_widget")) {
					return "blob";
				}
				else if (templateName.contains("single_user_widget_template")) {
					return "readonlytext";
				}
				else if (templateName.contains("single_user_widget_template")) {
					return "readonlytext";
				}
				else if (templateName.contains("subjects_widget")) {
					return "selectManyDirectory";
				}
				else if (templateName.contains("coverage_widget")) {
					return "selectOneDirectory";
				}
			}
		}
		return null;
	}

	public AndroidWidgetWrapper getWidgetWrapper(WidgetDefinition wDef) {
		try {
			String type = getAndroidWidgetType(wDef);
			if (type==null) {
				Log.w(this.getClass().getSimpleName(), "No Widget mapping found for type " + wDef.getType() + " with name " + wDef.getName());
				return null;
			}
			Class <? extends AndroidWidgetWrapper> wrapperClass =wrappers.get(type);
			AndroidWidgetWrapper wrapper = wrapperClass.newInstance();
			return wrapper;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
