package oscar.riksdagskollen;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.ColorInt;
import android.util.TypedValue;

import com.evernote.android.job.JobManager;
import com.google.firebase.FirebaseApp;

import oscar.riksdagskollen.Manager.AlertManager;
import oscar.riksdagskollen.Manager.RepresentativeManager;
import oscar.riksdagskollen.Manager.RequestManager;
import oscar.riksdagskollen.Manager.RiksdagenAPIManager;
import oscar.riksdagskollen.Manager.ThemeManager;
import oscar.riksdagskollen.Util.Job.AlertJobCreator;
import oscar.riksdagskollen.Util.Job.CheckAlertsJob;
import oscar.riksdagskollen.Util.Job.DownloadRepresentativesJob;

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


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        FirebaseApp.initializeApp(this);

        requestManager = new RequestManager();
        riksdagenAPIManager = new RiksdagenAPIManager(this);
        themeManager = new ThemeManager(this);
        alertManager = new AlertManager(this);
        JobManager.create(this).addJobCreator(new AlertJobCreator());
        scheduleCheckAlertsJobIfNotRunning();

        representativeManager = new RepresentativeManager(this);

    }

    public void scheduleCheckAlertsJobIfNotRunning() {
        if (!isCheckRepliesScheduled()) {
            System.out.println("Scheduling job");
            //Create jobs which will search for replies to tracked questions
            CheckAlertsJob.scheduleJob();
        }
    }

    public void scheduleDownloadRepresentativesJobIfNotRunning() {
        if (!isDownloadRepsRunningOrScheduled()) {
            DownloadRepresentativesJob.scheduleJob();
        }
    }

    public static int getColorFromAttribute(int attr, Context activity) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = activity.getTheme();
        theme.resolveAttribute(attr, typedValue, true);
        @ColorInt int color = typedValue.data;
        return color;
    }

    public boolean isCheckRepliesScheduled() {
        System.out.println("Checking if job is scheduled");
        return !JobManager.instance().getAllJobRequestsForTag(CheckAlertsJob.TAG).isEmpty();
    }

    public boolean isDownloadRepsRunningOrScheduled() {
        return !(JobManager.instance().
                getAllJobRequestsForTag(DownloadRepresentativesJob.TAG).isEmpty() &&
                JobManager.instance()
                        .getAllJobsForTag(DownloadRepresentativesJob.TAG).isEmpty());
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

    public RepresentativeManager getRepresentativeManager() {
        return representativeManager;
    }
}
