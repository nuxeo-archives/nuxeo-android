package org.nuxeo.android.documentprovider;

import org.nuxeo.android.testsfrag.BaseSampleDocumentsListFragment;

import android.os.Bundle;

public class DocumentProviderViewFragment extends
		BaseSampleDocumentsListFragment {

	public DocumentProviderViewFragment() {
	}
	
	public static final String PROVIDER_NAME_PARAM = "providerName";

    @Override
    protected LazyUpdatableDocumentsList fetchDocumentsList(byte cacheParam)
            throws Exception {
        DocumentProvider docProvider = getAutomationClient().getDocumentProvider();
        Bundle args = getArguments();
        String providerName = args.getString(PROVIDER_NAME_PARAM);
        return docProvider.getDocumentsList(providerName, getNuxeoSession());
    }
}
