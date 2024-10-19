package android.support.v4.view;

import android.support.v4.view.LayoutInflaterCompatHC;
import android.view.LayoutInflater;

/* loaded from: classes.dex */
class LayoutInflaterCompatLollipop {
    LayoutInflaterCompatLollipop() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setFactory(LayoutInflater layoutInflater, LayoutInflaterFactory layoutInflaterFactory) {
        layoutInflater.setFactory2(layoutInflaterFactory != null ? new LayoutInflaterCompatHC.FactoryWrapperHC(layoutInflaterFactory) : null);
    }
}
