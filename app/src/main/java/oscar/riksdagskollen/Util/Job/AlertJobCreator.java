package oscar.riksdagskollen.Util.Job;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;

/**
 * Created by gustavaaro on 2018-09-24.
 */

public class AlertJobCreator implements com.evernote.android.job.JobCreator {


    @Nullable
    @Override
    public Job create(@NonNull String tag) {


        switch (tag) {
            case CheckRepliesJob.TAG:
                return new CheckRepliesJob();
            default:
                return null;
        }
    }
}
