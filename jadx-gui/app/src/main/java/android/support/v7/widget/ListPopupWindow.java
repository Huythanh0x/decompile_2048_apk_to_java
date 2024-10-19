package android.support.v7.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.text.TextUtilsCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.support.v4.widget.PopupWindowCompat;
import android.support.v7.appcompat.R;
import android.support.v7.widget.ActivityChooserView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public class ListPopupWindow {
    private static final boolean DEBUG = false;
    private static final int EXPAND_LIST_TIMEOUT = 250;
    public static final int INPUT_METHOD_FROM_FOCUSABLE = 0;
    public static final int INPUT_METHOD_NEEDED = 1;
    public static final int INPUT_METHOD_NOT_NEEDED = 2;
    public static final int MATCH_PARENT = -1;
    public static final int POSITION_PROMPT_ABOVE = 0;
    public static final int POSITION_PROMPT_BELOW = 1;
    private static final String TAG = "ListPopupWindow";
    public static final int WRAP_CONTENT = -2;
    private static Method sClipToWindowEnabledMethod;
    private static Method sGetMaxAvailableHeightMethod;
    private ListAdapter mAdapter;
    private Context mContext;
    private boolean mDropDownAlwaysVisible;
    private View mDropDownAnchorView;
    private int mDropDownGravity;
    private int mDropDownHeight;
    private int mDropDownHorizontalOffset;
    private DropDownListView mDropDownList;
    private Drawable mDropDownListHighlight;
    private int mDropDownVerticalOffset;
    private boolean mDropDownVerticalOffsetSet;
    private int mDropDownWidth;
    private int mDropDownWindowLayoutType;
    private boolean mForceIgnoreOutsideTouch;
    private final Handler mHandler;
    private final ListSelectorHider mHideSelector;
    private AdapterView.OnItemClickListener mItemClickListener;
    private AdapterView.OnItemSelectedListener mItemSelectedListener;
    private int mLayoutDirection;
    int mListItemExpandMaximum;
    private boolean mModal;
    private DataSetObserver mObserver;
    private PopupWindow mPopup;
    private int mPromptPosition;
    private View mPromptView;
    private final ResizePopupRunnable mResizePopupRunnable;
    private final PopupScrollListener mScrollListener;
    private Runnable mShowDropDownRunnable;
    private Rect mTempRect;
    private final PopupTouchInterceptor mTouchInterceptor;

    private static boolean isConfirmKey(int i) {
        return i == 66 || i == 23;
    }

    static {
        try {
            sClipToWindowEnabledMethod = PopupWindow.class.getDeclaredMethod("setClipToScreenEnabled", Boolean.TYPE);
        } catch (NoSuchMethodException unused) {
            Log.i(TAG, "Could not find method setClipToScreenEnabled() on PopupWindow. Oh well.");
        }
        try {
            sGetMaxAvailableHeightMethod = PopupWindow.class.getDeclaredMethod("getMaxAvailableHeight", View.class, Integer.TYPE, Boolean.TYPE);
        } catch (NoSuchMethodException unused2) {
            Log.i(TAG, "Could not find method getMaxAvailableHeight(View, int, boolean) on PopupWindow. Oh well.");
        }
    }

    public ListPopupWindow(Context context) {
        this(context, null, R.attr.listPopupWindowStyle);
    }

    public ListPopupWindow(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.listPopupWindowStyle);
    }

    public ListPopupWindow(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public ListPopupWindow(Context context, AttributeSet attributeSet, int i, int i2) {
        this.mDropDownHeight = -2;
        this.mDropDownWidth = -2;
        this.mDropDownWindowLayoutType = 1002;
        this.mDropDownGravity = 0;
        this.mDropDownAlwaysVisible = false;
        this.mForceIgnoreOutsideTouch = false;
        this.mListItemExpandMaximum = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        this.mPromptPosition = 0;
        this.mResizePopupRunnable = new ResizePopupRunnable();
        this.mTouchInterceptor = new PopupTouchInterceptor();
        this.mScrollListener = new PopupScrollListener();
        this.mHideSelector = new ListSelectorHider();
        this.mTempRect = new Rect();
        this.mContext = context;
        this.mHandler = new Handler(context.getMainLooper());
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ListPopupWindow, i, i2);
        this.mDropDownHorizontalOffset = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.ListPopupWindow_android_dropDownHorizontalOffset, 0);
        this.mDropDownVerticalOffset = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.ListPopupWindow_android_dropDownVerticalOffset, 0);
        if (this.mDropDownVerticalOffset != 0) {
            this.mDropDownVerticalOffsetSet = true;
        }
        obtainStyledAttributes.recycle();
        this.mPopup = new AppCompatPopupWindow(context, attributeSet, i);
        this.mPopup.setInputMethodMode(1);
        this.mLayoutDirection = TextUtilsCompat.getLayoutDirectionFromLocale(this.mContext.getResources().getConfiguration().locale);
    }

    public void setAdapter(ListAdapter listAdapter) {
        if (this.mObserver == null) {
            this.mObserver = new PopupDataSetObserver();
        } else if (this.mAdapter != null) {
            this.mAdapter.unregisterDataSetObserver(this.mObserver);
        }
        this.mAdapter = listAdapter;
        if (this.mAdapter != null) {
            listAdapter.registerDataSetObserver(this.mObserver);
        }
        if (this.mDropDownList != null) {
            this.mDropDownList.setAdapter(this.mAdapter);
        }
    }

    public void setPromptPosition(int i) {
        this.mPromptPosition = i;
    }

    public int getPromptPosition() {
        return this.mPromptPosition;
    }

    public void setModal(boolean z) {
        this.mModal = z;
        this.mPopup.setFocusable(z);
    }

    public boolean isModal() {
        return this.mModal;
    }

    public void setForceIgnoreOutsideTouch(boolean z) {
        this.mForceIgnoreOutsideTouch = z;
    }

    public void setDropDownAlwaysVisible(boolean z) {
        this.mDropDownAlwaysVisible = z;
    }

    public boolean isDropDownAlwaysVisible() {
        return this.mDropDownAlwaysVisible;
    }

    public void setSoftInputMode(int i) {
        this.mPopup.setSoftInputMode(i);
    }

    public int getSoftInputMode() {
        return this.mPopup.getSoftInputMode();
    }

    public void setListSelector(Drawable drawable) {
        this.mDropDownListHighlight = drawable;
    }

    public Drawable getBackground() {
        return this.mPopup.getBackground();
    }

    public void setBackgroundDrawable(Drawable drawable) {
        this.mPopup.setBackgroundDrawable(drawable);
    }

    public void setAnimationStyle(int i) {
        this.mPopup.setAnimationStyle(i);
    }

    public int getAnimationStyle() {
        return this.mPopup.getAnimationStyle();
    }

    public View getAnchorView() {
        return this.mDropDownAnchorView;
    }

    public void setAnchorView(View view) {
        this.mDropDownAnchorView = view;
    }

    public int getHorizontalOffset() {
        return this.mDropDownHorizontalOffset;
    }

    public void setHorizontalOffset(int i) {
        this.mDropDownHorizontalOffset = i;
    }

    public int getVerticalOffset() {
        if (this.mDropDownVerticalOffsetSet) {
            return this.mDropDownVerticalOffset;
        }
        return 0;
    }

    public void setVerticalOffset(int i) {
        this.mDropDownVerticalOffset = i;
        this.mDropDownVerticalOffsetSet = true;
    }

    public void setDropDownGravity(int i) {
        this.mDropDownGravity = i;
    }

    public int getWidth() {
        return this.mDropDownWidth;
    }

    public void setWidth(int i) {
        this.mDropDownWidth = i;
    }

    public void setContentWidth(int i) {
        Drawable background = this.mPopup.getBackground();
        if (background != null) {
            background.getPadding(this.mTempRect);
            this.mDropDownWidth = this.mTempRect.left + this.mTempRect.right + i;
        } else {
            setWidth(i);
        }
    }

    public int getHeight() {
        return this.mDropDownHeight;
    }

    public void setHeight(int i) {
        this.mDropDownHeight = i;
    }

    public void setWindowLayoutType(int i) {
        this.mDropDownWindowLayoutType = i;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.mItemClickListener = onItemClickListener;
    }

    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener onItemSelectedListener) {
        this.mItemSelectedListener = onItemSelectedListener;
    }

    public void setPromptView(View view) {
        boolean isShowing = isShowing();
        if (isShowing) {
            removePromptView();
        }
        this.mPromptView = view;
        if (isShowing) {
            show();
        }
    }

    public void postShow() {
        this.mHandler.post(this.mShowDropDownRunnable);
    }

    public void show() {
        int i;
        int i2;
        int buildDropDown = buildDropDown();
        boolean isInputMethodNotNeeded = isInputMethodNotNeeded();
        PopupWindowCompat.setWindowLayoutType(this.mPopup, this.mDropDownWindowLayoutType);
        if (this.mPopup.isShowing()) {
            if (this.mDropDownWidth == -1) {
                i2 = -1;
            } else if (this.mDropDownWidth == -2) {
                i2 = getAnchorView().getWidth();
            } else {
                i2 = this.mDropDownWidth;
            }
            if (this.mDropDownHeight == -1) {
                if (!isInputMethodNotNeeded) {
                    buildDropDown = -1;
                }
                if (isInputMethodNotNeeded) {
                    this.mPopup.setWidth(this.mDropDownWidth == -1 ? -1 : 0);
                    this.mPopup.setHeight(0);
                } else {
                    this.mPopup.setWidth(this.mDropDownWidth == -1 ? -1 : 0);
                    this.mPopup.setHeight(-1);
                }
            } else if (this.mDropDownHeight != -2) {
                buildDropDown = this.mDropDownHeight;
            }
            this.mPopup.setOutsideTouchable((this.mForceIgnoreOutsideTouch || this.mDropDownAlwaysVisible) ? false : true);
            this.mPopup.update(getAnchorView(), this.mDropDownHorizontalOffset, this.mDropDownVerticalOffset, i2 < 0 ? -1 : i2, buildDropDown < 0 ? -1 : buildDropDown);
            return;
        }
        if (this.mDropDownWidth == -1) {
            i = -1;
        } else if (this.mDropDownWidth == -2) {
            i = getAnchorView().getWidth();
        } else {
            i = this.mDropDownWidth;
        }
        if (this.mDropDownHeight == -1) {
            buildDropDown = -1;
        } else if (this.mDropDownHeight != -2) {
            buildDropDown = this.mDropDownHeight;
        }
        this.mPopup.setWidth(i);
        this.mPopup.setHeight(buildDropDown);
        setPopupClipToScreenEnabled(true);
        this.mPopup.setOutsideTouchable((this.mForceIgnoreOutsideTouch || this.mDropDownAlwaysVisible) ? false : true);
        this.mPopup.setTouchInterceptor(this.mTouchInterceptor);
        PopupWindowCompat.showAsDropDown(this.mPopup, getAnchorView(), this.mDropDownHorizontalOffset, this.mDropDownVerticalOffset, this.mDropDownGravity);
        this.mDropDownList.setSelection(-1);
        if (!this.mModal || this.mDropDownList.isInTouchMode()) {
            clearListSelection();
        }
        if (this.mModal) {
            return;
        }
        this.mHandler.post(this.mHideSelector);
    }

    public void dismiss() {
        this.mPopup.dismiss();
        removePromptView();
        this.mPopup.setContentView(null);
        this.mDropDownList = null;
        this.mHandler.removeCallbacks(this.mResizePopupRunnable);
    }

    public void setOnDismissListener(PopupWindow.OnDismissListener onDismissListener) {
        this.mPopup.setOnDismissListener(onDismissListener);
    }

    private void removePromptView() {
        if (this.mPromptView != null) {
            ViewParent parent = this.mPromptView.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(this.mPromptView);
            }
        }
    }

    public void setInputMethodMode(int i) {
        this.mPopup.setInputMethodMode(i);
    }

    public int getInputMethodMode() {
        return this.mPopup.getInputMethodMode();
    }

    public void setSelection(int i) {
        DropDownListView dropDownListView = this.mDropDownList;
        if (!isShowing() || dropDownListView == null) {
            return;
        }
        dropDownListView.mListSelectionHidden = false;
        dropDownListView.setSelection(i);
        if (Build.VERSION.SDK_INT < 11 || dropDownListView.getChoiceMode() == 0) {
            return;
        }
        dropDownListView.setItemChecked(i, true);
    }

    public void clearListSelection() {
        DropDownListView dropDownListView = this.mDropDownList;
        if (dropDownListView != null) {
            dropDownListView.mListSelectionHidden = true;
            dropDownListView.requestLayout();
        }
    }

    public boolean isShowing() {
        return this.mPopup.isShowing();
    }

    public boolean isInputMethodNotNeeded() {
        return this.mPopup.getInputMethodMode() == 2;
    }

    public boolean performItemClick(int i) {
        if (!isShowing()) {
            return false;
        }
        if (this.mItemClickListener == null) {
            return true;
        }
        DropDownListView dropDownListView = this.mDropDownList;
        this.mItemClickListener.onItemClick(dropDownListView, dropDownListView.getChildAt(i - dropDownListView.getFirstVisiblePosition()), i, dropDownListView.getAdapter().getItemId(i));
        return true;
    }

    public Object getSelectedItem() {
        if (isShowing()) {
            return this.mDropDownList.getSelectedItem();
        }
        return null;
    }

    public int getSelectedItemPosition() {
        if (isShowing()) {
            return this.mDropDownList.getSelectedItemPosition();
        }
        return -1;
    }

    public long getSelectedItemId() {
        if (isShowing()) {
            return this.mDropDownList.getSelectedItemId();
        }
        return Long.MIN_VALUE;
    }

    public View getSelectedView() {
        if (isShowing()) {
            return this.mDropDownList.getSelectedView();
        }
        return null;
    }

    public ListView getListView() {
        return this.mDropDownList;
    }

    void setListItemExpandMax(int i) {
        this.mListItemExpandMaximum = i;
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        int i2;
        if (isShowing() && i != 62 && (this.mDropDownList.getSelectedItemPosition() >= 0 || !isConfirmKey(i))) {
            int selectedItemPosition = this.mDropDownList.getSelectedItemPosition();
            boolean z = !this.mPopup.isAboveAnchor();
            ListAdapter listAdapter = this.mAdapter;
            int i3 = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
            if (listAdapter != null) {
                boolean areAllItemsEnabled = listAdapter.areAllItemsEnabled();
                int lookForSelectablePosition = areAllItemsEnabled ? 0 : this.mDropDownList.lookForSelectablePosition(0, true);
                i2 = areAllItemsEnabled ? listAdapter.getCount() - 1 : this.mDropDownList.lookForSelectablePosition(listAdapter.getCount() - 1, false);
                i3 = lookForSelectablePosition;
            } else {
                i2 = Integer.MIN_VALUE;
            }
            if ((z && i == 19 && selectedItemPosition <= i3) || (!z && i == 20 && selectedItemPosition >= i2)) {
                clearListSelection();
                this.mPopup.setInputMethodMode(1);
                show();
                return true;
            }
            this.mDropDownList.mListSelectionHidden = false;
            if (this.mDropDownList.onKeyDown(i, keyEvent)) {
                this.mPopup.setInputMethodMode(2);
                this.mDropDownList.requestFocusFromTouch();
                show();
                if (i != 23 && i != 66) {
                    switch (i) {
                    }
                }
                return true;
            }
            if (z && i == 20) {
                if (selectedItemPosition == i2) {
                    return true;
                }
            } else if (!z && i == 19 && selectedItemPosition == i3) {
                return true;
            }
        }
        return false;
    }

    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (!isShowing() || this.mDropDownList.getSelectedItemPosition() < 0) {
            return false;
        }
        boolean onKeyUp = this.mDropDownList.onKeyUp(i, keyEvent);
        if (onKeyUp && isConfirmKey(i)) {
            dismiss();
        }
        return onKeyUp;
    }

    public boolean onKeyPreIme(int i, KeyEvent keyEvent) {
        if (i != 4 || !isShowing()) {
            return false;
        }
        View view = this.mDropDownAnchorView;
        if (keyEvent.getAction() == 0 && keyEvent.getRepeatCount() == 0) {
            KeyEvent.DispatcherState keyDispatcherState = view.getKeyDispatcherState();
            if (keyDispatcherState != null) {
                keyDispatcherState.startTracking(keyEvent, this);
            }
            return true;
        }
        if (keyEvent.getAction() != 1) {
            return false;
        }
        KeyEvent.DispatcherState keyDispatcherState2 = view.getKeyDispatcherState();
        if (keyDispatcherState2 != null) {
            keyDispatcherState2.handleUpEvent(keyEvent);
        }
        if (!keyEvent.isTracking() || keyEvent.isCanceled()) {
            return false;
        }
        dismiss();
        return true;
    }

    public View.OnTouchListener createDragToOpenListener(View view) {
        return new ForwardingListener(view) { // from class: android.support.v7.widget.ListPopupWindow.1
            @Override // android.support.v7.widget.ListPopupWindow.ForwardingListener
            public ListPopupWindow getPopup() {
                return ListPopupWindow.this;
            }
        };
    }

    private int buildDropDown() {
        int i;
        int i2;
        int makeMeasureSpec;
        int i3;
        int i4;
        if (this.mDropDownList == null) {
            Context context = this.mContext;
            this.mShowDropDownRunnable = new Runnable() { // from class: android.support.v7.widget.ListPopupWindow.2
                @Override // java.lang.Runnable
                public void run() {
                    View anchorView = ListPopupWindow.this.getAnchorView();
                    if (anchorView == null || anchorView.getWindowToken() == null) {
                        return;
                    }
                    ListPopupWindow.this.show();
                }
            };
            this.mDropDownList = new DropDownListView(context, !this.mModal);
            if (this.mDropDownListHighlight != null) {
                this.mDropDownList.setSelector(this.mDropDownListHighlight);
            }
            this.mDropDownList.setAdapter(this.mAdapter);
            this.mDropDownList.setOnItemClickListener(this.mItemClickListener);
            this.mDropDownList.setFocusable(true);
            this.mDropDownList.setFocusableInTouchMode(true);
            this.mDropDownList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: android.support.v7.widget.ListPopupWindow.3
                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onNothingSelected(AdapterView<?> adapterView) {
                }

                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onItemSelected(AdapterView<?> adapterView, View view, int i5, long j) {
                    DropDownListView dropDownListView;
                    if (i5 == -1 || (dropDownListView = ListPopupWindow.this.mDropDownList) == null) {
                        return;
                    }
                    dropDownListView.mListSelectionHidden = false;
                }
            });
            this.mDropDownList.setOnScrollListener(this.mScrollListener);
            if (this.mItemSelectedListener != null) {
                this.mDropDownList.setOnItemSelectedListener(this.mItemSelectedListener);
            }
            View view = this.mDropDownList;
            View view2 = this.mPromptView;
            if (view2 != null) {
                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(1);
                ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, 0, 1.0f);
                switch (this.mPromptPosition) {
                    case 0:
                        linearLayout.addView(view2);
                        linearLayout.addView(view, layoutParams);
                        break;
                    case 1:
                        linearLayout.addView(view, layoutParams);
                        linearLayout.addView(view2);
                        break;
                    default:
                        Log.e(TAG, "Invalid hint position " + this.mPromptPosition);
                        break;
                }
                if (this.mDropDownWidth >= 0) {
                    i3 = this.mDropDownWidth;
                    i4 = Integer.MIN_VALUE;
                } else {
                    i3 = 0;
                    i4 = 0;
                }
                view2.measure(View.MeasureSpec.makeMeasureSpec(i3, i4), 0);
                LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) view2.getLayoutParams();
                i = view2.getMeasuredHeight() + layoutParams2.topMargin + layoutParams2.bottomMargin;
                view = linearLayout;
            } else {
                i = 0;
            }
            this.mPopup.setContentView(view);
        } else {
            View view3 = this.mPromptView;
            if (view3 != null) {
                LinearLayout.LayoutParams layoutParams3 = (LinearLayout.LayoutParams) view3.getLayoutParams();
                i = view3.getMeasuredHeight() + layoutParams3.topMargin + layoutParams3.bottomMargin;
            } else {
                i = 0;
            }
        }
        Drawable background = this.mPopup.getBackground();
        if (background != null) {
            background.getPadding(this.mTempRect);
            i2 = this.mTempRect.top + this.mTempRect.bottom;
            if (!this.mDropDownVerticalOffsetSet) {
                this.mDropDownVerticalOffset = -this.mTempRect.top;
            }
        } else {
            this.mTempRect.setEmpty();
            i2 = 0;
        }
        int maxAvailableHeight = getMaxAvailableHeight(getAnchorView(), this.mDropDownVerticalOffset, this.mPopup.getInputMethodMode() == 2);
        if (this.mDropDownAlwaysVisible || this.mDropDownHeight == -1) {
            return maxAvailableHeight + i2;
        }
        switch (this.mDropDownWidth) {
            case -2:
                makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(this.mContext.getResources().getDisplayMetrics().widthPixels - (this.mTempRect.left + this.mTempRect.right), Integer.MIN_VALUE);
                break;
            case -1:
                makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(this.mContext.getResources().getDisplayMetrics().widthPixels - (this.mTempRect.left + this.mTempRect.right), 1073741824);
                break;
            default:
                makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(this.mDropDownWidth, 1073741824);
                break;
        }
        int measureHeightOfChildrenCompat = this.mDropDownList.measureHeightOfChildrenCompat(makeMeasureSpec, 0, -1, maxAvailableHeight - i, -1);
        if (measureHeightOfChildrenCompat > 0) {
            i += i2;
        }
        return measureHeightOfChildrenCompat + i;
    }

    /* loaded from: classes.dex */
    public static abstract class ForwardingListener implements View.OnTouchListener {
        private int mActivePointerId;
        private Runnable mDisallowIntercept;
        private boolean mForwarding;
        private final float mScaledTouchSlop;
        private final View mSrc;
        private Runnable mTriggerLongPress;
        private boolean mWasLongPress;
        private final int[] mTmpLocation = new int[2];
        private final int mTapTimeout = ViewConfiguration.getTapTimeout();
        private final int mLongPressTimeout = (this.mTapTimeout + ViewConfiguration.getLongPressTimeout()) / 2;

        public abstract ListPopupWindow getPopup();

        public ForwardingListener(View view) {
            this.mSrc = view;
            this.mScaledTouchSlop = ViewConfiguration.get(view.getContext()).getScaledTouchSlop();
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            boolean z;
            boolean z2 = this.mForwarding;
            if (z2) {
                z = this.mWasLongPress ? onTouchForwarded(motionEvent) : onTouchForwarded(motionEvent) || !onForwardingStopped();
            } else {
                z = onTouchObserved(motionEvent) && onForwardingStarted();
                if (z) {
                    long uptimeMillis = SystemClock.uptimeMillis();
                    MotionEvent obtain = MotionEvent.obtain(uptimeMillis, uptimeMillis, 3, 0.0f, 0.0f, 0);
                    this.mSrc.onTouchEvent(obtain);
                    obtain.recycle();
                }
            }
            this.mForwarding = z;
            return z || z2;
        }

        protected boolean onForwardingStarted() {
            ListPopupWindow popup = getPopup();
            if (popup == null || popup.isShowing()) {
                return true;
            }
            popup.show();
            return true;
        }

        protected boolean onForwardingStopped() {
            ListPopupWindow popup = getPopup();
            if (popup == null || !popup.isShowing()) {
                return true;
            }
            popup.dismiss();
            return true;
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        private boolean onTouchObserved(MotionEvent motionEvent) {
            View view = this.mSrc;
            if (!view.isEnabled()) {
                return false;
            }
            switch (MotionEventCompat.getActionMasked(motionEvent)) {
                case 0:
                    this.mActivePointerId = motionEvent.getPointerId(0);
                    this.mWasLongPress = false;
                    if (this.mDisallowIntercept == null) {
                        this.mDisallowIntercept = new DisallowIntercept();
                    }
                    view.postDelayed(this.mDisallowIntercept, this.mTapTimeout);
                    if (this.mTriggerLongPress == null) {
                        this.mTriggerLongPress = new TriggerLongPress();
                    }
                    view.postDelayed(this.mTriggerLongPress, this.mLongPressTimeout);
                    return false;
                case 1:
                case 3:
                    clearCallbacks();
                    return false;
                case 2:
                    int findPointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
                    if (findPointerIndex >= 0 && !pointInView(view, motionEvent.getX(findPointerIndex), motionEvent.getY(findPointerIndex), this.mScaledTouchSlop)) {
                        clearCallbacks();
                        view.getParent().requestDisallowInterceptTouchEvent(true);
                        return true;
                    }
                    return false;
                default:
                    return false;
            }
        }

        private void clearCallbacks() {
            if (this.mTriggerLongPress != null) {
                this.mSrc.removeCallbacks(this.mTriggerLongPress);
            }
            if (this.mDisallowIntercept != null) {
                this.mSrc.removeCallbacks(this.mDisallowIntercept);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void onLongPress() {
            clearCallbacks();
            View view = this.mSrc;
            if (view.isEnabled() && !view.isLongClickable() && onForwardingStarted()) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                long uptimeMillis = SystemClock.uptimeMillis();
                MotionEvent obtain = MotionEvent.obtain(uptimeMillis, uptimeMillis, 3, 0.0f, 0.0f, 0);
                view.onTouchEvent(obtain);
                obtain.recycle();
                this.mForwarding = true;
                this.mWasLongPress = true;
            }
        }

        private boolean onTouchForwarded(MotionEvent motionEvent) {
            DropDownListView dropDownListView;
            View view = this.mSrc;
            ListPopupWindow popup = getPopup();
            if (popup == null || !popup.isShowing() || (dropDownListView = popup.mDropDownList) == null || !dropDownListView.isShown()) {
                return false;
            }
            MotionEvent obtainNoHistory = MotionEvent.obtainNoHistory(motionEvent);
            toGlobalMotionEvent(view, obtainNoHistory);
            toLocalMotionEvent(dropDownListView, obtainNoHistory);
            boolean onForwardedEvent = dropDownListView.onForwardedEvent(obtainNoHistory, this.mActivePointerId);
            obtainNoHistory.recycle();
            int actionMasked = MotionEventCompat.getActionMasked(motionEvent);
            return onForwardedEvent && (actionMasked != 1 && actionMasked != 3);
        }

        private static boolean pointInView(View view, float f, float f2, float f3) {
            float f4 = -f3;
            return f >= f4 && f2 >= f4 && f < ((float) (view.getRight() - view.getLeft())) + f3 && f2 < ((float) (view.getBottom() - view.getTop())) + f3;
        }

        private boolean toLocalMotionEvent(View view, MotionEvent motionEvent) {
            view.getLocationOnScreen(this.mTmpLocation);
            motionEvent.offsetLocation(-r0[0], -r0[1]);
            return true;
        }

        private boolean toGlobalMotionEvent(View view, MotionEvent motionEvent) {
            view.getLocationOnScreen(this.mTmpLocation);
            motionEvent.offsetLocation(r0[0], r0[1]);
            return true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public class DisallowIntercept implements Runnable {
            private DisallowIntercept() {
            }

            @Override // java.lang.Runnable
            public void run() {
                ForwardingListener.this.mSrc.getParent().requestDisallowInterceptTouchEvent(true);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public class TriggerLongPress implements Runnable {
            private TriggerLongPress() {
            }

            @Override // java.lang.Runnable
            public void run() {
                ForwardingListener.this.onLongPress();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class DropDownListView extends ListViewCompat {
        private ViewPropertyAnimatorCompat mClickAnimation;
        private boolean mDrawsInPressedState;
        private boolean mHijackFocus;
        private boolean mListSelectionHidden;
        private ListViewAutoScrollHelper mScrollHelper;

        public DropDownListView(Context context, boolean z) {
            super(context, null, R.attr.dropDownListViewStyle);
            this.mHijackFocus = z;
            setCacheColorHint(0);
        }

        /* JADX WARN: Removed duplicated region for block: B:21:0x0019  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public boolean onForwardedEvent(android.view.MotionEvent r8, int r9) {
            /*
                r7 = this;
                int r0 = android.support.v4.view.MotionEventCompat.getActionMasked(r8)
                r1 = 0
                r2 = 1
                switch(r0) {
                    case 1: goto L11;
                    case 2: goto Lf;
                    case 3: goto Lc;
                    default: goto L9;
                }
            L9:
                r9 = 0
                r3 = 1
                goto L41
            Lc:
                r9 = 0
                r3 = 0
                goto L41
            Lf:
                r3 = 1
                goto L12
            L11:
                r3 = 0
            L12:
                int r9 = r8.findPointerIndex(r9)
                if (r9 >= 0) goto L19
                goto Lc
            L19:
                float r4 = r8.getX(r9)
                int r4 = (int) r4
                float r9 = r8.getY(r9)
                int r9 = (int) r9
                int r5 = r7.pointToPosition(r4, r9)
                r6 = -1
                if (r5 != r6) goto L2c
                r9 = 1
                goto L41
            L2c:
                int r3 = r7.getFirstVisiblePosition()
                int r3 = r5 - r3
                android.view.View r3 = r7.getChildAt(r3)
                float r4 = (float) r4
                float r9 = (float) r9
                r7.setPressedItem(r3, r5, r4, r9)
                if (r0 != r2) goto L9
                r7.clickPressedItem(r3, r5)
                goto L9
            L41:
                if (r3 == 0) goto L45
                if (r9 == 0) goto L48
            L45:
                r7.clearPressedItem()
            L48:
                if (r3 == 0) goto L60
                android.support.v4.widget.ListViewAutoScrollHelper r9 = r7.mScrollHelper
                if (r9 != 0) goto L55
                android.support.v4.widget.ListViewAutoScrollHelper r9 = new android.support.v4.widget.ListViewAutoScrollHelper
                r9.<init>(r7)
                r7.mScrollHelper = r9
            L55:
                android.support.v4.widget.ListViewAutoScrollHelper r9 = r7.mScrollHelper
                r9.setEnabled(r2)
                android.support.v4.widget.ListViewAutoScrollHelper r9 = r7.mScrollHelper
                r9.onTouch(r7, r8)
                goto L69
            L60:
                android.support.v4.widget.ListViewAutoScrollHelper r8 = r7.mScrollHelper
                if (r8 == 0) goto L69
                android.support.v4.widget.ListViewAutoScrollHelper r8 = r7.mScrollHelper
                r8.setEnabled(r1)
            L69:
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.ListPopupWindow.DropDownListView.onForwardedEvent(android.view.MotionEvent, int):boolean");
        }

        private void clickPressedItem(View view, int i) {
            performItemClick(view, i, getItemIdAtPosition(i));
        }

        private void clearPressedItem() {
            this.mDrawsInPressedState = false;
            setPressed(false);
            drawableStateChanged();
            View childAt = getChildAt(this.mMotionPosition - getFirstVisiblePosition());
            if (childAt != null) {
                childAt.setPressed(false);
            }
            if (this.mClickAnimation != null) {
                this.mClickAnimation.cancel();
                this.mClickAnimation = null;
            }
        }

        private void setPressedItem(View view, int i, float f, float f2) {
            View childAt;
            this.mDrawsInPressedState = true;
            if (Build.VERSION.SDK_INT >= 21) {
                drawableHotspotChanged(f, f2);
            }
            if (!isPressed()) {
                setPressed(true);
            }
            layoutChildren();
            if (this.mMotionPosition != -1 && (childAt = getChildAt(this.mMotionPosition - getFirstVisiblePosition())) != null && childAt != view && childAt.isPressed()) {
                childAt.setPressed(false);
            }
            this.mMotionPosition = i;
            float left = f - view.getLeft();
            float top = f2 - view.getTop();
            if (Build.VERSION.SDK_INT >= 21) {
                view.drawableHotspotChanged(left, top);
            }
            if (!view.isPressed()) {
                view.setPressed(true);
            }
            setSelection(i);
            positionSelectorLikeTouchCompat(i, view, f, f2);
            setSelectorEnabled(false);
            refreshDrawableState();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.support.v7.widget.ListViewCompat
        public boolean touchModeDrawsInPressedStateCompat() {
            return this.mDrawsInPressedState || super.touchModeDrawsInPressedStateCompat();
        }

        @Override // android.view.View
        public boolean isInTouchMode() {
            return (this.mHijackFocus && this.mListSelectionHidden) || super.isInTouchMode();
        }

        @Override // android.view.View
        public boolean hasWindowFocus() {
            return this.mHijackFocus || super.hasWindowFocus();
        }

        @Override // android.view.View
        public boolean isFocused() {
            return this.mHijackFocus || super.isFocused();
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean hasFocus() {
            return this.mHijackFocus || super.hasFocus();
        }
    }

    /* loaded from: classes.dex */
    private class PopupDataSetObserver extends DataSetObserver {
        private PopupDataSetObserver() {
        }

        @Override // android.database.DataSetObserver
        public void onChanged() {
            if (ListPopupWindow.this.isShowing()) {
                ListPopupWindow.this.show();
            }
        }

        @Override // android.database.DataSetObserver
        public void onInvalidated() {
            ListPopupWindow.this.dismiss();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ListSelectorHider implements Runnable {
        private ListSelectorHider() {
        }

        @Override // java.lang.Runnable
        public void run() {
            ListPopupWindow.this.clearListSelection();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ResizePopupRunnable implements Runnable {
        private ResizePopupRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (ListPopupWindow.this.mDropDownList == null || !ViewCompat.isAttachedToWindow(ListPopupWindow.this.mDropDownList) || ListPopupWindow.this.mDropDownList.getCount() <= ListPopupWindow.this.mDropDownList.getChildCount() || ListPopupWindow.this.mDropDownList.getChildCount() > ListPopupWindow.this.mListItemExpandMaximum) {
                return;
            }
            ListPopupWindow.this.mPopup.setInputMethodMode(2);
            ListPopupWindow.this.show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class PopupTouchInterceptor implements View.OnTouchListener {
        private PopupTouchInterceptor() {
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            int action = motionEvent.getAction();
            int x = (int) motionEvent.getX();
            int y = (int) motionEvent.getY();
            if (action == 0 && ListPopupWindow.this.mPopup != null && ListPopupWindow.this.mPopup.isShowing() && x >= 0 && x < ListPopupWindow.this.mPopup.getWidth() && y >= 0 && y < ListPopupWindow.this.mPopup.getHeight()) {
                ListPopupWindow.this.mHandler.postDelayed(ListPopupWindow.this.mResizePopupRunnable, 250L);
                return false;
            }
            if (action != 1) {
                return false;
            }
            ListPopupWindow.this.mHandler.removeCallbacks(ListPopupWindow.this.mResizePopupRunnable);
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class PopupScrollListener implements AbsListView.OnScrollListener {
        @Override // android.widget.AbsListView.OnScrollListener
        public void onScroll(AbsListView absListView, int i, int i2, int i3) {
        }

        private PopupScrollListener() {
        }

        @Override // android.widget.AbsListView.OnScrollListener
        public void onScrollStateChanged(AbsListView absListView, int i) {
            if (i != 1 || ListPopupWindow.this.isInputMethodNotNeeded() || ListPopupWindow.this.mPopup.getContentView() == null) {
                return;
            }
            ListPopupWindow.this.mHandler.removeCallbacks(ListPopupWindow.this.mResizePopupRunnable);
            ListPopupWindow.this.mResizePopupRunnable.run();
        }
    }

    private void setPopupClipToScreenEnabled(boolean z) {
        if (sClipToWindowEnabledMethod != null) {
            try {
                sClipToWindowEnabledMethod.invoke(this.mPopup, Boolean.valueOf(z));
            } catch (Exception unused) {
                Log.i(TAG, "Could not call setClipToScreenEnabled() on PopupWindow. Oh well.");
            }
        }
    }

    private int getMaxAvailableHeight(View view, int i, boolean z) {
        if (sGetMaxAvailableHeightMethod != null) {
            try {
                return ((Integer) sGetMaxAvailableHeightMethod.invoke(this.mPopup, view, Integer.valueOf(i), Boolean.valueOf(z))).intValue();
            } catch (Exception unused) {
                Log.i(TAG, "Could not call getMaxAvailableHeightMethod(View, int, boolean) on PopupWindow. Using the public version.");
            }
        }
        return this.mPopup.getMaxAvailableHeight(view, i);
    }
}
