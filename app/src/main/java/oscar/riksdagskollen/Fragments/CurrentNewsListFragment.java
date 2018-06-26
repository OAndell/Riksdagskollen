package oscar.riksdagskollen.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import oscar.riksdagskollen.Activities.NewsReaderActivity;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RikdagskollenApp;
import oscar.riksdagskollen.Utilities.Callbacks.CurrentNewsCallback;
import oscar.riksdagskollen.Utilities.Adapters.CurrentNewsListAdapter;
import oscar.riksdagskollen.Utilities.JSONModels.CurrentNews;
import oscar.riksdagskollen.Utilities.Adapters.RiksdagenViewHolderAdapter;


/**
 * Created by oscar on 2018-03-29.
 */

public class CurrentNewsListFragment extends RiksdagenAutoLoadingListFragment {
    private List<CurrentNews> newsList = new ArrayList<>();
    private CurrentNewsListAdapter adapter;

    public static CurrentNewsListFragment newInstance(){
        CurrentNewsListFragment newInstance = new CurrentNewsListFragment();
        return newInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.news);
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new CurrentNewsListAdapter(newsList, new RiksdagenViewHolderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Object document) {
                /*
                TODO now the mobile browser us used. It may be good to create an in app viewer.
                Intent intent = new Intent(getContext(), NewsReaderActivity.class);
                intent.putExtra("document", (CurrentNews) document);
                startActivity(intent);*/
                CurrentNews newsDoc = (CurrentNews) document;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://riksdagen.se" + newsDoc.getLinklista().getLink().getUrl()));
                startActivity(browserIntent);
            }
        });

    }

    /**
     * Load the next page and add it to the adapter when downloaded and parsed.
     * Hides the loading view.s
     */
    protected void loadNextPage(){
        setLoadingMoreItems(true);
        RikdagskollenApp.getInstance().getRiksdagenAPIManager().getCurrentNews( new CurrentNewsCallback() {
            @Override
            public void onNewsFetched(List<CurrentNews> currentNews) {
                setShowLoadingView(false);
                newsList.addAll(currentNews);
                getAdapter().notifyDataSetChanged();
                setLoadingMoreItems(false);
            }

            @Override
            public void onFail(VolleyError error) {
                setLoadingMoreItems(false);
                decrementPage();
            }
        }, getPageToLoad());
        incrementPage();
    }

    @Override
    RiksdagenViewHolderAdapter getAdapter() {
        return adapter;
    }
}
