package oscar.riksdagskollen.Util.Job;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evernote.android.job.Job;

/**
 * Created by gustavaaro on 2018-09-24.
 */

public class AlertJobCreator implements com.evernote.android.job.JobCreator {


    @Nullable
    @Override
    public Job create(@NonNull String tag) {


        switch (tag) {
            case CheckAlertsJob.TAG:
                return new CheckAlertsJob();
            case DownloadAllRepresentativesJob.TAG:
                return new DownloadAllRepresentativesJob();
            default:
                return null;
        }
    }
}
