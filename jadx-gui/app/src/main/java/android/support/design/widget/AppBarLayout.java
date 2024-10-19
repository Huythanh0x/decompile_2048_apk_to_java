package android.support.design.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.R;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.ValueAnimatorCompat;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

@CoordinatorLayout.DefaultBehavior(Behavior.class)
/* loaded from: classes.dex */
public class AppBarLayout extends LinearLayout {
    private static final int INVALID_SCROLL_RANGE = -1;
    private static final int PENDING_ACTION_ANIMATE_ENABLED = 4;
    private static final int PENDING_ACTION_COLLAPSED = 2;
    private static final int PENDING_ACTION_EXPANDED = 1;
    private static final int PENDING_ACTION_NONE = 0;
    private int mDownPreScrollRange;
    private int mDownScrollRange;
    boolean mHaveChildWithInterpolator;
    private WindowInsetsCompat mLastInsets;
    private final List<OnOffsetChangedListener> mListeners;
    private int mPendingAction;
    private float mTargetElevation;
    private int mTotalScrollRange;

    /* loaded from: classes.dex */
    public interface OnOffsetChangedListener {
        void onOffsetChanged(AppBarLayout appBarLayout, int i);
    }

    public AppBarLayout(Context context) {
        this(context, null);
    }

    public AppBarLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mTotalScrollRange = -1;
        this.mDownPreScrollRange = -1;
        this.mDownScrollRange = -1;
        this.mPendingAction = 0;
        setOrientation(1);
        ThemeUtils.checkAppCompatTheme(context);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.AppBarLayout, 0, R.style.Widget_Design_AppBarLayout);
        this.mTargetElevation = obtainStyledAttributes.getDimensionPixelSize(R.styleable.AppBarLayout_elevation, 0);
        setBackgroundDrawable(obtainStyledAttributes.getDrawable(R.styleable.AppBarLayout_android_background));
        if (obtainStyledAttributes.hasValue(R.styleable.AppBarLayout_expanded)) {
            setExpanded(obtainStyledAttributes.getBoolean(R.styleable.AppBarLayout_expanded, false));
        }
        obtainStyledAttributes.recycle();
        ViewUtils.setBoundsViewOutlineProvider(this);
        this.mListeners = new ArrayList();
        ViewCompat.setElevation(this, this.mTargetElevation);
        ViewCompat.setOnApplyWindowInsetsListener(this, new OnApplyWindowInsetsListener() { // from class: android.support.design.widget.AppBarLayout.1
            @Override // android.support.v4.view.OnApplyWindowInsetsListener
            public WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
                AppBarLayout.this.setWindowInsets(windowInsetsCompat);
                return windowInsetsCompat.consumeSystemWindowInsets();
            }
        });
    }

    public void addOnOffsetChangedListener(OnOffsetChangedListener onOffsetChangedListener) {
        if (onOffsetChangedListener == null || this.mListeners.contains(onOffsetChangedListener)) {
            return;
        }
        this.mListeners.add(onOffsetChangedListener);
    }

    public void removeOnOffsetChangedListener(OnOffsetChangedListener onOffsetChangedListener) {
        if (onOffsetChangedListener != null) {
            this.mListeners.remove(onOffsetChangedListener);
        }
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        invalidateScrollRanges();
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        invalidateScrollRanges();
        this.mHaveChildWithInterpolator = false;
        int childCount = getChildCount();
        for (int i5 = 0; i5 < childCount; i5++) {
            if (((LayoutParams) getChildAt(i5).getLayoutParams()).getScrollInterpolator() != null) {
                this.mHaveChildWithInterpolator = true;
                return;
            }
        }
    }

    private void invalidateScrollRanges() {
        this.mTotalScrollRange = -1;
        this.mDownPreScrollRange = -1;
        this.mDownScrollRange = -1;
    }

    @Override // android.widget.LinearLayout
    public void setOrientation(int i) {
        if (i != 1) {
            throw new IllegalArgumentException("AppBarLayout is always vertical and does not support horizontal orientation");
        }
        super.setOrientation(i);
    }

    public void setExpanded(boolean z) {
        setExpanded(z, ViewCompat.isLaidOut(this));
    }

    public void setExpanded(boolean z, boolean z2) {
        this.mPendingAction = (z ? 1 : 2) | (z2 ? 4 : 0);
        requestLayout();
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup
    protected boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.ViewGroup
    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-1, -2);
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.ViewGroup
    public LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        if (layoutParams instanceof LinearLayout.LayoutParams) {
            return new LayoutParams((LinearLayout.LayoutParams) layoutParams);
        }
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            return new LayoutParams((ViewGroup.MarginLayoutParams) layoutParams);
        }
        return new LayoutParams(layoutParams);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean hasChildWithInterpolator() {
        return this.mHaveChildWithInterpolator;
    }

    public final int getTotalScrollRange() {
        if (this.mTotalScrollRange != -1) {
            return this.mTotalScrollRange;
        }
        int childCount = getChildCount();
        int i = 0;
        int i2 = 0;
        while (true) {
            if (i >= childCount) {
                break;
            }
            View childAt = getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
            int measuredHeight = childAt.getMeasuredHeight();
            int i3 = layoutParams.mScrollFlags;
            if ((i3 & 1) == 0) {
                break;
            }
            i2 += measuredHeight + layoutParams.topMargin + layoutParams.bottomMargin;
            if ((i3 & 2) != 0) {
                i2 -= ViewCompat.getMinimumHeight(childAt);
                break;
            }
            i++;
        }
        int max = Math.max(0, i2 - getTopInset());
        this.mTotalScrollRange = max;
        return max;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean hasScrollableChildren() {
        return getTotalScrollRange() != 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getUpNestedPreScrollRange() {
        return getTotalScrollRange();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getDownNestedPreScrollRange() {
        if (this.mDownPreScrollRange != -1) {
            return this.mDownPreScrollRange;
        }
        int i = 0;
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            View childAt = getChildAt(childCount);
            LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
            int measuredHeight = childAt.getMeasuredHeight();
            int i2 = layoutParams.mScrollFlags;
            if ((i2 & 5) != 5) {
                if (i > 0) {
                    break;
                }
            } else {
                int i3 = i + layoutParams.topMargin + layoutParams.bottomMargin;
                i = (i2 & 8) != 0 ? i3 + ViewCompat.getMinimumHeight(childAt) : i3 + measuredHeight;
            }
        }
        this.mDownPreScrollRange = i;
        return i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getDownNestedScrollRange() {
        if (this.mDownScrollRange != -1) {
            return this.mDownScrollRange;
        }
        int childCount = getChildCount();
        int i = 0;
        int i2 = 0;
        while (true) {
            if (i >= childCount) {
                break;
            }
            View childAt = getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
            int measuredHeight = childAt.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;
            int i3 = layoutParams.mScrollFlags;
            if ((i3 & 1) == 0) {
                break;
            }
            i2 += measuredHeight;
            if ((i3 & 2) != 0) {
                i2 -= ViewCompat.getMinimumHeight(childAt) + getTopInset();
                break;
            }
            i++;
        }
        int max = Math.max(0, i2);
        this.mDownScrollRange = max;
        return max;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int getMinimumHeightForVisibleOverlappingContent() {
        int systemWindowInsetTop = this.mLastInsets != null ? this.mLastInsets.getSystemWindowInsetTop() : 0;
        int minimumHeight = ViewCompat.getMinimumHeight(this);
        if (minimumHeight != 0) {
            return (minimumHeight * 2) + systemWindowInsetTop;
        }
        int childCount = getChildCount();
        if (childCount >= 1) {
            return (ViewCompat.getMinimumHeight(getChildAt(childCount - 1)) * 2) + systemWindowInsetTop;
        }
        return 0;
    }

    public void setTargetElevation(float f) {
        this.mTargetElevation = f;
    }

    public float getTargetElevation() {
        return this.mTargetElevation;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getPendingAction() {
        return this.mPendingAction;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetPendingAction() {
        this.mPendingAction = 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getTopInset() {
        if (this.mLastInsets != null) {
            return this.mLastInsets.getSystemWindowInsetTop();
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setWindowInsets(WindowInsetsCompat windowInsetsCompat) {
        this.mTotalScrollRange = -1;
        this.mLastInsets = windowInsetsCompat;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            windowInsetsCompat = ViewCompat.dispatchApplyWindowInsets(getChildAt(i), windowInsetsCompat);
            if (windowInsetsCompat.isConsumed()) {
                return;
            }
        }
    }

    /* loaded from: classes.dex */
    public static class LayoutParams extends LinearLayout.LayoutParams {
        static final int FLAG_QUICK_RETURN = 5;
        static final int FLAG_SNAP = 17;
        public static final int SCROLL_FLAG_ENTER_ALWAYS = 4;
        public static final int SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED = 8;
        public static final int SCROLL_FLAG_EXIT_UNTIL_COLLAPSED = 2;
        public static final int SCROLL_FLAG_SCROLL = 1;
        public static final int SCROLL_FLAG_SNAP = 16;
        int mScrollFlags;
        Interpolator mScrollInterpolator;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes.dex */
        public @interface ScrollFlags {
        }

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            this.mScrollFlags = 1;
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.AppBarLayout_LayoutParams);
            this.mScrollFlags = obtainStyledAttributes.getInt(R.styleable.AppBarLayout_LayoutParams_layout_scrollFlags, 0);
            if (obtainStyledAttributes.hasValue(R.styleable.AppBarLayout_LayoutParams_layout_scrollInterpolator)) {
                this.mScrollInterpolator = android.view.animation.AnimationUtils.loadInterpolator(context, obtainStyledAttributes.getResourceId(R.styleable.AppBarLayout_LayoutParams_layout_scrollInterpolator, 0));
            }
            obtainStyledAttributes.recycle();
        }

        public LayoutParams(int i, int i2) {
            super(i, i2);
            this.mScrollFlags = 1;
        }

        public LayoutParams(int i, int i2, float f) {
            super(i, i2, f);
            this.mScrollFlags = 1;
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
            this.mScrollFlags = 1;
        }

        public LayoutParams(ViewGroup.MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
            this.mScrollFlags = 1;
        }

        public LayoutParams(LinearLayout.LayoutParams layoutParams) {
            super(layoutParams);
            this.mScrollFlags = 1;
        }

        public LayoutParams(LayoutParams layoutParams) {
            super((LinearLayout.LayoutParams) layoutParams);
            this.mScrollFlags = 1;
            this.mScrollFlags = layoutParams.mScrollFlags;
            this.mScrollInterpolator = layoutParams.mScrollInterpolator;
        }

        public void setScrollFlags(int i) {
            this.mScrollFlags = i;
        }

        public int getScrollFlags() {
            return this.mScrollFlags;
        }

        public void setScrollInterpolator(Interpolator interpolator) {
            this.mScrollInterpolator = interpolator;
        }

        public Interpolator getScrollInterpolator() {
            return this.mScrollInterpolator;
        }
    }

    /* loaded from: classes.dex */
    public static class Behavior extends HeaderBehavior<AppBarLayout> {
        private static final int ANIMATE_OFFSET_DIPS_PER_SECOND = 300;
        private static final int INVALID_POSITION = -1;
        private ValueAnimatorCompat mAnimator;
        private WeakReference<View> mLastNestedScrollingChildRef;
        private int mOffsetDelta;
        private int mOffsetToChildIndexOnLayout;
        private boolean mOffsetToChildIndexOnLayoutIsMinHeight;
        private float mOffsetToChildIndexOnLayoutPerc;
        private DragCallback mOnDragCallback;
        private boolean mSkipNestedPreScroll;
        private boolean mWasFlung;

        /* loaded from: classes.dex */
        public static abstract class DragCallback {
            public abstract boolean canDrag(@NonNull AppBarLayout appBarLayout);
        }

        @Override // android.support.design.widget.ViewOffsetBehavior
        public /* bridge */ /* synthetic */ int getLeftAndRightOffset() {
            return super.getLeftAndRightOffset();
        }

        @Override // android.support.design.widget.ViewOffsetBehavior
        public /* bridge */ /* synthetic */ int getTopAndBottomOffset() {
            return super.getTopAndBottomOffset();
        }

        @Override // android.support.design.widget.ViewOffsetBehavior
        public /* bridge */ /* synthetic */ boolean setLeftAndRightOffset(int i) {
            return super.setLeftAndRightOffset(i);
        }

        @Override // android.support.design.widget.ViewOffsetBehavior
        public /* bridge */ /* synthetic */ boolean setTopAndBottomOffset(int i) {
            return super.setTopAndBottomOffset(i);
        }

        public Behavior() {
            this.mOffsetToChildIndexOnLayout = -1;
        }

        public Behavior(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            this.mOffsetToChildIndexOnLayout = -1;
        }

        @Override // android.support.design.widget.CoordinatorLayout.Behavior
        public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout appBarLayout, View view, View view2, int i) {
            boolean z = (i & 2) != 0 && appBarLayout.hasScrollableChildren() && coordinatorLayout.getHeight() - view.getHeight() <= appBarLayout.getHeight();
            if (z && this.mAnimator != null) {
                this.mAnimator.cancel();
            }
            this.mLastNestedScrollingChildRef = null;
            return z;
        }

        @Override // android.support.design.widget.CoordinatorLayout.Behavior
        public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout appBarLayout, View view, int i, int i2, int[] iArr) {
            int i3;
            int i4;
            if (i2 == 0 || this.mSkipNestedPreScroll) {
                return;
            }
            if (i2 >= 0) {
                i3 = -appBarLayout.getUpNestedPreScrollRange();
                i4 = 0;
            } else {
                int i5 = -appBarLayout.getTotalScrollRange();
                i3 = i5;
                i4 = appBarLayout.getDownNestedPreScrollRange() + i5;
            }
            iArr[1] = scroll(coordinatorLayout, appBarLayout, i2, i3, i4);
        }

        @Override // android.support.design.widget.CoordinatorLayout.Behavior
        public void onNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout appBarLayout, View view, int i, int i2, int i3, int i4) {
            if (i4 < 0) {
                scroll(coordinatorLayout, appBarLayout, i4, -appBarLayout.getDownNestedScrollRange(), 0);
                this.mSkipNestedPreScroll = true;
            } else {
                this.mSkipNestedPreScroll = false;
            }
        }

        @Override // android.support.design.widget.CoordinatorLayout.Behavior
        public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout appBarLayout, View view) {
            if (!this.mWasFlung) {
                snapToChildIfNeeded(coordinatorLayout, appBarLayout);
            }
            this.mSkipNestedPreScroll = false;
            this.mWasFlung = false;
            this.mLastNestedScrollingChildRef = new WeakReference<>(view);
        }

        @Override // android.support.design.widget.CoordinatorLayout.Behavior
        public boolean onNestedFling(CoordinatorLayout coordinatorLayout, AppBarLayout appBarLayout, View view, float f, float f2, boolean z) {
            boolean z2 = true;
            if (!z) {
                z2 = fling(coordinatorLayout, appBarLayout, -appBarLayout.getTotalScrollRange(), 0, -f2);
            } else if (f2 < 0.0f) {
                int downNestedPreScrollRange = (-appBarLayout.getTotalScrollRange()) + appBarLayout.getDownNestedPreScrollRange();
                if (getTopBottomOffsetForScrollingSibling() < downNestedPreScrollRange) {
                    animateOffsetTo(coordinatorLayout, appBarLayout, downNestedPreScrollRange);
                }
                z2 = false;
            } else {
                int i = -appBarLayout.getUpNestedPreScrollRange();
                if (getTopBottomOffsetForScrollingSibling() > i) {
                    animateOffsetTo(coordinatorLayout, appBarLayout, i);
                }
                z2 = false;
            }
            this.mWasFlung = z2;
            return z2;
        }

        public void setDragCallback(@Nullable DragCallback dragCallback) {
            this.mOnDragCallback = dragCallback;
        }

        private void animateOffsetTo(final CoordinatorLayout coordinatorLayout, final AppBarLayout appBarLayout, int i) {
            int topBottomOffsetForScrollingSibling = getTopBottomOffsetForScrollingSibling();
            if (topBottomOffsetForScrollingSibling == i) {
                if (this.mAnimator == null || !this.mAnimator.isRunning()) {
                    return;
                }
                this.mAnimator.cancel();
                return;
            }
            if (this.mAnimator == null) {
                this.mAnimator = ViewUtils.createAnimator();
                this.mAnimator.setInterpolator(AnimationUtils.DECELERATE_INTERPOLATOR);
                this.mAnimator.setUpdateListener(new ValueAnimatorCompat.AnimatorUpdateListener() { // from class: android.support.design.widget.AppBarLayout.Behavior.1
                    @Override // android.support.design.widget.ValueAnimatorCompat.AnimatorUpdateListener
                    public void onAnimationUpdate(ValueAnimatorCompat valueAnimatorCompat) {
                        Behavior.this.setHeaderTopBottomOffset(coordinatorLayout, appBarLayout, valueAnimatorCompat.getAnimatedIntValue());
                    }
                });
            } else {
                this.mAnimator.cancel();
            }
            this.mAnimator.setDuration(Math.round(((Math.abs(topBottomOffsetForScrollingSibling - i) / coordinatorLayout.getResources().getDisplayMetrics().density) * 1000.0f) / 300.0f));
            this.mAnimator.setIntValues(topBottomOffsetForScrollingSibling, i);
            this.mAnimator.start();
        }

        private View getChildOnOffset(AppBarLayout appBarLayout, int i) {
            int childCount = appBarLayout.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = appBarLayout.getChildAt(i2);
                int i3 = -i;
                if (childAt.getTop() <= i3 && childAt.getBottom() >= i3) {
                    return childAt;
                }
            }
            return null;
        }

        private void snapToChildIfNeeded(CoordinatorLayout coordinatorLayout, AppBarLayout appBarLayout) {
            int topBottomOffsetForScrollingSibling = getTopBottomOffsetForScrollingSibling();
            View childOnOffset = getChildOnOffset(appBarLayout, topBottomOffsetForScrollingSibling);
            if (childOnOffset != null) {
                LayoutParams layoutParams = (LayoutParams) childOnOffset.getLayoutParams();
                if ((layoutParams.getScrollFlags() & 17) == 17) {
                    int i = -childOnOffset.getTop();
                    int i2 = -childOnOffset.getBottom();
                    if ((layoutParams.getScrollFlags() & 2) == 2) {
                        i2 += ViewCompat.getMinimumHeight(childOnOffset);
                    }
                    if (topBottomOffsetForScrollingSibling < (i2 + i) / 2) {
                        i = i2;
                    }
                    animateOffsetTo(coordinatorLayout, appBarLayout, MathUtils.constrain(i, -appBarLayout.getTotalScrollRange(), 0));
                }
            }
        }

        @Override // android.support.design.widget.ViewOffsetBehavior, android.support.design.widget.CoordinatorLayout.Behavior
        public boolean onLayoutChild(CoordinatorLayout coordinatorLayout, AppBarLayout appBarLayout, int i) {
            int round;
            boolean onLayoutChild = super.onLayoutChild(coordinatorLayout, (CoordinatorLayout) appBarLayout, i);
            int pendingAction = appBarLayout.getPendingAction();
            if (pendingAction != 0) {
                boolean z = (pendingAction & 4) != 0;
                if ((pendingAction & 2) != 0) {
                    int i2 = -appBarLayout.getUpNestedPreScrollRange();
                    if (z) {
                        animateOffsetTo(coordinatorLayout, appBarLayout, i2);
                    } else {
                        setHeaderTopBottomOffset(coordinatorLayout, appBarLayout, i2);
                    }
                } else if ((pendingAction & 1) != 0) {
                    if (z) {
                        animateOffsetTo(coordinatorLayout, appBarLayout, 0);
                    } else {
                        setHeaderTopBottomOffset(coordinatorLayout, appBarLayout, 0);
                    }
                }
            } else if (this.mOffsetToChildIndexOnLayout >= 0) {
                View childAt = appBarLayout.getChildAt(this.mOffsetToChildIndexOnLayout);
                int i3 = -childAt.getBottom();
                if (this.mOffsetToChildIndexOnLayoutIsMinHeight) {
                    round = i3 + ViewCompat.getMinimumHeight(childAt);
                } else {
                    round = i3 + Math.round(childAt.getHeight() * this.mOffsetToChildIndexOnLayoutPerc);
                }
                setTopAndBottomOffset(round);
            }
            appBarLayout.resetPendingAction();
            this.mOffsetToChildIndexOnLayout = -1;
            dispatchOffsetUpdates(appBarLayout);
            return onLayoutChild;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // android.support.design.widget.HeaderBehavior
        public boolean canDragView(AppBarLayout appBarLayout) {
            if (this.mOnDragCallback != null) {
                return this.mOnDragCallback.canDrag(appBarLayout);
            }
            if (this.mLastNestedScrollingChildRef == null) {
                return true;
            }
            View view = this.mLastNestedScrollingChildRef.get();
            return (view == null || !view.isShown() || ViewCompat.canScrollVertically(view, -1)) ? false : true;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // android.support.design.widget.HeaderBehavior
        public int getMaxDragOffset(AppBarLayout appBarLayout) {
            return -appBarLayout.getDownNestedScrollRange();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // android.support.design.widget.HeaderBehavior
        public int getScrollRangeForDragFling(AppBarLayout appBarLayout) {
            return appBarLayout.getTotalScrollRange();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // android.support.design.widget.HeaderBehavior
        public int setHeaderTopBottomOffset(CoordinatorLayout coordinatorLayout, AppBarLayout appBarLayout, int i, int i2, int i3) {
            int constrain;
            int topBottomOffsetForScrollingSibling = getTopBottomOffsetForScrollingSibling();
            if (i2 == 0 || topBottomOffsetForScrollingSibling < i2 || topBottomOffsetForScrollingSibling > i3 || topBottomOffsetForScrollingSibling == (constrain = MathUtils.constrain(i, i2, i3))) {
                return 0;
            }
            int interpolateOffset = appBarLayout.hasChildWithInterpolator() ? interpolateOffset(appBarLayout, constrain) : constrain;
            boolean topAndBottomOffset = setTopAndBottomOffset(interpolateOffset);
            int i4 = topBottomOffsetForScrollingSibling - constrain;
            this.mOffsetDelta = constrain - interpolateOffset;
            if (!topAndBottomOffset && appBarLayout.hasChildWithInterpolator()) {
                coordinatorLayout.dispatchDependentViewsChanged(appBarLayout);
            }
            dispatchOffsetUpdates(appBarLayout);
            return i4;
        }

        private void dispatchOffsetUpdates(AppBarLayout appBarLayout) {
            List list = appBarLayout.mListeners;
            int size = list.size();
            for (int i = 0; i < size; i++) {
                OnOffsetChangedListener onOffsetChangedListener = (OnOffsetChangedListener) list.get(i);
                if (onOffsetChangedListener != null) {
                    onOffsetChangedListener.onOffsetChanged(appBarLayout, getTopAndBottomOffset());
                }
            }
        }

        private int interpolateOffset(AppBarLayout appBarLayout, int i) {
            int abs = Math.abs(i);
            int childCount = appBarLayout.getChildCount();
            int i2 = 0;
            int i3 = 0;
            while (true) {
                if (i3 >= childCount) {
                    break;
                }
                View childAt = appBarLayout.getChildAt(i3);
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                Interpolator scrollInterpolator = layoutParams.getScrollInterpolator();
                if (abs < childAt.getTop() || abs > childAt.getBottom()) {
                    i3++;
                } else if (scrollInterpolator != null) {
                    int scrollFlags = layoutParams.getScrollFlags();
                    if ((scrollFlags & 1) != 0) {
                        i2 = 0 + childAt.getHeight() + layoutParams.topMargin + layoutParams.bottomMargin;
                        if ((scrollFlags & 2) != 0) {
                            i2 -= ViewCompat.getMinimumHeight(childAt);
                        }
                    }
                    if (ViewCompat.getFitsSystemWindows(childAt)) {
                        i2 -= appBarLayout.getTopInset();
                    }
                    if (i2 > 0) {
                        float f = i2;
                        return Integer.signum(i) * (childAt.getTop() + Math.round(f * scrollInterpolator.getInterpolation((abs - childAt.getTop()) / f)));
                    }
                }
            }
            return i;
        }

        @Override // android.support.design.widget.HeaderBehavior
        int getTopBottomOffsetForScrollingSibling() {
            return getTopAndBottomOffset() + this.mOffsetDelta;
        }

        @Override // android.support.design.widget.CoordinatorLayout.Behavior
        public Parcelable onSaveInstanceState(CoordinatorLayout coordinatorLayout, AppBarLayout appBarLayout) {
            Parcelable onSaveInstanceState = super.onSaveInstanceState(coordinatorLayout, (CoordinatorLayout) appBarLayout);
            int topAndBottomOffset = getTopAndBottomOffset();
            int childCount = appBarLayout.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = appBarLayout.getChildAt(i);
                int bottom = childAt.getBottom() + topAndBottomOffset;
                if (childAt.getTop() + topAndBottomOffset <= 0 && bottom >= 0) {
                    SavedState savedState = new SavedState(onSaveInstanceState);
                    savedState.firstVisibleChildIndex = i;
                    savedState.firstVisibileChildAtMinimumHeight = bottom == ViewCompat.getMinimumHeight(childAt);
                    savedState.firstVisibileChildPercentageShown = bottom / childAt.getHeight();
                    return savedState;
                }
            }
            return onSaveInstanceState;
        }

        @Override // android.support.design.widget.CoordinatorLayout.Behavior
        public void onRestoreInstanceState(CoordinatorLayout coordinatorLayout, AppBarLayout appBarLayout, Parcelable parcelable) {
            if (parcelable instanceof SavedState) {
                SavedState savedState = (SavedState) parcelable;
                super.onRestoreInstanceState(coordinatorLayout, (CoordinatorLayout) appBarLayout, savedState.getSuperState());
                this.mOffsetToChildIndexOnLayout = savedState.firstVisibleChildIndex;
                this.mOffsetToChildIndexOnLayoutPerc = savedState.firstVisibileChildPercentageShown;
                this.mOffsetToChildIndexOnLayoutIsMinHeight = savedState.firstVisibileChildAtMinimumHeight;
                return;
            }
            super.onRestoreInstanceState(coordinatorLayout, (CoordinatorLayout) appBarLayout, parcelable);
            this.mOffsetToChildIndexOnLayout = -1;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* loaded from: classes.dex */
        public static class SavedState extends View.BaseSavedState {
            public static final Parcelable.Creator<SavedState> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<SavedState>() { // from class: android.support.design.widget.AppBarLayout.Behavior.SavedState.1
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // android.support.v4.os.ParcelableCompatCreatorCallbacks
                public SavedState createFromParcel(Parcel parcel, ClassLoader classLoader) {
                    return new SavedState(parcel, classLoader);
                }

                /* JADX WARN: Can't rename method to resolve collision */
                @Override // android.support.v4.os.ParcelableCompatCreatorCallbacks
                public SavedState[] newArray(int i) {
                    return new SavedState[i];
                }
            });
            boolean firstVisibileChildAtMinimumHeight;
            float firstVisibileChildPercentageShown;
            int firstVisibleChildIndex;

            public SavedState(Parcel parcel, ClassLoader classLoader) {
                super(parcel);
                this.firstVisibleChildIndex = parcel.readInt();
                this.firstVisibileChildPercentageShown = parcel.readFloat();
                this.firstVisibileChildAtMinimumHeight = parcel.readByte() != 0;
            }

            public SavedState(Parcelable parcelable) {
                super(parcelable);
            }

            @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.os.Parcelable
            public void writeToParcel(Parcel parcel, int i) {
                super.writeToParcel(parcel, i);
                parcel.writeInt(this.firstVisibleChildIndex);
                parcel.writeFloat(this.firstVisibileChildPercentageShown);
                parcel.writeByte(this.firstVisibileChildAtMinimumHeight ? (byte) 1 : (byte) 0);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class ScrollingViewBehavior extends HeaderScrollingViewBehavior {
        private int mOverlayTop;

        @Override // android.support.design.widget.ViewOffsetBehavior
        public /* bridge */ /* synthetic */ int getLeftAndRightOffset() {
            return super.getLeftAndRightOffset();
        }

        @Override // android.support.design.widget.ViewOffsetBehavior
        public /* bridge */ /* synthetic */ int getTopAndBottomOffset() {
            return super.getTopAndBottomOffset();
        }

        @Override // android.support.design.widget.HeaderScrollingViewBehavior, android.support.design.widget.CoordinatorLayout.Behavior
        public /* bridge */ /* synthetic */ boolean onMeasureChild(CoordinatorLayout coordinatorLayout, View view, int i, int i2, int i3, int i4) {
            return super.onMeasureChild(coordinatorLayout, view, i, i2, i3, i4);
        }

        @Override // android.support.design.widget.ViewOffsetBehavior
        public /* bridge */ /* synthetic */ boolean setLeftAndRightOffset(int i) {
            return super.setLeftAndRightOffset(i);
        }

        @Override // android.support.design.widget.ViewOffsetBehavior
        public /* bridge */ /* synthetic */ boolean setTopAndBottomOffset(int i) {
            return super.setTopAndBottomOffset(i);
        }

        public ScrollingViewBehavior() {
        }

        public ScrollingViewBehavior(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ScrollingViewBehavior_Params);
            this.mOverlayTop = obtainStyledAttributes.getDimensionPixelSize(R.styleable.ScrollingViewBehavior_Params_behavior_overlapTop, 0);
            obtainStyledAttributes.recycle();
        }

        @Override // android.support.design.widget.CoordinatorLayout.Behavior
        public boolean layoutDependsOn(CoordinatorLayout coordinatorLayout, View view, View view2) {
            return view2 instanceof AppBarLayout;
        }

        @Override // android.support.design.widget.ViewOffsetBehavior, android.support.design.widget.CoordinatorLayout.Behavior
        public boolean onLayoutChild(CoordinatorLayout coordinatorLayout, View view, int i) {
            super.onLayoutChild(coordinatorLayout, view, i);
            List<View> dependencies = coordinatorLayout.getDependencies(view);
            int size = dependencies.size();
            for (int i2 = 0; i2 < size && !updateOffset(coordinatorLayout, view, dependencies.get(i2)); i2++) {
            }
            return true;
        }

        @Override // android.support.design.widget.CoordinatorLayout.Behavior
        public boolean onDependentViewChanged(CoordinatorLayout coordinatorLayout, View view, View view2) {
            updateOffset(coordinatorLayout, view, view2);
            return false;
        }

        private boolean updateOffset(CoordinatorLayout coordinatorLayout, View view, View view2) {
            CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) view2.getLayoutParams()).getBehavior();
            if (!(behavior instanceof Behavior)) {
                return false;
            }
            int topBottomOffsetForScrollingSibling = ((Behavior) behavior).getTopBottomOffsetForScrollingSibling();
            setTopAndBottomOffset((view2.getHeight() + topBottomOffsetForScrollingSibling) - getOverlapForOffset(view2, topBottomOffsetForScrollingSibling));
            return true;
        }

        private int getOverlapForOffset(View view, int i) {
            if (this.mOverlayTop != 0 && (view instanceof AppBarLayout)) {
                AppBarLayout appBarLayout = (AppBarLayout) view;
                int totalScrollRange = appBarLayout.getTotalScrollRange();
                int downNestedPreScrollRange = appBarLayout.getDownNestedPreScrollRange();
                if (downNestedPreScrollRange != 0 && totalScrollRange + i <= downNestedPreScrollRange) {
                    return 0;
                }
                int i2 = totalScrollRange - downNestedPreScrollRange;
                if (i2 != 0) {
                    return MathUtils.constrain(Math.round(((i / i2) + 1.0f) * this.mOverlayTop), 0, this.mOverlayTop);
                }
            }
            return this.mOverlayTop;
        }

        public void setOverlayTop(int i) {
            this.mOverlayTop = i;
        }

        public int getOverlayTop() {
            return this.mOverlayTop;
        }

        @Override // android.support.design.widget.HeaderScrollingViewBehavior
        View findFirstDependency(List<View> list) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                View view = list.get(i);
                if (view instanceof AppBarLayout) {
                    return view;
                }
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // android.support.design.widget.HeaderScrollingViewBehavior
        public int getScrollRange(View view) {
            if (view instanceof AppBarLayout) {
                return ((AppBarLayout) view).getTotalScrollRange();
            }
            return super.getScrollRange(view);
        }
    }
}
