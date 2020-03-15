package oscar.riksdagskollen.DebateView;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import oscar.riksdagskollen.DebateView.Data.DebateStatement;
import oscar.riksdagskollen.DebateView.Data.Speech;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Helper.AnimUtil;
import oscar.riksdagskollen.Util.JSONModel.PartyDocument;
import oscar.riksdagskollen.Util.View.DocumentHtmlView;
import oscar.riksdagskollen.Util.WebTV.DebateAudioPlayerView;
import oscar.riksdagskollen.Util.WebTV.DebatePlayer;
import oscar.riksdagskollen.Util.WebTV.DebateWebTvView;

import static oscar.riksdagskollen.DebateView.DebateViewPresenter.DEBATE_INITIATOR_ID;
import static oscar.riksdagskollen.DebateView.DebateViewPresenter.INITIATING_DOCUMENT;
import static oscar.riksdagskollen.DebateView.DebateViewPresenter.SHOW_INITIATING_DOCUMENT;


public class DebateActivity extends AppCompatActivity implements DebateViewContract.View, DebateAdapter.WebTvListener {
    private RecyclerView recyclerView;
    private ViewGroup loadingView;
    private TextView debateLabel;
    private Button scrollHint;

    private LinearLayout audioPlayerHeader;
    private DebateAudioPlayerView audioPlayerControllView;

    private LinearLayout webTVHeader;
    private DebateWebTvView debateWebTvView;

    private ImageView webExpansionArrow;
    private ImageView audioExpansionArrow;

    private DocumentHtmlView documentHtmlView;
    private DebateViewContract.Presenter presenter = new DebateViewPresenter(this);
    private DebateAdapter adapter;

    private boolean isWebTVExpanded = false;
    private boolean firstWebTvExpand = true;

    private boolean isAudioPlayerExpanded = false;
    private boolean firstAudioPlayerExpand = true;
    private DebatePlayer currentPlayer = null;

    private AudioPlayerService audioPlayerService;
    private Intent intent;
    private boolean bound = false;


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            AudioPlayerService.LocalBinder binder = (AudioPlayerService.LocalBinder) iBinder;
            audioPlayerService = binder.getService();
            bound = true;
            initializePlayer();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            System.out.println("DISCONNECTED SERVICE ASÅDOJ A");
            bound = false;
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

        audioPlayerControllView = findViewById(R.id.player_controller_view);
        audioPlayerHeader = findViewById(R.id.show_audio_player_header);

        debateWebTvView = findViewById(R.id.tv_view);
        webTVHeader = findViewById(R.id.show_web_tv_header);

