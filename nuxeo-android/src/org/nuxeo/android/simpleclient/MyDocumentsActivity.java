package org.nuxeo.android.simpleclient;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.smartnsoft.droid4me.app.WrappedSmartListActivity;
import com.smartnsoft.droid4me.framework.DetailsProvider.BusinessViewWrapper;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectUnavailableException;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectsRetrievalAsynchronousPolicy;

public class MyDocumentsActivity extends WrappedSmartListActivity implements
        BusinessObjectsRetrievalAsynchronousPolicy {

    private final static class DocumentAttributes {

        private final TextView title;

        public DocumentAttributes(View view) {
            title = (TextView) view.findViewById(R.id.title);
        }

        public void update(Document businessObject) {
            title.setText(businessObject.getTitle());
        }

    }

    private static final class DocumentWrapper extends
            BusinessViewWrapper<Document> {

        public DocumentWrapper(Document businessObject) {
            super(businessObject);
        }

        @Override
        protected View createNewView(Activity activity, Document businessObject) {
            return activity.getLayoutInflater().inflate(
                    R.layout.my_documents_document, null);
        }

        @Override
        protected Object extractNewViewAttributes(Activity activity, View view,
                Document businessObject) {
            return new DocumentAttributes(view);
        }

        @Override
        protected void updateView(Activity activity, Object viewAttributes,
                View view, Document businessObject, int position) {
            ((DocumentAttributes) viewAttributes).update(businessObject);
        }

    }

    public List<? extends BusinessViewWrapper<?>> retrieveBusinessObjectsList()
            throws BusinessObjectUnavailableException {

        HttpAutomationClient client = new HttpAutomationClient(
                "http://10.213.2.104:8080/nuxeo/site/automation");
        Session session = client.getSession("Administrator", "Administrator");
        Documents docs;
        try {
            docs = (Documents) session.newRequest("Document.Query").set(
                    "query",
                    "SELECT * FROM Document where ecm:mixinType != 'HiddenInNavigation'").execute();
        } catch (Exception e) {
            throw new BusinessObjectUnavailableException(e);
        }
        client.shutdown();

        List<BusinessViewWrapper<?>> wrappers = new ArrayList<BusinessViewWrapper<?>>();

        for (Document document : docs) {
            wrappers.add(new DocumentWrapper(document));
        }
        return wrappers;
    }

}
