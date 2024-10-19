package android.support.design.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButtonImpl;
import android.support.v4.view.ViewCompat;
import android.view.View;

/* loaded from: classes.dex */
class FloatingActionButtonHoneycombMr1 extends FloatingActionButtonEclairMr1 {
    private boolean mIsHiding;

    @Override // android.support.design.widget.FloatingActionButtonImpl
    boolean requirePreDrawListener() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public FloatingActionButtonHoneycombMr1(View view, ShadowViewDelegate shadowViewDelegate) {
        super(view, shadowViewDelegate);
    }

    @Override // android.support.design.widget.FloatingActionButtonImpl
    void onPreDraw() {
        updateFromViewRotation(this.mView.getRotation());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.design.widget.FloatingActionButtonEclairMr1, android.support.design.widget.FloatingActionButtonImpl
    public void hide(@Nullable final FloatingActionButtonImpl.InternalVisibilityChangedListener internalVisibilityChangedListener) {
        if (this.mIsHiding || this.mView.getVisibility() != 0) {
            if (internalVisibilityChangedListener != null) {
                internalVisibilityChangedListener.onHidden();
            }
        } else {
            if (!ViewCompat.isLaidOut(this.mView) || this.mView.isInEditMode()) {
                this.mView.setVisibility(8);
                if (internalVisibilityChangedListener != null) {
                    internalVisibilityChangedListener.onHidden();
                    return;
                }
                return;
            }
            this.mView.animate().scaleX(0.0f).scaleY(0.0f).alpha(0.0f).setDuration(200L).setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR).setListener(new AnimatorListenerAdapter() { // from class: android.support.design.widget.FloatingActionButtonHoneycombMr1.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animator) {
                    FloatingActionButtonHoneycombMr1.this.mIsHiding = true;
                    FloatingActionButtonHoneycombMr1.this.mView.setVisibility(0);
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animator) {
                    FloatingActionButtonHoneycombMr1.this.mIsHiding = false;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    FloatingActionButtonHoneycombMr1.this.mIsHiding = false;
                    FloatingActionButtonHoneycombMr1.this.mView.setVisibility(8);
                    if (internalVisibilityChangedListener != null) {
                        internalVisibilityChangedListener.onHidden();
                    }
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.design.widget.FloatingActionButtonEclairMr1, android.support.design.widget.FloatingActionButtonImpl
    public void show(@Nullable final FloatingActionButtonImpl.InternalVisibilityChangedListener internalVisibilityChangedListener) {
        if (this.mView.getVisibility() != 0) {
            if (ViewCompat.isLaidOut(this.mView) && !this.mView.isInEditMode()) {
                this.mView.setAlpha(0.0f);
                this.mView.setScaleY(0.0f);
                this.mView.setScaleX(0.0f);
                this.mView.animate().scaleX(1.0f).scaleY(1.0f).alpha(1.0f).setDuration(200L).setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR).setListener(new AnimatorListenerAdapter() { // from class: android.support.design.widget.FloatingActionButtonHoneycombMr1.2
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationStart(Animator animator) {
                        FloatingActionButtonHoneycombMr1.this.mView.setVisibility(0);
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        if (internalVisibilityChangedListener != null) {
                            internalVisibilityChangedListener.onShown();
                        }
                    }
                });
                return;
            }
            this.mView.setVisibility(0);
            this.mView.setAlpha(1.0f);
            this.mView.setScaleY(1.0f);
            this.mView.setScaleX(1.0f);
            if (internalVisibilityChangedListener != null) {
                internalVisibilityChangedListener.onShown();
            }
        }
    }

    private void updateFromViewRotation(float f) {
        if (this.mShadowDrawable != null) {
            this.mShadowDrawable.setRotation(-f);
        }
        if (this.mBorderDrawable != null) {
            this.mBorderDrawable.setRotation(-f);
        }
    }
}
