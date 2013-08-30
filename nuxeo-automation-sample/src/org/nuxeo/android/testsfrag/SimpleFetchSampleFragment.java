package org.nuxeo.android.testsfrag;

import org.nuxeo.android.automationsample.R;
import org.nuxeo.android.contentprovider.NuxeoContentProviderConfig;
import org.nuxeo.android.fragments.BaseNuxeoFragment;
import org.nuxeo.ecm.automation.client.cache.CacheBehavior;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SimpleFetchSampleFragment extends BaseNuxeoFragment implements
		View.OnClickListener {

	protected TextView statusText;

	protected TextView docTitle;

	protected ImageView iconView;

	protected ImageView pictureView;

	protected Button openBtn;

	protected Document selectedDocument;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.simplefetch, container, false);

        statusText = (TextView)v.findViewById(R.id.statusText);

        docTitle = (TextView) v.findViewById(R.id.docTitle);
        docTitle.setText("???");

        iconView = (ImageView) v.findViewById(R.id.iconView);
        pictureView = (ImageView) v.findViewById(R.id.pictureView);

        openBtn = (Button) v.findViewById(R.id.openBtn);
        openBtn.setOnClickListener(this);
		
		return v;
	}

	@Override
	protected boolean requireAsyncDataRetrieval() {
		return true;
	}

    @Override
    protected void onNuxeoDataRetrievalStarted() {
        statusText.setText("Fetching ...");
        statusText.setVisibility(View.VISIBLE);
        openBtn.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        Documents docs = (Documents) data;

        if (docs.size() == 0) {
            statusText.setText("No Picture found on your server ...");
            statusText.setVisibility(View.VISIBLE);
            openBtn.setVisibility(View.INVISIBLE);
        } else {
            statusText.setText("");
            statusText.setVisibility(View.INVISIBLE);
            selectedDocument = docs.get(0);

            docTitle.setText(selectedDocument.getTitle());

            // Document properties are fetched, but not binary data
            // you can use ContentProvider binding to let Android fetch them via
            // Uri
            //
            // icon : content://nuxeo/icons/<type> (can use Document.getIcon())
            // blob : content://nuxeo/blobs/<UUID>/<idx> (can use
            // Document.getBlob(idx))

            iconView.setImageURI(Uri.parse("content://"
                    + NuxeoContentProviderConfig.getAuthority() + "/icons"
                    + selectedDocument.getString("common:icon")));

            String contentUri = "content://"
                    + NuxeoContentProviderConfig.getAuthority() + "/blobs/"
                    + selectedDocument.getId();
            pictureView.setImageURI(Uri.parse(contentUri));

            openBtn.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        return getNuxeoContext().getDocumentManager().query(
                "select * from Picture order by dc:modified desc", null, null,
                null, 0, 10, CacheBehavior.STORE);
    }

    @Override
    public void onClick(View srcView) {
        if (srcView == openBtn) {

            // could use startViewerFromBlob(Uri uri) from base class
            // but do it by hand for now in order to show usage of Uri
            // to send Nuxeo data to external applications

            Uri contentUri = selectedDocument.getBlob();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(contentUri);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try {
                startActivity(intent);
            } catch (android.content.ActivityNotFoundException e) {
                Toast.makeText(getActivity().getBaseContext(), "No Application Available to View this",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
