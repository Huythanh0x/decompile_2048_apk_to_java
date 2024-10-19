package android.support.v4.media;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.IMediaBrowserServiceCompat;
import android.support.v4.media.IMediaBrowserServiceCompatCallbacks;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.os.ResultReceiver;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public final class MediaBrowserCompat {
    private final MediaBrowserImplBase mImpl;

    /* loaded from: classes.dex */
    public static class ConnectionCallback {
        public void onConnected() {
        }

        public void onConnectionFailed() {
        }

        public void onConnectionSuspended() {
        }
    }

    /* loaded from: classes.dex */
    public static abstract class ItemCallback {
        public void onError(@NonNull String str) {
        }

        public void onItemLoaded(MediaItem mediaItem) {
        }
    }

    /* loaded from: classes.dex */
    public static abstract class SubscriptionCallback {
        public void onChildrenLoaded(@NonNull String str, @NonNull List<MediaItem> list) {
        }

        public void onError(@NonNull String str) {
        }
    }

    public MediaBrowserCompat(Context context, ComponentName componentName, ConnectionCallback connectionCallback, Bundle bundle) {
        this.mImpl = new MediaBrowserImplBase(context, componentName, connectionCallback, bundle);
    }

    public void connect() {
        this.mImpl.connect();
    }

    public void disconnect() {
        this.mImpl.disconnect();
    }

    public boolean isConnected() {
        return this.mImpl.isConnected();
    }

    @NonNull
    public ComponentName getServiceComponent() {
        return this.mImpl.getServiceComponent();
    }

    @NonNull
    public String getRoot() {
        return this.mImpl.getRoot();
    }

    @Nullable
    public Bundle getExtras() {
        return this.mImpl.getExtras();
    }

    @NonNull
    public MediaSessionCompat.Token getSessionToken() {
        return this.mImpl.getSessionToken();
    }

    public void subscribe(@NonNull String str, @NonNull SubscriptionCallback subscriptionCallback) {
        this.mImpl.subscribe(str, subscriptionCallback);
    }

    public void unsubscribe(@NonNull String str) {
        this.mImpl.unsubscribe(str);
    }

    public void getItem(@NonNull String str, @NonNull ItemCallback itemCallback) {
        this.mImpl.getItem(str, itemCallback);
    }

    /* loaded from: classes.dex */
    public static class MediaItem implements Parcelable {
        public static final Parcelable.Creator<MediaItem> CREATOR = new Parcelable.Creator<MediaItem>() { // from class: android.support.v4.media.MediaBrowserCompat.MediaItem.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public MediaItem createFromParcel(Parcel parcel) {
                return new MediaItem(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public MediaItem[] newArray(int i) {
                return new MediaItem[i];
            }
        };
        public static final int FLAG_BROWSABLE = 1;
        public static final int FLAG_PLAYABLE = 2;
        private final MediaDescriptionCompat mDescription;
        private final int mFlags;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes.dex */
        public @interface Flags {
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public MediaItem(@NonNull MediaDescriptionCompat mediaDescriptionCompat, int i) {
            if (mediaDescriptionCompat == null) {
                throw new IllegalArgumentException("description cannot be null");
            }
            if (TextUtils.isEmpty(mediaDescriptionCompat.getMediaId())) {
                throw new IllegalArgumentException("description must have a non-empty media id");
            }
            this.mFlags = i;
            this.mDescription = mediaDescriptionCompat;
        }

        private MediaItem(Parcel parcel) {
            this.mFlags = parcel.readInt();
            this.mDescription = MediaDescriptionCompat.CREATOR.createFromParcel(parcel);
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.mFlags);
            this.mDescription.writeToParcel(parcel, i);
        }

        public String toString() {
            return "MediaItem{mFlags=" + this.mFlags + ", mDescription=" + this.mDescription + '}';
        }

        public int getFlags() {
            return this.mFlags;
        }

        public boolean isBrowsable() {
            return (this.mFlags & 1) != 0;
        }

        public boolean isPlayable() {
            return (this.mFlags & 2) != 0;
        }

        @NonNull
        public MediaDescriptionCompat getDescription() {
            return this.mDescription;
        }

        @NonNull
        public String getMediaId() {
            return this.mDescription.getMediaId();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class MediaBrowserImplBase {
        private static final int CONNECT_STATE_CONNECTED = 2;
        private static final int CONNECT_STATE_CONNECTING = 1;
        private static final int CONNECT_STATE_DISCONNECTED = 0;
        private static final int CONNECT_STATE_SUSPENDED = 3;
        private static final boolean DBG = false;
        private static final String TAG = "MediaBrowserCompat";
        private final ConnectionCallback mCallback;
        private final Context mContext;
        private Bundle mExtras;
        private MediaSessionCompat.Token mMediaSessionToken;
        private final Bundle mRootHints;
        private String mRootId;
        private IMediaBrowserServiceCompat mServiceBinder;
        private IMediaBrowserServiceCompatCallbacks mServiceCallbacks;
        private final ComponentName mServiceComponent;
        private MediaServiceConnection mServiceConnection;
        private final Handler mHandler = new Handler();
        private final ArrayMap<String, Subscription> mSubscriptions = new ArrayMap<>();
        private int mState = 0;

        public MediaBrowserImplBase(Context context, ComponentName componentName, ConnectionCallback connectionCallback, Bundle bundle) {
            if (context == null) {
                throw new IllegalArgumentException("context must not be null");
            }
            if (componentName == null) {
                throw new IllegalArgumentException("service component must not be null");
            }
            if (connectionCallback == null) {
                throw new IllegalArgumentException("connection callback must not be null");
            }
            this.mContext = context;
            this.mServiceComponent = componentName;
            this.mCallback = connectionCallback;
            this.mRootHints = bundle;
        }

        public void connect() {
            boolean z;
            if (this.mState != 0) {
                throw new IllegalStateException("connect() called while not disconnected (state=" + getStateLabel(this.mState) + ")");
            }
            if (this.mServiceBinder != null) {
                throw new RuntimeException("mServiceBinder should be null. Instead it is " + this.mServiceBinder);
            }
            if (this.mServiceCallbacks != null) {
                throw new RuntimeException("mServiceCallbacks should be null. Instead it is " + this.mServiceCallbacks);
            }
            this.mState = 1;
            Intent intent = new Intent(MediaBrowserServiceCompat.SERVICE_INTERFACE);
            intent.setComponent(this.mServiceComponent);
            final MediaServiceConnection mediaServiceConnection = new MediaServiceConnection();
            this.mServiceConnection = mediaServiceConnection;
            try {
                z = this.mContext.bindService(intent, this.mServiceConnection, 1);
            } catch (Exception unused) {
                Log.e(TAG, "Failed binding to service " + this.mServiceComponent);
                z = false;
            }
            if (z) {
                return;
            }
            this.mHandler.post(new Runnable() { // from class: android.support.v4.media.MediaBrowserCompat.MediaBrowserImplBase.1
                @Override // java.lang.Runnable
                public void run() {
                    if (mediaServiceConnection == MediaBrowserImplBase.this.mServiceConnection) {
                        MediaBrowserImplBase.this.forceCloseConnection();
                        MediaBrowserImplBase.this.mCallback.onConnectionFailed();
                    }
                }
            });
        }

        public void disconnect() {
            if (this.mServiceCallbacks != null) {
                try {
                    this.mServiceBinder.disconnect(this.mServiceCallbacks);
                } catch (RemoteException unused) {
                    Log.w(TAG, "RemoteException during connect for " + this.mServiceComponent);
                }
            }
            forceCloseConnection();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void forceCloseConnection() {
            if (this.mServiceConnection != null) {
                this.mContext.unbindService(this.mServiceConnection);
            }
            this.mState = 0;
            this.mServiceConnection = null;
            this.mServiceBinder = null;
            this.mServiceCallbacks = null;
            this.mRootId = null;
            this.mMediaSessionToken = null;
        }

        public boolean isConnected() {
            return this.mState == 2;
        }

        @NonNull
        public ComponentName getServiceComponent() {
            if (!isConnected()) {
                throw new IllegalStateException("getServiceComponent() called while not connected (state=" + this.mState + ")");
            }
            return this.mServiceComponent;
        }

        @NonNull
        public String getRoot() {
            if (!isConnected()) {
                throw new IllegalStateException("getSessionToken() called while not connected(state=" + getStateLabel(this.mState) + ")");
            }
            return this.mRootId;
        }

        @Nullable
        public Bundle getExtras() {
            if (!isConnected()) {
                throw new IllegalStateException("getExtras() called while not connected (state=" + getStateLabel(this.mState) + ")");
            }
            return this.mExtras;
        }

        @NonNull
        public MediaSessionCompat.Token getSessionToken() {
            if (!isConnected()) {
                throw new IllegalStateException("getSessionToken() called while not connected(state=" + this.mState + ")");
            }
            return this.mMediaSessionToken;
        }

        public void subscribe(@NonNull String str, @NonNull SubscriptionCallback subscriptionCallback) {
            if (str == null) {
                throw new IllegalArgumentException("parentId is null");
            }
            if (subscriptionCallback == null) {
                throw new IllegalArgumentException("callback is null");
            }
            Subscription subscription = this.mSubscriptions.get(str);
            if (subscription == null) {
                subscription = new Subscription(str);
                this.mSubscriptions.put(str, subscription);
            }
            subscription.callback = subscriptionCallback;
            if (this.mState == 2) {
                try {
                    this.mServiceBinder.addSubscription(str, this.mServiceCallbacks);
                } catch (RemoteException unused) {
                    Log.d(TAG, "addSubscription failed with RemoteException parentId=" + str);
                }
            }
        }

        public void unsubscribe(@NonNull String str) {
            if (TextUtils.isEmpty(str)) {
                throw new IllegalArgumentException("parentId is empty.");
            }
            Subscription remove = this.mSubscriptions.remove(str);
            if (this.mState != 2 || remove == null) {
                return;
            }
            try {
                this.mServiceBinder.removeSubscription(str, this.mServiceCallbacks);
            } catch (RemoteException unused) {
                Log.d(TAG, "removeSubscription failed with RemoteException parentId=" + str);
            }
        }

        public void getItem(@NonNull final String str, @NonNull final ItemCallback itemCallback) {
            if (TextUtils.isEmpty(str)) {
                throw new IllegalArgumentException("mediaId is empty.");
            }
            if (itemCallback == null) {
                throw new IllegalArgumentException("cb is null.");
            }
            if (this.mState != 2) {
                Log.i(TAG, "Not connected, unable to retrieve the MediaItem.");
                this.mHandler.post(new Runnable() { // from class: android.support.v4.media.MediaBrowserCompat.MediaBrowserImplBase.2
                    @Override // java.lang.Runnable
                    public void run() {
                        itemCallback.onError(str);
                    }
                });
                return;
            }
            try {
                this.mServiceBinder.getMediaItem(str, new ResultReceiver(this.mHandler) { // from class: android.support.v4.media.MediaBrowserCompat.MediaBrowserImplBase.3
                    @Override // android.support.v4.os.ResultReceiver
                    protected void onReceiveResult(int i, Bundle bundle) {
                        if (i != 0 || bundle == null || !bundle.containsKey(MediaBrowserServiceCompat.KEY_MEDIA_ITEM)) {
                            itemCallback.onError(str);
                            return;
                        }
                        Parcelable parcelable = bundle.getParcelable(MediaBrowserServiceCompat.KEY_MEDIA_ITEM);
                        if (!(parcelable instanceof MediaItem)) {
                            itemCallback.onError(str);
                        } else {
                            itemCallback.onItemLoaded((MediaItem) parcelable);
                        }
                    }
                });
            } catch (RemoteException unused) {
                Log.i(TAG, "Remote error getting media item.");
                this.mHandler.post(new Runnable() { // from class: android.support.v4.media.MediaBrowserCompat.MediaBrowserImplBase.4
                    @Override // java.lang.Runnable
                    public void run() {
                        itemCallback.onError(str);
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static String getStateLabel(int i) {
            switch (i) {
                case 0:
                    return "CONNECT_STATE_DISCONNECTED";
                case 1:
                    return "CONNECT_STATE_CONNECTING";
                case 2:
                    return "CONNECT_STATE_CONNECTED";
                case 3:
                    return "CONNECT_STATE_SUSPENDED";
                default:
                    return "UNKNOWN/" + i;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final void onServiceConnected(final IMediaBrowserServiceCompatCallbacks iMediaBrowserServiceCompatCallbacks, final String str, final MediaSessionCompat.Token token, final Bundle bundle) {
            this.mHandler.post(new Runnable() { // from class: android.support.v4.media.MediaBrowserCompat.MediaBrowserImplBase.5
                @Override // java.lang.Runnable
                public void run() {
                    if (MediaBrowserImplBase.this.isCurrent(iMediaBrowserServiceCompatCallbacks, "onConnect")) {
                        if (MediaBrowserImplBase.this.mState != 1) {
                            Log.w(MediaBrowserImplBase.TAG, "onConnect from service while mState=" + MediaBrowserImplBase.getStateLabel(MediaBrowserImplBase.this.mState) + "... ignoring");
                            return;
                        }
                        MediaBrowserImplBase.this.mRootId = str;
                        MediaBrowserImplBase.this.mMediaSessionToken = token;
                        MediaBrowserImplBase.this.mExtras = bundle;
                        MediaBrowserImplBase.this.mState = 2;
                        MediaBrowserImplBase.this.mCallback.onConnected();
                        for (String str2 : MediaBrowserImplBase.this.mSubscriptions.keySet()) {
                            try {
                                MediaBrowserImplBase.this.mServiceBinder.addSubscription(str2, MediaBrowserImplBase.this.mServiceCallbacks);
                            } catch (RemoteException unused) {
                                Log.d(MediaBrowserImplBase.TAG, "addSubscription failed with RemoteException parentId=" + str2);
                            }
                        }
                    }
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final void onConnectionFailed(final IMediaBrowserServiceCompatCallbacks iMediaBrowserServiceCompatCallbacks) {
            this.mHandler.post(new Runnable() { // from class: android.support.v4.media.MediaBrowserCompat.MediaBrowserImplBase.6
                @Override // java.lang.Runnable
                public void run() {
                    Log.e(MediaBrowserImplBase.TAG, "onConnectFailed for " + MediaBrowserImplBase.this.mServiceComponent);
                    if (MediaBrowserImplBase.this.isCurrent(iMediaBrowserServiceCompatCallbacks, "onConnectFailed")) {
                        if (MediaBrowserImplBase.this.mState == 1) {
                            MediaBrowserImplBase.this.forceCloseConnection();
                            MediaBrowserImplBase.this.mCallback.onConnectionFailed();
                            return;
                        }
                        Log.w(MediaBrowserImplBase.TAG, "onConnect from service while mState=" + MediaBrowserImplBase.getStateLabel(MediaBrowserImplBase.this.mState) + "... ignoring");
                    }
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final void onLoadChildren(final IMediaBrowserServiceCompatCallbacks iMediaBrowserServiceCompatCallbacks, final String str, final List list) {
            this.mHandler.post(new Runnable() { // from class: android.support.v4.media.MediaBrowserCompat.MediaBrowserImplBase.7
                /* JADX WARN: Multi-variable type inference failed */
                @Override // java.lang.Runnable
                public void run() {
                    if (MediaBrowserImplBase.this.isCurrent(iMediaBrowserServiceCompatCallbacks, "onLoadChildren")) {
                        List<MediaItem> list2 = list;
                        if (list2 == null) {
                            list2 = Collections.emptyList();
                        }
                        Subscription subscription = (Subscription) MediaBrowserImplBase.this.mSubscriptions.get(str);
                        if (subscription == null) {
                            return;
                        }
                        subscription.callback.onChildrenLoaded(str, list2);
                    }
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isCurrent(IMediaBrowserServiceCompatCallbacks iMediaBrowserServiceCompatCallbacks, String str) {
            if (this.mServiceCallbacks == iMediaBrowserServiceCompatCallbacks) {
                return true;
            }
            if (this.mState == 0) {
                return false;
            }
            Log.i(TAG, str + " for " + this.mServiceComponent + " with mServiceConnection=" + this.mServiceCallbacks + " this=" + this);
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public ServiceCallbacks getNewServiceCallbacks() {
            return new ServiceCallbacks(this);
        }

        void dump() {
            Log.d(TAG, "MediaBrowserCompat...");
            Log.d(TAG, "  mServiceComponent=" + this.mServiceComponent);
            Log.d(TAG, "  mCallback=" + this.mCallback);
            Log.d(TAG, "  mRootHints=" + this.mRootHints);
            Log.d(TAG, "  mState=" + getStateLabel(this.mState));
            Log.d(TAG, "  mServiceConnection=" + this.mServiceConnection);
            Log.d(TAG, "  mServiceBinder=" + this.mServiceBinder);
            Log.d(TAG, "  mServiceCallbacks=" + this.mServiceCallbacks);
            Log.d(TAG, "  mRootId=" + this.mRootId);
            Log.d(TAG, "  mMediaSessionToken=" + this.mMediaSessionToken);
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public class MediaServiceConnection implements ServiceConnection {
            private MediaServiceConnection() {
            }

            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                if (isCurrent("onServiceConnected")) {
                    MediaBrowserImplBase.this.mServiceBinder = IMediaBrowserServiceCompat.Stub.asInterface(iBinder);
                    MediaBrowserImplBase.this.mServiceCallbacks = MediaBrowserImplBase.this.getNewServiceCallbacks();
                    MediaBrowserImplBase.this.mState = 1;
                    try {
                        MediaBrowserImplBase.this.mServiceBinder.connect(MediaBrowserImplBase.this.mContext.getPackageName(), MediaBrowserImplBase.this.mRootHints, MediaBrowserImplBase.this.mServiceCallbacks);
                    } catch (RemoteException unused) {
                        Log.w(MediaBrowserImplBase.TAG, "RemoteException during connect for " + MediaBrowserImplBase.this.mServiceComponent);
                    }
                }
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName componentName) {
                if (isCurrent("onServiceDisconnected")) {
                    MediaBrowserImplBase.this.mServiceBinder = null;
                    MediaBrowserImplBase.this.mServiceCallbacks = null;
                    MediaBrowserImplBase.this.mState = 3;
                    MediaBrowserImplBase.this.mCallback.onConnectionSuspended();
                }
            }

            private boolean isCurrent(String str) {
                if (MediaBrowserImplBase.this.mServiceConnection == this) {
                    return true;
                }
                if (MediaBrowserImplBase.this.mState == 0) {
                    return false;
                }
                Log.i(MediaBrowserImplBase.TAG, str + " for " + MediaBrowserImplBase.this.mServiceComponent + " with mServiceConnection=" + MediaBrowserImplBase.this.mServiceConnection + " this=" + this);
                return false;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class ServiceCallbacks extends IMediaBrowserServiceCompatCallbacks.Stub {
            private WeakReference<MediaBrowserImplBase> mMediaBrowser;

            public ServiceCallbacks(MediaBrowserImplBase mediaBrowserImplBase) {
                this.mMediaBrowser = new WeakReference<>(mediaBrowserImplBase);
            }

            @Override // android.support.v4.media.IMediaBrowserServiceCompatCallbacks
            public void onConnect(String str, MediaSessionCompat.Token token, Bundle bundle) {
                MediaBrowserImplBase mediaBrowserImplBase = this.mMediaBrowser.get();
                if (mediaBrowserImplBase != null) {
                    mediaBrowserImplBase.onServiceConnected(this, str, token, bundle);
                }
            }

            @Override // android.support.v4.media.IMediaBrowserServiceCompatCallbacks
            public void onConnectFailed() {
                MediaBrowserImplBase mediaBrowserImplBase = this.mMediaBrowser.get();
                if (mediaBrowserImplBase != null) {
                    mediaBrowserImplBase.onConnectionFailed(this);
                }
            }

            @Override // android.support.v4.media.IMediaBrowserServiceCompatCallbacks
            public void onLoadChildren(String str, List list) {
                MediaBrowserImplBase mediaBrowserImplBase = this.mMediaBrowser.get();
                if (mediaBrowserImplBase != null) {
                    mediaBrowserImplBase.onLoadChildren(this, str, list);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class Subscription {
            SubscriptionCallback callback;
            final String id;

            Subscription(String str) {
                this.id = str;
            }
        }
    }
}
