package android.support.v4.widget;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewParentCompat;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityManagerCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityNodeProviderCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/* loaded from: classes.dex */
public abstract class ExploreByTouchHelper extends AccessibilityDelegateCompat {
    private static final String DEFAULT_CLASS_NAME = View.class.getName();
    public static final int HOST_ID = -1;
    public static final int INVALID_ID = Integer.MIN_VALUE;
    private final AccessibilityManager mManager;
    private ExploreByTouchNodeProvider mNodeProvider;
    private final View mView;
    private final Rect mTempScreenRect = new Rect();
    private final Rect mTempParentRect = new Rect();
    private final Rect mTempVisibleRect = new Rect();
    private final int[] mTempGlobalRect = new int[2];
    private int mFocusedVirtualViewId = Integer.MIN_VALUE;
    private int mHoveredVirtualViewId = Integer.MIN_VALUE;

    protected abstract int getVirtualViewAt(float f, float f2);

    protected abstract void getVisibleVirtualViews(List<Integer> list);

    protected abstract boolean onPerformActionForVirtualView(int i, int i2, Bundle bundle);

    protected abstract void onPopulateEventForVirtualView(int i, AccessibilityEvent accessibilityEvent);

    public void onPopulateNodeForHost(AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
    }

    protected abstract void onPopulateNodeForVirtualView(int i, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat);

    public ExploreByTouchHelper(View view) {
        if (view == null) {
            throw new IllegalArgumentException("View may not be null");
        }
        this.mView = view;
        this.mManager = (AccessibilityManager) view.getContext().getSystemService("accessibility");
    }

    @Override // android.support.v4.view.AccessibilityDelegateCompat
    public AccessibilityNodeProviderCompat getAccessibilityNodeProvider(View view) {
        if (this.mNodeProvider == null) {
            this.mNodeProvider = new ExploreByTouchNodeProvider();
        }
        return this.mNodeProvider;
    }

    public boolean dispatchHoverEvent(MotionEvent motionEvent) {
        if (!this.mManager.isEnabled() || !AccessibilityManagerCompat.isTouchExplorationEnabled(this.mManager)) {
            return false;
        }
        int action = motionEvent.getAction();
        if (action != 7) {
            switch (action) {
                case 9:
                    break;
                case 10:
                    if (this.mFocusedVirtualViewId == Integer.MIN_VALUE) {
                        return false;
                    }
                    updateHoveredVirtualView(Integer.MIN_VALUE);
                    return true;
                default:
                    return false;
            }
        }
        int virtualViewAt = getVirtualViewAt(motionEvent.getX(), motionEvent.getY());
        updateHoveredVirtualView(virtualViewAt);
        return virtualViewAt != Integer.MIN_VALUE;
    }

    public boolean sendEventForVirtualView(int i, int i2) {
        ViewParent parent;
        if (i == Integer.MIN_VALUE || !this.mManager.isEnabled() || (parent = this.mView.getParent()) == null) {
            return false;
        }
        return ViewParentCompat.requestSendAccessibilityEvent(parent, this.mView, createEvent(i, i2));
    }

    public void invalidateRoot() {
        invalidateVirtualView(-1);
    }

    public void invalidateVirtualView(int i) {
        sendEventForVirtualView(i, 2048);
    }

    public int getFocusedVirtualView() {
        return this.mFocusedVirtualViewId;
    }

    private void updateHoveredVirtualView(int i) {
        if (this.mHoveredVirtualViewId == i) {
            return;
        }
        int i2 = this.mHoveredVirtualViewId;
        this.mHoveredVirtualViewId = i;
        sendEventForVirtualView(i, 128);
        sendEventForVirtualView(i2, 256);
    }

    private AccessibilityEvent createEvent(int i, int i2) {
        if (i == -1) {
            return createEventForHost(i2);
        }
        return createEventForChild(i, i2);
    }

    private AccessibilityEvent createEventForHost(int i) {
        AccessibilityEvent obtain = AccessibilityEvent.obtain(i);
        ViewCompat.onInitializeAccessibilityEvent(this.mView, obtain);
        return obtain;
    }

