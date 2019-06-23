package oscar.riksdagskollen.Util.RiksdagenCallback;

import com.android.volley.VolleyError;

import oscar.riksdagskollen.DebateView.Speech;

public interface SpeechCallback {

    void onSpeechFetched(Speech speech, String anf);

    void onFail(VolleyError error);
}
