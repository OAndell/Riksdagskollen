package oscar.riksdagskollen.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Adapter.RiksdagenViewHolderAdapter;

/**
 * Created by gustavaaro on 2018-04-04.
 */

public abstract class RiksdagenAutoLoadingListFragment extends Fragment {

    private RiksdagenViewHolderAdapter adapter;
    private ViewGroup loadingView;
    private RecyclerView recyclerView;
    private boolean loading;
    private boolean loadingUntilFull = false;
    private int pageToLoad = 1;
    private int searchPageToLoad = 1;
    private boolean isSearching = false;
    private ProgressBar itemsLoadingView;
    private int pastVisiblesItems;
    protected TextView noContentWarning;
    protected static final int MIN_DOC = 6;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_party_list,null);
        recyclerView = view.findViewById(R.id.recycler_view);
        adapter = getAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(true);
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

        ((ProgressBar) loadingView.findViewById(R.id.progress_bar)).getIndeterminateDrawable().setColorFilter(
                RiksdagskollenApp.getColorFromAttribute(R.attr.secondaryLightColor, getContext()),
                android.graphics.PorterDuff.Mode.MULTIPLY);

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

    public boolean isLoading() {
        return loading;
    }

    public boolean isSearching() {
        return isSearching;
    }

    public void setSearching(boolean searching) {
        isSearching = searching;
    }

    int getPageToLoad() {
        return pageToLoad;
    }

    void incrementPage(){
        pageToLoad++;
    }

    void decrementPage(){
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

    public int getSearchPageToLoad() {
        return searchPageToLoad;
    }

    abstract RiksdagenViewHolderAdapter getAdapter();

    public boolean isLoadingUntilFull() {
        return loadingUntilFull;
    }

    void setLoadingMoreItems(Boolean loading){
        this.loading = loading;

        // The runnables are apparently needed to avoid long warnings
        if(loading && pageToLoad > 1){
            recyclerView.post(new Runnable() {
                public void run() {
                    adapter.addFooter(itemsLoadingView);
                }
            });
        } else {
            recyclerView.post(new Runnable() {
                public void run() {
                    adapter.removeFooter(itemsLoadingView);
                }
            });
        }

    }

    RecyclerView getRecyclerView() {
        return recyclerView;
    }

    void setShowLoadingView(final boolean loading) {
        if(loading) loadingView.setVisibility(View.VISIBLE);
        else loadingView.setVisibility(View.GONE);
    }
}
