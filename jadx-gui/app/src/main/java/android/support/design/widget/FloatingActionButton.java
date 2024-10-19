package android.support.design.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.design.R;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButtonImpl;
import android.support.design.widget.Snackbar;
import android.support.design.widget.ValueAnimatorCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import java.util.List;

@CoordinatorLayout.DefaultBehavior(Behavior.class)
/* loaded from: classes.dex */
public class FloatingActionButton extends ImageButton {
    private static final String LOG_TAG = "FloatingActionButton";
    private static final int SIZE_MINI = 1;
    private static final int SIZE_NORMAL = 0;
    private ColorStateList mBackgroundTint;
    private PorterDuff.Mode mBackgroundTintMode;
    private int mBorderWidth;
    private int mContentPadding;
    private final FloatingActionButtonImpl mImpl;
    private int mRippleColor;
    private final Rect mShadowPadding;
    private int mSize;

    /* loaded from: classes.dex */
    public static abstract class OnVisibilityChangedListener {
        public void onHidden(FloatingActionButton floatingActionButton) {
        }

        public void onShown(FloatingActionButton floatingActionButton) {
        }
    }

    public FloatingActionButton(Context context) {
        this(context, null);
    }

    public FloatingActionButton(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public FloatingActionButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        ThemeUtils.checkAppCompatTheme(context);
        this.mShadowPadding = new Rect();
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.FloatingActionButton, i, R.style.Widget_Design_FloatingActionButton);
        this.mBackgroundTint = obtainStyledAttributes.getColorStateList(R.styleable.FloatingActionButton_backgroundTint);
        this.mBackgroundTintMode = parseTintMode(obtainStyledAttributes.getInt(R.styleable.FloatingActionButton_backgroundTintMode, -1), null);
        this.mRippleColor = obtainStyledAttributes.getColor(R.styleable.FloatingActionButton_rippleColor, 0);
        this.mSize = obtainStyledAttributes.getInt(R.styleable.FloatingActionButton_fabSize, 0);
        this.mBorderWidth = obtainStyledAttributes.getDimensionPixelSize(R.styleable.FloatingActionButton_borderWidth, 0);
        float dimension = obtainStyledAttributes.getDimension(R.styleable.FloatingActionButton_elevation, 0.0f);
        float dimension2 = obtainStyledAttributes.getDimension(R.styleable.FloatingActionButton_pressedTranslationZ, 0.0f);
        obtainStyledAttributes.recycle();
        ShadowViewDelegate shadowViewDelegate = new ShadowViewDelegate() { // from class: android.support.design.widget.FloatingActionButton.1
            @Override // android.support.design.widget.ShadowViewDelegate
            public float getRadius() {
                return FloatingActionButton.this.getSizeDimension() / 2.0f;
            }

            @Override // android.support.design.widget.ShadowViewDelegate
            public void setShadowPadding(int i2, int i3, int i4, int i5) {
                FloatingActionButton.this.mShadowPadding.set(i2, i3, i4, i5);
                FloatingActionButton.this.setPadding(i2 + FloatingActionButton.this.mContentPadding, i3 + FloatingActionButton.this.mContentPadding, i4 + FloatingActionButton.this.mContentPadding, i5 + FloatingActionButton.this.mContentPadding);
            }

            @Override // android.support.design.widget.ShadowViewDelegate
            public void setBackgroundDrawable(Drawable drawable) {
                FloatingActionButton.super.setBackgroundDrawable(drawable);
            }
        };
        int i2 = Build.VERSION.SDK_INT;
        if (i2 >= 21) {
            this.mImpl = new FloatingActionButtonLollipop(this, shadowViewDelegate);
        } else if (i2 >= 12) {
            this.mImpl = new FloatingActionButtonHoneycombMr1(this, shadowViewDelegate);
        } else {
            this.mImpl = new FloatingActionButtonEclairMr1(this, shadowViewDelegate);
        }
        this.mContentPadding = (getSizeDimension() - ((int) getResources().getDimension(R.dimen.design_fab_content_size))) / 2;
        this.mImpl.setBackgroundDrawable(this.mBackgroundTint, this.mBackgroundTintMode, this.mRippleColor, this.mBorderWidth);
        this.mImpl.setElevation(dimension);
        this.mImpl.setPressedTranslationZ(dimension2);
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onMeasure(int i, int i2) {
        int sizeDimension = getSizeDimension();
        int min = Math.min(resolveAdjustedSize(sizeDimension, i), resolveAdjustedSize(sizeDimension, i2));
        setMeasuredDimension(this.mShadowPadding.left + min + this.mShadowPadding.right, min + this.mShadowPadding.top + this.mShadowPadding.bottom);
    }

