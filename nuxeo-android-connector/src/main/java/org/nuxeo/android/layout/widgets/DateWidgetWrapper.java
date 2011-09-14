package org.nuxeo.android.layout.widgets;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.nuxeo.android.adapters.DocumentAttributeResolver;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.android.layout.NuxeoWidget;
import org.nuxeo.android.layout.WidgetDefinition;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.app.Activity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

public class DateWidgetWrapper extends BaseAndroidWidgetWrapper implements AndroidWidgetWrapper {

	protected static SimpleDateFormat fmt = new SimpleDateFormat("yy/MM/dd HH:mm:ss");

	@Override
	public void applyChanges(View nativeWidget, LayoutMode mode, Document doc,
			String attributeName, NuxeoWidget nuxeoWidget) {
		if (mode!=LayoutMode.VIEW) {
			DatePicker widget = (DatePicker) nativeWidget;
			Date date = (Date) widget.getTag();
			if (date!=null) {
				doc.set(attributeName, date);
			}
		}
	}

	@Override
	public void refresh(View nativeWidget, LayoutMode mode, Document doc,
			String attributeName, NuxeoWidget nuxeoWidget) {
		if (mode==LayoutMode.VIEW) {
			applyBinding((TextView) nativeWidget, doc, attributeName);
		} else {
			applyBinding((DatePicker)nativeWidget, doc, attributeName);
		}

	}

	protected void applyBinding(TextView widget, Document doc, String attributeName) {
		Date date = DocumentAttributeResolver.getDate(doc, attributeName);
		if (date==null) {
			widget.setText("");
		} else {
			widget.setText(fmt.format(date));
		}
	}

	protected void applyBinding(DatePicker widget, Document doc, String attributeName) {
		Date date = DocumentAttributeResolver.getDate(doc, attributeName);
		Calendar cal = Calendar.getInstance();
		if (date!=null) {
			cal.setTime(date);
		}
		widget.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
			@Override
			public void onDateChanged(DatePicker view, int year, int month,
					int dayOfMonth) {
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, year);
				cal.set(Calendar.MONTH, month);
				cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				Date date = cal.getTime();
				view.setTag(date);
			}

		});
	}

	@Override
	public View build(Activity ctx, LayoutMode mode, Document doc,
			String attributeName, WidgetDefinition widgetDef) {

		if (mode==LayoutMode.VIEW) {
			TextView widget = new TextView(ctx);
			applyBinding(widget, doc, attributeName);
			return widget;
		} else {
			DatePicker widget = new DatePicker(ctx);
			applyBinding(widget, doc, attributeName);
			return widget;
		}
	}




}
