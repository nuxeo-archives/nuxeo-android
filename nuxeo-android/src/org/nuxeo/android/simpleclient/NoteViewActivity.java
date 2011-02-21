package org.nuxeo.android.simpleclient;

import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.text.Html;
import android.widget.TextView;

import com.smartnsoft.droid4me.app.AppPublics.BroadcastListenerProvider;
import com.smartnsoft.droid4me.app.AppPublics.SendLoadingIntent;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectUnavailableException;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectsRetrievalAsynchronousPolicy;

public class NoteViewActivity extends BaseDocumentViewActivity implements
        BusinessObjectsRetrievalAsynchronousPolicy, SendLoadingIntent,
        BroadcastListenerProvider,
        NuxeoAndroidApplication.TitleBarShowHomeFeature,
        NuxeoAndroidApplication.TitleBarRefreshFeature {

    private TextView description;
    private TextView title;
    private TextView content;

    @Override
    public void onRetrieveDisplayObjects() {
        setContentView(R.layout.note_view_layout);
        title = (TextView) findViewById(R.id.title);
        description = (TextView) findViewById(R.id.description);
        content = (TextView) findViewById(R.id.content);
    }

    @Override
    public void onRetrieveBusinessObjects()
            throws BusinessObjectUnavailableException {
        Document mynote = fetchDocument(false);
        if (mynote.getString("note:note")==null) {
            fetchDocument(true);
        }
    }

    @Override
    public void onFulfillDisplayObjects() {

        if (document != null) {
            title.setText(document.getTitle());
            description.setText(document.getString("dc:description", ""));

            String mt = document.getString("note:mime_type", "text/plain");
            String contentText = document.getString("note:note", "");

            if ("text/html".equals(mt)) {
                content.setText(Html.fromHtml(contentText),TextView.BufferType.SPANNABLE);
            } else {
                content.setText(contentText);
            }
        }
    }

    @Override
    protected String getSchemas() {
        return "note";
    }

}
