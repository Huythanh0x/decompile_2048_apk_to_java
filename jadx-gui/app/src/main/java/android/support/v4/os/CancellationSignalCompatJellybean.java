package android.support.v4.os;

/* loaded from: classes.dex */
class CancellationSignalCompatJellybean {
    CancellationSignalCompatJellybean() {
    }

    public static Object create() {
        return new android.os.CancellationSignal();
    }

    public static void cancel(Object obj) {
        ((android.os.CancellationSignal) obj).cancel();
    }
}
