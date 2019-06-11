package oscar.riksdagskollen.Util.Helper;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

public class TwitterGetRequest extends JsonArrayRequest {

    private String bearerToken;

    public TwitterGetRequest(int method, String url, String bearerToken, JSONArray jsonArray, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonArray, listener, errorListener);
        this.bearerToken = bearerToken;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> authHeader = new HashMap<>();
        authHeader.put("Authorization", "Bearer " + bearerToken);
        return authHeader;
    }
}
