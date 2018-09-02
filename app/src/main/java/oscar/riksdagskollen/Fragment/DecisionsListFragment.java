package oscar.riksdagskollen.Fragment;

import android.content.DialogInterface;
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

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RikdagskollenApp;
import oscar.riksdagskollen.Util.Adapter.DecisionListAdapter;
import oscar.riksdagskollen.Util.Adapter.RiksdagenViewHolderAdapter;
import oscar.riksdagskollen.Util.Callback.DecisionsCallback;
import oscar.riksdagskollen.Util.DecicionCategory;
import oscar.riksdagskollen.Util.JSONModel.DecisionDocument;

/**
 * Created by gustavaaro on 2018-06-16.
 */

public class DecisionsListFragment extends RiksdagenAutoLoadingListFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final List<DecisionDocument> decisionDocuments = new ArrayList<>();
    private List<DecicionCategory> oldFilter;
    private DecisionListAdapter adapter;
    private SharedPreferences preferences;

    public static DecisionsListFragment newInstance(){
        DecisionsListFragment newInstance = new DecisionsListFragment();
        return newInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.decisions);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        preferences = getActivity().getSharedPreferences("decisions_settings", getActivity().MODE_PRIVATE);

        adapter = new DecisionListAdapter(decisionDocuments, new RiksdagenViewHolderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Object document) {

            }
        }, getRecyclerView());

    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        setHasOptionsMenu(false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        applyFilter();
        showNoContentWarning(getFilter().isEmpty());
    }

    private List<DecicionCategory> getFilter() {
        ArrayList<DecicionCategory> filter = new ArrayList<>();
        for (DecicionCategory category : DecicionCategory.values()) {
            if (preferences.getBoolean(category.getId(), true)) filter.add(category);
        }
        return filter;
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
                    .setTitle("Filtrera beslut efter kategori")
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

    private List<DecisionDocument> filter(List<DecisionDocument> documents) {
        final List<DecisionDocument> filteredDocumentList = new ArrayList<>();
        for (DecisionDocument document : documents) {
            if (getFilter().contains(DecicionCategory.getCategoryFromBet(document.getBeteckning()))) {
                filteredDocumentList.add(document);
            }
        }
        return filteredDocumentList;
    }

    private void applyFilter() {
        getAdapter().replaceAll(filter(decisionDocuments));
    }

    private void onFilterChanged() {
        List<DecicionCategory> filter = getFilter();
        if (!filter.equals(oldFilter)) {
            applyFilter();
        }

        showNoContentWarning(filter.isEmpty());

        if (getAdapter().getItemCount() < MIN_DOC && !filter.isEmpty()) loadNextPage();
    }


    @Override
    protected void loadNextPage() {
        setLoadingMoreItems(true);
        RikdagskollenApp.getInstance().getRiksdagenAPIManager().getDecisions(new DecisionsCallback() {
            @Override
            public void onDecisionsFetched(List<DecisionDocument> documents) {
                decisionDocuments.addAll(documents);
                List<DecisionDocument> filteredDocuments = filter(documents);
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

    @Override
    RiksdagenViewHolderAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        onFilterChanged();
    }
}
