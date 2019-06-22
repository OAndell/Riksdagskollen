package oscar.riksdagskollen.CurrentNews;

import android.content.Context;

import java.util.List;

import oscar.riksdagskollen.CurrentNews.CurrentNewsJSONModels.CurrentNews;

public interface CurrentNewsContract {

    interface View {
        void addItemsToView(List<CurrentNews> news);

        void showLoadingView(boolean loading);

        void showLoadingItemsView(boolean loading);

        void showPlayRatingView();

        void showLoadFailView();

        void fillNotificationItem();

        void unFillNotificationItem();
    }

    interface Presenter {
        void loadMoreItems();

        void handleNotificationItemClick(String latestId);

        void updateAlertsLatestDocument(String id);

        void handleItemClick(CurrentNews newsItem, Context context);

        boolean isNotificationsEnabled();
    }

    interface Model {
        void getNews(CurrentNewsCallback newsCallback);

        int getPageToLoad();

        void incrementPage();

        void resetPage();
    }

}
