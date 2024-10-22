package me.veryyoung.game2048;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.preference.PreferenceManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class MainGame {
    public static final int FADE_GLOBAL_ANIMATION = 0;
    public static final int GAME_ENDLESS = 2;
    public static final int GAME_ENDLESS_WON = 3;
    public static final int GAME_LOST = -1;
    public static final int GAME_NORMAL = 0;
    public static final int GAME_NORMAL_WON = 1;
    public static final int GAME_WIN = 1;
    private static final String HIGH_SCORE = "high score";
    public static final int MERGE_ANIMATION = 1;
    public static final int MOVE_ANIMATION = 0;
    public static final long MOVE_ANIMATION_TIME = 100000000;
    public static final long NOTIFICATION_ANIMATION_TIME = 500000000;
    public static final long NOTIFICATION_DELAY_TIME = 200000000;
    public static final int SPAWN_ANIMATION = -1;
    public static final long SPAWN_ANIMATION_TIME = 100000000;
    public static final int endingMaxValue = 32768;
    public static final int startingMaxValue = 2048;
    public AnimationGrid aGrid;
    public boolean canUndo;
    private Context mContext;
    private MainView mView;
    private SoundPool soudPool;
    private HashMap<Integer, Integer> spMap;
    public Grid grid = null;
    final int numSquaresX = 4;
    final int numSquaresY = 4;
    final int startTiles = 2;
    public int gameState = 0;
    public long score = 0;
    public long highScore = 0;
    public long lastScore = 0;
    public int lastGameState = 0;
    private long bufferScore = 0;
    private int bufferGameState = 0;

    public MainGame(Context context, MainView mainView) {
        this.mContext = context;
        this.mView = mainView;
        initSoundPool();
    }

    public void newGame() {
        playSound(3, 1);
        if (this.grid == null) {
            this.grid = new Grid(4, 4);
        } else {
            prepareUndoState();
            saveUndoState();
            this.grid.clearGrid();
        }
        this.aGrid = new AnimationGrid(4, 4);
        this.highScore = getHighScore();
        if (this.score >= this.highScore) {
            this.highScore = this.score;
            recordHighScore();
        }
        this.score = 0L;
        this.gameState = 0;
        addStartTiles();
        this.mView.refreshLastTime = true;
        this.mView.resyncTime();
        this.mView.invalidate();
    }

    private void addStartTiles() {
        for (int i = 0; i < 2; i++) {
            addRandomTile();
        }
    }

    private void addRandomTile() {
        if (this.grid.isCellsAvailable()) {
            spawnTile(new Tile(this.grid.randomAvailableCell(), Math.random() < 0.9d ? 2 : 4));
        }
    }

    private void spawnTile(Tile tile) {
        this.grid.insertTile(tile);
        this.aGrid.startAnimation(tile.getX(), tile.getY(), -1, 100000000L, 100000000L, null);
    }

    private void recordHighScore() {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this.mContext).edit();
        edit.putLong(HIGH_SCORE, this.highScore);
        edit.commit();
    }

    private long getHighScore() {
        return PreferenceManager.getDefaultSharedPreferences(this.mContext).getLong(HIGH_SCORE, -1L);
    }

    private void prepareTiles() {
        for (Tile[] tileArr : this.grid.field) {
            for (Tile tile : tileArr) {
                if (this.grid.isCellOccupied(tile)) {
                    tile.setMergedFrom(null);
                }
            }
        }
    }

    private void moveTile(Tile tile, Cell cell) {
        this.grid.field[tile.getX()][tile.getY()] = null;
        this.grid.field[cell.getX()][cell.getY()] = tile;
        tile.updatePosition(cell);
    }

    private void saveUndoState() {
        this.grid.saveTiles();
        this.canUndo = true;
        this.lastScore = this.bufferScore;
        this.lastGameState = this.bufferGameState;
    }

    public void cheat() {
        playSound(3, 1);
        ArrayList<Cell> notAvailableCells = this.grid.getNotAvailableCells();
        prepareUndoState();
        Iterator<Cell> it = notAvailableCells.iterator();
        while (it.hasNext()) {
            Tile cellContent = this.grid.getCellContent(it.next());
            if (2 == cellContent.getValue()) {
                this.grid.removeTile(cellContent);
            }
        }
        if (this.grid.getNotAvailableCells().size() == 0) {
            addStartTiles();
        }
        saveUndoState();
        this.mView.resyncTime();
        this.mView.invalidate();
    }

    private void prepareUndoState() {
        this.grid.prepareSaveTiles();
        this.bufferScore = this.score;
        this.bufferGameState = this.gameState;
    }

    public void revertUndoState() {
        playSound(3, 1);
        if (this.canUndo) {
            this.canUndo = false;
            this.aGrid.cancelAnimations();
            this.grid.revertTiles();
            this.score = this.lastScore;
            this.gameState = this.lastGameState;
            this.mView.refreshLastTime = true;
            this.mView.invalidate();
        }
    }

    public boolean gameWon() {
        return this.gameState > 0 && this.gameState % 2 != 0;
    }

    public boolean gameLost() {
        return this.gameState == -1;
    }

    public boolean isActive() {
        return (gameWon() || gameLost()) ? false : true;
    }

    public void move(int i) {
        int i2 = 1;
        playSound(1, 1);
        this.aGrid.cancelAnimations();
        if (isActive()) {
            prepareUndoState();
            Cell vector = getVector(i);
            List<Integer> buildTraversalsX = buildTraversalsX(vector);
            List<Integer> buildTraversalsY = buildTraversalsY(vector);
            prepareTiles();
            Iterator<Integer> it = buildTraversalsX.iterator();
            boolean z = false;
            while (it.hasNext()) {
                int intValue = it.next().intValue();
                Iterator<Integer> it2 = buildTraversalsY.iterator();
                while (it2.hasNext()) {
                    int intValue2 = it2.next().intValue();
                    Cell cell = new Cell(intValue, intValue2);
                    Tile cellContent = this.grid.getCellContent(cell);
                    if (cellContent != null) {
                        Cell[] findFarthestPosition = findFarthestPosition(cell, vector);
                        Tile cellContent2 = this.grid.getCellContent(findFarthestPosition[i2]);
                        if (cellContent2 != null && cellContent2.getValue() == cellContent.getValue() && cellContent2.getMergedFrom() == null) {
                            playSound(2, i2);
                            Tile tile = new Tile(findFarthestPosition[i2], cellContent.getValue() * 2);
                            tile.setMergedFrom(new Tile[]{cellContent, cellContent2});
                            this.grid.insertTile(tile);
                            this.grid.removeTile(cellContent);
                            cellContent.updatePosition(findFarthestPosition[1]);
                            this.aGrid.startAnimation(tile.getX(), tile.getY(), 0, 100000000L, 0L, new int[]{intValue, intValue2});
                            this.aGrid.startAnimation(tile.getX(), tile.getY(), 1, 100000000L, 100000000L, null);
                            this.score += tile.getValue();
                            this.highScore = Math.max(this.score, this.highScore);
                            if (tile.getValue() >= winValue() && !gameWon()) {
                                this.gameState++;
                                playSound(4, 1);
                                endGame();
                            }
                        } else {
                            moveTile(cellContent, findFarthestPosition[0]);
                            this.aGrid.startAnimation(findFarthestPosition[0].getX(), findFarthestPosition[0].getY(), 0, 100000000L, 0L, new int[]{intValue, intValue2, 0});
                        }
                        if (!positionsEqual(cell, cellContent)) {
                            z = true;
                        }
                    }
                    i2 = 1;
                }
                i2 = 1;
            }
            if (z) {
                saveUndoState();
                addRandomTile();
                checkLose();
            }
            this.mView.resyncTime();
            this.mView.invalidate();
        }
    }

    private void checkLose() {
        if (movesAvailable() || gameWon()) {
            return;
        }
        this.gameState = -1;
        endGame();
        playSound(5, 1);
    }

    private void endGame() {
        this.aGrid.startAnimation(-1, -1, 0, NOTIFICATION_ANIMATION_TIME, NOTIFICATION_DELAY_TIME, null);
        if (this.score >= this.highScore) {
            this.highScore = this.score;
            recordHighScore();
        }
    }

    private Cell getVector(int i) {
        return new Cell[]{new Cell(0, -1), new Cell(1, 0), new Cell(0, 1), new Cell(-1, 0)}[i];
    }

    private List<Integer> buildTraversalsX(Cell cell) {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < 4; i++) {
            arrayList.add(Integer.valueOf(i));
        }
        if (cell.getX() == 1) {
            Collections.reverse(arrayList);
        }
        return arrayList;
    }

    private List<Integer> buildTraversalsY(Cell cell) {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < 4; i++) {
            arrayList.add(Integer.valueOf(i));
        }
        if (cell.getY() == 1) {
            Collections.reverse(arrayList);
        }
        return arrayList;
    }

    private Cell[] findFarthestPosition(Cell cell, Cell cell2) {
        Cell cell3;
        Cell cell4 = new Cell(cell.getX(), cell.getY());
        while (true) {
            cell3 = new Cell(cell4.getX() + cell2.getX(), cell4.getY() + cell2.getY());
            if (!this.grid.isCellWithinBounds(cell3) || !this.grid.isCellAvailable(cell3)) {
                break;
            }
            cell4 = cell3;
        }
        return new Cell[]{cell4, cell3};
    }

    private boolean movesAvailable() {
        return this.grid.isCellsAvailable() || tileMatchesAvailable();
    }

    private boolean tileMatchesAvailable() {
        for (int i = 0; i < 4; i++) {
            for (int i2 = 0; i2 < 4; i2++) {
                Tile cellContent = this.grid.getCellContent(new Cell(i, i2));
                if (cellContent != null) {
                    for (int i3 = 0; i3 < 4; i3++) {
                        Cell vector = getVector(i3);
                        Tile cellContent2 = this.grid.getCellContent(new Cell(vector.getX() + i, vector.getY() + i2));
                        if (cellContent2 != null && cellContent2.getValue() == cellContent.getValue()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean positionsEqual(Cell cell, Cell cell2) {
        return cell.getX() == cell2.getX() && cell.getY() == cell2.getY();
    }

    private int winValue() {
        return !canContinue() ? 32768 : 2048;
    }

    public void setEndlessMode() {
        this.gameState = 2;
        this.mView.invalidate();
        this.mView.refreshLastTime = true;
    }

    public boolean canContinue() {
        return (this.gameState == 2 || this.gameState == 3) ? false : true;
    }

    private void initSoundPool() {
        this.soudPool = new SoundPool(5, 3, 0);
        this.spMap = new HashMap<>();
        this.spMap.put(1, Integer.valueOf(this.soudPool.load(this.mView.getContext(), R.raw.sfx_wing, 1)));
        this.spMap.put(2, Integer.valueOf(this.soudPool.load(this.mView.getContext(), R.raw.sfx_point, 1)));
        this.spMap.put(3, Integer.valueOf(this.soudPool.load(this.mView.getContext(), R.raw.sfx_swooshing, 1)));
        this.spMap.put(4, Integer.valueOf(this.soudPool.load(this.mView.getContext(), R.raw.die, 1)));
        this.spMap.put(5, Integer.valueOf(this.soudPool.load(this.mView.getContext(), R.raw.win, 1)));
    }

    private void playSound(int i, int i2) {
        AudioManager audioManager = (AudioManager) this.mView.getContext().getSystemService("audio");
        float streamVolume = audioManager.getStreamVolume(3) / audioManager.getStreamMaxVolume(3);
        this.soudPool.play(this.spMap.get(Integer.valueOf(i)).intValue(), streamVolume, streamVolume, 1, i2, 1.0f);
    }
}
