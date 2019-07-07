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

import oscar.riksdagskollen.Util.Adapter.RiksdagenViewHolderAdapter;
import oscar.riksdagskollen.Util.Adapter.TweetAdapter;
import oscar.riksdagskollen.Util.Helper.CustomTabs;
import oscar.riksdagskollen.Util.JSONModel.Party;
import oscar.riksdagskollen.Util.JSONModel.Twitter.Tweet;
import oscar.riksdagskollen.Util.RiksdagenCallback.TwitterCallback;
import oscar.riksdagskollen.Util.Twitter.TwitterTimeline;
import oscar.riksdagskollen.Util.Twitter.TwitterTimelineFactory;


public class TwitterListFragment extends RiksdagenAutoLoadingListFragment {


    public static int TYPE_DEFAULT = 0;
    public static int TYPE_PARTY = 1;
    private final List<Tweet> documentList = new ArrayList<>();
    private TweetAdapter adapter;
    public static final String SECTION_NAME_TWITTER = "twitter";
    private TwitterTimeline twitterTimeline;


    public static TwitterListFragment newInstance() {
        Bundle args = new Bundle();
        args.putInt("type", TYPE_DEFAULT);
        TwitterListFragment newInstance = new TwitterListFragment();
        newInstance.setArguments(args);
        return newInstance;
    }

    public static TwitterListFragment newInstance(Party party) {
        Bundle args = new Bundle();
        args.putInt("type", TYPE_PARTY);
        args.putParcelable("party", party);
        TwitterListFragment newInstance = new TwitterListFragment();
        newInstance.setArguments(args);
        return newInstance;
    }

    public void setTwitterTimeline(TwitterTimeline twitterTimeline) {
        this.twitterTimeline = twitterTimeline;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments().getInt("type") == TYPE_PARTY) {
            Party party = getArguments().getParcelable("party");
            twitterTimeline = TwitterTimelineFactory.getUser(party);
        } else {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Riksdagen på Twitter");
            twitterTimeline = TwitterTimelineFactory.getTwitterList(TwitterTimelineFactory.LIST_RIKSDAGEN_ALL);
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new TweetAdapter(documentList, new RiksdagenViewHolderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Object clickedDocument) {
                Tweet tweet = (Tweet) clickedDocument;
                String url = "https://twitter.com/" + tweet.getUser().getScreen_name() + "/status/" + tweet.getId();
                CustomTabs.openTab(getContext(), url);

            }
        }, this);
    }

    /**
     * Load the next page and add it to the adapter when downloaded and parsed.
     * Hides the loading view.
     */
    protected void loadNextPage() {
        setLoadingMoreItems(true);
        twitterTimeline.getTimeline(new TwitterCallback() {
            @Override
            public void onTweetsFetched(List<Tweet> tweets) {
                documentList.addAll(tweets);
                getAdapter().addAll(tweets);
                setShowLoadingView(false);
                setLoadingMoreItems(false);
            }

            @Override
            public void onFail(VolleyError error) {

            }
        });
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

    @Override
    protected void resetPageToLoad() {
        twitterTimeline.reset();
    }

}
