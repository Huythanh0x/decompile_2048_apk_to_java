package me.veryyoung.game2048;

import android.view.MotionEvent;
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

    //thanh0x note: the name is forced un-obfuscated
    private float x;

    /* renamed from: y */
    private float y;
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
    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        final int action = motionEvent.getAction();
        final boolean b = false;
        switch (action) {
            case 2: {
                this.x = motionEvent.getX();
                this.y = motionEvent.getY();
                if (this.mView.game.isActive()) {
                    final float n = this.x - this.previousX;
                    if (Math.abs(this.lastdx + n) < Math.abs(this.lastdx) + Math.abs(n) && Math.abs(n) > 10.0f && Math.abs(this.x - this.startingX) > 0.0f) {
                        this.startingX = this.x;
                        this.startingY = this.y;
                        this.lastdx = n;
                        this.previousDirection = this.veryLastDirection;
                    }
                    if (this.lastdx == 0.0f) {
                        this.lastdx = n;
                    }
                    final float n2 = this.y - this.previousY;
                    if (Math.abs(this.lastdy + n2) < Math.abs(this.lastdy) + Math.abs(n2) && Math.abs(n2) > 10.0f && Math.abs(this.y - this.startingY) > 0.0f) {
                        this.startingX = this.x;
                        this.startingY = this.y;
                        this.lastdy = n2;
                        this.previousDirection = this.veryLastDirection;
                    }
                    if (this.lastdy == 0.0f) {
                        this.lastdy = n2;
                    }
                    if (this.pathMoved() > 0.0f) {
                        int n3 = 0;
                        Label_0581: {
                            if (((n2 >= 25.0f && this.previousDirection == 1) || this.y - this.startingY >= 250.0f) && this.previousDirection % 2 != 0) {
                                this.previousDirection *= 2;
                                this.veryLastDirection = 2;
                                this.mView.game.move(2);
                            }
                            else if (((n2 <= -25.0f && this.previousDirection == 1) || this.y - this.startingY <= -250.0f) && this.previousDirection % 3 != 0) {
                                this.previousDirection *= 3;
                                this.veryLastDirection = 3;
                                this.mView.game.move(0);
                            }
                            else if (((n >= 25.0f && this.previousDirection == 1) || this.x - this.startingX >= 250.0f) && this.previousDirection % 5 != 0) {
                                this.previousDirection *= 5;
                                this.veryLastDirection = 5;
                                this.mView.game.move(1);
                            }
                            else {
                                if (n > -25.0f || this.previousDirection != 1) {
                                    n3 = (b ? 1 : 0);
                                    if (this.x - this.startingX > -250.0f) {
                                        break Label_0581;
                                    }
                                }
                                n3 = (b ? 1 : 0);
                                if (this.previousDirection % 7 == 0) {
                                    break Label_0581;
                                }
                                this.previousDirection *= 7;
                                this.veryLastDirection = 7;
                                this.mView.game.move(3);
                            }
                            n3 = 1;
                        }
                        if (n3 != 0) {
                            this.hasMoved = true;
                            this.startingX = this.x;
                            this.startingY = this.y;
                        }
                    }
                }
                this.previousX = this.x;
                this.previousY = this.y;
                return true;
            }
            case 1: {
                this.x = motionEvent.getX();
                this.y = motionEvent.getY();
                this.previousDirection = 1;
                this.veryLastDirection = 1;
                if (this.hasMoved) {
                    break;
                }
                if (this.iconPressed(this.mView.sXNewGame, this.mView.sYIcons)) {
                    this.mView.game.newGame();
                    break;
                }
                if (this.iconPressed(this.mView.sXUndo, this.mView.sYIcons)) {
                    this.mView.game.revertUndoState();
                    break;
                }
                if (this.iconPressed(this.mView.sXCheat, this.mView.sYIcons)) {
                    this.mView.game.cheat();
                    break;
                }
                if (this.isTap(2) && this.inRange((float)this.mView.startingX, this.x, (float)this.mView.endingX) && this.inRange((float)this.mView.startingY, this.x, (float)this.mView.endingY) && this.mView.continueButtonEnabled) {
                    this.mView.game.setEndlessMode();
                    break;
                }
                break;
            }
            case 0: {
                this.x = motionEvent.getX();
                this.y = motionEvent.getY();
                this.startingX = this.x;
                this.startingY = this.y;
                this.previousX = this.x;
                this.previousY = this.y;
                this.lastdx = 0.0f;
                this.lastdy = 0.0f;
                this.hasMoved = false;
                return true;
            }
        }
        return true;
    }

    private float pathMoved() {
        return ((this.x - this.startingX) * (this.x - this.startingX)) + ((this.y - this.startingY) * (this.y - this.startingY));
    }

    private boolean iconPressed(int i, int i2) {
        return isTap(1) && inRange((float) i, this.x, (float) (i + this.mView.iconSize)) && inRange((float) i2, this.y, (float) (i2 + this.mView.iconSize));
    }

    private boolean isTap(int i) {
        return pathMoved() <= ((float) (this.mView.iconSize * i));
    }
}