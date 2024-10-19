package me.veryyoung.game2048;

import java.lang.reflect.Array;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class Grid {
    private Tile[][] bufferField;
    public Tile[][] field;
    public Tile[][] undoField;

    public Grid(int i, int i2) {
        this.field = (Tile[][]) Array.newInstance((Class<?>) Tile.class, i, i2);
        this.undoField = (Tile[][]) Array.newInstance((Class<?>) Tile.class, i, i2);
        this.bufferField = (Tile[][]) Array.newInstance((Class<?>) Tile.class, i, i2);
        clearGrid();
        clearUndoGrid();
    }

    public Cell randomAvailableCell() {
        ArrayList<Cell> availableCells = getAvailableCells();
        if (availableCells.size() < 1) {
            return null;
        }
        double random = Math.random();
        double size = availableCells.size();
        Double.isNaN(size);
        return availableCells.get((int) Math.floor(random * size));
    }

    public ArrayList<Cell> getAvailableCells() {
        ArrayList<Cell> arrayList = new ArrayList<>();
        for (int i = 0; i < this.field.length; i++) {
            for (int i2 = 0; i2 < this.field[0].length; i2++) {
                if (this.field[i][i2] == null) {
                    arrayList.add(new Cell(i, i2));
                }
            }
        }
        return arrayList;
    }

    public ArrayList<Cell> getNotAvailableCells() {
        ArrayList<Cell> arrayList = new ArrayList<>();
        for (int i = 0; i < this.field.length; i++) {
            for (int i2 = 0; i2 < this.field[0].length; i2++) {
                if (this.field[i][i2] != null) {
                    arrayList.add(new Cell(i, i2));
                }
            }
        }
        return arrayList;
    }

    public boolean isCellsAvailable() {
        return getAvailableCells().size() >= 1;
    }

    public boolean isCellAvailable(Cell cell) {
        return !isCellOccupied(cell);
    }

    public boolean isCellOccupied(Cell cell) {
        return getCellContent(cell) != null;
    }

    public Tile getCellContent(Cell cell) {
        if (cell == null || !isCellWithinBounds(cell)) {
            return null;
        }
        return this.field[cell.getX()][cell.getY()];
    }

    public Tile getCellContent(int i, int i2) {
        if (isCellWithinBounds(i, i2)) {
            return this.field[i][i2];
        }
        return null;
    }

    public boolean isCellWithinBounds(Cell cell) {
        return cell.getX() >= 0 && cell.getX() < this.field.length && cell.getY() >= 0 && cell.getY() < this.field[0].length;
    }

    public boolean isCellWithinBounds(int i, int i2) {
        return i >= 0 && i < this.field.length && i2 >= 0 && i2 < this.field[0].length;
    }

    public void insertTile(Tile tile) {
        this.field[tile.getX()][tile.getY()] = tile;
    }

    public void removeTile(Tile tile) {
        this.field[tile.getX()][tile.getY()] = null;
    }

    public void saveTiles() {
        for (int i = 0; i < this.bufferField.length; i++) {
            for (int i2 = 0; i2 < this.bufferField[0].length; i2++) {
                if (this.bufferField[i][i2] == null) {
                    this.undoField[i][i2] = null;
                } else {
                    this.undoField[i][i2] = new Tile(i, i2, this.bufferField[i][i2].getValue());
                }
            }
        }
    }

    public void prepareSaveTiles() {
        for (int i = 0; i < this.field.length; i++) {
            for (int i2 = 0; i2 < this.field[0].length; i2++) {
                if (this.field[i][i2] == null) {
                    this.bufferField[i][i2] = null;
                } else {
                    this.bufferField[i][i2] = new Tile(i, i2, this.field[i][i2].getValue());
                }
            }
        }
    }

    public void revertTiles() {
        for (int i = 0; i < this.undoField.length; i++) {
            for (int i2 = 0; i2 < this.undoField[0].length; i2++) {
                if (this.undoField[i][i2] == null) {
                    this.field[i][i2] = null;
                } else {
                    this.field[i][i2] = new Tile(i, i2, this.undoField[i][i2].getValue());
                }
            }
        }
    }

    public void clearGrid() {
        for (int i = 0; i < this.field.length; i++) {
            for (int i2 = 0; i2 < this.field[0].length; i2++) {
                this.field[i][i2] = null;
            }
        }
    }

    public void clearUndoGrid() {
        for (int i = 0; i < this.field.length; i++) {
            for (int i2 = 0; i2 < this.field[0].length; i2++) {
                this.undoField[i][i2] = null;
            }
        }
    }
}
