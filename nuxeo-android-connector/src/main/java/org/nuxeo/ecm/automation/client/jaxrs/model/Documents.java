/*
 * (C) Copyright 2006-2008 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     bstefanescu
 */
package org.nuxeo.ecm.automation.client.jaxrs.model;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.android.adapters.NuxeoDocumentCursor;
import org.nuxeo.android.documentprovider.LazyDocumentsList;
import org.nuxeo.android.documentprovider.LazyDocumentsListImpl;
import org.nuxeo.android.documentprovider.LazyUpdatableDocumentsList;
import org.nuxeo.android.documentprovider.LazyUpdatableDocumentsListImpl;
import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;


/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * @author tiry
 */
public class Documents extends ArrayList<Document> implements OperationInput {

    private static final long serialVersionUID = 1L;

    protected boolean batched = false;

    protected int pageSize=0;

    protected int pageIndex=0;

    protected int pageCount=0;

    protected int totalSize=0;

    protected OperationRequest sourceRequest;

    public Documents() {
    }

    public Documents(int size) {
        super(size);
    }

    public Documents(int size,int totalSize, int pageSize, int pageIndex, int pageCount) {
        super(size);
        batched=true;
        this.totalSize=totalSize;
        this.pageSize=pageSize;
        this.pageIndex=pageIndex;
        this.pageCount=pageCount;
    }

    public Documents(Documents docs,int totalSize, int pageSize, int pageIndex, int pageCount) {
        super(docs);
        batched=true;
        this.totalSize=totalSize;
        this.pageSize=pageSize;
        this.pageIndex=pageIndex;
        this.pageCount=pageCount;
    }

    public Documents(Documents docs) {
        super(docs);
    }

    public String getInputType() {
        return "documents";
    }

    public boolean isBinary() {
        return false;
    }

    public String getInputRef() {
        StringBuilder buf = new StringBuilder();
        int size = size();
        if (size == 0) {
            return buf.toString();
        }
        buf.append(get(0).getId());
        for (int i = 1; i < size; i++) {
            buf.append(",").append(get(i).getId());
        }
        return buf.toString();
    }

    public String toString() {
        StringBuilder buf = new StringBuilder("docs:");
        int size = size();
        if (size == 0) {
            return buf.toString();
        }
        buf.append(get(0).getId());
        for (int i = 1; i < size; i++) {
            buf.append(",").append(get(i).getId());
        }
        return buf.toString();
    }

    public String dump() {
        return super.toString();
    }

	public boolean isBatched() {
		return batched;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public int getPageCount() {
		return pageCount;
	}

	public int getTotalSize() {
		return totalSize;
	}

	public OperationRequest getSourceRequest() {
		return sourceRequest;
	}

	public void setSourceRequest(OperationRequest sourceRequest) {
		this.sourceRequest = sourceRequest;
	}

	public NuxeoDocumentCursor asCursor(String pageParameterName) {
		NuxeoDocumentCursor cursor = new NuxeoDocumentCursor(sourceRequest, pageParameterName, false);
		return cursor;
	}

	public NuxeoDocumentCursor asCursor() {
		return asCursor("page");
	}

	public NuxeoDocumentCursor asUpdatableCursor(String pageParameterName) {
		NuxeoDocumentCursor cursor = new NuxeoDocumentCursor(sourceRequest, pageParameterName, true);
		return cursor;
	}

	public LazyDocumentsList asDocumentsList() {
		return new LazyDocumentsListImpl(sourceRequest, "page");
	}

	public LazyUpdatableDocumentsList asUpdatableDocumentsList() {
  	    return new LazyUpdatableDocumentsListImpl(sourceRequest, "page");
	}

	public void removeById(String uid) {
		for (int i = 0; i< this.size(); i++) {
			if (uid.equals(get(i).getId())) {
				remove(i);
				break;
			}
		}
	}

	public boolean containsDocWithId(String uid) {
		for (int i = 0; i< this.size(); i++) {
			if (uid.equals(get(i).getId())) {
				return true;
			}
		}
		return false;
	}

	public Document getById(String uid) {
		for (int i = 0; i< this.size(); i++) {
			if (uid.equals(get(i).getId())) {
				return get(i);
			}
		}
		return null;
	}


	public List<String> getIds() {
		List<String> ids = new ArrayList<String>();
		for (int i = 0; i< this.size(); i++) {
			ids.add(get(i).getId());
		}
		return ids;
	}



}
