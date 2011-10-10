package org.nuxeo.android.automationsample;

import org.nuxeo.android.documentprovider.DocumentProvider;
import org.nuxeo.android.documentprovider.LazyUpdatableDocumentsList;

public class DocumentProviderViewActivity extends BaseSampleDocumentsListActivity {

	public static final String PROVIDER_NAME_PARAM = "providerName";

	@Override
	protected LazyUpdatableDocumentsList fetchDocumentsList() throws Exception {
		DocumentProvider docProvider = getAutomationClient().getDocumentProvider();
		String providerName = getIntent().getExtras().getString(PROVIDER_NAME_PARAM);
		return docProvider.getDocumentsList(providerName, getNuxeoSession());
	}

}
