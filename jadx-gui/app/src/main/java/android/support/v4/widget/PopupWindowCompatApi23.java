package android.support.v4.widget;

import android.widget.PopupWindow;

/* loaded from: classes.dex */
class PopupWindowCompatApi23 {
    PopupWindowCompatApi23() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setOverlapAnchor(PopupWindow popupWindow, boolean z) {
        popupWindow.setOverlapAnchor(z);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean getOverlapAnchor(PopupWindow popupWindow) {
        return popupWindow.getOverlapAnchor();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setWindowLayoutType(PopupWindow popupWindow, int i) {
        popupWindow.setWindowLayoutType(i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getWindowLayoutType(PopupWindow popupWindow) {
        return popupWindow.getWindowLayoutType();
    }
}
