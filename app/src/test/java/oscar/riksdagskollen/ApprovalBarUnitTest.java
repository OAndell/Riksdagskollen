package oscar.riksdagskollen;

import android.test.mock.MockContext;

import org.junit.Before;
import org.junit.Test;

import oscar.riksdagskollen.Util.View.ApprovalBarView;

import static junit.framework.Assert.assertEquals;

public class ApprovalBarUnitTest {

    private ApprovalBarView approvalBarView;
    private MockContext mContext;

    @Before
    public void setup() {
        mContext = new MockContext();
        approvalBarView = new ApprovalBarView(mContext);
    }

    @Test
    public void likeTest() {
        approvalBarView.setRatio(0, 0);
        approvalBarView.like();
        assertEquals(1, approvalBarView.getLikes());
        approvalBarView.like();
        assertEquals(2, approvalBarView.getLikes());
    }

    @Test
    public void dislikeTest() {
        approvalBarView.setRatio(0, 0);
        approvalBarView.dislike();
        assertEquals(1, approvalBarView.getDislikes());
        approvalBarView.dislike();
        assertEquals(2, approvalBarView.getDislikes());
    }

    @Test
    public void percentTest() {

        approvalBarView.setRatio(50, 50);

        assertEquals(50, approvalBarView.getPercent());

        approvalBarView.setRatio(1, 99);
        assertEquals(1, approvalBarView.getPercent());

        approvalBarView.setRatio(200, 100);
        assertEquals(67, approvalBarView.getPercent());
    }

    @Test
    public void affect_percentTest() {

        approvalBarView.setRatio(0, 0);
        approvalBarView.like();
        assertEquals(100, approvalBarView.getPercent());
        approvalBarView.dislike();
        assertEquals(50, approvalBarView.getPercent());
        approvalBarView.dislike();
        assertEquals(33, approvalBarView.getPercent());
    }

}
