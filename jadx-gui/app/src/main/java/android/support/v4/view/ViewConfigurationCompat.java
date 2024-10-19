package android.support.v4.view;

import android.os.Build;
import android.view.ViewConfiguration;

/* loaded from: classes.dex */
public class ViewConfigurationCompat {
    static final ViewConfigurationVersionImpl IMPL;

    /* loaded from: classes.dex */
    interface ViewConfigurationVersionImpl {
        int getScaledPagingTouchSlop(ViewConfiguration viewConfiguration);

        boolean hasPermanentMenuKey(ViewConfiguration viewConfiguration);
    }

    /* loaded from: classes.dex */
    static class BaseViewConfigurationVersionImpl implements ViewConfigurationVersionImpl {
        @Override // android.support.v4.view.ViewConfigurationCompat.ViewConfigurationVersionImpl
        public boolean hasPermanentMenuKey(ViewConfiguration viewConfiguration) {
            return true;
        }

        BaseViewConfigurationVersionImpl() {
        }

        @Override // android.support.v4.view.ViewConfigurationCompat.ViewConfigurationVersionImpl
        public int getScaledPagingTouchSlop(ViewConfiguration viewConfiguration) {
            return viewConfiguration.getScaledTouchSlop();
        }
    }

    /* loaded from: classes.dex */
    static class FroyoViewConfigurationVersionImpl extends BaseViewConfigurationVersionImpl {
        FroyoViewConfigurationVersionImpl() {
        }

        @Override // android.support.v4.view.ViewConfigurationCompat.BaseViewConfigurationVersionImpl, android.support.v4.view.ViewConfigurationCompat.ViewConfigurationVersionImpl
        public int getScaledPagingTouchSlop(ViewConfiguration viewConfiguration) {
            return ViewConfigurationCompatFroyo.getScaledPagingTouchSlop(viewConfiguration);
        }
    }

    /* loaded from: classes.dex */
    static class HoneycombViewConfigurationVersionImpl extends FroyoViewConfigurationVersionImpl {
        @Override // android.support.v4.view.ViewConfigurationCompat.BaseViewConfigurationVersionImpl, android.support.v4.view.ViewConfigurationCompat.ViewConfigurationVersionImpl
        public boolean hasPermanentMenuKey(ViewConfiguration viewConfiguration) {
            return false;
        }

        HoneycombViewConfigurationVersionImpl() {
        }
    }

    /* loaded from: classes.dex */
    static class IcsViewConfigurationVersionImpl extends HoneycombViewConfigurationVersionImpl {
        IcsViewConfigurationVersionImpl() {
        }

        @Override // android.support.v4.view.ViewConfigurationCompat.HoneycombViewConfigurationVersionImpl, android.support.v4.view.ViewConfigurationCompat.BaseViewConfigurationVersionImpl, android.support.v4.view.ViewConfigurationCompat.ViewConfigurationVersionImpl
        public boolean hasPermanentMenuKey(ViewConfiguration viewConfiguration) {
            return ViewConfigurationCompatICS.hasPermanentMenuKey(viewConfiguration);
        }
    }

    static {
        if (Build.VERSION.SDK_INT >= 14) {
            IMPL = new IcsViewConfigurationVersionImpl();
            return;
        }
        if (Build.VERSION.SDK_INT >= 11) {
            IMPL = new HoneycombViewConfigurationVersionImpl();
        } else if (Build.VERSION.SDK_INT >= 8) {
            IMPL = new FroyoViewConfigurationVersionImpl();
        } else {
            IMPL = new BaseViewConfigurationVersionImpl();
        }
    }

    public static int getScaledPagingTouchSlop(ViewConfiguration viewConfiguration) {
        return IMPL.getScaledPagingTouchSlop(viewConfiguration);
    }

    public static boolean hasPermanentMenuKey(ViewConfiguration viewConfiguration) {
        return IMPL.hasPermanentMenuKey(viewConfiguration);
    }
}
