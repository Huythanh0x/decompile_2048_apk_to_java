package android.support.design.widget;

import android.R;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.animation.Interpolator;

@TargetApi(21)
/* loaded from: classes.dex */
class FloatingActionButtonLollipop extends FloatingActionButtonHoneycombMr1 {
    private Interpolator mInterpolator;

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.design.widget.FloatingActionButtonEclairMr1, android.support.design.widget.FloatingActionButtonImpl
    public void jumpDrawableToCurrentState() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.design.widget.FloatingActionButtonEclairMr1, android.support.design.widget.FloatingActionButtonImpl
    public void onDrawableStateChanged(int[] iArr) {
    }

    @Override // android.support.design.widget.FloatingActionButtonHoneycombMr1, android.support.design.widget.FloatingActionButtonImpl
    boolean requirePreDrawListener() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public FloatingActionButtonLollipop(View view, ShadowViewDelegate shadowViewDelegate) {
        super(view, shadowViewDelegate);
        if (view.isInEditMode()) {
            return;
        }
        this.mInterpolator = android.view.animation.AnimationUtils.loadInterpolator(this.mView.getContext(), R.interpolator.fast_out_slow_in);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.design.widget.FloatingActionButtonEclairMr1, android.support.design.widget.FloatingActionButtonImpl
    public void setBackgroundDrawable(ColorStateList colorStateList, PorterDuff.Mode mode, int i, int i2) {
        Drawable drawable;
        this.mShapeDrawable = DrawableCompat.wrap(createShapeDrawable());
        DrawableCompat.setTintList(this.mShapeDrawable, colorStateList);
        if (mode != null) {
            DrawableCompat.setTintMode(this.mShapeDrawable, mode);
        }
        if (i2 > 0) {
            this.mBorderDrawable = createBorderDrawable(i2, colorStateList);
            drawable = new LayerDrawable(new Drawable[]{this.mBorderDrawable, this.mShapeDrawable});
        } else {
            this.mBorderDrawable = null;
            drawable = this.mShapeDrawable;
        }
        this.mRippleDrawable = new RippleDrawable(ColorStateList.valueOf(i), drawable, null);
        this.mShadowViewDelegate.setBackgroundDrawable(this.mRippleDrawable);
        this.mShadowViewDelegate.setShadowPadding(0, 0, 0, 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.design.widget.FloatingActionButtonEclairMr1, android.support.design.widget.FloatingActionButtonImpl
    public void setRippleColor(int i) {
        if (this.mRippleDrawable instanceof RippleDrawable) {
            ((RippleDrawable) this.mRippleDrawable).setColor(ColorStateList.valueOf(i));
        } else {
            super.setRippleColor(i);
        }
    }

    @Override // android.support.design.widget.FloatingActionButtonEclairMr1, android.support.design.widget.FloatingActionButtonImpl
    public void setElevation(float f) {
        ViewCompat.setElevation(this.mView, f);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.design.widget.FloatingActionButtonEclairMr1, android.support.design.widget.FloatingActionButtonImpl
    public void setPressedTranslationZ(float f) {
        android.animation.StateListAnimator stateListAnimator = new android.animation.StateListAnimator();
        stateListAnimator.addState(PRESSED_ENABLED_STATE_SET, setupAnimator(ObjectAnimator.ofFloat(this.mView, "translationZ", f)));
        stateListAnimator.addState(FOCUSED_ENABLED_STATE_SET, setupAnimator(ObjectAnimator.ofFloat(this.mView, "translationZ", f)));
        stateListAnimator.addState(EMPTY_STATE_SET, setupAnimator(ObjectAnimator.ofFloat(this.mView, "translationZ", 0.0f)));
        this.mView.setStateListAnimator(stateListAnimator);
    }

    private Animator setupAnimator(Animator animator) {
        animator.setInterpolator(this.mInterpolator);
        return animator;
    }

    @Override // android.support.design.widget.FloatingActionButtonImpl
    CircularBorderDrawable newCircularDrawable() {
        return new CircularBorderDrawableLollipop();
    }
}
