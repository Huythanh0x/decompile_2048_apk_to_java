package android.support.v4.widget;

import android.util.Log;
import android.widget.TextView;
import java.lang.reflect.Field;

/* loaded from: classes.dex */
class TextViewCompatDonut {
    private static final int LINES = 1;
    private static final String LOG_TAG = "TextViewCompatDonut";
    private static Field sMaxModeField;
    private static boolean sMaxModeFieldFetched;
    private static Field sMaximumField;
    private static boolean sMaximumFieldFetched;
    private static Field sMinModeField;
    private static boolean sMinModeFieldFetched;
    private static Field sMinimumField;
    private static boolean sMinimumFieldFetched;

    TextViewCompatDonut() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getMaxLines(TextView textView) {
        if (!sMaxModeFieldFetched) {
            sMaxModeField = retrieveField("mMaxMode");
            sMaxModeFieldFetched = true;
        }
        if (sMaxModeField == null || retrieveIntFromField(sMaxModeField, textView) != 1) {
            return -1;
        }
        if (!sMaximumFieldFetched) {
            sMaximumField = retrieveField("mMaximum");
            sMaximumFieldFetched = true;
        }
        if (sMaximumField != null) {
            return retrieveIntFromField(sMaximumField, textView);
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getMinLines(TextView textView) {
        if (!sMinModeFieldFetched) {
            sMinModeField = retrieveField("mMinMode");
            sMinModeFieldFetched = true;
        }
        if (sMinModeField == null || retrieveIntFromField(sMinModeField, textView) != 1) {
            return -1;
        }
        if (!sMinimumFieldFetched) {
            sMinimumField = retrieveField("mMinimum");
            sMinimumFieldFetched = true;
        }
        if (sMinimumField != null) {
            return retrieveIntFromField(sMinimumField, textView);
        }
        return -1;
    }

    private static Field retrieveField(String str) {
        Field field;
        try {
            field = TextView.class.getDeclaredField(str);
        } catch (NoSuchFieldException unused) {
            field = null;
        }
        try {
            field.setAccessible(true);
        } catch (NoSuchFieldException unused2) {
            Log.e(LOG_TAG, "Could not retrieve " + str + " field.");
            return field;
        }
        return field;
    }

    private static int retrieveIntFromField(Field field, TextView textView) {
        try {
            return field.getInt(textView);
        } catch (IllegalAccessException unused) {
            Log.d(LOG_TAG, "Could not retrieve value of " + field.getName() + " field.");
            return -1;
        }
    }
}
