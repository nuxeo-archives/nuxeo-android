package org.nuxeo.android.layout;

import java.util.Map;

import org.nuxeo.android.layout.widgets.AndroidWidgetMapper;
import org.nuxeo.android.layout.widgets.AndroidWidgetWrapper;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.view.View;

public class NuxeoWidget {

	protected final WidgetDefinition widgetDef;

	protected final View view;

	protected final AndroidWidgetWrapper wrapper;

	public NuxeoWidget(WidgetDefinition widgetDef, View view) {
		this.widgetDef=widgetDef;
		this.view=view;
		wrapper = AndroidWidgetMapper.getInstance().getWidgetWrapper(widgetDef.getType());
		if (wrapper==null) {
			throw new RuntimeException("No native Widget wrapper registred for WidgetType " + widgetDef.getType());
		}
	}

	public void applyChanges(Document doc) {
		if (view!=null) {
			wrapper.applyChanges(view, widgetDef.getMode(), doc, widgetDef.getAttributeName(), this);
		} else {
			throw new RuntimeException("Can not apply changes with a null view");
		}
	}

	public void refresh(Document doc) {
		if (view!=null) {
			wrapper.refresh(view, widgetDef.getMode(), doc, widgetDef.getAttributeName(), this);
		} else {
			throw new RuntimeException("Can not refresh a null view");
		}

	}

	public Map<Integer, ActivityResultHandler> getAndFlushPendingActivityResultHandler() {
		return wrapper.getAndFlushPendingActivityResultHandler();
	}

	public WidgetDefinition getWidgetDef() {
		return widgetDef;
	}

	public View getView() {
		return view;
	}


}
