package android.support.v4.graphics.drawable;

import android.graphics.drawable.Drawable;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class DrawableWrapperKitKat extends DrawableWrapperHoneycomb {
    /* JADX INFO: Access modifiers changed from: package-private */
    public DrawableWrapperKitKat(Drawable drawable) {
        super(drawable);
    }

    @Override // android.graphics.drawable.Drawable
    public void setAutoMirrored(boolean z) {
        this.mDrawable.setAutoMirrored(z);
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isAutoMirrored() {
        return this.mDrawable.isAutoMirrored();
    }
}
