package android.support.v4.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.SimpleArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* loaded from: classes.dex */
public abstract class FragmentHostCallback<E> extends FragmentContainer {
    private final Activity mActivity;
    private SimpleArrayMap<String, LoaderManager> mAllLoaderManagers;
    private boolean mCheckedForLoaderManager;
    final Context mContext;
    final FragmentManagerImpl mFragmentManager;
    private final Handler mHandler;
    private LoaderManagerImpl mLoaderManager;
    private boolean mLoadersStarted;
    private boolean mRetainLoaders;
    final int mWindowAnimations;

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onAttachFragment(Fragment fragment) {
    }

    public void onDump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
    }

    @Override // android.support.v4.app.FragmentContainer
    @Nullable
    public View onFindViewById(int i) {
        return null;
    }

    @Nullable
    public abstract E onGetHost();

    @Override // android.support.v4.app.FragmentContainer
    public boolean onHasView() {
        return true;
    }

    public boolean onHasWindowAnimations() {
        return true;
    }

    public void onRequestPermissionsFromFragment(@NonNull Fragment fragment, @NonNull String[] strArr, int i) {
    }

    public boolean onShouldSaveFragmentState(Fragment fragment) {
        return true;
    }

    public boolean onShouldShowRequestPermissionRationale(@NonNull String str) {
        return false;
    }

    public void onSupportInvalidateOptionsMenu() {
    }

    public FragmentHostCallback(Context context, Handler handler, int i) {
        this(null, context, handler, i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public FragmentHostCallback(FragmentActivity fragmentActivity) {
        this(fragmentActivity, fragmentActivity, fragmentActivity.mHandler, 0);
    }

    FragmentHostCallback(Activity activity, Context context, Handler handler, int i) {
        this.mFragmentManager = new FragmentManagerImpl();
        this.mActivity = activity;
        this.mContext = context;
        this.mHandler = handler;
        this.mWindowAnimations = i;
    }

    public LayoutInflater onGetLayoutInflater() {
        return (LayoutInflater) this.mContext.getSystemService("layout_inflater");
    }

    public void onStartActivityFromFragment(Fragment fragment, Intent intent, int i) {
        if (i != -1) {
            throw new IllegalStateException("Starting activity with a requestCode requires a FragmentActivity host");
        }
        this.mContext.startActivity(intent);
    }

    public int onGetWindowAnimations() {
        return this.mWindowAnimations;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Activity getActivity() {
        return this.mActivity;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Context getContext() {
        return this.mContext;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Handler getHandler() {
        return this.mHandler;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public FragmentManagerImpl getFragmentManagerImpl() {
        return this.mFragmentManager;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public LoaderManagerImpl getLoaderManagerImpl() {
        if (this.mLoaderManager != null) {
            return this.mLoaderManager;
        }
        this.mCheckedForLoaderManager = true;
        this.mLoaderManager = getLoaderManager("(root)", this.mLoadersStarted, true);
        return this.mLoaderManager;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void inactivateFragment(String str) {
        LoaderManagerImpl loaderManagerImpl;
        if (this.mAllLoaderManagers == null || (loaderManagerImpl = (LoaderManagerImpl) this.mAllLoaderManagers.get(str)) == null || loaderManagerImpl.mRetaining) {
            return;
        }
        loaderManagerImpl.doDestroy();
        this.mAllLoaderManagers.remove(str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean getRetainLoaders() {
        return this.mRetainLoaders;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void doLoaderStart() {
        if (this.mLoadersStarted) {
            return;
        }
        this.mLoadersStarted = true;
        if (this.mLoaderManager != null) {
            this.mLoaderManager.doStart();
        } else if (!this.mCheckedForLoaderManager) {
            this.mLoaderManager = getLoaderManager("(root)", this.mLoadersStarted, false);
            if (this.mLoaderManager != null && !this.mLoaderManager.mStarted) {
                this.mLoaderManager.doStart();
            }
        }
        this.mCheckedForLoaderManager = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void doLoaderStop(boolean z) {
        this.mRetainLoaders = z;
        if (this.mLoaderManager != null && this.mLoadersStarted) {
            this.mLoadersStarted = false;
            if (z) {
                this.mLoaderManager.doRetain();
            } else {
                this.mLoaderManager.doStop();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void doLoaderRetain() {
        if (this.mLoaderManager == null) {
            return;
        }
        this.mLoaderManager.doRetain();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void doLoaderDestroy() {
        if (this.mLoaderManager == null) {
            return;
        }
        this.mLoaderManager.doDestroy();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void reportLoaderStart() {
        if (this.mAllLoaderManagers != null) {
            int size = this.mAllLoaderManagers.size();
            LoaderManagerImpl[] loaderManagerImplArr = new LoaderManagerImpl[size];
            for (int i = size - 1; i >= 0; i--) {
                loaderManagerImplArr[i] = (LoaderManagerImpl) this.mAllLoaderManagers.valueAt(i);
            }
            for (int i2 = 0; i2 < size; i2++) {
                LoaderManagerImpl loaderManagerImpl = loaderManagerImplArr[i2];
                loaderManagerImpl.finishRetain();
                loaderManagerImpl.doReportStart();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public LoaderManagerImpl getLoaderManager(String str, boolean z, boolean z2) {
        if (this.mAllLoaderManagers == null) {
            this.mAllLoaderManagers = new SimpleArrayMap<>();
        }
        LoaderManagerImpl loaderManagerImpl = (LoaderManagerImpl) this.mAllLoaderManagers.get(str);
        if (loaderManagerImpl != null) {
            loaderManagerImpl.updateHostController(this);
            return loaderManagerImpl;
        }
        if (!z2) {
            return loaderManagerImpl;
        }
        LoaderManagerImpl loaderManagerImpl2 = new LoaderManagerImpl(str, this, z);
        this.mAllLoaderManagers.put(str, loaderManagerImpl2);
        return loaderManagerImpl2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SimpleArrayMap<String, LoaderManager> retainLoaderNonConfig() {
        int i = 0;
        if (this.mAllLoaderManagers != null) {
            int size = this.mAllLoaderManagers.size();
            LoaderManagerImpl[] loaderManagerImplArr = new LoaderManagerImpl[size];
            for (int i2 = size - 1; i2 >= 0; i2--) {
                loaderManagerImplArr[i2] = (LoaderManagerImpl) this.mAllLoaderManagers.valueAt(i2);
            }
            int i3 = 0;
            while (i < size) {
                LoaderManagerImpl loaderManagerImpl = loaderManagerImplArr[i];
                if (loaderManagerImpl.mRetaining) {
                    i3 = 1;
                } else {
                    loaderManagerImpl.doDestroy();
                    this.mAllLoaderManagers.remove(loaderManagerImpl.mWho);
                }
                i++;
            }
            i = i3;
        }
        if (i != 0) {
            return this.mAllLoaderManagers;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void restoreLoaderNonConfig(SimpleArrayMap<String, LoaderManager> simpleArrayMap) {
        this.mAllLoaderManagers = simpleArrayMap;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dumpLoaders(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.print(str);
        printWriter.print("mLoadersStarted=");
        printWriter.println(this.mLoadersStarted);
        if (this.mLoaderManager != null) {
            printWriter.print(str);
            printWriter.print("Loader Manager ");
            printWriter.print(Integer.toHexString(System.identityHashCode(this.mLoaderManager)));
            printWriter.println(":");
            this.mLoaderManager.dump(str + "  ", fileDescriptor, printWriter, strArr);
        }
    }
}
