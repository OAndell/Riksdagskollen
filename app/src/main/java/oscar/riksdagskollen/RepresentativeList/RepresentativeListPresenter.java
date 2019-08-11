package oscar.riksdagskollen.RepresentativeList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import oscar.riksdagskollen.RepresentativeList.data.Representative;
import oscar.riksdagskollen.Util.Adapter.RepresentativeAdapter;
import oscar.riksdagskollen.Util.Enum.CurrentParties;
import oscar.riksdagskollen.Util.JSONModel.Party;

public class RepresentativeListPresenter implements RepresentativeListContract.Presenter {

    private RepresentativeListContract.View view;
    private RepresentativeListContract.Model model;

    public enum SortingParameter {
        NAME,
        SURNAME,
        AGE,
        DISTRICT,
        SORT_ORDER
    }

    RepresentativeListPresenter(RepresentativeListContract.View view) {
        this.view = view;
        this.model = new RepresentativeListModel(this);
        model.initRepresentatives();
    }

    @Override
    public void onRepresentativesDataChanged() {
        view.clearAdapter();
        view.showLoadingView(false);
        view.addRepresentativesToView(model.getRepresentatives());
        view.showLoadingItemsView(false);
    }

    @Override
    public void onDataFiltered(HashMap<String, Boolean> changes) {
        HashMap<String, Boolean> newFilter = getFilter();
        for (String partyID : changes.keySet()) {
            newFilter.put(partyID, changes.get(partyID));
        }
        model.setCurrentFilter(newFilter);
        ArrayList<Representative> filteredReps = filter(model.getRepresentatives());
        view.replaceRepresentatives(filteredReps);

        if (filteredReps.isEmpty()) {
            view.showNoContent(true);
        } else {
            view.showNoContent(false);
        }
    }

    private HashMap<String, Boolean> getFilter() {
        return model.getCurrentFilter();
    }

    private ArrayList<Representative> filter(ArrayList<Representative> representatives) {
        final ArrayList<Representative> filteredRepresentativeList = new ArrayList<>();
        for (Representative representative : representatives) {
            if (getFilter().containsKey(representative.getParti().toLowerCase()) &&
                    getFilter().get(representative.getParti().toLowerCase())) {
                filteredRepresentativeList.add(representative);
            }
        }
        return filteredRepresentativeList;
    }

    @Override
    public void onLoadDataFailed() {
        view.showLoadFailView();
    }

    @Override
    public void sortingOptionPressed(SortingParameter sortingParameter) {
        Comparator<Representative> comparator = null;
        switch (sortingParameter) {
            case NAME:
                comparator = model.isSortOrderAscending() ?
                        RepresentativeAdapter.NAME_COMPARATOR :
                        new ReverseOrder<>(RepresentativeAdapter.NAME_COMPARATOR);
                break;
            case SURNAME:
                comparator = model.isSortOrderAscending() ?
                        RepresentativeAdapter.SURNAME_COMPARATOR :
                        new ReverseOrder<>(RepresentativeAdapter.SURNAME_COMPARATOR);
                break;
            case AGE:
                comparator = model.isSortOrderAscending() ?
                        RepresentativeAdapter.AGE_COMPARATOR :
                        new ReverseOrder<>(RepresentativeAdapter.AGE_COMPARATOR);
                break;
            case DISTRICT:
                comparator = model.isSortOrderAscending() ?
                        RepresentativeAdapter.DISTRICT_COMPARATOR :
                        new ReverseOrder<>(RepresentativeAdapter.DISTRICT_COMPARATOR);
                break;
            case SORT_ORDER:
                model.setSortOrderAscending(!model.isSortOrderAscending());
                view.refreshSortOrderIcon(model.isSortOrderAscending());
                comparator = new ReverseOrder<>(model.getCurrentComparator());
        }
        model.setCurrentComparator(comparator);
        view.swapAdapter(comparator, filter(model.getRepresentatives()));

    }

    @Override
    public void onFilterItemPressed() {
        model.setOldFilter(getFilter());
        final ArrayList<Party> parties = new ArrayList<>(CurrentParties.getParties());
        final CharSequence[] items = new CharSequence[parties.size()];
        for (int i = 0; i < parties.size(); i++) {
            items[i] = parties.get(i).getName();
        }

        boolean[] checked = new boolean[parties.size()];
        for (int i = 0; i < parties.size(); i++) {
            checked[i] = model.getCurrentFilter().get(parties.get(i).getID());
        }
        view.showFilterDialog(parties, checked, items);
    }

    @Override
    public void onDestroy() {
        model.onDestroy();
    }


    private class ReverseOrder<T> implements Comparator<T> {
        private Comparator<T> delegate;

        ReverseOrder(Comparator<T> delegate) {
            this.delegate = delegate;
        }

        public int compare(T a, T b) {
            //reverse order of a and b!!!
            return this.delegate.compare(b, a);
        }
    }


}
