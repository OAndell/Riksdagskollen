package oscar.riksdagskollen.Util.Helper;

import android.app.Activity;
import android.content.Context;

public interface AnalyticsWrapper {

    void initAnalytics(Context context);

    void setCurrentScreen(Activity activity, String screenName);

}
