package oscar.riksdagskollen.DebateView;

import com.android.volley.VolleyError;

import oscar.riksdagskollen.Manager.RiksdagenAPIManager;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.JSONModel.PartyDocument;
import oscar.riksdagskollen.Util.RiksdagenCallback.ProtocolCallback;
import oscar.riksdagskollen.Util.RiksdagenCallback.SpeechCallback;

public class DebateViewModel implements DebateViewContract.Model {

    private PartyDocument initiatingDocument;
    private boolean shouldShowInitiatingDocument;
    private String debateInitiatorId;
    private String protocolId;

    @Override
    public void getProtocolForDate(ProtocolCallback callback) {
        if (initiatingDocument == null) {
            callback.onFail(new VolleyError("Initating document null"));
            return;
        }
        RiksdagenAPIManager.getInstance().getProtocolForDate(initiatingDocument.getDebattdag(), initiatingDocument.getRm(), callback);
    }

    @Override
    public void getSpeech(String anf, SpeechCallback callback) {
        RiksdagskollenApp.getInstance().getRiksdagenAPIManager().getSpeech(protocolId, anf, callback);
    }


    @Override
    public boolean shouldShowInitiatingDocument() {
        return shouldShowInitiatingDocument;
    }

    @Override
    public void setShouldShowInitiatingDocument(boolean showInitiatingDocument) {
        this.shouldShowInitiatingDocument = showInitiatingDocument;
    }

    @Override
    public String getDebateInitiatorId() {
        return debateInitiatorId;
    }

    @Override
    public void setProtocolId(String protocolId) {
        this.protocolId = protocolId;
    }

    @Override
    public String getProtocolId() {
        return protocolId;
    }

    @Override
    public void setDebateInitiatorId(String initiatorId) {
        this.debateInitiatorId = initiatorId;
    }

    @Override
    public PartyDocument getInitiatingDocument() {
        return initiatingDocument;
    }

    @Override
    public void setInitiatingDocument(PartyDocument initiatingDocument) {
        this.initiatingDocument = initiatingDocument;
    }
}
