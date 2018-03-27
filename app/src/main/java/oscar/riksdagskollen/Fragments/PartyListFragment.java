package oscar.riksdagskollen.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

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
    private int preLast;
    private ListView listView;
    private boolean loading = false;
    private ArrayAdapter listAdapter;
    private ProgressBar loadingView;

    private List<PartyDocument> documentList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PartyListViewholderAdapter partyListAdapter;

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
    private void getDocumentsFromRiksdagen(){

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_party_list,null);
        partyListAdapter = new PartyListViewholderAdapter(getContext(), documentList);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(partyListAdapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        RikdagskollenApp.getInstance().getRiksdagenAPIManager().getDocumentsForParty(party, 1, new PartyDocumentCallback() {
            @Override
            public void onDocumentsFetched(List<PartyDocument> documents) {
                documentList.addAll(documents);
                partyListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(VolleyError error) {

            }
        });
        return view;
    }



}



