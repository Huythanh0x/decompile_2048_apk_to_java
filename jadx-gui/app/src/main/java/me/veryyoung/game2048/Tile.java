package me.veryyoung.game2048;

/* loaded from: classes.dex */
public class Tile extends Cell {
    private Tile[] mergedFrom;
    private int value;

    public Tile(int i, int i2, int i3) {
        super(i, i2);
        this.mergedFrom = null;
        this.value = i3;
    }

    public Tile(Cell cell, int i) {
        super(cell.getX(), cell.getY());
        this.mergedFrom = null;
        this.value = i;
    }

    public void updatePosition(Cell cell) {
        setX(cell.getX());
        setY(cell.getY());
    }

    public int getValue() {
        return this.value;
    }

    public Tile[] getMergedFrom() {
        return this.mergedFrom;
    }

    public void setMergedFrom(Tile[] tileArr) {
        this.mergedFrom = tileArr;
    }
}
