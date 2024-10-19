package android.support.v7.widget;

import android.R;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.SeekBar;

/* loaded from: classes.dex */
class AppCompatSeekBarHelper extends AppCompatProgressBarHelper {
    private static final int[] TINT_ATTRS = {R.attr.thumb};
    private final SeekBar mView;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AppCompatSeekBarHelper(SeekBar seekBar, TintManager tintManager) {
        super(seekBar, tintManager);
        this.mView = seekBar;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.v7.widget.AppCompatProgressBarHelper
    public void loadFromAttributes(AttributeSet attributeSet, int i) {
        super.loadFromAttributes(attributeSet, i);
        TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(this.mView.getContext(), attributeSet, TINT_ATTRS, i, 0);
        Drawable drawableIfKnown = obtainStyledAttributes.getDrawableIfKnown(0);
        if (drawableIfKnown != null) {
            this.mView.setThumb(drawableIfKnown);
        }
        obtainStyledAttributes.recycle();
    }
}
