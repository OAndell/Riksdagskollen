package oscar.riksdagskollen.Util.Helper;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TwitterAuthRequest extends JsonObjectRequest {

    private String apiKey;

    public TwitterAuthRequest(int method, String url, String apiKey, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
        this.apiKey = apiKey;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> authHeader = new HashMap<>();
        authHeader.put("Authorization", "Basic " + apiKey);
        return authHeader;
    }
}
