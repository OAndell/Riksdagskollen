package oscar.riksdagskollen.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import oscar.riksdagskollen.Activity.DocumentReaderActivity;
import oscar.riksdagskollen.Manager.RiksdagenAPIManager;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Adapter.PartyListViewholderAdapter;
import oscar.riksdagskollen.Util.Adapter.RiksdagenViewHolderAdapter;
import oscar.riksdagskollen.Util.JSONModel.PartyDocument;
import oscar.riksdagskollen.Util.RiksdagenCallback.PartyDocumentCallback;

public class SearchListFragment extends RiksdagenAutoLoadingListFragment {

    private ArrayList<PartyDocument> documentList = new ArrayList<>();
    private PartyListViewholderAdapter adapter;
    private SearchView searchView;
    private String searchTerm = "";
    private Boolean hasSearched = false; //To make sure it does start searching before the user has entered a query.
    private String searchOption = RiksdagenAPIManager.SEARCH_OPTION_REL;

    public static final String SECTION_NAME_SEARCH = "search";



    public static SearchListFragment newInstance() {
        SearchListFragment newInstance = new SearchListFragment();
        newInstance.setRetainInstance(true);
        return newInstance;
    }


    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.search_nav);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        adapter = new PartyListViewholderAdapter(documentList, new RiksdagenViewHolderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Object document) {
                Intent intent = new Intent(getContext(), DocumentReaderActivity.class);
                intent.putExtra("document", ((PartyDocument) document));
                startActivity(intent);
            }
        }, this);
    }


    protected void loadNextPage() {
        if (hasSearched) {
            setLoadingMoreItems(true);
            RiksdagskollenApp.getInstance().getRiksdagenAPIManager().searchForDocument(searchTerm, searchOption, getPageToLoad(), new PartyDocumentCallback() {
                @Override
                public void onDocumentsFetched(List<PartyDocument> documents) {
                    documentList.addAll(documents);
                    getAdapter().addAll(documents);
                    if (!isLoadingUntilFull()) setLoadingMoreItems(false);
                    setShowLoadingView(false);
                }

                @Override
                public void onFail(VolleyError error) {
                    onLoadFail();
                }
            });
            incrementPage();
        } else {
            //Remove loading if user has not yet entered a search.
            setShowLoadingView(false);
        }
    }


    @Override
    protected void clearItems() {
        documentList.clear();
        adapter.clear();
    }

    @Override
    RiksdagenViewHolderAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.doc_search_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.menu_search);
        searchView = (SearchView) searchItem.getActionView();
        changeSearchViewTextColor(searchView);

        searchItem.expandActionView();
        searchView.setQueryHint("Sök efter dokument...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchTerm = query;
                hasSearched = true;
                searchView.clearFocus();
                doNewSearch();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        MenuItem optionsItem = menu.findItem(R.id.menu_sort);
        optionsItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                CharSequence[] options = {"Relevans", "Datum"};
                int checkedItem = 0;
                if (searchOption.equals(RiksdagenAPIManager.SEARCH_OPTION_DATE)) {
                    checkedItem = 1;
                }
                AlertDialog dialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom)
                        .setTitle("Sortera dokument efter:")
                        .setSingleChoiceItems(options, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        searchOption = RiksdagenAPIManager.SEARCH_OPTION_REL;
                                        dialog.dismiss();
                                        doNewSearch();
                                        break;
                                    case 1:
                                        searchOption = RiksdagenAPIManager.SEARCH_OPTION_DATE;
                                        dialog.dismiss();
                                        doNewSearch();
                                        break;
                                }
                            }
                        })
                        .create();
                dialog.show();
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void doNewSearch() {
        refresh();
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


}

