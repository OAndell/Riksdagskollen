package oscar.riksdagskollen;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.util.TypedValue;

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

public class RiksdagskollenApp extends Application {

    private static RiksdagskollenApp instance;
    private RequestManager requestManager;
    private RiksdagenAPIManager riksdagenAPIManager;
    private ThemeManager themeManager;
    private AlertManager alertManager;
    private RepresentativeManager representativeManager;
    private SavedDocumentManager savedDocumentManager;
    private AnalyticsManager analyticsManager;
    private TwitterAPIManager twitterAPIManager;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        analyticsManager = new AnalyticsManager(this);

        // Set up Crashlytics, disabled for debug builds
        if (!BuildConfig.DEBUG) {
            analyticsManager.initCrashlytics();
        }

        requestManager = new RequestManager();
        riksdagenAPIManager = new RiksdagenAPIManager(this);
        themeManager = new ThemeManager(this);
        alertManager = new AlertManager(this);
        JobManager.create(this).addJobCreator(new AlertJobCreator());
        scheduleAndCheckAlertsJob();

        representativeManager = new RepresentativeManager(this);
        savedDocumentManager = new SavedDocumentManager(this);

        twitterAPIManager = new TwitterAPIManager(this);


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
