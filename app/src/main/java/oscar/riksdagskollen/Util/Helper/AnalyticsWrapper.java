package oscar.riksdagskollen.Util.Helper;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

public interface AnalyticsWrapper {

    void initAnalytics(Context context);

    void setCurrentScreen(Activity activity, String screenName);

    void logEvent(String event, @Nullable Bundle bundle);

}
