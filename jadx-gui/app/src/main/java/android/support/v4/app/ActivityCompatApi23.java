package android.support.v4.app;

import android.app.Activity;

/* compiled from: ActivityCompat23.java */
/* loaded from: classes.dex */
class ActivityCompatApi23 {

    /* compiled from: ActivityCompat23.java */
    /* loaded from: classes.dex */
    public interface RequestPermissionsRequestCodeValidator {
        void validateRequestPermissionsRequestCode(int i);
    }

    ActivityCompatApi23() {
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static void requestPermissions(Activity activity, String[] strArr, int i) {
        if (activity instanceof RequestPermissionsRequestCodeValidator) {
            ((RequestPermissionsRequestCodeValidator) activity).validateRequestPermissionsRequestCode(i);
        }
        activity.requestPermissions(strArr, i);
    }

    public static boolean shouldShowRequestPermissionRationale(Activity activity, String str) {
        return activity.shouldShowRequestPermissionRationale(str);
    }
}
