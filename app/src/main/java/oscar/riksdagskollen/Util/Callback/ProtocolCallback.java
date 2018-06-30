package oscar.riksdagskollen.Util.Callback;

import com.android.volley.VolleyError;

import java.util.List;

import oscar.riksdagskollen.Util.JSONModel.Protocol;


/**
 * Created by oscar on 2018-06-16.
 */

public interface ProtocolCallback {
    void onProtocolsFetched(List<Protocol> protocols);

    void onFail(VolleyError error);
}