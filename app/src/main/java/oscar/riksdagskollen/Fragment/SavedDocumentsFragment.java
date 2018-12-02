package oscar.riksdagskollen.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import oscar.riksdagskollen.Activity.MotionActivity;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Adapter.PartyListViewholderAdapter;
import oscar.riksdagskollen.Util.Adapter.RiksdagenViewHolderAdapter;
import oscar.riksdagskollen.Util.JSONModel.PartyDocument;
import oscar.riksdagskollen.Util.RiksdagenCallback.PartyDocumentCallback;

public class SavedDocumentsFragment extends Fragment {
    private RecyclerView recyclerView;
    private HashMap<String, Long> savedDocs;
    private List<PartyDocument> documents = new ArrayList<>();
    private RiksdagskollenApp app;
    private PartyListViewholderAdapter adapter;
    private View noSavedView;

    private Comparator<PartyDocument> savedComparator = new Comparator<PartyDocument>() {
        @Override
        public int compare(PartyDocument a, PartyDocument b) {
            return Long.valueOf(b.getSaved()).compareTo(Long.valueOf(a.getSaved()));
        }
    };

    public static SavedDocumentsFragment newInstance() {
        Bundle args = new Bundle();
        SavedDocumentsFragment fragment = new SavedDocumentsFragment();
        fragment.setArguments(args);
        fragment.setRetainInstance(true);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = RiksdagskollenApp.getInstance();
        savedDocs = app.getSavedDocumentManager().getSavedDocs();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.saved_docs);
        View view = inflater.inflate(R.layout.fragment_saved_documents, null);
        System.out.println("Create view");
        recyclerView = view.findViewById(R.id.saved_docs_recyclerview);
        noSavedView = view.findViewById(R.id.no_saved_view);

        PartyListViewholderAdapter adapter = new PartyListViewholderAdapter(documents, new RiksdagenViewHolderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Object document) {
                Intent intent = new Intent(getContext(), MotionActivity.class);
                intent.putExtra("document", ((PartyDocument) document));
                startActivity(intent);
            }
        }, this);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        this.adapter = adapter;
        adapter.setComparator(savedComparator);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        savedDocs = app.getSavedDocumentManager().getSavedDocs();
        if (savedDocs.size() != documents.size() || savedDocs.isEmpty()) {
            adapter.clear();
            loadSavedDocs();
        }
    }

    private void loadSavedDocs() {

        if (savedDocs.isEmpty()) {
            noSavedView.setVisibility(View.VISIBLE);
        } else {
            noSavedView.setVisibility(View.GONE);
            for (final String docId : savedDocs.keySet()) {
                app.getRiksdagenAPIManager().getDocument(docId, new PartyDocumentCallback() {
                    @Override
                    public void onDocumentsFetched(List<PartyDocument> fetchedDoc) {
                        PartyDocument fetched = fetchedDoc.get(0);
                        fetched.setSaved(savedDocs.get(docId));
                        documents.add(fetched);
                        adapter.add(fetched);
                        recyclerView.scrollToPosition(0);
                    }

                    @Override
                    public void onFail(VolleyError error) {

                    }
                });
            }
        }

    }


}
