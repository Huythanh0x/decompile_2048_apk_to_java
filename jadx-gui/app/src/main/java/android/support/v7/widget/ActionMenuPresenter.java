package android.support.v7.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.GravityCompat;
import android.support.v7.appcompat.R;
import android.support.v7.transition.ActionBarTransition;
import android.support.v7.view.ActionBarPolicy;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.view.menu.BaseMenuPresenter;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.view.menu.MenuPresenter;
import android.support.v7.view.menu.MenuView;
import android.support.v7.view.menu.SubMenuBuilder;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.ListPopupWindow;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class ActionMenuPresenter extends BaseMenuPresenter implements ActionProvider.SubUiVisibilityListener {
    private static final String TAG = "ActionMenuPresenter";
    private final SparseBooleanArray mActionButtonGroups;
    private ActionButtonSubmenu mActionButtonPopup;
    private int mActionItemWidthLimit;
    private boolean mExpandedActionViewsExclusive;
    private int mMaxItems;
    private boolean mMaxItemsSet;
    private int mMinCellSize;
    int mOpenSubMenuId;
    private OverflowMenuButton mOverflowButton;
    private OverflowPopup mOverflowPopup;
    private Drawable mPendingOverflowIcon;
    private boolean mPendingOverflowIconSet;
    private ActionMenuPopupCallback mPopupCallback;
    final PopupPresenterCallback mPopupPresenterCallback;
    private OpenOverflowRunnable mPostedOpenRunnable;
    private boolean mReserveOverflow;
    private boolean mReserveOverflowSet;
    private View mScrapActionButtonView;
    private boolean mStrictWidthLimit;
    private int mWidthLimit;
    private boolean mWidthLimitSet;

    public ActionMenuPresenter(Context context) {
        super(context, R.layout.abc_action_menu_layout, R.layout.abc_action_menu_item_layout);
        this.mActionButtonGroups = new SparseBooleanArray();
        this.mPopupPresenterCallback = new PopupPresenterCallback();
    }

    @Override // android.support.v7.view.menu.BaseMenuPresenter, android.support.v7.view.menu.MenuPresenter
    public void initForMenu(Context context, MenuBuilder menuBuilder) {
        super.initForMenu(context, menuBuilder);
        Resources resources = context.getResources();
        ActionBarPolicy actionBarPolicy = ActionBarPolicy.get(context);
        if (!this.mReserveOverflowSet) {
            this.mReserveOverflow = actionBarPolicy.showsOverflowMenuButton();
        }
        if (!this.mWidthLimitSet) {
            this.mWidthLimit = actionBarPolicy.getEmbeddedMenuWidthLimit();
        }
        if (!this.mMaxItemsSet) {
            this.mMaxItems = actionBarPolicy.getMaxActionButtons();
        }
        int i = this.mWidthLimit;
        if (this.mReserveOverflow) {
            if (this.mOverflowButton == null) {
                this.mOverflowButton = new OverflowMenuButton(this.mSystemContext);
                if (this.mPendingOverflowIconSet) {
                    this.mOverflowButton.setImageDrawable(this.mPendingOverflowIcon);
                    this.mPendingOverflowIcon = null;
                    this.mPendingOverflowIconSet = false;
                }
                int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
                this.mOverflowButton.measure(makeMeasureSpec, makeMeasureSpec);
            }
            i -= this.mOverflowButton.getMeasuredWidth();
        } else {
            this.mOverflowButton = null;
        }
        this.mActionItemWidthLimit = i;
        this.mMinCellSize = (int) (resources.getDisplayMetrics().density * 56.0f);
        this.mScrapActionButtonView = null;
    }

    public void onConfigurationChanged(Configuration configuration) {
        if (!this.mMaxItemsSet) {
            this.mMaxItems = this.mContext.getResources().getInteger(R.integer.abc_max_action_buttons);
        }
        if (this.mMenu != null) {
            this.mMenu.onItemsChanged(true);
        }
    }

    public void setWidthLimit(int i, boolean z) {
        this.mWidthLimit = i;
        this.mStrictWidthLimit = z;
        this.mWidthLimitSet = true;
    }

    public void setReserveOverflow(boolean z) {
        this.mReserveOverflow = z;
        this.mReserveOverflowSet = true;
    }

    public void setItemLimit(int i) {
        this.mMaxItems = i;
        this.mMaxItemsSet = true;
    }

    public void setExpandedActionViewsExclusive(boolean z) {
        this.mExpandedActionViewsExclusive = z;
    }

    public void setOverflowIcon(Drawable drawable) {
        if (this.mOverflowButton != null) {
            this.mOverflowButton.setImageDrawable(drawable);
        } else {
            this.mPendingOverflowIconSet = true;
            this.mPendingOverflowIcon = drawable;
        }
    }

    public Drawable getOverflowIcon() {
        if (this.mOverflowButton != null) {
            return this.mOverflowButton.getDrawable();
        }
        if (this.mPendingOverflowIconSet) {
            return this.mPendingOverflowIcon;
        }
        return null;
    }

    @Override // android.support.v7.view.menu.BaseMenuPresenter, android.support.v7.view.menu.MenuPresenter
    public MenuView getMenuView(ViewGroup viewGroup) {
        MenuView menuView = super.getMenuView(viewGroup);
        ((ActionMenuView) menuView).setPresenter(this);
        return menuView;
    }

    @Override // android.support.v7.view.menu.BaseMenuPresenter
    public View getItemView(MenuItemImpl menuItemImpl, View view, ViewGroup viewGroup) {
        View actionView = menuItemImpl.getActionView();
        if (actionView == null || menuItemImpl.hasCollapsibleActionView()) {
            actionView = super.getItemView(menuItemImpl, view, viewGroup);
        }
        actionView.setVisibility(menuItemImpl.isActionViewExpanded() ? 8 : 0);
        ActionMenuView actionMenuView = (ActionMenuView) viewGroup;
        ViewGroup.LayoutParams layoutParams = actionView.getLayoutParams();
        if (!actionMenuView.checkLayoutParams(layoutParams)) {
            actionView.setLayoutParams(actionMenuView.generateLayoutParams(layoutParams));
        }
        return actionView;
    }

    @Override // android.support.v7.view.menu.BaseMenuPresenter
    public void bindItemView(MenuItemImpl menuItemImpl, MenuView.ItemView itemView) {
        itemView.initialize(menuItemImpl, 0);
        ActionMenuItemView actionMenuItemView = (ActionMenuItemView) itemView;
        actionMenuItemView.setItemInvoker((ActionMenuView) this.mMenuView);
        if (this.mPopupCallback == null) {
            this.mPopupCallback = new ActionMenuPopupCallback();
        }
        actionMenuItemView.setPopupCallback(this.mPopupCallback);
    }

    @Override // android.support.v7.view.menu.BaseMenuPresenter
    public boolean shouldIncludeItem(int i, MenuItemImpl menuItemImpl) {
        return menuItemImpl.isActionButton();
    }

    @Override // android.support.v7.view.menu.BaseMenuPresenter, android.support.v7.view.menu.MenuPresenter
    public void updateMenuView(boolean z) {
        ViewGroup viewGroup = (ViewGroup) ((View) this.mMenuView).getParent();
        if (viewGroup != null) {
            ActionBarTransition.beginDelayedTransition(viewGroup);
        }
        super.updateMenuView(z);
        ((View) this.mMenuView).requestLayout();
        boolean z2 = false;
        if (this.mMenu != null) {
            ArrayList<MenuItemImpl> actionItems = this.mMenu.getActionItems();
            int size = actionItems.size();
            for (int i = 0; i < size; i++) {
                ActionProvider supportActionProvider = actionItems.get(i).getSupportActionProvider();
                if (supportActionProvider != null) {
                    supportActionProvider.setSubUiVisibilityListener(this);
                }
            }
        }
        ArrayList<MenuItemImpl> nonActionItems = this.mMenu != null ? this.mMenu.getNonActionItems() : null;
        if (this.mReserveOverflow && nonActionItems != null) {
            int size2 = nonActionItems.size();
            if (size2 == 1) {
                z2 = !nonActionItems.get(0).isActionViewExpanded();
            } else if (size2 > 0) {
                z2 = true;
            }
        }
        if (z2) {
            if (this.mOverflowButton == null) {
                this.mOverflowButton = new OverflowMenuButton(this.mSystemContext);
            }
            ViewGroup viewGroup2 = (ViewGroup) this.mOverflowButton.getParent();
            if (viewGroup2 != this.mMenuView) {
                if (viewGroup2 != null) {
                    viewGroup2.removeView(this.mOverflowButton);
                }
                ActionMenuView actionMenuView = (ActionMenuView) this.mMenuView;
                actionMenuView.addView(this.mOverflowButton, actionMenuView.generateOverflowButtonLayoutParams());
            }
        } else if (this.mOverflowButton != null && this.mOverflowButton.getParent() == this.mMenuView) {
            ((ViewGroup) this.mMenuView).removeView(this.mOverflowButton);
        }
        ((ActionMenuView) this.mMenuView).setOverflowReserved(this.mReserveOverflow);
    }

    @Override // android.support.v7.view.menu.BaseMenuPresenter
    public boolean filterLeftoverView(ViewGroup viewGroup, int i) {
        if (viewGroup.getChildAt(i) == this.mOverflowButton) {
            return false;
        }
        return super.filterLeftoverView(viewGroup, i);
    }

    @Override // android.support.v7.view.menu.BaseMenuPresenter, android.support.v7.view.menu.MenuPresenter
    public boolean onSubMenuSelected(SubMenuBuilder subMenuBuilder) {
        if (!subMenuBuilder.hasVisibleItems()) {
            return false;
        }
        SubMenuBuilder subMenuBuilder2 = subMenuBuilder;
        while (subMenuBuilder2.getParentMenu() != this.mMenu) {
            subMenuBuilder2 = (SubMenuBuilder) subMenuBuilder2.getParentMenu();
        }
        View findViewForItem = findViewForItem(subMenuBuilder2.getItem());
        if (findViewForItem == null) {
            if (this.mOverflowButton == null) {
                return false;
            }
            findViewForItem = this.mOverflowButton;
        }
        this.mOpenSubMenuId = subMenuBuilder.getItem().getItemId();
        this.mActionButtonPopup = new ActionButtonSubmenu(this.mContext, subMenuBuilder);
        this.mActionButtonPopup.setAnchorView(findViewForItem);
        this.mActionButtonPopup.show();
        super.onSubMenuSelected(subMenuBuilder);
        return true;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private View findViewForItem(MenuItem menuItem) {
        ViewGroup viewGroup = (ViewGroup) this.mMenuView;
        if (viewGroup == null) {
            return null;
        }
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if ((childAt instanceof MenuView.ItemView) && ((MenuView.ItemView) childAt).getItemData() == menuItem) {
                return childAt;
            }
        }
        return null;
    }

    public boolean showOverflowMenu() {
        if (!this.mReserveOverflow || isOverflowMenuShowing() || this.mMenu == null || this.mMenuView == null || this.mPostedOpenRunnable != null || this.mMenu.getNonActionItems().isEmpty()) {
            return false;
        }
        this.mPostedOpenRunnable = new OpenOverflowRunnable(new OverflowPopup(this.mContext, this.mMenu, this.mOverflowButton, true));
        ((View) this.mMenuView).post(this.mPostedOpenRunnable);
        super.onSubMenuSelected(null);
        return true;
    }

    public boolean hideOverflowMenu() {
        if (this.mPostedOpenRunnable != null && this.mMenuView != null) {
            ((View) this.mMenuView).removeCallbacks(this.mPostedOpenRunnable);
            this.mPostedOpenRunnable = null;
            return true;
        }
        OverflowPopup overflowPopup = this.mOverflowPopup;
        if (overflowPopup == null) {
            return false;
        }
        overflowPopup.dismiss();
        return true;
    }

    public boolean dismissPopupMenus() {
        return hideOverflowMenu() | hideSubMenus();
    }

    public boolean hideSubMenus() {
        if (this.mActionButtonPopup == null) {
            return false;
        }
        this.mActionButtonPopup.dismiss();
        return true;
    }

    public boolean isOverflowMenuShowing() {
        return this.mOverflowPopup != null && this.mOverflowPopup.isShowing();
    }

    public boolean isOverflowMenuShowPending() {
        return this.mPostedOpenRunnable != null || isOverflowMenuShowing();
    }

    public boolean isOverflowReserved() {
        return this.mReserveOverflow;
    }

    @Override // android.support.v7.view.menu.BaseMenuPresenter, android.support.v7.view.menu.MenuPresenter
    public boolean flagActionItems() {
        int i;
        int i2;
        int i3;
        boolean z;
        ActionMenuPresenter actionMenuPresenter = this;
        ArrayList<MenuItemImpl> visibleItems = actionMenuPresenter.mMenu.getVisibleItems();
        int size = visibleItems.size();
        int i4 = actionMenuPresenter.mMaxItems;
        int i5 = actionMenuPresenter.mActionItemWidthLimit;
        int i6 = 0;
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
        ViewGroup viewGroup = (ViewGroup) actionMenuPresenter.mMenuView;
        int i7 = i4;
        int i8 = 0;
        boolean z2 = false;
        int i9 = 0;
        for (int i10 = 0; i10 < size; i10++) {
            MenuItemImpl menuItemImpl = visibleItems.get(i10);
            if (menuItemImpl.requiresActionButton()) {
                i8++;
            } else if (menuItemImpl.requestsActionButton()) {
                i9++;
            } else {
                z2 = true;
            }
            if (actionMenuPresenter.mExpandedActionViewsExclusive && menuItemImpl.isActionViewExpanded()) {
                i7 = 0;
            }
        }
        if (actionMenuPresenter.mReserveOverflow && (z2 || i9 + i8 > i7)) {
            i7--;
        }
        int i11 = i7 - i8;
        SparseBooleanArray sparseBooleanArray = actionMenuPresenter.mActionButtonGroups;
        sparseBooleanArray.clear();
        if (actionMenuPresenter.mStrictWidthLimit) {
            i = i5 / actionMenuPresenter.mMinCellSize;
            i2 = ((i5 % actionMenuPresenter.mMinCellSize) / i) + actionMenuPresenter.mMinCellSize;
        } else {
            i = 0;
            i2 = 0;
        }
        int i12 = i5;
        int i13 = 0;
        int i14 = 0;
        while (i13 < size) {
            MenuItemImpl menuItemImpl2 = visibleItems.get(i13);
            if (menuItemImpl2.requiresActionButton()) {
                View itemView = actionMenuPresenter.getItemView(menuItemImpl2, actionMenuPresenter.mScrapActionButtonView, viewGroup);
                if (actionMenuPresenter.mScrapActionButtonView == null) {
                    actionMenuPresenter.mScrapActionButtonView = itemView;
                }
                if (actionMenuPresenter.mStrictWidthLimit) {
                    i -= ActionMenuView.measureChildForCells(itemView, i2, i, makeMeasureSpec, i6);
                } else {
                    itemView.measure(makeMeasureSpec, makeMeasureSpec);
                }
                int measuredWidth = itemView.getMeasuredWidth();
                i12 -= measuredWidth;
                if (i14 != 0) {
                    measuredWidth = i14;
                }
                int groupId = menuItemImpl2.getGroupId();
                if (groupId != 0) {
                    z = true;
                    sparseBooleanArray.put(groupId, true);
                } else {
                    z = true;
                }
                menuItemImpl2.setIsActionButton(z);
                i3 = size;
                i14 = measuredWidth;
            } else if (menuItemImpl2.requestsActionButton()) {
                int groupId2 = menuItemImpl2.getGroupId();
                boolean z3 = sparseBooleanArray.get(groupId2);
                boolean z4 = (i11 > 0 || z3) && i12 > 0 && (!actionMenuPresenter.mStrictWidthLimit || i > 0);
                if (z4) {
                    i3 = size;
                    View itemView2 = actionMenuPresenter.getItemView(menuItemImpl2, actionMenuPresenter.mScrapActionButtonView, viewGroup);
                    boolean z5 = z4;
                    if (actionMenuPresenter.mScrapActionButtonView == null) {
                        actionMenuPresenter.mScrapActionButtonView = itemView2;
                    }
                    if (actionMenuPresenter.mStrictWidthLimit) {
                        int measureChildForCells = ActionMenuView.measureChildForCells(itemView2, i2, i, makeMeasureSpec, 0);
                        i -= measureChildForCells;
                        z5 = measureChildForCells == 0 ? false : z5;
                    } else {
                        itemView2.measure(makeMeasureSpec, makeMeasureSpec);
                    }
                    int measuredWidth2 = itemView2.getMeasuredWidth();
                    i12 -= measuredWidth2;
                    if (i14 == 0) {
                        i14 = measuredWidth2;
                    }
                    z4 = actionMenuPresenter.mStrictWidthLimit ? z5 & (i12 >= 0) : z5 & (i12 + i14 > 0);
                } else {
                    i3 = size;
                }
                if (z4 && groupId2 != 0) {
                    sparseBooleanArray.put(groupId2, true);
                } else if (z3) {
                    sparseBooleanArray.put(groupId2, false);
                    for (int i15 = 0; i15 < i13; i15++) {
                        MenuItemImpl menuItemImpl3 = visibleItems.get(i15);
                        if (menuItemImpl3.getGroupId() == groupId2) {
                            if (menuItemImpl3.isActionButton()) {
                                i11++;
                            }
                            menuItemImpl3.setIsActionButton(false);
                        }
                    }
                }
                if (z4) {
                    i11--;
                }
                menuItemImpl2.setIsActionButton(z4);
            } else {
                i3 = size;
                menuItemImpl2.setIsActionButton(false);
                i13++;
                size = i3;
                actionMenuPresenter = this;
                i6 = 0;
            }
            i13++;
            size = i3;
            actionMenuPresenter = this;
            i6 = 0;
        }
        return true;
    }

    @Override // android.support.v7.view.menu.BaseMenuPresenter, android.support.v7.view.menu.MenuPresenter
    public void onCloseMenu(MenuBuilder menuBuilder, boolean z) {
        dismissPopupMenus();
        super.onCloseMenu(menuBuilder, z);
    }

    @Override // android.support.v7.view.menu.MenuPresenter
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState();
        savedState.openSubMenuId = this.mOpenSubMenuId;
        return savedState;
    }

    @Override // android.support.v7.view.menu.MenuPresenter
    public void onRestoreInstanceState(Parcelable parcelable) {
        MenuItem findItem;
        SavedState savedState = (SavedState) parcelable;
        if (savedState.openSubMenuId <= 0 || (findItem = this.mMenu.findItem(savedState.openSubMenuId)) == null) {
            return;
        }
        onSubMenuSelected((SubMenuBuilder) findItem.getSubMenu());
    }

    @Override // android.support.v4.view.ActionProvider.SubUiVisibilityListener
    public void onSubUiVisibilityChanged(boolean z) {
        if (z) {
            super.onSubMenuSelected(null);
        } else {
            this.mMenu.close(false);
        }
    }

    public void setMenuView(ActionMenuView actionMenuView) {
        this.mMenuView = actionMenuView;
        actionMenuView.initialize(this.mMenu);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class SavedState implements Parcelable {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: android.support.v7.widget.ActionMenuPresenter.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        public int openSubMenuId;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        SavedState() {
        }

        SavedState(Parcel parcel) {
            this.openSubMenuId = parcel.readInt();
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.openSubMenuId);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class OverflowMenuButton extends AppCompatImageView implements ActionMenuView.ActionMenuChildView {
        private final float[] mTempPts;

        @Override // android.support.v7.widget.ActionMenuView.ActionMenuChildView
        public boolean needsDividerAfter() {
            return false;
        }

        @Override // android.support.v7.widget.ActionMenuView.ActionMenuChildView
        public boolean needsDividerBefore() {
            return false;
        }

        public OverflowMenuButton(Context context) {
            super(context, null, R.attr.actionOverflowButtonStyle);
            this.mTempPts = new float[2];
            setClickable(true);
            setFocusable(true);
            setVisibility(0);
            setEnabled(true);
            setOnTouchListener(new ListPopupWindow.ForwardingListener(this) { // from class: android.support.v7.widget.ActionMenuPresenter.OverflowMenuButton.1
                @Override // android.support.v7.widget.ListPopupWindow.ForwardingListener
                public ListPopupWindow getPopup() {
                    if (ActionMenuPresenter.this.mOverflowPopup == null) {
                        return null;
                    }
                    return ActionMenuPresenter.this.mOverflowPopup.getPopup();
                }

                @Override // android.support.v7.widget.ListPopupWindow.ForwardingListener
                public boolean onForwardingStarted() {
                    ActionMenuPresenter.this.showOverflowMenu();
                    return true;
                }

                @Override // android.support.v7.widget.ListPopupWindow.ForwardingListener
                public boolean onForwardingStopped() {
                    if (ActionMenuPresenter.this.mPostedOpenRunnable != null) {
                        return false;
                    }
                    ActionMenuPresenter.this.hideOverflowMenu();
                    return true;
                }
            });
        }

        @Override // android.view.View
        public boolean performClick() {
            if (super.performClick()) {
                return true;
            }
            playSoundEffect(0);
            ActionMenuPresenter.this.showOverflowMenu();
            return true;
        }

        @Override // android.widget.ImageView
        protected boolean setFrame(int i, int i2, int i3, int i4) {
            boolean frame = super.setFrame(i, i2, i3, i4);
            Drawable drawable = getDrawable();
            Drawable background = getBackground();
            if (drawable != null && background != null) {
                int width = getWidth();
                int height = getHeight();
                int max = Math.max(width, height) / 2;
                int paddingLeft = (width + (getPaddingLeft() - getPaddingRight())) / 2;
                int paddingTop = (height + (getPaddingTop() - getPaddingBottom())) / 2;
                DrawableCompat.setHotspotBounds(background, paddingLeft - max, paddingTop - max, paddingLeft + max, paddingTop + max);
            }
            return frame;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class OverflowPopup extends MenuPopupHelper {
        public OverflowPopup(Context context, MenuBuilder menuBuilder, View view, boolean z) {
            super(context, menuBuilder, view, z, R.attr.actionOverflowMenuStyle);
            setGravity(GravityCompat.END);
            setCallback(ActionMenuPresenter.this.mPopupPresenterCallback);
        }

        @Override // android.support.v7.view.menu.MenuPopupHelper, android.widget.PopupWindow.OnDismissListener
        public void onDismiss() {
            super.onDismiss();
            if (ActionMenuPresenter.this.mMenu != null) {
                ActionMenuPresenter.this.mMenu.close();
            }
            ActionMenuPresenter.this.mOverflowPopup = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ActionButtonSubmenu extends MenuPopupHelper {
        private SubMenuBuilder mSubMenu;

        public ActionButtonSubmenu(Context context, SubMenuBuilder subMenuBuilder) {
            super(context, subMenuBuilder, null, false, R.attr.actionOverflowMenuStyle);
            this.mSubMenu = subMenuBuilder;
            if (!((MenuItemImpl) subMenuBuilder.getItem()).isActionButton()) {
                setAnchorView(ActionMenuPresenter.this.mOverflowButton == null ? (View) ActionMenuPresenter.this.mMenuView : ActionMenuPresenter.this.mOverflowButton);
            }
            setCallback(ActionMenuPresenter.this.mPopupPresenterCallback);
            int size = subMenuBuilder.size();
            boolean z = false;
            int i = 0;
            while (true) {
                if (i >= size) {
                    break;
                }
                MenuItem item = subMenuBuilder.getItem(i);
                if (item.isVisible() && item.getIcon() != null) {
                    z = true;
                    break;
                }
                i++;
            }
            setForceShowIcon(z);
        }

        @Override // android.support.v7.view.menu.MenuPopupHelper, android.widget.PopupWindow.OnDismissListener
        public void onDismiss() {
            super.onDismiss();
            ActionMenuPresenter.this.mActionButtonPopup = null;
            ActionMenuPresenter.this.mOpenSubMenuId = 0;
        }
    }

    /* loaded from: classes.dex */
    private class PopupPresenterCallback implements MenuPresenter.Callback {
        private PopupPresenterCallback() {
        }

        @Override // android.support.v7.view.menu.MenuPresenter.Callback
        public boolean onOpenSubMenu(MenuBuilder menuBuilder) {
            if (menuBuilder == null) {
                return false;
            }
            ActionMenuPresenter.this.mOpenSubMenuId = ((SubMenuBuilder) menuBuilder).getItem().getItemId();
            MenuPresenter.Callback callback = ActionMenuPresenter.this.getCallback();
            if (callback != null) {
                return callback.onOpenSubMenu(menuBuilder);
            }
            return false;
        }

        @Override // android.support.v7.view.menu.MenuPresenter.Callback
        public void onCloseMenu(MenuBuilder menuBuilder, boolean z) {
            if (menuBuilder instanceof SubMenuBuilder) {
                ((SubMenuBuilder) menuBuilder).getRootMenu().close(false);
            }
            MenuPresenter.Callback callback = ActionMenuPresenter.this.getCallback();
            if (callback != null) {
                callback.onCloseMenu(menuBuilder, z);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class OpenOverflowRunnable implements Runnable {
        private OverflowPopup mPopup;

        public OpenOverflowRunnable(OverflowPopup overflowPopup) {
            this.mPopup = overflowPopup;
        }

        @Override // java.lang.Runnable
        public void run() {
            ActionMenuPresenter.this.mMenu.changeMenuMode();
            View view = (View) ActionMenuPresenter.this.mMenuView;
            if (view != null && view.getWindowToken() != null && this.mPopup.tryShow()) {
                ActionMenuPresenter.this.mOverflowPopup = this.mPopup;
            }
            ActionMenuPresenter.this.mPostedOpenRunnable = null;
        }
    }

    /* loaded from: classes.dex */
    private class ActionMenuPopupCallback extends ActionMenuItemView.PopupCallback {
        private ActionMenuPopupCallback() {
        }

        @Override // android.support.v7.view.menu.ActionMenuItemView.PopupCallback
        public ListPopupWindow getPopup() {
            if (ActionMenuPresenter.this.mActionButtonPopup != null) {
                return ActionMenuPresenter.this.mActionButtonPopup.getPopup();
            }
            return null;
        }
    }
}
