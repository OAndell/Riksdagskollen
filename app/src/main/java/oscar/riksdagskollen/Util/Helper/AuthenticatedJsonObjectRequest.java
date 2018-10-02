package oscar.riksdagskollen.Util.Helper;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gustavaaro on 2018-10-01.
 */

public class AuthenticatedJsonObjectRequest extends JsonObjectRequest {

    String accessToken;

    public AuthenticatedJsonObjectRequest(int method, String url, String accessToken, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
        this.accessToken = accessToken;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> authHeader = new HashMap<>();
        authHeader.put("Authorization", "Bearer " + accessToken);
        return authHeader;
    }
}
