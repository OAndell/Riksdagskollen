package oscar.riksdagskollen;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.TypedValue;

import androidx.annotation.ColorInt;
import androidx.multidex.MultiDexApplication;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;

import java.util.Set;

import oscar.riksdagskollen.Manager.AlertManager;
import oscar.riksdagskollen.Manager.AnalyticsManager;
import oscar.riksdagskollen.Manager.RepresentativeManager;
import oscar.riksdagskollen.Manager.RequestManager;
import oscar.riksdagskollen.Manager.RiksdagenAPIManager;
import oscar.riksdagskollen.Manager.SavedDocumentManager;
import oscar.riksdagskollen.Manager.ThemeManager;
import oscar.riksdagskollen.Manager.TwitterAPIManager;
import oscar.riksdagskollen.Util.Job.AlertJobCreator;
import oscar.riksdagskollen.Util.Job.CheckAlertsJob;
import oscar.riksdagskollen.Util.Job.DownloadAllRepresentativesJob;


/**
 * Created by gustavaaro on 2018-03-25.
 */

public class RiksdagskollenApp extends MultiDexApplication {

    private static RiksdagskollenApp instance;
    private RequestManager requestManager;
    private RiksdagenAPIManager riksdagenAPIManager;
    private ThemeManager themeManager;
    private AlertManager alertManager;
    private RepresentativeManager representativeManager;
    private SavedDocumentManager savedDocumentManager;
    private AnalyticsManager analyticsManager;
    private TwitterAPIManager twitterAPIManager;

    private final static String LAUNCH_COUNT = "launches";
    private final static String CAN_ASK_FOR_RATING = "can_ask_for_rating";
    private final static int LAUNCHES_TO_ASK = 15;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        analyticsManager = new AnalyticsManager(this);
        analyticsManager.initCrashlytics();

        requestManager = new RequestManager();
        riksdagenAPIManager = new RiksdagenAPIManager(this);
        themeManager = new ThemeManager(this);
        alertManager = new AlertManager(this);
        JobManager.create(this).addJobCreator(new AlertJobCreator());
        scheduleAndCheckAlertsJob();

        representativeManager = new RepresentativeManager(this);
        savedDocumentManager = new SavedDocumentManager(this);
        twitterAPIManager = new TwitterAPIManager(this);

        incrementLaunches();


    }

    public void scheduleAndCheckAlertsJob() {
        CheckAlertsJob.scheduleJob();
    }

    public void scheduleCheckAlertsJobIfNotRunningOrScheduled() {
        if (!jobIsRunningOrScheduledWithTag(CheckAlertsJob.TAG)) {
            CheckAlertsJob.scheduleJob();
        }
    }

    public void scheduleDownloadRepresentativesJobIfNotRunning() {
        if (!isDownloadRepsRunningOrScheduled()) {
            DownloadAllRepresentativesJob.scheduleJob();
        }
    }

    public static int getColorFromAttribute(int attr, Context activity) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = activity.getTheme();
        theme.resolveAttribute(attr, typedValue, true);
        @ColorInt int color = typedValue.data;
        return color;
    }

    public boolean isCheckRepliesScheduledorRunning() {
        System.out.println("Checking if job is scheduled or running");
        return !jobIsRunningOrScheduledWithTag(CheckAlertsJob.TAG);
    }

    public boolean isDownloadRepsRunningOrScheduled() {
        return jobIsRunningOrScheduledWithTag(DownloadAllRepresentativesJob.TAG);
    }

    public boolean isDataSaveModeActive() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getBoolean("data_save_mode", false);
    }

    private void incrementLaunches() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int launchCount = preferences.getInt(LAUNCH_COUNT, 0);
        launchCount++;
        preferences.edit().putInt(LAUNCH_COUNT, launchCount).apply();
    }

    public boolean shouldAskForRating() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int launchCount = preferences.getInt(LAUNCH_COUNT, 0);
        boolean canAskForRating = preferences.getBoolean(CAN_ASK_FOR_RATING, true);
        return launchCount >= LAUNCHES_TO_ASK && canAskForRating;
    }

    public void disableRatingQuestion() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putBoolean(CAN_ASK_FOR_RATING, false).apply();
    }

    public void remindLater() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putInt(LAUNCH_COUNT, LAUNCHES_TO_ASK / 2).apply();
    }

    public AnalyticsManager getAnalyticsManager() {
        return analyticsManager;
    }

    public AlertManager getAlertManager() {
        return alertManager;
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

    public SavedDocumentManager getSavedDocumentManager() {
        return savedDocumentManager;
    }

    private boolean jobIsRunningOrScheduledWithTag(String tag) {
        if (!JobManager.instance().getAllJobRequestsForTag(tag).isEmpty()) return true;
        Set<Job> jobs = JobManager.instance().getAllJobsForTag(tag);
        for (Job job : jobs) {
            if (!job.isFinished()) return true;
        }
        return false;
    }

    public RepresentativeManager getRepresentativeManager() {
        return representativeManager;
    }

    public TwitterAPIManager getTwitterAPIManager() {
        return twitterAPIManager;
    }
}
