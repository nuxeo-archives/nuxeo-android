package org.nuxeo.android.contentprovider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyList;

import android.database.AbstractCursor;
import android.os.Bundle;

public class NuxeoDocumentCursor extends AbstractCursor {

	protected String[] columns;

	protected final UUIDMapper mapper;

	protected final LazyUpdatableDocumentsList docList;

	public NuxeoDocumentCursor (Session session, String nxql, String[] queryParams, String sortOrder, String schemas, int pageSize, UUIDMapper mapper) {
		if (mapper!=null) {
			this.mapper = mapper;
		} else {
			this.mapper = new UUIDMapper();
		}
		docList = new LazyUpdatebleDocumentsListImpl(session, nxql, queryParams, sortOrder, schemas, pageSize);
		docList.registerListener(new DocumentsListChangeListener() {
			@Override
			public void notifyContentChanged(int page) {
				onChange(true);
			}
		});
	}

	public NuxeoDocumentCursor (OperationRequest fetchOperation, String pageParametrerName) {
     	this.mapper = new UUIDMapper();
     	docList = new LazyUpdatebleDocumentsListImpl(fetchOperation, pageParametrerName);
		docList.registerListener(new DocumentsListChangeListener() {
			@Override
			public void notifyContentChanged(int page) {
				onChange(true);
			}
		});
	}

	@Override
	public boolean onMove(int oldPosition, int newPosition) {
		docList.setCurrentPosition(newPosition);
		return super.onMove(oldPosition, newPosition);
	}

	public Document getCurrentDocument() {
		return docList.getCurrentDocument();
	}

	protected Documents getCurrentPage() {
		return docList.getCurrentPage();
	}

	protected Long getCurrentIdentifier() {
		return mapper.getIdentifier(getCurrentDocument());
	}

	@Override
	public String[] getColumnNames() {
		if (columns == null) {
			List<String> cols = new ArrayList(getCurrentPage().get(0)
					.getProperties().getKeys());
			Collections.sort(cols);
			cols.add(0,"_ID");
			cols.add(1,"status");
			columns = cols.toArray(new String[0]);
		}
		return columns;
	}

	@Override
	public int getCount() {
		return docList.getCurrentSize();
	}

	@Override
	public double getDouble(int column) {
		return getCurrentDocument().getDouble(columns[column]).floatValue();
	}

	@Override
	public float getFloat(int column) {
		return getCurrentDocument().getDouble(columns[column]).floatValue();
	}

	@Override
	public int getInt(int column) {
		if (column==0) {
			return getCurrentIdentifier().intValue();
		}
		return getCurrentDocument().getLong(columns[column]).intValue();
	}

	@Override
	public long getLong(int column) {
		if (column==0) {
			return getCurrentIdentifier();
		}
		return getCurrentDocument().getLong(columns[column]);
	}

	@Override
	public short getShort(int column) {
		return getCurrentDocument().getLong(columns[column])
				.shortValue();
	}

	@Override
	public String getString(int column) {
		if (column==0) {
			return getCurrentIdentifier().toString();
		} else if (column==1) {
			return getCurrentDocument().getStatusFlag();
		}
		return getCurrentDocument().getString(columns[column]);
	}

	@Override
	public boolean isNull(int column) {
		return getCurrentDocument().getString(columns[column])==null;
	}

	@Override
	public void close() {
		super.close();
		for (Documents docs : docList.getLoadedPages()) {
			mapper.release(docs);
		}
	}

	@Override
	public Bundle getExtras() {
		Bundle bundle = new Bundle();
		Document doc = getCurrentDocument();
		Map<String, Object> data =  doc.getProperties().map();

		for (String k : data.keySet()) {
			Object val = data.get(k);
			if (val instanceof String) {
				bundle.putString(k, (String)val);
			} else if (val instanceof Boolean) {
				bundle.putBoolean(k, (Boolean)val);
			} else if (val instanceof Double) {
				bundle.putDouble(k, (Double)val);
			} else if (val instanceof Integer) {
				bundle.putInt(k, (Integer)val);
			} else if (val instanceof Long) {
				bundle.putLong(k, (Long)val);
			} else if (val instanceof PropertyList) {
				// XXX should do better
				PropertyList propList = (PropertyList) val;
				String[] stringArray = new String[propList.size()];
				for (int i = 0; i <propList.size(); i++) {
					stringArray[i] = propList.getString(i);
				}
				bundle.putStringArray(k, stringArray);
			}
		}
		return null;
	}

	public Document getDocument(int index) {
		return docList.getDocument(index);
	}

	public Integer getLoadingPagesCount() {
		return docList.getLoadingPagesCount();
	}

	public void documentChanged(Document doc) {
		docList.updateDocument(doc);
	}
}

