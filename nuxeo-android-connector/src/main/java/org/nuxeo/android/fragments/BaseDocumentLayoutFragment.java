package org.nuxeo.android.fragments;

import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.android.layout.NuxeoLayout;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.DocumentStatus;
import org.nuxeo.ecm.automation.client.jaxrs.model.IdRef;

import android.app.Activity;
import android.support.v4.app.FragmentTransaction;
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
    
    public static final String FRAGMENT_CONTAINER_ID = "fragContainerId";

    protected Document currentDocument;
    
    protected Intent callingIntent;

    protected boolean requireAsyncFetch = true;

    public static final int MNU_SWITCH_EDIT = 1000;

    public static final int MNU_SWITCH_VIEW = 1001;

    protected NuxeoLayout layout;
    
    protected BaseDocumentLayoutFragment documentLayoutFragment = null;
    
    public BaseDocumentLayoutFragment() {
    }
    
    Integer containerId = null;

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
        mCallback.saveDocument(doc);
//        setResult(RESULT_OK, new Intent().putExtra(DOCUMENT, doc));
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
    


//    protected abstract void populateMenu(Menu menu);
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//    	if (callingIntent.getBooleanExtra(BaseDocumentLayoutActivity.FIRST_CALL, true) == false)
//    	{
//    		finish();
//    	} 
//		return super.onOnptionsItemSelected(item);
//    }
    
	public interface Callback {

		// public void exchangeFragments();

		public int getFragmentContainerId();
		
		public void saveDocument(Document doc);
	}

	protected Callback mCallback;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callback)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallback = (Callback) activity;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		
		exchangeFragments();
//		if (isFirstCall() == false) {
//			exchangeFragments();
//		} else {
//			switch (item.getItemId()) {
//			case MNU_SWITCH_EDIT:
//				switchToView();
//				break;
//			case MNU_SWITCH_VIEW:
//				switchToEdit();
//				break;
//			}
//		}
		return true;
	}

	private void exchangeFragments() {
		
		Bundle args = getArguments();
		//should be removed
		args.putSerializable(DOCUMENT, currentDocument);
		if (isEditMode()) {
			args.putSerializable(MODE, LayoutMode.VIEW);
		} else{
			args.putSerializable(MODE, LayoutMode.EDIT);
		}
    	args.putBoolean(FIRST_CALL, false);
		args.putInt(BaseDocumentLayoutFragment.FRAGMENT_CONTAINER_ID,
				getContainerId());
    	getDocumentLayoutFragment().setArguments(args);
		
		FragmentTransaction transaction = getFragmentManager().beginTransaction();

    	transaction.replace(getContainerId(), getDocumentLayoutFragment());
    	
		transaction.commit();
	}

//	public void switchToView() {
//		FragmentTransaction contentTransaction = getFragmentManager()
//				.beginTransaction();
//
//		Bundle args = getArguments();
//		args.putSerializable(BaseDocumentLayoutFragment.DOCUMENT,
//				currentDocument);
//		args.putSerializable(BaseDocumentLayoutFragment.MODE, LayoutMode.VIEW);
//		args.putBoolean(BaseDocumentLayoutFragment.FIRST_CALL, false);
//		args.putInt(BaseDocumentLayoutFragment.FRAGMENT_CONTAINER_ID,
//				getContainerId());
//		getDocumentLayoutFragment().setArguments(args);
//		// secondFrag = documentLayoutFrag;
//
//		// contentTransaction.detach(firstFragment);
//		contentTransaction
//				.replace(containerId, getDocumentLayoutFragment());
//		contentTransaction.commit();
//	}
	
	public abstract BaseDocumentLayoutFragment getDocumentLayoutFragment();
	
	protected int getContainerId() {
		if (containerId == null) {
			containerId = mCallback.getFragmentContainerId();
		}
		return containerId;
	}
	
//	public void switchToEdit() {
//		FragmentTransaction contentTransaction = getFragmentManager()
//				.beginTransaction();
//
//		Bundle args = getArguments();
//		args.putSerializable(BaseDocumentLayoutFragment.DOCUMENT,
//				currentDocument);
//		args.putSerializable(BaseDocumentLayoutFragment.MODE, LayoutMode.EDIT);
//		args.putBoolean(BaseDocumentLayoutFragment.FIRST_CALL, false);
//		args.putInt(BaseDocumentLayoutFragment.FRAGMENT_CONTAINER_ID,
//				getContainerId());
//		getDocumentLayoutFragment().setArguments(args);
//		// secondFrag = documentLayoutFrag;
//
//		// contentTransaction.detach(firstFragment);
//		contentTransaction
//				.replace(containerId, getDocumentLayoutFragment());
//		contentTransaction.commit();
//	}
	
}

