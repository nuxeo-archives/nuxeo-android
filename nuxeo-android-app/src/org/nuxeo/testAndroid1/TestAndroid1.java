package org.nuxeo.testAndroid1;

import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.smartnsoft.droid4me.app.SmartActivity;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectUnavailableException;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectsRetrievalAsynchronousPolicy;

public class TestAndroid1 extends SmartActivity<Void> implements
		BusinessObjectsRetrievalAsynchronousPolicy, OnClickListener {

	/** Called when the activity is first created. */

	TextView myText = null;
	TextView result = null;
	Documents docs = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	// call back of object retrieval => Thread UI
	public void onFulfillDisplayObjects() {

		myText.setText("Automation call succeed");

		Button button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(this);

		if (docs != null) {
			StringBuffer sb = new StringBuffer();

			sb.append("<h3>query result</h3> <ul>");

			for (Document doc : docs) {
				sb.append("<li>" +  doc.getType() + " - ");
				sb.append("<b>" +  doc.getTitle() + "</b>");
				sb.append("</li>");
			}
			sb.append("</ul>");
			result.setText(Html.fromHtml(sb.toString()));
		}
	}

	// executed in an async thread
	public void onRetrieveBusinessObjects()
			throws BusinessObjectUnavailableException {

		HttpAutomationClient client = new HttpAutomationClient("http://10.0.2.2:8080/nuxeo/site/automation");
		Session session = client.getSession("Administrator", "Administrator");
		try {
			docs = (Documents) session.newRequest("Document.Query").set(
					"query", "SELECT * FROM Document where ecm:mixinType != 'HiddenInNavigation'").execute();
		} catch (Exception e) {
			throw new BusinessObjectUnavailableException(e);
		}
		client.shutdown();

	}

	public void onRetrieveDisplayObjects() {
		setContentView(R.layout.main);
		myText = (TextView) findViewById(R.id.myTextBox);
		result = (TextView) findViewById(R.id.result);

		myText.setText("Automation Call in progress ...");
	}

	public void onSynchronizeDisplayObjects() {
		// resync business object
	}

	public void onClick(View v) {
		myText.setText("should refresh query in a non UI Thread" );
	}

}