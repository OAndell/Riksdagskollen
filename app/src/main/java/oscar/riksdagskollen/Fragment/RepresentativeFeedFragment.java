package oscar.riksdagskollen.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import oscar.riksdagskollen.Activity.DocumentReaderActivity;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Adapter.PartyListViewholderAdapter;
import oscar.riksdagskollen.Util.Adapter.RiksdagenViewHolderAdapter;
import oscar.riksdagskollen.Util.Enum.DocumentType;
import oscar.riksdagskollen.Util.JSONModel.PartyDocument;
import oscar.riksdagskollen.Util.RiksdagenCallback.RepresentativeDocumentCallback;

/**
 * Created by gustavaaro on 2018-09-23.
 */

public class RepresentativeFeedFragment extends RiksdagenAutoLoadingListFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private String iid;
    private PartyListViewholderAdapter adapter;
    private ArrayList<PartyDocument> documentList = new ArrayList<>();
    private SharedPreferences preferences;
    private ArrayList<DocumentType> oldFilter;


    public static RepresentativeFeedFragment newInstance(String iid, ArrayList<PartyDocument> firstPage) {
        RepresentativeFeedFragment feedFragment = new RepresentativeFeedFragment();
        Bundle args = new Bundle();
        args.putString("iid", iid);
        args.putParcelableArrayList("firstPage", firstPage);
        feedFragment.setArguments(args);
        return feedFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
        preferences.registerOnSharedPreferenceChangeListener(this);
        showNoContentWarning(getFilter().isEmpty());
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
        if (getAdapter().getItemCount() < MIN_DOC && !getFilter().isEmpty()) loadNextPage();
        showNoContentWarning(getFilter().isEmpty());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.iid = getArguments().getString("iid");
        ArrayList<PartyDocument> firstPage = getArguments().getParcelableArrayList("firstPage");
        if (firstPage != null && !firstPage.isEmpty()) {
            documentList.addAll(firstPage);
            incrementPage();
        }
        preferences = getActivity().getSharedPreferences(iid, 0); //private mode
        adapter = new PartyListViewholderAdapter(documentList, new RiksdagenViewHolderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Object document) {
                Intent intent = new Intent(getContext(), DocumentReaderActivity.class);
                intent.putExtra("document", ((PartyDocument) document));
                intent.putExtra("intressent", iid);
                startActivity(intent);
            }
        }, this);
        getAdapter().setHasStableIds(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_filter) {
            oldFilter = getFilter();
            final CharSequence[] items = DocumentType.getDisplayNames();
            boolean[] checked = new boolean[items.length];
            for (int i = 0; i < items.length; i++) {
                checked[i] = preferences.getBoolean(DocumentType.getAllDokTypes().get(i).getDocType(), true);
            }

            final SharedPreferences.Editor editor = preferences.edit();


            AlertDialog dialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom)
                    .setTitle("Filtrera partiflöde")
                    .setMultiChoiceItems(items, checked, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                            editor.putBoolean(DocumentType.getAllDokTypes().get(indexSelected).getDocType(), isChecked);
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


    /**
     * Load the next page and add it to the adapter when downloaded and parsed.
     * Hides the loading view.
     */
    protected void loadNextPage() {
        setLoadingMoreItems(true);
        RiksdagskollenApp.getInstance().getRiksdagenAPIManager().getDocumentsForRepresentative(iid, getPageToLoad(), new RepresentativeDocumentCallback() {
            @Override
            public void onDocumentsFetched(List<PartyDocument> documents, String hits) {
                documentList.addAll(documents);
                List<PartyDocument> filteredDocuments = filter(documents);

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
                if (getAdapter().getObjectCount() == 0 && error.getMessage().equals("no_docs")) {
                    showNoContentWarning(true);
                } else {
                    onLoadFail();
                }

            }
        });
        incrementPage();
    }

    @Override
    protected void clearItems() {
        documentList.clear();
        adapter.clear();
    }

    private ArrayList<DocumentType> getFilter() {
        ArrayList<DocumentType> filter = new ArrayList<>();
        for (DocumentType documentType : DocumentType.values()) {
            if (preferences.getBoolean(documentType.getDocType(), true)) filter.add(documentType);
        }
        return filter;
    }

    private List<PartyDocument> filter(List<PartyDocument> documents) {
        final List<PartyDocument> filteredDocumentList = new ArrayList<>();
        for (PartyDocument document : documents) {
            if (getFilter().contains(DocumentType.getDocTypeForDocument(document))) {
                filteredDocumentList.add(document);
            }
        }
        return filteredDocumentList;
    }

    private void applyFilter() {
        getAdapter().replaceAll(filter(documentList));
    }

    private void onFilterChanged() {
        List<DocumentType> filter = getFilter();

        if (!filter.equals(oldFilter)) {
            applyFilter();
        }

        showNoContentWarning(filter.isEmpty());
        if (getAdapter().getItemCount() < MIN_DOC && !filter.isEmpty()) loadNextPage();

    }

    @Override
    protected RiksdagenViewHolderAdapter getAdapter() {
        return adapter;
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        onFilterChanged();
    }
}


