package android.support.design.widget;

import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.support.design.R;
import android.support.v4.text.TextDirectionHeuristicsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Interpolator;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class CollapsingTextHelper {
    private static final boolean DEBUG_DRAW = false;
    private static final Paint DEBUG_DRAW_PAINT;
    private static final boolean USE_SCALING_TEXTURE;
    private boolean mBoundsChanged;
    private final Rect mCollapsedBounds;
    private float mCollapsedDrawX;
    private float mCollapsedDrawY;
    private int mCollapsedShadowColor;
    private float mCollapsedShadowDx;
    private float mCollapsedShadowDy;
    private float mCollapsedShadowRadius;
    private int mCollapsedTextColor;
    private Typeface mCollapsedTypeface;
    private final RectF mCurrentBounds;
    private float mCurrentDrawX;
    private float mCurrentDrawY;
    private float mCurrentTextSize;
    private Typeface mCurrentTypeface;
    private boolean mDrawTitle;
    private final Rect mExpandedBounds;
    private float mExpandedDrawX;
    private float mExpandedDrawY;
    private float mExpandedFraction;
    private int mExpandedShadowColor;
    private float mExpandedShadowDx;
    private float mExpandedShadowDy;
    private float mExpandedShadowRadius;
    private int mExpandedTextColor;
    private Bitmap mExpandedTitleTexture;
    private Typeface mExpandedTypeface;
    private boolean mIsRtl;
    private Interpolator mPositionInterpolator;
    private float mScale;
    private CharSequence mText;
    private Interpolator mTextSizeInterpolator;
    private CharSequence mTextToDraw;
    private float mTextureAscent;
    private float mTextureDescent;
    private Paint mTexturePaint;
    private boolean mUseTexture;
    private final View mView;
    private int mExpandedTextGravity = 16;
    private int mCollapsedTextGravity = 16;
    private float mExpandedTextSize = 15.0f;
    private float mCollapsedTextSize = 15.0f;
    private final TextPaint mTextPaint = new TextPaint();

    static {
        USE_SCALING_TEXTURE = Build.VERSION.SDK_INT < 18;
        DEBUG_DRAW_PAINT = null;
        if (DEBUG_DRAW_PAINT != null) {
            DEBUG_DRAW_PAINT.setAntiAlias(true);
            DEBUG_DRAW_PAINT.setColor(-65281);
        }
    }

    public CollapsingTextHelper(View view) {
        this.mView = view;
        this.mTextPaint.setAntiAlias(true);
        this.mCollapsedBounds = new Rect();
        this.mExpandedBounds = new Rect();
        this.mCurrentBounds = new RectF();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setTextSizeInterpolator(Interpolator interpolator) {
        this.mTextSizeInterpolator = interpolator;
        recalculate();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setPositionInterpolator(Interpolator interpolator) {
        this.mPositionInterpolator = interpolator;
        recalculate();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setExpandedTextSize(float f) {
        if (this.mExpandedTextSize != f) {
            this.mExpandedTextSize = f;
            recalculate();
        }
    }

    void setCollapsedTextSize(float f) {
        if (this.mCollapsedTextSize != f) {
            this.mCollapsedTextSize = f;
            recalculate();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCollapsedTextColor(int i) {
        if (this.mCollapsedTextColor != i) {
            this.mCollapsedTextColor = i;
            recalculate();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setExpandedTextColor(int i) {
        if (this.mExpandedTextColor != i) {
            this.mExpandedTextColor = i;
            recalculate();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setExpandedBounds(int i, int i2, int i3, int i4) {
        if (rectEquals(this.mExpandedBounds, i, i2, i3, i4)) {
            return;
        }
        this.mExpandedBounds.set(i, i2, i3, i4);
        this.mBoundsChanged = true;
        onBoundsChanged();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCollapsedBounds(int i, int i2, int i3, int i4) {
        if (rectEquals(this.mCollapsedBounds, i, i2, i3, i4)) {
            return;
        }
        this.mCollapsedBounds.set(i, i2, i3, i4);
        this.mBoundsChanged = true;
        onBoundsChanged();
    }

    void onBoundsChanged() {
        this.mDrawTitle = this.mCollapsedBounds.width() > 0 && this.mCollapsedBounds.height() > 0 && this.mExpandedBounds.width() > 0 && this.mExpandedBounds.height() > 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setExpandedTextGravity(int i) {
        if (this.mExpandedTextGravity != i) {
            this.mExpandedTextGravity = i;
            recalculate();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getExpandedTextGravity() {
        return this.mExpandedTextGravity;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCollapsedTextGravity(int i) {
        if (this.mCollapsedTextGravity != i) {
            this.mCollapsedTextGravity = i;
            recalculate();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getCollapsedTextGravity() {
        return this.mCollapsedTextGravity;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCollapsedTextAppearance(int i) {
        TypedArray obtainStyledAttributes = this.mView.getContext().obtainStyledAttributes(i, R.styleable.TextAppearance);
        if (obtainStyledAttributes.hasValue(R.styleable.TextAppearance_android_textColor)) {
            this.mCollapsedTextColor = obtainStyledAttributes.getColor(R.styleable.TextAppearance_android_textColor, this.mCollapsedTextColor);
        }
        if (obtainStyledAttributes.hasValue(R.styleable.TextAppearance_android_textSize)) {
            this.mCollapsedTextSize = obtainStyledAttributes.getDimensionPixelSize(R.styleable.TextAppearance_android_textSize, (int) this.mCollapsedTextSize);
        }
        this.mCollapsedShadowColor = obtainStyledAttributes.getInt(R.styleable.TextAppearance_android_shadowColor, 0);
        this.mCollapsedShadowDx = obtainStyledAttributes.getFloat(R.styleable.TextAppearance_android_shadowDx, 0.0f);
        this.mCollapsedShadowDy = obtainStyledAttributes.getFloat(R.styleable.TextAppearance_android_shadowDy, 0.0f);
        this.mCollapsedShadowRadius = obtainStyledAttributes.getFloat(R.styleable.TextAppearance_android_shadowRadius, 0.0f);
        obtainStyledAttributes.recycle();
        if (Build.VERSION.SDK_INT >= 16) {
            this.mCollapsedTypeface = readFontFamilyTypeface(i);
        }
        recalculate();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setExpandedTextAppearance(int i) {
        TypedArray obtainStyledAttributes = this.mView.getContext().obtainStyledAttributes(i, R.styleable.TextAppearance);
        if (obtainStyledAttributes.hasValue(R.styleable.TextAppearance_android_textColor)) {
            this.mExpandedTextColor = obtainStyledAttributes.getColor(R.styleable.TextAppearance_android_textColor, this.mExpandedTextColor);
        }
        if (obtainStyledAttributes.hasValue(R.styleable.TextAppearance_android_textSize)) {
            this.mExpandedTextSize = obtainStyledAttributes.getDimensionPixelSize(R.styleable.TextAppearance_android_textSize, (int) this.mExpandedTextSize);
        }
        this.mExpandedShadowColor = obtainStyledAttributes.getInt(R.styleable.TextAppearance_android_shadowColor, 0);
        this.mExpandedShadowDx = obtainStyledAttributes.getFloat(R.styleable.TextAppearance_android_shadowDx, 0.0f);
        this.mExpandedShadowDy = obtainStyledAttributes.getFloat(R.styleable.TextAppearance_android_shadowDy, 0.0f);
        this.mExpandedShadowRadius = obtainStyledAttributes.getFloat(R.styleable.TextAppearance_android_shadowRadius, 0.0f);
        obtainStyledAttributes.recycle();
        if (Build.VERSION.SDK_INT >= 16) {
            this.mExpandedTypeface = readFontFamilyTypeface(i);
        }
        recalculate();
    }

    private Typeface readFontFamilyTypeface(int i) {
        TypedArray obtainStyledAttributes = this.mView.getContext().obtainStyledAttributes(i, new int[]{android.R.attr.fontFamily});
        try {
            String string = obtainStyledAttributes.getString(0);
            if (string != null) {
                return Typeface.create(string, 0);
            }
            obtainStyledAttributes.recycle();
            return null;
        } finally {
            obtainStyledAttributes.recycle();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCollapsedTypeface(Typeface typeface) {
        if (this.mCollapsedTypeface != typeface) {
            this.mCollapsedTypeface = typeface;
            recalculate();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setExpandedTypeface(Typeface typeface) {
        if (this.mExpandedTypeface != typeface) {
            this.mExpandedTypeface = typeface;
            recalculate();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setTypefaces(Typeface typeface) {
        this.mExpandedTypeface = typeface;
        this.mCollapsedTypeface = typeface;
        recalculate();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Typeface getCollapsedTypeface() {
        return this.mCollapsedTypeface != null ? this.mCollapsedTypeface : Typeface.DEFAULT;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Typeface getExpandedTypeface() {
        return this.mExpandedTypeface != null ? this.mExpandedTypeface : Typeface.DEFAULT;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setExpansionFraction(float f) {
        float constrain = MathUtils.constrain(f, 0.0f, 1.0f);
        if (constrain != this.mExpandedFraction) {
            this.mExpandedFraction = constrain;
            calculateCurrentOffsets();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public float getExpansionFraction() {
        return this.mExpandedFraction;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public float getCollapsedTextSize() {
        return this.mCollapsedTextSize;
    }

    float getExpandedTextSize() {
        return this.mExpandedTextSize;
    }

    private void calculateCurrentOffsets() {
        calculateOffsets(this.mExpandedFraction);
    }

    private void calculateOffsets(float f) {
        interpolateBounds(f);
        this.mCurrentDrawX = lerp(this.mExpandedDrawX, this.mCollapsedDrawX, f, this.mPositionInterpolator);
        this.mCurrentDrawY = lerp(this.mExpandedDrawY, this.mCollapsedDrawY, f, this.mPositionInterpolator);
        setInterpolatedTextSize(lerp(this.mExpandedTextSize, this.mCollapsedTextSize, f, this.mTextSizeInterpolator));
        if (this.mCollapsedTextColor != this.mExpandedTextColor) {
            this.mTextPaint.setColor(blendColors(this.mExpandedTextColor, this.mCollapsedTextColor, f));
        } else {
            this.mTextPaint.setColor(this.mCollapsedTextColor);
        }
        this.mTextPaint.setShadowLayer(lerp(this.mExpandedShadowRadius, this.mCollapsedShadowRadius, f, null), lerp(this.mExpandedShadowDx, this.mCollapsedShadowDx, f, null), lerp(this.mExpandedShadowDy, this.mCollapsedShadowDy, f, null), blendColors(this.mExpandedShadowColor, this.mCollapsedShadowColor, f));
        ViewCompat.postInvalidateOnAnimation(this.mView);
    }

    private void calculateBaseOffsets() {
        float f = this.mCurrentTextSize;
        calculateUsingTextSize(this.mCollapsedTextSize);
        float measureText = this.mTextToDraw != null ? this.mTextPaint.measureText(this.mTextToDraw, 0, this.mTextToDraw.length()) : 0.0f;
        int absoluteGravity = GravityCompat.getAbsoluteGravity(this.mCollapsedTextGravity, this.mIsRtl ? 1 : 0);
        int i = absoluteGravity & 112;
        if (i == 48) {
            this.mCollapsedDrawY = this.mCollapsedBounds.top - this.mTextPaint.ascent();
        } else if (i == 80) {
            this.mCollapsedDrawY = this.mCollapsedBounds.bottom;
        } else {
            this.mCollapsedDrawY = this.mCollapsedBounds.centerY() + (((this.mTextPaint.descent() - this.mTextPaint.ascent()) / 2.0f) - this.mTextPaint.descent());
        }
        int i2 = absoluteGravity & 7;
        if (i2 == 1) {
            this.mCollapsedDrawX = this.mCollapsedBounds.centerX() - (measureText / 2.0f);
        } else if (i2 == 5) {
            this.mCollapsedDrawX = this.mCollapsedBounds.right - measureText;
        } else {
            this.mCollapsedDrawX = this.mCollapsedBounds.left;
        }
        calculateUsingTextSize(this.mExpandedTextSize);
        float measureText2 = this.mTextToDraw != null ? this.mTextPaint.measureText(this.mTextToDraw, 0, this.mTextToDraw.length()) : 0.0f;
        int absoluteGravity2 = GravityCompat.getAbsoluteGravity(this.mExpandedTextGravity, this.mIsRtl ? 1 : 0);
        int i3 = absoluteGravity2 & 112;
        if (i3 == 48) {
            this.mExpandedDrawY = this.mExpandedBounds.top - this.mTextPaint.ascent();
        } else if (i3 == 80) {
            this.mExpandedDrawY = this.mExpandedBounds.bottom;
        } else {
            this.mExpandedDrawY = this.mExpandedBounds.centerY() + (((this.mTextPaint.descent() - this.mTextPaint.ascent()) / 2.0f) - this.mTextPaint.descent());
        }
        int i4 = absoluteGravity2 & 7;
        if (i4 == 1) {
            this.mExpandedDrawX = this.mExpandedBounds.centerX() - (measureText2 / 2.0f);
        } else if (i4 == 5) {
            this.mExpandedDrawX = this.mExpandedBounds.right - measureText2;
        } else {
            this.mExpandedDrawX = this.mExpandedBounds.left;
        }
        clearTexture();
        setInterpolatedTextSize(f);
    }

    private void interpolateBounds(float f) {
        this.mCurrentBounds.left = lerp(this.mExpandedBounds.left, this.mCollapsedBounds.left, f, this.mPositionInterpolator);
        this.mCurrentBounds.top = lerp(this.mExpandedDrawY, this.mCollapsedDrawY, f, this.mPositionInterpolator);
        this.mCurrentBounds.right = lerp(this.mExpandedBounds.right, this.mCollapsedBounds.right, f, this.mPositionInterpolator);
        this.mCurrentBounds.bottom = lerp(this.mExpandedBounds.bottom, this.mCollapsedBounds.bottom, f, this.mPositionInterpolator);
    }

    public void draw(Canvas canvas) {
        float ascent;
        int save = canvas.save();
        if (this.mTextToDraw != null && this.mDrawTitle) {
            float f = this.mCurrentDrawX;
            float f2 = this.mCurrentDrawY;
            boolean z = this.mUseTexture && this.mExpandedTitleTexture != null;
            this.mTextPaint.setTextSize(this.mCurrentTextSize);
            if (z) {
                ascent = this.mTextureAscent * this.mScale;
                float f3 = this.mTextureDescent;
                float f4 = this.mScale;
            } else {
                ascent = this.mTextPaint.ascent() * this.mScale;
                this.mTextPaint.descent();
                float f5 = this.mScale;
            }
            if (z) {
                f2 += ascent;
            }
            float f6 = f2;
            if (this.mScale != 1.0f) {
                canvas.scale(this.mScale, this.mScale, f, f6);
            }
            if (z) {
                canvas.drawBitmap(this.mExpandedTitleTexture, f, f6, this.mTexturePaint);
            } else {
                canvas.drawText(this.mTextToDraw, 0, this.mTextToDraw.length(), f, f6, this.mTextPaint);
            }
        }
        canvas.restoreToCount(save);
    }

    private boolean calculateIsRtl(CharSequence charSequence) {
        return (ViewCompat.getLayoutDirection(this.mView) == 1 ? TextDirectionHeuristicsCompat.FIRSTSTRONG_RTL : TextDirectionHeuristicsCompat.FIRSTSTRONG_LTR).isRtl(charSequence, 0, charSequence.length());
    }

    private void setInterpolatedTextSize(float f) {
        calculateUsingTextSize(f);
        this.mUseTexture = USE_SCALING_TEXTURE && this.mScale != 1.0f;
        if (this.mUseTexture) {
            ensureExpandedTexture();
        }
        ViewCompat.postInvalidateOnAnimation(this.mView);
    }

    private void calculateUsingTextSize(float f) {
        boolean z;
        float f2;
        float f3;
        if (this.mText == null) {
            return;
        }
        if (isClose(f, this.mCollapsedTextSize)) {
            f2 = this.mCollapsedBounds.width();
            f3 = this.mCollapsedTextSize;
            this.mScale = 1.0f;
            if (this.mCurrentTypeface != this.mCollapsedTypeface) {
                this.mCurrentTypeface = this.mCollapsedTypeface;
                z = true;
            } else {
                z = false;
            }
        } else {
            float width = this.mExpandedBounds.width();
            float f4 = this.mExpandedTextSize;
            if (this.mCurrentTypeface != this.mExpandedTypeface) {
                this.mCurrentTypeface = this.mExpandedTypeface;
                z = true;
            } else {
                z = false;
            }
            if (isClose(f, this.mExpandedTextSize)) {
                this.mScale = 1.0f;
            } else {
                this.mScale = f / this.mExpandedTextSize;
            }
            f2 = width;
            f3 = f4;
        }
        if (f2 > 0.0f) {
            z = this.mCurrentTextSize != f3 || this.mBoundsChanged || z;
            this.mCurrentTextSize = f3;
            this.mBoundsChanged = false;
        }
        if (this.mTextToDraw == null || z) {
            this.mTextPaint.setTextSize(this.mCurrentTextSize);
            this.mTextPaint.setTypeface(this.mCurrentTypeface);
            CharSequence ellipsize = TextUtils.ellipsize(this.mText, this.mTextPaint, f2, TextUtils.TruncateAt.END);
            if (TextUtils.equals(ellipsize, this.mTextToDraw)) {
                return;
            }
            this.mTextToDraw = ellipsize;
            this.mIsRtl = calculateIsRtl(this.mTextToDraw);
        }
    }

    private void ensureExpandedTexture() {
        if (this.mExpandedTitleTexture != null || this.mExpandedBounds.isEmpty() || TextUtils.isEmpty(this.mTextToDraw)) {
            return;
        }
        calculateOffsets(0.0f);
        this.mTextureAscent = this.mTextPaint.ascent();
        this.mTextureDescent = this.mTextPaint.descent();
        int round = Math.round(this.mTextPaint.measureText(this.mTextToDraw, 0, this.mTextToDraw.length()));
        int round2 = Math.round(this.mTextureDescent - this.mTextureAscent);
        if (round <= 0 || round2 <= 0) {
            return;
        }
        this.mExpandedTitleTexture = Bitmap.createBitmap(round, round2, Bitmap.Config.ARGB_8888);
        new Canvas(this.mExpandedTitleTexture).drawText(this.mTextToDraw, 0, this.mTextToDraw.length(), 0.0f, round2 - this.mTextPaint.descent(), this.mTextPaint);
        if (this.mTexturePaint == null) {
            this.mTexturePaint = new Paint(3);
        }
    }

    public void recalculate() {
        if (this.mView.getHeight() <= 0 || this.mView.getWidth() <= 0) {
            return;
        }
        calculateBaseOffsets();
        calculateCurrentOffsets();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setText(CharSequence charSequence) {
        if (charSequence == null || !charSequence.equals(this.mText)) {
            this.mText = charSequence;
            this.mTextToDraw = null;
            clearTexture();
            recalculate();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CharSequence getText() {
        return this.mText;
    }

    private void clearTexture() {
        if (this.mExpandedTitleTexture != null) {
            this.mExpandedTitleTexture.recycle();
            this.mExpandedTitleTexture = null;
        }
    }

    private static boolean isClose(float f, float f2) {
        return Math.abs(f - f2) < 0.001f;
    }

    int getExpandedTextColor() {
        return this.mExpandedTextColor;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getCollapsedTextColor() {
        return this.mCollapsedTextColor;
    }

    private static int blendColors(int i, int i2, float f) {
        float f2 = 1.0f - f;
        return Color.argb((int) ((Color.alpha(i) * f2) + (Color.alpha(i2) * f)), (int) ((Color.red(i) * f2) + (Color.red(i2) * f)), (int) ((Color.green(i) * f2) + (Color.green(i2) * f)), (int) ((Color.blue(i) * f2) + (Color.blue(i2) * f)));
    }

    private static float lerp(float f, float f2, float f3, Interpolator interpolator) {
        if (interpolator != null) {
            f3 = interpolator.getInterpolation(f3);
        }
        return AnimationUtils.lerp(f, f2, f3);
    }

    private static boolean rectEquals(Rect rect, int i, int i2, int i3, int i4) {
        return rect.left == i && rect.top == i2 && rect.right == i3 && rect.bottom == i4;
    }
}
