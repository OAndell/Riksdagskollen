package oscar.riksdagskollen.Util.Helper;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class DesktopStringRequest extends CacheRequest {
    public DesktopStringRequest(int method, String url, CachingPolicy cachingPolicy, final Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, cachingPolicy, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                try {
                    final String stringResponse = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    listener.onResponse(stringResponse);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("User-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.111 Safari/537.36");
        return headers;
    }

}
