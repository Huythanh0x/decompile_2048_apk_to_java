package android.support.v4.widget;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.TextView;

/* loaded from: classes.dex */
public class TextViewCompat {
    static final TextViewCompatImpl IMPL;

    /* loaded from: classes.dex */
    interface TextViewCompatImpl {
        int getMaxLines(TextView textView);

        int getMinLines(TextView textView);

        void setCompoundDrawablesRelative(@NonNull TextView textView, @Nullable Drawable drawable, @Nullable Drawable drawable2, @Nullable Drawable drawable3, @Nullable Drawable drawable4);

        void setCompoundDrawablesRelativeWithIntrinsicBounds(@NonNull TextView textView, int i, int i2, int i3, int i4);

        void setCompoundDrawablesRelativeWithIntrinsicBounds(@NonNull TextView textView, @Nullable Drawable drawable, @Nullable Drawable drawable2, @Nullable Drawable drawable3, @Nullable Drawable drawable4);
    }

    private TextViewCompat() {
    }

    /* loaded from: classes.dex */
    static class BaseTextViewCompatImpl implements TextViewCompatImpl {
        BaseTextViewCompatImpl() {
        }

        @Override // android.support.v4.widget.TextViewCompat.TextViewCompatImpl
        public void setCompoundDrawablesRelative(@NonNull TextView textView, @Nullable Drawable drawable, @Nullable Drawable drawable2, @Nullable Drawable drawable3, @Nullable Drawable drawable4) {
            textView.setCompoundDrawables(drawable, drawable2, drawable3, drawable4);
        }

        @Override // android.support.v4.widget.TextViewCompat.TextViewCompatImpl
        public void setCompoundDrawablesRelativeWithIntrinsicBounds(@NonNull TextView textView, @Nullable Drawable drawable, @Nullable Drawable drawable2, @Nullable Drawable drawable3, @Nullable Drawable drawable4) {
            textView.setCompoundDrawablesWithIntrinsicBounds(drawable, drawable2, drawable3, drawable4);
        }

        @Override // android.support.v4.widget.TextViewCompat.TextViewCompatImpl
        public void setCompoundDrawablesRelativeWithIntrinsicBounds(@NonNull TextView textView, int i, int i2, int i3, int i4) {
            textView.setCompoundDrawablesWithIntrinsicBounds(i, i2, i3, i4);
        }

        @Override // android.support.v4.widget.TextViewCompat.TextViewCompatImpl
        public int getMaxLines(TextView textView) {
            return TextViewCompatDonut.getMaxLines(textView);
        }

        @Override // android.support.v4.widget.TextViewCompat.TextViewCompatImpl
        public int getMinLines(TextView textView) {
            return TextViewCompatDonut.getMinLines(textView);
        }
    }

    /* loaded from: classes.dex */
    static class JbTextViewCompatImpl extends BaseTextViewCompatImpl {
        JbTextViewCompatImpl() {
        }

        @Override // android.support.v4.widget.TextViewCompat.BaseTextViewCompatImpl, android.support.v4.widget.TextViewCompat.TextViewCompatImpl
        public int getMaxLines(TextView textView) {
            return TextViewCompatJb.getMaxLines(textView);
        }

        @Override // android.support.v4.widget.TextViewCompat.BaseTextViewCompatImpl, android.support.v4.widget.TextViewCompat.TextViewCompatImpl
        public int getMinLines(TextView textView) {
            return TextViewCompatJb.getMinLines(textView);
        }
    }

    /* loaded from: classes.dex */
    static class JbMr1TextViewCompatImpl extends JbTextViewCompatImpl {
        JbMr1TextViewCompatImpl() {
        }

        @Override // android.support.v4.widget.TextViewCompat.BaseTextViewCompatImpl, android.support.v4.widget.TextViewCompat.TextViewCompatImpl
        public void setCompoundDrawablesRelative(@NonNull TextView textView, @Nullable Drawable drawable, @Nullable Drawable drawable2, @Nullable Drawable drawable3, @Nullable Drawable drawable4) {
            TextViewCompatJbMr1.setCompoundDrawablesRelative(textView, drawable, drawable2, drawable3, drawable4);
        }

