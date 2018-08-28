package oscar.riksdagskollen.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
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
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import oscar.riksdagskollen.Activity.MotionActivity;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RikdagskollenApp;
import oscar.riksdagskollen.Util.Callback.PartyDocumentCallback;
import oscar.riksdagskollen.Util.JSONModel.Party;
import oscar.riksdagskollen.Util.JSONModel.PartyDocument;
import oscar.riksdagskollen.Util.Adapter.RiksdagenViewHolderAdapter;
import oscar.riksdagskollen.Util.Adapter.PartyListViewholderAdapter;
import oscar.riksdagskollen.Util.PartyDocumentType;

/**
 * Created by gustavaaro on 2018-03-26.
 */

public class PartyListFragment extends RiksdagenAutoLoadingListFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Party party;
    private final List<PartyDocument> documentList = new ArrayList<>();
    private PartyListViewholderAdapter adapter;
    private SharedPreferences preferences;
    private ArrayList<PartyDocumentType> oldFilter;

    /**
     *
     * @param party the party object that the fragment will display feed for
     * @return a new instance of this fragment with the Party object in its arguments
     */

    public static PartyListFragment newInstance(Party party){
        Bundle args = new Bundle();
        args.putParcelable("party",party);
        PartyListFragment newInstance = new PartyListFragment();
        newInstance.setArguments(args);

        return newInstance;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(party.getName());
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        this.party = getArguments().getParcelable("party");
        preferences = getActivity().getSharedPreferences(party.getID(),getActivity().MODE_PRIVATE);
        adapter = new PartyListViewholderAdapter(documentList, new RiksdagenViewHolderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Object document) {
                Intent intent = new Intent(getContext(), MotionActivity.class);
                intent.putExtra("document",((PartyDocument)document));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.menu_filter){
            oldFilter = getFilter();
            final CharSequence[] items = PartyDocumentType.getDisplayNames();
            boolean[] checked = new boolean[]{false, false, false};
            for (int i = 0; i < items.length; i++){
                checked[i] = preferences.getBoolean(PartyDocumentType.getAllDokTypes().get(i).getDocType(), false);
            }

            final SharedPreferences.Editor editor = preferences.edit();


            AlertDialog dialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom)
                    .setTitle("Filtrera partiflöde")
                    .setMultiChoiceItems(items, checked, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                            editor.putBoolean(PartyDocumentType.getAllDokTypes().get(indexSelected).getDocType(), isChecked);
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

    private void onFilterChange(){
        // Only reload if necessary
        if(!oldFilter.equals(getFilter())){
            resetPage();
            documentList.clear();
            loadNextPage();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.filter, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
        }, getFilter());
        incrementPage();
    }

    private ArrayList<PartyDocumentType> getFilter(){
        ArrayList<PartyDocumentType> filter = new ArrayList<>();
        for (PartyDocumentType documentType: PartyDocumentType.values()) {
            if(preferences.getBoolean(documentType.getDocType(), false)) filter.add(documentType);
        }
        return filter;
    }

    @Override
    RiksdagenViewHolderAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        onFilterChange();
    }
}



