package org.nuxeo.android.adapters;

import org.nuxeo.android.documentprovider.DocumentsListChangeListener;
import org.nuxeo.android.documentprovider.LazyDocumentsList;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class AbstractDocumentListAdapter extends BaseAdapter {

	protected LayoutInflater inflater;
	protected final LazyDocumentsList docList;
	protected int currentCount = -1;
	protected final UUIDMapper mapper;
	protected Handler handler;

	public AbstractDocumentListAdapter(Context context, LazyDocumentsList docList) {
		super();
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.docList = docList;
		this.mapper = new UUIDMapper();
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

		Document doc= getDocument(position);
		View currentView = recycledView;
		if (currentView==null) {
			currentView = createNewView(position, doc, inflater, parent);
		}
		bindViewToDocument(position, doc, currentView);
		return currentView;
	}

	protected abstract View createNewView(int position, Document doc, LayoutInflater inflater, ViewGroup parent);

	protected abstract void bindViewToDocument(int position, Document doc, View view);

}