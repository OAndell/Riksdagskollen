package oscar.riksdagskollen;

import android.app.Application;

import oscar.riksdagskollen.Manager.RequestManager;
import oscar.riksdagskollen.Manager.RiksdagenAPIManager;
import oscar.riksdagskollen.Manager.ThemeManager;

/**
 * Created by gustavaaro on 2018-03-25.
 */

public class RiksdagskollenApp extends Application {

    private static RiksdagskollenApp instance;
    private RequestManager requestManager;
    private RiksdagenAPIManager riksdagenAPIManager;
    private ThemeManager themeManager;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        requestManager = new RequestManager();
        riksdagenAPIManager = new RiksdagenAPIManager(this);
        themeManager = new ThemeManager(this);
    }

    public static RiksdagskollenApp getInstance() {
        return instance;
    }

    public RequestManager getRequestManager() {
        return requestManager;
    }

    public RiksdagenAPIManager getRiksdagenAPIManager() {
        return riksdagenAPIManager;
    }

    public ThemeManager getThemeManager() {
        return themeManager;
    }
}
