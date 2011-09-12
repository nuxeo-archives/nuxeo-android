package org.nuxeo.android.layout.widgets;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.nuxeo.android.adapters.DocumentAttributeResolver;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.android.layout.WidgetDefinition;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

public class DateWidgetWrapper implements AndroidWidgetWrapper {

	protected static SimpleDateFormat fmt = new SimpleDateFormat("yy/MM/dd HH:mm:ss");

	@Override
	public void apply(View nativeWidget, LayoutMode mode, Document doc,
			String attributeName, WidgetDefinition widgetDef) {
		if (mode!=LayoutMode.VIEW) {
			DatePicker widget = (DatePicker) nativeWidget;
			Date date = (Date) widget.getTag();
			if (date!=null) {
				doc.set(attributeName, date);
			}
		}
	}

	@Override
	public View build(Context ctx, LayoutMode mode, Document doc,
			String attributeName, WidgetDefinition widgetDef) {

		if (mode==LayoutMode.VIEW) {
			TextView widget = new TextView(ctx);
			Date date = DocumentAttributeResolver.getDate(doc, attributeName);
			if (date==null) {
				widget.setText("");
			} else {
				widget.setText(fmt.format(date));
			}
			return widget;
		} else {
			DatePicker widget = new DatePicker(ctx);
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
			return widget;
		}
	}



}
