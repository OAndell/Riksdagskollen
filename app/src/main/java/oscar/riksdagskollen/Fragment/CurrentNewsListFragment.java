package oscar.riksdagskollen.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import oscar.riksdagskollen.Activity.NewsReaderActivity;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Adapter.CurrentNewsListAdapter;
import oscar.riksdagskollen.Util.Adapter.RiksdagenViewHolderAdapter;
import oscar.riksdagskollen.Util.Callback.CurrentNewsCallback;
import oscar.riksdagskollen.Util.CustomTabs;
import oscar.riksdagskollen.Util.JSONModel.CurrentNews;


/**
 * Created by oscar on 2018-03-29.
 */

public class CurrentNewsListFragment extends RiksdagenAutoLoadingListFragment {
    private final List<CurrentNews> newsList = new ArrayList<>();
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

                CurrentNews newsDoc = (CurrentNews) document;
                Intent intent;
                try{

                    intent = new Intent(getContext(), NewsReaderActivity.class);
                    intent.putExtra("url",  newsDoc.getLinklista().getLink().getUrl());
                    //startActivity(intent);

                    String url = newsDoc.getLinklista().getLink().getUrl();
                    if (url.startsWith("http")) {
                        CustomTabs.openTab(getContext(), url);
                    } else {
                        CustomTabs.openTab(getContext(), "http://riksdagen.se" + newsDoc.getLinklista().getLink().getUrl());
                    }
                }catch (NullPointerException e){ //Some news does not contain the LinkLista object
                    System.out.println("Could not open news url");
                }
            }
        });

    }

    /**
     * Load the next page and add it to the adapter when downloaded and parsed.
     * Hides the loading view.s
     */
    protected void loadNextPage(){
        setLoadingMoreItems(true);
        RiksdagskollenApp.getInstance().getRiksdagenAPIManager().getCurrentNews(new CurrentNewsCallback() {
            @Override
            public void onNewsFetched(List<CurrentNews> currentNews) {
                setShowLoadingView(false);
                newsList.addAll(currentNews);
                getAdapter().addAll(currentNews);
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
