package android.support.v4.graphics.drawable;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class DrawableCompatBase {
    DrawableCompatBase() {
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static void setTint(Drawable drawable, int i) {
        if (drawable instanceof DrawableWrapper) {
            ((DrawableWrapper) drawable).setTint(i);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static void setTintList(Drawable drawable, ColorStateList colorStateList) {
        if (drawable instanceof DrawableWrapper) {
            ((DrawableWrapper) drawable).setTintList(colorStateList);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static void setTintMode(Drawable drawable, PorterDuff.Mode mode) {
        if (drawable instanceof DrawableWrapper) {
            ((DrawableWrapper) drawable).setTintMode(mode);
        }
    }

    public static Drawable wrapForTinting(Drawable drawable) {
        return !(drawable instanceof DrawableWrapperDonut) ? new DrawableWrapperDonut(drawable) : drawable;
    }
}
