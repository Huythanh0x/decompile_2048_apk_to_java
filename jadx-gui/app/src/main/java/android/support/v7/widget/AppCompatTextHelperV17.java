package android.support.v7.widget;

import android.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class AppCompatTextHelperV17 extends AppCompatTextHelper {
    private static final int[] VIEW_ATTRS_v17 = {R.attr.drawableStart, R.attr.drawableEnd};
    private TintInfo mDrawableEndTint;
    private TintInfo mDrawableStartTint;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AppCompatTextHelperV17(TextView textView) {
        super(textView);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.v7.widget.AppCompatTextHelper
    public void loadFromAttributes(AttributeSet attributeSet, int i) {
        super.loadFromAttributes(attributeSet, i);
        Context context = this.mView.getContext();
        TintManager tintManager = TintManager.get(context);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, VIEW_ATTRS_v17, i, 0);
        if (obtainStyledAttributes.hasValue(0)) {
            this.mDrawableStartTint = createTintInfo(context, tintManager, obtainStyledAttributes.getResourceId(0, 0));
        }
        if (obtainStyledAttributes.hasValue(1)) {
            this.mDrawableEndTint = createTintInfo(context, tintManager, obtainStyledAttributes.getResourceId(1, 0));
        }
        obtainStyledAttributes.recycle();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.v7.widget.AppCompatTextHelper
    public void applyCompoundDrawablesTints() {
        super.applyCompoundDrawablesTints();
        if (this.mDrawableStartTint == null && this.mDrawableEndTint == null) {
            return;
        }
        Drawable[] compoundDrawablesRelative = this.mView.getCompoundDrawablesRelative();
        applyCompoundDrawableTint(compoundDrawablesRelative[0], this.mDrawableStartTint);
        applyCompoundDrawableTint(compoundDrawablesRelative[2], this.mDrawableEndTint);
    }
}
