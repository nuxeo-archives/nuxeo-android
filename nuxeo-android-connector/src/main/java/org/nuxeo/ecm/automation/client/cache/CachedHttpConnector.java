package org.nuxeo.ecm.automation.client.cache;

import java.io.InputStream;

import org.apache.http.client.HttpClient;
import org.nuxeo.android.network.NuxeoNetworkStatus;
import org.nuxeo.ecm.automation.client.jaxrs.RemoteException;
import org.nuxeo.ecm.automation.client.jaxrs.impl.CacheKeyHelper;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpConnector;
import org.nuxeo.ecm.automation.client.jaxrs.impl.NotAvailableOffline;
import org.nuxeo.ecm.automation.client.jaxrs.spi.Connector;
import org.nuxeo.ecm.automation.client.jaxrs.spi.Request;
import org.nuxeo.ecm.automation.client.jaxrs.spi.Response;

public class CachedHttpConnector extends HttpConnector implements Connector {

	protected final InputStreamCacheManager cacheManager;

	protected final NuxeoNetworkStatus offlineSettings;

	public CachedHttpConnector(HttpClient http, InputStreamCacheManager cacheManager, NuxeoNetworkStatus offlineSettings) {
		super(http);
		this.cacheManager = cacheManager;
		this.offlineSettings = offlineSettings;
	}

    protected Object getResultFromCacheEntry(Request request, CacheEntry cachedResult) {
        System.out.println("Cache HIT");
        try {
        	return request.handleResult(200, cachedResult.getReponseContentType(), cachedResult.getResponseContentDisposition(), cachedResult.getResponseStream());
        } catch (Exception e) {
        	e.printStackTrace();
        	return null;
		}
    }

	@Override
    public Object execute(Request request,  boolean forceRefresh, boolean cachable) {

    	String cacheKey=null;
        CacheEntry cachedEntry = null;
        if (cacheManager!=null ) {
            cacheKey = CacheKeyHelper.computeRequestKey(request);
            cachedEntry = cacheManager.getFromCache(cacheKey);
            if (cachedEntry!=null && !forceRefresh) {
                Object result = getResultFromCacheEntry(request, cachedEntry);
                if (result!=null) {
                	return result;
                }
            }
        }

        if (!offlineSettings.canUseNetwork()) {
        	throw new NotAvailableOffline("No data in cache, must be online");
        }

        try {
            Response response =  doExecute(request);
            if (response!=null) {
            	response = onBeforeResponseComplete(request, response, cacheKey);
            	return response.getResult(request);
            } else {
            	throw new RuntimeException("Cannot execute " + request);
            }

        } catch (RemoteException e) {
        	offlineSettings.setNetworkReachable(false);
            if (cachedEntry!=null) {
                try {
                    return getResultFromCacheEntry(request, cachedEntry);
                } catch (Exception e2) {
                    throw e;
                }
            } else {
                throw e;
            }
        }
        catch (Throwable t) {
        	if (isNetworkError(t)) {
        		offlineSettings.setNetworkReachable(false);
        		if (cachedEntry!=null) {
                    try {
                        return getResultFromCacheEntry(request, cachedEntry);
                    } catch (Throwable t2) {
                        throw new NotAvailableOffline("Can not fetch result from cache", t2);
                    }
                } else {
                	throw new NotAvailableOffline("No data in cache, must be online");
                }
        	} else {
        		throw new RuntimeException("Cannot execute " + request, t);
        	}
        }
    }

    protected boolean isNetworkError(Throwable t) {
    	String className = t.getClass().getName();

    	if (className.startsWith("java.net.")) {
    		return true;
    	}
    	if (className.startsWith("org.apache.http.conn.")) {
    		return true;
    	}
    	return false;
    }

    protected Response onBeforeResponseComplete(Request request, Response response, String cacheKey) {

    	if (cacheKey!=null && cacheManager!=null && response.getStatus()==200) {
            // store in cache
            InputStream is = cacheManager.addToCache(cacheKey, new CacheEntry(request, response));
            response.setInputStream(is);
        }

    	return response;
    }

}
