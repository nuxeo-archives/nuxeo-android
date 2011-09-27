package org.nuxeo.android.layout;

public interface NuxeoLayoutService {

	NuxeoLayout parseLayoutDefinition(String definition);

	NuxeoLayout getLayout(String layoutName);

}
