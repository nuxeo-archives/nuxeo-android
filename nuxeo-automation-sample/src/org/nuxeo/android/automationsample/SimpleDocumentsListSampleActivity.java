package org.nuxeo.android.automationsample;


import org.nuxeo.android.documentprovider.LazyUpdatableDocumentsList;
import org.nuxeo.ecm.automation.client.cache.CacheBehavior;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;


public class SimpleDocumentsListSampleActivity extends BaseSampleDocumentsListActivity {

	@Override
	protected LazyUpdatableDocumentsList fetchDocumentsList() throws Exception {
		Documents docs = (Documents) getNuxeoContext().getDocumentManager().query(
				"select * from Document where ecm:mixinType != \"HiddenInNavigation\" AND ecm:isCheckedInVersion = 0 order by dc:modified desc", null, null, null, 0, 10,
				CacheBehavior.STORE);
		if (docs!=null) {
			return docs.asUpdatableDocumentsList();
		}
		throw new RuntimeException("fetch Operation did return null");
	}


}
