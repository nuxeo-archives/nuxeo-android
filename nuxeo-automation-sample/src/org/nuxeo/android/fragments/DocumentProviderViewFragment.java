package org.nuxeo.android.fragments;

import org.nuxeo.android.documentprovider.DocumentProvider;
import org.nuxeo.android.documentprovider.LazyUpdatableDocumentsList;

import android.os.Bundle;

public class DocumentProviderViewFragment extends
		BaseSampleDocumentsListFragment {

	public DocumentProviderViewFragment() {
	}
	
	public static final String PROVIDER_NAME_PARAM = "providerName";

    @Override
    protected LazyUpdatableDocumentsList fetchDocumentsList(byte cacheParam, String order)
            throws Exception {
        DocumentProvider docProvider = getAutomationClient().getDocumentProvider();
        Bundle args = getArguments();
        String providerName = args.getString(PROVIDER_NAME_PARAM);
        return docProvider.getDocumentsList(providerName, getNuxeoSession());
    }

	@Override
	protected String getBaseQuery() {
		// TODO Auto-generated method stub
		return null;
	}
}
