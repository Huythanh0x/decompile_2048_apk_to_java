package android.support.v4.widget;

import android.widget.PopupWindow;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
class PopupWindowCompatGingerbread {
    private static Method sGetWindowLayoutTypeMethod;
    private static boolean sGetWindowLayoutTypeMethodAttempted;
    private static Method sSetWindowLayoutTypeMethod;
    private static boolean sSetWindowLayoutTypeMethodAttempted;

    PopupWindowCompatGingerbread() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setWindowLayoutType(PopupWindow popupWindow, int i) {
        if (!sSetWindowLayoutTypeMethodAttempted) {
            try {
                sSetWindowLayoutTypeMethod = PopupWindow.class.getDeclaredMethod("setWindowLayoutType", Integer.TYPE);
                sSetWindowLayoutTypeMethod.setAccessible(true);
            } catch (Exception unused) {
            }
            sSetWindowLayoutTypeMethodAttempted = true;
        }
        if (sSetWindowLayoutTypeMethod != null) {
            try {
                sSetWindowLayoutTypeMethod.invoke(popupWindow, Integer.valueOf(i));
            } catch (Exception unused2) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getWindowLayoutType(PopupWindow popupWindow) {
        if (!sGetWindowLayoutTypeMethodAttempted) {
            try {
                sGetWindowLayoutTypeMethod = PopupWindow.class.getDeclaredMethod("getWindowLayoutType", new Class[0]);
                sGetWindowLayoutTypeMethod.setAccessible(true);
            } catch (Exception unused) {
            }
            sGetWindowLayoutTypeMethodAttempted = true;
        }
        if (sGetWindowLayoutTypeMethod != null) {
            try {
                return ((Integer) sGetWindowLayoutTypeMethod.invoke(popupWindow, new Object[0])).intValue();
            } catch (Exception unused2) {
            }
        }
        return 0;
    }
}
