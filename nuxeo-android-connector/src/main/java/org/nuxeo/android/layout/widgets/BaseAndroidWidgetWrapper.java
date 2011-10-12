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
import java.util.List;
import java.util.Map;

import org.nuxeo.android.context.NuxeoContext;
import org.nuxeo.android.layout.ActivityResultHandler;
import org.nuxeo.android.layout.ActivityResultHandlerRegistry;
import org.nuxeo.android.layout.LayoutContext;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.android.layout.WidgetDefinition;
import org.nuxeo.ecm.automation.client.android.AndroidAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.app.Activity;
import android.content.Context;
import android.view.View;

public abstract class BaseAndroidWidgetWrapper<T> implements ActivityResultHandlerRegistry {

	protected final Map<Integer, ActivityResultHandler> pendingActivityResultHandlers = new HashMap<Integer, ActivityResultHandler>();

	protected List<String> attributeNames;

	protected T currentValue;

	protected WidgetDefinition widgetDef;

	protected LayoutMode mode;

	protected LayoutContext layoutContext;

	@Override
	public void registerActivityResultHandler(int requestCode,
			ActivityResultHandler handler) {
		// store pending registration
		pendingActivityResultHandlers.put(requestCode, handler);
	}

	public Map<Integer, ActivityResultHandler> getAndFlushPendingActivityResultHandler() {
		Map<Integer, ActivityResultHandler> pending = new HashMap<Integer, ActivityResultHandler>(pendingActivityResultHandlers);
		pendingActivityResultHandlers.clear();
		return pending;
	}

	protected AndroidAutomationClient getClient() {
		return NuxeoContext.get(layoutContext.getActivity().getApplicationContext()).getNuxeoClient();
	}

	protected String getAttributeName() {
		if (attributeNames==null || attributeNames.size()==0)  {
			return null;
		}
		return attributeNames.get(0);
	}

	protected List<String> getAttributeNames() {
		return attributeNames;
	}

	protected void setAttributeName(String attributeName) {
		this.attributeNames.set(0,attributeName);
	}

	protected void setAttributeNames(List<String> attributeNames) {
		this.attributeNames=attributeNames;
	}
	protected abstract void initCurrentValueFromDocument(Document doc);

	protected T getCurrentValue() {
		return currentValue;
	}

	protected void setCurrentValue(T value) {
		currentValue = value;
	}

	public View buildView(LayoutContext context, LayoutMode mode, Document doc,
			List<String> attributeNames, WidgetDefinition widgetDef) {
		setAttributeNames(attributeNames);
		initCurrentValueFromDocument(doc);
		this.widgetDef=widgetDef;
		this.mode=mode;
		this.layoutContext=context;
		return null;
	}

	protected Context getRootContext() {
		return layoutContext.getActivity();
	}

	protected Activity getHomeActivity() {
		return layoutContext.getActivity();
	}

	protected LayoutContext getLayoutContext() {
		return layoutContext;
	}
}
