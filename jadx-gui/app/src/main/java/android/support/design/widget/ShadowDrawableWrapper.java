package android.support.design.widget;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.design.R;
import android.support.v7.graphics.drawable.DrawableWrapper;

/* loaded from: classes.dex */
class ShadowDrawableWrapper extends DrawableWrapper {
    static final double COS_45 = Math.cos(Math.toRadians(45.0d));
    static final float SHADOW_BOTTOM_SCALE = 1.0f;
    static final float SHADOW_HORIZ_SCALE = 0.5f;
    static final float SHADOW_MULTIPLIER = 1.5f;
    static final float SHADOW_TOP_SCALE = 0.25f;
    private boolean mAddPaddingForCorners;
    final RectF mContentBounds;
    float mCornerRadius;
    final Paint mCornerShadowPaint;
    Path mCornerShadowPath;
    private boolean mDirty;
    final Paint mEdgeShadowPaint;
    float mMaxShadowSize;
    private boolean mPrintedShadowClipWarning;
    float mRawMaxShadowSize;
    float mRawShadowSize;
    private float mRotation;
    private final int mShadowEndColor;
    private final int mShadowMiddleColor;
    float mShadowSize;
    private final int mShadowStartColor;

    @Override // android.support.v7.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    public ShadowDrawableWrapper(Resources resources, Drawable drawable, float f, float f2, float f3) {
        super(drawable);
        this.mDirty = true;
        this.mAddPaddingForCorners = true;
        this.mPrintedShadowClipWarning = false;
        this.mShadowStartColor = resources.getColor(R.color.design_fab_shadow_start_color);
        this.mShadowMiddleColor = resources.getColor(R.color.design_fab_shadow_mid_color);
        this.mShadowEndColor = resources.getColor(R.color.design_fab_shadow_end_color);
        this.mCornerShadowPaint = new Paint(5);
        this.mCornerShadowPaint.setStyle(Paint.Style.FILL);
        this.mCornerRadius = Math.round(f);
        this.mContentBounds = new RectF();
        this.mEdgeShadowPaint = new Paint(this.mCornerShadowPaint);
        this.mEdgeShadowPaint.setAntiAlias(false);
        setShadowSize(f2, f3);
    }

    private static int toEven(float f) {
        int round = Math.round(f);
        return round % 2 == 1 ? round - 1 : round;
    }

    public void setAddPaddingForCorners(boolean z) {
        this.mAddPaddingForCorners = z;
        invalidateSelf();
    }

    @Override // android.support.v7.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        super.setAlpha(i);
        this.mCornerShadowPaint.setAlpha(i);
        this.mEdgeShadowPaint.setAlpha(i);
    }

    @Override // android.support.v7.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect rect) {
        this.mDirty = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setShadowSize(float f, float f2) {
        if (f < 0.0f || f2 < 0.0f) {
            throw new IllegalArgumentException("invalid shadow size");
        }
        float even = toEven(f);
        float even2 = toEven(f2);
        if (even > even2) {
            if (!this.mPrintedShadowClipWarning) {
                this.mPrintedShadowClipWarning = true;
            }
            even = even2;
        }
        if (this.mRawShadowSize == even && this.mRawMaxShadowSize == even2) {
            return;
        }
        this.mRawShadowSize = even;
        this.mRawMaxShadowSize = even2;
        this.mShadowSize = Math.round(even * SHADOW_MULTIPLIER);
        this.mMaxShadowSize = even2;
        this.mDirty = true;
        invalidateSelf();
    }

    @Override // android.support.v7.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public boolean getPadding(Rect rect) {
        int ceil = (int) Math.ceil(calculateVerticalPadding(this.mRawMaxShadowSize, this.mCornerRadius, this.mAddPaddingForCorners));
        int ceil2 = (int) Math.ceil(calculateHorizontalPadding(this.mRawMaxShadowSize, this.mCornerRadius, this.mAddPaddingForCorners));
        rect.set(ceil2, ceil, ceil2, ceil);
        return true;
    }

    public static float calculateVerticalPadding(float f, float f2, boolean z) {
        if (!z) {
            return f * SHADOW_MULTIPLIER;
        }
        double d = f * SHADOW_MULTIPLIER;
        double d2 = 1.0d - COS_45;
        double d3 = f2;
        Double.isNaN(d3);
        Double.isNaN(d);
        return (float) (d + (d2 * d3));
    }

    public static float calculateHorizontalPadding(float f, float f2, boolean z) {
        if (!z) {
            return f;
        }
        double d = f;
        double d2 = 1.0d - COS_45;
        double d3 = f2;
        Double.isNaN(d3);
        Double.isNaN(d);
        return (float) (d + (d2 * d3));
    }

    public void setCornerRadius(float f) {
        float round = Math.round(f);
        if (this.mCornerRadius == round) {
            return;
        }
        this.mCornerRadius = round;
        this.mDirty = true;
        invalidateSelf();
    }

    @Override // android.support.v7.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        if (this.mDirty) {
            buildComponents(getBounds());
            this.mDirty = false;
        }
        drawShadow(canvas);
        super.draw(canvas);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setRotation(float f) {
        if (this.mRotation != f) {
            this.mRotation = f;
            invalidateSelf();
        }
    }

    private void drawShadow(Canvas canvas) {
        int i;
        float f;
        int i2;
        float f2;
        float f3;
        float f4;
        int save = canvas.save();
        canvas.rotate(this.mRotation, this.mContentBounds.centerX(), this.mContentBounds.centerY());
        float f5 = (-this.mCornerRadius) - this.mShadowSize;
        float f6 = this.mCornerRadius;
        float f7 = f6 * 2.0f;
        boolean z = this.mContentBounds.width() - f7 > 0.0f;
        boolean z2 = this.mContentBounds.height() - f7 > 0.0f;
        float f8 = this.mRawShadowSize - (this.mRawShadowSize * SHADOW_TOP_SCALE);
        float f9 = f6 / ((this.mRawShadowSize - (this.mRawShadowSize * SHADOW_HORIZ_SCALE)) + f6);
        float f10 = f6 / (f8 + f6);
        float f11 = f6 / ((this.mRawShadowSize - (this.mRawShadowSize * SHADOW_BOTTOM_SCALE)) + f6);
        int save2 = canvas.save();
        canvas.translate(this.mContentBounds.left + f6, this.mContentBounds.top + f6);
        canvas.scale(f9, f10);
        canvas.drawPath(this.mCornerShadowPath, this.mCornerShadowPaint);
        if (z) {
            canvas.scale(SHADOW_BOTTOM_SCALE / f9, SHADOW_BOTTOM_SCALE);
            i = save2;
            f = f11;
            i2 = save;
            f2 = f10;
            canvas.drawRect(0.0f, f5, this.mContentBounds.width() - f7, -this.mCornerRadius, this.mEdgeShadowPaint);
        } else {
            i = save2;
            f = f11;
            i2 = save;
            f2 = f10;
        }
        canvas.restoreToCount(i);
        int save3 = canvas.save();
        canvas.translate(this.mContentBounds.right - f6, this.mContentBounds.bottom - f6);
        float f12 = f;
        canvas.scale(f9, f12);
        canvas.rotate(180.0f);
        canvas.drawPath(this.mCornerShadowPath, this.mCornerShadowPaint);
        if (z) {
            canvas.scale(SHADOW_BOTTOM_SCALE / f9, SHADOW_BOTTOM_SCALE);
            f3 = f2;
            f4 = f12;
            canvas.drawRect(0.0f, f5, this.mContentBounds.width() - f7, (-this.mCornerRadius) + this.mShadowSize, this.mEdgeShadowPaint);
        } else {
            f3 = f2;
            f4 = f12;
        }
        canvas.restoreToCount(save3);
        int save4 = canvas.save();
        canvas.translate(this.mContentBounds.left + f6, this.mContentBounds.bottom - f6);
        canvas.scale(f9, f4);
        canvas.rotate(270.0f);
        canvas.drawPath(this.mCornerShadowPath, this.mCornerShadowPaint);
        if (z2) {
            canvas.scale(SHADOW_BOTTOM_SCALE / f4, SHADOW_BOTTOM_SCALE);
            canvas.drawRect(0.0f, f5, this.mContentBounds.height() - f7, -this.mCornerRadius, this.mEdgeShadowPaint);
        }
        canvas.restoreToCount(save4);
        int save5 = canvas.save();
        canvas.translate(this.mContentBounds.right - f6, this.mContentBounds.top + f6);
        float f13 = f3;
        canvas.scale(f9, f13);
        canvas.rotate(90.0f);
        canvas.drawPath(this.mCornerShadowPath, this.mCornerShadowPaint);
        if (z2) {
            canvas.scale(SHADOW_BOTTOM_SCALE / f13, SHADOW_BOTTOM_SCALE);
            canvas.drawRect(0.0f, f5, this.mContentBounds.height() - f7, -this.mCornerRadius, this.mEdgeShadowPaint);
        }
        canvas.restoreToCount(save5);
        canvas.restoreToCount(i2);
    }

    private void buildShadowCorners() {
        RectF rectF = new RectF(-this.mCornerRadius, -this.mCornerRadius, this.mCornerRadius, this.mCornerRadius);
        RectF rectF2 = new RectF(rectF);
        rectF2.inset(-this.mShadowSize, -this.mShadowSize);
        if (this.mCornerShadowPath == null) {
            this.mCornerShadowPath = new Path();
        } else {
            this.mCornerShadowPath.reset();
        }
        this.mCornerShadowPath.setFillType(Path.FillType.EVEN_ODD);
        this.mCornerShadowPath.moveTo(-this.mCornerRadius, 0.0f);
        this.mCornerShadowPath.rLineTo(-this.mShadowSize, 0.0f);
        this.mCornerShadowPath.arcTo(rectF2, 180.0f, 90.0f, false);
        this.mCornerShadowPath.arcTo(rectF, 270.0f, -90.0f, false);
        this.mCornerShadowPath.close();
        float f = -rectF2.top;
        if (f > 0.0f) {
            float f2 = this.mCornerRadius / f;
            this.mCornerShadowPaint.setShader(new RadialGradient(0.0f, 0.0f, f, new int[]{0, this.mShadowStartColor, this.mShadowMiddleColor, this.mShadowEndColor}, new float[]{0.0f, f2, ((SHADOW_BOTTOM_SCALE - f2) / 2.0f) + f2, SHADOW_BOTTOM_SCALE}, Shader.TileMode.CLAMP));
        }
        this.mEdgeShadowPaint.setShader(new LinearGradient(0.0f, rectF.top, 0.0f, rectF2.top, new int[]{this.mShadowStartColor, this.mShadowMiddleColor, this.mShadowEndColor}, new float[]{0.0f, SHADOW_HORIZ_SCALE, SHADOW_BOTTOM_SCALE}, Shader.TileMode.CLAMP));
        this.mEdgeShadowPaint.setAntiAlias(false);
    }

    private void buildComponents(Rect rect) {
        float f = this.mRawMaxShadowSize * SHADOW_MULTIPLIER;
        this.mContentBounds.set(rect.left + this.mRawMaxShadowSize, rect.top + f, rect.right - this.mRawMaxShadowSize, rect.bottom - f);
        getWrappedDrawable().setBounds((int) this.mContentBounds.left, (int) this.mContentBounds.top, (int) this.mContentBounds.right, (int) this.mContentBounds.bottom);
        buildShadowCorners();
    }

    public float getCornerRadius() {
        return this.mCornerRadius;
    }

    public void setShadowSize(float f) {
        setShadowSize(f, this.mRawMaxShadowSize);
    }

    public void setMaxShadowSize(float f) {
        setShadowSize(this.mRawShadowSize, f);
    }

    public float getShadowSize() {
        return this.mRawShadowSize;
    }

    public float getMaxShadowSize() {
        return this.mRawMaxShadowSize;
    }

    public float getMinWidth() {
        return (Math.max(this.mRawMaxShadowSize, this.mCornerRadius + (this.mRawMaxShadowSize / 2.0f)) * 2.0f) + (this.mRawMaxShadowSize * 2.0f);
    }

    public float getMinHeight() {
        return (Math.max(this.mRawMaxShadowSize, this.mCornerRadius + ((this.mRawMaxShadowSize * SHADOW_MULTIPLIER) / 2.0f)) * 2.0f) + (this.mRawMaxShadowSize * SHADOW_MULTIPLIER * 2.0f);
    }
}
