package me.veryyoung.game2048;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class AnimationGrid {
    public ArrayList<AnimationCell>[][] field;
    int activeAnimations = 0;
    boolean oneMoreFrame = false;
    public ArrayList<AnimationCell> globalAnimation = new ArrayList<>();

    public AnimationGrid(int i, int i2) {
        this.field = (ArrayList[][]) Array.newInstance((Class<?>) ArrayList.class, i, i2);
        for (int i3 = 0; i3 < i; i3++) {
            for (int i4 = 0; i4 < i2; i4++) {
                this.field[i3][i4] = new ArrayList<>();
            }
        }
    }

    public void startAnimation(int i, int i2, int i3, long j, long j2, int[] iArr) {
        AnimationCell animationCell = new AnimationCell(i, i2, i3, j, j2, iArr);
        if (i == -1 && i2 == -1) {
            this.globalAnimation.add(animationCell);
        } else {
            this.field[i][i2].add(animationCell);
        }
        this.activeAnimations++;
    }

    public void tickAll(long j) {
        ArrayList arrayList = new ArrayList();
        Iterator<AnimationCell> it = this.globalAnimation.iterator();
        while (it.hasNext()) {
            AnimationCell next = it.next();
            next.tick(j);
            if (next.animationDone()) {
                arrayList.add(next);
                this.activeAnimations--;
            }
        }
        for (ArrayList<AnimationCell>[] arrayListArr : this.field) {
            for (ArrayList<AnimationCell> arrayList2 : arrayListArr) {
                Iterator<AnimationCell> it2 = arrayList2.iterator();
                while (it2.hasNext()) {
                    AnimationCell next2 = it2.next();
                    next2.tick(j);
                    if (next2.animationDone()) {
                        arrayList.add(next2);
                        this.activeAnimations--;
                    }
                }
            }
        }
        Iterator it3 = arrayList.iterator();
        while (it3.hasNext()) {
            cancelAnimation((AnimationCell) it3.next());
        }
    }

    public boolean isAnimationActive() {
        if (this.activeAnimations != 0) {
            this.oneMoreFrame = true;
            return true;
        }
        if (!this.oneMoreFrame) {
            return false;
        }
        this.oneMoreFrame = false;
        return true;
    }

    public ArrayList<AnimationCell> getAnimationCell(int i, int i2) {
        return this.field[i][i2];
    }

    public void cancelAnimations() {
        for (ArrayList<AnimationCell>[] arrayListArr : this.field) {
            for (ArrayList<AnimationCell> arrayList : arrayListArr) {
                arrayList.clear();
            }
        }
        this.globalAnimation.clear();
        this.activeAnimations = 0;
    }

    public void cancelAnimation(AnimationCell animationCell) {
        if (animationCell.getX() == -1 && animationCell.getY() == -1) {
            this.globalAnimation.remove(animationCell);
        } else {
            this.field[animationCell.getX()][animationCell.getY()].remove(animationCell);
        }
    }
}
