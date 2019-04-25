package oscar.riksdagskollen.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import oscar.riksdagskollen.Activity.DebateActivity;
import oscar.riksdagskollen.Manager.RiksdagenAPIManager;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.Util.Adapter.DebateListAdapter;
import oscar.riksdagskollen.Util.Adapter.RiksdagenViewHolderAdapter;
import oscar.riksdagskollen.Util.Enum.DocumentType;
import oscar.riksdagskollen.Util.JSONModel.PartyDocument;
import oscar.riksdagskollen.Util.RiksdagenCallback.PartyDocumentCallback;

import static oscar.riksdagskollen.Activity.DebateActivity.DEBATE_INITIATOR_ID;


/**
 * List of most recent Interpolations with debates.
 */

public class DebateListFragment extends RiksdagenAutoLoadingListFragment {
    private final List<PartyDocument> documentList = new ArrayList<>();
    private DebateListAdapter adapter;

    public static DebateListFragment newInstance() {
        DebateListFragment newInstance = new DebateListFragment();
        return newInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.debate_nav);
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new DebateListAdapter(documentList, new RiksdagenViewHolderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Object clickedDocument) {
                PartyDocument document = (PartyDocument) clickedDocument;
                Intent intent = new Intent(getContext(), DebateActivity.class);
                if (((PartyDocument) clickedDocument).getDoktyp().equals(DocumentType.Interpellation.getDocType()))
                    intent.putExtra(DebateActivity.SHOW_INITIATING_DOCUMENT, true);
                else
                    intent.putExtra(DebateActivity.SHOW_INITIATING_DOCUMENT, true);
                intent.putExtra(DebateActivity.INITIATING_DOCUMENT, (PartyDocument) document);
                if (document.getSenders().size() > 0)
                    intent.putExtra(DEBATE_INITIATOR_ID, document.getSenders().get(0));
                startActivity(intent);
            }
        }, this);
    }

    /**
     * Load the next page and add it to the adapter when downloaded and parsed.
     * Hides the loading view.
     */
    protected void loadNextPage() {
        setLoadingMoreItems(true);
        RiksdagenAPIManager.getInstance().getDebates(getPageToLoad(), new PartyDocumentCallback() {
            @Override
            public void onDocumentsFetched(List<PartyDocument> documents) {
                //Sort out interpolations with no debate.
                for (PartyDocument document : documents) {
                    if (document.getDebattdag() != null) {
                        documentList.add(document);
                    }
                }
                getAdapter().addAll(documentList);
                setShowLoadingView(false);

                //Load next page if first page
                if (adapter.getItemCount() < MIN_DOC) {
                    loadNextPage();
                } else {
                    setLoadingMoreItems(false);
                }
            }

            @Override
            public void onFail(VolleyError error) {

            }
        });
        incrementPage(); //Hopefully this is not a race condition?
    }

    @Override
    protected void clearItems() {
        documentList.clear();
        adapter.clear();
    }

    @Override
    RiksdagenViewHolderAdapter getAdapter() {
        return adapter;
    }
}
