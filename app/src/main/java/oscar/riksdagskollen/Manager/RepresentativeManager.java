package oscar.riksdagskollen.Manager;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import oscar.riksdagskollen.Util.JSONModel.RepresentativeModels.Representative;
import oscar.riksdagskollen.Util.Job.DownloadRepresentativesJob;

/**
 * Created by gustavaaro on 2018-09-27.
 */

public class RepresentativeManager {

    private Context context;
    private boolean representativesDownloaded;
    // Hashmap to store reps in according to partyId : {intressentid: representative }
    private HashMap<String, HashMap<String, Representative>> representatives;
    private ArrayList<Representative> currentRepresentatives;
    private ArrayList<RepresentativeDownloadListener> listenerList;

    private String[] partyIds = {"m", "s", "sd", "l", "c", "mp", "v", "kd", "-"};


    public RepresentativeManager(Context appContext) {
        this.context = appContext;
        representatives = new HashMap<>();
        listenerList = new ArrayList<>();
        currentRepresentatives = new ArrayList<>();
        DownloadRepresentativesJob.scheduleJob();

        for (String partyId : partyIds) {
            representatives.put(partyId, new HashMap<String, Representative>());
        }
    }


    public void addCurrentRepresentatives(Collection<Representative> representatives) {
        currentRepresentatives.addAll(representatives);
        for (Representative rep : representatives) {
            addRepresentative(rep);
        }
        representativesDownloaded = true;
        notifyDownloaded();
    }

    public void addRepresentatives(Collection<Representative> representatives) {
        for (Representative rep : representatives) {
            addRepresentative(rep);
        }
        representativesDownloaded = true;
    }

    public void addRepresentative(Representative representative) {
        addRepresentativeToParty(representative.getParti(), representative);
    }

    private void addRepresentativeToParty(String party, Representative representative) {
        representatives.get(party.toLowerCase()).put(representative.getIntressent_id(), representative);
    }

    public ArrayList<Representative> getCurrentRepresentatives() {
        return currentRepresentatives;
    }

    public Representative getRepresentative(String iid, @Nullable String party) {
        if (party != null) {
            return representatives.get(party.toLowerCase()).get(iid);
        } else {
            for (String partyId : representatives.keySet()) {
                if (representatives.get(partyId).containsKey(iid)) {
                    return representatives.get(partyId).get(iid);
                }
            }
        }
        // Not found
        return null;
    }

    public ArrayList<Representative> getRepresentativesForParty(String party) {
        return new ArrayList<>(representatives.get(party.toLowerCase()).values());
    }

    private void notifyDownloaded() {
        for (RepresentativeDownloadListener listener : listenerList) {
            listener.onRepresentativesDownloaded(getCurrentRepresentatives());
        }
    }

    public void notifyError() {
        for (RepresentativeDownloadListener listener : listenerList) {
            listener.onFail();
        }
    }

    public boolean isRepresentativesDownloaded() {
        return representativesDownloaded;
    }

    public void addDownloadListener(RepresentativeDownloadListener listener) {
        listenerList.add(listener);
    }

    public void removeDownloadListener(RepresentativeDownloadListener listener) {
        listenerList.remove(listener);
    }

    public interface RepresentativeDownloadListener {
        void onRepresentativesDownloaded(ArrayList<Representative> representatives);

        void onFail();
    }
}
