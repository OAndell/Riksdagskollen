package oscar.riksdagskollen.RepresentativeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import oscar.riksdagskollen.RepresentativeList.RepresentativeAdapter.SortingMode;
import oscar.riksdagskollen.RepresentativeList.data.Representative;
import oscar.riksdagskollen.Util.JSONModel.Party;

public interface RepresentativeListContract {

    interface Model {
        ArrayList<Representative> getRepresentatives();

        void onFilterChanged(HashMap<String, Boolean> filter);

        void initRepresentatives();

        boolean isSortOrderAscending();

        void setSortOrderAscending(boolean ascending);

        HashMap<String, Boolean> getCurrentFilter();

        void setSortingMode(SortingMode sortingMode);

        SortingMode getSortingMode();

        void setOldFilter(HashMap<String, Boolean> oldFilter);

        void setCurrentFilter(HashMap<String, Boolean> currentFilter);

        HashMap<String, Boolean> getOldFilter();

        void onDestroy();
    }

    interface View {
        void addRepresentativesToView(List<Representative> representatives);

        void showLoadingView(boolean loading);

        void showLoadingItemsView(boolean loading);

        void showLoadFailView();

        void clearAdapter();

        void replaceRepresentatives(ArrayList<Representative> representatives);

        void showNoContent(boolean noContent);

        void showFilterDialog(ArrayList<Party> parties, boolean[] checked, CharSequence[] displayNames);

        void swapAdapter(SortingMode sortingMode, boolean ascending, ArrayList<Representative> filteredList);

        void refreshSortOrderIcon(boolean isAscending);
    }

    interface Presenter {

        void onRepresentativesDataChanged();

        void onDataFiltered(HashMap<String, Boolean> changes);

        void onLoadDataFailed();

        void sortingOptionPressed(RepresentativeListPresenter.SortingParameter sortingParameter);

        void onFilterItemPressed();

        void onDestroy();

        boolean shouldFilterIndicators();
    }
}
