package oscar.riksdagskollen;

import android.test.mock.MockContext;
import android.view.View;

import org.junit.Before;
import org.junit.Test;

import oscar.riksdagskollen.Util.View.LikeBar;

public class LikeBarUnitTest {

    private LikeBar likeBar;

    @Before
    public void setup() {
        likeBar = new LikeBar(new MockContext());
    }

    @Test
    public void likeTest() {
        likeBar.setRatio(0, 0);
        likeBar.like();
        assert (likeBar.getLikes() == 1);
        likeBar.like();
        assert (likeBar.getLikes() == 2);
    }

    @Test
    public void dislikeTest() {
        likeBar.setRatio(0, 0);
        likeBar.dislike();
        assert (likeBar.getDislikes() == 1);
        likeBar.dislike();
        assert (likeBar.getDislikes() == 2);
    }

    @Test
    public void percentTest() {
        likeBar.setRatio(50, 50);
        assert (likeBar.getPercent() == 50);

        likeBar.setRatio(1, 2);
        assert (likeBar.getPercent() == 1);

        likeBar.setRatio(200, 100);
        assert (likeBar.getPercent() == 33);
    }

    @Test
    public void invisible_when_no_votes_Test() {
        likeBar.setRatio(0, 0);
        assert (likeBar.getVisibility() == View.GONE);
    }

    @Test
    public void visible_when_votes_Test() {
        likeBar.setRatio(1, 0);
        assert (likeBar.getVisibility() == View.VISIBLE);
    }
}
