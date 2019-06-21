package oscar.riksdagskollen.Util.Helper;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class AnimUtil {


    public static void expand(final View v, Animation.AnimationListener listener) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.setAlpha(interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(125);
        a.setAnimationListener(listener);
        v.startAnimation(a);
    }

    public static void collapse(final View v, Animation.AnimationListener listener) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {

                v.setAlpha(1 - interpolatedTime);
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(125);
        a.setAnimationListener(listener);
        v.startAnimation(a);
    }

    public static void toggleExpansion(final View v, Animation.AnimationListener listener) {
        final int height = v.getMeasuredHeight();

        if (height == 0 || v.getVisibility() == View.GONE) expand(v, listener);
        else collapse(v, listener);
    }

    public static void fadeOut(final View v, Animation.AnimationListener listener) {
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.setAlpha(1 - interpolatedTime);
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(400);
        a.setAnimationListener(listener);
        v.startAnimation(a);
    }

    public static void fadeIn(final View v, Animation.AnimationListener listener) {
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.setAlpha(interpolatedTime);
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(400);
        a.setAnimationListener(listener);
        v.startAnimation(a);
    }
}
