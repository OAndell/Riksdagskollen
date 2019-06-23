package oscar.riksdagskollen.DebateList;

import android.content.Context;
import android.content.Intent;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import oscar.riksdagskollen.DebateView.DebateActivity;
import oscar.riksdagskollen.Util.Enum.DocumentType;
import oscar.riksdagskollen.Util.JSONModel.PartyDocument;
import oscar.riksdagskollen.Util.RiksdagenCallback.PartyDocumentCallback;

import static oscar.riksdagskollen.DebateView.DebateViewPresenter.DEBATE_INITIATOR_ID;
import static oscar.riksdagskollen.DebateView.DebateViewPresenter.INITIATING_DOCUMENT;
import static oscar.riksdagskollen.DebateView.DebateViewPresenter.SHOW_INITIATING_DOCUMENT;
import static oscar.riksdagskollen.Fragment.RiksdagenAutoLoadingListFragment.MIN_DOC;

public class DebateListPresenter implements DebateContract.Presenter, PartyDocumentCallback {

    private DebateContract.View view;
    private DebateContract.Model model;

    public DebateListPresenter(DebateContract.View view) {
        this.view = view;
        this.model = new DebateListModel();
    }

    @Override
    public void loadMoreItems() {
        view.showLoadingItemsView(true);
        model.getDebateItems(this);
    }

    @Override
    public void handleItemClick(PartyDocument document, Context context) {
        Intent intent = new Intent(context, DebateActivity.class);
        if (document.getDoktyp().equals(DocumentType.Interpellation.getDocType()))
            intent.putExtra(SHOW_INITIATING_DOCUMENT, true);
        else
            intent.putExtra(SHOW_INITIATING_DOCUMENT, false);
        intent.putExtra(INITIATING_DOCUMENT, (PartyDocument) document);
        if (document.getSenders().size() > 0)
            intent.putExtra(DEBATE_INITIATOR_ID, document.getSenders().get(0));
        context.startActivity(intent);
    }

    @Override
    public void clear() {
        model.resetPage();
        model.resetDocumentCount();
    }

    @Override
    public void onDocumentsFetched(List<PartyDocument> documents) {
        // Filter interpolations with no debate.
        List<PartyDocument> filteredDocument = new ArrayList<>();
        for (PartyDocument document : documents) {
            if (document.getDebattdag() != null) {
                filteredDocument.add(document);
            }
        }

        view.addItemsToView(filteredDocument);
        view.showLoadingView(false);
        view.showLoadingItemsView(false);
        model.incrementPage();
        model.increaseDocumentCount(filteredDocument.size());
        //Load next page if first page
        if (model.getDocumentCount() < MIN_DOC) {
            loadMoreItems();
        }
    }

    @Override
    public void onFail(VolleyError error) {
        view.showLoadingView(false);
        view.showLoadingItemsView(false);
        view.showLoadFailView();
    }
}
