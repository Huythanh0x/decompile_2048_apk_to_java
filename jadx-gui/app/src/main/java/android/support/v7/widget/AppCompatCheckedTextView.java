package android.support.v7.widget;

import android.R;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.widget.CheckedTextView;

/* loaded from: classes.dex */
public class AppCompatCheckedTextView extends CheckedTextView {
    private static final int[] TINT_ATTRS = {R.attr.checkMark};
    private AppCompatTextHelper mTextHelper;
    private TintManager mTintManager;

    public AppCompatCheckedTextView(Context context) {
        this(context, null);
    }

    public AppCompatCheckedTextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.checkedTextViewStyle);
    }

    public AppCompatCheckedTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mTextHelper = AppCompatTextHelper.create(this);
        this.mTextHelper.loadFromAttributes(attributeSet, i);
        this.mTextHelper.applyCompoundDrawablesTints();
        if (TintManager.SHOULD_BE_USED) {
            TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(getContext(), attributeSet, TINT_ATTRS, i, 0);
            setCheckMarkDrawable(obtainStyledAttributes.getDrawable(0));
            obtainStyledAttributes.recycle();
            this.mTintManager = obtainStyledAttributes.getTintManager();
        }
    }

    @Override // android.widget.CheckedTextView
    public void setCheckMarkDrawable(@DrawableRes int i) {
        if (this.mTintManager != null) {
            setCheckMarkDrawable(this.mTintManager.getDrawable(i));
        } else {
            super.setCheckMarkDrawable(i);
        }
    }

    @Override // android.widget.TextView
    public void setTextAppearance(Context context, int i) {
        super.setTextAppearance(context, i);
        if (this.mTextHelper != null) {
            this.mTextHelper.onSetTextAppearance(context, i);
        }
    }

    @Override // android.widget.CheckedTextView, android.widget.TextView, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (this.mTextHelper != null) {
            this.mTextHelper.applyCompoundDrawablesTints();
        }
    }
}
