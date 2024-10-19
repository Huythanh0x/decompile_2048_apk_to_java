package android.support.v4.app;

import android.app.Notification;
import android.app.NotificationManager;

/* loaded from: classes.dex */
class NotificationManagerCompatEclair {
    NotificationManagerCompatEclair() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void cancelNotification(NotificationManager notificationManager, String str, int i) {
        notificationManager.cancel(str, i);
    }

    public static void postNotification(NotificationManager notificationManager, String str, int i, Notification notification) {
        notificationManager.notify(str, i, notification);
    }
}
