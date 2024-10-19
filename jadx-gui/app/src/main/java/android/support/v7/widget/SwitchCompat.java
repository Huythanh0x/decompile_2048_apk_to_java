package android.support.v7.widget;

import android.R;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.text.AllCapsTransformationMethod;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.CompoundButton;

/* loaded from: classes.dex */
public class SwitchCompat extends CompoundButton {
    private static final String ACCESSIBILITY_EVENT_CLASS_NAME = "android.widget.Switch";
    private static final int[] CHECKED_STATE_SET = {R.attr.state_checked};
    private static final int MONOSPACE = 3;
    private static final int SANS = 1;
    private static final int SERIF = 2;
    private static final int THUMB_ANIMATION_DURATION = 250;
    private static final int TOUCH_MODE_DOWN = 1;
    private static final int TOUCH_MODE_DRAGGING = 2;
    private static final int TOUCH_MODE_IDLE = 0;
    private int mMinFlingVelocity;
    private Layout mOffLayout;
    private Layout mOnLayout;
    private ThumbAnimation mPositionAnimator;
    private boolean mShowText;
    private boolean mSplitTrack;
    private int mSwitchBottom;
    private int mSwitchHeight;
    private int mSwitchLeft;
    private int mSwitchMinWidth;
    private int mSwitchPadding;
    private int mSwitchRight;
    private int mSwitchTop;
    private TransformationMethod mSwitchTransformationMethod;
    private int mSwitchWidth;
    private final Rect mTempRect;
    private ColorStateList mTextColors;
    private CharSequence mTextOff;
    private CharSequence mTextOn;
    private TextPaint mTextPaint;
    private Drawable mThumbDrawable;
    private float mThumbPosition;
    private int mThumbTextPadding;
    private int mThumbWidth;
    private final TintManager mTintManager;
    private int mTouchMode;
    private int mTouchSlop;
    private float mTouchX;
    private float mTouchY;
    private Drawable mTrackDrawable;
    private VelocityTracker mVelocityTracker;

    private static float constrain(float f, float f2, float f3) {
        return f < f2 ? f2 : f > f3 ? f3 : f;
    }

    public SwitchCompat(Context context) {
        this(context, null);
    }

