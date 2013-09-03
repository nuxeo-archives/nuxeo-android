package org.nuxeo.android.testsfrag;

import org.nuxeo.android.automationsample.R;
import org.nuxeo.android.fragments.BaseDocumentLayoutFragment;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class DocumentLayoutFragment extends BaseDocumentLayoutFragment implements OnClickListener {

	protected Button saveBtn;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.createeditlayout, container, false);
		
		saveBtn = (Button) v.findViewById(R.id.updateDocument);
		saveBtn.setOnClickListener(this);

//        if (isEditMode()) {
//            setTitle("Edit " + currentDoc.getType() + " " + getCurrentDocument().getTitle());
//        } else if (isCreateMode()) {
//        	setTitle("Create new " + currentDoc.getType());
//        } else {
//        	setTitle("View " + currentDoc.getType() + " " + getCurrentDocument().getTitle());
//        }
		
        return v;
	}
	
	@Override
	public void onResume() {
        super.onResume();
        if (getMode() == LayoutMode.VIEW) {
            saveBtn.setVisibility(View.GONE);
        } else {
            saveBtn.setVisibility(View.VISIBLE);
        }
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
	}

	@Override
	protected ViewGroup getLayoutContainer() {
		return (ViewGroup) getView().findViewById(R.id.layoutContainer);
	}
    
    @Override
    protected void onNuxeoDataRetrieved(Object data) {
    	super.onNuxeoDataRetrieved(data);
    	View v = getView();
    	if(!isEditMode() && !isCreateMode()) {
    		if (currentDocument.getType().equals("Picture"))
    		{
            	ImageView imageView = (ImageView)v.findViewById(R.id.thumb);
            	imageView.setVisibility(View.VISIBLE);
            	imageView.setImageURI(currentDocument.getBlob());
    		}
    	}
    }
    
    public interface Callback{
    	
//    	public void exchangeFragments();
    	public void switchToEdit();
    	public void switchToView();
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
//		if (isFirstCall() == false) {
//			mCallback.exchangeFragments();
//		} else {
			switch (item.getItemId()) {
			case MNU_SWITCH_EDIT:
				mCallback.switchToEdit();
				break;
			case MNU_SWITCH_VIEW:
				mCallback.switchToView();
				break;
			}
//		}
		return true;
	}

    @Override
    public void onClick(View view) {
        if (view == saveBtn) {
            saveDocument();
            mCallback.switchToView();
        }
    }

//  @Override
//  public boolean onOptionsItemSelected(MenuItem item) {
//  	if (isFirstCall() == false)
//  	{
//  		finish();
//  	} else {
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
//  	}
//  	return super.onOptionsItemSelected(item);
//  }
}
