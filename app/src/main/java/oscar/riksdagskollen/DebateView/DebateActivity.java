package oscar.riksdagskollen.DebateView;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import oscar.riksdagskollen.DebateView.Data.DebateSpeech;
import oscar.riksdagskollen.DebateView.Data.Speech;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.JSONModel.PartyDocument;
import oscar.riksdagskollen.Util.RiksdagenCallback.OnDocumentHtmlViewLoadedCallback;
import oscar.riksdagskollen.Util.View.DocumentHtmlView;

public class DebateActivity extends AppCompatActivity implements DebateViewContract.View {
    private RecyclerView recyclerView;
    private ViewGroup loadingView;
    private TextView debateLabel;
    private Button scrollHint;
    private DocumentHtmlView documentHtmlView;
    private DebateViewContract.Presenter presenter = new DebateViewPresenter(this);
    private DebateAdapter adapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(RiksdagskollenApp.getInstance().getThemeManager().getCurrentTheme(true));
        setContentView(R.layout.activity_debate);
        recyclerView = findViewById(R.id.activity_debate_recyclerview);
        loadingView = findViewById(R.id.loading_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        scrollHint = findViewById(R.id.scroll_hint);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(RiksdagskollenApp.getColorFromAttribute(R.attr.mainBackgroundColor, this));
        }

        presenter.handleExtrasAndSetupView(getIntent().getExtras());
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
    public void showFailToastAndFinish() {
        Toast.makeText(this, "Kunde inte hämta debatt", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void showScrollHint() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int scrollY = recyclerView.computeVerticalScrollOffset();
                float alpha = (300 - scrollY) / 100f;
                scrollHint.setAlpha(alpha);
                if (alpha < 0) scrollHint.setVisibility(View.INVISIBLE);
                else scrollHint.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void setUpToolbar(String title) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void setUpAdapter(PartyDocument initiatingDocument, String initiatorId) {
        final DebateSpeech[] speeches = initiatingDocument.getDebatt().getAnforande();
        List<DebateSpeech> debateSpeeches = Arrays.asList(speeches);
        adapter = new DebateAdapter(this, new ArrayList<>(debateSpeeches), initiatorId);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void showInitiatingDocument(PartyDocument initiatingDocument) {
        final View header = getLayoutInflater().inflate(R.layout.debate_document_header, null);
        documentHtmlView = header.findViewById(R.id.document_view);
        debateLabel = header.findViewById(R.id.debate_label);
        documentHtmlView.setDocument(initiatingDocument);
        adapter.addHeader(header);

        documentHtmlView.setLoadedCallack(new OnDocumentHtmlViewLoadedCallback() {
            @Override
            public void onDocumentLoaded() {
                loadingView.setVisibility(View.GONE);
                scrollHint.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        recyclerView.smoothScrollBy(0, header.getMeasuredHeight() - recyclerView.computeVerticalScrollOffset());
                    }
                });
            }
        });
    }

    @Override
    public void hideScrollHint() {
        scrollHint.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
        if (debateLabel != null) debateLabel.setVisibility(View.GONE);
    }

    @Override
    public void setSpeechForAnforande(Speech speech, String anf) {
        adapter.setSpeechDetail(speech, anf);
    }
}
