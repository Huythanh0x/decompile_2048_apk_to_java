package android.support.design.widget;

import android.view.animation.Interpolator;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class ValueAnimatorCompat {
    private final Impl mImpl;

    /* loaded from: classes.dex */
    interface AnimatorListener {
        void onAnimationCancel(ValueAnimatorCompat valueAnimatorCompat);

        void onAnimationEnd(ValueAnimatorCompat valueAnimatorCompat);

        void onAnimationStart(ValueAnimatorCompat valueAnimatorCompat);
    }

    /* loaded from: classes.dex */
    static class AnimatorListenerAdapter implements AnimatorListener {
        @Override // android.support.design.widget.ValueAnimatorCompat.AnimatorListener
        public void onAnimationCancel(ValueAnimatorCompat valueAnimatorCompat) {
        }

        @Override // android.support.design.widget.ValueAnimatorCompat.AnimatorListener
        public void onAnimationEnd(ValueAnimatorCompat valueAnimatorCompat) {
        }

        @Override // android.support.design.widget.ValueAnimatorCompat.AnimatorListener
        public void onAnimationStart(ValueAnimatorCompat valueAnimatorCompat) {
        }
    }

    /* loaded from: classes.dex */
    interface AnimatorUpdateListener {
        void onAnimationUpdate(ValueAnimatorCompat valueAnimatorCompat);
    }

    /* loaded from: classes.dex */
    interface Creator {
        ValueAnimatorCompat createAnimator();
    }

    /* loaded from: classes.dex */
    static abstract class Impl {

        /* loaded from: classes.dex */
        interface AnimatorListenerProxy {
            void onAnimationCancel();

            void onAnimationEnd();

            void onAnimationStart();
        }

        /* loaded from: classes.dex */
        interface AnimatorUpdateListenerProxy {
            void onAnimationUpdate();
        }

        abstract void cancel();

        abstract void end();

        abstract float getAnimatedFloatValue();

        abstract float getAnimatedFraction();

        abstract int getAnimatedIntValue();

        abstract long getDuration();

        abstract boolean isRunning();

        abstract void setDuration(int i);

        abstract void setFloatValues(float f, float f2);

        abstract void setIntValues(int i, int i2);

        abstract void setInterpolator(Interpolator interpolator);

        abstract void setListener(AnimatorListenerProxy animatorListenerProxy);

        abstract void setUpdateListener(AnimatorUpdateListenerProxy animatorUpdateListenerProxy);

        abstract void start();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ValueAnimatorCompat(Impl impl) {
        this.mImpl = impl;
    }

    public void start() {
        this.mImpl.start();
    }

    public boolean isRunning() {
        return this.mImpl.isRunning();
    }

    public void setInterpolator(Interpolator interpolator) {
        this.mImpl.setInterpolator(interpolator);
    }

    public void setUpdateListener(final AnimatorUpdateListener animatorUpdateListener) {
        if (animatorUpdateListener != null) {
            this.mImpl.setUpdateListener(new Impl.AnimatorUpdateListenerProxy() { // from class: android.support.design.widget.ValueAnimatorCompat.1
                @Override // android.support.design.widget.ValueAnimatorCompat.Impl.AnimatorUpdateListenerProxy
                public void onAnimationUpdate() {
                    animatorUpdateListener.onAnimationUpdate(ValueAnimatorCompat.this);
                }
            });
        } else {
            this.mImpl.setUpdateListener(null);
        }
    }

    public void setListener(final AnimatorListener animatorListener) {
        if (animatorListener != null) {
            this.mImpl.setListener(new Impl.AnimatorListenerProxy() { // from class: android.support.design.widget.ValueAnimatorCompat.2
                @Override // android.support.design.widget.ValueAnimatorCompat.Impl.AnimatorListenerProxy
                public void onAnimationStart() {
                    animatorListener.onAnimationStart(ValueAnimatorCompat.this);
                }

                @Override // android.support.design.widget.ValueAnimatorCompat.Impl.AnimatorListenerProxy
                public void onAnimationEnd() {
                    animatorListener.onAnimationEnd(ValueAnimatorCompat.this);
                }

                @Override // android.support.design.widget.ValueAnimatorCompat.Impl.AnimatorListenerProxy
                public void onAnimationCancel() {
                    animatorListener.onAnimationCancel(ValueAnimatorCompat.this);
                }
            });
        } else {
            this.mImpl.setListener(null);
        }
    }

    public void setIntValues(int i, int i2) {
        this.mImpl.setIntValues(i, i2);
    }

    public int getAnimatedIntValue() {
        return this.mImpl.getAnimatedIntValue();
    }

    public void setFloatValues(float f, float f2) {
        this.mImpl.setFloatValues(f, f2);
    }

    public float getAnimatedFloatValue() {
        return this.mImpl.getAnimatedFloatValue();
    }

    public void setDuration(int i) {
        this.mImpl.setDuration(i);
    }

    public void cancel() {
        this.mImpl.cancel();
    }

    public float getAnimatedFraction() {
        return this.mImpl.getAnimatedFraction();
    }

    public void end() {
        this.mImpl.end();
    }

    public long getDuration() {
        return this.mImpl.getDuration();
    }
}
