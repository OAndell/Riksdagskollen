package oscar.riksdagskollen.DebateList;

import oscar.riksdagskollen.Manager.RiksdagenAPIManager;
import oscar.riksdagskollen.Util.RiksdagenCallback.PartyDocumentCallback;

public class DebateListModel implements DebateContract.Model {

    private int pageToLoad = 1;
    private int documentCount = 0;


    @Override
    public void getDebateItems(PartyDocumentCallback documentCallback) {
        RiksdagenAPIManager.getInstance().getDebates(getPageToLoad(), documentCallback);
    }

    @Override
    public int getPageToLoad() {
        return pageToLoad;
    }

    @Override
    public void incrementPage() {
        pageToLoad++;
    }

    @Override
    public void resetPage() {
        pageToLoad = 1;
    }

    @Override
    public int getDocumentCount() {
        return documentCount;
    }

    @Override
    public void increaseDocumentCount(int c) {
        documentCount += c;
    }

    @Override
    public void resetDocumentCount() {
        documentCount = 0;
    }
}
