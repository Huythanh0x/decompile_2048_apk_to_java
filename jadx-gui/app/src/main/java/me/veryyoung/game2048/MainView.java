package me.veryyoung.game2048;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.media.TransportMediator;
import android.view.View;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class MainView extends View {
    static final int BASE_ANIMATION_TIME = 100000000;
    static final float INITIAL_VELOCITY = 0.375f;
    static final float MERGING_ACCELERATION = -0.5f;
    private int TEXT_BLACK;
    private int TEXT_BROWN;
    private int TEXT_WHITE;
    private Bitmap background;
    private Drawable backgroundRectangle;
    private BitmapDrawable[] bitmapCell;
    private int bodyStartYAll;
    float bodyTextSize;
    private Drawable[] cellRectangle;
    private int cellSize;
    private float cellTextSize;
    private Drawable cheatIcon;
    public boolean continueButtonEnabled;
    private String continueText;
    long currentTime;
    private int eYAll;
    public int endingX;
    public int endingY;
    private String endlessModeText;
    private Drawable fadeRectangle;
    private String forNowText;
    public MainGame game;
    float gameOverTextSize;
    private int gridWidth;
    public boolean hasSaveState;
    private String headerText;
    float headerTextSize;
    private String highScoreTitle;
    private int iconPaddingSize;
    public int iconSize;
    private String instructionsText;
    float instructionsTextSize;
    long lastFPSTime;
    private Drawable lightUpRectangle;
    private BitmapDrawable loseGameOverlay;
    private String loseText;
    private Drawable newGameIcon;
    private final int numCellTypes;
    Paint paint;
    boolean refreshLastTime;
    public int sXCheat;
    public int sXNewGame;
    public int sXUndo;
    private int sYAll;
    public int sYIcons;
    private String scoreTitle;
    public int startingX;
    public int startingY;
    private int textPaddingSize;
    private float textSize;
    private int titleStartYAll;
    float titleTextSize;
    private int titleWidthHighScore;
    private int titleWidthScore;
    private Drawable undoIcon;
    private BitmapDrawable winGameContinueOverlay;
    private BitmapDrawable winGameFinalOverlay;
    private String winText;

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(this.background, 0.0f, 0.0f, this.paint);
        drawScoreText(canvas);
        if (!this.game.isActive() && !this.game.aGrid.isAnimationActive()) {
            drawNewGameButton(canvas, true);
        }
        drawCells(canvas);
        if (!this.game.isActive()) {
            drawEndGameState(canvas);
        }
        if (!this.game.canContinue()) {
            drawEndlessText(canvas);
        }
        if (this.game.aGrid.isAnimationActive()) {
            invalidate(this.startingX, this.startingY, this.endingX, this.endingY);
            tick();
        } else {
            if (this.game.isActive() || !this.refreshLastTime) {
                return;
            }
            invalidate();
            this.refreshLastTime = false;
        }
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        getLayout(i, i2);
        createBackgroundBitmap(i, i2);
        createBitmapCells();
        createOverlays();
    }

    private void drawDrawable(Canvas canvas, Drawable drawable, int i, int i2, int i3, int i4) {
        drawable.setBounds(i, i2, i3, i4);
        drawable.draw(canvas);
    }

    private void drawCellText(Canvas canvas, int i, int i2, int i3) {
        int centerText = centerText();
        if (i >= 8) {
            this.paint.setColor(this.TEXT_WHITE);
        } else {
            this.paint.setColor(this.TEXT_BLACK);
        }
        canvas.drawText("" + i, i2 + (this.cellSize / 2), (i3 + (this.cellSize / 2)) - centerText, this.paint);
    }

    private void drawScoreText(Canvas canvas) {
        this.paint.setTextSize(this.bodyTextSize);
        this.paint.setTextAlign(Paint.Align.CENTER);
        int measureText = (int) this.paint.measureText("" + this.game.highScore);
        int measureText2 = (int) this.paint.measureText("" + this.game.score);
        int max = Math.max(this.titleWidthHighScore, measureText) + (this.textPaddingSize * 2);
        int max2 = Math.max(this.titleWidthScore, measureText2) + (this.textPaddingSize * 2);
        int i = max / 2;
        int i2 = max2 / 2;
        int i3 = this.endingX;
        int i4 = i3 - max;
        int i5 = i4 - this.textPaddingSize;
        int i6 = i5 - max2;
        this.backgroundRectangle.setBounds(i4, this.sYAll, i3, this.eYAll);
        this.backgroundRectangle.draw(canvas);
        this.paint.setTextSize(this.titleTextSize);
        this.paint.setColor(this.TEXT_BROWN);
        float f = i4 + i;
        canvas.drawText(this.highScoreTitle, f, this.titleStartYAll, this.paint);
        this.paint.setTextSize(this.bodyTextSize);
        this.paint.setColor(this.TEXT_WHITE);
        canvas.drawText(String.valueOf(this.game.highScore), f, this.bodyStartYAll, this.paint);
        this.backgroundRectangle.setBounds(i6, this.sYAll, i5, this.eYAll);
        this.backgroundRectangle.draw(canvas);
        this.paint.setTextSize(this.titleTextSize);
        this.paint.setColor(this.TEXT_BROWN);
        float f2 = i6 + i2;
        canvas.drawText(this.scoreTitle, f2, this.titleStartYAll, this.paint);
        this.paint.setTextSize(this.bodyTextSize);
        this.paint.setColor(this.TEXT_WHITE);
        canvas.drawText(String.valueOf(this.game.score), f2, this.bodyStartYAll, this.paint);
    }

    private void drawNewGameButton(Canvas canvas, boolean z) {
        if (z) {
            drawDrawable(canvas, this.lightUpRectangle, this.sXNewGame, this.sYIcons, this.sXNewGame + this.iconSize, this.sYIcons + this.iconSize);
        } else {
            drawDrawable(canvas, this.backgroundRectangle, this.sXNewGame, this.sYIcons, this.sXNewGame + this.iconSize, this.sYIcons + this.iconSize);
        }
        drawDrawable(canvas, this.newGameIcon, this.sXNewGame + this.iconPaddingSize, this.sYIcons + this.iconPaddingSize, (this.sXNewGame + this.iconSize) - this.iconPaddingSize, (this.sYIcons + this.iconSize) - this.iconPaddingSize);
    }

    public void drawCheatButton(Canvas canvas) {
        drawDrawable(canvas, this.backgroundRectangle, this.sXCheat, this.sYIcons, this.sXCheat + this.iconSize, this.sYIcons + this.iconSize);
        drawDrawable(canvas, this.cheatIcon, this.sXCheat + this.iconPaddingSize, this.sYIcons + this.iconPaddingSize, (this.sXCheat + this.iconSize) - this.iconPaddingSize, (this.sYIcons + this.iconSize) - this.iconPaddingSize);
    }

    private void drawUndoButton(Canvas canvas) {
        drawDrawable(canvas, this.backgroundRectangle, this.sXUndo, this.sYIcons, this.sXUndo + this.iconSize, this.sYIcons + this.iconSize);
        drawDrawable(canvas, this.undoIcon, this.sXUndo + this.iconPaddingSize, this.sYIcons + this.iconPaddingSize, (this.sXUndo + this.iconSize) - this.iconPaddingSize, (this.sYIcons + this.iconSize) - this.iconPaddingSize);
    }

    private void drawHeader(Canvas canvas) {
        this.paint.setTextSize(this.headerTextSize);
        this.paint.setColor(this.TEXT_BLACK);
        this.paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(this.headerText, this.startingX, this.sYAll - (centerText() * 2), this.paint);
    }

    public void drawInstructions(Canvas canvas) {
        this.paint.setTextSize(this.instructionsTextSize);
        this.paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(this.instructionsText, this.startingX, (this.endingY - (centerText() * 5)) + this.textPaddingSize, this.paint);
    }

    private void drawBackground(Canvas canvas) {
        drawDrawable(canvas, this.backgroundRectangle, this.startingX, this.startingY, this.endingX, this.endingY);
    }

    private void drawBackgroundGrid(Canvas canvas) {
        int i = 0;
        while (true) {
            this.game.getClass();
            if (i >= 4) {
                return;
            }
            int i2 = 0;
            while (true) {
                this.game.getClass();
                if (i2 < 4) {
                    int i3 = this.startingX + this.gridWidth + ((this.cellSize + this.gridWidth) * i);
                    int i4 = i3 + this.cellSize;
                    int i5 = this.startingY + this.gridWidth + ((this.cellSize + this.gridWidth) * i2);
                    drawDrawable(canvas, this.cellRectangle[0], i3, i5, i4, i5 + this.cellSize);
                    i2++;
                }
            }
            //todo fix unreachable code
            i++;
        }
    }

    private void drawCells(Canvas canvas) {
        int i;
        int i2;
        int i3;
        int i4;
        Tile tile;
        this.paint.setTextSize(this.textSize);
        this.paint.setTextAlign(Paint.Align.CENTER);
        int i5 = 0;
        while (true) {
            this.game.getClass();
            int i6 = 4;
            if (i5 >= 4) {
                return;
            }
            int i7 = 0;
            while (true) {
                this.game.getClass();
                if (i7 < i6) {
                    int i8 = this.startingX + this.gridWidth + ((this.cellSize + this.gridWidth) * i5);
                    int i9 = this.cellSize + i8;
                    int i10 = this.startingY + this.gridWidth + ((this.cellSize + this.gridWidth) * i7);
                    int i11 = this.cellSize + i10;
                    Tile cellContent = this.game.grid.getCellContent(i5, i7);
                    if (cellContent != null) {
                        int log2 = log2(cellContent.getValue());
                        ArrayList<AnimationCell> animationCell = this.game.aGrid.getAnimationCell(i5, i7);
                        int size = animationCell.size() - 1;
                        boolean z = false;
                        while (size >= 0) {
                            AnimationCell animationCell2 = animationCell.get(size);
                            if (animationCell2.getAnimationType() == -1) {
                                z = true;
                            }
                            if (animationCell2.isActive()) {
                                if (animationCell2.getAnimationType() == -1) {
                                    i3 = i5;
                                    float percentageDone = (float) animationCell2.getPercentageDone();
                                    this.paint.setTextSize(this.textSize * percentageDone);
                                    float f = (this.cellSize / 2) * (1.0f - percentageDone);
                                    i4 = i7;
                                    this.bitmapCell[log2].setBounds((int) (i8 + f), (int) (i10 + f), (int) (i9 - f), (int) (i11 - f));
                                    this.bitmapCell[log2].draw(canvas);
                                } else {
                                    i3 = i5;
                                    i4 = i7;
                                    if (animationCell2.getAnimationType() == 1) {
                                        double percentageDone2 = animationCell2.getPercentageDone();
                                        float f2 = (float) ((0.375d * percentageDone2) + 1.0d + ((((-0.5d) * percentageDone2) * percentageDone2) / 2.0d));
                                        this.paint.setTextSize(this.textSize * f2);
                                        float f3 = (this.cellSize / 2) * (1.0f - f2);
                                        this.bitmapCell[log2].setBounds((int) (i8 + f3), (int) (i10 + f3), (int) (i9 - f3), (int) (i11 - f3));
                                        this.bitmapCell[log2].draw(canvas);
                                    } else if (animationCell2.getAnimationType() == 0) {
                                        double percentageDone3 = animationCell2.getPercentageDone();
                                        int i12 = animationCell.size() >= 2 ? log2 - 1 : log2;
                                        int i13 = animationCell2.extras[0];
                                        int i14 = animationCell2.extras[1];
                                        int x = cellContent.getX();
                                        int y = cellContent.getY();
                                        tile = cellContent;
                                        double d = (x - i13) * (this.cellSize + this.gridWidth);
                                        double d2 = percentageDone3 - 1.0d;
                                        Double.isNaN(d);
                                        int i15 = (int) (d * d2 * 1.0d);
                                        double d3 = (this.cellSize + this.gridWidth) * (y - i14);
                                        Double.isNaN(d3);
                                        int i16 = (int) (d3 * d2 * 1.0d);
                                        this.bitmapCell[i12].setBounds(i8 + i15, i10 + i16, i15 + i9, i16 + i11);
                                        this.bitmapCell[i12].draw(canvas);
                                        z = true;
                                    }
                                }
                                tile = cellContent;
                                z = true;
                            } else {
                                i3 = i5;
                                i4 = i7;
                                tile = cellContent;
                            }
                            size--;
                            i5 = i3;
                            i7 = i4;
                            cellContent = tile;
                        }
                        i = i5;
                        i2 = i7;
                        if (!z) {
                            this.bitmapCell[log2].setBounds(i8, i10, i9, i11);
                            this.bitmapCell[log2].draw(canvas);
                        }
                    } else {
                        i = i5;
                        i2 = i7;
                    }
                    i7 = i2 + 1;
                    i5 = i;
                    i6 = 4;
                }
            }
            //todo fix unreachable code
            i5++;
        }
    }

    private void drawEndGameState(Canvas canvas) {
        this.continueButtonEnabled = false;
        Iterator<AnimationCell> it = this.game.aGrid.globalAnimation.iterator();
        double d = 1.0d;
        while (it.hasNext()) {
            AnimationCell next = it.next();
            if (next.getAnimationType() == 0) {
                d = next.getPercentageDone();
            }
        }
        BitmapDrawable bitmapDrawable = null;
        if (this.game.gameWon()) {
            if (this.game.canContinue()) {
                this.continueButtonEnabled = true;
                bitmapDrawable = this.winGameContinueOverlay;
            } else {
                bitmapDrawable = this.winGameFinalOverlay;
            }
        } else if (this.game.gameLost()) {
            bitmapDrawable = this.loseGameOverlay;
        }
        if (bitmapDrawable != null) {
            bitmapDrawable.setBounds(this.startingX, this.startingY, this.endingX, this.endingY);
            bitmapDrawable.setAlpha((int) (d * 255.0d));
            bitmapDrawable.draw(canvas);
        }
    }

    private void drawEndlessText(Canvas canvas) {
        this.paint.setTextAlign(Paint.Align.LEFT);
        this.paint.setTextSize(this.bodyTextSize);
        this.paint.setColor(this.TEXT_BLACK);
        canvas.drawText(this.endlessModeText, this.startingX, this.sYIcons - (centerText() * 2), this.paint);
    }

    private void createEndGameStates(Canvas canvas, boolean z, boolean z2) {
        int i = this.endingX - this.startingX;
        int i2 = this.endingY - this.startingY;
        int i3 = i / 2;
        int i4 = i2 / 2;
        if (z) {
            this.lightUpRectangle.setAlpha(TransportMediator.KEYCODE_MEDIA_PAUSE);
            drawDrawable(canvas, this.lightUpRectangle, 0, 0, i, i2);
            this.lightUpRectangle.setAlpha(255);
            this.paint.setColor(this.TEXT_WHITE);
            this.paint.setAlpha(255);
            this.paint.setTextSize(this.gameOverTextSize);
            this.paint.setTextAlign(Paint.Align.CENTER);
            float f = i3;
            canvas.drawText(this.winText, f, i4 - centerText(), this.paint);
            this.paint.setTextSize(this.bodyTextSize);
            //todo fix this undefine variable `r1`
            canvas.drawText(z2 ? this.continueText : this.forNowText, f, (r1 + (this.textPaddingSize * 2)) - (centerText() * 2), this.paint);
            return;
        }
        this.fadeRectangle.setAlpha(TransportMediator.KEYCODE_MEDIA_PAUSE);
        drawDrawable(canvas, this.fadeRectangle, 0, 0, i, i2);
        this.fadeRectangle.setAlpha(255);
        this.paint.setColor(this.TEXT_BLACK);
        this.paint.setAlpha(255);
        this.paint.setTextSize(this.gameOverTextSize);
        this.paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(this.loseText, i3, i4 - centerText(), this.paint);
    }

    private void createBackgroundBitmap(int i, int i2) {
        this.background = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(this.background);
        drawHeader(canvas);
        drawCheatButton(canvas);
        drawNewGameButton(canvas, false);
        drawUndoButton(canvas);
        drawBackground(canvas);
        drawBackgroundGrid(canvas);
        drawInstructions(canvas);
    }

    private void createBitmapCells() {
        this.paint.setTextSize(this.cellTextSize);
        this.paint.setTextAlign(Paint.Align.CENTER);
        Resources resources = getResources();
        for (int i = 0; i < this.bitmapCell.length; i++) {
            Bitmap createBitmap = Bitmap.createBitmap(this.cellSize, this.cellSize, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            drawDrawable(canvas, this.cellRectangle[i], 0, 0, this.cellSize, this.cellSize);
            drawCellText(canvas, (int) Math.pow(2.0d, i), 0, 0);
            this.bitmapCell[i] = new BitmapDrawable(resources, createBitmap);
        }
    }

    private void createOverlays() {
        Resources resources = getResources();
        Bitmap createBitmap = Bitmap.createBitmap(this.endingX - this.startingX, this.endingY - this.startingY, Bitmap.Config.ARGB_8888);
        createEndGameStates(new Canvas(createBitmap), true, true);
        this.winGameContinueOverlay = new BitmapDrawable(resources, createBitmap);
        Bitmap createBitmap2 = Bitmap.createBitmap(this.endingX - this.startingX, this.endingY - this.startingY, Bitmap.Config.ARGB_8888);
        createEndGameStates(new Canvas(createBitmap2), true, false);
        this.winGameFinalOverlay = new BitmapDrawable(resources, createBitmap2);
        Bitmap createBitmap3 = Bitmap.createBitmap(this.endingX - this.startingX, this.endingY - this.startingY, Bitmap.Config.ARGB_8888);
        createEndGameStates(new Canvas(createBitmap3), false, false);
        this.loseGameOverlay = new BitmapDrawable(resources, createBitmap3);
    }

    private void tick() {
        this.currentTime = System.nanoTime();
        this.game.aGrid.tickAll(this.currentTime - this.lastFPSTime);
        this.lastFPSTime = this.currentTime;
    }

    public void resyncTime() {
        this.lastFPSTime = System.nanoTime();
    }

    private static int log2(int i) {
        if (i <= 0) {
            throw new IllegalArgumentException();
        }
        return 31 - Integer.numberOfLeadingZeros(i);
    }

    private void getLayout(int i, int i2) {
        this.game.getClass();
        this.game.getClass();
        this.cellSize = Math.min(i / 5, i2 / 7);
        this.gridWidth = this.cellSize / 7;
        int i3 = (i2 / 2) + (this.cellSize / 2);
        this.iconSize = this.cellSize / 2;
        this.paint.setTextAlign(Paint.Align.CENTER);
        this.paint.setTextSize(this.cellSize);
        this.textSize = (this.cellSize * this.cellSize) / Math.max(this.cellSize, this.paint.measureText("0000"));
        this.cellTextSize = this.textSize * 0.9f;
        this.titleTextSize = this.textSize / 3.0f;
        Double.isNaN(this.textSize);
        //todo fix this undefine variable `r2`
        this.bodyTextSize = (int) (r2 / 1.5d);
        Double.isNaN(this.textSize);
        //todo fix this undefine variable `r2`
        this.instructionsTextSize = (int) (r2 / 1.8d);
        this.headerTextSize = this.textSize * 2.0f;
        this.gameOverTextSize = this.textSize * 2.0f;
        this.textPaddingSize = (int) (this.textSize / 3.0f);
        this.iconPaddingSize = (int) (this.textSize / 5.0f);
        this.game.getClass();
        this.game.getClass();
        double d = i / 2;
        double d2 = this.cellSize + this.gridWidth;
        Double.isNaN(d2);
        Double.isNaN(d);
        double d3 = this.gridWidth / 2;
        Double.isNaN(d3);
        this.startingX = (int) ((d - (d2 * 2.0d)) - d3);
        double d4 = this.cellSize + this.gridWidth;
        Double.isNaN(d4);
        Double.isNaN(d);
        double d5 = d + (d4 * 2.0d);
        double d6 = this.gridWidth / 2;
        Double.isNaN(d6);
        this.endingX = (int) (d5 + d6);
        double d7 = i3;
        double d8 = this.cellSize + this.gridWidth;
        Double.isNaN(d8);
        Double.isNaN(d7);
        double d9 = this.gridWidth / 2;
        Double.isNaN(d9);
        this.startingY = (int) ((d7 - (d8 * 2.0d)) - d9);
        double d10 = this.cellSize + this.gridWidth;
        Double.isNaN(d10);
        Double.isNaN(d7);
        double d11 = d7 + (d10 * 2.0d);
        double d12 = this.gridWidth / 2;
        Double.isNaN(d12);
        this.endingY = (int) (d11 + d12);
        this.paint.setTextSize(this.titleTextSize);
        int centerText = centerText();
        double d13 = this.startingY;
        double d14 = this.cellSize;
        Double.isNaN(d14);
        Double.isNaN(d13);
        this.sYAll = (int) (d13 - (d14 * 1.5d));
        this.titleStartYAll = (int) (((this.sYAll + this.textPaddingSize) + (this.titleTextSize / 2.0f)) - centerText);
        this.bodyStartYAll = (int) (this.titleStartYAll + this.textPaddingSize + (this.titleTextSize / 2.0f) + (this.bodyTextSize / 2.0f));
        this.titleWidthHighScore = (int) this.paint.measureText(this.highScoreTitle);
        this.titleWidthScore = (int) this.paint.measureText(this.scoreTitle);
        this.paint.setTextSize(this.bodyTextSize);
        this.eYAll = (int) (this.bodyStartYAll + centerText() + (this.bodyTextSize / 2.0f) + this.textPaddingSize);
        this.sYIcons = ((this.startingY + this.eYAll) / 2) - (this.iconSize / 2);
        this.sXNewGame = this.endingX - this.iconSize;
        this.sXUndo = (this.sXNewGame - ((this.iconSize * 3) / 2)) - this.iconPaddingSize;
        this.sXCheat = (this.sXUndo - ((this.iconSize * 3) / 2)) - this.iconPaddingSize;
        resyncTime();
    }

    private int centerText() {
        return (int) ((this.paint.descent() + this.paint.ascent()) / 2.0f);
    }

    public MainView(Context context) {
        super(context);
        this.paint = new Paint();
        this.hasSaveState = false;
        this.numCellTypes = 16;
        this.continueButtonEnabled = false;
        this.cellSize = 0;
        this.textSize = 0.0f;
        this.cellTextSize = 0.0f;
        this.gridWidth = 0;
        this.cellRectangle = new Drawable[16];
        this.bitmapCell = new BitmapDrawable[16];
        this.background = null;
        this.lastFPSTime = System.nanoTime();
        this.currentTime = System.nanoTime();
        this.refreshLastTime = true;
        Resources resources = context.getResources();
        this.game = new MainGame(context, this);
        try {
            this.headerText = resources.getString(R.string.header);
            this.highScoreTitle = resources.getString(R.string.high_score);
            this.scoreTitle = resources.getString(R.string.score);
            this.instructionsText = resources.getString(R.string.instructions);
            this.winText = resources.getString(R.string.you_win);
            this.loseText = resources.getString(R.string.game_over);
            this.continueText = resources.getString(R.string.go_on);
            this.forNowText = resources.getString(R.string.for_now);
            this.endlessModeText = resources.getString(R.string.endless);
            this.backgroundRectangle = resources.getDrawable(R.drawable.background_rectangle);
            this.cellRectangle[0] = resources.getDrawable(R.drawable.cell_rectangle);
            this.cellRectangle[1] = resources.getDrawable(R.drawable.cell_rectangle_2);
            this.cellRectangle[2] = resources.getDrawable(R.drawable.cell_rectangle_4);
            this.cellRectangle[3] = resources.getDrawable(R.drawable.cell_rectangle_8);
            this.cellRectangle[4] = resources.getDrawable(R.drawable.cell_rectangle_16);
            this.cellRectangle[5] = resources.getDrawable(R.drawable.cell_rectangle_32);
            this.cellRectangle[6] = resources.getDrawable(R.drawable.cell_rectangle_64);
            this.cellRectangle[7] = resources.getDrawable(R.drawable.cell_rectangle_128);
            this.cellRectangle[8] = resources.getDrawable(R.drawable.cell_rectangle_256);
            this.cellRectangle[9] = resources.getDrawable(R.drawable.cell_rectangle_512);
            this.cellRectangle[10] = resources.getDrawable(R.drawable.cell_rectangle_1024);
            this.cellRectangle[11] = resources.getDrawable(R.drawable.cell_rectangle_2048);
            this.cellRectangle[12] = resources.getDrawable(R.drawable.cell_rectangle_4096);
            this.cellRectangle[13] = resources.getDrawable(R.drawable.cell_rectangle_8192);
            this.cellRectangle[14] = resources.getDrawable(R.drawable.cell_rectangle_16384);
            this.cellRectangle[15] = resources.getDrawable(R.drawable.cell_rectangle_32768);
            this.newGameIcon = resources.getDrawable(R.drawable.ic_action_refresh);
            this.undoIcon = resources.getDrawable(R.drawable.ic_action_undo);
            this.cheatIcon = resources.getDrawable(R.drawable.ic_action_cheat);
            this.lightUpRectangle = resources.getDrawable(R.drawable.light_up_rectangle);
            this.fadeRectangle = resources.getDrawable(R.drawable.fade_rectangle);
            this.TEXT_WHITE = resources.getColor(R.color.text_white);
            this.TEXT_BLACK = resources.getColor(R.color.text_black);
            this.TEXT_BROWN = resources.getColor(R.color.text_brown);
            setBackgroundColor(resources.getColor(R.color.background));
            this.paint.setTypeface(Typeface.createFromAsset(resources.getAssets(), "ClearSans-Bold.ttf"));
            this.paint.setAntiAlias(true);
        } catch (Exception unused) {
            System.out.println("Error getting assets?");
        }
        setOnTouchListener(new InputListener(this));
        this.game.newGame();
    }
}