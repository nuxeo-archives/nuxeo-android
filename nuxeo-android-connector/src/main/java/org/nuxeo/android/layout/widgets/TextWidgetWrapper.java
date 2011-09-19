package org.nuxeo.android.layout.widgets;

import org.nuxeo.android.adapters.DocumentAttributeResolver;
import org.nuxeo.android.layout.LayoutContext;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.android.layout.WidgetDefinition;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class TextWidgetWrapper extends BaseAndroidWidgetWrapper<String> implements AndroidWidgetWrapper {

	protected TextView txtWidget;
	protected EditText editWidget;

	@Override
	public boolean validateBeforeModelUpdate() {
		return true;
	}

	@Override
	public void updateModel(Document doc) {
		DocumentAttributeResolver.put(doc, attributeName, getCurrentValue());
	}

	@Override
	public void refreshViewFromDocument(Document doc) {
		initCurrentValueFromDocument(doc);
		applyBinding();
	}

	protected void applyBinding() {
		if (txtWidget!=null) {
			txtWidget.setText(getCurrentValue());
		}
		if (editWidget!=null) {
			editWidget.setText(getCurrentValue());
		}
	}

	@Override
	public View buildView(LayoutContext context, LayoutMode mode, Document doc,
			String attributeName, WidgetDefinition widgetDef) {

		super.buildView(context, mode, doc, attributeName, widgetDef);

		Context ctx = context.getActivity();

		if (mode==LayoutMode.VIEW) {
			txtWidget = new TextView(ctx);
			applyBinding();
			return txtWidget;
		} else {
			editWidget = new EditText(ctx);
			applyBinding();
			return editWidget;
		}
	}

	@Override
	protected void initCurrentValueFromDocument(Document doc) {
		setCurrentValue(DocumentAttributeResolver.getString(doc, attributeName));
	}

}
