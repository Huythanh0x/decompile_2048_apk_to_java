package android.support.design.widget;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import java.util.List;

/* loaded from: classes.dex */
abstract class HeaderScrollingViewBehavior extends ViewOffsetBehavior<View> {
    abstract View findFirstDependency(List<View> list);

    public HeaderScrollingViewBehavior() {
    }

    public HeaderScrollingViewBehavior(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // android.support.design.widget.CoordinatorLayout.Behavior
    public boolean onMeasureChild(CoordinatorLayout coordinatorLayout, View view, int i, int i2, int i3, int i4) {
        View findFirstDependency;
        int i5 = view.getLayoutParams().height;
        if (i5 == -1 || i5 == -2) {
            List<View> dependencies = coordinatorLayout.getDependencies(view);
            if (!dependencies.isEmpty() && (findFirstDependency = findFirstDependency(dependencies)) != null && ViewCompat.isLaidOut(findFirstDependency)) {
                if (ViewCompat.getFitsSystemWindows(findFirstDependency)) {
                    ViewCompat.setFitsSystemWindows(view, true);
                }
                int size = View.MeasureSpec.getSize(i3);
                if (size == 0) {
                    size = coordinatorLayout.getHeight();
                }
                coordinatorLayout.onMeasureChild(view, i, i2, View.MeasureSpec.makeMeasureSpec((size - findFirstDependency.getMeasuredHeight()) + getScrollRange(findFirstDependency), i5 == -1 ? 1073741824 : Integer.MIN_VALUE), i4);
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getScrollRange(View view) {
        return view.getMeasuredHeight();
    }
}
