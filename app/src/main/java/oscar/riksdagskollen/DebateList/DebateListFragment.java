package oscar.riksdagskollen.DebateList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import oscar.riksdagskollen.Fragment.RiksdagenAutoLoadingListFragment;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.Util.Adapter.RiksdagenViewHolderAdapter;
import oscar.riksdagskollen.Util.JSONModel.PartyDocument;


/**
 * List of most recent Interpolations with debates.
 */

public class DebateListFragment extends RiksdagenAutoLoadingListFragment implements DebateContract.View {
    private final List<PartyDocument> documentList = new ArrayList<>();
    private DebateListAdapter adapter;
    private DebateContract.Presenter presenter = new DebateListPresenter(this);

    public static final String SECTION_NAME_DEBATE = "debate";

    public static DebateListFragment newInstance() {
        DebateListFragment newInstance = new DebateListFragment();
        return newInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.debate_nav);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new DebateListAdapter(documentList, clickedDocument -> presenter.handleItemClick((PartyDocument) clickedDocument, getContext()), this);
    }

    /**
     * Load the next page and add it to the adapter when downloaded and parsed.
     * Hides the loading view.
     */
    protected void loadNextPage() {
        presenter.loadMoreItems();
    }

    @Override
    protected void clearItems() {
        documentList.clear();
        adapter.clear();
        presenter.clear();
    }

    @Override
    protected RiksdagenViewHolderAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void addItemsToView(List<PartyDocument> documents) {
        documentList.addAll(documents);
        getAdapter().addAll(documents);
    }

    @Override
    public void showLoadingView(boolean loading) {
        setShowLoadingView(loading);
    }

    @Override
    public void showLoadingItemsView(boolean loading) {
        setLoadingMoreItems(loading);
    }

    @Override
    public void showLoadFailView() {
        onLoadFail();
    }
}
