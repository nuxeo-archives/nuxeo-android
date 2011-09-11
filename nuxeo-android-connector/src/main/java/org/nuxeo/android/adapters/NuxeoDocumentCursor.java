package org.nuxeo.android.adapters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.nuxeo.android.documentprovider.DocumentsListChangeListener;
import org.nuxeo.android.documentprovider.LazyDocumentsList;
import org.nuxeo.android.documentprovider.LazyDocumentsListImpl;
import org.nuxeo.android.documentprovider.LazyUpdatableDocumentsList;
import org.nuxeo.android.documentprovider.LazyUpdatableDocumentsListImpl;
import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyList;



import android.database.AbstractCursor;
import android.os.Bundle;

public class NuxeoDocumentCursor extends AbstractCursor {

	protected static final String[] FIXED_COLUMNS={"_ID",DocumentAttributeResolver.STATUS, DocumentAttributeResolver.ICONURI};

	protected String[] columns;

	protected final UUIDMapper mapper;

	protected final LazyDocumentsList docList;

	protected final boolean updatable;

	public NuxeoDocumentCursor (Session session, String nxql, String[] queryParams, String sortOrder, String schemas, int pageSize, UUIDMapper mapper, boolean updatable) {
		if (mapper!=null) {
			this.mapper = mapper;
		} else {
			this.mapper = new UUIDMapper();
		}
		this.updatable=updatable;
		if (updatable) {
			docList = new LazyUpdatableDocumentsListImpl(session, nxql, queryParams, sortOrder, schemas, pageSize);
		} else {
			docList = new LazyDocumentsListImpl(session, nxql, queryParams, sortOrder, schemas, pageSize);
		}
		registerEventListener();
	}

	public NuxeoDocumentCursor (OperationRequest fetchOperation, String pageParametrerName, boolean updatable) {
     	this.mapper = new UUIDMapper();
     	this.updatable=updatable;
     	if (updatable) {
     		docList = new LazyUpdatableDocumentsListImpl(fetchOperation, pageParametrerName);
     	} else {
     		docList = new LazyDocumentsListImpl(fetchOperation, pageParametrerName);
     	}
     	registerEventListener();
	}

	public NuxeoDocumentCursor (LazyDocumentsList docList) {
     	this.mapper = new UUIDMapper();
     	this.docList = docList;
     	if (docList.getClass().isAssignableFrom(LazyUpdatableDocumentsList.class)) {
     		this.updatable=true;
     	} else {
     		this.updatable=false;
     	}
     	registerEventListener();
	}

	protected void registerEventListener() {
		docList.registerListener(new DocumentsListChangeListener() {
			@Override
			public void notifyContentChanged(int page) {
				onChange(true);
				requery();
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
			List<String> cols = new ArrayList<String>(getCurrentPage().get(0)
					.getProperties().getKeys());
			Collections.sort(cols);
			cols.addAll(0, Arrays.asList(FIXED_COLUMNS));
			//cols.add(0,"_ID");
			//cols.add(1,"status");
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
		}
		if (column < FIXED_COLUMNS.length) {
			return DocumentAttributeResolver.getString(getCurrentDocument(), FIXED_COLUMNS[column]);
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

	public LazyDocumentsList getDocumentsList() {
		return docList;
	}

	public LazyUpdatableDocumentsList getUpdatableDocumentsList() {
		if (updatable) {
			return (LazyUpdatableDocumentsList) docList;
		}
		throw new UnsupportedOperationException("DocumentList is readOnly");
	}

}

