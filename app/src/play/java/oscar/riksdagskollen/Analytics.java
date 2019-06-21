package oscar.riksdagskollen;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;

import io.fabric.sdk.android.Fabric;
import oscar.riksdagskollen.Util.Helper.AnalyticsWrapper;

public class Analytics implements AnalyticsWrapper {

    private Context context;

    @Override
    public void initAnalytics(Context context) {
        this.context = context;
        FirebaseApp.initializeApp(context);
        Crashlytics crashlytics = new Crashlytics();
        Fabric.with(context, crashlytics);
    }

    @Override
    public void setCurrentScreen(Activity activity, String screenName) {
        if (context != null) {
            FirebaseAnalytics fireBase = FirebaseAnalytics.getInstance(context);
            fireBase.setCurrentScreen(activity, screenName, null);
        }

    }

    @Override
    public void logEvent(String event, @Nullable Bundle bundle) {
        FirebaseAnalytics.getInstance(context).logEvent(event, bundle);
    }
}