    public SwitchCompat(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, android.support.v7.appcompat.R.attr.switchStyle);
    }

    public SwitchCompat(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mVelocityTracker = VelocityTracker.obtain();
        this.mTempRect = new Rect();
        this.mTextPaint = new TextPaint(1);
        Resources resources = getResources();
        this.mTextPaint.density = resources.getDisplayMetrics().density;
        TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(context, attributeSet, android.support.v7.appcompat.R.styleable.SwitchCompat, i, 0);
        this.mThumbDrawable = obtainStyledAttributes.getDrawable(android.support.v7.appcompat.R.styleable.SwitchCompat_android_thumb);
        if (this.mThumbDrawable != null) {
            this.mThumbDrawable.setCallback(this);
        }
        this.mTrackDrawable = obtainStyledAttributes.getDrawable(android.support.v7.appcompat.R.styleable.SwitchCompat_track);
        if (this.mTrackDrawable != null) {
            this.mTrackDrawable.setCallback(this);
        }
        this.mTextOn = obtainStyledAttributes.getText(android.support.v7.appcompat.R.styleable.SwitchCompat_android_textOn);
        this.mTextOff = obtainStyledAttributes.getText(android.support.v7.appcompat.R.styleable.SwitchCompat_android_textOff);
        this.mShowText = obtainStyledAttributes.getBoolean(android.support.v7.appcompat.R.styleable.SwitchCompat_showText, true);
        this.mThumbTextPadding = obtainStyledAttributes.getDimensionPixelSize(android.support.v7.appcompat.R.styleable.SwitchCompat_thumbTextPadding, 0);
        this.mSwitchMinWidth = obtainStyledAttributes.getDimensionPixelSize(android.support.v7.appcompat.R.styleable.SwitchCompat_switchMinWidth, 0);
        this.mSwitchPadding = obtainStyledAttributes.getDimensionPixelSize(android.support.v7.appcompat.R.styleable.SwitchCompat_switchPadding, 0);
        this.mSplitTrack = obtainStyledAttributes.getBoolean(android.support.v7.appcompat.R.styleable.SwitchCompat_splitTrack, false);
        int resourceId = obtainStyledAttributes.getResourceId(android.support.v7.appcompat.R.styleable.SwitchCompat_switchTextAppearance, 0);
        if (resourceId != 0) {
            setSwitchTextAppearance(context, resourceId);
        }
        this.mTintManager = obtainStyledAttributes.getTintManager();
        obtainStyledAttributes.recycle();
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        this.mTouchSlop = viewConfiguration.getScaledTouchSlop();
        this.mMinFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        refreshDrawableState();
        setChecked(isChecked());
    }

    public void setSwitchTextAppearance(Context context, int i) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(i, android.support.v7.appcompat.R.styleable.TextAppearance);
        ColorStateList colorStateList = obtainStyledAttributes.getColorStateList(android.support.v7.appcompat.R.styleable.TextAppearance_android_textColor);
        if (colorStateList != null) {
            this.mTextColors = colorStateList;
        } else {
            this.mTextColors = getTextColors();
        }
        int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(android.support.v7.appcompat.R.styleable.TextAppearance_android_textSize, 0);
        if (dimensionPixelSize != 0) {
            float f = dimensionPixelSize;
            if (f != this.mTextPaint.getTextSize()) {
                this.mTextPaint.setTextSize(f);
                requestLayout();
            }
        }
        setSwitchTypefaceByIndex(obtainStyledAttributes.getInt(android.support.v7.appcompat.R.styleable.TextAppearance_android_typeface, -1), obtainStyledAttributes.getInt(android.support.v7.appcompat.R.styleable.TextAppearance_android_textStyle, -1));
        if (obtainStyledAttributes.getBoolean(android.support.v7.appcompat.R.styleable.TextAppearance_textAllCaps, false)) {
            this.mSwitchTransformationMethod = new AllCapsTransformationMethod(getContext());
        } else {
            this.mSwitchTransformationMethod = null;
        }
        obtainStyledAttributes.recycle();
    }

    private void setSwitchTypefaceByIndex(int i, int i2) {
        Typeface typeface;
        switch (i) {
            case 1:
                typeface = Typeface.SANS_SERIF;
                break;
            case 2:
                typeface = Typeface.SERIF;
                break;
            case 3:
                typeface = Typeface.MONOSPACE;
                break;
            default:
                typeface = null;
                break;
        }
        setSwitchTypeface(typeface, i2);
    }

    public void setSwitchTypeface(Typeface typeface, int i) {
        Typeface create;
        if (i > 0) {
            if (typeface == null) {
                create = Typeface.defaultFromStyle(i);
            } else {
                create = Typeface.create(typeface, i);
            }
            setSwitchTypeface(create);
            int style = ((create != null ? create.getStyle() : 0) ^ (-1)) & i;
            this.mTextPaint.setFakeBoldText((style & 1) != 0);
            this.mTextPaint.setTextSkewX((style & 2) != 0 ? -0.25f : 0.0f);
            return;
        }
        this.mTextPaint.setFakeBoldText(false);
        this.mTextPaint.setTextSkewX(0.0f);
        setSwitchTypeface(typeface);
    }

    public void setSwitchTypeface(Typeface typeface) {
        if (this.mTextPaint.getTypeface() != typeface) {
            this.mTextPaint.setTypeface(typeface);
            requestLayout();
            invalidate();
        }
    }

    public void setSwitchPadding(int i) {
        this.mSwitchPadding = i;
        requestLayout();
    }

    public int getSwitchPadding() {
        return this.mSwitchPadding;
    }

    public void setSwitchMinWidth(int i) {
        this.mSwitchMinWidth = i;
        requestLayout();
    }

    public int getSwitchMinWidth() {
        return this.mSwitchMinWidth;
    }

    public void setThumbTextPadding(int i) {
        this.mThumbTextPadding = i;
        requestLayout();
    }

    public int getThumbTextPadding() {
        return this.mThumbTextPadding;
    }

    public void setTrackDrawable(Drawable drawable) {
        this.mTrackDrawable = drawable;
        requestLayout();
    }

    public void setTrackResource(int i) {
        setTrackDrawable(this.mTintManager.getDrawable(i));
    }

    public Drawable getTrackDrawable() {
        return this.mTrackDrawable;
    }

    public void setThumbDrawable(Drawable drawable) {
        this.mThumbDrawable = drawable;
        requestLayout();
    }

    public void setThumbResource(int i) {
        setThumbDrawable(this.mTintManager.getDrawable(i));
    }

    public Drawable getThumbDrawable() {
        return this.mThumbDrawable;
    }

    public void setSplitTrack(boolean z) {
        this.mSplitTrack = z;
        invalidate();
    }

    public boolean getSplitTrack() {
        return this.mSplitTrack;
    }

    public CharSequence getTextOn() {
        return this.mTextOn;
    }

    public void setTextOn(CharSequence charSequence) {
        this.mTextOn = charSequence;
        requestLayout();
    }

    public CharSequence getTextOff() {
        return this.mTextOff;
    }

    public void setTextOff(CharSequence charSequence) {
        this.mTextOff = charSequence;
        requestLayout();
    }

    public void setShowText(boolean z) {
        if (this.mShowText != z) {
            this.mShowText = z;
            requestLayout();
        }
    }

    public boolean getShowText() {
        return this.mShowText;
    }

    @Override // android.widget.TextView, android.view.View
    public void onMeasure(int i, int i2) {
        int i3;
        int i4;
        if (this.mShowText) {
            if (this.mOnLayout == null) {
                this.mOnLayout = makeLayout(this.mTextOn);
            }
            if (this.mOffLayout == null) {
                this.mOffLayout = makeLayout(this.mTextOff);
            }
        }
        Rect rect = this.mTempRect;
        int i5 = 0;
        if (this.mThumbDrawable != null) {
            this.mThumbDrawable.getPadding(rect);
            i3 = (this.mThumbDrawable.getIntrinsicWidth() - rect.left) - rect.right;
            i4 = this.mThumbDrawable.getIntrinsicHeight();
        } else {
            i3 = 0;
            i4 = 0;
        }
        this.mThumbWidth = Math.max(this.mShowText ? Math.max(this.mOnLayout.getWidth(), this.mOffLayout.getWidth()) + (this.mThumbTextPadding * 2) : 0, i3);
        if (this.mTrackDrawable != null) {
            this.mTrackDrawable.getPadding(rect);
            i5 = this.mTrackDrawable.getIntrinsicHeight();
        } else {
            rect.setEmpty();
        }
        int i6 = rect.left;
        int i7 = rect.right;
        if (this.mThumbDrawable != null) {
            Rect opticalBounds = DrawableUtils.getOpticalBounds(this.mThumbDrawable);
            i6 = Math.max(i6, opticalBounds.left);
            i7 = Math.max(i7, opticalBounds.right);
        }
        int max = Math.max(this.mSwitchMinWidth, (this.mThumbWidth * 2) + i6 + i7);
        int max2 = Math.max(i5, i4);
        this.mSwitchWidth = max;
        this.mSwitchHeight = max2;
        super.onMeasure(i, i2);
        if (getMeasuredHeight() < max2) {
            setMeasuredDimension(ViewCompat.getMeasuredWidthAndState(this), max2);
        }
    }

    @Override // android.view.View
    @TargetApi(14)
    public void onPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onPopulateAccessibilityEvent(accessibilityEvent);
        CharSequence charSequence = isChecked() ? this.mTextOn : this.mTextOff;
        if (charSequence != null) {
            accessibilityEvent.getText().add(charSequence);
        }
    }

    private Layout makeLayout(CharSequence charSequence) {
        if (this.mSwitchTransformationMethod != null) {
            charSequence = this.mSwitchTransformationMethod.getTransformation(charSequence, this);
        }
        CharSequence charSequence2 = charSequence;
        return new StaticLayout(charSequence2, this.mTextPaint, charSequence2 != null ? (int) Math.ceil(Layout.getDesiredWidth(charSequence2, this.mTextPaint)) : 0, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
    }

    private boolean hitThumb(float f, float f2) {
        if (this.mThumbDrawable == null) {
            return false;
        }
        int thumbOffset = getThumbOffset();
        this.mThumbDrawable.getPadding(this.mTempRect);
        int i = this.mSwitchTop - this.mTouchSlop;
        int i2 = (this.mSwitchLeft + thumbOffset) - this.mTouchSlop;
        return f > ((float) i2) && f < ((float) ((((this.mThumbWidth + i2) + this.mTempRect.left) + this.mTempRect.right) + this.mTouchSlop)) && f2 > ((float) i) && f2 < ((float) (this.mSwitchBottom + this.mTouchSlop));
    }

    @Override // android.widget.TextView, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        this.mVelocityTracker.addMovement(motionEvent);
        switch (MotionEventCompat.getActionMasked(motionEvent)) {
            case 0:
                float x = motionEvent.getX();
                float y = motionEvent.getY();
                if (isEnabled() && hitThumb(x, y)) {
                    this.mTouchMode = 1;
                    this.mTouchX = x;
                    this.mTouchY = y;
                    break;
                }
                break;
            case 1:
            case 3:
                if (this.mTouchMode == 2) {
                    stopDrag(motionEvent);
                    super.onTouchEvent(motionEvent);
                    return true;
                }
                this.mTouchMode = 0;
                this.mVelocityTracker.clear();
                break;
            case 2:
                switch (this.mTouchMode) {
                    case 1:
                        float x2 = motionEvent.getX();
                        float y2 = motionEvent.getY();
                        if (Math.abs(x2 - this.mTouchX) > this.mTouchSlop || Math.abs(y2 - this.mTouchY) > this.mTouchSlop) {
                            this.mTouchMode = 2;
                            getParent().requestDisallowInterceptTouchEvent(true);
                            this.mTouchX = x2;
                            this.mTouchY = y2;
                            return true;
                        }
                        break;
                    case 2:
                        float x3 = motionEvent.getX();
                        int thumbScrollRange = getThumbScrollRange();
                        float f = x3 - this.mTouchX;
                        float f2 = thumbScrollRange != 0 ? f / thumbScrollRange : f > 0.0f ? 1.0f : -1.0f;
                        if (ViewUtils.isLayoutRtl(this)) {
                            f2 = -f2;
                        }
                        float constrain = constrain(this.mThumbPosition + f2, 0.0f, 1.0f);
                        if (constrain != this.mThumbPosition) {
                            this.mTouchX = x3;
                            setThumbPosition(constrain);
                        }
                        return true;
                }
        }
        return super.onTouchEvent(motionEvent);
    }

    private void cancelSuperTouch(MotionEvent motionEvent) {
        MotionEvent obtain = MotionEvent.obtain(motionEvent);
        obtain.setAction(3);
        super.onTouchEvent(obtain);
        obtain.recycle();
    }

    private void stopDrag(MotionEvent motionEvent) {
        boolean z;
        this.mTouchMode = 0;
        boolean z2 = true;
        boolean z3 = motionEvent.getAction() == 1 && isEnabled();
        boolean isChecked = isChecked();
        if (z3) {
            this.mVelocityTracker.computeCurrentVelocity(1000);
            float xVelocity = this.mVelocityTracker.getXVelocity();
            if (Math.abs(xVelocity) > this.mMinFlingVelocity) {
                if (!ViewUtils.isLayoutRtl(this) ? xVelocity <= 0.0f : xVelocity >= 0.0f) {
                    z2 = false;
                }
                z = z2;
            } else {
                z = getTargetCheckedState();
            }
        } else {
            z = isChecked;
        }
        if (z != isChecked) {
            playSoundEffect(0);
        }
        setChecked(z);
        cancelSuperTouch(motionEvent);
    }

    private void animateThumbToCheckedState(final boolean z) {
        if (this.mPositionAnimator != null) {
            cancelPositionAnimator();
        }
        this.mPositionAnimator = new ThumbAnimation(this.mThumbPosition, z ? 1.0f : 0.0f);
        this.mPositionAnimator.setDuration(250L);
        this.mPositionAnimator.setAnimationListener(new Animation.AnimationListener() { // from class: android.support.v7.widget.SwitchCompat.1
            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationRepeat(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationStart(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationEnd(Animation animation) {
                if (SwitchCompat.this.mPositionAnimator == animation) {
                    SwitchCompat.this.setThumbPosition(z ? 1.0f : 0.0f);
                    SwitchCompat.this.mPositionAnimator = null;
                }
            }
        });
        startAnimation(this.mPositionAnimator);
    }

    private void cancelPositionAnimator() {
        if (this.mPositionAnimator != null) {
            clearAnimation();
            this.mPositionAnimator = null;
        }
    }

    private boolean getTargetCheckedState() {
        return this.mThumbPosition > 0.5f;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setThumbPosition(float f) {
        this.mThumbPosition = f;
        invalidate();
    }

    @Override // android.widget.CompoundButton, android.widget.Checkable
    public void toggle() {
        setChecked(!isChecked());
    }

    @Override // android.widget.CompoundButton, android.widget.Checkable
    public void setChecked(boolean z) {
        super.setChecked(z);
        boolean isChecked = isChecked();
        if (getWindowToken() != null && ViewCompat.isLaidOut(this) && isShown()) {
            animateThumbToCheckedState(isChecked);
        } else {
            cancelPositionAnimator();
            setThumbPosition(isChecked ? 1.0f : 0.0f);
        }
    }

    @Override // android.widget.TextView, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        int width;
        int i6;
        int paddingTop;
        int i7;
        super.onLayout(z, i, i2, i3, i4);
        int i8 = 0;
        if (this.mThumbDrawable != null) {
            Rect rect = this.mTempRect;
            if (this.mTrackDrawable != null) {
                this.mTrackDrawable.getPadding(rect);
            } else {
                rect.setEmpty();
            }
            Rect opticalBounds = DrawableUtils.getOpticalBounds(this.mThumbDrawable);
            int max = Math.max(0, opticalBounds.left - rect.left);
            i5 = Math.max(0, opticalBounds.right - rect.right);
            i8 = max;
        } else {
            i5 = 0;
        }
        if (ViewUtils.isLayoutRtl(this)) {
            i6 = getPaddingLeft() + i8;
            width = ((this.mSwitchWidth + i6) - i8) - i5;
        } else {
            width = (getWidth() - getPaddingRight()) - i5;
            i6 = (width - this.mSwitchWidth) + i8 + i5;
        }
        int gravity = getGravity() & 112;
        if (gravity == 16) {
            paddingTop = (((getPaddingTop() + getHeight()) - getPaddingBottom()) / 2) - (this.mSwitchHeight / 2);
            i7 = this.mSwitchHeight + paddingTop;
        } else if (gravity != 80) {
            paddingTop = getPaddingTop();
            i7 = this.mSwitchHeight + paddingTop;
        } else {
            i7 = getHeight() - getPaddingBottom();
            paddingTop = i7 - this.mSwitchHeight;
        }
        this.mSwitchLeft = i6;
        this.mSwitchTop = paddingTop;
        this.mSwitchBottom = i7;
        this.mSwitchRight = width;
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        Rect rect;
        int i;
        int i2;
        Rect rect2 = this.mTempRect;
        int i3 = this.mSwitchLeft;
        int i4 = this.mSwitchTop;
        int i5 = this.mSwitchRight;
        int i6 = this.mSwitchBottom;
        int thumbOffset = getThumbOffset() + i3;
        if (this.mThumbDrawable != null) {
            rect = DrawableUtils.getOpticalBounds(this.mThumbDrawable);
        } else {
            rect = DrawableUtils.INSETS_NONE;
        }
        if (this.mTrackDrawable != null) {
            this.mTrackDrawable.getPadding(rect2);
            thumbOffset += rect2.left;
            if (rect != null) {
                if (rect.left > rect2.left) {
                    i3 += rect.left - rect2.left;
                }
                i = rect.top > rect2.top ? (rect.top - rect2.top) + i4 : i4;
                if (rect.right > rect2.right) {
                    i5 -= rect.right - rect2.right;
                }
                if (rect.bottom > rect2.bottom) {
                    i2 = i6 - (rect.bottom - rect2.bottom);
                    this.mTrackDrawable.setBounds(i3, i, i5, i2);
                }
            } else {
                i = i4;
            }
            i2 = i6;
            this.mTrackDrawable.setBounds(i3, i, i5, i2);
        }
        if (this.mThumbDrawable != null) {
            this.mThumbDrawable.getPadding(rect2);
            int i7 = thumbOffset - rect2.left;
            int i8 = thumbOffset + this.mThumbWidth + rect2.right;
            this.mThumbDrawable.setBounds(i7, i4, i8, i6);
            Drawable background = getBackground();
            if (background != null) {
                DrawableCompat.setHotspotBounds(background, i7, i4, i8, i6);
            }
        }
        super.draw(canvas);
    }

    @Override // android.widget.CompoundButton, android.widget.TextView, android.view.View
    protected void onDraw(Canvas canvas) {
        int width;
        super.onDraw(canvas);
        Rect rect = this.mTempRect;
        Drawable drawable = this.mTrackDrawable;
        if (drawable != null) {
            drawable.getPadding(rect);
        } else {
            rect.setEmpty();
        }
        int i = this.mSwitchTop;
        int i2 = this.mSwitchBottom;
        int i3 = i + rect.top;
        int i4 = i2 - rect.bottom;
        Drawable drawable2 = this.mThumbDrawable;
        if (drawable != null) {
            if (this.mSplitTrack && drawable2 != null) {
                Rect opticalBounds = DrawableUtils.getOpticalBounds(drawable2);
                drawable2.copyBounds(rect);
                rect.left += opticalBounds.left;
                rect.right -= opticalBounds.right;
                int save = canvas.save();
                canvas.clipRect(rect, Region.Op.DIFFERENCE);
                drawable.draw(canvas);
                canvas.restoreToCount(save);
            } else {
                drawable.draw(canvas);
            }
        }
        int save2 = canvas.save();
        if (drawable2 != null) {
            drawable2.draw(canvas);
        }
        Layout layout = getTargetCheckedState() ? this.mOnLayout : this.mOffLayout;
        if (layout != null) {
            int[] drawableState = getDrawableState();
            if (this.mTextColors != null) {
                this.mTextPaint.setColor(this.mTextColors.getColorForState(drawableState, 0));
            }
            this.mTextPaint.drawableState = drawableState;
            if (drawable2 != null) {
                Rect bounds = drawable2.getBounds();
                width = bounds.left + bounds.right;
            } else {
                width = getWidth();
            }
            canvas.translate((width / 2) - (layout.getWidth() / 2), ((i3 + i4) / 2) - (layout.getHeight() / 2));
            layout.draw(canvas);
        }
        canvas.restoreToCount(save2);
    }

    @Override // android.widget.CompoundButton, android.widget.TextView
    public int getCompoundPaddingLeft() {
        if (!ViewUtils.isLayoutRtl(this)) {
            return super.getCompoundPaddingLeft();
        }
        int compoundPaddingLeft = super.getCompoundPaddingLeft() + this.mSwitchWidth;
        return !TextUtils.isEmpty(getText()) ? compoundPaddingLeft + this.mSwitchPadding : compoundPaddingLeft;
    }

    @Override // android.widget.CompoundButton, android.widget.TextView
    public int getCompoundPaddingRight() {
        if (ViewUtils.isLayoutRtl(this)) {
            return super.getCompoundPaddingRight();
        }
        int compoundPaddingRight = super.getCompoundPaddingRight() + this.mSwitchWidth;
        return !TextUtils.isEmpty(getText()) ? compoundPaddingRight + this.mSwitchPadding : compoundPaddingRight;
    }

    private int getThumbOffset() {
        float f;
        if (ViewUtils.isLayoutRtl(this)) {
            f = 1.0f - this.mThumbPosition;
        } else {
            f = this.mThumbPosition;
        }
        return (int) ((f * getThumbScrollRange()) + 0.5f);
    }

    private int getThumbScrollRange() {
        Rect rect;
        if (this.mTrackDrawable == null) {
            return 0;
        }
        Rect rect2 = this.mTempRect;
        this.mTrackDrawable.getPadding(rect2);
        if (this.mThumbDrawable != null) {
            rect = DrawableUtils.getOpticalBounds(this.mThumbDrawable);
        } else {
            rect = DrawableUtils.INSETS_NONE;
        }
        return ((((this.mSwitchWidth - this.mThumbWidth) - rect2.left) - rect2.right) - rect.left) - rect.right;
    }

    @Override // android.widget.CompoundButton, android.widget.TextView, android.view.View
    protected int[] onCreateDrawableState(int i) {
        int[] onCreateDrawableState = super.onCreateDrawableState(i + 1);
        if (isChecked()) {
            mergeDrawableStates(onCreateDrawableState, CHECKED_STATE_SET);
        }
        return onCreateDrawableState;
    }

    @Override // android.widget.CompoundButton, android.widget.TextView, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        int[] drawableState = getDrawableState();
        if (this.mThumbDrawable != null) {
            this.mThumbDrawable.setState(drawableState);
        }
        if (this.mTrackDrawable != null) {
            this.mTrackDrawable.setState(drawableState);
        }
        invalidate();
    }

    @Override // android.widget.CompoundButton, android.widget.TextView, android.view.View
    public void drawableHotspotChanged(float f, float f2) {
        if (Build.VERSION.SDK_INT >= 21) {
            super.drawableHotspotChanged(f, f2);
        }
        if (this.mThumbDrawable != null) {
            DrawableCompat.setHotspot(this.mThumbDrawable, f, f2);
        }
        if (this.mTrackDrawable != null) {
            DrawableCompat.setHotspot(this.mTrackDrawable, f, f2);
        }
    }

    @Override // android.widget.CompoundButton, android.widget.TextView, android.view.View
    protected boolean verifyDrawable(Drawable drawable) {
        return super.verifyDrawable(drawable) || drawable == this.mThumbDrawable || drawable == this.mTrackDrawable;
    }

    @Override // android.widget.CompoundButton, android.widget.TextView, android.view.View
    public void jumpDrawablesToCurrentState() {
        if (Build.VERSION.SDK_INT >= 11) {
            super.jumpDrawablesToCurrentState();
            if (this.mThumbDrawable != null) {
                this.mThumbDrawable.jumpToCurrentState();
            }
            if (this.mTrackDrawable != null) {
                this.mTrackDrawable.jumpToCurrentState();
            }
            cancelPositionAnimator();
        }
    }

    @Override // android.view.View
    @TargetApi(14)
    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName(ACCESSIBILITY_EVENT_CLASS_NAME);
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        if (Build.VERSION.SDK_INT >= 14) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setClassName(ACCESSIBILITY_EVENT_CLASS_NAME);
            CharSequence charSequence = isChecked() ? this.mTextOn : this.mTextOff;
            if (TextUtils.isEmpty(charSequence)) {
                return;
            }
            CharSequence text = accessibilityNodeInfo.getText();
            if (TextUtils.isEmpty(text)) {
                accessibilityNodeInfo.setText(charSequence);
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(text);
            sb.append(' ');
            sb.append(charSequence);
            accessibilityNodeInfo.setText(sb);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ThumbAnimation extends Animation {
        final float mDiff;
        final float mEndPosition;
        final float mStartPosition;

        private ThumbAnimation(float f, float f2) {
            this.mStartPosition = f;
            this.mEndPosition = f2;
            this.mDiff = f2 - f;
        }

        @Override // android.view.animation.Animation
        protected void applyTransformation(float f, Transformation transformation) {
            SwitchCompat.this.setThumbPosition(this.mStartPosition + (this.mDiff * f));
        }
    }
}
