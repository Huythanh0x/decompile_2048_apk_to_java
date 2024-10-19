package android.support.v7.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TintableCompoundButton;
import android.support.v7.appcompat.R;
import android.util.AttributeSet;
import android.widget.RadioButton;

/* loaded from: classes.dex */
public class AppCompatRadioButton extends RadioButton implements TintableCompoundButton {
    private AppCompatCompoundButtonHelper mCompoundButtonHelper;
    private TintManager mTintManager;

    public AppCompatRadioButton(Context context) {
        this(context, null);
    }

    public AppCompatRadioButton(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.radioButtonStyle);
    }

    public AppCompatRadioButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mTintManager = TintManager.get(context);
        this.mCompoundButtonHelper = new AppCompatCompoundButtonHelper(this, this.mTintManager);
        this.mCompoundButtonHelper.loadFromAttributes(attributeSet, i);
    }

    @Override // android.widget.CompoundButton
    public void setButtonDrawable(Drawable drawable) {
        super.setButtonDrawable(drawable);
        if (this.mCompoundButtonHelper != null) {
            this.mCompoundButtonHelper.onSetButtonDrawable();
        }
    }

    @Override // android.widget.CompoundButton
    public void setButtonDrawable(@DrawableRes int i) {
        setButtonDrawable(this.mTintManager != null ? this.mTintManager.getDrawable(i) : ContextCompat.getDrawable(getContext(), i));
    }

    @Override // android.widget.CompoundButton, android.widget.TextView
    public int getCompoundPaddingLeft() {
        int compoundPaddingLeft = super.getCompoundPaddingLeft();
        return this.mCompoundButtonHelper != null ? this.mCompoundButtonHelper.getCompoundPaddingLeft(compoundPaddingLeft) : compoundPaddingLeft;
    }

    @Override // android.support.v4.widget.TintableCompoundButton
    public void setSupportButtonTintList(@Nullable ColorStateList colorStateList) {
        if (this.mCompoundButtonHelper != null) {
            this.mCompoundButtonHelper.setSupportButtonTintList(colorStateList);
        }
    }

    @Override // android.support.v4.widget.TintableCompoundButton
    @Nullable
    public ColorStateList getSupportButtonTintList() {
        if (this.mCompoundButtonHelper != null) {
            return this.mCompoundButtonHelper.getSupportButtonTintList();
        }
        return null;
    }

    @Override // android.support.v4.widget.TintableCompoundButton
    public void setSupportButtonTintMode(@Nullable PorterDuff.Mode mode) {
        if (this.mCompoundButtonHelper != null) {
            this.mCompoundButtonHelper.setSupportButtonTintMode(mode);
        }
    }

    @Override // android.support.v4.widget.TintableCompoundButton
    @Nullable
    public PorterDuff.Mode getSupportButtonTintMode() {
        if (this.mCompoundButtonHelper != null) {
            return this.mCompoundButtonHelper.getSupportButtonTintMode();
        }
        return null;
    }
}
