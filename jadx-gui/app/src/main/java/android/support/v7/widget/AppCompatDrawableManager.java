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
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.util.LruCache;
import android.support.v7.appcompat.R;
import android.util.Log;
import android.util.SparseArray;
import java.util.ArrayList;
import java.util.WeakHashMap;

/* loaded from: classes.dex */
public final class AppCompatDrawableManager {
    private static final boolean DEBUG = false;
    private static AppCompatDrawableManager INSTANCE = null;
    private static final String TAG = "TintManager";
    private ArrayList<InflateDelegate> mDelegates;
    private WeakHashMap<Context, SparseArray<ColorStateList>> mTintLists;
    private static final PorterDuff.Mode DEFAULT_MODE = PorterDuff.Mode.SRC_IN;
    private static final ColorFilterLruCache COLOR_FILTER_CACHE = new ColorFilterLruCache(6);
    private static final int[] COLORFILTER_TINT_COLOR_CONTROL_NORMAL = {R.drawable.abc_textfield_search_default_mtrl_alpha, R.drawable.abc_textfield_default_mtrl_alpha, R.drawable.abc_ab_share_pack_mtrl_alpha};
    private static final int[] TINT_COLOR_CONTROL_NORMAL = {R.drawable.abc_ic_ab_back_mtrl_am_alpha, R.drawable.abc_ic_go_search_api_mtrl_alpha, R.drawable.abc_ic_search_api_mtrl_alpha, R.drawable.abc_ic_commit_search_api_mtrl_alpha, R.drawable.abc_ic_clear_mtrl_alpha, R.drawable.abc_ic_menu_share_mtrl_alpha, R.drawable.abc_ic_menu_copy_mtrl_am_alpha, R.drawable.abc_ic_menu_cut_mtrl_alpha, R.drawable.abc_ic_menu_selectall_mtrl_alpha, R.drawable.abc_ic_menu_paste_mtrl_am_alpha, R.drawable.abc_ic_menu_moreoverflow_mtrl_alpha, R.drawable.abc_ic_voice_search_api_mtrl_alpha};
    private static final int[] COLORFILTER_COLOR_CONTROL_ACTIVATED = {R.drawable.abc_textfield_activated_mtrl_alpha, R.drawable.abc_textfield_search_activated_mtrl_alpha, R.drawable.abc_cab_background_top_mtrl_alpha, R.drawable.abc_text_cursor_material};
    private static final int[] COLORFILTER_COLOR_BACKGROUND_MULTIPLY = {R.drawable.abc_popup_background_mtrl_mult, R.drawable.abc_cab_background_internal_bg, R.drawable.abc_menu_hardkey_panel_mtrl_mult};
    private static final int[] TINT_COLOR_CONTROL_STATE_LIST = {R.drawable.abc_edit_text_material, R.drawable.abc_tab_indicator_material, R.drawable.abc_textfield_search_material, R.drawable.abc_spinner_mtrl_am_alpha, R.drawable.abc_spinner_textfield_background_material, R.drawable.abc_ratingbar_full_material, R.drawable.abc_switch_track_mtrl_alpha, R.drawable.abc_switch_thumb_material, R.drawable.abc_btn_default_mtrl_shape, R.drawable.abc_btn_borderless_material};
    private static final int[] TINT_CHECKABLE_BUTTON_LIST = {R.drawable.abc_btn_check_material, R.drawable.abc_btn_radio_material};

    /* loaded from: classes.dex */
    public interface InflateDelegate {
        @Nullable
        Drawable onInflateDrawable(@NonNull Context context, @DrawableRes int i);
    }

    public static AppCompatDrawableManager get() {
        if (INSTANCE == null) {
            INSTANCE = new AppCompatDrawableManager();
        }
        return INSTANCE;
    }

    public Drawable getDrawable(@NonNull Context context, @DrawableRes int i) {
        return getDrawable(context, i, false);
    }

