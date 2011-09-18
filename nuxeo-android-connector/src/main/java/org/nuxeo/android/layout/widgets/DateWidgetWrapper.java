package org.nuxeo.android.layout.widgets;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.nuxeo.android.adapters.DocumentAttributeResolver;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.android.layout.WidgetDefinition;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DateWidgetWrapper extends BaseAndroidWidgetWrapper<Calendar> implements AndroidWidgetWrapper, View.OnClickListener, OnDateSetListener {

	protected static SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
	protected LinearLayout layout;
	protected TextView txt;
	protected Button btn;
	protected DatePickerDialog datePickerDialog;

	@Override
	public boolean validateBeforeModelUpdate() {
		return true;
	}

	@Override
	public void updateModel(Document doc) {
		if (mode!=LayoutMode.VIEW) {
			if (getCurrentValue()!=null) {
				doc.set(attributeName, getCurrentValue().getTime());
			}
		}
	}

	@Override
	public void refreshViewFromDocument(Document doc) {
		initCurrentValueFromDocument(doc);
		applyBinding();
	}

	protected void initCurrentValueFromDocument(Document doc) {
		Date date = DocumentAttributeResolver.getDate(doc, attributeName);
		if (date!=null) {
			currentValue = Calendar.getInstance();
			currentValue.setTime(date);
		}
	}

	protected void applyBinding() {
		Calendar value = getCurrentValue();
		if (value!=null) {
			txt.setText(fmt.format(value.getTime()));
		} else {
			txt.setText("-- not set --");
		}
		txt.invalidate();
	}

	@Override
	public View buildView(Activity ctx, LayoutMode mode, Document doc,
			String attributeName, WidgetDefinition widgetDef) {

		super.buildView(ctx, mode, doc, attributeName, widgetDef);

		layout = new LinearLayout(ctx);
		layout.setOrientation(LinearLayout.HORIZONTAL);

		txt = new TextView(ctx);
		layout.addView(txt);
		if (mode!=LayoutMode.VIEW) {
			btn = new Button(ctx);
			btn.setBackgroundResource(android.R.drawable.ic_menu_agenda);
			//btn.setImageResource(android.R.drawable.edit_text);
			btn.setOnClickListener(this);
			layout.addView(btn);
		}
		applyBinding();
		return layout;

	}

	@Override
	public void onClick(View view) {
		Calendar value = getCurrentValue();
		if (value==null) {
			value=Calendar.getInstance();
		}
		datePickerDialog = new DatePickerDialog(getRootContext(), 0, this, value.get(Calendar.YEAR), value.get(Calendar.MONTH), value.get(Calendar.DAY_OF_MONTH));
		datePickerDialog.show();
	}

	@Override
	public void onDateSet(DatePicker dialog, int year, int monthOfYear, int dayOfMonth) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, monthOfYear);
		cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		currentValue = cal;
		applyBinding();
	}

}
