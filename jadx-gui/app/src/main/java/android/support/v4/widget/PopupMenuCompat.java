package android.support.v4.widget;

import android.os.Build;
import android.view.View;

/* loaded from: classes.dex */
public class PopupMenuCompat {
    static final PopupMenuImpl IMPL;

    /* loaded from: classes.dex */
    interface PopupMenuImpl {
        View.OnTouchListener getDragToOpenListener(Object obj);
    }

    /* loaded from: classes.dex */
    static class BasePopupMenuImpl implements PopupMenuImpl {
        @Override // android.support.v4.widget.PopupMenuCompat.PopupMenuImpl
        public View.OnTouchListener getDragToOpenListener(Object obj) {
            return null;
        }

        BasePopupMenuImpl() {
        }
    }

    /* loaded from: classes.dex */
    static class KitKatPopupMenuImpl extends BasePopupMenuImpl {
        KitKatPopupMenuImpl() {
        }

        @Override // android.support.v4.widget.PopupMenuCompat.BasePopupMenuImpl, android.support.v4.widget.PopupMenuCompat.PopupMenuImpl
        public View.OnTouchListener getDragToOpenListener(Object obj) {
            return PopupMenuCompatKitKat.getDragToOpenListener(obj);
        }
    }

    static {
        if (Build.VERSION.SDK_INT >= 19) {
            IMPL = new KitKatPopupMenuImpl();
        } else {
            IMPL = new BasePopupMenuImpl();
        }
    }

    private PopupMenuCompat() {
    }

    public static View.OnTouchListener getDragToOpenListener(Object obj) {
        return IMPL.getDragToOpenListener(obj);
    }
}
