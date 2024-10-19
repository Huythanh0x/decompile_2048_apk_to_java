package android.support.design.widget;

import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

/* loaded from: classes.dex */
class AnimationUtils {
    static final Interpolator LINEAR_INTERPOLATOR = new LinearInterpolator();
    static final Interpolator FAST_OUT_SLOW_IN_INTERPOLATOR = new FastOutSlowInInterpolator();
    static final Interpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();

    /* loaded from: classes.dex */
    static class AnimationListenerAdapter implements Animation.AnimationListener {
        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationEnd(Animation animation) {
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationRepeat(Animation animation) {
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationStart(Animation animation) {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static float lerp(float f, float f2, float f3) {
        return f + (f3 * (f2 - f));
    }

    AnimationUtils() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int lerp(int i, int i2, float f) {
        return i + Math.round(f * (i2 - i));
    }
}