    private AccessibilityEvent createEventForChild(int i, int i2) {
        AccessibilityEvent obtain = AccessibilityEvent.obtain(i2);
        obtain.setEnabled(true);
        obtain.setClassName(DEFAULT_CLASS_NAME);
        onPopulateEventForVirtualView(i, obtain);
        if (obtain.getText().isEmpty() && obtain.getContentDescription() == null) {
            throw new RuntimeException("Callbacks must add text or a content description in populateEventForVirtualViewId()");
        }
        obtain.setPackageName(this.mView.getContext().getPackageName());
        AccessibilityEventCompat.asRecord(obtain).setSource(this.mView, i);
        return obtain;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public AccessibilityNodeInfoCompat createNode(int i) {
        if (i == -1) {
            return createNodeForHost();
        }
        return createNodeForChild(i);
    }

    private AccessibilityNodeInfoCompat createNodeForHost() {
        AccessibilityNodeInfoCompat obtain = AccessibilityNodeInfoCompat.obtain(this.mView);
        ViewCompat.onInitializeAccessibilityNodeInfo(this.mView, obtain);
        onPopulateNodeForHost(obtain);
        LinkedList linkedList = new LinkedList();
        getVisibleVirtualViews(linkedList);
        Iterator it = linkedList.iterator();
        while (it.hasNext()) {
            obtain.addChild(this.mView, ((Integer) it.next()).intValue());
        }
        return obtain;
    }

    private AccessibilityNodeInfoCompat createNodeForChild(int i) {
        AccessibilityNodeInfoCompat obtain = AccessibilityNodeInfoCompat.obtain();
        obtain.setEnabled(true);
        obtain.setClassName(DEFAULT_CLASS_NAME);
        onPopulateNodeForVirtualView(i, obtain);
        if (obtain.getText() == null && obtain.getContentDescription() == null) {
            throw new RuntimeException("Callbacks must add text or a content description in populateNodeForVirtualViewId()");
        }
        obtain.getBoundsInParent(this.mTempParentRect);
        if (this.mTempParentRect.isEmpty()) {
            throw new RuntimeException("Callbacks must set parent bounds in populateNodeForVirtualViewId()");
        }
        int actions = obtain.getActions();
        if ((actions & 64) != 0) {
            throw new RuntimeException("Callbacks must not add ACTION_ACCESSIBILITY_FOCUS in populateNodeForVirtualViewId()");
        }
        if ((actions & 128) != 0) {
            throw new RuntimeException("Callbacks must not add ACTION_CLEAR_ACCESSIBILITY_FOCUS in populateNodeForVirtualViewId()");
        }
        obtain.setPackageName(this.mView.getContext().getPackageName());
        obtain.setSource(this.mView, i);
        obtain.setParent(this.mView);
        if (this.mFocusedVirtualViewId == i) {
            obtain.setAccessibilityFocused(true);
            obtain.addAction(128);
        } else {
            obtain.setAccessibilityFocused(false);
            obtain.addAction(64);
        }
        if (intersectVisibleToUser(this.mTempParentRect)) {
            obtain.setVisibleToUser(true);
            obtain.setBoundsInParent(this.mTempParentRect);
        }
        this.mView.getLocationOnScreen(this.mTempGlobalRect);
        int i2 = this.mTempGlobalRect[0];
        int i3 = this.mTempGlobalRect[1];
        this.mTempScreenRect.set(this.mTempParentRect);
        this.mTempScreenRect.offset(i2, i3);
        obtain.setBoundsInScreen(this.mTempScreenRect);
        return obtain;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean performAction(int i, int i2, Bundle bundle) {
        if (i == -1) {
            return performActionForHost(i2, bundle);
        }
        return performActionForChild(i, i2, bundle);
    }

    private boolean performActionForHost(int i, Bundle bundle) {
        return ViewCompat.performAccessibilityAction(this.mView, i, bundle);
    }

    private boolean performActionForChild(int i, int i2, Bundle bundle) {
        if (i2 == 64 || i2 == 128) {
            return manageFocusForChild(i, i2, bundle);
        }
        return onPerformActionForVirtualView(i, i2, bundle);
    }

    private boolean manageFocusForChild(int i, int i2, Bundle bundle) {
        if (i2 == 64) {
            return requestAccessibilityFocus(i);
        }
        if (i2 != 128) {
            return false;
        }
        return clearAccessibilityFocus(i);
    }

    private boolean intersectVisibleToUser(Rect rect) {
        if (rect == null || rect.isEmpty() || this.mView.getWindowVisibility() != 0) {
            return false;
        }
        Object parent = this.mView.getParent();
        while (parent instanceof View) {
            View view = (View) parent;
            if (ViewCompat.getAlpha(view) <= 0.0f || view.getVisibility() != 0) {
                return false;
            }
            parent = view.getParent();
        }
        if (parent != null && this.mView.getLocalVisibleRect(this.mTempVisibleRect)) {
            return rect.intersect(this.mTempVisibleRect);
        }
        return false;
    }

    private boolean isAccessibilityFocused(int i) {
        return this.mFocusedVirtualViewId == i;
    }

    private boolean requestAccessibilityFocus(int i) {
        if (!this.mManager.isEnabled() || !AccessibilityManagerCompat.isTouchExplorationEnabled(this.mManager) || isAccessibilityFocused(i)) {
            return false;
        }
        if (this.mFocusedVirtualViewId != Integer.MIN_VALUE) {
            sendEventForVirtualView(this.mFocusedVirtualViewId, 65536);
        }
        this.mFocusedVirtualViewId = i;
        this.mView.invalidate();
        sendEventForVirtualView(i, 32768);
        return true;
    }

    private boolean clearAccessibilityFocus(int i) {
        if (!isAccessibilityFocused(i)) {
            return false;
        }
        this.mFocusedVirtualViewId = Integer.MIN_VALUE;
        this.mView.invalidate();
        sendEventForVirtualView(i, 65536);
        return true;
    }

    /* loaded from: classes.dex */
    private class ExploreByTouchNodeProvider extends AccessibilityNodeProviderCompat {
        private ExploreByTouchNodeProvider() {
        }

        @Override // android.support.v4.view.accessibility.AccessibilityNodeProviderCompat
        public AccessibilityNodeInfoCompat createAccessibilityNodeInfo(int i) {
            return ExploreByTouchHelper.this.createNode(i);
        }

        @Override // android.support.v4.view.accessibility.AccessibilityNodeProviderCompat
        public boolean performAction(int i, int i2, Bundle bundle) {
            return ExploreByTouchHelper.this.performAction(i, i2, bundle);
        }
    }
}
