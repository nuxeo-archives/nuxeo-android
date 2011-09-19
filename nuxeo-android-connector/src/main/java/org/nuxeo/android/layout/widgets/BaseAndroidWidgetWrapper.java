package org.nuxeo.android.layout.widgets;

import java.util.HashMap;
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

	protected String attributeName;

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
		return attributeName;
	}

	protected void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	protected abstract void initCurrentValueFromDocument(Document doc);

	protected T getCurrentValue() {
		return currentValue;
	}

	protected void setCurrentValue(T value) {
		currentValue = value;
	}

	public View buildView(LayoutContext context, LayoutMode mode, Document doc,
			String attributeName, WidgetDefinition widgetDef) {
		setAttributeName(attributeName);
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
