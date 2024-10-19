package android.support.v4.view;

import android.view.MenuItem;

/* loaded from: classes.dex */
class MenuItemCompatIcs {

    /* loaded from: classes.dex */
    interface SupportActionExpandProxy {
        boolean onMenuItemActionCollapse(MenuItem menuItem);

        boolean onMenuItemActionExpand(MenuItem menuItem);
    }

    MenuItemCompatIcs() {
    }

    public static boolean expandActionView(MenuItem menuItem) {
        return menuItem.expandActionView();
    }

    public static boolean collapseActionView(MenuItem menuItem) {
        return menuItem.collapseActionView();
    }

    public static boolean isActionViewExpanded(MenuItem menuItem) {
        return menuItem.isActionViewExpanded();
    }

    public static MenuItem setOnActionExpandListener(MenuItem menuItem, SupportActionExpandProxy supportActionExpandProxy) {
        return menuItem.setOnActionExpandListener(new OnActionExpandListenerWrapper(supportActionExpandProxy));
    }

    /* loaded from: classes.dex */
    static class OnActionExpandListenerWrapper implements MenuItem.OnActionExpandListener {
        private SupportActionExpandProxy mWrapped;

        public OnActionExpandListenerWrapper(SupportActionExpandProxy supportActionExpandProxy) {
            this.mWrapped = supportActionExpandProxy;
        }

        @Override // android.view.MenuItem.OnActionExpandListener
        public boolean onMenuItemActionExpand(MenuItem menuItem) {
            return this.mWrapped.onMenuItemActionExpand(menuItem);
        }

        @Override // android.view.MenuItem.OnActionExpandListener
        public boolean onMenuItemActionCollapse(MenuItem menuItem) {
            return this.mWrapped.onMenuItemActionCollapse(menuItem);
        }
    }
}
