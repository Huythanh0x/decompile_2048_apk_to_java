package android.support.design.widget;

import android.R;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewTreeObserver;

/* loaded from: classes.dex */
abstract class FloatingActionButtonImpl {
    static final int SHOW_HIDE_ANIM_DURATION = 200;
    private ViewTreeObserver.OnPreDrawListener mPreDrawListener;
    final ShadowViewDelegate mShadowViewDelegate;
    final View mView;
    static final int[] PRESSED_ENABLED_STATE_SET = {R.attr.state_pressed, R.attr.state_enabled};
    static final int[] FOCUSED_ENABLED_STATE_SET = {R.attr.state_focused, R.attr.state_enabled};
    static final int[] EMPTY_STATE_SET = new int[0];

    /* loaded from: classes.dex */
    interface InternalVisibilityChangedListener {
        void onHidden();

        void onShown();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void hide(@Nullable InternalVisibilityChangedListener internalVisibilityChangedListener);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void jumpDrawableToCurrentState();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void onDrawableStateChanged(int[] iArr);

    void onPreDraw() {
    }

    boolean requirePreDrawListener() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void setBackgroundDrawable(ColorStateList colorStateList, PorterDuff.Mode mode, int i, int i2);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void setBackgroundTintList(ColorStateList colorStateList);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void setBackgroundTintMode(PorterDuff.Mode mode);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void setElevation(float f);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void setPressedTranslationZ(float f);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void setRippleColor(int i);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void show(@Nullable InternalVisibilityChangedListener internalVisibilityChangedListener);

    /* JADX INFO: Access modifiers changed from: package-private */
    public FloatingActionButtonImpl(View view, ShadowViewDelegate shadowViewDelegate) {
        this.mView = view;
        this.mShadowViewDelegate = shadowViewDelegate;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onAttachedToWindow() {
        if (requirePreDrawListener()) {
            ensurePreDrawListener();
            this.mView.getViewTreeObserver().addOnPreDrawListener(this.mPreDrawListener);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onDetachedFromWindow() {
        if (this.mPreDrawListener != null) {
            this.mView.getViewTreeObserver().removeOnPreDrawListener(this.mPreDrawListener);
            this.mPreDrawListener = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CircularBorderDrawable createBorderDrawable(int i, ColorStateList colorStateList) {
        Resources resources = this.mView.getResources();
        CircularBorderDrawable newCircularDrawable = newCircularDrawable();
        newCircularDrawable.setGradientColors(resources.getColor(android.support.design.R.color.design_fab_stroke_top_outer_color), resources.getColor(android.support.design.R.color.design_fab_stroke_top_inner_color), resources.getColor(android.support.design.R.color.design_fab_stroke_end_inner_color), resources.getColor(android.support.design.R.color.design_fab_stroke_end_outer_color));
        newCircularDrawable.setBorderWidth(i);
        newCircularDrawable.setBorderTint(colorStateList);
        return newCircularDrawable;
    }

    CircularBorderDrawable newCircularDrawable() {
        return new CircularBorderDrawable();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public GradientDrawable createShapeDrawable() {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(1);
        gradientDrawable.setColor(-1);
        return gradientDrawable;
    }

    private void ensurePreDrawListener() {
        if (this.mPreDrawListener == null) {
            this.mPreDrawListener = new ViewTreeObserver.OnPreDrawListener() { // from class: android.support.design.widget.FloatingActionButtonImpl.1
                @Override // android.view.ViewTreeObserver.OnPreDrawListener
                public boolean onPreDraw() {
                    FloatingActionButtonImpl.this.onPreDraw();
                    return true;
                }
            };
        }
    }
}
