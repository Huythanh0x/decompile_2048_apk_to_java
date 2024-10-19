package android.support.v7.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.util.LruCache;
import android.support.v7.appcompat.R;
import android.util.Log;
import android.util.SparseArray;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

/* loaded from: classes.dex */
public final class TintManager {
    private static final int[] COLORFILTER_COLOR_BACKGROUND_MULTIPLY;
    private static final int[] COLORFILTER_COLOR_CONTROL_ACTIVATED;
    private static final int[] COLORFILTER_TINT_COLOR_CONTROL_NORMAL;
    private static final ColorFilterLruCache COLOR_FILTER_CACHE;
    private static final boolean DEBUG = false;
    private static final PorterDuff.Mode DEFAULT_MODE;
    private static final WeakHashMap<Context, TintManager> INSTANCE_CACHE;
    public static final boolean SHOULD_BE_USED;
    private static final String TAG = "TintManager";
    private static final int[] TINT_CHECKABLE_BUTTON_LIST;
    private static final int[] TINT_COLOR_CONTROL_NORMAL;
    private static final int[] TINT_COLOR_CONTROL_STATE_LIST;
    private final WeakReference<Context> mContextRef;
    private ColorStateList mDefaultColorStateList;
    private SparseArray<ColorStateList> mTintLists;

    static {
        SHOULD_BE_USED = Build.VERSION.SDK_INT < 21;
        DEFAULT_MODE = PorterDuff.Mode.SRC_IN;
        INSTANCE_CACHE = new WeakHashMap<>();
        COLOR_FILTER_CACHE = new ColorFilterLruCache(6);
        COLORFILTER_TINT_COLOR_CONTROL_NORMAL = new int[]{R.drawable.abc_textfield_search_default_mtrl_alpha, R.drawable.abc_textfield_default_mtrl_alpha, R.drawable.abc_ab_share_pack_mtrl_alpha};
        TINT_COLOR_CONTROL_NORMAL = new int[]{R.drawable.abc_ic_ab_back_mtrl_am_alpha, R.drawable.abc_ic_go_search_api_mtrl_alpha, R.drawable.abc_ic_search_api_mtrl_alpha, R.drawable.abc_ic_commit_search_api_mtrl_alpha, R.drawable.abc_ic_clear_mtrl_alpha, R.drawable.abc_ic_menu_share_mtrl_alpha, R.drawable.abc_ic_menu_copy_mtrl_am_alpha, R.drawable.abc_ic_menu_cut_mtrl_alpha, R.drawable.abc_ic_menu_selectall_mtrl_alpha, R.drawable.abc_ic_menu_paste_mtrl_am_alpha, R.drawable.abc_ic_menu_moreoverflow_mtrl_alpha, R.drawable.abc_ic_voice_search_api_mtrl_alpha};
        COLORFILTER_COLOR_CONTROL_ACTIVATED = new int[]{R.drawable.abc_textfield_activated_mtrl_alpha, R.drawable.abc_textfield_search_activated_mtrl_alpha, R.drawable.abc_cab_background_top_mtrl_alpha, R.drawable.abc_text_cursor_material};
        COLORFILTER_COLOR_BACKGROUND_MULTIPLY = new int[]{R.drawable.abc_popup_background_mtrl_mult, R.drawable.abc_cab_background_internal_bg, R.drawable.abc_menu_hardkey_panel_mtrl_mult};
        TINT_COLOR_CONTROL_STATE_LIST = new int[]{R.drawable.abc_edit_text_material, R.drawable.abc_tab_indicator_material, R.drawable.abc_textfield_search_material, R.drawable.abc_spinner_mtrl_am_alpha, R.drawable.abc_spinner_textfield_background_material, R.drawable.abc_ratingbar_full_material, R.drawable.abc_switch_track_mtrl_alpha, R.drawable.abc_switch_thumb_material, R.drawable.abc_btn_default_mtrl_shape, R.drawable.abc_btn_borderless_material};
        TINT_CHECKABLE_BUTTON_LIST = new int[]{R.drawable.abc_btn_check_material, R.drawable.abc_btn_radio_material};
    }

