package android.support.v7.widget;

import android.R;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.view.TintableBackgroundView;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.ListPopupWindow;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

/* loaded from: classes.dex */
public class AppCompatSpinner extends Spinner implements TintableBackgroundView {
    private static final int[] ATTRS_ANDROID_SPINNERMODE;
    private static final boolean IS_AT_LEAST_JB;
    private static final boolean IS_AT_LEAST_M;
    private static final int MAX_ITEMS_MEASURED = 15;
    private static final int MODE_DIALOG = 0;
    private static final int MODE_DROPDOWN = 1;
    private static final int MODE_THEME = -1;
    private static final String TAG = "AppCompatSpinner";
    private AppCompatBackgroundHelper mBackgroundTintHelper;
    private int mDropDownWidth;
    private ListPopupWindow.ForwardingListener mForwardingListener;
    private DropdownPopup mPopup;
    private Context mPopupContext;
    private boolean mPopupSet;
    private SpinnerAdapter mTempAdapter;
    private final Rect mTempRect;
    private TintManager mTintManager;

    static {
        IS_AT_LEAST_M = Build.VERSION.SDK_INT >= 23;
        IS_AT_LEAST_JB = Build.VERSION.SDK_INT >= 16;
        ATTRS_ANDROID_SPINNERMODE = new int[]{R.attr.spinnerMode};
    }

    public AppCompatSpinner(Context context) {
        this(context, (AttributeSet) null);
    }

    public AppCompatSpinner(Context context, int i) {
        this(context, null, android.support.v7.appcompat.R.attr.spinnerStyle, i);
    }

