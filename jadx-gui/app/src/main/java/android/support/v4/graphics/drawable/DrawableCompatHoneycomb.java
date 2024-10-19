package android.support.v4.graphics.drawable;

import android.graphics.drawable.Drawable;

/* loaded from: classes.dex */
class DrawableCompatHoneycomb {
    DrawableCompatHoneycomb() {
    }

    public static void jumpToCurrentState(Drawable drawable) {
        drawable.jumpToCurrentState();
    }

    public static Drawable wrapForTinting(Drawable drawable) {
        return !(drawable instanceof DrawableWrapperHoneycomb) ? new DrawableWrapperHoneycomb(drawable) : drawable;
    }
}
