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

import oscar.riksdagskollen.Activity.VoteActivity;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RikdagskollenApp;
import oscar.riksdagskollen.Util.Adapter.VoteAdapter;
import oscar.riksdagskollen.Util.Callback.VoteCallback;
import oscar.riksdagskollen.Util.Adapter.RiksdagenViewHolderAdapter;
import oscar.riksdagskollen.Util.JSONModel.Vote;


/**
 * Created by oscar on 2018-03-29.
 */

public class VoteListFragment extends RiksdagenAutoLoadingListFragment {

    private List<Vote> voteList = new ArrayList<>();
    private VoteAdapter adapter;
    private boolean isShowingSearchedVotes = false;

    public static VoteListFragment newInstance(@Nullable ArrayList<Vote> votes){
        VoteListFragment newInstance = new VoteListFragment();
        if(votes != null) {
            Bundle args = new Bundle();
            args.putParcelableArrayList("votes",votes);
            newInstance.setArguments(args);
        }
        return newInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(!isShowingSearchedVotes){
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.votes);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            voteList = getArguments().getParcelableArrayList("votes");
            if(voteList != null && !voteList.isEmpty()) isShowingSearchedVotes = true;
        }

        adapter = new VoteAdapter(voteList, new RiksdagenViewHolderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Object document) {
                Intent intent = new Intent(getContext(), VoteActivity.class);
                intent.putExtra("document", (Vote) document);
                startActivity(intent);
            }
        });

    }

    /**
     * Load the next page and add it to the adapter when downloaded and parsed.
     * Hides the loading view.s
     */
    protected void loadNextPage(){

        if(!isShowingSearchedVotes) {
            setLoadingMoreItems(true);
            RikdagskollenApp.getInstance().getRiksdagenAPIManager().getVotes(new VoteCallback() {

                @Override
                public void onVotesFetched(List<Vote> votes) {
                    setShowLoadingView(false);
                    voteList.addAll(votes);
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
    }

    @Override
    RiksdagenViewHolderAdapter getAdapter() {
        return adapter;
    }
}