    public Drawable getDrawable(@NonNull Context context, @DrawableRes int i, boolean z) {
        if (this.mDelegates != null) {
            int size = this.mDelegates.size();
            for (int i2 = 0; i2 < size; i2++) {
                Drawable onInflateDrawable = this.mDelegates.get(i2).onInflateDrawable(context, i);
                if (onInflateDrawable != null) {
                    return onInflateDrawable;
                }
            }
        }
        Drawable drawable = ContextCompat.getDrawable(context, i);
        if (drawable == null) {
            return drawable;
        }
        if (Build.VERSION.SDK_INT >= 8) {
            drawable = drawable.mutate();
        }
        ColorStateList tintList = getTintList(context, i);
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
            return new LayerDrawable(new Drawable[]{getDrawable(context, R.drawable.abc_cab_background_internal_bg), getDrawable(context, R.drawable.abc_cab_background_top_mtrl_alpha)});
        }
        if (i == R.drawable.abc_seekbar_track_material) {
            LayerDrawable layerDrawable = (LayerDrawable) drawable;
            setPorterDuffColorFilter(layerDrawable.findDrawableByLayerId(android.R.id.background), ThemeUtils.getThemeAttrColor(context, R.attr.colorControlNormal), DEFAULT_MODE);
            setPorterDuffColorFilter(layerDrawable.findDrawableByLayerId(android.R.id.secondaryProgress), ThemeUtils.getThemeAttrColor(context, R.attr.colorControlNormal), DEFAULT_MODE);
            setPorterDuffColorFilter(layerDrawable.findDrawableByLayerId(android.R.id.progress), ThemeUtils.getThemeAttrColor(context, R.attr.colorControlActivated), DEFAULT_MODE);
            return drawable;
        }
        if (tintDrawableUsingColorFilter(context, i, drawable) || !z) {
            return drawable;
        }
        return null;
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x005f A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:6:0x004e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final boolean tintDrawableUsingColorFilter(@android.support.annotation.NonNull android.content.Context r7, @android.support.annotation.DrawableRes int r8, @android.support.annotation.NonNull android.graphics.drawable.Drawable r9) {
        /*
            r6 = this;
            android.graphics.PorterDuff$Mode r0 = android.support.v7.widget.AppCompatDrawableManager.DEFAULT_MODE
            int[] r1 = android.support.v7.widget.AppCompatDrawableManager.COLORFILTER_TINT_COLOR_CONTROL_NORMAL
            boolean r1 = arrayContains(r1, r8)
            r2 = -1
            r3 = 0
            r4 = 1
            if (r1 == 0) goto L14
            int r8 = android.support.v7.appcompat.R.attr.colorControlNormal
        Lf:
            r1 = r0
            r5 = -1
            r0 = r8
            r8 = 1
            goto L4c
        L14:
            int[] r1 = android.support.v7.widget.AppCompatDrawableManager.COLORFILTER_COLOR_CONTROL_ACTIVATED
            boolean r1 = arrayContains(r1, r8)
            if (r1 == 0) goto L1f
            int r8 = android.support.v7.appcompat.R.attr.colorControlActivated
            goto Lf
        L1f:
            int[] r1 = android.support.v7.widget.AppCompatDrawableManager.COLORFILTER_COLOR_BACKGROUND_MULTIPLY
            boolean r1 = arrayContains(r1, r8)
            if (r1 == 0) goto L33
            r8 = 16842801(0x1010031, float:2.3693695E-38)
            android.graphics.PorterDuff$Mode r0 = android.graphics.PorterDuff.Mode.MULTIPLY
            r1 = r0
            r8 = 1
            r0 = 16842801(0x1010031, float:2.3693695E-38)
        L31:
            r5 = -1
            goto L4c
        L33:
            int r1 = android.support.v7.appcompat.R.drawable.abc_list_divider_mtrl_alpha
            if (r8 != r1) goto L48
            r8 = 16842800(0x1010030, float:2.3693693E-38)
            r1 = 1109603123(0x42233333, float:40.8)
            int r1 = java.lang.Math.round(r1)
            r5 = r1
            r8 = 1
            r1 = r0
            r0 = 16842800(0x1010030, float:2.3693693E-38)
            goto L4c
        L48:
            r1 = r0
            r8 = 0
            r0 = 0
            goto L31
        L4c:
            if (r8 == 0) goto L5f
            int r7 = android.support.v7.widget.ThemeUtils.getThemeAttrColor(r7, r0)
            android.graphics.PorterDuffColorFilter r7 = getPorterDuffColorFilter(r7, r1)
            r9.setColorFilter(r7)
            if (r5 == r2) goto L5e
            r9.setAlpha(r5)
        L5e:
            return r4
        L5f:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.AppCompatDrawableManager.tintDrawableUsingColorFilter(android.content.Context, int, android.graphics.drawable.Drawable):boolean");
    }

    public void addDelegate(@NonNull InflateDelegate inflateDelegate) {
        if (this.mDelegates == null) {
            this.mDelegates = new ArrayList<>();
        }
        if (this.mDelegates.contains(inflateDelegate)) {
            return;
        }
        this.mDelegates.add(inflateDelegate);
    }

    public void removeDelegate(@NonNull InflateDelegate inflateDelegate) {
        if (this.mDelegates != null) {
            this.mDelegates.remove(inflateDelegate);
        }
    }

    private static boolean arrayContains(int[] iArr, int i) {
        for (int i2 : iArr) {
            if (i2 == i) {
                return true;
            }
        }
        return false;
    }

    final PorterDuff.Mode getTintMode(int i) {
        if (i == R.drawable.abc_switch_thumb_material) {
            return PorterDuff.Mode.MULTIPLY;
        }
        return null;
    }

    public final ColorStateList getTintList(@NonNull Context context, @DrawableRes int i) {
        ColorStateList tintListFromCache = getTintListFromCache(context, i);
        if (tintListFromCache == null) {
            if (i == R.drawable.abc_edit_text_material) {
                tintListFromCache = createEditTextColorStateList(context);
            } else if (i == R.drawable.abc_switch_track_mtrl_alpha) {
                tintListFromCache = createSwitchTrackColorStateList(context);
            } else if (i == R.drawable.abc_switch_thumb_material) {
                tintListFromCache = createSwitchThumbColorStateList(context);
            } else if (i == R.drawable.abc_btn_default_mtrl_shape || i == R.drawable.abc_btn_borderless_material) {
                tintListFromCache = createDefaultButtonColorStateList(context);
            } else if (i == R.drawable.abc_btn_colored_material) {
                tintListFromCache = createColoredButtonColorStateList(context);
            } else if (i == R.drawable.abc_spinner_mtrl_am_alpha || i == R.drawable.abc_spinner_textfield_background_material) {
                tintListFromCache = createSpinnerColorStateList(context);
            } else if (arrayContains(TINT_COLOR_CONTROL_NORMAL, i)) {
                tintListFromCache = ThemeUtils.getThemeAttrColorStateList(context, R.attr.colorControlNormal);
            } else if (arrayContains(TINT_COLOR_CONTROL_STATE_LIST, i)) {
                tintListFromCache = createDefaultColorStateList(context);
            } else if (arrayContains(TINT_CHECKABLE_BUTTON_LIST, i)) {
                tintListFromCache = createCheckableButtonColorStateList(context);
            } else if (i == R.drawable.abc_seekbar_thumb_material) {
                tintListFromCache = createSeekbarThumbColorStateList(context);
            }
            if (tintListFromCache != null) {
                addTintListToCache(context, i, tintListFromCache);
            }
        }
        return tintListFromCache;
    }

    private ColorStateList getTintListFromCache(@NonNull Context context, @DrawableRes int i) {
        SparseArray<ColorStateList> sparseArray;
        if (this.mTintLists == null || (sparseArray = this.mTintLists.get(context)) == null) {
            return null;
        }
        return sparseArray.get(i);
    }

    private void addTintListToCache(@NonNull Context context, @DrawableRes int i, @NonNull ColorStateList colorStateList) {
        if (this.mTintLists == null) {
            this.mTintLists = new WeakHashMap<>();
        }
        SparseArray<ColorStateList> sparseArray = this.mTintLists.get(context);
        if (sparseArray == null) {
            sparseArray = new SparseArray<>();
            this.mTintLists.put(context, sparseArray);
        }
        sparseArray.append(i, colorStateList);
    }

    private ColorStateList createDefaultColorStateList(Context context) {
        int themeAttrColor = ThemeUtils.getThemeAttrColor(context, R.attr.colorControlNormal);
        int themeAttrColor2 = ThemeUtils.getThemeAttrColor(context, R.attr.colorControlActivated);
        return new ColorStateList(new int[][]{ThemeUtils.DISABLED_STATE_SET, ThemeUtils.FOCUSED_STATE_SET, ThemeUtils.ACTIVATED_STATE_SET, ThemeUtils.PRESSED_STATE_SET, ThemeUtils.CHECKED_STATE_SET, ThemeUtils.SELECTED_STATE_SET, ThemeUtils.EMPTY_STATE_SET}, new int[]{ThemeUtils.getDisabledThemeAttrColor(context, R.attr.colorControlNormal), themeAttrColor2, themeAttrColor2, themeAttrColor2, themeAttrColor2, themeAttrColor2, themeAttrColor});
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
