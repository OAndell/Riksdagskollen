package oscar.riksdagskollen.Util.Helper;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

public class CacheRequest extends Request<NetworkResponse> {
    private final Response.Listener<NetworkResponse> mListener;
    private final Response.ErrorListener mErrorListener;
    private CachingPolicy cachingPolicy;

    public CacheRequest(int method, String url, CachingPolicy cachingPolicy, Response.Listener<NetworkResponse> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
        this.cachingPolicy = cachingPolicy;
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
        if (cacheEntry == null) {
            cacheEntry = new Cache.Entry();
        }
        final long cacheHitButRefreshed = this.cachingPolicy.softCacheExpire;
        final long cacheExpired = this.cachingPolicy.cacheExpire; // in 48 hours this cache entry expires completely
        long now = System.currentTimeMillis();
        final long softExpire = now + cacheHitButRefreshed;
        final long ttl = now + cacheExpired;
        cacheEntry.data = response.data;
        cacheEntry.softTtl = softExpire;
        cacheEntry.ttl = ttl;
        String headerValue;
        headerValue = response.headers.get("Date");
        if (headerValue != null) {
            cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
        }
        headerValue = response.headers.get("Last-Modified");
        if (headerValue != null) {
            cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
        }
        cacheEntry.responseHeaders = response.headers;
        return Response.success(response, cacheEntry);
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        mListener.onResponse(response);
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        return super.parseNetworkError(volleyError);
    }

    @Override
    public void deliverError(VolleyError error) {
        mErrorListener.onErrorResponse(error);
    }

    public enum CachingPolicy {

        DO_NOT_CACHE(0, 0),
        SHORT_TIME_CACHE(3 * 60 * 60 * 1000, 10 * 60 * 1000), // 3h, 10m
        MEDIUM_TIME_CACHE(24 * 60 * 60 * 1000, 2 * 60 * 60 * 1000), // 24h, 2h
        LONG_TIME_CACHE(72 * 60 * 60 * 1000, 48 * 60 * 60 * 1000); // 3d, 2d


        private long cacheExpire;
        private long softCacheExpire;

        CachingPolicy(long cacheExpire, long softCacheExpire) {
            this.cacheExpire = cacheExpire;
            this.softCacheExpire = softCacheExpire;
        }


    }
}
