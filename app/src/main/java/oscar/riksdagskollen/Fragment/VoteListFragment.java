package oscar.riksdagskollen.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import oscar.riksdagskollen.Activity.VoteActivity;
import oscar.riksdagskollen.Manager.AlertManager;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Adapter.RiksdagenViewHolderAdapter;
import oscar.riksdagskollen.Util.Adapter.VoteAdapter;
import oscar.riksdagskollen.Util.Enum.DecicionCategory;
import oscar.riksdagskollen.Util.JSONModel.Vote;
import oscar.riksdagskollen.Util.RiksdagenCallback.VoteCallback;
import oscar.riksdagskollen.Util.View.FilterDialog;


/**
 * Created by oscar on 2018-03-29.
 */

public class VoteListFragment extends RiksdagenAutoLoadingListFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private List<Vote> voteList = new ArrayList<>();
    private List<DecicionCategory> oldFilter;
    private VoteAdapter adapter;
    private boolean isShowingSearchedVotes = false;
    private SharedPreferences preferences;
    private MenuItem notificationItem;
    public static final String SECTION_NAME_VOTE = "vote";


    public static VoteListFragment newInstance(@Nullable ArrayList<Vote> votes) {
        VoteListFragment newInstance = new VoteListFragment();
        if (votes != null) {
            Bundle args = new Bundle();
            args.putParcelableArrayList("votes", votes);
            newInstance.setArguments(args);
        }
        return newInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (!isShowingSearchedVotes) {
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

        if (getArguments() != null) {
            voteList = getArguments().getParcelableArrayList("votes");
            if (voteList != null && !voteList.isEmpty()) isShowingSearchedVotes = true;
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
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.notification_menu_item:
                if (voteList.isEmpty()) break;
                boolean enabled = RiksdagskollenApp.getInstance().getAlertManager().toggleEnabledForPage(SECTION_NAME_VOTE, voteList.get(0).getId());
                if (enabled) {
                    item.setIcon(R.drawable.notifications_border_to_filled_animated);
                    Toast.makeText(getContext(), "Du kommer nu få en notis när en ny votering publiceras", Toast.LENGTH_LONG).show();
                } else {
                    item.setIcon(R.drawable.notifications_filled_to_border_animated);
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((Animatable) item.getIcon()).start();
                    }
                });
                break;
            case R.id.menu_filter:
                oldFilter = getFilter();
                final CharSequence[] items = DecicionCategory.getCategoryNames();
                boolean[] checked = new boolean[items.length];
                for (int i = 0; i < items.length; i++) {
                    checked[i] = preferences.getBoolean(DecicionCategory.getAllCategories().get(i).getId(), true);
                }

                final SharedPreferences.Editor editor = preferences.edit();

                FilterDialog dialog = new FilterDialog("Filtrera voteringar efter kategori", items, checked);
                dialog.setItemSelectedListener((which, isChecked) -> {
                    editor.putBoolean(DecicionCategory.getAllCategories().get(which).getId(), isChecked);
                });
                dialog.setPositiveButtonListener(v -> editor.apply());
                dialog.setNegativeButtonListener(v -> editor.clear());
                dialog.setOnDismissListener(dialogInterface -> editor.clear());
                dialog.show(getFragmentManager(), "dialog");
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.party_feed_menu, menu);
        notificationItem = menu.findItem(R.id.notification_menu_item);
        if (RiksdagskollenApp.getInstance().getAlertManager().isAlertEnabledForSection(SECTION_NAME_VOTE)) {
            notificationItem.setIcon(R.drawable.ic_notification_enabled);
        }
        if (voteList.size() > 0) {
            notificationItem.setVisible(true);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Load the next page and add it to the adapter when downloaded and parsed.
     * Hides the loading view.s
     */
    protected void loadNextPage() {

        if (!isShowingSearchedVotes) {
            setLoadingMoreItems(true);
            RiksdagskollenApp.getInstance().getRiksdagenAPIManager().getVotes(new VoteCallback() {

                @Override
                public void onVotesFetched(List<Vote> votes) {
                    voteList.addAll(votes);
                    List<Vote> filteredDocuments = filter(votes);
                    getAdapter().addAll(filteredDocuments);

                    if (getPageToLoad() <= 2) {
                        updateAlerts();
                    }

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
                    if (notificationItem != null) notificationItem.setVisible(true);
                }

                @Override
                public void onFail(VolleyError error) {
                    onLoadFail();
                }
            }, getPageToLoad());

            incrementPage();
        }
    }

    @Override
    protected void clearItems() {
        voteList.clear();
        adapter.clear();
    }

    @Override
    protected RiksdagenViewHolderAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        onFilterChanged();
    }

    private void updateAlerts() {
        if (AlertManager.getInstance().isAlertEnabledForSection(SECTION_NAME_VOTE)) {
            AlertManager.getInstance().setAlertEnabledForSection(SECTION_NAME_VOTE, voteList.get(0).getId(), true);
        }
    }
}
