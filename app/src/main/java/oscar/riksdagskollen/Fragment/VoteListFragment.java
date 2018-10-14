package oscar.riksdagskollen.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import oscar.riksdagskollen.Activity.VoteActivity;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Adapter.RiksdagenViewHolderAdapter;
import oscar.riksdagskollen.Util.Adapter.VoteAdapter;
import oscar.riksdagskollen.Util.Enum.DecicionCategory;
import oscar.riksdagskollen.Util.JSONModel.Vote;
import oscar.riksdagskollen.Util.RiksdagenCallback.VoteCallback;


/**
 * Created by oscar on 2018-03-29.
 */

public class VoteListFragment extends RiksdagenAutoLoadingListFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private List<Vote> voteList = new ArrayList<>();
    private List<DecicionCategory> oldFilter;
    private VoteAdapter adapter;
    private boolean isShowingSearchedVotes = false;
    private SharedPreferences preferences;


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
            applyFilter();
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isShowingSearchedVotes) setHasOptionsMenu(true);
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        setHasOptionsMenu(false);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            voteList = getArguments().getParcelableArrayList("votes");
            if(voteList != null && !voteList.isEmpty()) isShowingSearchedVotes = true;
        }

        if (!isShowingSearchedVotes) setHasOptionsMenu(true);

        preferences = getActivity().getSharedPreferences("vote_settings", Context.MODE_PRIVATE);
        adapter = new VoteAdapter(voteList, new RiksdagenViewHolderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Object document) {
                Intent intent = new Intent(getContext(), VoteActivity.class);
                intent.putExtra("document", (Vote) document);
                startActivity(intent);
            }
        });

    }

    private void applyFilter() {
        getAdapter().replaceAll(filter(voteList));
    }

    private void onFilterChanged() {
        List<DecicionCategory> filter = getFilter();
        if (!filter.equals(oldFilter)) {
            applyFilter();
        }

        if (filter.isEmpty()) noContentWarning.setVisibility(View.VISIBLE);
        else noContentWarning.setVisibility(View.GONE);

        if (getAdapter().getItemCount() < MIN_DOC && !filter.isEmpty()) loadNextPage();
    }

    private List<DecicionCategory> getFilter() {
        ArrayList<DecicionCategory> filter = new ArrayList<>();
        for (DecicionCategory category : DecicionCategory.values()) {
            if (preferences.getBoolean(category.getId(), true)) filter.add(category);
        }
        return filter;
    }

    private List<Vote> filter(List<Vote> documents) {
        final List<Vote> filteredDocumentList = new ArrayList<>();
        for (Vote document : documents) {
            if (getFilter().contains(DecicionCategory.getCategoryFromBet(document.getBeteckning()))) {
                filteredDocumentList.add(document);
            }
        }
        return filteredDocumentList;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_filter) {
            oldFilter = getFilter();
            final CharSequence[] items = DecicionCategory.getCategoryNames();
            boolean[] checked = new boolean[items.length];
            for (int i = 0; i < items.length; i++) {
                checked[i] = preferences.getBoolean(DecicionCategory.getAllCategories().get(i).getId(), true);
            }

            final SharedPreferences.Editor editor = preferences.edit();


            AlertDialog dialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom)
                    .setTitle("Filtrera voteringar efter kategori")
                    .setMultiChoiceItems(items, checked, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                            editor.putBoolean(DecicionCategory.getAllCategories().get(indexSelected).getId(), isChecked);
                        }
                    }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            editor.apply();
                        }
                    }).setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            editor.clear();
                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            editor.clear();
                        }
                    })
                    .create();

            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.filter, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Load the next page and add it to the adapter when downloaded and parsed.
     * Hides the loading view.s
     */
    protected void loadNextPage(){

        if(!isShowingSearchedVotes) {
            setLoadingMoreItems(true);
            RiksdagskollenApp.getInstance().getRiksdagenAPIManager().getVotes(new VoteCallback() {

                @Override
                public void onVotesFetched(List<Vote> votes) {
                    voteList.addAll(votes);
                    List<Vote> filteredDocuments = filter(votes);
                    getAdapter().addAll(filteredDocuments);

                    // Load next page if the requested page does not contain any documents matching the filter
                    // or if there are too few documents in the list

                    if ((filteredDocuments.isEmpty() || getAdapter().getItemCount() < MIN_DOC) && !getFilter().isEmpty()) {
                        loadNextPage();
                        setLoadingUntilFull(true);
                    } else {
                        setLoadingUntilFull(false);
                    }
                    if (!isLoadingUntilFull()) setLoadingMoreItems(false);
                    setShowLoadingView(false);
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        onFilterChanged();
    }
}
