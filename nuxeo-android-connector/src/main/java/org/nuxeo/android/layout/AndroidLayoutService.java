package org.nuxeo.android.layout;

import java.util.HashMap;
import java.util.Map;

public class AndroidLayoutService implements NuxeoLayoutService {

	protected Map<String, LayoutDefinition> definitions = new HashMap<String, LayoutDefinition>();

	@Override
	public NuxeoLayout getLayout(String layoutName) {

		LayoutDefinition def = definitions.get(layoutName);
		if (def==null) {
			def = loadLayout(layoutName);
		}
		return null;
	}

	protected LayoutDefinition loadLayout(String layoutName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NuxeoLayout parseLayoutDefinition(String definition) {
		return null;
	}

}
