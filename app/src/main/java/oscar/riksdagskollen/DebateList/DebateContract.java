package oscar.riksdagskollen.DebateList;

import android.content.Context;

import java.util.List;

import oscar.riksdagskollen.Util.JSONModel.PartyDocument;
import oscar.riksdagskollen.Util.RiksdagenCallback.PartyDocumentCallback;

public interface DebateContract {

    interface Model {
        void getDebateItems(PartyDocumentCallback documentCallback);

        int getPageToLoad();

        void incrementPage();

        void resetPage();

        int getDocumentCount();

        void increaseDocumentCount(int c);

        void resetDocumentCount();
    }

    interface View {
        void addItemsToView(List<PartyDocument> documents);

        void showLoadingView(boolean loading);

        void showLoadingItemsView(boolean loading);

        void showLoadFailView();
    }

    interface Presenter {
        void loadMoreItems();

        void handleItemClick(PartyDocument document, Context context);

        void clear();
    }

}
