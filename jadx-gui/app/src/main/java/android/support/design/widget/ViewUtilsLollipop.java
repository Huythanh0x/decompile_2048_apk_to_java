package android.support.design.widget;

import android.view.View;
import android.view.ViewOutlineProvider;

/* loaded from: classes.dex */
class ViewUtilsLollipop {
    ViewUtilsLollipop() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setBoundsViewOutlineProvider(View view) {
        view.setOutlineProvider(ViewOutlineProvider.BOUNDS);
    }
}
