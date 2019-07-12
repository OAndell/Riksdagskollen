package oscar.riksdagskollen.Util.Helper;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class CachedJSONObjectRequest extends CacheRequest {

    public CachedJSONObjectRequest(int method, String url, CachingPolicy cachingPolicy, final Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {

        super(method, url, cachingPolicy, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                try {
                    final String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    JSONObject jsonObject = new JSONObject(jsonString);
                    listener.onResponse(jsonObject);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, errorListener);
    }

}
