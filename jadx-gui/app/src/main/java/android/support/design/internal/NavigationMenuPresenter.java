package android.support.design.internal;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.design.R;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuPresenter;
import android.support.v7.view.menu.MenuView;
import android.support.v7.view.menu.SubMenuBuilder;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class NavigationMenuPresenter implements MenuPresenter {
    private static final String STATE_ADAPTER = "android:menu:adapter";
    private static final String STATE_HIERARCHY = "android:menu:list";
    private NavigationMenuAdapter mAdapter;
    private MenuPresenter.Callback mCallback;
    private LinearLayout mHeaderLayout;
    private ColorStateList mIconTintList;
    private int mId;
    private Drawable mItemBackground;
    private LayoutInflater mLayoutInflater;
    private MenuBuilder mMenu;
    private NavigationMenuView mMenuView;
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() { // from class: android.support.design.internal.NavigationMenuPresenter.1
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            NavigationMenuPresenter.this.setUpdateSuspended(true);
            MenuItemImpl itemData = ((NavigationMenuItemView) view).getItemData();
            boolean performItemAction = NavigationMenuPresenter.this.mMenu.performItemAction(itemData, NavigationMenuPresenter.this, 0);
            if (itemData != null && itemData.isCheckable() && performItemAction) {
                NavigationMenuPresenter.this.mAdapter.setCheckedItem(itemData);
            }
            NavigationMenuPresenter.this.setUpdateSuspended(false);
            NavigationMenuPresenter.this.updateMenuView(false);
        }
    };
    private int mPaddingSeparator;
    private int mPaddingTopDefault;
    private int mTextAppearance;
    private boolean mTextAppearanceSet;
    private ColorStateList mTextColor;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public interface NavigationMenuItem {
    }

    @Override // android.support.v7.view.menu.MenuPresenter
    public boolean collapseItemActionView(MenuBuilder menuBuilder, MenuItemImpl menuItemImpl) {
        return false;
    }

    @Override // android.support.v7.view.menu.MenuPresenter
    public boolean expandItemActionView(MenuBuilder menuBuilder, MenuItemImpl menuItemImpl) {
        return false;
    }

    @Override // android.support.v7.view.menu.MenuPresenter
    public boolean flagActionItems() {
        return false;
    }

    @Override // android.support.v7.view.menu.MenuPresenter
    public boolean onSubMenuSelected(SubMenuBuilder subMenuBuilder) {
        return false;
    }

    @Override // android.support.v7.view.menu.MenuPresenter
    public void initForMenu(Context context, MenuBuilder menuBuilder) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mMenu = menuBuilder;
        Resources resources = context.getResources();
        this.mPaddingTopDefault = resources.getDimensionPixelOffset(R.dimen.design_navigation_padding_top_default);
        this.mPaddingSeparator = resources.getDimensionPixelOffset(R.dimen.design_navigation_separator_vertical_padding);
    }

    @Override // android.support.v7.view.menu.MenuPresenter
    public MenuView getMenuView(ViewGroup viewGroup) {
        if (this.mMenuView == null) {
            this.mMenuView = (NavigationMenuView) this.mLayoutInflater.inflate(R.layout.design_navigation_menu, viewGroup, false);
            if (this.mAdapter == null) {
                this.mAdapter = new NavigationMenuAdapter();
            }
            this.mHeaderLayout = (LinearLayout) this.mLayoutInflater.inflate(R.layout.design_navigation_item_header, (ViewGroup) this.mMenuView, false);
            this.mMenuView.setAdapter(this.mAdapter);
        }
        return this.mMenuView;
    }

    @Override // android.support.v7.view.menu.MenuPresenter
    public void updateMenuView(boolean z) {
        if (this.mAdapter != null) {
            this.mAdapter.update();
        }
    }

    @Override // android.support.v7.view.menu.MenuPresenter
    public void setCallback(MenuPresenter.Callback callback) {
        this.mCallback = callback;
    }

    @Override // android.support.v7.view.menu.MenuPresenter
    public void onCloseMenu(MenuBuilder menuBuilder, boolean z) {
        if (this.mCallback != null) {
            this.mCallback.onCloseMenu(menuBuilder, z);
        }
    }

    @Override // android.support.v7.view.menu.MenuPresenter
    public int getId() {
        return this.mId;
    }

    public void setId(int i) {
        this.mId = i;
    }

    @Override // android.support.v7.view.menu.MenuPresenter
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        if (this.mMenuView != null) {
            SparseArray<? extends Parcelable> sparseArray = new SparseArray<>();
            this.mMenuView.saveHierarchyState(sparseArray);
            bundle.putSparseParcelableArray("android:menu:list", sparseArray);
        }
        if (this.mAdapter != null) {
            bundle.putBundle(STATE_ADAPTER, this.mAdapter.createInstanceState());
        }
        return bundle;
    }

    @Override // android.support.v7.view.menu.MenuPresenter
    public void onRestoreInstanceState(Parcelable parcelable) {
        Bundle bundle = (Bundle) parcelable;
        SparseArray sparseParcelableArray = bundle.getSparseParcelableArray("android:menu:list");
        if (sparseParcelableArray != null) {
            this.mMenuView.restoreHierarchyState(sparseParcelableArray);
        }
        Bundle bundle2 = bundle.getBundle(STATE_ADAPTER);
        if (bundle2 != null) {
            this.mAdapter.restoreInstanceState(bundle2);
        }
    }

    public void setCheckedItem(MenuItemImpl menuItemImpl) {
        this.mAdapter.setCheckedItem(menuItemImpl);
    }

    public View inflateHeaderView(@LayoutRes int i) {
        View inflate = this.mLayoutInflater.inflate(i, (ViewGroup) this.mHeaderLayout, false);
        addHeaderView(inflate);
        return inflate;
    }

    public void addHeaderView(@NonNull View view) {
        this.mHeaderLayout.addView(view);
        this.mMenuView.setPadding(0, 0, 0, this.mMenuView.getPaddingBottom());
    }

    public void removeHeaderView(@NonNull View view) {
        this.mHeaderLayout.removeView(view);
        if (this.mHeaderLayout.getChildCount() == 0) {
            this.mMenuView.setPadding(0, this.mPaddingTopDefault, 0, this.mMenuView.getPaddingBottom());
        }
    }

    public int getHeaderCount() {
        return this.mHeaderLayout.getChildCount();
    }

    public View getHeaderView(int i) {
        return this.mHeaderLayout.getChildAt(i);
    }

    @Nullable
    public ColorStateList getItemTintList() {
        return this.mIconTintList;
    }

    public void setItemIconTintList(@Nullable ColorStateList colorStateList) {
        this.mIconTintList = colorStateList;
        updateMenuView(false);
    }

    @Nullable
    public ColorStateList getItemTextColor() {
        return this.mTextColor;
    }

    public void setItemTextColor(@Nullable ColorStateList colorStateList) {
        this.mTextColor = colorStateList;
        updateMenuView(false);
    }

    public void setItemTextAppearance(@StyleRes int i) {
        this.mTextAppearance = i;
        this.mTextAppearanceSet = true;
        updateMenuView(false);
    }

    public Drawable getItemBackground() {
        return this.mItemBackground;
    }

    public void setItemBackground(Drawable drawable) {
        this.mItemBackground = drawable;
    }

    public void setUpdateSuspended(boolean z) {
        if (this.mAdapter != null) {
            this.mAdapter.setUpdateSuspended(z);
        }
    }

    /* loaded from: classes.dex */
    private static abstract class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class NormalViewHolder extends ViewHolder {
        public NormalViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup, View.OnClickListener onClickListener) {
            super(layoutInflater.inflate(R.layout.design_navigation_item, viewGroup, false));
            this.itemView.setOnClickListener(onClickListener);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class SubheaderViewHolder extends ViewHolder {
        public SubheaderViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup) {
            super(layoutInflater.inflate(R.layout.design_navigation_item_subheader, viewGroup, false));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class SeparatorViewHolder extends ViewHolder {
        public SeparatorViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup) {
            super(layoutInflater.inflate(R.layout.design_navigation_item_separator, viewGroup, false));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class HeaderViewHolder extends ViewHolder {
        public HeaderViewHolder(View view) {
            super(view);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class NavigationMenuAdapter extends RecyclerView.Adapter<ViewHolder> {
        private static final String STATE_ACTION_VIEWS = "android:menu:action_views";
        private static final String STATE_CHECKED_ITEM = "android:menu:checked";
        private static final int VIEW_TYPE_HEADER = 3;
        private static final int VIEW_TYPE_NORMAL = 0;
        private static final int VIEW_TYPE_SEPARATOR = 2;
        private static final int VIEW_TYPE_SUBHEADER = 1;
        private MenuItemImpl mCheckedItem;
        private final ArrayList<NavigationMenuItem> mItems = new ArrayList<>();
        private ColorDrawable mTransparentIcon;
        private boolean mUpdateSuspended;

        @Override // android.support.v7.widget.RecyclerView.Adapter
        public long getItemId(int i) {
            return i;
        }

        NavigationMenuAdapter() {
            prepareMenuItems();
        }

        @Override // android.support.v7.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.mItems.size();
        }

        @Override // android.support.v7.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            NavigationMenuItem navigationMenuItem = this.mItems.get(i);
            if (navigationMenuItem instanceof NavigationMenuSeparatorItem) {
                return 2;
            }
            if (navigationMenuItem instanceof NavigationMenuHeaderItem) {
                return 3;
            }
            if (navigationMenuItem instanceof NavigationMenuTextItem) {
                return ((NavigationMenuTextItem) navigationMenuItem).getMenuItem().hasSubMenu() ? 1 : 0;
            }
            throw new RuntimeException("Unknown item type.");
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.support.v7.widget.RecyclerView.Adapter
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            switch (i) {
                case 0:
                    return new NormalViewHolder(NavigationMenuPresenter.this.mLayoutInflater, viewGroup, NavigationMenuPresenter.this.mOnClickListener);
                case 1:
                    return new SubheaderViewHolder(NavigationMenuPresenter.this.mLayoutInflater, viewGroup);
                case 2:
                    return new SeparatorViewHolder(NavigationMenuPresenter.this.mLayoutInflater, viewGroup);
                case 3:
                    return new HeaderViewHolder(NavigationMenuPresenter.this.mHeaderLayout);
                default:
                    return null;
            }
        }

        @Override // android.support.v7.widget.RecyclerView.Adapter
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            switch (getItemViewType(i)) {
                case 0:
                    NavigationMenuItemView navigationMenuItemView = (NavigationMenuItemView) viewHolder.itemView;
                    navigationMenuItemView.setIconTintList(NavigationMenuPresenter.this.mIconTintList);
                    if (NavigationMenuPresenter.this.mTextAppearanceSet) {
                        navigationMenuItemView.setTextAppearance(navigationMenuItemView.getContext(), NavigationMenuPresenter.this.mTextAppearance);
                    }
                    if (NavigationMenuPresenter.this.mTextColor != null) {
                        navigationMenuItemView.setTextColor(NavigationMenuPresenter.this.mTextColor);
                    }
                    navigationMenuItemView.setBackgroundDrawable(NavigationMenuPresenter.this.mItemBackground != null ? NavigationMenuPresenter.this.mItemBackground.getConstantState().newDrawable() : null);
                    navigationMenuItemView.initialize(((NavigationMenuTextItem) this.mItems.get(i)).getMenuItem(), 0);
                    return;
                case 1:
                    ((TextView) viewHolder.itemView).setText(((NavigationMenuTextItem) this.mItems.get(i)).getMenuItem().getTitle());
                    return;
                case 2:
                    NavigationMenuSeparatorItem navigationMenuSeparatorItem = (NavigationMenuSeparatorItem) this.mItems.get(i);
                    viewHolder.itemView.setPadding(0, navigationMenuSeparatorItem.getPaddingTop(), 0, navigationMenuSeparatorItem.getPaddingBottom());
                    return;
                default:
                    return;
            }
        }

        @Override // android.support.v7.widget.RecyclerView.Adapter
        public void onViewRecycled(ViewHolder viewHolder) {
            if (viewHolder instanceof NormalViewHolder) {
                ((NavigationMenuItemView) viewHolder.itemView).recycle();
            }
        }

        public void update() {
            prepareMenuItems();
            notifyDataSetChanged();
        }

        private void prepareMenuItems() {
            if (this.mUpdateSuspended) {
                return;
            }
            this.mUpdateSuspended = true;
            this.mItems.clear();
            this.mItems.add(new NavigationMenuHeaderItem());
            int size = NavigationMenuPresenter.this.mMenu.getVisibleItems().size();
            boolean z = false;
            int i = 0;
            int i2 = -1;
            boolean z2 = false;
            int i3 = 0;
            while (i < size) {
                MenuItemImpl menuItemImpl = NavigationMenuPresenter.this.mMenu.getVisibleItems().get(i);
                if (menuItemImpl.isChecked()) {
                    setCheckedItem(menuItemImpl);
                }
                if (menuItemImpl.isCheckable()) {
                    menuItemImpl.setExclusiveCheckable(z);
                }
                if (menuItemImpl.hasSubMenu()) {
                    SubMenu subMenu = menuItemImpl.getSubMenu();
                    if (subMenu.hasVisibleItems()) {
                        if (i != 0) {
                            this.mItems.add(new NavigationMenuSeparatorItem(NavigationMenuPresenter.this.mPaddingSeparator, z ? 1 : 0));
                        }
                        this.mItems.add(new NavigationMenuTextItem(menuItemImpl));
                        int size2 = this.mItems.size();
                        int size3 = subMenu.size();
                        int i4 = 0;
                        boolean z3 = false;
                        while (i4 < size3) {
                            MenuItemImpl menuItemImpl2 = (MenuItemImpl) subMenu.getItem(i4);
                            if (menuItemImpl2.isVisible()) {
                                if (!z3 && menuItemImpl2.getIcon() != null) {
                                    z3 = true;
                                }
                                if (menuItemImpl2.isCheckable()) {
                                    menuItemImpl2.setExclusiveCheckable(z);
                                }
                                if (menuItemImpl.isChecked()) {
                                    setCheckedItem(menuItemImpl);
                                }
                                this.mItems.add(new NavigationMenuTextItem(menuItemImpl2));
                            }
                            i4++;
                            z = false;
                        }
                        if (z3) {
                            appendTransparentIconIfMissing(size2, this.mItems.size());
                        }
                    }
                } else {
                    int groupId = menuItemImpl.getGroupId();
                    if (groupId != i2) {
                        i3 = this.mItems.size();
                        boolean z4 = menuItemImpl.getIcon() != null;
                        if (i != 0) {
                            i3++;
                            this.mItems.add(new NavigationMenuSeparatorItem(NavigationMenuPresenter.this.mPaddingSeparator, NavigationMenuPresenter.this.mPaddingSeparator));
                        }
                        z2 = z4;
                    } else if (!z2 && menuItemImpl.getIcon() != null) {
                        appendTransparentIconIfMissing(i3, this.mItems.size());
                        z2 = true;
                    }
                    if (z2 && menuItemImpl.getIcon() == null) {
                        menuItemImpl.setIcon(android.R.color.transparent);
                    }
                    this.mItems.add(new NavigationMenuTextItem(menuItemImpl));
                    i2 = groupId;
                }
                i++;
                z = false;
            }
            this.mUpdateSuspended = false;
        }

        private void appendTransparentIconIfMissing(int i, int i2) {
            while (i < i2) {
                MenuItemImpl menuItem = ((NavigationMenuTextItem) this.mItems.get(i)).getMenuItem();
                if (menuItem.getIcon() == null) {
                    if (this.mTransparentIcon == null) {
                        this.mTransparentIcon = new ColorDrawable(android.R.color.transparent);
                    }
                    menuItem.setIcon(this.mTransparentIcon);
                }
                i++;
            }
        }

        public void setCheckedItem(MenuItemImpl menuItemImpl) {
            if (this.mCheckedItem == menuItemImpl || !menuItemImpl.isCheckable()) {
                return;
            }
            if (this.mCheckedItem != null) {
                this.mCheckedItem.setChecked(false);
            }
            this.mCheckedItem = menuItemImpl;
            menuItemImpl.setChecked(true);
        }

        public Bundle createInstanceState() {
            Bundle bundle = new Bundle();
            if (this.mCheckedItem != null) {
                bundle.putInt(STATE_CHECKED_ITEM, this.mCheckedItem.getItemId());
            }
            SparseArray<? extends Parcelable> sparseArray = new SparseArray<>();
            Iterator<NavigationMenuItem> it = this.mItems.iterator();
            while (it.hasNext()) {
                NavigationMenuItem next = it.next();
                if (next instanceof NavigationMenuTextItem) {
                    MenuItemImpl menuItem = ((NavigationMenuTextItem) next).getMenuItem();
                    View actionView = menuItem != null ? menuItem.getActionView() : null;
                    if (actionView != null) {
                        ParcelableSparseArray parcelableSparseArray = new ParcelableSparseArray();
                        actionView.saveHierarchyState(parcelableSparseArray);
                        sparseArray.put(menuItem.getItemId(), parcelableSparseArray);
                    }
                }
            }
            bundle.putSparseParcelableArray(STATE_ACTION_VIEWS, sparseArray);
            return bundle;
        }

        public void restoreInstanceState(Bundle bundle) {
            MenuItemImpl menuItem;
            int i = bundle.getInt(STATE_CHECKED_ITEM, 0);
            if (i != 0) {
                this.mUpdateSuspended = true;
                Iterator<NavigationMenuItem> it = this.mItems.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    NavigationMenuItem next = it.next();
                    if ((next instanceof NavigationMenuTextItem) && (menuItem = ((NavigationMenuTextItem) next).getMenuItem()) != null && menuItem.getItemId() == i) {
                        setCheckedItem(menuItem);
                        break;
                    }
                }
                this.mUpdateSuspended = false;
                prepareMenuItems();
            }
            SparseArray sparseParcelableArray = bundle.getSparseParcelableArray(STATE_ACTION_VIEWS);
            Iterator<NavigationMenuItem> it2 = this.mItems.iterator();
            while (it2.hasNext()) {
                NavigationMenuItem next2 = it2.next();
                if (next2 instanceof NavigationMenuTextItem) {
                    MenuItemImpl menuItem2 = ((NavigationMenuTextItem) next2).getMenuItem();
                    View actionView = menuItem2 != null ? menuItem2.getActionView() : null;
                    if (actionView != null) {
                        actionView.restoreHierarchyState((SparseArray) sparseParcelableArray.get(menuItem2.getItemId()));
                    }
                }
            }
        }

        public void setUpdateSuspended(boolean z) {
            this.mUpdateSuspended = z;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class NavigationMenuTextItem implements NavigationMenuItem {
        private final MenuItemImpl mMenuItem;

        private NavigationMenuTextItem(MenuItemImpl menuItemImpl) {
            this.mMenuItem = menuItemImpl;
        }

        public MenuItemImpl getMenuItem() {
            return this.mMenuItem;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class NavigationMenuSeparatorItem implements NavigationMenuItem {
        private final int mPaddingBottom;
        private final int mPaddingTop;

        public NavigationMenuSeparatorItem(int i, int i2) {
            this.mPaddingTop = i;
            this.mPaddingBottom = i2;
        }

        public int getPaddingTop() {
            return this.mPaddingTop;
        }

        public int getPaddingBottom() {
            return this.mPaddingBottom;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class NavigationMenuHeaderItem implements NavigationMenuItem {
        private NavigationMenuHeaderItem() {
        }
    }
}
