package oscar.riksdagskollen.Util.View;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.Animatable2Compat;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.util.AttributeSet;
import android.view.View;

import oscar.riksdagskollen.R;

public class LoadingView extends android.support.v7.widget.AppCompatImageView {
    private Context context;

    public LoadingView(Context context) {
        super(context);
        this.context = context;
        setup();
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setup();
    }

    private void setup() {
        final AnimatedVectorDrawableCompat loadingAnimation = AnimatedVectorDrawableCompat.create(context, R.drawable.loading_animation);

        if (loadingAnimation == null) return;
        loadingAnimation.setAutoMirrored(false);

        final View container = this;
        loadingAnimation.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
            @Override
            public void onAnimationEnd(Drawable drawable) {
                container.post(new Runnable() {
                    @Override
                    public void run() {
                        loadingAnimation.start();
                    }
                });
            }
        });
        this.setImageDrawable(loadingAnimation);
        loadingAnimation.start();
    }

}
