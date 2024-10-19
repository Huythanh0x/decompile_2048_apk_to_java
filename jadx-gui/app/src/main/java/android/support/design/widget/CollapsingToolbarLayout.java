package android.support.design.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.design.R;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.ValueAnimatorCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

/* loaded from: classes.dex */
public class CollapsingToolbarLayout extends FrameLayout {
    private static final int SCRIM_ANIMATION_DURATION = 600;
    private final CollapsingTextHelper mCollapsingTextHelper;
    private boolean mCollapsingTitleEnabled;
    private Drawable mContentScrim;
    private int mCurrentOffset;
    private boolean mDrawCollapsingTitle;
    private View mDummyView;
    private int mExpandedMarginBottom;
    private int mExpandedMarginLeft;
    private int mExpandedMarginRight;
    private int mExpandedMarginTop;
    private WindowInsetsCompat mLastInsets;
    private AppBarLayout.OnOffsetChangedListener mOnOffsetChangedListener;
    private boolean mRefreshToolbar;
    private int mScrimAlpha;
    private ValueAnimatorCompat mScrimAnimator;
    private boolean mScrimsAreShown;
    private Drawable mStatusBarScrim;
    private final Rect mTmpRect;
    private Toolbar mToolbar;
    private int mToolbarId;

    public CollapsingToolbarLayout(Context context) {
        this(context, null);
    }

