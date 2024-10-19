package android.support.design.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.R;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.SnackbarManager;
import android.support.design.widget.SwipeDismissBehavior;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* loaded from: classes.dex */
public final class Snackbar {
    private static final int ANIMATION_DURATION = 250;
    private static final int ANIMATION_FADE_DURATION = 180;
    public static final int LENGTH_INDEFINITE = -2;
    public static final int LENGTH_LONG = 0;
    public static final int LENGTH_SHORT = -1;
    private static final int MSG_DISMISS = 1;
    private static final int MSG_SHOW = 0;
    private static final Handler sHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() { // from class: android.support.design.widget.Snackbar.1
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    ((Snackbar) message.obj).showView();
                    return true;
                case 1:
                    ((Snackbar) message.obj).hideView(message.arg1);
                    return true;
                default:
                    return false;
            }
        }
    });
    private Callback mCallback;
    private final Context mContext;
    private int mDuration;
    private final SnackbarManager.Callback mManagerCallback = new SnackbarManager.Callback() { // from class: android.support.design.widget.Snackbar.3
        @Override // android.support.design.widget.SnackbarManager.Callback
        public void show() {
            Snackbar.sHandler.sendMessage(Snackbar.sHandler.obtainMessage(0, Snackbar.this));
        }

        @Override // android.support.design.widget.SnackbarManager.Callback
        public void dismiss(int i) {
            Snackbar.sHandler.sendMessage(Snackbar.sHandler.obtainMessage(1, i, 0, Snackbar.this));
        }
    };
    private final ViewGroup mTargetParent;
    private final SnackbarLayout mView;

    /* loaded from: classes.dex */
    public static abstract class Callback {
        public static final int DISMISS_EVENT_ACTION = 1;
        public static final int DISMISS_EVENT_CONSECUTIVE = 4;
        public static final int DISMISS_EVENT_MANUAL = 3;
        public static final int DISMISS_EVENT_SWIPE = 0;
        public static final int DISMISS_EVENT_TIMEOUT = 2;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes.dex */
        public @interface DismissEvent {
        }

        public void onDismissed(Snackbar snackbar, int i) {
        }

        public void onShown(Snackbar snackbar) {
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Duration {
    }

    private Snackbar(ViewGroup viewGroup) {
        this.mTargetParent = viewGroup;
        this.mContext = viewGroup.getContext();
        ThemeUtils.checkAppCompatTheme(this.mContext);
        this.mView = (SnackbarLayout) LayoutInflater.from(this.mContext).inflate(R.layout.design_layout_snackbar, this.mTargetParent, false);
    }

    @NonNull
    public static Snackbar make(@NonNull View view, @NonNull CharSequence charSequence, int i) {
        Snackbar snackbar = new Snackbar(findSuitableParent(view));
        snackbar.setText(charSequence);
        snackbar.setDuration(i);
        return snackbar;
    }

    @NonNull
    public static Snackbar make(@NonNull View view, @StringRes int i, int i2) {
        return make(view, view.getResources().getText(i), i2);
    }

    private static ViewGroup findSuitableParent(View view) {
        ViewGroup viewGroup = null;
        while (!(view instanceof CoordinatorLayout)) {
            if (view instanceof FrameLayout) {
                if (view.getId() == 16908290) {
                    return (ViewGroup) view;
                }
                viewGroup = (ViewGroup) view;
            }
            if (view != null) {
                Object parent = view.getParent();
                view = parent instanceof View ? (View) parent : null;
            }
            if (view == null) {
                return viewGroup;
            }
        }
        return (ViewGroup) view;
    }

    @NonNull
    public Snackbar setAction(@StringRes int i, View.OnClickListener onClickListener) {
        return setAction(this.mContext.getText(i), onClickListener);
    }

    @NonNull
    public Snackbar setAction(CharSequence charSequence, final View.OnClickListener onClickListener) {
        Button actionView = this.mView.getActionView();
        if (TextUtils.isEmpty(charSequence) || onClickListener == null) {
            actionView.setVisibility(8);
            actionView.setOnClickListener(null);
        } else {
            actionView.setVisibility(0);
            actionView.setText(charSequence);
            actionView.setOnClickListener(new View.OnClickListener() { // from class: android.support.design.widget.Snackbar.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    onClickListener.onClick(view);
                    Snackbar.this.dispatchDismiss(1);
                }
            });
        }
        return this;
    }

    @NonNull
    public Snackbar setActionTextColor(ColorStateList colorStateList) {
        this.mView.getActionView().setTextColor(colorStateList);
        return this;
    }

    @NonNull
    public Snackbar setActionTextColor(@ColorInt int i) {
        this.mView.getActionView().setTextColor(i);
        return this;
    }

    @NonNull
    public Snackbar setText(@NonNull CharSequence charSequence) {
        this.mView.getMessageView().setText(charSequence);
        return this;
    }

    @NonNull
    public Snackbar setText(@StringRes int i) {
        return setText(this.mContext.getText(i));
    }

    @NonNull
    public Snackbar setDuration(int i) {
        this.mDuration = i;
        return this;
    }

    public int getDuration() {
        return this.mDuration;
    }

    @NonNull
    public View getView() {
        return this.mView;
    }

    public void show() {
        SnackbarManager.getInstance().show(this.mDuration, this.mManagerCallback);
    }

    public void dismiss() {
        dispatchDismiss(3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchDismiss(int i) {
        SnackbarManager.getInstance().dismiss(this.mManagerCallback, i);
    }

    @NonNull
    public Snackbar setCallback(Callback callback) {
        this.mCallback = callback;
        return this;
    }

    public boolean isShown() {
        return SnackbarManager.getInstance().isCurrent(this.mManagerCallback);
    }

    public boolean isShownOrQueued() {
        return SnackbarManager.getInstance().isCurrentOrNext(this.mManagerCallback);
    }

    final void showView() {
        if (this.mView.getParent() == null) {
            ViewGroup.LayoutParams layoutParams = this.mView.getLayoutParams();
            if (layoutParams instanceof CoordinatorLayout.LayoutParams) {
                Behavior behavior = new Behavior();
                behavior.setStartAlphaSwipeDistance(0.1f);
                behavior.setEndAlphaSwipeDistance(0.6f);
                behavior.setSwipeDirection(0);
                behavior.setListener(new SwipeDismissBehavior.OnDismissListener() { // from class: android.support.design.widget.Snackbar.4
                    @Override // android.support.design.widget.SwipeDismissBehavior.OnDismissListener
                    public void onDismiss(View view) {
                        Snackbar.this.dispatchDismiss(0);
                    }

                    @Override // android.support.design.widget.SwipeDismissBehavior.OnDismissListener
                    public void onDragStateChanged(int i) {
                        switch (i) {
                            case 0:
                                SnackbarManager.getInstance().restoreTimeout(Snackbar.this.mManagerCallback);
                                return;
                            case 1:
                            case 2:
                                SnackbarManager.getInstance().cancelTimeout(Snackbar.this.mManagerCallback);
                                return;
                            default:
                                return;
                        }
                    }
                });
                ((CoordinatorLayout.LayoutParams) layoutParams).setBehavior(behavior);
            }
            this.mTargetParent.addView(this.mView);
        }
        this.mView.setOnAttachStateChangeListener(new SnackbarLayout.OnAttachStateChangeListener() { // from class: android.support.design.widget.Snackbar.5
            @Override // android.support.design.widget.Snackbar.SnackbarLayout.OnAttachStateChangeListener
            public void onViewAttachedToWindow(View view) {
            }

            @Override // android.support.design.widget.Snackbar.SnackbarLayout.OnAttachStateChangeListener
            public void onViewDetachedFromWindow(View view) {
                if (Snackbar.this.isShownOrQueued()) {
                    Snackbar.sHandler.post(new Runnable() { // from class: android.support.design.widget.Snackbar.5.1
                        @Override // java.lang.Runnable
                        public void run() {
                            Snackbar.this.onViewHidden(3);
                        }
                    });
                }
            }
        });
        if (ViewCompat.isLaidOut(this.mView)) {
            animateViewIn();
        } else {
            this.mView.setOnLayoutChangeListener(new SnackbarLayout.OnLayoutChangeListener() { // from class: android.support.design.widget.Snackbar.6
                @Override // android.support.design.widget.Snackbar.SnackbarLayout.OnLayoutChangeListener
                public void onLayoutChange(View view, int i, int i2, int i3, int i4) {
                    Snackbar.this.animateViewIn();
                    Snackbar.this.mView.setOnLayoutChangeListener(null);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void animateViewIn() {
        if (Build.VERSION.SDK_INT >= 14) {
            ViewCompat.setTranslationY(this.mView, this.mView.getHeight());
            ViewCompat.animate(this.mView).translationY(0.0f).setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR).setDuration(250L).setListener(new ViewPropertyAnimatorListenerAdapter() { // from class: android.support.design.widget.Snackbar.7
                @Override // android.support.v4.view.ViewPropertyAnimatorListenerAdapter, android.support.v4.view.ViewPropertyAnimatorListener
                public void onAnimationStart(View view) {
                    Snackbar.this.mView.animateChildrenIn(70, Snackbar.ANIMATION_FADE_DURATION);
                }

                @Override // android.support.v4.view.ViewPropertyAnimatorListenerAdapter, android.support.v4.view.ViewPropertyAnimatorListener
                public void onAnimationEnd(View view) {
                    if (Snackbar.this.mCallback != null) {
                        Snackbar.this.mCallback.onShown(Snackbar.this);
                    }
                    SnackbarManager.getInstance().onShown(Snackbar.this.mManagerCallback);
                }
            }).start();
            return;
        }
        Animation loadAnimation = android.view.animation.AnimationUtils.loadAnimation(this.mView.getContext(), R.anim.design_snackbar_in);
        loadAnimation.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
        loadAnimation.setDuration(250L);
        loadAnimation.setAnimationListener(new Animation.AnimationListener() { // from class: android.support.design.widget.Snackbar.8
            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationRepeat(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationStart(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationEnd(Animation animation) {
                if (Snackbar.this.mCallback != null) {
                    Snackbar.this.mCallback.onShown(Snackbar.this);
                }
                SnackbarManager.getInstance().onShown(Snackbar.this.mManagerCallback);
            }
        });
        this.mView.startAnimation(loadAnimation);
    }

    private void animateViewOut(final int i) {
        if (Build.VERSION.SDK_INT >= 14) {
            ViewCompat.animate(this.mView).translationY(this.mView.getHeight()).setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR).setDuration(250L).setListener(new ViewPropertyAnimatorListenerAdapter() { // from class: android.support.design.widget.Snackbar.9
                @Override // android.support.v4.view.ViewPropertyAnimatorListenerAdapter, android.support.v4.view.ViewPropertyAnimatorListener
                public void onAnimationStart(View view) {
                    Snackbar.this.mView.animateChildrenOut(0, Snackbar.ANIMATION_FADE_DURATION);
                }

                @Override // android.support.v4.view.ViewPropertyAnimatorListenerAdapter, android.support.v4.view.ViewPropertyAnimatorListener
                public void onAnimationEnd(View view) {
                    Snackbar.this.onViewHidden(i);
                }
            }).start();
            return;
        }
        Animation loadAnimation = android.view.animation.AnimationUtils.loadAnimation(this.mView.getContext(), R.anim.design_snackbar_out);
        loadAnimation.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
        loadAnimation.setDuration(250L);
        loadAnimation.setAnimationListener(new Animation.AnimationListener() { // from class: android.support.design.widget.Snackbar.10
            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationRepeat(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationStart(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationEnd(Animation animation) {
                Snackbar.this.onViewHidden(i);
            }
        });
        this.mView.startAnimation(loadAnimation);
    }

    final void hideView(int i) {
        if (this.mView.getVisibility() != 0 || isBeingDragged()) {
            onViewHidden(i);
        } else {
            animateViewOut(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onViewHidden(int i) {
        SnackbarManager.getInstance().onDismissed(this.mManagerCallback);
        if (this.mCallback != null) {
            this.mCallback.onDismissed(this, i);
        }
        ViewParent parent = this.mView.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(this.mView);
        }
    }

    private boolean isBeingDragged() {
        ViewGroup.LayoutParams layoutParams = this.mView.getLayoutParams();
        if (layoutParams instanceof CoordinatorLayout.LayoutParams) {
            CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) layoutParams).getBehavior();
            return (behavior instanceof SwipeDismissBehavior) && ((SwipeDismissBehavior) behavior).getDragState() != 0;
        }
        return false;
    }

    /* loaded from: classes.dex */
    public static class SnackbarLayout extends LinearLayout {
        private Button mActionView;
        private int mMaxInlineActionWidth;
        private int mMaxWidth;
        private TextView mMessageView;
        private OnAttachStateChangeListener mOnAttachStateChangeListener;
        private OnLayoutChangeListener mOnLayoutChangeListener;

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public interface OnAttachStateChangeListener {
            void onViewAttachedToWindow(View view);

            void onViewDetachedFromWindow(View view);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public interface OnLayoutChangeListener {
            void onLayoutChange(View view, int i, int i2, int i3, int i4);
        }

        public SnackbarLayout(Context context) {
            this(context, null);
        }

        public SnackbarLayout(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.SnackbarLayout);
            this.mMaxWidth = obtainStyledAttributes.getDimensionPixelSize(R.styleable.SnackbarLayout_android_maxWidth, -1);
            this.mMaxInlineActionWidth = obtainStyledAttributes.getDimensionPixelSize(R.styleable.SnackbarLayout_maxActionInlineWidth, -1);
            if (obtainStyledAttributes.hasValue(R.styleable.SnackbarLayout_elevation)) {
                ViewCompat.setElevation(this, obtainStyledAttributes.getDimensionPixelSize(R.styleable.SnackbarLayout_elevation, 0));
            }
            obtainStyledAttributes.recycle();
            setClickable(true);
            LayoutInflater.from(context).inflate(R.layout.design_layout_snackbar_include, this);
            ViewCompat.setAccessibilityLiveRegion(this, 1);
        }

        @Override // android.view.View
        protected void onFinishInflate() {
            super.onFinishInflate();
            this.mMessageView = (TextView) findViewById(R.id.snackbar_text);
            this.mActionView = (Button) findViewById(R.id.snackbar_action);
        }

        TextView getMessageView() {
            return this.mMessageView;
        }

        Button getActionView() {
            return this.mActionView;
        }

        /* JADX WARN: Code restructure failed: missing block: B:15:0x0055, code lost:
        
            if (updateViewsWithinLayout(1, r0, r0 - r1) != false) goto L26;
         */
        /* JADX WARN: Code restructure failed: missing block: B:16:0x0063, code lost:
        
            r4 = false;
         */
        /* JADX WARN: Code restructure failed: missing block: B:25:0x0060, code lost:
        
            if (updateViewsWithinLayout(0, r0, r0) != false) goto L26;
         */
        @Override // android.widget.LinearLayout, android.view.View
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        protected void onMeasure(int r8, int r9) {
            /*
                r7 = this;
                super.onMeasure(r8, r9)
                int r0 = r7.mMaxWidth
                if (r0 <= 0) goto L1a
                int r0 = r7.getMeasuredWidth()
                int r1 = r7.mMaxWidth
                if (r0 <= r1) goto L1a
                int r8 = r7.mMaxWidth
                r0 = 1073741824(0x40000000, float:2.0)
                int r8 = android.view.View.MeasureSpec.makeMeasureSpec(r8, r0)
                super.onMeasure(r8, r9)
            L1a:
                android.content.res.Resources r0 = r7.getResources()
                int r1 = android.support.design.R.dimen.design_snackbar_padding_vertical_2lines
                int r0 = r0.getDimensionPixelSize(r1)
                android.content.res.Resources r1 = r7.getResources()
                int r2 = android.support.design.R.dimen.design_snackbar_padding_vertical
                int r1 = r1.getDimensionPixelSize(r2)
                android.widget.TextView r2 = r7.mMessageView
                android.text.Layout r2 = r2.getLayout()
                int r2 = r2.getLineCount()
                r3 = 0
                r4 = 1
                if (r2 <= r4) goto L3e
                r2 = 1
                goto L3f
            L3e:
                r2 = 0
            L3f:
                if (r2 == 0) goto L58
                int r5 = r7.mMaxInlineActionWidth
                if (r5 <= 0) goto L58
                android.widget.Button r5 = r7.mActionView
                int r5 = r5.getMeasuredWidth()
                int r6 = r7.mMaxInlineActionWidth
                if (r5 <= r6) goto L58
                int r1 = r0 - r1
                boolean r0 = r7.updateViewsWithinLayout(r4, r0, r1)
                if (r0 == 0) goto L63
                goto L64
            L58:
                if (r2 == 0) goto L5b
                goto L5c
            L5b:
                r0 = r1
            L5c:
                boolean r0 = r7.updateViewsWithinLayout(r3, r0, r0)
                if (r0 == 0) goto L63
                goto L64
            L63:
                r4 = 0
            L64:
                if (r4 == 0) goto L69
                super.onMeasure(r8, r9)
            L69:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.design.widget.Snackbar.SnackbarLayout.onMeasure(int, int):void");
        }

        void animateChildrenIn(int i, int i2) {
            ViewCompat.setAlpha(this.mMessageView, 0.0f);
            long j = i2;
            long j2 = i;
            ViewCompat.animate(this.mMessageView).alpha(1.0f).setDuration(j).setStartDelay(j2).start();
            if (this.mActionView.getVisibility() == 0) {
                ViewCompat.setAlpha(this.mActionView, 0.0f);
                ViewCompat.animate(this.mActionView).alpha(1.0f).setDuration(j).setStartDelay(j2).start();
            }
        }

        void animateChildrenOut(int i, int i2) {
            ViewCompat.setAlpha(this.mMessageView, 1.0f);
            long j = i2;
            long j2 = i;
            ViewCompat.animate(this.mMessageView).alpha(0.0f).setDuration(j).setStartDelay(j2).start();
            if (this.mActionView.getVisibility() == 0) {
                ViewCompat.setAlpha(this.mActionView, 1.0f);
                ViewCompat.animate(this.mActionView).alpha(0.0f).setDuration(j).setStartDelay(j2).start();
            }
        }

        @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            if (!z || this.mOnLayoutChangeListener == null) {
                return;
            }
            this.mOnLayoutChangeListener.onLayoutChange(this, i, i2, i3, i4);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            if (this.mOnAttachStateChangeListener != null) {
                this.mOnAttachStateChangeListener.onViewAttachedToWindow(this);
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if (this.mOnAttachStateChangeListener != null) {
                this.mOnAttachStateChangeListener.onViewDetachedFromWindow(this);
            }
        }

        void setOnLayoutChangeListener(OnLayoutChangeListener onLayoutChangeListener) {
            this.mOnLayoutChangeListener = onLayoutChangeListener;
        }

        void setOnAttachStateChangeListener(OnAttachStateChangeListener onAttachStateChangeListener) {
            this.mOnAttachStateChangeListener = onAttachStateChangeListener;
        }

        private boolean updateViewsWithinLayout(int i, int i2, int i3) {
            boolean z;
            if (i != getOrientation()) {
                setOrientation(i);
                z = true;
            } else {
                z = false;
            }
            if (this.mMessageView.getPaddingTop() == i2 && this.mMessageView.getPaddingBottom() == i3) {
                return z;
            }
            updateTopBottomPadding(this.mMessageView, i2, i3);
            return true;
        }

        private static void updateTopBottomPadding(View view, int i, int i2) {
            if (ViewCompat.isPaddingRelative(view)) {
                ViewCompat.setPaddingRelative(view, ViewCompat.getPaddingStart(view), i, ViewCompat.getPaddingEnd(view), i2);
            } else {
                view.setPadding(view.getPaddingLeft(), i, view.getPaddingRight(), i2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public final class Behavior extends SwipeDismissBehavior<SnackbarLayout> {
        Behavior() {
        }

        @Override // android.support.design.widget.SwipeDismissBehavior
        public boolean canSwipeDismissView(View view) {
            return view instanceof SnackbarLayout;
        }

        @Override // android.support.design.widget.SwipeDismissBehavior, android.support.design.widget.CoordinatorLayout.Behavior
        public boolean onInterceptTouchEvent(CoordinatorLayout coordinatorLayout, SnackbarLayout snackbarLayout, MotionEvent motionEvent) {
            if (coordinatorLayout.isPointInChildBounds(snackbarLayout, (int) motionEvent.getX(), (int) motionEvent.getY())) {
                int actionMasked = motionEvent.getActionMasked();
                if (actionMasked != 3) {
                    switch (actionMasked) {
                        case 0:
                            SnackbarManager.getInstance().cancelTimeout(Snackbar.this.mManagerCallback);
                            break;
                    }
                }
                SnackbarManager.getInstance().restoreTimeout(Snackbar.this.mManagerCallback);
            }
            return super.onInterceptTouchEvent(coordinatorLayout, (CoordinatorLayout) snackbarLayout, motionEvent);
        }
    }
}
