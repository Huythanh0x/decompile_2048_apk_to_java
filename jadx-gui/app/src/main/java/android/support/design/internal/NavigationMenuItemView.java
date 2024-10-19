package android.support.design.internal;

import android.R;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;

/* loaded from: classes.dex */
public class NavigationMenuItemView extends ForegroundLinearLayout implements MenuView.ItemView {
    private static final int[] CHECKED_STATE_SET = {R.attr.state_checked};
    private FrameLayout mActionArea;
    private final int mIconSize;
    private ColorStateList mIconTintList;
    private MenuItemImpl mItemData;
    private final CheckedTextView mTextView;

    @Override // android.support.v7.view.menu.MenuView.ItemView
    public boolean prefersCondensedTitle() {
        return false;
    }

    @Override // android.support.v7.view.menu.MenuView.ItemView
    public void setShortcut(boolean z, char c) {
    }

    @Override // android.support.v7.view.menu.MenuView.ItemView
    public boolean showsIcon() {
        return true;
    }

    public NavigationMenuItemView(Context context) {
        this(context, null);
    }

    public NavigationMenuItemView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public NavigationMenuItemView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setOrientation(0);
        LayoutInflater.from(context).inflate(android.support.design.R.layout.design_navigation_menu_item, (ViewGroup) this, true);
        this.mIconSize = context.getResources().getDimensionPixelSize(android.support.design.R.dimen.design_navigation_icon_size);
        this.mTextView = (CheckedTextView) findViewById(android.support.design.R.id.design_menu_item_text);
        this.mTextView.setDuplicateParentStateEnabled(true);
    }

    @Override // android.support.v7.view.menu.MenuView.ItemView
    public void initialize(MenuItemImpl menuItemImpl, int i) {
        this.mItemData = menuItemImpl;
        setVisibility(menuItemImpl.isVisible() ? 0 : 8);
        if (getBackground() == null) {
            setBackgroundDrawable(createDefaultBackground());
        }
        setCheckable(menuItemImpl.isCheckable());
        setChecked(menuItemImpl.isChecked());
        setEnabled(menuItemImpl.isEnabled());
        setTitle(menuItemImpl.getTitle());
        setIcon(menuItemImpl.getIcon());
        setActionView(menuItemImpl.getActionView());
    }

    public void recycle() {
        if (this.mActionArea != null) {
            this.mActionArea.removeAllViews();
        }
        this.mTextView.setCompoundDrawables(null, null, null, null);
    }

    private void setActionView(View view) {
        if (this.mActionArea == null) {
            this.mActionArea = (FrameLayout) ((ViewStub) findViewById(android.support.design.R.id.design_menu_item_action_area_stub)).inflate();
        }
        this.mActionArea.removeAllViews();
        if (view != null) {
            this.mActionArea.addView(view);
        }
    }

    private StateListDrawable createDefaultBackground() {
        TypedValue typedValue = new TypedValue();
        if (!getContext().getTheme().resolveAttribute(android.support.design.R.attr.colorControlHighlight, typedValue, true)) {
            return null;
        }
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(CHECKED_STATE_SET, new ColorDrawable(typedValue.data));
        stateListDrawable.addState(EMPTY_STATE_SET, new ColorDrawable(0));
        return stateListDrawable;
    }

    @Override // android.support.v7.view.menu.MenuView.ItemView
    public MenuItemImpl getItemData() {
        return this.mItemData;
    }

    @Override // android.support.v7.view.menu.MenuView.ItemView
    public void setTitle(CharSequence charSequence) {
        this.mTextView.setText(charSequence);
    }

    @Override // android.support.v7.view.menu.MenuView.ItemView
    public void setCheckable(boolean z) {
        refreshDrawableState();
    }

    @Override // android.support.v7.view.menu.MenuView.ItemView
    public void setChecked(boolean z) {
        refreshDrawableState();
        this.mTextView.setChecked(z);
    }

    @Override // android.support.v7.view.menu.MenuView.ItemView
    public void setIcon(Drawable drawable) {
        if (drawable != null) {
            Drawable.ConstantState constantState = drawable.getConstantState();
            if (constantState != null) {
                drawable = constantState.newDrawable();
            }
            drawable = DrawableCompat.wrap(drawable).mutate();
            drawable.setBounds(0, 0, this.mIconSize, this.mIconSize);
            DrawableCompat.setTintList(drawable, this.mIconTintList);
        }
        TextViewCompat.setCompoundDrawablesRelative(this.mTextView, drawable, null, null, null);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected int[] onCreateDrawableState(int i) {
        int[] onCreateDrawableState = super.onCreateDrawableState(i + 1);
        if (this.mItemData != null && this.mItemData.isCheckable() && this.mItemData.isChecked()) {
            mergeDrawableStates(onCreateDrawableState, CHECKED_STATE_SET);
        }
        return onCreateDrawableState;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setIconTintList(ColorStateList colorStateList) {
        this.mIconTintList = colorStateList;
        if (this.mItemData != null) {
            setIcon(this.mItemData.getIcon());
        }
    }

    public void setTextAppearance(Context context, int i) {
        this.mTextView.setTextAppearance(context, i);
    }

    public void setTextColor(ColorStateList colorStateList) {
        this.mTextView.setTextColor(colorStateList);
    }
}