    public void setRippleColor(@ColorInt int i) {
        if (this.mRippleColor != i) {
            this.mRippleColor = i;
            this.mImpl.setRippleColor(i);
        }
    }

    @Override // android.view.View
    @Nullable
    public ColorStateList getBackgroundTintList() {
        return this.mBackgroundTint;
    }

    @Override // android.view.View
    public void setBackgroundTintList(@Nullable ColorStateList colorStateList) {
        if (this.mBackgroundTint != colorStateList) {
            this.mBackgroundTint = colorStateList;
            this.mImpl.setBackgroundTintList(colorStateList);
        }
    }

    @Override // android.view.View
    @Nullable
    public PorterDuff.Mode getBackgroundTintMode() {
        return this.mBackgroundTintMode;
    }

    @Override // android.view.View
    public void setBackgroundTintMode(@Nullable PorterDuff.Mode mode) {
        if (this.mBackgroundTintMode != mode) {
            this.mBackgroundTintMode = mode;
            this.mImpl.setBackgroundTintMode(mode);
        }
    }

    @Override // android.view.View
    public void setBackgroundDrawable(Drawable drawable) {
        Log.i(LOG_TAG, "Setting a custom background is not supported.");
    }

    @Override // android.view.View
    public void setBackgroundResource(int i) {
        Log.i(LOG_TAG, "Setting a custom background is not supported.");
    }

    @Override // android.view.View
    public void setBackgroundColor(int i) {
        Log.i(LOG_TAG, "Setting a custom background is not supported.");
    }

    public void show() {
        this.mImpl.show(null);
    }

    public void show(@Nullable OnVisibilityChangedListener onVisibilityChangedListener) {
        this.mImpl.show(wrapOnVisibilityChangedListener(onVisibilityChangedListener));
    }

    public void hide() {
        this.mImpl.hide(null);
    }

    public void hide(@Nullable OnVisibilityChangedListener onVisibilityChangedListener) {
        this.mImpl.hide(wrapOnVisibilityChangedListener(onVisibilityChangedListener));
    }

    @Nullable
    private FloatingActionButtonImpl.InternalVisibilityChangedListener wrapOnVisibilityChangedListener(@Nullable final OnVisibilityChangedListener onVisibilityChangedListener) {
        if (onVisibilityChangedListener == null) {
            return null;
        }
        return new FloatingActionButtonImpl.InternalVisibilityChangedListener() { // from class: android.support.design.widget.FloatingActionButton.2
            @Override // android.support.design.widget.FloatingActionButtonImpl.InternalVisibilityChangedListener
            public void onShown() {
                onVisibilityChangedListener.onShown(FloatingActionButton.this);
            }

            @Override // android.support.design.widget.FloatingActionButtonImpl.InternalVisibilityChangedListener
            public void onHidden() {
                onVisibilityChangedListener.onHidden(FloatingActionButton.this);
            }
        };
    }

    final int getSizeDimension() {
        if (this.mSize == 1) {
            return getResources().getDimensionPixelSize(R.dimen.design_fab_size_mini);
        }
        return getResources().getDimensionPixelSize(R.dimen.design_fab_size_normal);
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mImpl.onAttachedToWindow();
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mImpl.onDetachedFromWindow();
    }

