package android.support.v7.widget;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

/* loaded from: classes.dex */
class TintContextWrapper extends ContextWrapper {
    private Resources mResources;

    public static Context wrap(Context context) {
        return !(context instanceof TintContextWrapper) ? new TintContextWrapper(context) : context;
    }

    private TintContextWrapper(Context context) {
        super(context);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public Resources getResources() {
        if (this.mResources == null) {
            this.mResources = new TintResources(super.getResources(), TintManager.get(this));
        }
        return this.mResources;
    }

    /* loaded from: classes.dex */
    static class TintResources extends ResourcesWrapper {
        private final TintManager mTintManager;

        public TintResources(Resources resources, TintManager tintManager) {
            super(resources);
            this.mTintManager = tintManager;
        }

        @Override // android.support.v7.widget.ResourcesWrapper, android.content.res.Resources
        public Drawable getDrawable(int i) throws Resources.NotFoundException {
            Drawable drawable = super.getDrawable(i);
            if (drawable != null) {
                this.mTintManager.tintDrawableUsingColorFilter(i, drawable);
            }
            return drawable;
        }
    }
}
