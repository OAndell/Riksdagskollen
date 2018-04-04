package oscar.riksdagskollen.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import oscar.riksdagskollen.Activities.DocumentReaderActivity;
import oscar.riksdagskollen.Activities.MotionActivity;
import oscar.riksdagskollen.RikdagskollenApp;
import oscar.riksdagskollen.Utilities.Callbacks.PartyDocumentCallback;
import oscar.riksdagskollen.Utilities.JSONModels.Party;
import oscar.riksdagskollen.Utilities.JSONModels.PartyDocument;
import oscar.riksdagskollen.Utilities.JSONModels.RiksdagenViewHolderAdapter;
import oscar.riksdagskollen.Utilities.JSONModels.PartyListViewholderAdapter;

/**
 * Created by gustavaaro on 2018-03-26.
 */

public class PartyListFragment extends RiksdagenAutoLoadingListFragment {

    Party party;
    private List<PartyDocument> documentList = new ArrayList<>();

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
        setAdapter(new PartyListViewholderAdapter(documentList, new RiksdagenViewHolderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Object document) {
                Intent intent;

                if(((PartyDocument) document).isMotion()){
                    intent = new Intent(getContext(), MotionActivity.class);
                } else {
                    intent = new Intent(getContext(), DocumentReaderActivity.class);
                }
                intent.putExtra("document",((PartyDocument)document));
                startActivity(intent);
            }
        }));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(party.getName());
    }


    /**
     * Load the next page and add it to the adapter when downloaded and parsed.
     * Hides the loading view.
     */
    protected void loadNextPage(){
        setLoadingMoreItems(true);
        RikdagskollenApp.getInstance().getRiksdagenAPIManager().getDocumentsForParty(party, getPageToLoad(), new PartyDocumentCallback() {
            @Override
            public void onDocumentsFetched(List<PartyDocument> documents) {
                setShowLoadingView(false);
                documentList.addAll(documents);
                getAdapter().notifyDataSetChanged();
                setLoadingMoreItems(false);
            }

            @Override
            public void onFail(VolleyError error) {
                setLoadingMoreItems(false);
                decrementPage();
            }
        });
        incrementPage();
    }






}



