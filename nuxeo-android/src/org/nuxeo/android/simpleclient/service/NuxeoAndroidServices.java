package org.nuxeo.android.simpleclient.service;

import org.nuxeo.android.simpleclient.Constants;

import com.smartnsoft.droid4me.ws.WebServiceCaller;

/**
 * A single point of access to the web services.
 * 
 * @author Nuxeo & Smart&Soft
 * @since 2011.02.17
 */
public final class NuxeoAndroidServices
    extends WebServiceCaller
{

  private static volatile NuxeoAndroidServices instance;

  // We accept the "out-of-order writes" case
  public static NuxeoAndroidServices getInstance()
  {
    if (instance == null)
    {
      synchronized (NuxeoAndroidServices.class)
      {
        if (instance == null)
        {
          instance = new NuxeoAndroidServices();
        }
      }
    }
    return instance;
  }

  private NuxeoAndroidServices()
  {
  }

  @Override
  protected String getUrlEncoding()
  {
    return Constants.WEBSERVICES_HTML_ENCODING;
  }

}