    public static Drawable getDrawable(Context context, int i) {
        if (isInTintList(i)) {
            return get(context).getDrawable(i);
        }
        return ContextCompat.getDrawable(context, i);
    }

    public static TintManager get(Context context) {
        TintManager tintManager = INSTANCE_CACHE.get(context);
        if (tintManager != null) {
            return tintManager;
        }
        TintManager tintManager2 = new TintManager(context);
        INSTANCE_CACHE.put(context, tintManager2);
        return tintManager2;
    }

    private TintManager(Context context) {
        this.mContextRef = new WeakReference<>(context);
    }

    public Drawable getDrawable(int i) {
        return getDrawable(i, false);
    }

    public Drawable getDrawable(int i, boolean z) {
        Context context = this.mContextRef.get();
        if (context == null) {
            return null;
        }
        Drawable drawable = ContextCompat.getDrawable(context, i);
        if (drawable != null) {
            if (Build.VERSION.SDK_INT >= 8) {
                drawable = drawable.mutate();
            }
            ColorStateList tintList = getTintList(i);
            if (tintList != null) {
                Drawable wrap = DrawableCompat.wrap(drawable);
                DrawableCompat.setTintList(wrap, tintList);
                PorterDuff.Mode tintMode = getTintMode(i);
                if (tintMode == null) {
                    return wrap;
                }
                DrawableCompat.setTintMode(wrap, tintMode);
                return wrap;
            }
            if (i == R.drawable.abc_cab_background_top_material) {
                return new LayerDrawable(new Drawable[]{getDrawable(R.drawable.abc_cab_background_internal_bg), getDrawable(R.drawable.abc_cab_background_top_mtrl_alpha)});
            }
            if (i == R.drawable.abc_seekbar_track_material) {
                LayerDrawable layerDrawable = (LayerDrawable) drawable;
                setPorterDuffColorFilter(layerDrawable.findDrawableByLayerId(android.R.id.background), ThemeUtils.getThemeAttrColor(context, R.attr.colorControlNormal), DEFAULT_MODE);
                setPorterDuffColorFilter(layerDrawable.findDrawableByLayerId(android.R.id.secondaryProgress), ThemeUtils.getThemeAttrColor(context, R.attr.colorControlNormal), DEFAULT_MODE);
                setPorterDuffColorFilter(layerDrawable.findDrawableByLayerId(android.R.id.progress), ThemeUtils.getThemeAttrColor(context, R.attr.colorControlActivated), DEFAULT_MODE);
            } else if (!tintDrawableUsingColorFilter(i, drawable) && z) {
                return null;
            }
        }
        return drawable;
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x0059  */
    /* JADX WARN: Removed duplicated region for block: B:14:0x006a A[RETURN] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final boolean tintDrawableUsingColorFilter(int r8, android.graphics.drawable.Drawable r9) {
        /*
            r7 = this;
            java.lang.ref.WeakReference<android.content.Context> r0 = r7.mContextRef
            java.lang.Object r0 = r0.get()
            android.content.Context r0 = (android.content.Context) r0
            r1 = 0
            if (r0 != 0) goto Lc
            return r1
        Lc:
            android.graphics.PorterDuff$Mode r2 = android.support.v7.widget.TintManager.DEFAULT_MODE
            int[] r3 = android.support.v7.widget.TintManager.COLORFILTER_TINT_COLOR_CONTROL_NORMAL
            boolean r3 = arrayContains(r3, r8)
            r4 = -1
            r5 = 1
            if (r3 == 0) goto L1f
            int r8 = android.support.v7.appcompat.R.attr.colorControlNormal
        L1a:
            r3 = r2
            r6 = -1
            r2 = r8
            r8 = 1
            goto L57
        L1f:
            int[] r3 = android.support.v7.widget.TintManager.COLORFILTER_COLOR_CONTROL_ACTIVATED
            boolean r3 = arrayContains(r3, r8)
            if (r3 == 0) goto L2a
            int r8 = android.support.v7.appcompat.R.attr.colorControlActivated
            goto L1a
        L2a:
            int[] r3 = android.support.v7.widget.TintManager.COLORFILTER_COLOR_BACKGROUND_MULTIPLY
            boolean r3 = arrayContains(r3, r8)
            if (r3 == 0) goto L3e
            r8 = 16842801(0x1010031, float:2.3693695E-38)
            android.graphics.PorterDuff$Mode r2 = android.graphics.PorterDuff.Mode.MULTIPLY
            r3 = r2
            r8 = 1
            r2 = 16842801(0x1010031, float:2.3693695E-38)
        L3c:
            r6 = -1
            goto L57
        L3e:
            int r3 = android.support.v7.appcompat.R.drawable.abc_list_divider_mtrl_alpha
            if (r8 != r3) goto L53
            r8 = 16842800(0x1010030, float:2.3693693E-38)
            r3 = 1109603123(0x42233333, float:40.8)
            int r3 = java.lang.Math.round(r3)
            r6 = r3
            r8 = 1
            r3 = r2
            r2 = 16842800(0x1010030, float:2.3693693E-38)
            goto L57
        L53:
            r3 = r2
            r8 = 0
            r2 = 0
            goto L3c
        L57:
            if (r8 == 0) goto L6a
            int r8 = android.support.v7.widget.ThemeUtils.getThemeAttrColor(r0, r2)
            android.graphics.PorterDuffColorFilter r8 = getPorterDuffColorFilter(r8, r3)
            r9.setColorFilter(r8)
            if (r6 == r4) goto L69
            r9.setAlpha(r6)
        L69:
            return r5
        L6a:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.TintManager.tintDrawableUsingColorFilter(int, android.graphics.drawable.Drawable):boolean");
    }

    private static boolean arrayContains(int[] iArr, int i) {
        for (int i2 : iArr) {
            if (i2 == i) {
                return true;
            }
        }
        return false;
    }

    private static boolean isInTintList(int i) {
        return arrayContains(TINT_COLOR_CONTROL_NORMAL, i) || arrayContains(COLORFILTER_TINT_COLOR_CONTROL_NORMAL, i) || arrayContains(COLORFILTER_COLOR_CONTROL_ACTIVATED, i) || arrayContains(TINT_COLOR_CONTROL_STATE_LIST, i) || arrayContains(COLORFILTER_COLOR_BACKGROUND_MULTIPLY, i) || arrayContains(TINT_CHECKABLE_BUTTON_LIST, i) || i == R.drawable.abc_cab_background_top_material;
    }

    final PorterDuff.Mode getTintMode(int i) {
        if (i == R.drawable.abc_switch_thumb_material) {
            return PorterDuff.Mode.MULTIPLY;
        }
        return null;
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x0090  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final android.content.res.ColorStateList getTintList(int r4) {
        /*
            r3 = this;
            java.lang.ref.WeakReference<android.content.Context> r0 = r3.mContextRef
            java.lang.Object r0 = r0.get()
            android.content.Context r0 = (android.content.Context) r0
            r1 = 0
            if (r0 != 0) goto Lc
            return r1
        Lc:
            android.util.SparseArray<android.content.res.ColorStateList> r2 = r3.mTintLists
            if (r2 == 0) goto L18
            android.util.SparseArray<android.content.res.ColorStateList> r1 = r3.mTintLists
            java.lang.Object r1 = r1.get(r4)
            android.content.res.ColorStateList r1 = (android.content.res.ColorStateList) r1
        L18:
            if (r1 != 0) goto La0
            int r2 = android.support.v7.appcompat.R.drawable.abc_edit_text_material
            if (r4 != r2) goto L25
            android.content.res.ColorStateList r0 = r3.createEditTextColorStateList(r0)
        L22:
            r1 = r0
            goto L8e
        L25:
            int r2 = android.support.v7.appcompat.R.drawable.abc_switch_track_mtrl_alpha
            if (r4 != r2) goto L2e
            android.content.res.ColorStateList r0 = r3.createSwitchTrackColorStateList(r0)
            goto L22
        L2e:
            int r2 = android.support.v7.appcompat.R.drawable.abc_switch_thumb_material
            if (r4 != r2) goto L37
            android.content.res.ColorStateList r0 = r3.createSwitchThumbColorStateList(r0)
            goto L22
        L37:
            int r2 = android.support.v7.appcompat.R.drawable.abc_btn_default_mtrl_shape
            if (r4 == r2) goto L89
            int r2 = android.support.v7.appcompat.R.drawable.abc_btn_borderless_material
            if (r4 != r2) goto L40
            goto L89
        L40:
            int r2 = android.support.v7.appcompat.R.drawable.abc_btn_colored_material
            if (r4 != r2) goto L49
            android.content.res.ColorStateList r0 = r3.createColoredButtonColorStateList(r0)
            goto L22
        L49:
            int r2 = android.support.v7.appcompat.R.drawable.abc_spinner_mtrl_am_alpha
            if (r4 == r2) goto L84
            int r2 = android.support.v7.appcompat.R.drawable.abc_spinner_textfield_background_material
            if (r4 != r2) goto L52
            goto L84
        L52:
            int[] r2 = android.support.v7.widget.TintManager.TINT_COLOR_CONTROL_NORMAL
            boolean r2 = arrayContains(r2, r4)
            if (r2 == 0) goto L61
            int r1 = android.support.v7.appcompat.R.attr.colorControlNormal
            android.content.res.ColorStateList r0 = android.support.v7.widget.ThemeUtils.getThemeAttrColorStateList(r0, r1)
            goto L22
        L61:
            int[] r2 = android.support.v7.widget.TintManager.TINT_COLOR_CONTROL_STATE_LIST
            boolean r2 = arrayContains(r2, r4)
            if (r2 == 0) goto L6e
            android.content.res.ColorStateList r0 = r3.getDefaultColorStateList(r0)
            goto L22
        L6e:
            int[] r2 = android.support.v7.widget.TintManager.TINT_CHECKABLE_BUTTON_LIST
            boolean r2 = arrayContains(r2, r4)
            if (r2 == 0) goto L7b
            android.content.res.ColorStateList r0 = r3.createCheckableButtonColorStateList(r0)
            goto L22
        L7b:
            int r2 = android.support.v7.appcompat.R.drawable.abc_seekbar_thumb_material
            if (r4 != r2) goto L8e
            android.content.res.ColorStateList r0 = r3.createSeekbarThumbColorStateList(r0)
            goto L22
        L84:
            android.content.res.ColorStateList r0 = r3.createSpinnerColorStateList(r0)
            goto L22
        L89:
            android.content.res.ColorStateList r0 = r3.createDefaultButtonColorStateList(r0)
            goto L22
        L8e:
            if (r1 == 0) goto La0
            android.util.SparseArray<android.content.res.ColorStateList> r0 = r3.mTintLists
            if (r0 != 0) goto L9b
            android.util.SparseArray r0 = new android.util.SparseArray
            r0.<init>()
            r3.mTintLists = r0
        L9b:
            android.util.SparseArray<android.content.res.ColorStateList> r0 = r3.mTintLists
            r0.append(r4, r1)
        La0:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.TintManager.getTintList(int):android.content.res.ColorStateList");
    }

    private ColorStateList getDefaultColorStateList(Context context) {
        if (this.mDefaultColorStateList == null) {
            int themeAttrColor = ThemeUtils.getThemeAttrColor(context, R.attr.colorControlNormal);
            int themeAttrColor2 = ThemeUtils.getThemeAttrColor(context, R.attr.colorControlActivated);
            this.mDefaultColorStateList = new ColorStateList(new int[][]{ThemeUtils.DISABLED_STATE_SET, ThemeUtils.FOCUSED_STATE_SET, ThemeUtils.ACTIVATED_STATE_SET, ThemeUtils.PRESSED_STATE_SET, ThemeUtils.CHECKED_STATE_SET, ThemeUtils.SELECTED_STATE_SET, ThemeUtils.EMPTY_STATE_SET}, new int[]{ThemeUtils.getDisabledThemeAttrColor(context, R.attr.colorControlNormal), themeAttrColor2, themeAttrColor2, themeAttrColor2, themeAttrColor2, themeAttrColor2, themeAttrColor});
        }
        return this.mDefaultColorStateList;
    }

    private ColorStateList createCheckableButtonColorStateList(Context context) {
        return new ColorStateList(new int[][]{ThemeUtils.DISABLED_STATE_SET, ThemeUtils.CHECKED_STATE_SET, ThemeUtils.EMPTY_STATE_SET}, new int[]{ThemeUtils.getDisabledThemeAttrColor(context, R.attr.colorControlNormal), ThemeUtils.getThemeAttrColor(context, R.attr.colorControlActivated), ThemeUtils.getThemeAttrColor(context, R.attr.colorControlNormal)});
    }

    private ColorStateList createSwitchTrackColorStateList(Context context) {
        return new ColorStateList(new int[][]{ThemeUtils.DISABLED_STATE_SET, ThemeUtils.CHECKED_STATE_SET, ThemeUtils.EMPTY_STATE_SET}, new int[]{ThemeUtils.getThemeAttrColor(context, android.R.attr.colorForeground, 0.1f), ThemeUtils.getThemeAttrColor(context, R.attr.colorControlActivated, 0.3f), ThemeUtils.getThemeAttrColor(context, android.R.attr.colorForeground, 0.3f)});
    }

    private ColorStateList createSwitchThumbColorStateList(Context context) {
        int[][] iArr = new int[3];
        int[] iArr2 = new int[3];
        ColorStateList themeAttrColorStateList = ThemeUtils.getThemeAttrColorStateList(context, R.attr.colorSwitchThumbNormal);
        if (themeAttrColorStateList != null && themeAttrColorStateList.isStateful()) {
            iArr[0] = ThemeUtils.DISABLED_STATE_SET;
            iArr2[0] = themeAttrColorStateList.getColorForState(iArr[0], 0);
            iArr[1] = ThemeUtils.CHECKED_STATE_SET;
            iArr2[1] = ThemeUtils.getThemeAttrColor(context, R.attr.colorControlActivated);
            iArr[2] = ThemeUtils.EMPTY_STATE_SET;
            iArr2[2] = themeAttrColorStateList.getDefaultColor();
        } else {
            iArr[0] = ThemeUtils.DISABLED_STATE_SET;
            iArr2[0] = ThemeUtils.getDisabledThemeAttrColor(context, R.attr.colorSwitchThumbNormal);
            iArr[1] = ThemeUtils.CHECKED_STATE_SET;
            iArr2[1] = ThemeUtils.getThemeAttrColor(context, R.attr.colorControlActivated);
            iArr[2] = ThemeUtils.EMPTY_STATE_SET;
            iArr2[2] = ThemeUtils.getThemeAttrColor(context, R.attr.colorSwitchThumbNormal);
        }
        return new ColorStateList(iArr, iArr2);
    }

    private ColorStateList createEditTextColorStateList(Context context) {
        return new ColorStateList(new int[][]{ThemeUtils.DISABLED_STATE_SET, ThemeUtils.NOT_PRESSED_OR_FOCUSED_STATE_SET, ThemeUtils.EMPTY_STATE_SET}, new int[]{ThemeUtils.getDisabledThemeAttrColor(context, R.attr.colorControlNormal), ThemeUtils.getThemeAttrColor(context, R.attr.colorControlNormal), ThemeUtils.getThemeAttrColor(context, R.attr.colorControlActivated)});
    }

    private ColorStateList createDefaultButtonColorStateList(Context context) {
        return createButtonColorStateList(context, R.attr.colorButtonNormal);
    }

    private ColorStateList createColoredButtonColorStateList(Context context) {
        return createButtonColorStateList(context, R.attr.colorAccent);
    }

    private ColorStateList createButtonColorStateList(Context context, int i) {
        int themeAttrColor = ThemeUtils.getThemeAttrColor(context, i);
        int themeAttrColor2 = ThemeUtils.getThemeAttrColor(context, R.attr.colorControlHighlight);
        return new ColorStateList(new int[][]{ThemeUtils.DISABLED_STATE_SET, ThemeUtils.PRESSED_STATE_SET, ThemeUtils.FOCUSED_STATE_SET, ThemeUtils.EMPTY_STATE_SET}, new int[]{ThemeUtils.getDisabledThemeAttrColor(context, R.attr.colorButtonNormal), ColorUtils.compositeColors(themeAttrColor2, themeAttrColor), ColorUtils.compositeColors(themeAttrColor2, themeAttrColor), themeAttrColor});
    }

    private ColorStateList createSpinnerColorStateList(Context context) {
        return new ColorStateList(new int[][]{ThemeUtils.DISABLED_STATE_SET, ThemeUtils.NOT_PRESSED_OR_FOCUSED_STATE_SET, ThemeUtils.EMPTY_STATE_SET}, new int[]{ThemeUtils.getDisabledThemeAttrColor(context, R.attr.colorControlNormal), ThemeUtils.getThemeAttrColor(context, R.attr.colorControlNormal), ThemeUtils.getThemeAttrColor(context, R.attr.colorControlActivated)});
    }

    private ColorStateList createSeekbarThumbColorStateList(Context context) {
        return new ColorStateList(new int[][]{ThemeUtils.DISABLED_STATE_SET, ThemeUtils.EMPTY_STATE_SET}, new int[]{ThemeUtils.getDisabledThemeAttrColor(context, R.attr.colorControlActivated), ThemeUtils.getThemeAttrColor(context, R.attr.colorControlActivated)});
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ColorFilterLruCache extends LruCache<Integer, PorterDuffColorFilter> {
        public ColorFilterLruCache(int i) {
            super(i);
        }

        PorterDuffColorFilter get(int i, PorterDuff.Mode mode) {
            return get(Integer.valueOf(generateCacheKey(i, mode)));
        }

        PorterDuffColorFilter put(int i, PorterDuff.Mode mode, PorterDuffColorFilter porterDuffColorFilter) {
            return put(Integer.valueOf(generateCacheKey(i, mode)), porterDuffColorFilter);
        }

        private static int generateCacheKey(int i, PorterDuff.Mode mode) {
            return ((i + 31) * 31) + mode.hashCode();
        }
    }

    public static void tintDrawable(Drawable drawable, TintInfo tintInfo, int[] iArr) {
        if (shouldMutateBackground(drawable) && drawable.mutate() != drawable) {
            Log.d(TAG, "Mutated drawable is not the same instance as the input.");
            return;
        }
        if (tintInfo.mHasTintList || tintInfo.mHasTintMode) {
            drawable.setColorFilter(createTintFilter(tintInfo.mHasTintList ? tintInfo.mTintList : null, tintInfo.mHasTintMode ? tintInfo.mTintMode : DEFAULT_MODE, iArr));
        } else {
            drawable.clearColorFilter();
        }
        if (Build.VERSION.SDK_INT <= 10) {
            drawable.invalidateSelf();
        }
    }

    private static boolean shouldMutateBackground(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= 16) {
            return true;
        }
        if (drawable instanceof LayerDrawable) {
            return Build.VERSION.SDK_INT >= 16;
        }
        if (drawable instanceof InsetDrawable) {
            return Build.VERSION.SDK_INT >= 14;
        }
        if (drawable instanceof DrawableContainer) {
            Drawable.ConstantState constantState = drawable.getConstantState();
            if (constantState instanceof DrawableContainer.DrawableContainerState) {
                for (Drawable drawable2 : ((DrawableContainer.DrawableContainerState) constantState).getChildren()) {
                    if (!shouldMutateBackground(drawable2)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static PorterDuffColorFilter createTintFilter(ColorStateList colorStateList, PorterDuff.Mode mode, int[] iArr) {
        if (colorStateList == null || mode == null) {
            return null;
        }
        return getPorterDuffColorFilter(colorStateList.getColorForState(iArr, 0), mode);
    }

    private static PorterDuffColorFilter getPorterDuffColorFilter(int i, PorterDuff.Mode mode) {
        PorterDuffColorFilter porterDuffColorFilter = COLOR_FILTER_CACHE.get(i, mode);
        if (porterDuffColorFilter != null) {
            return porterDuffColorFilter;
        }
        PorterDuffColorFilter porterDuffColorFilter2 = new PorterDuffColorFilter(i, mode);
        COLOR_FILTER_CACHE.put(i, mode, porterDuffColorFilter2);
        return porterDuffColorFilter2;
    }

    private static void setPorterDuffColorFilter(Drawable drawable, int i, PorterDuff.Mode mode) {
        if (mode == null) {
            mode = DEFAULT_MODE;
        }
        drawable.setColorFilter(getPorterDuffColorFilter(i, mode));
    }
}
