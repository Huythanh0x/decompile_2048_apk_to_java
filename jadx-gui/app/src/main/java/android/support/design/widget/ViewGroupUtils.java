package android.support.design.widget;

import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

/* loaded from: classes.dex */
class ViewGroupUtils {
    private static final ViewGroupUtilsImpl IMPL;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public interface ViewGroupUtilsImpl {
        void offsetDescendantRect(ViewGroup viewGroup, View view, Rect rect);
    }

    ViewGroupUtils() {
    }

    /* loaded from: classes.dex */
    private static class ViewGroupUtilsImplBase implements ViewGroupUtilsImpl {
        private ViewGroupUtilsImplBase() {
        }

        @Override // android.support.design.widget.ViewGroupUtils.ViewGroupUtilsImpl
        public void offsetDescendantRect(ViewGroup viewGroup, View view, Rect rect) {
            viewGroup.offsetDescendantRectToMyCoords(view, rect);
        }
    }

    /* loaded from: classes.dex */
    private static class ViewGroupUtilsImplHoneycomb implements ViewGroupUtilsImpl {
        private ViewGroupUtilsImplHoneycomb() {
        }

        @Override // android.support.design.widget.ViewGroupUtils.ViewGroupUtilsImpl
        public void offsetDescendantRect(ViewGroup viewGroup, View view, Rect rect) {
            ViewGroupUtilsHoneycomb.offsetDescendantRect(viewGroup, view, rect);
        }
    }

    static {
        if (Build.VERSION.SDK_INT >= 11) {
            IMPL = new ViewGroupUtilsImplHoneycomb();
        } else {
            IMPL = new ViewGroupUtilsImplBase();
        }
    }

    static void offsetDescendantRect(ViewGroup viewGroup, View view, Rect rect) {
        IMPL.offsetDescendantRect(viewGroup, view, rect);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void getDescendantRect(ViewGroup viewGroup, View view, Rect rect) {
        rect.set(0, 0, view.getWidth(), view.getHeight());
        offsetDescendantRect(viewGroup, view, rect);
    }
}
