package oscar.riksdagskollen;

import android.app.Activity;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
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
        Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build();
        // Initialize Fabric with the debug-disabled crashlytics.
        Fabric.with(context, crashlyticsKit);
    }

    @Override
    public void setCurrentScreen(Activity activity, String screenName) {
        if (context != null) {
            FirebaseAnalytics fireBase = FirebaseAnalytics.getInstance(context);
            fireBase.setCurrentScreen(activity, screenName, null);
        }

    }
}
