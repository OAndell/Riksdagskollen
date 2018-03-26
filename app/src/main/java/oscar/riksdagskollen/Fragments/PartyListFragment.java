package oscar.riksdagskollen.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
        ListView listView = (ListView) view.findViewById(R.id.party_new_listview);

        final ArrayAdapter<PartyDocument> adapter = new ArrayAdapter<PartyDocument>(getContext(),R.layout.party_view,R.id.doc_title);
        listView.setAdapter(adapter);

        RikdagskollenApp.getInstance().getRiksdagenAPIManager().getDocumentsForParty(party, new PartyDocumentCallback() {
            @Override
            public void onDocumentsFetched(List<PartyDocument> documents) {
                adapter.addAll(documents);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(VolleyError error) {

            }
        });


        return view;
    }

}



