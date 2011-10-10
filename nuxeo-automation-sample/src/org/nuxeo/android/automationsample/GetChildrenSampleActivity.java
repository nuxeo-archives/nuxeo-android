package org.nuxeo.android.automationsample;


import org.nuxeo.android.activities.BaseDocumentLayoutActivity;
import org.nuxeo.android.documentprovider.LazyUpdatableDocumentsList;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.ecm.automation.client.cache.CacheBehavior;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import android.content.Intent;

public class GetChildrenSampleActivity extends BaseSampleDocumentsListActivity {

	public static final String ROOT_UUID_PARAM = "rootDocId";
	public static final String ROOT_DOC_PARAM = "rootDoc";

	protected Document root;

	protected String getRootUUID() {
		if (getRoot()!=null) {
			return getRoot().getId();
		}
		if (getIntent().getExtras()!=null) {
			return getIntent().getExtras().getString(ROOT_UUID_PARAM);
		}
		return null;
	}

	protected Document getRoot() {
		if (getIntent().getExtras()!=null) {
			Object param = getIntent().getExtras().get(ROOT_DOC_PARAM);
			if (param!=null && param instanceof Document) {
				root = (Document) param;
			}
		}
		return root;
	}

	@Override
	protected Object retrieveNuxeoData() throws Exception {
		if (getRoot()==null) {
			if (getRootUUID()==null) {
				root = getNuxeoContext().getDocumentManager().getRootDocument();
			} else {
				root = getNuxeoContext().getDocumentManager().getDocument(getRootUUID());
			}
		}
		// let base class fetch the list
		return super.retrieveNuxeoData();
	}

	@Override
	protected LazyUpdatableDocumentsList fetchDocumentsList() throws Exception {
		Documents docs = (Documents) getNuxeoContext().getDocumentManager().query(
				"select * from Document where ecm:mixinType != \"HiddenInNavigation\" AND ecm:isCheckedInVersion = 0 AND ecm:parentId=? order by dc:modified desc", new String[]{getRootUUID()}, null, null, 0, 10,
				CacheBehavior.STORE);
		if (docs!=null) {
			return docs.asUpdatableDocumentsList();
		}
		throw new RuntimeException("fetch Operation did return null");
	}

	@Override
	protected void onListItemClicked(int listItemPosition)
	{
		Document selectedDocument = getDocumentsList().getDocument(listItemPosition);
		if (selectedDocument.isFolder()) {
			startActivity(new Intent(getApplicationContext(), this.getClass()).putExtra(ROOT_DOC_PARAM, selectedDocument));
		} else {
			startActivity(new Intent(this, getEditActivityClass())
			.putExtra(BaseDocumentLayoutActivity.DOCUMENT, selectedDocument).putExtra(
					BaseDocumentLayoutActivity.MODE, LayoutMode.VIEW));
		}
	}

	@Override
	protected Document initNewDocument(String type) {
		return new Document(getRoot().getPath(),"newAndroidDoc",type);
	}

}
