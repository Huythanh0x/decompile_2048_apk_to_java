package android.support.design.widget;

import android.util.StateSet;
import android.view.View;
import android.view.animation.Animation;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/* loaded from: classes.dex */
final class StateListAnimator {
    private WeakReference<View> mViewRef;
    private final ArrayList<Tuple> mTuples = new ArrayList<>();
    private Tuple mLastMatch = null;
    private Animation mRunningAnimation = null;
    private Animation.AnimationListener mAnimationListener = new Animation.AnimationListener() { // from class: android.support.design.widget.StateListAnimator.1
        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationRepeat(Animation animation) {
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationStart(Animation animation) {
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationEnd(Animation animation) {
            if (StateListAnimator.this.mRunningAnimation == animation) {
                StateListAnimator.this.mRunningAnimation = null;
            }
        }
    };

    public void addState(int[] iArr, Animation animation) {
        Tuple tuple = new Tuple(iArr, animation);
        animation.setAnimationListener(this.mAnimationListener);
        this.mTuples.add(tuple);
    }

    Animation getRunningAnimation() {
        return this.mRunningAnimation;
    }

    View getTarget() {
        if (this.mViewRef == null) {
            return null;
        }
        return this.mViewRef.get();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setTarget(View view) {
        View target = getTarget();
        if (target == view) {
            return;
        }
        if (target != null) {
            clearTarget();
        }
        if (view != null) {
            this.mViewRef = new WeakReference<>(view);
        }
    }

    private void clearTarget() {
        View target = getTarget();
        int size = this.mTuples.size();
        for (int i = 0; i < size; i++) {
            if (target.getAnimation() == this.mTuples.get(i).mAnimation) {
                target.clearAnimation();
            }
        }
        this.mViewRef = null;
        this.mLastMatch = null;
        this.mRunningAnimation = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setState(int[] iArr) {
        Tuple tuple;
        int size = this.mTuples.size();
        int i = 0;
        while (true) {
            if (i >= size) {
                tuple = null;
                break;
            }
            tuple = this.mTuples.get(i);
            if (StateSet.stateSetMatches(tuple.mSpecs, iArr)) {
                break;
            } else {
                i++;
            }
        }
        if (tuple == this.mLastMatch) {
            return;
        }
        if (this.mLastMatch != null) {
            cancel();
        }
        this.mLastMatch = tuple;
        View view = this.mViewRef.get();
        if (tuple == null || view == null || view.getVisibility() != 0) {
            return;
        }
        start(tuple);
    }

    private void start(Tuple tuple) {
        this.mRunningAnimation = tuple.mAnimation;
        View target = getTarget();
        if (target != null) {
            target.startAnimation(this.mRunningAnimation);
        }
    }

    private void cancel() {
        if (this.mRunningAnimation != null) {
            View target = getTarget();
            if (target != null && target.getAnimation() == this.mRunningAnimation) {
                target.clearAnimation();
            }
            this.mRunningAnimation = null;
        }
    }

    ArrayList<Tuple> getTuples() {
        return this.mTuples;
    }

    public void jumpToCurrentState() {
        View target;
        if (this.mRunningAnimation == null || (target = getTarget()) == null || target.getAnimation() != this.mRunningAnimation) {
            return;
        }
        target.clearAnimation();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class Tuple {
        final Animation mAnimation;
        final int[] mSpecs;

        private Tuple(int[] iArr, Animation animation) {
            this.mSpecs = iArr;
            this.mAnimation = animation;
        }

        int[] getSpecs() {
            return this.mSpecs;
        }

        Animation getAnimation() {
            return this.mAnimation;
        }
    }
}
