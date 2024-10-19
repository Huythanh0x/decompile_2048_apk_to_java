package android.support.design.widget;

import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/* loaded from: classes.dex */
public class SwipeDismissBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {
    private static final float DEFAULT_ALPHA_END_DISTANCE = 0.5f;
    private static final float DEFAULT_ALPHA_START_DISTANCE = 0.0f;
    private static final float DEFAULT_DRAG_DISMISS_THRESHOLD = 0.5f;
    public static final int STATE_DRAGGING = 1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_SETTLING = 2;
    public static final int SWIPE_DIRECTION_ANY = 2;
    public static final int SWIPE_DIRECTION_END_TO_START = 1;
    public static final int SWIPE_DIRECTION_START_TO_END = 0;
    private boolean mIgnoreEvents;
    private OnDismissListener mListener;
    private boolean mSensitivitySet;
    private ViewDragHelper mViewDragHelper;
    private float mSensitivity = 0.0f;
    private int mSwipeDirection = 2;
    private float mDragDismissThreshold = 0.5f;
    private float mAlphaStartSwipeDistance = 0.0f;
    private float mAlphaEndSwipeDistance = 0.5f;
    private final ViewDragHelper.Callback mDragCallback = new ViewDragHelper.Callback() { // from class: android.support.design.widget.SwipeDismissBehavior.1
        private int mOriginalCapturedViewLeft;

        @Override // android.support.v4.widget.ViewDragHelper.Callback
        public boolean tryCaptureView(View view, int i) {
            return SwipeDismissBehavior.this.canSwipeDismissView(view);
        }

        @Override // android.support.v4.widget.ViewDragHelper.Callback
        public void onViewCaptured(View view, int i) {
            this.mOriginalCapturedViewLeft = view.getLeft();
        }

        @Override // android.support.v4.widget.ViewDragHelper.Callback
        public void onViewDragStateChanged(int i) {
            if (SwipeDismissBehavior.this.mListener != null) {
                SwipeDismissBehavior.this.mListener.onDragStateChanged(i);
            }
        }

        @Override // android.support.v4.widget.ViewDragHelper.Callback
        public void onViewReleased(View view, float f, float f2) {
            int i;
            boolean z;
            int width = view.getWidth();
            if (shouldDismiss(view, f)) {
                i = view.getLeft() < this.mOriginalCapturedViewLeft ? this.mOriginalCapturedViewLeft - width : this.mOriginalCapturedViewLeft + width;
                z = true;
            } else {
                i = this.mOriginalCapturedViewLeft;
                z = false;
            }
            if (!SwipeDismissBehavior.this.mViewDragHelper.settleCapturedViewAt(i, view.getTop())) {
                if (!z || SwipeDismissBehavior.this.mListener == null) {
                    return;
                }
                SwipeDismissBehavior.this.mListener.onDismiss(view);
                return;
            }
            ViewCompat.postOnAnimation(view, new SettleRunnable(view, z));
        }

        private boolean shouldDismiss(View view, float f) {
            if (f == 0.0f) {
                return Math.abs(view.getLeft() - this.mOriginalCapturedViewLeft) >= Math.round(((float) view.getWidth()) * SwipeDismissBehavior.this.mDragDismissThreshold);
            }
            boolean z = ViewCompat.getLayoutDirection(view) == 1;
            if (SwipeDismissBehavior.this.mSwipeDirection == 2) {
                return true;
            }
            if (SwipeDismissBehavior.this.mSwipeDirection == 0) {
                if (z) {
                    if (f >= 0.0f) {
                        return false;
                    }
                } else if (f <= 0.0f) {
                    return false;
                }
                return true;
            }
            if (SwipeDismissBehavior.this.mSwipeDirection != 1) {
                return false;
            }
            if (z) {
                if (f <= 0.0f) {
                    return false;
                }
            } else if (f >= 0.0f) {
                return false;
            }
            return true;
        }

        @Override // android.support.v4.widget.ViewDragHelper.Callback
        public int getViewHorizontalDragRange(View view) {
            return view.getWidth();
        }

        @Override // android.support.v4.widget.ViewDragHelper.Callback
        public int clampViewPositionHorizontal(View view, int i, int i2) {
            int width;
            int width2;
            boolean z = ViewCompat.getLayoutDirection(view) == 1;
            if (SwipeDismissBehavior.this.mSwipeDirection == 0) {
                if (z) {
                    width = this.mOriginalCapturedViewLeft - view.getWidth();
                    width2 = this.mOriginalCapturedViewLeft;
                } else {
                    width = this.mOriginalCapturedViewLeft;
                    width2 = view.getWidth() + this.mOriginalCapturedViewLeft;
                }
            } else if (SwipeDismissBehavior.this.mSwipeDirection != 1) {
                width = this.mOriginalCapturedViewLeft - view.getWidth();
                width2 = view.getWidth() + this.mOriginalCapturedViewLeft;
            } else if (z) {
                width = this.mOriginalCapturedViewLeft;
                width2 = view.getWidth() + this.mOriginalCapturedViewLeft;
            } else {
                width = this.mOriginalCapturedViewLeft - view.getWidth();
                width2 = this.mOriginalCapturedViewLeft;
            }
            return SwipeDismissBehavior.clamp(width, i, width2);
        }

        @Override // android.support.v4.widget.ViewDragHelper.Callback
        public int clampViewPositionVertical(View view, int i, int i2) {
            return view.getTop();
        }

        @Override // android.support.v4.widget.ViewDragHelper.Callback
        public void onViewPositionChanged(View view, int i, int i2, int i3, int i4) {
            float width = this.mOriginalCapturedViewLeft + (view.getWidth() * SwipeDismissBehavior.this.mAlphaStartSwipeDistance);
            float width2 = this.mOriginalCapturedViewLeft + (view.getWidth() * SwipeDismissBehavior.this.mAlphaEndSwipeDistance);
            float f = i;
            if (f <= width) {
                ViewCompat.setAlpha(view, 1.0f);
            } else if (f < width2) {
                ViewCompat.setAlpha(view, SwipeDismissBehavior.clamp(0.0f, 1.0f - SwipeDismissBehavior.fraction(width, width2, f), 1.0f));
            } else {
                ViewCompat.setAlpha(view, 0.0f);
            }
        }
    };

