package oscar.riksdagskollen.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Adapter.RiksdagenViewHolderAdapter;
import oscar.riksdagskollen.Util.Adapter.TweetAdapter;
import oscar.riksdagskollen.Util.JSONModel.Twitter.Tweet;
import oscar.riksdagskollen.Util.RiksdagenCallback.TwitterCallback;


public class TwitterListFragment extends RiksdagenAutoLoadingListFragment {
    private final List<Tweet> documentList = new ArrayList<>();
    private TweetAdapter adapter;
    public static final String SECTION_NAME_TWITTER = "twitter";

    //private TwitterUser twitterUser = TwitterUserFactory.getUser(CurrentParties.getV());

    public static TwitterListFragment newInstance() {
        TwitterListFragment newInstance = new TwitterListFragment();
        return newInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.debate_nav);
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new TweetAdapter(documentList, new RiksdagenViewHolderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Object clickedDocument) {

            }
        });
    }

    /**
     * Load the next page and add it to the adapter when downloaded and parsed.
     * Hides the loading view.
     */
    protected void loadNextPage() {
        setLoadingMoreItems(true);
        RiksdagskollenApp.getInstance().getTwitterAPIManager().getRiksdagenTweetList(new TwitterCallback() {
            @Override
            public void onTweetsFetched(List<Tweet> tweets) {
                documentList.addAll(tweets);
                getAdapter().addAll(documentList);
                setShowLoadingView(false);
                setLoadingMoreItems(false);
            }

            @Override
            public void onFail(VolleyError error) {

            }
        });
        incrementPage();
    }

    @Override
    protected void clearItems() {
        documentList.clear();
        adapter.clear();
    }

    @Override
    RiksdagenViewHolderAdapter getAdapter() {
        return adapter;
    }
}
