package android.support.v7.widget;

import android.R;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ImageView;

/* loaded from: classes.dex */
class AppCompatImageHelper {
    private static final int[] VIEW_ATTRS = {R.attr.src};
    private final TintManager mTintManager;
    private final ImageView mView;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AppCompatImageHelper(ImageView imageView, TintManager tintManager) {
        this.mView = imageView;
        this.mTintManager = tintManager;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void loadFromAttributes(AttributeSet attributeSet, int i) {
        TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(this.mView.getContext(), attributeSet, VIEW_ATTRS, i, 0);
        try {
            if (obtainStyledAttributes.hasValue(0)) {
                this.mView.setImageDrawable(obtainStyledAttributes.getDrawable(0));
            }
        } finally {
            obtainStyledAttributes.recycle();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setImageResource(int i) {
        if (i != 0) {
            this.mView.setImageDrawable(this.mTintManager != null ? this.mTintManager.getDrawable(i) : ContextCompat.getDrawable(this.mView.getContext(), i));
        } else {
            this.mView.setImageDrawable(null);
        }
    }
}
