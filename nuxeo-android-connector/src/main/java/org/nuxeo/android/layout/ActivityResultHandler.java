package org.nuxeo.android.layout;

import android.content.Intent;

public interface ActivityResultHandler {

	boolean onActivityResult(int requestCode, int resultCode, Intent data);

}
