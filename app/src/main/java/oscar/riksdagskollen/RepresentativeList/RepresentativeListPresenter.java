package oscar.riksdagskollen.RepresentativeList;

import java.util.ArrayList;
import java.util.HashMap;

import oscar.riksdagskollen.RepresentativeList.RepresentativeAdapter.SortingMode;
import oscar.riksdagskollen.RepresentativeList.data.Representative;
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

        SortingMode sortingMode = model.getSortingMode();
        switch (sortingParameter) {
            case NAME:
                sortingMode = SortingMode.NAME;
                break;
            case SURNAME:
                sortingMode = SortingMode.SURNAME;
                break;
            case AGE:
                sortingMode = SortingMode.AGE;
                break;
            case DISTRICT:
                sortingMode = SortingMode.DISTRICT;
                break;
            case SORT_ORDER:
                model.setSortOrderAscending(!model.isSortOrderAscending());
                view.refreshSortOrderIcon(model.isSortOrderAscending());
        }
        model.setSortingMode(sortingMode);
        view.swapAdapter(sortingMode, model.isSortOrderAscending(), filter(model.getRepresentatives()));
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

    @Override
    public boolean shouldFilterIndicators() {
        return model.getSortingMode() == SortingMode.AGE;
    }





}
