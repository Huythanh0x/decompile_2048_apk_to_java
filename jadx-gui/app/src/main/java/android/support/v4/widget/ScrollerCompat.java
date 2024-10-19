package android.support.v4.widget;

import android.content.Context;
import android.os.Build;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/* loaded from: classes.dex */
public class ScrollerCompat {
    static final int CHASE_FRAME_TIME = 16;
    private static final String TAG = "ScrollerCompat";
    ScrollerCompatImpl mImpl;
    Object mScroller;

    /* loaded from: classes.dex */
    interface ScrollerCompatImpl {
        void abortAnimation(Object obj);

        boolean computeScrollOffset(Object obj);

        Object createScroller(Context context, Interpolator interpolator);

        void fling(Object obj, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8);

        void fling(Object obj, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10);

        float getCurrVelocity(Object obj);

        int getCurrX(Object obj);

        int getCurrY(Object obj);

        int getFinalX(Object obj);

        int getFinalY(Object obj);

        boolean isFinished(Object obj);

        boolean isOverScrolled(Object obj);

        void notifyHorizontalEdgeReached(Object obj, int i, int i2, int i3);

        void notifyVerticalEdgeReached(Object obj, int i, int i2, int i3);

        boolean springBack(Object obj, int i, int i2, int i3, int i4, int i5, int i6);

        void startScroll(Object obj, int i, int i2, int i3, int i4);

        void startScroll(Object obj, int i, int i2, int i3, int i4, int i5);
    }

