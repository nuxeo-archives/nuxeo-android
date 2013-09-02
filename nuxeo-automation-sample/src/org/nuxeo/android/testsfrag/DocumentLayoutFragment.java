package org.nuxeo.android.testsfrag;

import org.nuxeo.android.automationsample.R;
import org.nuxeo.android.fragments.BaseDocumentLayoutFragment;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.android.layout.NuxeoLayout;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.DocumentStatus;
import org.nuxeo.ecm.automation.client.jaxrs.model.IdRef;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class DocumentLayoutFragment extends BaseDocumentLayoutFragment {

    public static final String DOCUMENT = "document";

    public static final String MODE = "mode";

    public static final String FIRST_CALL = "first call";

    protected Document currentDocument;
    
    protected Intent callingIntent;

    protected boolean requireAsyncFetch = true;

    public static final int MNU_SWITCH_EDIT = 1000;

    public static final int MNU_SWITCH_VIEW = 1001;

    protected NuxeoLayout layout;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.createeditlayout, container, false);
        
//        if (isEditMode()) {
//            setTitle("Edit " + currentDoc.getType() + " " + getCurrentDocument().getTitle());
//        } else if (isCreateMode()) {
//        	setTitle("Create new " + currentDoc.getType());
//        } else {
//        	setTitle("View " + currentDoc.getType() + " " + getCurrentDocument().getTitle());
//        }
		
        return v;
	}

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//    	if (requestCode == BaseDocumentsListActivity.ACTION_EDIT_DOCUMENT && resultCode == RESULT_OK)
//    	{
//            Document doc = (Document) data.getExtras().get(DOCUMENT);
//            getLayout().applyChanges(doc);
//            setResult(RESULT_OK, new Intent().putExtra(DOCUMENT, doc));
//            this.finish();
//    	}
//        if (getLayout() != null) {
//            layout.onActivityResult(requestCode, resultCode, data);
//        }
//    }

	public DocumentLayoutFragment() {
		super();
	}

	@Override
	protected ViewGroup getLayoutContainer() {
		return (ViewGroup) getView().findViewById(R.id.layoutContainer);
	}
	
    protected void saveDocument() {
        Document doc = getCurrentDocument();
        getLayout().applyChanges(doc);
//        setResult(RESULT_OK, new Intent().putExtra(DOCUMENT, doc));
//        this.finish();
    }

    protected void cancelUpdate() {
//        Document doc = getCurrentDocument();
//        setResult(RESULT_CANCELED, new Intent().putExtra(DOCUMENT, doc));
//        this.finish();
    }

}
