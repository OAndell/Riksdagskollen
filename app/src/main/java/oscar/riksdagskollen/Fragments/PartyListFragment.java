package oscar.riksdagskollen.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import oscar.riksdagskollen.Activities.DocumentReaderActivity;
import oscar.riksdagskollen.Activities.MotionActivity;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RikdagskollenApp;
import oscar.riksdagskollen.Utilities.Callbacks.PartyDocumentCallback;
import oscar.riksdagskollen.Utilities.JSONModels.Party;
import oscar.riksdagskollen.Utilities.JSONModels.PartyDocument;
import oscar.riksdagskollen.Utilities.PartyListViewholderAdapter;

/**
 * Created by gustavaaro on 2018-03-26.
 */

public class PartyListFragment extends Fragment {

    Party party;
    int pageToLoad = 1;
    private int pastVisiblesItems;
    private boolean loading = false;
    private ProgressBar itemsLoadingView;

    private List<PartyDocument> documentList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PartyListViewholderAdapter partyListAdapter;
    private ViewGroup loadingView;

    /**
     *
     * @param party the party object that the fragment will display feed for
     * @return a new instance of this fragment with the Party object in its arguments
     */

    public static PartyListFragment newIntance(Party party){
        Bundle args = new Bundle();
        args.putParcelable("party",party);
        PartyListFragment newInstance = new PartyListFragment();
        newInstance.setArguments(args);
        return newInstance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.party = getArguments().getParcelable("party");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(party.getName());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_party_list,null);

        loadingView = view.findViewById(R.id.loading_view);


        partyListAdapter = new PartyListViewholderAdapter(documentList, new PartyListViewholderAdapter.OnPartyDocumentClickListener() {
            @Override
            public void onPartyDocumentClickListener(PartyDocument document) {
                Intent intent;
                if(document.isMotion()){
                    intent = new Intent(getContext(), MotionActivity.class);
                } else {
                    intent = new Intent(getContext(), DocumentReaderActivity.class);
                }
                intent.putExtra("document",document);
                startActivity(intent);
            }
        });
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(partyListAdapter);
        recyclerView.setNestedScrollingEnabled(true);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        // Listener to determine when the scollview has reached the bottom. Then we load the next page
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

        itemsLoadingView = new ProgressBar(getContext());
        itemsLoadingView.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.primaryColor),
                android.graphics.PorterDuff.Mode.MULTIPLY);

        loadNextPage();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // If we already have content in the adapter, do not show the loading view
        if(partyListAdapter.getDocumentCount() > 0) loadingView.setVisibility(View.GONE);
    }


    /**
     * Load the next page and add it to the adapter when downloaded and parsed.
     * Hides the loading view.
     */
    private void loadNextPage(){
        setLoading(true);
        RikdagskollenApp.getInstance().getRiksdagenAPIManager().getDocumentsForParty(party, pageToLoad, new PartyDocumentCallback() {
            @Override
            public void onDocumentsFetched(List<PartyDocument> documents) {
                loadingView.setVisibility(View.GONE);
                documentList.addAll(documents);
                partyListAdapter.notifyDataSetChanged();
                setLoading(false);
            }

            @Override
            public void onFail(VolleyError error) {
                setLoading(false);
            }
        });
        pageToLoad++;
    }


    private void setLoading(Boolean loading){
        this.loading = loading;

        // The runnables are apparently needed to avoid long warnings
        if(loading && pageToLoad > 1){
            recyclerView.post(new Runnable() {
                public void run() {
                    partyListAdapter.addFooter(itemsLoadingView);
                }
            });
        } else {
            recyclerView.post(new Runnable() {
                public void run() {
                    partyListAdapter.removeFooter(itemsLoadingView);
                }
            });
        }

    }



}



