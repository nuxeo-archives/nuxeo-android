package org.nuxeo.android.fragments;

import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.android.layout.NuxeoLayout;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.DocumentStatus;
import org.nuxeo.ecm.automation.client.jaxrs.model.IdRef;

import android.app.Activity;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
    
    protected static final int ACTION_EDIT_DOCUMENT = 0;
    
    public BaseDocumentLayoutFragment() {
    }
    
    Integer containerId = null;
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	buildLayout();
    }

    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isCreateMode()) {
            // can not refresh from the server a not yet existing document
            requireAsyncFetch = false;
        } else {
            if (getCurrentDocument().getStatusFlag() != DocumentStatus.SYNCHRONIZED) {
                // do not refresh if local update
                requireAsyncFetch = false;
            }
        }
        if (isEditMode()) {
            getActivity().setTitle("Edit ");
        } else if (isCreateMode()) {
        	getActivity().setTitle("Create");
        } else {
        	getActivity().setTitle("View ");
        }
    }

    protected abstract ViewGroup getLayoutContainer();

    protected void buildLayout() {
        layout = getAutomationClient().getLayoutService().getLayout(this,
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
        //Check that the fragment hasn't been hidden before onNuxeoDataRetrieved is called
        if(getActivity()!=null && getActivity().getBaseContext()!=null) {
            currentDocument = (Document) data;
            if (layout == null) {
                Toast.makeText(getActivity().getBaseContext(), "Unable to get Layout", Toast.LENGTH_SHORT).show();
            } else {
                layout.refreshFromDocument(currentDocument);
                Toast.makeText(getActivity().getBaseContext(), "Refreshed document", Toast.LENGTH_SHORT).show();
            }
            requireAsyncFetch = false;
            if (isEditMode()) {
                getActivity().setTitle("Edit " + getCurrentDocument().getType() + " " + getCurrentDocument().getTitle());
            } else if (isCreateMode()) {
                getActivity().setTitle("Create new " + getCurrentDocument().getType());
            } else {
                getActivity().setTitle("View " + getCurrentDocument().getType() + " " + getCurrentDocument().getTitle());
            }
            setHasOptionsMenu(true);
        }
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
    
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (getLayout() != null) {
            layout.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

	protected void saveDocument() {
		Document doc = getCurrentDocument();
		getLayout().applyChanges(doc);
		LayoutMode mode = (LayoutMode) getArguments().getSerializable(MODE);
		if (mode == LayoutMode.EDIT) {
			if (mCallback.isTwoPane()) {
				BaseDocumentsListFragment listFragment = (BaseDocumentsListFragment) getFragmentManager()
						.findFragmentById(
								mCallback.getListFragmentContainerId());
				listFragment.saveDocument(doc);
			} else {
				getActivity().setResult(
						Activity.RESULT_OK,
						new Intent().putExtra(
								BaseDocumentLayoutFragment.DOCUMENT, doc));
				getActivity().finish();
			}
		} else {
			if (mCallback.isTwoPane()) {
				BaseDocumentsListFragment listFragment = (BaseDocumentsListFragment) getFragmentManager()
						.findFragmentById(
								mCallback.getListFragmentContainerId());
				listFragment.saveNewDocument(doc);
			} else {
				getActivity().setResult(
						Activity.RESULT_OK,
						new Intent().putExtra(
								BaseDocumentLayoutFragment.DOCUMENT, doc));
				getActivity().finish();
			}
		}
	}

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){
    	super.onCreateOptionsMenu(menu, inflater);
        if(Build.VERSION.SDK_INT >= 11) {
        	if (LayoutMode.VIEW == getMode()) {
        	    if (mCallback.isTwoPane()) {
        	        menu.add(Menu.NONE, MNU_SWITCH_EDIT, 2, "Edit").
                    setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        	    } else {
        	        menu.add(Menu.NONE, MNU_SWITCH_EDIT, 2, "Edit Document").
                    setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        	    }
            }
            if (LayoutMode.EDIT == getMode()) {
                if (mCallback.isTwoPane()) {
                	menu.add(Menu.NONE, MNU_SWITCH_VIEW, 2, "View").
                	setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                } else {
                    menu.add(Menu.NONE, MNU_SWITCH_VIEW, 2, "Back to View").
                    setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                }
            }
        } else {
        	if (LayoutMode.VIEW == getMode()) {
                menu.add(Menu.NONE, MNU_SWITCH_EDIT, 0, "Switch to Edit");
            }
            if (LayoutMode.EDIT == getMode()) {
                menu.add(Menu.NONE, MNU_SWITCH_VIEW, 0, "Switch to View");
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }
    
    public boolean isFirstCall() {
    	return getArguments().getBoolean(FIRST_CALL);
    }
    
	public interface Callback {

		public int getLayoutFragmentContainerId();
		
		public boolean isTwoPane();

		public int getListFragmentContainerId();
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
		if (item.getItemId() == MNU_SWITCH_EDIT || item.getItemId() == MNU_SWITCH_VIEW) {
			exchangeFragments();
		}
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

	public abstract BaseDocumentLayoutFragment getDocumentLayoutFragment();
	
	protected int getContainerId() {
		if (containerId == null) {
			containerId = mCallback.getLayoutFragmentContainerId();
		}
		return containerId;
	}
}

