package android.support.v7.app;

import android.content.Context;
import android.support.v7.app.AppCompatDelegateImplBase;
import android.support.v7.view.SupportActionModeWrapper;
import android.view.ActionMode;
import android.view.Window;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class AppCompatDelegateImplV14 extends AppCompatDelegateImplV11 {
    private boolean mHandleNativeActionModes;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AppCompatDelegateImplV14(Context context, Window window, AppCompatCallback appCompatCallback) {
        super(context, window, appCompatCallback);
        this.mHandleNativeActionModes = true;
    }

    @Override // android.support.v7.app.AppCompatDelegateImplBase
    Window.Callback wrapWindowCallback(Window.Callback callback) {
        return new AppCompatWindowCallbackV14(callback);
    }

    @Override // android.support.v7.app.AppCompatDelegateImplBase, android.support.v7.app.AppCompatDelegate
    public void setHandleNativeActionModesEnabled(boolean z) {
        this.mHandleNativeActionModes = z;
    }

    @Override // android.support.v7.app.AppCompatDelegateImplBase, android.support.v7.app.AppCompatDelegate
    public boolean isHandleNativeActionModesEnabled() {
        return this.mHandleNativeActionModes;
    }

    /* loaded from: classes.dex */
    class AppCompatWindowCallbackV14 extends AppCompatDelegateImplBase.AppCompatWindowCallbackBase {
        /* JADX INFO: Access modifiers changed from: package-private */
        public AppCompatWindowCallbackV14(Window.Callback callback) {
            super(callback);
        }

        @Override // android.support.v7.view.WindowCallbackWrapper, android.view.Window.Callback
        public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
            if (AppCompatDelegateImplV14.this.isHandleNativeActionModesEnabled()) {
                return startAsSupportActionMode(callback);
            }
            return super.onWindowStartingActionMode(callback);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final ActionMode startAsSupportActionMode(ActionMode.Callback callback) {
            SupportActionModeWrapper.CallbackWrapper callbackWrapper = new SupportActionModeWrapper.CallbackWrapper(AppCompatDelegateImplV14.this.mContext, callback);
            android.support.v7.view.ActionMode startSupportActionMode = AppCompatDelegateImplV14.this.startSupportActionMode(callbackWrapper);
            if (startSupportActionMode != null) {
                return callbackWrapper.getActionModeWrapper(startSupportActionMode);
            }
            return null;
        }
    }
}
