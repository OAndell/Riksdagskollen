package oscar.riksdagskollen.News;

import java.util.Observable;

import oscar.riksdagskollen.RiksdagskollenApp;

public class CurrentNewsModel extends Observable implements CurrentNewsContract.Model {

    private int pageToLoad = 1;

    @Override
    public int getPageToLoad() {
        return pageToLoad;
    }

    public void incrementPage() {
        pageToLoad++;
    }

    public void decrementPage() {
        pageToLoad--;
    }


    public void resetPage() {
        pageToLoad = 1;
    }

    @Override
    public void getNews(CurrentNewsCallback newsCallback) {
        RiksdagskollenApp.getInstance().getRiksdagenAPIManager().getCurrentNews(newsCallback, getPageToLoad());
    }


}