    public CollapsingToolbarLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CollapsingToolbarLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mRefreshToolbar = true;
        this.mTmpRect = new Rect();
        ThemeUtils.checkAppCompatTheme(context);
        this.mCollapsingTextHelper = new CollapsingTextHelper(this);
        this.mCollapsingTextHelper.setTextSizeInterpolator(AnimationUtils.DECELERATE_INTERPOLATOR);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.CollapsingToolbarLayout, i, R.style.Widget_Design_CollapsingToolbar);
        this.mCollapsingTextHelper.setExpandedTextGravity(obtainStyledAttributes.getInt(R.styleable.CollapsingToolbarLayout_expandedTitleGravity, 8388691));
        this.mCollapsingTextHelper.setCollapsedTextGravity(obtainStyledAttributes.getInt(R.styleable.CollapsingToolbarLayout_collapsedTitleGravity, 8388627));
        int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(R.styleable.CollapsingToolbarLayout_expandedTitleMargin, 0);
        this.mExpandedMarginBottom = dimensionPixelSize;
        this.mExpandedMarginRight = dimensionPixelSize;
        this.mExpandedMarginTop = dimensionPixelSize;
        this.mExpandedMarginLeft = dimensionPixelSize;
        boolean z = ViewCompat.getLayoutDirection(this) == 1;
        if (obtainStyledAttributes.hasValue(R.styleable.CollapsingToolbarLayout_expandedTitleMarginStart)) {
            int dimensionPixelSize2 = obtainStyledAttributes.getDimensionPixelSize(R.styleable.CollapsingToolbarLayout_expandedTitleMarginStart, 0);
            if (z) {
                this.mExpandedMarginRight = dimensionPixelSize2;
            } else {
                this.mExpandedMarginLeft = dimensionPixelSize2;
            }
        }
        if (obtainStyledAttributes.hasValue(R.styleable.CollapsingToolbarLayout_expandedTitleMarginEnd)) {
            int dimensionPixelSize3 = obtainStyledAttributes.getDimensionPixelSize(R.styleable.CollapsingToolbarLayout_expandedTitleMarginEnd, 0);
            if (z) {
                this.mExpandedMarginLeft = dimensionPixelSize3;
            } else {
                this.mExpandedMarginRight = dimensionPixelSize3;
            }
        }
        if (obtainStyledAttributes.hasValue(R.styleable.CollapsingToolbarLayout_expandedTitleMarginTop)) {
            this.mExpandedMarginTop = obtainStyledAttributes.getDimensionPixelSize(R.styleable.CollapsingToolbarLayout_expandedTitleMarginTop, 0);
        }
        if (obtainStyledAttributes.hasValue(R.styleable.CollapsingToolbarLayout_expandedTitleMarginBottom)) {
            this.mExpandedMarginBottom = obtainStyledAttributes.getDimensionPixelSize(R.styleable.CollapsingToolbarLayout_expandedTitleMarginBottom, 0);
        }
        this.mCollapsingTitleEnabled = obtainStyledAttributes.getBoolean(R.styleable.CollapsingToolbarLayout_titleEnabled, true);
        setTitle(obtainStyledAttributes.getText(R.styleable.CollapsingToolbarLayout_title));
        this.mCollapsingTextHelper.setExpandedTextAppearance(R.style.TextAppearance_Design_CollapsingToolbar_Expanded);
        this.mCollapsingTextHelper.setCollapsedTextAppearance(R.style.TextAppearance_AppCompat_Widget_ActionBar_Title);
        if (obtainStyledAttributes.hasValue(R.styleable.CollapsingToolbarLayout_expandedTitleTextAppearance)) {
            this.mCollapsingTextHelper.setExpandedTextAppearance(obtainStyledAttributes.getResourceId(R.styleable.CollapsingToolbarLayout_expandedTitleTextAppearance, 0));
        }
        if (obtainStyledAttributes.hasValue(R.styleable.CollapsingToolbarLayout_collapsedTitleTextAppearance)) {
            this.mCollapsingTextHelper.setCollapsedTextAppearance(obtainStyledAttributes.getResourceId(R.styleable.CollapsingToolbarLayout_collapsedTitleTextAppearance, 0));
        }
        setContentScrim(obtainStyledAttributes.getDrawable(R.styleable.CollapsingToolbarLayout_contentScrim));
        setStatusBarScrim(obtainStyledAttributes.getDrawable(R.styleable.CollapsingToolbarLayout_statusBarScrim));
        this.mToolbarId = obtainStyledAttributes.getResourceId(R.styleable.CollapsingToolbarLayout_toolbarId, -1);
        obtainStyledAttributes.recycle();
        setWillNotDraw(false);
        ViewCompat.setOnApplyWindowInsetsListener(this, new OnApplyWindowInsetsListener() { // from class: android.support.design.widget.CollapsingToolbarLayout.1
            @Override // android.support.v4.view.OnApplyWindowInsetsListener
            public WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
                CollapsingToolbarLayout.this.mLastInsets = windowInsetsCompat;
                CollapsingToolbarLayout.this.requestLayout();
                return windowInsetsCompat.consumeSystemWindowInsets();
            }
        });
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewParent parent = getParent();
        if (parent instanceof AppBarLayout) {
            if (this.mOnOffsetChangedListener == null) {
                this.mOnOffsetChangedListener = new OffsetUpdateListener();
            }
            ((AppBarLayout) parent).addOnOffsetChangedListener(this.mOnOffsetChangedListener);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        ViewParent parent = getParent();
        if (this.mOnOffsetChangedListener != null && (parent instanceof AppBarLayout)) {
            ((AppBarLayout) parent).removeOnOffsetChangedListener(this.mOnOffsetChangedListener);
        }
        super.onDetachedFromWindow();
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        super.draw(canvas);
        ensureToolbar();
        if (this.mToolbar == null && this.mContentScrim != null && this.mScrimAlpha > 0) {
            this.mContentScrim.mutate().setAlpha(this.mScrimAlpha);
            this.mContentScrim.draw(canvas);
        }
        if (this.mCollapsingTitleEnabled && this.mDrawCollapsingTitle) {
            this.mCollapsingTextHelper.draw(canvas);
        }
        if (this.mStatusBarScrim == null || this.mScrimAlpha <= 0) {
            return;
        }
        int systemWindowInsetTop = this.mLastInsets != null ? this.mLastInsets.getSystemWindowInsetTop() : 0;
        if (systemWindowInsetTop > 0) {
            this.mStatusBarScrim.setBounds(0, -this.mCurrentOffset, getWidth(), systemWindowInsetTop - this.mCurrentOffset);
            this.mStatusBarScrim.mutate().setAlpha(this.mScrimAlpha);
            this.mStatusBarScrim.draw(canvas);
        }
    }

    @Override // android.view.ViewGroup
    protected boolean drawChild(Canvas canvas, View view, long j) {
        ensureToolbar();
        if (view == this.mToolbar && this.mContentScrim != null && this.mScrimAlpha > 0) {
            this.mContentScrim.mutate().setAlpha(this.mScrimAlpha);
            this.mContentScrim.draw(canvas);
        }
        return super.drawChild(canvas, view, j);
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (this.mContentScrim != null) {
            this.mContentScrim.setBounds(0, 0, i, i2);
        }
    }

    private void ensureToolbar() {
        if (this.mRefreshToolbar) {
            int childCount = getChildCount();
            Toolbar toolbar = null;
            Toolbar toolbar2 = null;
            int i = 0;
            while (true) {
                if (i >= childCount) {
                    break;
                }
                View childAt = getChildAt(i);
                if (childAt instanceof Toolbar) {
                    if (this.mToolbarId != -1) {
                        if (this.mToolbarId == childAt.getId()) {
                            toolbar = (Toolbar) childAt;
                            break;
                        } else if (toolbar2 == null) {
                            toolbar2 = (Toolbar) childAt;
                        }
                    } else {
                        toolbar = (Toolbar) childAt;
                        break;
                    }
                }
                i++;
            }
            if (toolbar == null) {
                toolbar = toolbar2;
            }
            this.mToolbar = toolbar;
            updateDummyView();
            this.mRefreshToolbar = false;
        }
    }

    private void updateDummyView() {
        if (!this.mCollapsingTitleEnabled && this.mDummyView != null) {
            ViewParent parent = this.mDummyView.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(this.mDummyView);
            }
        }
        if (!this.mCollapsingTitleEnabled || this.mToolbar == null) {
            return;
        }
        if (this.mDummyView == null) {
            this.mDummyView = new View(getContext());
        }
        if (this.mDummyView.getParent() == null) {
            this.mToolbar.addView(this.mDummyView, -1, -1);
        }
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        ensureToolbar();
        super.onMeasure(i, i2);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int systemWindowInsetTop;
        super.onLayout(z, i, i2, i3, i4);
        if (this.mCollapsingTitleEnabled && this.mDummyView != null) {
            this.mDrawCollapsingTitle = this.mDummyView.isShown();
            if (this.mDrawCollapsingTitle) {
                ViewGroupUtils.getDescendantRect(this, this.mDummyView, this.mTmpRect);
                this.mCollapsingTextHelper.setCollapsedBounds(this.mTmpRect.left, i4 - this.mTmpRect.height(), this.mTmpRect.right, i4);
                this.mCollapsingTextHelper.setExpandedBounds(this.mExpandedMarginLeft, this.mTmpRect.bottom + this.mExpandedMarginTop, (i3 - i) - this.mExpandedMarginRight, (i4 - i2) - this.mExpandedMarginBottom);
                this.mCollapsingTextHelper.recalculate();
            }
        }
        int childCount = getChildCount();
        for (int i5 = 0; i5 < childCount; i5++) {
            View childAt = getChildAt(i5);
            if (this.mLastInsets != null && !ViewCompat.getFitsSystemWindows(childAt) && childAt.getTop() < (systemWindowInsetTop = this.mLastInsets.getSystemWindowInsetTop())) {
                childAt.offsetTopAndBottom(systemWindowInsetTop);
            }
            getViewOffsetHelper(childAt).onViewLayout();
        }
        if (this.mToolbar != null) {
            if (this.mCollapsingTitleEnabled && TextUtils.isEmpty(this.mCollapsingTextHelper.getText())) {
                this.mCollapsingTextHelper.setText(this.mToolbar.getTitle());
            }
            setMinimumHeight(this.mToolbar.getHeight());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static ViewOffsetHelper getViewOffsetHelper(View view) {
        ViewOffsetHelper viewOffsetHelper = (ViewOffsetHelper) view.getTag(R.id.view_offset_helper);
        if (viewOffsetHelper != null) {
            return viewOffsetHelper;
        }
        ViewOffsetHelper viewOffsetHelper2 = new ViewOffsetHelper(view);
        view.setTag(R.id.view_offset_helper, viewOffsetHelper2);
        return viewOffsetHelper2;
    }

    public void setTitle(@Nullable CharSequence charSequence) {
        this.mCollapsingTextHelper.setText(charSequence);
    }

    @Nullable
    public CharSequence getTitle() {
        if (this.mCollapsingTitleEnabled) {
            return this.mCollapsingTextHelper.getText();
        }
        return null;
    }

    public void setTitleEnabled(boolean z) {
        if (z != this.mCollapsingTitleEnabled) {
            this.mCollapsingTitleEnabled = z;
            updateDummyView();
            requestLayout();
        }
    }

    public boolean isTitleEnabled() {
        return this.mCollapsingTitleEnabled;
    }

    public void setScrimsShown(boolean z) {
        setScrimsShown(z, ViewCompat.isLaidOut(this) && !isInEditMode());
    }

    public void setScrimsShown(boolean z, boolean z2) {
        if (this.mScrimsAreShown != z) {
            if (z2) {
                animateScrim(z ? 255 : 0);
            } else {
                setScrimAlpha(z ? 255 : 0);
            }
            this.mScrimsAreShown = z;
        }
    }

    private void animateScrim(int i) {
        ensureToolbar();
        if (this.mScrimAnimator == null) {
            this.mScrimAnimator = ViewUtils.createAnimator();
            this.mScrimAnimator.setDuration(SCRIM_ANIMATION_DURATION);
            this.mScrimAnimator.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
            this.mScrimAnimator.setUpdateListener(new ValueAnimatorCompat.AnimatorUpdateListener() { // from class: android.support.design.widget.CollapsingToolbarLayout.2
                @Override // android.support.design.widget.ValueAnimatorCompat.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimatorCompat valueAnimatorCompat) {
                    CollapsingToolbarLayout.this.setScrimAlpha(valueAnimatorCompat.getAnimatedIntValue());
                }
            });
        } else if (this.mScrimAnimator.isRunning()) {
            this.mScrimAnimator.cancel();
        }
        this.mScrimAnimator.setIntValues(this.mScrimAlpha, i);
        this.mScrimAnimator.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setScrimAlpha(int i) {
        if (i != this.mScrimAlpha) {
            if (this.mContentScrim != null && this.mToolbar != null) {
                ViewCompat.postInvalidateOnAnimation(this.mToolbar);
            }
            this.mScrimAlpha = i;
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void setContentScrim(@Nullable Drawable drawable) {
        if (this.mContentScrim != drawable) {
            if (this.mContentScrim != null) {
                this.mContentScrim.setCallback(null);
            }
            if (drawable != null) {
                this.mContentScrim = drawable.mutate();
                drawable.setBounds(0, 0, getWidth(), getHeight());
                drawable.setCallback(this);
                drawable.setAlpha(this.mScrimAlpha);
            } else {
                this.mContentScrim = null;
            }
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void setContentScrimColor(@ColorInt int i) {
        setContentScrim(new ColorDrawable(i));
    }

    public void setContentScrimResource(@DrawableRes int i) {
        setContentScrim(ContextCompat.getDrawable(getContext(), i));
    }

    public Drawable getContentScrim() {
        return this.mContentScrim;
    }

    public void setStatusBarScrim(@Nullable Drawable drawable) {
        if (this.mStatusBarScrim != drawable) {
            if (this.mStatusBarScrim != null) {
                this.mStatusBarScrim.setCallback(null);
            }
            this.mStatusBarScrim = drawable;
            drawable.setCallback(this);
            drawable.mutate().setAlpha(this.mScrimAlpha);
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void setStatusBarScrimColor(@ColorInt int i) {
        setStatusBarScrim(new ColorDrawable(i));
    }

    public void setStatusBarScrimResource(@DrawableRes int i) {
        setStatusBarScrim(ContextCompat.getDrawable(getContext(), i));
    }

    public Drawable getStatusBarScrim() {
        return this.mStatusBarScrim;
    }

    public void setCollapsedTitleTextAppearance(@StyleRes int i) {
        this.mCollapsingTextHelper.setCollapsedTextAppearance(i);
    }

    public void setCollapsedTitleTextColor(@ColorInt int i) {
        this.mCollapsingTextHelper.setCollapsedTextColor(i);
    }

    public void setCollapsedTitleGravity(int i) {
        this.mCollapsingTextHelper.setCollapsedTextGravity(i);
    }

    public int getCollapsedTitleGravity() {
        return this.mCollapsingTextHelper.getCollapsedTextGravity();
    }

    public void setExpandedTitleTextAppearance(@StyleRes int i) {
        this.mCollapsingTextHelper.setExpandedTextAppearance(i);
    }

    public void setExpandedTitleColor(@ColorInt int i) {
        this.mCollapsingTextHelper.setExpandedTextColor(i);
    }

    public void setExpandedTitleGravity(int i) {
        this.mCollapsingTextHelper.setExpandedTextGravity(i);
    }

    public int getExpandedTitleGravity() {
        return this.mCollapsingTextHelper.getExpandedTextGravity();
    }

    public void setCollapsedTitleTypeface(@Nullable Typeface typeface) {
        this.mCollapsingTextHelper.setCollapsedTypeface(typeface);
    }

    @NonNull
    public Typeface getCollapsedTitleTypeface() {
        return this.mCollapsingTextHelper.getCollapsedTypeface();
    }

    public void setExpandedTitleTypeface(@Nullable Typeface typeface) {
        this.mCollapsingTextHelper.setExpandedTypeface(typeface);
    }

    @NonNull
    public Typeface getExpandedTitleTypeface() {
        return this.mCollapsingTextHelper.getExpandedTypeface();
    }

    final int getScrimTriggerOffset() {
        return ViewCompat.getMinimumHeight(this) * 2;
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup
    protected boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.FrameLayout, android.view.ViewGroup
    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(super.generateDefaultLayoutParams());
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup
    public FrameLayout.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.FrameLayout, android.view.ViewGroup
    public FrameLayout.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return new LayoutParams(layoutParams);
    }

    /* loaded from: classes.dex */
    public static class LayoutParams extends FrameLayout.LayoutParams {
        public static final int COLLAPSE_MODE_OFF = 0;
        public static final int COLLAPSE_MODE_PARALLAX = 2;
        public static final int COLLAPSE_MODE_PIN = 1;
        private static final float DEFAULT_PARALLAX_MULTIPLIER = 0.5f;
        int mCollapseMode;
        float mParallaxMult;

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            this.mCollapseMode = 0;
            this.mParallaxMult = DEFAULT_PARALLAX_MULTIPLIER;
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.CollapsingAppBarLayout_LayoutParams);
            this.mCollapseMode = obtainStyledAttributes.getInt(R.styleable.CollapsingAppBarLayout_LayoutParams_layout_collapseMode, 0);
            setParallaxMultiplier(obtainStyledAttributes.getFloat(R.styleable.CollapsingAppBarLayout_LayoutParams_layout_collapseParallaxMultiplier, DEFAULT_PARALLAX_MULTIPLIER));
            obtainStyledAttributes.recycle();
        }

        public LayoutParams(int i, int i2) {
            super(i, i2);
            this.mCollapseMode = 0;
            this.mParallaxMult = DEFAULT_PARALLAX_MULTIPLIER;
        }

        public LayoutParams(int i, int i2, int i3) {
            super(i, i2, i3);
            this.mCollapseMode = 0;
            this.mParallaxMult = DEFAULT_PARALLAX_MULTIPLIER;
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
            this.mCollapseMode = 0;
            this.mParallaxMult = DEFAULT_PARALLAX_MULTIPLIER;
        }

        public LayoutParams(ViewGroup.MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
            this.mCollapseMode = 0;
            this.mParallaxMult = DEFAULT_PARALLAX_MULTIPLIER;
        }

        public LayoutParams(FrameLayout.LayoutParams layoutParams) {
            super(layoutParams);
            this.mCollapseMode = 0;
            this.mParallaxMult = DEFAULT_PARALLAX_MULTIPLIER;
        }

        public void setCollapseMode(int i) {
            this.mCollapseMode = i;
        }

        public int getCollapseMode() {
            return this.mCollapseMode;
        }

        public void setParallaxMultiplier(float f) {
            this.mParallaxMult = f;
        }

        public float getParallaxMultiplier() {
            return this.mParallaxMult;
        }
    }

    /* loaded from: classes.dex */
    private class OffsetUpdateListener implements AppBarLayout.OnOffsetChangedListener {
        private OffsetUpdateListener() {
        }

        @Override // android.support.design.widget.AppBarLayout.OnOffsetChangedListener
        public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
            CollapsingToolbarLayout.this.mCurrentOffset = i;
            int systemWindowInsetTop = CollapsingToolbarLayout.this.mLastInsets != null ? CollapsingToolbarLayout.this.mLastInsets.getSystemWindowInsetTop() : 0;
            int totalScrollRange = appBarLayout.getTotalScrollRange();
            int childCount = CollapsingToolbarLayout.this.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = CollapsingToolbarLayout.this.getChildAt(i2);
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                ViewOffsetHelper viewOffsetHelper = CollapsingToolbarLayout.getViewOffsetHelper(childAt);
                switch (layoutParams.mCollapseMode) {
                    case 1:
                        if ((CollapsingToolbarLayout.this.getHeight() - systemWindowInsetTop) + i >= childAt.getHeight()) {
                            viewOffsetHelper.setTopAndBottomOffset(-i);
                            break;
                        } else {
                            break;
                        }
                    case 2:
                        viewOffsetHelper.setTopAndBottomOffset(Math.round((-i) * layoutParams.mParallaxMult));
                        break;
                }
            }
            if (CollapsingToolbarLayout.this.mContentScrim != null || CollapsingToolbarLayout.this.mStatusBarScrim != null) {
                CollapsingToolbarLayout.this.setScrimsShown(CollapsingToolbarLayout.this.getHeight() + i < CollapsingToolbarLayout.this.getScrimTriggerOffset() + systemWindowInsetTop);
            }
            if (CollapsingToolbarLayout.this.mStatusBarScrim != null && systemWindowInsetTop > 0) {
                ViewCompat.postInvalidateOnAnimation(CollapsingToolbarLayout.this);
            }
            CollapsingToolbarLayout.this.mCollapsingTextHelper.setExpansionFraction(Math.abs(i) / ((CollapsingToolbarLayout.this.getHeight() - ViewCompat.getMinimumHeight(CollapsingToolbarLayout.this)) - systemWindowInsetTop));
            if (Math.abs(i) == totalScrollRange) {
                ViewCompat.setElevation(appBarLayout, appBarLayout.getTargetElevation());
            } else {
                ViewCompat.setElevation(appBarLayout, 0.0f);
            }
        }
    }
}
