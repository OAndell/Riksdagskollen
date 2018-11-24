package oscar.riksdagskollen;

import android.test.mock.MockContext;

import org.junit.Before;
import org.junit.Test;

import oscar.riksdagskollen.Util.View.LikeBar;

import static junit.framework.Assert.assertEquals;

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
        assertEquals(1, likeBar.getLikes());
        likeBar.like();
        assertEquals(2, likeBar.getLikes());
    }

    @Test
    public void dislikeTest() {
        likeBar.setRatio(0, 0);
        likeBar.dislike();
        assertEquals(1, likeBar.getDislikes());
        likeBar.dislike();
        assertEquals(2, likeBar.getDislikes());
    }

    @Test
    public void percentTest() {
        likeBar.setRatio(50, 50);

        assertEquals(50, likeBar.getPercent());

        likeBar.setRatio(1, 99);
        assertEquals(1, likeBar.getPercent());

        likeBar.setRatio(200, 100);
        assertEquals(67, likeBar.getPercent());
    }

    @Test
    public void affect_percentTest() {
        likeBar.setRatio(0, 0);
        likeBar.like();
        assertEquals(100, likeBar.getPercent());
        likeBar.dislike();
        assertEquals(50, likeBar.getPercent());
        likeBar.dislike();
        assertEquals(33, likeBar.getPercent());
    }

}
