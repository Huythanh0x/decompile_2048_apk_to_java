package android.support.v4.net;

import android.net.ConnectivityManager;

/* loaded from: classes.dex */
class ConnectivityManagerCompatJellyBean {
    ConnectivityManagerCompatJellyBean() {
    }

    public static boolean isActiveNetworkMetered(ConnectivityManager connectivityManager) {
        return connectivityManager.isActiveNetworkMetered();
    }
}
