package oscar.riksdagskollen.RepresentativeList;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.reddit.indicatorfastscroll.FastScrollItemIndicator;
import com.reddit.indicatorfastscroll.FastScrollerThumbView;
import com.reddit.indicatorfastscroll.FastScrollerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import oscar.riksdagskollen.Activity.RepresentativeDetailActivity;
import oscar.riksdagskollen.Fragment.RiksdagenAutoLoadingListFragment;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RepresentativeList.RepresentativeAdapter.SortingMode;
import oscar.riksdagskollen.RepresentativeList.data.Representative;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Adapter.RiksdagenViewHolderAdapter;
import oscar.riksdagskollen.Util.JSONModel.Party;
import oscar.riksdagskollen.Util.View.FilterDialog;

/**
 * Created by oscar on 2018-09-27.
 */

public class RepresentativeListFragment extends RiksdagenAutoLoadingListFragment implements RepresentativeListContract.View {

    private RepresentativeAdapter adapter;
    private RepresentativeListContract.Presenter presenter;
    public static final String SECTION_NAME_REPS = "reps";
    private MenuItem sortOrderItem;


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
        super.onViewCreated(view, savedInstanceState);
        presenter = new RepresentativeListPresenter(this);
        if (Build.VERSION.SDK_INT >= 21) {
            FastScrollerView fastScrollerView = view.findViewById(R.id.fastscroller);
            fastScrollerView.setVisibility(View.VISIBLE);

            fastScrollerView.setupWithRecyclerView(getRecyclerView(),
                    (position) -> new FastScrollItemIndicator.Text(adapter.getIndicatorStringAtIndex(position)),
                    // Hide every other age indicator
                    (indicator, indicatorPosition, totalIndicators) -> !presenter.shouldFilterIndicators() || indicatorPosition % 2 == 0);
            FastScrollerThumbView fastScrollerThumbView = view.findViewById(R.id.fastscroller_thumb);
            fastScrollerThumbView.setupWithFastScroller(fastScrollerView);
            fastScrollerView.setUseDefaultScroller(false);
            fastScrollerView.getItemIndicatorSelectedCallbacks().add(
                    (indicator, indicatorCenterY, itemPosition) -> {
                        getRecyclerView().stopScroll();
                        getRecyclerView().smoothScrollToPosition(itemPosition);
                    }
            );
        }
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
                presenter.sortingOptionPressed(RepresentativeListPresenter.SortingParameter.NAME);
                break;
            case R.id.sort_by_surname:
                presenter.sortingOptionPressed(RepresentativeListPresenter.SortingParameter.SURNAME);
                break;
            case R.id.sort_by_age:
                presenter.sortingOptionPressed(RepresentativeListPresenter.SortingParameter.AGE);
                break;
            case R.id.sort_by_district:
                presenter.sortingOptionPressed(RepresentativeListPresenter.SortingParameter.DISTRICT);
                break;
            case R.id.sort_order:
                presenter.sortingOptionPressed(RepresentativeListPresenter.SortingParameter.SORT_ORDER);
                break;
            case R.id.filter_rep:
                presenter.onFilterItemPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showFilterDialog(final ArrayList<Party> parties, boolean[] checked, CharSequence[] displayNames) {

        final HashMap<String, Boolean> changes = new HashMap<>();
        FilterDialog dialog = new FilterDialog("Filtrera ledamöter", displayNames, checked);
        dialog.setItemSelectedListener((which, isChecked) -> {
            changes.put(parties.get(which).getID(), isChecked);
        });
        dialog.setPositiveButtonListener(v -> presenter.onDataFiltered(changes));
        dialog.setNegativeButtonListener(v -> changes.clear());
        dialog.setOnDismissListener(dialogInterface -> changes.clear());
        dialog.show(getFragmentManager(), "dialog");
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.representative_list_menu, menu);
        sortOrderItem = menu.findItem(R.id.sort_order);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        swapAdapter(SortingMode.NAME, true, new ArrayList<>());
    }

    //not used for this fragment
    @Override
    protected void loadNextPage() {

    }

    @Override
    public void replaceRepresentatives(ArrayList<Representative> representatives) {
        getAdapter().replaceAll(representatives);
    }

    @Override
    public void showNoContent(boolean noContent) {
        showNoContentWarning(noContent);
    }

    @Override
    public void addRepresentativesToView(List<Representative> representatives) {
        adapter.addAll(representatives);
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

    @Override
    public void clearAdapter() {
        adapter.clear();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        presenter.onDestroy();
    }

    @Override
    public void swapAdapter(SortingMode sortingMode, boolean ascending, ArrayList<Representative> filteredList) {
        adapter = new RepresentativeAdapter(filteredList, sortingMode, ascending, this, document -> {
            Intent repDetailsIntent = new Intent(getContext(), RepresentativeDetailActivity.class);
            repDetailsIntent.putExtra("representative", (Representative) document);
            startActivity(repDetailsIntent);
        });

        if (getRecyclerView() != null) {
            getRecyclerView().swapAdapter(adapter, false);
        }
    }

    @Override
    public void refreshSortOrderIcon(boolean isAscending) {
        if (isAscending) {
            sortOrderItem.setIcon(R.drawable.ic_sort_ascending_animated);
        } else {
            sortOrderItem.setIcon(R.drawable.ic_sort_descending_animated);
        }
        getActivity().runOnUiThread(() -> ((Animatable) sortOrderItem.getIcon()).start());
    }
    @Override
    protected void clearItems() {
    }

    @Override
    protected RiksdagenViewHolderAdapter getAdapter() {
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
