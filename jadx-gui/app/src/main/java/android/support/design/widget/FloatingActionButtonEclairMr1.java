package android.support.design.widget;

import android.R;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.Nullable;
import android.support.design.widget.AnimationUtils;
import android.support.design.widget.FloatingActionButtonImpl;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class FloatingActionButtonEclairMr1 extends FloatingActionButtonImpl {
    private int mAnimationDuration;
    CircularBorderDrawable mBorderDrawable;
    private float mElevation;
    private boolean mIsHiding;
    private float mPressedTranslationZ;
    Drawable mRippleDrawable;
    ShadowDrawableWrapper mShadowDrawable;
    Drawable mShapeDrawable;
    private StateListAnimator mStateListAnimator;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FloatingActionButtonEclairMr1(View view, ShadowViewDelegate shadowViewDelegate) {
        super(view, shadowViewDelegate);
        this.mAnimationDuration = view.getResources().getInteger(R.integer.config_shortAnimTime);
        this.mStateListAnimator = new StateListAnimator();
        this.mStateListAnimator.setTarget(view);
        this.mStateListAnimator.addState(PRESSED_ENABLED_STATE_SET, setupAnimation(new ElevateToTranslationZAnimation()));
        this.mStateListAnimator.addState(FOCUSED_ENABLED_STATE_SET, setupAnimation(new ElevateToTranslationZAnimation()));
        this.mStateListAnimator.addState(EMPTY_STATE_SET, setupAnimation(new ResetElevationAnimation()));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.design.widget.FloatingActionButtonImpl
    public void setBackgroundDrawable(ColorStateList colorStateList, PorterDuff.Mode mode, int i, int i2) {
        Drawable[] drawableArr;
        this.mShapeDrawable = DrawableCompat.wrap(createShapeDrawable());
        DrawableCompat.setTintList(this.mShapeDrawable, colorStateList);
        if (mode != null) {
            DrawableCompat.setTintMode(this.mShapeDrawable, mode);
        }
        this.mRippleDrawable = DrawableCompat.wrap(createShapeDrawable());
        DrawableCompat.setTintList(this.mRippleDrawable, createColorStateList(i));
        DrawableCompat.setTintMode(this.mRippleDrawable, PorterDuff.Mode.MULTIPLY);
        if (i2 > 0) {
            this.mBorderDrawable = createBorderDrawable(i2, colorStateList);
            drawableArr = new Drawable[]{this.mBorderDrawable, this.mShapeDrawable, this.mRippleDrawable};
        } else {
            this.mBorderDrawable = null;
            drawableArr = new Drawable[]{this.mShapeDrawable, this.mRippleDrawable};
        }
        this.mShadowDrawable = new ShadowDrawableWrapper(this.mView.getResources(), new LayerDrawable(drawableArr), this.mShadowViewDelegate.getRadius(), this.mElevation, this.mElevation + this.mPressedTranslationZ);
        this.mShadowDrawable.setAddPaddingForCorners(false);
        this.mShadowViewDelegate.setBackgroundDrawable(this.mShadowDrawable);
        updatePadding();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.design.widget.FloatingActionButtonImpl
    public void setBackgroundTintList(ColorStateList colorStateList) {
        DrawableCompat.setTintList(this.mShapeDrawable, colorStateList);
        if (this.mBorderDrawable != null) {
            this.mBorderDrawable.setBorderTint(colorStateList);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.design.widget.FloatingActionButtonImpl
    public void setBackgroundTintMode(PorterDuff.Mode mode) {
        DrawableCompat.setTintMode(this.mShapeDrawable, mode);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.design.widget.FloatingActionButtonImpl
    public void setRippleColor(int i) {
        DrawableCompat.setTintList(this.mRippleDrawable, createColorStateList(i));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.design.widget.FloatingActionButtonImpl
    public void setElevation(float f) {
        if (this.mElevation == f || this.mShadowDrawable == null) {
            return;
        }
        this.mShadowDrawable.setShadowSize(f, this.mPressedTranslationZ + f);
        this.mElevation = f;
        updatePadding();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.design.widget.FloatingActionButtonImpl
    public void setPressedTranslationZ(float f) {
        if (this.mPressedTranslationZ == f || this.mShadowDrawable == null) {
            return;
        }
        this.mPressedTranslationZ = f;
        this.mShadowDrawable.setMaxShadowSize(this.mElevation + f);
        updatePadding();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.design.widget.FloatingActionButtonImpl
    public void onDrawableStateChanged(int[] iArr) {
        this.mStateListAnimator.setState(iArr);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.design.widget.FloatingActionButtonImpl
    public void jumpDrawableToCurrentState() {
        this.mStateListAnimator.jumpToCurrentState();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.design.widget.FloatingActionButtonImpl
    public void hide(@Nullable final FloatingActionButtonImpl.InternalVisibilityChangedListener internalVisibilityChangedListener) {
        if (this.mIsHiding || this.mView.getVisibility() != 0) {
            if (internalVisibilityChangedListener != null) {
                internalVisibilityChangedListener.onHidden();
            }
        } else {
            Animation loadAnimation = android.view.animation.AnimationUtils.loadAnimation(this.mView.getContext(), android.support.design.R.anim.design_fab_out);
            loadAnimation.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
            loadAnimation.setDuration(200L);
            loadAnimation.setAnimationListener(new AnimationUtils.AnimationListenerAdapter() { // from class: android.support.design.widget.FloatingActionButtonEclairMr1.1
                @Override // android.support.design.widget.AnimationUtils.AnimationListenerAdapter, android.view.animation.Animation.AnimationListener
                public void onAnimationStart(Animation animation) {
                    FloatingActionButtonEclairMr1.this.mIsHiding = true;
                }

                @Override // android.support.design.widget.AnimationUtils.AnimationListenerAdapter, android.view.animation.Animation.AnimationListener
                public void onAnimationEnd(Animation animation) {
                    FloatingActionButtonEclairMr1.this.mIsHiding = false;
                    FloatingActionButtonEclairMr1.this.mView.setVisibility(8);
                    if (internalVisibilityChangedListener != null) {
                        internalVisibilityChangedListener.onHidden();
                    }
                }
            });
            this.mView.startAnimation(loadAnimation);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.design.widget.FloatingActionButtonImpl
    public void show(@Nullable final FloatingActionButtonImpl.InternalVisibilityChangedListener internalVisibilityChangedListener) {
        if (this.mView.getVisibility() == 0 && !this.mIsHiding) {
            if (internalVisibilityChangedListener != null) {
                internalVisibilityChangedListener.onShown();
                return;
            }
            return;
        }
        this.mView.clearAnimation();
        this.mView.setVisibility(0);
        Animation loadAnimation = android.view.animation.AnimationUtils.loadAnimation(this.mView.getContext(), android.support.design.R.anim.design_fab_in);
        loadAnimation.setDuration(200L);
        loadAnimation.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
        loadAnimation.setAnimationListener(new AnimationUtils.AnimationListenerAdapter() { // from class: android.support.design.widget.FloatingActionButtonEclairMr1.2
            @Override // android.support.design.widget.AnimationUtils.AnimationListenerAdapter, android.view.animation.Animation.AnimationListener
            public void onAnimationEnd(Animation animation) {
                if (internalVisibilityChangedListener != null) {
                    internalVisibilityChangedListener.onShown();
                }
            }
        });
        this.mView.startAnimation(loadAnimation);
    }

    private void updatePadding() {
        Rect rect = new Rect();
        this.mShadowDrawable.getPadding(rect);
        this.mShadowViewDelegate.setShadowPadding(rect.left, rect.top, rect.right, rect.bottom);
    }

    private Animation setupAnimation(Animation animation) {
        animation.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
        animation.setDuration(this.mAnimationDuration);
        return animation;
    }

    /* loaded from: classes.dex */
    private abstract class BaseShadowAnimation extends Animation {
        private float mShadowSizeDiff;
        private float mShadowSizeStart;

        protected abstract float getTargetShadowSize();

        private BaseShadowAnimation() {
        }

        @Override // android.view.animation.Animation
        public void reset() {
            super.reset();
            this.mShadowSizeStart = FloatingActionButtonEclairMr1.this.mShadowDrawable.getShadowSize();
            this.mShadowSizeDiff = getTargetShadowSize() - this.mShadowSizeStart;
        }

        @Override // android.view.animation.Animation
        protected void applyTransformation(float f, Transformation transformation) {
            FloatingActionButtonEclairMr1.this.mShadowDrawable.setShadowSize(this.mShadowSizeStart + (this.mShadowSizeDiff * f));
        }
    }

    /* loaded from: classes.dex */
    private class ResetElevationAnimation extends BaseShadowAnimation {
        private ResetElevationAnimation() {
            super();
        }

        @Override // android.support.design.widget.FloatingActionButtonEclairMr1.BaseShadowAnimation
        protected float getTargetShadowSize() {
            return FloatingActionButtonEclairMr1.this.mElevation;
        }
    }

    /* loaded from: classes.dex */
    private class ElevateToTranslationZAnimation extends BaseShadowAnimation {
        private ElevateToTranslationZAnimation() {
            super();
        }

        @Override // android.support.design.widget.FloatingActionButtonEclairMr1.BaseShadowAnimation
        protected float getTargetShadowSize() {
            return FloatingActionButtonEclairMr1.this.mElevation + FloatingActionButtonEclairMr1.this.mPressedTranslationZ;
        }
    }

    private static ColorStateList createColorStateList(int i) {
        return new ColorStateList(new int[][]{FOCUSED_ENABLED_STATE_SET, PRESSED_ENABLED_STATE_SET, new int[0]}, new int[]{i, i, 0});
    }
}
