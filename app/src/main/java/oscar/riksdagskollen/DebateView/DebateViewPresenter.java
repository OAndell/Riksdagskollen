package oscar.riksdagskollen.DebateView;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.android.volley.VolleyError;

import java.util.List;

import oscar.riksdagskollen.DebateView.Data.DebateStatement;
import oscar.riksdagskollen.DebateView.Data.Speech;
import oscar.riksdagskollen.Util.JSONModel.PartyDocument;
import oscar.riksdagskollen.Util.JSONModel.Protocol;
import oscar.riksdagskollen.Util.RiksdagenCallback.DebateAudioSourceCallback;
import oscar.riksdagskollen.Util.RiksdagenCallback.ProtocolCallback;
import oscar.riksdagskollen.Util.RiksdagenCallback.SpeechCallback;

public class DebateViewPresenter implements DebateViewContract.Presenter, ProtocolCallback, SpeechCallback, DebateAudioSourceCallback {

    public static final String SPEECHES = "speeches";
    public static final String DEBATE_INITIATOR_ID = "debate_initiator_id";
    public static final String DEBATE_NAME = "debate_name";
    public static final String SHOW_INITIATING_DOCUMENT = "show_initiating_document";
    public static final String INITIATING_DOCUMENT = "initiating_document";

    private DebateViewContract.View view;
    private DebateViewContract.Model model;

    DebateViewPresenter(DebateViewContract.View view) {
        this.view = view;
        model = new DebateViewModel();
    }

    @Override
    public void handleExtrasAndSetupView(Bundle bundle) {
        model.setInitiatingDocument((PartyDocument) bundle.getParcelable(INITIATING_DOCUMENT));
        model.setDebateInitiatorId(bundle.getString(DEBATE_INITIATOR_ID));
        model.setShouldShowInitiatingDocument(bundle.getBoolean(SHOW_INITIATING_DOCUMENT, false));

        String debateName = "Debatt";
        if (model.getInitiatingDocument().getDebattnamn() != null)
            debateName = model.getInitiatingDocument().getTitel();
        view.setUpToolbar(debateName);

        view.setUpAdapter(model.getInitiatingDocument(), model.getDebateInitiatorId());

        if (model.shouldShowInitiatingDocument()) {
            view.showInitiatingDocument(model.getInitiatingDocument());
            view.showScrollHint();
        } else {
            view.hideScrollHint();
        }

        view.setUpPlayers(model.getInitiatingDocument());

        ConnectivityManager connManager = view.getConnectivityManager();
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        // To avoid unnecessary data usage, only pre-load if wifi is connected
        if (mWifi.isConnected()) {
            view.loadDebate();
        }

        model.getProtocolForDate(this);
        model.getDebateAudioSourceUrl(this);
    }


    @Override
    public void onProtocolsFetched(List<Protocol> protocols) {
        if (protocols.size() == 1) {
            model.setProtocolId(protocols.get(0).getId());
            for (DebateStatement debateStatement : model.getInitiatingDocument().getDebatt().getAnforande()) {
                model.getSpeech(debateStatement.getAnf_nummer(), this);
            }
        } else {
            view.showFailToastAndFinish();
        }
    }

    @Override
    public void onSpeechFetched(Speech speech, String anf) {
        view.setSpeechForAnforande(speech, anf);
    }


    private String createWebTVUrl(DebateStatement debateStatement) {

        String start = debateStatement.getVideo_url().split("pos=")[1];
        String end = "";
        try {
            int startSec = Integer.parseInt(start);
            int endSec = startSec + Integer.parseInt(debateStatement.getAnf_sekunder());
            end = Integer.toString(endSec);
        } catch (NumberFormatException e) {
            return "";
        }

        return String.format("http://www.riksdagen.se/views/pages/embedpage.aspx" +
                        "?did=%s&start=%s&end=%s",
                model.getInitiatingDocument().getId(),
                start,
                end);
    }

    @Override
    public void onFail(VolleyError error) {
        view.hideDebateTranscript();
    }

    @Override
    public void onDebateAudioSource(String sourceUrl) {
        view.prepareAudioPlayer(sourceUrl, model.getInitiatingDocument());
    }

    @Override
    public void onAudioSourceFail(VolleyError error) {
        System.out.println(error);
        // Failed to get audio source url
        view.hideAudioPlayer();
    }
}
