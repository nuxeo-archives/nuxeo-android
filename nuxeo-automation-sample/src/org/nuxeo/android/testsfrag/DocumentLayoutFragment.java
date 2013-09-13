package org.nuxeo.android.testsfrag;

import org.nuxeo.android.automationsample.R;
import org.nuxeo.android.fragments.BaseDocumentLayoutFragment;
import org.nuxeo.android.layout.LayoutMode;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DocumentLayoutFragment extends BaseDocumentLayoutFragment implements OnClickListener {

    protected static final int ACTION_CREATE_DOCUMENT = 1;

	protected Button saveBtn;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.createeditlayout, container, false);
		
		saveBtn = (Button) v.findViewById(R.id.updateDocument);
		saveBtn.setOnClickListener(this);
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
    	TextView txtView = (TextView)v.findViewById(R.id.loading_label);
    	txtView.setVisibility(View.GONE);
    	if(!isEditMode() && !isCreateMode()) {
    		if (currentDocument.getType().equals("Picture"))
    		{
            	ImageView imageView = (ImageView)v.findViewById(R.id.thumb);
            	imageView.setVisibility(View.VISIBLE);
            	imageView.setImageURI(currentDocument.getBlob());
    		}
    	}
    }    
    

    @Override
    public void onClick(View view) {
        if (view == saveBtn) {
            saveDocument();
        }
    }

	@Override
	public BaseDocumentLayoutFragment getDocumentLayoutFragment() {
		if (documentLayoutFragment == null) {
			documentLayoutFragment = new DocumentLayoutFragment();
		}
		return documentLayoutFragment;
	}
}
