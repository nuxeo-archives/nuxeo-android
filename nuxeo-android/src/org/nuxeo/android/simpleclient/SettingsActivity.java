package org.nuxeo.android.simpleclient;

import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.Preference;

import com.smartnsoft.droid4me.app.SmartPreferenceActivity;

/**
 * The activity which enables to tune the application.
 * 
 * @author Nuxeo & Smart&Soft
 * @since 2011.02.17
 */
public final class SettingsActivity
    extends SmartPreferenceActivity
{

  public void onRetrieveDisplayObjects()
  {
    addPreferencesFromResource(R.xml.settings);
    {
      final Preference versionPreference = findPreference("version");
      try
      {
        versionPreference.setSummary(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
      }
      catch (NameNotFoundException exception)
      {
        if (log.isErrorEnabled())
        {
          log.error("Cannot determine the application version name", exception);
        }
        versionPreference.setSummary("???");
      }
    }
  }

}
