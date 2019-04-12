package oscar.riksdagskollen.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import oscar.riksdagskollen.Manager.RiksdagenAPIManager;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Adapter.DebateAdapter;
import oscar.riksdagskollen.Util.JSONModel.DebateSpeech;
import oscar.riksdagskollen.Util.JSONModel.PartyDocument;
import oscar.riksdagskollen.Util.JSONModel.Protocol;
import oscar.riksdagskollen.Util.RiksdagenCallback.OnDocumentHtmlViewLoadedCallback;
import oscar.riksdagskollen.Util.RiksdagenCallback.ProtocolCallback;
import oscar.riksdagskollen.Util.View.DocumentHtmlView;

public class DebateActivity extends AppCompatActivity {

    public static final String SPEECHES = "speeches";
    public static final String DEBATE_INITIATOR_ID = "debate_initiator_id";
    public static final String DEBATE_NAME = "debate_name";
    public static final String SHOW_INITIATING_DOCUMENT = "show_initiating_document";
    public static final String INITIATING_DOCUMENT = "initiating_document";


    private RecyclerView recyclerView;
    private String debateProtocolId;
    private String debateInitiatorId;
    private boolean showInitiatingDocument = false;
    private PartyDocument initiatingDocument;
    private ViewGroup loadingView;




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(RiksdagskollenApp.getInstance().getThemeManager().getCurrentTheme(true));
        setContentView(R.layout.activity_debate);

        initiatingDocument = getIntent().getParcelableExtra(INITIATING_DOCUMENT);
        debateInitiatorId = getIntent().getExtras().getString(DEBATE_INITIATOR_ID);
        showInitiatingDocument = getIntent().getBooleanExtra(SHOW_INITIATING_DOCUMENT, false);

        recyclerView = findViewById(R.id.activity_debate_recyclerview);
        loadingView = findViewById(R.id.loading_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);

        ((ProgressBar) loadingView.findViewById(R.id.progress_bar)).getIndeterminateDrawable().setColorFilter(
                RiksdagskollenApp.getColorFromAttribute(R.attr.secondaryLightColor, this),
                android.graphics.PorterDuff.Mode.MULTIPLY);

        String debateName = "Debatt";
        if (initiatingDocument.getDebattnamn() != null)
            debateName = initiatingDocument.getDebattnamn() + " " + initiatingDocument.getDebattdag();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(debateName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Context context = this;


        RiksdagenAPIManager.getInstance().getProtocolForDate(initiatingDocument.getDebattdag(), initiatingDocument.getRm(), new ProtocolCallback() {
            @Override
            public void onProtocolsFetched(final List<Protocol> protocols) {
                if (protocols.size() == 1) {
                    debateProtocolId = protocols.get(0).getId();
                    refreshAdapter();
                } else {
                    Toast.makeText(context, "Kunde inte hämta debatt", Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onFail(VolleyError error) {
                Log.e("DebateActivity", "onFail: Could not get protocol");
            }
        });

        if (showInitiatingDocument) {
            DocumentHtmlView documentHtmlView = findViewById(R.id.document_view);
            documentHtmlView.setDocument(initiatingDocument);
            documentHtmlView.setLoadedCallack(new OnDocumentHtmlViewLoadedCallback() {
                @Override
                public void onDocumentLoaded() {
                    loadingView.setVisibility(View.GONE);
                }
            });
        } else {
            loadingView.setVisibility(View.GONE);
        }
    }

    private void refreshAdapter() {
        final DebateSpeech[] speeches = initiatingDocument.getDebatt().getAnforande();
        List<DebateSpeech> debateSpeeches = Arrays.asList(speeches);
        DebateAdapter adapter = new DebateAdapter(this, new ArrayList<>(debateSpeeches), debateProtocolId, debateInitiatorId);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
