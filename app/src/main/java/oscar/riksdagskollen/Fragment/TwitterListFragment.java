package oscar.riksdagskollen.Fragment;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import oscar.riksdagskollen.R;
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

    public static final String SHARED_PREFERENCE = "twitter.preference";
    public static final String PREFERENCE_RETWEET = "retweet.preference";
    public static final String PREFERENCE_LIST = "list.preference";

    private final List<Tweet> documentList = new ArrayList<>();
    private TweetAdapter adapter;
    public static final String SECTION_NAME_TWITTER = "twitter";
    private TwitterTimeline twitterTimeline;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private LayoutInflater inflater;

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


    private void applyPreferences() {
        int preferredList = preferences.getInt(PREFERENCE_LIST, TwitterTimelineFactory.LIST_RIKSDAGEN_ALL);
        twitterTimeline = TwitterTimelineFactory.getTwitterList(preferredList);
        boolean inculdeRT = preferences.getBoolean(PREFERENCE_RETWEET, true);
        twitterTimeline.setIncludeRT(inculdeRT);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        if (getArguments().getInt("type") == TYPE_PARTY) {
            Party party = getArguments().getParcelable("party");
            twitterTimeline = TwitterTimelineFactory.getUser(party);
        } else {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Twitter");
            preferences = getActivity().getSharedPreferences(SHARED_PREFERENCE, getActivity().MODE_PRIVATE);
            editor = preferences.edit();
            applyPreferences();
            setHasOptionsMenu(true);
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
    protected RiksdagenViewHolderAdapter getAdapter() {
        return adapter;
    }

    @Override
    protected void resetPageToLoad() {
        twitterTimeline.reset();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.twitter_menu, menu);
        final MenuItem menuItem = menu.findItem(R.id.menu_filter);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                final View dialogView = inflater.inflate(R.layout.twitter_preferences_dialog, null);
                configurePreferenceDialog(dialogView);
                final AlertDialog dialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom)
                        .setTitle("Inställningar:")
                        .setView(dialogView)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                editor.apply();
                                applyPreferences();
                                refresh();
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create();
                dialog.show();
                return true;
            }
        });
        final MenuItem infoItem = menu.findItem(R.id.menu_info);
        infoItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                return true;
            }
        });

        super.onCreateOptionsMenu(menu, menuInflater);
    }

    private void configurePreferenceDialog(View dialogView) {
        final int prefAll = TwitterTimelineFactory.LIST_RIKSDAGEN_ALL;
        final int prefParties = TwitterTimelineFactory.LIST_RIKSDAGEN_PARTIES;

        final CheckBox optionAll = dialogView.findViewById(R.id.checkBox1);
        final CheckBox optionParties = dialogView.findViewById(R.id.checkBox2);
        CheckBox optionRetweet = dialogView.findViewById(R.id.checkBox3);

        optionAll.setChecked(preferences.getInt(PREFERENCE_LIST, prefAll) == prefAll);
        optionParties.setChecked(preferences.getInt(PREFERENCE_LIST, prefAll) == prefParties);
        optionRetweet.setChecked(preferences.getBoolean(PREFERENCE_RETWEET, true));

        optionAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                optionParties.setChecked(!isChecked); //Toggle the other option.
                if (isChecked) {
                    editor.putInt(PREFERENCE_LIST, prefAll);
                }
            }
        });

        optionParties.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                optionAll.setChecked(!isChecked);
                if (isChecked) {
                    editor.putInt(PREFERENCE_LIST, prefParties);
                }
            }
        });

        optionRetweet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                editor.putBoolean(PREFERENCE_RETWEET, isChecked);
            }
        });


    }

}
