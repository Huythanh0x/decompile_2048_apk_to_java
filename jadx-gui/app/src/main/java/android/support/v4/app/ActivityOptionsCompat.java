package android.support.v4.app;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.view.View;

/* loaded from: classes.dex */
public class ActivityOptionsCompat {
    public Bundle toBundle() {
        return null;
    }

    public void update(ActivityOptionsCompat activityOptionsCompat) {
    }

    public static ActivityOptionsCompat makeCustomAnimation(Context context, int i, int i2) {
        if (Build.VERSION.SDK_INT >= 16) {
            return new ActivityOptionsImplJB(ActivityOptionsCompatJB.makeCustomAnimation(context, i, i2));
        }
        return new ActivityOptionsCompat();
    }

    public static ActivityOptionsCompat makeScaleUpAnimation(View view, int i, int i2, int i3, int i4) {
        if (Build.VERSION.SDK_INT >= 16) {
            return new ActivityOptionsImplJB(ActivityOptionsCompatJB.makeScaleUpAnimation(view, i, i2, i3, i4));
        }
        return new ActivityOptionsCompat();
    }

    public static ActivityOptionsCompat makeThumbnailScaleUpAnimation(View view, Bitmap bitmap, int i, int i2) {
        if (Build.VERSION.SDK_INT >= 16) {
            return new ActivityOptionsImplJB(ActivityOptionsCompatJB.makeThumbnailScaleUpAnimation(view, bitmap, i, i2));
        }
        return new ActivityOptionsCompat();
    }

    public static ActivityOptionsCompat makeSceneTransitionAnimation(Activity activity, View view, String str) {
        if (Build.VERSION.SDK_INT >= 21) {
            return new ActivityOptionsImpl21(ActivityOptionsCompat21.makeSceneTransitionAnimation(activity, view, str));
        }
        return new ActivityOptionsCompat();
    }

    public static ActivityOptionsCompat makeSceneTransitionAnimation(Activity activity, Pair<View, String>... pairArr) {
        String[] strArr;
        if (Build.VERSION.SDK_INT >= 21) {
            View[] viewArr = null;
            if (pairArr != null) {
                viewArr = new View[pairArr.length];
                strArr = new String[pairArr.length];
                for (int i = 0; i < pairArr.length; i++) {
                    viewArr[i] = pairArr[i].first;
                    strArr[i] = pairArr[i].second;
                }
            } else {
                strArr = null;
            }
            return new ActivityOptionsImpl21(ActivityOptionsCompat21.makeSceneTransitionAnimation(activity, viewArr, strArr));
        }
        return new ActivityOptionsCompat();
    }

    /* loaded from: classes.dex */
    private static class ActivityOptionsImplJB extends ActivityOptionsCompat {
        private final ActivityOptionsCompatJB mImpl;

        ActivityOptionsImplJB(ActivityOptionsCompatJB activityOptionsCompatJB) {
            this.mImpl = activityOptionsCompatJB;
        }

        @Override // android.support.v4.app.ActivityOptionsCompat
        public Bundle toBundle() {
            return this.mImpl.toBundle();
        }

        @Override // android.support.v4.app.ActivityOptionsCompat
        public void update(ActivityOptionsCompat activityOptionsCompat) {
            if (activityOptionsCompat instanceof ActivityOptionsImplJB) {
                this.mImpl.update(((ActivityOptionsImplJB) activityOptionsCompat).mImpl);
            }
        }
    }

    /* loaded from: classes.dex */
    private static class ActivityOptionsImpl21 extends ActivityOptionsCompat {
        private final ActivityOptionsCompat21 mImpl;

        ActivityOptionsImpl21(ActivityOptionsCompat21 activityOptionsCompat21) {
            this.mImpl = activityOptionsCompat21;
        }

        @Override // android.support.v4.app.ActivityOptionsCompat
        public Bundle toBundle() {
            return this.mImpl.toBundle();
        }

        @Override // android.support.v4.app.ActivityOptionsCompat
        public void update(ActivityOptionsCompat activityOptionsCompat) {
            if (activityOptionsCompat instanceof ActivityOptionsImpl21) {
                this.mImpl.update(((ActivityOptionsImpl21) activityOptionsCompat).mImpl);
            }
        }
    }

    protected ActivityOptionsCompat() {
    }
}
