package oscar.riksdagskollen.Util.RiksdagenCallback;

import com.android.volley.VolleyError;

import oscar.riksdagskollen.Util.JSONModel.Speech;

public interface SpeechCallback {

    void onSpeechFetched(Speech speech);

    void onFail(VolleyError error);
}