    @Override // android.widget.ImageView, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        this.mImpl.onDrawableStateChanged(getDrawableState());
    }

    @Override // android.widget.ImageView, android.view.View
    @TargetApi(11)
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        this.mImpl.jumpDrawableToCurrentState();
    }

    private static int resolveAdjustedSize(int i, int i2) {
        int mode = View.MeasureSpec.getMode(i2);
        int size = View.MeasureSpec.getSize(i2);
        if (mode != Integer.MIN_VALUE) {
            return (mode == 0 || mode != 1073741824) ? i : size;
        }
        return Math.min(i, size);
    }

    static PorterDuff.Mode parseTintMode(int i, PorterDuff.Mode mode) {
        if (i == 3) {
            return PorterDuff.Mode.SRC_OVER;
        }
        if (i == 5) {
            return PorterDuff.Mode.SRC_IN;
        }
        if (i == 9) {
            return PorterDuff.Mode.SRC_ATOP;
        }
        switch (i) {
            case 14:
                return PorterDuff.Mode.MULTIPLY;
            case 15:
                return PorterDuff.Mode.SCREEN;
            default:
                return mode;
        }
    }

    /* loaded from: classes.dex */
    public static class Behavior extends CoordinatorLayout.Behavior<FloatingActionButton> {
        private static final boolean SNACKBAR_BEHAVIOR_ENABLED;
        private float mFabTranslationY;
        private ValueAnimatorCompat mFabTranslationYAnimator;
        private Rect mTmpRect;

        static {
            SNACKBAR_BEHAVIOR_ENABLED = Build.VERSION.SDK_INT >= 11;
        }

        @Override // android.support.design.widget.CoordinatorLayout.Behavior
        public boolean layoutDependsOn(CoordinatorLayout coordinatorLayout, FloatingActionButton floatingActionButton, View view) {
            return SNACKBAR_BEHAVIOR_ENABLED && (view instanceof Snackbar.SnackbarLayout);
        }

        @Override // android.support.design.widget.CoordinatorLayout.Behavior
        public boolean onDependentViewChanged(CoordinatorLayout coordinatorLayout, FloatingActionButton floatingActionButton, View view) {
            if (view instanceof Snackbar.SnackbarLayout) {
                updateFabTranslationForSnackbar(coordinatorLayout, floatingActionButton, view);
                return false;
            }
            if (!(view instanceof AppBarLayout)) {
                return false;
            }
            updateFabVisibility(coordinatorLayout, (AppBarLayout) view, floatingActionButton);
            return false;
        }

        private boolean updateFabVisibility(CoordinatorLayout coordinatorLayout, AppBarLayout appBarLayout, FloatingActionButton floatingActionButton) {
            if (((CoordinatorLayout.LayoutParams) floatingActionButton.getLayoutParams()).getAnchorId() != appBarLayout.getId()) {
                return false;
            }
            if (this.mTmpRect == null) {
                this.mTmpRect = new Rect();
            }
            Rect rect = this.mTmpRect;
            ViewGroupUtils.getDescendantRect(coordinatorLayout, appBarLayout, rect);
            if (rect.bottom <= appBarLayout.getMinimumHeightForVisibleOverlappingContent()) {
                floatingActionButton.hide();
                return true;
            }
            floatingActionButton.show();
            return true;
        }

        private void updateFabTranslationForSnackbar(CoordinatorLayout coordinatorLayout, final FloatingActionButton floatingActionButton, View view) {
            if (floatingActionButton.getVisibility() != 0) {
                return;
            }
            float fabTranslationYForSnackbar = getFabTranslationYForSnackbar(coordinatorLayout, floatingActionButton);
            if (this.mFabTranslationY == fabTranslationYForSnackbar) {
                return;
            }
            float translationY = ViewCompat.getTranslationY(floatingActionButton);
            if (this.mFabTranslationYAnimator != null && this.mFabTranslationYAnimator.isRunning()) {
                this.mFabTranslationYAnimator.cancel();
            }
            if (Math.abs(translationY - fabTranslationYForSnackbar) > floatingActionButton.getHeight() * 0.667f) {
                if (this.mFabTranslationYAnimator == null) {
                    this.mFabTranslationYAnimator = ViewUtils.createAnimator();
                    this.mFabTranslationYAnimator.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
                    this.mFabTranslationYAnimator.setUpdateListener(new ValueAnimatorCompat.AnimatorUpdateListener() { // from class: android.support.design.widget.FloatingActionButton.Behavior.1
                        @Override // android.support.design.widget.ValueAnimatorCompat.AnimatorUpdateListener
                        public void onAnimationUpdate(ValueAnimatorCompat valueAnimatorCompat) {
                            ViewCompat.setTranslationY(floatingActionButton, valueAnimatorCompat.getAnimatedFloatValue());
                        }
                    });
                }
                this.mFabTranslationYAnimator.setFloatValues(translationY, fabTranslationYForSnackbar);
                this.mFabTranslationYAnimator.start();
            } else {
                ViewCompat.setTranslationY(floatingActionButton, fabTranslationYForSnackbar);
            }
            this.mFabTranslationY = fabTranslationYForSnackbar;
        }

        private float getFabTranslationYForSnackbar(CoordinatorLayout coordinatorLayout, FloatingActionButton floatingActionButton) {
            List<View> dependencies = coordinatorLayout.getDependencies(floatingActionButton);
            int size = dependencies.size();
            float f = 0.0f;
            for (int i = 0; i < size; i++) {
                View view = dependencies.get(i);
                if ((view instanceof Snackbar.SnackbarLayout) && coordinatorLayout.doViewsOverlap(floatingActionButton, view)) {
                    f = Math.min(f, ViewCompat.getTranslationY(view) - view.getHeight());
                }
            }
            return f;
        }

        @Override // android.support.design.widget.CoordinatorLayout.Behavior
        public boolean onLayoutChild(CoordinatorLayout coordinatorLayout, FloatingActionButton floatingActionButton, int i) {
            List<View> dependencies = coordinatorLayout.getDependencies(floatingActionButton);
            int size = dependencies.size();
            for (int i2 = 0; i2 < size; i2++) {
                View view = dependencies.get(i2);
                if ((view instanceof AppBarLayout) && updateFabVisibility(coordinatorLayout, (AppBarLayout) view, floatingActionButton)) {
                    break;
                }
            }
            coordinatorLayout.onLayoutChild(floatingActionButton, i);
            offsetIfNeeded(coordinatorLayout, floatingActionButton);
            return true;
        }

        private void offsetIfNeeded(CoordinatorLayout coordinatorLayout, FloatingActionButton floatingActionButton) {
            int i;
            Rect rect = floatingActionButton.mShadowPadding;
            if (rect == null || rect.centerX() <= 0 || rect.centerY() <= 0) {
                return;
            }
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) floatingActionButton.getLayoutParams();
            int i2 = 0;
            if (floatingActionButton.getRight() >= coordinatorLayout.getWidth() - layoutParams.rightMargin) {
                i = rect.right;
            } else {
                i = floatingActionButton.getLeft() <= layoutParams.leftMargin ? -rect.left : 0;
            }
            if (floatingActionButton.getBottom() >= coordinatorLayout.getBottom() - layoutParams.bottomMargin) {
                i2 = rect.bottom;
            } else if (floatingActionButton.getTop() <= layoutParams.topMargin) {
                i2 = -rect.top;
            }
            floatingActionButton.offsetTopAndBottom(i2);
            floatingActionButton.offsetLeftAndRight(i);
        }
    }
}
