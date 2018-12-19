package oscar.riksdagskollen.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Animatable;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import oscar.riksdagskollen.Activity.RepresentativeDetailActivity;
import oscar.riksdagskollen.Manager.RepresentativeManager;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Adapter.RepresentativeAdapter;
import oscar.riksdagskollen.Util.Adapter.RiksdagenViewHolderAdapter;
import oscar.riksdagskollen.Util.Enum.CurrentParties;
import oscar.riksdagskollen.Util.JSONModel.Party;
import oscar.riksdagskollen.Util.JSONModel.RepresentativeModels.Representative;

/**
 * Created by oscar on 2018-09-27.
 */

public class RepresentativeListFragment extends RiksdagenAutoLoadingListFragment implements RepresentativeManager.RepresentativeDownloadListener {

    private final List<Representative> representativeList = new ArrayList<>();
    private RepresentativeAdapter adapter;
    private boolean ascending = true;
    private Comparator<Representative> currentComparator = RepresentativeAdapter.NAME_COMPARATOR;
    private HashMap<String, Boolean> currentFilter = new HashMap<>();
    private HashMap<String, Boolean> oldFilter = new HashMap<>();

    public static RepresentativeListFragment newInstance() {
        return new RepresentativeListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.representatives);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        RiksdagskollenApp app = RiksdagskollenApp.getInstance();

        if (app.getRepresentativeManager().isRepresentativesDownloaded()) {
            setShowLoadingView(false);
            ArrayList<Representative> representatives = app.getRepresentativeManager().getCurrentRepresentatives();
            representativeList.addAll(representatives);
            getAdapter().addAll(representatives);
            setLoadingMoreItems(false);
        } else {
            // Make sure to download representatives if job for some reason could not be scheduled at startup
            app.scheduleDownloadRepresentativesJobIfNotRunning();
            app.getRepresentativeManager().addDownloadListener(this);
        }

        for (Party party : CurrentParties.getParties()) {
            currentFilter.put(party.getID(), true);
        }

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        setHasOptionsMenu(false);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_by_name:
                swapAdapter(ascending ?
                        RepresentativeAdapter.NAME_COMPARATOR :
                        new ReverseOrder<>(RepresentativeAdapter.NAME_COMPARATOR));
                break;
            case R.id.sort_by_surname:
                swapAdapter(ascending ?
                        RepresentativeAdapter.SURNAME_COMPARATOR :
                        new ReverseOrder<>(RepresentativeAdapter.SURNAME_COMPARATOR));
                break;
            case R.id.sort_by_age:
                swapAdapter(ascending ?
                        RepresentativeAdapter.AGE_COMPARATOR :
                        new ReverseOrder<>(RepresentativeAdapter.AGE_COMPARATOR));
                break;
            case R.id.sort_by_district:
                swapAdapter(ascending ?
                        RepresentativeAdapter.DISTRICT_COMPARATOR :
                        new ReverseOrder<>(RepresentativeAdapter.DISTRICT_COMPARATOR));
                break;
            case R.id.sort_order:
                setSortOrderAscending(!ascending);
                if (ascending) {
                    item.setIcon(R.drawable.ic_sort_ascending_animated);
                } else {
                    item.setIcon(R.drawable.ic_sort_descending_animated);
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((Animatable) item.getIcon()).start();
                    }
                });
                break;
            case R.id.filter_rep:
                showFilterDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showFilterDialog() {
        oldFilter = getFilter();
        final ArrayList<Party> parties = new ArrayList<>(CurrentParties.getParties());
        final CharSequence[] items = new CharSequence[parties.size()];
        for (int i = 0; i < parties.size(); i++) {
            items[i] = parties.get(i).getName();
        }

        boolean[] checked = new boolean[parties.size()];
        for (int i = 0; i < parties.size(); i++) {
            checked[i] = oldFilter.get(parties.get(i).getID());
        }

        final HashMap<String, Boolean> changes = new HashMap<>();

        AlertDialog dialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom)
                .setTitle("Filtrera ledamöter")
                .setMultiChoiceItems(items, checked, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        changes.put(parties.get(indexSelected).getID(), isChecked);
                    }
                }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        for (String partyID : changes.keySet()) {
                            currentFilter.put(partyID, changes.get(partyID));
                        }
                        applyFilter();
                    }
                }).setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
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

    private void setSortOrderAscending(boolean ascending) {
        this.ascending = ascending;
        swapAdapter(new ReverseOrder<>(currentComparator));
    }


    public void setCurrentComparator(Comparator<Representative> currentComparator) {
        this.currentComparator = currentComparator;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.representative_list_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        swapAdapter(RepresentativeAdapter.NAME_COMPARATOR);
    }

    //not used for this fragment
    @Override
    protected void loadNextPage() {

    }

    private HashMap<String, Boolean> getFilter() {
        return currentFilter;
    }

    private List<Representative> filter(List<Representative> representatives) {
        final List<Representative> filteredRepresentativeList = new ArrayList<>();
        for (Representative representative : representatives) {
            if (getFilter().get(representative.getParti().toLowerCase())) {
                filteredRepresentativeList.add(representative);
            }
        }
        return filteredRepresentativeList;
    }

    private void applyFilter() {
        HashMap<String, Boolean> filter = getFilter();
        getAdapter().replaceAll(filter(representativeList));
        showNoContentWarning(filter.isEmpty());
    }

    @Override
    public void onRepresentativesDownloaded(ArrayList<Representative> representatives) {
        setShowLoadingView(false);
        representativeList.addAll(representatives);
        getAdapter().addAll(representatives);
        setLoadingMoreItems(false);
    }

    @Override
    public void onFail() {
        onLoadFail();
    }


    private class ReverseOrder<T> implements Comparator<T> {
        private Comparator<T> delegate;

        public ReverseOrder(Comparator<T> delegate) {
            this.delegate = delegate;
        }

        public int compare(T a, T b) {
            //reverse order of a and b!!!
            return this.delegate.compare(b, a);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        RiksdagskollenApp.getInstance().getRepresentativeManager().removeDownloadListener(this);
    }

    private void swapAdapter(Comparator<Representative> comparator) {
        currentComparator = comparator;
        adapter = new RepresentativeAdapter(filter(representativeList), comparator, this, new RiksdagenViewHolderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Object document) {
                Intent repDetailsIntent = new Intent(getContext(), RepresentativeDetailActivity.class);
                repDetailsIntent.putExtra("representative", (Representative) document);
                startActivity(repDetailsIntent);
            }
        });
        if (getRecyclerView() != null) getRecyclerView().swapAdapter(adapter, false);
    }

    @Override
    protected void clearItems() {
    }

    @Override
    RiksdagenViewHolderAdapter getAdapter() {
        return adapter;
    }

    @Override
    protected void refresh() {
        showNoConnectionWarning(false);
        if (adapter.getItemCount() == 0 && !RiksdagskollenApp.getInstance().isDownloadRepsRunningOrScheduled()) {
            RiksdagskollenApp.getInstance().scheduleDownloadRepresentativesJobIfNotRunning();
        }
        if (!RiksdagskollenApp.getInstance().isDownloadRepsRunningOrScheduled()) {
            setLoadingMoreItems(false);
        }
    }
}
