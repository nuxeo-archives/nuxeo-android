package org.nuxeo.android.layout;

public interface ActivityResultHandlerRegistry {

	void registerActivityResultHandler(int requestCode , ActivityResultHandler handler);

}
