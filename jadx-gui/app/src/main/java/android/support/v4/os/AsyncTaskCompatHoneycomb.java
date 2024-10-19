package android.support.v4.os;

import android.os.AsyncTask;

/* loaded from: classes.dex */
class AsyncTaskCompatHoneycomb {
    AsyncTaskCompatHoneycomb() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <Params, Progress, Result> void executeParallel(AsyncTask<Params, Progress, Result> asyncTask, Params... paramsArr) {
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArr);
    }
}
