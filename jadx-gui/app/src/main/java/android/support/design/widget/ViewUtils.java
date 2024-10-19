package android.support.design.widget;

import android.os.Build;
import android.support.design.widget.ValueAnimatorCompat;
import android.view.View;

/* loaded from: classes.dex */
class ViewUtils {
    static final ValueAnimatorCompat.Creator DEFAULT_ANIMATOR_CREATOR = new ValueAnimatorCompat.Creator() { // from class: android.support.design.widget.ViewUtils.1
        @Override // android.support.design.widget.ValueAnimatorCompat.Creator
        public ValueAnimatorCompat createAnimator() {
            return new ValueAnimatorCompat(Build.VERSION.SDK_INT >= 12 ? new ValueAnimatorCompatImplHoneycombMr1() : new ValueAnimatorCompatImplEclairMr1());
        }
    };
    private static final ViewUtilsImpl IMPL;

    /* loaded from: classes.dex */
    private interface ViewUtilsImpl {
        void setBoundsViewOutlineProvider(View view);
    }

    ViewUtils() {
    }

    static {
        if (Build.VERSION.SDK_INT >= 21) {
            IMPL = new ViewUtilsImplLollipop();
        } else {
            IMPL = new ViewUtilsImplBase();
        }
    }

    /* loaded from: classes.dex */
    private static class ViewUtilsImplBase implements ViewUtilsImpl {
        @Override // android.support.design.widget.ViewUtils.ViewUtilsImpl
        public void setBoundsViewOutlineProvider(View view) {
        }

        private ViewUtilsImplBase() {
        }
    }

    /* loaded from: classes.dex */
    private static class ViewUtilsImplLollipop implements ViewUtilsImpl {
        private ViewUtilsImplLollipop() {
        }

        @Override // android.support.design.widget.ViewUtils.ViewUtilsImpl
        public void setBoundsViewOutlineProvider(View view) {
            ViewUtilsLollipop.setBoundsViewOutlineProvider(view);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setBoundsViewOutlineProvider(View view) {
        IMPL.setBoundsViewOutlineProvider(view);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ValueAnimatorCompat createAnimator() {
        return DEFAULT_ANIMATOR_CREATOR.createAnimator();
    }
}
