package android.support.v7.app;

import android.content.Context;
import android.support.v7.app.AppCompatDelegateImplV14;
import android.view.ActionMode;
import android.view.Window;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class AppCompatDelegateImplV23 extends AppCompatDelegateImplV14 {
    /* JADX INFO: Access modifiers changed from: package-private */
    public AppCompatDelegateImplV23(Context context, Window window, AppCompatCallback appCompatCallback) {
        super(context, window, appCompatCallback);
    }

    @Override // android.support.v7.app.AppCompatDelegateImplV14, android.support.v7.app.AppCompatDelegateImplBase
    Window.Callback wrapWindowCallback(Window.Callback callback) {
        return new AppCompatWindowCallbackV23(callback);
    }

    /* loaded from: classes.dex */
    class AppCompatWindowCallbackV23 extends AppCompatDelegateImplV14.AppCompatWindowCallbackV14 {
        @Override // android.support.v7.app.AppCompatDelegateImplV14.AppCompatWindowCallbackV14, android.support.v7.view.WindowCallbackWrapper, android.view.Window.Callback
        public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
            return null;
        }

        AppCompatWindowCallbackV23(Window.Callback callback) {
            super(callback);
        }

        @Override // android.support.v7.view.WindowCallbackWrapper, android.view.Window.Callback
        public ActionMode onWindowStartingActionMode(ActionMode.Callback callback, int i) {
            if (AppCompatDelegateImplV23.this.isHandleNativeActionModesEnabled() && i == 0) {
                return startAsSupportActionMode(callback);
            }
            return super.onWindowStartingActionMode(callback, i);
        }
    }
}
