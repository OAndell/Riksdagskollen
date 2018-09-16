package oscar.riksdagskollen;

import android.os.Parcel;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Pair;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by oscar on 2018-09-16.
 */
import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import com.android.volley.VolleyError;

import oscar.riksdagskollen.Manager.RiksdagenAPIManager;
import oscar.riksdagskollen.Util.Callback.CurrentNewsCallback;
import oscar.riksdagskollen.Util.JSONModel.CurrentNews;


// @RunWith is required only if you use a mix of JUnit3 and JUnit4.
@RunWith(AndroidJUnit4.class)
@SmallTest
public class APITest {

    RiksdagenAPIManager apiManager;

    @Before
    public void setup(){
        RikdagskollenApp app = RikdagskollenApp.getInstance();
        apiManager = app.getRiksdagenAPIManager();
    }

    @Test
    public void currentNews_CorrectlyFetched_ReturnsTrue() {
        apiManager.getCurrentNews(new CurrentNewsCallback() {
            @Override
            public void onNewsFetched(List<CurrentNews> currentNews) {
                assertTrue(currentNews.size() > 0);
                assertTrue(currentNews.get(0).getTitel() != null);
            }

            @Override
            public void onFail(VolleyError error) {
                fail();
            }
        },1);
    }
}