        webExpansionArrow = findViewById(R.id.web_tv_expand_icon);
        audioExpansionArrow = findViewById(R.id.audio_expand_icon);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(RiksdagskollenApp.getColorFromAttribute(R.attr.mainBackgroundColor, this));
        }

        presenter.handleExtrasAndSetupView(getIntent().getExtras());

    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        unbindService(mConnection);
        bound = false;
        super.onStop();
    }

    private void initializePlayer() {
        if (bound) {
            SimpleExoPlayer player = audioPlayerService.getPlayerInstance();
            audioPlayerControllView.setPlayer(player);
            System.out.println(player.getApplicationLooper() == Looper.getMainLooper());
        }
    }


    @Override
    protected void onDestroy() {
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

        documentHtmlView.setLoadedCallack(() -> {
            loadingView.setVisibility(View.GONE);
            scrollHint.setOnClickListener(view -> recyclerView.smoothScrollBy(0, header.getMeasuredHeight() - recyclerView.computeVerticalScrollOffset()));
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
    public void setUpPlayers(PartyDocument debateDocument) {
        webTVHeader.setOnClickListener(view -> {
            isWebTVExpanded = !isWebTVExpanded;

            if (isAudioPlayerExpanded && isWebTVExpanded) {
                isAudioPlayerExpanded = false;
                collapseAudioPlayer();
            }

            if (isWebTVExpanded) expandWebTv();
            else collapseWebTv();
        });

        audioPlayerHeader.setOnClickListener(v -> {
            isAudioPlayerExpanded = !isAudioPlayerExpanded;

            if (isWebTVExpanded && isAudioPlayerExpanded) {
                isWebTVExpanded = false;
                collapseWebTv();
            }

            if (isAudioPlayerExpanded) expandAudioPlayer();
            else collapseAudioPlayer();
        });

        debateWebTvView = findViewById(R.id.tv_view);
        debateWebTvView.setDebate(debateDocument);
    }

    @Override
    public void loadDebate() {
        debateWebTvView.loadDebate();
    }

    @Override
    public void hideAudioPlayer() {
        audioPlayerHeader.setVisibility(View.GONE);
    }

    @Override
    public void prepareAudioPlayer(String audioSourceUrl, PartyDocument debateDocument) {
        intent = new Intent(this, AudioPlayerService.class);
        intent.putExtra(AudioPlayerService.DEBATE_DOCUMENT, debateDocument);
        intent.putExtra(AudioPlayerService.AUDIO_SOURCE_URL, audioSourceUrl);


        //insert variables to recreate parent Intent
        Bundle bundle = getIntent().getExtras();

        intent.putExtra(INITIATING_DOCUMENT,
                (PartyDocument) bundle.getParcelable(INITIATING_DOCUMENT));
        intent.putExtra(SHOW_INITIATING_DOCUMENT,
                bundle.getBoolean(SHOW_INITIATING_DOCUMENT, false));
        intent.putExtra(DEBATE_INITIATOR_ID,
                bundle.getString(DEBATE_INITIATOR_ID));

        //audioPlayerControllView.setupMediaSource(audioSourceUrl);
    }

    @Override
    public ConnectivityManager getConnectivityManager() {
        return (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    private void expandAudioPlayer() {
        adapter.setShowPlayLabel(true);
        adapter.notifyDataSetChanged();
        if (firstAudioPlayerExpand) {
            Util.startForegroundService(this, intent);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            initializePlayer();
            firstAudioPlayerExpand = false;
        }
        AnimUtil.expand(audioPlayerControllView, null);
        int rotationAngle = 180;  //toggle
        audioExpansionArrow.animate().rotation(rotationAngle).setDuration(200).start();
        currentPlayer = audioPlayerControllView;
    }

    private void collapseAudioPlayer() {
        adapter.setShowPlayLabel(false);
        adapter.notifyDataSetChanged();
        AnimUtil.collapse(audioPlayerControllView, null);
        int rotationAngle = 0;  //toggle
        audioExpansionArrow.animate().rotation(rotationAngle).setDuration(200).start();
    }

    private void expandWebTv() {
        adapter.setShowPlayLabel(true);
        adapter.notifyDataSetChanged();
        if (firstWebTvExpand) {
            loadDebate();
            firstWebTvExpand = false;
        }
        AnimUtil.expand(debateWebTvView, null);
        int rotationAngle = 180;  //toggle
        webExpansionArrow.animate().rotation(rotationAngle).setDuration(200).start();
        currentPlayer = debateWebTvView;
    }

    private void collapseWebTv() {
        adapter.setShowPlayLabel(false);
        adapter.notifyDataSetChanged();
        AnimUtil.collapse(debateWebTvView, null);
        int rotationAngle = 0;  //toggle
        webExpansionArrow.animate().rotation(rotationAngle).setDuration(200).start();
    }

    private void playDebateFromSecond(int start) {
        if (currentPlayer != null && (isWebTVExpanded || isAudioPlayerExpanded))
            currentPlayer.seekTo(start);
    }

    private void playDebate() {
        if (currentPlayer != null) currentPlayer.play();
    }

    private void pauseDebate() {
        if (currentPlayer != null) currentPlayer.pause();
    }

    private void seekForward() {
        if (currentPlayer != null) currentPlayer.fastForward();
    }

    private void seekBackward() {
        if (currentPlayer != null) currentPlayer.fastRewind();
    }

    @Override
    public void onPlayLabelPressed(int second) {
        playDebateFromSecond(second);
    }
}
