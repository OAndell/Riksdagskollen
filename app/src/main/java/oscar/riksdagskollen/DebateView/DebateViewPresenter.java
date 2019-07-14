package oscar.riksdagskollen.DebateView;

import android.os.Bundle;

import com.android.volley.VolleyError;

import java.util.List;

import oscar.riksdagskollen.DebateView.Data.DebateStatement;
import oscar.riksdagskollen.DebateView.Data.Speech;
import oscar.riksdagskollen.Util.JSONModel.PartyDocument;
import oscar.riksdagskollen.Util.JSONModel.Protocol;
import oscar.riksdagskollen.Util.RiksdagenCallback.ProtocolCallback;
import oscar.riksdagskollen.Util.RiksdagenCallback.SpeechCallback;

public class DebateViewPresenter implements DebateViewContract.Presenter, ProtocolCallback, SpeechCallback {

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


        model.getProtocolForDate(this);
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

    @Override
    public void onFail(VolleyError error) {
        view.showFailToastAndFinish();
    }
}
