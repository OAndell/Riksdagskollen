package oscar.riksdagskollen.DebateView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import oscar.riksdagskollen.DebateView.Data.DebateStatement;
import oscar.riksdagskollen.DebateView.Data.Speech;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Helper.AnimUtil;
import oscar.riksdagskollen.Util.JSONModel.PartyDocument;
import oscar.riksdagskollen.Util.RiksdagenCallback.OnDocumentHtmlViewLoadedCallback;
import oscar.riksdagskollen.Util.View.DocumentHtmlView;
import oscar.riksdagskollen.Util.WebTV.DebateWebTvView;
import oscar.riksdagskollen.Util.WebTV.JSInterface;

import static oscar.riksdagskollen.Util.WebTV.DebateWebTvView.ACTION_PAUSE;
import static oscar.riksdagskollen.Util.WebTV.DebateWebTvView.ACTION_PLAY;
import static oscar.riksdagskollen.Util.WebTV.DebateWebTvView.ACTION_SEEK_BACKWARD;
import static oscar.riksdagskollen.Util.WebTV.DebateWebTvView.ACTION_SEEK_FORWARD;

public class DebateActivity extends AppCompatActivity implements DebateViewContract.View, DebateAdapter.WebTvListener {
    private RecyclerView recyclerView;
    private ViewGroup loadingView;
    private TextView debateLabel;
    private Button scrollHint;
    private LinearLayout webTVHeader;
    private DebateWebTvView debateWebTvView;
    private ImageView expansionArrow;
    private DocumentHtmlView documentHtmlView;
    private DebateViewContract.Presenter presenter = new DebateViewPresenter(this);
    private DebateAdapter adapter;
    private boolean isWebTVExpanded = false;
    private boolean firstExpand = true;
    private NotificationManagerCompat notificationManager;

    private BroadcastReceiver mediaPlaybackReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) return;

            switch (intent.getAction()) {
                case ACTION_PLAY:
                    playDebate();
                    break;

                case ACTION_PAUSE:
                    pauseDebate();
                    break;

                case ACTION_SEEK_FORWARD:
                    seekForward();
                    break;

                case ACTION_SEEK_BACKWARD:
                    seekBackward();
                    break;

                case Intent.ACTION_HEADSET_PLUG:
                    pauseDebate();
                    break;
            }
        }
    };



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(RiksdagskollenApp.getInstance().getThemeManager().getCurrentTheme(true));
        setContentView(R.layout.activity_debate);
        recyclerView = findViewById(R.id.activity_debate_recyclerview);
        loadingView = findViewById(R.id.loading_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        scrollHint = findViewById(R.id.scroll_hint);
        debateWebTvView = findViewById(R.id.tv_view);
        webTVHeader = findViewById(R.id.show_web_tv_header);
        expansionArrow = findViewById(R.id.web_tv_expand_icon);

        notificationManager = NotificationManagerCompat.from(this);

        IntentFilter filter = new IntentFilter(ACTION_PAUSE);
        filter.addAction(ACTION_PLAY);
        filter.addAction(ACTION_SEEK_BACKWARD);
        filter.addAction(ACTION_SEEK_FORWARD);
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(mediaPlaybackReceiver, filter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(RiksdagskollenApp.getColorFromAttribute(R.attr.mainBackgroundColor, this));
        }

        presenter.handleExtrasAndSetupView(getIntent().getExtras());
    }


    @Override
    protected void onDestroy() {
        notificationManager.cancel(JSInterface.NOTIF_ID);
        unregisterReceiver(mediaPlaybackReceiver);
        if (debateWebTvView != null) debateWebTvView.removeJavascriptInterface("JSOUT");
        super.onDestroy();
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
        final DebateStatement[] speeches = initiatingDocument.getDebatt().getAnforande();
        List<DebateStatement> debateStatements = Arrays.asList(speeches);
        adapter = new DebateAdapter(this, new ArrayList<>(debateStatements), initiatorId);
        recyclerView.setAdapter(adapter);
        adapter.setWebTvListener(this);
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

    @Override
    public void setUpWebTvView(PartyDocument debateDocument) {
        webTVHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isWebTVExpanded = !isWebTVExpanded;

                if (isWebTVExpanded) expandWebTv();
                else collapseWebTv();
            }
        });

        debateWebTvView = findViewById(R.id.tv_view);
        debateWebTvView.setDebate(debateDocument);
    }

    @Override
    public void loadDebate() {
        debateWebTvView.loadDebate();
    }

    @Override
    public ConnectivityManager getConnectivityManager() {
        return (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    private void expandWebTv() {
        adapter.setShowPlayLabel(true);
        adapter.notifyDataSetChanged();
        if (firstExpand) {
            loadDebate();
            firstExpand = false;
        }
        AnimUtil.expand(debateWebTvView, null);
        int rotationAngle = 180;  //toggle
        expansionArrow.animate().rotation(rotationAngle).setDuration(200).start();
    }

    private void collapseWebTv() {
        adapter.setShowPlayLabel(false);
        adapter.notifyDataSetChanged();
        AnimUtil.collapse(debateWebTvView, null);
        int rotationAngle = 0;  //toggle
        expansionArrow.animate().rotation(rotationAngle).setDuration(200).start();
    }

    private void playDebateFromSecond(int start) {
        if (debateWebTvView != null && isWebTVExpanded) debateWebTvView.setCurrentTime(start);
    }

    private void playDebate() {
        if (debateWebTvView != null) debateWebTvView.play();
    }

    private void pauseDebate() {
        if (debateWebTvView != null) debateWebTvView.pause();
    }

    private void seekForward() {
        if (debateWebTvView != null) debateWebTvView.seekForward();
    }

    private void seekBackward() {
        if (debateWebTvView != null) debateWebTvView.seekBackward();
    }

    @Override
    public void onPlayLabelPressed(int second) {
        playDebateFromSecond(second);
    }
}
