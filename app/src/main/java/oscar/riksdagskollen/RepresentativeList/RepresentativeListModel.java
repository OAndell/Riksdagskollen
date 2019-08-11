package oscar.riksdagskollen.RepresentativeList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import oscar.riksdagskollen.Manager.RepresentativeManager;
import oscar.riksdagskollen.RepresentativeList.data.Representative;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Adapter.RepresentativeAdapter;
import oscar.riksdagskollen.Util.Enum.CurrentParties;
import oscar.riksdagskollen.Util.JSONModel.Party;

public class RepresentativeListModel implements RepresentativeListContract.Model, RepresentativeManager.RepresentativeDownloadListener {
    private final List<Representative> representativeList = new ArrayList<>();
    private boolean ascending = true;
    private Comparator<Representative> currentComparator = RepresentativeAdapter.NAME_COMPARATOR;
    private HashMap<String, Boolean> currentFilter = new HashMap<>();
    private HashMap<String, Boolean> oldFilter = new HashMap<>();
    private RepresentativeListContract.Presenter presenter;


    RepresentativeListModel(RepresentativeListContract.Presenter presenter) {
        this.presenter = presenter;

        for (Party party : CurrentParties.getParties()) {
            currentFilter.put(party.getID(), true);
        }
    }

    @Override
    public void initRepresentatives() {
        RiksdagskollenApp app = RiksdagskollenApp.getInstance();
        if (app.getRepresentativeManager().isRepresentativesDownloaded()) {
            representativeList.addAll(app.getRepresentativeManager().getCurrentRepresentatives());
            presenter.onRepresentativesDataChanged();
        } else {
            // Make sure to download representatives if job for some reason could not be scheduled at startup
            app.scheduleDownloadRepresentativesJobIfNotRunning();
            app.getRepresentativeManager().addDownloadListener(this);
        }
    }

    @Override
    public boolean isSortOrderAscending() {
        return ascending;
    }

    @Override
    public void setSortOrderAscending(boolean ascending) {
        this.ascending = ascending;
    }

    @Override
    public HashMap<String, Boolean> getCurrentFilter() {
        return currentFilter;
    }

    @Override
    public ArrayList<Representative> getRepresentatives() {
        return (ArrayList<Representative>) representativeList;
    }

    public void setCurrentComparator(Comparator<Representative> currentComparator) {
        this.currentComparator = currentComparator;
    }

    public Comparator<Representative> getCurrentComparator() {
        return currentComparator;
    }

    public void setOldFilter(HashMap<String, Boolean> oldFilter) {
        this.oldFilter = oldFilter;
    }

    public void setCurrentFilter(HashMap<String, Boolean> currentFilter) {
        this.currentFilter = currentFilter;
    }

    public HashMap<String, Boolean> getOldFilter() {
        return oldFilter;
    }

    @Override
    public void onDestroy() {
        RiksdagskollenApp.getInstance().getRepresentativeManager().removeDownloadListener(this);
    }

    @Override
    public void onFilterChanged(HashMap<String, Boolean> filter) {
        this.currentFilter = filter;
    }

    @Override
    public void onRepresentativesDownloaded(ArrayList<Representative> representatives) {
        this.representativeList.addAll(representatives);
        presenter.onRepresentativesDataChanged();
    }

    @Override
    public void onFail() {
        presenter.onLoadDataFailed();
    }
}
