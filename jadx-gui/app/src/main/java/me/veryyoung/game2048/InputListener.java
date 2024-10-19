package me.veryyoung.game2048;

import android.view.View;

/* loaded from: classes.dex */
public class InputListener implements View.OnTouchListener {
    private static final int MOVE_THRESHOLD = 250;
    private static final int RESET_STARTING = 10;
    private static final int SWIPE_MIN_DISTANCE = 0;
    private static final int SWIPE_THRESHOLD_VELOCITY = 25;
    private float lastdx;
    private float lastdy;
    MainView mView;
    private float previousX;
    private float previousY;
    private float startingX;
    private float startingY;

    /* renamed from: x */
    private float f25x;

    /* renamed from: y */
    private float f26y;
    private int previousDirection = 1;
    private int veryLastDirection = 1;
    private boolean hasMoved = false;

    private boolean inRange(float f, float f2, float f3) {
        return f <= f2 && f2 <= f3;
    }

    public InputListener(MainView mainView) {
        this.mView = mainView;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:36:0x0160  */
    @Override // android.view.View.OnTouchListener
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean onTouch(android.view.View r9, android.view.MotionEvent r10) {
        /*
            Method dump skipped, instructions count: 560
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        //todo fix this method
        throw new UnsupportedOperationException("Method not decompiled: me.veryyoung.game2048.InputListener.onTouch(android.view.View, android.view.MotionEvent):boolean");
    }

    private float pathMoved() {
        return ((this.f25x - this.startingX) * (this.f25x - this.startingX)) + ((this.f26y - this.startingY) * (this.f26y - this.startingY));
    }

    private boolean iconPressed(int i, int i2) {
        return isTap(1) && inRange((float) i, this.f25x, (float) (i + this.mView.iconSize)) && inRange((float) i2, this.f26y, (float) (i2 + this.mView.iconSize));
    }

    private boolean isTap(int i) {
        return pathMoved() <= ((float) (this.mView.iconSize * i));
    }
}