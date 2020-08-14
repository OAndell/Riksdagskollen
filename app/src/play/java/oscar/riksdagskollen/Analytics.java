package oscar.riksdagskollen;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import oscar.riksdagskollen.Util.Helper.AnalyticsWrapper;

public class Analytics implements AnalyticsWrapper, SharedPreferences.OnSharedPreferenceChangeListener {

    private Context context;
    private boolean shouldLog = true;
    private final String ANALYTICS_KEY = "send_analytics";
    private SharedPreferences preferences;

    @Override
    public void initAnalytics(Context context) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.registerOnSharedPreferenceChangeListener(this);
        shouldLog = preferences.getBoolean(ANALYTICS_KEY, true);
    }

    @Override
    public void setCurrentScreen(Activity activity, String screenName) {
        if (context != null && shouldLog) {
            FirebaseAnalytics fireBase = FirebaseAnalytics.getInstance(context);
            fireBase.setCurrentScreen(activity, screenName, activity.getClass().getSimpleName());
        }
    }

    @Override
    public void log(String logMessage) {
        if (shouldLog) {
            FirebaseCrashlytics.getInstance().log(logMessage);
            FirebaseCrashlytics.getInstance().setCustomKey("last_log", logMessage);
        }
    }

    @Override
    public void logEvent(String event, @Nullable Bundle bundle) {
        if (shouldLog) {
            FirebaseAnalytics.getInstance(context).logEvent(event, bundle);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(ANALYTICS_KEY)) {
            shouldLog = preferences.getBoolean(ANALYTICS_KEY, true);
        }
    }
}
