package oscar.riksdagskollen.Fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RikdagskollenApp;
import oscar.riksdagskollen.Utilities.Callbacks.CurrentNewsCallback;
import oscar.riksdagskollen.Utilities.CurrentNewsListAdapter;
import oscar.riksdagskollen.Utilities.JSONModels.CurrentNews;


/**
 * Created by oscar on 2018-03-29.
 */

public class CurrentNewsListFragment extends Fragment {
    int pageToLoad = 1;
    private boolean loading = false;
    private List<CurrentNews> newsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private int pastVisiblesItems;
    private CurrentNewsListAdapter currentNewsListAdapter;
    private ViewGroup loadingView;


    public static CurrentNewsListFragment newInstance(){
        CurrentNewsListFragment newInstance = new CurrentNewsListFragment();
        return newInstance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.news);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_party_list,null);
        currentNewsListAdapter = new CurrentNewsListAdapter(newsList);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(currentNewsListAdapter);
        recyclerView.setNestedScrollingEnabled(true);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    int visibleItemCount = mLayoutManager.getChildCount();
                    int totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        if(!loading) loadNextPage();
                    }

                }
            }
        });
        loadingView = view.findViewById(R.id.loading_view);
        loadNextPage();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // If we already have content in the adapter, do not show the loading view
        if(currentNewsListAdapter.getItemCount() > 0) loadingView.setVisibility(View.GONE);
        super.onViewCreated(view, savedInstanceState);
    }

    private void loadNextPage(){
        loading = true;

        RikdagskollenApp.getInstance().getRiksdagenAPIManager().getCurrentNews(new CurrentNewsCallback() {

            @Override
            public void onNewsFetched(List<CurrentNews> currentNews) {
                loading = false;
                loadingView.setVisibility(View.GONE);
                newsList.addAll(currentNews);
                System.out.println(currentNews.get(0).getTitel());
                currentNewsListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(VolleyError error) {
                loading = false;
                pageToLoad--;
            }
        });
        pageToLoad++;
    }


}
