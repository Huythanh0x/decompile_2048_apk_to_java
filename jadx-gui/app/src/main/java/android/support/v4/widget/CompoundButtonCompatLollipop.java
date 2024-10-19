package android.support.v4.widget;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.widget.CompoundButton;

/* loaded from: classes.dex */
class CompoundButtonCompatLollipop {
    CompoundButtonCompatLollipop() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setButtonTintList(CompoundButton compoundButton, ColorStateList colorStateList) {
        compoundButton.setButtonTintList(colorStateList);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ColorStateList getButtonTintList(CompoundButton compoundButton) {
        return compoundButton.getButtonTintList();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setButtonTintMode(CompoundButton compoundButton, PorterDuff.Mode mode) {
        compoundButton.setButtonTintMode(mode);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static PorterDuff.Mode getButtonTintMode(CompoundButton compoundButton) {
        return compoundButton.getButtonTintMode();
    }
}
