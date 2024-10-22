package me.veryyoung.game2048;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;

/* loaded from: classes.dex */
public class MainActivity extends Activity {
    public static final String CAN_UNDO = "can undo";
    public static final String GAME_STATE = "game state";
    public static final String HEIGHT = "height";
    public static final String HIGH_SCORE = "high score temp";
    public static final String SCORE = "score";
    public static final String UNDO_GAME_STATE = "undo game state";
    public static final String UNDO_GRID = "undo";
    public static final String UNDO_SCORE = "undo score";
    public static final String WIDTH = "width";
    MainView view;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        this.view = new MainView(getBaseContext());
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.view.hasSaveState = defaultSharedPreferences.getBoolean("save_state", false);
        if (bundle != null && bundle.getBoolean("hasState")) {
            load();
        }
        setContentView(this.view);
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 82) {
            return true;
        }
        if (i == 20) {
            this.view.game.move(2);
            return true;
        }
        if (i == 19) {
            this.view.game.move(0);
            return true;
        }
        if (i == 21) {
            this.view.game.move(3);
            return true;
        }
        if (i == 22) {
            this.view.game.move(1);
            return true;
        }
        return super.onKeyDown(i, keyEvent);
    }

    @Override // android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putBoolean("hasState", true);
        save();
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        save();
    }

    private void save() {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
        Tile[][] tileArr = this.view.game.grid.field;
        Tile[][] tileArr2 = this.view.game.grid.undoField;
        edit.putInt(WIDTH, tileArr.length);
        edit.putInt(HEIGHT, tileArr.length);
        for (int i = 0; i < tileArr.length; i++) {
            for (int i2 = 0; i2 < tileArr[0].length; i2++) {
                if (tileArr[i][i2] != null) {
                    edit.putInt(i + " " + i2, tileArr[i][i2].getValue());
                } else {
                    edit.putInt(i + " " + i2, 0);
                }
                if (tileArr2[i][i2] != null) {
                    edit.putInt(UNDO_GRID + i + " " + i2, tileArr2[i][i2].getValue());
                } else {
                    edit.putInt(UNDO_GRID + i + " " + i2, 0);
                }
            }
        }
        edit.putLong(SCORE, this.view.game.score);
        edit.putLong(HIGH_SCORE, this.view.game.highScore);
        edit.putLong(UNDO_SCORE, this.view.game.lastScore);
        edit.putBoolean(CAN_UNDO, this.view.game.canUndo);
        edit.putInt(GAME_STATE, this.view.game.gameState);
        edit.putInt(UNDO_GAME_STATE, this.view.game.lastGameState);
        edit.commit();
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        this.view.game.aGrid.cancelAnimations();
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        for (int i = 0; i < this.view.game.grid.field.length; i++) {
            for (int i2 = 0; i2 < this.view.game.grid.field[0].length; i2++) {
                int i3 = defaultSharedPreferences.getInt(i + " " + i2, -1);
                if (i3 > 0) {
                    this.view.game.grid.field[i][i2] = new Tile(i, i2, i3);
                } else if (i3 == 0) {
                    this.view.game.grid.field[i][i2] = null;
                }
                int i4 = defaultSharedPreferences.getInt(UNDO_GRID + i + " " + i2, -1);
                if (i4 > 0) {
                    this.view.game.grid.undoField[i][i2] = new Tile(i, i2, i4);
                } else if (i3 == 0) {
                    this.view.game.grid.undoField[i][i2] = null;
                }
            }
        }
        this.view.game.score = defaultSharedPreferences.getLong(SCORE, this.view.game.score);
        this.view.game.highScore = defaultSharedPreferences.getLong(HIGH_SCORE, this.view.game.highScore);
        this.view.game.lastScore = defaultSharedPreferences.getLong(UNDO_SCORE, this.view.game.lastScore);
        this.view.game.canUndo = defaultSharedPreferences.getBoolean(CAN_UNDO, this.view.game.canUndo);
        this.view.game.gameState = defaultSharedPreferences.getInt(GAME_STATE, this.view.game.gameState);
        this.view.game.lastGameState = defaultSharedPreferences.getInt(UNDO_GAME_STATE, this.view.game.lastGameState);
    }
}
