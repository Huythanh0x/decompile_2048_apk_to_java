package android.support.v7.app;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.NotificationBuilderWithBuilderAccessor;
import android.support.v4.app.NotificationCompatBase;
import android.support.v7.appcompat.R;
import android.widget.RemoteViews;
import java.util.List;

/* loaded from: classes.dex */
class NotificationCompatImplBase {
    static final int MAX_MEDIA_BUTTONS = 5;
    static final int MAX_MEDIA_BUTTONS_IN_COMPACT = 3;

    NotificationCompatImplBase() {
    }

    public static <T extends NotificationCompatBase.Action> void overrideContentView(NotificationBuilderWithBuilderAccessor notificationBuilderWithBuilderAccessor, Context context, CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, int i, Bitmap bitmap, CharSequence charSequence4, boolean z, long j, List<T> list, int[] iArr, boolean z2, PendingIntent pendingIntent) {
        notificationBuilderWithBuilderAccessor.getBuilder().setContent(generateContentView(context, charSequence, charSequence2, charSequence3, i, bitmap, charSequence4, z, j, list, iArr, z2, pendingIntent));
        if (z2) {
            notificationBuilderWithBuilderAccessor.getBuilder().setOngoing(true);
        }
    }

    private static <T extends NotificationCompatBase.Action> RemoteViews generateContentView(Context context, CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, int i, Bitmap bitmap, CharSequence charSequence4, boolean z, long j, List<T> list, int[] iArr, boolean z2, PendingIntent pendingIntent) {
        RemoteViews applyStandardTemplate = applyStandardTemplate(context, charSequence, charSequence2, charSequence3, i, bitmap, charSequence4, z, j, R.layout.notification_template_media, true);
        int size = list.size();
        int min = iArr == null ? 0 : Math.min(iArr.length, 3);
        applyStandardTemplate.removeAllViews(R.id.media_actions);
        if (min > 0) {
            for (int i2 = 0; i2 < min; i2++) {
                if (i2 >= size) {
                    throw new IllegalArgumentException(String.format("setShowActionsInCompactView: action %d out of bounds (max %d)", Integer.valueOf(i2), Integer.valueOf(size - 1)));
                }
                applyStandardTemplate.addView(R.id.media_actions, generateMediaActionButton(context, list.get(iArr[i2])));
            }
        }
        if (z2) {
            applyStandardTemplate.setViewVisibility(R.id.end_padder, 8);
            applyStandardTemplate.setViewVisibility(R.id.cancel_action, 0);
            applyStandardTemplate.setOnClickPendingIntent(R.id.cancel_action, pendingIntent);
            applyStandardTemplate.setInt(R.id.cancel_action, "setAlpha", context.getResources().getInteger(R.integer.cancel_button_image_alpha));
        } else {
            applyStandardTemplate.setViewVisibility(R.id.end_padder, 0);
            applyStandardTemplate.setViewVisibility(R.id.cancel_action, 8);
        }
        return applyStandardTemplate;
    }

    public static <T extends NotificationCompatBase.Action> void overrideBigContentView(Notification notification, Context context, CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, int i, Bitmap bitmap, CharSequence charSequence4, boolean z, long j, List<T> list, boolean z2, PendingIntent pendingIntent) {
        notification.bigContentView = generateBigContentView(context, charSequence, charSequence2, charSequence3, i, bitmap, charSequence4, z, j, list, z2, pendingIntent);
        if (z2) {
            notification.flags |= 2;
        }
    }

    private static <T extends NotificationCompatBase.Action> RemoteViews generateBigContentView(Context context, CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, int i, Bitmap bitmap, CharSequence charSequence4, boolean z, long j, List<T> list, boolean z2, PendingIntent pendingIntent) {
        int min = Math.min(list.size(), 5);
        RemoteViews applyStandardTemplate = applyStandardTemplate(context, charSequence, charSequence2, charSequence3, i, bitmap, charSequence4, z, j, getBigLayoutResource(min), false);
        applyStandardTemplate.removeAllViews(R.id.media_actions);
        if (min > 0) {
            for (int i2 = 0; i2 < min; i2++) {
                applyStandardTemplate.addView(R.id.media_actions, generateMediaActionButton(context, list.get(i2)));
            }
        }
        if (z2) {
            applyStandardTemplate.setViewVisibility(R.id.cancel_action, 0);
            applyStandardTemplate.setInt(R.id.cancel_action, "setAlpha", context.getResources().getInteger(R.integer.cancel_button_image_alpha));
            applyStandardTemplate.setOnClickPendingIntent(R.id.cancel_action, pendingIntent);
        } else {
            applyStandardTemplate.setViewVisibility(R.id.cancel_action, 8);
        }
        return applyStandardTemplate;
    }

    private static RemoteViews generateMediaActionButton(Context context, NotificationCompatBase.Action action) {
        boolean z = action.getActionIntent() == null;
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_media_action);
        remoteViews.setImageViewResource(R.id.action0, action.getIcon());
        if (!z) {
            remoteViews.setOnClickPendingIntent(R.id.action0, action.getActionIntent());
        }
        if (Build.VERSION.SDK_INT >= 15) {
            remoteViews.setContentDescription(R.id.action0, action.getTitle());
        }
        return remoteViews;
    }

    private static int getBigLayoutResource(int i) {
        if (i <= 3) {
            return R.layout.notification_template_big_media_narrow;
        }
        return R.layout.notification_template_big_media;
    }

    /* JADX WARN: Removed duplicated region for block: B:19:0x0098  */
    /* JADX WARN: Removed duplicated region for block: B:21:0x00ac  */
    /* JADX WARN: Removed duplicated region for block: B:24:0x00b2  */
    /* JADX WARN: Removed duplicated region for block: B:28:0x00dd  */
    /* JADX WARN: Removed duplicated region for block: B:33:0x010d  */
    /* JADX WARN: Removed duplicated region for block: B:37:0x00a4  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static android.widget.RemoteViews applyStandardTemplate(android.content.Context r16, java.lang.CharSequence r17, java.lang.CharSequence r18, java.lang.CharSequence r19, int r20, android.graphics.Bitmap r21, java.lang.CharSequence r22, boolean r23, long r24, int r26, boolean r27) {
        /*
            Method dump skipped, instructions count: 274
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.app.NotificationCompatImplBase.applyStandardTemplate(android.content.Context, java.lang.CharSequence, java.lang.CharSequence, java.lang.CharSequence, int, android.graphics.Bitmap, java.lang.CharSequence, boolean, long, int, boolean):android.widget.RemoteViews");
    }
}