    public AppCompatSpinner(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, android.support.v7.appcompat.R.attr.spinnerStyle);
    }

    public AppCompatSpinner(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, -1);
    }

    public AppCompatSpinner(Context context, AttributeSet attributeSet, int i, int i2) {
        this(context, attributeSet, i, i2, null);
    }

    /* JADX WARN: Code restructure failed: missing block: B:19:0x0063, code lost:
    
        if (r7 != null) goto L25;
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x0065, code lost:
    
        r7.recycle();
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0077, code lost:
    
        if (r7 == null) goto L40;
     */
    /* JADX WARN: Removed duplicated region for block: B:27:0x007d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public AppCompatSpinner(android.content.Context r7, android.util.AttributeSet r8, int r9, int r10, android.content.res.Resources.Theme r11) {
        /*
            r6 = this;
            r6.<init>(r7, r8, r9)
            android.graphics.Rect r0 = new android.graphics.Rect
            r0.<init>()
            r6.mTempRect = r0
            int[] r0 = android.support.v7.appcompat.R.styleable.Spinner
            r1 = 0
            android.support.v7.widget.TintTypedArray r0 = android.support.v7.widget.TintTypedArray.obtainStyledAttributes(r7, r8, r0, r9, r1)
            android.support.v7.widget.TintManager r2 = r0.getTintManager()
            r6.mTintManager = r2
            android.support.v7.widget.AppCompatBackgroundHelper r2 = new android.support.v7.widget.AppCompatBackgroundHelper
            android.support.v7.widget.TintManager r3 = r6.mTintManager
            r2.<init>(r6, r3)
            r6.mBackgroundTintHelper = r2
            r2 = 0
            if (r11 == 0) goto L2b
            android.support.v7.view.ContextThemeWrapper r3 = new android.support.v7.view.ContextThemeWrapper
            r3.<init>(r7, r11)
            r6.mPopupContext = r3
            goto L44
        L2b:
            int r11 = android.support.v7.appcompat.R.styleable.Spinner_popupTheme
            int r11 = r0.getResourceId(r11, r1)
            if (r11 == 0) goto L3b
            android.support.v7.view.ContextThemeWrapper r3 = new android.support.v7.view.ContextThemeWrapper
            r3.<init>(r7, r11)
            r6.mPopupContext = r3
            goto L44
        L3b:
            boolean r11 = android.support.v7.widget.AppCompatSpinner.IS_AT_LEAST_M
            if (r11 != 0) goto L41
            r11 = r7
            goto L42
        L41:
            r11 = r2
        L42:
            r6.mPopupContext = r11
        L44:
            android.content.Context r11 = r6.mPopupContext
            r3 = 1
            if (r11 == 0) goto Lba
            r11 = -1
            if (r10 != r11) goto L82
            int r11 = android.os.Build.VERSION.SDK_INT
            r4 = 11
            if (r11 < r4) goto L81
            int[] r11 = android.support.v7.widget.AppCompatSpinner.ATTRS_ANDROID_SPINNERMODE     // Catch: java.lang.Throwable -> L6b java.lang.Exception -> L6e
            android.content.res.TypedArray r7 = r7.obtainStyledAttributes(r8, r11, r9, r1)     // Catch: java.lang.Throwable -> L6b java.lang.Exception -> L6e
            boolean r11 = r7.hasValue(r1)     // Catch: java.lang.Exception -> L69 java.lang.Throwable -> L7a
            if (r11 == 0) goto L63
            int r11 = r7.getInt(r1, r1)     // Catch: java.lang.Exception -> L69 java.lang.Throwable -> L7a
            r10 = r11
        L63:
            if (r7 == 0) goto L82
        L65:
            r7.recycle()
            goto L82
        L69:
            r11 = move-exception
            goto L70
        L6b:
            r8 = move-exception
            r7 = r2
            goto L7b
        L6e:
            r11 = move-exception
            r7 = r2
        L70:
            java.lang.String r4 = "AppCompatSpinner"
            java.lang.String r5 = "Could not read android:spinnerMode"
            android.util.Log.i(r4, r5, r11)     // Catch: java.lang.Throwable -> L7a
            if (r7 == 0) goto L82
            goto L65
        L7a:
            r8 = move-exception
        L7b:
            if (r7 == 0) goto L80
            r7.recycle()
        L80:
            throw r8
        L81:
            r10 = 1
        L82:
            if (r10 != r3) goto Lba
            android.support.v7.widget.AppCompatSpinner$DropdownPopup r7 = new android.support.v7.widget.AppCompatSpinner$DropdownPopup
            android.content.Context r10 = r6.mPopupContext
            r7.<init>(r10, r8, r9)
            android.content.Context r10 = r6.mPopupContext
            int[] r11 = android.support.v7.appcompat.R.styleable.Spinner
            android.support.v7.widget.TintTypedArray r10 = android.support.v7.widget.TintTypedArray.obtainStyledAttributes(r10, r8, r11, r9, r1)
            int r11 = android.support.v7.appcompat.R.styleable.Spinner_android_dropDownWidth
            r1 = -2
            int r11 = r10.getLayoutDimension(r11, r1)
            r6.mDropDownWidth = r11
            int r11 = android.support.v7.appcompat.R.styleable.Spinner_android_popupBackground
            android.graphics.drawable.Drawable r11 = r10.getDrawable(r11)
            r7.setBackgroundDrawable(r11)
            int r11 = android.support.v7.appcompat.R.styleable.Spinner_android_prompt
            java.lang.String r11 = r0.getString(r11)
            r7.setPromptText(r11)
            r10.recycle()
            r6.mPopup = r7
            android.support.v7.widget.AppCompatSpinner$1 r10 = new android.support.v7.widget.AppCompatSpinner$1
            r10.<init>(r6)
            r6.mForwardingListener = r10
        Lba:
            r0.recycle()
            r6.mPopupSet = r3
            android.widget.SpinnerAdapter r7 = r6.mTempAdapter
            if (r7 == 0) goto Lca
            android.widget.SpinnerAdapter r7 = r6.mTempAdapter
            r6.setAdapter(r7)
            r6.mTempAdapter = r2
        Lca:
            android.support.v7.widget.AppCompatBackgroundHelper r7 = r6.mBackgroundTintHelper
            r7.loadFromAttributes(r8, r9)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.AppCompatSpinner.<init>(android.content.Context, android.util.AttributeSet, int, int, android.content.res.Resources$Theme):void");
    }

    @Override // android.widget.Spinner
    public Context getPopupContext() {
        if (this.mPopup != null) {
            return this.mPopupContext;
        }
        if (IS_AT_LEAST_M) {
            return super.getPopupContext();
        }
        return null;
    }

    @Override // android.widget.Spinner
    public void setPopupBackgroundDrawable(Drawable drawable) {
        if (this.mPopup != null) {
            this.mPopup.setBackgroundDrawable(drawable);
        } else if (IS_AT_LEAST_JB) {
            super.setPopupBackgroundDrawable(drawable);
        }
    }

    @Override // android.widget.Spinner
    public void setPopupBackgroundResource(@DrawableRes int i) {
        setPopupBackgroundDrawable(getPopupContext().getDrawable(i));
    }

    @Override // android.widget.Spinner
    public Drawable getPopupBackground() {
        if (this.mPopup != null) {
            return this.mPopup.getBackground();
        }
        if (IS_AT_LEAST_JB) {
            return super.getPopupBackground();
        }
        return null;
    }

    @Override // android.widget.Spinner
    public void setDropDownVerticalOffset(int i) {
        if (this.mPopup != null) {
            this.mPopup.setVerticalOffset(i);
        } else if (IS_AT_LEAST_JB) {
            super.setDropDownVerticalOffset(i);
        }
    }

    @Override // android.widget.Spinner
    public int getDropDownVerticalOffset() {
        if (this.mPopup != null) {
            return this.mPopup.getVerticalOffset();
        }
        if (IS_AT_LEAST_JB) {
            return super.getDropDownVerticalOffset();
        }
        return 0;
    }

    @Override // android.widget.Spinner
    public void setDropDownHorizontalOffset(int i) {
        if (this.mPopup != null) {
            this.mPopup.setHorizontalOffset(i);
        } else if (IS_AT_LEAST_JB) {
            super.setDropDownHorizontalOffset(i);
        }
    }

    @Override // android.widget.Spinner
    public int getDropDownHorizontalOffset() {
        if (this.mPopup != null) {
            return this.mPopup.getHorizontalOffset();
        }
        if (IS_AT_LEAST_JB) {
            return super.getDropDownHorizontalOffset();
        }
        return 0;
    }

    @Override // android.widget.Spinner
    public void setDropDownWidth(int i) {
        if (this.mPopup != null) {
            this.mDropDownWidth = i;
        } else if (IS_AT_LEAST_JB) {
            super.setDropDownWidth(i);
        }
    }

    @Override // android.widget.Spinner
    public int getDropDownWidth() {
        if (this.mPopup != null) {
            return this.mDropDownWidth;
        }
        if (IS_AT_LEAST_JB) {
            return super.getDropDownWidth();
        }
        return 0;
    }

    @Override // android.widget.AdapterView
    public void setAdapter(SpinnerAdapter spinnerAdapter) {
        if (!this.mPopupSet) {
            this.mTempAdapter = spinnerAdapter;
            return;
        }
        super.setAdapter(spinnerAdapter);
        if (this.mPopup != null) {
            this.mPopup.setAdapter(new DropDownAdapter(spinnerAdapter, (this.mPopupContext == null ? getContext() : this.mPopupContext).getTheme()));
        }
    }

    @Override // android.widget.Spinner, android.widget.AdapterView, android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mPopup == null || !this.mPopup.isShowing()) {
            return;
        }
        this.mPopup.dismiss();
    }

    @Override // android.widget.Spinner, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.mForwardingListener == null || !this.mForwardingListener.onTouch(this, motionEvent)) {
            return super.onTouchEvent(motionEvent);
        }
        return true;
    }

    @Override // android.widget.Spinner, android.widget.AbsSpinner, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.mPopup == null || View.MeasureSpec.getMode(i) != Integer.MIN_VALUE) {
            return;
        }
        setMeasuredDimension(Math.min(Math.max(getMeasuredWidth(), compatMeasureContentWidth(getAdapter(), getBackground())), View.MeasureSpec.getSize(i)), getMeasuredHeight());
    }

    @Override // android.widget.Spinner, android.view.View
    public boolean performClick() {
        if (this.mPopup != null && !this.mPopup.isShowing()) {
            this.mPopup.show();
            return true;
        }
        return super.performClick();
    }

    @Override // android.widget.Spinner
    public void setPrompt(CharSequence charSequence) {
        if (this.mPopup != null) {
            this.mPopup.setPromptText(charSequence);
        } else {
            super.setPrompt(charSequence);
        }
    }

    @Override // android.widget.Spinner
    public CharSequence getPrompt() {
        return this.mPopup != null ? this.mPopup.getHintText() : super.getPrompt();
    }

    @Override // android.view.View
    public void setBackgroundResource(@DrawableRes int i) {
        super.setBackgroundResource(i);
        if (this.mBackgroundTintHelper != null) {
            this.mBackgroundTintHelper.onSetBackgroundResource(i);
        }
    }

    @Override // android.view.View
    public void setBackgroundDrawable(Drawable drawable) {
        super.setBackgroundDrawable(drawable);
        if (this.mBackgroundTintHelper != null) {
            this.mBackgroundTintHelper.onSetBackgroundDrawable(drawable);
        }
    }

    @Override // android.support.v4.view.TintableBackgroundView
    public void setSupportBackgroundTintList(@Nullable ColorStateList colorStateList) {
        if (this.mBackgroundTintHelper != null) {
            this.mBackgroundTintHelper.setSupportBackgroundTintList(colorStateList);
        }
    }

    @Override // android.support.v4.view.TintableBackgroundView
    @Nullable
    public ColorStateList getSupportBackgroundTintList() {
        if (this.mBackgroundTintHelper != null) {
            return this.mBackgroundTintHelper.getSupportBackgroundTintList();
        }
        return null;
    }

    @Override // android.support.v4.view.TintableBackgroundView
    public void setSupportBackgroundTintMode(@Nullable PorterDuff.Mode mode) {
        if (this.mBackgroundTintHelper != null) {
            this.mBackgroundTintHelper.setSupportBackgroundTintMode(mode);
        }
    }

    @Override // android.support.v4.view.TintableBackgroundView
    @Nullable
    public PorterDuff.Mode getSupportBackgroundTintMode() {
        if (this.mBackgroundTintHelper != null) {
            return this.mBackgroundTintHelper.getSupportBackgroundTintMode();
        }
        return null;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (this.mBackgroundTintHelper != null) {
            this.mBackgroundTintHelper.applySupportBackgroundTint();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int compatMeasureContentWidth(SpinnerAdapter spinnerAdapter, Drawable drawable) {
        int i = 0;
        if (spinnerAdapter == null) {
            return 0;
        }
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 0);
        int makeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 0);
        int max = Math.max(0, getSelectedItemPosition());
        int min = Math.min(spinnerAdapter.getCount(), max + 15);
        View view = null;
        int i2 = 0;
        for (int max2 = Math.max(0, max - (15 - (min - max))); max2 < min; max2++) {
            int itemViewType = spinnerAdapter.getItemViewType(max2);
            if (itemViewType != i) {
                view = null;
                i = itemViewType;
            }
            view = spinnerAdapter.getView(max2, view, this);
            if (view.getLayoutParams() == null) {
                view.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
            }
            view.measure(makeMeasureSpec, makeMeasureSpec2);
            i2 = Math.max(i2, view.getMeasuredWidth());
        }
        if (drawable == null) {
            return i2;
        }
        drawable.getPadding(this.mTempRect);
        return i2 + this.mTempRect.left + this.mTempRect.right;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class DropDownAdapter implements ListAdapter, SpinnerAdapter {
        private SpinnerAdapter mAdapter;
        private ListAdapter mListAdapter;

        @Override // android.widget.Adapter
        public int getItemViewType(int i) {
            return 0;
        }

        @Override // android.widget.Adapter
        public int getViewTypeCount() {
            return 1;
        }

        public DropDownAdapter(@Nullable SpinnerAdapter spinnerAdapter, @Nullable Resources.Theme theme) {
            this.mAdapter = spinnerAdapter;
            if (spinnerAdapter instanceof ListAdapter) {
                this.mListAdapter = (ListAdapter) spinnerAdapter;
            }
            if (theme != null) {
                if (AppCompatSpinner.IS_AT_LEAST_M && (spinnerAdapter instanceof android.widget.ThemedSpinnerAdapter)) {
                    android.widget.ThemedSpinnerAdapter themedSpinnerAdapter = (android.widget.ThemedSpinnerAdapter) spinnerAdapter;
                    if (themedSpinnerAdapter.getDropDownViewTheme() != theme) {
                        themedSpinnerAdapter.setDropDownViewTheme(theme);
                        return;
                    }
                    return;
                }
                if (spinnerAdapter instanceof ThemedSpinnerAdapter) {
                    ThemedSpinnerAdapter themedSpinnerAdapter2 = (ThemedSpinnerAdapter) spinnerAdapter;
                    if (themedSpinnerAdapter2.getDropDownViewTheme() == null) {
                        themedSpinnerAdapter2.setDropDownViewTheme(theme);
                    }
                }
            }
        }

        @Override // android.widget.Adapter
        public int getCount() {
            if (this.mAdapter == null) {
                return 0;
            }
            return this.mAdapter.getCount();
        }

        @Override // android.widget.Adapter
        public Object getItem(int i) {
            if (this.mAdapter == null) {
                return null;
            }
            return this.mAdapter.getItem(i);
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            if (this.mAdapter == null) {
                return -1L;
            }
            return this.mAdapter.getItemId(i);
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            return getDropDownView(i, view, viewGroup);
        }

        @Override // android.widget.SpinnerAdapter
        public View getDropDownView(int i, View view, ViewGroup viewGroup) {
            if (this.mAdapter == null) {
                return null;
            }
            return this.mAdapter.getDropDownView(i, view, viewGroup);
        }

        @Override // android.widget.Adapter
        public boolean hasStableIds() {
            return this.mAdapter != null && this.mAdapter.hasStableIds();
        }

        @Override // android.widget.Adapter
        public void registerDataSetObserver(DataSetObserver dataSetObserver) {
            if (this.mAdapter != null) {
                this.mAdapter.registerDataSetObserver(dataSetObserver);
            }
        }

        @Override // android.widget.Adapter
        public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
            if (this.mAdapter != null) {
                this.mAdapter.unregisterDataSetObserver(dataSetObserver);
            }
        }

        @Override // android.widget.ListAdapter
        public boolean areAllItemsEnabled() {
            ListAdapter listAdapter = this.mListAdapter;
            if (listAdapter != null) {
                return listAdapter.areAllItemsEnabled();
            }
            return true;
        }

        @Override // android.widget.ListAdapter
        public boolean isEnabled(int i) {
            ListAdapter listAdapter = this.mListAdapter;
            if (listAdapter != null) {
                return listAdapter.isEnabled(i);
            }
            return true;
        }

        @Override // android.widget.Adapter
        public boolean isEmpty() {
            return getCount() == 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class DropdownPopup extends ListPopupWindow {
        private ListAdapter mAdapter;
        private CharSequence mHintText;
        private final Rect mVisibleRect;

        public DropdownPopup(Context context, AttributeSet attributeSet, int i) {
            super(context, attributeSet, i);
            this.mVisibleRect = new Rect();
            setAnchorView(AppCompatSpinner.this);
            setModal(true);
            setPromptPosition(0);
            setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: android.support.v7.widget.AppCompatSpinner.DropdownPopup.1
                @Override // android.widget.AdapterView.OnItemClickListener
                public void onItemClick(AdapterView<?> adapterView, View view, int i2, long j) {
                    AppCompatSpinner.this.setSelection(i2);
                    if (AppCompatSpinner.this.getOnItemClickListener() != null) {
                        AppCompatSpinner.this.performItemClick(view, i2, DropdownPopup.this.mAdapter.getItemId(i2));
                    }
                    DropdownPopup.this.dismiss();
                }
            });
        }

        @Override // android.support.v7.widget.ListPopupWindow
        public void setAdapter(ListAdapter listAdapter) {
            super.setAdapter(listAdapter);
            this.mAdapter = listAdapter;
        }

        public CharSequence getHintText() {
            return this.mHintText;
        }

        public void setPromptText(CharSequence charSequence) {
            this.mHintText = charSequence;
        }

        void computeContentWidth() {
            Drawable background = getBackground();
            int i = 0;
            if (background != null) {
                background.getPadding(AppCompatSpinner.this.mTempRect);
                i = ViewUtils.isLayoutRtl(AppCompatSpinner.this) ? AppCompatSpinner.this.mTempRect.right : -AppCompatSpinner.this.mTempRect.left;
            } else {
                Rect rect = AppCompatSpinner.this.mTempRect;
                AppCompatSpinner.this.mTempRect.right = 0;
                rect.left = 0;
            }
            int paddingLeft = AppCompatSpinner.this.getPaddingLeft();
            int paddingRight = AppCompatSpinner.this.getPaddingRight();
            int width = AppCompatSpinner.this.getWidth();
            if (AppCompatSpinner.this.mDropDownWidth == -2) {
                int compatMeasureContentWidth = AppCompatSpinner.this.compatMeasureContentWidth((SpinnerAdapter) this.mAdapter, getBackground());
                int i2 = (AppCompatSpinner.this.getContext().getResources().getDisplayMetrics().widthPixels - AppCompatSpinner.this.mTempRect.left) - AppCompatSpinner.this.mTempRect.right;
                if (compatMeasureContentWidth > i2) {
                    compatMeasureContentWidth = i2;
                }
                setContentWidth(Math.max(compatMeasureContentWidth, (width - paddingLeft) - paddingRight));
            } else if (AppCompatSpinner.this.mDropDownWidth != -1) {
                setContentWidth(AppCompatSpinner.this.mDropDownWidth);
            } else {
                setContentWidth((width - paddingLeft) - paddingRight);
            }
            setHorizontalOffset(ViewUtils.isLayoutRtl(AppCompatSpinner.this) ? i + ((width - paddingRight) - getWidth()) : i + paddingLeft);
        }

        @Override // android.support.v7.widget.ListPopupWindow
        public void show() {
            ViewTreeObserver viewTreeObserver;
            boolean isShowing = isShowing();
            computeContentWidth();
            setInputMethodMode(2);
            super.show();
            getListView().setChoiceMode(1);
            setSelection(AppCompatSpinner.this.getSelectedItemPosition());
            if (isShowing || (viewTreeObserver = AppCompatSpinner.this.getViewTreeObserver()) == null) {
                return;
            }
            final ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() { // from class: android.support.v7.widget.AppCompatSpinner.DropdownPopup.2
                @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
                public void onGlobalLayout() {
                    if (!DropdownPopup.this.isVisibleToUser(AppCompatSpinner.this)) {
                        DropdownPopup.this.dismiss();
                    } else {
                        DropdownPopup.this.computeContentWidth();
                        DropdownPopup.super.show();
                    }
                }
            };
            viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener);
            setOnDismissListener(new PopupWindow.OnDismissListener() { // from class: android.support.v7.widget.AppCompatSpinner.DropdownPopup.3
                @Override // android.widget.PopupWindow.OnDismissListener
                public void onDismiss() {
                    ViewTreeObserver viewTreeObserver2 = AppCompatSpinner.this.getViewTreeObserver();
                    if (viewTreeObserver2 != null) {
                        viewTreeObserver2.removeGlobalOnLayoutListener(onGlobalLayoutListener);
                    }
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isVisibleToUser(View view) {
            return ViewCompat.isAttachedToWindow(view) && view.getGlobalVisibleRect(this.mVisibleRect);
        }
    }
}
