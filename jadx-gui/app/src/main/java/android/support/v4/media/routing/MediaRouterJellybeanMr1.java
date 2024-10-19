package android.support.v4.media.routing;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.media.MediaRouter;
import android.os.Build;
import android.os.Handler;
import android.support.v4.media.routing.MediaRouterJellybean;
import android.util.Log;
import android.view.Display;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
class MediaRouterJellybeanMr1 extends MediaRouterJellybean {
    private static final String TAG = "MediaRouterJellybeanMr1";

    /* loaded from: classes.dex */
    public interface Callback extends MediaRouterJellybean.Callback {
        void onRoutePresentationDisplayChanged(Object obj);
    }

    public static Object createCallback(Callback callback) {
        return new CallbackProxy(callback);
    }

    /* loaded from: classes.dex */
    public static final class RouteInfo {
        public static boolean isEnabled(Object obj) {
            return ((MediaRouter.RouteInfo) obj).isEnabled();
        }

        public static Display getPresentationDisplay(Object obj) {
            return ((MediaRouter.RouteInfo) obj).getPresentationDisplay();
        }
    }

    /* loaded from: classes.dex */
    public static final class ActiveScanWorkaround implements Runnable {
        private static final int WIFI_DISPLAY_SCAN_INTERVAL = 15000;
        private boolean mActivelyScanningWifiDisplays;
        private final DisplayManager mDisplayManager;
        private final Handler mHandler;
        private Method mScanWifiDisplaysMethod;

        public ActiveScanWorkaround(Context context, Handler handler) {
            if (Build.VERSION.SDK_INT != 17) {
                throw new UnsupportedOperationException();
            }
            this.mDisplayManager = (DisplayManager) context.getSystemService("display");
            this.mHandler = handler;
            try {
                this.mScanWifiDisplaysMethod = DisplayManager.class.getMethod("scanWifiDisplays", new Class[0]);
            } catch (NoSuchMethodException unused) {
            }
        }

        public void setActiveScanRouteTypes(int i) {
            if ((i & 2) != 0) {
                if (this.mActivelyScanningWifiDisplays) {
                    return;
                }
                if (this.mScanWifiDisplaysMethod != null) {
                    this.mActivelyScanningWifiDisplays = true;
                    this.mHandler.post(this);
                    return;
                } else {
                    Log.w(MediaRouterJellybeanMr1.TAG, "Cannot scan for wifi displays because the DisplayManager.scanWifiDisplays() method is not available on this device.");
                    return;
                }
            }
            if (this.mActivelyScanningWifiDisplays) {
                this.mActivelyScanningWifiDisplays = false;
                this.mHandler.removeCallbacks(this);
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.mActivelyScanningWifiDisplays) {
                try {
                    this.mScanWifiDisplaysMethod.invoke(this.mDisplayManager, new Object[0]);
                } catch (IllegalAccessException e) {
                    Log.w(MediaRouterJellybeanMr1.TAG, "Cannot scan for wifi displays.", e);
                } catch (InvocationTargetException e2) {
                    Log.w(MediaRouterJellybeanMr1.TAG, "Cannot scan for wifi displays.", e2);
                }
                this.mHandler.postDelayed(this, 15000L);
            }
        }
    }

    /* loaded from: classes.dex */
    public static final class IsConnectingWorkaround {
        private Method mGetStatusCodeMethod;
        private int mStatusConnecting;

        public IsConnectingWorkaround() {
            if (Build.VERSION.SDK_INT != 17) {
                throw new UnsupportedOperationException();
            }
            try {
                this.mStatusConnecting = MediaRouter.RouteInfo.class.getField("STATUS_CONNECTING").getInt(null);
                this.mGetStatusCodeMethod = MediaRouter.RouteInfo.class.getMethod("getStatusCode", new Class[0]);
            } catch (IllegalAccessException | NoSuchFieldException | NoSuchMethodException unused) {
            }
        }

        public boolean isConnecting(Object obj) {
            MediaRouter.RouteInfo routeInfo = (MediaRouter.RouteInfo) obj;
            if (this.mGetStatusCodeMethod != null) {
                try {
                    return ((Integer) this.mGetStatusCodeMethod.invoke(routeInfo, new Object[0])).intValue() == this.mStatusConnecting;
                } catch (IllegalAccessException | InvocationTargetException unused) {
                }
            }
            return false;
        }
    }

    /* loaded from: classes.dex */
    static class CallbackProxy<T extends Callback> extends MediaRouterJellybean.CallbackProxy<T> {
        public CallbackProxy(T t) {
            super(t);
        }

        @Override // android.media.MediaRouter.Callback
        public void onRoutePresentationDisplayChanged(MediaRouter mediaRouter, MediaRouter.RouteInfo routeInfo) {
            ((Callback) this.mCallback).onRoutePresentationDisplayChanged(routeInfo);
        }
    }
}
