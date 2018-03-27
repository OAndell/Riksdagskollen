package oscar.riksdagskollen.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;

import java.util.List;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RikdagskollenApp;
import oscar.riksdagskollen.Utilities.Callbacks.PartyDocumentCallback;
import oscar.riksdagskollen.Utilities.JSONModels.Party;
import oscar.riksdagskollen.Utilities.JSONModels.PartyDocument;

/**
 * Created by gustavaaro on 2018-03-26.
 */

public class PartyListFragment extends Fragment {

    Party party;
    int pageToLoad = 1;
    private int preLast;
    private ListView listView;
    private boolean loading = false;
    private ArrayAdapter listAdapter;
    private ProgressBar loadingView;

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
        loadingView = new ProgressBar(getContext());
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(party.getName());

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_party_list,null);
        listView = view.findViewById(R.id.party_new_listview);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView lw, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {
                final int lastItem = firstVisibleItem + visibleItemCount;

                if(lastItem == totalItemCount)
                {
                    if(preLast!=lastItem && !loading)
                    {
                       loadMoreItems();
                    }
                }
            }
        });

        listAdapter= new ArrayAdapter<>(getContext(),R.layout.party_view,R.id.doc_title);
        listView.setAdapter(listAdapter);
        getNextPage();

        return view;
    }

    private void loadMoreItems(){
        loading = true;
        listView.addFooterView(loadingView);
        getNextPage();
    }


    private void getNextPage(){
        loading = true;
        RikdagskollenApp.getInstance().getRiksdagenAPIManager().getDocumentsForParty(party, pageToLoad, new PartyDocumentCallback() {
            @Override
            public void onDocumentsFetched(List<PartyDocument> documents) {
                listAdapter.addAll(documents);
                listAdapter.notifyDataSetChanged();
                listView.removeFooterView(loadingView);
                pageToLoad++;
                loading = false;
            }

            @Override
            public void onFail(VolleyError error) {
                loading = false;
                listView.removeFooterView(loadingView);
            }
        });
    }


}



