package android.support.v4.graphics.drawable;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;

/* loaded from: classes.dex */
public class DrawableCompat {
    static final DrawableImpl IMPL;

    /* loaded from: classes.dex */
    interface DrawableImpl {
        int getLayoutDirection(Drawable drawable);

        boolean isAutoMirrored(Drawable drawable);

        void jumpToCurrentState(Drawable drawable);

        void setAutoMirrored(Drawable drawable, boolean z);

        void setHotspot(Drawable drawable, float f, float f2);

        void setHotspotBounds(Drawable drawable, int i, int i2, int i3, int i4);

        void setLayoutDirection(Drawable drawable, int i);

        void setTint(Drawable drawable, int i);

        void setTintList(Drawable drawable, ColorStateList colorStateList);

        void setTintMode(Drawable drawable, PorterDuff.Mode mode);

        Drawable wrap(Drawable drawable);
    }

    /* loaded from: classes.dex */
    static class BaseDrawableImpl implements DrawableImpl {
        @Override // android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public int getLayoutDirection(Drawable drawable) {
            return 0;
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public boolean isAutoMirrored(Drawable drawable) {
            return false;
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public void jumpToCurrentState(Drawable drawable) {
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public void setAutoMirrored(Drawable drawable, boolean z) {
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public void setHotspot(Drawable drawable, float f, float f2) {
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public void setHotspotBounds(Drawable drawable, int i, int i2, int i3, int i4) {
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public void setLayoutDirection(Drawable drawable, int i) {
        }

        BaseDrawableImpl() {
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public void setTint(Drawable drawable, int i) {
            DrawableCompatBase.setTint(drawable, i);
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public void setTintList(Drawable drawable, ColorStateList colorStateList) {
            DrawableCompatBase.setTintList(drawable, colorStateList);
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public void setTintMode(Drawable drawable, PorterDuff.Mode mode) {
            DrawableCompatBase.setTintMode(drawable, mode);
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public Drawable wrap(Drawable drawable) {
            return DrawableCompatBase.wrapForTinting(drawable);
        }
    }

    /* loaded from: classes.dex */
    static class HoneycombDrawableImpl extends BaseDrawableImpl {
        HoneycombDrawableImpl() {
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.BaseDrawableImpl, android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public void jumpToCurrentState(Drawable drawable) {
            DrawableCompatHoneycomb.jumpToCurrentState(drawable);
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.BaseDrawableImpl, android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public Drawable wrap(Drawable drawable) {
            return DrawableCompatHoneycomb.wrapForTinting(drawable);
        }
    }

    /* loaded from: classes.dex */
    static class JellybeanMr1DrawableImpl extends HoneycombDrawableImpl {
        JellybeanMr1DrawableImpl() {
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.BaseDrawableImpl, android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public void setLayoutDirection(Drawable drawable, int i) {
            DrawableCompatJellybeanMr1.setLayoutDirection(drawable, i);
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.BaseDrawableImpl, android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public int getLayoutDirection(Drawable drawable) {
            int layoutDirection = DrawableCompatJellybeanMr1.getLayoutDirection(drawable);
            if (layoutDirection >= 0) {
                return layoutDirection;
            }
            return 0;
        }
    }

    /* loaded from: classes.dex */
    static class KitKatDrawableImpl extends JellybeanMr1DrawableImpl {
        KitKatDrawableImpl() {
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.BaseDrawableImpl, android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public void setAutoMirrored(Drawable drawable, boolean z) {
            DrawableCompatKitKat.setAutoMirrored(drawable, z);
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.BaseDrawableImpl, android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public boolean isAutoMirrored(Drawable drawable) {
            return DrawableCompatKitKat.isAutoMirrored(drawable);
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.HoneycombDrawableImpl, android.support.v4.graphics.drawable.DrawableCompat.BaseDrawableImpl, android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public Drawable wrap(Drawable drawable) {
            return DrawableCompatKitKat.wrapForTinting(drawable);
        }
    }

    /* loaded from: classes.dex */
    static class LollipopDrawableImpl extends KitKatDrawableImpl {
        LollipopDrawableImpl() {
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.BaseDrawableImpl, android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public void setHotspot(Drawable drawable, float f, float f2) {
            DrawableCompatLollipop.setHotspot(drawable, f, f2);
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.BaseDrawableImpl, android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public void setHotspotBounds(Drawable drawable, int i, int i2, int i3, int i4) {
            DrawableCompatLollipop.setHotspotBounds(drawable, i, i2, i3, i4);
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.BaseDrawableImpl, android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public void setTint(Drawable drawable, int i) {
            DrawableCompatLollipop.setTint(drawable, i);
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.BaseDrawableImpl, android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public void setTintList(Drawable drawable, ColorStateList colorStateList) {
            DrawableCompatLollipop.setTintList(drawable, colorStateList);
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.BaseDrawableImpl, android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public void setTintMode(Drawable drawable, PorterDuff.Mode mode) {
            DrawableCompatLollipop.setTintMode(drawable, mode);
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.KitKatDrawableImpl, android.support.v4.graphics.drawable.DrawableCompat.HoneycombDrawableImpl, android.support.v4.graphics.drawable.DrawableCompat.BaseDrawableImpl, android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public Drawable wrap(Drawable drawable) {
            return DrawableCompatLollipop.wrapForTinting(drawable);
        }
    }

    /* loaded from: classes.dex */
    static class LollipopMr1DrawableImpl extends LollipopDrawableImpl {
        LollipopMr1DrawableImpl() {
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.LollipopDrawableImpl, android.support.v4.graphics.drawable.DrawableCompat.KitKatDrawableImpl, android.support.v4.graphics.drawable.DrawableCompat.HoneycombDrawableImpl, android.support.v4.graphics.drawable.DrawableCompat.BaseDrawableImpl, android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public Drawable wrap(Drawable drawable) {
            return DrawableCompatApi22.wrapForTinting(drawable);
        }
    }

    /* loaded from: classes.dex */
    static class MDrawableImpl extends LollipopMr1DrawableImpl {
        MDrawableImpl() {
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.JellybeanMr1DrawableImpl, android.support.v4.graphics.drawable.DrawableCompat.BaseDrawableImpl, android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public void setLayoutDirection(Drawable drawable, int i) {
            DrawableCompatApi23.setLayoutDirection(drawable, i);
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.JellybeanMr1DrawableImpl, android.support.v4.graphics.drawable.DrawableCompat.BaseDrawableImpl, android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public int getLayoutDirection(Drawable drawable) {
            return DrawableCompatApi23.getLayoutDirection(drawable);
        }
    }

    static {
        int i = Build.VERSION.SDK_INT;
        if (i >= 23) {
            IMPL = new MDrawableImpl();
            return;
        }
        if (i >= 22) {
            IMPL = new LollipopMr1DrawableImpl();
            return;
        }
        if (i >= 21) {
            IMPL = new LollipopDrawableImpl();
            return;
        }
        if (i >= 19) {
            IMPL = new KitKatDrawableImpl();
            return;
        }
        if (i >= 17) {
            IMPL = new JellybeanMr1DrawableImpl();
        } else if (i >= 11) {
            IMPL = new HoneycombDrawableImpl();
        } else {
            IMPL = new BaseDrawableImpl();
        }
    }

    public static void jumpToCurrentState(Drawable drawable) {
        IMPL.jumpToCurrentState(drawable);
    }

    public static void setAutoMirrored(Drawable drawable, boolean z) {
        IMPL.setAutoMirrored(drawable, z);
    }

    public static boolean isAutoMirrored(Drawable drawable) {
        return IMPL.isAutoMirrored(drawable);
    }

    public static void setHotspot(Drawable drawable, float f, float f2) {
        IMPL.setHotspot(drawable, f, f2);
    }

    public static void setHotspotBounds(Drawable drawable, int i, int i2, int i3, int i4) {
        IMPL.setHotspotBounds(drawable, i, i2, i3, i4);
    }

    public static void setTint(Drawable drawable, int i) {
        IMPL.setTint(drawable, i);
    }

    public static void setTintList(Drawable drawable, ColorStateList colorStateList) {
        IMPL.setTintList(drawable, colorStateList);
    }

    public static void setTintMode(Drawable drawable, PorterDuff.Mode mode) {
        IMPL.setTintMode(drawable, mode);
    }

    public static Drawable wrap(Drawable drawable) {
        return IMPL.wrap(drawable);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <T extends Drawable> T unwrap(Drawable drawable) {
        return drawable instanceof DrawableWrapper ? (T) ((DrawableWrapper) drawable).getWrappedDrawable() : drawable;
    }

    public static void setLayoutDirection(Drawable drawable, int i) {
        IMPL.setLayoutDirection(drawable, i);
    }

    public static int getLayoutDirection(Drawable drawable) {
        return IMPL.getLayoutDirection(drawable);
    }
}
