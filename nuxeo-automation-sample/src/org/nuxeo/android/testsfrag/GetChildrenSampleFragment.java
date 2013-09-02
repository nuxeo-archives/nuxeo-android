package org.nuxeo.android.testsfrag;

import org.nuxeo.android.activities.BaseDocumentLayoutActivity;
import org.nuxeo.android.automationsample.R;
import org.nuxeo.android.documentprovider.LazyUpdatableDocumentsList;
import org.nuxeo.android.fragments.BaseDocumentLayoutFragment;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

public class GetChildrenSampleFragment extends BaseSampleDocumentsListFragment {

	public static final String ROOT_UUID_PARAM = "rootDocId";

    public static final String ROOT_DOC_PARAM = "rootDoc";

    protected Document root;

    protected String getRootUUID() {
        if (getRoot() != null) {
            return getRoot().getId();
        }
        if (getActivity().getIntent().getExtras() != null) {
            return getActivity().getIntent().getExtras().getString(ROOT_UUID_PARAM);
        }
        return null;
    }

    protected Document getRoot() {
        if (getArguments() != null) {
            Object param = getArguments().getSerializable(ROOT_DOC_PARAM);
            if (param != null && param instanceof Document) {
                root = (Document) param;
            }
        }
        return root;
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        if (getRoot() == null) {
            if (getRootUUID() == null) {
                root = getNuxeoContext().getDocumentManager().getRootDocument();
            } else {
                root = getNuxeoContext().getDocumentManager().getDocument(
                        getRootUUID());
            }
        }
        // let base class fetch the list
        return super.retrieveNuxeoData();
    }

    @Override
    protected LazyUpdatableDocumentsList fetchDocumentsList(byte cacheParam)
            throws Exception {
        Documents docs = getNuxeoContext().getDocumentManager().query(
                "select * from Document where ecm:mixinType != \"HiddenInNavigation\" AND ecm:isCheckedInVersion = 0 AND ecm:parentId=? order by dc:modified desc",
                new String[] { getRootUUID() }, null, null, 0, 10, cacheParam);
        if (docs != null) {
            return docs.asUpdatableDocumentsList();
        }
        throw new RuntimeException("fetch Operation did return null");
    }

    @Override
    public void onListItemClicked(int listItemPosition) {
        Document selectedDocument = getDocumentsList().getDocument(
                listItemPosition);
        if (selectedDocument.isFolder()) {
        	
        	Bundle args = new Bundle();
        	args.putSerializable(ROOT_DOC_PARAM, selectedDocument);
        	GetChildrenSampleFragment childrenSampleFragment = new GetChildrenSampleFragment();
        	childrenSampleFragment.setArguments(args);
        	
        	FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
    		transaction.replace(R.id.list_frag_container, childrenSampleFragment);
    		transaction.addToBackStack(null);
    		transaction.commit();
        } else {
        	mCallback.onItemSelected(documentsList, listItemPosition);
//        	Toast.makeText(getActivity().getBaseContext(), "Doesn't work as fragment yet", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(getActivity().getBaseContext(), getEditActivityClass()).putExtra(
//                    BaseDocumentLayoutActivity.DOCUMENT, selectedDocument).putExtra(
//                    BaseDocumentLayoutActivity.MODE, LayoutMode.VIEW));
        }
    }

    @Override
    protected Document initNewDocument(String type) {
        return new Document(getRoot().getPath(), "newAndroidDoc", type);
    }

}
