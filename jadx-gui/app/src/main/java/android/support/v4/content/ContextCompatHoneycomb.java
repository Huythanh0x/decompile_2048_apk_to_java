package android.support.v4.content;

import android.content.Context;
import android.content.Intent;
import java.io.File;

/* loaded from: classes.dex */
class ContextCompatHoneycomb {
    ContextCompatHoneycomb() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void startActivities(Context context, Intent[] intentArr) {
        context.startActivities(intentArr);
    }

    public static File getObbDir(Context context) {
        return context.getObbDir();
    }
}
