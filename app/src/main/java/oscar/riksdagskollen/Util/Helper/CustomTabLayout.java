package oscar.riksdagskollen.Util.Helper;

import android.content.Context;
import android.util.AttributeSet;

import com.google.android.material.tabs.TabLayout;

import java.lang.reflect.Field;


/**
 * Source: https://medium.com/@elsenovraditya/set-tab-minimum-width-of-scrollable-tablayout-programmatically-8146d6101efe
 */
public class CustomTabLayout extends TabLayout {

    private static final int WIDTH_INDEX = 0;
    private static final int DIVIDER_FACTOR = 4;
    private static final String SCROLLABLE_TAB_MIN_WIDTH = "mScrollableTabMinWidth";

    public CustomTabLayout(Context context) {
        super(context);
        initTabMinWidth();
    }

    public CustomTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTabMinWidth();
    }

    public CustomTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTabMinWidth();
    }

    private void initTabMinWidth() {
        int[] wh = ScreenSizeCalc.getScreenSize(getContext());
        int tabMinWidth = wh[WIDTH_INDEX] / DIVIDER_FACTOR;
        tabMinWidth = (int) Math.ceil(tabMinWidth * 0.95);

        Field field;
        try {
            field = TabLayout.class.getDeclaredField(SCROLLABLE_TAB_MIN_WIDTH);
            field.setAccessible(true);
            field.set(this, tabMinWidth);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}