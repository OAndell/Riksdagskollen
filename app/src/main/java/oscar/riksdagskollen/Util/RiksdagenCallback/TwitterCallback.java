package oscar.riksdagskollen.Util.RiksdagenCallback;

import com.android.volley.VolleyError;

import java.util.List;

import oscar.riksdagskollen.Util.JSONModel.Twitter.Tweet;

public interface TwitterCallback {

    void onTweetsFetched(List<Tweet> tweet);

    void onFail(VolleyError error);
}