    /* loaded from: classes.dex */
    static class ScrollerCompatImplBase implements ScrollerCompatImpl {
        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public float getCurrVelocity(Object obj) {
            return 0.0f;
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public boolean isOverScrolled(Object obj) {
            return false;
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public void notifyHorizontalEdgeReached(Object obj, int i, int i2, int i3) {
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public void notifyVerticalEdgeReached(Object obj, int i, int i2, int i3) {
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public boolean springBack(Object obj, int i, int i2, int i3, int i4, int i5, int i6) {
            return false;
        }

        ScrollerCompatImplBase() {
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public Object createScroller(Context context, Interpolator interpolator) {
            return interpolator != null ? new Scroller(context, interpolator) : new Scroller(context);
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public boolean isFinished(Object obj) {
            return ((Scroller) obj).isFinished();
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public int getCurrX(Object obj) {
            return ((Scroller) obj).getCurrX();
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public int getCurrY(Object obj) {
            return ((Scroller) obj).getCurrY();
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public boolean computeScrollOffset(Object obj) {
            return ((Scroller) obj).computeScrollOffset();
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public void startScroll(Object obj, int i, int i2, int i3, int i4) {
            ((Scroller) obj).startScroll(i, i2, i3, i4);
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public void startScroll(Object obj, int i, int i2, int i3, int i4, int i5) {
            ((Scroller) obj).startScroll(i, i2, i3, i4, i5);
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public void fling(Object obj, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            ((Scroller) obj).fling(i, i2, i3, i4, i5, i6, i7, i8);
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public void fling(Object obj, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10) {
            ((Scroller) obj).fling(i, i2, i3, i4, i5, i6, i7, i8);
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public void abortAnimation(Object obj) {
            ((Scroller) obj).abortAnimation();
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public int getFinalX(Object obj) {
            return ((Scroller) obj).getFinalX();
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public int getFinalY(Object obj) {
            return ((Scroller) obj).getFinalY();
        }
    }

    /* loaded from: classes.dex */
    static class ScrollerCompatImplGingerbread implements ScrollerCompatImpl {
        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public float getCurrVelocity(Object obj) {
            return 0.0f;
        }

        ScrollerCompatImplGingerbread() {
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public Object createScroller(Context context, Interpolator interpolator) {
            return ScrollerCompatGingerbread.createScroller(context, interpolator);
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public boolean isFinished(Object obj) {
            return ScrollerCompatGingerbread.isFinished(obj);
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public int getCurrX(Object obj) {
            return ScrollerCompatGingerbread.getCurrX(obj);
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public int getCurrY(Object obj) {
            return ScrollerCompatGingerbread.getCurrY(obj);
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public boolean computeScrollOffset(Object obj) {
            return ScrollerCompatGingerbread.computeScrollOffset(obj);
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public void startScroll(Object obj, int i, int i2, int i3, int i4) {
            ScrollerCompatGingerbread.startScroll(obj, i, i2, i3, i4);
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public void startScroll(Object obj, int i, int i2, int i3, int i4, int i5) {
            ScrollerCompatGingerbread.startScroll(obj, i, i2, i3, i4, i5);
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public void fling(Object obj, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            ScrollerCompatGingerbread.fling(obj, i, i2, i3, i4, i5, i6, i7, i8);
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public void fling(Object obj, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10) {
            ScrollerCompatGingerbread.fling(obj, i, i2, i3, i4, i5, i6, i7, i8, i9, i10);
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public void abortAnimation(Object obj) {
            ScrollerCompatGingerbread.abortAnimation(obj);
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public void notifyHorizontalEdgeReached(Object obj, int i, int i2, int i3) {
            ScrollerCompatGingerbread.notifyHorizontalEdgeReached(obj, i, i2, i3);
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public void notifyVerticalEdgeReached(Object obj, int i, int i2, int i3) {
            ScrollerCompatGingerbread.notifyVerticalEdgeReached(obj, i, i2, i3);
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public boolean isOverScrolled(Object obj) {
            return ScrollerCompatGingerbread.isOverScrolled(obj);
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public int getFinalX(Object obj) {
            return ScrollerCompatGingerbread.getFinalX(obj);
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public int getFinalY(Object obj) {
            return ScrollerCompatGingerbread.getFinalY(obj);
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public boolean springBack(Object obj, int i, int i2, int i3, int i4, int i5, int i6) {
            return ScrollerCompatGingerbread.springBack(obj, i, i2, i3, i4, i5, i6);
        }
    }

    /* loaded from: classes.dex */
    static class ScrollerCompatImplIcs extends ScrollerCompatImplGingerbread {
        ScrollerCompatImplIcs() {
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImplGingerbread, android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public float getCurrVelocity(Object obj) {
            return ScrollerCompatIcs.getCurrVelocity(obj);
        }
    }

    public static ScrollerCompat create(Context context) {
        return create(context, null);
    }

    public static ScrollerCompat create(Context context, Interpolator interpolator) {
        return new ScrollerCompat(context, interpolator);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ScrollerCompat(Context context, Interpolator interpolator) {
        this(Build.VERSION.SDK_INT, context, interpolator);
    }

    private ScrollerCompat(int i, Context context, Interpolator interpolator) {
        if (i >= 14) {
            this.mImpl = new ScrollerCompatImplIcs();
        } else if (i >= 9) {
            this.mImpl = new ScrollerCompatImplGingerbread();
        } else {
            this.mImpl = new ScrollerCompatImplBase();
        }
        this.mScroller = this.mImpl.createScroller(context, interpolator);
    }

    public boolean isFinished() {
        return this.mImpl.isFinished(this.mScroller);
    }

    public int getCurrX() {
        return this.mImpl.getCurrX(this.mScroller);
    }

    public int getCurrY() {
        return this.mImpl.getCurrY(this.mScroller);
    }

    public int getFinalX() {
        return this.mImpl.getFinalX(this.mScroller);
    }

    public int getFinalY() {
        return this.mImpl.getFinalY(this.mScroller);
    }

    public float getCurrVelocity() {
        return this.mImpl.getCurrVelocity(this.mScroller);
    }

    public boolean computeScrollOffset() {
        return this.mImpl.computeScrollOffset(this.mScroller);
    }

    public void startScroll(int i, int i2, int i3, int i4) {
        this.mImpl.startScroll(this.mScroller, i, i2, i3, i4);
    }

    public void startScroll(int i, int i2, int i3, int i4, int i5) {
        this.mImpl.startScroll(this.mScroller, i, i2, i3, i4, i5);
    }

    public void fling(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        this.mImpl.fling(this.mScroller, i, i2, i3, i4, i5, i6, i7, i8);
    }

    public void fling(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10) {
        this.mImpl.fling(this.mScroller, i, i2, i3, i4, i5, i6, i7, i8, i9, i10);
    }

    public boolean springBack(int i, int i2, int i3, int i4, int i5, int i6) {
        return this.mImpl.springBack(this.mScroller, i, i2, i3, i4, i5, i6);
    }

    public void abortAnimation() {
        this.mImpl.abortAnimation(this.mScroller);
    }

    public void notifyHorizontalEdgeReached(int i, int i2, int i3) {
        this.mImpl.notifyHorizontalEdgeReached(this.mScroller, i, i2, i3);
    }

    public void notifyVerticalEdgeReached(int i, int i2, int i3) {
        this.mImpl.notifyVerticalEdgeReached(this.mScroller, i, i2, i3);
    }

    public boolean isOverScrolled() {
        return this.mImpl.isOverScrolled(this.mScroller);
    }
}
