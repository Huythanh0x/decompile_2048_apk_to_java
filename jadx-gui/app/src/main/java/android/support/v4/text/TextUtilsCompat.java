package android.support.v4.text;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.Locale;

/* loaded from: classes.dex */
public class TextUtilsCompat {
    private static String ARAB_SCRIPT_SUBTAG;
    private static String HEBR_SCRIPT_SUBTAG;
    private static final TextUtilsCompatImpl IMPL;
    public static final Locale ROOT;

    /* loaded from: classes.dex */
    private static class TextUtilsCompatImpl {
        private TextUtilsCompatImpl() {
        }

        @NonNull
        public String htmlEncode(@NonNull String str) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < str.length(); i++) {
                char charAt = str.charAt(i);
                if (charAt != '\"') {
                    if (charAt != '<') {
                        if (charAt == '>') {
                            sb.append("&gt;");
                        } else {
                            switch (charAt) {
                                case '&':
                                    sb.append("&amp;");
                                    break;
                                case '\'':
                                    sb.append("&#39;");
                                    break;
                                default:
                                    sb.append(charAt);
                                    break;
                            }
                        }
                    } else {
                        sb.append("&lt;");
                    }
                } else {
                    sb.append("&quot;");
                }
            }
            return sb.toString();
        }

        public int getLayoutDirectionFromLocale(@Nullable Locale locale) {
            if (locale == null || locale.equals(TextUtilsCompat.ROOT)) {
                return 0;
            }
            String maximizeAndGetScript = ICUCompat.maximizeAndGetScript(locale);
            if (maximizeAndGetScript == null) {
                return getLayoutDirectionFromFirstChar(locale);
            }
            return (maximizeAndGetScript.equalsIgnoreCase(TextUtilsCompat.ARAB_SCRIPT_SUBTAG) || maximizeAndGetScript.equalsIgnoreCase(TextUtilsCompat.HEBR_SCRIPT_SUBTAG)) ? 1 : 0;
        }

        private static int getLayoutDirectionFromFirstChar(@NonNull Locale locale) {
            switch (Character.getDirectionality(locale.getDisplayName(locale).charAt(0))) {
                case 1:
                case 2:
                    return 1;
                default:
                    return 0;
            }
        }
    }

    /* loaded from: classes.dex */
    private static class TextUtilsCompatJellybeanMr1Impl extends TextUtilsCompatImpl {
        private TextUtilsCompatJellybeanMr1Impl() {
            super();
        }

        @Override // android.support.v4.text.TextUtilsCompat.TextUtilsCompatImpl
        @NonNull
        public String htmlEncode(@NonNull String str) {
            return TextUtilsCompatJellybeanMr1.htmlEncode(str);
        }

        @Override // android.support.v4.text.TextUtilsCompat.TextUtilsCompatImpl
        public int getLayoutDirectionFromLocale(@Nullable Locale locale) {
            return TextUtilsCompatJellybeanMr1.getLayoutDirectionFromLocale(locale);
        }
    }

    static {
        if (Build.VERSION.SDK_INT >= 17) {
            IMPL = new TextUtilsCompatJellybeanMr1Impl();
        } else {
            IMPL = new TextUtilsCompatImpl();
        }
        ROOT = new Locale("", "");
        ARAB_SCRIPT_SUBTAG = "Arab";
        HEBR_SCRIPT_SUBTAG = "Hebr";
    }

    @NonNull
    public static String htmlEncode(@NonNull String str) {
        return IMPL.htmlEncode(str);
    }

    public static int getLayoutDirectionFromLocale(@Nullable Locale locale) {
        return IMPL.getLayoutDirectionFromLocale(locale);
    }
}
