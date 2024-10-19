package android.support.v4.graphics.drawable;

import android.content.res.Resources;
import android.graphics.Outline;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class DrawableWrapperLollipop extends DrawableWrapperKitKat {
    /* JADX INFO: Access modifiers changed from: package-private */
    public DrawableWrapperLollipop(Drawable drawable) {
        super(drawable);
    }

    @Override // android.graphics.drawable.Drawable
    public void setHotspot(float f, float f2) {
        this.mDrawable.setHotspot(f, f2);
    }

    @Override // android.graphics.drawable.Drawable
    public void setHotspotBounds(int i, int i2, int i3, int i4) {
        this.mDrawable.setHotspotBounds(i, i2, i3, i4);
    }

    @Override // android.graphics.drawable.Drawable
    public void getOutline(Outline outline) {
        this.mDrawable.getOutline(outline);
    }

    @Override // android.graphics.drawable.Drawable
    public void applyTheme(Resources.Theme theme) {
        this.mDrawable.applyTheme(theme);
    }

    @Override // android.graphics.drawable.Drawable
    public boolean canApplyTheme() {
        return this.mDrawable.canApplyTheme();
    }

    @Override // android.graphics.drawable.Drawable
    public Rect getDirtyBounds() {
        return this.mDrawable.getDirtyBounds();
    }
}
