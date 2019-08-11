package oscar.riksdagskollen.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Adapter.RiksdagenViewHolderAdapter;

/**
 * Created by gustavaaro on 2018-04-04.
 */

public abstract class RiksdagenAutoLoadingListFragment extends Fragment {

    private boolean loading;
    private boolean loadingUntilFull = false;
    private boolean isSearching = false;

    private int pageToLoad = 1;
    private int searchPageToLoad = 1;
    private int pastVisiblesItems;

    public static final int MIN_DOC = 6;

    private RiksdagenViewHolderAdapter adapter;
    private ViewGroup loadingView;
    private RecyclerView recyclerView;
    private ProgressBar itemsLoadingView;
    private SwipeRefreshLayout refreshLayout;
    protected TextView noContentWarning;
    private ViewGroup noConnectionWarning;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_autoloading_list, null);
        recyclerView = view.findViewById(R.id.recycler_view);
        adapter = getAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(true);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        noConnectionWarning = view.findViewById(R.id.no_connection_warning);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    int visibleItemCount = mLayoutManager.getChildCount();
                    int totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        if(!loading) loadNextPage();
                    }

                }
            }
        });
        loadingView = view.findViewById(R.id.loading_view);
        noContentWarning = view.findViewById(R.id.no_content_warning);

        itemsLoadingView = new ProgressBar(getContext());
        itemsLoadingView.getIndeterminateDrawable().setColorFilter(
                RiksdagskollenApp.getColorFromAttribute(R.attr.buttonColor, getContext()),
                android.graphics.PorterDuff.Mode.MULTIPLY);

        if (adapter.getItemCount() < MIN_DOC) loadNextPage();
        return view;
    }

    public void setLoadingUntilFull(boolean loadingUntilFull) {
        this.loadingUntilFull = loadingUntilFull;
    }

    protected void showNoContentWarning(boolean show) {
        if (show) noContentWarning.setVisibility(View.VISIBLE);
        else noContentWarning.setVisibility(View.GONE);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // If we already have content in the adapter, do not show the loading view
        if(adapter.getObjectCount() > 0) setShowLoadingView(false);
        super.onViewCreated(view, savedInstanceState);
    }

    protected abstract void loadNextPage();

    protected abstract void clearItems();

    public boolean isLoading() {
        return loading;
    }

    public boolean isSearching() {
        return isSearching;
    }

    public void setSearching(boolean searching) {
        isSearching = searching;
    }

    protected int getPageToLoad() {
        return pageToLoad;
    }

    protected void incrementPage() {
        pageToLoad++;
    }

    protected void decrementPage() {
        pageToLoad--;
    }

    protected void incrementSearchPage() {
        searchPageToLoad++;
    }

    protected void decrementSearchPage() {
        searchPageToLoad--;
    }

    protected void resetSearchPage() {
        searchPageToLoad = 1;
    }

    protected void resetPageToLoad() {
        pageToLoad = 1;
    }

    public int getSearchPageToLoad() {
        return searchPageToLoad;
    }

    protected abstract RiksdagenViewHolderAdapter getAdapter();

    public boolean isLoadingUntilFull() {
        return loadingUntilFull;
    }

    protected void setLoadingMoreItems(Boolean loading) {
        this.loading = loading;
        showNoConnectionWarning(false);
        // The runnables are apparently needed to avoid long warnings
        if (loading && getAdapter().getItemCount() > 0) {
            recyclerView.post(new Runnable() {
                public void run() {
                    adapter.addFooter(itemsLoadingView);
                }
            });
        } else {
            refreshLayout.setRefreshing(false);
            recyclerView.post(new Runnable() {
                public void run() {
                    adapter.removeFooter(itemsLoadingView);
                }
            });
        }
    }

    protected RecyclerView getRecyclerView() {
        return recyclerView;
    }

    protected void setShowLoadingView(final boolean loading) {
        showNoConnectionWarning(false);

        if (loading) {
            loadingView.setVisibility(View.VISIBLE);
            refreshLayout.setRefreshing(false);
        }
        else loadingView.setVisibility(View.GONE);
    }

    protected void showNoConnectionWarning(boolean show) {
        if (show) noConnectionWarning.setVisibility(View.VISIBLE);
        else noConnectionWarning.setVisibility(View.GONE);
    }

    protected void refresh() {
        showNoConnectionWarning(false);
        clearItems();
        resetPageToLoad();
        loadNextPage();
        refreshLayout.setRefreshing(true);
    }

    protected void onLoadFail() {
        setShowLoadingView(false);
        setLoadingMoreItems(false);
        decrementPage();

        // Most likely a network failure
        if (adapter != null && adapter.getItemCount() == 0) {
            showNoConnectionWarning(true);
        }
    }


}
