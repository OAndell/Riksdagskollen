package oscar.riksdagskollen.Util.RiksdagenCallback;

import com.android.volley.VolleyError;

import oscar.riksdagskollen.DebateView.Data.Speech;

public interface SpeechCallback {

    void onSpeechFetched(Speech speech, String anf);

    void onFail(VolleyError error);
}
