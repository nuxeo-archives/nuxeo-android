package org.nuxeo.android.layout.widgets;

import java.util.HashMap;
import java.util.Map;

import org.nuxeo.android.context.NuxeoContext;
import org.nuxeo.android.layout.ActivityResultHandler;
import org.nuxeo.android.layout.ActivityResultHandlerRegistry;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.android.layout.WidgetDefinition;
import org.nuxeo.ecm.automation.client.android.AndroidAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.app.Activity;
import android.content.Context;
import android.view.View;

public abstract class BaseAndroidWidgetWrapper<T> implements ActivityResultHandlerRegistry {

	protected final Map<Integer, ActivityResultHandler> pendingActivityResultHandlers = new HashMap<Integer, ActivityResultHandler>();

	protected Activity activity;

	protected String attributeName;

	protected T currentValue;

	protected WidgetDefinition widgetDef;

	protected LayoutMode mode;

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
		return NuxeoContext.get(activity.getApplicationContext()).getNuxeoClient();
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

	public View buildView(Activity ctx, LayoutMode mode, Document doc,
			String attributeName, WidgetDefinition widgetDef) {
		setAttributeName(attributeName);
		initCurrentValueFromDocument(doc);
		this.widgetDef=widgetDef;
		this.mode=mode;
		this.activity=ctx;
		return null;
	}

	protected Context getRootContext() {
		return activity;
	}

	protected Activity getHomeActivity() {
		return activity;
	}
}
