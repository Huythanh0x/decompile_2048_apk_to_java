package android.support.v4.graphics.drawable;

import android.graphics.drawable.Drawable;

/* loaded from: classes.dex */
class DrawableWrapperHoneycomb extends DrawableWrapperDonut {
    /* JADX INFO: Access modifiers changed from: package-private */
    public DrawableWrapperHoneycomb(Drawable drawable) {
        super(drawable);
    }

    @Override // android.graphics.drawable.Drawable
    public void jumpToCurrentState() {
        this.mDrawable.jumpToCurrentState();
    }
}
