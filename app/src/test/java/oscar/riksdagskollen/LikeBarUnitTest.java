package oscar.riksdagskollen;

import android.test.mock.MockContext;

import org.junit.Before;
import org.junit.Test;

import oscar.riksdagskollen.Util.View.LikeBarView;

import static junit.framework.Assert.assertEquals;

public class LikeBarUnitTest {

    private LikeBarView likeBarView;

    @Before
    public void setup() {
        likeBarView = new LikeBarView(new MockContext());
    }

    @Test
    public void likeTest() {
        likeBarView.setRatio(0, 0);
        likeBarView.like();
        assertEquals(1, likeBarView.getLikes());
        likeBarView.like();
        assertEquals(2, likeBarView.getLikes());
    }

    @Test
    public void dislikeTest() {
        likeBarView.setRatio(0, 0);
        likeBarView.dislike();
        assertEquals(1, likeBarView.getDislikes());
        likeBarView.dislike();
        assertEquals(2, likeBarView.getDislikes());
    }

    @Test
    public void percentTest() {
        likeBarView.setRatio(50, 50);

        assertEquals(50, likeBarView.getPercent());

        likeBarView.setRatio(1, 99);
        assertEquals(1, likeBarView.getPercent());

        likeBarView.setRatio(200, 100);
        assertEquals(67, likeBarView.getPercent());
    }

    @Test
    public void affect_percentTest() {
        likeBarView.setRatio(0, 0);
        likeBarView.like();
        assertEquals(100, likeBarView.getPercent());
        likeBarView.dislike();
        assertEquals(50, likeBarView.getPercent());
        likeBarView.dislike();
        assertEquals(33, likeBarView.getPercent());
    }

}
