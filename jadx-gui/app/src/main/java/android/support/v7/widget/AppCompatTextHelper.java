package android.support.v7.widget;

import android.R;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.text.AllCapsTransformationMethod;
import android.util.AttributeSet;
import android.widget.TextView;

/* loaded from: classes.dex */
class AppCompatTextHelper {
    private TintInfo mDrawableBottomTint;
    private TintInfo mDrawableLeftTint;
    private TintInfo mDrawableRightTint;
    private TintInfo mDrawableTopTint;
    final TextView mView;
    private static final int[] VIEW_ATTRS = {R.attr.textAppearance, R.attr.drawableLeft, R.attr.drawableTop, R.attr.drawableRight, R.attr.drawableBottom};
    private static final int[] TEXT_APPEARANCE_ATTRS = {android.support.v7.appcompat.R.attr.textAllCaps};

    /* JADX INFO: Access modifiers changed from: package-private */
    public static AppCompatTextHelper create(TextView textView) {
        if (Build.VERSION.SDK_INT >= 17) {
            return new AppCompatTextHelperV17(textView);
        }
        return new AppCompatTextHelper(textView);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AppCompatTextHelper(TextView textView) {
        this.mView = textView;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void loadFromAttributes(AttributeSet attributeSet, int i) {
        Context context = this.mView.getContext();
        TintManager tintManager = TintManager.get(context);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, VIEW_ATTRS, i, 0);
        int resourceId = obtainStyledAttributes.getResourceId(0, -1);
        if (obtainStyledAttributes.hasValue(1)) {
            this.mDrawableLeftTint = createTintInfo(context, tintManager, obtainStyledAttributes.getResourceId(1, 0));
        }
        if (obtainStyledAttributes.hasValue(2)) {
            this.mDrawableTopTint = createTintInfo(context, tintManager, obtainStyledAttributes.getResourceId(2, 0));
        }
        if (obtainStyledAttributes.hasValue(3)) {
            this.mDrawableRightTint = createTintInfo(context, tintManager, obtainStyledAttributes.getResourceId(3, 0));
        }
        if (obtainStyledAttributes.hasValue(4)) {
            this.mDrawableBottomTint = createTintInfo(context, tintManager, obtainStyledAttributes.getResourceId(4, 0));
        }
        obtainStyledAttributes.recycle();
        if (resourceId != -1) {
            TypedArray obtainStyledAttributes2 = context.obtainStyledAttributes(resourceId, android.support.v7.appcompat.R.styleable.TextAppearance);
            if (obtainStyledAttributes2.hasValue(android.support.v7.appcompat.R.styleable.TextAppearance_textAllCaps)) {
                setAllCaps(obtainStyledAttributes2.getBoolean(android.support.v7.appcompat.R.styleable.TextAppearance_textAllCaps, false));
            }
            obtainStyledAttributes2.recycle();
        }
        TypedArray obtainStyledAttributes3 = context.obtainStyledAttributes(attributeSet, TEXT_APPEARANCE_ATTRS, i, 0);
        if (obtainStyledAttributes3.getBoolean(0, false)) {
            setAllCaps(true);
        }
        obtainStyledAttributes3.recycle();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onSetTextAppearance(Context context, int i) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(i, TEXT_APPEARANCE_ATTRS);
        if (obtainStyledAttributes.hasValue(0)) {
            setAllCaps(obtainStyledAttributes.getBoolean(0, false));
        }
        obtainStyledAttributes.recycle();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAllCaps(boolean z) {
        this.mView.setTransformationMethod(z ? new AllCapsTransformationMethod(this.mView.getContext()) : null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void applyCompoundDrawablesTints() {
        if (this.mDrawableLeftTint == null && this.mDrawableTopTint == null && this.mDrawableRightTint == null && this.mDrawableBottomTint == null) {
            return;
        }
        Drawable[] compoundDrawables = this.mView.getCompoundDrawables();
        applyCompoundDrawableTint(compoundDrawables[0], this.mDrawableLeftTint);
        applyCompoundDrawableTint(compoundDrawables[1], this.mDrawableTopTint);
        applyCompoundDrawableTint(compoundDrawables[2], this.mDrawableRightTint);
        applyCompoundDrawableTint(compoundDrawables[3], this.mDrawableBottomTint);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void applyCompoundDrawableTint(Drawable drawable, TintInfo tintInfo) {
        if (drawable == null || tintInfo == null) {
            return;
        }
        TintManager.tintDrawable(drawable, tintInfo, this.mView.getDrawableState());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static TintInfo createTintInfo(Context context, TintManager tintManager, int i) {
        ColorStateList tintList = tintManager.getTintList(i);
        if (tintList == null) {
            return null;
        }
        TintInfo tintInfo = new TintInfo();
        tintInfo.mHasTintList = true;
        tintInfo.mTintList = tintList;
        return null;
    }
}
