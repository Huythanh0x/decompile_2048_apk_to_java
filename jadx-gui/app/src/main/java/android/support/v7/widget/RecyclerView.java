package android.support.v7.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Observable;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.os.TraceCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.ScrollingView;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityRecordCompat;
import android.support.v4.widget.EdgeEffectCompat;
import android.support.v4.widget.ScrollerCompat;
import android.support.v7.recyclerview.R;
import android.support.v7.widget.ActivityChooserView;
import android.support.v7.widget.AdapterHelper;
import android.support.v7.widget.ChildHelper;
import android.support.v7.widget.ViewInfoStore;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.FocusFinder;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Interpolator;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class RecyclerView extends ViewGroup implements ScrollingView, NestedScrollingChild {
    private static final boolean DEBUG = false;
    private static final boolean DISPATCH_TEMP_DETACH = false;
    private static final boolean FORCE_INVALIDATE_DISPLAY_LIST;
    public static final int HORIZONTAL = 0;
    private static final int INVALID_POINTER = -1;
    public static final int INVALID_TYPE = -1;
    private static final Class<?>[] LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE;
    private static final int MAX_SCROLL_DURATION = 2000;
    public static final long NO_ID = -1;
    public static final int NO_POSITION = -1;
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_SETTLING = 2;
    private static final String TAG = "RecyclerView";
    public static final int TOUCH_SLOP_DEFAULT = 0;
    public static final int TOUCH_SLOP_PAGING = 1;
    private static final String TRACE_BIND_VIEW_TAG = "RV OnBindView";
    private static final String TRACE_CREATE_VIEW_TAG = "RV CreateView";
    private static final String TRACE_HANDLE_ADAPTER_UPDATES_TAG = "RV PartialInvalidate";
    private static final String TRACE_ON_DATA_SET_CHANGE_LAYOUT_TAG = "RV FullInvalidate";
    private static final String TRACE_ON_LAYOUT_TAG = "RV OnLayout";
    private static final String TRACE_SCROLL_TAG = "RV Scroll";
    public static final int VERTICAL = 1;
    private static final Interpolator sQuinticInterpolator;
    private RecyclerViewAccessibilityDelegate mAccessibilityDelegate;
    private final AccessibilityManager mAccessibilityManager;
    private OnItemTouchListener mActiveOnItemTouchListener;
    private Adapter mAdapter;
    AdapterHelper mAdapterHelper;
    private boolean mAdapterUpdateDuringMeasure;
    private EdgeEffectCompat mBottomGlow;
    private ChildDrawingOrderCallback mChildDrawingOrderCallback;
    ChildHelper mChildHelper;
    private boolean mClipToPadding;
    private boolean mDataSetHasChangedAfterLayout;
    private boolean mEatRequestLayout;
    private int mEatenAccessibilityChangeFlags;
    private boolean mFirstLayoutComplete;
    private boolean mHasFixedSize;
    private boolean mIgnoreMotionEventTillDown;
    private int mInitialTouchX;
    private int mInitialTouchY;
    private boolean mIsAttached;
    ItemAnimator mItemAnimator;
    private ItemAnimator.ItemAnimatorListener mItemAnimatorListener;
    private Runnable mItemAnimatorRunner;
    private final ArrayList<ItemDecoration> mItemDecorations;
    boolean mItemsAddedOrRemoved;
    boolean mItemsChanged;
    private int mLastTouchX;
    private int mLastTouchY;
    private LayoutManager mLayout;
    private boolean mLayoutFrozen;
    private int mLayoutOrScrollCounter;
    private boolean mLayoutRequestEaten;
    private EdgeEffectCompat mLeftGlow;
    private final int mMaxFlingVelocity;
    private final int mMinFlingVelocity;
    private final int[] mMinMaxLayoutPositions;
    private final int[] mNestedOffsets;
    private final RecyclerViewDataObserver mObserver;
    private List<OnChildAttachStateChangeListener> mOnChildAttachStateListeners;
    private final ArrayList<OnItemTouchListener> mOnItemTouchListeners;
    private SavedState mPendingSavedState;
    private final boolean mPostUpdatesOnAnimation;
    private boolean mPostedAnimatorRunner;
    final Recycler mRecycler;
    private RecyclerListener mRecyclerListener;
    private EdgeEffectCompat mRightGlow;
    private final int[] mScrollConsumed;
    private float mScrollFactor;
    private OnScrollListener mScrollListener;
    private List<OnScrollListener> mScrollListeners;
    private final int[] mScrollOffset;
    private int mScrollPointerId;
    private int mScrollState;
    private final NestedScrollingChildHelper mScrollingChildHelper;
    final State mState;
    private final Rect mTempRect;
    private EdgeEffectCompat mTopGlow;
    private int mTouchSlop;
    private final Runnable mUpdateChildViewsRunnable;
    private VelocityTracker mVelocityTracker;
    private final ViewFlinger mViewFlinger;
    private final ViewInfoStore.ProcessCallback mViewInfoProcessCallback;
    final ViewInfoStore mViewInfoStore;

    /* loaded from: classes.dex */
    public interface ChildDrawingOrderCallback {
        int onGetChildDrawingOrder(int i, int i2);
    }

    /* loaded from: classes.dex */
    public interface OnChildAttachStateChangeListener {
        void onChildViewAttachedToWindow(View view);

        void onChildViewDetachedFromWindow(View view);
    }

    /* loaded from: classes.dex */
    public interface OnItemTouchListener {
        boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent);

        void onRequestDisallowInterceptTouchEvent(boolean z);

        void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent);
    }

    /* loaded from: classes.dex */
    public static abstract class OnScrollListener {
        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
        }

        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
        }
    }

    /* loaded from: classes.dex */
    public interface RecyclerListener {
        void onViewRecycled(ViewHolder viewHolder);
    }

    /* loaded from: classes.dex */
    public static class SimpleOnItemTouchListener implements OnItemTouchListener {
        @Override // android.support.v7.widget.RecyclerView.OnItemTouchListener
        public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
            return false;
        }

        @Override // android.support.v7.widget.RecyclerView.OnItemTouchListener
        public void onRequestDisallowInterceptTouchEvent(boolean z) {
        }

        @Override // android.support.v7.widget.RecyclerView.OnItemTouchListener
        public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
        }
    }

    /* loaded from: classes.dex */
    public static abstract class ViewCacheExtension {
        public abstract View getViewForPositionAndType(Recycler recycler, int i, int i2);
    }

    public void onChildAttachedToWindow(View view) {
    }

    public void onChildDetachedFromWindow(View view) {
    }

    public void onScrollStateChanged(int i) {
    }

    public void onScrolled(int i, int i2) {
    }

    static {
        FORCE_INVALIDATE_DISPLAY_LIST = Build.VERSION.SDK_INT == 18 || Build.VERSION.SDK_INT == 19 || Build.VERSION.SDK_INT == 20;
        LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE = new Class[]{Context.class, AttributeSet.class, Integer.TYPE, Integer.TYPE};
        sQuinticInterpolator = new Interpolator() { // from class: android.support.v7.widget.RecyclerView.3
            @Override // android.animation.TimeInterpolator
            public float getInterpolation(float f) {
                float f2 = f - 1.0f;
                return (f2 * f2 * f2 * f2 * f2) + 1.0f;
            }
        };
    }

    public RecyclerView(Context context) {
        this(context, null);
    }

    public RecyclerView(Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public RecyclerView(Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mObserver = new RecyclerViewDataObserver();
        this.mRecycler = new Recycler();
        this.mViewInfoStore = new ViewInfoStore();
        this.mUpdateChildViewsRunnable = new Runnable() { // from class: android.support.v7.widget.RecyclerView.1
            @Override // java.lang.Runnable
            public void run() {
                if (!RecyclerView.this.mFirstLayoutComplete || RecyclerView.this.isLayoutRequested()) {
                    return;
                }
                if (RecyclerView.this.mLayoutFrozen) {
                    RecyclerView.this.mLayoutRequestEaten = true;
                } else {
                    RecyclerView.this.consumePendingUpdateOperations();
                }
            }
        };
        this.mTempRect = new Rect();
        this.mItemDecorations = new ArrayList<>();
        this.mOnItemTouchListeners = new ArrayList<>();
        this.mDataSetHasChangedAfterLayout = false;
        this.mLayoutOrScrollCounter = 0;
        this.mItemAnimator = new DefaultItemAnimator();
        this.mScrollState = 0;
        this.mScrollPointerId = -1;
        this.mScrollFactor = Float.MIN_VALUE;
        this.mViewFlinger = new ViewFlinger();
        this.mState = new State();
        this.mItemsAddedOrRemoved = false;
        this.mItemsChanged = false;
        this.mItemAnimatorListener = new ItemAnimatorRestoreListener();
        this.mPostedAnimatorRunner = false;
        this.mMinMaxLayoutPositions = new int[2];
        this.mScrollOffset = new int[2];
        this.mScrollConsumed = new int[2];
        this.mNestedOffsets = new int[2];
        this.mItemAnimatorRunner = new Runnable() { // from class: android.support.v7.widget.RecyclerView.2
            @Override // java.lang.Runnable
            public void run() {
                if (RecyclerView.this.mItemAnimator != null) {
                    RecyclerView.this.mItemAnimator.runPendingAnimations();
                }
                RecyclerView.this.mPostedAnimatorRunner = false;
            }
        };
        this.mViewInfoProcessCallback = new ViewInfoStore.ProcessCallback() { // from class: android.support.v7.widget.RecyclerView.4
            @Override // android.support.v7.widget.ViewInfoStore.ProcessCallback
            public void processDisappeared(ViewHolder viewHolder, ItemAnimator.ItemHolderInfo itemHolderInfo, ItemAnimator.ItemHolderInfo itemHolderInfo2) {
                RecyclerView.this.mRecycler.unscrapView(viewHolder);
                RecyclerView.this.animateDisappearance(viewHolder, itemHolderInfo, itemHolderInfo2);
            }

            @Override // android.support.v7.widget.ViewInfoStore.ProcessCallback
            public void processAppeared(ViewHolder viewHolder, ItemAnimator.ItemHolderInfo itemHolderInfo, ItemAnimator.ItemHolderInfo itemHolderInfo2) {
                RecyclerView.this.animateAppearance(viewHolder, itemHolderInfo, itemHolderInfo2);
            }

            @Override // android.support.v7.widget.ViewInfoStore.ProcessCallback
            public void processPersistent(ViewHolder viewHolder, @NonNull ItemAnimator.ItemHolderInfo itemHolderInfo, @NonNull ItemAnimator.ItemHolderInfo itemHolderInfo2) {
                viewHolder.setIsRecyclable(false);
                if (RecyclerView.this.mDataSetHasChangedAfterLayout) {
                    if (RecyclerView.this.mItemAnimator.animateChange(viewHolder, viewHolder, itemHolderInfo, itemHolderInfo2)) {
                        RecyclerView.this.postAnimationRunner();
                    }
                } else if (RecyclerView.this.mItemAnimator.animatePersistence(viewHolder, itemHolderInfo, itemHolderInfo2)) {
                    RecyclerView.this.postAnimationRunner();
                }
            }

            @Override // android.support.v7.widget.ViewInfoStore.ProcessCallback
            public void unused(ViewHolder viewHolder) {
                RecyclerView.this.mLayout.removeAndRecycleView(viewHolder.itemView, RecyclerView.this.mRecycler);
            }
        };
        setScrollContainer(true);
        setFocusableInTouchMode(true);
        this.mPostUpdatesOnAnimation = Build.VERSION.SDK_INT >= 16;
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        this.mTouchSlop = viewConfiguration.getScaledTouchSlop();
        this.mMinFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        this.mMaxFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        setWillNotDraw(ViewCompat.getOverScrollMode(this) == 2);
        this.mItemAnimator.setListener(this.mItemAnimatorListener);
        initAdapterManager();
        initChildrenHelper();
        if (ViewCompat.getImportantForAccessibility(this) == 0) {
            ViewCompat.setImportantForAccessibility(this, 1);
        }
        this.mAccessibilityManager = (AccessibilityManager) getContext().getSystemService("accessibility");
        setAccessibilityDelegateCompat(new RecyclerViewAccessibilityDelegate(this));
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.RecyclerView, i, 0);
            String string = obtainStyledAttributes.getString(R.styleable.RecyclerView_layoutManager);
            obtainStyledAttributes.recycle();
            createLayoutManager(context, string, attributeSet, i, 0);
        }
        this.mScrollingChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
    }

    public RecyclerViewAccessibilityDelegate getCompatAccessibilityDelegate() {
        return this.mAccessibilityDelegate;
    }

    public void setAccessibilityDelegateCompat(RecyclerViewAccessibilityDelegate recyclerViewAccessibilityDelegate) {
        this.mAccessibilityDelegate = recyclerViewAccessibilityDelegate;
        ViewCompat.setAccessibilityDelegate(this, this.mAccessibilityDelegate);
    }

    private void createLayoutManager(Context context, String str, AttributeSet attributeSet, int i, int i2) {
        ClassLoader classLoader;
        Constructor constructor;
        if (str != null) {
            String trim = str.trim();
            if (trim.length() != 0) {
                String fullClassName = getFullClassName(context, trim);
                try {
                    if (isInEditMode()) {
                        classLoader = getClass().getClassLoader();
                    } else {
                        classLoader = context.getClassLoader();
                    }
                    Class<? extends U> asSubclass = classLoader.loadClass(fullClassName).asSubclass(LayoutManager.class);
                    Object[] objArr = null;
                    try {
                        constructor = asSubclass.getConstructor(LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE);
                        objArr = new Object[]{context, attributeSet, Integer.valueOf(i), Integer.valueOf(i2)};
                    } catch (NoSuchMethodException e) {
                        try {
                            constructor = asSubclass.getConstructor(new Class[0]);
                        } catch (NoSuchMethodException e2) {
                            e2.initCause(e);
                            throw new IllegalStateException(attributeSet.getPositionDescription() + ": Error creating LayoutManager " + fullClassName, e2);
                        }
                    }
                    constructor.setAccessible(true);
                    setLayoutManager((LayoutManager) constructor.newInstance(objArr));
                } catch (ClassCastException e3) {
                    throw new IllegalStateException(attributeSet.getPositionDescription() + ": Class is not a LayoutManager " + fullClassName, e3);
                } catch (ClassNotFoundException e4) {
                    throw new IllegalStateException(attributeSet.getPositionDescription() + ": Unable to find LayoutManager " + fullClassName, e4);
                } catch (IllegalAccessException e5) {
                    throw new IllegalStateException(attributeSet.getPositionDescription() + ": Cannot access non-public constructor " + fullClassName, e5);
                } catch (InstantiationException e6) {
                    throw new IllegalStateException(attributeSet.getPositionDescription() + ": Could not instantiate the LayoutManager: " + fullClassName, e6);
                } catch (InvocationTargetException e7) {
                    throw new IllegalStateException(attributeSet.getPositionDescription() + ": Could not instantiate the LayoutManager: " + fullClassName, e7);
                }
            }
        }
    }

    private String getFullClassName(Context context, String str) {
        if (str.charAt(0) == '.') {
            return context.getPackageName() + str;
        }
        if (str.contains(".")) {
            return str;
        }
        return RecyclerView.class.getPackage().getName() + '.' + str;
    }

    private void initChildrenHelper() {
        this.mChildHelper = new ChildHelper(new ChildHelper.Callback() { // from class: android.support.v7.widget.RecyclerView.5
            @Override // android.support.v7.widget.ChildHelper.Callback
            public int getChildCount() {
                return RecyclerView.this.getChildCount();
            }

            @Override // android.support.v7.widget.ChildHelper.Callback
            public void addView(View view, int i) {
                RecyclerView.this.addView(view, i);
                RecyclerView.this.dispatchChildAttached(view);
            }

            @Override // android.support.v7.widget.ChildHelper.Callback
            public int indexOfChild(View view) {
                return RecyclerView.this.indexOfChild(view);
            }

            @Override // android.support.v7.widget.ChildHelper.Callback
            public void removeViewAt(int i) {
                View childAt = RecyclerView.this.getChildAt(i);
                if (childAt != null) {
                    RecyclerView.this.dispatchChildDetached(childAt);
                }
                RecyclerView.this.removeViewAt(i);
            }

            @Override // android.support.v7.widget.ChildHelper.Callback
            public View getChildAt(int i) {
                return RecyclerView.this.getChildAt(i);
            }

            @Override // android.support.v7.widget.ChildHelper.Callback
            public void removeAllViews() {
                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    RecyclerView.this.dispatchChildDetached(getChildAt(i));
                }
                RecyclerView.this.removeAllViews();
            }

            @Override // android.support.v7.widget.ChildHelper.Callback
            public ViewHolder getChildViewHolder(View view) {
                return RecyclerView.getChildViewHolderInt(view);
            }

            @Override // android.support.v7.widget.ChildHelper.Callback
            public void attachViewToParent(View view, int i, ViewGroup.LayoutParams layoutParams) {
                ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
                if (childViewHolderInt != null) {
                    if (!childViewHolderInt.isTmpDetached() && !childViewHolderInt.shouldIgnore()) {
                        throw new IllegalArgumentException("Called attach on a child which is not detached: " + childViewHolderInt);
                    }
                    childViewHolderInt.clearTmpDetachFlag();
                }
                RecyclerView.this.attachViewToParent(view, i, layoutParams);
            }

            @Override // android.support.v7.widget.ChildHelper.Callback
            public void detachViewFromParent(int i) {
                ViewHolder childViewHolderInt;
                View childAt = getChildAt(i);
                if (childAt != null && (childViewHolderInt = RecyclerView.getChildViewHolderInt(childAt)) != null) {
                    if (childViewHolderInt.isTmpDetached() && !childViewHolderInt.shouldIgnore()) {
                        throw new IllegalArgumentException("called detach on an already detached child " + childViewHolderInt);
                    }
                    childViewHolderInt.addFlags(256);
                }
                RecyclerView.this.detachViewFromParent(i);
            }

            @Override // android.support.v7.widget.ChildHelper.Callback
            public void onEnteredHiddenState(View view) {
                ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
                if (childViewHolderInt != null) {
                    childViewHolderInt.onEnteredHiddenState();
                }
            }

            @Override // android.support.v7.widget.ChildHelper.Callback
            public void onLeftHiddenState(View view) {
                ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
                if (childViewHolderInt != null) {
                    childViewHolderInt.onLeftHiddenState();
                }
            }
        });
    }

    void initAdapterManager() {
        this.mAdapterHelper = new AdapterHelper(new AdapterHelper.Callback() { // from class: android.support.v7.widget.RecyclerView.6
            @Override // android.support.v7.widget.AdapterHelper.Callback
            public ViewHolder findViewHolder(int i) {
                ViewHolder findViewHolderForPosition = RecyclerView.this.findViewHolderForPosition(i, true);
                if (findViewHolderForPosition == null || RecyclerView.this.mChildHelper.isHidden(findViewHolderForPosition.itemView)) {
                    return null;
                }
                return findViewHolderForPosition;
            }

            @Override // android.support.v7.widget.AdapterHelper.Callback
            public void offsetPositionsForRemovingInvisible(int i, int i2) {
                RecyclerView.this.offsetPositionRecordsForRemove(i, i2, true);
                RecyclerView.this.mItemsAddedOrRemoved = true;
                State.access$1812(RecyclerView.this.mState, i2);
            }

            @Override // android.support.v7.widget.AdapterHelper.Callback
            public void offsetPositionsForRemovingLaidOutOrNewView(int i, int i2) {
                RecyclerView.this.offsetPositionRecordsForRemove(i, i2, false);
                RecyclerView.this.mItemsAddedOrRemoved = true;
            }

            @Override // android.support.v7.widget.AdapterHelper.Callback
            public void markViewHoldersUpdated(int i, int i2, Object obj) {
                RecyclerView.this.viewRangeUpdate(i, i2, obj);
                RecyclerView.this.mItemsChanged = true;
            }

            @Override // android.support.v7.widget.AdapterHelper.Callback
            public void onDispatchFirstPass(AdapterHelper.UpdateOp updateOp) {
                dispatchUpdate(updateOp);
            }

            void dispatchUpdate(AdapterHelper.UpdateOp updateOp) {
                int i = updateOp.cmd;
                if (i == 4) {
                    RecyclerView.this.mLayout.onItemsUpdated(RecyclerView.this, updateOp.positionStart, updateOp.itemCount, updateOp.payload);
                    return;
                }
                if (i != 8) {
                    switch (i) {
                        case 1:
                            RecyclerView.this.mLayout.onItemsAdded(RecyclerView.this, updateOp.positionStart, updateOp.itemCount);
                            return;
                        case 2:
                            RecyclerView.this.mLayout.onItemsRemoved(RecyclerView.this, updateOp.positionStart, updateOp.itemCount);
                            return;
                        default:
                            return;
                    }
                }
                RecyclerView.this.mLayout.onItemsMoved(RecyclerView.this, updateOp.positionStart, updateOp.itemCount, 1);
            }

            @Override // android.support.v7.widget.AdapterHelper.Callback
            public void onDispatchSecondPass(AdapterHelper.UpdateOp updateOp) {
                dispatchUpdate(updateOp);
            }

            @Override // android.support.v7.widget.AdapterHelper.Callback
            public void offsetPositionsForAdd(int i, int i2) {
                RecyclerView.this.offsetPositionRecordsForInsert(i, i2);
                RecyclerView.this.mItemsAddedOrRemoved = true;
            }

            @Override // android.support.v7.widget.AdapterHelper.Callback
            public void offsetPositionsForMove(int i, int i2) {
                RecyclerView.this.offsetPositionRecordsForMove(i, i2);
                RecyclerView.this.mItemsAddedOrRemoved = true;
            }
        });
    }

    public void setHasFixedSize(boolean z) {
        this.mHasFixedSize = z;
    }

    public boolean hasFixedSize() {
        return this.mHasFixedSize;
    }

    @Override // android.view.ViewGroup
    public void setClipToPadding(boolean z) {
        if (z != this.mClipToPadding) {
            invalidateGlows();
        }
        this.mClipToPadding = z;
        super.setClipToPadding(z);
        if (this.mFirstLayoutComplete) {
            requestLayout();
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0008. Please report as an issue. */
    public void setScrollingTouchSlop(int i) {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        switch (i) {
            case 0:
                this.mTouchSlop = viewConfiguration.getScaledTouchSlop();
                return;
            case 1:
                this.mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(viewConfiguration);
                return;
            default:
                Log.w(TAG, "setScrollingTouchSlop(): bad argument constant " + i + "; using default value");
                this.mTouchSlop = viewConfiguration.getScaledTouchSlop();
                return;
        }
    }

    public void swapAdapter(Adapter adapter, boolean z) {
        setLayoutFrozen(false);
        setAdapterInternal(adapter, true, z);
        setDataSetChangedAfterLayout();
        requestLayout();
    }

    public void setAdapter(Adapter adapter) {
        setLayoutFrozen(false);
        setAdapterInternal(adapter, false, true);
        requestLayout();
    }

    private void setAdapterInternal(Adapter adapter, boolean z, boolean z2) {
        if (this.mAdapter != null) {
            this.mAdapter.unregisterAdapterDataObserver(this.mObserver);
            this.mAdapter.onDetachedFromRecyclerView(this);
        }
        if (!z || z2) {
            if (this.mItemAnimator != null) {
                this.mItemAnimator.endAnimations();
            }
            if (this.mLayout != null) {
                this.mLayout.removeAndRecycleAllViews(this.mRecycler);
                this.mLayout.removeAndRecycleScrapInt(this.mRecycler);
            }
            this.mRecycler.clear();
        }
        this.mAdapterHelper.reset();
        Adapter adapter2 = this.mAdapter;
        this.mAdapter = adapter;
        if (adapter != null) {
            adapter.registerAdapterDataObserver(this.mObserver);
            adapter.onAttachedToRecyclerView(this);
        }
        if (this.mLayout != null) {
            this.mLayout.onAdapterChanged(adapter2, this.mAdapter);
        }
        this.mRecycler.onAdapterChanged(adapter2, this.mAdapter, z);
        this.mState.mStructureChanged = true;
        markKnownViewsInvalid();
    }

    public Adapter getAdapter() {
        return this.mAdapter;
    }

    public void setRecyclerListener(RecyclerListener recyclerListener) {
        this.mRecyclerListener = recyclerListener;
    }

    @Override // android.view.View
    public int getBaseline() {
        if (this.mLayout != null) {
            return this.mLayout.getBaseline();
        }
        return super.getBaseline();
    }

    public void addOnChildAttachStateChangeListener(OnChildAttachStateChangeListener onChildAttachStateChangeListener) {
        if (this.mOnChildAttachStateListeners == null) {
            this.mOnChildAttachStateListeners = new ArrayList();
        }
        this.mOnChildAttachStateListeners.add(onChildAttachStateChangeListener);
    }

    public void removeOnChildAttachStateChangeListener(OnChildAttachStateChangeListener onChildAttachStateChangeListener) {
        if (this.mOnChildAttachStateListeners == null) {
            return;
        }
        this.mOnChildAttachStateListeners.remove(onChildAttachStateChangeListener);
    }

    public void clearOnChildAttachStateChangeListeners() {
        if (this.mOnChildAttachStateListeners != null) {
            this.mOnChildAttachStateListeners.clear();
        }
    }

    public void setLayoutManager(LayoutManager layoutManager) {
        if (layoutManager == this.mLayout) {
            return;
        }
        if (this.mLayout != null) {
            if (this.mIsAttached) {
                this.mLayout.dispatchDetachedFromWindow(this, this.mRecycler);
            }
            this.mLayout.setRecyclerView(null);
        }
        this.mRecycler.clear();
        this.mChildHelper.removeAllViewsUnfiltered();
        this.mLayout = layoutManager;
        if (layoutManager != null) {
            if (layoutManager.mRecyclerView != null) {
                throw new IllegalArgumentException("LayoutManager " + layoutManager + " is already attached to a RecyclerView: " + layoutManager.mRecyclerView);
            }
            this.mLayout.setRecyclerView(this);
            if (this.mIsAttached) {
                this.mLayout.dispatchAttachedToWindow(this);
            }
        }
        requestLayout();
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        if (this.mPendingSavedState == null) {
            if (this.mLayout != null) {
                savedState.mLayoutState = this.mLayout.onSaveInstanceState();
            } else {
                savedState.mLayoutState = null;
            }
        } else {
            savedState.copyFrom(this.mPendingSavedState);
        }
        return savedState;
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable parcelable) {
        this.mPendingSavedState = (SavedState) parcelable;
        super.onRestoreInstanceState(this.mPendingSavedState.getSuperState());
        if (this.mLayout == null || this.mPendingSavedState.mLayoutState == null) {
            return;
        }
        this.mLayout.onRestoreInstanceState(this.mPendingSavedState.mLayoutState);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> sparseArray) {
        dispatchFreezeSelfOnly(sparseArray);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> sparseArray) {
        dispatchThawSelfOnly(sparseArray);
    }

    private void addAnimatingView(ViewHolder viewHolder) {
        View view = viewHolder.itemView;
        boolean z = view.getParent() == this;
        this.mRecycler.unscrapView(getChildViewHolder(view));
        if (viewHolder.isTmpDetached()) {
            this.mChildHelper.attachViewToParent(view, -1, view.getLayoutParams(), true);
        } else if (!z) {
            this.mChildHelper.addView(view, true);
        } else {
            this.mChildHelper.hide(view);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean removeAnimatingView(View view) {
        eatRequestLayout();
        boolean removeViewIfHidden = this.mChildHelper.removeViewIfHidden(view);
        if (removeViewIfHidden) {
            ViewHolder childViewHolderInt = getChildViewHolderInt(view);
            this.mRecycler.unscrapView(childViewHolderInt);
            this.mRecycler.recycleViewHolderInternal(childViewHolderInt);
        }
        resumeRequestLayout(false);
        return removeViewIfHidden;
    }

    public LayoutManager getLayoutManager() {
        return this.mLayout;
    }

    public RecycledViewPool getRecycledViewPool() {
        return this.mRecycler.getRecycledViewPool();
    }

    public void setRecycledViewPool(RecycledViewPool recycledViewPool) {
        this.mRecycler.setRecycledViewPool(recycledViewPool);
    }

    public void setViewCacheExtension(ViewCacheExtension viewCacheExtension) {
        this.mRecycler.setViewCacheExtension(viewCacheExtension);
    }

    public void setItemViewCacheSize(int i) {
        this.mRecycler.setViewCacheSize(i);
    }

    public int getScrollState() {
        return this.mScrollState;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setScrollState(int i) {
        if (i == this.mScrollState) {
            return;
        }
        this.mScrollState = i;
        if (i != 2) {
            stopScrollersInternal();
        }
        dispatchOnScrollStateChanged(i);
    }

    public void addItemDecoration(ItemDecoration itemDecoration, int i) {
        if (this.mLayout != null) {
            this.mLayout.assertNotInLayoutOrScroll("Cannot add item decoration during a scroll  or layout");
        }
        if (this.mItemDecorations.isEmpty()) {
            setWillNotDraw(false);
        }
        if (i < 0) {
            this.mItemDecorations.add(itemDecoration);
        } else {
            this.mItemDecorations.add(i, itemDecoration);
        }
        markItemDecorInsetsDirty();
        requestLayout();
    }

    public void addItemDecoration(ItemDecoration itemDecoration) {
        addItemDecoration(itemDecoration, -1);
    }

    public void removeItemDecoration(ItemDecoration itemDecoration) {
        if (this.mLayout != null) {
            this.mLayout.assertNotInLayoutOrScroll("Cannot remove item decoration during a scroll  or layout");
        }
        this.mItemDecorations.remove(itemDecoration);
        if (this.mItemDecorations.isEmpty()) {
            setWillNotDraw(ViewCompat.getOverScrollMode(this) == 2);
        }
        markItemDecorInsetsDirty();
        requestLayout();
    }

    public void setChildDrawingOrderCallback(ChildDrawingOrderCallback childDrawingOrderCallback) {
        if (childDrawingOrderCallback == this.mChildDrawingOrderCallback) {
            return;
        }
        this.mChildDrawingOrderCallback = childDrawingOrderCallback;
        setChildrenDrawingOrderEnabled(this.mChildDrawingOrderCallback != null);
    }

    @Deprecated
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.mScrollListener = onScrollListener;
    }

    public void addOnScrollListener(OnScrollListener onScrollListener) {
        if (this.mScrollListeners == null) {
            this.mScrollListeners = new ArrayList();
        }
        this.mScrollListeners.add(onScrollListener);
    }

    public void removeOnScrollListener(OnScrollListener onScrollListener) {
        if (this.mScrollListeners != null) {
            this.mScrollListeners.remove(onScrollListener);
        }
    }

    public void clearOnScrollListeners() {
        if (this.mScrollListeners != null) {
            this.mScrollListeners.clear();
        }
    }

    public void scrollToPosition(int i) {
        if (this.mLayoutFrozen) {
            return;
        }
        stopScroll();
        if (this.mLayout == null) {
            Log.e(TAG, "Cannot scroll to position a LayoutManager set. Call setLayoutManager with a non-null argument.");
        } else {
            this.mLayout.scrollToPosition(i);
            awakenScrollBars();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void jumpToPositionForSmoothScroller(int i) {
        if (this.mLayout == null) {
            return;
        }
        this.mLayout.scrollToPosition(i);
        awakenScrollBars();
    }

    public void smoothScrollToPosition(int i) {
        if (this.mLayoutFrozen) {
            return;
        }
        if (this.mLayout == null) {
            Log.e(TAG, "Cannot smooth scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
        } else {
            this.mLayout.smoothScrollToPosition(this, this.mState, i);
        }
    }

    @Override // android.view.View
    public void scrollTo(int i, int i2) {
        Log.w(TAG, "RecyclerView does not support scrolling to an absolute position. Use scrollToPosition instead");
    }

    @Override // android.view.View
    public void scrollBy(int i, int i2) {
        if (this.mLayout == null) {
            Log.e(TAG, "Cannot scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
            return;
        }
        if (this.mLayoutFrozen) {
            return;
        }
        boolean canScrollHorizontally = this.mLayout.canScrollHorizontally();
        boolean canScrollVertically = this.mLayout.canScrollVertically();
        if (canScrollHorizontally || canScrollVertically) {
            if (!canScrollHorizontally) {
                i = 0;
            }
            if (!canScrollVertically) {
                i2 = 0;
            }
            scrollByInternal(i, i2, null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void consumePendingUpdateOperations() {
        if (this.mFirstLayoutComplete) {
            if (this.mDataSetHasChangedAfterLayout) {
                TraceCompat.beginSection(TRACE_ON_DATA_SET_CHANGE_LAYOUT_TAG);
                dispatchLayout();
                TraceCompat.endSection();
                return;
            }
            if (this.mAdapterHelper.hasPendingUpdates()) {
                if (this.mAdapterHelper.hasAnyUpdateTypes(4) && !this.mAdapterHelper.hasAnyUpdateTypes(11)) {
                    TraceCompat.beginSection(TRACE_HANDLE_ADAPTER_UPDATES_TAG);
                    eatRequestLayout();
                    this.mAdapterHelper.preProcess();
                    if (!this.mLayoutRequestEaten) {
                        if (hasUpdatedView()) {
                            dispatchLayout();
                        } else {
                            this.mAdapterHelper.consumePostponedUpdates();
                        }
                    }
                    resumeRequestLayout(true);
                    TraceCompat.endSection();
                    return;
                }
                if (this.mAdapterHelper.hasPendingUpdates()) {
                    TraceCompat.beginSection(TRACE_ON_DATA_SET_CHANGE_LAYOUT_TAG);
                    dispatchLayout();
                    TraceCompat.endSection();
                }
            }
        }
    }

    private boolean hasUpdatedView() {
        int childCount = this.mChildHelper.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
            if (childViewHolderInt != null && !childViewHolderInt.shouldIgnore() && childViewHolderInt.isUpdated()) {
                return true;
            }
        }
        return false;
    }

    boolean scrollByInternal(int i, int i2, MotionEvent motionEvent) {
        int i3;
        int i4;
        int i5;
        int i6;
        consumePendingUpdateOperations();
        if (this.mAdapter != null) {
            eatRequestLayout();
            onEnterLayoutOrScroll();
            TraceCompat.beginSection(TRACE_SCROLL_TAG);
            if (i != 0) {
                i3 = this.mLayout.scrollHorizontallyBy(i, this.mRecycler, this.mState);
                i4 = i - i3;
            } else {
                i3 = 0;
                i4 = 0;
            }
            if (i2 != 0) {
                i5 = this.mLayout.scrollVerticallyBy(i2, this.mRecycler, this.mState);
                i6 = i2 - i5;
            } else {
                i5 = 0;
                i6 = 0;
            }
            TraceCompat.endSection();
            repositionShadowingViews();
            onExitLayoutOrScroll();
            resumeRequestLayout(false);
        } else {
            i3 = 0;
            i4 = 0;
            i5 = 0;
            i6 = 0;
        }
        if (!this.mItemDecorations.isEmpty()) {
            invalidate();
        }
        if (dispatchNestedScroll(i3, i5, i4, i6, this.mScrollOffset)) {
            this.mLastTouchX -= this.mScrollOffset[0];
            this.mLastTouchY -= this.mScrollOffset[1];
            if (motionEvent != null) {
                motionEvent.offsetLocation(this.mScrollOffset[0], this.mScrollOffset[1]);
            }
            int[] iArr = this.mNestedOffsets;
            iArr[0] = iArr[0] + this.mScrollOffset[0];
            int[] iArr2 = this.mNestedOffsets;
            iArr2[1] = iArr2[1] + this.mScrollOffset[1];
        } else if (ViewCompat.getOverScrollMode(this) != 2) {
            if (motionEvent != null) {
                pullGlows(motionEvent.getX(), i4, motionEvent.getY(), i6);
            }
            considerReleasingGlowsOnScroll(i, i2);
        }
        if (i3 != 0 || i5 != 0) {
            dispatchOnScrolled(i3, i5);
        }
        if (!awakenScrollBars()) {
            invalidate();
        }
        return (i3 == 0 && i5 == 0) ? false : true;
    }

    @Override // android.view.View, android.support.v4.view.ScrollingView
    public int computeHorizontalScrollOffset() {
        if (this.mLayout.canScrollHorizontally()) {
            return this.mLayout.computeHorizontalScrollOffset(this.mState);
        }
        return 0;
    }

    @Override // android.view.View, android.support.v4.view.ScrollingView
    public int computeHorizontalScrollExtent() {
        if (this.mLayout.canScrollHorizontally()) {
            return this.mLayout.computeHorizontalScrollExtent(this.mState);
        }
        return 0;
    }

    @Override // android.view.View, android.support.v4.view.ScrollingView
    public int computeHorizontalScrollRange() {
        if (this.mLayout.canScrollHorizontally()) {
            return this.mLayout.computeHorizontalScrollRange(this.mState);
        }
        return 0;
    }

    @Override // android.view.View, android.support.v4.view.ScrollingView
    public int computeVerticalScrollOffset() {
        if (this.mLayout.canScrollVertically()) {
            return this.mLayout.computeVerticalScrollOffset(this.mState);
        }
        return 0;
    }

    @Override // android.view.View, android.support.v4.view.ScrollingView
    public int computeVerticalScrollExtent() {
        if (this.mLayout.canScrollVertically()) {
            return this.mLayout.computeVerticalScrollExtent(this.mState);
        }
        return 0;
    }

    @Override // android.view.View, android.support.v4.view.ScrollingView
    public int computeVerticalScrollRange() {
        if (this.mLayout.canScrollVertically()) {
            return this.mLayout.computeVerticalScrollRange(this.mState);
        }
        return 0;
    }

    void eatRequestLayout() {
        if (this.mEatRequestLayout) {
            return;
        }
        this.mEatRequestLayout = true;
        if (this.mLayoutFrozen) {
            return;
        }
        this.mLayoutRequestEaten = false;
    }

    void resumeRequestLayout(boolean z) {
        if (this.mEatRequestLayout) {
            if (z && this.mLayoutRequestEaten && !this.mLayoutFrozen && this.mLayout != null && this.mAdapter != null) {
                dispatchLayout();
            }
            this.mEatRequestLayout = false;
            if (this.mLayoutFrozen) {
                return;
            }
            this.mLayoutRequestEaten = false;
        }
    }

    public void setLayoutFrozen(boolean z) {
        if (z != this.mLayoutFrozen) {
            assertNotInLayoutOrScroll("Do not setLayoutFrozen in layout or scroll");
            if (!z) {
                this.mLayoutFrozen = z;
                if (this.mLayoutRequestEaten && this.mLayout != null && this.mAdapter != null) {
                    requestLayout();
                }
                this.mLayoutRequestEaten = false;
                return;
            }
            long uptimeMillis = SystemClock.uptimeMillis();
            onTouchEvent(MotionEvent.obtain(uptimeMillis, uptimeMillis, 3, 0.0f, 0.0f, 0));
            this.mLayoutFrozen = z;
            this.mIgnoreMotionEventTillDown = true;
            stopScroll();
        }
    }

    public boolean isLayoutFrozen() {
        return this.mLayoutFrozen;
    }

    public void smoothScrollBy(int i, int i2) {
        if (this.mLayout == null) {
            Log.e(TAG, "Cannot smooth scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
            return;
        }
        if (this.mLayoutFrozen) {
            return;
        }
        if (!this.mLayout.canScrollHorizontally()) {
            i = 0;
        }
        if (!this.mLayout.canScrollVertically()) {
            i2 = 0;
        }
        if (i == 0 && i2 == 0) {
            return;
        }
        this.mViewFlinger.smoothScrollBy(i, i2);
    }

    public boolean fling(int i, int i2) {
        if (this.mLayout == null) {
            Log.e(TAG, "Cannot fling without a LayoutManager set. Call setLayoutManager with a non-null argument.");
            return false;
        }
        if (this.mLayoutFrozen) {
            return false;
        }
        boolean canScrollHorizontally = this.mLayout.canScrollHorizontally();
        boolean canScrollVertically = this.mLayout.canScrollVertically();
        if (!canScrollHorizontally || Math.abs(i) < this.mMinFlingVelocity) {
            i = 0;
        }
        if (!canScrollVertically || Math.abs(i2) < this.mMinFlingVelocity) {
            i2 = 0;
        }
        if (i == 0 && i2 == 0) {
            return false;
        }
        float f = i;
        float f2 = i2;
        if (!dispatchNestedPreFling(f, f2)) {
            boolean z = canScrollHorizontally || canScrollVertically;
            dispatchNestedFling(f, f2, z);
            if (z) {
                this.mViewFlinger.fling(Math.max(-this.mMaxFlingVelocity, Math.min(i, this.mMaxFlingVelocity)), Math.max(-this.mMaxFlingVelocity, Math.min(i2, this.mMaxFlingVelocity)));
                return true;
            }
        }
        return false;
    }

    public void stopScroll() {
        setScrollState(0);
        stopScrollersInternal();
    }

    private void stopScrollersInternal() {
        this.mViewFlinger.stop();
        if (this.mLayout != null) {
            this.mLayout.stopSmoothScroller();
        }
    }

    public int getMinFlingVelocity() {
        return this.mMinFlingVelocity;
    }

    public int getMaxFlingVelocity() {
        return this.mMaxFlingVelocity;
    }

    /* JADX WARN: Code restructure failed: missing block: B:23:0x007a, code lost:
    
        if (r6.mBottomGlow.onPull(r10 / getHeight(), 1.0f - (r7 / getWidth())) != false) goto L24;
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x003e, code lost:
    
        if (r6.mRightGlow.onPull(r8 / getWidth(), r9 / getHeight()) != false) goto L6;
     */
    /* JADX WARN: Code restructure failed: missing block: B:4:0x0020, code lost:
    
        if (r6.mLeftGlow.onPull((-r8) / getWidth(), 1.0f - (r9 / getHeight())) != false) goto L6;
     */
    /* JADX WARN: Code restructure failed: missing block: B:5:0x0022, code lost:
    
        r9 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x005c, code lost:
    
        if (r6.mTopGlow.onPull((-r10) / getHeight(), r7 / getWidth()) != false) goto L24;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void pullGlows(float r7, float r8, float r9, float r10) {
        /*
            r6 = this;
            r0 = 0
            int r1 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1))
            r2 = 1065353216(0x3f800000, float:1.0)
            r3 = 1
            if (r1 >= 0) goto L24
            r6.ensureLeftGlow()
            android.support.v4.widget.EdgeEffectCompat r1 = r6.mLeftGlow
            float r4 = -r8
            int r5 = r6.getWidth()
            float r5 = (float) r5
            float r4 = r4 / r5
            int r5 = r6.getHeight()
            float r5 = (float) r5
            float r9 = r9 / r5
            float r9 = r2 - r9
            boolean r9 = r1.onPull(r4, r9)
            if (r9 == 0) goto L41
        L22:
            r9 = 1
            goto L42
        L24:
            int r1 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1))
            if (r1 <= 0) goto L41
            r6.ensureRightGlow()
            android.support.v4.widget.EdgeEffectCompat r1 = r6.mRightGlow
            int r4 = r6.getWidth()
            float r4 = (float) r4
            float r4 = r8 / r4
            int r5 = r6.getHeight()
            float r5 = (float) r5
            float r9 = r9 / r5
            boolean r9 = r1.onPull(r4, r9)
            if (r9 == 0) goto L41
            goto L22
        L41:
            r9 = 0
        L42:
            int r1 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1))
            if (r1 >= 0) goto L5f
            r6.ensureTopGlow()
            android.support.v4.widget.EdgeEffectCompat r1 = r6.mTopGlow
            float r2 = -r10
            int r4 = r6.getHeight()
            float r4 = (float) r4
            float r2 = r2 / r4
            int r4 = r6.getWidth()
            float r4 = (float) r4
            float r7 = r7 / r4
            boolean r7 = r1.onPull(r2, r7)
            if (r7 == 0) goto L7d
            goto L7e
        L5f:
            int r1 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1))
            if (r1 <= 0) goto L7d
            r6.ensureBottomGlow()
            android.support.v4.widget.EdgeEffectCompat r1 = r6.mBottomGlow
            int r4 = r6.getHeight()
            float r4 = (float) r4
            float r4 = r10 / r4
            int r5 = r6.getWidth()
            float r5 = (float) r5
            float r7 = r7 / r5
            float r2 = r2 - r7
            boolean r7 = r1.onPull(r4, r2)
            if (r7 == 0) goto L7d
            goto L7e
        L7d:
            r3 = r9
        L7e:
            if (r3 != 0) goto L88
            int r7 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1))
            if (r7 != 0) goto L88
            int r7 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1))
            if (r7 == 0) goto L8b
        L88:
            android.support.v4.view.ViewCompat.postInvalidateOnAnimation(r6)
        L8b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.RecyclerView.pullGlows(float, float, float, float):void");
    }

    private void releaseGlows() {
        boolean onRelease = this.mLeftGlow != null ? this.mLeftGlow.onRelease() : false;
        if (this.mTopGlow != null) {
            onRelease |= this.mTopGlow.onRelease();
        }
        if (this.mRightGlow != null) {
            onRelease |= this.mRightGlow.onRelease();
        }
        if (this.mBottomGlow != null) {
            onRelease |= this.mBottomGlow.onRelease();
        }
        if (onRelease) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void considerReleasingGlowsOnScroll(int i, int i2) {
        boolean onRelease = (this.mLeftGlow == null || this.mLeftGlow.isFinished() || i <= 0) ? false : this.mLeftGlow.onRelease();
        if (this.mRightGlow != null && !this.mRightGlow.isFinished() && i < 0) {
            onRelease |= this.mRightGlow.onRelease();
        }
        if (this.mTopGlow != null && !this.mTopGlow.isFinished() && i2 > 0) {
            onRelease |= this.mTopGlow.onRelease();
        }
        if (this.mBottomGlow != null && !this.mBottomGlow.isFinished() && i2 < 0) {
            onRelease |= this.mBottomGlow.onRelease();
        }
        if (onRelease) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    void absorbGlows(int i, int i2) {
        if (i < 0) {
            ensureLeftGlow();
            this.mLeftGlow.onAbsorb(-i);
        } else if (i > 0) {
            ensureRightGlow();
            this.mRightGlow.onAbsorb(i);
        }
        if (i2 < 0) {
            ensureTopGlow();
            this.mTopGlow.onAbsorb(-i2);
        } else if (i2 > 0) {
            ensureBottomGlow();
            this.mBottomGlow.onAbsorb(i2);
        }
        if (i == 0 && i2 == 0) {
            return;
        }
        ViewCompat.postInvalidateOnAnimation(this);
    }

    void ensureLeftGlow() {
        if (this.mLeftGlow != null) {
            return;
        }
        this.mLeftGlow = new EdgeEffectCompat(getContext());
        if (this.mClipToPadding) {
            this.mLeftGlow.setSize((getMeasuredHeight() - getPaddingTop()) - getPaddingBottom(), (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight());
        } else {
            this.mLeftGlow.setSize(getMeasuredHeight(), getMeasuredWidth());
        }
    }

    void ensureRightGlow() {
        if (this.mRightGlow != null) {
            return;
        }
        this.mRightGlow = new EdgeEffectCompat(getContext());
        if (this.mClipToPadding) {
            this.mRightGlow.setSize((getMeasuredHeight() - getPaddingTop()) - getPaddingBottom(), (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight());
        } else {
            this.mRightGlow.setSize(getMeasuredHeight(), getMeasuredWidth());
        }
    }

    void ensureTopGlow() {
        if (this.mTopGlow != null) {
            return;
        }
        this.mTopGlow = new EdgeEffectCompat(getContext());
        if (this.mClipToPadding) {
            this.mTopGlow.setSize((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight(), (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom());
        } else {
            this.mTopGlow.setSize(getMeasuredWidth(), getMeasuredHeight());
        }
    }

    void ensureBottomGlow() {
        if (this.mBottomGlow != null) {
            return;
        }
        this.mBottomGlow = new EdgeEffectCompat(getContext());
        if (this.mClipToPadding) {
            this.mBottomGlow.setSize((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight(), (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom());
        } else {
            this.mBottomGlow.setSize(getMeasuredWidth(), getMeasuredHeight());
        }
    }

    void invalidateGlows() {
        this.mBottomGlow = null;
        this.mTopGlow = null;
        this.mRightGlow = null;
        this.mLeftGlow = null;
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public View focusSearch(View view, int i) {
        View onInterceptFocusSearch = this.mLayout.onInterceptFocusSearch(view, i);
        if (onInterceptFocusSearch != null) {
            return onInterceptFocusSearch;
        }
        View findNextFocus = FocusFinder.getInstance().findNextFocus(this, view, i);
        if (findNextFocus == null && this.mAdapter != null && this.mLayout != null && !isComputingLayout() && !this.mLayoutFrozen) {
            eatRequestLayout();
            findNextFocus = this.mLayout.onFocusSearchFailed(view, i, this.mRecycler, this.mState);
            resumeRequestLayout(false);
        }
        return findNextFocus != null ? findNextFocus : super.focusSearch(view, i);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void requestChildFocus(View view, View view2) {
        if (!this.mLayout.onRequestChildFocus(this, this.mState, view, view2) && view2 != null) {
            this.mTempRect.set(0, 0, view2.getWidth(), view2.getHeight());
            ViewGroup.LayoutParams layoutParams = view2.getLayoutParams();
            if (layoutParams instanceof LayoutParams) {
                LayoutParams layoutParams2 = (LayoutParams) layoutParams;
                if (!layoutParams2.mInsetsDirty) {
                    Rect rect = layoutParams2.mDecorInsets;
                    this.mTempRect.left -= rect.left;
                    this.mTempRect.right += rect.right;
                    this.mTempRect.top -= rect.top;
                    this.mTempRect.bottom += rect.bottom;
                }
            }
            offsetDescendantRectToMyCoords(view2, this.mTempRect);
            offsetRectIntoDescendantCoords(view, this.mTempRect);
            requestChildRectangleOnScreen(view, this.mTempRect, !this.mFirstLayoutComplete);
        }
        super.requestChildFocus(view, view2);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z) {
        return this.mLayout.requestChildRectangleOnScreen(this, view, rect, z);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void addFocusables(ArrayList<View> arrayList, int i, int i2) {
        if (this.mLayout == null || !this.mLayout.onAddFocusables(this, arrayList, i, i2)) {
            super.addFocusables(arrayList, i, i2);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mLayoutOrScrollCounter = 0;
        this.mIsAttached = true;
        this.mFirstLayoutComplete = false;
        if (this.mLayout != null) {
            this.mLayout.dispatchAttachedToWindow(this);
        }
        this.mPostedAnimatorRunner = false;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mItemAnimator != null) {
            this.mItemAnimator.endAnimations();
        }
        this.mFirstLayoutComplete = false;
        stopScroll();
        this.mIsAttached = false;
        if (this.mLayout != null) {
            this.mLayout.dispatchDetachedFromWindow(this, this.mRecycler);
        }
        removeCallbacks(this.mItemAnimatorRunner);
        this.mViewInfoStore.onDetach();
    }

    @Override // android.view.View
    public boolean isAttachedToWindow() {
        return this.mIsAttached;
    }

    void assertInLayoutOrScroll(String str) {
        if (isComputingLayout()) {
            return;
        }
        if (str == null) {
            throw new IllegalStateException("Cannot call this method unless RecyclerView is computing a layout or scrolling");
        }
        throw new IllegalStateException(str);
    }

    void assertNotInLayoutOrScroll(String str) {
        if (isComputingLayout()) {
            if (str == null) {
                throw new IllegalStateException("Cannot call this method while RecyclerView is computing a layout or scrolling");
            }
            throw new IllegalStateException(str);
        }
    }

    public void addOnItemTouchListener(OnItemTouchListener onItemTouchListener) {
        this.mOnItemTouchListeners.add(onItemTouchListener);
    }

    public void removeOnItemTouchListener(OnItemTouchListener onItemTouchListener) {
        this.mOnItemTouchListeners.remove(onItemTouchListener);
        if (this.mActiveOnItemTouchListener == onItemTouchListener) {
            this.mActiveOnItemTouchListener = null;
        }
    }

    private boolean dispatchOnItemTouchIntercept(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 3 || action == 0) {
            this.mActiveOnItemTouchListener = null;
        }
        int size = this.mOnItemTouchListeners.size();
        for (int i = 0; i < size; i++) {
            OnItemTouchListener onItemTouchListener = this.mOnItemTouchListeners.get(i);
            if (onItemTouchListener.onInterceptTouchEvent(this, motionEvent) && action != 3) {
                this.mActiveOnItemTouchListener = onItemTouchListener;
                return true;
            }
        }
        return false;
    }

    private boolean dispatchOnItemTouch(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (this.mActiveOnItemTouchListener != null) {
            if (action == 0) {
                this.mActiveOnItemTouchListener = null;
            } else {
                this.mActiveOnItemTouchListener.onTouchEvent(this, motionEvent);
                if (action == 3 || action == 1) {
                    this.mActiveOnItemTouchListener = null;
                }
                return true;
            }
        }
        if (action != 0) {
            int size = this.mOnItemTouchListeners.size();
            for (int i = 0; i < size; i++) {
                OnItemTouchListener onItemTouchListener = this.mOnItemTouchListeners.get(i);
                if (onItemTouchListener.onInterceptTouchEvent(this, motionEvent)) {
                    this.mActiveOnItemTouchListener = onItemTouchListener;
                    return true;
                }
            }
        }
        return false;
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        boolean z;
        if (this.mLayoutFrozen) {
            return false;
        }
        if (dispatchOnItemTouchIntercept(motionEvent)) {
            cancelTouch();
            return true;
        }
        if (this.mLayout == null) {
            return false;
        }
        boolean canScrollHorizontally = this.mLayout.canScrollHorizontally();
        boolean canScrollVertically = this.mLayout.canScrollVertically();
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(motionEvent);
        int actionMasked = MotionEventCompat.getActionMasked(motionEvent);
        int actionIndex = MotionEventCompat.getActionIndex(motionEvent);
        switch (actionMasked) {
            case 0:
                if (this.mIgnoreMotionEventTillDown) {
                    this.mIgnoreMotionEventTillDown = false;
                }
                this.mScrollPointerId = MotionEventCompat.getPointerId(motionEvent, 0);
                int x = (int) (motionEvent.getX() + 0.5f);
                this.mLastTouchX = x;
                this.mInitialTouchX = x;
                int y = (int) (motionEvent.getY() + 0.5f);
                this.mLastTouchY = y;
                this.mInitialTouchY = y;
                if (this.mScrollState == 2) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    setScrollState(1);
                }
                int[] iArr = this.mNestedOffsets;
                this.mNestedOffsets[1] = 0;
                iArr[0] = 0;
                int i = canScrollHorizontally ? 1 : 0;
                if (canScrollVertically) {
                    i |= 2;
                }
                startNestedScroll(i);
                break;
            case 1:
                this.mVelocityTracker.clear();
                stopNestedScroll();
                break;
            case 2:
                int findPointerIndex = MotionEventCompat.findPointerIndex(motionEvent, this.mScrollPointerId);
                if (findPointerIndex < 0) {
                    Log.e(TAG, "Error processing scroll; pointer index for id " + this.mScrollPointerId + " not found. Did any MotionEvents get skipped?");
                    return false;
                }
                int x2 = (int) (MotionEventCompat.getX(motionEvent, findPointerIndex) + 0.5f);
                int y2 = (int) (MotionEventCompat.getY(motionEvent, findPointerIndex) + 0.5f);
                if (this.mScrollState != 1) {
                    int i2 = x2 - this.mInitialTouchX;
                    int i3 = y2 - this.mInitialTouchY;
                    if (!canScrollHorizontally || Math.abs(i2) <= this.mTouchSlop) {
                        z = false;
                    } else {
                        this.mLastTouchX = this.mInitialTouchX + (this.mTouchSlop * (i2 < 0 ? -1 : 1));
                        z = true;
                    }
                    if (canScrollVertically && Math.abs(i3) > this.mTouchSlop) {
                        this.mLastTouchY = this.mInitialTouchY + (this.mTouchSlop * (i3 >= 0 ? 1 : -1));
                        z = true;
                    }
                    if (z) {
                        setScrollState(1);
                        break;
                    }
                }
                break;
            case 3:
                cancelTouch();
                break;
            case 5:
                this.mScrollPointerId = MotionEventCompat.getPointerId(motionEvent, actionIndex);
                int x3 = (int) (MotionEventCompat.getX(motionEvent, actionIndex) + 0.5f);
                this.mLastTouchX = x3;
                this.mInitialTouchX = x3;
                int y3 = (int) (MotionEventCompat.getY(motionEvent, actionIndex) + 0.5f);
                this.mLastTouchY = y3;
                this.mInitialTouchY = y3;
                break;
            case 6:
                onPointerUp(motionEvent);
                break;
        }
        return this.mScrollState == 1;
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void requestDisallowInterceptTouchEvent(boolean z) {
        int size = this.mOnItemTouchListeners.size();
        for (int i = 0; i < size; i++) {
            this.mOnItemTouchListeners.get(i).onRequestDisallowInterceptTouchEvent(z);
        }
        super.requestDisallowInterceptTouchEvent(z);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean z;
        boolean z2 = false;
        if (this.mLayoutFrozen || this.mIgnoreMotionEventTillDown) {
            return false;
        }
        if (dispatchOnItemTouch(motionEvent)) {
            cancelTouch();
            return true;
        }
        if (this.mLayout == null) {
            return false;
        }
        boolean canScrollHorizontally = this.mLayout.canScrollHorizontally();
        boolean canScrollVertically = this.mLayout.canScrollVertically();
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        MotionEvent obtain = MotionEvent.obtain(motionEvent);
        int actionMasked = MotionEventCompat.getActionMasked(motionEvent);
        int actionIndex = MotionEventCompat.getActionIndex(motionEvent);
        if (actionMasked == 0) {
            int[] iArr = this.mNestedOffsets;
            this.mNestedOffsets[1] = 0;
            iArr[0] = 0;
        }
        obtain.offsetLocation(this.mNestedOffsets[0], this.mNestedOffsets[1]);
        switch (actionMasked) {
            case 0:
                this.mScrollPointerId = MotionEventCompat.getPointerId(motionEvent, 0);
                int x = (int) (motionEvent.getX() + 0.5f);
                this.mLastTouchX = x;
                this.mInitialTouchX = x;
                int y = (int) (motionEvent.getY() + 0.5f);
                this.mLastTouchY = y;
                this.mInitialTouchY = y;
                int i = canScrollHorizontally ? 1 : 0;
                if (canScrollVertically) {
                    i |= 2;
                }
                startNestedScroll(i);
                break;
            case 1:
                this.mVelocityTracker.addMovement(obtain);
                this.mVelocityTracker.computeCurrentVelocity(1000, this.mMaxFlingVelocity);
                float f = canScrollHorizontally ? -VelocityTrackerCompat.getXVelocity(this.mVelocityTracker, this.mScrollPointerId) : 0.0f;
                float f2 = canScrollVertically ? -VelocityTrackerCompat.getYVelocity(this.mVelocityTracker, this.mScrollPointerId) : 0.0f;
                if ((f == 0.0f && f2 == 0.0f) || !fling((int) f, (int) f2)) {
                    setScrollState(0);
                }
                resetTouch();
                z2 = true;
                break;
            case 2:
                int findPointerIndex = MotionEventCompat.findPointerIndex(motionEvent, this.mScrollPointerId);
                if (findPointerIndex < 0) {
                    Log.e(TAG, "Error processing scroll; pointer index for id " + this.mScrollPointerId + " not found. Did any MotionEvents get skipped?");
                    return false;
                }
                int x2 = (int) (MotionEventCompat.getX(motionEvent, findPointerIndex) + 0.5f);
                int y2 = (int) (MotionEventCompat.getY(motionEvent, findPointerIndex) + 0.5f);
                int i2 = this.mLastTouchX - x2;
                int i3 = this.mLastTouchY - y2;
                if (dispatchNestedPreScroll(i2, i3, this.mScrollConsumed, this.mScrollOffset)) {
                    i2 -= this.mScrollConsumed[0];
                    i3 -= this.mScrollConsumed[1];
                    obtain.offsetLocation(this.mScrollOffset[0], this.mScrollOffset[1]);
                    int[] iArr2 = this.mNestedOffsets;
                    iArr2[0] = iArr2[0] + this.mScrollOffset[0];
                    int[] iArr3 = this.mNestedOffsets;
                    iArr3[1] = iArr3[1] + this.mScrollOffset[1];
                }
                if (this.mScrollState != 1) {
                    if (!canScrollHorizontally || Math.abs(i2) <= this.mTouchSlop) {
                        z = false;
                    } else {
                        if (i2 > 0) {
                            i2 -= this.mTouchSlop;
                        } else {
                            i2 += this.mTouchSlop;
                        }
                        z = true;
                    }
                    if (canScrollVertically && Math.abs(i3) > this.mTouchSlop) {
                        if (i3 > 0) {
                            i3 -= this.mTouchSlop;
                        } else {
                            i3 += this.mTouchSlop;
                        }
                        z = true;
                    }
                    if (z) {
                        setScrollState(1);
                    }
                }
                if (this.mScrollState == 1) {
                    this.mLastTouchX = x2 - this.mScrollOffset[0];
                    this.mLastTouchY = y2 - this.mScrollOffset[1];
                    if (!canScrollHorizontally) {
                        i2 = 0;
                    }
                    if (!canScrollVertically) {
                        i3 = 0;
                    }
                    if (scrollByInternal(i2, i3, obtain)) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    }
                }
                break;
            case 3:
                cancelTouch();
                break;
            case 5:
                this.mScrollPointerId = MotionEventCompat.getPointerId(motionEvent, actionIndex);
                int x3 = (int) (MotionEventCompat.getX(motionEvent, actionIndex) + 0.5f);
                this.mLastTouchX = x3;
                this.mInitialTouchX = x3;
                int y3 = (int) (MotionEventCompat.getY(motionEvent, actionIndex) + 0.5f);
                this.mLastTouchY = y3;
                this.mInitialTouchY = y3;
                break;
            case 6:
                onPointerUp(motionEvent);
                break;
        }
        if (!z2) {
            this.mVelocityTracker.addMovement(obtain);
        }
        obtain.recycle();
        return true;
    }

    private void resetTouch() {
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.clear();
        }
        stopNestedScroll();
        releaseGlows();
    }

    private void cancelTouch() {
        resetTouch();
        setScrollState(0);
    }

    private void onPointerUp(MotionEvent motionEvent) {
        int actionIndex = MotionEventCompat.getActionIndex(motionEvent);
        if (MotionEventCompat.getPointerId(motionEvent, actionIndex) == this.mScrollPointerId) {
            int i = actionIndex == 0 ? 1 : 0;
            this.mScrollPointerId = MotionEventCompat.getPointerId(motionEvent, i);
            int x = (int) (MotionEventCompat.getX(motionEvent, i) + 0.5f);
            this.mLastTouchX = x;
            this.mInitialTouchX = x;
            int y = (int) (MotionEventCompat.getY(motionEvent, i) + 0.5f);
            this.mLastTouchY = y;
            this.mInitialTouchY = y;
        }
    }

    @Override // android.view.View
    public boolean onGenericMotionEvent(MotionEvent motionEvent) {
        if (this.mLayout != null && !this.mLayoutFrozen && (MotionEventCompat.getSource(motionEvent) & 2) != 0 && motionEvent.getAction() == 8) {
            float f = this.mLayout.canScrollVertically() ? -MotionEventCompat.getAxisValue(motionEvent, 9) : 0.0f;
            float axisValue = this.mLayout.canScrollHorizontally() ? MotionEventCompat.getAxisValue(motionEvent, 10) : 0.0f;
            if (f != 0.0f || axisValue != 0.0f) {
                float scrollFactor = getScrollFactor();
                scrollByInternal((int) (axisValue * scrollFactor), (int) (f * scrollFactor), motionEvent);
            }
        }
        return false;
    }

    private float getScrollFactor() {
        if (this.mScrollFactor == Float.MIN_VALUE) {
            TypedValue typedValue = new TypedValue();
            if (!getContext().getTheme().resolveAttribute(android.R.attr.listPreferredItemHeight, typedValue, true)) {
                return 0.0f;
            }
            this.mScrollFactor = typedValue.getDimension(getContext().getResources().getDisplayMetrics());
        }
        return this.mScrollFactor;
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        if (this.mAdapterUpdateDuringMeasure) {
            eatRequestLayout();
            processAdapterUpdatesAndSetAnimationFlags();
            if (!this.mState.mRunPredictiveAnimations) {
                this.mAdapterHelper.consumeUpdatesInOnePass();
                this.mState.mInPreLayout = false;
            } else {
                this.mState.mInPreLayout = true;
            }
            this.mAdapterUpdateDuringMeasure = false;
            resumeRequestLayout(false);
        }
        if (this.mAdapter != null) {
            this.mState.mItemCount = this.mAdapter.getItemCount();
        } else {
            this.mState.mItemCount = 0;
        }
        if (this.mLayout == null) {
            defaultOnMeasure(i, i2);
        } else {
            this.mLayout.onMeasure(this.mRecycler, this.mState, i, i2);
        }
        this.mState.mInPreLayout = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void defaultOnMeasure(int i, int i2) {
        int mode = View.MeasureSpec.getMode(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        if (mode != Integer.MIN_VALUE && mode != 1073741824) {
            size = ViewCompat.getMinimumWidth(this);
        }
        if (mode2 != Integer.MIN_VALUE && mode2 != 1073741824) {
            size2 = ViewCompat.getMinimumHeight(this);
        }
        setMeasuredDimension(size, size2);
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (i == i3 && i2 == i4) {
            return;
        }
        invalidateGlows();
    }

    public void setItemAnimator(ItemAnimator itemAnimator) {
        if (this.mItemAnimator != null) {
            this.mItemAnimator.endAnimations();
            this.mItemAnimator.setListener(null);
        }
        this.mItemAnimator = itemAnimator;
        if (this.mItemAnimator != null) {
            this.mItemAnimator.setListener(this.mItemAnimatorListener);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onEnterLayoutOrScroll() {
        this.mLayoutOrScrollCounter++;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onExitLayoutOrScroll() {
        this.mLayoutOrScrollCounter--;
        if (this.mLayoutOrScrollCounter < 1) {
            this.mLayoutOrScrollCounter = 0;
            dispatchContentChangedIfNecessary();
        }
    }

    boolean isAccessibilityEnabled() {
        return this.mAccessibilityManager != null && this.mAccessibilityManager.isEnabled();
    }

    private void dispatchContentChangedIfNecessary() {
        int i = this.mEatenAccessibilityChangeFlags;
        this.mEatenAccessibilityChangeFlags = 0;
        if (i == 0 || !isAccessibilityEnabled()) {
            return;
        }
        AccessibilityEvent obtain = AccessibilityEvent.obtain();
        obtain.setEventType(2048);
        AccessibilityEventCompat.setContentChangeTypes(obtain, i);
        sendAccessibilityEventUnchecked(obtain);
    }

    public boolean isComputingLayout() {
        return this.mLayoutOrScrollCounter > 0;
    }

    boolean shouldDeferAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (!isComputingLayout()) {
            return false;
        }
        int contentChangeTypes = accessibilityEvent != null ? AccessibilityEventCompat.getContentChangeTypes(accessibilityEvent) : 0;
        if (contentChangeTypes == 0) {
            contentChangeTypes = 0;
        }
        this.mEatenAccessibilityChangeFlags = contentChangeTypes | this.mEatenAccessibilityChangeFlags;
        return true;
    }

    @Override // android.view.View, android.view.accessibility.AccessibilityEventSource
    public void sendAccessibilityEventUnchecked(AccessibilityEvent accessibilityEvent) {
        if (shouldDeferAccessibilityEvent(accessibilityEvent)) {
            return;
        }
        super.sendAccessibilityEventUnchecked(accessibilityEvent);
    }

    public ItemAnimator getItemAnimator() {
        return this.mItemAnimator;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postAnimationRunner() {
        if (this.mPostedAnimatorRunner || !this.mIsAttached) {
            return;
        }
        ViewCompat.postOnAnimation(this, this.mItemAnimatorRunner);
        this.mPostedAnimatorRunner = true;
    }

    private boolean predictiveItemAnimationsEnabled() {
        return this.mItemAnimator != null && this.mLayout.supportsPredictiveItemAnimations();
    }

    private void processAdapterUpdatesAndSetAnimationFlags() {
        if (this.mDataSetHasChangedAfterLayout) {
            this.mAdapterHelper.reset();
            markKnownViewsInvalid();
            this.mLayout.onItemsChanged(this);
        }
        if (this.mItemAnimator != null && this.mLayout.supportsPredictiveItemAnimations()) {
            this.mAdapterHelper.preProcess();
        } else {
            this.mAdapterHelper.consumeUpdatesInOnePass();
        }
        boolean z = false;
        boolean z2 = this.mItemsAddedOrRemoved || this.mItemsChanged;
        this.mState.mRunSimpleAnimations = this.mFirstLayoutComplete && this.mItemAnimator != null && (this.mDataSetHasChangedAfterLayout || z2 || this.mLayout.mRequestedSimpleAnimations) && (!this.mDataSetHasChangedAfterLayout || this.mAdapter.hasStableIds());
        State state = this.mState;
        if (this.mState.mRunSimpleAnimations && z2 && !this.mDataSetHasChangedAfterLayout && predictiveItemAnimationsEnabled()) {
            z = true;
        }
        state.mRunPredictiveAnimations = z;
    }

    void dispatchLayout() {
        if (this.mAdapter == null) {
            Log.e(TAG, "No adapter attached; skipping layout");
            return;
        }
        if (this.mLayout == null) {
            Log.e(TAG, "No layout manager attached; skipping layout");
            return;
        }
        this.mViewInfoStore.clear();
        eatRequestLayout();
        onEnterLayoutOrScroll();
        processAdapterUpdatesAndSetAnimationFlags();
        this.mState.mTrackOldChangeHolders = this.mState.mRunSimpleAnimations && this.mItemsChanged;
        this.mItemsChanged = false;
        this.mItemsAddedOrRemoved = false;
        this.mState.mInPreLayout = this.mState.mRunPredictiveAnimations;
        this.mState.mItemCount = this.mAdapter.getItemCount();
        findMinMaxChildLayoutPositions(this.mMinMaxLayoutPositions);
        if (this.mState.mRunSimpleAnimations) {
            int childCount = this.mChildHelper.getChildCount();
            for (int i = 0; i < childCount; i++) {
                ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
                if (!childViewHolderInt.shouldIgnore() && (!childViewHolderInt.isInvalid() || this.mAdapter.hasStableIds())) {
                    this.mViewInfoStore.addToPreLayout(childViewHolderInt, this.mItemAnimator.recordPreLayoutInformation(this.mState, childViewHolderInt, ItemAnimator.buildAdapterChangeFlagsForAnimations(childViewHolderInt), childViewHolderInt.getUnmodifiedPayloads()));
                    if (this.mState.mTrackOldChangeHolders && childViewHolderInt.isUpdated() && !childViewHolderInt.isRemoved() && !childViewHolderInt.shouldIgnore() && !childViewHolderInt.isInvalid()) {
                        this.mViewInfoStore.addToOldChangeHolders(getChangedHolderKey(childViewHolderInt), childViewHolderInt);
                    }
                }
            }
        }
        if (this.mState.mRunPredictiveAnimations) {
            saveOldPositions();
            boolean z = this.mState.mStructureChanged;
            this.mState.mStructureChanged = false;
            this.mLayout.onLayoutChildren(this.mRecycler, this.mState);
            this.mState.mStructureChanged = z;
            for (int i2 = 0; i2 < this.mChildHelper.getChildCount(); i2++) {
                ViewHolder childViewHolderInt2 = getChildViewHolderInt(this.mChildHelper.getChildAt(i2));
                if (!childViewHolderInt2.shouldIgnore() && !this.mViewInfoStore.isInPreLayout(childViewHolderInt2)) {
                    int buildAdapterChangeFlagsForAnimations = ItemAnimator.buildAdapterChangeFlagsForAnimations(childViewHolderInt2);
                    boolean hasAnyOfTheFlags = childViewHolderInt2.hasAnyOfTheFlags(8192);
                    if (!hasAnyOfTheFlags) {
                        buildAdapterChangeFlagsForAnimations |= 4096;
                    }
                    ItemAnimator.ItemHolderInfo recordPreLayoutInformation = this.mItemAnimator.recordPreLayoutInformation(this.mState, childViewHolderInt2, buildAdapterChangeFlagsForAnimations, childViewHolderInt2.getUnmodifiedPayloads());
                    if (hasAnyOfTheFlags) {
                        recordAnimationInfoIfBouncedHiddenView(childViewHolderInt2, recordPreLayoutInformation);
                    } else {
                        this.mViewInfoStore.addToAppearedInPreLayoutHolders(childViewHolderInt2, recordPreLayoutInformation);
                    }
                }
            }
            clearOldPositions();
            this.mAdapterHelper.consumePostponedUpdates();
        } else {
            clearOldPositions();
        }
        this.mState.mItemCount = this.mAdapter.getItemCount();
        this.mState.mDeletedInvisibleItemCountSincePreviousLayout = 0;
        this.mState.mInPreLayout = false;
        this.mLayout.onLayoutChildren(this.mRecycler, this.mState);
        this.mState.mStructureChanged = false;
        this.mPendingSavedState = null;
        this.mState.mRunSimpleAnimations = this.mState.mRunSimpleAnimations && this.mItemAnimator != null;
        if (this.mState.mRunSimpleAnimations) {
            int childCount2 = this.mChildHelper.getChildCount();
            for (int i3 = 0; i3 < childCount2; i3++) {
                ViewHolder childViewHolderInt3 = getChildViewHolderInt(this.mChildHelper.getChildAt(i3));
                if (!childViewHolderInt3.shouldIgnore()) {
                    long changedHolderKey = getChangedHolderKey(childViewHolderInt3);
                    ItemAnimator.ItemHolderInfo recordPostLayoutInformation = this.mItemAnimator.recordPostLayoutInformation(this.mState, childViewHolderInt3);
                    ViewHolder fromOldChangeHolders = this.mViewInfoStore.getFromOldChangeHolders(changedHolderKey);
                    if (fromOldChangeHolders != null && !fromOldChangeHolders.shouldIgnore()) {
                        animateChange(fromOldChangeHolders, childViewHolderInt3, this.mViewInfoStore.popFromPreLayout(fromOldChangeHolders), recordPostLayoutInformation);
                    } else {
                        this.mViewInfoStore.addToPostLayout(childViewHolderInt3, recordPostLayoutInformation);
                    }
                }
            }
            this.mViewInfoStore.process(this.mViewInfoProcessCallback);
        }
        resumeRequestLayout(false);
        this.mLayout.removeAndRecycleScrapInt(this.mRecycler);
        this.mState.mPreviousLayoutItemCount = this.mState.mItemCount;
        this.mDataSetHasChangedAfterLayout = false;
        this.mState.mRunSimpleAnimations = false;
        this.mState.mRunPredictiveAnimations = false;
        onExitLayoutOrScroll();
        this.mLayout.mRequestedSimpleAnimations = false;
        if (this.mRecycler.mChangedScrap != null) {
            this.mRecycler.mChangedScrap.clear();
        }
        this.mViewInfoStore.clear();
        if (didChildRangeChange(this.mMinMaxLayoutPositions[0], this.mMinMaxLayoutPositions[1])) {
            dispatchOnScrolled(0, 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void recordAnimationInfoIfBouncedHiddenView(ViewHolder viewHolder, ItemAnimator.ItemHolderInfo itemHolderInfo) {
        viewHolder.setFlags(0, 8192);
        if (this.mState.mTrackOldChangeHolders && viewHolder.isUpdated() && !viewHolder.isRemoved() && !viewHolder.shouldIgnore()) {
            this.mViewInfoStore.addToOldChangeHolders(getChangedHolderKey(viewHolder), viewHolder);
        }
        this.mViewInfoStore.addToPreLayout(viewHolder, itemHolderInfo);
    }

    private void findMinMaxChildLayoutPositions(int[] iArr) {
        int childCount = this.mChildHelper.getChildCount();
        if (childCount == 0) {
            iArr[0] = 0;
            iArr[1] = 0;
            return;
        }
        int i = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        int i2 = Integer.MIN_VALUE;
        for (int i3 = 0; i3 < childCount; i3++) {
            ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getChildAt(i3));
            if (!childViewHolderInt.shouldIgnore()) {
                int layoutPosition = childViewHolderInt.getLayoutPosition();
                if (layoutPosition < i) {
                    i = layoutPosition;
                }
                if (layoutPosition > i2) {
                    i2 = layoutPosition;
                }
            }
        }
        iArr[0] = i;
        iArr[1] = i2;
    }

    private boolean didChildRangeChange(int i, int i2) {
        int layoutPosition;
        int childCount = this.mChildHelper.getChildCount();
        if (childCount == 0) {
            return (i == 0 && i2 == 0) ? false : true;
        }
        for (int i3 = 0; i3 < childCount; i3++) {
            ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getChildAt(i3));
            if (!childViewHolderInt.shouldIgnore() && ((layoutPosition = childViewHolderInt.getLayoutPosition()) < i || layoutPosition > i2)) {
                return true;
            }
        }
        return false;
    }

    @Override // android.view.ViewGroup
    protected void removeDetachedView(View view, boolean z) {
        ViewHolder childViewHolderInt = getChildViewHolderInt(view);
        if (childViewHolderInt != null) {
            if (childViewHolderInt.isTmpDetached()) {
                childViewHolderInt.clearTmpDetachFlag();
            } else if (!childViewHolderInt.shouldIgnore()) {
                throw new IllegalArgumentException("Called removeDetachedView with a view which is not flagged as tmp detached." + childViewHolderInt);
            }
        }
        dispatchChildDetached(view);
        super.removeDetachedView(view, z);
    }

    long getChangedHolderKey(ViewHolder viewHolder) {
        return this.mAdapter.hasStableIds() ? viewHolder.getItemId() : viewHolder.mPosition;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void animateAppearance(@NonNull ViewHolder viewHolder, @Nullable ItemAnimator.ItemHolderInfo itemHolderInfo, @NonNull ItemAnimator.ItemHolderInfo itemHolderInfo2) {
        viewHolder.setIsRecyclable(false);
        if (this.mItemAnimator.animateAppearance(viewHolder, itemHolderInfo, itemHolderInfo2)) {
            postAnimationRunner();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void animateDisappearance(@NonNull ViewHolder viewHolder, @NonNull ItemAnimator.ItemHolderInfo itemHolderInfo, @Nullable ItemAnimator.ItemHolderInfo itemHolderInfo2) {
        addAnimatingView(viewHolder);
        viewHolder.setIsRecyclable(false);
        if (this.mItemAnimator.animateDisappearance(viewHolder, itemHolderInfo, itemHolderInfo2)) {
            postAnimationRunner();
        }
    }

    private void animateChange(@NonNull ViewHolder viewHolder, @NonNull ViewHolder viewHolder2, @NonNull ItemAnimator.ItemHolderInfo itemHolderInfo, @NonNull ItemAnimator.ItemHolderInfo itemHolderInfo2) {
        viewHolder.setIsRecyclable(false);
        if (viewHolder != viewHolder2) {
            viewHolder.mShadowedHolder = viewHolder2;
            addAnimatingView(viewHolder);
            this.mRecycler.unscrapView(viewHolder);
            viewHolder2.setIsRecyclable(false);
            viewHolder2.mShadowingHolder = viewHolder;
        }
        if (this.mItemAnimator.animateChange(viewHolder, viewHolder2, itemHolderInfo, itemHolderInfo2)) {
            postAnimationRunner();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        eatRequestLayout();
        TraceCompat.beginSection(TRACE_ON_LAYOUT_TAG);
        dispatchLayout();
        TraceCompat.endSection();
        resumeRequestLayout(false);
        this.mFirstLayoutComplete = true;
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (!this.mEatRequestLayout && !this.mLayoutFrozen) {
            super.requestLayout();
        } else {
            this.mLayoutRequestEaten = true;
        }
    }

    void markItemDecorInsetsDirty() {
        int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i = 0; i < unfilteredChildCount; i++) {
            ((LayoutParams) this.mChildHelper.getUnfilteredChildAt(i).getLayoutParams()).mInsetsDirty = true;
        }
        this.mRecycler.markItemDecorInsetsDirty();
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        boolean z;
        boolean z2;
        super.draw(canvas);
        int size = this.mItemDecorations.size();
        boolean z3 = false;
        for (int i = 0; i < size; i++) {
            this.mItemDecorations.get(i).onDrawOver(canvas, this, this.mState);
        }
        if (this.mLeftGlow == null || this.mLeftGlow.isFinished()) {
            z = false;
        } else {
            int save = canvas.save();
            int paddingBottom = this.mClipToPadding ? getPaddingBottom() : 0;
            canvas.rotate(270.0f);
            canvas.translate((-getHeight()) + paddingBottom, 0.0f);
            z = this.mLeftGlow != null && this.mLeftGlow.draw(canvas);
            canvas.restoreToCount(save);
        }
        if (this.mTopGlow != null && !this.mTopGlow.isFinished()) {
            int save2 = canvas.save();
            if (this.mClipToPadding) {
                canvas.translate(getPaddingLeft(), getPaddingTop());
            }
            z |= this.mTopGlow != null && this.mTopGlow.draw(canvas);
            canvas.restoreToCount(save2);
        }
        if (this.mRightGlow != null && !this.mRightGlow.isFinished()) {
            int save3 = canvas.save();
            int width = getWidth();
            int paddingTop = this.mClipToPadding ? getPaddingTop() : 0;
            canvas.rotate(90.0f);
            canvas.translate(-paddingTop, -width);
            z |= this.mRightGlow != null && this.mRightGlow.draw(canvas);
            canvas.restoreToCount(save3);
        }
        if (this.mBottomGlow == null || this.mBottomGlow.isFinished()) {
            z2 = z;
        } else {
            int save4 = canvas.save();
            canvas.rotate(180.0f);
            if (this.mClipToPadding) {
                canvas.translate((-getWidth()) + getPaddingRight(), (-getHeight()) + getPaddingBottom());
            } else {
                canvas.translate(-getWidth(), -getHeight());
            }
            if (this.mBottomGlow != null && this.mBottomGlow.draw(canvas)) {
                z3 = true;
            }
            z2 = z3 | z;
            canvas.restoreToCount(save4);
        }
        if (!z2 && this.mItemAnimator != null && this.mItemDecorations.size() > 0 && this.mItemAnimator.isRunning()) {
            z2 = true;
        }
        if (z2) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int size = this.mItemDecorations.size();
        for (int i = 0; i < size; i++) {
            this.mItemDecorations.get(i).onDraw(canvas, this, this.mState);
        }
    }

    @Override // android.view.ViewGroup
    protected boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return (layoutParams instanceof LayoutParams) && this.mLayout.checkLayoutParams((LayoutParams) layoutParams);
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        if (this.mLayout == null) {
            throw new IllegalStateException("RecyclerView has no LayoutManager");
        }
        return this.mLayout.generateDefaultLayoutParams();
    }

    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        if (this.mLayout == null) {
            throw new IllegalStateException("RecyclerView has no LayoutManager");
        }
        return this.mLayout.generateLayoutParams(getContext(), attributeSet);
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        if (this.mLayout == null) {
            throw new IllegalStateException("RecyclerView has no LayoutManager");
        }
        return this.mLayout.generateLayoutParams(layoutParams);
    }

    public boolean isAnimating() {
        return this.mItemAnimator != null && this.mItemAnimator.isRunning();
    }

    void saveOldPositions() {
        int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i = 0; i < unfilteredChildCount; i++) {
            ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (!childViewHolderInt.shouldIgnore()) {
                childViewHolderInt.saveOldPosition();
            }
        }
    }

    void clearOldPositions() {
        int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i = 0; i < unfilteredChildCount; i++) {
            ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (!childViewHolderInt.shouldIgnore()) {
                childViewHolderInt.clearOldPosition();
            }
        }
        this.mRecycler.clearOldPositions();
    }

    void offsetPositionRecordsForMove(int i, int i2) {
        int i3;
        int i4;
        int i5;
        int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
        if (i < i2) {
            i4 = i;
            i3 = i2;
            i5 = -1;
        } else {
            i3 = i;
            i4 = i2;
            i5 = 1;
        }
        for (int i6 = 0; i6 < unfilteredChildCount; i6++) {
            ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i6));
            if (childViewHolderInt != null && childViewHolderInt.mPosition >= i4 && childViewHolderInt.mPosition <= i3) {
                if (childViewHolderInt.mPosition == i) {
                    childViewHolderInt.offsetPosition(i2 - i, false);
                } else {
                    childViewHolderInt.offsetPosition(i5, false);
                }
                this.mState.mStructureChanged = true;
            }
        }
        this.mRecycler.offsetPositionRecordsForMove(i, i2);
        requestLayout();
    }

    void offsetPositionRecordsForInsert(int i, int i2) {
        int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i3 = 0; i3 < unfilteredChildCount; i3++) {
            ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i3));
            if (childViewHolderInt != null && !childViewHolderInt.shouldIgnore() && childViewHolderInt.mPosition >= i) {
                childViewHolderInt.offsetPosition(i2, false);
                this.mState.mStructureChanged = true;
            }
        }
        this.mRecycler.offsetPositionRecordsForInsert(i, i2);
        requestLayout();
    }

    void offsetPositionRecordsForRemove(int i, int i2, boolean z) {
        int i3 = i + i2;
        int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i4 = 0; i4 < unfilteredChildCount; i4++) {
            ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i4));
            if (childViewHolderInt != null && !childViewHolderInt.shouldIgnore()) {
                if (childViewHolderInt.mPosition >= i3) {
                    childViewHolderInt.offsetPosition(-i2, z);
                    this.mState.mStructureChanged = true;
                } else if (childViewHolderInt.mPosition >= i) {
                    childViewHolderInt.flagRemovedAndOffsetPosition(i - 1, -i2, z);
                    this.mState.mStructureChanged = true;
                }
            }
        }
        this.mRecycler.offsetPositionRecordsForRemove(i, i2, z);
        requestLayout();
    }

    void viewRangeUpdate(int i, int i2, Object obj) {
        int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
        int i3 = i + i2;
        for (int i4 = 0; i4 < unfilteredChildCount; i4++) {
            View unfilteredChildAt = this.mChildHelper.getUnfilteredChildAt(i4);
            ViewHolder childViewHolderInt = getChildViewHolderInt(unfilteredChildAt);
            if (childViewHolderInt != null && !childViewHolderInt.shouldIgnore() && childViewHolderInt.mPosition >= i && childViewHolderInt.mPosition < i3) {
                childViewHolderInt.addFlags(2);
                childViewHolderInt.addChangePayload(obj);
                ((LayoutParams) unfilteredChildAt.getLayoutParams()).mInsetsDirty = true;
            }
        }
        this.mRecycler.viewRangeUpdate(i, i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean canReuseUpdatedViewHolder(ViewHolder viewHolder) {
        return this.mItemAnimator == null || this.mItemAnimator.canReuseUpdatedViewHolder(viewHolder);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setDataSetChangedAfterLayout() {
        if (this.mDataSetHasChangedAfterLayout) {
            return;
        }
        this.mDataSetHasChangedAfterLayout = true;
        int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i = 0; i < unfilteredChildCount; i++) {
            ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (childViewHolderInt != null && !childViewHolderInt.shouldIgnore()) {
                childViewHolderInt.addFlags(512);
            }
        }
        this.mRecycler.setAdapterPositionsAsUnknown();
    }

    void markKnownViewsInvalid() {
        int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i = 0; i < unfilteredChildCount; i++) {
            ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (childViewHolderInt != null && !childViewHolderInt.shouldIgnore()) {
                childViewHolderInt.addFlags(6);
            }
        }
        markItemDecorInsetsDirty();
        this.mRecycler.markKnownViewsInvalid();
    }

    public void invalidateItemDecorations() {
        if (this.mItemDecorations.size() == 0) {
            return;
        }
        if (this.mLayout != null) {
            this.mLayout.assertNotInLayoutOrScroll("Cannot invalidate item decorations during a scroll or layout");
        }
        markItemDecorInsetsDirty();
        requestLayout();
    }

    public ViewHolder getChildViewHolder(View view) {
        ViewParent parent = view.getParent();
        if (parent != null && parent != this) {
            throw new IllegalArgumentException("View " + view + " is not a direct child of " + this);
        }
        return getChildViewHolderInt(view);
    }

    static ViewHolder getChildViewHolderInt(View view) {
        if (view == null) {
            return null;
        }
        return ((LayoutParams) view.getLayoutParams()).mViewHolder;
    }

    @Deprecated
    public int getChildPosition(View view) {
        return getChildAdapterPosition(view);
    }

    public int getChildAdapterPosition(View view) {
        ViewHolder childViewHolderInt = getChildViewHolderInt(view);
        if (childViewHolderInt != null) {
            return childViewHolderInt.getAdapterPosition();
        }
        return -1;
    }

    public int getChildLayoutPosition(View view) {
        ViewHolder childViewHolderInt = getChildViewHolderInt(view);
        if (childViewHolderInt != null) {
            return childViewHolderInt.getLayoutPosition();
        }
        return -1;
    }

    public long getChildItemId(View view) {
        ViewHolder childViewHolderInt;
        if (this.mAdapter == null || !this.mAdapter.hasStableIds() || (childViewHolderInt = getChildViewHolderInt(view)) == null) {
            return -1L;
        }
        return childViewHolderInt.getItemId();
    }

    @Deprecated
    public ViewHolder findViewHolderForPosition(int i) {
        return findViewHolderForPosition(i, false);
    }

    public ViewHolder findViewHolderForLayoutPosition(int i) {
        return findViewHolderForPosition(i, false);
    }

    public ViewHolder findViewHolderForAdapterPosition(int i) {
        if (this.mDataSetHasChangedAfterLayout) {
            return null;
        }
        int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i2 = 0; i2 < unfilteredChildCount; i2++) {
            ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i2));
            if (childViewHolderInt != null && !childViewHolderInt.isRemoved() && getAdapterPositionFor(childViewHolderInt) == i) {
                return childViewHolderInt;
            }
        }
        return null;
    }

    ViewHolder findViewHolderForPosition(int i, boolean z) {
        int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i2 = 0; i2 < unfilteredChildCount; i2++) {
            ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i2));
            if (childViewHolderInt != null && !childViewHolderInt.isRemoved()) {
                if (z) {
                    if (childViewHolderInt.mPosition == i) {
                        return childViewHolderInt;
                    }
                } else if (childViewHolderInt.getLayoutPosition() == i) {
                    return childViewHolderInt;
                }
            }
        }
        return null;
    }

    public ViewHolder findViewHolderForItemId(long j) {
        int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i = 0; i < unfilteredChildCount; i++) {
            ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (childViewHolderInt != null && childViewHolderInt.getItemId() == j) {
                return childViewHolderInt;
            }
        }
        return null;
    }

    public View findChildViewUnder(float f, float f2) {
        for (int childCount = this.mChildHelper.getChildCount() - 1; childCount >= 0; childCount--) {
            View childAt = this.mChildHelper.getChildAt(childCount);
            float translationX = ViewCompat.getTranslationX(childAt);
            float translationY = ViewCompat.getTranslationY(childAt);
            if (f >= childAt.getLeft() + translationX && f <= childAt.getRight() + translationX && f2 >= childAt.getTop() + translationY && f2 <= childAt.getBottom() + translationY) {
                return childAt;
            }
        }
        return null;
    }

    @Override // android.view.ViewGroup
    public boolean drawChild(Canvas canvas, View view, long j) {
        return super.drawChild(canvas, view, j);
    }

    public void offsetChildrenVertical(int i) {
        int childCount = this.mChildHelper.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            this.mChildHelper.getChildAt(i2).offsetTopAndBottom(i);
        }
    }

    public void offsetChildrenHorizontal(int i) {
        int childCount = this.mChildHelper.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            this.mChildHelper.getChildAt(i2).offsetLeftAndRight(i);
        }
    }

    Rect getItemDecorInsetsForChild(View view) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        if (!layoutParams.mInsetsDirty) {
            return layoutParams.mDecorInsets;
        }
        Rect rect = layoutParams.mDecorInsets;
        rect.set(0, 0, 0, 0);
        int size = this.mItemDecorations.size();
        for (int i = 0; i < size; i++) {
            this.mTempRect.set(0, 0, 0, 0);
            this.mItemDecorations.get(i).getItemOffsets(this.mTempRect, view, this, this.mState);
            rect.left += this.mTempRect.left;
            rect.top += this.mTempRect.top;
            rect.right += this.mTempRect.right;
            rect.bottom += this.mTempRect.bottom;
        }
        layoutParams.mInsetsDirty = false;
        return rect;
    }

    void dispatchOnScrolled(int i, int i2) {
        int scrollX = getScrollX();
        int scrollY = getScrollY();
        onScrollChanged(scrollX, scrollY, scrollX, scrollY);
        onScrolled(i, i2);
        if (this.mScrollListener != null) {
            this.mScrollListener.onScrolled(this, i, i2);
        }
        if (this.mScrollListeners != null) {
            for (int size = this.mScrollListeners.size() - 1; size >= 0; size--) {
                this.mScrollListeners.get(size).onScrolled(this, i, i2);
            }
        }
    }

    void dispatchOnScrollStateChanged(int i) {
        if (this.mLayout != null) {
            this.mLayout.onScrollStateChanged(i);
        }
        onScrollStateChanged(i);
        if (this.mScrollListener != null) {
            this.mScrollListener.onScrollStateChanged(this, i);
        }
        if (this.mScrollListeners != null) {
            for (int size = this.mScrollListeners.size() - 1; size >= 0; size--) {
                this.mScrollListeners.get(size).onScrollStateChanged(this, i);
            }
        }
    }

    public boolean hasPendingAdapterUpdates() {
        return !this.mFirstLayoutComplete || this.mDataSetHasChangedAfterLayout || this.mAdapterHelper.hasPendingUpdates();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ViewFlinger implements Runnable {
        private int mLastFlingX;
        private int mLastFlingY;
        private ScrollerCompat mScroller;
        private Interpolator mInterpolator = RecyclerView.sQuinticInterpolator;
        private boolean mEatRunOnAnimationRequest = false;
        private boolean mReSchedulePostAnimationCallback = false;

        public ViewFlinger() {
            this.mScroller = ScrollerCompat.create(RecyclerView.this.getContext(), RecyclerView.sQuinticInterpolator);
        }

        /* JADX WARN: Code restructure failed: missing block: B:79:0x00fe, code lost:
        
            if (r12 > 0) goto L48;
         */
        /* JADX WARN: Removed duplicated region for block: B:65:0x00fa  */
        /* JADX WARN: Removed duplicated region for block: B:69:0x010a  */
        /* JADX WARN: Removed duplicated region for block: B:71:0x0111 A[ADDED_TO_REGION] */
        @Override // java.lang.Runnable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public void run() {
            /*
                Method dump skipped, instructions count: 408
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.RecyclerView.ViewFlinger.run():void");
        }

        private void disableRunOnAnimationRequests() {
            this.mReSchedulePostAnimationCallback = false;
            this.mEatRunOnAnimationRequest = true;
        }

        private void enableRunOnAnimationRequests() {
            this.mEatRunOnAnimationRequest = false;
            if (this.mReSchedulePostAnimationCallback) {
                postOnAnimation();
            }
        }

        void postOnAnimation() {
            if (this.mEatRunOnAnimationRequest) {
                this.mReSchedulePostAnimationCallback = true;
            } else {
                RecyclerView.this.removeCallbacks(this);
                ViewCompat.postOnAnimation(RecyclerView.this, this);
            }
        }

        public void fling(int i, int i2) {
            RecyclerView.this.setScrollState(2);
            this.mLastFlingY = 0;
            this.mLastFlingX = 0;
            this.mScroller.fling(0, 0, i, i2, Integer.MIN_VALUE, ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED, Integer.MIN_VALUE, ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
            postOnAnimation();
        }

        public void smoothScrollBy(int i, int i2) {
            smoothScrollBy(i, i2, 0, 0);
        }

        public void smoothScrollBy(int i, int i2, int i3, int i4) {
            smoothScrollBy(i, i2, computeScrollDuration(i, i2, i3, i4));
        }

        private float distanceInfluenceForSnapDuration(float f) {
            Double.isNaN(f - 0.5f);
            return (float) Math.sin((float) (r0 * 0.4712389167638204d));
        }

        private int computeScrollDuration(int i, int i2, int i3, int i4) {
            int i5;
            int abs = Math.abs(i);
            int abs2 = Math.abs(i2);
            boolean z = abs > abs2;
            int sqrt = (int) Math.sqrt((i3 * i3) + (i4 * i4));
            int sqrt2 = (int) Math.sqrt((i * i) + (i2 * i2));
            int width = z ? RecyclerView.this.getWidth() : RecyclerView.this.getHeight();
            int i6 = width / 2;
            float f = width;
            float f2 = i6;
            float distanceInfluenceForSnapDuration = f2 + (distanceInfluenceForSnapDuration(Math.min(1.0f, (sqrt2 * 1.0f) / f)) * f2);
            if (sqrt > 0) {
                i5 = Math.round(Math.abs(distanceInfluenceForSnapDuration / sqrt) * 1000.0f) * 4;
            } else {
                if (!z) {
                    abs = abs2;
                }
                i5 = (int) (((abs / f) + 1.0f) * 300.0f);
            }
            return Math.min(i5, RecyclerView.MAX_SCROLL_DURATION);
        }

        public void smoothScrollBy(int i, int i2, int i3) {
            smoothScrollBy(i, i2, i3, RecyclerView.sQuinticInterpolator);
        }

        public void smoothScrollBy(int i, int i2, int i3, Interpolator interpolator) {
            if (this.mInterpolator != interpolator) {
                this.mInterpolator = interpolator;
                this.mScroller = ScrollerCompat.create(RecyclerView.this.getContext(), interpolator);
            }
            RecyclerView.this.setScrollState(2);
            this.mLastFlingY = 0;
            this.mLastFlingX = 0;
            this.mScroller.startScroll(0, 0, i, i2, i3);
            postOnAnimation();
        }

        public void stop() {
            RecyclerView.this.removeCallbacks(this);
            this.mScroller.abortAnimation();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void repositionShadowingViews() {
        int childCount = this.mChildHelper.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.mChildHelper.getChildAt(i);
            ViewHolder childViewHolder = getChildViewHolder(childAt);
            if (childViewHolder != null && childViewHolder.mShadowingHolder != null) {
                View view = childViewHolder.mShadowingHolder.itemView;
                int left = childAt.getLeft();
                int top = childAt.getTop();
                if (left != view.getLeft() || top != view.getTop()) {
                    view.layout(left, top, view.getWidth() + left, view.getHeight() + top);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class RecyclerViewDataObserver extends AdapterDataObserver {
        private RecyclerViewDataObserver() {
        }

        @Override // android.support.v7.widget.RecyclerView.AdapterDataObserver
        public void onChanged() {
            RecyclerView.this.assertNotInLayoutOrScroll(null);
            if (RecyclerView.this.mAdapter.hasStableIds()) {
                RecyclerView.this.mState.mStructureChanged = true;
                RecyclerView.this.setDataSetChangedAfterLayout();
            } else {
                RecyclerView.this.mState.mStructureChanged = true;
                RecyclerView.this.setDataSetChangedAfterLayout();
            }
            if (RecyclerView.this.mAdapterHelper.hasPendingUpdates()) {
                return;
            }
            RecyclerView.this.requestLayout();
        }

        @Override // android.support.v7.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeChanged(int i, int i2, Object obj) {
            RecyclerView.this.assertNotInLayoutOrScroll(null);
            if (RecyclerView.this.mAdapterHelper.onItemRangeChanged(i, i2, obj)) {
                triggerUpdateProcessor();
            }
        }

        @Override // android.support.v7.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeInserted(int i, int i2) {
            RecyclerView.this.assertNotInLayoutOrScroll(null);
            if (RecyclerView.this.mAdapterHelper.onItemRangeInserted(i, i2)) {
                triggerUpdateProcessor();
            }
        }

        @Override // android.support.v7.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeRemoved(int i, int i2) {
            RecyclerView.this.assertNotInLayoutOrScroll(null);
            if (RecyclerView.this.mAdapterHelper.onItemRangeRemoved(i, i2)) {
                triggerUpdateProcessor();
            }
        }

        @Override // android.support.v7.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeMoved(int i, int i2, int i3) {
            RecyclerView.this.assertNotInLayoutOrScroll(null);
            if (RecyclerView.this.mAdapterHelper.onItemRangeMoved(i, i2, i3)) {
                triggerUpdateProcessor();
            }
        }

        void triggerUpdateProcessor() {
            if (!RecyclerView.this.mPostUpdatesOnAnimation || !RecyclerView.this.mHasFixedSize || !RecyclerView.this.mIsAttached) {
                RecyclerView.this.mAdapterUpdateDuringMeasure = true;
                RecyclerView.this.requestLayout();
            } else {
                ViewCompat.postOnAnimation(RecyclerView.this, RecyclerView.this.mUpdateChildViewsRunnable);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class RecycledViewPool {
        private static final int DEFAULT_MAX_SCRAP = 5;
        private SparseArray<ArrayList<ViewHolder>> mScrap = new SparseArray<>();
        private SparseIntArray mMaxScrap = new SparseIntArray();
        private int mAttachCount = 0;

        public void clear() {
            this.mScrap.clear();
        }

        public void setMaxRecycledViews(int i, int i2) {
            this.mMaxScrap.put(i, i2);
            ArrayList<ViewHolder> arrayList = this.mScrap.get(i);
            if (arrayList != null) {
                while (arrayList.size() > i2) {
                    arrayList.remove(arrayList.size() - 1);
                }
            }
        }

        public ViewHolder getRecycledView(int i) {
            ArrayList<ViewHolder> arrayList = this.mScrap.get(i);
            if (arrayList == null || arrayList.isEmpty()) {
                return null;
            }
            int size = arrayList.size() - 1;
            ViewHolder viewHolder = arrayList.get(size);
            arrayList.remove(size);
            return viewHolder;
        }

        int size() {
            int i = 0;
            for (int i2 = 0; i2 < this.mScrap.size(); i2++) {
                ArrayList<ViewHolder> valueAt = this.mScrap.valueAt(i2);
                if (valueAt != null) {
                    i += valueAt.size();
                }
            }
            return i;
        }

        public void putRecycledView(ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            ArrayList<ViewHolder> scrapHeapForType = getScrapHeapForType(itemViewType);
            if (this.mMaxScrap.get(itemViewType) <= scrapHeapForType.size()) {
                return;
            }
            viewHolder.resetInternal();
            scrapHeapForType.add(viewHolder);
        }

        void attach(Adapter adapter) {
            this.mAttachCount++;
        }

        void detach() {
            this.mAttachCount--;
        }

        void onAdapterChanged(Adapter adapter, Adapter adapter2, boolean z) {
            if (adapter != null) {
                detach();
            }
            if (!z && this.mAttachCount == 0) {
                clear();
            }
            if (adapter2 != null) {
                attach(adapter2);
            }
        }

        private ArrayList<ViewHolder> getScrapHeapForType(int i) {
            ArrayList<ViewHolder> arrayList = this.mScrap.get(i);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                this.mScrap.put(i, arrayList);
                if (this.mMaxScrap.indexOfKey(i) < 0) {
                    this.mMaxScrap.put(i, 5);
                }
            }
            return arrayList;
        }
    }

    /* loaded from: classes.dex */
    public final class Recycler {
        private static final int DEFAULT_CACHE_SIZE = 2;
        private RecycledViewPool mRecyclerPool;
        private ViewCacheExtension mViewCacheExtension;
        final ArrayList<ViewHolder> mAttachedScrap = new ArrayList<>();
        private ArrayList<ViewHolder> mChangedScrap = null;
        final ArrayList<ViewHolder> mCachedViews = new ArrayList<>();
        private final List<ViewHolder> mUnmodifiableAttachedScrap = Collections.unmodifiableList(this.mAttachedScrap);
        private int mViewCacheMax = 2;

        public Recycler() {
        }

        public void clear() {
            this.mAttachedScrap.clear();
            recycleAndClearCachedViews();
        }

        public void setViewCacheSize(int i) {
            this.mViewCacheMax = i;
            for (int size = this.mCachedViews.size() - 1; size >= 0 && this.mCachedViews.size() > i; size--) {
                recycleCachedViewAt(size);
            }
        }

        public List<ViewHolder> getScrapList() {
            return this.mUnmodifiableAttachedScrap;
        }

        boolean validateViewHolderForOffsetPosition(ViewHolder viewHolder) {
            if (viewHolder.isRemoved()) {
                return RecyclerView.this.mState.isPreLayout();
            }
            if (viewHolder.mPosition < 0 || viewHolder.mPosition >= RecyclerView.this.mAdapter.getItemCount()) {
                throw new IndexOutOfBoundsException("Inconsistency detected. Invalid view holder adapter position" + viewHolder);
            }
            if (RecyclerView.this.mState.isPreLayout() || RecyclerView.this.mAdapter.getItemViewType(viewHolder.mPosition) == viewHolder.getItemViewType()) {
                return !RecyclerView.this.mAdapter.hasStableIds() || viewHolder.getItemId() == RecyclerView.this.mAdapter.getItemId(viewHolder.mPosition);
            }
            return false;
        }

        public void bindViewToPosition(View view, int i) {
            LayoutParams layoutParams;
            ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            if (childViewHolderInt == null) {
                throw new IllegalArgumentException("The view does not have a ViewHolder. You cannot pass arbitrary views to this method, they should be created by the Adapter");
            }
            int findPositionOffset = RecyclerView.this.mAdapterHelper.findPositionOffset(i);
            if (findPositionOffset < 0 || findPositionOffset >= RecyclerView.this.mAdapter.getItemCount()) {
                throw new IndexOutOfBoundsException("Inconsistency detected. Invalid item position " + i + "(offset:" + findPositionOffset + ").state:" + RecyclerView.this.mState.getItemCount());
            }
            childViewHolderInt.mOwnerRecyclerView = RecyclerView.this;
            RecyclerView.this.mAdapter.bindViewHolder(childViewHolderInt, findPositionOffset);
            attachAccessibilityDelegate(view);
            if (RecyclerView.this.mState.isPreLayout()) {
                childViewHolderInt.mPreLayoutPosition = i;
            }
            ViewGroup.LayoutParams layoutParams2 = childViewHolderInt.itemView.getLayoutParams();
            if (layoutParams2 == null) {
                layoutParams = (LayoutParams) RecyclerView.this.generateDefaultLayoutParams();
                childViewHolderInt.itemView.setLayoutParams(layoutParams);
            } else if (!RecyclerView.this.checkLayoutParams(layoutParams2)) {
                layoutParams = (LayoutParams) RecyclerView.this.generateLayoutParams(layoutParams2);
                childViewHolderInt.itemView.setLayoutParams(layoutParams);
            } else {
                layoutParams = (LayoutParams) layoutParams2;
            }
            layoutParams.mInsetsDirty = true;
            layoutParams.mViewHolder = childViewHolderInt;
            layoutParams.mPendingInvalidate = childViewHolderInt.itemView.getParent() == null;
        }

        public int convertPreLayoutPositionToPostLayout(int i) {
            if (i >= 0 && i < RecyclerView.this.mState.getItemCount()) {
                return !RecyclerView.this.mState.isPreLayout() ? i : RecyclerView.this.mAdapterHelper.findPositionOffset(i);
            }
            throw new IndexOutOfBoundsException("invalid position " + i + ". State item count is " + RecyclerView.this.mState.getItemCount());
        }

        public View getViewForPosition(int i) {
            return getViewForPosition(i, false);
        }

        /* JADX WARN: Removed duplicated region for block: B:14:0x0032  */
        /* JADX WARN: Removed duplicated region for block: B:23:0x0057  */
        /* JADX WARN: Removed duplicated region for block: B:25:0x005a  */
        /* JADX WARN: Removed duplicated region for block: B:66:0x0143  */
        /* JADX WARN: Removed duplicated region for block: B:75:0x01b5  */
        /* JADX WARN: Removed duplicated region for block: B:78:0x01df A[ADDED_TO_REGION] */
        /* JADX WARN: Removed duplicated region for block: B:82:0x01c3  */
        /* JADX WARN: Removed duplicated region for block: B:94:0x01aa  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        android.view.View getViewForPosition(int r9, boolean r10) {
            /*
                Method dump skipped, instructions count: 535
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.RecyclerView.Recycler.getViewForPosition(int, boolean):android.view.View");
        }

        private void attachAccessibilityDelegate(View view) {
            if (RecyclerView.this.isAccessibilityEnabled()) {
                if (ViewCompat.getImportantForAccessibility(view) == 0) {
                    ViewCompat.setImportantForAccessibility(view, 1);
                }
                if (ViewCompat.hasAccessibilityDelegate(view)) {
                    return;
                }
                ViewCompat.setAccessibilityDelegate(view, RecyclerView.this.mAccessibilityDelegate.getItemDelegate());
            }
        }

        private void invalidateDisplayListInt(ViewHolder viewHolder) {
            if (viewHolder.itemView instanceof ViewGroup) {
                invalidateDisplayListInt((ViewGroup) viewHolder.itemView, false);
            }
        }

        private void invalidateDisplayListInt(ViewGroup viewGroup, boolean z) {
            for (int childCount = viewGroup.getChildCount() - 1; childCount >= 0; childCount--) {
                View childAt = viewGroup.getChildAt(childCount);
                if (childAt instanceof ViewGroup) {
                    invalidateDisplayListInt((ViewGroup) childAt, true);
                }
            }
            if (z) {
                if (viewGroup.getVisibility() == 4) {
                    viewGroup.setVisibility(0);
                    viewGroup.setVisibility(4);
                } else {
                    int visibility = viewGroup.getVisibility();
                    viewGroup.setVisibility(4);
                    viewGroup.setVisibility(visibility);
                }
            }
        }

        public void recycleView(View view) {
            ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            if (childViewHolderInt.isTmpDetached()) {
                RecyclerView.this.removeDetachedView(view, false);
            }
            if (childViewHolderInt.isScrap()) {
                childViewHolderInt.unScrap();
            } else if (childViewHolderInt.wasReturnedFromScrap()) {
                childViewHolderInt.clearReturnedFromScrapFlag();
            }
            recycleViewHolderInternal(childViewHolderInt);
        }

        void recycleViewInternal(View view) {
            recycleViewHolderInternal(RecyclerView.getChildViewHolderInt(view));
        }

        void recycleAndClearCachedViews() {
            for (int size = this.mCachedViews.size() - 1; size >= 0; size--) {
                recycleCachedViewAt(size);
            }
            this.mCachedViews.clear();
        }

        void recycleCachedViewAt(int i) {
            addViewHolderToRecycledViewPool(this.mCachedViews.get(i));
            this.mCachedViews.remove(i);
        }

        /* JADX WARN: Removed duplicated region for block: B:39:0x006b  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        void recycleViewHolderInternal(android.support.v7.widget.RecyclerView.ViewHolder r6) {
            /*
                r5 = this;
                boolean r0 = r6.isScrap()
                r1 = 1
                r2 = 0
                if (r0 != 0) goto L9f
                android.view.View r0 = r6.itemView
                android.view.ViewParent r0 = r0.getParent()
                if (r0 == 0) goto L12
                goto L9f
            L12:
                boolean r0 = r6.isTmpDetached()
                if (r0 != 0) goto L88
                boolean r0 = r6.shouldIgnore()
                if (r0 != 0) goto L80
                boolean r0 = android.support.v7.widget.RecyclerView.ViewHolder.access$4700(r6)
                android.support.v7.widget.RecyclerView r3 = android.support.v7.widget.RecyclerView.this
                android.support.v7.widget.RecyclerView$Adapter r3 = android.support.v7.widget.RecyclerView.access$2900(r3)
                if (r3 == 0) goto L3a
                if (r0 == 0) goto L3a
                android.support.v7.widget.RecyclerView r3 = android.support.v7.widget.RecyclerView.this
                android.support.v7.widget.RecyclerView$Adapter r3 = android.support.v7.widget.RecyclerView.access$2900(r3)
                boolean r3 = r3.onFailedToRecycleView(r6)
                if (r3 == 0) goto L3a
                r3 = 1
                goto L3b
            L3a:
                r3 = 0
            L3b:
                if (r3 != 0) goto L46
                boolean r3 = r6.isRecyclable()
                if (r3 == 0) goto L44
                goto L46
            L44:
                r3 = 0
                goto L6f
            L46:
                r3 = 14
                boolean r3 = r6.hasAnyOfTheFlags(r3)
                if (r3 != 0) goto L68
                java.util.ArrayList<android.support.v7.widget.RecyclerView$ViewHolder> r3 = r5.mCachedViews
                int r3 = r3.size()
                int r4 = r5.mViewCacheMax
                if (r3 != r4) goto L5d
                if (r3 <= 0) goto L5d
                r5.recycleCachedViewAt(r2)
            L5d:
                int r4 = r5.mViewCacheMax
                if (r3 >= r4) goto L68
                java.util.ArrayList<android.support.v7.widget.RecyclerView$ViewHolder> r3 = r5.mCachedViews
                r3.add(r6)
                r3 = 1
                goto L69
            L68:
                r3 = 0
            L69:
                if (r3 != 0) goto L6f
                r5.addViewHolderToRecycledViewPool(r6)
                r2 = 1
            L6f:
                android.support.v7.widget.RecyclerView r1 = android.support.v7.widget.RecyclerView.this
                android.support.v7.widget.ViewInfoStore r1 = r1.mViewInfoStore
                r1.removeViewHolder(r6)
                if (r3 != 0) goto L7f
                if (r2 != 0) goto L7f
                if (r0 == 0) goto L7f
                r0 = 0
                r6.mOwnerRecyclerView = r0
            L7f:
                return
            L80:
                java.lang.IllegalArgumentException r6 = new java.lang.IllegalArgumentException
                java.lang.String r0 = "Trying to recycle an ignored view holder. You should first call stopIgnoringView(view) before calling recycle."
                r6.<init>(r0)
                throw r6
            L88:
                java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = "Tmp detached view should be removed from RecyclerView before it can be recycled: "
                r1.append(r2)
                r1.append(r6)
                java.lang.String r6 = r1.toString()
                r0.<init>(r6)
                throw r0
            L9f:
                java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r4 = "Scrapped or attached views may not be recycled. isScrap:"
                r3.append(r4)
                boolean r4 = r6.isScrap()
                r3.append(r4)
                java.lang.String r4 = " isAttached:"
                r3.append(r4)
                android.view.View r6 = r6.itemView
                android.view.ViewParent r6 = r6.getParent()
                if (r6 == 0) goto Lc0
                goto Lc1
            Lc0:
                r1 = 0
            Lc1:
                r3.append(r1)
                java.lang.String r6 = r3.toString()
                r0.<init>(r6)
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.RecyclerView.Recycler.recycleViewHolderInternal(android.support.v7.widget.RecyclerView$ViewHolder):void");
        }

        void addViewHolderToRecycledViewPool(ViewHolder viewHolder) {
            ViewCompat.setAccessibilityDelegate(viewHolder.itemView, null);
            dispatchViewRecycled(viewHolder);
            viewHolder.mOwnerRecyclerView = null;
            getRecycledViewPool().putRecycledView(viewHolder);
        }

        void quickRecycleScrapView(View view) {
            ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            childViewHolderInt.mScrapContainer = null;
            childViewHolderInt.mInChangeScrap = false;
            childViewHolderInt.clearReturnedFromScrapFlag();
            recycleViewHolderInternal(childViewHolderInt);
        }

        void scrapView(View view) {
            ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            if (childViewHolderInt.hasAnyOfTheFlags(12) || !childViewHolderInt.isUpdated() || RecyclerView.this.canReuseUpdatedViewHolder(childViewHolderInt)) {
                if (childViewHolderInt.isInvalid() && !childViewHolderInt.isRemoved() && !RecyclerView.this.mAdapter.hasStableIds()) {
                    throw new IllegalArgumentException("Called scrap view with an invalid view. Invalid views cannot be reused from scrap, they should rebound from recycler pool.");
                }
                childViewHolderInt.setScrapContainer(this, false);
                this.mAttachedScrap.add(childViewHolderInt);
                return;
            }
            if (this.mChangedScrap == null) {
                this.mChangedScrap = new ArrayList<>();
            }
            childViewHolderInt.setScrapContainer(this, true);
            this.mChangedScrap.add(childViewHolderInt);
        }

        void unscrapView(ViewHolder viewHolder) {
            if (viewHolder.mInChangeScrap) {
                this.mChangedScrap.remove(viewHolder);
            } else {
                this.mAttachedScrap.remove(viewHolder);
            }
            viewHolder.mScrapContainer = null;
            viewHolder.mInChangeScrap = false;
            viewHolder.clearReturnedFromScrapFlag();
        }

        int getScrapCount() {
            return this.mAttachedScrap.size();
        }

        View getScrapViewAt(int i) {
            return this.mAttachedScrap.get(i).itemView;
        }

        void clearScrap() {
            this.mAttachedScrap.clear();
            if (this.mChangedScrap != null) {
                this.mChangedScrap.clear();
            }
        }

        ViewHolder getChangedScrapViewForPosition(int i) {
            int size;
            int findPositionOffset;
            if (this.mChangedScrap == null || (size = this.mChangedScrap.size()) == 0) {
                return null;
            }
            for (int i2 = 0; i2 < size; i2++) {
                ViewHolder viewHolder = this.mChangedScrap.get(i2);
                if (!viewHolder.wasReturnedFromScrap() && viewHolder.getLayoutPosition() == i) {
                    viewHolder.addFlags(32);
                    return viewHolder;
                }
            }
            if (RecyclerView.this.mAdapter.hasStableIds() && (findPositionOffset = RecyclerView.this.mAdapterHelper.findPositionOffset(i)) > 0 && findPositionOffset < RecyclerView.this.mAdapter.getItemCount()) {
                long itemId = RecyclerView.this.mAdapter.getItemId(findPositionOffset);
                for (int i3 = 0; i3 < size; i3++) {
                    ViewHolder viewHolder2 = this.mChangedScrap.get(i3);
                    if (!viewHolder2.wasReturnedFromScrap() && viewHolder2.getItemId() == itemId) {
                        viewHolder2.addFlags(32);
                        return viewHolder2;
                    }
                }
            }
            return null;
        }

        /* JADX WARN: Removed duplicated region for block: B:31:0x00cb  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        android.support.v7.widget.RecyclerView.ViewHolder getScrapViewForPosition(int r7, int r8, boolean r9) {
            /*
                r6 = this;
                java.util.ArrayList<android.support.v7.widget.RecyclerView$ViewHolder> r0 = r6.mAttachedScrap
                int r0 = r0.size()
                r1 = 0
                r2 = 0
            L8:
                r3 = -1
                if (r2 >= r0) goto L7b
                java.util.ArrayList<android.support.v7.widget.RecyclerView$ViewHolder> r4 = r6.mAttachedScrap
                java.lang.Object r4 = r4.get(r2)
                android.support.v7.widget.RecyclerView$ViewHolder r4 = (android.support.v7.widget.RecyclerView.ViewHolder) r4
                boolean r5 = r4.wasReturnedFromScrap()
                if (r5 != 0) goto L78
                int r5 = r4.getLayoutPosition()
                if (r5 != r7) goto L78
                boolean r5 = r4.isInvalid()
                if (r5 != 0) goto L78
                android.support.v7.widget.RecyclerView r5 = android.support.v7.widget.RecyclerView.this
                android.support.v7.widget.RecyclerView$State r5 = r5.mState
                boolean r5 = android.support.v7.widget.RecyclerView.State.access$2200(r5)
                if (r5 != 0) goto L35
                boolean r5 = r4.isRemoved()
                if (r5 != 0) goto L78
            L35:
                if (r8 == r3) goto L72
                int r0 = r4.getItemViewType()
                if (r0 == r8) goto L72
                java.lang.String r0 = "RecyclerView"
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                java.lang.String r5 = "Scrap view for position "
                r2.append(r5)
                r2.append(r7)
                java.lang.String r5 = " isn't dirty but has"
                r2.append(r5)
                java.lang.String r5 = " wrong view type! (found "
                r2.append(r5)
                int r4 = r4.getItemViewType()
                r2.append(r4)
                java.lang.String r4 = " but expected "
                r2.append(r4)
                r2.append(r8)
                java.lang.String r4 = ")"
                r2.append(r4)
                java.lang.String r2 = r2.toString()
                android.util.Log.e(r0, r2)
                goto L7b
            L72:
                r7 = 32
                r4.addFlags(r7)
                return r4
            L78:
                int r2 = r2 + 1
                goto L8
            L7b:
                if (r9 != 0) goto Lc3
                android.support.v7.widget.RecyclerView r0 = android.support.v7.widget.RecyclerView.this
                android.support.v7.widget.ChildHelper r0 = r0.mChildHelper
                android.view.View r8 = r0.findHiddenNonRemovedView(r7, r8)
                if (r8 == 0) goto Lc3
                android.support.v7.widget.RecyclerView$ViewHolder r7 = android.support.v7.widget.RecyclerView.getChildViewHolderInt(r8)
                android.support.v7.widget.RecyclerView r9 = android.support.v7.widget.RecyclerView.this
                android.support.v7.widget.ChildHelper r9 = r9.mChildHelper
                r9.unhide(r8)
                android.support.v7.widget.RecyclerView r9 = android.support.v7.widget.RecyclerView.this
                android.support.v7.widget.ChildHelper r9 = r9.mChildHelper
                int r9 = r9.indexOfChild(r8)
                if (r9 == r3) goto Lac
                android.support.v7.widget.RecyclerView r0 = android.support.v7.widget.RecyclerView.this
                android.support.v7.widget.ChildHelper r0 = r0.mChildHelper
                r0.detachViewFromParent(r9)
                r6.scrapView(r8)
                r8 = 8224(0x2020, float:1.1524E-41)
                r7.addFlags(r8)
                return r7
            Lac:
                java.lang.IllegalStateException r8 = new java.lang.IllegalStateException
                java.lang.StringBuilder r9 = new java.lang.StringBuilder
                r9.<init>()
                java.lang.String r0 = "layout index should not be -1 after unhiding a view:"
                r9.append(r0)
                r9.append(r7)
                java.lang.String r7 = r9.toString()
                r8.<init>(r7)
                throw r8
            Lc3:
                java.util.ArrayList<android.support.v7.widget.RecyclerView$ViewHolder> r8 = r6.mCachedViews
                int r8 = r8.size()
            Lc9:
                if (r1 >= r8) goto Lea
                java.util.ArrayList<android.support.v7.widget.RecyclerView$ViewHolder> r0 = r6.mCachedViews
                java.lang.Object r0 = r0.get(r1)
                android.support.v7.widget.RecyclerView$ViewHolder r0 = (android.support.v7.widget.RecyclerView.ViewHolder) r0
                boolean r2 = r0.isInvalid()
                if (r2 != 0) goto Le7
                int r2 = r0.getLayoutPosition()
                if (r2 != r7) goto Le7
                if (r9 != 0) goto Le6
                java.util.ArrayList<android.support.v7.widget.RecyclerView$ViewHolder> r7 = r6.mCachedViews
                r7.remove(r1)
            Le6:
                return r0
            Le7:
                int r1 = r1 + 1
                goto Lc9
            Lea:
                r7 = 0
                return r7
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.RecyclerView.Recycler.getScrapViewForPosition(int, int, boolean):android.support.v7.widget.RecyclerView$ViewHolder");
        }

        ViewHolder getScrapViewForId(long j, int i, boolean z) {
            for (int size = this.mAttachedScrap.size() - 1; size >= 0; size--) {
                ViewHolder viewHolder = this.mAttachedScrap.get(size);
                if (viewHolder.getItemId() == j && !viewHolder.wasReturnedFromScrap()) {
                    if (i == viewHolder.getItemViewType()) {
                        viewHolder.addFlags(32);
                        if (viewHolder.isRemoved() && !RecyclerView.this.mState.isPreLayout()) {
                            viewHolder.setFlags(2, 14);
                        }
                        return viewHolder;
                    }
                    if (!z) {
                        this.mAttachedScrap.remove(size);
                        RecyclerView.this.removeDetachedView(viewHolder.itemView, false);
                        quickRecycleScrapView(viewHolder.itemView);
                    }
                }
            }
            for (int size2 = this.mCachedViews.size() - 1; size2 >= 0; size2--) {
                ViewHolder viewHolder2 = this.mCachedViews.get(size2);
                if (viewHolder2.getItemId() == j) {
                    if (i == viewHolder2.getItemViewType()) {
                        if (!z) {
                            this.mCachedViews.remove(size2);
                        }
                        return viewHolder2;
                    }
                    if (!z) {
                        recycleCachedViewAt(size2);
                    }
                }
            }
            return null;
        }

        void dispatchViewRecycled(ViewHolder viewHolder) {
            if (RecyclerView.this.mRecyclerListener != null) {
                RecyclerView.this.mRecyclerListener.onViewRecycled(viewHolder);
            }
            if (RecyclerView.this.mAdapter != null) {
                RecyclerView.this.mAdapter.onViewRecycled(viewHolder);
            }
            if (RecyclerView.this.mState != null) {
                RecyclerView.this.mViewInfoStore.removeViewHolder(viewHolder);
            }
        }

        void onAdapterChanged(Adapter adapter, Adapter adapter2, boolean z) {
            clear();
            getRecycledViewPool().onAdapterChanged(adapter, adapter2, z);
        }

        void offsetPositionRecordsForMove(int i, int i2) {
            int i3;
            int i4;
            int i5;
            if (i < i2) {
                i4 = i;
                i3 = i2;
                i5 = -1;
            } else {
                i3 = i;
                i4 = i2;
                i5 = 1;
            }
            int size = this.mCachedViews.size();
            for (int i6 = 0; i6 < size; i6++) {
                ViewHolder viewHolder = this.mCachedViews.get(i6);
                if (viewHolder != null && viewHolder.mPosition >= i4 && viewHolder.mPosition <= i3) {
                    if (viewHolder.mPosition == i) {
                        viewHolder.offsetPosition(i2 - i, false);
                    } else {
                        viewHolder.offsetPosition(i5, false);
                    }
                }
            }
        }

        void offsetPositionRecordsForInsert(int i, int i2) {
            int size = this.mCachedViews.size();
            for (int i3 = 0; i3 < size; i3++) {
                ViewHolder viewHolder = this.mCachedViews.get(i3);
                if (viewHolder != null && viewHolder.getLayoutPosition() >= i) {
                    viewHolder.offsetPosition(i2, true);
                }
            }
        }

        void offsetPositionRecordsForRemove(int i, int i2, boolean z) {
            int i3 = i + i2;
            for (int size = this.mCachedViews.size() - 1; size >= 0; size--) {
                ViewHolder viewHolder = this.mCachedViews.get(size);
                if (viewHolder != null) {
                    if (viewHolder.getLayoutPosition() >= i3) {
                        viewHolder.offsetPosition(-i2, z);
                    } else if (viewHolder.getLayoutPosition() >= i) {
                        viewHolder.addFlags(8);
                        recycleCachedViewAt(size);
                    }
                }
            }
        }

        void setViewCacheExtension(ViewCacheExtension viewCacheExtension) {
            this.mViewCacheExtension = viewCacheExtension;
        }

        void setRecycledViewPool(RecycledViewPool recycledViewPool) {
            if (this.mRecyclerPool != null) {
                this.mRecyclerPool.detach();
            }
            this.mRecyclerPool = recycledViewPool;
            if (recycledViewPool != null) {
                this.mRecyclerPool.attach(RecyclerView.this.getAdapter());
            }
        }

        RecycledViewPool getRecycledViewPool() {
            if (this.mRecyclerPool == null) {
                this.mRecyclerPool = new RecycledViewPool();
            }
            return this.mRecyclerPool;
        }

        void viewRangeUpdate(int i, int i2) {
            int layoutPosition;
            int i3 = i2 + i;
            for (int size = this.mCachedViews.size() - 1; size >= 0; size--) {
                ViewHolder viewHolder = this.mCachedViews.get(size);
                if (viewHolder != null && (layoutPosition = viewHolder.getLayoutPosition()) >= i && layoutPosition < i3) {
                    viewHolder.addFlags(2);
                    recycleCachedViewAt(size);
                }
            }
        }

        void setAdapterPositionsAsUnknown() {
            int size = this.mCachedViews.size();
            for (int i = 0; i < size; i++) {
                ViewHolder viewHolder = this.mCachedViews.get(i);
                if (viewHolder != null) {
                    viewHolder.addFlags(512);
                }
            }
        }

        void markKnownViewsInvalid() {
            if (RecyclerView.this.mAdapter != null && RecyclerView.this.mAdapter.hasStableIds()) {
                int size = this.mCachedViews.size();
                for (int i = 0; i < size; i++) {
                    ViewHolder viewHolder = this.mCachedViews.get(i);
                    if (viewHolder != null) {
                        viewHolder.addFlags(6);
                        viewHolder.addChangePayload(null);
                    }
                }
                return;
            }
            recycleAndClearCachedViews();
        }

        void clearOldPositions() {
            int size = this.mCachedViews.size();
            for (int i = 0; i < size; i++) {
                this.mCachedViews.get(i).clearOldPosition();
            }
            int size2 = this.mAttachedScrap.size();
            for (int i2 = 0; i2 < size2; i2++) {
                this.mAttachedScrap.get(i2).clearOldPosition();
            }
            if (this.mChangedScrap != null) {
                int size3 = this.mChangedScrap.size();
                for (int i3 = 0; i3 < size3; i3++) {
                    this.mChangedScrap.get(i3).clearOldPosition();
                }
            }
        }

        void markItemDecorInsetsDirty() {
            int size = this.mCachedViews.size();
            for (int i = 0; i < size; i++) {
                LayoutParams layoutParams = (LayoutParams) this.mCachedViews.get(i).itemView.getLayoutParams();
                if (layoutParams != null) {
                    layoutParams.mInsetsDirty = true;
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Adapter<VH extends ViewHolder> {
        private final AdapterDataObservable mObservable = new AdapterDataObservable();
        private boolean mHasStableIds = false;

        public abstract int getItemCount();

        public long getItemId(int i) {
            return -1L;
        }

        public int getItemViewType(int i) {
            return 0;
        }

        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        }

        public abstract void onBindViewHolder(VH vh, int i);

        public abstract VH onCreateViewHolder(ViewGroup viewGroup, int i);

        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        }

        public boolean onFailedToRecycleView(VH vh) {
            return false;
        }

        public void onViewAttachedToWindow(VH vh) {
        }

        public void onViewDetachedFromWindow(VH vh) {
        }

        public void onViewRecycled(VH vh) {
        }

        public void onBindViewHolder(VH vh, int i, List<Object> list) {
            onBindViewHolder(vh, i);
        }

        public final VH createViewHolder(ViewGroup viewGroup, int i) {
            TraceCompat.beginSection(RecyclerView.TRACE_CREATE_VIEW_TAG);
            VH onCreateViewHolder = onCreateViewHolder(viewGroup, i);
            onCreateViewHolder.mItemViewType = i;
            TraceCompat.endSection();
            return onCreateViewHolder;
        }

        public final void bindViewHolder(VH vh, int i) {
            vh.mPosition = i;
            if (hasStableIds()) {
                vh.mItemId = getItemId(i);
            }
            vh.setFlags(1, 519);
            TraceCompat.beginSection(RecyclerView.TRACE_BIND_VIEW_TAG);
            onBindViewHolder(vh, i, vh.getUnmodifiedPayloads());
            vh.clearPayload();
            TraceCompat.endSection();
        }

        public void setHasStableIds(boolean z) {
            if (hasObservers()) {
                throw new IllegalStateException("Cannot change whether this adapter has stable IDs while the adapter has registered observers.");
            }
            this.mHasStableIds = z;
        }

        public final boolean hasStableIds() {
            return this.mHasStableIds;
        }

        public final boolean hasObservers() {
            return this.mObservable.hasObservers();
        }

        public void registerAdapterDataObserver(AdapterDataObserver adapterDataObserver) {
            this.mObservable.registerObserver(adapterDataObserver);
        }

        public void unregisterAdapterDataObserver(AdapterDataObserver adapterDataObserver) {
            this.mObservable.unregisterObserver(adapterDataObserver);
        }

        public final void notifyDataSetChanged() {
            this.mObservable.notifyChanged();
        }

        public final void notifyItemChanged(int i) {
            this.mObservable.notifyItemRangeChanged(i, 1);
        }

        public final void notifyItemChanged(int i, Object obj) {
            this.mObservable.notifyItemRangeChanged(i, 1, obj);
        }

        public final void notifyItemRangeChanged(int i, int i2) {
            this.mObservable.notifyItemRangeChanged(i, i2);
        }

        public final void notifyItemRangeChanged(int i, int i2, Object obj) {
            this.mObservable.notifyItemRangeChanged(i, i2, obj);
        }

        public final void notifyItemInserted(int i) {
            this.mObservable.notifyItemRangeInserted(i, 1);
        }

        public final void notifyItemMoved(int i, int i2) {
            this.mObservable.notifyItemMoved(i, i2);
        }

        public final void notifyItemRangeInserted(int i, int i2) {
            this.mObservable.notifyItemRangeInserted(i, i2);
        }

        public final void notifyItemRemoved(int i) {
            this.mObservable.notifyItemRangeRemoved(i, 1);
        }

        public final void notifyItemRangeRemoved(int i, int i2) {
            this.mObservable.notifyItemRangeRemoved(i, i2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchChildDetached(View view) {
        ViewHolder childViewHolderInt = getChildViewHolderInt(view);
        onChildDetachedFromWindow(view);
        if (this.mAdapter != null && childViewHolderInt != null) {
            this.mAdapter.onViewDetachedFromWindow(childViewHolderInt);
        }
        if (this.mOnChildAttachStateListeners != null) {
            for (int size = this.mOnChildAttachStateListeners.size() - 1; size >= 0; size--) {
                this.mOnChildAttachStateListeners.get(size).onChildViewDetachedFromWindow(view);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchChildAttached(View view) {
        ViewHolder childViewHolderInt = getChildViewHolderInt(view);
        onChildAttachedToWindow(view);
        if (this.mAdapter != null && childViewHolderInt != null) {
            this.mAdapter.onViewAttachedToWindow(childViewHolderInt);
        }
        if (this.mOnChildAttachStateListeners != null) {
            for (int size = this.mOnChildAttachStateListeners.size() - 1; size >= 0; size--) {
                this.mOnChildAttachStateListeners.get(size).onChildViewAttachedToWindow(view);
            }
        }
    }

    /* loaded from: classes.dex */
    public static abstract class LayoutManager {
        ChildHelper mChildHelper;
        RecyclerView mRecyclerView;

        @Nullable
        SmoothScroller mSmoothScroller;
        private boolean mRequestedSimpleAnimations = false;
        private boolean mIsAttachedToWindow = false;

        /* loaded from: classes.dex */
        public static class Properties {
            public int orientation;
            public boolean reverseLayout;
            public int spanCount;
            public boolean stackFromEnd;
        }

        public boolean canScrollHorizontally() {
            return false;
        }

        public boolean canScrollVertically() {
            return false;
        }

        public boolean checkLayoutParams(LayoutParams layoutParams) {
            return layoutParams != null;
        }

        public int computeHorizontalScrollExtent(State state) {
            return 0;
        }

        public int computeHorizontalScrollOffset(State state) {
            return 0;
        }

        public int computeHorizontalScrollRange(State state) {
            return 0;
        }

        public int computeVerticalScrollExtent(State state) {
            return 0;
        }

        public int computeVerticalScrollOffset(State state) {
            return 0;
        }

        public int computeVerticalScrollRange(State state) {
            return 0;
        }

        public abstract LayoutParams generateDefaultLayoutParams();

        public int getBaseline() {
            return -1;
        }

        public int getSelectionModeForAccessibility(Recycler recycler, State state) {
            return 0;
        }

        public boolean isLayoutHierarchical(Recycler recycler, State state) {
            return false;
        }

        public void onAdapterChanged(Adapter adapter, Adapter adapter2) {
        }

        public boolean onAddFocusables(RecyclerView recyclerView, ArrayList<View> arrayList, int i, int i2) {
            return false;
        }

        @CallSuper
        public void onAttachedToWindow(RecyclerView recyclerView) {
        }

        @Deprecated
        public void onDetachedFromWindow(RecyclerView recyclerView) {
        }

        @Nullable
        public View onFocusSearchFailed(View view, int i, Recycler recycler, State state) {
            return null;
        }

        public View onInterceptFocusSearch(View view, int i) {
            return null;
        }

        public void onItemsAdded(RecyclerView recyclerView, int i, int i2) {
        }

        public void onItemsChanged(RecyclerView recyclerView) {
        }

        public void onItemsMoved(RecyclerView recyclerView, int i, int i2, int i3) {
        }

        public void onItemsRemoved(RecyclerView recyclerView, int i, int i2) {
        }

        public void onItemsUpdated(RecyclerView recyclerView, int i, int i2) {
        }

        public void onRestoreInstanceState(Parcelable parcelable) {
        }

        public Parcelable onSaveInstanceState() {
            return null;
        }

        public void onScrollStateChanged(int i) {
        }

        public boolean performAccessibilityActionForItem(Recycler recycler, State state, View view, int i, Bundle bundle) {
            return false;
        }

        public int scrollHorizontallyBy(int i, Recycler recycler, State state) {
            return 0;
        }

        public void scrollToPosition(int i) {
        }

        public int scrollVerticallyBy(int i, Recycler recycler, State state) {
            return 0;
        }

        public boolean supportsPredictiveItemAnimations() {
            return false;
        }

        void setRecyclerView(RecyclerView recyclerView) {
            if (recyclerView == null) {
                this.mRecyclerView = null;
                this.mChildHelper = null;
            } else {
                this.mRecyclerView = recyclerView;
                this.mChildHelper = recyclerView.mChildHelper;
            }
        }

        public void requestLayout() {
            if (this.mRecyclerView != null) {
                this.mRecyclerView.requestLayout();
            }
        }

        public void assertInLayoutOrScroll(String str) {
            if (this.mRecyclerView != null) {
                this.mRecyclerView.assertInLayoutOrScroll(str);
            }
        }

        public void assertNotInLayoutOrScroll(String str) {
            if (this.mRecyclerView != null) {
                this.mRecyclerView.assertNotInLayoutOrScroll(str);
            }
        }

        void dispatchAttachedToWindow(RecyclerView recyclerView) {
            this.mIsAttachedToWindow = true;
            onAttachedToWindow(recyclerView);
        }

        void dispatchDetachedFromWindow(RecyclerView recyclerView, Recycler recycler) {
            this.mIsAttachedToWindow = false;
            onDetachedFromWindow(recyclerView, recycler);
        }

        public boolean isAttachedToWindow() {
            return this.mIsAttachedToWindow;
        }

        public void postOnAnimation(Runnable runnable) {
            if (this.mRecyclerView != null) {
                ViewCompat.postOnAnimation(this.mRecyclerView, runnable);
            }
        }

        public boolean removeCallbacks(Runnable runnable) {
            if (this.mRecyclerView != null) {
                return this.mRecyclerView.removeCallbacks(runnable);
            }
            return false;
        }

        @CallSuper
        public void onDetachedFromWindow(RecyclerView recyclerView, Recycler recycler) {
            onDetachedFromWindow(recyclerView);
        }

        public boolean getClipToPadding() {
            return this.mRecyclerView != null && this.mRecyclerView.mClipToPadding;
        }

        public void onLayoutChildren(Recycler recycler, State state) {
            Log.e(RecyclerView.TAG, "You must override onLayoutChildren(Recycler recycler, State state) ");
        }

        public LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
            if (layoutParams instanceof LayoutParams) {
                return new LayoutParams((LayoutParams) layoutParams);
            }
            if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                return new LayoutParams((ViewGroup.MarginLayoutParams) layoutParams);
            }
            return new LayoutParams(layoutParams);
        }

        public LayoutParams generateLayoutParams(Context context, AttributeSet attributeSet) {
            return new LayoutParams(context, attributeSet);
        }

        public void smoothScrollToPosition(RecyclerView recyclerView, State state, int i) {
            Log.e(RecyclerView.TAG, "You must override smoothScrollToPosition to support smooth scrolling");
        }

        public void startSmoothScroll(SmoothScroller smoothScroller) {
            if (this.mSmoothScroller != null && smoothScroller != this.mSmoothScroller && this.mSmoothScroller.isRunning()) {
                this.mSmoothScroller.stop();
            }
            this.mSmoothScroller = smoothScroller;
            this.mSmoothScroller.start(this.mRecyclerView, this);
        }

        public boolean isSmoothScrolling() {
            return this.mSmoothScroller != null && this.mSmoothScroller.isRunning();
        }

        public int getLayoutDirection() {
            return ViewCompat.getLayoutDirection(this.mRecyclerView);
        }

        public void endAnimation(View view) {
            if (this.mRecyclerView.mItemAnimator != null) {
                this.mRecyclerView.mItemAnimator.endAnimation(RecyclerView.getChildViewHolderInt(view));
            }
        }

        public void addDisappearingView(View view) {
            addDisappearingView(view, -1);
        }

        public void addDisappearingView(View view, int i) {
            addViewInt(view, i, true);
        }

        public void addView(View view) {
            addView(view, -1);
        }

        public void addView(View view, int i) {
            addViewInt(view, i, false);
        }

        private void addViewInt(View view, int i, boolean z) {
            ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            if (z || childViewHolderInt.isRemoved()) {
                this.mRecyclerView.mViewInfoStore.addToDisappearedInLayout(childViewHolderInt);
            } else {
                this.mRecyclerView.mViewInfoStore.removeFromDisappearedInLayout(childViewHolderInt);
            }
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            if (childViewHolderInt.wasReturnedFromScrap() || childViewHolderInt.isScrap()) {
                if (childViewHolderInt.isScrap()) {
                    childViewHolderInt.unScrap();
                } else {
                    childViewHolderInt.clearReturnedFromScrapFlag();
                }
                this.mChildHelper.attachViewToParent(view, i, view.getLayoutParams(), false);
            } else if (view.getParent() == this.mRecyclerView) {
                int indexOfChild = this.mChildHelper.indexOfChild(view);
                if (i == -1) {
                    i = this.mChildHelper.getChildCount();
                }
                if (indexOfChild == -1) {
                    throw new IllegalStateException("Added View has RecyclerView as parent but view is not a real child. Unfiltered index:" + this.mRecyclerView.indexOfChild(view));
                }
                if (indexOfChild != i) {
                    this.mRecyclerView.mLayout.moveView(indexOfChild, i);
                }
            } else {
                this.mChildHelper.addView(view, i, false);
                layoutParams.mInsetsDirty = true;
                if (this.mSmoothScroller != null && this.mSmoothScroller.isRunning()) {
                    this.mSmoothScroller.onChildAttachedToWindow(view);
                }
            }
            if (layoutParams.mPendingInvalidate) {
                childViewHolderInt.itemView.invalidate();
                layoutParams.mPendingInvalidate = false;
            }
        }

        public void removeView(View view) {
            this.mChildHelper.removeView(view);
        }

        public void removeViewAt(int i) {
            if (getChildAt(i) != null) {
                this.mChildHelper.removeViewAt(i);
            }
        }

        public void removeAllViews() {
            for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
                this.mChildHelper.removeViewAt(childCount);
            }
        }

        public int getPosition(View view) {
            return ((LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        }

        public int getItemViewType(View view) {
            return RecyclerView.getChildViewHolderInt(view).getItemViewType();
        }

        public View findViewByPosition(int i) {
            int childCount = getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = getChildAt(i2);
                ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(childAt);
                if (childViewHolderInt != null && childViewHolderInt.getLayoutPosition() == i && !childViewHolderInt.shouldIgnore() && (this.mRecyclerView.mState.isPreLayout() || !childViewHolderInt.isRemoved())) {
                    return childAt;
                }
            }
            return null;
        }

        public void detachView(View view) {
            int indexOfChild = this.mChildHelper.indexOfChild(view);
            if (indexOfChild >= 0) {
                detachViewInternal(indexOfChild, view);
            }
        }

        public void detachViewAt(int i) {
            detachViewInternal(i, getChildAt(i));
        }

        private void detachViewInternal(int i, View view) {
            this.mChildHelper.detachViewFromParent(i);
        }

        public void attachView(View view, int i, LayoutParams layoutParams) {
            ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            if (childViewHolderInt.isRemoved()) {
                this.mRecyclerView.mViewInfoStore.addToDisappearedInLayout(childViewHolderInt);
            } else {
                this.mRecyclerView.mViewInfoStore.removeFromDisappearedInLayout(childViewHolderInt);
            }
            this.mChildHelper.attachViewToParent(view, i, layoutParams, childViewHolderInt.isRemoved());
        }

        public void attachView(View view, int i) {
            attachView(view, i, (LayoutParams) view.getLayoutParams());
        }

        public void attachView(View view) {
            attachView(view, -1);
        }

        public void removeDetachedView(View view) {
            this.mRecyclerView.removeDetachedView(view, false);
        }

        public void moveView(int i, int i2) {
            View childAt = getChildAt(i);
            if (childAt == null) {
                throw new IllegalArgumentException("Cannot move a child from non-existing index:" + i);
            }
            detachViewAt(i);
            attachView(childAt, i2);
        }

        public void detachAndScrapView(View view, Recycler recycler) {
            scrapOrRecycleView(recycler, this.mChildHelper.indexOfChild(view), view);
        }

        public void detachAndScrapViewAt(int i, Recycler recycler) {
            scrapOrRecycleView(recycler, i, getChildAt(i));
        }

        public void removeAndRecycleView(View view, Recycler recycler) {
            removeView(view);
            recycler.recycleView(view);
        }

        public void removeAndRecycleViewAt(int i, Recycler recycler) {
            View childAt = getChildAt(i);
            removeViewAt(i);
            recycler.recycleView(childAt);
        }

        public int getChildCount() {
            if (this.mChildHelper != null) {
                return this.mChildHelper.getChildCount();
            }
            return 0;
        }

        public View getChildAt(int i) {
            if (this.mChildHelper != null) {
                return this.mChildHelper.getChildAt(i);
            }
            return null;
        }

        public int getWidth() {
            if (this.mRecyclerView != null) {
                return this.mRecyclerView.getWidth();
            }
            return 0;
        }

        public int getHeight() {
            if (this.mRecyclerView != null) {
                return this.mRecyclerView.getHeight();
            }
            return 0;
        }

        public int getPaddingLeft() {
            if (this.mRecyclerView != null) {
                return this.mRecyclerView.getPaddingLeft();
            }
            return 0;
        }

        public int getPaddingTop() {
            if (this.mRecyclerView != null) {
                return this.mRecyclerView.getPaddingTop();
            }
            return 0;
        }

        public int getPaddingRight() {
            if (this.mRecyclerView != null) {
                return this.mRecyclerView.getPaddingRight();
            }
            return 0;
        }

        public int getPaddingBottom() {
            if (this.mRecyclerView != null) {
                return this.mRecyclerView.getPaddingBottom();
            }
            return 0;
        }

        public int getPaddingStart() {
            if (this.mRecyclerView != null) {
                return ViewCompat.getPaddingStart(this.mRecyclerView);
            }
            return 0;
        }

        public int getPaddingEnd() {
            if (this.mRecyclerView != null) {
                return ViewCompat.getPaddingEnd(this.mRecyclerView);
            }
            return 0;
        }

        public boolean isFocused() {
            return this.mRecyclerView != null && this.mRecyclerView.isFocused();
        }

        public boolean hasFocus() {
            return this.mRecyclerView != null && this.mRecyclerView.hasFocus();
        }

        public View getFocusedChild() {
            View focusedChild;
            if (this.mRecyclerView == null || (focusedChild = this.mRecyclerView.getFocusedChild()) == null || this.mChildHelper.isHidden(focusedChild)) {
                return null;
            }
            return focusedChild;
        }

        public int getItemCount() {
            Adapter adapter = this.mRecyclerView != null ? this.mRecyclerView.getAdapter() : null;
            if (adapter != null) {
                return adapter.getItemCount();
            }
            return 0;
        }

        public void offsetChildrenHorizontal(int i) {
            if (this.mRecyclerView != null) {
                this.mRecyclerView.offsetChildrenHorizontal(i);
            }
        }

        public void offsetChildrenVertical(int i) {
            if (this.mRecyclerView != null) {
                this.mRecyclerView.offsetChildrenVertical(i);
            }
        }

        public void ignoreView(View view) {
            if (view.getParent() != this.mRecyclerView || this.mRecyclerView.indexOfChild(view) == -1) {
                throw new IllegalArgumentException("View should be fully attached to be ignored");
            }
            ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            childViewHolderInt.addFlags(128);
            this.mRecyclerView.mViewInfoStore.removeViewHolder(childViewHolderInt);
        }

        public void stopIgnoringView(View view) {
            ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            childViewHolderInt.stopIgnoring();
            childViewHolderInt.resetInternal();
            childViewHolderInt.addFlags(4);
        }

        public void detachAndScrapAttachedViews(Recycler recycler) {
            for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
                scrapOrRecycleView(recycler, childCount, getChildAt(childCount));
            }
        }

        private void scrapOrRecycleView(Recycler recycler, int i, View view) {
            ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            if (childViewHolderInt.shouldIgnore()) {
                return;
            }
            if (childViewHolderInt.isInvalid() && !childViewHolderInt.isRemoved() && !this.mRecyclerView.mAdapter.hasStableIds()) {
                removeViewAt(i);
                recycler.recycleViewHolderInternal(childViewHolderInt);
            } else {
                detachViewAt(i);
                recycler.scrapView(view);
            }
        }

        void removeAndRecycleScrapInt(Recycler recycler) {
            int scrapCount = recycler.getScrapCount();
            for (int i = scrapCount - 1; i >= 0; i--) {
                View scrapViewAt = recycler.getScrapViewAt(i);
                ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(scrapViewAt);
                if (!childViewHolderInt.shouldIgnore()) {
                    childViewHolderInt.setIsRecyclable(false);
                    if (childViewHolderInt.isTmpDetached()) {
                        this.mRecyclerView.removeDetachedView(scrapViewAt, false);
                    }
                    if (this.mRecyclerView.mItemAnimator != null) {
                        this.mRecyclerView.mItemAnimator.endAnimation(childViewHolderInt);
                    }
                    childViewHolderInt.setIsRecyclable(true);
                    recycler.quickRecycleScrapView(scrapViewAt);
                }
            }
            recycler.clearScrap();
            if (scrapCount > 0) {
                this.mRecyclerView.invalidate();
            }
        }

        public void measureChild(View view, int i, int i2) {
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            Rect itemDecorInsetsForChild = this.mRecyclerView.getItemDecorInsetsForChild(view);
            view.measure(getChildMeasureSpec(getWidth(), getPaddingLeft() + getPaddingRight() + i + itemDecorInsetsForChild.left + itemDecorInsetsForChild.right, layoutParams.width, canScrollHorizontally()), getChildMeasureSpec(getHeight(), getPaddingTop() + getPaddingBottom() + i2 + itemDecorInsetsForChild.top + itemDecorInsetsForChild.bottom, layoutParams.height, canScrollVertically()));
        }

        public void measureChildWithMargins(View view, int i, int i2) {
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            Rect itemDecorInsetsForChild = this.mRecyclerView.getItemDecorInsetsForChild(view);
            view.measure(getChildMeasureSpec(getWidth(), getPaddingLeft() + getPaddingRight() + layoutParams.leftMargin + layoutParams.rightMargin + i + itemDecorInsetsForChild.left + itemDecorInsetsForChild.right, layoutParams.width, canScrollHorizontally()), getChildMeasureSpec(getHeight(), getPaddingTop() + getPaddingBottom() + layoutParams.topMargin + layoutParams.bottomMargin + i2 + itemDecorInsetsForChild.top + itemDecorInsetsForChild.bottom, layoutParams.height, canScrollVertically()));
        }

        /* JADX WARN: Code restructure failed: missing block: B:3:0x000a, code lost:
        
            if (r3 >= 0) goto L5;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public static int getChildMeasureSpec(int r1, int r2, int r3, boolean r4) {
            /*
                int r1 = r1 - r2
                r2 = 0
                int r1 = java.lang.Math.max(r2, r1)
                r0 = 1073741824(0x40000000, float:2.0)
                if (r4 == 0) goto L12
                if (r3 < 0) goto L10
            Lc:
                r1 = r3
            Ld:
                r2 = 1073741824(0x40000000, float:2.0)
                goto L1e
            L10:
                r1 = 0
                goto L1e
            L12:
                if (r3 < 0) goto L15
                goto Lc
            L15:
                r4 = -1
                if (r3 != r4) goto L19
                goto Ld
            L19:
                r4 = -2
                if (r3 != r4) goto L10
                r2 = -2147483648(0xffffffff80000000, float:-0.0)
            L1e:
                int r1 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r2)
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.RecyclerView.LayoutManager.getChildMeasureSpec(int, int, int, boolean):int");
        }

        public int getDecoratedMeasuredWidth(View view) {
            Rect rect = ((LayoutParams) view.getLayoutParams()).mDecorInsets;
            return view.getMeasuredWidth() + rect.left + rect.right;
        }

        public int getDecoratedMeasuredHeight(View view) {
            Rect rect = ((LayoutParams) view.getLayoutParams()).mDecorInsets;
            return view.getMeasuredHeight() + rect.top + rect.bottom;
        }

        public void layoutDecorated(View view, int i, int i2, int i3, int i4) {
            Rect rect = ((LayoutParams) view.getLayoutParams()).mDecorInsets;
            view.layout(i + rect.left, i2 + rect.top, i3 - rect.right, i4 - rect.bottom);
        }

        public int getDecoratedLeft(View view) {
            return view.getLeft() - getLeftDecorationWidth(view);
        }

        public int getDecoratedTop(View view) {
            return view.getTop() - getTopDecorationHeight(view);
        }

        public int getDecoratedRight(View view) {
            return view.getRight() + getRightDecorationWidth(view);
        }

        public int getDecoratedBottom(View view) {
            return view.getBottom() + getBottomDecorationHeight(view);
        }

        public void calculateItemDecorationsForChild(View view, Rect rect) {
            if (this.mRecyclerView == null) {
                rect.set(0, 0, 0, 0);
            } else {
                rect.set(this.mRecyclerView.getItemDecorInsetsForChild(view));
            }
        }

        public int getTopDecorationHeight(View view) {
            return ((LayoutParams) view.getLayoutParams()).mDecorInsets.top;
        }

        public int getBottomDecorationHeight(View view) {
            return ((LayoutParams) view.getLayoutParams()).mDecorInsets.bottom;
        }

        public int getLeftDecorationWidth(View view) {
            return ((LayoutParams) view.getLayoutParams()).mDecorInsets.left;
        }

        public int getRightDecorationWidth(View view) {
            return ((LayoutParams) view.getLayoutParams()).mDecorInsets.right;
        }

        public boolean requestChildRectangleOnScreen(RecyclerView recyclerView, View view, Rect rect, boolean z) {
            int paddingLeft = getPaddingLeft();
            int paddingTop = getPaddingTop();
            int width = getWidth() - getPaddingRight();
            int height = getHeight() - getPaddingBottom();
            int left = view.getLeft() + rect.left;
            int top = view.getTop() + rect.top;
            int width2 = rect.width() + left;
            int height2 = rect.height() + top;
            int i = left - paddingLeft;
            int min = Math.min(0, i);
            int i2 = top - paddingTop;
            int min2 = Math.min(0, i2);
            int i3 = width2 - width;
            int max = Math.max(0, i3);
            int max2 = Math.max(0, height2 - height);
            if (getLayoutDirection() != 1) {
                if (min == 0) {
                    min = Math.min(i, max);
                }
                max = min;
            } else if (max == 0) {
                max = Math.max(min, i3);
            }
            if (min2 == 0) {
                min2 = Math.min(i2, max2);
            }
            if (max == 0 && min2 == 0) {
                return false;
            }
            if (z) {
                recyclerView.scrollBy(max, min2);
            } else {
                recyclerView.smoothScrollBy(max, min2);
            }
            return true;
        }

        @Deprecated
        public boolean onRequestChildFocus(RecyclerView recyclerView, View view, View view2) {
            return isSmoothScrolling() || recyclerView.isComputingLayout();
        }

        public boolean onRequestChildFocus(RecyclerView recyclerView, State state, View view, View view2) {
            return onRequestChildFocus(recyclerView, view, view2);
        }

        public void onItemsUpdated(RecyclerView recyclerView, int i, int i2, Object obj) {
            onItemsUpdated(recyclerView, i, i2);
        }

        public void onMeasure(Recycler recycler, State state, int i, int i2) {
            this.mRecyclerView.defaultOnMeasure(i, i2);
        }

        public void setMeasuredDimension(int i, int i2) {
            this.mRecyclerView.setMeasuredDimension(i, i2);
        }

        public int getMinimumWidth() {
            return ViewCompat.getMinimumWidth(this.mRecyclerView);
        }

        public int getMinimumHeight() {
            return ViewCompat.getMinimumHeight(this.mRecyclerView);
        }

        void stopSmoothScroller() {
            if (this.mSmoothScroller != null) {
                this.mSmoothScroller.stop();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void onSmoothScrollerStopped(SmoothScroller smoothScroller) {
            if (this.mSmoothScroller == smoothScroller) {
                this.mSmoothScroller = null;
            }
        }

        public void removeAndRecycleAllViews(Recycler recycler) {
            for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
                if (!RecyclerView.getChildViewHolderInt(getChildAt(childCount)).shouldIgnore()) {
                    removeAndRecycleViewAt(childCount, recycler);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            onInitializeAccessibilityNodeInfo(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, accessibilityNodeInfoCompat);
        }

        public void onInitializeAccessibilityNodeInfo(Recycler recycler, State state, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            if (ViewCompat.canScrollVertically(this.mRecyclerView, -1) || ViewCompat.canScrollHorizontally(this.mRecyclerView, -1)) {
                accessibilityNodeInfoCompat.addAction(8192);
                accessibilityNodeInfoCompat.setScrollable(true);
            }
            if (ViewCompat.canScrollVertically(this.mRecyclerView, 1) || ViewCompat.canScrollHorizontally(this.mRecyclerView, 1)) {
                accessibilityNodeInfoCompat.addAction(4096);
                accessibilityNodeInfoCompat.setScrollable(true);
            }
            accessibilityNodeInfoCompat.setCollectionInfo(AccessibilityNodeInfoCompat.CollectionInfoCompat.obtain(getRowCountForAccessibility(recycler, state), getColumnCountForAccessibility(recycler, state), isLayoutHierarchical(recycler, state), getSelectionModeForAccessibility(recycler, state)));
        }

        public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
            onInitializeAccessibilityEvent(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, accessibilityEvent);
        }

        public void onInitializeAccessibilityEvent(Recycler recycler, State state, AccessibilityEvent accessibilityEvent) {
            AccessibilityRecordCompat asRecord = AccessibilityEventCompat.asRecord(accessibilityEvent);
            if (this.mRecyclerView == null || asRecord == null) {
                return;
            }
            boolean z = true;
            if (!ViewCompat.canScrollVertically(this.mRecyclerView, 1) && !ViewCompat.canScrollVertically(this.mRecyclerView, -1) && !ViewCompat.canScrollHorizontally(this.mRecyclerView, -1) && !ViewCompat.canScrollHorizontally(this.mRecyclerView, 1)) {
                z = false;
            }
            asRecord.setScrollable(z);
            if (this.mRecyclerView.mAdapter != null) {
                asRecord.setItemCount(this.mRecyclerView.mAdapter.getItemCount());
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void onInitializeAccessibilityNodeInfoForItem(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            if (childViewHolderInt == null || childViewHolderInt.isRemoved() || this.mChildHelper.isHidden(childViewHolderInt.itemView)) {
                return;
            }
            onInitializeAccessibilityNodeInfoForItem(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, view, accessibilityNodeInfoCompat);
        }

        public void onInitializeAccessibilityNodeInfoForItem(Recycler recycler, State state, View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            accessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(canScrollVertically() ? getPosition(view) : 0, 1, canScrollHorizontally() ? getPosition(view) : 0, 1, false, false));
        }

        public void requestSimpleAnimationsInNextLayout() {
            this.mRequestedSimpleAnimations = true;
        }

        public int getRowCountForAccessibility(Recycler recycler, State state) {
            if (this.mRecyclerView == null || this.mRecyclerView.mAdapter == null || !canScrollVertically()) {
                return 1;
            }
            return this.mRecyclerView.mAdapter.getItemCount();
        }

        public int getColumnCountForAccessibility(Recycler recycler, State state) {
            if (this.mRecyclerView == null || this.mRecyclerView.mAdapter == null || !canScrollHorizontally()) {
                return 1;
            }
            return this.mRecyclerView.mAdapter.getItemCount();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean performAccessibilityAction(int i, Bundle bundle) {
            return performAccessibilityAction(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, i, bundle);
        }

        /* JADX WARN: Removed duplicated region for block: B:12:0x0074 A[ADDED_TO_REGION] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public boolean performAccessibilityAction(android.support.v7.widget.RecyclerView.Recycler r2, android.support.v7.widget.RecyclerView.State r3, int r4, android.os.Bundle r5) {
            /*
                r1 = this;
                android.support.v7.widget.RecyclerView r2 = r1.mRecyclerView
                r3 = 0
                if (r2 != 0) goto L6
                return r3
            L6:
                r2 = 4096(0x1000, float:5.74E-42)
                r5 = 1
                if (r4 == r2) goto L44
                r2 = 8192(0x2000, float:1.148E-41)
                if (r4 == r2) goto L12
                r2 = 0
            L10:
                r4 = 0
                goto L72
            L12:
                android.support.v7.widget.RecyclerView r2 = r1.mRecyclerView
                r4 = -1
                boolean r2 = android.support.v4.view.ViewCompat.canScrollVertically(r2, r4)
                if (r2 == 0) goto L2b
                int r2 = r1.getHeight()
                int r0 = r1.getPaddingTop()
                int r2 = r2 - r0
                int r0 = r1.getPaddingBottom()
                int r2 = r2 - r0
                int r2 = -r2
                goto L2c
            L2b:
                r2 = 0
            L2c:
                android.support.v7.widget.RecyclerView r0 = r1.mRecyclerView
                boolean r4 = android.support.v4.view.ViewCompat.canScrollHorizontally(r0, r4)
                if (r4 == 0) goto L10
                int r4 = r1.getWidth()
                int r0 = r1.getPaddingLeft()
                int r4 = r4 - r0
                int r0 = r1.getPaddingRight()
                int r4 = r4 - r0
                int r4 = -r4
                goto L72
            L44:
                android.support.v7.widget.RecyclerView r2 = r1.mRecyclerView
                boolean r2 = android.support.v4.view.ViewCompat.canScrollVertically(r2, r5)
                if (r2 == 0) goto L5b
                int r2 = r1.getHeight()
                int r4 = r1.getPaddingTop()
                int r2 = r2 - r4
                int r4 = r1.getPaddingBottom()
                int r2 = r2 - r4
                goto L5c
            L5b:
                r2 = 0
            L5c:
                android.support.v7.widget.RecyclerView r4 = r1.mRecyclerView
                boolean r4 = android.support.v4.view.ViewCompat.canScrollHorizontally(r4, r5)
                if (r4 == 0) goto L10
                int r4 = r1.getWidth()
                int r0 = r1.getPaddingLeft()
                int r4 = r4 - r0
                int r0 = r1.getPaddingRight()
                int r4 = r4 - r0
            L72:
                if (r2 != 0) goto L77
                if (r4 != 0) goto L77
                return r3
            L77:
                android.support.v7.widget.RecyclerView r3 = r1.mRecyclerView
                r3.scrollBy(r4, r2)
                return r5
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.RecyclerView.LayoutManager.performAccessibilityAction(android.support.v7.widget.RecyclerView$Recycler, android.support.v7.widget.RecyclerView$State, int, android.os.Bundle):boolean");
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean performAccessibilityActionForItem(View view, int i, Bundle bundle) {
            return performAccessibilityActionForItem(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, view, i, bundle);
        }

        public static Properties getProperties(Context context, AttributeSet attributeSet, int i, int i2) {
            Properties properties = new Properties();
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.RecyclerView, i, i2);
            properties.orientation = obtainStyledAttributes.getInt(R.styleable.RecyclerView_android_orientation, 1);
            properties.spanCount = obtainStyledAttributes.getInt(R.styleable.RecyclerView_spanCount, 1);
            properties.reverseLayout = obtainStyledAttributes.getBoolean(R.styleable.RecyclerView_reverseLayout, false);
            properties.stackFromEnd = obtainStyledAttributes.getBoolean(R.styleable.RecyclerView_stackFromEnd, false);
            obtainStyledAttributes.recycle();
            return properties;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class ItemDecoration {
        @Deprecated
        public void onDraw(Canvas canvas, RecyclerView recyclerView) {
        }

        @Deprecated
        public void onDrawOver(Canvas canvas, RecyclerView recyclerView) {
        }

        public void onDraw(Canvas canvas, RecyclerView recyclerView, State state) {
            onDraw(canvas, recyclerView);
        }

        public void onDrawOver(Canvas canvas, RecyclerView recyclerView, State state) {
            onDrawOver(canvas, recyclerView);
        }

        @Deprecated
        public void getItemOffsets(Rect rect, int i, RecyclerView recyclerView) {
            rect.set(0, 0, 0, 0);
        }

        public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, State state) {
            getItemOffsets(rect, ((LayoutParams) view.getLayoutParams()).getViewLayoutPosition(), recyclerView);
        }
    }

    /* loaded from: classes.dex */
    public static abstract class ViewHolder {
        static final int FLAG_ADAPTER_FULLUPDATE = 1024;
        static final int FLAG_ADAPTER_POSITION_UNKNOWN = 512;
        static final int FLAG_APPEARED_IN_PRE_LAYOUT = 4096;
        static final int FLAG_BOUNCED_FROM_HIDDEN_LIST = 8192;
        static final int FLAG_BOUND = 1;
        static final int FLAG_IGNORE = 128;
        static final int FLAG_INVALID = 4;
        static final int FLAG_MOVED = 2048;
        static final int FLAG_NOT_RECYCLABLE = 16;
        static final int FLAG_REMOVED = 8;
        static final int FLAG_RETURNED_FROM_SCRAP = 32;
        static final int FLAG_TMP_DETACHED = 256;
        static final int FLAG_UPDATE = 2;
        private static final List<Object> FULLUPDATE_PAYLOADS = Collections.EMPTY_LIST;
        public final View itemView;
        private int mFlags;
        RecyclerView mOwnerRecyclerView;
        int mPosition = -1;
        int mOldPosition = -1;
        long mItemId = -1;
        int mItemViewType = -1;
        int mPreLayoutPosition = -1;
        ViewHolder mShadowedHolder = null;
        ViewHolder mShadowingHolder = null;
        List<Object> mPayloads = null;
        List<Object> mUnmodifiedPayloads = null;
        private int mIsRecyclableCount = 0;
        private Recycler mScrapContainer = null;
        private boolean mInChangeScrap = false;
        private int mWasImportantForAccessibilityBeforeHidden = 0;

        public ViewHolder(View view) {
            if (view == null) {
                throw new IllegalArgumentException("itemView may not be null");
            }
            this.itemView = view;
        }

        void flagRemovedAndOffsetPosition(int i, int i2, boolean z) {
            addFlags(8);
            offsetPosition(i2, z);
            this.mPosition = i;
        }

        void offsetPosition(int i, boolean z) {
            if (this.mOldPosition == -1) {
                this.mOldPosition = this.mPosition;
            }
            if (this.mPreLayoutPosition == -1) {
                this.mPreLayoutPosition = this.mPosition;
            }
            if (z) {
                this.mPreLayoutPosition += i;
            }
            this.mPosition += i;
            if (this.itemView.getLayoutParams() != null) {
                ((LayoutParams) this.itemView.getLayoutParams()).mInsetsDirty = true;
            }
        }

        void clearOldPosition() {
            this.mOldPosition = -1;
            this.mPreLayoutPosition = -1;
        }

        void saveOldPosition() {
            if (this.mOldPosition == -1) {
                this.mOldPosition = this.mPosition;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean shouldIgnore() {
            return (this.mFlags & 128) != 0;
        }

        @Deprecated
        public final int getPosition() {
            return this.mPreLayoutPosition == -1 ? this.mPosition : this.mPreLayoutPosition;
        }

        public final int getLayoutPosition() {
            return this.mPreLayoutPosition == -1 ? this.mPosition : this.mPreLayoutPosition;
        }

        public final int getAdapterPosition() {
            if (this.mOwnerRecyclerView == null) {
                return -1;
            }
            return this.mOwnerRecyclerView.getAdapterPositionFor(this);
        }

        public final int getOldPosition() {
            return this.mOldPosition;
        }

        public final long getItemId() {
            return this.mItemId;
        }

        public final int getItemViewType() {
            return this.mItemViewType;
        }

        boolean isScrap() {
            return this.mScrapContainer != null;
        }

        void unScrap() {
            this.mScrapContainer.unscrapView(this);
        }

        boolean wasReturnedFromScrap() {
            return (this.mFlags & 32) != 0;
        }

        void clearReturnedFromScrapFlag() {
            this.mFlags &= -33;
        }

        void clearTmpDetachFlag() {
            this.mFlags &= -257;
        }

        void stopIgnoring() {
            this.mFlags &= -129;
        }

        void setScrapContainer(Recycler recycler, boolean z) {
            this.mScrapContainer = recycler;
            this.mInChangeScrap = z;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean isInvalid() {
            return (this.mFlags & 4) != 0;
        }

        boolean needsUpdate() {
            return (this.mFlags & 2) != 0;
        }

        boolean isBound() {
            return (this.mFlags & 1) != 0;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean isRemoved() {
            return (this.mFlags & 8) != 0;
        }

        boolean hasAnyOfTheFlags(int i) {
            return (i & this.mFlags) != 0;
        }

        boolean isTmpDetached() {
            return (this.mFlags & 256) != 0;
        }

        boolean isAdapterPositionUnknown() {
            return (this.mFlags & 512) != 0 || isInvalid();
        }

        void setFlags(int i, int i2) {
            this.mFlags = (i & i2) | (this.mFlags & (i2 ^ (-1)));
        }

        void addFlags(int i) {
            this.mFlags = i | this.mFlags;
        }

        void addChangePayload(Object obj) {
            if (obj == null) {
                addFlags(1024);
            } else if ((1024 & this.mFlags) == 0) {
                createPayloadsIfNeeded();
                this.mPayloads.add(obj);
            }
        }

        private void createPayloadsIfNeeded() {
            if (this.mPayloads == null) {
                this.mPayloads = new ArrayList();
                this.mUnmodifiedPayloads = Collections.unmodifiableList(this.mPayloads);
            }
        }

        void clearPayload() {
            if (this.mPayloads != null) {
                this.mPayloads.clear();
            }
            this.mFlags &= -1025;
        }

        List<Object> getUnmodifiedPayloads() {
            if ((this.mFlags & 1024) == 0) {
                if (this.mPayloads == null || this.mPayloads.size() == 0) {
                    return FULLUPDATE_PAYLOADS;
                }
                return this.mUnmodifiedPayloads;
            }
            return FULLUPDATE_PAYLOADS;
        }

        void resetInternal() {
            this.mFlags = 0;
            this.mPosition = -1;
            this.mOldPosition = -1;
            this.mItemId = -1L;
            this.mPreLayoutPosition = -1;
            this.mIsRecyclableCount = 0;
            this.mShadowedHolder = null;
            this.mShadowingHolder = null;
            clearPayload();
            this.mWasImportantForAccessibilityBeforeHidden = 0;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void onEnteredHiddenState() {
            this.mWasImportantForAccessibilityBeforeHidden = ViewCompat.getImportantForAccessibility(this.itemView);
            ViewCompat.setImportantForAccessibility(this.itemView, 4);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void onLeftHiddenState() {
            ViewCompat.setImportantForAccessibility(this.itemView, this.mWasImportantForAccessibilityBeforeHidden);
            this.mWasImportantForAccessibilityBeforeHidden = 0;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("ViewHolder{" + Integer.toHexString(hashCode()) + " position=" + this.mPosition + " id=" + this.mItemId + ", oldPos=" + this.mOldPosition + ", pLpos:" + this.mPreLayoutPosition);
            if (isScrap()) {
                sb.append(" scrap ");
                sb.append(this.mInChangeScrap ? "[changeScrap]" : "[attachedScrap]");
            }
            if (isInvalid()) {
                sb.append(" invalid");
            }
            if (!isBound()) {
                sb.append(" unbound");
            }
            if (needsUpdate()) {
                sb.append(" update");
            }
            if (isRemoved()) {
                sb.append(" removed");
            }
            if (shouldIgnore()) {
                sb.append(" ignored");
            }
            if (isTmpDetached()) {
                sb.append(" tmpDetached");
            }
            if (!isRecyclable()) {
                sb.append(" not recyclable(" + this.mIsRecyclableCount + ")");
            }
            if (isAdapterPositionUnknown()) {
                sb.append(" undefined adapter position");
            }
            if (this.itemView.getParent() == null) {
                sb.append(" no parent");
            }
            sb.append("}");
            return sb.toString();
        }

        public final void setIsRecyclable(boolean z) {
            this.mIsRecyclableCount = z ? this.mIsRecyclableCount - 1 : this.mIsRecyclableCount + 1;
            if (this.mIsRecyclableCount < 0) {
                this.mIsRecyclableCount = 0;
                Log.e("View", "isRecyclable decremented below 0: unmatched pair of setIsRecyable() calls for " + this);
                return;
            }
            if (!z && this.mIsRecyclableCount == 1) {
                this.mFlags |= 16;
            } else if (z && this.mIsRecyclableCount == 0) {
                this.mFlags &= -17;
            }
        }

        public final boolean isRecyclable() {
            return (this.mFlags & 16) == 0 && !ViewCompat.hasTransientState(this.itemView);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean shouldBeKeptAsChild() {
            return (this.mFlags & 16) != 0;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean doesTransientStatePreventRecycling() {
            return (this.mFlags & 16) == 0 && ViewCompat.hasTransientState(this.itemView);
        }

        boolean isUpdated() {
            return (this.mFlags & 2) != 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getAdapterPositionFor(ViewHolder viewHolder) {
        if (viewHolder.hasAnyOfTheFlags(524) || !viewHolder.isBound()) {
            return -1;
        }
        return this.mAdapterHelper.applyPendingUpdatesToPosition(viewHolder.mPosition);
    }

    @Override // android.view.View, android.support.v4.view.NestedScrollingChild
    public void setNestedScrollingEnabled(boolean z) {
        this.mScrollingChildHelper.setNestedScrollingEnabled(z);
    }

    @Override // android.view.View, android.support.v4.view.NestedScrollingChild
    public boolean isNestedScrollingEnabled() {
        return this.mScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override // android.view.View, android.support.v4.view.NestedScrollingChild
    public boolean startNestedScroll(int i) {
        return this.mScrollingChildHelper.startNestedScroll(i);
    }

    @Override // android.view.View, android.support.v4.view.NestedScrollingChild
    public void stopNestedScroll() {
        this.mScrollingChildHelper.stopNestedScroll();
    }

    @Override // android.view.View, android.support.v4.view.NestedScrollingChild
    public boolean hasNestedScrollingParent() {
        return this.mScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override // android.view.View, android.support.v4.view.NestedScrollingChild
    public boolean dispatchNestedScroll(int i, int i2, int i3, int i4, int[] iArr) {
        return this.mScrollingChildHelper.dispatchNestedScroll(i, i2, i3, i4, iArr);
    }

    @Override // android.view.View, android.support.v4.view.NestedScrollingChild
    public boolean dispatchNestedPreScroll(int i, int i2, int[] iArr, int[] iArr2) {
        return this.mScrollingChildHelper.dispatchNestedPreScroll(i, i2, iArr, iArr2);
    }

    @Override // android.view.View, android.support.v4.view.NestedScrollingChild
    public boolean dispatchNestedFling(float f, float f2, boolean z) {
        return this.mScrollingChildHelper.dispatchNestedFling(f, f2, z);
    }

    @Override // android.view.View, android.support.v4.view.NestedScrollingChild
    public boolean dispatchNestedPreFling(float f, float f2) {
        return this.mScrollingChildHelper.dispatchNestedPreFling(f, f2);
    }

    /* loaded from: classes.dex */
    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        final Rect mDecorInsets;
        boolean mInsetsDirty;
        boolean mPendingInvalidate;
        ViewHolder mViewHolder;

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            this.mDecorInsets = new Rect();
            this.mInsetsDirty = true;
            this.mPendingInvalidate = false;
        }

        public LayoutParams(int i, int i2) {
            super(i, i2);
            this.mDecorInsets = new Rect();
            this.mInsetsDirty = true;
            this.mPendingInvalidate = false;
        }

        public LayoutParams(ViewGroup.MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
            this.mDecorInsets = new Rect();
            this.mInsetsDirty = true;
            this.mPendingInvalidate = false;
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
            this.mDecorInsets = new Rect();
            this.mInsetsDirty = true;
            this.mPendingInvalidate = false;
        }

        public LayoutParams(LayoutParams layoutParams) {
            super((ViewGroup.LayoutParams) layoutParams);
            this.mDecorInsets = new Rect();
            this.mInsetsDirty = true;
            this.mPendingInvalidate = false;
        }

        public boolean viewNeedsUpdate() {
            return this.mViewHolder.needsUpdate();
        }

        public boolean isViewInvalid() {
            return this.mViewHolder.isInvalid();
        }

        public boolean isItemRemoved() {
            return this.mViewHolder.isRemoved();
        }

        public boolean isItemChanged() {
            return this.mViewHolder.isUpdated();
        }

        public int getViewPosition() {
            return this.mViewHolder.getPosition();
        }

        public int getViewLayoutPosition() {
            return this.mViewHolder.getLayoutPosition();
        }

        public int getViewAdapterPosition() {
            return this.mViewHolder.getAdapterPosition();
        }
    }

    /* loaded from: classes.dex */
    public static abstract class AdapterDataObserver {
        public void onChanged() {
        }

        public void onItemRangeChanged(int i, int i2) {
        }

        public void onItemRangeInserted(int i, int i2) {
        }

        public void onItemRangeMoved(int i, int i2, int i3) {
        }

        public void onItemRangeRemoved(int i, int i2) {
        }

        public void onItemRangeChanged(int i, int i2, Object obj) {
            onItemRangeChanged(i, i2);
        }
    }

    /* loaded from: classes.dex */
    public static abstract class SmoothScroller {
        private LayoutManager mLayoutManager;
        private boolean mPendingInitialRun;
        private RecyclerView mRecyclerView;
        private boolean mRunning;
        private View mTargetView;
        private int mTargetPosition = -1;
        private final Action mRecyclingAction = new Action(0, 0);

        protected abstract void onSeekTargetStep(int i, int i2, State state, Action action);

        protected abstract void onStart();

        protected abstract void onStop();

        protected abstract void onTargetFound(View view, State state, Action action);

        void start(RecyclerView recyclerView, LayoutManager layoutManager) {
            this.mRecyclerView = recyclerView;
            this.mLayoutManager = layoutManager;
            if (this.mTargetPosition == -1) {
                throw new IllegalArgumentException("Invalid target position");
            }
            this.mRecyclerView.mState.mTargetPosition = this.mTargetPosition;
            this.mRunning = true;
            this.mPendingInitialRun = true;
            this.mTargetView = findViewByPosition(getTargetPosition());
            onStart();
            this.mRecyclerView.mViewFlinger.postOnAnimation();
        }

        public void setTargetPosition(int i) {
            this.mTargetPosition = i;
        }

        @Nullable
        public LayoutManager getLayoutManager() {
            return this.mLayoutManager;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public final void stop() {
            if (this.mRunning) {
                onStop();
                this.mRecyclerView.mState.mTargetPosition = -1;
                this.mTargetView = null;
                this.mTargetPosition = -1;
                this.mPendingInitialRun = false;
                this.mRunning = false;
                this.mLayoutManager.onSmoothScrollerStopped(this);
                this.mLayoutManager = null;
                this.mRecyclerView = null;
            }
        }

        public boolean isPendingInitialRun() {
            return this.mPendingInitialRun;
        }

        public boolean isRunning() {
            return this.mRunning;
        }

        public int getTargetPosition() {
            return this.mTargetPosition;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void onAnimation(int i, int i2) {
            RecyclerView recyclerView = this.mRecyclerView;
            if (!this.mRunning || this.mTargetPosition == -1 || recyclerView == null) {
                stop();
            }
            this.mPendingInitialRun = false;
            if (this.mTargetView != null) {
                if (getChildPosition(this.mTargetView) == this.mTargetPosition) {
                    onTargetFound(this.mTargetView, recyclerView.mState, this.mRecyclingAction);
                    this.mRecyclingAction.runIfNecessary(recyclerView);
                    stop();
                } else {
                    Log.e(RecyclerView.TAG, "Passed over target position while smooth scrolling.");
                    this.mTargetView = null;
                }
            }
            if (this.mRunning) {
                onSeekTargetStep(i, i2, recyclerView.mState, this.mRecyclingAction);
                boolean hasJumpTarget = this.mRecyclingAction.hasJumpTarget();
                this.mRecyclingAction.runIfNecessary(recyclerView);
                if (hasJumpTarget) {
                    if (this.mRunning) {
                        this.mPendingInitialRun = true;
                        recyclerView.mViewFlinger.postOnAnimation();
                    } else {
                        stop();
                    }
                }
            }
        }

        public int getChildPosition(View view) {
            return this.mRecyclerView.getChildLayoutPosition(view);
        }

        public int getChildCount() {
            return this.mRecyclerView.mLayout.getChildCount();
        }

        public View findViewByPosition(int i) {
            return this.mRecyclerView.mLayout.findViewByPosition(i);
        }

        @Deprecated
        public void instantScrollToPosition(int i) {
            this.mRecyclerView.scrollToPosition(i);
        }

        protected void onChildAttachedToWindow(View view) {
            if (getChildPosition(view) == getTargetPosition()) {
                this.mTargetView = view;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void normalize(PointF pointF) {
            double sqrt = Math.sqrt((pointF.x * pointF.x) + (pointF.y * pointF.y));
            double d = pointF.x;
            Double.isNaN(d);
            pointF.x = (float) (d / sqrt);
            double d2 = pointF.y;
            Double.isNaN(d2);
            pointF.y = (float) (d2 / sqrt);
        }

        /* loaded from: classes.dex */
        public static class Action {
            public static final int UNDEFINED_DURATION = Integer.MIN_VALUE;
            private boolean changed;
            private int consecutiveUpdates;
            private int mDuration;
            private int mDx;
            private int mDy;
            private Interpolator mInterpolator;
            private int mJumpToPosition;

            public Action(int i, int i2) {
                this(i, i2, Integer.MIN_VALUE, null);
            }

            public Action(int i, int i2, int i3) {
                this(i, i2, i3, null);
            }

            public Action(int i, int i2, int i3, Interpolator interpolator) {
                this.mJumpToPosition = -1;
                this.changed = false;
                this.consecutiveUpdates = 0;
                this.mDx = i;
                this.mDy = i2;
                this.mDuration = i3;
                this.mInterpolator = interpolator;
            }

            public void jumpTo(int i) {
                this.mJumpToPosition = i;
            }

            boolean hasJumpTarget() {
                return this.mJumpToPosition >= 0;
            }

            /* JADX INFO: Access modifiers changed from: private */
            public void runIfNecessary(RecyclerView recyclerView) {
                if (this.mJumpToPosition >= 0) {
                    int i = this.mJumpToPosition;
                    this.mJumpToPosition = -1;
                    recyclerView.jumpToPositionForSmoothScroller(i);
                    this.changed = false;
                    return;
                }
                if (this.changed) {
                    validate();
                    if (this.mInterpolator != null) {
                        recyclerView.mViewFlinger.smoothScrollBy(this.mDx, this.mDy, this.mDuration, this.mInterpolator);
                    } else if (this.mDuration == Integer.MIN_VALUE) {
                        recyclerView.mViewFlinger.smoothScrollBy(this.mDx, this.mDy);
                    } else {
                        recyclerView.mViewFlinger.smoothScrollBy(this.mDx, this.mDy, this.mDuration);
                    }
                    this.consecutiveUpdates++;
                    if (this.consecutiveUpdates > 10) {
                        Log.e(RecyclerView.TAG, "Smooth Scroll action is being updated too frequently. Make sure you are not changing it unless necessary");
                    }
                    this.changed = false;
                    return;
                }
                this.consecutiveUpdates = 0;
            }

            private void validate() {
                if (this.mInterpolator != null && this.mDuration < 1) {
                    throw new IllegalStateException("If you provide an interpolator, you must set a positive duration");
                }
                if (this.mDuration < 1) {
                    throw new IllegalStateException("Scroll duration must be a positive number");
                }
            }

            public int getDx() {
                return this.mDx;
            }

            public void setDx(int i) {
                this.changed = true;
                this.mDx = i;
            }

            public int getDy() {
                return this.mDy;
            }

            public void setDy(int i) {
                this.changed = true;
                this.mDy = i;
            }

            public int getDuration() {
                return this.mDuration;
            }

            public void setDuration(int i) {
                this.changed = true;
                this.mDuration = i;
            }

            public Interpolator getInterpolator() {
                return this.mInterpolator;
            }

            public void setInterpolator(Interpolator interpolator) {
                this.changed = true;
                this.mInterpolator = interpolator;
            }

            public void update(int i, int i2, int i3, Interpolator interpolator) {
                this.mDx = i;
                this.mDy = i2;
                this.mDuration = i3;
                this.mInterpolator = interpolator;
                this.changed = true;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class AdapterDataObservable extends Observable<AdapterDataObserver> {
        AdapterDataObservable() {
        }

        public boolean hasObservers() {
            return !this.mObservers.isEmpty();
        }

        public void notifyChanged() {
            for (int size = this.mObservers.size() - 1; size >= 0; size--) {
                ((AdapterDataObserver) this.mObservers.get(size)).onChanged();
            }
        }

        public void notifyItemRangeChanged(int i, int i2) {
            notifyItemRangeChanged(i, i2, null);
        }

        public void notifyItemRangeChanged(int i, int i2, Object obj) {
            for (int size = this.mObservers.size() - 1; size >= 0; size--) {
                ((AdapterDataObserver) this.mObservers.get(size)).onItemRangeChanged(i, i2, obj);
            }
        }

        public void notifyItemRangeInserted(int i, int i2) {
            for (int size = this.mObservers.size() - 1; size >= 0; size--) {
                ((AdapterDataObserver) this.mObservers.get(size)).onItemRangeInserted(i, i2);
            }
        }

        public void notifyItemRangeRemoved(int i, int i2) {
            for (int size = this.mObservers.size() - 1; size >= 0; size--) {
                ((AdapterDataObserver) this.mObservers.get(size)).onItemRangeRemoved(i, i2);
            }
        }

        public void notifyItemMoved(int i, int i2) {
            for (int size = this.mObservers.size() - 1; size >= 0; size--) {
                ((AdapterDataObserver) this.mObservers.get(size)).onItemRangeMoved(i, i2, 1);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: android.support.v7.widget.RecyclerView.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        Parcelable mLayoutState;

        SavedState(Parcel parcel) {
            super(parcel);
            this.mLayoutState = parcel.readParcelable(LayoutManager.class.getClassLoader());
        }

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeParcelable(this.mLayoutState, 0);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void copyFrom(SavedState savedState) {
            this.mLayoutState = savedState.mLayoutState;
        }
    }

    /* loaded from: classes.dex */
    public static class State {
        private SparseArray<Object> mData;
        private int mTargetPosition = -1;
        int mItemCount = 0;
        private int mPreviousLayoutItemCount = 0;
        private int mDeletedInvisibleItemCountSincePreviousLayout = 0;
        private boolean mStructureChanged = false;
        private boolean mInPreLayout = false;
        private boolean mRunSimpleAnimations = false;
        private boolean mRunPredictiveAnimations = false;
        private boolean mTrackOldChangeHolders = false;

        static /* synthetic */ int access$1812(State state, int i) {
            int i2 = state.mDeletedInvisibleItemCountSincePreviousLayout + i;
            state.mDeletedInvisibleItemCountSincePreviousLayout = i2;
            return i2;
        }

        State reset() {
            this.mTargetPosition = -1;
            if (this.mData != null) {
                this.mData.clear();
            }
            this.mItemCount = 0;
            this.mStructureChanged = false;
            return this;
        }

        public boolean isPreLayout() {
            return this.mInPreLayout;
        }

        public boolean willRunPredictiveAnimations() {
            return this.mRunPredictiveAnimations;
        }

        public boolean willRunSimpleAnimations() {
            return this.mRunSimpleAnimations;
        }

        public void remove(int i) {
            if (this.mData == null) {
                return;
            }
            this.mData.remove(i);
        }

        public <T> T get(int i) {
            if (this.mData == null) {
                return null;
            }
            return (T) this.mData.get(i);
        }

        public void put(int i, Object obj) {
            if (this.mData == null) {
                this.mData = new SparseArray<>();
            }
            this.mData.put(i, obj);
        }

        public int getTargetScrollPosition() {
            return this.mTargetPosition;
        }

        public boolean hasTargetScrollPosition() {
            return this.mTargetPosition != -1;
        }

        public boolean didStructureChange() {
            return this.mStructureChanged;
        }

        public int getItemCount() {
            return this.mInPreLayout ? this.mPreviousLayoutItemCount - this.mDeletedInvisibleItemCountSincePreviousLayout : this.mItemCount;
        }

        public String toString() {
            return "State{mTargetPosition=" + this.mTargetPosition + ", mData=" + this.mData + ", mItemCount=" + this.mItemCount + ", mPreviousLayoutItemCount=" + this.mPreviousLayoutItemCount + ", mDeletedInvisibleItemCountSincePreviousLayout=" + this.mDeletedInvisibleItemCountSincePreviousLayout + ", mStructureChanged=" + this.mStructureChanged + ", mInPreLayout=" + this.mInPreLayout + ", mRunSimpleAnimations=" + this.mRunSimpleAnimations + ", mRunPredictiveAnimations=" + this.mRunPredictiveAnimations + '}';
        }
    }

    /* loaded from: classes.dex */
    private class ItemAnimatorRestoreListener implements ItemAnimator.ItemAnimatorListener {
        private ItemAnimatorRestoreListener() {
        }

        @Override // android.support.v7.widget.RecyclerView.ItemAnimator.ItemAnimatorListener
        public void onAnimationFinished(ViewHolder viewHolder) {
            viewHolder.setIsRecyclable(true);
            if (viewHolder.mShadowedHolder != null && viewHolder.mShadowingHolder == null) {
                viewHolder.mShadowedHolder = null;
            }
            viewHolder.mShadowingHolder = null;
            if (viewHolder.shouldBeKeptAsChild() || RecyclerView.this.removeAnimatingView(viewHolder.itemView) || !viewHolder.isTmpDetached()) {
                return;
            }
            RecyclerView.this.removeDetachedView(viewHolder.itemView, false);
        }
    }

    /* loaded from: classes.dex */
    public static abstract class ItemAnimator {
        public static final int FLAG_APPEARED_IN_PRE_LAYOUT = 4096;
        public static final int FLAG_CHANGED = 2;
        public static final int FLAG_INVALIDATED = 4;
        public static final int FLAG_MOVED = 2048;
        public static final int FLAG_REMOVED = 8;
        private ItemAnimatorListener mListener = null;
        private ArrayList<ItemAnimatorFinishedListener> mFinishedListeners = new ArrayList<>();
        private long mAddDuration = 120;
        private long mRemoveDuration = 120;
        private long mMoveDuration = 250;
        private long mChangeDuration = 250;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes.dex */
        public @interface AdapterChanges {
        }

        /* loaded from: classes.dex */
        public interface ItemAnimatorFinishedListener {
            void onAnimationsFinished();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public interface ItemAnimatorListener {
            void onAnimationFinished(ViewHolder viewHolder);
        }

        public abstract boolean animateAppearance(@NonNull ViewHolder viewHolder, @Nullable ItemHolderInfo itemHolderInfo, @NonNull ItemHolderInfo itemHolderInfo2);

        public abstract boolean animateChange(@NonNull ViewHolder viewHolder, @NonNull ViewHolder viewHolder2, @NonNull ItemHolderInfo itemHolderInfo, @NonNull ItemHolderInfo itemHolderInfo2);

        public abstract boolean animateDisappearance(@NonNull ViewHolder viewHolder, @NonNull ItemHolderInfo itemHolderInfo, @Nullable ItemHolderInfo itemHolderInfo2);

        public abstract boolean animatePersistence(@NonNull ViewHolder viewHolder, @NonNull ItemHolderInfo itemHolderInfo, @NonNull ItemHolderInfo itemHolderInfo2);

        public boolean canReuseUpdatedViewHolder(ViewHolder viewHolder) {
            return true;
        }

        public abstract void endAnimation(ViewHolder viewHolder);

        public abstract void endAnimations();

        public abstract boolean isRunning();

        public void onAnimationFinished(ViewHolder viewHolder) {
        }

        public void onAnimationStarted(ViewHolder viewHolder) {
        }

        public abstract void runPendingAnimations();

        public long getMoveDuration() {
            return this.mMoveDuration;
        }

        public void setMoveDuration(long j) {
            this.mMoveDuration = j;
        }

        public long getAddDuration() {
            return this.mAddDuration;
        }

        public void setAddDuration(long j) {
            this.mAddDuration = j;
        }

        public long getRemoveDuration() {
            return this.mRemoveDuration;
        }

        public void setRemoveDuration(long j) {
            this.mRemoveDuration = j;
        }

        public long getChangeDuration() {
            return this.mChangeDuration;
        }

        public void setChangeDuration(long j) {
            this.mChangeDuration = j;
        }

        void setListener(ItemAnimatorListener itemAnimatorListener) {
            this.mListener = itemAnimatorListener;
        }

        @NonNull
        public ItemHolderInfo recordPreLayoutInformation(@NonNull State state, @NonNull ViewHolder viewHolder, int i, @NonNull List<Object> list) {
            return obtainHolderInfo().setFrom(viewHolder);
        }

        @NonNull
        public ItemHolderInfo recordPostLayoutInformation(@NonNull State state, @NonNull ViewHolder viewHolder) {
            return obtainHolderInfo().setFrom(viewHolder);
        }

        static int buildAdapterChangeFlagsForAnimations(ViewHolder viewHolder) {
            int i = viewHolder.mFlags & 14;
            if (viewHolder.isInvalid()) {
                return 4;
            }
            if ((i & 4) != 0) {
                return i;
            }
            int oldPosition = viewHolder.getOldPosition();
            int adapterPosition = viewHolder.getAdapterPosition();
            return (oldPosition == -1 || adapterPosition == -1 || oldPosition == adapterPosition) ? i : i | 2048;
        }

        public final void dispatchAnimationFinished(ViewHolder viewHolder) {
            onAnimationFinished(viewHolder);
            if (this.mListener != null) {
                this.mListener.onAnimationFinished(viewHolder);
            }
        }

        public final void dispatchAnimationStarted(ViewHolder viewHolder) {
            onAnimationStarted(viewHolder);
        }

        public final boolean isRunning(ItemAnimatorFinishedListener itemAnimatorFinishedListener) {
            boolean isRunning = isRunning();
            if (itemAnimatorFinishedListener != null) {
                if (!isRunning) {
                    itemAnimatorFinishedListener.onAnimationsFinished();
                } else {
                    this.mFinishedListeners.add(itemAnimatorFinishedListener);
                }
            }
            return isRunning;
        }

        public final void dispatchAnimationsFinished() {
            int size = this.mFinishedListeners.size();
            for (int i = 0; i < size; i++) {
                this.mFinishedListeners.get(i).onAnimationsFinished();
            }
            this.mFinishedListeners.clear();
        }

        public ItemHolderInfo obtainHolderInfo() {
            return new ItemHolderInfo();
        }

        /* loaded from: classes.dex */
        public static class ItemHolderInfo {
            public int bottom;
            public int changeFlags;
            public int left;
            public int right;
            public int top;

            public ItemHolderInfo setFrom(ViewHolder viewHolder) {
                return setFrom(viewHolder, 0);
            }

            public ItemHolderInfo setFrom(ViewHolder viewHolder, int i) {
                View view = viewHolder.itemView;
                this.left = view.getLeft();
                this.top = view.getTop();
                this.right = view.getRight();
                this.bottom = view.getBottom();
                return this;
            }
        }
    }

    @Override // android.view.ViewGroup
    protected int getChildDrawingOrder(int i, int i2) {
        if (this.mChildDrawingOrderCallback == null) {
            return super.getChildDrawingOrder(i, i2);
        }
        return this.mChildDrawingOrderCallback.onGetChildDrawingOrder(i, i2);
    }
}
