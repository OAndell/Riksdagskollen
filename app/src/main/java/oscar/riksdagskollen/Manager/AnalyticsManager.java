package oscar.riksdagskollen.Manager;

import android.app.Activity;
import android.content.Context;

import oscar.riksdagskollen.Analytics;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Helper.AnalyticsWrapper;

public class AnalyticsManager {

    AnalyticsWrapper analyticsWrapper;
    Context app;

    public AnalyticsManager(Context app) {
        this.app = app;
        analyticsWrapper = new Analytics();
    }

    public void initCrashlytics() {
        analyticsWrapper.initAnalytics(app);
    }

    public void setCurrentScreen(Activity activity, String currentScreen) {
        analyticsWrapper.setCurrentScreen(activity, currentScreen);
    }


    public static AnalyticsManager getInstance() {
        return RiksdagskollenApp.getInstance().getAnalyticsManager();
    }


}
