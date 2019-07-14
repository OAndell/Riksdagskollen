package oscar.riksdagskollen.Activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import oscar.riksdagskollen.Manager.RiksdagenAPIManager;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Adapter.DebateAdapter;
import oscar.riksdagskollen.Util.JSONModel.DebateStatement;
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
    private TextView debateLabel;
    private NestedScrollView scrollView;
    private Button scrollHint;
    private DocumentHtmlView documentHtmlView;



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
        debateLabel = findViewById(R.id.debate_label);
        scrollView = findViewById(R.id.debate_scrollview);
        scrollHint = findViewById(R.id.scroll_hint);
        documentHtmlView = findViewById(R.id.document_view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(RiksdagskollenApp.getColorFromAttribute(R.attr.mainBackgroundColor, this));
        }


        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (showInitiatingDocument) {
                    float alpha = (300 - scrollY) / 100f;
                    scrollHint.setAlpha(alpha);
                    if (alpha < 0) scrollHint.setVisibility(View.INVISIBLE);
                    else scrollHint.setVisibility(View.VISIBLE);
                }
            }
        });
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);


        String debateName = "Debatt";
        if (initiatingDocument.getDebattnamn() != null)
            debateName = initiatingDocument.getTitel();
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
                Toast.makeText(context, "Kunde inte hämta debatt", Toast.LENGTH_LONG).show();
                finish();
                Log.e("DebateActivity", "onFail: Could not get protocol");
            }
        });

        if (showInitiatingDocument) {
            documentHtmlView.setDocument(initiatingDocument);
            documentHtmlView.setLoadedCallack(new OnDocumentHtmlViewLoadedCallback() {
                @Override
                public void onDocumentLoaded() {
                    loadingView.setVisibility(View.GONE);
                    scrollHint.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            scrollView.smoothScrollTo(0, documentHtmlView.getMeasuredHeight());
                        }
                    });
                }
            });
        } else {
            scrollHint.setVisibility(View.GONE);
            debateLabel.setVisibility(View.GONE);
            loadingView.setVisibility(View.GONE);
        }
    }

    private void refreshAdapter() {
        final DebateStatement[] speeches = initiatingDocument.getDebatt().getAnforande();
        List<DebateStatement> debateStatements = Arrays.asList(speeches);
        DebateAdapter adapter = new DebateAdapter(this, new ArrayList<>(debateStatements), debateProtocolId, debateInitiatorId);
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
