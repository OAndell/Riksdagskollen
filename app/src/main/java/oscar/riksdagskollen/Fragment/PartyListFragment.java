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

import oscar.riksdagskollen.Activity.MotionActivity;
import oscar.riksdagskollen.RikdagskollenApp;
import oscar.riksdagskollen.Util.Callback.PartyDocumentCallback;
import oscar.riksdagskollen.Util.Callback.PartyLeadersCallback;
import oscar.riksdagskollen.Util.JSONModel.Party;
import oscar.riksdagskollen.Util.JSONModel.PartyDocument;
import oscar.riksdagskollen.Util.Adapter.RiksdagenViewHolderAdapter;
import oscar.riksdagskollen.Util.Adapter.PartyListViewholderAdapter;
import oscar.riksdagskollen.Util.JSONModel.Representative;

/**
 * Created by gustavaaro on 2018-03-26.
 */

public class PartyListFragment extends RiksdagenAutoLoadingListFragment {

    private Party party;
    private final List<PartyDocument> documentList = new ArrayList<>();
    private PartyListViewholderAdapter adapter;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(party.getName());
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.party = getArguments().getParcelable("party");
        adapter = new PartyListViewholderAdapter(documentList, new RiksdagenViewHolderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Object document) {
                Intent intent = new Intent(getContext(), MotionActivity.class);
                intent.putExtra("document",((PartyDocument)document));
                startActivity(intent);
            }
        });
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

    @Override
    RiksdagenViewHolderAdapter getAdapter() {
        return adapter;
    }


}



