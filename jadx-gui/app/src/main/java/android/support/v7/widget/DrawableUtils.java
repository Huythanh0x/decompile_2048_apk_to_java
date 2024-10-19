package android.support.v7.widget;

import android.graphics.Rect;
import android.os.Build;

/* loaded from: classes.dex */
class DrawableUtils {
    public static final Rect INSETS_NONE = new Rect();
    private static final String TAG = "DrawableUtils";
    private static Class<?> sInsetsClazz;

    static {
        if (Build.VERSION.SDK_INT >= 18) {
            try {
                sInsetsClazz = Class.forName("android.graphics.Insets");
            } catch (ClassNotFoundException unused) {
            }
        }
    }

    private DrawableUtils() {
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x0078  */
    /* JADX WARN: Removed duplicated region for block: B:25:0x0079 A[Catch: Exception -> 0x0098, TryCatch #0 {Exception -> 0x0098, blocks: (B:7:0x0004, B:9:0x001d, B:11:0x002c, B:21:0x0075, B:25:0x0079, B:27:0x0080, B:29:0x0087, B:31:0x008e, B:33:0x004c, B:36:0x0056, B:39:0x0060, B:42:0x006a), top: B:6:0x0004 }] */
    /* JADX WARN: Removed duplicated region for block: B:27:0x0080 A[Catch: Exception -> 0x0098, TryCatch #0 {Exception -> 0x0098, blocks: (B:7:0x0004, B:9:0x001d, B:11:0x002c, B:21:0x0075, B:25:0x0079, B:27:0x0080, B:29:0x0087, B:31:0x008e, B:33:0x004c, B:36:0x0056, B:39:0x0060, B:42:0x006a), top: B:6:0x0004 }] */
    /* JADX WARN: Removed duplicated region for block: B:29:0x0087 A[Catch: Exception -> 0x0098, TryCatch #0 {Exception -> 0x0098, blocks: (B:7:0x0004, B:9:0x001d, B:11:0x002c, B:21:0x0075, B:25:0x0079, B:27:0x0080, B:29:0x0087, B:31:0x008e, B:33:0x004c, B:36:0x0056, B:39:0x0060, B:42:0x006a), top: B:6:0x0004 }] */
    /* JADX WARN: Removed duplicated region for block: B:31:0x008e A[Catch: Exception -> 0x0098, TRY_LEAVE, TryCatch #0 {Exception -> 0x0098, blocks: (B:7:0x0004, B:9:0x001d, B:11:0x002c, B:21:0x0075, B:25:0x0079, B:27:0x0080, B:29:0x0087, B:31:0x008e, B:33:0x004c, B:36:0x0056, B:39:0x0060, B:42:0x006a), top: B:6:0x0004 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static android.graphics.Rect getOpticalBounds(android.graphics.drawable.Drawable r10) {
        /*
            java.lang.Class<?> r0 = android.support.v7.widget.DrawableUtils.sInsetsClazz
            if (r0 == 0) goto L9f
            android.graphics.drawable.Drawable r10 = android.support.v4.graphics.drawable.DrawableCompat.unwrap(r10)     // Catch: java.lang.Exception -> L98
            java.lang.Class r0 = r10.getClass()     // Catch: java.lang.Exception -> L98
            java.lang.String r1 = "getOpticalInsets"
            r2 = 0
            java.lang.Class[] r3 = new java.lang.Class[r2]     // Catch: java.lang.Exception -> L98
            java.lang.reflect.Method r0 = r0.getMethod(r1, r3)     // Catch: java.lang.Exception -> L98
            java.lang.Object[] r1 = new java.lang.Object[r2]     // Catch: java.lang.Exception -> L98
            java.lang.Object r10 = r0.invoke(r10, r1)     // Catch: java.lang.Exception -> L98
            if (r10 == 0) goto L9f
            android.graphics.Rect r0 = new android.graphics.Rect     // Catch: java.lang.Exception -> L98
            r0.<init>()     // Catch: java.lang.Exception -> L98
            java.lang.Class<?> r1 = android.support.v7.widget.DrawableUtils.sInsetsClazz     // Catch: java.lang.Exception -> L98
            java.lang.reflect.Field[] r1 = r1.getFields()     // Catch: java.lang.Exception -> L98
            int r3 = r1.length     // Catch: java.lang.Exception -> L98
            r4 = 0
        L2a:
            if (r4 >= r3) goto L97
            r5 = r1[r4]     // Catch: java.lang.Exception -> L98
            java.lang.String r6 = r5.getName()     // Catch: java.lang.Exception -> L98
            r7 = -1
            int r8 = r6.hashCode()     // Catch: java.lang.Exception -> L98
            r9 = -1383228885(0xffffffffad8d9a2b, float:-1.6098308E-11)
            if (r8 == r9) goto L6a
            r9 = 115029(0x1c155, float:1.6119E-40)
            if (r8 == r9) goto L60
            r9 = 3317767(0x32a007, float:4.649182E-39)
            if (r8 == r9) goto L56
            r9 = 108511772(0x677c21c, float:4.6598146E-35)
            if (r8 == r9) goto L4c
            goto L74
        L4c:
            java.lang.String r8 = "right"
            boolean r6 = r6.equals(r8)     // Catch: java.lang.Exception -> L98
            if (r6 == 0) goto L74
            r6 = 2
            goto L75
        L56:
            java.lang.String r8 = "left"
            boolean r6 = r6.equals(r8)     // Catch: java.lang.Exception -> L98
            if (r6 == 0) goto L74
            r6 = 0
            goto L75
        L60:
            java.lang.String r8 = "top"
            boolean r6 = r6.equals(r8)     // Catch: java.lang.Exception -> L98
            if (r6 == 0) goto L74
            r6 = 1
            goto L75
        L6a:
            java.lang.String r8 = "bottom"
            boolean r6 = r6.equals(r8)     // Catch: java.lang.Exception -> L98
            if (r6 == 0) goto L74
            r6 = 3
            goto L75
        L74:
            r6 = -1
        L75:
            switch(r6) {
                case 0: goto L8e;
                case 1: goto L87;
                case 2: goto L80;
                case 3: goto L79;
                default: goto L78;
            }     // Catch: java.lang.Exception -> L98
        L78:
            goto L94
        L79:
            int r5 = r5.getInt(r10)     // Catch: java.lang.Exception -> L98
            r0.bottom = r5     // Catch: java.lang.Exception -> L98
            goto L94
        L80:
            int r5 = r5.getInt(r10)     // Catch: java.lang.Exception -> L98
            r0.right = r5     // Catch: java.lang.Exception -> L98
            goto L94
        L87:
            int r5 = r5.getInt(r10)     // Catch: java.lang.Exception -> L98
            r0.top = r5     // Catch: java.lang.Exception -> L98
            goto L94
        L8e:
            int r5 = r5.getInt(r10)     // Catch: java.lang.Exception -> L98
            r0.left = r5     // Catch: java.lang.Exception -> L98
        L94:
            int r4 = r4 + 1
            goto L2a
        L97:
            return r0
        L98:
            java.lang.String r10 = "DrawableUtils"
            java.lang.String r0 = "Couldn't obtain the optical insets. Ignoring."
            android.util.Log.e(r10, r0)
        L9f:
            android.graphics.Rect r10 = android.support.v7.widget.DrawableUtils.INSETS_NONE
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.DrawableUtils.getOpticalBounds(android.graphics.drawable.Drawable):android.graphics.Rect");
    }
}
