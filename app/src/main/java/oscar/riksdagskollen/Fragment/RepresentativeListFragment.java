package oscar.riksdagskollen.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import oscar.riksdagskollen.Activity.RepresentativeDetailActivity;
import oscar.riksdagskollen.Manager.RepresentativeManager;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Adapter.RepresentativeAdapter;
import oscar.riksdagskollen.Util.Adapter.RiksdagenViewHolderAdapter;
import oscar.riksdagskollen.Util.JSONModel.RepresentativeModels.Representative;

/**
 * Created by oscar on 2018-09-27.
 */

public class RepresentativeListFragment extends RiksdagenAutoLoadingListFragment {

    private final List<Representative> representativeList = new ArrayList<>();
    private RepresentativeAdapter adapter;
    private boolean ascending = true;
    private Comparator<Representative> currentComparator = RepresentativeAdapter.NAME_COMPARATOR;

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
            app.getRepresentativeManager().addDownloadListener(new RepresentativeManager.RepresentativeDownloadListener() {
                @Override
                public void onRepresentativesDownloaded(ArrayList<Representative> representatives) {
                    setShowLoadingView(false);
                    representativeList.addAll(representatives);
                    getAdapter().addAll(representatives);
                    setLoadingMoreItems(false);
                }
            });
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_by_name:
                setCurrentComparator(RepresentativeAdapter.NAME_COMPARATOR);
                swapAdapter(RepresentativeAdapter.NAME_COMPARATOR);
                break;
            case R.id.sort_by_surname:
                setCurrentComparator(RepresentativeAdapter.SURNAME_COMPARATOR);
                swapAdapter(RepresentativeAdapter.SURNAME_COMPARATOR);
                break;
            case R.id.sort_by_age:
                setCurrentComparator(RepresentativeAdapter.AGE_COMPARATOR);
                swapAdapter(RepresentativeAdapter.AGE_COMPARATOR);
            case R.id.sort_order_ascending:
                setSortOrderAscending(false);
                break;
            case R.id.sort_order_descending:
                setSortOrderAscending(true);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setSortOrderAscending(boolean ascending) {
        this.ascending = ascending;
        getActivity().invalidateOptionsMenu();
        swapAdapter(new ReverseOrder<>(currentComparator));
        setCurrentComparator(new ReverseOrder<>(currentComparator));
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        if (ascending) {
            menu.findItem(R.id.sort_order_ascending).setVisible(true);
            menu.findItem(R.id.sort_order_descending).setVisible(false);
        } else {
            menu.findItem(R.id.sort_order_ascending).setVisible(false);
            menu.findItem(R.id.sort_order_descending).setVisible(true);
        }

        super.onPrepareOptionsMenu(menu);
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


    private void swapAdapter(Comparator<Representative> comparator) {
        adapter = new RepresentativeAdapter(representativeList, comparator, new RiksdagenViewHolderAdapter.OnItemClickListener() {
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
    RiksdagenViewHolderAdapter getAdapter() {
        return adapter;
    }
}
