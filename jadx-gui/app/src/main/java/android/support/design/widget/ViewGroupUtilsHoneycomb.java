package android.support.design.widget;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

/* loaded from: classes.dex */
class ViewGroupUtilsHoneycomb {
    private static final ThreadLocal<Matrix> sMatrix = new ThreadLocal<>();
    private static final ThreadLocal<RectF> sRectF = new ThreadLocal<>();
    private static final Matrix IDENTITY = new Matrix();

    ViewGroupUtilsHoneycomb() {
    }

    public static void offsetDescendantRect(ViewGroup viewGroup, View view, Rect rect) {
        Matrix matrix = sMatrix.get();
        if (matrix == null) {
            matrix = new Matrix();
            sMatrix.set(matrix);
        } else {
            matrix.set(IDENTITY);
        }
        offsetDescendantMatrix(viewGroup, view, matrix);
        RectF rectF = sRectF.get();
        if (rectF == null) {
            rectF = new RectF();
        }
        rectF.set(rect);
        matrix.mapRect(rectF);
        rect.set((int) (rectF.left + 0.5f), (int) (rectF.top + 0.5f), (int) (rectF.right + 0.5f), (int) (rectF.bottom + 0.5f));
    }

    static void offsetDescendantMatrix(ViewParent viewParent, View view, Matrix matrix) {
        Object parent = view.getParent();
        if ((parent instanceof View) && parent != viewParent) {
            offsetDescendantMatrix(viewParent, (View) parent, matrix);
            matrix.preTranslate(-r0.getScrollX(), -r0.getScrollY());
        }
        matrix.preTranslate(view.getLeft(), view.getTop());
        if (view.getMatrix().isIdentity()) {
            return;
        }
        matrix.preConcat(view.getMatrix());
    }
}
