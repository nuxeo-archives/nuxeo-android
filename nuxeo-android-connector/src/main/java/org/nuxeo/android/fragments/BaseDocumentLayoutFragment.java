package org.nuxeo.android.fragments;

import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.android.layout.NuxeoLayout;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.DocumentStatus;
import org.nuxeo.ecm.automation.client.jaxrs.model.IdRef;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

public abstract class BaseDocumentLayoutFragment extends BaseNuxeoFragment {

	public static final String DOCUMENT = "document";

    public static final String MODE = "mode";

    public static final String FIRST_CALL = "first call";

    protected Document currentDocument;
    
    protected Intent callingIntent;

    protected boolean requireAsyncFetch = true;

    public static final int MNU_SWITCH_EDIT = 1000;

    public static final int MNU_SWITCH_VIEW = 1001;

    protected NuxeoLayout layout;
    
    public BaseDocumentLayoutFragment() {
    }

    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Document currentDoc = getCurrentDocument();
        if (isCreateMode()) {
            // can not refresh from the server a not yet existing document
            requireAsyncFetch = false;
        } else {
            if (currentDoc.getStatusFlag() != DocumentStatus.SYNCHRONIZED) {
                // do not refresh if local update
                requireAsyncFetch = false;
            }
        }
        setHasOptionsMenu(true);
        
//        if (isEditMode()) {
//            setTitle("Edit " + currentDoc.getType() + " " + getCurrentDocument().getTitle());
//        } else if (isCreateMode()) {
//        	setTitle("Create new " + currentDoc.getType());
//        } else {
//        	setTitle("View " + currentDoc.getType() + " " + getCurrentDocument().getTitle());
//        }
    }

    protected abstract ViewGroup getLayoutContainer();

    protected void buildLayout() {
        layout = getAutomationClient().getLayoutService().getLayout(getActivity(),
                getCurrentDocument(), getLayoutContainer(), getMode());
    }

    protected Document getCurrentDocument() {
        if (currentDocument == null) {
        	Bundle args = getArguments();
            currentDocument = (Document) args.getSerializable(DOCUMENT);
        }
        return currentDocument;
    }

    protected LayoutMode getMode() {
    	Bundle args = getArguments();
        return (LayoutMode) args.getSerializable(MODE);
    }

    protected boolean isCreateMode() {
        return getMode() == LayoutMode.CREATE;
    }

    protected boolean isEditMode() {
        return getMode() == LayoutMode.EDIT;
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        currentDocument = (Document) data;
        buildLayout();
        if (layout == null) {
            Toast.makeText(getActivity().getBaseContext(), "Unable to get Layout", Toast.LENGTH_SHORT).show();
        } else {
            layout.refreshFromDocument(currentDocument);
            Toast.makeText(getActivity().getBaseContext(), "Refreshed document", Toast.LENGTH_SHORT).show();
        }
        requireAsyncFetch = false;
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        Document refreshedDocument = getNuxeoContext().getDocumentManager().getDocument(
                new IdRef(getCurrentDocument().getId()), true);
        return refreshedDocument;
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return requireAsyncFetch;
    }

    protected NuxeoLayout getLayout() {
        if (layout == null) {
            buildLayout();
        }
        return layout;
    }

//
////    @Override
////    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
////    	if (requestCode == BaseDocumentsListActivity.ACTION_EDIT_DOCUMENT && resultCode == RESULT_OK)
////    	{
////            Document doc = (Document) data.getExtras().get(DOCUMENT);
////            getLayout().applyChanges(doc);
////            setResult(RESULT_OK, new Intent().putExtra(DOCUMENT, doc));
////            this.finish();
////    	}
////        if (getLayout() != null) {
////            layout.onActivityResult(requestCode, resultCode, data);
////        }
////    }
//
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

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
//        if(Build.VERSION.SDK_INT >= 11) {
        	if (LayoutMode.VIEW == getMode()) {
                menu.add(Menu.NONE, MNU_SWITCH_EDIT, 2, "Switch to Edit").
                	setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            }
            if (LayoutMode.EDIT == getMode()) {
            	menu.add(Menu.NONE, MNU_SWITCH_VIEW, 2, "Switch to View").
            	setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            }
//        } else {
//        	if (LayoutMode.VIEW == getMode()) {
//                menu.add(Menu.NONE, MNU_SWITCH_EDIT, 0, "Switch to Edit");
//            }
//            if (LayoutMode.EDIT == getMode()) {
//                menu.add(Menu.NONE, MNU_SWITCH_VIEW, 0, "Switch to View");
//            }
//        }
        super.onPrepareOptionsMenu(menu);
    }
    
    public boolean isFirstCall() {
    	return getArguments().getBoolean(FIRST_CALL);
    }
    
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//    	Bundle args = getArguments();
//    	boolean firstCall = args.getBoolean(FIRST_CALL);
//    	if (!firstCall) {
//    		
//    	}
//    	
//    	return super.onOptionsItemSelected(item);
//    }

//    protected abstract void populateMenu(Menu menu);
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//    	if (callingIntent.getBooleanExtra(BaseDocumentLayoutActivity.FIRST_CALL, true) == false)
//    	{
//    		finish();
//    	} else {
//    	
//	        switch (item.getItemId()) {
//	        case MNU_SWITCH_EDIT:
////	            restart(MODE, LayoutMode.EDIT);
//	            Intent editIntent = new Intent(new Intent(this, this.getClass())
//	        	.putExtra(BaseDocumentLayoutActivity.DOCUMENT, currentDocument)
//	        	.putExtra(BaseDocumentLayoutActivity.MODE, LayoutMode.EDIT)
//	        	.putExtra(BaseDocumentLayoutActivity.FIRST_CALL, false));
//	        startActivityForResult(editIntent, BaseDocumentsListActivity.ACTION_EDIT_DOCUMENT);
//	            return true;
//	        case MNU_SWITCH_VIEW:
//	//            restart(MODE, LayoutMode.VIEW);
//	        	Intent viewIntent = new Intent(new Intent(this, this.getClass())
//	        	.putExtra(BaseDocumentLayoutActivity.DOCUMENT, getCurrentDocument())
//	        	.putExtra(BaseDocumentLayoutActivity.MODE, LayoutMode.VIEW)
//	        	.putExtra(BaseDocumentLayoutActivity.FIRST_CALL, false));
//	        startActivityForResult(viewIntent, BaseDocumentsListActivity.ACTION_EDIT_DOCUMENT);
//	            return true;
//	        }
//    	}
//    	return super.onOptionsItemSelected(item);
//    }
}
