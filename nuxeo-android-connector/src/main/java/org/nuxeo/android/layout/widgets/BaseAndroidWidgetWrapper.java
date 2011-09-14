package org.nuxeo.android.layout.widgets;

import java.util.HashMap;
import java.util.Map;

import org.nuxeo.android.layout.ActivityResultHandler;
import org.nuxeo.android.layout.ActivityResultHandlerRegistry;

public class BaseAndroidWidgetWrapper implements ActivityResultHandlerRegistry {

	protected final Map<Integer, ActivityResultHandler> pendingActivityResultHandlers = new HashMap<Integer, ActivityResultHandler>();

	@Override
	public void registerActivityResultHandler(int requestCode,
			ActivityResultHandler handler) {
		// store pending registration
		pendingActivityResultHandlers.put(requestCode, handler);
	}

	public Map<Integer, ActivityResultHandler> getAndFlushPendingActivityResultHandler() {
		Map<Integer, ActivityResultHandler> pending = new HashMap<Integer, ActivityResultHandler>(pendingActivityResultHandlers);
		pendingActivityResultHandlers.clear();
		return pending;
	}

}
