package org.nuxeo.android.adapters;

import java.util.Map;

import org.nuxeo.android.cursor.UUIDMapper;
import org.nuxeo.android.documentprovider.DocumentsListChangeListener;
import org.nuxeo.android.documentprovider.LazyDocumentsList;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

public class DocumentsListAdapter extends BaseAdapter implements ListAdapter {

	protected LayoutInflater inflater;

	protected final LazyDocumentsList docList;

	protected int currentCount=-1;

	protected final UUIDMapper mapper;

	protected final int layoutId;

	protected final Map<Integer, String> documentAttributesMapping;

	protected Handler handler;

	public DocumentsListAdapter(Context context, LazyDocumentsList docList, int layoutId, Map<Integer, String> documentAttributesMapping) {
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.docList = docList;
		this.mapper = new UUIDMapper();
		this.layoutId = layoutId;
		this.documentAttributesMapping = documentAttributesMapping;
		registerEventListener();
	}

	protected void registerEventListener() {
		// enforce UI Thread for the List resfresh : even if this seems strange to have to do this ...
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				notifyDataSetChanged();
			}
		};

		docList.registerListener(new DocumentsListChangeListener() {
			@Override
			public void notifyContentChanged(int page) {
				currentCount=-1;
				handler.sendEmptyMessage(page);
			}
		});
	}

	@Override
	public int getCount() {
		if (currentCount<0) {
			currentCount = docList.getCurrentSize();
		}
		return currentCount;
	}


	protected Document getDocument(int position) {
		// XXX use this for now to be sure to trigger lazy fetch
		// => to be replaced by docList.getDocument(position);
		docList.setCurrentPosition(position);
		return docList.getCurrentDocument();
	}

	@Override
	public Object getItem(int position) {
		return getDocument(position);
	}

	@Override
	public long getItemId(int position) {
		return mapper.getIdentifier(getDocument(position));
	}

	@Override
	public View getView(int position, View recycledView, ViewGroup parent) {

		View currentView = recycledView;
		if (currentView==null) {
			currentView = createNewView(position, parent);
		}
		bindViewToDocument(position, getDocument(position), currentView);
		return currentView;
	}

	protected View createNewView(int position, ViewGroup parent) {
		return inflater.inflate(layoutId, parent,false);
	}

	protected void bindViewToDocument(int position, Document doc, View view) {
		for (Integer idx : documentAttributesMapping.keySet()) {
			View widget = view.findViewById(idx);
			bindWidgetToDocumentAttribute(widget, doc, documentAttributesMapping.get(idx));
		}
	}

	protected void bindWidgetToDocumentAttribute(View widget, Document doc, String attribute) {
		if (widget instanceof TextView) {
			((TextView)widget).setText(DocumentAttributeResolver.getString(doc, attribute));
		}
		else if (widget instanceof ImageView) {
			Uri uri = (Uri) DocumentAttributeResolver.get(doc, attribute);
			if (uri!=null) {
				((ImageView)widget).setImageURI((Uri) DocumentAttributeResolver.get(doc, attribute));
			}
		}
	}
}