    /* loaded from: classes.dex */
    public interface OnDismissListener {
        void onDismiss(View view);

        void onDragStateChanged(int i);
    }

    static float fraction(float f, float f2, float f3) {
        return (f3 - f) / (f2 - f);
    }

    public boolean canSwipeDismissView(@NonNull View view) {
        return true;
    }

    public void setListener(OnDismissListener onDismissListener) {
        this.mListener = onDismissListener;
    }

    public void setSwipeDirection(int i) {
        this.mSwipeDirection = i;
    }

    public void setDragDismissDistance(float f) {
        this.mDragDismissThreshold = clamp(0.0f, f, 1.0f);
    }

    public void setStartAlphaSwipeDistance(float f) {
        this.mAlphaStartSwipeDistance = clamp(0.0f, f, 1.0f);
    }

    public void setEndAlphaSwipeDistance(float f) {
        this.mAlphaEndSwipeDistance = clamp(0.0f, f, 1.0f);
    }

    public void setSensitivity(float f) {
        this.mSensitivity = f;
        this.mSensitivitySet = true;
    }

    @Override // android.support.design.widget.CoordinatorLayout.Behavior
    public boolean onInterceptTouchEvent(CoordinatorLayout coordinatorLayout, V v, MotionEvent motionEvent) {
        int actionMasked = MotionEventCompat.getActionMasked(motionEvent);
        if (actionMasked == 1 || actionMasked == 3) {
            if (this.mIgnoreEvents) {
                this.mIgnoreEvents = false;
                return false;
            }
        } else {
            this.mIgnoreEvents = !coordinatorLayout.isPointInChildBounds(v, (int) motionEvent.getX(), (int) motionEvent.getY());
        }
        if (this.mIgnoreEvents) {
            return false;
        }
        ensureViewDragHelper(coordinatorLayout);
        return this.mViewDragHelper.shouldInterceptTouchEvent(motionEvent);
    }

    @Override // android.support.design.widget.CoordinatorLayout.Behavior
    public boolean onTouchEvent(CoordinatorLayout coordinatorLayout, V v, MotionEvent motionEvent) {
        if (this.mViewDragHelper == null) {
            return false;
        }
        this.mViewDragHelper.processTouchEvent(motionEvent);
        return true;
    }

    private void ensureViewDragHelper(ViewGroup viewGroup) {
        if (this.mViewDragHelper == null) {
            this.mViewDragHelper = this.mSensitivitySet ? ViewDragHelper.create(viewGroup, this.mSensitivity, this.mDragCallback) : ViewDragHelper.create(viewGroup, this.mDragCallback);
        }
    }

    /* loaded from: classes.dex */
    private class SettleRunnable implements Runnable {
        private final boolean mDismiss;
        private final View mView;

        SettleRunnable(View view, boolean z) {
            this.mView = view;
            this.mDismiss = z;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (SwipeDismissBehavior.this.mViewDragHelper != null && SwipeDismissBehavior.this.mViewDragHelper.continueSettling(true)) {
                ViewCompat.postOnAnimation(this.mView, this);
            } else {
                if (!this.mDismiss || SwipeDismissBehavior.this.mListener == null) {
                    return;
                }
                SwipeDismissBehavior.this.mListener.onDismiss(this.mView);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static float clamp(float f, float f2, float f3) {
        return Math.min(Math.max(f, f2), f3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int clamp(int i, int i2, int i3) {
        return Math.min(Math.max(i, i2), i3);
    }

    public int getDragState() {
        if (this.mViewDragHelper != null) {
            return this.mViewDragHelper.getViewDragState();
        }
        return 0;
    }
}
