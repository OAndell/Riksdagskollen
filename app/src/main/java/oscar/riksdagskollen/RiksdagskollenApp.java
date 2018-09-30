package oscar.riksdagskollen;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;

import com.evernote.android.job.JobConfig;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.util.JobLogger;

import oscar.riksdagskollen.Manager.AlertManager;
import oscar.riksdagskollen.Manager.RepresentativeManager;
import oscar.riksdagskollen.Manager.RequestManager;
import oscar.riksdagskollen.Manager.RiksdagenAPIManager;
import oscar.riksdagskollen.Manager.ThemeManager;
import oscar.riksdagskollen.Util.Job.AlertJobCreator;
import oscar.riksdagskollen.Util.Job.CheckRepliesJob;

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

    class MyLogger implements JobLogger {
        @Override
        public void log(int priority, @NonNull String tag, @NonNull String message, @Nullable Throwable t) {
            // log
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        requestManager = new RequestManager();
        riksdagenAPIManager = new RiksdagenAPIManager(this);
        themeManager = new ThemeManager(this);
        alertManager = new AlertManager(this);
        JobConfig.addLogger(new MyLogger());
        JobManager.create(this).addJobCreator(new AlertJobCreator());
        scheduleCheckRepliesJobIfNotRunning();

        representativeManager = new RepresentativeManager(this);
    }

    public void scheduleCheckRepliesJobIfNotRunning() {
        if (!isCheckRepliesScheduled()) {
            //Create jobs which will search for replies to tracked questions
            System.out.println("Scheduled job");
            CheckRepliesJob.scheduleJob();
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
        return !JobManager.instance().getAllJobRequestsForTag(CheckRepliesJob.TAG).isEmpty();
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
