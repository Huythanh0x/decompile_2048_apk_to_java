package android.support.v4.app;

import android.R;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.v4.app.BackStackRecord;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.DebugUtils;
import android.support.v4.util.LogWriter;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: FragmentManager.java */
/* loaded from: classes.dex */
public final class FragmentManagerImpl extends FragmentManager implements LayoutInflaterFactory {
    static final Interpolator ACCELERATE_CUBIC;
    static final Interpolator ACCELERATE_QUINT;
    static final int ANIM_DUR = 220;
    public static final int ANIM_STYLE_CLOSE_ENTER = 3;
    public static final int ANIM_STYLE_CLOSE_EXIT = 4;
    public static final int ANIM_STYLE_FADE_ENTER = 5;
    public static final int ANIM_STYLE_FADE_EXIT = 6;
    public static final int ANIM_STYLE_OPEN_ENTER = 1;
    public static final int ANIM_STYLE_OPEN_EXIT = 2;
    static boolean DEBUG = false;
    static final Interpolator DECELERATE_CUBIC;
    static final Interpolator DECELERATE_QUINT;
    static final boolean HONEYCOMB;
    static final String TAG = "FragmentManager";
    static final String TARGET_REQUEST_CODE_STATE_TAG = "android:target_req_state";
    static final String TARGET_STATE_TAG = "android:target_state";
    static final String USER_VISIBLE_HINT_TAG = "android:user_visible_hint";
    static final String VIEW_STATE_TAG = "android:view_state";
    static Field sAnimationListenerField;
    ArrayList<Fragment> mActive;
    ArrayList<Fragment> mAdded;
    ArrayList<Integer> mAvailBackStackIndices;
    ArrayList<Integer> mAvailIndices;
    ArrayList<BackStackRecord> mBackStack;
    ArrayList<FragmentManager.OnBackStackChangedListener> mBackStackChangeListeners;
    ArrayList<BackStackRecord> mBackStackIndices;
    FragmentContainer mContainer;
    FragmentController mController;
    ArrayList<Fragment> mCreatedMenus;
    boolean mDestroyed;
    boolean mExecutingActions;
    boolean mHavePendingDeferredStart;
    FragmentHostCallback mHost;
    boolean mNeedMenuInvalidate;
    String mNoTransactionsBecause;
    Fragment mParent;
    ArrayList<Runnable> mPendingActions;
    boolean mStateSaved;
    Runnable[] mTmpActions;
    int mCurState = 0;
    Bundle mStateBundle = null;
    SparseArray<Parcelable> mStateArray = null;
    Runnable mExecCommit = new Runnable() { // from class: android.support.v4.app.FragmentManagerImpl.1
        @Override // java.lang.Runnable
        public void run() {
            FragmentManagerImpl.this.execPendingActions();
        }
    };

    public static int reverseTransit(int i) {
        if (i == 4097) {
            return 8194;
        }
        if (i == 4099) {
            return FragmentTransaction.TRANSIT_FRAGMENT_FADE;
        }
        if (i != 8194) {
            return 0;
        }
        return FragmentTransaction.TRANSIT_FRAGMENT_OPEN;
    }

