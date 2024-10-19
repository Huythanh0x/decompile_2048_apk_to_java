package android.support.v4.view;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.view.View;
import android.view.WindowInsets;

/* loaded from: classes.dex */
class ViewCompatLollipop {
    ViewCompatLollipop() {
    }

    public static void setTransitionName(View view, String str) {
        view.setTransitionName(str);
    }

    public static String getTransitionName(View view) {
        return view.getTransitionName();
    }

    public static void requestApplyInsets(View view) {
        view.requestApplyInsets();
    }

    public static void setElevation(View view, float f) {
        view.setElevation(f);
    }

    public static float getElevation(View view) {
        return view.getElevation();
    }

    public static void setTranslationZ(View view, float f) {
        view.setTranslationZ(f);
    }

    public static float getTranslationZ(View view) {
        return view.getTranslationZ();
    }

    public static void setOnApplyWindowInsetsListener(View view, final OnApplyWindowInsetsListener onApplyWindowInsetsListener) {
        view.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() { // from class: android.support.v4.view.ViewCompatLollipop.1
            @Override // android.view.View.OnApplyWindowInsetsListener
            public WindowInsets onApplyWindowInsets(View view2, WindowInsets windowInsets) {
                return ((WindowInsetsCompatApi21) OnApplyWindowInsetsListener.this.onApplyWindowInsets(view2, new WindowInsetsCompatApi21(windowInsets))).unwrap();
            }
        });
    }

    public static boolean isImportantForAccessibility(View view) {
        return view.isImportantForAccessibility();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ColorStateList getBackgroundTintList(View view) {
        return view.getBackgroundTintList();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setBackgroundTintList(View view, ColorStateList colorStateList) {
        view.setBackgroundTintList(colorStateList);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static PorterDuff.Mode getBackgroundTintMode(View view) {
        return view.getBackgroundTintMode();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setBackgroundTintMode(View view, PorterDuff.Mode mode) {
        view.setBackgroundTintMode(mode);
    }

    public static WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
        WindowInsets unwrap;
        WindowInsets onApplyWindowInsets;
        return (!(windowInsetsCompat instanceof WindowInsetsCompatApi21) || (onApplyWindowInsets = view.onApplyWindowInsets((unwrap = ((WindowInsetsCompatApi21) windowInsetsCompat).unwrap()))) == unwrap) ? windowInsetsCompat : new WindowInsetsCompatApi21(onApplyWindowInsets);
    }

    public static WindowInsetsCompat dispatchApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
        WindowInsets unwrap;
        WindowInsets dispatchApplyWindowInsets;
        return (!(windowInsetsCompat instanceof WindowInsetsCompatApi21) || (dispatchApplyWindowInsets = view.dispatchApplyWindowInsets((unwrap = ((WindowInsetsCompatApi21) windowInsetsCompat).unwrap()))) == unwrap) ? windowInsetsCompat : new WindowInsetsCompatApi21(dispatchApplyWindowInsets);
    }

    public static void setNestedScrollingEnabled(View view, boolean z) {
        view.setNestedScrollingEnabled(z);
    }

    public static boolean isNestedScrollingEnabled(View view) {
        return view.isNestedScrollingEnabled();
    }

    public static boolean startNestedScroll(View view, int i) {
        return view.startNestedScroll(i);
    }

    public static void stopNestedScroll(View view) {
        view.stopNestedScroll();
    }

    public static boolean hasNestedScrollingParent(View view) {
        return view.hasNestedScrollingParent();
    }

    public static boolean dispatchNestedScroll(View view, int i, int i2, int i3, int i4, int[] iArr) {
        return view.dispatchNestedScroll(i, i2, i3, i4, iArr);
    }

    public static boolean dispatchNestedPreScroll(View view, int i, int i2, int[] iArr, int[] iArr2) {
        return view.dispatchNestedPreScroll(i, i2, iArr, iArr2);
    }

    public static boolean dispatchNestedFling(View view, float f, float f2, boolean z) {
        return view.dispatchNestedFling(f, f2, z);
    }

    public static boolean dispatchNestedPreFling(View view, float f, float f2) {
        return view.dispatchNestedPreFling(f, f2);
    }

    public static float getZ(View view) {
        return view.getZ();
    }
}
