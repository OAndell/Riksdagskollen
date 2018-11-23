package oscar.riksdagskollen;

import android.test.mock.MockContext;

import org.junit.Before;
import org.junit.Test;

import oscar.riksdagskollen.Util.View.LikeBar;

import static junit.framework.TestCase.assertTrue;

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
        assertTrue(likeBar.getLikes() == 1);
        likeBar.like();
        assertTrue(likeBar.getLikes() == 2);
    }

    @Test
    public void dislikeTest() {
        likeBar.setRatio(0, 0);
        likeBar.dislike();
        assertTrue(likeBar.getDislikes() == 1);
        likeBar.dislike();
        assertTrue(likeBar.getDislikes() == 2);
    }

    @Test
    public void percentTest() {
        likeBar.setRatio(50, 50);

        assertTrue(likeBar.getPercent() == 50);

        likeBar.setRatio(1, 99);
        assertTrue(likeBar.getPercent() == 1);

        likeBar.setRatio(200, 100);
        assertTrue(likeBar.getPercent() == 67);
    }

}
