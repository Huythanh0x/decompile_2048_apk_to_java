package android.support.v4.media;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.IMediaBrowserServiceCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.os.ResultReceiver;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public abstract class MediaBrowserServiceCompat extends Service {
    private static final boolean DBG = false;
    public static final String KEY_MEDIA_ITEM = "media_item";
    public static final String SERVICE_INTERFACE = "android.media.browse.MediaBrowserServiceCompat";
    private static final String TAG = "MediaBrowserServiceCompat";
    private ServiceBinder mBinder;
    private final ArrayMap<IBinder, ConnectionRecord> mConnections = new ArrayMap<>();
    private final Handler mHandler = new Handler();
    MediaSessionCompat.Token mSession;

    @Override // android.app.Service
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
    }

    @Nullable
    public abstract BrowserRoot onGetRoot(@NonNull String str, int i, @Nullable Bundle bundle);

    public abstract void onLoadChildren(@NonNull String str, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result);

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ConnectionRecord {
        IMediaBrowserServiceCompatCallbacks callbacks;
        String pkg;
        BrowserRoot root;
        Bundle rootHints;
        HashSet<String> subscriptions;

        private ConnectionRecord() {
            this.subscriptions = new HashSet<>();
        }
    }

    /* loaded from: classes.dex */
    public class Result<T> {
        private Object mDebug;
        private boolean mDetachCalled;
        private boolean mSendResultCalled;

        void onResultSent(T t) {
        }

        Result(Object obj) {
            this.mDebug = obj;
        }

        public void sendResult(T t) {
            if (this.mSendResultCalled) {
                throw new IllegalStateException("sendResult() called twice for: " + this.mDebug);
            }
            this.mSendResultCalled = true;
            onResultSent(t);
        }

        public void detach() {
            if (this.mDetachCalled) {
                throw new IllegalStateException("detach() called when detach() had already been called for: " + this.mDebug);
            }
            if (this.mSendResultCalled) {
                throw new IllegalStateException("detach() called when sendResult() had already been called for: " + this.mDebug);
            }
            this.mDetachCalled = true;
        }

        boolean isDone() {
            return this.mDetachCalled || this.mSendResultCalled;
        }
    }

    /* loaded from: classes.dex */
    private class ServiceBinder extends IMediaBrowserServiceCompat.Stub {
        private ServiceBinder() {
        }

        @Override // android.support.v4.media.IMediaBrowserServiceCompat
        public void connect(final String str, final Bundle bundle, final IMediaBrowserServiceCompatCallbacks iMediaBrowserServiceCompatCallbacks) {
            final int callingUid = Binder.getCallingUid();
            if (MediaBrowserServiceCompat.this.isValidPackage(str, callingUid)) {
                MediaBrowserServiceCompat.this.mHandler.post(new Runnable() { // from class: android.support.v4.media.MediaBrowserServiceCompat.ServiceBinder.1
                    @Override // java.lang.Runnable
                    public void run() {
                        IBinder asBinder = iMediaBrowserServiceCompatCallbacks.asBinder();
                        MediaBrowserServiceCompat.this.mConnections.remove(asBinder);
                        ConnectionRecord connectionRecord = new ConnectionRecord();
                        connectionRecord.pkg = str;
                        connectionRecord.rootHints = bundle;
                        connectionRecord.callbacks = iMediaBrowserServiceCompatCallbacks;
                        connectionRecord.root = MediaBrowserServiceCompat.this.onGetRoot(str, callingUid, bundle);
                        if (connectionRecord.root != null) {
                            try {
                                MediaBrowserServiceCompat.this.mConnections.put(asBinder, connectionRecord);
                                if (MediaBrowserServiceCompat.this.mSession != null) {
                                    iMediaBrowserServiceCompatCallbacks.onConnect(connectionRecord.root.getRootId(), MediaBrowserServiceCompat.this.mSession, connectionRecord.root.getExtras());
                                    return;
                                }
                                return;
                            } catch (RemoteException unused) {
                                Log.w(MediaBrowserServiceCompat.TAG, "Calling onConnect() failed. Dropping client. pkg=" + str);
                                MediaBrowserServiceCompat.this.mConnections.remove(asBinder);
                                return;
                            }
                        }
                        Log.i(MediaBrowserServiceCompat.TAG, "No root for client " + str + " from service " + getClass().getName());
                        try {
                            iMediaBrowserServiceCompatCallbacks.onConnectFailed();
                        } catch (RemoteException unused2) {
                            Log.w(MediaBrowserServiceCompat.TAG, "Calling onConnectFailed() failed. Ignoring. pkg=" + str);
                        }
                    }
                });
                return;
            }
            throw new IllegalArgumentException("Package/uid mismatch: uid=" + callingUid + " package=" + str);
        }

        @Override // android.support.v4.media.IMediaBrowserServiceCompat
        public void disconnect(final IMediaBrowserServiceCompatCallbacks iMediaBrowserServiceCompatCallbacks) {
            MediaBrowserServiceCompat.this.mHandler.post(new Runnable() { // from class: android.support.v4.media.MediaBrowserServiceCompat.ServiceBinder.2
                /* JADX WARN: Multi-variable type inference failed */
                @Override // java.lang.Runnable
                public void run() {
                }
            });
        }

        @Override // android.support.v4.media.IMediaBrowserServiceCompat
        public void addSubscription(final String str, final IMediaBrowserServiceCompatCallbacks iMediaBrowserServiceCompatCallbacks) {
            MediaBrowserServiceCompat.this.mHandler.post(new Runnable() { // from class: android.support.v4.media.MediaBrowserServiceCompat.ServiceBinder.3
                /* JADX WARN: Multi-variable type inference failed */
                @Override // java.lang.Runnable
                public void run() {
                    ConnectionRecord connectionRecord = (ConnectionRecord) MediaBrowserServiceCompat.this.mConnections.get(iMediaBrowserServiceCompatCallbacks.asBinder());
                    if (connectionRecord != null) {
                        MediaBrowserServiceCompat.this.addSubscription(str, connectionRecord);
                        return;
                    }
                    Log.w(MediaBrowserServiceCompat.TAG, "addSubscription for callback that isn't registered id=" + str);
                }
            });
        }

        @Override // android.support.v4.media.IMediaBrowserServiceCompat
        public void removeSubscription(final String str, final IMediaBrowserServiceCompatCallbacks iMediaBrowserServiceCompatCallbacks) {
            MediaBrowserServiceCompat.this.mHandler.post(new Runnable() { // from class: android.support.v4.media.MediaBrowserServiceCompat.ServiceBinder.4
                /* JADX WARN: Multi-variable type inference failed */
                @Override // java.lang.Runnable
                public void run() {
                    ConnectionRecord connectionRecord = (ConnectionRecord) MediaBrowserServiceCompat.this.mConnections.get(iMediaBrowserServiceCompatCallbacks.asBinder());
                    if (connectionRecord == null) {
                        Log.w(MediaBrowserServiceCompat.TAG, "removeSubscription for callback that isn't registered id=" + str);
                        return;
                    }
                    if (connectionRecord.subscriptions.remove(str)) {
                        return;
                    }
                    Log.w(MediaBrowserServiceCompat.TAG, "removeSubscription called for " + str + " which is not subscribed");
                }
            });
        }

        @Override // android.support.v4.media.IMediaBrowserServiceCompat
        public void getMediaItem(final String str, final ResultReceiver resultReceiver) {
            if (TextUtils.isEmpty(str) || resultReceiver == null) {
                return;
            }
            MediaBrowserServiceCompat.this.mHandler.post(new Runnable() { // from class: android.support.v4.media.MediaBrowserServiceCompat.ServiceBinder.5
                @Override // java.lang.Runnable
                public void run() {
                    MediaBrowserServiceCompat.this.performLoadItem(str, resultReceiver);
                }
            });
        }
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        this.mBinder = new ServiceBinder();
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        if (SERVICE_INTERFACE.equals(intent.getAction())) {
            return this.mBinder;
        }
        return null;
    }

    public void onLoadItem(String str, Result<MediaBrowserCompat.MediaItem> result) {
        result.sendResult(null);
    }

    public void setSessionToken(final MediaSessionCompat.Token token) {
        if (token == null) {
            throw new IllegalArgumentException("Session token may not be null.");
        }
        if (this.mSession != null) {
            throw new IllegalStateException("The session token has already been set.");
        }
        this.mSession = token;
        this.mHandler.post(new Runnable() { // from class: android.support.v4.media.MediaBrowserServiceCompat.1
            /* JADX WARN: Multi-variable type inference failed */
            @Override // java.lang.Runnable
            public void run() {
                for (IBinder iBinder : MediaBrowserServiceCompat.this.mConnections.keySet()) {
                    ConnectionRecord connectionRecord = (ConnectionRecord) MediaBrowserServiceCompat.this.mConnections.get(iBinder);
                    try {
                        connectionRecord.callbacks.onConnect(connectionRecord.root.getRootId(), token, connectionRecord.root.getExtras());
                    } catch (RemoteException unused) {
                        Log.w(MediaBrowserServiceCompat.TAG, "Connection for " + connectionRecord.pkg + " is no longer valid.");
                        MediaBrowserServiceCompat.this.mConnections.remove(iBinder);
                    }
                }
            }
        });
    }

    @Nullable
    public MediaSessionCompat.Token getSessionToken() {
        return this.mSession;
    }

    public void notifyChildrenChanged(@NonNull final String str) {
        if (str == null) {
            throw new IllegalArgumentException("parentId cannot be null in notifyChildrenChanged");
        }
        this.mHandler.post(new Runnable() { // from class: android.support.v4.media.MediaBrowserServiceCompat.2
            /* JADX WARN: Multi-variable type inference failed */
            @Override // java.lang.Runnable
            public void run() {
                Iterator it = MediaBrowserServiceCompat.this.mConnections.keySet().iterator();
                while (it.hasNext()) {
                    ConnectionRecord connectionRecord = (ConnectionRecord) MediaBrowserServiceCompat.this.mConnections.get((IBinder) it.next());
                    if (connectionRecord.subscriptions.contains(str)) {
                        MediaBrowserServiceCompat.this.performLoadChildren(str, connectionRecord);
                    }
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isValidPackage(String str, int i) {
        if (str == null) {
            return false;
        }
        for (String str2 : getPackageManager().getPackagesForUid(i)) {
            if (str2.equals(str)) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addSubscription(String str, ConnectionRecord connectionRecord) {
        connectionRecord.subscriptions.add(str);
        performLoadChildren(str, connectionRecord);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performLoadChildren(final String str, final ConnectionRecord connectionRecord) {
        Result<List<MediaBrowserCompat.MediaItem>> result = new Result<List<MediaBrowserCompat.MediaItem>>(str) { // from class: android.support.v4.media.MediaBrowserServiceCompat.3
            /* JADX INFO: Access modifiers changed from: package-private */
            @Override // android.support.v4.media.MediaBrowserServiceCompat.Result
            public void onResultSent(List<MediaBrowserCompat.MediaItem> list) {
                if (list != null) {
                    if (MediaBrowserServiceCompat.this.mConnections.get(connectionRecord.callbacks.asBinder()) != connectionRecord) {
                        return;
                    }
                    try {
                        connectionRecord.callbacks.onLoadChildren(str, list);
                        return;
                    } catch (RemoteException unused) {
                        Log.w(MediaBrowserServiceCompat.TAG, "Calling onLoadChildren() failed for id=" + str + " package=" + connectionRecord.pkg);
                        return;
                    }
                }
                throw new IllegalStateException("onLoadChildren sent null list for id " + str);
            }
        };
        onLoadChildren(str, result);
        if (result.isDone()) {
            return;
        }
        throw new IllegalStateException("onLoadChildren must call detach() or sendResult() before returning for package=" + connectionRecord.pkg + " id=" + str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performLoadItem(String str, final ResultReceiver resultReceiver) {
        Result<MediaBrowserCompat.MediaItem> result = new Result<MediaBrowserCompat.MediaItem>(str) { // from class: android.support.v4.media.MediaBrowserServiceCompat.4
            /* JADX INFO: Access modifiers changed from: package-private */
            @Override // android.support.v4.media.MediaBrowserServiceCompat.Result
            public void onResultSent(MediaBrowserCompat.MediaItem mediaItem) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(MediaBrowserServiceCompat.KEY_MEDIA_ITEM, mediaItem);
                resultReceiver.send(0, bundle);
            }
        };
        onLoadItem(str, result);
        if (result.isDone()) {
            return;
        }
        throw new IllegalStateException("onLoadItem must call detach() or sendResult() before returning for id=" + str);
    }

    /* loaded from: classes.dex */
    public static final class BrowserRoot {
        private final Bundle mExtras;
        private final String mRootId;

        public BrowserRoot(@NonNull String str, @Nullable Bundle bundle) {
            if (str == null) {
                throw new IllegalArgumentException("The root id in BrowserRoot cannot be null. Use null for BrowserRoot instead.");
            }
            this.mRootId = str;
            this.mExtras = bundle;
        }

        public String getRootId() {
            return this.mRootId;
        }

        public Bundle getExtras() {
            return this.mExtras;
        }
    }
}
