package android.support.design.widget;

import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.view.View;

/* loaded from: classes.dex */
class CoordinatorLayoutInsetsHelperLollipop implements CoordinatorLayoutInsetsHelper {
    @Override // android.support.design.widget.CoordinatorLayoutInsetsHelper
    public void setupForWindowInsets(View view, OnApplyWindowInsetsListener onApplyWindowInsetsListener) {
        if (ViewCompat.getFitsSystemWindows(view)) {
            ViewCompat.setOnApplyWindowInsetsListener(view, onApplyWindowInsetsListener);
            view.setSystemUiVisibility(1280);
        }
    }
}
