package oscar.riksdagskollen.Fragment;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Adapter.DecisionListAdapter;
import oscar.riksdagskollen.Util.Adapter.RiksdagenViewHolderAdapter;
import oscar.riksdagskollen.Util.Enum.DecicionCategory;
import oscar.riksdagskollen.Util.JSONModel.DecisionDocument;
import oscar.riksdagskollen.Util.RiksdagenCallback.DecisionsCallback;

/**
 * Created by gustavaaro on 2018-06-16.
 */

public class DecisionsListFragment extends RiksdagenAutoLoadingListFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final List<DecisionDocument> decisionDocuments = new ArrayList<>();
    private final List<DecisionDocument> searchedDecisions = new ArrayList<>();

    public static final String SECTION_NAME_DECISIONS = "decisions";


    private HashMap<String, Boolean> searchFilter = new HashMap<>();

    private List<DecicionCategory> oldFilter;
    private DecisionListAdapter adapter;
    private SharedPreferences preferences;
    private SearchView searchView;
    private String currentQuery;

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

        resetSearchFilter();
        preferences = getActivity().getSharedPreferences("decisions_settings", getActivity().MODE_PRIVATE);

        adapter = new DecisionListAdapter(decisionDocuments, new RiksdagenViewHolderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Object document) {

            }
        }, getRecyclerView());

    }

    private void resetSearchFilter() {
        searchFilter.clear();
        for (DecicionCategory category : DecicionCategory.getAllCategories()) {
            searchFilter.put(category.getId(), true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        preferences.unregisterOnSharedPreferenceChangeListener(this);
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
            if (!isSearching()) {
                if (preferences.getBoolean(category.getId(), true)) filter.add(category);
            } else {
                if (searchFilter.containsKey(category.getId()) && searchFilter.get(category.getId()))
                    filter.add(category);
            }
        }
        return filter;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_filter) {
            if (isSearching()) showSearchFilter();
            else showPreferenceFilter();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.dec_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        searchView = (SearchView) searchItem.getActionView();
        changeSearchViewTextColor(searchView);
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                setIsSearching(true);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                setIsSearching(false);
                return true;
            }
        });

        searchView.setQueryHint("Sök efter beslut");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchedDecisions.clear();
                getAdapter().replaceAll(searchedDecisions);
                currentQuery = query;
                resetSearchPage();
                loadNextPage();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
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
        if (!isSearching()) getAdapter().replaceAll(filter(decisionDocuments));
        else getAdapter().replaceAll(filter(searchedDecisions));

    }

    private void onFilterChanged() {
        List<DecicionCategory> filter = getFilter();
        if (!filter.equals(oldFilter)) {
            applyFilter();
        }

        showNoContentWarning(filter.isEmpty());

        if (getAdapter().getItemCount() < MIN_DOC && !filter.isEmpty()) loadNextPage();
    }

    private void setIsSearching(boolean searching) {
        if (searching) {
            setSearching(true);
            getAdapter().replaceAll(filter(searchedDecisions));
        } else {
            setSearching(false);
            currentQuery = "";
            searchedDecisions.clear();
            searchFilter.clear();
            resetSearchPage();
            resetSearchFilter();
            applyFilter();
        }
    }


    @Override
    protected void loadNextPage() {
        setLoadingMoreItems(true);
        if (isSearching()) {
            loadMoreSearchItems();
            incrementSearchPage();
        } else {
            loadMoreItems();
            incrementPage();
        }
    }

    @Override
    protected void clearItems() {
        decisionDocuments.clear();
        adapter.clear();
    }

    private void changeSearchViewTextColor(View view) {
        if (view != null) {
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(RiksdagskollenApp.getColorFromAttribute(R.attr.secondaryLightColor, getActivity()));
                if (view instanceof EditText)
                    ((EditText) view).setHintTextColor(RiksdagskollenApp.getColorFromAttribute(R.attr.secondaryLightColor, getActivity()));
                return;
            } else if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    changeSearchViewTextColor(viewGroup.getChildAt(i));
                }
            }
        }
    }

    private void loadMoreSearchItems() {
        RiksdagskollenApp.getInstance().getRiksdagenAPIManager().searchForDecision(new DecisionsCallback() {
            @Override
            public void onDecisionsFetched(List<DecisionDocument> decisions) {

                searchedDecisions.addAll(decisions);
                List<DecisionDocument> filteredDocuments = filter(decisions);

                int itemCountBeforeLoad = getAdapter().getItemCount();
                getAdapter().addAll(filteredDocuments);

                // Unpretty fix for a bug where recyclerview sometimes scrolls to the bottom after initial filter
                if (itemCountBeforeLoad <= 1) getRecyclerView().scrollToPosition(0);

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
                Toast.makeText(getContext(), "Inga resultat", Toast.LENGTH_LONG).show();
                setLoadingMoreItems(false);
                decrementSearchPage();
            }
        }, currentQuery, getSearchPageToLoad());
    }

    private void loadMoreItems() {
        RiksdagskollenApp.getInstance().getRiksdagenAPIManager().getDecisions(new DecisionsCallback() {
            @Override
            public void onDecisionsFetched(List<DecisionDocument> documents) {
                decisionDocuments.addAll(documents);
                List<DecisionDocument> filteredDocuments = filter(documents);

                int itemCountBeforeLoad = getAdapter().getItemCount();
                getAdapter().addAll(filteredDocuments);

                // Unpretty fix for a bug where recyclerview sometimes scrolls to the bottom after initial filter

                if (itemCountBeforeLoad <= 1) getRecyclerView().scrollToPosition(0);
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
                onLoadFail();
            }
        }, getPageToLoad());
    }

    @Override
    protected RiksdagenViewHolderAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        onFilterChanged();
    }

    private void showSearchFilter() {
        final CharSequence[] items = DecicionCategory.getCategoryNames();
        final DecicionCategory[] categories = DecicionCategory.values();
        boolean[] checked = new boolean[items.length];
        for (int i = 0; i < items.length; i++) {
            if (searchFilter.containsKey(categories[i].getId()))
                checked[i] = searchFilter.get(categories[i].getId());
            else checked[i] = true;
        }

        final HashMap<String, Boolean> changes = new HashMap<>();

        AlertDialog dialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom)
                .setTitle("Filtrera sökresultat efter kategori")
                .setMultiChoiceItems(items, checked, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        changes.put(categories[indexSelected].getId(), isChecked);
                    }
                }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        for (String catId : changes.keySet()) {
                            searchFilter.put(catId, changes.get(catId));
                        }
                        applyFilter();
                    }
                }).setNegativeButton("", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        changes.clear();
                        dialogInterface.dismiss();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        changes.clear();
                    }
                })
                .create();

        dialog.show();

    }

    private void showPreferenceFilter() {
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
}
