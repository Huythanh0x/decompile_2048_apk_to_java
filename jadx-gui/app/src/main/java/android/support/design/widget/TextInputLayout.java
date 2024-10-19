package android.support.design.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.design.R;
import android.support.design.widget.ValueAnimatorCompat;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.widget.Space;
import android.support.v7.widget.TintManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/* loaded from: classes.dex */
public class TextInputLayout extends LinearLayout {
    private static final int ANIMATION_DURATION = 200;
    private static final int INVALID_MAX_LENGTH = -1;
    private ValueAnimatorCompat mAnimator;
    private final CollapsingTextHelper mCollapsingTextHelper;
    private boolean mCounterEnabled;
    private int mCounterMaxLength;
    private int mCounterOverflowTextAppearance;
    private boolean mCounterOverflowed;
    private int mCounterTextAppearance;
    private TextView mCounterView;
    private ColorStateList mDefaultTextColor;
    private EditText mEditText;
    private boolean mErrorEnabled;
    private boolean mErrorShown;
    private int mErrorTextAppearance;
    private TextView mErrorView;
    private ColorStateList mFocusedTextColor;
    private CharSequence mHint;
    private boolean mHintAnimationEnabled;
    private LinearLayout mIndicatorArea;
    private Paint mTmpPaint;

    public TextInputLayout(Context context) {
        this(context, null);
    }

