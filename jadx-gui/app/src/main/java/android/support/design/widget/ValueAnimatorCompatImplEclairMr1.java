package android.support.design.widget;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.design.widget.ValueAnimatorCompat;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

/* loaded from: classes.dex */
class ValueAnimatorCompatImplEclairMr1 extends ValueAnimatorCompat.Impl {
    private static final int DEFAULT_DURATION = 200;
    private static final int HANDLER_DELAY = 10;
    private static final Handler sHandler = new Handler(Looper.getMainLooper());
    private float mAnimatedFraction;
    private Interpolator mInterpolator;
    private boolean mIsRunning;
    private ValueAnimatorCompat.Impl.AnimatorListenerProxy mListener;
    private long mStartTime;
    private ValueAnimatorCompat.Impl.AnimatorUpdateListenerProxy mUpdateListener;
    private final int[] mIntValues = new int[2];
    private final float[] mFloatValues = new float[2];
    private int mDuration = 200;
    private final Runnable mRunnable = new Runnable() { // from class: android.support.design.widget.ValueAnimatorCompatImplEclairMr1.1
        @Override // java.lang.Runnable
        public void run() {
            ValueAnimatorCompatImplEclairMr1.this.update();
        }
    };

    @Override // android.support.design.widget.ValueAnimatorCompat.Impl
    public void start() {
        if (this.mIsRunning) {
            return;
        }
        if (this.mInterpolator == null) {
            this.mInterpolator = new AccelerateDecelerateInterpolator();
        }
        this.mStartTime = SystemClock.uptimeMillis();
        this.mIsRunning = true;
        if (this.mListener != null) {
            this.mListener.onAnimationStart();
        }
        sHandler.postDelayed(this.mRunnable, 10L);
    }

    @Override // android.support.design.widget.ValueAnimatorCompat.Impl
    public boolean isRunning() {
        return this.mIsRunning;
    }

    @Override // android.support.design.widget.ValueAnimatorCompat.Impl
    public void setInterpolator(Interpolator interpolator) {
        this.mInterpolator = interpolator;
    }

    @Override // android.support.design.widget.ValueAnimatorCompat.Impl
    public void setListener(ValueAnimatorCompat.Impl.AnimatorListenerProxy animatorListenerProxy) {
        this.mListener = animatorListenerProxy;
    }

    @Override // android.support.design.widget.ValueAnimatorCompat.Impl
    public void setUpdateListener(ValueAnimatorCompat.Impl.AnimatorUpdateListenerProxy animatorUpdateListenerProxy) {
        this.mUpdateListener = animatorUpdateListenerProxy;
    }

    @Override // android.support.design.widget.ValueAnimatorCompat.Impl
    public void setIntValues(int i, int i2) {
        this.mIntValues[0] = i;
        this.mIntValues[1] = i2;
    }

    @Override // android.support.design.widget.ValueAnimatorCompat.Impl
    public int getAnimatedIntValue() {
        return AnimationUtils.lerp(this.mIntValues[0], this.mIntValues[1], getAnimatedFraction());
    }

    @Override // android.support.design.widget.ValueAnimatorCompat.Impl
    public void setFloatValues(float f, float f2) {
        this.mFloatValues[0] = f;
        this.mFloatValues[1] = f2;
    }

    @Override // android.support.design.widget.ValueAnimatorCompat.Impl
    public float getAnimatedFloatValue() {
        return AnimationUtils.lerp(this.mFloatValues[0], this.mFloatValues[1], getAnimatedFraction());
    }

    @Override // android.support.design.widget.ValueAnimatorCompat.Impl
    public void setDuration(int i) {
        this.mDuration = i;
    }

    @Override // android.support.design.widget.ValueAnimatorCompat.Impl
    public void cancel() {
        this.mIsRunning = false;
        sHandler.removeCallbacks(this.mRunnable);
        if (this.mListener != null) {
            this.mListener.onAnimationCancel();
        }
    }

    @Override // android.support.design.widget.ValueAnimatorCompat.Impl
    public float getAnimatedFraction() {
        return this.mAnimatedFraction;
    }

    @Override // android.support.design.widget.ValueAnimatorCompat.Impl
    public void end() {
        if (this.mIsRunning) {
            this.mIsRunning = false;
            sHandler.removeCallbacks(this.mRunnable);
            this.mAnimatedFraction = 1.0f;
            if (this.mUpdateListener != null) {
                this.mUpdateListener.onAnimationUpdate();
            }
            if (this.mListener != null) {
                this.mListener.onAnimationEnd();
            }
        }
    }

    @Override // android.support.design.widget.ValueAnimatorCompat.Impl
    public long getDuration() {
        return this.mDuration;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void update() {
        if (this.mIsRunning) {
            float uptimeMillis = ((float) (SystemClock.uptimeMillis() - this.mStartTime)) / this.mDuration;
            if (this.mInterpolator != null) {
                uptimeMillis = this.mInterpolator.getInterpolation(uptimeMillis);
            }
            this.mAnimatedFraction = uptimeMillis;
            if (this.mUpdateListener != null) {
                this.mUpdateListener.onAnimationUpdate();
            }
            if (SystemClock.uptimeMillis() >= this.mStartTime + this.mDuration) {
                this.mIsRunning = false;
                if (this.mListener != null) {
                    this.mListener.onAnimationEnd();
                }
            }
        }
        if (this.mIsRunning) {
            sHandler.postDelayed(this.mRunnable, 10L);
        }
    }
}
