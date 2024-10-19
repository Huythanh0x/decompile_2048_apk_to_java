package android.support.v4.media.session;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.media.AudioManager;
import android.media.RemoteControlClient;
import android.os.SystemClock;
import android.support.v4.media.session.MediaSessionCompatApi14;
import android.util.Log;

/* loaded from: classes.dex */
class MediaSessionCompatApi18 {
    private static final long ACTION_SEEK_TO = 256;
    private static final String TAG = "MediaSessionCompatApi18";
    private static boolean sIsMbrPendingIntentSupported = true;

    MediaSessionCompatApi18() {
    }

    public static Object createPlaybackPositionUpdateListener(MediaSessionCompatApi14.Callback callback) {
        return new OnPlaybackPositionUpdateListener(callback);
    }

    public static void registerMediaButtonEventReceiver(Context context, PendingIntent pendingIntent, ComponentName componentName) {
        AudioManager audioManager = (AudioManager) context.getSystemService("audio");
        if (sIsMbrPendingIntentSupported) {
            try {
                audioManager.registerMediaButtonEventReceiver(pendingIntent);
            } catch (NullPointerException unused) {
                Log.w(TAG, "Unable to register media button event receiver with PendingIntent, falling back to ComponentName.");
                sIsMbrPendingIntentSupported = false;
            }
        }
        if (sIsMbrPendingIntentSupported) {
            return;
        }
        audioManager.registerMediaButtonEventReceiver(componentName);
    }

    public static void unregisterMediaButtonEventReceiver(Context context, PendingIntent pendingIntent, ComponentName componentName) {
        AudioManager audioManager = (AudioManager) context.getSystemService("audio");
        if (sIsMbrPendingIntentSupported) {
            audioManager.unregisterMediaButtonEventReceiver(pendingIntent);
        } else {
            audioManager.unregisterMediaButtonEventReceiver(componentName);
        }
    }

    public static void setState(Object obj, int i, long j, float f, long j2) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        if (i == 3) {
            long j3 = 0;
            if (j > 0) {
                if (j2 > 0) {
                    j3 = elapsedRealtime - j2;
                    if (f > 0.0f && f != 1.0f) {
                        j3 = ((float) j3) * f;
                    }
                }
                j += j3;
            }
        }
        ((RemoteControlClient) obj).setPlaybackState(MediaSessionCompatApi14.getRccStateFromState(i), j, f);
    }

    public static void setTransportControlFlags(Object obj, long j) {
        ((RemoteControlClient) obj).setTransportControlFlags(getRccTransportControlFlagsFromActions(j));
    }

    public static void setOnPlaybackPositionUpdateListener(Object obj, Object obj2) {
        ((RemoteControlClient) obj).setPlaybackPositionUpdateListener((RemoteControlClient.OnPlaybackPositionUpdateListener) obj2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getRccTransportControlFlagsFromActions(long j) {
        int rccTransportControlFlagsFromActions = MediaSessionCompatApi14.getRccTransportControlFlagsFromActions(j);
        return (j & 256) != 0 ? rccTransportControlFlagsFromActions | 256 : rccTransportControlFlagsFromActions;
    }

    /* loaded from: classes.dex */
    static class OnPlaybackPositionUpdateListener<T extends MediaSessionCompatApi14.Callback> implements RemoteControlClient.OnPlaybackPositionUpdateListener {
        protected final T mCallback;

        public OnPlaybackPositionUpdateListener(T t) {
            this.mCallback = t;
        }

        @Override // android.media.RemoteControlClient.OnPlaybackPositionUpdateListener
        public void onPlaybackPositionUpdate(long j) {
            this.mCallback.onSeekTo(j);
        }
    }
}
