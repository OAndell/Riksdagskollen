package oscar.riksdagskollen.Util.RiksdagenCallback;

import com.android.volley.VolleyError;

public interface DebateAudioSourceCallback {
    void onDebateAudioSource(String sourceUrl);

    void onAudioSourceFail(VolleyError error);
}
