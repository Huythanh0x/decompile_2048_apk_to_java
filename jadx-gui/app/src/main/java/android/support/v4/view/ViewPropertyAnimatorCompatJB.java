package android.support.v4.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

/* loaded from: classes.dex */
class ViewPropertyAnimatorCompatJB {
    ViewPropertyAnimatorCompatJB() {
    }

    public static void withStartAction(View view, Runnable runnable) {
        view.animate().withStartAction(runnable);
    }

    public static void withEndAction(View view, Runnable runnable) {
        view.animate().withEndAction(runnable);
    }

    public static void withLayer(View view) {
        view.animate().withLayer();
    }

    public static void setListener(final View view, final ViewPropertyAnimatorListener viewPropertyAnimatorListener) {
        if (viewPropertyAnimatorListener != null) {
            view.animate().setListener(new AnimatorListenerAdapter() { // from class: android.support.v4.view.ViewPropertyAnimatorCompatJB.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animator) {
                    ViewPropertyAnimatorListener.this.onAnimationCancel(view);
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    ViewPropertyAnimatorListener.this.onAnimationEnd(view);
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animator) {
                    ViewPropertyAnimatorListener.this.onAnimationStart(view);
                }
            });
        } else {
            view.animate().setListener(null);
        }
    }
}