    public TextInputLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public TextInputLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet);
        this.mCollapsingTextHelper = new CollapsingTextHelper(this);
        ThemeUtils.checkAppCompatTheme(context);
        setOrientation(1);
        setWillNotDraw(false);
        setAddStatesFromChildren(true);
        this.mCollapsingTextHelper.setTextSizeInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
        this.mCollapsingTextHelper.setPositionInterpolator(new AccelerateInterpolator());
        this.mCollapsingTextHelper.setCollapsedTextGravity(8388659);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.TextInputLayout, i, R.style.Widget_Design_TextInputLayout);
        setHint(obtainStyledAttributes.getText(R.styleable.TextInputLayout_android_hint));
        this.mHintAnimationEnabled = obtainStyledAttributes.getBoolean(R.styleable.TextInputLayout_hintAnimationEnabled, true);
        if (obtainStyledAttributes.hasValue(R.styleable.TextInputLayout_android_textColorHint)) {
            ColorStateList colorStateList = obtainStyledAttributes.getColorStateList(R.styleable.TextInputLayout_android_textColorHint);
            this.mFocusedTextColor = colorStateList;
            this.mDefaultTextColor = colorStateList;
        }
        if (obtainStyledAttributes.getResourceId(R.styleable.TextInputLayout_hintTextAppearance, -1) != -1) {
            setHintTextAppearance(obtainStyledAttributes.getResourceId(R.styleable.TextInputLayout_hintTextAppearance, 0));
        }
        this.mErrorTextAppearance = obtainStyledAttributes.getResourceId(R.styleable.TextInputLayout_errorTextAppearance, 0);
        boolean z = obtainStyledAttributes.getBoolean(R.styleable.TextInputLayout_errorEnabled, false);
        boolean z2 = obtainStyledAttributes.getBoolean(R.styleable.TextInputLayout_counterEnabled, false);
        setCounterMaxLength(obtainStyledAttributes.getInt(R.styleable.TextInputLayout_counterMaxLength, -1));
        this.mCounterTextAppearance = obtainStyledAttributes.getResourceId(R.styleable.TextInputLayout_counterTextAppearance, 0);
        this.mCounterOverflowTextAppearance = obtainStyledAttributes.getResourceId(R.styleable.TextInputLayout_counterOverflowTextAppearance, 0);
        obtainStyledAttributes.recycle();
        setErrorEnabled(z);
        setCounterEnabled(z2);
        if (ViewCompat.getImportantForAccessibility(this) == 0) {
            ViewCompat.setImportantForAccessibility(this, 1);
        }
        ViewCompat.setAccessibilityDelegate(this, new TextInputAccessibilityDelegate());
    }

    @Override // android.view.ViewGroup
    public void addView(View view, int i, ViewGroup.LayoutParams layoutParams) {
        if (view instanceof EditText) {
            setEditText((EditText) view);
            super.addView(view, 0, updateEditTextMargin(layoutParams));
        } else {
            super.addView(view, i, layoutParams);
        }
    }

    public void setTypeface(@Nullable Typeface typeface) {
        this.mCollapsingTextHelper.setTypefaces(typeface);
    }

    @NonNull
    public Typeface getTypeface() {
        return this.mCollapsingTextHelper.getCollapsedTypeface();
    }

    private void setEditText(EditText editText) {
        if (this.mEditText != null) {
            throw new IllegalArgumentException("We already have an EditText, can only have one");
        }
        this.mEditText = editText;
        this.mCollapsingTextHelper.setTypefaces(this.mEditText.getTypeface());
        this.mCollapsingTextHelper.setExpandedTextSize(this.mEditText.getTextSize());
        this.mCollapsingTextHelper.setExpandedTextGravity(this.mEditText.getGravity());
        this.mEditText.addTextChangedListener(new TextWatcher() { // from class: android.support.design.widget.TextInputLayout.1
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                TextInputLayout.this.updateLabelVisibility(true);
                if (TextInputLayout.this.mCounterEnabled) {
                    TextInputLayout.this.updateCounter(editable.length());
                }
            }
        });
        if (this.mDefaultTextColor == null) {
            this.mDefaultTextColor = this.mEditText.getHintTextColors();
        }
        if (TextUtils.isEmpty(this.mHint)) {
            setHint(this.mEditText.getHint());
            this.mEditText.setHint((CharSequence) null);
        }
        if (this.mCounterView != null) {
            updateCounter(this.mEditText.getText().length());
        }
        if (this.mIndicatorArea != null) {
            adjustIndicatorPadding();
        }
        updateLabelVisibility(false);
    }

    private LinearLayout.LayoutParams updateEditTextMargin(ViewGroup.LayoutParams layoutParams) {
        LinearLayout.LayoutParams layoutParams2 = layoutParams instanceof LinearLayout.LayoutParams ? (LinearLayout.LayoutParams) layoutParams : new LinearLayout.LayoutParams(layoutParams);
        if (this.mTmpPaint == null) {
            this.mTmpPaint = new Paint();
        }
        this.mTmpPaint.setTypeface(this.mCollapsingTextHelper.getCollapsedTypeface());
        this.mTmpPaint.setTextSize(this.mCollapsingTextHelper.getCollapsedTextSize());
        layoutParams2.topMargin = (int) (-this.mTmpPaint.ascent());
        return layoutParams2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateLabelVisibility(boolean z) {
        boolean z2 = (this.mEditText == null || TextUtils.isEmpty(this.mEditText.getText())) ? false : true;
        boolean arrayContains = arrayContains(getDrawableState(), android.R.attr.state_focused);
        boolean isEmpty = true ^ TextUtils.isEmpty(getError());
        if (this.mDefaultTextColor != null) {
            this.mCollapsingTextHelper.setExpandedTextColor(this.mDefaultTextColor.getDefaultColor());
        }
        if (this.mCounterOverflowed && this.mCounterView != null) {
            this.mCollapsingTextHelper.setCollapsedTextColor(this.mCounterView.getCurrentTextColor());
        } else if (isEmpty && this.mErrorView != null) {
            this.mCollapsingTextHelper.setCollapsedTextColor(this.mErrorView.getCurrentTextColor());
        } else if (arrayContains && this.mFocusedTextColor != null) {
            this.mCollapsingTextHelper.setCollapsedTextColor(this.mFocusedTextColor.getDefaultColor());
        } else if (this.mDefaultTextColor != null) {
            this.mCollapsingTextHelper.setCollapsedTextColor(this.mDefaultTextColor.getDefaultColor());
        }
        if (z2 || arrayContains || isEmpty) {
            collapseHint(z);
        } else {
            expandHint(z);
        }
    }

    @Nullable
    public EditText getEditText() {
        return this.mEditText;
    }

    public void setHint(@Nullable CharSequence charSequence) {
        this.mHint = charSequence;
        this.mCollapsingTextHelper.setText(charSequence);
        sendAccessibilityEvent(2048);
    }

    @Nullable
    public CharSequence getHint() {
        return this.mHint;
    }

    public void setHintTextAppearance(@StyleRes int i) {
        this.mCollapsingTextHelper.setCollapsedTextAppearance(i);
        this.mFocusedTextColor = ColorStateList.valueOf(this.mCollapsingTextHelper.getCollapsedTextColor());
        if (this.mEditText != null) {
            updateLabelVisibility(false);
            this.mEditText.setLayoutParams(updateEditTextMargin(this.mEditText.getLayoutParams()));
            this.mEditText.requestLayout();
        }
    }

    private void addIndicator(TextView textView, int i) {
        if (this.mIndicatorArea == null) {
            this.mIndicatorArea = new LinearLayout(getContext());
            this.mIndicatorArea.setOrientation(0);
            addView(this.mIndicatorArea, -1, -2);
            this.mIndicatorArea.addView(new Space(getContext()), new LinearLayout.LayoutParams(0, 0, 1.0f));
            if (this.mEditText != null) {
                adjustIndicatorPadding();
            }
        }
        this.mIndicatorArea.setVisibility(0);
        this.mIndicatorArea.addView(textView, i);
    }

    private void adjustIndicatorPadding() {
        ViewCompat.setPaddingRelative(this.mIndicatorArea, ViewCompat.getPaddingStart(this.mEditText), 0, ViewCompat.getPaddingEnd(this.mEditText), this.mEditText.getPaddingBottom());
    }

    private void removeIndicator(TextView textView) {
        if (this.mIndicatorArea != null) {
            this.mIndicatorArea.removeView(textView);
            if (this.mIndicatorArea.getChildCount() == 0) {
                this.mIndicatorArea.setVisibility(8);
            }
        }
    }

    public void setErrorEnabled(boolean z) {
        if (this.mErrorEnabled != z) {
            if (this.mErrorView != null) {
                ViewCompat.animate(this.mErrorView).cancel();
            }
            if (z) {
                this.mErrorView = new TextView(getContext());
                this.mErrorView.setTextAppearance(getContext(), this.mErrorTextAppearance);
                this.mErrorView.setVisibility(4);
                ViewCompat.setAccessibilityLiveRegion(this.mErrorView, 1);
                addIndicator(this.mErrorView, 0);
            } else {
                this.mErrorShown = false;
                updateEditTextBackground();
                removeIndicator(this.mErrorView);
                this.mErrorView = null;
            }
            this.mErrorEnabled = z;
        }
    }

    public boolean isErrorEnabled() {
        return this.mErrorEnabled;
    }

    public void setError(@Nullable CharSequence charSequence) {
        if (!this.mErrorEnabled) {
            if (TextUtils.isEmpty(charSequence)) {
                return;
            } else {
                setErrorEnabled(true);
            }
        }
        if (!TextUtils.isEmpty(charSequence)) {
            ViewCompat.setAlpha(this.mErrorView, 0.0f);
            this.mErrorView.setText(charSequence);
            ViewCompat.animate(this.mErrorView).alpha(1.0f).setDuration(200L).setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR).setListener(new ViewPropertyAnimatorListenerAdapter() { // from class: android.support.design.widget.TextInputLayout.2
                @Override // android.support.v4.view.ViewPropertyAnimatorListenerAdapter, android.support.v4.view.ViewPropertyAnimatorListener
                public void onAnimationStart(View view) {
                    view.setVisibility(0);
                }
            }).start();
            this.mErrorShown = true;
            updateEditTextBackground();
            updateLabelVisibility(true);
            return;
        }
        if (this.mErrorView.getVisibility() == 0) {
            ViewCompat.animate(this.mErrorView).alpha(0.0f).setDuration(200L).setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR).setListener(new ViewPropertyAnimatorListenerAdapter() { // from class: android.support.design.widget.TextInputLayout.3
                @Override // android.support.v4.view.ViewPropertyAnimatorListenerAdapter, android.support.v4.view.ViewPropertyAnimatorListener
                public void onAnimationEnd(View view) {
                    view.setVisibility(4);
                    TextInputLayout.this.updateLabelVisibility(true);
                }
            }).start();
            this.mErrorShown = false;
            updateEditTextBackground();
        }
    }

    public void setCounterEnabled(boolean z) {
        if (this.mCounterEnabled != z) {
            if (z) {
                this.mCounterView = new TextView(getContext());
                this.mCounterView.setMaxLines(1);
                this.mCounterView.setTextAppearance(getContext(), this.mCounterTextAppearance);
                ViewCompat.setAccessibilityLiveRegion(this.mCounterView, 1);
                addIndicator(this.mCounterView, -1);
                if (this.mEditText == null) {
                    updateCounter(0);
                } else {
                    updateCounter(this.mEditText.getText().length());
                }
            } else {
                removeIndicator(this.mCounterView);
                this.mCounterView = null;
            }
            this.mCounterEnabled = z;
        }
    }

    public void setCounterMaxLength(int i) {
        if (this.mCounterMaxLength != i) {
            if (i > 0) {
                this.mCounterMaxLength = i;
            } else {
                this.mCounterMaxLength = -1;
            }
            if (this.mCounterEnabled) {
                updateCounter(this.mEditText == null ? 0 : this.mEditText.getText().length());
            }
        }
    }

    public int getCounterMaxLength() {
        return this.mCounterMaxLength;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCounter(int i) {
        boolean z = this.mCounterOverflowed;
        if (this.mCounterMaxLength == -1) {
            this.mCounterView.setText(String.valueOf(i));
            this.mCounterOverflowed = false;
        } else {
            this.mCounterOverflowed = i > this.mCounterMaxLength;
            if (z != this.mCounterOverflowed) {
                this.mCounterView.setTextAppearance(getContext(), this.mCounterOverflowed ? this.mCounterOverflowTextAppearance : this.mCounterTextAppearance);
            }
            this.mCounterView.setText(getContext().getString(R.string.character_counter_pattern, Integer.valueOf(i), Integer.valueOf(this.mCounterMaxLength)));
        }
        if (this.mEditText == null || z == this.mCounterOverflowed) {
            return;
        }
        updateLabelVisibility(false);
        updateEditTextBackground();
    }

    private void updateEditTextBackground() {
        if (this.mErrorShown && this.mErrorView != null) {
            ViewCompat.setBackgroundTintList(this.mEditText, ColorStateList.valueOf(this.mErrorView.getCurrentTextColor()));
        } else if (this.mCounterOverflowed && this.mCounterView != null) {
            ViewCompat.setBackgroundTintList(this.mEditText, ColorStateList.valueOf(this.mCounterView.getCurrentTextColor()));
        } else {
            ViewCompat.setBackgroundTintList(this.mEditText, TintManager.get(getContext()).getTintList(R.drawable.abc_edit_text_material));
        }
    }

    @Nullable
    public CharSequence getError() {
        if (this.mErrorEnabled && this.mErrorView != null && this.mErrorView.getVisibility() == 0) {
            return this.mErrorView.getText();
        }
        return null;
    }

    public boolean isHintAnimationEnabled() {
        return this.mHintAnimationEnabled;
    }

    public void setHintAnimationEnabled(boolean z) {
        this.mHintAnimationEnabled = z;
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        super.draw(canvas);
        this.mCollapsingTextHelper.draw(canvas);
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.mEditText != null) {
            int left = this.mEditText.getLeft() + this.mEditText.getCompoundPaddingLeft();
            int right = this.mEditText.getRight() - this.mEditText.getCompoundPaddingRight();
            this.mCollapsingTextHelper.setExpandedBounds(left, this.mEditText.getTop() + this.mEditText.getCompoundPaddingTop(), right, this.mEditText.getBottom() - this.mEditText.getCompoundPaddingBottom());
            this.mCollapsingTextHelper.setCollapsedBounds(left, getPaddingTop(), right, (i4 - i2) - getPaddingBottom());
            this.mCollapsingTextHelper.recalculate();
        }
    }

    @Override // android.view.View
    public void refreshDrawableState() {
        super.refreshDrawableState();
        updateLabelVisibility(ViewCompat.isLaidOut(this));
    }

    private void collapseHint(boolean z) {
        if (this.mAnimator != null && this.mAnimator.isRunning()) {
            this.mAnimator.cancel();
        }
        if (z && this.mHintAnimationEnabled) {
            animateToExpansionFraction(1.0f);
        } else {
            this.mCollapsingTextHelper.setExpansionFraction(1.0f);
        }
    }

    private void expandHint(boolean z) {
        if (this.mAnimator != null && this.mAnimator.isRunning()) {
            this.mAnimator.cancel();
        }
        if (z && this.mHintAnimationEnabled) {
            animateToExpansionFraction(0.0f);
        } else {
            this.mCollapsingTextHelper.setExpansionFraction(0.0f);
        }
    }

    private void animateToExpansionFraction(float f) {
        if (this.mCollapsingTextHelper.getExpansionFraction() == f) {
            return;
        }
        if (this.mAnimator == null) {
            this.mAnimator = ViewUtils.createAnimator();
            this.mAnimator.setInterpolator(AnimationUtils.LINEAR_INTERPOLATOR);
            this.mAnimator.setDuration(200);
            this.mAnimator.setUpdateListener(new ValueAnimatorCompat.AnimatorUpdateListener() { // from class: android.support.design.widget.TextInputLayout.4
                @Override // android.support.design.widget.ValueAnimatorCompat.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimatorCompat valueAnimatorCompat) {
                    TextInputLayout.this.mCollapsingTextHelper.setExpansionFraction(valueAnimatorCompat.getAnimatedFloatValue());
                }
            });
        }
        this.mAnimator.setFloatValues(this.mCollapsingTextHelper.getExpansionFraction(), f);
        this.mAnimator.start();
    }

    private int getThemeAttrColor(int i) {
        TypedValue typedValue = new TypedValue();
        if (getContext().getTheme().resolveAttribute(i, typedValue, true)) {
            return typedValue.data;
        }
        return -65281;
    }

    /* loaded from: classes.dex */
    private class TextInputAccessibilityDelegate extends AccessibilityDelegateCompat {
        private TextInputAccessibilityDelegate() {
        }

        @Override // android.support.v4.view.AccessibilityDelegateCompat
        public void onInitializeAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
            super.onInitializeAccessibilityEvent(view, accessibilityEvent);
            accessibilityEvent.setClassName(TextInputLayout.class.getSimpleName());
        }

        @Override // android.support.v4.view.AccessibilityDelegateCompat
        public void onPopulateAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
            super.onPopulateAccessibilityEvent(view, accessibilityEvent);
            CharSequence text = TextInputLayout.this.mCollapsingTextHelper.getText();
            if (TextUtils.isEmpty(text)) {
                return;
            }
            accessibilityEvent.getText().add(text);
        }

        @Override // android.support.v4.view.AccessibilityDelegateCompat
        public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
            accessibilityNodeInfoCompat.setClassName(TextInputLayout.class.getSimpleName());
            CharSequence text = TextInputLayout.this.mCollapsingTextHelper.getText();
            if (!TextUtils.isEmpty(text)) {
                accessibilityNodeInfoCompat.setText(text);
            }
            if (TextInputLayout.this.mEditText != null) {
                accessibilityNodeInfoCompat.setLabelFor(TextInputLayout.this.mEditText);
            }
            CharSequence text2 = TextInputLayout.this.mErrorView != null ? TextInputLayout.this.mErrorView.getText() : null;
            if (TextUtils.isEmpty(text2)) {
                return;
            }
            accessibilityNodeInfoCompat.setContentInvalid(true);
            accessibilityNodeInfoCompat.setError(text2);
        }
    }

    private static boolean arrayContains(int[] iArr, int i) {
        for (int i2 : iArr) {
            if (i2 == i) {
                return true;
            }
        }
        return false;
    }
}
