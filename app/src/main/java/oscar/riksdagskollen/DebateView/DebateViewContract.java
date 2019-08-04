package oscar.riksdagskollen.DebateView;

import android.net.ConnectivityManager;
import android.os.Bundle;

import oscar.riksdagskollen.DebateView.Data.Speech;
import oscar.riksdagskollen.Util.JSONModel.PartyDocument;
import oscar.riksdagskollen.Util.RiksdagenCallback.ProtocolCallback;
import oscar.riksdagskollen.Util.RiksdagenCallback.SpeechCallback;

public interface DebateViewContract {
    interface Model {
        void getProtocolForDate(ProtocolCallback callback);

        void getSpeech(String anf, SpeechCallback callback);

        void setProtocolId(String protocolId);

        String getProtocolId();

        boolean shouldShowInitiatingDocument();

        void setShouldShowInitiatingDocument(boolean showInitiatingDocument);

        String getDebateInitiatorId();

        void setDebateInitiatorId(String initiatorId);

        PartyDocument getInitiatingDocument();

        void setInitiatingDocument(PartyDocument initiatingDocument);
    }

    interface View {
        void showFailToastAndFinish();

        void showScrollHint();

        void setUpToolbar(String title);

        void setUpAdapter(PartyDocument initiatingDocument, String initiatorId);

        void showInitiatingDocument(PartyDocument initiatingDocument);

        void hideScrollHint();

        void setSpeechForAnforande(Speech speech, String anfNummer);

        void setUpWebTvView(PartyDocument initiatingDocument);

        void loadDebate();

        ConnectivityManager getConnectivityManager();
    }

    interface Presenter {
        void handleExtrasAndSetupView(Bundle bundle);
    }
}
