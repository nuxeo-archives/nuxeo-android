package org.nuxeo.android.simpleclient.listing;

import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

public class DocumentHelper {

    // For now Facets are not integrated into JSON export ...
    protected static final String[] folderishTypes = { "Domain",
            "WorkspaceRoot", "Workspace", "SectionRoot", "Section",
            "TemplateRoot", "PictureBook", "Folder", "OrderedFolder",
            "UserWorkspace" };

    protected static  boolean isFolderish(Document doc) {
        for (String fType : folderishTypes) {
            if (fType.equals(doc.getType())) {
                return true;
            }
        }
        return false;
    }
}
