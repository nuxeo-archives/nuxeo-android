package org.nuxeo.android.simpleclient.forms;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.android.simpleclient.R;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyList;

import android.app.Activity;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LinearFormManager {

    protected static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy hh:mm:ss");

    public static String DEFAULT_FIELDS = "[ { xpath : 'dc:creator', label : 'Creator'}, " +
    		" { xpath : 'dc:lastContributor', label : 'Last contributor'} ," +
    		" { xpath : 'dc:created', label : 'Creation date'} ," +
    		" { xpath : 'dc:modified', label : 'Modification date'} ]";

    public static void displayForm(Activity targetActivity,
            LinearLayout currentLayout, Document currentDocument) throws JSONException {
       displayForm(targetActivity, currentLayout, currentDocument,DEFAULT_FIELDS, true);
    }

    public  static void displayForm(Activity targetActivity,LinearLayout currentLayout,  Document doc, String fieldDef, boolean showHeader) throws JSONException {
        displayForm(targetActivity, currentLayout, doc ,new JSONArray(fieldDef), showHeader);
    }

    public  static void displayForm(Activity targetActivity,LinearLayout currentLayout,  Document doc, JSONArray fieldArray, boolean showHeader) throws JSONException {

        final int padding = targetActivity.getResources().getDimensionPixelSize(R.dimen.defaultPadding);

        if (showHeader) {
            final TextView hTextView = new TextView(targetActivity);
            String header ="&nbsp;&nbsp;<b><i>State</i></b> : "  + doc.getState();
            String size = doc.getProperties().getString("common:size");
            if (size!=null && !"null".equals(size)) {
                header += "  &nbsp;&nbsp; <b><i> Size </i></b> : " + Integer.parseInt(size)/1024 + "KB";
            }
            hTextView.setText(Html.fromHtml(header));
            currentLayout.addView(hTextView);

            String desc =doc.getProperties().getString("dc:description");
            if (desc!=null && ! "null".equals(desc)) {
                desc ="&nbsp;&nbsp;<b><i>Description</i></b> : "  + doc.getProperties().getString("dc:description");
                final TextView descTextView = new TextView(targetActivity);
                descTextView.setText(Html.fromHtml(desc));
                currentLayout.addView(descTextView);
            }
        }

        for (int i = 0; i< fieldArray.length(); i++) {
            JSONObject field = fieldArray.getJSONObject(i);

            String xpath = field.getString("xpath");
            Object value = doc.getProperties().get(xpath);
            if ("null".equals(value)) {
                value=null;
            }

            if (value!=null) {
                final TextView textView = new TextView(targetActivity);
                String txt = "<b><i>" + field.getString("label") + "</i></b> : ";

                if (value instanceof String) {
                    txt += (String) value;
                } else if (value instanceof Date) {
                    txt += dateFormat.format((Date) value);
                } else if (value instanceof PropertyList) {
                    PropertyList list = (PropertyList) value;
                    for (int j = 0; j< list.size(); j++) {
                        txt += list.getString(j);
                        txt += " ";
                    }
                }
                textView.setText(Html.fromHtml(txt));
                textView.setPadding(padding, 0, padding,0);
                currentLayout.addView(textView);
            }
        }
    }
}