    public static int transitToStyleIndex(int i, boolean z) {
        if (i == 4097) {
            return z ? 1 : 2;
        }
        if (i == 4099) {
            return z ? 5 : 6;
        }
        if (i != 8194) {
            return -1;
        }
        return z ? 3 : 4;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public LayoutInflaterFactory getLayoutInflaterFactory() {
        return this;
    }

    static {
        HONEYCOMB = Build.VERSION.SDK_INT >= 11;
        sAnimationListenerField = null;
        DECELERATE_QUINT = new DecelerateInterpolator(2.5f);
        DECELERATE_CUBIC = new DecelerateInterpolator(1.5f);
        ACCELERATE_QUINT = new AccelerateInterpolator(2.5f);
        ACCELERATE_CUBIC = new AccelerateInterpolator(1.5f);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* compiled from: FragmentManager.java */
    /* loaded from: classes.dex */
    public static class AnimateOnHWLayerIfNeededListener implements Animation.AnimationListener {
        private Animation.AnimationListener mOrignalListener;
        private boolean mShouldRunOnHWLayer;
        private View mView;

        public AnimateOnHWLayerIfNeededListener(View view, Animation animation) {
            this.mOrignalListener = null;
            this.mShouldRunOnHWLayer = false;
            this.mView = null;
            if (view == null || animation == null) {
                return;
            }
            this.mView = view;
        }

        public AnimateOnHWLayerIfNeededListener(View view, Animation animation, Animation.AnimationListener animationListener) {
            this.mOrignalListener = null;
            this.mShouldRunOnHWLayer = false;
            this.mView = null;
            if (view == null || animation == null) {
                return;
            }
            this.mOrignalListener = animationListener;
            this.mView = view;
        }

        @Override // android.view.animation.Animation.AnimationListener
        @CallSuper
        public void onAnimationStart(Animation animation) {
            if (this.mView != null) {
                this.mShouldRunOnHWLayer = FragmentManagerImpl.shouldRunOnHWLayer(this.mView, animation);
                if (this.mShouldRunOnHWLayer) {
                    this.mView.post(new Runnable() { // from class: android.support.v4.app.FragmentManagerImpl.AnimateOnHWLayerIfNeededListener.1
                        @Override // java.lang.Runnable
                        public void run() {
                            ViewCompat.setLayerType(AnimateOnHWLayerIfNeededListener.this.mView, 2, null);
                        }
                    });
                }
            }
            if (this.mOrignalListener != null) {
                this.mOrignalListener.onAnimationStart(animation);
            }
        }

        @Override // android.view.animation.Animation.AnimationListener
        @CallSuper
        public void onAnimationEnd(Animation animation) {
            if (this.mView != null && this.mShouldRunOnHWLayer) {
                this.mView.post(new Runnable() { // from class: android.support.v4.app.FragmentManagerImpl.AnimateOnHWLayerIfNeededListener.2
                    @Override // java.lang.Runnable
                    public void run() {
                        ViewCompat.setLayerType(AnimateOnHWLayerIfNeededListener.this.mView, 0, null);
                    }
                });
            }
            if (this.mOrignalListener != null) {
                this.mOrignalListener.onAnimationEnd(animation);
            }
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationRepeat(Animation animation) {
            if (this.mOrignalListener != null) {
                this.mOrignalListener.onAnimationRepeat(animation);
            }
        }
    }

    static boolean modifiesAlpha(Animation animation) {
        if (animation instanceof AlphaAnimation) {
            return true;
        }
        if (animation instanceof AnimationSet) {
            List<Animation> animations = ((AnimationSet) animation).getAnimations();
            for (int i = 0; i < animations.size(); i++) {
                if (animations.get(i) instanceof AlphaAnimation) {
                    return true;
                }
            }
        }
        return false;
    }

    static boolean shouldRunOnHWLayer(View view, Animation animation) {
        return Build.VERSION.SDK_INT >= 19 && ViewCompat.getLayerType(view) == 0 && ViewCompat.hasOverlappingRendering(view) && modifiesAlpha(animation);
    }

    private void throwException(RuntimeException runtimeException) {
        Log.e(TAG, runtimeException.getMessage());
        Log.e(TAG, "Activity state:");
        PrintWriter printWriter = new PrintWriter(new LogWriter(TAG));
        if (this.mHost != null) {
            try {
                this.mHost.onDump("  ", null, printWriter, new String[0]);
                throw runtimeException;
            } catch (Exception e) {
                Log.e(TAG, "Failed dumping state", e);
                throw runtimeException;
            }
        }
        try {
            dump("  ", null, printWriter, new String[0]);
            throw runtimeException;
        } catch (Exception e2) {
            Log.e(TAG, "Failed dumping state", e2);
            throw runtimeException;
        }
    }

    @Override // android.support.v4.app.FragmentManager
    public FragmentTransaction beginTransaction() {
        return new BackStackRecord(this);
    }

    @Override // android.support.v4.app.FragmentManager
    public boolean executePendingTransactions() {
        return execPendingActions();
    }

    @Override // android.support.v4.app.FragmentManager
    public void popBackStack() {
        enqueueAction(new Runnable() { // from class: android.support.v4.app.FragmentManagerImpl.2
            @Override // java.lang.Runnable
            public void run() {
                FragmentManagerImpl.this.popBackStackState(FragmentManagerImpl.this.mHost.getHandler(), null, -1, 0);
            }
        }, false);
    }

    @Override // android.support.v4.app.FragmentManager
    public boolean popBackStackImmediate() {
        checkStateLoss();
        executePendingTransactions();
        return popBackStackState(this.mHost.getHandler(), null, -1, 0);
    }

    @Override // android.support.v4.app.FragmentManager
    public void popBackStack(final String str, final int i) {
        enqueueAction(new Runnable() { // from class: android.support.v4.app.FragmentManagerImpl.3
            @Override // java.lang.Runnable
            public void run() {
                FragmentManagerImpl.this.popBackStackState(FragmentManagerImpl.this.mHost.getHandler(), str, -1, i);
            }
        }, false);
    }

    @Override // android.support.v4.app.FragmentManager
    public boolean popBackStackImmediate(String str, int i) {
        checkStateLoss();
        executePendingTransactions();
        return popBackStackState(this.mHost.getHandler(), str, -1, i);
    }

    @Override // android.support.v4.app.FragmentManager
    public void popBackStack(final int i, final int i2) {
        if (i < 0) {
            throw new IllegalArgumentException("Bad id: " + i);
        }
        enqueueAction(new Runnable() { // from class: android.support.v4.app.FragmentManagerImpl.4
            @Override // java.lang.Runnable
            public void run() {
                FragmentManagerImpl.this.popBackStackState(FragmentManagerImpl.this.mHost.getHandler(), null, i, i2);
            }
        }, false);
    }

    @Override // android.support.v4.app.FragmentManager
    public boolean popBackStackImmediate(int i, int i2) {
        checkStateLoss();
        executePendingTransactions();
        if (i < 0) {
            throw new IllegalArgumentException("Bad id: " + i);
        }
        return popBackStackState(this.mHost.getHandler(), null, i, i2);
    }

    @Override // android.support.v4.app.FragmentManager
    public int getBackStackEntryCount() {
        if (this.mBackStack != null) {
            return this.mBackStack.size();
        }
        return 0;
    }

    @Override // android.support.v4.app.FragmentManager
    public FragmentManager.BackStackEntry getBackStackEntryAt(int i) {
        return this.mBackStack.get(i);
    }

    @Override // android.support.v4.app.FragmentManager
    public void addOnBackStackChangedListener(FragmentManager.OnBackStackChangedListener onBackStackChangedListener) {
        if (this.mBackStackChangeListeners == null) {
            this.mBackStackChangeListeners = new ArrayList<>();
        }
        this.mBackStackChangeListeners.add(onBackStackChangedListener);
    }

    @Override // android.support.v4.app.FragmentManager
    public void removeOnBackStackChangedListener(FragmentManager.OnBackStackChangedListener onBackStackChangedListener) {
        if (this.mBackStackChangeListeners != null) {
            this.mBackStackChangeListeners.remove(onBackStackChangedListener);
        }
    }

    @Override // android.support.v4.app.FragmentManager
    public void putFragment(Bundle bundle, String str, Fragment fragment) {
        if (fragment.mIndex < 0) {
            throwException(new IllegalStateException("Fragment " + fragment + " is not currently in the FragmentManager"));
        }
        bundle.putInt(str, fragment.mIndex);
    }

    @Override // android.support.v4.app.FragmentManager
    public Fragment getFragment(Bundle bundle, String str) {
        int i = bundle.getInt(str, -1);
        if (i == -1) {
            return null;
        }
        if (i >= this.mActive.size()) {
            throwException(new IllegalStateException("Fragment no longer exists for key " + str + ": index " + i));
        }
        Fragment fragment = this.mActive.get(i);
        if (fragment == null) {
            throwException(new IllegalStateException("Fragment no longer exists for key " + str + ": index " + i));
        }
        return fragment;
    }

    @Override // android.support.v4.app.FragmentManager
    public List<Fragment> getFragments() {
        return this.mActive;
    }

    @Override // android.support.v4.app.FragmentManager
    public Fragment.SavedState saveFragmentInstanceState(Fragment fragment) {
        Bundle saveFragmentBasicState;
        if (fragment.mIndex < 0) {
            throwException(new IllegalStateException("Fragment " + fragment + " is not currently in the FragmentManager"));
        }
        if (fragment.mState <= 0 || (saveFragmentBasicState = saveFragmentBasicState(fragment)) == null) {
            return null;
        }
        return new Fragment.SavedState(saveFragmentBasicState);
    }

    @Override // android.support.v4.app.FragmentManager
    public boolean isDestroyed() {
        return this.mDestroyed;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("FragmentManager{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(" in ");
        if (this.mParent != null) {
            DebugUtils.buildShortClassTag(this.mParent, sb);
        } else {
            DebugUtils.buildShortClassTag(this.mHost, sb);
        }
        sb.append("}}");
        return sb.toString();
    }

    @Override // android.support.v4.app.FragmentManager
    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        int size;
        int size2;
        int size3;
        int size4;
        int size5;
        int size6;
        String str2 = str + "    ";
        if (this.mActive != null && (size6 = this.mActive.size()) > 0) {
            printWriter.print(str);
            printWriter.print("Active Fragments in ");
            printWriter.print(Integer.toHexString(System.identityHashCode(this)));
            printWriter.println(":");
            for (int i = 0; i < size6; i++) {
                Fragment fragment = this.mActive.get(i);
                printWriter.print(str);
                printWriter.print("  #");
                printWriter.print(i);
                printWriter.print(": ");
                printWriter.println(fragment);
                if (fragment != null) {
                    fragment.dump(str2, fileDescriptor, printWriter, strArr);
                }
            }
        }
        if (this.mAdded != null && (size5 = this.mAdded.size()) > 0) {
            printWriter.print(str);
            printWriter.println("Added Fragments:");
            for (int i2 = 0; i2 < size5; i2++) {
                Fragment fragment2 = this.mAdded.get(i2);
                printWriter.print(str);
                printWriter.print("  #");
                printWriter.print(i2);
                printWriter.print(": ");
                printWriter.println(fragment2.toString());
            }
        }
        if (this.mCreatedMenus != null && (size4 = this.mCreatedMenus.size()) > 0) {
            printWriter.print(str);
            printWriter.println("Fragments Created Menus:");
            for (int i3 = 0; i3 < size4; i3++) {
                Fragment fragment3 = this.mCreatedMenus.get(i3);
                printWriter.print(str);
                printWriter.print("  #");
                printWriter.print(i3);
                printWriter.print(": ");
                printWriter.println(fragment3.toString());
            }
        }
        if (this.mBackStack != null && (size3 = this.mBackStack.size()) > 0) {
            printWriter.print(str);
            printWriter.println("Back Stack:");
            for (int i4 = 0; i4 < size3; i4++) {
                BackStackRecord backStackRecord = this.mBackStack.get(i4);
                printWriter.print(str);
                printWriter.print("  #");
                printWriter.print(i4);
                printWriter.print(": ");
                printWriter.println(backStackRecord.toString());
                backStackRecord.dump(str2, fileDescriptor, printWriter, strArr);
            }
        }
        synchronized (this) {
            if (this.mBackStackIndices != null && (size2 = this.mBackStackIndices.size()) > 0) {
                printWriter.print(str);
                printWriter.println("Back Stack Indices:");
                for (int i5 = 0; i5 < size2; i5++) {
                    Object obj = (BackStackRecord) this.mBackStackIndices.get(i5);
                    printWriter.print(str);
                    printWriter.print("  #");
                    printWriter.print(i5);
                    printWriter.print(": ");
                    printWriter.println(obj);
                }
            }
            if (this.mAvailBackStackIndices != null && this.mAvailBackStackIndices.size() > 0) {
                printWriter.print(str);
                printWriter.print("mAvailBackStackIndices: ");
                printWriter.println(Arrays.toString(this.mAvailBackStackIndices.toArray()));
            }
        }
        if (this.mPendingActions != null && (size = this.mPendingActions.size()) > 0) {
            printWriter.print(str);
            printWriter.println("Pending Actions:");
            for (int i6 = 0; i6 < size; i6++) {
                Object obj2 = (Runnable) this.mPendingActions.get(i6);
                printWriter.print(str);
                printWriter.print("  #");
                printWriter.print(i6);
                printWriter.print(": ");
                printWriter.println(obj2);
            }
        }
        printWriter.print(str);
        printWriter.println("FragmentManager misc state:");
        printWriter.print(str);
        printWriter.print("  mHost=");
        printWriter.println(this.mHost);
        printWriter.print(str);
        printWriter.print("  mContainer=");
        printWriter.println(this.mContainer);
        if (this.mParent != null) {
            printWriter.print(str);
            printWriter.print("  mParent=");
            printWriter.println(this.mParent);
        }
        printWriter.print(str);
        printWriter.print("  mCurState=");
        printWriter.print(this.mCurState);
        printWriter.print(" mStateSaved=");
        printWriter.print(this.mStateSaved);
        printWriter.print(" mDestroyed=");
        printWriter.println(this.mDestroyed);
        if (this.mNeedMenuInvalidate) {
            printWriter.print(str);
            printWriter.print("  mNeedMenuInvalidate=");
            printWriter.println(this.mNeedMenuInvalidate);
        }
        if (this.mNoTransactionsBecause != null) {
            printWriter.print(str);
            printWriter.print("  mNoTransactionsBecause=");
            printWriter.println(this.mNoTransactionsBecause);
        }
        if (this.mAvailIndices == null || this.mAvailIndices.size() <= 0) {
            return;
        }
        printWriter.print(str);
        printWriter.print("  mAvailIndices: ");
        printWriter.println(Arrays.toString(this.mAvailIndices.toArray()));
    }

    static Animation makeOpenCloseAnimation(Context context, float f, float f2, float f3, float f4) {
        AnimationSet animationSet = new AnimationSet(false);
        ScaleAnimation scaleAnimation = new ScaleAnimation(f, f2, f, f2, 1, 0.5f, 1, 0.5f);
        scaleAnimation.setInterpolator(DECELERATE_QUINT);
        scaleAnimation.setDuration(220L);
        animationSet.addAnimation(scaleAnimation);
        AlphaAnimation alphaAnimation = new AlphaAnimation(f3, f4);
        alphaAnimation.setInterpolator(DECELERATE_CUBIC);
        alphaAnimation.setDuration(220L);
        animationSet.addAnimation(alphaAnimation);
        return animationSet;
    }

    static Animation makeFadeAnimation(Context context, float f, float f2) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(f, f2);
        alphaAnimation.setInterpolator(DECELERATE_CUBIC);
        alphaAnimation.setDuration(220L);
        return alphaAnimation;
    }

    Animation loadAnimation(Fragment fragment, int i, boolean z, int i2) {
        int transitToStyleIndex;
        Animation loadAnimation;
        Animation onCreateAnimation = fragment.onCreateAnimation(i, z, fragment.mNextAnim);
        if (onCreateAnimation != null) {
            return onCreateAnimation;
        }
        if (fragment.mNextAnim != 0 && (loadAnimation = AnimationUtils.loadAnimation(this.mHost.getContext(), fragment.mNextAnim)) != null) {
            return loadAnimation;
        }
        if (i == 0 || (transitToStyleIndex = transitToStyleIndex(i, z)) < 0) {
            return null;
        }
        switch (transitToStyleIndex) {
            case 1:
                return makeOpenCloseAnimation(this.mHost.getContext(), 1.125f, 1.0f, 0.0f, 1.0f);
            case 2:
                return makeOpenCloseAnimation(this.mHost.getContext(), 1.0f, 0.975f, 1.0f, 0.0f);
            case 3:
                return makeOpenCloseAnimation(this.mHost.getContext(), 0.975f, 1.0f, 0.0f, 1.0f);
            case 4:
                return makeOpenCloseAnimation(this.mHost.getContext(), 1.0f, 1.075f, 1.0f, 0.0f);
            case 5:
                return makeFadeAnimation(this.mHost.getContext(), 0.0f, 1.0f);
            case 6:
                return makeFadeAnimation(this.mHost.getContext(), 1.0f, 0.0f);
            default:
                if (i2 == 0 && this.mHost.onHasWindowAnimations()) {
                    i2 = this.mHost.onGetWindowAnimations();
                }
                return i2 == 0 ? null : null;
        }
    }

    public void performPendingDeferredStart(Fragment fragment) {
        if (fragment.mDeferStart) {
            if (this.mExecutingActions) {
                this.mHavePendingDeferredStart = true;
            } else {
                fragment.mDeferStart = false;
                moveToState(fragment, this.mCurState, 0, 0, false);
            }
        }
    }

    private void setHWLayerAnimListenerIfAlpha(View view, Animation animation) {
        if (view == null || animation == null || !shouldRunOnHWLayer(view, animation)) {
            return;
        }
        Animation.AnimationListener animationListener = null;
        try {
            if (sAnimationListenerField == null) {
                sAnimationListenerField = Animation.class.getDeclaredField("mListener");
                sAnimationListenerField.setAccessible(true);
            }
            animationListener = (Animation.AnimationListener) sAnimationListenerField.get(animation);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "Cannot access Animation's mListener field", e);
        } catch (NoSuchFieldException e2) {
            Log.e(TAG, "No field with the name mListener is found in Animation class", e2);
        }
        animation.setAnimationListener(new AnimateOnHWLayerIfNeededListener(view, animation, animationListener));
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:121:0x027c. Please report as an issue. */
    /* JADX WARN: Failed to find 'out' block for switch in B:29:0x005a. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:130:0x02a6  */
    /* JADX WARN: Removed duplicated region for block: B:135:0x02c5  */
    /* JADX WARN: Removed duplicated region for block: B:141:0x02e5  */
    /* JADX WARN: Removed duplicated region for block: B:166:0x0351  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    void moveToState(final android.support.v4.app.Fragment r17, int r18, int r19, int r20, boolean r21) {
        /*
            Method dump skipped, instructions count: 992
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.app.FragmentManagerImpl.moveToState(android.support.v4.app.Fragment, int, int, int, boolean):void");
    }

    void moveToState(Fragment fragment) {
        moveToState(fragment, this.mCurState, 0, 0, false);
    }

    void moveToState(int i, boolean z) {
        moveToState(i, 0, 0, z);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void moveToState(int i, int i2, int i3, boolean z) {
        if (this.mHost == null && i != 0) {
            throw new IllegalStateException("No host");
        }
        if (z || this.mCurState != i) {
            this.mCurState = i;
            if (this.mActive != null) {
                boolean z2 = false;
                for (int i4 = 0; i4 < this.mActive.size(); i4++) {
                    Fragment fragment = this.mActive.get(i4);
                    if (fragment != null) {
                        moveToState(fragment, i, i2, i3, false);
                        if (fragment.mLoaderManager != null) {
                            z2 |= fragment.mLoaderManager.hasRunningLoaders();
                        }
                    }
                }
                if (!z2) {
                    startPendingDeferredFragments();
                }
                if (this.mNeedMenuInvalidate && this.mHost != null && this.mCurState == 5) {
                    this.mHost.onSupportInvalidateOptionsMenu();
                    this.mNeedMenuInvalidate = false;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void startPendingDeferredFragments() {
        if (this.mActive == null) {
            return;
        }
        for (int i = 0; i < this.mActive.size(); i++) {
            Fragment fragment = this.mActive.get(i);
            if (fragment != null) {
                performPendingDeferredStart(fragment);
            }
        }
    }

    void makeActive(Fragment fragment) {
        if (fragment.mIndex >= 0) {
            return;
        }
        if (this.mAvailIndices == null || this.mAvailIndices.size() <= 0) {
            if (this.mActive == null) {
                this.mActive = new ArrayList<>();
            }
            fragment.setIndex(this.mActive.size(), this.mParent);
            this.mActive.add(fragment);
        } else {
            fragment.setIndex(this.mAvailIndices.remove(this.mAvailIndices.size() - 1).intValue(), this.mParent);
            this.mActive.set(fragment.mIndex, fragment);
        }
        if (DEBUG) {
            Log.v(TAG, "Allocated fragment index " + fragment);
        }
    }

    void makeInactive(Fragment fragment) {
        if (fragment.mIndex < 0) {
            return;
        }
        if (DEBUG) {
            Log.v(TAG, "Freeing fragment index " + fragment);
        }
        this.mActive.set(fragment.mIndex, null);
        if (this.mAvailIndices == null) {
            this.mAvailIndices = new ArrayList<>();
        }
        this.mAvailIndices.add(Integer.valueOf(fragment.mIndex));
        this.mHost.inactivateFragment(fragment.mWho);
        fragment.initState();
    }

    public void addFragment(Fragment fragment, boolean z) {
        if (this.mAdded == null) {
            this.mAdded = new ArrayList<>();
        }
        if (DEBUG) {
            Log.v(TAG, "add: " + fragment);
        }
        makeActive(fragment);
        if (fragment.mDetached) {
            return;
        }
        if (this.mAdded.contains(fragment)) {
            throw new IllegalStateException("Fragment already added: " + fragment);
        }
        this.mAdded.add(fragment);
        fragment.mAdded = true;
        fragment.mRemoving = false;
        if (fragment.mHasMenu && fragment.mMenuVisible) {
            this.mNeedMenuInvalidate = true;
        }
        if (z) {
            moveToState(fragment);
        }
    }

    public void removeFragment(Fragment fragment, int i, int i2) {
        if (DEBUG) {
            Log.v(TAG, "remove: " + fragment + " nesting=" + fragment.mBackStackNesting);
        }
        boolean z = !fragment.isInBackStack();
        if (!fragment.mDetached || z) {
            if (this.mAdded != null) {
                this.mAdded.remove(fragment);
            }
            if (fragment.mHasMenu && fragment.mMenuVisible) {
                this.mNeedMenuInvalidate = true;
            }
            fragment.mAdded = false;
            fragment.mRemoving = true;
            moveToState(fragment, z ? 0 : 1, i, i2, false);
        }
    }

    public void hideFragment(Fragment fragment, int i, int i2) {
        if (DEBUG) {
            Log.v(TAG, "hide: " + fragment);
        }
        if (fragment.mHidden) {
            return;
        }
        fragment.mHidden = true;
        if (fragment.mView != null) {
            Animation loadAnimation = loadAnimation(fragment, i, false, i2);
            if (loadAnimation != null) {
                setHWLayerAnimListenerIfAlpha(fragment.mView, loadAnimation);
                fragment.mView.startAnimation(loadAnimation);
            }
            fragment.mView.setVisibility(8);
        }
        if (fragment.mAdded && fragment.mHasMenu && fragment.mMenuVisible) {
            this.mNeedMenuInvalidate = true;
        }
        fragment.onHiddenChanged(true);
    }

    public void showFragment(Fragment fragment, int i, int i2) {
        if (DEBUG) {
            Log.v(TAG, "show: " + fragment);
        }
        if (fragment.mHidden) {
            fragment.mHidden = false;
            if (fragment.mView != null) {
                Animation loadAnimation = loadAnimation(fragment, i, true, i2);
                if (loadAnimation != null) {
                    setHWLayerAnimListenerIfAlpha(fragment.mView, loadAnimation);
                    fragment.mView.startAnimation(loadAnimation);
                }
                fragment.mView.setVisibility(0);
            }
            if (fragment.mAdded && fragment.mHasMenu && fragment.mMenuVisible) {
                this.mNeedMenuInvalidate = true;
            }
            fragment.onHiddenChanged(false);
        }
    }

    public void detachFragment(Fragment fragment, int i, int i2) {
        if (DEBUG) {
            Log.v(TAG, "detach: " + fragment);
        }
        if (fragment.mDetached) {
            return;
        }
        fragment.mDetached = true;
        if (fragment.mAdded) {
            if (this.mAdded != null) {
                if (DEBUG) {
                    Log.v(TAG, "remove from detach: " + fragment);
                }
                this.mAdded.remove(fragment);
            }
            if (fragment.mHasMenu && fragment.mMenuVisible) {
                this.mNeedMenuInvalidate = true;
            }
            fragment.mAdded = false;
            moveToState(fragment, 1, i, i2, false);
        }
    }

    public void attachFragment(Fragment fragment, int i, int i2) {
        if (DEBUG) {
            Log.v(TAG, "attach: " + fragment);
        }
        if (fragment.mDetached) {
            fragment.mDetached = false;
            if (fragment.mAdded) {
                return;
            }
            if (this.mAdded == null) {
                this.mAdded = new ArrayList<>();
            }
            if (this.mAdded.contains(fragment)) {
                throw new IllegalStateException("Fragment already added: " + fragment);
            }
            if (DEBUG) {
                Log.v(TAG, "add from attach: " + fragment);
            }
            this.mAdded.add(fragment);
            fragment.mAdded = true;
            if (fragment.mHasMenu && fragment.mMenuVisible) {
                this.mNeedMenuInvalidate = true;
            }
            moveToState(fragment, this.mCurState, i, i2, false);
        }
    }

    @Override // android.support.v4.app.FragmentManager
    public Fragment findFragmentById(int i) {
        if (this.mAdded != null) {
            for (int size = this.mAdded.size() - 1; size >= 0; size--) {
                Fragment fragment = this.mAdded.get(size);
                if (fragment != null && fragment.mFragmentId == i) {
                    return fragment;
                }
            }
        }
        if (this.mActive == null) {
            return null;
        }
        for (int size2 = this.mActive.size() - 1; size2 >= 0; size2--) {
            Fragment fragment2 = this.mActive.get(size2);
            if (fragment2 != null && fragment2.mFragmentId == i) {
                return fragment2;
            }
        }
        return null;
    }

    @Override // android.support.v4.app.FragmentManager
    public Fragment findFragmentByTag(String str) {
        if (this.mAdded != null && str != null) {
            for (int size = this.mAdded.size() - 1; size >= 0; size--) {
                Fragment fragment = this.mAdded.get(size);
                if (fragment != null && str.equals(fragment.mTag)) {
                    return fragment;
                }
            }
        }
        if (this.mActive == null || str == null) {
            return null;
        }
        for (int size2 = this.mActive.size() - 1; size2 >= 0; size2--) {
            Fragment fragment2 = this.mActive.get(size2);
            if (fragment2 != null && str.equals(fragment2.mTag)) {
                return fragment2;
            }
        }
        return null;
    }

    public Fragment findFragmentByWho(String str) {
        Fragment findFragmentByWho;
        if (this.mActive == null || str == null) {
            return null;
        }
        for (int size = this.mActive.size() - 1; size >= 0; size--) {
            Fragment fragment = this.mActive.get(size);
            if (fragment != null && (findFragmentByWho = fragment.findFragmentByWho(str)) != null) {
                return findFragmentByWho;
            }
        }
        return null;
    }

    private void checkStateLoss() {
        if (this.mStateSaved) {
            throw new IllegalStateException("Can not perform this action after onSaveInstanceState");
        }
        if (this.mNoTransactionsBecause == null) {
            return;
        }
        throw new IllegalStateException("Can not perform this action inside of " + this.mNoTransactionsBecause);
    }

    public void enqueueAction(Runnable runnable, boolean z) {
        if (!z) {
            checkStateLoss();
        }
        synchronized (this) {
            if (this.mDestroyed || this.mHost == null) {
                throw new IllegalStateException("Activity has been destroyed");
            }
            if (this.mPendingActions == null) {
                this.mPendingActions = new ArrayList<>();
            }
            this.mPendingActions.add(runnable);
            if (this.mPendingActions.size() == 1) {
                this.mHost.getHandler().removeCallbacks(this.mExecCommit);
                this.mHost.getHandler().post(this.mExecCommit);
            }
        }
    }

    public int allocBackStackIndex(BackStackRecord backStackRecord) {
        synchronized (this) {
            if (this.mAvailBackStackIndices != null && this.mAvailBackStackIndices.size() > 0) {
                int intValue = this.mAvailBackStackIndices.remove(this.mAvailBackStackIndices.size() - 1).intValue();
                if (DEBUG) {
                    Log.v(TAG, "Adding back stack index " + intValue + " with " + backStackRecord);
                }
                this.mBackStackIndices.set(intValue, backStackRecord);
                return intValue;
            }
            if (this.mBackStackIndices == null) {
                this.mBackStackIndices = new ArrayList<>();
            }
            int size = this.mBackStackIndices.size();
            if (DEBUG) {
                Log.v(TAG, "Setting back stack index " + size + " to " + backStackRecord);
            }
            this.mBackStackIndices.add(backStackRecord);
            return size;
        }
    }

    public void setBackStackIndex(int i, BackStackRecord backStackRecord) {
        synchronized (this) {
            if (this.mBackStackIndices == null) {
                this.mBackStackIndices = new ArrayList<>();
            }
            int size = this.mBackStackIndices.size();
            if (i < size) {
                if (DEBUG) {
                    Log.v(TAG, "Setting back stack index " + i + " to " + backStackRecord);
                }
                this.mBackStackIndices.set(i, backStackRecord);
            } else {
                while (size < i) {
                    this.mBackStackIndices.add(null);
                    if (this.mAvailBackStackIndices == null) {
                        this.mAvailBackStackIndices = new ArrayList<>();
                    }
                    if (DEBUG) {
                        Log.v(TAG, "Adding available back stack index " + size);
                    }
                    this.mAvailBackStackIndices.add(Integer.valueOf(size));
                    size++;
                }
                if (DEBUG) {
                    Log.v(TAG, "Adding back stack index " + i + " with " + backStackRecord);
                }
                this.mBackStackIndices.add(backStackRecord);
            }
        }
    }

    public void freeBackStackIndex(int i) {
        synchronized (this) {
            this.mBackStackIndices.set(i, null);
            if (this.mAvailBackStackIndices == null) {
                this.mAvailBackStackIndices = new ArrayList<>();
            }
            if (DEBUG) {
                Log.v(TAG, "Freeing back stack index " + i);
            }
            this.mAvailBackStackIndices.add(Integer.valueOf(i));
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:18:0x0050, code lost:
    
        r6.mExecutingActions = true;
        r3 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x0053, code lost:
    
        if (r3 >= r2) goto L50;
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x0055, code lost:
    
        r6.mTmpActions[r3].run();
        r6.mTmpActions[r3] = null;
        r3 = r3 + 1;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean execPendingActions() {
        /*
            r6 = this;
            boolean r0 = r6.mExecutingActions
            if (r0 != 0) goto La2
            android.os.Looper r0 = android.os.Looper.myLooper()
            android.support.v4.app.FragmentHostCallback r1 = r6.mHost
            android.os.Handler r1 = r1.getHandler()
            android.os.Looper r1 = r1.getLooper()
            if (r0 != r1) goto L9a
            r0 = 1
            r1 = 0
            r2 = 0
        L17:
            monitor-enter(r6)
            java.util.ArrayList<java.lang.Runnable> r3 = r6.mPendingActions     // Catch: java.lang.Throwable -> L97
            if (r3 == 0) goto L68
            java.util.ArrayList<java.lang.Runnable> r3 = r6.mPendingActions     // Catch: java.lang.Throwable -> L97
            int r3 = r3.size()     // Catch: java.lang.Throwable -> L97
            if (r3 != 0) goto L25
            goto L68
        L25:
            java.util.ArrayList<java.lang.Runnable> r2 = r6.mPendingActions     // Catch: java.lang.Throwable -> L97
            int r2 = r2.size()     // Catch: java.lang.Throwable -> L97
            java.lang.Runnable[] r3 = r6.mTmpActions     // Catch: java.lang.Throwable -> L97
            if (r3 == 0) goto L34
            java.lang.Runnable[] r3 = r6.mTmpActions     // Catch: java.lang.Throwable -> L97
            int r3 = r3.length     // Catch: java.lang.Throwable -> L97
            if (r3 >= r2) goto L38
        L34:
            java.lang.Runnable[] r3 = new java.lang.Runnable[r2]     // Catch: java.lang.Throwable -> L97
            r6.mTmpActions = r3     // Catch: java.lang.Throwable -> L97
        L38:
            java.util.ArrayList<java.lang.Runnable> r3 = r6.mPendingActions     // Catch: java.lang.Throwable -> L97
            java.lang.Runnable[] r4 = r6.mTmpActions     // Catch: java.lang.Throwable -> L97
            r3.toArray(r4)     // Catch: java.lang.Throwable -> L97
            java.util.ArrayList<java.lang.Runnable> r3 = r6.mPendingActions     // Catch: java.lang.Throwable -> L97
            r3.clear()     // Catch: java.lang.Throwable -> L97
            android.support.v4.app.FragmentHostCallback r3 = r6.mHost     // Catch: java.lang.Throwable -> L97
            android.os.Handler r3 = r3.getHandler()     // Catch: java.lang.Throwable -> L97
            java.lang.Runnable r4 = r6.mExecCommit     // Catch: java.lang.Throwable -> L97
            r3.removeCallbacks(r4)     // Catch: java.lang.Throwable -> L97
            monitor-exit(r6)     // Catch: java.lang.Throwable -> L97
            r6.mExecutingActions = r0
            r3 = 0
        L53:
            if (r3 >= r2) goto L64
            java.lang.Runnable[] r4 = r6.mTmpActions
            r4 = r4[r3]
            r4.run()
            java.lang.Runnable[] r4 = r6.mTmpActions
            r5 = 0
            r4[r3] = r5
            int r3 = r3 + 1
            goto L53
        L64:
            r6.mExecutingActions = r1
            r2 = 1
            goto L17
        L68:
            monitor-exit(r6)     // Catch: java.lang.Throwable -> L97
            boolean r0 = r6.mHavePendingDeferredStart
            if (r0 == 0) goto L96
            r0 = 0
            r3 = 0
        L6f:
            java.util.ArrayList<android.support.v4.app.Fragment> r4 = r6.mActive
            int r4 = r4.size()
            if (r0 >= r4) goto L8f
            java.util.ArrayList<android.support.v4.app.Fragment> r4 = r6.mActive
            java.lang.Object r4 = r4.get(r0)
            android.support.v4.app.Fragment r4 = (android.support.v4.app.Fragment) r4
            if (r4 == 0) goto L8c
            android.support.v4.app.LoaderManagerImpl r5 = r4.mLoaderManager
            if (r5 == 0) goto L8c
            android.support.v4.app.LoaderManagerImpl r4 = r4.mLoaderManager
            boolean r4 = r4.hasRunningLoaders()
            r3 = r3 | r4
        L8c:
            int r0 = r0 + 1
            goto L6f
        L8f:
            if (r3 != 0) goto L96
            r6.mHavePendingDeferredStart = r1
            r6.startPendingDeferredFragments()
        L96:
            return r2
        L97:
            r0 = move-exception
            monitor-exit(r6)     // Catch: java.lang.Throwable -> L97
            throw r0
        L9a:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            java.lang.String r1 = "Must be called from main thread of process"
            r0.<init>(r1)
            throw r0
        La2:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            java.lang.String r1 = "Recursive entry to executePendingTransactions"
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.app.FragmentManagerImpl.execPendingActions():boolean");
    }

    void reportBackStackChanged() {
        if (this.mBackStackChangeListeners != null) {
            for (int i = 0; i < this.mBackStackChangeListeners.size(); i++) {
                this.mBackStackChangeListeners.get(i).onBackStackChanged();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addBackStackState(BackStackRecord backStackRecord) {
        if (this.mBackStack == null) {
            this.mBackStack = new ArrayList<>();
        }
        this.mBackStack.add(backStackRecord);
        reportBackStackChanged();
    }

    boolean popBackStackState(Handler handler, String str, int i, int i2) {
        int size;
        if (this.mBackStack == null) {
            return false;
        }
        if (str == null && i < 0 && (i2 & 1) == 0) {
            int size2 = this.mBackStack.size() - 1;
            if (size2 < 0) {
                return false;
            }
            BackStackRecord remove = this.mBackStack.remove(size2);
            SparseArray<Fragment> sparseArray = new SparseArray<>();
            SparseArray<Fragment> sparseArray2 = new SparseArray<>();
            remove.calculateBackFragments(sparseArray, sparseArray2);
            remove.popFromBackStack(true, null, sparseArray, sparseArray2);
            reportBackStackChanged();
        } else {
            if (str != null || i >= 0) {
                size = this.mBackStack.size() - 1;
                while (size >= 0) {
                    BackStackRecord backStackRecord = this.mBackStack.get(size);
                    if ((str != null && str.equals(backStackRecord.getName())) || (i >= 0 && i == backStackRecord.mIndex)) {
                        break;
                    }
                    size--;
                }
                if (size < 0) {
                    return false;
                }
                if ((i2 & 1) != 0) {
                    size--;
                    while (size >= 0) {
                        BackStackRecord backStackRecord2 = this.mBackStack.get(size);
                        if ((str == null || !str.equals(backStackRecord2.getName())) && (i < 0 || i != backStackRecord2.mIndex)) {
                            break;
                        }
                        size--;
                    }
                }
            } else {
                size = -1;
            }
            if (size == this.mBackStack.size() - 1) {
                return false;
            }
            ArrayList arrayList = new ArrayList();
            for (int size3 = this.mBackStack.size() - 1; size3 > size; size3--) {
                arrayList.add(this.mBackStack.remove(size3));
            }
            int size4 = arrayList.size() - 1;
            SparseArray<Fragment> sparseArray3 = new SparseArray<>();
            SparseArray<Fragment> sparseArray4 = new SparseArray<>();
            for (int i3 = 0; i3 <= size4; i3++) {
                ((BackStackRecord) arrayList.get(i3)).calculateBackFragments(sparseArray3, sparseArray4);
            }
            BackStackRecord.TransitionState transitionState = null;
            int i4 = 0;
            while (i4 <= size4) {
                if (DEBUG) {
                    Log.v(TAG, "Popping back stack state: " + arrayList.get(i4));
                }
                transitionState = ((BackStackRecord) arrayList.get(i4)).popFromBackStack(i4 == size4, transitionState, sparseArray3, sparseArray4);
                i4++;
            }
            reportBackStackChanged();
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ArrayList<Fragment> retainNonConfig() {
        ArrayList<Fragment> arrayList = null;
        if (this.mActive != null) {
            for (int i = 0; i < this.mActive.size(); i++) {
                Fragment fragment = this.mActive.get(i);
                if (fragment != null && fragment.mRetainInstance) {
                    if (arrayList == null) {
                        arrayList = new ArrayList<>();
                    }
                    arrayList.add(fragment);
                    fragment.mRetaining = true;
                    fragment.mTargetIndex = fragment.mTarget != null ? fragment.mTarget.mIndex : -1;
                    if (DEBUG) {
                        Log.v(TAG, "retainNonConfig: keeping retained " + fragment);
                    }
                }
            }
        }
        return arrayList;
    }

    void saveFragmentViewState(Fragment fragment) {
        if (fragment.mInnerView == null) {
            return;
        }
        if (this.mStateArray == null) {
            this.mStateArray = new SparseArray<>();
        } else {
            this.mStateArray.clear();
        }
        fragment.mInnerView.saveHierarchyState(this.mStateArray);
        if (this.mStateArray.size() > 0) {
            fragment.mSavedViewState = this.mStateArray;
            this.mStateArray = null;
        }
    }

    Bundle saveFragmentBasicState(Fragment fragment) {
        Bundle bundle;
        if (this.mStateBundle == null) {
            this.mStateBundle = new Bundle();
        }
        fragment.performSaveInstanceState(this.mStateBundle);
        if (this.mStateBundle.isEmpty()) {
            bundle = null;
        } else {
            bundle = this.mStateBundle;
            this.mStateBundle = null;
        }
        if (fragment.mView != null) {
            saveFragmentViewState(fragment);
        }
        if (fragment.mSavedViewState != null) {
            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putSparseParcelableArray(VIEW_STATE_TAG, fragment.mSavedViewState);
        }
        if (!fragment.mUserVisibleHint) {
            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putBoolean(USER_VISIBLE_HINT_TAG, fragment.mUserVisibleHint);
        }
        return bundle;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Parcelable saveAllState() {
        int[] iArr;
        int size;
        int size2;
        execPendingActions();
        if (HONEYCOMB) {
            this.mStateSaved = true;
        }
        BackStackState[] backStackStateArr = null;
        if (this.mActive == null || this.mActive.size() <= 0) {
            return null;
        }
        int size3 = this.mActive.size();
        FragmentState[] fragmentStateArr = new FragmentState[size3];
        boolean z = false;
        for (int i = 0; i < size3; i++) {
            Fragment fragment = this.mActive.get(i);
            if (fragment != null) {
                if (fragment.mIndex < 0) {
                    throwException(new IllegalStateException("Failure saving state: active " + fragment + " has cleared index: " + fragment.mIndex));
                }
                FragmentState fragmentState = new FragmentState(fragment);
                fragmentStateArr[i] = fragmentState;
                if (fragment.mState > 0 && fragmentState.mSavedFragmentState == null) {
                    fragmentState.mSavedFragmentState = saveFragmentBasicState(fragment);
                    if (fragment.mTarget != null) {
                        if (fragment.mTarget.mIndex < 0) {
                            throwException(new IllegalStateException("Failure saving state: " + fragment + " has target not in fragment manager: " + fragment.mTarget));
                        }
                        if (fragmentState.mSavedFragmentState == null) {
                            fragmentState.mSavedFragmentState = new Bundle();
                        }
                        putFragment(fragmentState.mSavedFragmentState, TARGET_STATE_TAG, fragment.mTarget);
                        if (fragment.mTargetRequestCode != 0) {
                            fragmentState.mSavedFragmentState.putInt(TARGET_REQUEST_CODE_STATE_TAG, fragment.mTargetRequestCode);
                        }
                    }
                } else {
                    fragmentState.mSavedFragmentState = fragment.mSavedFragmentState;
                }
                if (DEBUG) {
                    Log.v(TAG, "Saved state of " + fragment + ": " + fragmentState.mSavedFragmentState);
                }
                z = true;
            }
        }
        if (!z) {
            if (DEBUG) {
                Log.v(TAG, "saveAllState: no fragments!");
            }
            return null;
        }
        if (this.mAdded == null || (size2 = this.mAdded.size()) <= 0) {
            iArr = null;
        } else {
            iArr = new int[size2];
            for (int i2 = 0; i2 < size2; i2++) {
                iArr[i2] = this.mAdded.get(i2).mIndex;
                if (iArr[i2] < 0) {
                    throwException(new IllegalStateException("Failure saving state: active " + this.mAdded.get(i2) + " has cleared index: " + iArr[i2]));
                }
                if (DEBUG) {
                    Log.v(TAG, "saveAllState: adding fragment #" + i2 + ": " + this.mAdded.get(i2));
                }
            }
        }
        if (this.mBackStack != null && (size = this.mBackStack.size()) > 0) {
            backStackStateArr = new BackStackState[size];
            for (int i3 = 0; i3 < size; i3++) {
                backStackStateArr[i3] = new BackStackState(this.mBackStack.get(i3));
                if (DEBUG) {
                    Log.v(TAG, "saveAllState: adding back stack #" + i3 + ": " + this.mBackStack.get(i3));
                }
            }
        }
        FragmentManagerState fragmentManagerState = new FragmentManagerState();
        fragmentManagerState.mActive = fragmentStateArr;
        fragmentManagerState.mAdded = iArr;
        fragmentManagerState.mBackStack = backStackStateArr;
        return fragmentManagerState;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void restoreAllState(Parcelable parcelable, List<Fragment> list) {
        if (parcelable == null) {
            return;
        }
        FragmentManagerState fragmentManagerState = (FragmentManagerState) parcelable;
        if (fragmentManagerState.mActive == null) {
            return;
        }
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                Fragment fragment = list.get(i);
                if (DEBUG) {
                    Log.v(TAG, "restoreAllState: re-attaching retained " + fragment);
                }
                FragmentState fragmentState = fragmentManagerState.mActive[fragment.mIndex];
                fragmentState.mInstance = fragment;
                fragment.mSavedViewState = null;
                fragment.mBackStackNesting = 0;
                fragment.mInLayout = false;
                fragment.mAdded = false;
                fragment.mTarget = null;
                if (fragmentState.mSavedFragmentState != null) {
                    fragmentState.mSavedFragmentState.setClassLoader(this.mHost.getContext().getClassLoader());
                    fragment.mSavedViewState = fragmentState.mSavedFragmentState.getSparseParcelableArray(VIEW_STATE_TAG);
                    fragment.mSavedFragmentState = fragmentState.mSavedFragmentState;
                }
            }
        }
        this.mActive = new ArrayList<>(fragmentManagerState.mActive.length);
        if (this.mAvailIndices != null) {
            this.mAvailIndices.clear();
        }
        for (int i2 = 0; i2 < fragmentManagerState.mActive.length; i2++) {
            FragmentState fragmentState2 = fragmentManagerState.mActive[i2];
            if (fragmentState2 != null) {
                Fragment instantiate = fragmentState2.instantiate(this.mHost, this.mParent);
                if (DEBUG) {
                    Log.v(TAG, "restoreAllState: active #" + i2 + ": " + instantiate);
                }
                this.mActive.add(instantiate);
                fragmentState2.mInstance = null;
            } else {
                this.mActive.add(null);
                if (this.mAvailIndices == null) {
                    this.mAvailIndices = new ArrayList<>();
                }
                if (DEBUG) {
                    Log.v(TAG, "restoreAllState: avail #" + i2);
                }
                this.mAvailIndices.add(Integer.valueOf(i2));
            }
        }
        if (list != null) {
            for (int i3 = 0; i3 < list.size(); i3++) {
                Fragment fragment2 = list.get(i3);
                if (fragment2.mTargetIndex >= 0) {
                    if (fragment2.mTargetIndex < this.mActive.size()) {
                        fragment2.mTarget = this.mActive.get(fragment2.mTargetIndex);
                    } else {
                        Log.w(TAG, "Re-attaching retained fragment " + fragment2 + " target no longer exists: " + fragment2.mTargetIndex);
                        fragment2.mTarget = null;
                    }
                }
            }
        }
        if (fragmentManagerState.mAdded != null) {
            this.mAdded = new ArrayList<>(fragmentManagerState.mAdded.length);
            for (int i4 = 0; i4 < fragmentManagerState.mAdded.length; i4++) {
                Fragment fragment3 = this.mActive.get(fragmentManagerState.mAdded[i4]);
                if (fragment3 == null) {
                    throwException(new IllegalStateException("No instantiated fragment for index #" + fragmentManagerState.mAdded[i4]));
                }
                fragment3.mAdded = true;
                if (DEBUG) {
                    Log.v(TAG, "restoreAllState: added #" + i4 + ": " + fragment3);
                }
                if (this.mAdded.contains(fragment3)) {
                    throw new IllegalStateException("Already added!");
                }
                this.mAdded.add(fragment3);
            }
        } else {
            this.mAdded = null;
        }
        if (fragmentManagerState.mBackStack != null) {
            this.mBackStack = new ArrayList<>(fragmentManagerState.mBackStack.length);
            for (int i5 = 0; i5 < fragmentManagerState.mBackStack.length; i5++) {
                BackStackRecord instantiate2 = fragmentManagerState.mBackStack[i5].instantiate(this);
                if (DEBUG) {
                    Log.v(TAG, "restoreAllState: back stack #" + i5 + " (index " + instantiate2.mIndex + "): " + instantiate2);
                    instantiate2.dump("  ", new PrintWriter(new LogWriter(TAG)), false);
                }
                this.mBackStack.add(instantiate2);
                if (instantiate2.mIndex >= 0) {
                    setBackStackIndex(instantiate2.mIndex, instantiate2);
                }
            }
            return;
        }
        this.mBackStack = null;
    }

    public void attachController(FragmentHostCallback fragmentHostCallback, FragmentContainer fragmentContainer, Fragment fragment) {
        if (this.mHost != null) {
            throw new IllegalStateException("Already attached");
        }
        this.mHost = fragmentHostCallback;
        this.mContainer = fragmentContainer;
        this.mParent = fragment;
    }

    public void noteStateNotSaved() {
        this.mStateSaved = false;
    }

    public void dispatchCreate() {
        this.mStateSaved = false;
        moveToState(1, false);
    }

    public void dispatchActivityCreated() {
        this.mStateSaved = false;
        moveToState(2, false);
    }

    public void dispatchStart() {
        this.mStateSaved = false;
        moveToState(4, false);
    }

    public void dispatchResume() {
        this.mStateSaved = false;
        moveToState(5, false);
    }

    public void dispatchPause() {
        moveToState(4, false);
    }

    public void dispatchStop() {
        this.mStateSaved = true;
        moveToState(3, false);
    }

    public void dispatchReallyStop() {
        moveToState(2, false);
    }

    public void dispatchDestroyView() {
        moveToState(1, false);
    }

    public void dispatchDestroy() {
        this.mDestroyed = true;
        execPendingActions();
        moveToState(0, false);
        this.mHost = null;
        this.mContainer = null;
        this.mParent = null;
    }

    public void dispatchConfigurationChanged(Configuration configuration) {
        if (this.mAdded != null) {
            for (int i = 0; i < this.mAdded.size(); i++) {
                Fragment fragment = this.mAdded.get(i);
                if (fragment != null) {
                    fragment.performConfigurationChanged(configuration);
                }
            }
        }
    }

    public void dispatchLowMemory() {
        if (this.mAdded != null) {
            for (int i = 0; i < this.mAdded.size(); i++) {
                Fragment fragment = this.mAdded.get(i);
                if (fragment != null) {
                    fragment.performLowMemory();
                }
            }
        }
    }

    public boolean dispatchCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        boolean z;
        ArrayList<Fragment> arrayList = null;
        if (this.mAdded != null) {
            z = false;
            for (int i = 0; i < this.mAdded.size(); i++) {
                Fragment fragment = this.mAdded.get(i);
                if (fragment != null && fragment.performCreateOptionsMenu(menu, menuInflater)) {
                    if (arrayList == null) {
                        arrayList = new ArrayList<>();
                    }
                    arrayList.add(fragment);
                    z = true;
                }
            }
        } else {
            z = false;
        }
        if (this.mCreatedMenus != null) {
            for (int i2 = 0; i2 < this.mCreatedMenus.size(); i2++) {
                Fragment fragment2 = this.mCreatedMenus.get(i2);
                if (arrayList == null || !arrayList.contains(fragment2)) {
                    fragment2.onDestroyOptionsMenu();
                }
            }
        }
        this.mCreatedMenus = arrayList;
        return z;
    }

    public boolean dispatchPrepareOptionsMenu(Menu menu) {
        if (this.mAdded == null) {
            return false;
        }
        boolean z = false;
        for (int i = 0; i < this.mAdded.size(); i++) {
            Fragment fragment = this.mAdded.get(i);
            if (fragment != null && fragment.performPrepareOptionsMenu(menu)) {
                z = true;
            }
        }
        return z;
    }

    public boolean dispatchOptionsItemSelected(MenuItem menuItem) {
        if (this.mAdded != null) {
            for (int i = 0; i < this.mAdded.size(); i++) {
                Fragment fragment = this.mAdded.get(i);
                if (fragment != null && fragment.performOptionsItemSelected(menuItem)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean dispatchContextItemSelected(MenuItem menuItem) {
        if (this.mAdded != null) {
            for (int i = 0; i < this.mAdded.size(); i++) {
                Fragment fragment = this.mAdded.get(i);
                if (fragment != null && fragment.performContextItemSelected(menuItem)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void dispatchOptionsMenuClosed(Menu menu) {
        if (this.mAdded != null) {
            for (int i = 0; i < this.mAdded.size(); i++) {
                Fragment fragment = this.mAdded.get(i);
                if (fragment != null) {
                    fragment.performOptionsMenuClosed(menu);
                }
            }
        }
    }

    @Override // android.support.v4.view.LayoutInflaterFactory
    public View onCreateView(View view, String str, Context context, AttributeSet attributeSet) {
        Fragment fragment;
        if (!"fragment".equals(str)) {
            return null;
        }
        String attributeValue = attributeSet.getAttributeValue(null, "class");
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, FragmentTag.Fragment);
        if (attributeValue == null) {
            attributeValue = obtainStyledAttributes.getString(0);
        }
        String str2 = attributeValue;
        int resourceId = obtainStyledAttributes.getResourceId(1, -1);
        String string = obtainStyledAttributes.getString(2);
        obtainStyledAttributes.recycle();
        if (!Fragment.isSupportFragmentClass(this.mHost.getContext(), str2)) {
            return null;
        }
        int id = view != null ? view.getId() : 0;
        if (id == -1 && resourceId == -1 && string == null) {
            throw new IllegalArgumentException(attributeSet.getPositionDescription() + ": Must specify unique android:id, android:tag, or have a parent with an id for " + str2);
        }
        Fragment findFragmentById = resourceId != -1 ? findFragmentById(resourceId) : null;
        if (findFragmentById == null && string != null) {
            findFragmentById = findFragmentByTag(string);
        }
        if (findFragmentById == null && id != -1) {
            findFragmentById = findFragmentById(id);
        }
        if (DEBUG) {
            Log.v(TAG, "onCreateView: id=0x" + Integer.toHexString(resourceId) + " fname=" + str2 + " existing=" + findFragmentById);
        }
        if (findFragmentById == null) {
            Fragment instantiate = Fragment.instantiate(context, str2);
            instantiate.mFromLayout = true;
            instantiate.mFragmentId = resourceId != 0 ? resourceId : id;
            instantiate.mContainerId = id;
            instantiate.mTag = string;
            instantiate.mInLayout = true;
            instantiate.mFragmentManager = this;
            instantiate.mHost = this.mHost;
            instantiate.onInflate(this.mHost.getContext(), attributeSet, instantiate.mSavedFragmentState);
            addFragment(instantiate, true);
            fragment = instantiate;
        } else {
            if (findFragmentById.mInLayout) {
                throw new IllegalArgumentException(attributeSet.getPositionDescription() + ": Duplicate id 0x" + Integer.toHexString(resourceId) + ", tag " + string + ", or parent id 0x" + Integer.toHexString(id) + " with another fragment for " + str2);
            }
            findFragmentById.mInLayout = true;
            if (!findFragmentById.mRetaining) {
                findFragmentById.onInflate(this.mHost.getContext(), attributeSet, findFragmentById.mSavedFragmentState);
            }
            fragment = findFragmentById;
        }
        if (this.mCurState < 1 && fragment.mFromLayout) {
            moveToState(fragment, 1, 0, 0, false);
        } else {
            moveToState(fragment);
        }
        if (fragment.mView == null) {
            throw new IllegalStateException("Fragment " + str2 + " did not create a view.");
        }
        if (resourceId != 0) {
            fragment.mView.setId(resourceId);
        }
        if (fragment.mView.getTag() == null) {
            fragment.mView.setTag(string);
        }
        return fragment.mView;
    }

    /* compiled from: FragmentManager.java */
    /* loaded from: classes.dex */
    static class FragmentTag {
        public static final int[] Fragment = {R.attr.name, R.attr.id, R.attr.tag};
        public static final int Fragment_id = 1;
        public static final int Fragment_name = 0;
        public static final int Fragment_tag = 2;

        FragmentTag() {
        }
    }
}
