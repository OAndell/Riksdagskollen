package oscar.riksdagskollen.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import oscar.riksdagskollen.Activities.NewsReaderActivity;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RikdagskollenApp;
import oscar.riksdagskollen.Utilities.Adapters.CurrentNewsListAdapter;
import oscar.riksdagskollen.Utilities.Adapters.DecisionListAdapter;
import oscar.riksdagskollen.Utilities.Callbacks.CurrentNewsCallback;
import oscar.riksdagskollen.Utilities.Callbacks.DecisionsCallback;
import oscar.riksdagskollen.Utilities.JSONModels.CurrentNews;
import oscar.riksdagskollen.Utilities.JSONModels.DecisionDocument;
import oscar.riksdagskollen.Utilities.Adapters.RiksdagenViewHolderAdapter;

/**
 * Created by gustavaaro on 2018-06-16.
 */

public class DecisionsListFragment extends RiksdagenAutoLoadingListFragment {

    private List<DecisionDocument> decisionDocuments = new ArrayList<>();
    private DecisionListAdapter adapter;

    public static DecisionsListFragment newInstance(){
        DecisionsListFragment newInstance = new DecisionsListFragment();
        return newInstance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.decisions);
        adapter = new DecisionListAdapter(decisionDocuments, new RiksdagenViewHolderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Object document) {

            }
        });

    }


    @Override
    protected void loadNextPage() {
        setLoadingMoreItems(true);
        RikdagskollenApp.getInstance().getRiksdagenAPIManager().getDecisions(new DecisionsCallback() {
            @Override
            public void onDecisionsFetched(List<DecisionDocument> documents) {
                setShowLoadingView(false);
                decisionDocuments.addAll(documents);
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
