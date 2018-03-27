package oscar.riksdagskollen;

import android.app.Application;

import com.android.volley.VolleyError;

import java.io.Console;

import oscar.riksdagskollen.Managers.RequestManager;
import oscar.riksdagskollen.Managers.RiksdagenAPIManager;
import oscar.riksdagskollen.Utilities.Callbacks.RepresentativeCallback;
import oscar.riksdagskollen.Utilities.JSONModels.Representative;

/**
 * Created by gustavaaro on 2018-03-25.
 */

public class RikdagskollenApp extends Application {

    private static RikdagskollenApp instance;
    private RequestManager requestManager;
    private RiksdagenAPIManager riksdagenAPIManager;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        requestManager = new RequestManager();
        riksdagenAPIManager = new RiksdagenAPIManager(this);
    }

    public static RikdagskollenApp getInstance() {
        return instance;
    }

    public RequestManager getRequestManager() {
        return requestManager;
    }

    public RiksdagenAPIManager getRiksdagenAPIManager() {
        return riksdagenAPIManager;
    }
}
