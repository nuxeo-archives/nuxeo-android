package org.nuxeo.android.fragments;

import org.nuxeo.android.documentprovider.LazyUpdatableDocumentsList;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;


public class SimpleListFragment extends BaseSampleDocumentsListFragment {

	protected LazyUpdatableDocumentsList fetchDocumentsList(byte cacheParam, String order)
			throws Exception {
		if (order.equals("")) {
			order = " order by dc:modified desc";
		}
		Documents docs = getNuxeoContext()
				.getDocumentManager()
				.query(getBaseQuery() + order,
						null, null, null, 0, 10, cacheParam);
		if (docs != null) {
			return docs.asUpdatableDocumentsList();
		}
		throw new RuntimeException("fetch Operation did return null");
	}
	
	protected String getBaseQuery() {
		return "select * from Document where ecm:mixinType != \"HiddenInNavigation\" AND ecm:currentLifeCycleState!='deleted' AND ecm:isCheckedInVersion = 0";
	}
	
	

}
