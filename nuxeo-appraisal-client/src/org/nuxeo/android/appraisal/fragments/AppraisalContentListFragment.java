package org.nuxeo.android.appraisal.fragments;

import java.util.HashMap;
import java.util.Map;

import org.nuxeo.android.adapters.DocumentAttributeResolver;
import org.nuxeo.android.adapters.DocumentsListAdapter;
import org.nuxeo.android.appraisal.R;
import org.nuxeo.android.documentprovider.LazyDocumentsList;
import org.nuxeo.android.documentprovider.LazyUpdatableDocumentsList;
import org.nuxeo.android.fragments.BaseDocLayoutFragAct;
import org.nuxeo.android.fragments.BaseDocumentsListFragment;
import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;
import org.nuxeo.ecm.automation.client.jaxrs.model.PathRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertiesHelper;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyMap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AppraisalContentListFragment extends BaseDocumentsListFragment {

    public static final String ROOT_DOC_PARAM = "rootDoc";
    boolean emptyList = false;

	public AppraisalContentListFragment() {
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
    	View v = inflater.inflate(R.layout.listview_layout, container, false);

        waitingMessage = (TextView)v.findViewById(R.id.waitingMessage);
        listView = (ListView) v.findViewById(R.id.myList);
        registerDocTypesForCreation("Picture", "Picture");
        return v;
    }

    protected Map<Integer, String> getMapping() {
        Map<Integer, String> mapping = new HashMap<Integer, String>();
        mapping.put(R.id.title_entry, "dc:title");
        mapping.put(R.id.description, "dc:description");
        mapping.put(R.id.status_entry, "status");
        mapping.put(R.id.thumb, DocumentAttributeResolver.PICTUREURI
                + ":Small");
        return mapping;
    }

    @Override
    protected void onDocumentCreate(Document newDocument) {
        OperationRequest createOperation = getNuxeoSession().newRequest(
                "Picture.Create");

        PropertyMap dirty = newDocument.getDirtyProperties();
        if (dirty.get("file:content") != null) {
            dirty.map().put("originalPicture", dirty.get("file:content"));
//            dirty.map().remove("file:content");
        }
        String dirtyString = PropertiesHelper.toStringProperties(dirty);

        PathRef parent = new PathRef(newDocument.getParentPath());
        createOperation.setInput(parent).set("properties", dirtyString);
        if (newDocument.getName() != null) {
            createOperation.set("name", newDocument.getName());
        }
        documentsList.createDocument(newDocument, createOperation);
    }

	@Override
	protected LazyUpdatableDocumentsList fetchDocumentsList(byte cacheParam,
			String order) throws Exception {
		if(order == ""){
			order = " order by dc:modified desc";
		}
        Documents docs = getNuxeoContext().getDocumentManager().query(
                "select * from Document where ecm:mixinType != \"HiddenInNavigation\" AND ecm:currentLifeCycleState!='deleted' AND ecm:isCheckedInVersion = 0 AND ecm:parentId=?" + order,
                new String[] { getInitFragmentParam(ROOT_DOC_PARAM, Document.class).getId() },
                null, null, 0, 10, cacheParam);
        if (docs != null) {
        	if (docs.size()==0)
        	{
        		emptyList = true;
        	}
            return docs.asUpdatableDocumentsList();
        }
        throw new RuntimeException("fetch Operation did return null");
	}

	@Override
	protected void displayDocumentList(ListView listView,
			LazyDocumentsList documentsList) {
    	DocumentsListAdapter adapter = new DocumentsListAdapter(getActivity(),
                documentsList, R.layout.picture_item, getMapping());
        getActivity().setTitle(getInitFragmentParam(ROOT_DOC_PARAM, Document.class).getName() + " pictures");
        if(emptyList)
        {
        	Toast.makeText(getActivity().getBaseContext(), "No pictures to display", Toast.LENGTH_LONG).show();
        }
        listView.setAdapter(adapter);
	}

	@Override
	protected Document initNewDocument(String type) {
        if (documentsList == null) {
            return null;
        } else {
            return new Document(
            		getInitFragmentParam(ROOT_DOC_PARAM, Document.class).getPath(),
                    "appraisalPicture-" + documentsList.getCurrentSize(),
                    "File");
        }
	}

	@Override
	protected Class<? extends BaseDocLayoutFragAct> getEditActivityClass() {
		return LayoutFragActivity.class;
	}
	
}