        @Override // android.support.v4.widget.TextViewCompat.BaseTextViewCompatImpl, android.support.v4.widget.TextViewCompat.TextViewCompatImpl
        public void setCompoundDrawablesRelativeWithIntrinsicBounds(@NonNull TextView textView, @Nullable Drawable drawable, @Nullable Drawable drawable2, @Nullable Drawable drawable3, @Nullable Drawable drawable4) {
            TextViewCompatJbMr1.setCompoundDrawablesRelativeWithIntrinsicBounds(textView, drawable, drawable2, drawable3, drawable4);
        }

        @Override // android.support.v4.widget.TextViewCompat.BaseTextViewCompatImpl, android.support.v4.widget.TextViewCompat.TextViewCompatImpl
        public void setCompoundDrawablesRelativeWithIntrinsicBounds(@NonNull TextView textView, int i, int i2, int i3, int i4) {
            TextViewCompatJbMr1.setCompoundDrawablesRelativeWithIntrinsicBounds(textView, i, i2, i3, i4);
        }
    }

    /* loaded from: classes.dex */
    static class JbMr2TextViewCompatImpl extends JbMr1TextViewCompatImpl {
        JbMr2TextViewCompatImpl() {
        }

        @Override // android.support.v4.widget.TextViewCompat.JbMr1TextViewCompatImpl, android.support.v4.widget.TextViewCompat.BaseTextViewCompatImpl, android.support.v4.widget.TextViewCompat.TextViewCompatImpl
        public void setCompoundDrawablesRelative(@NonNull TextView textView, @Nullable Drawable drawable, @Nullable Drawable drawable2, @Nullable Drawable drawable3, @Nullable Drawable drawable4) {
            TextViewCompatJbMr2.setCompoundDrawablesRelative(textView, drawable, drawable2, drawable3, drawable4);
        }

        @Override // android.support.v4.widget.TextViewCompat.JbMr1TextViewCompatImpl, android.support.v4.widget.TextViewCompat.BaseTextViewCompatImpl, android.support.v4.widget.TextViewCompat.TextViewCompatImpl
        public void setCompoundDrawablesRelativeWithIntrinsicBounds(@NonNull TextView textView, @Nullable Drawable drawable, @Nullable Drawable drawable2, @Nullable Drawable drawable3, @Nullable Drawable drawable4) {
            TextViewCompatJbMr2.setCompoundDrawablesRelativeWithIntrinsicBounds(textView, drawable, drawable2, drawable3, drawable4);
        }

        @Override // android.support.v4.widget.TextViewCompat.JbMr1TextViewCompatImpl, android.support.v4.widget.TextViewCompat.BaseTextViewCompatImpl, android.support.v4.widget.TextViewCompat.TextViewCompatImpl
        public void setCompoundDrawablesRelativeWithIntrinsicBounds(@NonNull TextView textView, int i, int i2, int i3, int i4) {
            TextViewCompatJbMr2.setCompoundDrawablesRelativeWithIntrinsicBounds(textView, i, i2, i3, i4);
        }
    }

    static {
        int i = Build.VERSION.SDK_INT;
        if (i >= 18) {
            IMPL = new JbMr2TextViewCompatImpl();
            return;
        }
        if (i >= 17) {
            IMPL = new JbMr1TextViewCompatImpl();
        } else if (i >= 16) {
            IMPL = new JbTextViewCompatImpl();
        } else {
            IMPL = new BaseTextViewCompatImpl();
        }
    }

    public static void setCompoundDrawablesRelative(@NonNull TextView textView, @Nullable Drawable drawable, @Nullable Drawable drawable2, @Nullable Drawable drawable3, @Nullable Drawable drawable4) {
        IMPL.setCompoundDrawablesRelative(textView, drawable, drawable2, drawable3, drawable4);
    }

    public static void setCompoundDrawablesRelativeWithIntrinsicBounds(@NonNull TextView textView, @Nullable Drawable drawable, @Nullable Drawable drawable2, @Nullable Drawable drawable3, @Nullable Drawable drawable4) {
        IMPL.setCompoundDrawablesRelativeWithIntrinsicBounds(textView, drawable, drawable2, drawable3, drawable4);
    }

    public static void setCompoundDrawablesRelativeWithIntrinsicBounds(@NonNull TextView textView, int i, int i2, int i3, int i4) {
        IMPL.setCompoundDrawablesRelativeWithIntrinsicBounds(textView, i, i2, i3, i4);
    }

    public static int getMaxLines(@NonNull TextView textView) {
        return IMPL.getMaxLines(textView);
    }

    public static int getMinLines(@NonNull TextView textView) {
        return IMPL.getMinLines(textView);
    }
}
