package org.nuxeo.android.automationsample.test;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public final class FilePickerActivity
    extends Activity
{

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    setResult(Activity.RESULT_OK, new Intent().setData(Uri.parse(getApplication().getCacheDir().getAbsolutePath())));
    finish();
  }

}
